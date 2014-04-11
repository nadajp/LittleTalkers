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
   private static final int DB_VERSION = 1;
   private static final String DB_NAME = "littletalkers_db";
   private Context ctxt = null;

   public DatabaseHelper(Context ctxt)
   {
      super(ctxt, DB_NAME, null, DB_VERSION);
      this.ctxt = ctxt;
   }

   // Table Create Statements

   // Words table create statement
   private static final String CREATE_TABLE_WORDS = "CREATE TABLE "
         + DbContract.Words.TABLE_NAME + "(" + DbContract.Words._ID
         + " INTEGER PRIMARY KEY," + DbContract.Words.COLUMN_NAME_WORD
         + " TEXT," + DbContract.Words.COLUMN_NAME_AUDIO_FILE + " TEXT,"
         + DbContract.Words.COLUMN_NAME_KID + " TEXT,"
         + DbContract.Words.COLUMN_NAME_LANGUAGE + " TEXT,"
         + DbContract.Words.COLUMN_NAME_DATE + " INTEGER,"
         + DbContract.Words.COLUMN_NAME_LOCATION + " TEXT,"
         + DbContract.Words.COLUMN_NAME_TRANSLATION + " TEXT,"
         + DbContract.Words.COLUMN_NAME_TOWHOM + " TEXT,"
         + DbContract.Words.COLUMN_NAME_NOTES + " TEXT" + ");";

   // Kids table create statement
   private static final String CREATE_TABLE_KIDS = "CREATE TABLE "
         + DbContract.Kids.TABLE_NAME + "(" + DbContract.Kids._ID
         + " INTEGER PRIMARY KEY," + DbContract.Kids.COLUMN_NAME_NAME
         + " TEXT UNIQUE," + DbContract.Kids.COLUMN_NAME_BIRTHDATE + " TEXT,"
         + DbContract.Kids.COLUMN_NAME_DEFAULT_LOCATION + " TEXT,"
         + DbContract.Kids.COLUMN_NAME_DEFAULT_LANGUAGE + " TEXT,"
         + DbContract.Kids.COLUMN_NAME_PICTURE_URI + " TEXT" + ");";

   @Override
   public void onCreate(SQLiteDatabase db)
   {
      // creating required tables
      db.execSQL(CREATE_TABLE_WORDS);
      db.execSQL(CREATE_TABLE_KIDS);
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
   {
      // Logs that the database is being upgraded
      Log.w(DEBUG_TAG, "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");

      // Kills the table and existing data
      db.execSQL("DROP TABLE IF EXISTS " + DbContract.Words.TABLE_NAME);
      db.execSQL("DROP TABLE IF EXISTS " + DbContract.Kids.TABLE_NAME);

      // Recreates the database with a new version
      onCreate(db);
   }

   @Override
   public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
   {
      onUpgrade(db, oldVersion, newVersion);
   }
}
