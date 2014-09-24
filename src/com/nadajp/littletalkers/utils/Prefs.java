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
   public static final String SORT_COLUMN_ID = "sort_column_id";
   public static final String ITEM_ID = "item_id";
   public static final String POSITION = "position";
   public static final String AUDIO_RECORDED = "audio_recorded";
   public static final String AUDIO_FILE = "audio_file";
   public static final String ADD_TYPE = "add_type";
   public static final String FRAGMENT_LAYOUT = "fragment_layout";
   public static final String HEADER_LAYOUT = "header_layout";
   public static final String ROW_LAYOUT = "row_layout";
   public static final String PHRASE_HEADER_ID = "phrase_header_id";
   public static final String PHRASE_COLUMN_NAME = "phrase_column_name";
   public static final String LIST_TYPE = "list_type";
   public static final String TYPE = "type";
   public static final String TAB_ID = "tab_id";
   public static final String SECOND_RECORDING = "second_recording";
   public static final String KIDS_CHANGED = "kids_changed";
   public static final String SHOWING_MORE_FIELDS = "more_fields";
   public static final String PHRASE_ENTERED = "phrase_entered";
   
   public static final int EDIT_KID = 0;

   public static final int TYPE_WORD = 0;
   public static final int TYPE_QA = 1;
   
   public static final int SORT_COLUMN_PHRASE = 0;
   public static final int SORT_COLUMN_DATE = 1;

   public static long getKidId(Context context, long defaultId)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      return sharedPrefs.getLong(CURRENT_KID_ID, defaultId);
   }

   public static int getType(Context context, int defaultType)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      return sharedPrefs.getInt(TYPE, defaultType);
   }
   
   public static void saveKidId(Context context, long id)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPrefs.edit();
      editor.putLong(CURRENT_KID_ID, id);
      editor.commit();
   }

   public static void saveType(Context context, int type)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPrefs.edit();
      editor.putInt(TYPE, type);
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
   
   public static int getSortColumnId(Context context)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      return sharedPrefs.getInt(SORT_COLUMN_ID, SORT_COLUMN_PHRASE);
   }

   public static boolean getIsAscending(Context context)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      return sharedPrefs.getBoolean(SORT_ASCENDING, true);
   }

   public static void saveAll(Context context, long id, String language,
         int column, boolean ascending)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPrefs.edit();

      editor.putLong(CURRENT_KID_ID, id);
      editor.putString(LANGUAGE_FILTER, language);
      editor.putInt(SORT_COLUMN_ID, column);
      editor.putBoolean(SORT_ASCENDING, ascending);

      editor.commit();
   }
}
