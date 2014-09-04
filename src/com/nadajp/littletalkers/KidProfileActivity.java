package com.nadajp.littletalkers;

import com.nadajp.littletalkers.utils.Utils;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

public class KidProfileActivity extends BaseActivity
{

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_kid_profile);

      if (savedInstanceState == null)
      {
         getFragmentManager().beginTransaction()
               .add(R.id.container, new KidProfileFragment()).commit();
      }
      ActionBar actionBar = this.getActionBar();
      Utils.setColor(actionBar, Utils.COLOR_RED, this);
      actionBar.setDisplayShowHomeEnabled(true);
      actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
      actionBar.setDisplayShowCustomEnabled(false);      
      actionBar.setTitle(R.string.title_activity_kid_profile);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {
       // Respond to the action bar's Up/Home button
       case android.R.id.home:
           if (this.getIntent().hasExtra("ManageKidsView"))
           {
              Intent intent = new Intent(this, ManageKidsActivity.class);
              startActivity(intent);
           }
           else
           {
              Intent intent = NavUtils.getParentActivityIntent(this);
              // navigate up to the logical parent activity.
              NavUtils.navigateUpTo(this, intent);
           }
           
           return true;
       }
       return super.onOptionsItemSelected(item);
   }
  
}
