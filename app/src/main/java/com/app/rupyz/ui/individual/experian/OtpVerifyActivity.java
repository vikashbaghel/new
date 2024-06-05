package com.app.rupyz.ui.individual.experian;

import static com.app.rupyz.generic.utils.SharePrefConstant.IS_LOGIN;
import static com.app.rupyz.generic.utils.SharePrefConstant.IS_SKIP_GSTIN;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;
import static com.app.rupyz.generic.utils.SharePrefConstant.USER_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.USER_TYPE_FM_EM;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.app.rupyz.MainActivity;
import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityOtpVerifyBinding;
import com.app.rupyz.generic.helper.ButtonStyleHelper;
import com.app.rupyz.generic.json.JsonHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.individual.ExpInfoModel;
import com.app.rupyz.generic.model.user.UserViewModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.equifax.EquiFaxMainActivity;
import com.app.rupyz.ui.organization.EquiFaxOtpActivity;
import com.app.rupyz.ui.organization.PANVerifyActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerifyActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityOtpVerifyBinding binding;
    private ApiInterface mApiInterface;
    private UserViewModel mUserData;
    private Utility mUtil;
    private ExpInfoModel mExpData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        binding = ActivityOtpVerifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mUtil = new Utility(this);
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
                OtpVerifyActivity.this.finish();
                break;
        }
    }

    private void initRequest() {
        if (binding.txtOtp.getText().toString().equalsIgnoreCase("")) {
            binding.txtOtp.setError("OTP Required.");
        } else if (binding.txtOtp.getText().toString().length() == 6) {
            new ButtonStyleHelper(this)
                    .initButton(false, binding.btnContinue, "Please wait...");
            doNetworkRequest(binding.txtOtp.getText().toString().trim());
        } else {
            binding.txtOtp.setError("Invalid OTP.");
        }
    }

    private void doNetworkRequest(String otp) {
        Call<String> call1 = mApiInterface.generateExperian(JsonHelper.getGenerateExpJson(
                getIntent().getExtras().getString("session_id"),
                getIntent().getExtras().getInt("id"), otp), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    new ButtonStyleHelper(OtpVerifyActivity.this)
                            .initButton(true, binding.btnContinue, "Continue");
                    Logger.errorLogger(this.getClass().getName(), response.code() + "");
                    if (response.code() == 200) {
                        Logger.errorLogger(this.getClass().getName(), response.body());
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                        Gson gson = new Gson();
                        SharedPref.getInstance().putBoolean(IS_LOGIN, true);
                        if (SharedPref.getInstance().getBoolean(IS_SKIP_GSTIN, false)) {
                            Intent intent = new Intent(OtpVerifyActivity.this, EquiFaxMainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(OtpVerifyActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    } else {
                        try {
                            String responseBody = response.errorBody().string();
                            Logger.errorLogger(this.getClass().getName(), responseBody + "");
                            JsonParser jsonParser = new JsonParser();
                            JsonObject jsonObj = (JsonObject) jsonParser.parse(responseBody);
                            Gson gson = new Gson();
                            mExpData = gson.fromJson(jsonObj.get("data"), ExpInfoModel.class);
                            //this is for the crq (step 2)
                            if (mExpData.getStep() == 2) {
                                Intent intent = new Intent(OtpVerifyActivity.this, MaskedNumberActivity.class);
                                intent.putExtra("session_id", mExpData.getExp_data().getSession_id());
                                startActivity(intent);
                            } else if (mExpData.getStep() == -3) {
                                SharedPref.getInstance().putString(USER_TYPE_FM_EM, "FM");
                                Intent intent = new Intent(OtpVerifyActivity.this, PANVerifyActivity.class);
                                SharedPref.getInstance().putString(USER_TYPE, getResources().getString(R.string.individual));
                                startActivity(intent);
                            }
                            Logger.errorLogger(this.getClass().getName(), responseBody);

                        } catch (Exception ex) {
                            Logger.errorLogger(this.getClass().getName(), ex.getMessage());
                        }
                    }
                } catch (Exception ex) {
                    Logger.errorLogger(this.getClass().getName(), ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                new ButtonStyleHelper(OtpVerifyActivity.this)
                        .initButton(true, binding.btnContinue, "Continue");
                OtpVerifyActivity.this.finish();
            }
        });
    }
}