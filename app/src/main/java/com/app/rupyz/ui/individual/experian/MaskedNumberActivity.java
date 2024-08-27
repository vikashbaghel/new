package com.app.rupyz.ui.individual.experian;

import static com.app.rupyz.generic.utils.SharePrefConstant.IS_LOGIN;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.app.rupyz.MainActivity;
import com.app.rupyz.databinding.ActivityMaskedNumberBinding;
import com.app.rupyz.R;
import com.app.rupyz.generic.helper.ButtonStyleHelper;
import com.app.rupyz.generic.json.JsonHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.individual.ExpInfoModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.toast.MessageHelper;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.generic.utils.Validations;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaskedNumberActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMaskedNumberBinding binding;
    private ApiInterface mApiInterface;
    private Utility mUtil;
    private String session_id = "";
    private ExpInfoModel mExpData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        binding = ActivityMaskedNumberBinding.inflate(getLayoutInflater());
        mUtil = new Utility(this);
        setContentView(binding.getRoot());
        session_id = getIntent().getExtras().getString("session_id");
        initLayout();
    }

    private void initLayout() {
        binding.btnNext.setOnClickListener(this);
        doMessageRequired();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                initRequest();
                break;
        }
    }

    private void initRequest() {
        if (!new Validations().mobileNumberValidation(binding.txtMobileNumber.getText().toString())) {
            new MessageHelper().initMessage(getResources().getString(R.string.mobile_number_validation),
                    findViewById(android.R.id.content));
        } else if (binding.txtEmail.getText().toString().equalsIgnoreCase("")) {
            new MessageHelper().initMessage("Email Required!",
                    findViewById(android.R.id.content));
        } else {
            new ButtonStyleHelper(this).initButton(false, binding.btnNext,
                    "Please wait...");
            doNetworkRequest(binding.txtMobileNumber.getText().toString(),
                    binding.txtEmail.getText().toString());
        }
    }

    private void doMessageRequired() {
        Call<String> call1 = mApiInterface.generateStep2(JsonHelper.getGenerateExpStep2Json(
                session_id), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Logger.errorLogger(this.getClass().getName(), response.code() + "");
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body());
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    Gson gson = new Gson();
                    mExpData = gson.fromJson(jsonObj.get("data"), ExpInfoModel.class);
                    session_id = mExpData.getExp_data().getSession_id();
                    binding.txtMessage.setText(jsonObj.get("message").getAsString());
                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        Logger.errorLogger(this.getClass().getName(), responseBody);
                        new SessionHelper(MaskedNumberActivity.this).requestErrorMessage(
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

    private void doNetworkRequest(String mobileNumber, String email) {
        Call<String> call1 = mApiInterface.generateStep2Point1(JsonHelper.getGenerateExpJson2Point1(
                session_id,
                mobileNumber, email), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                new ButtonStyleHelper(MaskedNumberActivity.this)
                        .initButton(true, binding.btnNext, "Next");
                Logger.errorLogger(this.getClass().getName(), response.code() + "////");

                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body());
                    SharedPref.getInstance().putBoolean(IS_LOGIN, true);
                    Intent intent = new Intent(MaskedNumberActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (response.code() == 201) {
                    Logger.errorLogger(this.getClass().getName(), response.body());
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    initMobileExits(jsonObj.get("message").getAsString());
                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        Logger.errorLogger(this.getClass().getName(), responseBody);
                        new SessionHelper(MaskedNumberActivity.this).requestErrorMessage(
                                responseBody,
                                findViewById(android.R.id.content));
                    } catch (Exception ex) {
                        Logger.errorLogger(this.getClass().getName(), ex.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), "Failed");
                new ButtonStyleHelper(MaskedNumberActivity.this)
                        .initButton(true, binding.btnNext, "Next");
            }
        });
    }

    public void initMobileExits(String message) {
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        MobileExitBottomSheet fragment = new MobileExitBottomSheet();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "");
    }
}