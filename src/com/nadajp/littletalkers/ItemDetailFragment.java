package com.nadajp.littletalkers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

public abstract class ItemDetailFragment extends Fragment implements
      OnItemSelectedListener, OnClickListener, OnErrorListener, OnInfoListener,
      OnCompletionListener
{
   private static final String DEBUG_TAG = "AddItemFragment";
   public static final String ITEM_ID = "Item_ID";
   private static final int DELETE_AUDIO_DIALOG_ID = 1;
   private static final int REPLACE_AUDIO_DIALOG_ID = 2;
   private static final int SHARE_DIALOG_ID = 3;
   private OnAddNewPhraseListener mListener; // listener to notify activity that
                                             // new
   private File mDirectory = null; // directory to store audio file
   private File mOutFile = null; // audio file
   private File mTempFile = null; // temporary audio file
   protected String mCurrentAudioFile; // name of audio file, empty string if
                                       // none
   protected long mCurrentKidId; // current kid id, must be valid
   protected long mItemId; // current item id, 0 if nothing has been saved yet
   private MediaRecorder mRecorder; // audio recorder
   private MediaPlayer mPlayer; // audio player
   private boolean mRecording = false; // are we currently recording audio?
   final static Animation mAnimation = new AlphaAnimation(1, 0); // Change alpha
                                                                 // from fully
                                                                 // visible to
                                                                 // invisible
   
   protected Calendar mDate; // calendar for current date
   protected String mLanguage; // current language
   protected ShareActionProvider mShareActionProvider; // used to share data
                                                       // from
                                                       // action bar
   protected String mKidName; // name of current kid, used for audio file name

   // common user interface elements
   protected EditText mEditPhrase, mEditDate, mEditLocation, mEditToWhom,
         mEditNotes;
   private ImageView mImgPlay;
   private ImageView mImgDelete;
   private ImageView mImgMic;
   protected Spinner mLangSpinner;
   protected Button mButtonSave, mButtonCancel, mButtonShare;
   private boolean mAudioRecorded;

   // to be set by derived classes
   int mFragmentLayout; // res id of the layout for this fragment
   int mEditPhraseResId; // res id of the edittext containing main item (word,
                         // question)

   public abstract void initializeExtras(View v);

   public abstract void setShareData(String data);

   public abstract boolean savePhrase();

   public abstract void clearExtraViews();

   public abstract void insertItemDetails(View v);

   public abstract void updateExtraKidDetails();

   public abstract String getShareBody();

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState)
   {
      // Inflate the layout for this fragment
      View v = inflater.inflate(mFragmentLayout, container, false);

      // Create language spinner
      mLangSpinner = (Spinner) v.findViewById(R.id.spinnerLanguage);
      mLangSpinner.setOnItemSelectedListener(this);
      ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
            getActivity(), R.array.array_languages, R.layout.lt_spinner_item);
      adapter.setDropDownViewResource(R.layout.lt_spinner_dropdown_item);
      mLangSpinner.setAdapter(adapter);

      mAnimation.setDuration(500); // duration - half a second
      mAnimation.setInterpolator(new LinearInterpolator()); // do not alter
                                                            // animation rate
      mAnimation.setRepeatCount(Animation.INFINITE); // Repeat animation
                                                     // infinitely
      mAnimation.setRepeatMode(Animation.REVERSE); // Reverse animation at the
                                                   // end so the button will
                                                   // fade back in

      mEditPhrase = (EditText) v.findViewById(mEditPhraseResId);
      mEditDate = (EditText) v.findViewById(R.id.editDate);
      mEditLocation = (EditText) v.findViewById(R.id.editLocation);
      mEditToWhom = (EditText) v.findViewById(R.id.editToWhom);
      mEditNotes = (EditText) v.findViewById(R.id.editNotes);

      mButtonCancel = (Button) v.findViewById(R.id.buttonCancel);
      mButtonSave = (Button) v.findViewById(R.id.buttonSave);

      mImgMic = (ImageView) v.findViewById(R.id.imgMic);
      mImgPlay = (ImageView) v.findViewById(R.id.imgPlay);
      mImgDelete = (ImageView) v.findViewById(R.id.imgDelete);

      mEditDate.setOnClickListener(this);
      mImgMic.setOnClickListener(this);
      mImgPlay.setOnClickListener(this);
      mImgDelete.setOnClickListener(this);
      mButtonCancel.setOnClickListener(this);
      mButtonSave.setOnClickListener(this);

      initializeExtras(v);

      // the following listener is added in order to clear the error message
      // once the user starts typing
      mEditPhrase.addTextChangedListener(new TextWatcher()
      {
         @Override
         public void onTextChanged(CharSequence s, int start, int before,
               int count)
         {
            // TODO Auto-generated method stub
         }

         @Override
         public void beforeTextChanged(CharSequence s, int start, int count,
               int after)
         {
            // TODO Auto-generated method stub
         }

         @Override
         public void afterTextChanged(Editable s)
         {
            mEditPhrase.setError(null);
         }
      });

      // set current date in the date field
      mDate = Calendar.getInstance();
      updateDate();

      // set directory for storing audio files
      String subdirectory = getString(R.string.app_name);
      mDirectory = Utils.getPublicDirectory(subdirectory, getActivity());

      // if audio has already been recorded, show play/delete buttons
      if (savedInstanceState != null)
      {
         if (savedInstanceState.getBoolean(Prefs.AUDIO_RECORDED) == true)
         {
            mImgPlay.setVisibility(View.VISIBLE);
            mImgDelete.setVisibility(View.VISIBLE);
            mAudioRecorded = true;
         }

         mItemId = savedInstanceState.getLong(Prefs.ITEM_ID);
         mCurrentKidId = savedInstanceState.getLong(Prefs.CURRENT_KID_ID);
         Log.i(DEBUG_TAG, "Retreiving Instance State: " + mCurrentKidId);
      } else
      {
         mCurrentKidId = Prefs.getKidId(getActivity(), -1);
         Log.i(DEBUG_TAG, "kid id in addWord = " + mCurrentKidId);
         mItemId = getActivity().getIntent().getLongExtra(ITEM_ID, 0);
         Log.i(DEBUG_TAG, "item ID = " + mItemId);
      }

      // Utils.updateTitlebar(mCurrentKidId, v, this.getActivity());
      // insertKidDefaults(mCurrentKidId, v, false);

      // If editing/viewing an existing item, fill in all the fields
      if (mItemId > 0)
      {
         updateItem(v);
         getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
         mButtonCancel.setText(R.string.share);
         mButtonSave.setText(R.string.save_changes);
      } else
      {
         getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);

         if (mAudioRecorded)
         {
            mTempFile = new File(mDirectory, "temp.3gp");
         }
      }
      return v;
   }

   @Override
   public void onActivityCreated(Bundle savedInstanceState)
   {
      super.onActivityCreated(savedInstanceState);
      if (mItemId > 0)
      {
         updateItem(this.getView());
      }
   }

   public File getAudioFile()
   {
      return mOutFile;
   }

   /*
    * @Override public boolean onOptionsItemSelected(MenuItem item) { // Handle
    * presses on the action bar items switch (item.getItemId()) { case
    * R.id.action_share: ShareDialog dlg = new ShareDialog();
    * dlg.setTargetFragment(this, SHARE_DIALOG_ID);
    * dlg.show(getFragmentManager(), ShareDialog.class.toString()); return true;
    * default: return super.onOptionsItemSelected(item); } }
    */

   public static class ShareDialog extends DialogFragment
   {
      private AppListAdapter mAdapter;
      private ArrayList<ComponentName> mComponents;
      private Intent mIntent;

      @Override
      public Dialog onCreateDialog(Bundle savedInstanceState)
      {
         super.onCreate(savedInstanceState);
         setupSharing();
         AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
         builder
               .setTitle(R.string.share)
               .setNegativeButton(R.string.cancel,
                     new DialogInterface.OnClickListener()
                     {
                        public void onClick(DialogInterface dialog, int id)
                        {
                        }
                     })
               .setAdapter(mAdapter, new DialogInterface.OnClickListener()
               {
                  public void onClick(DialogInterface dialog, int which)
                  {
                     // The 'which' argument contains the index position
                     // of the selected item
                     mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                     mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                           | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                     mIntent.setComponent(mComponents.get(which));
                     getActivity().startActivity(mIntent);
                  }
               });
         return builder.create();
      }

      private void setupSharing()
      {
         mIntent = new Intent(android.content.Intent.ACTION_SEND);
         File audioFile = ((ItemDetailFragment) getTargetFragment())
               .getAudioFile();

         if (audioFile != null)
         {
            Uri uri = Uri.fromFile(audioFile);
            mIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mIntent.putExtra(Intent.EXTRA_STREAM, uri);
            mIntent.setType("audio/*");
         } else
         {
            mIntent.setType("text/plain");
         }

         mIntent.addCategory(Intent.CATEGORY_DEFAULT);
         mIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
               (String) getString(R.string.app_name));
         mIntent.putExtra(android.content.Intent.EXTRA_TEXT,
               ((ItemDetailFragment) this.getTargetFragment()).getShareBody());

         PackageManager packageManager = getActivity().getPackageManager();

         List<ResolveInfo> activities = packageManager.queryIntentActivities(
               mIntent, 0);
         ArrayList<String> appNames = new ArrayList<String>();
         ArrayList<Drawable> icons = new ArrayList<Drawable>();
         mComponents = new ArrayList<ComponentName>();

         for (ResolveInfo app : activities)
         {
            String name = app.loadLabel(packageManager).toString();
            Log.i(DEBUG_TAG, "*" + name + "*" + "\n");
            if (!name.equals("Facebook"))
            {
               mComponents.add(new ComponentName(
                     app.activityInfo.applicationInfo.packageName,
                     app.activityInfo.name));
               appNames.add(app.loadLabel(packageManager).toString());
               icons.add(app.loadIcon(packageManager));
            }
         }
         mAdapter = new AppListAdapter(getActivity(), appNames, icons);
      }

      public class AppListAdapter extends ArrayAdapter<String>
      {
         private final Context context;
         private final ArrayList<String> names;
         private final ArrayList<Drawable> icons;

         public AppListAdapter(Context context, ArrayList<String> names,
               ArrayList<Drawable> icons)
         {
            super(context, R.layout.app_list_row, names);
            this.context = context;
            this.names = names;
            this.icons = icons;
         }

         @Override
         public View getView(int position, View convertView, ViewGroup parent)
         {
            LayoutInflater inflater = (LayoutInflater) context
                  .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.app_list_row, parent,
                  false);
            TextView textView = (TextView) rowView.findViewById(R.id.app_name);
            ImageView imageView = (ImageView) rowView
                  .findViewById(R.id.app_icon);

            textView.setText(names.get(position));
            // change the icon for Windows and iPhone
            imageView.setImageDrawable(icons.get(position));

            return rowView;
         }
      }
   }

   @Override
   public void onClick(View v)
   {
      switch (v.getId())
      {
      case R.id.editDate:
         showCalendar(v);
         break;
      case R.id.imgMic:
         clickedMic(v);
         break;
      case R.id.imgPlay:
         clickedAudioPlay(v);
         break;
      case R.id.imgDelete:
         deleteAudio();
         break;
      case R.id.buttonSave:
         saveItem(true);
         break;
      case R.id.buttonCancel:
         if (mButtonCancel.getText().toString().contains("Show"))
         {
            mListener.onClickedShowDictionary(mCurrentKidId);
         } else
         {
            ShareDialog dlg = new ShareDialog();
            dlg.setTargetFragment(this, SHARE_DIALOG_ID);
            dlg.show(getFragmentManager(), ShareDialog.class.toString());
         }
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
      mImgPlay.setPressed(true);
      mImgPlay.startAnimation(mAnimation);
      if (mTempFile != null)
      {
         Log.i(DEBUG_TAG, "Playing file: " + mTempFile.getAbsolutePath());
      }
      try
      {
         if (mTempFile != null)
         {
            mPlayer.setDataSource(mTempFile.getAbsolutePath());
         } else { mPlayer.setDataSource(mOutFile.getAbsolutePath());}
         mPlayer.prepare();
         mPlayer.start();
      } catch (IOException e)
      {
         Log.e(DEBUG_TAG, "Audio player start failed: " + e.getMessage());
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
      mImgPlay.clearAnimation();
      mImgPlay.setPressed(false);
      Log.i(DEBUG_TAG, "Stopped.");
   }

   private void deleteAudio()
   {
      DeleteAudioDialogFragment dlg = new DeleteAudioDialogFragment();
      dlg.setTargetFragment(this, DELETE_AUDIO_DIALOG_ID);
      dlg.show(getFragmentManager(), DeleteAudioDialogFragment.class.toString());
   }

   private void showCalendar(View v)
   {
      new DatePickerDialog(v.getContext(), d, mDate.get(Calendar.YEAR),
            mDate.get(Calendar.MONTH), mDate.get(Calendar.DAY_OF_MONTH)).show();
   }

   private void startRecording()
   {
      if (mTempFile != null && mTempFile.exists())
      {
         mTempFile.delete();
         mTempFile = null;
      }

      mTempFile = new File(mDirectory, "temp.3gp");

      mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
      mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
      mRecorder.setOutputFile(mTempFile.getAbsolutePath());
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
      mImgPlay.setVisibility(View.VISIBLE);
      mImgDelete.setVisibility(View.VISIBLE);
      if (!(mEditPhrase.getText().toString().isEmpty()))
      {
         if (mOutFile != null) // if editing, pop up dialog
         {
            Log.i(DEBUG_TAG, mOutFile.toString());

            ReplaceAudioDialogFragment dlg = new ReplaceAudioDialogFragment();
            dlg.setTargetFragment(this, REPLACE_AUDIO_DIALOG_ID);
            dlg.show(getFragmentManager(),
                  ReplaceAudioDialogFragment.class.toString());
         } else
         {
            saveItem(false);
         }
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
      mEditDate.setText(DateUtils.formatDateTime(getActivity(),
            mDate.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
                  | DateUtils.FORMAT_SHOW_YEAR));
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
                           ((ItemDetailFragment) getTargetFragment())
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

   public static class ReplaceAudioDialogFragment extends DialogFragment
   {
      @Override
      public Dialog onCreateDialog(Bundle savedInstanceState)
      {
         // Use the Builder class for convenient dialog construction
         AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
         builder
               .setMessage(R.string.replace_audio_dialog)
               .setPositiveButton(R.string.replace_audio,
                     new DialogInterface.OnClickListener()
                     {
                        public void onClick(DialogInterface dialog, int id)
                        {
                           ((ItemDetailFragment) getTargetFragment()).saveItem(false);
                        }
                     })
               .setNegativeButton(R.string.cancel,
                     new DialogInterface.OnClickListener()
                     {
                        public void onClick(DialogInterface dialog, int id)
                        {
                           ((ItemDetailFragment) getTargetFragment()).mTempFile = null;
                        }
                     });

         // Create the AlertDialog object and return it
         return builder.create();
      }
   }

   public void confirmDeleteAudio()
   {
      mAudioRecorded = false;
      mCurrentAudioFile = "";
      if (mOutFile != null && mOutFile.exists())
      {
         mOutFile.delete();
         mOutFile = null;
         mImgPlay.setVisibility(View.GONE);
         mImgDelete.setVisibility(View.GONE);
        
         if (mEditPhrase.getText().length() > 0)
         {
            saveItem(false);
         }
      }

      if (mTempFile != null && mTempFile.exists())
      {
         mTempFile.delete();
         mTempFile = null;
         mImgPlay.setVisibility(View.GONE);
         mImgDelete.setVisibility(View.GONE);
      }
   }

   private void saveItem(boolean exit)
   {
      saveAudioFile();
      if (mOutFile != null && mOutFile.exists())
      {
         mCurrentAudioFile = mOutFile.getAbsolutePath();
      }
      Boolean test = savePhrase();
      Log.i(DEBUG_TAG, "saved: " + test.toString());
      if (test && exit)
      {
         mListener.onClickedShowDictionary(mCurrentKidId);
      }
   }

   private void saveAudioFile()
   {
      if (mTempFile != null && mTempFile.exists())
      {
         saveFile();
      } else if (mOutFile != null && mOutFile.exists())
      {
         renameFile();
      }
   }

   private void clearForm()
   {
      mEditPhrase.setText("");
      mDate = Calendar.getInstance();
      updateDate();
      mEditToWhom.setText("");
      mEditNotes.setText("");
      mImgPlay.setVisibility(View.INVISIBLE);
      mImgDelete.setVisibility(View.INVISIBLE);
      mAudioRecorded = false;
      mEditPhrase.setError(null);
      clearExtraViews();
   }

   public void updateItem(View v)
   {
      insertItemDetails(v);
      setAudio();
   }

   public void insertKidDefaults(long kidId, View v)
   {
      mCurrentKidId = kidId;
      mKidName = DbSingleton.get().getKidName(mCurrentKidId);
      // Log.i(DEBUG_TAG, "kid id in addWord = " + mCurrentKidId);
      String[] defaults = DbSingleton.get().getDefaults(mCurrentKidId);
      mLanguage = defaults[0];
      Log.i(DEBUG_TAG, mLanguage);
      ArrayAdapter<String> adapter = (ArrayAdapter<String>) mLangSpinner
            .getAdapter();
      mLangSpinner.setSelection(adapter.getPosition(mLanguage));
      mEditLocation.setText(defaults[1]);

      updateExtraKidDetails();
      //Utils.updateTitlebar(mCurrentKidId, v, this.getActivity());
   }

   protected void setAudio()
   {
      if (mCurrentAudioFile != null && !mCurrentAudioFile.isEmpty())
      {
         mOutFile = new File(mCurrentAudioFile);
         Log.i(DEBUG_TAG, "HERE");
         mImgPlay.setVisibility(View.VISIBLE);
         mImgDelete.setVisibility(View.VISIBLE);
         mAudioRecorded = true;
      }
   }

   private String getFilename()
   {
      String[] a = mEditPhrase.getText().toString().split(" ");
      StringBuffer str = new StringBuffer(a[0].trim());
      for (int i = 1; i < a.length; i++)
      {
         str.append(a[i].trim());
         if (i == 5)
         {
            break;
         }
      }
      return mKidName + "-" + str + mDate.getTimeInMillis()
            + ".3gp"; 
   }
   
   private void renameFile()
   {
      File newfile = new File(mDirectory, getFilename());
      
      if (mOutFile.getAbsolutePath().equals(newfile.getAbsolutePath())) { return; }
      if (newfile.exists()) { newfile.delete(); }

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
      mTempFile = null;
   }

   private void saveFile()
   {
      mOutFile = new File(mDirectory, getFilename());

      if (mOutFile.exists()) { mOutFile.delete(); }

      if (mTempFile.renameTo(mOutFile)) { Log.i(DEBUG_TAG, "Rename succesful"); } 
      else { Log.i(DEBUG_TAG, "Rename failed"); }

      mTempFile.delete();
      mTempFile = null;
      String[] paths = { mOutFile.getAbsolutePath() };
      String[] mimes = { "audio/*" };
      MediaScannerConnection.scanFile(getActivity(), paths, mimes,
            new MediaScannerConnection.OnScanCompletedListener()
            {
               public void onScanCompleted(String path, Uri uri)
               {
                  Log.i("ExternalStorage", "Scanned " + path + ":");
                  Log.i("ExternalStorage", "-> uri=" + uri);
               }
            });
      mCurrentAudioFile = mOutFile.getAbsolutePath();
   }

   public void setCurrentKidId(long kidId)
   {
      mCurrentKidId = kidId;
      insertKidDefaults(kidId, getView());
   }

   public long getCurrentKidId()
   {
      return mCurrentKidId;
   }

   public interface OnAddNewPhraseListener
   {
      public void onPhraseAdded(long kidId);

      public void onClickedShowDictionary(long kidId);
   }

   @Override
   public void onAttach(Activity activity)
   {
      super.onAttach(activity);
      if (activity instanceof OnAddNewPhraseListener)
      {
         mListener = (OnAddNewPhraseListener) activity;
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
   public void onSaveInstanceState(Bundle outState)
   {
      super.onSaveInstanceState(outState);
      if (mImgPlay.getVisibility() == View.VISIBLE)
      {
         outState.putBoolean(Prefs.AUDIO_RECORDED, true);
      }

      outState.putLong(Prefs.CURRENT_KID_ID, mCurrentKidId);
      outState.putLong(Prefs.ITEM_ID, mItemId);
      Log.i(DEBUG_TAG, "Saving Instance State: " + mCurrentKidId);
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
      //Utils.updateTitlebar(mCurrentKidId, getView(), getActivity());
      insertKidDefaults(mCurrentKidId, getView());
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
      mTempFile = null;
   }
}
