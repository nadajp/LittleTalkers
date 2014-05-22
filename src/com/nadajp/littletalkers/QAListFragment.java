package com.nadajp.littletalkers;

import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.utils.Prefs;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class QAListFragment extends ItemListFragment
{
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState)
   {
      mFragmentLayout = R.layout.fragment_dictionary;
      mHeaderLayout = R.layout.qa_list_header;
      mRowLayout = R.layout.qa_list_row;
      mPhraseHeaderResId = R.id.header_qa;
      mPhraseColumnName = DbContract.Questions.COLUMN_NAME_QUESTION;
      mEmptyListText = getString(R.string.no_qa);
      mEmptyListButtonText = getString(R.string.add_new_qa); 
      mViewBinder = new ListRowViewBinder(mPlayer);

      if (Prefs.getSortColumnId(getActivity()) == Prefs.SORT_COLUMN_PHRASE)
      {
         mSortColumn = DbContract.Words.COLUMN_NAME_WORD;
      }
      else mSortColumn = DbContract.Words.COLUMN_NAME_DATE;
      //setHasOptionsMenu(true);
      
      return super.onCreateView(inflater, container, savedInstanceState);
   }
   
   @Override
   public void onActivityCreated(Bundle savedInstanceState)
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
      
      /*if (cursor.getCount() == 0)
      {
         Button addNew = (Button) getView().findViewById(R.id.button_add_word);
         addNew.setText(getString(R.string.add_new_qa));
                  
         TextView tv = (TextView) getView().findViewById(R.id.no_words);
         tv.setText(getString(R.string.no_qa));
      }*/
      
      super.onActivityCreated(savedInstanceState);

      /*
      if (cursor.getCount() > 0)
      {
         cursor.moveToFirst();
         if (cursor.getInt(cursor.getColumnIndex(DbContract.Questions.COLUMN_NAME_ASKED)) == 1)
         {
            TextView question = (TextView) getActivity().findViewById(R.id.question);
            question.setTypeface(null, Typeface.BOLD);
         }
         if (cursor.getInt(cursor.getColumnIndex(DbContract.Questions.COLUMN_NAME_ANSWERED)) == 1)
         {
            TextView answer = (TextView) getActivity().findViewById(R.id.answer);
            answer.setTypeface(null, Typeface.BOLD);
         }
      }*/
   }
   
   
   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      inflater.inflate(R.menu.qa_list, menu);
      super.onCreateOptionsMenu(menu, inflater);
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
