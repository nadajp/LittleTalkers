package com.nadajp.littletalkers;

import android.content.Intent;
import android.os.Bundle;

import com.nadajp.littletalkers.AddWordFragment.OnAddWordListener;

public class AddWordActivity extends BaseActivity implements OnAddWordListener 
{
   private static String DEBUG_TAG = "AddWord Activity";

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_word);
	}
	
	@Override
	protected void setCurrentKidData(long kidId)
	{	 
		AddWordFragment addWordFragment = (AddWordFragment)
	                getFragmentManager().findFragmentById(R.id.add_word_fragment);
		if (addWordFragment != null) 
		{
		   addWordFragment.insertKidDefaults(kidId, addWordFragment.getView());	 
		}
	}
	
	public void onWordAdded(long kidId)
	{
		
	}
	
	public void onClickedShowDictionary(long kidId)
	{
		Intent intent = new Intent(this, DictionaryActivity.class);
    	intent.putExtra("current_kid_id", kidId);
    	startActivity(intent);	
   }
	
}
