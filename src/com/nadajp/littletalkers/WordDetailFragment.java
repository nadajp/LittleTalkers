package com.nadajp.littletalkers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.annotation.SuppressLint;
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

import com.google.gson.Gson;
import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.server.littletalkersapi.model.Word;
import com.nadajp.littletalkers.utils.Prefs;
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
      mTempFileStem = "temp";
      return super.onCreateView(inflater, container, savedInstanceState);
   }

   public void initializeExtras(View v)
   {
      mEditTranslation = (EditText) v.findViewById(R.id.editTranslation);
   }

   public void startAudioRecording(boolean secondRecording)
   {
      Intent intent = new Intent(this.getActivity(), AudioRecordActivity.class);
      intent.putExtra(Prefs.TYPE, Prefs.TYPE_WORD);
      intent.putExtra(Prefs.TEMP_FILE_STEM, mTempFileStem);
      intent.putExtra(Prefs.SECOND_RECORDING, secondRecording);
      startActivityForResult(intent, RECORD_AUDIO_REQUEST);
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
   
   public long savePhrase(boolean automatic)
   {
      if (mEditPhrase.length() == 0)
      {
         mEditPhrase.requestFocus();
         mEditPhrase.setError(getString(R.string.word_required_error));
         return -1;
      }

      super.saveAudioFile();

      // convert date to miliseconds for SQLite
      long msDate = mDate.getTimeInMillis();

      String phrase = mEditPhrase.getText().toString();
      String location = mEditLocation.getText().toString();
      String translation = mEditTranslation.length() == 0 ? phrase
            : mEditTranslation.getText().toString();
      String towhom = mEditToWhom.getText().toString();
      String notes = mEditNotes.getText().toString();
      
      Word word = new Word();
      word.setKidId((long) mCurrentKidId);
      word.setWord(phrase);
      word.setLocation(location);
      word.setTranslation(translation);
      word.setToWhom(towhom);
      word.setNotes(notes);
      word.setDate(msDate);
      word.setAudioFileUri(mCurrentAudioFile);        

      // if adding new word, save it here
      if (mItemId == 0)
      {
         //mItemId = DbSingleton.get().saveWord(mCurrentKidId, phrase, mLanguage,
         //      msDate, location, mCurrentAudioFile, translation, towhom, notes);
         mItemId = DbSingleton.get().saveWord(word);

         if (mItemId == -1)
         {
            if (!automatic)
            {
               mEditPhrase.requestFocus();
               mEditPhrase
                     .setError(getString(R.string.word_already_exists_error));
            }
            return -1;
         }

         // word was saved successfully
         Toast toast = Toast.makeText(this.getActivity(), R.string.word_saved,
               Toast.LENGTH_LONG);
         toast.show();
         return mItemId;
      }

      else
      // we are editing an existing entry
      {
         //Log.i(DEBUG_TAG, "updating word with audio file " + mCurrentAudioFile);
         //Log.i(DEBUG_TAG, "updating word with language: " + mLanguage);
         if (DbSingleton.get().updateWord(mItemId, mCurrentKidId, phrase,
               mLanguage, msDate, location, mCurrentAudioFile, translation,
               towhom, notes) == false)
         {
            if (!automatic)
            {
               mEditPhrase.requestFocus();
               mEditPhrase
                     .setError(getString(R.string.word_already_exists_error));
            }
            return -1;
         }
         // Word was updated successfully
         Toast toast = Toast.makeText(this.getActivity(),
               R.string.word_updated, Toast.LENGTH_LONG);
         toast.show();
         // invalidate menu to add sharing capabilities
         this.getActivity().invalidateOptionsMenu();
         return mItemId;
      }
   }

   public void saveToPrefs()
   {
      // convert date to miliseconds for SQLite
      long msDate = mDate.getTimeInMillis();

      String phrase = mEditPhrase.getText().toString();

      String location = mEditLocation.getText().toString();
      String translation = mEditTranslation.length() == 0 ? phrase
            : mEditTranslation.getText().toString();
      String towhom = mEditToWhom.getText().toString();
      String notes = mEditNotes.getText().toString();

      String audioFile = mCurrentAudioFile;

      Prefs.savePhraseInfo(this.getActivity(), msDate, phrase, location,
            translation, towhom, notes, audioFile);
   }

   public void clearExtraViews()
   {
      mEditTranslation.setText("");
   }

   public void updateExtraKidDetails()
   {
      if (this.mItemId > 0)
      {
         mTextHeading.setText(mKidName + " " + getString(R.string.said) + ":");
      } else
      {
         mTextHeading.setText(mKidName + " "
               + getString(R.string.said_something) + " ?");
      }
   }

   public void insertItemDetails(View v)
   {
      //Log.i(DEBUG_TAG, "Inserting word details");
      Cursor cursor = DbSingleton.get().getWordDetails(mItemId);
      cursor.moveToFirst();
      mEditPhrase.setText(cursor.getString(cursor
            .getColumnIndex(DbContract.Words.COLUMN_NAME_WORD)));

      // get date in milliseconds from db, convert to text, set current date
      long rawdate = cursor.getLong(cursor
            .getColumnIndex(DbContract.Words.COLUMN_NAME_DATE));
      mEditDate.setText(Utils.getDateForDisplay(rawdate, this.getActivity()));
      mDate.setTimeInMillis(rawdate);
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

      /*Log.i(DEBUG_TAG,
            "Language from DB: "
                  + cursor.getString(cursor
                        .getColumnIndex(DbContract.Words.COLUMN_NAME_LANGUAGE)));
      */
      ArrayAdapter<String> adapter = (ArrayAdapter<String>) mLangSpinner
            .getAdapter();
      mLangSpinner.setSelection(adapter.getPosition(cursor.getString(cursor
            .getColumnIndex(DbContract.Words.COLUMN_NAME_LANGUAGE))));

      mCurrentAudioFile = cursor.getString(cursor
            .getColumnIndex(DbContract.Words.COLUMN_NAME_AUDIO_FILE));

      if (this.mItemId > 0)
      {
         mTextHeading.setText(mKidName + getString(R.string.said) + ":");
      } else
      {
         mTextHeading.setText(mKidName + getString(R.string.said_something)
               + " ?");
      }
      cursor.close();

      displayWordHistory(v);
   }

   @SuppressLint("NewApi")
   @SuppressWarnings("deprecation")
   private void displayWordHistory(View v)
   {
      Cursor cursor = DbSingleton.get().getWordHistory(mCurrentKidId, mItemId);

      if (cursor.getCount() < 2)
         return;

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
            ll.setOrientation(LinearLayout.VERTICAL);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                  LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

            params.addRule(RelativeLayout.BELOW, id);
            int sideMargin = (int) (20 * getActivity().getResources()
                  .getDisplayMetrics().density);
            int bottomMargin = (int) (10 * getActivity().getResources()
                  .getDisplayMetrics().density);
            params.setMargins(sideMargin, 0, sideMargin, bottomMargin);
            ll.setLayoutParams(params);

            LayoutParams txtParams = new LayoutParams(
                  LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            TextView txtWord = new TextView(getActivity());
            TextView txtDate = new TextView(getActivity());

            txtWord.setLayoutParams(txtParams);
            txtWord.setTextAppearance(getActivity(),
                  R.style.DictionaryWordStyle);
            txtDate.setLayoutParams(txtParams);
            txtDate.setTextAppearance(getActivity(),
                  R.style.DictionaryDateStyle);

            txtWord.setText(cursor.getString(cursor
                  .getColumnIndex(DbContract.Words.COLUMN_NAME_WORD)));

            long rawdate = cursor.getLong(cursor
                  .getColumnIndex(DbContract.Words.COLUMN_NAME_DATE));
            txtDate
                  .setText(Utils.getDateForDisplay(rawdate, this.getActivity()));

            if (android.os.Build.VERSION.SDK_INT > 15)
            {
               ll.setBackground(this.getResources().getDrawable(
                     R.drawable.white_card_background));
            } else
            {
               ll.setBackgroundDrawable(this.getResources().getDrawable(
                     R.drawable.white_card_background));
            }

            ll.setPadding(20, 20, 20, 20);
            ll.addView(txtWord);
            ll.addView(txtDate);
            ll.setId(Utils.generateViewId());
            id = ll.getId();

            rl.addView(ll);

         } while (cursor.moveToNext());
         rl.setPadding(0, 0, 0, 30);
      }
      cursor.close();
   }
}
