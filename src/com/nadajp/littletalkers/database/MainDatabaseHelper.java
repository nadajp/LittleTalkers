package com.nadajp.littletalkers.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MainDatabaseHelper extends SQLiteOpenHelper
{   
   private static final String DEBUG_TAG = "MainDatabaseHelper";
   private static final int DB_VERSION = 1;
   private static final String DB_NAME = "littletalkers_db";
   
   // Kids table create statement
   private static final String CREATE_TABLE_KIDS = "CREATE TABLE "
         + DbContract.Kids.TABLE_NAME + "(" 
         + DbContract.Kids._ID + " INTEGER PRIMARY KEY," 
         + DbContract.Kids.COLUMN_NAME_NAME + " TEXT UNIQUE," 
         + DbContract.Kids.COLUMN_NAME_BIRTHDATE_MILLIS + " INTEGER,"
         + DbContract.Kids.COLUMN_NAME_DEFAULT_LOCATION + " TEXT,"
         + DbContract.Kids.COLUMN_NAME_DEFAULT_LANGUAGE + " TEXT,"
         + DbContract.Kids.COLUMN_NAME_PICTURE_URI + " TEXT" + ");";

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
         + DbContract.Words.COLUMN_NAME_NOTES + " TEXT" + ");";

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
         + DbContract.Words.COLUMN_NAME_NOTES + " TEXT" + ");";
   
   /*
    * Instantiates an open helper for the provider's SQLite data repository
    * Do not do database creation and upgrade here.
    */
   public MainDatabaseHelper(Context context) 
   {
       super(context, DB_NAME, null, 1);
   }
   
   MainDatabaseHelper(Context context, String databaseName, Cursor cursor, int version) 
   {
       super(context, databaseName, null, 1);
   }

   /*
    * Creates the data repository. This is called when the provider attempts to open the
    * repository and SQLite reports that it doesn't exist.
    */
   public void onCreate(SQLiteDatabase db) 
   {
      // creating required tables
      db.execSQL(CREATE_TABLE_KIDS);
      db.execSQL(CREATE_TABLE_WORDS);
      db.execSQL(CREATE_TABLE_QUESTIONS);
   }

   @Override
   public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2)
   {
      // TODO Auto-generated method stub
      
   }
}
