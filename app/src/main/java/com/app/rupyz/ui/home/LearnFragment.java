package com.app.rupyz.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.app.rupyz.R;
import com.app.rupyz.databinding.FragmentLearnListBinding;
import com.app.rupyz.generic.utils.AppConstant;
import com.app.rupyz.ui.home.fragment.LearnListFragment;

import java.util.ArrayList;
import java.util.List;

public class LearnFragment extends Fragment {
    FragmentLearnListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLearnListBinding.inflate(getLayoutInflater());
        replaceFragment(new LearnListFragment());
        return binding.getRoot();
    }


    private void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment, AppConstant.TOTAL_AMOUNT_RECEIVE);
        ft.commit();
    }
}
