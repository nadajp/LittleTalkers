package com.nadajp.littletalkers;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.utils.Utils;

public class WordDetailFragment extends ItemDetailFragment
{
   private static final String DEBUG_TAG = "AddWordFragment";

   private EditText mEditTranslation;
   
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState)
   {
      mFragmentLayout = R.layout.fragment_word_detail;
      mEditPhraseResId = R.id.editWord;
      ActionBar actionBar = this.getActivity().getActionBar();
      Utils.setColor(actionBar, Utils.COLOR_BLUE, this.getActivity());      
      return super.onCreateView(inflater, container, savedInstanceState); 
   }
   
   public void initializeExtras(View v)
   {
      mEditTranslation = (EditText) v.findViewById(R.id.editTranslation);
   }

   public void setShareData(String data)
   {
      Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
      sharingIntent.setType("text/plain");

      String shareBody = "On " + mEditDate.getText().toString() + ", "
            + mKidName + " said: " + mEditPhrase.getText().toString();

      if (mEditTranslation.getText().toString()
            .compareTo(mEditPhrase.getText().toString()) != 0)
      {
         shareBody += ", which means " + mEditTranslation.getText().toString()
               + ".\n";
      } else
      {
         shareBody += ".\n";
      }

      sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
            "New Words From " + mKidName);
      sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
      /*
       * if (mOutFile != null) { Uri uri = Uri.fromFile(mOutFile);
       * sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
       * sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
       * 
       * }
       */
      if (mShareActionProvider != null)
      {
         mShareActionProvider.setShareIntent(sharingIntent);
      }
   }

   public String getShareBody()
   {
      String shareBody = "On " + mEditDate.getText().toString() + ", "
            + mKidName + " said: " + mEditPhrase.getText().toString();

      if (mEditTranslation.getText().toString()
            .compareTo(mEditPhrase.getText().toString()) != 0)
      {
         shareBody += ", which means " + mEditTranslation.getText().toString()
               + ".\n";
      } else
      {
         shareBody += ".\n";
      }
      return shareBody;
   }
   
   public boolean savePhrase()
   {
      Log.i(DEBUG_TAG, "in savePhrase...");

      if (mEditPhrase.length() == 0)
      {
         mEditPhrase.requestFocus();
         mEditPhrase.setError(getString(R.string.word_required_error));
         return false;
      }

      // convert date to miliseconds for SQLite
      long msDate = mDate.getTimeInMillis();

      String phrase = mEditPhrase.getText().toString();
      String location = mEditLocation.getText().toString();
      String translation = mEditTranslation.length() == 0 ? phrase
            : mEditTranslation.getText().toString();
      String towhom = mEditToWhom.getText().toString();
      String notes = mEditNotes.getText().toString();

      // if adding new word, save it here
      if (mItemId == 0)
      {
         if (DbSingleton.get().saveWord(mCurrentKidId, phrase, mLanguage, msDate,
               location, mCurrentAudioFile, translation, towhom, notes) == false)
         {
            mEditPhrase.requestFocus();
            mEditPhrase.setError(getString(R.string.word_already_exists_error));
            return false;
         }

         // word was saved successfully
         Toast toast = Toast.makeText(this.getActivity(), R.string.word_saved,
               Toast.LENGTH_LONG);
         toast.show();
         return true;
      }

      else
      // we are editing an existing entry
      {
         Log.i(DEBUG_TAG, "updating word with audio file " + mCurrentAudioFile);
         if (DbSingleton.get().updateWord(mItemId, mCurrentKidId, phrase,
               mLanguage, msDate, location, mCurrentAudioFile, translation, towhom,
               notes) == false)
         {
            mEditPhrase.requestFocus();
            mEditPhrase.setError(getString(R.string.word_already_exists_error));
            return false;
         }
         // Word was updated successfully, show dictionary
         Toast toast = Toast.makeText(this.getActivity(),
               R.string.word_updated, Toast.LENGTH_LONG);
         toast.show();
         // invalidate menu to add sharing capabilities
         this.getActivity().invalidateOptionsMenu();
         return true;
      }
   }
   
   public void clearExtraViews()
   {
      mEditTranslation.setText(""); 
   } 
   
   public void updateExtraKidDetails()
   {
      mTextHeading.setText(mKidName + " " + getString(R.string.said_something));
   }

   public void insertItemDetails(View v)
   {
      Log.i(DEBUG_TAG, "Inserting word details");
      Cursor cursor = DbSingleton.get().getWordDetails(mItemId);
      cursor.moveToFirst();
      mEditPhrase.setText(cursor.getString(cursor
            .getColumnIndex(DbContract.Words.COLUMN_NAME_WORD)));

      // get date in miliseconds from db, convert to text, set current date
      long rawdate = cursor.getLong(cursor
            .getColumnIndex(DbContract.Words.COLUMN_NAME_DATE));
      mEditDate.setText(Utils.getDateForDisplay(rawdate, this.getActivity()));
      mDate.setTimeInMillis(rawdate);

      // mEditDate.setText(cursor.getString(cursor.getColumnIndex(DbContract.Words.COLUMN_NAME_DATE)).toString());
      mEditLocation.setText(cursor.getString(
            cursor.getColumnIndex(DbContract.Words.COLUMN_NAME_LOCATION))
            .toString());
      mEditTranslation.setText(cursor.getString(
            cursor.getColumnIndex(DbContract.Words.COLUMN_NAME_TRANSLATION))
            .toString());
      mEditToWhom.setText(cursor.getString(
            cursor.getColumnIndex(DbContract.Words.COLUMN_NAME_TOWHOM))
            .toString());
      mEditNotes.setText(cursor.getString(
            cursor.getColumnIndex(DbContract.Words.COLUMN_NAME_NOTES))
            .toString());

      ArrayAdapter<String> adapter = (ArrayAdapter<String>) mLangSpinner
            .getAdapter();
      mLangSpinner.setSelection(adapter.getPosition(cursor.getString(cursor
            .getColumnIndex(DbContract.Words.COLUMN_NAME_LANGUAGE))));

      mCurrentAudioFile = cursor.getString(cursor
            .getColumnIndex(DbContract.Words.COLUMN_NAME_AUDIO_FILE));
      
      mTextHeading.setText(mKidName + getString(R.string.said_something) + "?");
      cursor.close();

      displayWordHistory(v);
   }

   private void displayWordHistory(View v)
   {
      Cursor cursor = DbSingleton.get().getWordHistory(mCurrentKidId, mItemId);

      if (cursor.getCount() < 2) return;

      TextView title = (TextView) v.findViewById(R.id.txtWordHistory);
      title.setVisibility(View.VISIBLE);

      RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.relative_layout);

      int id = R.id.txtWordHistory;
      // looping through all rows and adding to list
      if (cursor.moveToFirst())
      {
         do
         {
            LinearLayout ll = new LinearLayout(getActivity());
            ll.setOrientation(LinearLayout.HORIZONTAL);

            RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
                  LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

            rlParams.addRule(RelativeLayout.BELOW, id);
            int sideMargin = (int) (20 * getActivity().getResources()
                  .getDisplayMetrics().density);
            int bottomMargin = (int) (10 * getActivity().getResources()
                  .getDisplayMetrics().density);
            rlParams.setMargins(sideMargin, 0, sideMargin, bottomMargin);
            ll.setLayoutParams(rlParams);

            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                  LayoutParams.WRAP_CONTENT);

            TextView txtWord = new TextView(getActivity());
            TextView txtDate = new TextView(getActivity());

            txtWord.setLayoutParams(params);
            txtDate.setLayoutParams(params);

            txtDate.setPadding(0, 0, 20, 0);

            txtWord.setText(cursor.getString(cursor
                  .getColumnIndex(DbContract.Words.COLUMN_NAME_WORD)));

            long rawdate = cursor.getLong(cursor
                  .getColumnIndex(DbContract.Words.COLUMN_NAME_DATE));
            txtDate
                  .setText(Utils.getDateForDisplay(rawdate, this.getActivity()));

            txtDate.setTextSize(16);
            txtWord.setTextSize(16);

            ll.addView(txtDate);
            ll.addView(txtWord);
            ll.setId(Utils.generateViewId());
            id = ll.getId();

            rl.addView(ll);

         } while (cursor.moveToNext());
      }
      cursor.close();
   }
}
