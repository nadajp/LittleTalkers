package com.nadajp.littletalkers;

import com.google.android.gms.common.AccountPicker;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.nadajp.littletalkers.SettingsFragment.OnFragmentInteractionListener;
import com.nadajp.littletalkers.backup.UploadUserData;
import com.nadajp.littletalkers.utils.Prefs;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class SettingsActivity extends Activity implements OnFragmentInteractionListener
{
   private static final String DEBUG_TAG = "SettingsActivity";
  
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_settings);
      if (savedInstanceState == null)
      {
         getFragmentManager().beginTransaction()
               .add(R.id.container, new SettingsFragment()).commit();
      }
      this.getActionBar().setDisplayHomeAsUpEnabled(true);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.settings, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();
      if (id == R.id.action_settings)
      {
         return true;
      }
      return super.onOptionsItemSelected(item);
   }

   @Override
   public void onFragmentInteraction(Uri uri)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void doSync()
   {
      // TODO Auto-generated method stub
      
   }

}
