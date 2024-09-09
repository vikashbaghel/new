package com.app.rupyz.adapter.organization.individual;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.app.rupyz.ui.equifax.fragment.individual.EquiFaxIndividualAllAccountFragment;
import com.app.rupyz.ui.equifax.fragment.individual.EquiFaxIndividualDefaultsFragment;
import com.app.rupyz.ui.equifax.fragment.individual.EquiFaxIndividualOverdueFragment;
import com.app.rupyz.ui.equifax.fragment.individual.EquiFaxIndividualRepaymentFragment;
import com.app.rupyz.ui.equifax.fragment.my_account.EquiFaxDefaultsFragment;
import com.app.rupyz.ui.equifax.fragment.my_account.EquiFaxRepaymentFragment;

public class EquiFaxIndividualTabLayoutAdapter extends FragmentPagerAdapter {
    public EquiFaxIndividualTabLayoutAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new EquiFaxIndividualAllAccountFragment();
        } else if (position == 1) {
            fragment = new EquiFaxIndividualOverdueFragment();
        } else if (position == 2) {
            fragment = new EquiFaxIndividualRepaymentFragment();
        } else if (position == 3) {
            fragment = new EquiFaxIndividualDefaultsFragment();
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
