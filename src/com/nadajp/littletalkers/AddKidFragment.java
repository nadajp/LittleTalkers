package com.nadajp.littletalkers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.utils.Prefs;

public class AddKidFragment extends Fragment implements OnClickListener,
      OnItemSelectedListener 
{
   private static final String DEBUG_TAG = "AddKidFragment";
   private long mCurrentKidId;
   private OnKidAddedListener mListener;

   Calendar mBirthDate = Calendar.getInstance();
   private EditText mEditName;
   private EditText mEdiBirthDate;
   private EditText mEditLocation;
   private ImageView mImgProfilePic;
   private Button mButtonSave;
   private Spinner mSpinnerLanguage;
   private Uri mUriPicture;
   private String mPicturePath;
   private String mLanguage;
   private boolean mTempBitmapSaved;

   private static final String TEMP_PHOTO_FILE_NAME = "temp_profile.png";
   private static final int TAKE_PICTURE = 0;
   private static final int PICK_FROM_FILE = 1;
   private static final int CROP_PICTURE = 2;
   private static final int IMAGE_SIZE = 180;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState)
   {
      // Inflate the layout for this fragment
      View v = inflater.inflate(R.layout.fragment_add_kid, container, false);

      mEditName = (EditText) v.findViewById(R.id.editName);
      mEdiBirthDate = (EditText) v.findViewById(R.id.editBirthDate);
      mEditLocation = (EditText) v.findViewById(R.id.editDefaultLocation);
      mImgProfilePic = (ImageView) v.findViewById(R.id.profilePicture);
      mButtonSave = (Button) v.findViewById(R.id.buttonSaveKid);

      mEdiBirthDate.setOnClickListener(this);
      mImgProfilePic.setOnClickListener(this);
      mButtonSave.setOnClickListener(this);

      // Create a spinner for language selection
      mSpinnerLanguage = (Spinner) v.findViewById(R.id.spinnerDefaultLanguage);
      mSpinnerLanguage.setOnItemSelectedListener(this);
      ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
            getActivity(), R.array.array_languages,
            R.layout.lt_spinner_item);
      adapter
            .setDropDownViewResource(R.layout.lt_spinner_dropdown_item);
      mSpinnerLanguage.setAdapter(adapter);

      mCurrentKidId = getActivity().getIntent().getLongExtra(
            Prefs.CURRENT_KID_ID, -1);
      // Log.i(DEBUG_TAG, "kid id = " + mCurrentKidId);

      if (savedInstanceState != null)
      {
         if (savedInstanceState.getString(Prefs.PROFILE_PIC_PATH) != null)
         {
            mUriPicture = Uri.parse(savedInstanceState
                  .getString(Prefs.PROFILE_PIC_PATH));
            try
            {
               Bitmap photo = BitmapFactory.decodeFile(mPicturePath);
               mImgProfilePic.setImageBitmap(photo);
               photo = null;
            } catch (Exception e)
            {
               e.printStackTrace();
            }
         }
      } 
      // If editing/viewing an existing kid, fill all the fields
      if (mCurrentKidId > 0) 
      {
         insertKidDetails(mCurrentKidId);
      }

      return v;
   }

   @Override
   public void onClick(View v)
   {
      switch (v.getId())
      {
      case R.id.editBirthDate:
        showCalendar(v);
        break;
      case R.id.profilePicture:
        showProfileDialog();
        break;
      case R.id.buttonSaveKid:
        saveKid();
        break;
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

   private void updateDate()
   {
      mEdiBirthDate.setText(DateUtils.formatDateTime(this.getActivity(),
            mBirthDate.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
                  | DateUtils.FORMAT_SHOW_YEAR));
   }

   DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener()
   {
      public void onDateSet(DatePicker view, int year, int monthOfYear,
            int dayOfMonth)
      {
         mBirthDate.set(Calendar.YEAR, year);
         mBirthDate.set(Calendar.MONTH, monthOfYear);
         mBirthDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
         updateDate();
      }
   };

   private void showCalendar(View v)
   {
      new DatePickerDialog(v.getContext(), d, mBirthDate.get(Calendar.YEAR),
            mBirthDate.get(Calendar.MONTH),
            mBirthDate.get(Calendar.DAY_OF_MONTH)).show();
   }

   public void insertKidDetails(long mCurrentKidId)
   {
      getActivity().getActionBar().setTitle(R.string.edit_little_talker);

      Cursor cursor = DbSingleton.get().getKidDetails(mCurrentKidId);
      cursor.moveToFirst();
      mEditName
            .setText(cursor.getString(
                  cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_NAME))
                  .toString());
      mEdiBirthDate.setText(cursor.getString(
            cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_BIRTHDATE))
            .toString());
      mEditLocation
            .setText(cursor
                  .getString(
                        cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_DEFAULT_LOCATION))
                  .toString());

      ArrayAdapter<String> adapter = (ArrayAdapter<String>) mSpinnerLanguage
            .getAdapter();
      mSpinnerLanguage.setSelection(adapter.getPosition(cursor.getString(cursor
            .getColumnIndex(DbContract.Kids.COLUMN_NAME_DEFAULT_LANGUAGE))));

      mPicturePath = cursor.getString(cursor
            .getColumnIndex(DbContract.Kids.COLUMN_NAME_PICTURE_URI));
      cursor.close();

      if (mPicturePath == null)
      {
         return;
      }

      Log.i(DEBUG_TAG, mPicturePath);

      Bitmap photoBitmap = BitmapFactory.decodeFile(mPicturePath);
      Log.i(DEBUG_TAG, "Width: " + photoBitmap.getWidth());
      Log.i(DEBUG_TAG, "Height: " + photoBitmap.getHeight());

      mImgProfilePic.setImageBitmap(photoBitmap);
   }

   private void saveKid()
   {
      String name, location, birthday;

      // Name and Birthday are required, do validation
      if (mEditName.length() == 0)
      {
         mEditName.setError(getString(R.string.name_required_error));
         return;
      }

      if (mEdiBirthDate.length() == 0)
      {
         mEdiBirthDate.requestFocus();
         mEdiBirthDate.setError(getString(R.string.birthdate_required_error));
         return;
      }

      name = mEditName.getText().toString();
      birthday = mEdiBirthDate.getText().toString();
      location = mEditLocation.getText().toString();

      if (this.mTempBitmapSaved)
      {
         renameFile();
      }

      // Adding new kid
      if (mCurrentKidId < 0)
      {
         mCurrentKidId = DbSingleton.get().saveKid(name, birthday, location,
               mLanguage, mPicturePath);
      }

      // Updating a current kid
      else
      {
         if (!DbSingleton.get().updateKid(mCurrentKidId, name, birthday,
               location, mLanguage, mPicturePath))
         {
            // TODO error message (duplicate kid name)
            return;
         } else
         {
            // Kid was updated
            String msg = name + " " + getString(R.string.kid_updated);
            Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG);
            toast.show();
            mListener.onKidUpdated(mCurrentKidId);
            return;
         }
      }

      if (mCurrentKidId == -1)
      {
         mEditName.setError(getString(R.string.kid_already_exists_error));
         return;
      }

      // Kid was saved
      String msg = name + " " + getString(R.string.kid_saved);
      Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG);
      toast.show();
      if (mCurrentKidId < 0)
      {
         getActivity().invalidateOptionsMenu();
      }

      Prefs.saveKidId(getActivity(), mCurrentKidId);
      mListener.onKidAdded(mCurrentKidId);
   }

   public void showProfileDialog()
   {
      chooseProfilePic();
      // Create an instance of the dialog fragment and show it
      // ChangeProfilePicDialog dlg = new ChangeProfilePicDialog();
      // dlg.setTargetFragment(this, PICTURE_DIALOG_ID);
      // dlg.show(getFragmentManager(), "ChangeProfilePicDialog");
   }

   private void chooseProfilePic()
   {
      Intent intent = new Intent(Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
      intent.setType("image/*");
      startActivityForResult(intent, PICK_FROM_FILE);
   }

   public static class ChangeProfilePicDialog extends DialogFragment
   {
      @Override
      public Dialog onCreateDialog(Bundle savedInstanceState)
      {
         AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
         builder.setItems(R.array.choose_profile_pic_array,
               new DialogInterface.OnClickListener()
               {
                  public void onClick(DialogInterface dialog, int which)
                  {
                     Uri pictureUri;
                     switch (which)
                     {
                     case TAKE_PICTURE:
                        Intent intent = new Intent(
                              MediaStore.ACTION_IMAGE_CAPTURE);
                        try
                        {
                           String state = Environment.getExternalStorageState();
                           if (Environment.MEDIA_MOUNTED.equals(state))
                           {
                              pictureUri = Uri.fromFile(new File(Environment
                                    .getExternalStorageDirectory(),
                                    "lt_temp.jpg"));
                           } else
                           {
                              pictureUri = Uri.fromFile(new File(getActivity()
                                    .getFilesDir(), "lt_temp.jpg"));
                           }
                           Log.i(DEBUG_TAG, pictureUri.toString());
                           intent.putExtra(
                                 android.provider.MediaStore.EXTRA_OUTPUT,
                                 pictureUri);
                           intent.putExtra("return-data", true);
                           getTargetFragment().startActivityForResult(intent,
                                 TAKE_PICTURE);
                        } catch (ActivityNotFoundException e)
                        {
                           Log.d(DEBUG_TAG, "cannot take picture", e);
                        }
                        break;

                     case PICK_FROM_FILE:
                        intent = new Intent(
                              Intent.ACTION_PICK,
                              android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        getTargetFragment().startActivityForResult(intent,
                              PICK_FROM_FILE);
                     }
                  }
               });
         // Create the AlertDialog object and return it
         return builder.create();
      }
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data)
   {
      if (data == null)
      {
         Log.i(DEBUG_TAG, "RESULT: " + resultCode);
         return;
      }

      mUriPicture = data.getData();
      switch (requestCode)
      {
      case TAKE_PICTURE:
         //mBitmapProfile = BitmapFactory.decodeFile(mUriPicture.getPath());
         //Bitmap thumb = Bitmap.createScaledBitmap(mBitmapProfile, IMAGE_SIZE,
         //      IMAGE_SIZE, false);
         //mButtonProfilePic.setImageBitmap(thumb);
         break;

      case PICK_FROM_FILE:
         if (resultCode == Activity.RESULT_OK)
         {
            mUriPicture = data.getData();
            /*
             * try { cropPicture(mUriPicture); //the user's device may not
             * support cropping } catch(ActivityNotFoundException aNFE){
             * //display an error message if user device doesn't support String
             * errorMessage =
             * "Sorry - your device doesn't support the crop action!"; Toast
             * toast = Toast.makeText(this.getActivity(), errorMessage,
             * Toast.LENGTH_SHORT); toast.show(); }
             */

            try
            {
               Bitmap photo = MediaStore.Images.Media.getBitmap(this
                     .getActivity().getContentResolver(), mUriPicture);
               photo = ThumbnailUtils.extractThumbnail(photo,
                     IMAGE_SIZE, IMAGE_SIZE);
               mImgProfilePic.setImageBitmap(photo);
               saveProfileBitmapFile(photo);
            } catch (Exception e)
            {
               e.printStackTrace();
            }          
         }
         break;

      case CROP_PICTURE:
         Bundle extras = data.getExtras();
         Bitmap thePic = extras.getParcelable("data");
         mUriPicture = data.getData();
         mImgProfilePic.setImageBitmap(thePic);
         break;
      }
      super.onActivityResult(requestCode, resultCode, data);
   }

   public void cropPicture(Uri picUri)
   {
      // call the standard crop action intent
      Intent cropIntent = new Intent("com.android.camera.action.CROP");
      // indicate image type and Uri of image
      cropIntent.setDataAndType(picUri, "image/*");
      // set crop properties
      cropIntent.putExtra("crop", "true");
      // indicate aspect of desired crop
      cropIntent.putExtra("aspectX", 1);
      cropIntent.putExtra("aspectY", 1);
      // indicate output X and Y
      cropIntent.putExtra("outputX", 120);
      cropIntent.putExtra("outputY", 120);
      // retrieve data on return
      cropIntent.putExtra("return-data", true);
      // start the activity - we handle returning in onActivityResult
      startActivityForResult(cropIntent, CROP_PICTURE);
   }

   private void saveProfileBitmapFile(Bitmap photo)
   {
      if (photo == null)
      {
         mPicturePath = "";
         return;
      }

      String filename;
      boolean temp = mEditName.getText().toString().isEmpty();
      if (temp)
      {
         filename = TEMP_PHOTO_FILE_NAME;
      } else
      {
         filename = mEditName.getText().toString() + ".png";
      }

      File file = null;

      String state = Environment.getExternalStorageState();
      if (Environment.MEDIA_MOUNTED.equals(state))
      {
         file = new File(getActivity().getExternalFilesDir(
               Environment.DIRECTORY_PICTURES), filename);
      } else
      {
         file = new File(getActivity().getFilesDir(), filename);
      }

      mPicturePath = file.getAbsolutePath();
      Log.i(DEBUG_TAG, mPicturePath);

      FileOutputStream out = null;
      try
      {
         out = new FileOutputStream(file);
         photo.compress(Bitmap.CompressFormat.PNG, 100, out);
      } catch (Exception e)
      {
         e.printStackTrace();
      } finally
      {
         try
         {
            out.flush();
            out.close();
            if (temp)
            {
               mTempBitmapSaved = true;
            }
         } catch (Throwable ignore)
         {
         }
      }
   }

   public void renameFile()
   {
      File oldfile = new File(mPicturePath);
      File newfile = null;
      String filename = mEditName.getText().toString() + ".png";

      String state = Environment.getExternalStorageState();
      if (Environment.MEDIA_MOUNTED.equals(state))
      {
         newfile = new File(getActivity().getExternalFilesDir(
               Environment.DIRECTORY_PICTURES), filename);
      } else
      {
         newfile = new File(getActivity().getFilesDir(), filename);
      }

      mPicturePath = newfile.getAbsolutePath();
      if (newfile.exists())
      {
         newfile.delete();
      }

      Log.i(DEBUG_TAG, "Oldfile: " + oldfile.getAbsolutePath());
      Log.i(DEBUG_TAG, "Newfile: " + newfile.getAbsolutePath());

      if (oldfile.renameTo(newfile))
      {
         Log.i(DEBUG_TAG, "Rename succesful");
      } else
      {
         Log.i(DEBUG_TAG, "Rename failed");
      }

      oldfile.delete();
   }

   public interface OnKidAddedListener
   {
      public void onKidAdded(long kidId);
      public void onKidUpdated(long kidId);
   }

   @Override
   public void onAttach(Activity activity)
   {
      super.onAttach(activity);
      if (activity instanceof OnKidAddedListener)
      {
         mListener = (OnKidAddedListener) activity;
      } else
      {
         throw new ClassCastException(activity.toString()
               + " must implemenet AddKidFragment.OnKidAddedListener");
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
      if (mPicturePath != null)
      {
         outState.putString(Prefs.PROFILE_PIC_PATH, mPicturePath);
      }
   }

   @Override
   public void onDestroyView()
   {
      super.onDestroyView();
   }
}
