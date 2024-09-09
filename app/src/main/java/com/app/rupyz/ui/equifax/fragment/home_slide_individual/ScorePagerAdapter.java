package com.app.rupyz.ui.equifax.fragment.home_slide_individual;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ScorePagerAdapter extends FragmentPagerAdapter {
    boolean visible;
    public ScorePagerAdapter(@NonNull FragmentManager fm, int behavior, boolean visible) {
        super(fm, behavior);
        this.visible=visible;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ScoreFragment(visible);
        } else {
            return new LoanSummaryFragment();
        }
    }

    // size is hardcoded
    @Override
    public int getCount() {
        return 1;
    }
}