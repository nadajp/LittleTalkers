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
public class UploadUserData extends AsyncTask<Context, Integer, Long>
{
   private static final String DEBUG_TAG = "UploadKidsTask";
   private GoogleAccountCredential mCredential;

   public UploadUserData(GoogleAccountCredential credential)
   {
      super();
      mCredential = credential;
   }

   protected Long doInBackground(Context... contexts)
   {
      Long userId = Prefs.getUserId(contexts[0]);
      UserProfile profile = new UserProfile();
      profile.setEmail(mCredential.getSelectedAccountName());

      // this user already exists, run the update instead
      /*if (userId != null)
      {
         profile.setId(userId);
         return (long) 0;
      }*/
      try
      {
         Littletalkersapi.Builder builder = new Littletalkersapi.Builder(
               AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
               mCredential);
         builder.setApplicationName(contexts[0].getString(R.string.app_name));
         Littletalkersapi ltEndpoint = builder.build();
         
         UserProfile result = ltEndpoint.insertProfile(profile).execute();
         userId = result.getId();
         Log.i(DEBUG_TAG, "User id: " + userId);
         Prefs.saveUserId(contexts[0], userId);
         
         UserDataWrapper data = ServerBackupUtils.getUserData(userId);       
         WordCollection words = ltEndpoint.insertUserData(userId, data).execute();
         Log.i(DEBUG_TAG, words.toString());
                    

       /*  Long id = (long) 1;
         Kid kid = ltEndpoint.getKid(userId, id).execute();
         Log.i(DEBUG_TAG, kid.toString());*/

      } catch (IOException e)
      {
         e.printStackTrace();
      }
      return (long) 0;

   }
}
