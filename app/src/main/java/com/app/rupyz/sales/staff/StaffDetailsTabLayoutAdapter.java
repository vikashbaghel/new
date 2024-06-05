package com.app.rupyz.sales.staff;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.app.rupyz.generic.utils.AppConstant;
import com.app.rupyz.model_kt.order.sales.StaffData;
import com.app.rupyz.sales.beatplan.StaffBeatDetailsFragment;
import com.app.rupyz.sales.targets.TargetDetailsFragment;

public class StaffDetailsTabLayoutAdapter extends FragmentPagerAdapter {
    int mTotalTabs;
    private StaffData staffData;

    public StaffDetailsTabLayoutAdapter(FragmentManager fragmentManager, int totalTabs, StaffData staffData) {
        super(fragmentManager);
        mTotalTabs = totalTabs;
        this.staffData = staffData;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                TargetDetailsFragment fragment = new TargetDetailsFragment();
                Bundle bundle = new Bundle();
                if (staffData.getUser() != null) {
                    bundle.putInt(AppConstant.STAFF_ID, staffData.getUser());
                }
                bundle.putBoolean(AppConstant.STAFF_DETAILS, true);
                fragment.setArguments(bundle);
                return fragment;
            case 1:
                StaffBeatDetailsFragment staffBeatDetailsFragment = new StaffBeatDetailsFragment();
                Bundle bundle1 = new Bundle();
                if (staffData.getUser() != null) {
                    bundle1.putInt(AppConstant.STAFF_ID, staffData.getUser());
                }
                bundle1.putBoolean(AppConstant.STAFF_DETAILS, true);
                staffBeatDetailsFragment.setArguments(bundle1);
                return staffBeatDetailsFragment;
            default:
                return null;

        }
    }

    @Override
    public int getItemPosition(Object object) {
        // Causes adapter to reload all Fragments when
        // notifyDataSetChanged is called
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mTotalTabs;
    }
}