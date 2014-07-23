package com.nadajp.littletalkers;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;

public class ItemDetailPagerAdapter extends SectionsPagerAdapter
{

   public ItemDetailPagerAdapter(FragmentManager fm, Context c)
   {
      super(fm, c);
   }

   @Override
   public Fragment getItem(int position)
   {
      return ItemDetailFragment.newInstance(position);
   }
}
