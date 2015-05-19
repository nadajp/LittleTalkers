package com.nadajp.littletalkers.backup;

import java.io.IOException;
import java.util.ArrayList;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbContract.Kids;
//import com.nadajp.littletalkers.kidendpoint.Kidendpoint;
//import com.nadajp.littletalkers.kidendpoint.model.Kid;


import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Handle the transfer of data between a server and an app, using the Android
 * sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter
{

   // Global variables
   
   // Define a variable to contain a content resolver instance
   ContentResolver mContentResolver;

   private static final String FEED_URL = "http://android-developers.blogspot.com/atom.xml";
   private static final String SERVER_URL = "http://android-developers.blogspot.com/atom.xml";

   /**
    * Network connection timeout, in milliseconds.
    */
   private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000; // 15 seconds

   /**
    * Network read timeout, in milliseconds.
    */
   private static final int NET_READ_TIMEOUT_MILLIS = 10000; // 10 seconds

   private static final String[] KIDS_PROJECTION = new String[] { 
         DbContract.Kids._ID,
         DbContract.Kids.COLUMN_NAME_NAME,
         DbContract.Kids.COLUMN_NAME_BIRTHDATE_MILLIS,
         DbContract.Kids.COLUMN_NAME_DEFAULT_LOCATION,
         DbContract.Kids.COLUMN_NAME_DEFAULT_LANGUAGE,
         DbContract.Kids.COLUMN_NAME_PICTURE_URI};

   // Constants representing column positions from KID.
   public static final int KIDS_COLUMN_ID = 0;
   public static final int KIDS_COLUMN_NAME = 1;
   public static final int KIDS_COLUMN_BIRTHDATE = 2;
   public static final int KIDS_COLUMN_LOCATION = 3;
   public static final int KIDS_COLUMN_LANGUAGE = 4;
   public static final int KIDS_COLUMN_PICTURE_URI = 5;

   private static final String DEBUG_TAG = "SyncAdapter";

   /**
    * Set up the sync adapter
    */
   public SyncAdapter(Context context, boolean autoInitialize)
   {
      super(context, autoInitialize);
      Log.i(DEBUG_TAG, "Entering SyncAdapter...");
      /*
       * If your app uses a content resolver, get an instance of it from the
       * incoming Context
       */
      mContentResolver = context.getContentResolver();
   }

   /**
    * Set up the sync adapter. This form of the constructor maintains
    * compatibility with Android 3.0 and later platform versions
    */
   public SyncAdapter(Context context, boolean autoInitialize,
         boolean allowParallelSyncs)
   {
      super(context, autoInitialize, allowParallelSyncs);
      /*
       * If your app uses a content resolver, get an instance of it from the
       * incoming Context
       */
      Log.i(DEBUG_TAG, "Entering SyncAdapter...");
      mContentResolver = context.getContentResolver();
      
   }

   /*
    * Specify the code you want to run in the sync adapter. The entire sync
    * adapter runs in a background thread, so you don't have to set up your own
    * background processing.
    */
   @Override
   public void onPerformSync(Account account, Bundle extras, String authority,
         ContentProviderClient provider, SyncResult syncResult)
   {
      /*
       * Put the data transfer code here.
       */
      
      Log.i(DEBUG_TAG, "Performing Sync!");

      
      /*Kidendpoint.Builder builder = new Kidendpoint.Builder(
             AndroidHttp.newCompatibleTransport(), 
             new JacksonFactory(),
             new HttpRequestInitializer() {
                public void initialize(HttpRequest httpRequest) { }
                });
             
         builder.setApplicationName("littletalkerstest");
         Kidendpoint endpoint = CloudEndpointUtils.updateBuilder(
            builder).build();
         
         Uri uri = DbContract.Kids.CONTENT_URI; // Get all entries
         Cursor cursor = mContentResolver.query(uri, KIDS_PROJECTION, null, null, null);
         assert cursor != null;
         
         ArrayList<Kid> kids = new ArrayList<Kid>();
         if (cursor.moveToFirst())
         {
            do
            {
               Kid kid = new Kid();
               kid.setId(cursor.getInt(cursor.getColumnIndex(Kids._ID)));
               kid.setName(cursor.getString(cursor.getColumnIndex(Kids.COLUMN_NAME_NAME)));
               kid.setBirthdate(cursor.getInt(cursor.getColumnIndex(Kids.COLUMN_NAME_BIRTHDATE_MILLIS)));
               kid.setLocation(cursor.getString(cursor.getColumnIndex(Kids.COLUMN_NAME_DEFAULT_LOCATION)));
               kid.setLanguage(cursor.getString(cursor.getColumnIndex(Kids.COLUMN_NAME_DEFAULT_LANGUAGE)));
               kid.setPictureUri(cursor.getString(cursor.getColumnIndex(Kids.COLUMN_NAME_PICTURE_URI)));
               kids.add(kid);
            } while (cursor.moveToNext());
         }
         cursor.close();
         
         try {
            endpoint.insertKid(kids.get(0)).execute();
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          } */
     }
}