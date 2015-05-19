package com.nadajp.littletalkers.contentprovider;

import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.MainDatabaseHelper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class LTContentProvider extends ContentProvider
{
   /*
    * Defines a handle to the database helper object. The MainDatabaseHelper
    * class is defined in a following snippet.
    */
   private MainDatabaseHelper mDbHelper;

   /**
    * Content authority for this provider.
    */
   public static final String CONTENT_AUTHORITY = DbContract.AUTHORITY;

   /**
    * Base URI. (content://com.example.android.network.sync.basicsyncadapter)
    */
   public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
   
   public static final Uri KIDS_URI = Uri.parse("content://" + CONTENT_AUTHORITY + "Kids");
   public static final Uri WORDS_URI = Uri.parse("content://" + CONTENT_AUTHORITY + "Words");

   /**
    * Path components for various type resources..
    */
   private static final String PATH_KIDS = DbContract.Kids.TABLE_NAME;
   private static final String PATH_WORDS = DbContract.Words.TABLE_NAME;
   private static final String PATH_QAS = DbContract.Questions.TABLE_NAME;

   // The constants below represent individual URI routes, as IDs. Every URI pattern recognized by
   // this ContentProvider is defined using sUriMatcher.addURI(), and associated with one of these
   // IDs.
   //
   // When a incoming URI is run through sUriMatcher, it will be tested against the defined
   // URI patterns, and the corresponding route ID will be returned.
   /**
    * URI ID for route: /kids
    */
   public static final int ROUTE_KIDS = 1;

   /**
    * URI ID for route: /kids/{ID}
    */
   public static final int ROUTE_KIDS_ID = 2;
   /**
    * URI ID for route: /kids
    */
   public static final int ROUTE_WORDS = 3;

   /**
    * URI ID for route: /kids/{ID}
    */
   public static final int ROUTE_WORDS_ID = 4;
   /**
    * URI ID for route: /kids
    */
   public static final int ROUTE_QAS = 5;

   /**
    * URI ID for route: /kids/{ID}
    */
   public static final int ROUTE_QAS_ID = 6;

   /**
    * UriMatcher, used to decode incoming URIs.
    */
   private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
   static {
          sUriMatcher.addURI(CONTENT_AUTHORITY, "kids", ROUTE_KIDS);
          sUriMatcher.addURI(CONTENT_AUTHORITY, "kids/#", ROUTE_KIDS_ID);
          sUriMatcher.addURI(CONTENT_AUTHORITY, "words", ROUTE_WORDS);
          sUriMatcher.addURI(CONTENT_AUTHORITY, "words/#", ROUTE_WORDS_ID);
          sUriMatcher.addURI(CONTENT_AUTHORITY, "questions", ROUTE_QAS);
          sUriMatcher.addURI(CONTENT_AUTHORITY, "questions/#", ROUTE_QAS_ID);
   }
   
   public boolean onCreate()
   {

      /*
       * Creates a new helper object. This method always returns quickly. Notice
       * that the database itself isn't created or opened until
       * SQLiteOpenHelper.getWritableDatabase is called
       */
      mDbHelper = new MainDatabaseHelper(getContext());
      return true;
   }

   @Override
   public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                       String sortOrder) 
   {
      // Using SQLiteQueryBuilder instead of query() method
      SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

      switch (sUriMatcher.match(uri)) 
      {
         case ROUTE_KIDS:
            queryBuilder.setTables(DbContract.Kids.TABLE_NAME);
            break;
         case ROUTE_KIDS_ID:
            // adding the ID to the original query
            queryBuilder.setTables(DbContract.Kids.TABLE_NAME);
            queryBuilder.appendWhere(DbContract.Kids._ID + "="
            + uri.getLastPathSegment());
            break;
         case ROUTE_WORDS:
            queryBuilder.setTables(DbContract.Words.TABLE_NAME);
            break;
         case ROUTE_WORDS_ID:
            queryBuilder.setTables(DbContract.Words.TABLE_NAME);
            queryBuilder.appendWhere(DbContract.Words._ID + "=" 
                + uri.getLastPathSegment());
            break;
      default:
        throw new IllegalArgumentException("Unknown URI: " + uri);
      }

      SQLiteDatabase db = mDbHelper.getReadableDatabase();
      Cursor cursor = queryBuilder.query(db, projection, selection,
          selectionArgs, null, null, sortOrder);
      // make sure that potential listeners are getting notified
      cursor.setNotificationUri(getContext().getContentResolver(), uri);

      return cursor;
   }

   @Override
   public int delete(Uri arg0, String arg1, String[] arg2)
   {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public String getType(Uri arg0)
   {
      // TODO Auto-generated method stub
      return null;
   }


   @Override
   public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3)
   {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public Uri insert(Uri uri, ContentValues values)
   {
      // TODO Auto-generated method stub
      return null;
   }
   
}
