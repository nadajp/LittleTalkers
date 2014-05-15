package com.nadajp.littletalkers;

import com.nadajp.littletalkers.utils.Prefs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ItemListActivity extends BaseActivity
{
   private static String DEBUG_TAG = "PhraseList Activity";
   private int mType;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_dictionary);
      Log.i(DEBUG_TAG, "Entering dictionary...");

      mType = getIntent().getIntExtra(Prefs.TYPE, Prefs.TYPE_WORD);

      if (savedInstanceState != null)
      {
         mType = savedInstanceState.getInt(Prefs.TYPE);
         return;
      }

      if (mType == Prefs.TYPE_WORD)
      {
         WordListFragment wordListFragment = new WordListFragment();
         getFragmentManager().beginTransaction()
               .replace(R.id.dictionary_fragment_container, wordListFragment)
               .commit();

      } else if (mType == Prefs.TYPE_QA)
      {
         QAListFragment qaListFragment = new QAListFragment();
         getFragmentManager().beginTransaction()
               .replace(R.id.dictionary_fragment_container, qaListFragment).commit();
      }
   }

   @Override
   protected void setCurrentKidData(long kidId)
   {
      ItemListFragment listFragment = (ItemListFragment) getFragmentManager()
            .findFragmentById(R.id.dictionary_fragment_container);
         if (listFragment != null)
         {
            listFragment.updateData(kidId);
         }
   }

   public void sortByWord(View v)
   {
      ItemListFragment listFragment = (ItemListFragment) getFragmentManager()
            .findFragmentById(R.id.dictionary_fragment_container);
      if (listFragment != null)
      {
         listFragment.sortByPhrase(v);
      }
   }

   public void sortByDate(View v)
   {
      ItemListFragment listFragment = (ItemListFragment) getFragmentManager()
            .findFragmentById(R.id.dictionary_fragment_container);
      if (listFragment != null)
      {
         listFragment.sortByDate(v);
      }
   }

   public void addNewWord(View view)
   {
      Intent intent = new Intent(this, AddItemActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
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
