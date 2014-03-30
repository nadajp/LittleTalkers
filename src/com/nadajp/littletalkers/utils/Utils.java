package com.nadajp.littletalkers.utils;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbSingleton;

public class Utils
{
   private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

   public static String getDateForDisplay(String rawdate, Context context)
   {
      String[] dateArray = rawdate.split("-");
      Calendar date = Calendar.getInstance();
      date.set(Calendar.YEAR, Integer.parseInt(dateArray[0]));
      date.set(Calendar.MONTH, Integer.parseInt(dateArray[1]));
      date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[2]));
      
      String formatted = DateUtils.formatDateTime(context,
                                                  date.getTimeInMillis(),
                                                  DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_SHOW_YEAR);
      return formatted;
   }
   
   public static String getDateForDisplay(long msDate, Context context)
   {
      String formatted = DateUtils.formatDateTime(context, msDate,
                                                  DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_SHOW_YEAR);
      return formatted;
   }

  /**
   * Generate a value suitable for use in {@link #setId(int)}.
   * This value will not collide with ID values generated at build time by aapt for R.id.
   *
   * @return a generated ID value
   */
   public static int generateViewId() 
   {
      for (;;) 
      {
         final int result = sNextGeneratedId.get();
         // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
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
      TextView mTvBirthdate = (TextView) v.findViewById(R.id.tvBirthdate);
      TextView mTvWords = (TextView) v.findViewById(R.id.tvNumOfWords);
      ImageView mImageView = (ImageView) v.findViewById(R.id.ivProfilePic);
      
      //Log.i(DEBUG_TAG, "kidId = " + mCurrentKidId);
      Cursor cursor = DbSingleton.get().getKidDetails(kidId);
      cursor.moveToFirst();
      mTvBirthdate.setText(cursor.getString(cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_BIRTHDATE)).toString());
      mTvWords.setText(Integer.toString(DbSingleton.get().getNumberOfWords(kidId)));
      String pictureUri = cursor.getString(cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_PICTURE_URI));
      cursor.close();
      Bitmap profilePicture = null;
      if (pictureUri == null)
      {
         profilePicture = BitmapFactory.decodeResource(context.getResources(), R.drawable.profilepicture);
      }
      else 
      {
         profilePicture = BitmapFactory.decodeFile(pictureUri);
      }
      mImageView.setImageBitmap(profilePicture);   
    }
}
