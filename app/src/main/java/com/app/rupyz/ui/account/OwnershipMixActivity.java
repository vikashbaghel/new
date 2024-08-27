package com.app.rupyz.ui.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.app.rupyz.R;
import com.app.rupyz.adapter.myaccount.TabLayoutAdapter;
import com.app.rupyz.databinding.ActivityOwnershipMixBinding;
import com.app.rupyz.generic.model.individual.experian.Tradeline;
import com.app.rupyz.ui.account.dailog.AddEMIDetailSheet;
import com.app.rupyz.ui.account.dailog.MyAccountDetailSheet;
import com.app.rupyz.ui.account.fragment.AllAccountFragment;
import com.app.rupyz.ui.account.fragment.DefaultsFragment;
import com.app.rupyz.ui.account.fragment.OverdueFragment;
import com.app.rupyz.ui.account.fragment.RepaymentFragment;
import com.app.rupyz.ui.account.ownership_mix.IndividualFragment;
import com.app.rupyz.ui.account.ownership_mix.JointFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OwnershipMixActivity extends AppCompatActivity {

    private ActivityOwnershipMixBinding binding;
    public static List<Tradeline> mData = new ArrayList<>();
    public static List<Tradeline> mCustomData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOwnershipMixBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initLayout();
    }

    private void initToolbar() {
        Toolbar toolBar = this.findViewById(R.id.toolbar_my);
        ImageView imageViewBack = toolBar.findViewById(R.id.img_back);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initLayout() {
        Gson gson = new Gson();
        mData = new ArrayList<>();
        mCustomData = Arrays.asList(gson.fromJson(getIntent().getStringExtra("data"), Tradeline[].class));
        for (Tradeline Item : mCustomData) {
            if (Item.getAccount_Status().equalsIgnoreCase("active")) {
                mData.add(Item);
            }
        }
        for (Tradeline Item : mCustomData) {
            if (Item.getAccount_Status().equalsIgnoreCase("closed")) {
                mData.add(Item);
            }
        }
        TabAdapter mTabLayoutAdapter = new TabAdapter(getSupportFragmentManager());
        binding.viewPager.setAdapter(mTabLayoutAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }

    public void initBottomSheet(Tradeline tradeline) {
        Bundle bundle = new Bundle();
        bundle.putString("data", new Gson().toJson(tradeline));
        MyAccountDetailSheet fragment = new MyAccountDetailSheet();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "");
    }

    public void initAddEmiBottomSheet(Tradeline tradeline, int pos, int type) {
        Bundle bundle = new Bundle();
        bundle.putString("data", new Gson().toJson(tradeline));
        bundle.putInt("pos", pos);
        bundle.putInt("type", type);
        AddEMIDetailSheet fragment = new AddEMIDetailSheet();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "");
    }

    class TabAdapter extends FragmentPagerAdapter {
        public TabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0) {
                fragment = new IndividualFragment();
            } else {
                fragment = new JointFragment();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            if (position == 0) {
                title = "Individual";
            } else if (position == 1) {
                title = "Joint/Guarantor";
            }
            return title;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}