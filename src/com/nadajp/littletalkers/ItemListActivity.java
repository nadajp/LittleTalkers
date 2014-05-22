package com.nadajp.littletalkers;

import com.nadajp.littletalkers.utils.Prefs;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ItemListActivity extends BaseActivity
{
   private static String DEBUG_TAG = "PhraseList Activity";

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_dictionary);
      Log.i(DEBUG_TAG, "Entering dictionary...");

      final ActionBar actionBar = getActionBar();

      ItemListFragment wordListFragment = new WordListFragment();
      Tab wordListTab = actionBar.newTab().setText(R.string.word_or_phrase);
      wordListTab.setTag(Prefs.TYPE_WORD);
      actionBar.addTab(wordListTab
            .setTabListener(new MyTabListener(wordListFragment, Prefs.TYPE_WORD)));

      ItemListFragment QAListFragment = new QAListFragment();
      Tab qaListTab = actionBar.newTab().setText(R.string.q_and_a);
      qaListTab.setTag(Prefs.TYPE_QA);
      actionBar.addTab(qaListTab
            .setTabListener(new MyTabListener(QAListFragment, Prefs.TYPE_QA)));

      if (mType == Prefs.TYPE_WORD) { actionBar.selectTab(wordListTab); }
      else { actionBar.selectTab(qaListTab); }
   }

   @Override
   protected void setCurrentKidData(long kidId)
   {
      ItemListFragment listFragment = (ItemListFragment) getFragmentManager()
            .findFragmentById(R.id.fragment_container);
      if (listFragment != null) { listFragment.updateData(kidId); }
   }

   public void sortByWord(View v)
   {
      ItemListFragment listFragment = (ItemListFragment) getFragmentManager()
            .findFragmentById(R.id.fragment_container);
      if (listFragment != null) { listFragment.sortByPhrase(v); }
   }

   public void sortByDate(View v)
   {
      ItemListFragment listFragment = (ItemListFragment) getFragmentManager()
            .findFragmentById(R.id.fragment_container);
      if (listFragment != null) { listFragment.sortByDate(v); }
   }

   public void addNewWord(View view)
   {
      Intent intent = new Intent(this, AddItemActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
      final ActionBar actionBar = getActionBar();
      mType = (Integer) actionBar.getSelectedTab().getTag(); 
      startActivity(intent);
   }

}
