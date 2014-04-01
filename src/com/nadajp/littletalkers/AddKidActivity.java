package com.nadajp.littletalkers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AddKidActivity extends Activity implements AddKidFragment.OnKidAddedListener 
{
	private long mCurrentKidId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_kid);
	   getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{ 
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_kid, menu);		
		MenuItem item = menu.findItem(R.id.action_add_word);
		mCurrentKidId = this.getIntent().getLongExtra(getString(R.string.current_kid_id), -1);
		if (mCurrentKidId < 0)
		{
		   item.setVisible(false); 
		}
		else
		{
		   item.setVisible(true);
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_add_word:
	        	switchToAddWord();
	            return true;
	        case R.id.action_manage_kids:
	            Intent manage_intent = new Intent(this, ManageKidsActivity.class);
	            startActivity(manage_intent);  
	            return true;
	        case R.id.action_backup:
	            Intent backup_intent = new Intent(this, DataBackupActivity.class);
	            startActivity(backup_intent); 
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void onKidAdded(long kidId)
	{
	   Intent intent = new Intent(this, AddWordActivity.class);
    	intent.putExtra(getString(R.string.current_kid_id), kidId);
    	startActivity(intent); 
	}
	
	private void switchToAddWord()
	{
		Intent intent = new Intent(this, AddWordActivity.class);
		intent.putExtra(getString(R.string.current_kid_id), mCurrentKidId);
      startActivity(intent);
	}

}
