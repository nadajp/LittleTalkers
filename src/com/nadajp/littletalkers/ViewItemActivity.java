package com.nadajp.littletalkers;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

import com.nadajp.littletalkers.ItemDetailFragment.OnAddNewPhraseListener;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

public class ViewItemActivity extends Activity implements OnAddNewPhraseListener
{   
   private static final String DEBUG_TAG = "ViewItemActivity";
   private int mType;
   private ItemDetailFragment mFragment;
   
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_view_item);

      final ActionBar actionBar = getActionBar();
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setDisplayUseLogoEnabled(true);
      actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
      
      if (savedInstanceState != null)
      {
         mType = savedInstanceState.getInt(Prefs.TYPE);  
      } 
      else 
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
            Utils.setColor(actionBar, Utils.COLOR_BLUE, this);
         }
         else 
         { 
            mFragment = new QADetailFragment(); 
            Utils.setColor(actionBar, Utils.COLOR_GREEN, this);
         }
         mFragment.setArguments(arguments);
         getFragmentManager().beginTransaction()
                  .add(R.id.fragment_container, mFragment).commit();        
      }  
      
      if (mType == Prefs.TYPE_WORD) 
      { 
         Utils.setColor(actionBar, Utils.COLOR_BLUE, this);
      }
      else 
      { 
         Utils.setColor(actionBar, Utils.COLOR_GREEN, this);
      }
   }
   
   public void onPhraseAdded(long kidId)
   {
   }
   
   public void onClickedShowDictionary(long kidId)
   {

   }
   
   @Override
   public void onSaveInstanceState(Bundle outState)
   {
      super.onSaveInstanceState(outState);
      outState.putInt(Prefs.TYPE, mType);
   }
   
}
