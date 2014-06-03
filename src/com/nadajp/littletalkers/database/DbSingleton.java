package com.nadajp.littletalkers.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.database.DbContract.Words;

public class DbSingleton
{
   private static DbSingleton sInstance;
   private Context mContext;
   private SQLiteDatabase mDb;

   public DbSingleton(Context appContext)
   {
      // Create my one and only handle the database.
      DatabaseHelper dbHelper = new DatabaseHelper(appContext);
      mDb = dbHelper.getWritableDatabase();
      mContext = appContext;
   }

   public static void init(Context appContext)
   {
      sInstance = new DbSingleton(appContext);
   }

   public static DbSingleton get()
   {
      if (sInstance == null)
      {
         throw new java.lang.IllegalStateException(
               "DbSingleton has not been initialized.");
      }
      return sInstance;
   }

   public int getNumberOfKids()
   {
      Cursor cursor = null;
      try
      {
         cursor = mDb.rawQuery("SELECT COUNT(*) FROM Kids", null);
         cursor.moveToFirst();
         return cursor.getInt(0);
      } finally
      {
         if (cursor != null)
         {
            cursor.close();
         }
      }
   }

   public int getNumberOfWords(long kidId)
   {
      Cursor cursor = null;
      try
      {
         cursor = mDb.rawQuery("SELECT COUNT(*) FROM Words WHERE "
               + DbContract.Words.COLUMN_NAME_KID + " = " + kidId, null);
         cursor.moveToFirst();
         return cursor.getInt(0);
      } finally
      {
         if (cursor != null)
         {
            cursor.close();
         }
      }
   }

   public int getNumberOfQAs(long kidId)
   {
      Cursor cursor = null;
      try
      {
         cursor = mDb.rawQuery("SELECT COUNT(*) FROM Questions WHERE "
               + DbContract.Questions.COLUMN_NAME_KID + " = " + kidId, null);
         cursor.moveToFirst();
         return cursor.getInt(0);
      } finally
      {
         if (cursor != null)
         {
            cursor.close();
         }
      }
   }

   public String getKidName(long kidId)
   {
      Cursor cursor = null;
      try
      {
         cursor = mDb.rawQuery("SELECT " + DbContract.Kids.COLUMN_NAME_NAME
               + " FROM Kids WHERE " + DbContract.Kids._ID + " = " + kidId,
               null);
         cursor.moveToFirst();
         return cursor.getString(0);
      } finally
      {
         if (cursor != null)
         {
            cursor.close();
         }
      }
   }

   public Cursor getKidsForSpinner()
   {
      return mDb.rawQuery("SELECT _id, name FROM Kids", null);
   }

   public Cursor getWords(long kidId, String sortColumn, boolean bAscending,
         String language)
   {
      String query;

      if (language.equals(mContext.getString(R.string.all_languages)))
      {
         query = "SELECT _id, " + DbContract.Words.COLUMN_NAME_WORD + ", "
               + DbContract.Words.COLUMN_NAME_DATE + ", "
               + DbContract.Words.COLUMN_NAME_AUDIO_FILE + " FROM Words WHERE "
               + DbContract.Words.COLUMN_NAME_KID + " = " + kidId
               + " ORDER BY " + sortColumn;
      } else {
         query = "SELECT _id, " + DbContract.Words.COLUMN_NAME_WORD + ", "
               + DbContract.Words.COLUMN_NAME_DATE + ", "
               + DbContract.Words.COLUMN_NAME_AUDIO_FILE + " FROM Words WHERE "
               + DbContract.Words.COLUMN_NAME_KID + " = " + kidId + " AND "
               + DbContract.Words.COLUMN_NAME_LANGUAGE + " = '" + language
               + "' ORDER BY " + sortColumn;
      }
      if (bAscending) { query += " ASC"; } 
      else { query += " DESC"; }

      return mDb.rawQuery(query, null);
   }

   public Cursor getQuestions(long kidId, String sortColumn,
         boolean bAscending, String language)
   {
      String query;

      if (language.equals(mContext.getString(R.string.all_languages)))
      {
         query = "SELECT _id, " + DbContract.Questions.COLUMN_NAME_QUESTION
               + ", " + DbContract.Questions.COLUMN_NAME_ANSWER + ", "
               + DbContract.Questions.COLUMN_NAME_ASKED + ","
               + DbContract.Questions.COLUMN_NAME_ANSWERED + ","
               + DbContract.Questions.COLUMN_NAME_TOWHOM + ","
               + DbContract.Questions.COLUMN_NAME_DATE + ", "
               + DbContract.Questions.COLUMN_NAME_AUDIO_FILE
               + " FROM Questions WHERE "
               + DbContract.Questions.COLUMN_NAME_KID + " = " + kidId
               + " ORDER BY " + sortColumn;
      } else
      {
         query = "SELECT _id, " + DbContract.Questions.COLUMN_NAME_QUESTION
               + ", " + DbContract.Questions.COLUMN_NAME_ANSWER + ", "
               + DbContract.Questions.COLUMN_NAME_ASKED + ","
               + DbContract.Questions.COLUMN_NAME_ANSWERED + ","
               + DbContract.Questions.COLUMN_NAME_TOWHOM + ","
               + DbContract.Questions.COLUMN_NAME_DATE + ", "
               + DbContract.Questions.COLUMN_NAME_AUDIO_FILE
               + " FROM Questions WHERE "
               + DbContract.Questions.COLUMN_NAME_KID + " = " + kidId + " AND "
               + DbContract.Questions.COLUMN_NAME_LANGUAGE + " = '" + language
               + "' ORDER BY " + sortColumn;
      }
      if (bAscending)
      {
         query += " ASC";
      } else
      {
         query += " DESC";
      }

      return mDb.rawQuery(query, null);
   }

   public Cursor getWordsForExport(long kidId)
   {
      String query;

      query = "SELECT _id, " + DbContract.Words.COLUMN_NAME_WORD + ", "
            + DbContract.Words.COLUMN_NAME_DATE + ", "
            + DbContract.Words.COLUMN_NAME_LANGUAGE + ", "
            + DbContract.Words.COLUMN_NAME_TRANSLATION + " FROM Words WHERE "
            + DbContract.Words.COLUMN_NAME_KID + " = " + kidId + " ORDER BY "
            + Words.COLUMN_NAME_DATE + " ASC";

      return mDb.rawQuery(query, null);
   }
   
   public Cursor getQAForExport(long kidId)
   {
      String query;

      query = "SELECT _id, " 
            + DbContract.Questions.COLUMN_NAME_QUESTION + ", "
            + DbContract.Questions.COLUMN_NAME_ANSWER + ", "
            + DbContract.Questions.COLUMN_NAME_ASKED + ", "
            + DbContract.Questions.COLUMN_NAME_ANSWERED + ", "
            + DbContract.Questions.COLUMN_NAME_DATE + ", "
            + DbContract.Questions.COLUMN_NAME_LANGUAGE + ", "
            + DbContract.Questions.COLUMN_NAME_TOWHOM + " FROM Questions WHERE "
            + DbContract.Questions.COLUMN_NAME_KID + " = " + kidId + " ORDER BY "
            + DbContract.Questions.COLUMN_NAME_DATE + " ASC";

      return mDb.rawQuery(query, null);
   }

   public String getTranslation(long wordId)
   {
      String query = "SELECT " + DbContract.Words.COLUMN_NAME_TRANSLATION
            + " FROM Words WHERE _id = " + wordId;

      Cursor cursor = null;
      try
      {
         cursor = mDb.rawQuery(query, null);
         cursor.moveToFirst();
         return cursor.getString(0);
      } finally {
         if (cursor != null) {
            cursor.close();
         }
      }
   }

   public Cursor getWordHistory(long kidId, long wordId)
   {
      String query;
      String translation = getTranslation(wordId);

      query = "SELECT _id, " + DbContract.Words.COLUMN_NAME_WORD + ", "
            + DbContract.Words.COLUMN_NAME_DATE + ", "
            + DbContract.Words.COLUMN_NAME_AUDIO_FILE + " FROM Words WHERE "
            + DbContract.Words.COLUMN_NAME_KID + " = " + kidId + " AND "
            + DbContract.Words.COLUMN_NAME_TRANSLATION + " = '" + translation
            + "' ORDER BY " + DbContract.Words.COLUMN_NAME_DATE + " ASC";

      return mDb.rawQuery(query, null);
   }

   public List<String> getLanguages(long kidId)
   {
      String query = "SELECT DISTINCT " + DbContract.Words.COLUMN_NAME_LANGUAGE
            + " FROM Words WHERE " + DbContract.Words.COLUMN_NAME_KID + " = "
            + kidId + " ORDER BY " + DbContract.Words.COLUMN_NAME_LANGUAGE
            + " ASC";

      List<String> languages = new ArrayList<String>();
      Cursor cursor = mDb.rawQuery(query, null);

      // looping through all rows and adding to list
      if (cursor.moveToFirst())
      {
         do
         {
            languages.add(cursor.getString(cursor
                  .getColumnIndex(DbContract.Words.COLUMN_NAME_LANGUAGE)));
         } while (cursor.moveToNext());
      }
      cursor.close();
      return languages;
   }

   public Cursor getWordDetails(long wordId)
   {
      String query = "SELECT * FROM Words WHERE _id = " + wordId;
      return mDb.rawQuery(query, null);
   }

   public Cursor getQuestionDetails(long questionID)
   {
      String query = "SELECT * FROM Questions WHERE _id = " + questionID;
      return mDb.rawQuery(query, null);
   }

   public Cursor getKidDetails(long kidId)
   {
      String query = "SELECT * FROM Kids WHERE _id = " + kidId;
      return mDb.rawQuery(query, null);
   }

   public String getDefaultLanguage(String kidName)
   {
      String query = "SELECT " + DbContract.Kids.COLUMN_NAME_DEFAULT_LANGUAGE
            + " FROM Kids WHERE name = '" + kidName + "' ";
      // Log.i(DEBUG_TAG, query);
      Cursor cursor = null;
      try
      {
         cursor = mDb.rawQuery(query, null);
         cursor.moveToFirst();
         return cursor.getString(0);
      } finally
      {
         if (cursor != null)
         {
            cursor.close();
         }
      }
   }

   public String[] getDefaults(long kidId)
   {
      String[] rtn = new String[2];
      String query = "SELECT " + DbContract.Kids.COLUMN_NAME_DEFAULT_LANGUAGE
            + "," + DbContract.Kids.COLUMN_NAME_DEFAULT_LOCATION
            + " FROM Kids WHERE _id = " + kidId;
      // Log.i(DEBUG_TAG, query);
      Cursor cursor = null;
      try
      {
         cursor = mDb.rawQuery(query, null);
         cursor.moveToFirst();
         rtn[0] = cursor.getString(cursor
               .getColumnIndex(DbContract.Kids.COLUMN_NAME_DEFAULT_LANGUAGE));
         rtn[1] = cursor.getString(cursor
               .getColumnIndex(DbContract.Kids.COLUMN_NAME_DEFAULT_LOCATION));
      } finally
      {
         if (cursor != null)
         {
            cursor.close();
         }
      }
      return rtn;
   }

   public long getLastAddedKid()
   {
      String query = "SELECT _id FROM Kids ORDER BY _id DESC LIMIT 1";
      // Log.i(DEBUG_TAG, query);
      Cursor cursor = null;
      try
      {
         cursor = mDb.rawQuery(query, null);
         cursor.moveToFirst();
         return cursor.getLong(0);
      } finally
      {
         if (cursor != null)
            cursor.close();
      }
   }

   public long saveKid(String name, String birthday, String location,
         String language, String pictureUri)
   {
      // check if name already exists
      String query = "SELECT * FROM Kids WHERE name = '" + name + "'";
      Cursor cursor = mDb.rawQuery(query, null);
      if (cursor.getCount() > 0)
      {
         cursor.close();
         return -1;
      }
      cursor.close();

      ContentValues values = new ContentValues();
      values.put(DbContract.Kids.COLUMN_NAME_NAME, name);
      values.put(DbContract.Kids.COLUMN_NAME_BIRTHDATE, birthday);
      values.put(DbContract.Kids.COLUMN_NAME_DEFAULT_LOCATION, location);
      values.put(DbContract.Kids.COLUMN_NAME_DEFAULT_LANGUAGE, language);
      values.put(DbContract.Kids.COLUMN_NAME_PICTURE_URI, pictureUri);

      // Insert row and return row id
      return mDb.insert("Kids", null, values);
   }

   public boolean updateKid(long id, String name, String birthday,
         String location, String language, String pictureUri)
   {
      // check if another kid with this name already exists
      String query = "SELECT " + DbContract.Kids.COLUMN_NAME_NAME
            + " FROM Kids WHERE " + DbContract.Kids.COLUMN_NAME_NAME + " = '"
            + name + "' AND _id != " + id;

      // if there is a different kid (different ID) with the same name, return
      // false
      Cursor cursor = mDb.rawQuery(query, null);
      if (cursor.getCount() > 0)
      {
         cursor.close();
         return false;
      }

      cursor.close();

      // otherwise, update all values
      ContentValues values = new ContentValues();
      values.put(DbContract.Kids.COLUMN_NAME_NAME, name);
      values.put(DbContract.Kids.COLUMN_NAME_BIRTHDATE, birthday);
      values.put(DbContract.Kids.COLUMN_NAME_DEFAULT_LANGUAGE, language);
      values.put(DbContract.Kids.COLUMN_NAME_DEFAULT_LOCATION, location);
      values.put(DbContract.Kids.COLUMN_NAME_PICTURE_URI, pictureUri);

      // update
      mDb.update("Kids", values, "_id=" + id, null);
      return true;
   }

   public void deleteKids(long[] ids)
   {
      for (long id : ids)
      {
         mDb.delete("Kids", "_id = " + id, null);
         mDb.delete("Words", DbContract.Words.COLUMN_NAME_KID + " = " + id,
               null);
      }
   }

   public String getPicturePath(long id)
   {
      String query = "SELECT " + DbContract.Kids.COLUMN_NAME_PICTURE_URI
            + " FROM Kids WHERE _id = " + id;
      Cursor cursor = null;
      try
      {
         cursor = mDb.rawQuery(query, null);
         cursor.moveToFirst();
         return cursor.getString(0);
      } finally
      {
         if (cursor != null)
         {
            cursor.close();
         }
      }
   }

   public void deleteWords(long[] ids)
   {
      for (long id : ids)
      {
         mDb.delete("Words", "_id = " + id, null);
      }
   }
   
   public void deleteQuestions(long[] ids)
   {
      for (long id : ids)
      {
         mDb.delete("Questions", "_id = " + id, null);
      }
   }

   public boolean saveWord(long kidId, String word, String language, long date,
         String location, String audioFile, String translation, String towhom,
         String notes)
   {
      // check if word already exists for this kid
      String query = "SELECT word FROM Words WHERE "
            + DbContract.Words.COLUMN_NAME_KID + " = " + kidId
            + " AND word = '" + word + "'";
      Cursor cursor = mDb.rawQuery(query, null);
      if (cursor.getCount() > 0)
      {
         cursor.close();
         return false;
      }
      cursor.close();
      ContentValues values = new ContentValues();
      values.put(DbContract.Words.COLUMN_NAME_KID, kidId);
      values.put(DbContract.Words.COLUMN_NAME_WORD, word);
      values.put(DbContract.Words.COLUMN_NAME_LANGUAGE, language);
      values.put(DbContract.Words.COLUMN_NAME_DATE, date);
      values.put(DbContract.Words.COLUMN_NAME_LOCATION, location);
      values.put(DbContract.Words.COLUMN_NAME_AUDIO_FILE, audioFile);
      values.put(DbContract.Words.COLUMN_NAME_TRANSLATION, translation);
      values.put(DbContract.Words.COLUMN_NAME_TOWHOM, towhom);
      values.put(DbContract.Words.COLUMN_NAME_NOTES, notes);

      // Inserting Row
      mDb.insert("Words", null, values);
      return true;
   }

   public boolean saveQuestion(long kidId, String question, String answer,
         int asked, int answered, String towhom, String language, long date,
         String location, String audioFile, String notes)
   {
      // check if qa already exists for this kid
      String query = "SELECT question FROM Questions WHERE "
            + DbContract.Questions.COLUMN_NAME_KID + " = " + kidId
            + " AND question = '" + question + "'" + " AND answer = '" + answer
            + "'";
      Cursor cursor = mDb.rawQuery(query, null);
      if (cursor.getCount() > 0)
      {
         cursor.close();
         return false;
      }
      cursor.close();
      ContentValues values = new ContentValues();
      values.put(DbContract.Questions.COLUMN_NAME_KID, kidId);
      values.put(DbContract.Questions.COLUMN_NAME_QUESTION, question);
      values.put(DbContract.Questions.COLUMN_NAME_ANSWER, answer);
      values.put(DbContract.Questions.COLUMN_NAME_TOWHOM, towhom);
      values.put(DbContract.Questions.COLUMN_NAME_ASKED, asked);
      values.put(DbContract.Questions.COLUMN_NAME_ANSWERED, answered);
      values.put(DbContract.Questions.COLUMN_NAME_LANGUAGE, language);
      values.put(DbContract.Questions.COLUMN_NAME_DATE, date);
      values.put(DbContract.Questions.COLUMN_NAME_LOCATION, location);
      values.put(DbContract.Questions.COLUMN_NAME_AUDIO_FILE, audioFile);
      values.put(DbContract.Questions.COLUMN_NAME_NOTES, notes);

      // Inserting Row
      mDb.insert("Questions", null, values);
      return true;

   }

   public boolean updateWord(long wordId, long kidId, String word,
         String language, long date, String location, String audioFile,
         String translation, String towhom, String notes)
   {
      ContentValues values = new ContentValues();
      values.put(DbContract.Words.COLUMN_NAME_KID, kidId);
      values.put(DbContract.Words.COLUMN_NAME_WORD, word);
      values.put(DbContract.Words.COLUMN_NAME_LANGUAGE, language);
      values.put(DbContract.Words.COLUMN_NAME_DATE, date);
      values.put(DbContract.Words.COLUMN_NAME_LOCATION, location);
      values.put(DbContract.Words.COLUMN_NAME_AUDIO_FILE, audioFile);
      values.put(DbContract.Words.COLUMN_NAME_TRANSLATION, translation);
      values.put(DbContract.Words.COLUMN_NAME_TOWHOM, towhom);
      values.put(DbContract.Words.COLUMN_NAME_NOTES, notes);

      // update
      mDb.update("Words", values, "_id=" + wordId, null);
      return true;
   }

   public boolean updateQuestion(long questionID, long kidId, String question,
         String answer, int asked, int answered, String towhom,
         String language, long date, String location, String audioFile,
         String notes)
   {
      ContentValues values = new ContentValues();
      values.put(DbContract.Questions.COLUMN_NAME_KID, kidId);
      values.put(DbContract.Questions.COLUMN_NAME_QUESTION, question);
      values.put(DbContract.Questions.COLUMN_NAME_ANSWER, answer);
      values.put(DbContract.Questions.COLUMN_NAME_TOWHOM, towhom);
      values.put(DbContract.Questions.COLUMN_NAME_ASKED, asked);
      values.put(DbContract.Questions.COLUMN_NAME_ANSWERED, answered);
      values.put(DbContract.Questions.COLUMN_NAME_LANGUAGE, language);
      values.put(DbContract.Questions.COLUMN_NAME_DATE, date);
      values.put(DbContract.Questions.COLUMN_NAME_LOCATION, location);
      values.put(DbContract.Questions.COLUMN_NAME_AUDIO_FILE, audioFile);
      values.put(DbContract.Questions.COLUMN_NAME_NOTES, notes);

      // Update Row
      mDb.update("Questions", values, "_id=" + questionID, null);
      return true;

   }

   public void updateAudoFile(long wordId, String audioFile)
   {
      ContentValues values = new ContentValues();
      values.put(DbContract.Words.COLUMN_NAME_AUDIO_FILE, audioFile);
      mDb.update("Words", values, "_id=" + wordId, null);
   }
}
