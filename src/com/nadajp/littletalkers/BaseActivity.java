package com.nadajp.littletalkers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

public class BaseActivity extends Activity implements OnItemSelectedListener
{
   private SimpleCursorAdapter mCursorAdapter = null;
   private static final String DEBUG_TAG = "BaseActivity";
   private long mCurrentKidId;
   private int mPosition;
   protected int mType;
   private ImageView mImgProfile;
   private Spinner mSpinner;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      // get kid id from intent, if not available then from shared prefs, if not
      // then from database
      mCurrentKidId = Prefs.getKidId(this, DbSingleton.get()
            .getLastAddedKid());
           
      if (savedInstanceState != null)
      {
         mPosition = savedInstanceState.getInt(Prefs.POSITION);
         mType = savedInstanceState.getInt(Prefs.TYPE);  
      } else 
      { 
         mPosition = -1; 
         mType = Prefs.getType(this, Prefs.TYPE_WORD);
      }

      Log.i(DEBUG_TAG, "Type: " + mType);
      // Log.i(DEBUG_TAG, "Position: " + mPosition);
      final ActionBar actionBar = getActionBar();
      
      LayoutInflater mInflater = LayoutInflater.from(this);

      View customView = mInflater.inflate(R.layout.actionbar, null);

      mImgProfile = (ImageView) customView
              .findViewById(R.id.action_profile);
      
      mSpinner = (Spinner) customView.findViewById(R.id.action_main_spinner);

      actionBar.setCustomView(customView);
      actionBar.setDisplayShowCustomEnabled(true);     
      actionBar.setDisplayShowTitleEnabled(false); 
      actionBar.setDisplayUseLogoEnabled(false);
      actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
      setupMainMenuSpinner();
   }

   public class MyTabListener implements ActionBar.TabListener 
   {
      private final Fragment mFragment;

      public MyTabListener(Fragment fragment) {
         mFragment = fragment;
      }

      @Override
      public void onTabReselected(Tab tab, FragmentTransaction ft) 
      {
      
      }

      @Override
      public void onTabSelected(Tab tab, FragmentTransaction ft) 
      {
         if (null != mFragment) 
         {
            Prefs.saveKidId(getApplicationContext(), mCurrentKidId);
            Log.i(DEBUG_TAG, "Saved ID: " + mCurrentKidId);
            ft.replace(R.id.fragment_container, mFragment);          
            Prefs.saveType(getApplicationContext(), (Integer) tab.getTag());
         }
      }

      @Override
      public void onTabUnselected(Tab tab, FragmentTransaction ft) 
      {
         if (null != mFragment)
            ft.remove(mFragment);
      }
   }
   
   public void setItemType(int type) {}
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      getMenuInflater().inflate(R.menu.base, menu);
      //MenuItem mainMenuSpinner = menu.findItem( R.id.menu_main_spinner);
      //MenuItem profilePic = menu.findItem(R.id.action_profile);
      //setupMainMenuSpinner(mainMenuSpinner); 
      return super.onCreateOptionsMenu(menu);
   }
   
   private void changeProfilePic(String pictureUri)
   {
      Bitmap profilePicture = null;
      if (pictureUri == null)
      {
         profilePicture = BitmapFactory.decodeResource(this.getResources(),
               R.drawable.profilepicture);
      } else
      {
         profilePicture = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(pictureUri),
               80, 80);
      }
      mImgProfile.setImageBitmap(profilePicture); 
   }

   private void setupMainMenuSpinner() 
   {
      //View view = menuSpinner.getActionView();
      //if (view instanceof Spinner) 
      //{
          //Spinner spinner = (Spinner) view;
          
          Cursor cursor = DbSingleton.get().getKidsForSpinner();
          if (cursor.getCount() == 0) { return; }

          Log.i(DEBUG_TAG, "Adding Spinner to ActionBar");

          String[] adapterCols = new String[] { "name" };
          int[] adapterRowViews = new int[] { android.R.id.text1 };

          mCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
                cursor, adapterCols, adapterRowViews, 0);
          mCursorAdapter
                .setDropDownViewResource(R.layout.kid_spinner_dropdown_item);
          //mCursorAdapter.setViewBinder(new NavigationSpinnerViewBinder());
          mSpinner.setAdapter(mCursorAdapter);
          mSpinner.setOnItemSelectedListener(this);
          
          // select the current kid
          mCurrentKidId = Prefs.getKidId(this, DbSingleton.get()
                .getLastAddedKid());
          
          String pictureUri = cursor.getString(cursor
                .getColumnIndex(DbContract.Kids.COLUMN_NAME_PICTURE_URI));
          
          changeProfilePic(pictureUri);
         
          Log.i(DEBUG_TAG, "Selecting kid with id: " + mCurrentKidId);
          if (mPosition > 0) { mSpinner.setSelection(mPosition); } else
          {
             for (int i = 0; i < mCursorAdapter.getCount(); i++)
             {
                if (mCursorAdapter.getItemId(i) == mCurrentKidId)
                {
                   mSpinner.setSelection(i);
                   Log.i(DEBUG_TAG, "i: " + i);
                   return;
                }
             }
             mSpinner.setSelection(0);
          }
      //}
   }

   public void clickProfile(View v)
   {
      Intent intent = new Intent(this, KidProfileActivity.class);
      intent.putExtra(Prefs.CURRENT_KID_ID, mCurrentKidId);
      startActivity(intent);
   }
   
   public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
   {
      Log.i(DEBUG_TAG, "Selected kid with ID " + id);
      mCurrentKidId = id;     
      String pictureUri = DbSingleton.get().getPicturePath(id);
      
      changeProfilePic(pictureUri);
      
      mPosition = pos;
      setCurrentKidData(id); 
   }

   public void onNothingSelected(AdapterView<?> parent)
   {
      // Another interface callback
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      // Handle presses on the action bar items
      switch (item.getItemId())
      {
      case R.id.action_add_kid:
         Intent intent = new Intent(this, AddKidActivity.class);
         startActivity(intent);
         return true;
      case R.id.action_export:
         Intent backup_intent = new Intent(this, DataExportActivity.class);
         startActivity(backup_intent);
         return true;
      case R.id.action_manage_kids:
         Intent manage_intent = new Intent(this, ManageKidsActivity.class);
         startActivity(manage_intent);
         return true;
      case R.id.action_dictionary:
         Intent dict_intent = new Intent(this, ItemListActivity.class);
         startActivity(dict_intent);
         return true;
      default:
         return super.onOptionsItemSelected(item);
      }
   }

   protected void setCurrentKidData(long kidId)
   {
   }
  
   public void clickTitlebar(View v)
   {
      Intent intent = new Intent(this, AddKidActivity.class);
      intent.putExtra(Prefs.CURRENT_KID_ID, mCurrentKidId);
      startActivity(intent);
   }

   @Override
   protected void onResume()
   {
      invalidateOptionsMenu();
      super.onResume();
   }

   @Override
   protected void onPause()
   {
      super.onPause();
      Prefs.saveKidId(this, mCurrentKidId);
   }

   @Override
   public void onDestroy()
   {
      super.onDestroy();
      if (mCursorAdapter != null)
      {
         if (mCursorAdapter.getCursor() != null)
         {
            mCursorAdapter.getCursor().close();
         }
      }
      mCursorAdapter = null;
      //exportDB();
   }

   public void exportDB()
   {
      try
      {
         File sd = Environment.getExternalStorageDirectory();
         Log.i("DEBUG_TAG", "Trying to export DB");

         if (sd.canWrite())
         {
            Log.i("DEBUG_TAG", "Can write db");

            String currentDBPath = "/data/data/" + getPackageName()
                  + "/databases/littletalkers_db";
            Log.i(DEBUG_TAG, "currentDBPath = " + currentDBPath);
            String backupDBPath = "LittleTalkers/LTbackup.db";
            File currentDB = new File(currentDBPath);
            File backupDB = new File(sd, backupDBPath);

            if (currentDB.exists())
            {
               FileChannel src = new FileInputStream(currentDB).getChannel();
               FileChannel dst = new FileOutputStream(backupDB).getChannel();
               dst.transferFrom(src, 0, src.size());
               src.close();
               dst.close();
               MediaScannerConnection.scanFile(this,
                     new String[] { backupDB.getAbsolutePath() }, null, null);
               // Intent mediaScannerIntent = new
               // Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
               // Uri fileContentUri = Uri.fromFile(backupDB); // With
               // 'permFile' being the File object
               // mediaScannerIntent.setData(fileContentUri);
               // this.sendBroadcast(mediaScannerIntent); //

               Log.i("DEBUG_TAG",
                     "DB exported to " + backupDB.getAbsolutePath());
            } else
            {
               Log.i("DEBUG_TAG", "DB does not exist!");
            }
         }
      } catch (Exception e)
      {
         Log.i(DEBUG_TAG, "Could not export DB");
      }
   }

   @Override
   public void onSaveInstanceState(Bundle outState)
   {
      super.onSaveInstanceState(outState);
      outState.putInt(Prefs.POSITION, mPosition);
      outState.putLong(Prefs.CURRENT_KID_ID, mCurrentKidId);
      mType = Prefs.getType(this, Prefs.TYPE_WORD);
      Log.i(DEBUG_TAG, "Type: " + mType);
      outState.putInt(Prefs.TYPE, mType);
   }

   @Override
   public void onRestoreInstanceState(Bundle savedInstanceState)
   {
      mPosition = savedInstanceState.getInt(Prefs.POSITION);
      mCurrentKidId = savedInstanceState.getLong(Prefs.CURRENT_KID_ID);
      mType = savedInstanceState.getInt(Prefs.TYPE);
      Log.i(DEBUG_TAG, "Restoring Type: " + mType);
   }
}
