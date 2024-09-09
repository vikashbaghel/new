package com.app.rupyz.ui.individual;

import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityProfileReviewBinding;
import com.app.rupyz.generic.helper.ButtonStyleHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.individual.ExpInfoModel;
import com.app.rupyz.generic.model.user.UserViewModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.individual.experian.OtpVerifyActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileReviewActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityProfileReviewBinding binding;
    private ApiInterface mApiInterface;
    private UserViewModel mData;
    private ExpInfoModel mExpData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        binding = ActivityProfileReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initData();
    }

    private void initData() {
        getProfileData();
        binding.spinnerState.setAdapter(new ArrayAdapter<>(this, R.layout.single_text_view_spinner,
                getResources().getStringArray(R.array.states)));
        binding.btnNext.setOnClickListener(this);
        binding.btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                new ButtonStyleHelper(this).initButton(false, binding.btnNext, "Please wait...");
                initExperian();
                break;
        }
    }

    private void getProfileData() {
        Logger.errorLogger("Profile", SharedPref.getInstance().getString(TOKEN));
        Call<String> call1 = mApiInterface.getReviewData("Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName()+"hello", response.body());
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    Gson gson = new Gson();
                    mData = gson.fromJson(jsonObj.get("data"), UserViewModel.class);
                    binding.txtPanCard.setText(mData.getPan_id());
                    binding.txtName.setText(mData.getFirst_name() + " " + mData.getMiddle_name() + " " + mData.getLast_name());
                    binding.txtEmail.setText(mData.getEmail());
                    binding.txtCity.setText(mData.getCity());
                    binding.txtPinCode.setText(mData.getPincode());
                    binding.txtOrgAddressLineOne.setText(mData.getAddress_line_1());
                    binding.txtOrgAddressLineTwo.setText(mData.getAddress_line_2());
                    binding.txtDob.setText(mData.getDob());
                    try{
                        if(mData.getGender().equalsIgnoreCase("Male")){
                            binding.rdbFemale.setChecked(false);
                            binding.rdbMale.setChecked(true);

                        }else{
                            binding.rdbMale.setChecked(false);
                            binding.rdbFemale.setChecked(true);
                        }
                    }catch (Exception ex){

                    }
                    int index = 0;
                    int state_index = 0;
                    for (String state : getResources().getStringArray(R.array.states)) {
                        if (state.equalsIgnoreCase(mData.getState())) {
                            state_index = index;
                        }
                        index++;
                    }
                    binding.spinnerState.setSelection(state_index);

                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        new SessionHelper(ProfileReviewActivity.this).requestErrorMessage(
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

    private void initExperian() {

        Call<String> call1 = mApiInterface.initExperian("Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Logger.errorLogger(this.getClass().getName(), response.code() + "");
                new ButtonStyleHelper(ProfileReviewActivity.this)
                        .initButton(true, binding.btnNext, "Next");
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body());
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    Gson gson = new Gson();
                    mExpData = gson.fromJson(jsonObj.get("data"), ExpInfoModel.class);
                    Intent intent = new Intent(ProfileReviewActivity.this, OtpVerifyActivity.class);
                    intent.putExtra("session_id", mExpData.getExp_data().getSession_id());
                    intent.putExtra("id", mExpData.getId());
                    startActivity(intent);

                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        Logger.errorLogger(this.getClass().getName(), responseBody + "");
                        new SessionHelper(ProfileReviewActivity.this).requestErrorMessage(
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
                new ButtonStyleHelper(ProfileReviewActivity.this)
                        .initButton(true, binding.btnNext, "Next");
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }
}