package com.nadajp.littletalkers;

import com.nadajp.littletalkers.ManageKidsFragment.ModifyKidsListener;
import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ManageKidsActivity extends BaseActivity implements ModifyKidsListener
{
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_manage_kids);
      ActionBar actionBar = this.getActionBar();
      actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
      actionBar.setDisplayShowCustomEnabled(false);      
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

      default:
         return super.onOptionsItemSelected(item);
      }
   }

   @Override
   public void onKidsDeleted()
   {
      if (DbSingleton.get().isEmpty())
      {
         Prefs.saveKidId(this, 0);
         Intent intent = new Intent(this, AddKidActivity.class);
         startActivity(intent);
         return;
      }
      long id = DbSingleton.get().getLastAddedKid();
      super.setCurrentKidId(id);
      setupMainMenuSpinner();
      invalidateOptionsMenu();
      Bundle bundle = new Bundle();
      bundle.putBoolean(Prefs.KIDS_CHANGED, true);
      super.onSaveInstanceState(bundle);
   }
}
