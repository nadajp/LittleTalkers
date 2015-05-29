package com.nadajp.littletalkers.backup;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.SettingsActivity;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.server.littletalkersapi.Littletalkersapi;
import com.nadajp.littletalkers.server.littletalkersapi.model.Kid;
import com.nadajp.littletalkers.server.littletalkersapi.model.Word;
import com.nadajp.littletalkers.server.littletalkersapi.model.UserDataWrapper;
import com.nadajp.littletalkers.server.littletalkersapi.model.UserProfile;
import com.nadajp.littletalkers.server.littletalkersapi.model.WordCollection;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceFragment;
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
   private Long mUserId;
   private final PreferenceFragment mParent;

   public SyncToServer(GoogleAccountCredential credential, final PreferenceFragment parent)
   {
      super();
      mCredential = credential;
      mParent = parent;
   }
   
   @Override
   protected void onPreExecute() {
       super.onPreExecute();
   }

   protected Long doInBackground(Context... contexts)
   {
      Littletalkersapi.Builder builder = new Littletalkersapi.Builder(
            AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
            mCredential);
      builder.setApplicationName(contexts[0].getString(R.string.app_name));
      Littletalkersapi ltEndpoint = builder.build();
      
      Long userId = Prefs.getUserId(contexts[0]);     
      Log.i(DEBUG_TAG, "user id from Prefs: " + userId);
      UserProfile profile = null;
      try
      {
         profile = ltEndpoint.getProfileById(userId).execute();        
      } catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
         return (long) 0;
      }   
      Log.i(DEBUG_TAG, "userId: " + profile.getId() + "  email: " + profile.getEmail());
      if (profile.getId() != userId)
      {
         userId = profile.getId();
         Prefs.saveUserId(contexts[0], userId);
      }
      
      // TODO: deletions
      
      UserDataWrapper data = ServerBackupUtils.getUserData(); 
      try {
         UserDataWrapper result = ltEndpoint.insertUserData(userId, data).execute();
      } catch (IOException e)
      {
         e.printStackTrace();
         return (long) 0;
      }
      DbSingleton.get().setNotDirty(data);
      return (long) 1;
   }
   
   protected void onProgressUpdate(Integer... progress) {
      //setProgressPercent(progress[0]);
  }

  protected void onPostExecute(Long result) {
     //DbSingleton.get().setNotDirty(data);
  }
}
