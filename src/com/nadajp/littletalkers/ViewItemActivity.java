package com.nadajp.littletalkers;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.nadajp.littletalkers.ItemDetailFragment.OnAddNewPhraseListener;
import com.nadajp.littletalkers.utils.Prefs;

public class ViewItemActivity extends Activity implements OnAddNewPhraseListener
{   
   private int mType;
   private ItemDetailFragment mFragment;
   
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_add_word);

      final ActionBar actionBar = getActionBar();
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setDisplayUseLogoEnabled(false);
      
      if (savedInstanceState != null)
      {
         mType = savedInstanceState.getInt(Prefs.TYPE);  
      } else 
      { 
         mType = Prefs.getType(this, Prefs.TYPE_WORD);
         // Create the detail fragment and add it to the activity
         // using a fragment transaction.
         Bundle arguments = new Bundle();
         arguments.putLong(ItemDetailFragment.ITEM_ID, getIntent()
                  .getLongExtra(ItemDetailFragment.ITEM_ID, 0));
         if (mType == Prefs.TYPE_WORD) 
         { 
            mFragment = new WordDetailFragment(); 
         }
         else 
         { 
            mFragment = new QADetailFragment(); 
         }
         mFragment.setArguments(arguments);
         getFragmentManager().beginTransaction()
                  .add(R.id.fragment_container, mFragment).commit();
         
      }                  
   }
   
   public void onPhraseAdded(long kidId)
   {

   }

   public void onClickedShowDictionary(long kidId)
   {
      Intent intent = new Intent(this, ItemListActivity.class);
      intent.putExtra(Prefs.CURRENT_KID_ID, kidId);
      intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
      //final ActionBar actionBar = getActionBar();
      mType = Prefs.getType(this, Prefs.TYPE_WORD); 
      startActivity(intent);
   }
}
