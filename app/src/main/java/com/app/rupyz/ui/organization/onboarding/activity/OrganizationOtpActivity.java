package com.app.rupyz.ui.organization.onboarding.activity;

import static com.app.rupyz.generic.utils.SharePrefConstant.FCM_TOKEN;
import static com.app.rupyz.generic.utils.SharePrefConstant.IS_EQUI_FAX;
import static com.app.rupyz.generic.utils.SharePrefConstant.IS_LOGIN;
import static com.app.rupyz.generic.utils.SharePrefConstant.NAME;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;
import static com.app.rupyz.generic.utils.SharePrefConstant.USER_INFO;
import static com.app.rupyz.generic.utils.SharePrefConstant.USER_TYPE_FM_EM;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.rupyz.MainActivity;
import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityOrganizationOtpBinding;
import com.app.rupyz.generic.helper.ButtonStyleHelper;
import com.app.rupyz.generic.json.JsonHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.LoginModelForAccess;
import com.app.rupyz.generic.model.UserInfoModel;
import com.app.rupyz.generic.model.user.UserViewModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.SmsBroadcastReceiver;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.generic.utils.Utils;
import com.app.rupyz.sales.organization.ChooseOrganizationActivity;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.aabhasjindal.otptextview.OTPListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizationOtpActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityOrganizationOtpBinding binding;
    private ApiInterface mApiInterface;
    private UserViewModel mUserData;
    private UserInfoModel mUserInfo;
    private Utility mUtil;
    String otpRef;
    SmsBroadcastReceiver smsBroadcastReceiver;
    ActivityResultLauncher<Intent> someActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        binding = ActivityOrganizationOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mUtil = new Utility(this);
        otpRef = getIntent().getExtras().getString("otp_ref");

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String message = result.getData().getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                        getOtpFromMessage(message);
                    } else {
                        Log.e("TAG", "initBio: " + "1Your device doesnt have fingerprint saved,please check your security settings");
                    }
                });

        initLayout();
        initTimer();
        startSmartUserConsent();
        openSomeActivityForResult();
    }

    private void startSmartUserConsent() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        client.startSmsUserConsent(null);
    }

    public void openSomeActivityForResult() {

        smsBroadcastReceiver = new SmsBroadcastReceiver();

        smsBroadcastReceiver.smsBroadcastReceiverListener = new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
            @Override
            public void onSuccess(Intent intent) {
                someActivityResultLauncher.launch(intent);
            }

            @Override
            public void onFailure() {

            }
        };

        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        ContextCompat.registerReceiver(this, smsBroadcastReceiver, intentFilter, ContextCompat.RECEIVER_EXPORTED);
    }

    private void initTimer() {
        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished / 1000 > 0) {
                    binding.counterLayout.setVisibility(View.VISIBLE);
                    binding.txtCounter.setText("" + millisUntilFinished / 1000);
                    binding.btnResend.setVisibility(View.GONE);
                }
            }

            public void onFinish() {
                binding.counterLayout.setVisibility(View.GONE);
                binding.btnResend.setVisibility(View.VISIBLE);
            }

        }.start();
    }

    private void initLayout() {
        binding.txtMobileNumber.setText(getIntent().getExtras().getString("username"));
        binding.btnContinue.setOnClickListener(this);
        binding.btnGoBack.setOnClickListener(this);
        binding.btnResend.setOnClickListener(this);

        binding.txtOtpOne.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
            }

            @Override
            public void onOTPComplete(String otp) {
                Utils.hideKeyboard(OrganizationOtpActivity.this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnContinue:
                initRequest();
                break;
            case R.id.btnGoBack:
                finish();
                break;
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.btnResend:
                initTimer();
                resendOtp(getIntent().getExtras().getString("username"), "SARE360");
                break;
        }
    }

    private void initRequest() {
        if (binding.txtOtpOne.getOTP().equalsIgnoreCase("")) {
            Toast.makeText(this, "OTP Required", Toast.LENGTH_SHORT).show();
        } else if (binding.txtOtpOne.getOTP().length() == 4) {
            new ButtonStyleHelper(this).initButton(false, binding.btnContinue, "Please wait...");
            doNetworkRequest(getIntent().getExtras().getString("username"), "SARE360",
                    binding.txtOtpOne.getOTP(), otpRef);
        } else {
            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
        }

    }

    private void doNetworkRequest(String username, String access_type, String otp, String otp_ref) {
        final LoginModelForAccess login = new LoginModelForAccess(username, access_type, otp, otp_ref, true, true, true);
        Call<String> call1 = mApiInterface.otpVerify(login);
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                new ButtonStyleHelper(OrganizationOtpActivity.this)
                        .initButton(true, binding.btnContinue, "Continue");
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body());
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    Gson gson = new Gson();
                    mUserData = gson.fromJson(jsonObj.get("data"), UserViewModel.class);

                    SharedPref.getInstance().putModelClass(USER_INFO, mUserData);
                    SharedPref.getInstance().putString(TOKEN, mUserData.getCredentials().getAccess_token());
                    SharedPref.getInstance().putString(USER_TYPE_FM_EM, mUserData.getExperian_report_info().getUser_type());
                    SharedPref.getInstance().putString(NAME, mUserData.getFirst_name() + " " + mUserData.getLast_name());

                    initFCM();

                    if (mUserData.isEquifax_generated()) {
                        SharedPref.getInstance().putBoolean(IS_EQUI_FAX, true);
                        if (mUserData.getOrgIds().size() > 0) {
                            Intent intent = new Intent(OrganizationOtpActivity.this, ChooseOrganizationActivity.class);
                            intent.putExtra("username", getIntent().getExtras().getString("username"));
                            intent.putExtra("data", response.body());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(OrganizationOtpActivity.this, BusinessDetailsActivity.class);
                            intent.putExtra("org_type", getIntent().getExtras().getString("org_type"));
                            intent.putExtra("username", getIntent().getExtras().getString("username"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    } else {
                        SharedPref.getInstance().putBoolean(IS_EQUI_FAX, false);
                        if (mUserData.isExperian_generated() && mUserData.getOrgIds().size() == 0) {
                            SharedPref.getInstance().putBoolean(IS_LOGIN, true);
                            Intent intent = new Intent(OrganizationOtpActivity.this, MainActivity.class);
                            intent.putExtra("org_type", getIntent().getExtras().getString("org_type"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else if (mUserData.getOrgIds().size() > 0) {
                            Intent intent = new Intent(OrganizationOtpActivity.this, ChooseOrganizationActivity.class);
                            intent.putExtra("username", getIntent().getExtras().getString("username"));
                            intent.putExtra("data", response.body());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(OrganizationOtpActivity.this, BusinessDetailsActivity.class);
                            intent.putExtra("username", getIntent().getExtras().getString("username"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }

                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        new SessionHelper(OrganizationOtpActivity.this).requestErrorMessage(
                                responseBody,
                                findViewById(android.R.id.content));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                new ButtonStyleHelper(OrganizationOtpActivity.this)
                        .initButton(true, binding.btnContinue, "Continue");
                call.cancel();
            }
        });
    }

    private void resendOtp(String userId, String access_type) {
        new ButtonStyleHelper(OrganizationOtpActivity.this).initButton(false, binding.btnContinue,
                "Continue");
        final LoginModelForAccess login = new LoginModelForAccess(userId, access_type, "", "", false, false, false);
        Call<String> call1 = mApiInterface.loginUser(login);
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    new ButtonStyleHelper(OrganizationOtpActivity.this)
                            .initButton(true, binding.btnContinue, "Continue");
                    Logger.errorLogger(this.getClass().getName(), response.body() + "");
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    Gson gson = new Gson();
                    mUserInfo = gson.fromJson(jsonObj.get("data"), UserInfoModel.class);
                    otpRef = mUserInfo.getOtp_ref();
                    Log.e("APIResponse", "onResponse: " + mUserInfo.getOtp_ref());
                } else {
                    try {
                        String responseBody = response.errorBody().string();
                    } catch (Exception ex) {
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                call.cancel();
                new ButtonStyleHelper(OrganizationOtpActivity.this)
                        .initButton(true, binding.btnContinue, "Continue");
            }
        });
    }

    private void initFCM() {
        Logger.errorLogger("DEBUG", "INIT FCM REQUEST " +  SharedPref.getInstance().getString(FCM_TOKEN));
        Call<String> call1 = mApiInterface.saveFcmToken(JsonHelper.getFCMJson("Android",
                        mUtil.getDeviceManufacturer(), mUtil.getOS(), mUtil.getDeviceName(),
                        SharedPref.getInstance().getString(FCM_TOKEN)),
                "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    Logger.errorLogger("DEBUG", response.body());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                call.cancel();
            }
        });
    }


   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_USER_CONSENT) {
            if ((resultCode == RESULT_OK) && (data != null)) {
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                getOtpFromMessage(message);
            }
        }
    }*/

    private void getOtpFromMessage(String message) {
        Pattern otpPattern = Pattern.compile("(|^)\\d{4}");
        Matcher matcher = otpPattern.matcher(message);
        if (matcher.find()) {
            if (message.contains("RUPYZ")) {
                try {
                    binding.txtOtpOne.setOTP(matcher.group(0));
                    binding.btnContinue.performClick();
                } catch (Exception exception) {

                }
            }
        } else {
            try {
                binding.txtOtpOne.setOTP("");
            } catch (Exception exception) {

            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        openSomeActivityForResult();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadcastReceiver);
    }
}