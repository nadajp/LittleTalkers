package com.nadajp.littletalkers;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;

public class ItemListPagerAdapter extends SectionsPagerAdapter
{

   public ItemListPagerAdapter(FragmentManager fm, Context c)
   {
      super(fm, c);
   }

   @Override
   public Fragment getItem(int position)
   {
      return ItemListFragment.newInstance(position);
   }
}
