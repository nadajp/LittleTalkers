package com.nadajp.littletalkers.utils;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.database.DbContract;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs
{
   public static final String SHARED_PREFS_FILENAME = "com.nadajp.littletalkers.shared_prefs";
   public static final String CURRENT_KID_ID = "current_kid_id";
   public static final String PROFILE_PIC_PATH = "profile_picture_path";
   public static final String LANGUAGE_FILTER = "language_filter";
   public static final String SORT_ASCENDING = "sort_ascending";
   public static final String SORT_COLUMN = "sort_column";
   public static final String WORD_ID = "word_id";
   public static final String POSITION = "position";

   public static long getKidId(Context context, long defaultId)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      return sharedPrefs.getLong(CURRENT_KID_ID, defaultId);
   }

   public static void saveKidId(Context context, long id)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPrefs.edit();
      editor.putLong(CURRENT_KID_ID, id);
      editor.commit();
   }

   public static String getLanguage(Context context)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      return sharedPrefs.getString(LANGUAGE_FILTER,
            context.getString(R.string.all_languages));
   }

   public static String getSortColumn(Context context)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      return sharedPrefs.getString(SORT_COLUMN,
            DbContract.Words.COLUMN_NAME_WORD);
   }

   public static boolean getIsAscending(Context context)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      return sharedPrefs.getBoolean(SORT_ASCENDING, true);
   }

   public static void saveAll(Context context, long id, String language,
         String column, boolean ascending)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPrefs.edit();

      editor.putLong(CURRENT_KID_ID, id);
      editor.putString(LANGUAGE_FILTER, language);
      editor.putString(SORT_COLUMN, column);
      editor.putBoolean(SORT_ASCENDING, ascending);

      editor.commit();
   }
}
