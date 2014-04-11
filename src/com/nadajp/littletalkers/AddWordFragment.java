package com.nadajp.littletalkers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

public class AddWordFragment extends Fragment implements
      OnItemSelectedListener, OnClickListener, OnErrorListener, OnInfoListener,
      OnCompletionListener
{
   private static final String DEBUG_TAG = "AddWordFragment";
   private static final int DELETE_AUDIO_DIALOG_ID = 1;
   private OnAddWordListener mListener; // listener to notify activity that new
                                        // word was added
   private File mDirectory = null; // directory to store audio file
   private File mOutFile = null; // audio file
   private long mCurrentKidId; // current kid id, must be valid
   public long mWordId; // current word id, 0 if no word has been saved yet
   private MediaRecorder mRecorder; // audio recorder
   private MediaPlayer mPlayer; // audio player
   private boolean mRecording = false; // are we currently recording audio?
   final static Animation mAnimation = new AlphaAnimation(1, 0); // Change alpha
                                                                 // from fully
                                                                 // visible to
                                                                 // invisible
   private Calendar mDate = Calendar.getInstance(); // calendar for current date
   private String mLanguage; // current language
   private ShareActionProvider mShareActionProvider; // used to share data from
                                                     // action bar
   private String mKidName; // name of current kid, used for audio file name
   private boolean mTempFile; // is current audio file temporary (i.e. does not
                              // have permanent name)?

   // user interface elements
   private EditText mEditWord, mEditDate, mEditLocation, mEditTranslation,
         mEditToWhom, mEditNotes;
   private ImageButton mButtonMic, mButtonPlayAudio, mButtonDeleteAudio;
   private Spinner mLangSpinner;
   private Button mButtonShowDictionary, mButtonSave;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState)
   {
      // Inflate the layout for this fragment
      View v = inflater.inflate(R.layout.fragment_add_word, container, false);

      // Create language spinner
      mLangSpinner = (Spinner) v.findViewById(R.id.spinnerLanguage);
      mLangSpinner.setOnItemSelectedListener(this);
      ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
            getActivity(), R.array.array_languages,
            android.R.layout.simple_spinner_item);
      adapter
            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      mLangSpinner.setAdapter(adapter);

      mAnimation.setDuration(500); // duration - half a second
      mAnimation.setInterpolator(new LinearInterpolator()); // do not alter
                                                            // animation rate
      mAnimation.setRepeatCount(Animation.INFINITE); // Repeat animation
                                                     // infinitely
      mAnimation.setRepeatMode(Animation.REVERSE); // Reverse animation at the
                                                   // end so the button will
                                                   // fade back in

      mEditWord = (EditText) v.findViewById(R.id.editWord);
      mEditDate = (EditText) v.findViewById(R.id.editDate);
      mEditLocation = (EditText) v.findViewById(R.id.editLocation);
      mEditTranslation = (EditText) v.findViewById(R.id.editTranslation);
      mEditToWhom = (EditText) v.findViewById(R.id.editToWhom);
      mEditNotes = (EditText) v.findViewById(R.id.editNotes);

      mButtonShowDictionary = (Button) v
            .findViewById(R.id.buttonShowDictionary);
      mButtonSave = (Button) v.findViewById(R.id.buttonSaveWord);
      mButtonMic = (ImageButton) v.findViewById(R.id.buttonMic);
      mButtonPlayAudio = (ImageButton) v.findViewById(R.id.buttonPlayAudio);
      mButtonDeleteAudio = (ImageButton) v.findViewById(R.id.buttonDeleteAudio);

      mButtonShowDictionary.setOnClickListener(this);
      mEditDate.setOnClickListener(this);
      mButtonSave.setOnClickListener(this);
      mButtonMic.setOnClickListener(this);
      mButtonPlayAudio.setOnClickListener(this);
      mButtonDeleteAudio.setOnClickListener(this);

      // set current date in the date field
      updateDate();

      // if audio has already been recorded, show play/delete buttons
      if (savedInstanceState != null)
      {
         if (savedInstanceState.getBoolean("showAudioButtons") == true)
         {
            mButtonPlayAudio.setVisibility(View.VISIBLE);
            mButtonDeleteAudio.setVisibility(View.VISIBLE);
         }

         mWordId = savedInstanceState.getLong(Prefs.WORD_ID);
         mCurrentKidId = savedInstanceState.getLong(Prefs.CURRENT_KID_ID);
         Log.i(DEBUG_TAG, "Retreiving Instance State: " + mCurrentKidId);
      } else
      {
         long latestKidId = Prefs.getKidId(getActivity(), -1);
         mCurrentKidId = getActivity().getIntent().getLongExtra(
               Prefs.CURRENT_KID_ID, latestKidId);
         Log.i(DEBUG_TAG, "kid id in addWord = " + mCurrentKidId);
         mWordId = getActivity().getIntent().getLongExtra(Prefs.WORD_ID, 0);
         Log.i(DEBUG_TAG, "word ID = " + mWordId);
      }

      // set directory for storing audio files
      String subdirectory = getString(R.string.subdirectory);
      String state = Environment.getExternalStorageState();
      if (Environment.MEDIA_MOUNTED.equals(state))
      {
         mDirectory = new File(
               Environment
                     .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
               subdirectory);
      } else
      {
         mDirectory = new File(getActivity().getFilesDir(), subdirectory);
      }

      if (!mDirectory.exists())
      {
         mDirectory.mkdir();
      }

      // If editing/viewing an existing word, fill in all the fields
      if (mWordId > 0)
      {
         insertWordDetails(mWordId, v);
      } else
      {
         // otherwise, just insert default language/location for this kid
         insertKidDefaults(mCurrentKidId, v);
      }

      mKidName = DbSingleton.get().getKidName(mCurrentKidId);

      Utils.updateTitlebar(mCurrentKidId, v, this.getActivity());

      setHasOptionsMenu(true);
      return v;
   }

   @SuppressLint("NewApi")
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      inflater.inflate(R.menu.add_word, menu);
      MenuItem item = menu.findItem(R.id.menu_item_share);
      item.setVisible(true);
      if (android.os.Build.VERSION.SDK_INT > 13 && mWordId > 0)
      {
         // Fetch and store ShareActionProvider
         mShareActionProvider = (ShareActionProvider) item.getActionProvider();
         setShareData(mEditWord.getText().toString());
      } else
      {
         item.setVisible(false);
      }
   }

   @SuppressLint("NewApi")
   public void setShareData(String data)
   {
      Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
      sharingIntent.setType("text/plain");
      String shareBody = "On " + mEditDate.getText().toString()
            + ", Little Talker " + mKidName + " said: "
            + mEditWord.getText().toString() + ".";
      sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
            "New Little Talker " + mKidName + " words");
      sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

      if (mShareActionProvider != null)
      {
         mShareActionProvider.setShareIntent(sharingIntent);
      }
   }

   @Override
   public void onClick(View v)
   {
      switch (v.getId())
      {
      case R.id.buttonShowDictionary:
        mListener.onClickedShowDictionary(mCurrentKidId);
        break;
      case R.id.editDate:
        showCalendar(v);
        break;
      case R.id.buttonMic:
        clickedMic(v);
        break;
      case R.id.buttonPlayAudio:
        clickedAudioPlay(v);
        break;
      case R.id.buttonSaveWord:
        saveWord();
        clearForm();
        break;
      case R.id.buttonDeleteAudio:
        deleteAudio();
        break;
      default:
        return;
      }
   }

   @Override
   public void onInfo(MediaRecorder mr, int what, int extra)
   {
      String msg = getString(R.string.mediarecorder_error_msg);

      switch (what)
      {
      case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
         msg = getString(R.string.max_duration);
         break;
      case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
         msg = getString(R.string.max_size);
         break;
      }
      Toast.makeText(this.getActivity(), msg, Toast.LENGTH_LONG).show();
   }

   @Override
   public void onError(MediaRecorder mr, int what, int extra)
   {
      Toast.makeText(this.getActivity(), R.string.mediarecorder_error_msg,
            Toast.LENGTH_LONG).show();
   }

   private void clickedMic(View v)
   {
      if (!mRecording)
      {
         v.startAnimation(mAnimation);
         startRecording();
         mRecording = true;
      } else
      {
         v.clearAnimation();
         stopRecording();
         mRecording = false;
      }
   }

   private void clickedAudioPlay(View v)
   {
      if (mPlayer.isPlaying())
      {
         stopPlaying();
      } else
      {
         startPlaying();
      }
   }

   public void onCompletion(MediaPlayer mp)
   {
      stopPlaying();
   }

   private void startPlaying()
   {
      mButtonPlayAudio.setPressed(true);
      mButtonPlayAudio.startAnimation(mAnimation);
      Log.i(DEBUG_TAG, "Now Playing: " + mOutFile.getAbsolutePath());
      try
      {
         mPlayer.setDataSource(mOutFile.getAbsolutePath());
         mPlayer.prepare();
         mPlayer.start();
      } catch (IOException e)
      {
         Log.e(DEBUG_TAG, "Audio player start failed");
      }
   }

   private void stopPlaying()
   {
      if (mPlayer == null)
      {
         Log.i(DEBUG_TAG, "Media Player is null in Stop()");
      }
      mPlayer.stop();
      mPlayer.reset();
      mButtonPlayAudio.clearAnimation();
      mButtonPlayAudio.setPressed(false);
      Log.i(DEBUG_TAG, "Stopped.");
   }

   private void deleteAudio()
   {
      DeleteAudioDialogFragment dlg = new DeleteAudioDialogFragment();
      dlg.setTargetFragment(this, DELETE_AUDIO_DIALOG_ID);
      dlg.show(getFragmentManager(), "DeleteAudioDialogFragment");
   }

   private void showCalendar(View v)
   {
      new DatePickerDialog(v.getContext(), d, mDate.get(Calendar.YEAR),
            mDate.get(Calendar.MONTH), mDate.get(Calendar.DAY_OF_MONTH)).show();
   }

   private void startRecording()
   {
      // SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
      String word = mEditWord.getText().toString();
      String baseFilename;

      if (word == null)
      {
         baseFilename = "temp.3gp";
         mTempFile = true;
      }

      else
      {
         String str = mEditWord.getText().toString().split(" ")[0];
         baseFilename = mKidName + mDate.getTimeInMillis() + str + ".3gp"; // s.format(new
                                                                           // Date())
                                                                           // +
                                                                           // ".3gp";
         mTempFile = false;
      }

      if (mOutFile != null && mOutFile.exists())
      {
         mOutFile.delete();
         mOutFile = null;
      }

      mOutFile = new File(mDirectory, baseFilename);

      Log.i(DEBUG_TAG, mOutFile.getAbsolutePath());
      mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
      mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
      mRecorder.setOutputFile(mOutFile.getAbsolutePath());
      mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

      try
      {
         mRecorder.prepare();
         mRecorder.start();
      } catch (IOException e)
      {
         Log.e(DEBUG_TAG, "Exception in preparing recorder: " + e.getMessage());
         Toast.makeText(this.getActivity(), e.getMessage(), Toast.LENGTH_LONG)
               .show();
      }
   }

   private void stopRecording()
   {
      try
      {
         mRecorder.stop();
      } catch (Exception e)
      {
         Log.w(getClass().getSimpleName(), "Exception in stopping recorder", e);
         // can fail if start() failed for some reason
      }
      mRecorder.reset();
      mButtonPlayAudio.setVisibility(View.VISIBLE);
      mButtonDeleteAudio.setVisibility(View.VISIBLE);
      if (!(mEditWord.getText().toString().isEmpty()))
      {
         saveWord();
      }
   }

   public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
   {
      mLanguage = parent.getItemAtPosition(pos).toString();
   }

   public void onNothingSelected(AdapterView<?> parent)
   {
      // Another interface callback
   }

   DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener()
   {
      public void onDateSet(DatePicker view, int year, int monthOfYear,
            int dayOfMonth)
      {
         mDate.set(Calendar.YEAR, year);
         mDate.set(Calendar.MONTH, monthOfYear);
         mDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
         updateDate();
      }
   };

   private void updateDate()
   {
      mEditDate.setText(DateUtils.formatDateTime(this.getActivity(),
            mDate.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
                  | DateUtils.FORMAT_SHOW_YEAR));
   }

   @Override
   public void onSaveInstanceState(Bundle outState)
   {
      super.onSaveInstanceState(outState);
      if (mButtonPlayAudio.getVisibility() == View.VISIBLE)
      {
         outState.putBoolean("showAudioButtons", true);
      }

      outState.putLong(Prefs.CURRENT_KID_ID, mCurrentKidId);
      outState.putLong(Prefs.WORD_ID, mWordId);
      Log.i(DEBUG_TAG, "Saving Instance State: " + mCurrentKidId);
   }

   public static class DeleteAudioDialogFragment extends DialogFragment
   {
      @Override
      public Dialog onCreateDialog(Bundle savedInstanceState)
      {
         // Use the Builder class for convenient dialog construction
         AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
         builder
               .setMessage(R.string.delete_audio_dialog)
               .setPositiveButton(R.string.delete,
                     new DialogInterface.OnClickListener()
                     {
                        public void onClick(DialogInterface dialog, int id)
                        {
                           ((AddWordFragment) getTargetFragment())
                                 .confirmDeleteAudio();
                        }
                     })
               .setNegativeButton(R.string.cancel,
                     new DialogInterface.OnClickListener()
                     {
                        public void onClick(DialogInterface dialog, int id)
                        {
                        }
                     });

         // Create the AlertDialog object and return it
         return builder.create();
      }
   }

   public void confirmDeleteAudio()
   {
      if (mOutFile != null && mOutFile.exists())
      {
         mOutFile.delete();
         mButtonPlayAudio.setVisibility(View.INVISIBLE);
         mButtonDeleteAudio.setVisibility(View.INVISIBLE);
         if (mEditWord.getText().length() > 0)
         {
            saveWord();
         }
      }
   }

   private void saveWord()
   {
      // validate required fields
      String word, location, translation, towhom, notes, audiofile;

      // Name and Birthday are required, do validation
      if (mEditWord.length() == 0)
      {
         mEditWord.requestFocus();
         mEditWord.setError(getString(R.string.word_required_error));
         return;
      }

      word = mEditWord.getText().toString();

      // convert date to miliseconds for SQLite
      long msDate = mDate.getTimeInMillis();
      // String msDate = Utils.getDateForDisplay(dateAndTime.getTimeInMillis(),
      // getActivity());

      // Log.i(DEBUG_TAG, "Date from ms: " + msDate);

      location = mEditLocation.getText().toString();
      translation = mEditTranslation.length() == 0 ? word : mEditTranslation
            .getText().toString();
      towhom = mEditToWhom.getText().toString();
      notes = mEditNotes.getText().toString();

      if (mOutFile != null && mOutFile.exists())
      {
         if (mTempFile)
         {
            renameFile();
         }

         audiofile = mOutFile.getAbsolutePath();
      } else
      {
         audiofile = "";
      }

      // if adding new word, save it here
      if (mWordId == 0)
      {
         if (DbSingleton.get().saveWord(mCurrentKidId, word, mLanguage, msDate,
               location, audiofile, translation, towhom, notes) == false)
         {
            mEditWord.requestFocus();
            mEditWord.setError(getString(R.string.word_already_exists_error));
            return;
         }

         // Word was saved successfully, show dictionary
         Toast toast = Toast.makeText(this.getActivity(), R.string.word_saved,
               Toast.LENGTH_LONG);
         toast.show();
      }

      else
      // we are editing an existing entry
      {
         if (DbSingleton.get().updateWord(mWordId, mCurrentKidId, word,
               mLanguage, msDate, location, audiofile, translation, towhom,
               notes) == false)
         {
            mEditWord.requestFocus();
            mEditWord.setError(getString(R.string.word_already_exists_error));
            return;
         }
         // Word was updated successfully, show dictionary
         Toast toast = Toast.makeText(this.getActivity(),
               R.string.word_updated, Toast.LENGTH_LONG);
         toast.show();
         // invalidate menu to add sharing capabilities
         this.getActivity().invalidateOptionsMenu();
      }
   }

   private void clearForm()
   {
      mEditWord.setText("");
      mDate = Calendar.getInstance();
      updateDate();
      mEditTranslation.setText("");
      mEditToWhom.setText("");
      mEditNotes.setText("");
   }

   public void insertKidDefaults(long kidId, View v)
   {
      mCurrentKidId = kidId;
      // Log.i(DEBUG_TAG, "kid id in addWord = " + mCurrentKidId);
      String[] defaults = DbSingleton.get().getDefaults(mCurrentKidId);
      mLanguage = defaults[0];
      Log.i(DEBUG_TAG, mLanguage);
      ArrayAdapter<String> adapter = (ArrayAdapter<String>) mLangSpinner
            .getAdapter();
      mLangSpinner.setSelection(adapter.getPosition(mLanguage));
      mEditLocation.setText(defaults[1]);
      Utils.updateTitlebar(mCurrentKidId, v, this.getActivity());
   }

   private void insertWordDetails(long mWordId, View v)
   {
      Log.i(DEBUG_TAG, "Inserting word details");
      Cursor cursor = DbSingleton.get().getWordDetails(mWordId);
      cursor.moveToFirst();
      mEditWord.setText(cursor.getString(cursor
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

      String audiofile = cursor.getString(cursor
            .getColumnIndex(DbContract.Words.COLUMN_NAME_AUDIO_FILE));
      cursor.close();
      Log.i(DEBUG_TAG, "audiofile = " + audiofile);

      if (!audiofile.isEmpty())
      {
         mOutFile = new File(audiofile);
         Log.i(DEBUG_TAG, "HERE");
         mButtonPlayAudio.setVisibility(View.VISIBLE);
         mButtonDeleteAudio.setVisibility(View.VISIBLE);
      }

      mButtonShowDictionary.setText("Back to Dictionary");
      mButtonSave.setText("Save Changes");

      displayWordHistory(v);
   }

   public void displayWordHistory(View v)
   {
      Cursor cursor = DbSingleton.get().getWordHistory(mCurrentKidId, mWordId);

      if (cursor.getCount() < 2)
      {
         return;
      }

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
            Log.i(DEBUG_TAG, "SIDE MARGIN: " + sideMargin);
            rlParams.setMargins(sideMargin, 0, sideMargin, bottomMargin);
            Log.i(DEBUG_TAG, "ID : " + id);
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

   public void renameFile()
   {
      File newfile = null;
      String filename = mKidName + mEditWord.getText().toString() + ".3gp";
      newfile = new File(mDirectory, filename);

      if (newfile.exists())
      {
         newfile.delete();
      }

      Log.i(DEBUG_TAG, "Oldfile: " + mOutFile.getAbsolutePath());
      Log.i(DEBUG_TAG, "Newfile: " + newfile.getAbsolutePath());

      if (mOutFile.renameTo(newfile))
      {
         Log.i(DEBUG_TAG, "Rename succesful");
      } else
      {
         Log.i(DEBUG_TAG, "Rename failed");
      }

      mOutFile.delete();
      mOutFile = newfile;
      mTempFile = false;
   }

   public interface OnAddWordListener
   {
      public void onWordAdded(long kidId);

      public void onClickedShowDictionary(long kidId);
   }

   @Override
   public void onAttach(Activity activity)
   {
      super.onAttach(activity);
      if (activity instanceof OnAddWordListener)
      {
         mListener = (OnAddWordListener) activity;
      } else
      {
         throw new ClassCastException(activity.toString()
               + " must implemenet AddWordFragment.OnAddWordListener");
      }
   }

   @Override
   public void onDetach()
   {
      super.onDetach();
      mListener = null;
   }

   @Override
   public void onResume()
   {
      super.onResume();
      mRecorder = new MediaRecorder();
      mRecorder.setOnErrorListener(this);
      mRecorder.setOnInfoListener(this);
      mPlayer = new MediaPlayer();
      mPlayer.setOnCompletionListener(this);
   }

   @Override
   public void onPause()
   {
      if (mRecorder != null)
      {
         mRecorder.release();
         mRecorder = null;
      }
      if (mPlayer != null)
      {
         mPlayer.release();
         mPlayer = null;
      }
      super.onPause();
   }

   @Override
   public void onDestroyView()
   {
      super.onDestroyView();
      mDirectory = null;
      mOutFile = null;
      mDate = null;
   }
}
