package com.app.rupyz.ui.individual.em;

import static com.app.rupyz.generic.utils.SharePrefConstant.IS_SKIP_GSTIN;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.rupyz.MainActivity;
import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivitySmartMatchBinding;
import com.app.rupyz.generic.helper.ButtonStyleHelper;
import com.app.rupyz.generic.json.JsonHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.individual.ExpInfoModel;
import com.app.rupyz.generic.model.user.UserViewModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.toast.MessageHelper;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.account.dailog.ExperianConsentModal;
import com.app.rupyz.ui.equifax.EquiFaxMainActivity;
import com.app.rupyz.ui.individual.experian.OtpVerifyActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SmartMatchActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivitySmartMatchBinding binding;
    private UserViewModel mData;
    private ApiInterface mApiInterface;
    private Utility mUtil;
    private ExpInfoModel mExpData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySmartMatchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        initLayout();
        initData();
        getProfileData();
    }

    private void initLayout() {
        binding.btnTerms.setOnClickListener(this);
        binding.btnNext.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                initProfile();
                break;
            case R.id.btn_terms:
                initBottomSheet();
                break;

        }
    }


    public void initBottomSheet() {
        Bundle bundle = new Bundle();
        ExperianConsentModal fragment = new ExperianConsentModal();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "");
    }

    private void initProfile() {
        if (binding.txtName.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Name Required!", Toast.LENGTH_SHORT).show();
        } else if (binding.txtEmail.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Email Required!", Toast.LENGTH_SHORT).show();
        } else if (!binding.chbConsent.isChecked()) {
            new MessageHelper().initMessage(getResources().getString(R.string.experian_consent_validation),
                    findViewById(android.R.id.content));
        } else {
            new ButtonStyleHelper(this).initButton(false, binding.btnNext, "Please wait...");
            updateProfile();
        }
    }


    private void initExperian() {

        Call<String> call1 = mApiInterface.initExperian("Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Logger.errorLogger(this.getClass().getName(), response.code() + "");
                new ButtonStyleHelper(SmartMatchActivity.this).initButton(true, binding.btnNext,
                        "Next");
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body());
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    Gson gson = new Gson();
                    mExpData = gson.fromJson(jsonObj.get("data"), ExpInfoModel.class);
                    Intent intent;
                    if (mExpData.getStep() == 0) {
                        intent = new Intent(SmartMatchActivity.this, OtpVerifyActivity.class);
                    } else {
                        if (SharedPref.getInstance().getBoolean(IS_SKIP_GSTIN, false)) {
                            intent = new Intent(SmartMatchActivity.this, EquiFaxMainActivity.class);
                        } else {
                            intent = new Intent(SmartMatchActivity.this, MainActivity.class);
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    }
                    intent.putExtra("session_id", mExpData.getExp_data().getSession_id());
                    intent.putExtra("id", mExpData.getId());
                    startActivity(intent);
                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        Logger.errorLogger(this.getClass().getName(), responseBody + "");
                        new SessionHelper(SmartMatchActivity.this).requestErrorMessage(
                                responseBody,
                                findViewById(android.R.id.content));
                    } catch (Exception ex) {
                    }
                }
                try {
                    Logger.errorLogger(this.getClass().getName(), response.errorBody().string() + "");
                } catch (Exception ex) {
                    Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
                }
                Logger.errorLogger(this.getClass().getName(), response.body() + "");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                new ButtonStyleHelper(SmartMatchActivity.this)
                        .initButton(true, binding.btnNext, "Next");
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    private void initData() {
        try {
            Logger.errorLogger(this.getClass().getName(), getIntent().getExtras().getString("data"));
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObj = (JsonObject) jsonParser.parse(getIntent().getExtras().getString("data"));
            Gson gson = new Gson();
            mData = gson.fromJson(jsonObj.get("data"), UserViewModel.class);
            if (mData.getFirst_name().equalsIgnoreCase("")) {
                binding.txtName.setEnabled(true);
            } else {
                binding.txtName.setText(mData.getFirst_name() + " " + mData.getMiddle_name() + " " + mData.getLast_name());
                binding.txtName.setEnabled(false);
            }
        } catch (Exception ex) {
            binding.txtName.setEnabled(true);
        }
    }


    private void getProfileData() {
        Logger.errorLogger("Profile", SharedPref.getInstance().getString(TOKEN));
        Call<String> call1 = mApiInterface.getReviewData("Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body());
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    Gson gson = new Gson();
                    mData = gson.fromJson(jsonObj.get("data"), UserViewModel.class);
                    String full_name = mData.getFirst_name() + " " + mData.getMiddle_name() + " " + mData.getLast_name();
                    binding.txtName.setText(full_name.trim());
                    binding.txtEmail.setText(mData.getEmail().trim());
                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        new SessionHelper(SmartMatchActivity.this).requestErrorMessage(
                                responseBody,
                                findViewById(android.R.id.content));
                    } catch (Exception ex) {
                    }
                }
                try {
                    Logger.errorLogger(this.getClass().getName(), response.errorBody().string() + "");
                } catch (Exception ex) {
                    Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
                }
                Logger.errorLogger(this.getClass().getName(), response.code() + "");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    private void updateProfile() {
        Call<String> call1 = mApiInterface.profileUpdate(
                JsonHelper.getSmartMatch(binding.txtName.getText().toString(),
                        "", binding.txtEmail.getText().toString().trim(),
                        "",
                        true), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                new ButtonStyleHelper(SmartMatchActivity.this)
                        .initButton(true, binding.btnNext, "Next");
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body());
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    Gson gson = new Gson();
                    mData = gson.fromJson(jsonObj.get("data"), UserViewModel.class);
                    initExperian();
                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        Logger.errorLogger(this.getClass().getName(), responseBody);
                        new SessionHelper(SmartMatchActivity.this).requestErrorMessage(
                                responseBody,
                                findViewById(android.R.id.content));
                    } catch (Exception ex) {
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                new ButtonStyleHelper(SmartMatchActivity.this)
                        .initButton(true, binding.btnNext, "Next");
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }
}