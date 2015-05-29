package com.nadajp.littletalkers;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.plus.PlusOneButton;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.nadajp.littletalkers.utils.Prefs;

/**
 * A fragment with a Google +1 button. Activities that contain this fragment
 * must implement the {@link SettingsFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link SettingsFragment#newInstance} factory method to create an instance of
 * this fragment.
 *
 */
public class SettingsFragment extends Fragment
{
   static final int REQUEST_ACCOUNT_PICKER = 2;
   private static final int ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION = 2222;
   private GoogleAccountCredential mCredential;
   public SharedPreferences mSharedPrefs;
   public String mAccountName;
   // TODO: Rename parameter arguments, choose names that match
   // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
   private static final String ARG_PARAM1 = "param1";
   private static final String ARG_PARAM2 = "param2";

   // TODO: Rename and change types of parameters
   private String mParam1;
   private String mParam2;

   // The URL to +1. Must be a valid URL.
   private final String PLUS_ONE_URL = "http://developer.android.com";

   // The request code must be 0 or greater.
   private static final int PLUS_ONE_REQUEST_CODE = 0;

   private PlusOneButton mPlusOneButton;

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
      if (getArguments() != null)
      {
         mParam1 = getArguments().getString(ARG_PARAM1);
         mParam2 = getArguments().getString(ARG_PARAM2);
      }
      Boolean upgraded = true; //Prefs.getUpgraded(this.getActivity());
      final Long userId = Prefs.getUserId(this.getActivity());
      
      if (upgraded)
      { }
      else 
      {
         Intent intent = new Intent(this.getActivity(), UpgradeActivity.class);
      }
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState)
   {
      // Inflate the layout for this fragment
      View view = inflater
            .inflate(R.layout.fragment_settings, container, false);

      return view;
   }

   @Override
   public void onResume()
   {
      super.onResume();

   }

   // TODO: Rename method, update argument and hook method into UI event
   public void onButtonPressed(Uri uri)
   {
      if (mListener != null)
      {
         //mListener.onFragmentInteraction(uri);
      }
   }

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
   }

}
