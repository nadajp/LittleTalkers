package com.nadajp.littletalkers;

import java.util.List;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.utils.Utils;

public class DictionaryFragment extends ListFragment implements OnItemSelectedListener
{
   private static final String DEBUG_TAG = "DictionaryFragment";
   private long mCurrentKidId;            // database id of current kid
   private String mSortColumn;            // column to sort list by
   private String mLanguage;              // current language
   private boolean mbSortAscending;       // whether to sort list in ascending order
 	private TextView mHeaderWord;          // heading for the word column
 	private TextView mHeaderDate;          // heading for the date column
 	private Spinner mLanguageFilter;       // spinner for filtering language to view
	public static boolean sbPlaying = false;  // whether audio is currently playing
	private MediaPlayer mPlayer;           // audio player
	ListView mListView;                    
	private static int mNumSelected = 0;   // number of selected list items (selection mode begins with long click)
	public long[] mItemsToDelete;          // array of selected list items (will be used to delete if delete icon pressed)
	private static final int DELETE_SELECTED_WORDS_DIALOG_ID = 1;
   private SimpleCursorAdapter mscAdapter;
   private View mHeaderView;
   private SharedPreferences mSharedPrefs;
   private ListRowViewBinder mViewBinder;
   //private View mTitlebar;
	
	@Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) 
   {						   
	   // Inflate the layout for this fragment
      View v = inflater.inflate(R.layout.fragment_dictionary, null);
      mSharedPrefs = this.getActivity().getSharedPreferences(getString(R.string.shared_preferences_filename), Context.MODE_PRIVATE);
      
      // Get latest kid id from shared preferences
		long latestKidId = mSharedPrefs.getLong(getString(R.string.current_kid_id), DbSingleton.get().getLastAddedKid());	    
      
		// If we are getting re-created, then get current kid from saved instance state
		if (savedInstanceState != null)
		{
        	mCurrentKidId = savedInstanceState.getLong(getString(R.string.current_kid_id));
	    	Log.i(DEBUG_TAG, "Retrieving Instance State: " + mCurrentKidId);	
		}
      // Otherwise, get it from intent, or if not available, then get latest from shared prefs
		else if (this.getActivity().getIntent() != null) 
		{
	      mCurrentKidId = this.getActivity().getIntent().getLongExtra(getString(R.string.current_kid_id), latestKidId);
		}
      else 
      {
         mCurrentKidId = -1;    
      }
		
		Log.i(DEBUG_TAG, "kid id in dictionary = " + mCurrentKidId);

		mHeaderView = inflater.inflate(R.layout.dictionary_header, null);
		
		mLanguageFilter = (Spinner) mHeaderView.findViewById(R.id.spinnerLanguageFilter);
      List<String> languages = DbSingleton.get().getLanguages(mCurrentKidId);
      languages.add(0, this.getString(R.string.all_languages));

      ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(),
                  android.R.layout.simple_spinner_item, languages);
      dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      		
      mLanguageFilter.setAdapter(dataAdapter);   
      mLanguage = mSharedPrefs.getString(getString(R.string.language_filter), this.getString(R.string.all_languages));
      mLanguageFilter.setSelection(dataAdapter.getPosition(mLanguage));
      mLanguageFilter.setOnItemSelectedListener(this);
      
      mViewBinder = new ListRowViewBinder(mPlayer);

	    // Add arrows to word and date list headers for sorting
      mHeaderWord = (TextView) mHeaderView.findViewById(R.id.header_word);
      mHeaderDate = (TextView) mHeaderView.findViewById(R.id.header_date);
	    
      // Now do the sorting by column
      mSortColumn = mSharedPrefs.getString(getString(R.string.sort_column), DbContract.Words.COLUMN_NAME_WORD);
      mbSortAscending = mSharedPrefs.getBoolean(getString(R.string.sort_ascending), true);
       
      if (mSortColumn.equals(DbContract.Words.COLUMN_NAME_WORD))
      {
         if (mbSortAscending) 
         {
            mHeaderWord.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_up_float, 0);
         }
         else 
         { 
            mHeaderWord.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0);
         }
         mHeaderDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_up_float, 0);
      }
      
      else if (mSortColumn.equals(DbContract.Words.COLUMN_NAME_DATE))
      {
         if (mbSortAscending) 
         {
            mHeaderDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_up_float, 0);
         }
         else 
         {
            mHeaderDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0);
         }
         mHeaderWord.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_up_float, 0);
      }
      Utils.updateTitlebar(mCurrentKidId, mHeaderView, this.getActivity());
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
    
      Cursor cursor = DbSingleton.get().getWords(mCurrentKidId, mSortColumn, mbSortAscending, mLanguage);

 	   String[] adapterCols = new String[]{DbContract.Words.COLUMN_NAME_WORD,
 	   									        DbContract.Words.COLUMN_NAME_DATE, 
 	   									        DbContract.Words.COLUMN_NAME_AUDIO_FILE };
 	   int[] adapterRowViews = new int[]{R.id.dictionary_word, R.id.dictionary_word_date, R.id.dictionary_audio_button};
 	  
 	   mscAdapter = new SimpleCursorAdapter(this.getActivity(), R.layout.dictionary_row, cursor, adapterCols, adapterRowViews,0); 	   	
 	   mscAdapter.setViewBinder(mViewBinder);

      mListView = getListView();
      
      if (mHeaderView != null) 
      {
         mListView.addHeaderView(mHeaderView);
      }
      this.setListAdapter(mscAdapter);  
 	    
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
             }
             else 
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
                	  Log.i(DEBUG_TAG, "Items to delete: " + mItemsToDelete.length);   
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
               // the CAB is removed. By default, selected items are deselected/unchecked.
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
      Log.i(DEBUG_TAG, "Items to delete: " + mItemsToDelete.length);
		DbSingleton.get().deleteWords(mItemsToDelete);
		Cursor cursor = DbSingleton.get().getWords(mCurrentKidId, mSortColumn, mbSortAscending, mLanguage);
		SimpleCursorAdapter scAdapter = ((SimpleCursorAdapter)getListAdapter());
		scAdapter.swapCursor(cursor);
		scAdapter.notifyDataSetChanged();		
	}

   @Override
   public void onListItemClick(ListView l, View v, int position, long id) 
   {
      // show word detail view
    	Intent intent = new Intent(this.getActivity(), AddWordActivity.class);
		intent.putExtra(getString(R.string.current_kid_id), mCurrentKidId);
      intent.putExtra("word_id", id);		
      startActivity(intent);    
   }
		 
   public void changeLanguage()
   {
      Cursor newValues = DbSingleton.get().getWords(mCurrentKidId, mSortColumn, mbSortAscending, mLanguage);
		mscAdapter.swapCursor(newValues);
		mscAdapter.notifyDataSetChanged();
   }
     
	public void updateData(long kidId)
	{
      mCurrentKidId = kidId;
		Cursor newValues = DbSingleton.get().getWords(kidId, mSortColumn, mbSortAscending, mLanguage);
		mscAdapter.swapCursor(newValues);
		mscAdapter.notifyDataSetChanged(); 
		
		List<String> languages = DbSingleton.get().getLanguages(mCurrentKidId);
		languages.add(0, this.getString(R.string.all_languages));
		
		ArrayAdapter<String> dataAdapter = (ArrayAdapter<String>) mLanguageFilter.getAdapter();
		dataAdapter.clear();
		dataAdapter.addAll(languages);
		dataAdapter.notifyDataSetChanged();
		mLanguageFilter.setSelection(0);
		
		Utils.updateTitlebar(mCurrentKidId, mHeaderView, this.getActivity());
   }
	
	/*
   private class DictionaryRowViewBinder implements ViewBinder 
	{
	   @Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) 
		{				
	      if (columnIndex == cursor.getColumnIndex(DbContract.Words.COLUMN_NAME_DATE))
         {
            String rawdate = cursor.getString(columnIndex);
            Log.i(DEBUG_TAG, "RAW DATE: " + rawdate);
            String[] dateArray = rawdate.split("-");
            Calendar date = Calendar.getInstance();
            date.set(Calendar.YEAR, Integer.parseInt(dateArray[0]));
            date.set(Calendar.MONTH, Integer.parseInt(dateArray[1]));
            date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[2]));
            
            String formatted = DateUtils.formatDateTime(view.getContext(),
                                     date.getTimeInMillis(),
                                     DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_SHOW_YEAR);
            Log.i(DEBUG_TAG, formatted);     
            TextView txt = (TextView) view;
            txt.setText(formatted);
            return true;
         }
	      
	      else if (columnIndex == cursor.getColumnIndex(DbContract.Words.COLUMN_NAME_AUDIO_FILE)) 
		   {
	         // If the column is COLUMN_NAME_AUDIO_FILE then we use custom view.
		      String mAudioFile = cursor.getString(columnIndex);

		      if (mAudioFile.isEmpty()) 
		      {
		         view.setVisibility(View.INVISIBLE);
		      }
		        	
            else if (!mAudioFile.isEmpty()) 
		      {
               view.setFocusable(false);
               view.setFocusableInTouchMode(false);
               // set the visibility of the view to visible
               view.setVisibility(View.VISIBLE);
               view.setOnClickListener(new  MyListener(mAudioFile)); 
            }	
		      return true;
		   }		      
	     
	      // For others, we simply return false so that the default binding happens.
		   return false;
		}
			    
		private class MyListener implements OnClickListener, OnCompletionListener 
		{
		   private String mAudioFile;

		   public MyListener(String audioFile)
		   {
		      this.mAudioFile = audioFile;
		   }

		   @Override
			public void onClick(View v) 	
		   {
		      if (sbPlaying)
		         Stop();
				v.setPressed(true);
			 	try 
			   {
			 	   if (mPlayer == null)
			 	      Log.i(DEBUG_TAG, "Media Player is null in onClick");
			 	   mPlayer.setDataSource(mAudioFile);
					mPlayer.setOnCompletionListener(this);
			      Log.i(DEBUG_TAG, "Started Playing " + mAudioFile);
			      mPlayer.prepare();
			      mPlayer.start();
			      sbPlaying = true;
			    } 
			 	 catch (IOException e) {
			         Log.e(DEBUG_TAG, "Audio player start failed");
			    }	
		   }	

         public void onCompletion(MediaPlayer mp) 
         { 
            Stop();
         }	
            	 
         public void Stop()
         {
            if (mPlayer == null)
               Log.i(DEBUG_TAG, "Media Player is null in Stop()");
            mPlayer.stop();
            mPlayer.reset();
            sbPlaying = false;
         }        	 
		}
   }
	*/	
   public void sortByWord(View v)
	{
      this.mSortColumn = DbContract.Words.COLUMN_NAME_WORD;
      if (mbSortAscending) 
      {
      	mHeaderWord.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0);
      }
      else 
      {
      	mHeaderWord.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_up_float, 0);
      }
      sortList(DbContract.Words.COLUMN_NAME_WORD);			
	}
		
   public void sortByDate(View v)
   {
   	this.mSortColumn = DbContract.Words.COLUMN_NAME_DATE;
   	if (mbSortAscending) 
   	{
   		mHeaderDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0);
   	}
   	else 
   	{
   		mHeaderDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_up_float, 0);
   	}
   	sortList(DbContract.Words.COLUMN_NAME_DATE);			
   }
		
   private void sortList(String columnName)
   {
   	mbSortAscending = !mbSortAscending;
   	Cursor newValues = DbSingleton.get().getWords(mCurrentKidId, columnName, mbSortAscending, mLanguage);				
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
         builder.setMessage(R.string.delete_words_dialog)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() 
                {
                   public void onClick(DialogInterface dialog, int id) 
                   {	                    	   
                      ((DictionaryFragment)getTargetFragment()).confirmDelete();          	   
                   }
                })
               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() 
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
   	outState.putLong(getString(R.string.current_kid_id), mCurrentKidId);
    	Log.i(DEBUG_TAG, "Saving Instance State: " + mCurrentKidId);
   }
    
   @Override
   public void onDestroyView() 
   {
      super.onDestroyView();
   	mscAdapter.getCursor().close();
      mHeaderWord = null;
   	mHeaderDate = null;
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
      SharedPreferences sharedPrefs = this.getActivity().getSharedPreferences(getString(R.string.shared_preferences_filename), Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPrefs.edit();
      editor.putString(getString(R.string.sort_column), this.mSortColumn);
      editor.putBoolean(getString(R.string.sort_ascending), this.mbSortAscending);
      editor.putString(getString(R.string.language_filter), mLanguage);
      editor.putLong(getString(R.string.current_kid_id), mCurrentKidId);
      editor.commit();
      if (mPlayer != null) 
      {
         mPlayer.release();
     	   mPlayer = null;
      }
   }	
}
