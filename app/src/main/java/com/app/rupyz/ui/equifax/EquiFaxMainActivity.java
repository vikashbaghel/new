package com.app.rupyz.ui.equifax;

import static com.app.rupyz.generic.utils.SharePrefConstant.NAME;
import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityEquiFaxMainBinding;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.helper.StringHelper;
import com.app.rupyz.generic.helper.UpdateManager;
import com.app.rupyz.generic.logger.FirebaseLogger;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.createemi.CreateEMIResponse;
import com.app.rupyz.generic.model.createemi.Datum;
import com.app.rupyz.generic.model.organization.TradelinesItem;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.sales.home.SalesFragment;
import com.app.rupyz.ui.organization.profile.MyBusinessActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquiFaxMainActivity extends AppCompatActivity implements
        View.OnClickListener {

    private BottomNavigationView mBottomNavigationBar;
    private ConstraintLayout container;
    boolean doubleBackToExitPressedOnce = false;
    private ActivityEquiFaxMainBinding binding;
    private Utility mUtil;
    private EquiFaxApiInterface mEquiFaxApiInterface;
    private ApiInterface mApiInterface;
    private EquiFaxReportHelper mReportHelper;
    public static List<TradelinesItem> mCustomData;

    private UpdateManager appUpdateManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new FirebaseLogger(this).sendLog("Home Activity", "Home Activity");

        appUpdateManager = UpdateManager.Builder(this);
        appUpdateManager.checkUpdate();

        mUtil = new Utility(this);
        binding = ActivityEquiFaxMainBinding.inflate(getLayoutInflater());
        mEquiFaxApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface.class);
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        mReportHelper = EquiFaxReportHelper.getInstance();
        setContentView(binding.getRoot());
//        getCommercialReport();
        initLayout();
        initPrefix(SharedPref.getInstance().getString(NAME));
    }


    private void initLayout() {
//        binding.navHeaderView.btnChat.setOnClickListener(this);
//        binding.navHeaderView.btnNotification.setOnClickListener(this);
//        mMenuProfile = findViewById(R.id.btn_profile);
//        mMenuProfile.setOnClickListener(this);
//        mBottomNavigationBar = findViewById(R.id.bottomNavigationView);
//        mBottomNavigationBar.setOnNavigationItemSelectedListener(this);
        container = findViewById(R.id.container);

//        if (!SharedPref.getInstance().getBoolean(IS_EQUI_FAX)) {
//            mBottomNavigationBar.getMenu().getItem(1).setVisible(false);
//        }

        loadFragment(new SalesFragment());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_profile:
                startActivity(new Intent(this, MyBusinessActivity.class));
                break;
        }
    }

    public void initPrefix(String userName) {
        try {
            binding.navHeaderView.userPrefix.setText(StringHelper.printName(userName).trim().substring(0, 1));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (getVisibleFragment() instanceof SalesFragment) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Snackbar snackbar = Snackbar
                    .make(container, getResources().getString(R.string.alert_press_again), Snackbar.LENGTH_SHORT);
            snackbar.show();

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            loadFragment(new SalesFragment());
//            mBottomNavigationBar.setSelectedItemId(R.id.navigation_my_business);
        }
    }

    private Fragment getVisibleFragment() {
        FragmentManager fragmentManager = EquiFaxMainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }

    private void getCommercialReport() {
        Call<String> call1 = mEquiFaxApiInterface.getReport(getResources().getString(R.string.COMMERCIAL),
                SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    int responseCode = response.code();
                    if (response.isSuccessful()) {
                        if (responseCode == 200) {
                            initCommercialData(response.body());
                        } else if (responseCode == 202) {

                        }
                    } else {
                        if (responseCode == 403) {
                            mUtil.logout();
                        }
                    }
                    try {
                        Logger.errorLogger(this.getClass().getName(), response.errorBody().string() + "");
                    } catch (Exception ex) {
                        Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
                    }
                    Logger.errorLogger(this.getClass().getName(), response.code() + "");
                } catch (Exception ex) {
//
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    private void initCommercialData(String response) {
        mReportHelper.setEquiFaxCommercial(response);
        getIndividualReport();
    }

    private void getIndividualReport() {
        Logger.errorLogger("Token", SharedPref.getInstance().getString(TOKEN));
        Call<String> call1 = mEquiFaxApiInterface.getReport(getResources().getString(R.string.RETAIL), SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    Logger.errorLogger("Retailer Response Code", "");
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            Logger.errorLogger("Retailer Response", response.body() + "");
                            initRetailData(response.body());
                        }
                    } else {
                        if (response.code() == 403) {
                            mUtil.logout();
                        } else if (response.code() == 500) {
                        }
                    }
                    try {
                        Logger.errorLogger(this.getClass().getName(), response.errorBody().string() + "");
                    } catch (Exception ex) {
                        Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
                    }
                    Logger.errorLogger(this.getClass().getName(), response.code() + "");
                } catch (Exception ex) {
//
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    private void initRetailData(String response) {
        mReportHelper.setEquiFaxRetail(response);
        commercialEMIDetails();
    }

    private void commercialEMIDetails() {
        mCustomData = new ArrayList<>();
        Call<CreateEMIResponse> call = mEquiFaxApiInterface.getEMIList(
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

    @Override
    protected void onResume() {
        super.onResume();

    }
}