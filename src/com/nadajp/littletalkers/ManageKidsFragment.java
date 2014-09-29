package com.nadajp.littletalkers;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;

import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.utils.Prefs;

public class ManageKidsFragment extends ListFragment
{
   ListView listView;
   //SimpleCursorAdapter mCursorAdapter;
   KidsListCursorAdapter mAdapter;
   public long[] mItemsToDelete;
   private static final int DELETE_SELECTED_DIALOG_ID = 1;
   private static int mNumSelected = 0;
   private static String DEBUG_TAG = "ManageKids";
   private ModifyKidsListener mListener;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState)
   {
      // Inflate the layout for this fragment
      return inflater.inflate(R.layout.fragment_manage_kids, container, false);
   }

   @Override
   public void onActivityCreated(Bundle savedInstanceState)
   {
      super.onActivityCreated(savedInstanceState);

      // Populate the list with all the kids from the database
      Cursor cursor = DbSingleton.get().getKidsForList();

      if (cursor.getCount() == 0) { return; }
  
      mAdapter = new KidsListCursorAdapter(getActivity(), cursor, 0, this);
      setListAdapter(mAdapter);
      
      // Implement contextual menu
      listView = getListView();
      listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
      listView.setMultiChoiceModeListener(new MultiChoiceModeListener()
      {
         @Override
         public void onItemCheckedStateChanged(ActionMode mode, int position,
               long id, boolean checked)
         {
            // Here you can do something when items are selected/de-selected,
            // such as update the title in the CAB

            if (!checked)
            {
               mNumSelected--;
            } else
            {
               mNumSelected++;
               listView.setSelection(position);
            }
            mode.setTitle("Selected: " + mNumSelected);
         }

         @Override
         public boolean onActionItemClicked(ActionMode mode, MenuItem item)
         {
            // Respond to clicks on the actions in the CAB
            switch (item.getItemId())
            {
            case R.id.menu_delete:
               mItemsToDelete = listView.getCheckedItemIds();
               Log.i(DEBUG_TAG, "Items to delete: " + mItemsToDelete.length);
               deleteSelectedItems();
               mode.finish(); // Action picked, so close the CAB
               mNumSelected = 0;
               return true;
            default:
               return false;
            }
         }

         @Override
         public boolean onCreateActionMode(ActionMode mode, Menu menu)
         {
            // Inflate the menu for the CAB
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
         }

         @Override
         public void onDestroyActionMode(ActionMode mode)
         {
            mNumSelected = 0;
            // Here you can make any necessary updates to the activity when
            // the CAB is removed. By default, selected items are
            // deselected/unchecked.
         }

         @Override
         public boolean onPrepareActionMode(ActionMode mode, Menu menu)
         {
            // Here you can perform updates to the CAB due to
            // an invalidate() request
            return false;
         }
      });
   }

   
   public void deleteSelectedItems()
   {
      DeleteSelectedDialogFragment dlg = new DeleteSelectedDialogFragment(-1);
      dlg.setTargetFragment(this, DELETE_SELECTED_DIALOG_ID);
      dlg.show(getFragmentManager(), "DeleteSelectedDialogFragment");
   }
   
   public void deleteItem(long id)
   {
      DeleteSelectedDialogFragment dlg = new DeleteSelectedDialogFragment(id);
      dlg.setTargetFragment(this, DELETE_SELECTED_DIALOG_ID);
      dlg.show(getFragmentManager(), "DeleteSelectedDialogFragment");
   }

   public void confirmDelete(long singleId)
   {
      if (singleId > -1)
      {
         mItemsToDelete = new long[1];
         mItemsToDelete[0] = singleId;
      }
      Log.i(DEBUG_TAG, "Items to delete: " + mItemsToDelete.length);
      for (long id : mItemsToDelete)
      {
         String filename = DbSingleton.get().getPicturePath(id);
         if (filename != null)
         {
            File file = new File(filename);
            if (file.exists())
            {
               file.delete();
            }
         }
      }
      DbSingleton.get().deleteKids(mItemsToDelete);
      Cursor cursor = DbSingleton.get().getKidsForList();
      mAdapter.swapCursor(cursor);
      mAdapter.notifyDataSetChanged();
      mListener.onKidsDeleted();
      mItemsToDelete = null;
   }

   @Override
   public void onListItemClick(ListView l, View v, int position, long id)
   {     
      // show kid detail view
      Intent intent = new Intent(this.getActivity(), KidProfileActivity.class);
      intent.putExtra(Prefs.CURRENT_KID_ID, id);
      intent.putExtra("ManageKidsView", true);
      startActivity(intent);     
   }

   public static class DeleteSelectedDialogFragment extends DialogFragment
   {
      public long mId;
      
      DeleteSelectedDialogFragment(long id)
      {
         mId = id;
      }
      
      @Override
      public Dialog onCreateDialog(Bundle savedInstanceState)
      {
         // Use the Builder class for convenient dialog construction
         AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
         builder
               .setMessage(R.string.delete_kids_dialog)
               .setPositiveButton(R.string.delete,
                     new DialogInterface.OnClickListener()
                     {
                        public void onClick(DialogInterface dialog, int id)
                        {
                           ((ManageKidsFragment) getTargetFragment())
                                 .confirmDelete(mId);
                        }
                     })
               .setNegativeButton(R.string.cancel,
                     new DialogInterface.OnClickListener()
                     {
                        public void onClick(DialogInterface dialog, int id)
                        {
                           // User cancelled the dialog
                        }
                     });
         // Create the AlertDialog object and return it
         return builder.create();
      }
   }

   public interface ModifyKidsListener
   {
      public void onKidsDeleted();
      
      //public void onKidModified(int kidId);
   }
   
   @Override
   public void onAttach(Activity activity)
   {
      super.onAttach(activity);
      if (activity instanceof ModifyKidsListener)
      {
         mListener = (ModifyKidsListener) activity;
      } else
      {
         throw new ClassCastException(activity.toString()
               + " must implemenet ManageKidsFragment.ModifyKidsListener");
      }
   }

   @Override
   public void onDetach()
   {
      super.onDetach();
      mListener = null;
   }
   
   @Override
   public void onSaveInstanceState(Bundle outState)
   {
      super.onSaveInstanceState(outState);
   }

   @Override
   public void onDestroy()
   {
      super.onDestroy();
      mAdapter.getCursor().close();
   }
}
