package com.nadajp.littletalkers;

import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AddKidActivity extends Activity implements
      AddKidFragment.OnKidAddedListener
{
   private long mCurrentKidId;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_add_kid);
      ActionBar actionBar = this.getActionBar(); 
      actionBar.setDisplayHomeAsUpEnabled(true);
      Utils.setColor(actionBar, Utils.COLOR_ORANGE, this);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.add_kid, menu);
      MenuItem item = menu.findItem(R.id.action_add_word);
      mCurrentKidId = getIntent().getLongExtra(Prefs.CURRENT_KID_ID, -1);
      if (mCurrentKidId < 0)
      {
         item.setVisible(false);
      } else
      {
         item.setVisible(true);
      }
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      // Handle presses on the action bar items
      switch (item.getItemId())
      {
      case R.id.action_add_word:
         switchToAddWord();
         return true;
      case R.id.action_manage_kids:
         Intent manage_intent = new Intent(this, ManageKidsActivity.class);
         startActivity(manage_intent);
         return true;
      case R.id.action_export:
         Intent backup_intent = new Intent(this, DataExportActivity.class);
         startActivity(backup_intent);
      default:
         return super.onOptionsItemSelected(item);
      }
   }

   public void onKidAdded(long kidId)
   {
      Intent intent = new Intent(this, AddItemActivity.class);
      intent.putExtra(Prefs.CURRENT_KID_ID, kidId);
      startActivity(intent);
   }
   
   public void onKidUpdated(long kidId)
   {
      Intent intent = new Intent(this, ManageKidsActivity.class);
      startActivity(intent);
   }

   private void switchToAddWord()
   {
      Intent intent = new Intent(this, AddItemActivity.class);
      intent.putExtra(Prefs.CURRENT_KID_ID, mCurrentKidId);
      startActivity(intent);
   }
}
