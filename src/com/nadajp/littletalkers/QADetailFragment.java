package com.nadajp.littletalkers;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.utils.Utils;

public class QADetailFragment extends ItemDetailFragment
{
   private static final String DEBUG_TAG = "AddQAFragment";
  
   // user interface elements
   private EditText mEditAnswer;
   private CheckBox mCheckAsked, mCheckAnswered;
   private TextView mTextCheckInstructions;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState)
   {
      mFragmentLayout = R.layout.fragment_qa_detail;
      mEditPhraseResId = R.id.editQuestion;
      
      return super.onCreateView(inflater, container, savedInstanceState); 
   }
   
   public void initializeExtras(View v)
   {
      mEditAnswer = (EditText) v.findViewById(R.id.editAnswer);
      mCheckAsked = (CheckBox) v.findViewById(R.id.checkAsked);
      mCheckAnswered = (CheckBox) v.findViewById(R.id.checkAnswered);
      mTextCheckInstructions = (TextView) v.findViewById(R.id.textCheckInstructions);
   }

   public void updateExtraKidDetails()
   {
      //mCheckAsked.setText(mKidName + " " + getString(R.string.asked_question));
      //mCheckAnswered.setText(mKidName + " " + getString(R.string.answered_question)); 
      mTextCheckInstructions.setText(getString(R.string.check_instructions1) + " " + mKidName +
            getString(R.string.check_instructions2));
   }
   
   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      inflater.inflate(R.menu.add_question, menu);
      super.onCreateOptionsMenu(menu, inflater);
      if (mItemId > 0)
         {
         MenuItem switchType = menu.findItem(R.id.action_add_word);
         if (switchType != null){
            switchType.setVisible(false);
         }
      }
   }

   @Override
   public void onSaveInstanceState(Bundle outState)
   {
      super.onSaveInstanceState(outState);
      
   }
   
   public boolean savePhrase()
   {
      if (mEditPhrase.length() == 0)
      {
         mEditPhrase.requestFocus();
         mEditPhrase.setError(getString(R.string.question_required_error));
         return false;
      }

      if (mCheckAnswered.isChecked() && mEditAnswer.length() == 0)
      {
         mEditAnswer.requestFocus();
         mEditAnswer.setError(getString(R.string.answer_required_error));
         return false;  
      }
      // convert date to miliseconds for SQLite
      long msDate = mDate.getTimeInMillis();

      String question = mEditPhrase.getText().toString();
      String answer = mEditAnswer.getText().toString();
      String location = mEditLocation.getText().toString();
      String towhom = mEditToWhom.getText().toString();
      String notes = mEditNotes.getText().toString();
      int asked = mCheckAsked.isChecked() ? 1 : 0;
      int answered = mCheckAnswered.isChecked() ? 1 : 0;

      // if adding new question, save it here
      if (mItemId == 0)
      {
         if (DbSingleton.get().saveQuestion(mCurrentKidId, question, answer, asked, answered, towhom, mLanguage, msDate,
               location, mCurrentAudioFile, notes) == false)
         {
            mEditPhrase.requestFocus();
            mEditPhrase.setError(getString(R.string.QA_already_exists_error));
            return false;
         }

         // QA was saved successfull
         Toast toast = Toast.makeText(this.getActivity(), R.string.question_saved,
               Toast.LENGTH_LONG);
         toast.show();
         return true;
      }

      else
      // we are editing an existing entry
      {
         if (DbSingleton.get().updateQuestion(mItemId, mCurrentKidId, question, answer, 
               asked, answered, towhom, mLanguage, msDate, location, mCurrentAudioFile,
               notes) == false)
         { 
            mEditPhrase.requestFocus();
            mEditPhrase.setError(getString(R.string.QA_already_exists_error));
            return false;
         }
         // Word was updated successfully, show dictionary
         Toast toast = Toast.makeText(this.getActivity(),
               R.string.question_updated, Toast.LENGTH_LONG);
         toast.show();
         // invalidate menu to add sharing capabilities
         this.getActivity().invalidateOptionsMenu();
      }
      return true;
   }

   public void clearExtraViews()
   {
      mEditAnswer.setText("");
      mCheckAsked.setChecked(true);
      mCheckAnswered.setChecked(false);
   }

   public void insertItemDetails(View v)
   {
      Log.i(DEBUG_TAG, "Inserting word details");
      Cursor cursor = DbSingleton.get().getQuestionDetails(mItemId);
      
      cursor.moveToFirst();
      mEditPhrase.setText(cursor.getString(cursor
            .getColumnIndex(DbContract.Questions.COLUMN_NAME_QUESTION)));
      mEditAnswer.setText(cursor.getString(cursor
            .getColumnIndex(DbContract.Questions.COLUMN_NAME_ANSWER)));
      
      int asked = cursor.getInt(cursor.getColumnIndex(DbContract.Questions.COLUMN_NAME_ASKED));
      if (asked == 0){
         mCheckAsked.setChecked(false);
      }
      else mCheckAsked.setChecked(true);
      
      int answered = cursor.getInt(cursor.getColumnIndex(DbContract.Questions.COLUMN_NAME_ANSWERED));
      if (answered == 0){
         mCheckAnswered.setChecked(false);
      }
      else mCheckAnswered.setChecked(true);

      // get date in miliseconds from db, convert to text, set current date
      long rawdate = cursor.getLong(cursor
            .getColumnIndex(DbContract.Words.COLUMN_NAME_DATE));
      mEditDate.setText(Utils.getDateForDisplay(rawdate, this.getActivity()));
      mDate.setTimeInMillis(rawdate);

      // mEditDate.setText(cursor.getString(cursor.getColumnIndex(DbContract.Words.COLUMN_NAME_DATE)).toString());
      mEditLocation.setText(cursor.getString(
            cursor.getColumnIndex(DbContract.Questions.COLUMN_NAME_LOCATION))
            .toString());
      mEditToWhom.setText(cursor.getString(
            cursor.getColumnIndex(DbContract.Questions.COLUMN_NAME_TOWHOM))
            .toString());
      mEditNotes.setText(cursor.getString(
            cursor.getColumnIndex(DbContract.Questions.COLUMN_NAME_NOTES))
            .toString());

      ArrayAdapter<String> adapter = (ArrayAdapter<String>) mLangSpinner
            .getAdapter();
      mLangSpinner.setSelection(adapter.getPosition(cursor.getString(cursor
            .getColumnIndex(DbContract.Questions.COLUMN_NAME_LANGUAGE))));

      mCurrentAudioFile = cursor.getString(cursor
            .getColumnIndex(DbContract.Questions.COLUMN_NAME_AUDIO_FILE));
      cursor.close(); 
   }
   
   @Override
   public void setShareData(String data)
   {
      // TODO Auto-generated method stub
      
   }
   
   public String getShareBody()
   {
      String shareBody = "";
      return shareBody;
   }
}
