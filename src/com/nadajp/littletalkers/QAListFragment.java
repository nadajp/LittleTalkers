package com.nadajp.littletalkers;

import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class QAListFragment extends ItemListFragment
{
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState)
   {
      mFragmentLayout = R.layout.fragment_qa_list;
      mRowLayout = R.layout.qa_list_row;
      mPhraseColumnName = DbContract.Questions.COLUMN_NAME_QUESTION;
      mEmptyListText = getString(R.string.no_qa);
      mEmptyListButtonText = getString(R.string.add_new_qa); 
      mViewBinder = new ListRowViewBinder(mPlayer);

      if (Prefs.getSortColumnId(getActivity()) == Prefs.SORT_COLUMN_PHRASE)
      {
         mSortColumn = DbContract.Words.COLUMN_NAME_WORD;
      }
      else mSortColumn = DbContract.Words.COLUMN_NAME_DATE;

      return super.onCreateView(inflater, container, savedInstanceState);
   }
   
   @SuppressLint("NewApi")
   @Override
   public void onActivityCreated(Bundle savedInstanceState)
   {
      insertData();
      super.onActivityCreated(savedInstanceState);
   }
   
   public void insertData()
   {
      Cursor cursor = DbSingleton.get().getQuestions(mCurrentKidId, mSortColumn,
            mbSortAscending, mLanguage);

      String[] adapterCols = new String[] { DbContract.Questions.COLUMN_NAME_QUESTION,
            DbContract.Questions.COLUMN_NAME_ANSWER,
            DbContract.Questions.COLUMN_NAME_DATE,
            DbContract.Questions.COLUMN_NAME_AUDIO_FILE };
      int[] adapterRowViews = new int[] { R.id.question, R.id.answer, 
            R.id.dictionary_word_date, R.id.dictionary_audio_button };

      if (mscAdapter == null)
      {
         mscAdapter = new SimpleCursorAdapter(this.getActivity(),
               R.layout.qa_list_row, cursor, adapterCols, adapterRowViews, 0);
      }
       
   }


   public Cursor deleteFromDatabase()
   {
      DbSingleton.get().deleteQuestions(mItemsToDelete);
      return getFromDatabase();  
   }
   
   public Cursor getFromDatabase()
   {
      return DbSingleton.get().getQuestions(mCurrentKidId, mSortColumn,
            mbSortAscending, mLanguage);   
   }
}
