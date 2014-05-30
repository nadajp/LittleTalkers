package com.nadajp.littletalkers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbContract.Kids;
import com.nadajp.littletalkers.database.DbContract.Questions;
import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.database.DbContract.Words;
import com.nadajp.littletalkers.utils.Utils;

public class DataExportActivity extends Activity
{
   private static final String DEBUG_TAG = "DataExportActivity";
   private ListView mListView;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_data_export);
      getActionBar().setDisplayHomeAsUpEnabled(true);
      
      Cursor cursor = DbSingleton.get().getKidsForSpinner();
      if (cursor.getCount() == 0) return;
      
      String[] adapterCols = new String[] { "name" };
      int[] adapterRowViews = new int[] { android.R.id.text1 };

      SimpleCursorAdapter sca = new SimpleCursorAdapter(this,
            android.R.layout.simple_list_item_checked, cursor, adapterCols,
            adapterRowViews, 0);
     
      mListView = (ListView) findViewById(R.id.listChooseKids);
      mListView.setAdapter(sca);
      mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
      for (int i = 0; i < mListView.getCount(); i++)
      {
         mListView.setItemChecked(i, true);
      }
 }


   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.data_export, menu);
      return true;
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      // Handle presses on the action bar items
      switch (item.getItemId())
      {
         case R.id.action_manage_kids:
            Intent manage_intent = new Intent(this, ManageKidsActivity.class);
            startActivity(manage_intent);
            return true;
         default:
            return super.onOptionsItemSelected(item);
      }
   }
   public void exportToCSV(View v)
   {
      long[] checked = mListView.getCheckedItemIds();
      if (checked.length == 0) {
         Toast.makeText(this, getString(R.string.please_select_kids_to_export), Toast.LENGTH_LONG).show();
         return;
      }
           
      String phraseString = "";
      String qaString = "";

      for (long id : checked)
      {
         Log.i(DEBUG_TAG, "Checked: " + DbSingleton.get().getKidName(id));
         Cursor cursor = DbSingleton.get().getWordsForExport(id);
         if (cursor.moveToFirst())
         {
            do
            {
               long rawdate = cursor.getLong(cursor.getColumnIndex(Words.COLUMN_NAME_DATE));
               phraseString =  phraseString + DbSingleton.get().getKidName(id) + ";"
                     + Utils.getDateForDisplay(rawdate, this).replace(", ", "/") + ";"
                     + cursor.getString(cursor.getColumnIndex(Words.COLUMN_NAME_WORD)) + ";"
                     + cursor.getString(cursor.getColumnIndex(Words.COLUMN_NAME_TRANSLATION)) + ";"
                     + cursor.getString(cursor.getColumnIndex(Words.COLUMN_NAME_LANGUAGE)) + "\n";
            } while (cursor.moveToNext());
            phraseString += "\n";
         }

         cursor.close();
         cursor = DbSingleton.get().getQAForExport(id);
         if (cursor.moveToFirst())
         {
            do
            {
               long rawdate = cursor.getLong(cursor.getColumnIndex(Questions.COLUMN_NAME_DATE));
               qaString =  qaString + DbSingleton.get().getKidName(id) + ";"
                     + Utils.getDateForDisplay(rawdate, this).replace(", ", "/") + ";"
                     + cursor.getString(cursor.getColumnIndex(Questions.COLUMN_NAME_QUESTION));
               if (cursor.getInt(cursor.getColumnIndex(Questions.COLUMN_NAME_ASKED)) == 1)
               {
                  qaString += "*";
               }
               qaString = qaString + ";" 
               + cursor.getString(cursor.getColumnIndex(Questions.COLUMN_NAME_ANSWER));
               if (cursor.getInt(cursor.getColumnIndex(Questions.COLUMN_NAME_ANSWERED)) == 1)
               {
                  qaString += "*";
               }
               qaString = qaString + ";" 
                     + cursor.getString(cursor.getColumnIndex(Questions.COLUMN_NAME_TOWHOM)) + ";"
                     + cursor.getString(cursor.getColumnIndex(Questions.COLUMN_NAME_LANGUAGE)) + "\n";
               
            } while (cursor.moveToNext());
         }
         
         cursor.close(); 
         qaString += "\n";
      }
      
      // email csv file
      File file = createCSVfile();
      
      String header1 = Kids.COLUMN_NAME_NAME + ";"
                      + Words.COLUMN_NAME_DATE + ";" 
                      + Words.COLUMN_NAME_WORD + ";"
                      + Words.COLUMN_NAME_TRANSLATION + ";"
                      + Words.COLUMN_NAME_LANGUAGE;
      
      String header2 = Kids.COLUMN_NAME_NAME + ";"
            + Questions.COLUMN_NAME_DATE + ";" 
            + Questions.COLUMN_NAME_QUESTION + ";"
            + Questions.COLUMN_NAME_ANSWER + ";"
            + Questions.COLUMN_NAME_TOWHOM + ";"
            + Questions.COLUMN_NAME_LANGUAGE;

      String combinedString = header1 + "\n" + phraseString + header2 + "\n" + qaString;
      
      FileOutputStream out   =   null;
      try {
          out = new FileOutputStream(file);
          } catch (FileNotFoundException e) {
              e.printStackTrace();
          }
          try {
              out.write(combinedString.getBytes());
          } catch (IOException e) {
              e.printStackTrace();
          }
          try {
              out.close();
          } catch (IOException e) {
              e.printStackTrace();
          }
      Uri uri = Uri.fromFile(file);

      Intent sendIntent = new Intent(Intent.ACTION_SEND);
      sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Little Talkers Data");
      sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
      sendIntent.setType("text/html");
      startActivity(sendIntent);
   }
   
   private File createCSVfile()
   {
      Calendar date = Calendar.getInstance(); 
      
      String baseFilename = "LT_backup_" + date.getTime().toString() + ".csv"; 

      String subdirectory = getString(R.string.app_name);

      File newfile = new File(Utils.getPublicDirectory(subdirectory, this), baseFilename);

      if (newfile.exists()) { newfile.delete(); }
      
      return newfile;
   }

}
