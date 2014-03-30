package com.nadajp.littletalkers;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class DataBackupActivity extends Activity
{

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_data_backup);
      getActionBar().setDisplayHomeAsUpEnabled(true);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.data_backup, menu);
      return true;
   }

}
