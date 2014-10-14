package com.nadajp.littletalkers;

import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

import android.app.Fragment;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class KidProfileFragment extends Fragment
{
   private static final String DEBUG_TAG = "KidProfileFragment";
   private int mKidId;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState)
   {
      View view = inflater.inflate(R.layout.fragment_kid_profile,
            container, false);
      
      mKidId = this.getActivity().getIntent().getIntExtra(Prefs.CURRENT_KID_ID, -1); 
      
      Cursor cursor = DbSingleton.get().getKidDetails(mKidId);
      cursor.moveToFirst();
      
      String pictureUri = cursor.getString(cursor
            .getColumnIndex(DbContract.Kids.COLUMN_NAME_PICTURE_URI));
      
      Bitmap profilePicture = null;
            
      if (pictureUri == null)
      {
         profilePicture = BitmapFactory.decodeResource(view.getResources(),
               R.drawable.profile);
      } else
      {
         profilePicture = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(pictureUri),
               180, 180);
      }
      CircularImageView profile = (CircularImageView) view.findViewById(R.id.profile_picture);
      profile.setImageBitmap(profilePicture);     
      
      ((TextView) view.findViewById(R.id.name)).setText(cursor.getString(cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_NAME)));
      TextView birthdate = (TextView) view.findViewById(R.id.tvBirthdate);
      
      birthdate.setText(Utils.getAge(cursor.getLong(cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_BIRTHDATE_MILLIS))));
      //birthdate.setText(cursor.getString(cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_BIRTHDATE)));
      
      TextView words = (TextView) view.findViewById(R.id.tvNumOfWords);
      TextView questions = (TextView) view.findViewById(R.id.tvNumOfQuestions);

      String strWords = Integer.valueOf(DbSingleton.get().getNumberOfWords(mKidId)).toString() 
                        + " "
                        + this.getString(R.string.words_and_phrases)
                        + "!";
      String strQA = Integer.valueOf(DbSingleton.get().getNumberOfQAs(mKidId)).toString()
                     + " "
                     + this.getString(R.string.questions_and_answers)
                     + "!";
      words.setText(strWords);
      questions.setText(strQA);
      
      return view;
   }     

}
