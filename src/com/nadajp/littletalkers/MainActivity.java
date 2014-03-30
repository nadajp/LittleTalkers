	package com.nadajp.littletalkers;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity 
{
   private static String DEBUG_TAG = "Main Activity";

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
	   Log.i(DEBUG_TAG, "Entering Main...");
	   super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		
		// Find out from shared preferences whether there are any kids yet
 	   SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.shared_preferences_filename), MODE_PRIVATE);
		long kidId = sharedPrefs.getLong("current_kid_id", -1);
		
		// If no kids have been added yet, go to AddKidActivity
		if (kidId == -1)
		{
		   Intent intent = new Intent(this, AddKidActivity.class);
		   intent.putExtra("current_kid_id", kidId);
		   startActivity(intent);
		}
            
      else // Go to AddWordActivity
      {  
        	Intent intent = new Intent(this, AddWordActivity.class);
        	intent.putExtra("current_kid_id", kidId);
         startActivity(intent);
      }
	}
}
