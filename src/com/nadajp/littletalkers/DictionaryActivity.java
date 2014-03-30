package com.nadajp.littletalkers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class DictionaryActivity extends BaseActivity 
{
   private static String DEBUG_TAG = "Dictionary Activity";

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dictionary);
		Log.i(DEBUG_TAG, "Entering dictionary...");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
	   super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.dictionary, menu);
		return true;
	}

	@Override
	protected void setCurrentKidData(long kidId)
	{	 
		DictionaryFragment dictionaryFragment = (DictionaryFragment)
	                getFragmentManager().findFragmentById(R.id.dictionary_fragment);
		 if (dictionaryFragment != null)
		 {
			 dictionaryFragment.updateData(kidId);		
		 }
	}
	
	public void sortByWord(View v)
	{
		DictionaryFragment dictionaryFragment = (DictionaryFragment)
                getFragmentManager().findFragmentById(R.id.dictionary_fragment);
		if (dictionaryFragment != null)
		{
			dictionaryFragment.sortByWord(v);
		}
	}
	
	public void sortByDate(View v)
	{
		DictionaryFragment dictionaryFragment = (DictionaryFragment)
                getFragmentManager().findFragmentById(R.id.dictionary_fragment);
		if (dictionaryFragment != null)
		{
			dictionaryFragment.sortByDate(v);	
		}
	}
	

	public void addNewWord(View view) 
	{
	   Intent intent = new Intent(this, AddWordActivity.class);
      startActivity(intent);    
	}
}
