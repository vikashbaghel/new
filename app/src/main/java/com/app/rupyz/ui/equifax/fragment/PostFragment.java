package com.app.rupyz.ui.equifax.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.app.rupyz.R;
import com.app.rupyz.databinding.FragmentMoreBinding;
import com.app.rupyz.databinding.FragmentPostBinding;
import com.app.rupyz.generic.base.BrowserActivity;
import com.app.rupyz.generic.logger.FirebaseLogger;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.calculator.all_calculator.AllCalculatorActivity;
import com.app.rupyz.ui.common.ComingSoonActivity;
import com.app.rupyz.ui.equifax.EquiFaxProfileActivity;
import com.app.rupyz.ui.user.ProfileActivity;

public class PostFragment extends Fragment {

    private FragmentPostBinding binding;
    private Utility mUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mUtil = new Utility(getActivity());
        new FirebaseLogger(getContext()).sendLog("Setting", "Setting");
        binding = FragmentPostBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

}
