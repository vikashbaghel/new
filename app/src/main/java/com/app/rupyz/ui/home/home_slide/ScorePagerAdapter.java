package com.app.rupyz.ui.home.home_slide;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.app.rupyz.databinding.LoanSummaryFragmentBinding;
import com.app.rupyz.ui.overview.fragment.SliderItemFragment;

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