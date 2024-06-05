package com.app.rupyz.ui.organization.onboarding.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityMobileNumberBinding;
import com.app.rupyz.generic.base.BrowserActivity;
import com.app.rupyz.generic.helper.ButtonStyleHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.LoginModelForAccess;
import com.app.rupyz.generic.model.UserInfoModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.toast.MessageHelper;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.Utils;
import com.app.rupyz.generic.utils.Validations;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileNumberActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMobileNumberBinding binding;
    private ApiInterface mApiInterface;
    private UserInfoModel mUserData;
    private Context mContext;
    private Resources mResources;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMobileNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        initLayout();
        requestsmspermission();

        Bundle params = new Bundle();
        params.putString("mobile_number_screen", "open mobile number screen");
        mFirebaseAnalytics.logEvent("mobile_number", params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                if (new Validations().mobileNumberValidation(binding.txtMobileNumber.getText().toString())) {
                    if (binding.chbTerms.isChecked()) {
                        new ButtonStyleHelper(this).initButton(false, binding.btnLogin,
                                "Please wait...");
                        doLogin(binding.txtMobileNumber.getText().toString(), "SARE360", binding.chbWhatsapp.isChecked());
                    } else {
                        new MessageHelper().initMessage(getResources().getString(R.string.terms_conditions_validation),
                                findViewById(android.R.id.content));
                    }
                } else {
                    new MessageHelper().initMessage(getResources().getString(R.string.mobile_number_validation),
                            findViewById(android.R.id.content));
                }
                break;
            case R.id.btn_terms:
                initOpenBrowser(ApiClient.TERMS_URL, "Terms of Service");
                break;
            case R.id.btn_policy:
                initOpenBrowser(ApiClient.POLICY_URL, "Privacy Policy");
                break;
            case R.id.img_back:
                onBackPressed();
                break;
        }
    }

    private void initOpenBrowser(String url, String title) {
        Intent intent = new Intent(this, BrowserActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    private void doLogin(String userId, String access_type, boolean isWhatsApp) {
        Bundle params = new Bundle();
        params.putString("enter_mobile_number", "get otp api hit");
        mFirebaseAnalytics.logEvent("mobile_number", params);

        final LoginModelForAccess login = new LoginModelForAccess(userId, access_type, "", "", true, isWhatsApp, true);
        Log.e("Login", "" + new Gson().toJson(login));
        Call<String> call1 = mApiInterface.loginUser(login);
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                new ButtonStyleHelper(MobileNumberActivity.this).initButton(true, binding.btnLogin, "Continue");
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body() + "");
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    Gson gson = new Gson();
                    mUserData = gson.fromJson(jsonObj.get("data"), UserInfoModel.class);

                    Bundle params = new Bundle();
                    params.putString("get_otp_ref", mUserData.getOtp_ref());
                    mFirebaseAnalytics.logEvent("mobile_number", params);

                    Intent intent = new Intent(MobileNumberActivity.this, OrganizationOtpActivity.class);
                    intent.putExtra("otp_ref", mUserData.getOtp_ref());
                    intent.putExtra("username", binding.txtMobileNumber.getText().toString());
                    startActivity(intent);
                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        new SessionHelper(MobileNumberActivity.this).requestErrorMessage(
                                responseBody,
                                findViewById(android.R.id.content));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                call.cancel();
                new ButtonStyleHelper(MobileNumberActivity.this).initButton(true, binding.btnLogin, "Continue");
            }
        });
    }

    private void requestsmspermission() {
        String smspermission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, smspermission);
        //check if read SMS permission is granted or not
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = smspermission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    private void initLayout() {
        binding.btnLogin.setOnClickListener(this);
        binding.btnPolicy.setOnClickListener(this);
        binding.btnTerms.setOnClickListener(this);
        binding.chbWhatsapp.setChecked(true);

        binding.txtMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty() && charSequence.toString().length() == 10){
                    Utils.hideKeyboard(MobileNumberActivity.this);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}