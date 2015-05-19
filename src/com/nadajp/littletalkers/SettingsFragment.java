package com.nadajp.littletalkers;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import com.nadajp.littletalkers.backup.SyncToServer;
import com.nadajp.littletalkers.utils.Prefs;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link SettingsFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link SettingsFragment#newInstance} factory method to create an instance of
 * this fragment.
 *
 */
public class SettingsFragment extends PreferenceFragment
{
   // TODO: Rename parameter arguments, choose names that match
   // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
   private static final String ARG_PARAM1 = "param1";
   private static final String ARG_PARAM2 = "param2";  

   public SharedPreferences mSharedPrefs;
   public GoogleAccountCredential mCredential;
   public String mAccountName;
   
   // TODO: Rename and change types of parameters
   private String mParam1;
   private String mParam2;

   private OnFragmentInteractionListener mListener;

   /**
    * Use this factory method to create a new instance of this fragment using
    * the provided parameters.
    *
    * @param param1
    *           Parameter 1.
    * @param param2
    *           Parameter 2.
    * @return A new instance of fragment SettingsFragment.
    */
   // TODO: Rename and change types and number of parameters
   public static SettingsFragment newInstance(String param1, String param2)
   {
      SettingsFragment fragment = new SettingsFragment();
      Bundle args = new Bundle();
      args.putString(ARG_PARAM1, param1);
      args.putString(ARG_PARAM2, param2);
      fragment.setArguments(args);
      return fragment;
   }

   public SettingsFragment()
   {
      // Required empty public constructor
   }

   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      
      // Load the preferences from an XML resource
      addPreferencesFromResource(R.xml.preferences);
      Preference account = (Preference) findPreference("account");
      
      Boolean loggedIn = Prefs.getAccountName(getActivity()) == null ? false : true;

      if (loggedIn)
      {
         Preference sync = (Preference) findPreference("sync");
         sync.setDefaultValue(loggedIn);
         sync.setDependency("account");
         sync.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                      @Override
                      public boolean onPreferenceClick(Preference arg0) { 
                          new SyncToServer(mCredential).execute();
                          return true;
                      }
                  });
      }
      else 
      {
         Intent intent = new Intent(this.getActivity(), LoginActivity.class);
         account.setIntent(intent);
         
         /*account.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
         {            
            @Override
            public boolean onPreferenceClick(Preference arg0)
            {
               mListener.logIn();
               return false;
            }
         });*/
      }
     /* if (getArguments() != null)
      {
         mParam1 = getArguments().getString(ARG_PARAM1);
         mParam2 = getArguments().getString(ARG_PARAM2);
      }*/
   }

   // TODO: Rename method, update argument and hook method into UI event
   public void onButtonPressed(Uri uri)
   {
      if (mListener != null)
      {
         mListener.onFragmentInteraction(uri);
      }
   }
/*
   @Override
   public void onAttach(Activity activity)
   {
      super.onAttach(activity);
      try
      {
         mListener = (OnFragmentInteractionListener) activity;
      } catch (ClassCastException e)
      {
         throw new ClassCastException(activity.toString()
               + " must implement OnFragmentInteractionListener");
      }
   }

   @Override
   public void onDetach()
   {
      super.onDetach();
      mListener = null;
   }
*/
   /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated to
    * the activity and potentially other fragments contained in that activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
   public interface OnFragmentInteractionListener
   {
      // TODO: Update argument type and name
      public void onFragmentInteraction(Uri uri);      
      public void doSync();
   }
}
