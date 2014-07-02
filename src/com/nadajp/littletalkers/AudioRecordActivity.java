package com.nadajp.littletalkers;

import com.nadajp.littletalkers.utils.Prefs;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.os.Build;

public class AudioRecordActivity extends Activity
{
   private int mType;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_audio_record);

      mType = this.getIntent().getExtras().getInt(Prefs.TYPE);
      AudioRecordFragment fragment = new AudioRecordFragment();
      Bundle args = new Bundle();
      args.putInt(Prefs.TYPE, mType);
      fragment.setArguments(args);
      if (savedInstanceState == null)
      {
         getFragmentManager().beginTransaction()
               .add(R.id.container, fragment).commit();
      }
      
      final ActionBar actionBar = getActionBar();
      actionBar.hide();      
   }

   /**
    * A placeholder fragment containing a simple view.
    */
   public static class PlaceholderFragment extends Fragment
   {
      private int mType;
      
      public PlaceholderFragment() 
      {
      }

      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
      {
         View rootView = inflater.inflate(R.layout.fragment_audio_record,
               container, false);
         Bundle args = this.getArguments();
         mType = args.getInt(Prefs.TYPE);
         if (mType == Prefs.TYPE_QA){
            rootView.setBackgroundColor(this.getResources().getColor(R.color.green));
            ImageView mic = (ImageView) rootView.findViewById(R.id.button_mic);
            mic.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_circle_white_green_mic));
         }
         return rootView;
      }
   }

}
