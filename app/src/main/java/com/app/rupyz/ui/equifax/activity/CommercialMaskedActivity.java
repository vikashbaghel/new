package com.app.rupyz.ui.equifax.activity;

import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityCommercialMaskedBinding;
import com.app.rupyz.generic.json.JsonHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.organization.MaskedPhoneInfoModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.toast.MessageHelper;
import com.app.rupyz.generic.utils.AppConstant;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.generic.utils.Validations;
import com.app.rupyz.ui.organization.EquiFaxOtpActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommercialMaskedActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityCommercialMaskedBinding binding;
    private EquiFaxApiInterface mApiInterface;
    private boolean is_retail_masked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommercialMaskedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface.class);
        is_retail_masked = getIntent().getExtras().getBoolean(AppConstant.IS_RETAIL_MASKED);
        initLayout();
        initToolbar();
    }

    private void initLayout() {
        initMaskedMobile();
        binding.btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                validation();
                break;
        }
    }

    private void validation() {
        if (new Validations().mobileNumberValidation(binding.txtMobileNumber.getText().toString().trim())) {
            initOtp(binding.txtMobileNumber.getText().toString().trim());
        } else {
            new MessageHelper().initMessage(getResources().getString(R.string.mobile_number_validation),
                    findViewById(android.R.id.content));
        }
    }

    private void initMaskedMobile() {
        Call<String> call1 = mApiInterface.getCommercialMaskedMobile(SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Logger.errorLogger(this.getClass().getName(), response.code() + "");
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body());
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    Gson gson = new Gson();
                    JsonObject dataObj = jsonObj.getAsJsonObject("data");
                    MaskedPhoneInfoModel maskedPhoneInfoModel
                            = gson.fromJson(dataObj.get("response"), MaskedPhoneInfoModel.class);
                    if (is_retail_masked) {
                        if (maskedPhoneInfoModel.getRetail_masked_phone_list().size() > 0) {
                            String masked = "Masked Phone:- ";
                            for (String phone :
                                    maskedPhoneInfoModel.getRetail_masked_phone_list()) {
                                masked += phone + ",  ";

                            }
                            binding.txtMessage.setText(masked);
                        }
                    } else {
                        if (maskedPhoneInfoModel.getMasked_phone_list().size() > 0) {
                            String masked = "Masked Phone:- ";
                            for (String phone :
                                    maskedPhoneInfoModel.getMasked_phone_list()) {
                                masked += phone + ",  ";

                            }
                            binding.txtMessage.setText(masked);
                        }
                    }
                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        Logger.errorLogger(this.getClass().getName(), responseBody);
                        new SessionHelper(CommercialMaskedActivity.this).requestErrorMessage(
                                responseBody,
                                findViewById(android.R.id.content));
                    } catch (Exception ex) {
                        Logger.errorLogger(this.getClass().getName(), ex.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void initOtp(String correctMobile) {
        Call<String> call1 = mApiInterface.postCommercialMaskedMobile(
                JsonHelper.getCommercialMaskedPhoneJson(SharedPref.getInstance().getInt(ORG_ID), correctMobile), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Logger.errorLogger(this.getClass().getName(), response.code() + "");
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body());
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    JsonObject dataObj = jsonObj.getAsJsonObject("data");
                    new SessionHelper(CommercialMaskedActivity.this).initMessage(
                            jsonObj.get("message").getAsString(),
                            findViewById(android.R.id.content));
                    Intent intent = new Intent(CommercialMaskedActivity.this,
                            EquiFaxOtpActivity.class);
                    intent.putExtra("otp_ref", dataObj.get("otp_ref").getAsString());
                    intent.putExtra("is_otp", true);
                    intent.putExtra(AppConstant.IS_RETAIL_MASKED, is_retail_masked);
                    startActivity(intent);

                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        Logger.errorLogger(this.getClass().getName(), responseBody);
                        new SessionHelper(CommercialMaskedActivity.this).requestErrorMessage(
                                responseBody,
                                findViewById(android.R.id.content));
                    } catch (Exception ex) {
                        Logger.errorLogger(this.getClass().getName(), ex.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void initToolbar() {
        // using toolbar as ActionBar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.confirm_your_mobile_number));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}