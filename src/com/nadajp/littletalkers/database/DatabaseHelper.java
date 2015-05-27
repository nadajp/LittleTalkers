package com.nadajp.littletalkers.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This creates/opens the database.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
   private static final String DEBUG_TAG = "DatabaseHelper";
   private static final int DB_VERSION = 2;
   private static final String DB_NAME = "littletalkers_db";
   private Context ctxt = null;

   public DatabaseHelper(Context ctxt)
   {
      super(ctxt, DB_NAME, null, DB_VERSION);
      this.ctxt = ctxt;
   }

   // Table Create Statements

   // Kids table create statement
   private static final String CREATE_TABLE_KIDS = "CREATE TABLE "
         + DbContract.Kids.TABLE_NAME + "(" 
         + DbContract.Kids._ID + " INTEGER PRIMARY KEY," 
         + DbContract.Kids.COLUMN_NAME_NAME + " TEXT UNIQUE," 
         + DbContract.Kids.COLUMN_NAME_BIRTHDATE_MILLIS + " INTEGER,"
         + DbContract.Kids.COLUMN_NAME_DEFAULT_LOCATION + " TEXT,"
         + DbContract.Kids.COLUMN_NAME_DEFAULT_LANGUAGE + " TEXT,"
         + DbContract.Kids.COLUMN_NAME_PICTURE_URI + " TEXT,"
         + "is_dirty INTEGER DEFAULT 1);";

   // Words table create statement
   private static final String CREATE_TABLE_WORDS = "CREATE TABLE "
         + DbContract.Words.TABLE_NAME + "(" 
         + DbContract.Words._ID + " INTEGER PRIMARY KEY," 
         + DbContract.Words.COLUMN_NAME_KID + " TEXT,"
         + DbContract.Words.COLUMN_NAME_WORD + " TEXT," 
         + DbContract.Words.COLUMN_NAME_AUDIO_FILE + " TEXT,"
         + DbContract.Words.COLUMN_NAME_LANGUAGE + " TEXT,"
         + DbContract.Words.COLUMN_NAME_DATE + " INTEGER,"
         + DbContract.Words.COLUMN_NAME_LOCATION + " TEXT,"
         + DbContract.Words.COLUMN_NAME_TRANSLATION + " TEXT,"
         + DbContract.Words.COLUMN_NAME_TOWHOM + " TEXT,"
         + DbContract.Words.COLUMN_NAME_NOTES + " TEXT,"
         + "is_dirty INTEGER DEFAULT 1);";

   // Questions table create statement
   private static final String CREATE_TABLE_QUESTIONS = "CREATE TABLE "
         + DbContract.Questions.TABLE_NAME + "(" 
         + DbContract.Questions._ID + " INTEGER PRIMARY KEY," 
         + DbContract.Questions.COLUMN_NAME_KID + " INTEGER," 
         + DbContract.Questions.COLUMN_NAME_QUESTION + " TEXT,"
         + DbContract.Questions.COLUMN_NAME_ANSWER + " TEXT,"
         + DbContract.Questions.COLUMN_NAME_TOWHOM + " TEXT,"
         + DbContract.Questions.COLUMN_NAME_ASKED + " INTEGER,"
         + DbContract.Questions.COLUMN_NAME_ANSWERED + " INTEGER,"
         + DbContract.Questions.COLUMN_NAME_LANGUAGE + " TEXT,"
         + DbContract.Questions.COLUMN_NAME_DATE + " INTEGER,"
         + DbContract.Questions.COLUMN_NAME_LOCATION + " TEXT,"
         + DbContract.Questions.COLUMN_NAME_AUDIO_FILE + " TEXT,"
         + DbContract.Words.COLUMN_NAME_NOTES + " TEXT,"
         + "is_dirty INTEGER DEFAULT 1);";

   @Override
   public void onCreate(SQLiteDatabase db)
   {
      // creating required tables
      db.execSQL(CREATE_TABLE_KIDS);
      db.execSQL(CREATE_TABLE_WORDS);
      db.execSQL(CREATE_TABLE_QUESTIONS);
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
   {
      // Logs that the database is being upgraded
      Log.w(DEBUG_TAG, "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");

      switch (oldVersion) 
      {
        case 1:
           db.execSQL("ALTER TABLE " + 
                 DbContract.Kids.TABLE_NAME + " ADD COLUMN " + "is_dirty" + " INTEGER DEFAULT 1");
          // we want both updates, so no break statement here...
        case 2:
          //db.execSQL(DATABASE_CREATE_someothertable); 
      }
   }

   @Override
   public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
   {
      onUpgrade(db, oldVersion, newVersion);
   }
}
