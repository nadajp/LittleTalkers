package com.nadajp.littletalkers;

import com.google.android.gms.common.AccountPicker;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.nadajp.littletalkers.backup.UploadUserData;
import com.nadajp.littletalkers.utils.Prefs;

import android.accounts.AccountManager;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link LoginFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link LoginFragment#newInstance} factory method to create an instance of
 * this fragment.
 *
 */
public class LoginFragment extends Fragment implements OnClickListener
{
   static final int REQUEST_ACCOUNT_PICKER = 2;
   private static final int ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION = 2222;
   private static final String DEBUG_TAG = "LoginActivity";
   private GoogleAccountCredential mCredential;
  
   private String mEmailAccount = Prefs.getAccountName(this.getActivity());

   private Button btnLogIn;
   private Button btnUpgrade;
   
   // TODO: Rename parameter arguments, choose names that match
   // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
   private static final String ARG_PARAM1 = "param1";
   private static final String ARG_PARAM2 = "param2";

   // TODO: Rename and change types of parameters
   private String mParam1;
   private String mParam2;

   private OnFragmentInteractionListener mListener;
   private String mAccountName;

   /**
    * Use this factory method to create a new instance of this fragment using
    * the provided parameters.
    *
    * @param param1
    *           Parameter 1.
    * @param param2
    *           Parameter 2.
    * @return A new instance of fragment LoginFragment.
    */
   // TODO: Rename and change types and number of parameters
   public static LoginFragment newInstance(String param1, String param2)
   {
      LoginFragment fragment = new LoginFragment();
      Bundle args = new Bundle();
      args.putString(ARG_PARAM1, param1);
      args.putString(ARG_PARAM2, param2);
      fragment.setArguments(args);
      return fragment;
   }

   public LoginFragment()
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
     
      btnLogIn = (Button) this.getActivity().findViewById(R.id.button_login);
      btnUpgrade = (Button) this.getActivity().findViewById(R.id.button_upgrade);
      
      btnLogIn.setOnClickListener(this);
      btnUpgrade.setOnClickListener(this);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState)
   {
      View view = inflater.inflate(R.layout.fragment_login,
            container, false);
      return view;
   }

   /* TODO: Rename method, update argument and hook method into UI event
   public void onButtonPressed(Uri uri)
   {
      if (mListener != null)
      {
         mListener.onFragmentInteraction(uri);
      }
   }*

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
   }*/

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
   }
   
   @Override
   public void onClick(View v)
   {
      switch (v.getId())
      {
      case R.id.button_login:
         chooseAccount();
         break;
      case R.id.button_upgrade:
         
         break;
      default:
         return;
      }
   }

   
   // setSelectedAccountName definition
   private void setSelectedAccountName(String accountName)
   {
      Prefs.saveAccountName(this.getActivity(), accountName);
      mCredential.setSelectedAccountName(accountName);
      this.mAccountName = accountName;
   }

   /*
    * public class EndpointsTask extends AsyncTask<Context, Integer, Long> { //
    * Use a builder to help formulate the API request. Kidendpoint.Builder
    * endpointBuilder = new Kidendpoint.Builder(
    * AndroidHttp.newCompatibleTransport(), new JacksonFactory(), credential);
    * 
    * Kidendpoint endpoint = endpointBuilder.build(); protected Long
    * doInBackground(Context... contexts) { try { ArrayList<Kid> kids =
    * ServerBackupUtils.getKids(); //endpoint.removeKid((long) 1).execute(); Kid
    * result = endpoint.insertKid(kids.get(0)).execute(); Log.i(DEBUG_TAG,
    * "Birthdate:" + Utils.getDateForDisplay(result.getBirthdate(),
    * contexts[0])); Log.i(DEBUG_TAG, "Result: " + result); } catch (IOException
    * e) { e.printStackTrace(); } return (long) 0;
    * 
    * } }
    */

   // used in endpoints, this allows user to select account
   void chooseAccount()
   {
      startActivityForResult(AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
            false, null, null, null, null),
            REQUEST_ACCOUNT_PICKER);
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data)
   {
      super.onActivityResult(requestCode, resultCode, data);
      switch (requestCode)
      {
      case REQUEST_ACCOUNT_PICKER:
         if (data != null && data.getExtras() != null)
         {
            String accountName = data.getExtras().getString(
                  AccountManager.KEY_ACCOUNT_NAME);
            if (accountName != null)
            {
               setSelectedAccountName(accountName);

               // User is authorized.
               Log.i(DEBUG_TAG, "Authorized user: " + accountName
                     + ", starting upload");
               new UploadUserData(mCredential).execute(this.getActivity());
            }
         }
         break;
      }
   }
}
