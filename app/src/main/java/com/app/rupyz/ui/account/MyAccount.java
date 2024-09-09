package com.app.rupyz.ui.account;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.app.rupyz.R;
import com.app.rupyz.adapter.myaccount.TabLayoutAdapter;
import com.app.rupyz.generic.base.BaseActivity;
import com.app.rupyz.generic.model.createemi.experian.Datum;
import com.app.rupyz.generic.model.individual.experian.Tradeline;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.ui.account.dailog.AddEMIDetailSheet;
import com.app.rupyz.ui.account.dailog.MyAccountDetailSheet;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyAccount extends BaseActivity {

    private ViewPager mViewPager;
    private TabLayoutAdapter mTabLayoutAdapter;
    private TabLayout mTabLayout;
    public static List<Tradeline> mData = new ArrayList<>();
    public static List<Tradeline> mCustomData;
    public static List<Datum> mEmiData;
    public int index = 1;
    private Toolbar toolbar;
    private ApiInterface mApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        initToolbar();
        index = getIntent().getExtras().getInt("index");
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
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
        for (Tradeline Item : mCustomData) {
            if (!Item.getAccount_Status().equalsIgnoreCase("closed") && (!Item.getAccount_Status().equalsIgnoreCase("active"))) {
                mData.add(Item);
            }
        }
        mViewPager = findViewById(R.id.viewPager);
        toolbar = findViewById(R.id.toolbar_my);
        mTabLayoutAdapter = new TabLayoutAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabLayoutAdapter);
        mTabLayout = findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(index);
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
}