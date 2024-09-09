package com.app.rupyz.ui.organization;

import static com.app.rupyz.generic.utils.SharePrefConstant.IS_LOGIN;
import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityEquiFaxResWaitingBinding;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.toast.MessageHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquiFaxResWaitingActivity extends AppCompatActivity {

    ActivityEquiFaxResWaitingBinding binding;
    private Handler mHandler = new Handler();
    private final static int INTERVAL = 20000; //30 seconds
    private boolean isRequested = false;
    private EquiFaxApiInterface mApiInterface;
    private boolean doubleBackToExitPressedOnce = false;
    private Utility mUtil;
    private EquiFaxReportHelper mReportHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEquiFaxResWaitingBinding.inflate(getLayoutInflater());
        mApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface.class);
        mReportHelper = EquiFaxReportHelper.getInstance();
        setContentView(binding.getRoot());
        mUtil = new Utility(this);
        initLayout();
    }

    private void initLayout() {
        startRepeatingTask();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!isRequested) {
                isRequested = true;
                getCommercialReport();
                mHandler.postDelayed(runnable, INTERVAL);
            }
        }
    };

    void startRepeatingTask() {
        mHandler.post(runnable);
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(runnable);
    }

    private void getCommercialReport() {
        Logger.errorLogger("Profile", SharedPref.getInstance().getString(TOKEN));
        Logger.errorLogger("Profile", SharedPref.getInstance().getInt(ORG_ID) + "");
        Call<String> call1 = mApiInterface.getReport(getResources().getString(R.string.COMMERCIAL),
                SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            mReportHelper.setEquiFaxCommercial(response.body());
                            if (!(mReportHelper.getCommercialReport().getMetadata().getCommercial_progress_step() + "").equalsIgnoreCase("0.0")) {
                                stopRepeatingTask();
                                SharedPref.getInstance().putBoolean(IS_LOGIN, true);
                                SharedPref.getInstance().putInt(ORG_ID, mReportHelper.getOrgId());
//                                Intent intent = new Intent(EquiFaxResWaitingActivity.this, EquiFaxMainActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intent);
                            } else {
                                isRequested = false;
                                startRepeatingTask();
                            }
                        }
                    } else {
                        if (response.code() == 403) {
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
                    isRequested = false;
                    startRepeatingTask();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                isRequested = false;
                startRepeatingTask();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        new MessageHelper().initMessage(getResources().getString(R.string.alert_press_again),
                findViewById(android.R.id.content));
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}