package com.nadajp.littletalkers;

import java.util.List;

import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.OnItemSelectedListener;

public abstract class ItemListFragment extends ListFragment implements
      OnItemSelectedListener
{
   public long mCurrentKidId; // database id of current kid
   public String mSortColumn; // column to sort list by
   public String mLanguage; // current language
   public boolean mbSortAscending; // whether to sort list in ascending order

   protected String mEmptyListText; // Text to display when list is empty
   protected String mEmptyListButtonText; // Text to display on Add New button
                                          // when list is empty

   private TextView mTextHeaderPhrase; // heading for the phrase column
   private TextView mTextHeaderDate; // heading for the date column
   private Spinner mLanguageFilter; // spinner for filtering language to view

   protected MediaPlayer mPlayer; // audio player
   ListView mListView; // the list
   private static int mNumSelected = 0; // number of selected list items
                                        // (selection mode begins with long
                                        // click)
   public long[] mItemsToDelete; // array of selected list items (will be used
                                 // to delete if delete icon pressed)
   private static final int DELETE_SELECTED_WORDS_DIALOG_ID = 1;
   private static final String DEBUG_TAG = "ItemListFragment";
   protected SimpleCursorAdapter mscAdapter;
   private View mHeaderView;
   protected ListRowViewBinder mViewBinder;

   int mFragmentLayout; // res id of the layout for this fragment
   int mHeaderLayout; // res id of the layout for the header row
   int mRowLayout; // res id of the layout for list row
   int mPhraseHeaderResId; // res id of the header for the phrase column
   int mSortColumnId; // id of the column by which to sort, as defined in
                      // Prefs.java
   String mPhraseColumnName; // name of the main phrase column (i.e. word,
                             // question)

   // abstract classes for getting appropriate data from the database
   public abstract Cursor deleteFromDatabase();

   public abstract Cursor getFromDatabase();

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState)
   {
      // Inflate the layout for this fragment
      View v = inflater.inflate(mFragmentLayout, null);

      // If we are getting re-created, then get current kid from saved instance
      // state
      if (savedInstanceState != null)
      {
         mCurrentKidId = savedInstanceState.getLong(Prefs.CURRENT_KID_ID);
      }
      // Otherwise, get it from shared prefs
      else { mCurrentKidId = Prefs.getKidId(getActivity(), DbSingleton.get()
            .getLastAddedKid()); }
      Log.i(DEBUG_TAG, "Getting kid with ID: " + mCurrentKidId);
      
      mHeaderView = inflater.inflate(mHeaderLayout, null);

      mLanguageFilter = (Spinner) mHeaderView
            .findViewById(R.id.spinner_language_filter);
      List<String> languages = DbSingleton.get().getLanguages(mCurrentKidId);
      languages.add(0, this.getString(R.string.all_languages));

      ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
            this.getActivity(), R.layout.lt_spinner_item, languages);
      dataAdapter.setDropDownViewResource(R.layout.lt_spinner_dropdown_item);

      mLanguageFilter.setAdapter(dataAdapter);
      mLanguage = Prefs.getLanguage(getActivity());
      mLanguageFilter.setSelection(dataAdapter.getPosition(mLanguage));
      mLanguageFilter.setOnItemSelectedListener(this);

      // Add arrows to word and date list headers for sorting
      mTextHeaderPhrase = (TextView) mHeaderView
            .findViewById(mPhraseHeaderResId);
      mTextHeaderDate = (TextView) mHeaderView.findViewById(R.id.header_date);

      // Now do the sorting by column
      mSortColumnId = Prefs.getSortColumnId(getActivity());
      if (mSortColumnId == Prefs.SORT_COLUMN_PHRASE)
      {
         mSortColumn = mPhraseColumnName;
      } else
         mSortColumn = DbContract.Words.COLUMN_NAME_DATE;
      mbSortAscending = Prefs.getIsAscending(getActivity());

      if (mSortColumnId == Prefs.SORT_COLUMN_PHRASE)
      {
         if (mbSortAscending)
         {
            mTextHeaderPhrase.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                  android.R.drawable.arrow_up_float, 0);
         } else
         {
            mTextHeaderPhrase.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                  android.R.drawable.arrow_down_float, 0);
         }
         mTextHeaderDate.setCompoundDrawablesWithIntrinsicBounds(0, 0,
               android.R.drawable.arrow_up_float, 0);
      }

      else if (mSortColumnId == Prefs.SORT_COLUMN_DATE)
      {
         if (mbSortAscending)
         {
            mTextHeaderDate.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                  android.R.drawable.arrow_up_float, 0);
         } else
         {
            mTextHeaderDate.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                  android.R.drawable.arrow_down_float, 0);
         }
         mTextHeaderPhrase.setCompoundDrawablesWithIntrinsicBounds(0, 0,
               android.R.drawable.arrow_up_float, 0);
      }
      
      Utils.updateTitlebar(mCurrentKidId, mHeaderView, getActivity());
      return v;
   }

   // Select language from filter dropdown
   public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
   {
      mLanguage = parent.getItemAtPosition(pos).toString();
      this.changeLanguage();
   }

   public void onNothingSelected(AdapterView<?> parent)
   {
      // Another interface callback
   }

   @Override
   public void onActivityCreated(Bundle savedInstanceState)
   {
      super.onActivityCreated(savedInstanceState);
      Button addNew = (Button) getView().findViewById(R.id.button_add_word);
      addNew.setText(mEmptyListButtonText);
               
      TextView tv = (TextView) getView().findViewById(R.id.no_words);
      tv.setText(mEmptyListText);
      
      mListView = getListView();
      if (mHeaderView != null)
      {
         mListView.addHeaderView(mHeaderView);
      }
      mscAdapter.setViewBinder(mViewBinder);
      mscAdapter.notifyDataSetChanged();
      setListAdapter(mscAdapter);

      // Implement contextual menu
      mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

      mListView.setMultiChoiceModeListener(new MultiChoiceModeListener()
      {
         @Override
         public void onItemCheckedStateChanged(ActionMode mode, int position,
               long id, boolean checked)
         {
            // Here you can do something when items are selected/de-selected,
            // such as update the title in the CAB
            if (!checked)
            {
               mNumSelected--;
            } else
            {
               mNumSelected++;
            }
            mode.setTitle("Selected: " + mNumSelected);
         }

         @Override
         public boolean onActionItemClicked(ActionMode mode, MenuItem item)
         {
            // Respond to clicks on the actions in the CAB
            switch (item.getItemId())
            {
            case R.id.menu_delete:
               mItemsToDelete = mListView.getCheckedItemIds();
               deleteSelectedItems();
               mode.finish(); // Action picked, so close the CAB
               mNumSelected = 0;
               return true;
            default:
               return false;
            }
         }

         @Override
         public boolean onCreateActionMode(ActionMode mode, Menu menu)
         {
            // Inflate the menu for the CAB
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
         }

         @Override
         public void onDestroyActionMode(ActionMode mode)
         {
            mNumSelected = 0;

            // Here you can make any necessary updates to the activity when
            // the CAB is removed. By default, selected items are
            // deselected/unchecked.
         }

         @Override
         public boolean onPrepareActionMode(ActionMode mode, Menu menu)
         {
            // Here you can perform updates to the CAB due to
            // an invalidate() request
            return false;
         }
      });
   }

   public void deleteSelectedItems()
   {
      DeleteSelectedDialogFragment dlg = new DeleteSelectedDialogFragment();
      dlg.setTargetFragment(this, DELETE_SELECTED_WORDS_DIALOG_ID);
      dlg.show(getFragmentManager(), "DeleteSelectedDialogFragment");
   }

   public void confirmDelete()
   {
      Cursor cursor = deleteFromDatabase();
      SimpleCursorAdapter scAdapter = ((SimpleCursorAdapter) getListAdapter());
      scAdapter.swapCursor(cursor);
      scAdapter.notifyDataSetChanged();
   }

   @Override
   public void onListItemClick(ListView l, View v, int position, long id)
   {
      // show word detail view
      Intent intent = new Intent(this.getActivity(), ViewItemActivity.class);
      intent.putExtra(Prefs.CURRENT_KID_ID, mCurrentKidId);
      intent.putExtra(ItemDetailFragment.ITEM_ID, id);
      startActivity(intent);
   }
   
   public void changeLanguage()
   {
      Cursor newValues = getFromDatabase();
      Log.i(DEBUG_TAG, "Items: " + newValues.getCount());
      mscAdapter.swapCursor(newValues);
      mscAdapter.notifyDataSetChanged();
   }

   public void updateData(long kidId)
   {
      mCurrentKidId = kidId;
      Cursor newValues = getFromDatabase();
      mscAdapter.swapCursor(newValues);
      mscAdapter.notifyDataSetChanged();

      List<String> languages = DbSingleton.get().getLanguages(mCurrentKidId);
      languages.add(0, this.getString(R.string.all_languages));

      ArrayAdapter<String> dataAdapter = (ArrayAdapter<String>) mLanguageFilter
            .getAdapter();
      dataAdapter.clear();
      dataAdapter.addAll(languages);
      dataAdapter.notifyDataSetChanged();
      mLanguageFilter.setSelection(0);

      Utils.updateTitlebar(mCurrentKidId, mHeaderView, getActivity());
   }

   public void sortByPhrase(View v)
   {
      mSortColumnId = Prefs.SORT_COLUMN_PHRASE;
      mSortColumn = mPhraseColumnName;
      if (mbSortAscending)
      {
         mTextHeaderPhrase.setCompoundDrawablesWithIntrinsicBounds(0, 0,
               android.R.drawable.arrow_down_float, 0);
      } else
      {
         mTextHeaderPhrase.setCompoundDrawablesWithIntrinsicBounds(0, 0,
               android.R.drawable.arrow_up_float, 0);
      }
      sortList();
   }

   public void sortByDate(View v)
   {
      mSortColumnId = Prefs.SORT_COLUMN_DATE;
      mSortColumn = DbContract.Words.COLUMN_NAME_DATE;
      if (mbSortAscending)
      {
         mTextHeaderDate.setCompoundDrawablesWithIntrinsicBounds(0, 0,
               android.R.drawable.arrow_down_float, 0);
      } else
      {
         mTextHeaderDate.setCompoundDrawablesWithIntrinsicBounds(0, 0,
               android.R.drawable.arrow_up_float, 0);
      }
      sortList();
   }

   private void sortList()
   {
      mbSortAscending = !mbSortAscending;
      Cursor newValues = getFromDatabase();
      mscAdapter.swapCursor(newValues);
      mscAdapter.setViewBinder(mViewBinder);
      mscAdapter.notifyDataSetChanged();
   }

   public static class DeleteSelectedDialogFragment extends DialogFragment
   {
      @Override
      public Dialog onCreateDialog(Bundle savedInstanceState)
      {
         // Use the Builder class for convenient dialog construction
         AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
         builder
               .setMessage(R.string.delete_words_dialog)
               .setPositiveButton(R.string.delete,
                     new DialogInterface.OnClickListener()
                     {
                        public void onClick(DialogInterface dialog, int id)
                        {
                           ((ItemListFragment) getTargetFragment())
                                 .confirmDelete();
                        }
                     })
               .setNegativeButton(R.string.cancel,
                     new DialogInterface.OnClickListener()
                     {
                        public void onClick(DialogInterface dialog, int id)
                        {
                           // User cancelled the dialog
                        }
                     });
         // Create the AlertDialog object and return it
         return builder.create();
      }
   }

   @Override
   public void onSaveInstanceState(Bundle outState)
   {
      super.onSaveInstanceState(outState);
      outState.putLong(Prefs.CURRENT_KID_ID, mCurrentKidId);
   }

   @Override
   public void onDestroyView()
   {
      super.onDestroyView();
      if (mscAdapter != null)
      {
         mscAdapter.getCursor().close();
      }
      setListAdapter(null);
      mscAdapter = null;
      mTextHeaderPhrase = null;
      mTextHeaderDate = null;
      mLanguageFilter = null;
   }

   @Override
   public void onResume()
   {
      super.onResume();
      mPlayer = new MediaPlayer();
      mViewBinder.setMediaPlayer(mPlayer);
   }

   @Override
   public void onPause()
   {
      super.onPause();
      Prefs.saveAll(getActivity(), mCurrentKidId, mLanguage, mSortColumnId,
            mbSortAscending);

      if (mPlayer != null)
      {
         mPlayer.release();
         mPlayer = null;
      }
   }
}
