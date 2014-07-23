package com.nadajp.littletalkers;

import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

public class ItemListActivity extends BaseActivity implements ActionBar.TabListener
{
   private static String DEBUG_TAG = "ItemListActivity";
   ItemListPagerAdapter mSectionsPagerAdapter;
   ViewPager mViewPager;
   
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_item_list);
      Log.i(DEBUG_TAG, "Entering dictionary...");

      // Set up the action bar.
      final ActionBar actionBar = getActionBar();
      actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

      // Create the adapter that will return a fragment for each of the three
      // primary sections of the activity.
      mSectionsPagerAdapter = new ItemListPagerAdapter(getFragmentManager(), this);

      // Set up the ViewPager with the sections adapter.
      mViewPager = (ViewPager) findViewById(R.id.pager);
      mViewPager.setAdapter(mSectionsPagerAdapter);

      // When swiping between different sections, select the corresponding
      // tab. We can also use ActionBar.Tab#select() to do this if we have
      // a reference to the Tab.
      mViewPager
            .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
            {
               @Override
               public void onPageSelected(int position)
               {
                  actionBar.setSelectedNavigationItem(position);
               }
            });

      // For each of the sections in the app, add a tab to the action bar.
      for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++)
      {
         // Create a tab with text corresponding to the page title defined by
         // the adapter. Also specify this Activity object, which implements
         // the TabListener interface, as the callback (listener) for when
         // this tab is selected.
         actionBar.addTab(actionBar.newTab()
               .setText(mSectionsPagerAdapter.getPageTitle(i))
               .setTabListener(this));
      }
      Log.i(DEBUG_TAG, "TYPE IS: " + mType);
      
      if (savedInstanceState != null){
         mType = savedInstanceState.getInt(Prefs.TYPE);
         Log.i(DEBUG_TAG, "NEW TYPE IS: " + mType);
      }
      actionBar.setSelectedNavigationItem(mType); 
   }

   @Override
   public void onTabSelected(ActionBar.Tab tab,
         FragmentTransaction fragmentTransaction)
   {
      // When the given tab is selected, switch to the corresponding page in
      // the ViewPager.
      int position = tab.getPosition();
      Log.i(DEBUG_TAG, "CURRENT POSITION: " + position);
      mViewPager.setCurrentItem(position);
      
      ActionBar actionBar = getActionBar();

      switch (position)
      {
        case 0:
           Utils.setColor(actionBar, Utils.COLOR_BLUE, this);
           break;
        case 1:
           Utils.setColor(actionBar, Utils.COLOR_GREEN, this);           
           break;
      }
      Prefs.saveType(this, position);
   }

   @Override
   public void onTabUnselected(ActionBar.Tab tab,
         FragmentTransaction fragmentTransaction)
   {
   }

   @Override
   public void onTabReselected(ActionBar.Tab tab,
         FragmentTransaction fragmentTransaction)
   {
   }

   private ItemListFragment getCurrentFragment()
   {
      return (ItemListFragment) mSectionsPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
   }
   
   @Override
   protected void setCurrentKidData(long kidId)
   {
      ItemListFragment listFragment = getCurrentFragment();
      if (listFragment != null) { listFragment.updateData(kidId); }
   }

   public void sortByWord(View v)
   {
      ItemListFragment listFragment = getCurrentFragment();
      if (listFragment != null) { listFragment.sortByPhrase(v); }
   }

   public void sortByDate(View v)
   {
      ItemListFragment listFragment = getCurrentFragment();
      if (listFragment != null) { listFragment.sortByDate(v); }
   }

   public void addNewWord(View view)
   {
      Intent intent = new Intent(this, AddItemActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
      mType = Prefs.getType(this, Prefs.TYPE_WORD);
      intent.putExtra(Prefs.TYPE, mType);
      startActivity(intent);
   }
   
   @Override
   public void onSaveInstanceState(Bundle outState)
   {
      super.onSaveInstanceState(outState);      
      outState.putInt(Prefs.TYPE, mType);
   }

}
