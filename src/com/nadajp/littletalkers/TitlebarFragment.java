package com.nadajp.littletalkers;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbSingleton;


public class TitlebarFragment extends Fragment 
{	
    private long mCurrentKidId;
    private TextView mTvBirthdate, mTvWords;
    private ImageView mImageView;
    private static final String DEBUG_TAG = "TitlebarFragment";

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) 
	{
		
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_titlebar, container, false);

        mTvBirthdate = (TextView)v.findViewById(R.id.tvBirthdate);
        mTvWords = (TextView)v.findViewById(R.id.tvNumOfWords);
        mImageView = (ImageView)v.findViewById(R.id.ivProfilePic);
        
        SharedPreferences sharedPrefs = this.getActivity().getSharedPreferences(getString(R.string.shared_preferences_filename), Context.MODE_PRIVATE);
        long latestKidId = sharedPrefs.getLong("current_kid_id", -1);
        mCurrentKidId = this.getActivity().getIntent().getLongExtra("current_kid_id", latestKidId);
		
		return v;
    }
	
	public void updateData(long kidId)
	{
		mCurrentKidId = kidId;
		Log.i(DEBUG_TAG, "kidId = " + mCurrentKidId);
		Cursor cursor = DbSingleton.get().getKidDetails(kidId);
		cursor.moveToFirst();
		mTvBirthdate.setText(cursor.getString(cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_BIRTHDATE)).toString());
		mTvWords.setText(Integer.toString(DbSingleton.get().getNumberOfWords(kidId)));
		String pictureUri = cursor.getString(cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_PICTURE_URI));
      cursor.close();
      Bitmap profilePicture = null;
      if(pictureUri == null)
         profilePicture = BitmapFactory.decodeResource(getResources(), R.drawable.profilepicture);
      else 
         profilePicture = BitmapFactory.decodeFile(pictureUri);
      mImageView.setImageBitmap(profilePicture);	
	}
	
}