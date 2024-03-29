/**
 * 
 */
package com.nadajp.littletalkers.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author nadajp Defines the littletalkers_db schema
 */
public final class DbContract
{
   public static final String AUTHORITY = "com.nadajp.littletalkers.provider";
 
   // This class cannot be instantiated
   private DbContract()
   {
   }
 
   /**
    * Kids table contract
    */
   public static abstract class Kids implements BaseColumns
   {
      // This class cannot be instantiated
      private Kids()
      {
      }
            
      public static final String TABLE_NAME = "kids";
      public static final String _ID = "_id";
      public static final String COLUMN_NAME_NAME = "name";
      public static final String COLUMN_NAME_BIRTHDATE_MILLIS = "birthdate_millis";
      public static final String COLUMN_NAME_DEFAULT_LOCATION = "default_location";
      public static final String COLUMN_NAME_DEFAULT_LANGUAGE = "default_language";
      public static final String COLUMN_NAME_PICTURE_URI = "picture_uri";
      
      public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
   }
   
   /**
    * Words table contract
    */
   public static abstract class Words implements BaseColumns
   {
      // This class cannot be instantiated
      private Words()
      {
      }

      public static final String TABLE_NAME = "words";
      public static final String _ID = "_id";
      public static final String COLUMN_NAME_WORD = "word";
      public static final String COLUMN_NAME_KID = "kid_id";
      public static final String COLUMN_NAME_LANGUAGE = "language";
      public static final String COLUMN_NAME_DATE = "date";
      public static final String COLUMN_NAME_LOCATION = "location";
      public static final String COLUMN_NAME_AUDIO_FILE = "audio_file";
      public static final String COLUMN_NAME_TRANSLATION = "translation";
      public static final String COLUMN_NAME_TOWHOM = "towhom";
      public static final String COLUMN_NAME_NOTES = "notes";
      
      public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
   }
   
   /**
    * Questions table contract, for storing questions & answers
    */
   public static abstract class Questions implements BaseColumns
   {
      // This class cannot be instantiated
      private Questions()
      {
      }

      public static final String TABLE_NAME = "questions";
      public static final String _ID = "_id";
      public static final String COLUMN_NAME_KID = "kid_id";
      public static final String COLUMN_NAME_QUESTION = "question";
      public static final String COLUMN_NAME_ANSWER = "answer";
      public static final String COLUMN_NAME_TOWHOM = "towhom";
      public static final String COLUMN_NAME_ASKED = "asked";
      public static final String COLUMN_NAME_ANSWERED = "answered";
      public static final String COLUMN_NAME_LANGUAGE = "language";
      public static final String COLUMN_NAME_DATE = "date";
      public static final String COLUMN_NAME_LOCATION = "location";
      public static final String COLUMN_NAME_AUDIO_FILE = "audio_file";
      public static final String COLUMN_NAME_NOTES = "notes";
      
      public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
   }
}
