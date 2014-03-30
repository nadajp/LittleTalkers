package com.nadajp.littletalkers;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ManageKidsActivity extends BaseActivity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_kids);
		this.getActionBar().setTitle(R.string.title_activity_manage_kids);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		//super.onCreateOptionsMenu(menu);
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
}
