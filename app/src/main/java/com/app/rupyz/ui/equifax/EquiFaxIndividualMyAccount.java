package com.app.rupyz.ui.equifax;

import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.app.rupyz.R;
import com.app.rupyz.adapter.organization.individual.EquiFaxIndividualTabLayoutAdapter;
import com.app.rupyz.generic.base.BaseActivity;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.createemi.CreateEMIResponse;
import com.app.rupyz.generic.model.createemi.Datum;
import com.app.rupyz.generic.model.organization.individual.EquiFaxIndividualInfoModel;
import com.app.rupyz.generic.model.organization.individual.Tradeline;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.equifax.dailog.EquiFaxIndividualMyAccountDetailSheet;
import com.app.rupyz.ui.equifax.dailog.EquifaxIndividualAddEMIDetailSheet;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquiFaxIndividualMyAccount extends BaseActivity {

    private ViewPager mViewPager;
    private EquiFaxIndividualTabLayoutAdapter mTabLayoutAdapter;
    private TabLayout mTabLayout;
    public static List<Tradeline> mData = new ArrayList<>();
    public static List<Tradeline> mCustomData;
    public int index = 1;
    private EquiFaxReportHelper mReportHelper;
    public EquiFaxIndividualInfoModel equiFaxIndividualInfoModel;
    private EquiFaxApiInterface mApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equi_fax_individual_my_account);
        initToolbar();
        mReportHelper = EquiFaxReportHelper.getInstance();
        equiFaxIndividualInfoModel = mReportHelper.getRetailReport();
        index = getIntent().getExtras().getInt("index");
        mApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface.class);
        Gson gson = new Gson();
        mData = new ArrayList<>();
        //mCustomData = Arrays.asList(gson.fromJson(getIntent().getStringExtra("data"), Tradeline[].class));
        mCustomData = mReportHelper.getRetailReport().getReport().getTradelines();

        for (int i = 0; i < mCustomData.size(); i++) {
            Logger.errorLogger("DATTA " + i + ":- ", mCustomData.get(i).getAccount_no());
        }

        if (mCustomData.size() > 0) {
            for (Tradeline Item : mCustomData) {
                if (Item.getAccount_status().equalsIgnoreCase("open")) {
                    mData.add(Item);
                }
            }
            for (Tradeline Item : mCustomData) {
                if (Item.getAccount_status().equalsIgnoreCase("closed")) {
                    mData.add(Item);
                }
            }
        }
        mViewPager = findViewById(R.id.viewPager);
        mTabLayoutAdapter = new EquiFaxIndividualTabLayoutAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabLayoutAdapter);
        mTabLayout = findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(index);
        //emiDetails();
    }

    public void initBottomSheet(Tradeline tradeline) {
        Bundle bundle = new Bundle();
        bundle.putString("data", new Gson().toJson(tradeline));
        EquiFaxIndividualMyAccountDetailSheet fragment = new EquiFaxIndividualMyAccountDetailSheet();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "");
    }

    public void initAddEmiBottomSheet(Tradeline tradeline, int pos) {
        Bundle bundle = new Bundle();
        bundle.putString("data", new Gson().toJson(tradeline));
        bundle.putInt("pos", pos);
        EquifaxIndividualAddEMIDetailSheet fragment = new EquifaxIndividualAddEMIDetailSheet();
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

    private void emiDetails() {
        Call<CreateEMIResponse> call = mApiInterface.getEMIList(
                1, "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call.enqueue(new Callback<CreateEMIResponse>() {
            @Override
            public void onResponse(Call<CreateEMIResponse> call, Response<CreateEMIResponse> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());
                    CreateEMIResponse response1 = response.body();
                    if (response1.getData().size() > 0) {
                        for (Datum Item : response1.getData()) {
                            for (Tradeline tradelinesItem : mCustomData) {
                                if (Item.getAccountNo().equals(tradelinesItem.getAccount_no())) {
                                    tradelinesItem.setInstallment_amount(Item.getInstallmentAmount());
                                    tradelinesItem.setMonth_due_day(Item.getMonthDueDay());
                                    tradelinesItem.setRepayment_tenure(Item.getRepaymentTenure() + "");
                                    tradelinesItem.setInterest_rate(Item.getInterestRate() + "");
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