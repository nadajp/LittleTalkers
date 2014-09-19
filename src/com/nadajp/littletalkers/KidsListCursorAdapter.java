package com.nadajp.littletalkers;

import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class KidsListCursorAdapter extends CursorAdapter
{
   protected static final String DEBUG_TAG = "KidsListCursorAdapter";
   private static final int THUMBNAIL_SIZE = 100;
   private LayoutInflater mInflater;
   private Context mContext;
   
   public KidsListCursorAdapter(Context context, Cursor c, int flags)
   {
      super(context, c, flags);
      mInflater = LayoutInflater.from(context);
      mContext = context;
   }

   @Override
   public View newView(Context context, Cursor cursor, ViewGroup parent) 
   {
      return mInflater.inflate(R.layout.kid_list_row, parent, false);
   }

   @Override
   public void bindView(View view, Context context, final Cursor cursor)
   {      
      long id = cursor.getLong(cursor.getColumnIndex(DbContract.Kids._ID));

      TextView name = (TextView) view.findViewById(R.id.name);
      name.setText(cursor.getString(cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_NAME)));

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
               THUMBNAIL_SIZE, THUMBNAIL_SIZE);
      }
      CircularImageView profile = (CircularImageView) view.findViewById(R.id.profile);
      profile.setImageBitmap(profilePicture);    
      
      TextView age = (TextView) view.findViewById(R.id.age);
      age.setText(Utils.getAge(cursor.getLong(cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_BIRTHDATE_MILLIS))));
      
      TextView numOfPhrases = (TextView) view.findViewById(R.id.num_of_phrases);
      numOfPhrases.setText(Integer.valueOf(DbSingleton.get().getNumberOfWords(id)) + " ");
       
      TextView numOfQAs = (TextView) view.findViewById(R.id.num_of_qas);
      numOfQAs.setText(Integer.valueOf(DbSingleton.get().getNumberOfQAs(id)) + " ");
      
      ImageView edit = (ImageView) view.findViewById(R.id.icon_edit);
      Drawable myIcon = context.getResources().getDrawable( R.drawable.edit_query);
      edit.setImageDrawable(myIcon);
      edit.setTag(id);
      
      edit.setOnClickListener(new View.OnClickListener()
      {        
         @Override
         public void onClick(View v)
         {
            Intent intent = new Intent(mContext, AddKidActivity.class);
            long id = (Long) v.getTag();
            intent.putExtra(Prefs.CURRENT_KID_ID, id);
            mContext.startActivity(intent); 
         }
      });
      
   }   
}
