package com.app.rupyz.adapter.organization;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.app.rupyz.ui.equifax.fragment.my_account.EquiFaxAllAccountFragment;
import com.app.rupyz.ui.equifax.fragment.my_account.EquiFaxDefaultsFragment;
import com.app.rupyz.ui.equifax.fragment.my_account.EquiFaxOverdueFragment;
import com.app.rupyz.ui.equifax.fragment.my_account.EquiFaxRepaymentFragment;

public class EquiFaxTabLayoutAdapter extends FragmentPagerAdapter {
    public EquiFaxTabLayoutAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new EquiFaxAllAccountFragment();
        } else if (position == 1) {
            fragment = new EquiFaxOverdueFragment();
        } else if (position == 2) {
            fragment = new EquiFaxRepaymentFragment();
        } else if (position == 3) {
            fragment = new EquiFaxDefaultsFragment();
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
