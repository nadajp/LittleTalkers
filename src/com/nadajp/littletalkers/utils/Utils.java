package com.nadajp.littletalkers.utils;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.ActionBar;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbSingleton;

public class Utils
{
   private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
   private static final String DEBUG_TAG = "Utils";

   public static final int COLOR_BLUE = 0;
   public static final int COLOR_GREEN = 1;
   public static final int COLOR_RED = 2;
   public static final int COLOR_ORANGE = 3;

   public static void setColor(ActionBar actionBar, int color, Context context)
   {
      switch (color)
      {
      case COLOR_BLUE:
         actionBar.setBackgroundDrawable(context.getResources().getDrawable(
               R.drawable.ab_bottom_solid_littletalkersstyle));
         actionBar.setStackedBackgroundDrawable(context.getResources()
               .getDrawable(R.drawable.ab_stacked_solid_littletalkersstyle));
         break;
      case COLOR_GREEN:
         actionBar.setBackgroundDrawable(context.getResources().getDrawable(
               R.drawable.ab_bottom_solid_littletalkersgreenstyle));
         actionBar.setStackedBackgroundDrawable(context.getResources()
               .getDrawable(R.drawable.ab_stacked_solid_littletalkersgreenstyle));
         break; 
      case COLOR_RED:
         actionBar.setBackgroundDrawable(context.getResources().getDrawable(
               R.drawable.ab_bottom_solid_littletalkersredstyle));
         actionBar.setStackedBackgroundDrawable(context.getResources()
               .getDrawable(R.drawable.ab_stacked_solid_littletalkersredstyle));
         break;   
      case COLOR_ORANGE:
         actionBar.setBackgroundDrawable(context.getResources().getDrawable(
               R.drawable.ab_bottom_solid_littletalkersorangestyle));
         actionBar.setStackedBackgroundDrawable(context.getResources()
               .getDrawable(R.drawable.ab_stacked_solid_littletalkersorangestyle));
         break; 
      }
      

   }

   public static String getDateForDisplay(String rawdate, Context context)
   {
      String[] dateArray = rawdate.split("-");
      Calendar date = Calendar.getInstance();
      date.set(Calendar.YEAR, Integer.parseInt(dateArray[0]));
      date.set(Calendar.MONTH, Integer.parseInt(dateArray[1]));
      date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[2]));

      String formatted = DateUtils.formatDateTime(context,
            date.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
                  | DateUtils.FORMAT_SHOW_YEAR);
      return formatted;
   }

   public static String getDateForDisplay(long msDate, Context context)
   {
      String formatted = "";
      if (msDate == 0)
      {
         return formatted;
      }
      return DateUtils.formatDateTime(context, msDate,
            DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
   }

   public static File getPublicDirectory(String subdirectory, Context context)
   {
      String state = Environment.getExternalStorageState();
      File directory;

      if (Environment.MEDIA_MOUNTED.equals(state))
      {
         directory = new File(
               Environment
                     .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
               subdirectory);
      } else
      {
         directory = new File(context.getFilesDir(), subdirectory);
      }

      if (!directory.exists())
      {
         directory.mkdir();
      }
      return directory;
   }

   /**
    * Generate a value suitable for use in {@link #setId(int)}. This value will
    * not collide with ID values generated at build time by aapt for R.id.
    * 
    * @return a generated ID value
    */
   public static int generateViewId()
   {
      for (;;)
      {
         final int result = sNextGeneratedId.get();
         // aapt-generated IDs have the high byte nonzero; clamp to the range
         // under that.
         int newValue = result + 1;
         if (newValue > 0x00FFFFFF)
         {
            newValue = 1; // Roll over to 1, not 0.
         }
         if (sNextGeneratedId.compareAndSet(result, newValue))
         {
            return result;
         }
      }
   }

   public static void updateTitlebar(long kidId, View v, Context context)
   {
      TextView tvBirthdate = (TextView) v.findViewById(R.id.tvBirthdate);
      TextView tvWords = (TextView) v.findViewById(R.id.tvNumOfWords);
      TextView tvQuestions = (TextView) v.findViewById(R.id.tvNumOfQuestions);
      ImageView imageView = (ImageView) v.findViewById(R.id.ivProfilePic);

      // Log.i(DEBUG_TAG, "kidId = " + mCurrentKidId);
      Cursor cursor = DbSingleton.get().getKidDetails(kidId);
      cursor.moveToFirst();
      tvBirthdate.setText(cursor.getString(
            cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_BIRTHDATE))
            .toString());
      tvWords.setText(Integer.toString(DbSingleton.get()
            .getNumberOfWords(kidId)));
      tvQuestions.setText(Integer.toString(DbSingleton.get().getNumberOfQAs(
            kidId)));
      String pictureUri = cursor.getString(cursor
            .getColumnIndex(DbContract.Kids.COLUMN_NAME_PICTURE_URI));
      cursor.close();
      Bitmap profilePicture = null;
      if (pictureUri == null)
      {
         profilePicture = BitmapFactory.decodeResource(context.getResources(),
               R.drawable.profilepicture);
      } else
      {
         profilePicture = BitmapFactory.decodeFile(pictureUri);
      }
      imageView.setImageBitmap(profilePicture);
   }

   public static File renameAudioFile(String phrase, String kidName,
         File audioFile, File directory, Calendar date)
   {
      String[] a = phrase.split(" ");
      StringBuffer str = new StringBuffer(a[0].trim());
      for (int i = 1; i < a.length; i++)
      {
         str.append(a[i].trim());
         if (i == 5)
         {
            break;
         }
      }
      String baseFilename = kidName + "-" + str + date.getTimeInMillis()
            + ".3gp";

      File newfile = new File(directory, baseFilename);

      if (newfile.exists())
      {
         newfile.delete();
      }

      Log.i(DEBUG_TAG, "Oldfile: " + audioFile.getAbsolutePath());
      Log.i(DEBUG_TAG, "Newfile: " + newfile.getAbsolutePath());

      if (audioFile.renameTo(newfile))
      {
         Log.i(DEBUG_TAG, "Rename succesful");
      } else
      {
         Log.i(DEBUG_TAG, "Rename failed");
      }

      audioFile.delete();
      audioFile = newfile;
      return audioFile;
   }

}
