package com.nadajp.littletalkers.model;

public class Kid 
{
   //private variables
   int id;
   String name;
   String default_location;
   String default_language;
   String birthdate;
   String picture_uri;

   // Empty constructor
   public Kid()
   {
   }
   // constructor
   public Kid(String name, String default_location, String default_language, String birthdate, String picture_uri)
   {
      this.name = name;
      this.default_location = default_location;
      this.default_language = default_language;
      this.birthdate = birthdate;
      this.picture_uri = picture_uri;
   }
     
   // getting ID
   public int getID()
   {
      return this.id;
   }
     
   // set id
   public void setID(int id)
   {
      this.id = id;
   }
     
   // get name
   public String getName()
   {
      return this.name;
   }
     
   // set name
   public void setName(String name)
   {
      this.name = name;
   }
    
   // get default location
   public String getLocation()
   {
      return this.default_location;
   }
    
   // set default location
   public void get(String location)
   {
      this.default_location = location;
   }
    
   // get default language
   public String getLanguage()
   {
      return this.default_language;
   }
   
   // setting default language
   public void setLanguage(String language)
   {
      this.default_language = language;
   }
    
   // get birthdate
   public String getBirthdate()
   {
      return this.birthdate;
   }
     
   // set birthdate
   public void setBirthdate(String birthdate)
   {
      this.birthdate = birthdate;
   }
    
   // get pictureUri
   public String getPictureUri()
   {
      return this.picture_uri;
   }
     
   // set pictureUri
   public void setPictureUri(String pictureUri)
   {
      this.picture_uri = pictureUri;
   }  
}
