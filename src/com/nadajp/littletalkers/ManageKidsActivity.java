package com.nadajp.littletalkers;

import java.io.File;

import com.nadajp.littletalkers.ManageKidsFragment.DeleteSelectedDialogFragment;
import com.nadajp.littletalkers.ManageKidsFragment.ModifyKidsListener;
import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ManageKidsActivity extends Activity implements ModifyKidsListener
{
   private static final String DEBUG_TAG = "ManageKidsActivity";
   private static final int DELETE_SELECTED_DIALOG_ID = 1;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_manage_kids);
      ActionBar actionBar = this.getActionBar();
      actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
      actionBar.setTitle(R.string.title_activity_manage_kids);
      Utils.setColor(actionBar, Utils.COLOR_ORANGE, this);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.manage_kids, menu);
      return true;
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
      default:
         return super.onOptionsItemSelected(item);
      }
   }

   @Override
   public void onKidsDeleted()
   {
      //Log.i(DEBUG_TAG, "Number of Kids: " + DbSingleton.get().getNumberOfKids());
      if (DbSingleton.get().getNumberOfKids() == 0)
      {
         Prefs.saveKidId(this, -1);
         Intent intent = new Intent(this, AddKidActivity.class);
         startActivity(intent);
         return;
      }
      long id = DbSingleton.get().getLastAddedKid();
      Prefs.saveKidId(this, id);
   }
}
