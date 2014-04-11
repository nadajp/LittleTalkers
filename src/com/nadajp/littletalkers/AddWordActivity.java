package com.nadajp.littletalkers;

import android.content.Intent;
import android.os.Bundle;

import com.nadajp.littletalkers.AddWordFragment.OnAddWordListener;
import com.nadajp.littletalkers.utils.Prefs;

public class AddWordActivity extends BaseActivity implements OnAddWordListener
{
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_add_word);
   }

   @Override
   protected void setCurrentKidData(long kidId)
   {
      AddWordFragment addWordFragment = (AddWordFragment) getFragmentManager()
            .findFragmentById(R.id.add_word_fragment);
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
      intent.putExtra(Prefs.CURRENT_KID_ID, kidId);
      startActivity(intent);
   }

}
