package com.nadajp.littletalkers;

import java.io.File;

import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class TheBackupAgent extends BackupAgentHelper {

   // An arbitrary string used within the BackupAgentHelper implementation to
   // identify the SharedPreferencesBackupHelper's data.
   static final String MY_PREFS_BACKUP_KEY = "myprefs";
   // A key to uniquely identify the set of backup data
   static final String FILES_BACKUP_KEY = "myfiles";

   // Simply allocate a helper and install it
   public void onCreate() 
   {
       SharedPreferencesBackupHelper helper =
               new SharedPreferencesBackupHelper(this, Prefs.SHARED_PREFS_FILENAME);
       addHelper(MY_PREFS_BACKUP_KEY, helper);
   }
}
