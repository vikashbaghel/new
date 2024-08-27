package com.app.rupyz.adapter.myaccount;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.app.rupyz.ui.account.fragment.AllAccountFragment;
import com.app.rupyz.ui.account.fragment.DefaultsFragment;
import com.app.rupyz.ui.account.fragment.OverdueFragment;
import com.app.rupyz.ui.account.fragment.RepaymentFragment;

public class TabLayoutAdapter extends FragmentPagerAdapter {
    public TabLayoutAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new AllAccountFragment();
        } else if (position == 1) {
            fragment = new OverdueFragment();
        } else if (position == 2) {
            fragment = new RepaymentFragment();
        } else if (position == 3) {
            fragment = new DefaultsFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0) {
            title = "All Account";
        } else if (position == 1) {
            title = "Overdue";
        } else if (position == 2) {
            title = "Missed Repayments";
        } else if (position == 3) {
            title = "Defaults";
        }
        return title;
    }
}
