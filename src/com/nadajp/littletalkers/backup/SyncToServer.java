package com.nadajp.littletalkers.backup;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.server.littletalkersapi.Littletalkersapi;
import com.nadajp.littletalkers.server.littletalkersapi.model.Kid;
import com.nadajp.littletalkers.server.littletalkersapi.model.Word;
import com.nadajp.littletalkers.server.littletalkersapi.model.UserDataWrapper;
import com.nadajp.littletalkers.server.littletalkersapi.model.UserProfile;
import com.nadajp.littletalkers.server.littletalkersapi.model.WordCollection;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;

/*
 * Upload all data for this user to the cloud. This will only be called
 * once, when the user signs up for the service and launches it
 */
public class SyncToServer extends AsyncTask<Context, Integer, Long>
{
   private static final String DEBUG_TAG = "SyncToServerTask";
   private GoogleAccountCredential mCredential;

   public SyncToServer(GoogleAccountCredential credential)
   {
      super();
      mCredential = credential;
   }

   protected Long doInBackground(Context... contexts)
   {
      Littletalkersapi.Builder builder = new Littletalkersapi.Builder(
            AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
            mCredential);
      builder.setApplicationName(contexts[0].getString(R.string.app_name));
      Littletalkersapi ltEndpoint = builder.build();
      
      Long userId = Prefs.getUserId(contexts[0]);
      return (long) 0;

   }
}
