package com.nadajp.littletalkers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
   private SimpleCursorAdapter mCursorAdapter = null;
   private static final String DEBUG_TAG = "BaseActivity";
   private long mCurrentKidId;
   private int mPosition;
   protected int mType;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      // get kid id from intent, if not available then from shared prefs, if not
      // then from database
      long latestKidId = Prefs.getKidId(this, DbSingleton.get()
            .getLastAddedKid());
      mCurrentKidId = getIntent().getLongExtra(Prefs.CURRENT_KID_ID,
            latestKidId);
           
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
      actionBar.setDisplayShowTitleEnabled(false); 
      actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
   }

   public class MyTabListener implements ActionBar.TabListener 
   {
      private final Fragment mFragment;

      public MyTabListener(Fragment fragment, int type) {
         mFragment = fragment;
      }

      @Override
      public void onTabReselected(Tab tab, FragmentTransaction ft) 
      {
      
      }

      @Override
      public void onTabSelected(Tab tab, FragmentTransaction ft) {
         if (null != mFragment) {
            ft.replace(R.id.fragment_container, mFragment);
            Prefs.saveType(getApplicationContext(), (Integer) tab.getTag());
         }
      }

      @Override
      public void onTabUnselected(Tab tab, FragmentTransaction ft) {
         if (null != mFragment)
            ft.remove(mFragment);
      }
   }
   
   public void setItemType(int type) {}
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.base, menu);
      // Locate MenuItem with ShareActionProvider
      MenuItem mainMenuSpinner = menu.findItem( R.id.menu_main_spinner);
      setupMainMenuSpinner(mainMenuSpinner); 
      //addSpinnerToActionBar();
      return super.onCreateOptionsMenu(menu);
   }

   private void setupMainMenuSpinner(MenuItem item) 
   {
      View view = item.getActionView();
      if (view instanceof Spinner) 
      {
          Spinner spinner = (Spinner) view;
          
          Cursor cursor = DbSingleton.get().getKidsForSpinner();
          if (cursor.getCount() == 0) { return; }

          Log.i(DEBUG_TAG, "Adding Spinner to ActionBar");

          String[] adapterCols = new String[] { "name" };
          int[] adapterRowViews = new int[] { android.R.id.text1 };

          mCursorAdapter = new SimpleCursorAdapter(this, R.layout.kid_spinner_item,
                cursor, adapterCols, adapterRowViews, 0);
          mCursorAdapter
                .setDropDownViewResource(R.layout.kid_spinner_dropdown_item);
          
          spinner.setAdapter(mCursorAdapter);
          // select the current kid
          if (mPosition > 0) { spinner.setSelection(mPosition); } else
          {
             for (int i = 0; i < mCursorAdapter.getCount(); i++)
             {
                if (mCursorAdapter.getItemId(i) == mCurrentKidId)
                {
                   spinner.setSelection(i);
                   break;
                }
             }
             spinner.setSelection(0);
          }
          spinner.setOnItemSelectedListener(this);
      }
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
      case R.id.action_add_word:
         switchToAddNewItem(Prefs.TYPE_WORD);
         return true;
      case R.id.action_export:
         Intent backup_intent = new Intent(this, DataExportActivity.class);
         startActivity(backup_intent);
         return true;
      case R.id.action_manage_kids:
         Intent manage_intent = new Intent(this, ManageKidsActivity.class);
         startActivity(manage_intent);
         return true;
      default:
         return super.onOptionsItemSelected(item);
      }
   }

   OnNavigationListener mOnNavigationListener = new OnNavigationListener()
   {
      @Override
      public boolean onNavigationItemSelected(int position, long itemId)
      {
         Log.i(DEBUG_TAG, "Selected item with ID " + itemId);
         if (itemId == mCurrentKidId) { return true; }
         mCurrentKidId = itemId;
         mPosition = position;
         setCurrentKidData(itemId);
         /*
          * TitlebarFragment titlebarFragment = (TitlebarFragment)
          * getFragmentManager().findFragmentById(R.id.titlebar_fragment); if
          * (titlebarFragment != null) titlebarFragment.updateData(itemId);
          */
         return true;
      }
   };

   protected void setCurrentKidData(long kidId)
   {
   }

   private void addSpinnerToActionBar()
   {
      Cursor cursor = DbSingleton.get().getKidsForSpinner();
      if (cursor.getCount() == 0)
      {
         return;
      }

      Log.i(DEBUG_TAG, "Adding Spinner to ActionBar");

      String[] adapterCols = new String[] { "name" };
      int[] adapterRowViews = new int[] { android.R.id.text1 };

      mCursorAdapter = new SimpleCursorAdapter(this, R.layout.kid_spinner_item,
            cursor, adapterCols, adapterRowViews, 0);
      mCursorAdapter
            .setDropDownViewResource(R.layout.kid_spinner_dropdown_item);

      ActionBar actionBar = this.getActionBar();
      actionBar.setDisplayShowTitleEnabled(false);
      actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
      actionBar.setListNavigationCallbacks(mCursorAdapter,
            mOnNavigationListener);

      // select the current kid
      if (mPosition > 0)
      {
         actionBar.setSelectedNavigationItem(mPosition);
      } else
      {
         for (int i = 0; i < mCursorAdapter.getCount(); i++)
         {
            if (mCursorAdapter.getItemId(i) == mCurrentKidId)
            {
               actionBar.setSelectedNavigationItem(i);
               return;
            }
         }
         actionBar.setSelectedNavigationItem(0);
      }
   }

   private void switchToAddNewItem(int type)
   {
      Intent intent = new Intent(this, AddItemActivity.class);
      intent.putExtra(Prefs.CURRENT_KID_ID, mCurrentKidId);
      intent.putExtra(Prefs.TYPE, type);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
      startActivity(intent);
   }
   
   private void showItemList(int type) 
   {
      Intent intent = new Intent(this, ItemListActivity.class);
      intent.putExtra(Prefs.CURRENT_KID_ID, mCurrentKidId);
      intent.putExtra(Prefs.TYPE, type);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
      startActivity(intent);  
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
      final ActionBar actionBar = getActionBar();
      mType = (Integer) actionBar.getSelectedTab().getTag();
      Log.i(DEBUG_TAG, "Type: " + mType);
      outState.putInt(Prefs.TYPE, (Integer) actionBar.getSelectedTab().getTag());
   }

   @Override
   public void onRestoreInstanceState(Bundle savedInstanceState)
   {
      mPosition = savedInstanceState.getInt(Prefs.POSITION);
      mCurrentKidId = savedInstanceState.getLong(Prefs.CURRENT_KID_ID);
      mType = savedInstanceState.getInt(Prefs.TYPE);
      Log.i(DEBUG_TAG, "Restoring Type: " + mType);
   }

   public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
   {
      Log.i(DEBUG_TAG, "Selected item with ID " + id);
      mCurrentKidId = id;
      mPosition = pos;
      setCurrentKidData(id); 
   }

   public void onNothingSelected(AdapterView<?> parent)
   {
      // Another interface callback
   }
}
