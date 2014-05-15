package com.nadajp.littletalkers.model;

import android.provider.BaseColumns;

public class Kid implements BaseColumns
{
   // This class cannot be instantiated
   private Kid()
   {
   }

   public static final String TABLE_NAME = "kids";
   public static final String _ID = "_id";
   public static final String COLUMN_NAME_NAME = "name";
   public static final String COLUMN_NAME_BIRTHDATE = "birthdate";
   public static final String COLUMN_NAME_DEFAULT_LOCATION = "default_location";
   public static final String COLUMN_NAME_DEFAULT_LANGUAGE = "default_language";
   public static final String COLUMN_NAME_PICTURE_URI = "picture_uri";
}
