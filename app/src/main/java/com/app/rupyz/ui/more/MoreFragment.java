package com.app.rupyz.ui.more;

import static com.app.rupyz.generic.utils.SharePrefConstant.IS_EQUI_FAX;
import static com.app.rupyz.generic.utils.SharePrefConstant.IS_SKIP_GSTIN;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.rupyz.BuildConfig;
import com.app.rupyz.R;
import com.app.rupyz.databinding.FragmentMoreBinding;
import com.app.rupyz.generic.logger.FirebaseLogger;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.calculator.all_calculator.AllCalculatorActivity;
import com.app.rupyz.ui.common.ComingSoonActivity;
import com.app.rupyz.ui.equifax.fragment.EquiFaxAlertFragment;
import com.app.rupyz.ui.equifax.fragment.EquiFaxDebtProfileFragment;
import com.app.rupyz.ui.home.AlertActivity;
import com.app.rupyz.ui.home.DebtProfileActivity;
import com.app.rupyz.ui.user.UserInfoActivity;

public class MoreFragment extends Fragment implements View.OnClickListener {

    private FragmentMoreBinding binding;
    private Utility mUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMoreBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUtil = new Utility(requireActivity());

        new FirebaseLogger(getContext()).sendLog("Setting", "Setting");

        initLayout();

    }

    @SuppressLint("SetTextI18n")
    private void initLayout() {

        binding.btnAlert.setOnClickListener(this);
        binding.btnCalculate.setOnClickListener(this);
        binding.btnDebtProfile.setOnClickListener(this);
        binding.btnLearn.setOnClickListener(this);
        binding.btnSupport.setOnClickListener(this);
        binding.llSetting.setOnClickListener(this);
        binding.btnProfile.setOnClickListener(this);

        if (!SharedPref.getInstance().getBoolean(IS_EQUI_FAX, false)){
           binding.btnAlert.setVisibility(View.GONE);
           binding.btnDebtProfile.setVisibility(View.GONE);
        }

        binding.tvAppVersion.setText("V " + BuildConfig.VERSION_NAME);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_calculate:
                startActivity(new Intent(getActivity(), AllCalculatorActivity.class));
                break;
            case R.id.btn_alert:
                if (SharedPref.getInstance().getBoolean(IS_EQUI_FAX, false)) {
                    if (SharedPref.getInstance().getBoolean(IS_SKIP_GSTIN, false)) {
                        startActivity(new Intent(getActivity(), AlertActivity.class));
                    } else {
                        startActivity(new Intent(getActivity(), EquiFaxAlertFragment.class));
                    }
                } else {
                    startActivity(new Intent(getActivity(), AlertActivity.class));
                }
                break;
            case R.id.btn_debt_profile:
                if (SharedPref.getInstance().getBoolean(IS_EQUI_FAX, false)) {
                    if (SharedPref.getInstance().getBoolean(IS_SKIP_GSTIN, false)) {
                        startActivity(new Intent(getActivity(), DebtProfileActivity.class));
                    } else {
                        startActivity(new Intent(getActivity(), EquiFaxDebtProfileFragment.class));
                    }

                } else {
                    startActivity(new Intent(getActivity(), DebtProfileActivity.class));
                }
                break;
            case R.id.btn_learn:
//                if (SharedPref.getInstance().getBoolean(IS_EQUI_FAX)) {
//                    startActivity(new Intent(getActivity(), EquiFaxLearnFragment.class));
//                } else {
//                    startActivity(new Intent(getActivity(), LearnFragment.class));
//                }
                break;
            case R.id.btn_support:
                startActivity(new Intent(getActivity(), ComingSoonActivity.class));
                break;
            case R.id.ll_setting:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.btn_profile:
                startActivity(new Intent(getActivity(), UserInfoActivity.class));
                break;
        }
    }
}
