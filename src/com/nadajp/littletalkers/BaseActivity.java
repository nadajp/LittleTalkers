package com.nadajp.littletalkers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;

import com.nadajp.littletalkers.database.DbSingleton;

public class BaseActivity extends Activity
{
   private SimpleCursorAdapter mCursorAdapter = null;
   private static final String DEBUG_TAG = "BaseActivity";
   private long mCurrentKidId;
   private int mPosition;    
	
   @Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.shared_preferences_filename), MODE_PRIVATE);
		long latestKidId = sharedPrefs.getLong("current_kid_id", DbSingleton.get().getLastAddedKid());	
		mCurrentKidId = this.getIntent().getLongExtra("current_kid_id", latestKidId);
		if (savedInstanceState!= null)
		{
			mPosition = savedInstanceState.getInt("position");
		}
		else
		{
			mPosition = -1;
		}
		//Log.i(DEBUG_TAG, "Position: " + mPosition);
	}

   @SuppressLint("NewApi")
   @Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.    	
		getMenuInflater().inflate(R.menu.base, menu);
		// Locate MenuItem with ShareActionProvider
		
	   addSpinnerToActionBar();
		return super.onCreateOptionsMenu(menu);
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
	        	switchToAddWord();
	         return true;
	      case R.id.action_backup:
	         Intent backup_intent = new Intent(this, DataBackupActivity.class);
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
	      Log.i(DEBUG_TAG, "Selectied item with ID " + itemId);
			mCurrentKidId = itemId;	
			mPosition = position;
			setCurrentKidData(itemId);
			/*TitlebarFragment titlebarFragment = (TitlebarFragment)
		                getFragmentManager().findFragmentById(R.id.titlebar_fragment);
			if (titlebarFragment != null)
			   titlebarFragment.updateData(itemId);	*/
			return true;
		}
	};
		
	protected void setCurrentKidData(long kidId){}
	
	private void addSpinnerToActionBar()
	{
	   Cursor cursor = DbSingleton.get().getKidsForSpinner();
 	   if (cursor.getCount()==0)
 	   {
 	      return;
 	   }

 	   Log.i(DEBUG_TAG, "Adding Spinner to ActionBar");
 	    
      String[] adapterCols=new String[]{"name"};
   	int[] adapterRowViews=new int[]{android.R.id.text1};
  
   	mCursorAdapter = new SimpleCursorAdapter(this, R.layout.kid_spinner_item, cursor, adapterCols, adapterRowViews,0);
   	mCursorAdapter.setDropDownViewResource(R.layout.kid_spinner_dropdown_item);
   	
   	ActionBar actionBar = this.getActionBar();
   	actionBar.setDisplayShowTitleEnabled(false);
   	actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
   	actionBar.setListNavigationCallbacks(mCursorAdapter, mOnNavigationListener);
  
   	// select the current kid
      if (mPosition > 0)
      {
      	actionBar.setSelectedNavigationItem(mPosition); 
      }
      else
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
	
	private void switchToAddWord()
	{
		Intent intent = new Intent(this, AddWordActivity.class);
		intent.putExtra("current_kid_id", mCurrentKidId);
		startActivity(intent);
	}

	public void clickTitlebar(View v)
	{
	   Intent intent = new Intent(this, AddKidActivity.class);
	   intent.putExtra("current_kid_id", mCurrentKidId);
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

 	   SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.shared_preferences_filename), MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPrefs.edit();
      editor.putLong("current_kid_id", mCurrentKidId);
      editor.commit();
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
		try {
		   File sd = Environment.getExternalStorageDirectory();
         Log.i("DEBUG_TAG", "Trying to export DB");

         if (sd.canWrite()) 
         {
            Log.i("DEBUG_TAG", "Can write db");

            String currentDBPath = "/data/data/" + getPackageName() + "/databases/littletalkers_db";
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
               MediaScannerConnection.scanFile(this,new String[] {backupDB.getAbsolutePath()}, null, null);
                //Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                //Uri fileContentUri = Uri.fromFile(backupDB); // With 'permFile' being the File object
                //mediaScannerIntent.setData(fileContentUri);
                //this.sendBroadcast(mediaScannerIntent); //
                 
                Log.i("DEBUG_TAG", "DB exported to " + backupDB.getAbsolutePath());
             }
             else
             {
                Log.i("DEBUG_TAG", "DB does not exist!");
             }
           }
		  } catch (Exception e) {
     	Log.i(DEBUG_TAG, "Could not export DB");
     }
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
	   super.onSaveInstanceState(outState);
		outState.putInt("position", mPosition);
		outState.putLong("current_kid_id", mCurrentKidId);
	}
	 
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) 
	{
	   mPosition = savedInstanceState.getInt("position");
	}
}
