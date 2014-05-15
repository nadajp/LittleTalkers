package com.nadajp.littletalkers;

import android.content.Intent;
import android.os.Bundle;

import com.nadajp.littletalkers.AddItemFragment.OnAddNewPhraseListener;
import com.nadajp.littletalkers.utils.Prefs;

public class AddItemActivity extends BaseActivity implements OnAddNewPhraseListener
{
   private int mType; 
   
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_add_word);
      
      mType = getIntent().getIntExtra(Prefs.TYPE, Prefs.TYPE_WORD);
      
      if (savedInstanceState != null)
      {
         mType = savedInstanceState.getInt(Prefs.TYPE);  
         return;
      } 

      if (mType == Prefs.TYPE_WORD)
      {
         AddWordFragment addWordFragment = new AddWordFragment();
         getFragmentManager().beginTransaction().replace(R.id.fragment_container, addWordFragment).commit();
         
      }
      else if (mType == Prefs.TYPE_QA)
      {
         AddQAFragment addQAFragment = new AddQAFragment();
         getFragmentManager().beginTransaction().replace(R.id.fragment_container, addQAFragment).commit();
      }
      
   }

   @Override
   public void onSaveInstanceState(Bundle outState)
   {
      super.onSaveInstanceState(outState);      
      outState.putInt(Prefs.TYPE, mType);
   }

   @Override
   protected void setCurrentKidData(long kidId)
   {
      AddItemFragment addItemFragment = (AddItemFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
      if (addItemFragment != null)
      {
         addItemFragment.insertKidDefaults(kidId, addItemFragment.getView(), true);
      }
   }

   public void onPhraseAdded(long kidId)
   {

   }

   public void onClickedShowDictionary(long kidId)
   {
      Intent intent = new Intent(this, ItemListActivity.class);
      intent.putExtra(Prefs.CURRENT_KID_ID, kidId);
      intent.putExtra(Prefs.TYPE, mType);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
      startActivity(intent);
   }
}
