package com.nadajp.littletalkers;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.utils.Prefs;

public class BaseActivity extends Activity implements OnItemSelectedListener
{
   protected SimpleCursorAdapter mCursorAdapter = null;
   private static final String DEBUG_TAG = "BaseActivity";
   private int mCurrentKidId;
   private int mPosition;
   protected int mType;
   private CircularImageView mImgProfile;
   private Spinner mSpinner;
   
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      {
         mCurrentKidId = Prefs.getKidId(this, DbSingleton.get()
            .getLastAddedKid());
         //Log.i(DEBUG_TAG, "In BaseActivity, kid id from Prefs is " + mCurrentKidId);        
      }
      
      if (savedInstanceState != null)
      {
         mPosition = savedInstanceState.getInt(Prefs.POSITION);
      } else 
      { 
         mPosition = -1; 
      }

      final ActionBar actionBar = getActionBar();     
      LayoutInflater mInflater = LayoutInflater.from(this);
      View customView = mInflater.inflate(R.layout.actionbar, null);
      
      mImgProfile = (CircularImageView) customView
              .findViewById(R.id.action_profile);
      
      mSpinner = (Spinner) customView.findViewById(R.id.action_main_spinner);

      actionBar.setCustomView(customView);
      actionBar.setDisplayShowCustomEnabled(true);     
      actionBar.setDisplayShowTitleEnabled(false); 
   }
   
   public void setItemType(int type) {}
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      getMenuInflater().inflate(R.menu.base, menu);
      return super.onCreateOptionsMenu(menu);
   }
   
   public void setCurrentKidId(int id)
   {
      mCurrentKidId = id;
      Prefs.saveKidId(this, mCurrentKidId);
   }
   
   private void changeProfilePic(String pictureUri)
   {
      Bitmap profilePicture = null;
      if (pictureUri == null)
      {
         profilePicture = BitmapFactory.decodeResource(this.getResources(),
               R.drawable.profile);
      } else
      {
         profilePicture = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(pictureUri),
               80, 80);
      }
      mImgProfile.setImageBitmap(profilePicture); 
   }

   protected void setupMainMenuSpinner() 
   {
      Cursor cursor = DbSingleton.get().getKidsForSpinner();
      if (cursor.getCount() == 0) { return; }
      
      //Log.i(DEBUG_TAG, "Number of kids: " + cursor.getCount());
      
      String[] adapterCols = new String[] { "name" };
      int[] adapterRowViews = new int[] { android.R.id.text1 };
      
      mCursorAdapter = new SimpleCursorAdapter(this, R.layout.kid_spinner_item,
             cursor, adapterCols, adapterRowViews, 0);
      mCursorAdapter
             .setDropDownViewResource(R.layout.kid_spinner_dropdown_item);
      mSpinner.setAdapter(mCursorAdapter);
      mSpinner.setOnItemSelectedListener(this);
       
      // select the current kid
      mCurrentKidId = Prefs.getKidId(this, DbSingleton.get()
            .getLastAddedKid());
      //Log.i(DEBUG_TAG, "Setting up spinner kid id from Prefs is : " + mCurrentKidId); 
      
      String pictureUri = DbSingleton.get().getPicturePath(mCurrentKidId);
      changeProfilePic(pictureUri);
          
      if (mPosition >= 0 && mPosition < cursor.getCount()) 
      { 
         //Log.i(DEBUG_TAG, "Setting position: " + mPosition);
         mSpinner.setSelection(mPosition); 
      } 
      else
      {
         for (int i = 0; i < mCursorAdapter.getCount(); i++)
         {
            if (mCursorAdapter.getItemId(i) == mCurrentKidId)
            {
               mSpinner.setSelection(i);
               
               return;
            }
         }
         mSpinner.setSelection(0);
      }
   }

   public void clickProfile(View v)
   {
      Intent intent = new Intent(this, KidProfileActivity.class);
      intent.putExtra(Prefs.CURRENT_KID_ID, mCurrentKidId);
      startActivity(intent);
   }
   
   public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
   {
      if (mCurrentKidId == id)
      {
         return;
      }
      //Log.i(DEBUG_TAG, "Selected kid with ID " + id + ", setting current kid...");
      mCurrentKidId = (int) id;
      mPosition = pos;
      String pictureUri = DbSingleton.get().getPicturePath((int) id);      
      changeProfilePic(pictureUri); 
      setCurrentKidId((int) id);
      setCurrentKidData((int) id);
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
         Prefs.saveType(this, mType);
         dict_intent.putExtra(Prefs.TYPE, mType);
         startActivity(dict_intent);
         finish();
         return true;
      case R.id.action_settings:
         Intent settingsIntent = new Intent(this, SettingsActivity.class);
         startActivity(settingsIntent);
         return true;
      default:
         return super.onOptionsItemSelected(item);
      }
   }

   protected void setCurrentKidData(int kidId)
   {
   }

   @Override
   protected void onResume()
   {
      setupMainMenuSpinner();
      invalidateOptionsMenu();
      super.onResume();
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

   /*
   public void exportDB()
   {
      try
      {
         File sd = Environment.getExternalStorageDirectory();
         //Log.i("DEBUG_TAG", "Trying to export DB");

         if (sd.canWrite())
         {
            //Log.i("DEBUG_TAG", "Can write db");

            String currentDBPath = "/data/data/" + getPackageName()
                  + "/databases/littletalkers_db";
            //Log.i(DEBUG_TAG, "currentDBPath = " + currentDBPath);
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
   }*/

   @Override
   public void onSaveInstanceState(Bundle outState)
   {
      super.onSaveInstanceState(outState);
      outState.putInt(Prefs.POSITION, mPosition);
      outState.putInt(Prefs.CURRENT_KID_ID, mCurrentKidId);
      mType = Prefs.getType(this, Prefs.TYPE_WORD);
      //Log.i(DEBUG_TAG, "Type: " + mType);
      outState.putInt(Prefs.TYPE, mType);
   }

   @Override
   public void onRestoreInstanceState(Bundle savedInstanceState)
   {
      mPosition = savedInstanceState.getInt(Prefs.POSITION);
      mCurrentKidId = savedInstanceState.getInt(Prefs.CURRENT_KID_ID);
      mType = savedInstanceState.getInt(Prefs.TYPE);
      //Log.i(DEBUG_TAG, "Restoring Type: " + mType);
   }
}
