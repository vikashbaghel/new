package com.app.rupyz.ui.organization;

import static com.app.rupyz.generic.utils.SharePrefConstant.IS_LOGIN;
import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityEquiFaxOtpBinding;
import com.app.rupyz.generic.helper.ButtonStyleHelper;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.json.JsonHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.organization.EquiFaxOtpInfoModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.utils.AppConstant;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.equifax.EquiFaxMainActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquiFaxOtpActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityEquiFaxOtpBinding binding;
    private EquiFaxApiInterface mApiInterface;
    private EquiFaxReportHelper mReportHelper;
    private Utility mUtil;
    private EquiFaxOtpInfoModel mData;
    private boolean isOtp = false;
    private boolean is_retail_masked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface.class);
        binding = ActivityEquiFaxOtpBinding.inflate(getLayoutInflater());
        mReportHelper = EquiFaxReportHelper.getInstance();
        setContentView(binding.getRoot());
        mUtil = new Utility(this);
        if (getIntent().hasExtra(AppConstant.IS_RETAIL_MASKED)) {
            is_retail_masked = getIntent().getExtras().getBoolean(AppConstant.IS_RETAIL_MASKED);
        }
        isOtp = getIntent().getExtras().getBoolean("is_otp");
        if (!isOtp) {
            initEquiFaxOtp();
        }
        initLayout();
    }

    private void initLayout() {
        binding.btnContinue.setOnClickListener(this);
        binding.btnGoBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnContinue:
                initRequest();
                break;
            case R.id.btnGoBack:
                EquiFaxOtpActivity.this.finish();
                break;
        }
    }

    private void initRequest() {
        if (binding.txtOtp.getOTP().equalsIgnoreCase("")) {
            Toast.makeText(this, "OTP Required", Toast.LENGTH_SHORT).show();
        } else if (binding.txtOtp.getOTP().length() == 4) {
            new ButtonStyleHelper(this).initButton(false, binding.btnContinue,
                    "Please wait...");
            doNetworkRequest(binding.txtOtp.getOTP().trim());
        } else {
            Toast.makeText(this, "Invalid OTP.", Toast.LENGTH_SHORT).show();
        }
    }

    private void doNetworkRequest(String otp) {
        Call<String> call1;
        if (isOtp) {
            call1 = mApiInterface.checkEquiFaxOtp(JsonHelper.getEquiFaxOtpJson(
                    SharedPref.getInstance().getInt(ORG_ID), otp,
                    getIntent().getExtras().getString("otp_ref"), is_retail_masked), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        } else {
            call1 = mApiInterface.checkEquiFaxOtp(JsonHelper.getEquiFaxOtpJson(
                    mReportHelper.getOrgId(), otp, mData.getOtp_ref(), is_retail_masked), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        }
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                new ButtonStyleHelper(EquiFaxOtpActivity.this)
                        .initButton(true, binding.btnContinue, "Continue");
                if (response.code() == 200) {
                    if (is_retail_masked) {
                        mUtil.logout();
                    } else {
                        SharedPref.getInstance().putBoolean(IS_LOGIN, true);
                        SharedPref.getInstance().putInt(ORG_ID, mReportHelper.getOrgId());

                        Intent intent = new Intent(EquiFaxOtpActivity.this, EquiFaxMainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } else {
                    try {
                        Logger.errorLogger(this.getClass().getName(), "failed");
                        String responseBody = response.errorBody().string();
                        Logger.errorLogger(this.getClass().getName(), responseBody, response.code());
                        new SessionHelper(EquiFaxOtpActivity.this).requestErrorMessage(
                                responseBody,
                                findViewById(android.R.id.content));
//                        EquiFaxOtpActivity.this.finish();
                    } catch (Exception ex) {
                        Logger.errorLogger(this.getClass().getName(), "equifax error "
                                + ex.getMessage());
//                        EquiFaxOtpActivity.this.finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                new ButtonStyleHelper(EquiFaxOtpActivity.this).initButton(true, binding.btnContinue,
                        "Continue");
                Intent intent = new Intent(EquiFaxOtpActivity.this, EquiFaxResWaitingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });
    }

    private void initEquiFaxOtp() {
        Call<String> call1 = mApiInterface.initEquiFaxOtp(mReportHelper.getOrgId(), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body(), response.code());
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    Gson gson = new Gson();
                    mData = gson.fromJson(jsonObj.get("data"), EquiFaxOtpInfoModel.class);
                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        Logger.errorLogger(this.getClass().getName(), responseBody, response.code());
                        new SessionHelper(EquiFaxOtpActivity.this).requestErrorMessage(
                                responseBody,
                                findViewById(android.R.id.content));
                    } catch (Exception ex) {
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }
}