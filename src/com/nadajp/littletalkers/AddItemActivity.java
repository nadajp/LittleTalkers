package com.nadajp.littletalkers;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.nadajp.littletalkers.ItemDetailFragment.OnAddNewPhraseListener;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

public class AddItemActivity extends BaseActivity implements OnAddNewPhraseListener
{   
   
   private static final String DEBUG_TAG = "AddItemActivity";

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_add_word);

      final ActionBar actionBar = getActionBar();

      ItemDetailFragment addWordFragment = new WordDetailFragment();
      Tab addWordTab = actionBar.newTab().setText(R.string.word_or_phrase);
      addWordTab.setTag(Prefs.TYPE_WORD);
      actionBar.addTab(addWordTab
            .setTabListener(new MyTabListener(addWordFragment)));

      ItemDetailFragment addQAFragment = new QADetailFragment();
      Tab addQATab = actionBar.newTab().setText(R.string.q_and_a);
      addQATab.setTag(Prefs.TYPE_QA);
      actionBar.addTab(addQATab
            .setTabListener(new MyTabListener(addQAFragment)));  
      
      if (mType == Prefs.TYPE_WORD) 
      { 
         actionBar.selectTab(addWordTab); 
         Utils.setColor(actionBar, Utils.COLOR_BLUE, this);
      }
      else { 
         actionBar.selectTab(addQATab); 
         Utils.setColor(actionBar, Utils.COLOR_GREEN, this);
      }
   }

   @Override
   protected void setCurrentKidData(long kidId)
   {
      ItemDetailFragment addItemFragment = (ItemDetailFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
      
      if (addItemFragment != null)
      {
         addItemFragment.insertKidDefaults(kidId, addItemFragment.getView());
      }
   }

   public void onPhraseAdded(long kidId)
   {

   }

   public void onClickedShowDictionary(long kidId)
   {
      Prefs.saveKidId(getApplicationContext(), kidId);
      Log.i(DEBUG_TAG, "Saved ID: " + kidId);
      Intent intent = new Intent(this, ItemListActivity.class);
      intent.putExtra(Prefs.CURRENT_KID_ID, kidId);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
      mType = Prefs.getType(this, Prefs.TYPE_WORD);
      startActivity(intent);
   }
}
