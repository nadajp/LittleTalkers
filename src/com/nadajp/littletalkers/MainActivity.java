package com.nadajp.littletalkers;

import java.io.IOException;
import java.util.ArrayList;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.nadajp.littletalkers.MainFragment.AddKidListener;
import com.nadajp.littletalkers.backup.ServerBackupUtils;
import com.nadajp.littletalkers.backup.UploadUserData;
import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.utils.Prefs;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.util.Strings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity implements AddKidListener
{
   // Constants

   private static String DEBUG_TAG = "Main Activity";
   // The authority for the sync adapter's content provider
   public static final String AUTHORITY = DbContract.AUTHORITY;
   // An account type, in the form of a domain name
   public static final String ACCOUNT_TYPE = "littletalkers.com";
   // The account name
   public static final String ACCOUNT = "dummyaccount";
   // Instance fields
   Account mAccount;
   //private AuthorizationCheckTask mAuthTask;


   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      // Find out from shared preferences whether there are any kids yet
      int kidId = Prefs.getKidId(this, -1);
      // Log.i(DEBUG_TAG, "Kid Id in Main: " + kidId);

      // Create the dummy account
      mAccount = CreateSyncAccount(this);

      /*
       * Request the sync for the default account, authority, and manual sync
       * settings
       */
      /*
       * Bundle bundle = new Bundle();
       * bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
       * bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
       * ContentResolver.setIsSyncable(mAccount, DbContract.AUTHORITY, 1);
       * ContentResolver.requestSync(mAccount, DbContract.AUTHORITY, bundle);
       */

      /*
       * If no kids have been added yet, go to AddKidActivity if (kidId == -1) {
       * 
       * //Intent intent = new Intent(this, AddKidActivity.class);
       * //intent.putExtra(Prefs.CURRENT_KID_ID, kidId);
       * //startActivity(intent); }
       */

      if (kidId > 0)
      // Go to AddWordActivity
      {
         Intent intent = new Intent(this, AddItemActivity.class);
         intent.putExtra(Prefs.CURRENT_KID_ID, kidId);
         intent.putExtra(Prefs.ADD_TYPE, Prefs.TYPE_WORD);
         startActivity(intent);
      }
   }

   /**
    * Create a new dummy account for the sync adapter
    * 
    * @param context
    *           The application context
    */
   public Account CreateSyncAccount(Context context) { 
      // Create }the account type and default account 
      Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE); 
      // Get an instance of the Android account manager 
      AccountManager accountManager =
      (AccountManager) context .getSystemService(ACCOUNT_SERVICE); 
      /* Add the account and account type, no password or user data If
       * successful, return the Account object, otherwise report an error.
       */
      if (accountManager.addAccountExplicitly(newAccount, null, null)) { 
         /* If you don't set android:syncable="true" in in your
          * <provider> element in the manifest, then call
          * context.setIsSyncable(account, AUTHORITY, 1) here.*/
      } else { 
      /* The account exists or some other error occurred. Log
       * this, report it, or handle it internally.*/
       Log.i(DEBUG_TAG, "already have account"); 
    }
    return newAccount;
}
                    
   public void clickedAddKid()
   {
      Intent intent = new Intent(this, AddKidActivity.class);
      intent.putExtra(Prefs.CURRENT_KID_ID, -1);
      startActivity(intent);
   }

   /*
   public void performAuthCheck(String emailAccount)
   {
      // Cancel previously running tasks.
      if (mAuthTask != null)
      {
         try
         {
            mAuthTask.cancel(true);
         } catch (Exception e)
         {
            e.printStackTrace();
         }
      }

      new AuthorizationCheckTask().execute(emailAccount);
   }

   class AuthorizationCheckTask extends AsyncTask<String, Integer, Boolean>
   {
      @Override
      protected Boolean doInBackground(String... emailAccounts)
      {
         Log.i(DEBUG_TAG, "Background task started.");

         if (!ServerBackupUtils
               .checkGooglePlayServicesAvailable(MainActivity.this))
         {
            return false;
         }

         String emailAccount = emailAccounts[0];
         // Ensure only one task is running at a time.
         mAuthTask = this;

         // Ensure an email was selected.
         if (Strings.isNullOrEmpty(emailAccount))
         {
            publishProgress(R.string.toast_no_google_account_selected);
            // Failure.
            return false;
         }

         Log.d(DEBUG_TAG, "Attempting to get AuthToken for account: "
               + mEmailAccount);

         try
         {
            // If the application has the appropriate access then a token will
            // be retrieved, otherwise
            // an error will be thrown.
            GoogleAccountCredential credential = GoogleAccountCredential
                  .usingAudience(MainActivity.this, AppConstants.AUDIENCE);
            credential.setSelectedAccountName(emailAccount);

            String accessToken = credential.getToken();

            Log.d(DEBUG_TAG, "AccessToken retrieved");

            // Success.
            return true;
         } catch (GoogleAuthException unrecoverableException)
         {
            Log.e(DEBUG_TAG, "Exception checking OAuth2 authentication.",
                  unrecoverableException);
            publishProgress(R.string.toast_exception_checking_authorization);
            // Failure.
            return false;
         } catch (IOException ioException)
         {
            Log.e(DEBUG_TAG, "Exception checking OAuth2 authentication.",
                  ioException);
            publishProgress(R.string.toast_exception_checking_authorization);
            // Failure or cancel request.
            return false;
         }
      }

      @Override
      protected void onProgressUpdate(Integer... stringIds)
      {
         // Toast only the most recent.
         Integer stringId = stringIds[0];
         Toast.makeText(MainActivity.this, stringId, Toast.LENGTH_SHORT).show();
      }

      @Override
      protected void onPreExecute()
      {
         mAuthTask = this;
      }
*/
      /*
       * @Override protected void onPostExecute(Boolean success) { TextView
       * emailAddressTV = (TextView)
       * MainActivity.this.findViewById(R.id.email_address_tv); if (success) {
       * // Authorization check successful, set internal variable. mEmailAccount
       * = emailAddressTV.getText().toString(); } else { // Authorization check
       * unsuccessful, reset TextView to empty. emailAddressTV.setText(""); }
       * mAuthTask = null; }
       */
/*
      @Override
      protected void onCancelled()
      {
         mAuthTask = null;
      }
   }*/
}
