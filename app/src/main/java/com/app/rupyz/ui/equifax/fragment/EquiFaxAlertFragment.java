package com.app.rupyz.ui.equifax.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.rupyz.databinding.EquifaxAlertFragmentBinding;
import com.app.rupyz.databinding.FragmentLearnListBinding;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.logger.FirebaseLogger;
import com.app.rupyz.generic.model.organization.AlertsItem;
import com.app.rupyz.generic.model.organization.EquiFaxInfoModel;
import com.app.rupyz.generic.model.organization.individual.Alert;
import com.app.rupyz.generic.model.organization.individual.EquiFaxIndividualInfoModel;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.equifax.fragment.alerts.EquiFaxCommercialAlertFragment;
import com.app.rupyz.ui.equifax.fragment.alerts.EquiFaxIndividualAlertFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquiFaxAlertFragment extends AppCompatActivity {

    private EquifaxAlertFragmentBinding binding;
    EquiFaxInfoModel mData;
    private List<Alert> mRetailAlertData;
    private List<AlertsItem> mCommercialAlertData;
    private Utility mUtil;
    private EquiFaxReportHelper mReportHelper;
    private EquiFaxIndividualInfoModel equiFaxIndividualInfoModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new FirebaseLogger(this).sendLog("Learn", "Learn");
        binding = EquifaxAlertFragmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mReportHelper = EquiFaxReportHelper.getInstance();
        mUtil = new Utility(this);
        equiFaxIndividualInfoModel = mReportHelper.getRetailReport();
        mData = mReportHelper.getCommercialReport();
        initLayout();
    }


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        binding = EquifaxAlertFragmentBinding.inflate(getLayoutInflater());
//        mReportHelper = EquiFaxReportHelper.getInstance();
//        mUtil = new Utility(getActivity());
//        equiFaxIndividualInfoModel = mReportHelper.getRetailReport();
//        mData = mReportHelper.getCommercialReport();
//        new FirebaseLogger(getContext()).sendLog("Alert", "Alert");
//        initLayout();
//        return binding.getRoot();
//    }

    private void initLayout() {
        setupViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.imgClose.setOnClickListener(v -> finish());
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new EquiFaxCommercialAlertFragment(), mData.getReport().getLegalName());
        adapter.addFragment(new EquiFaxIndividualAlertFragment(), mData.getReport().getAuthorizedSignatory());
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
