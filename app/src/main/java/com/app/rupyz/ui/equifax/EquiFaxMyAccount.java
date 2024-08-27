package com.app.rupyz.ui.equifax;

import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.app.rupyz.R;
import com.app.rupyz.adapter.organization.EquiFaxTabLayoutAdapter;
import com.app.rupyz.generic.base.BaseActivity;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.inteface.BottomSheetCallback;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.createemi.CreateEMIResponse;
import com.app.rupyz.generic.model.createemi.Datum;
import com.app.rupyz.generic.model.createemi.EMIResponse;
import com.app.rupyz.generic.model.organization.EquiFaxInfoModel;
import com.app.rupyz.generic.model.organization.TradelinesItem;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.equifax.dailog.EquiFaxMyAccountDetailSheet;
import com.app.rupyz.ui.equifax.dailog.EquifaxCommercialAddEMIDetailSheet;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquiFaxMyAccount extends BaseActivity implements BottomSheetCallback {

    private ViewPager mViewPager;
    private EquiFaxTabLayoutAdapter mTabLayoutAdapter;
    private TabLayout mTabLayout;
    public static List<TradelinesItem> mData = new ArrayList<>();
    public static List<TradelinesItem> mCustomData;
    public int index = 1;
    private EquiFaxInfoModel equiFaxInfoModel;
    private BottomSheetCallback callback;
    private EquiFaxApiInterface mApiInterface;
    private EquiFaxReportHelper mReportHelper;
    public static List<Datum> mEmiData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        callback = this;
        initToolbar();
        index = getIntent().getExtras().getInt("index");
        mReportHelper = EquiFaxReportHelper.getInstance();
        equiFaxInfoModel = mReportHelper.getCommercialReport();
        mApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface.class);
        Gson gson = new Gson();
        mData = new ArrayList<>();
        //mCustomData = Arrays.asList(gson.fromJson(getIntent().getStringExtra("data"), TradelinesItem[].class));
        mCustomData = mReportHelper.getCommercialReport().getReport().getTradelines();
        mEmiData = mReportHelper.getEquifaxCommercialEMI().getData();

        for (Datum Item : mEmiData) {
            for (TradelinesItem tradelinesItem : mCustomData) {
                if (Item.getAccountNo().equals(tradelinesItem.getAccountNo())) {
                    tradelinesItem.setInstallmentAmount(Item.getInstallmentAmount());
                    tradelinesItem.setMonthDueDay(Item.getMonthDueDay());
                    tradelinesItem.setRepaymentTenure(Item.getRepaymentTenure());
                    tradelinesItem.setInterestRate(Item.getInterestRate());
                }
            }
        }

        for (TradelinesItem Item : mCustomData) {
            if (Item.getAccountStatus().equalsIgnoreCase("open")) {
                mData.add(Item);
            }
        }
        for (TradelinesItem Item : mCustomData) {
            if (Item.getAccountStatus().equalsIgnoreCase("closed")) {
                mData.add(Item);
            }
        }
        mViewPager = findViewById(R.id.viewPager);
        mTabLayoutAdapter = new EquiFaxTabLayoutAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabLayoutAdapter);
        mTabLayout = findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(index);
        //emiDetails();
    }

    public void initBottomSheet(TradelinesItem tradeline) {
        Bundle bundle = new Bundle();
        bundle.putString("data", new Gson().toJson(tradeline));
        EquiFaxMyAccountDetailSheet fragment = new EquiFaxMyAccountDetailSheet();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "");
    }

    public void initAddEmiBottomSheet(TradelinesItem tradeline, int pos) {
        Bundle bundle = new Bundle();
        bundle.putString("data", new Gson().toJson(tradeline));
        bundle.putInt("pos", pos);
        EquifaxCommercialAddEMIDetailSheet fragment = new EquifaxCommercialAddEMIDetailSheet(callback);
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

    @Override
    public void callbackMethod(EMIResponse emiResponse) {
        //Toast.makeText(this, "Yes"+emiResponse.getAccountNo(), Toast.LENGTH_SHORT).show();
    }

    private void emiDetails() {
        Call<CreateEMIResponse> call = mApiInterface.getEMIList(
                1, "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call.enqueue(new Callback<CreateEMIResponse>() {
            @Override
            public void onResponse(Call<CreateEMIResponse> call, Response<CreateEMIResponse> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());
                    CreateEMIResponse response1 = response.body();
                    mReportHelper.setEquifaxCommercialEMI(response.body());
                    if (response1.getData().size() > 0) {
                        for (Datum Item : response1.getData()) {
                            for (TradelinesItem tradelinesItem : mCustomData) {
                                if (Item.getAccountNo().equals(tradelinesItem.getAccountNo())) {
                                    tradelinesItem.setInstallmentAmount(Item.getInstallmentAmount());
                                    tradelinesItem.setMonthDueDay(Item.getMonthDueDay());
                                    tradelinesItem.setRepaymentTenure(Item.getRepaymentTenure());
                                    tradelinesItem.setInterestRate(Item.getInterestRate());
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CreateEMIResponse> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }
}