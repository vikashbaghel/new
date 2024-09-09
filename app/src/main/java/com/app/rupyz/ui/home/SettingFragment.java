package com.app.rupyz.ui.home;

import static com.app.rupyz.generic.utils.AppConstant.POLICY_URL;
import static com.app.rupyz.generic.utils.AppConstant.TERMS_URL;
import static com.app.rupyz.generic.utils.SharePrefConstant.ACCOUNT_TYPE;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.app.rupyz.R;
import com.app.rupyz.databinding.SettingFragmentBinding;
import com.app.rupyz.generic.base.BrowserActivity;
import com.app.rupyz.generic.logger.FirebaseLogger;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.common.faq.FaqActivity;
import com.app.rupyz.ui.equifax.EquiFaxProfileActivity;
import com.app.rupyz.ui.user.ProfileActivity;

public class SettingFragment extends Fragment implements View.OnClickListener {

    SettingFragmentBinding binding;
    private Utility mUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mUtil = new Utility(getActivity());
        new FirebaseLogger(getContext()).sendLog("Setting", "Setting");
        binding = SettingFragmentBinding.inflate(getLayoutInflater());
        initLayout();
        return binding.getRoot();
    }

    private void initLayout() {
        binding.btnLogout.setOnClickListener(this);
        binding.btnProfile.setOnClickListener(this);
        binding.btnRate.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);
        binding.btnPrivacy.setOnClickListener(this);
        binding.btnTerms.setOnClickListener(this);
        binding.btnFaq.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                mUtil.logout();
                break;
            case R.id.btn_profile:
                intiProfile();
                break;
            case R.id.btn_share:
                Utility.shareApp(getActivity(), (BitmapDrawable) binding.imageShare.getDrawable());
                break;
            case R.id.btn_rate:
                Utility.rateApp(getActivity());
                break;
            case R.id.btn_privacy:
                initOpenBrowser(POLICY_URL, "Privacy Policy");
                break;
            case R.id.btn_terms:
                initOpenBrowser(TERMS_URL, "Terms of Service");
                break;
            case R.id.btn_faq:
                startActivity(new Intent(getActivity(), FaqActivity.class));
                break;
        }
    }

    private void intiProfile() {
        if (SharedPref.getInstance().getString(ACCOUNT_TYPE).equalsIgnoreCase(getResources().getString(R.string.RETAIL))
                || SharedPref.getInstance().getString(ACCOUNT_TYPE).equalsIgnoreCase(getResources().getString(R.string.COMMERCIAL))) {
            startActivity(new Intent(getActivity(), EquiFaxProfileActivity.class));
        } else {
            startActivity(new Intent(getActivity(), ProfileActivity.class));
        }
    }

    private void initOpenBrowser(String url, String title) {
        Intent intent = new Intent(getActivity(), BrowserActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        startActivity(intent);
    }
}
