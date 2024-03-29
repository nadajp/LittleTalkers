package com.nadajp.littletalkers;

import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.utils.Prefs;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

public class WordListFragment extends ItemListFragment
{  
   private static final String DEBUG_TAG = "WordListFragment";
   
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState)
   {
      mFragmentLayout = R.layout.fragment_dictionary;
      mRowLayout = R.layout.dictionary_row;
      mPhraseColumnName = DbContract.Words.COLUMN_NAME_WORD;
      mEmptyListText = getString(R.string.no_words);
      mEmptyListButtonText = getString(R.string.add_word); 
      
      mViewBinder = new ListRowViewBinder(mPlayer);
 
      if (Prefs.getSortColumnId(getActivity()) == Prefs.SORT_COLUMN_PHRASE)
      {
         mSortColumn = DbContract.Words.COLUMN_NAME_WORD;
      }
      else mSortColumn = DbContract.Words.COLUMN_NAME_DATE;

      return super.onCreateView(inflater, container, savedInstanceState);      
   }
   
   @Override
   public void onActivityCreated(Bundle savedInstanceState)
   {
      insertData();
      super.onActivityCreated(savedInstanceState);
   }
   
   public void insertData()
   {
      Cursor cursor = DbSingleton.get().getWords(mCurrentKidId, mSortColumn,
            mbSortAscending, mLanguage);

      if (cursor == null || cursor.isLast())
      {
         //Log.i(DEBUG_TAG, "No DATA!");
      }
      String[] adapterCols = new String[] { DbContract.Words.COLUMN_NAME_WORD,
               DbContract.Words.COLUMN_NAME_DATE,
               DbContract.Words.COLUMN_NAME_AUDIO_FILE };
         
      int[] adapterRowViews = new int[] { R.id.dictionary_word,
               R.id.dictionary_word_date, R.id.dictionary_audio_button };
      
      if (mscAdapter == null)
      {
         mscAdapter = new SimpleCursorAdapter(this.getActivity(),
               R.layout.dictionary_row, cursor, adapterCols, adapterRowViews, 0);
      }
   }
   
   public Cursor deleteFromDatabase()
   {
      DbSingleton.get().deleteWords(mItemsToDelete);
      return getFromDatabase();  
   }
   
   public Cursor getFromDatabase()
   {
      return DbSingleton.get().getWords(mCurrentKidId, mSortColumn,
            mbSortAscending, mLanguage);   
   } 

}
