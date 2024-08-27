package com.app.rupyz.ui.organization.onboarding.activity;

import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityBusinessDetailsBinding;
import com.app.rupyz.generic.helper.ButtonStyleHelper;
import com.app.rupyz.generic.json.JsonHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.organization.onboarding.PanInfoModel;
import com.app.rupyz.generic.model.organization.saveorganization.SaveOrganizationRequest;
import com.app.rupyz.generic.model.organization.saveorganization.SaveOrganizationResponse;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.OnboardingInterface;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.sales.organization.ChooseGstActivity;
import com.app.rupyz.ui.organization.onboarding.sheet.ClaimProfileSheet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityBusinessDetailsBinding binding;
    private String strPanNumber, strBusinessName, strBusinessEmail;
    private OnboardingInterface mApiInterface;
    private Utility mUtil;
    private PanInfoModel mData;
    private String message = "";
    private int org_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBusinessDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mApiInterface = ApiClient.getRetrofit().create(OnboardingInterface.class);
        mUtil = new Utility(this);
        binding.btnSaveBusinessDetail.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
        binding.progressBar.setVisibility(View.GONE);
        try {
            org_id = getIntent().getExtras().getInt("org_id");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        binding.edtPanCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 10) {
                    new ButtonStyleHelper(BusinessDetailsActivity.this).initButton(false, binding.btnSaveBusinessDetail,
                            "Please wait...");
                    /* if the org id already available */
                    if (mData != null) {
                        org_id = mData.getId();
                        doRequest(org_id);
                    } else {
                        doRequest(org_id);
                    }
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.edtPanCard.setEnabled(false);
                    binding.edtBusinessEmail.setEnabled(false);
                    binding.edtBusinessName.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void doRequest(int org_id) {
        Call<String> call = mApiInterface.createOrganization(
                JsonHelper.getCreateOrgJson(binding.edtPanCard.getText().toString(), org_id),
                "Bearer " + SharedPref.getInstance().getString(TOKEN));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                new ButtonStyleHelper(BusinessDetailsActivity.this)
                        .initButton(true, binding.btnSaveBusinessDetail, "Continue");
                binding.progressBar.setVisibility(View.GONE);
                binding.edtPanCard.setEnabled(true);
                binding.edtBusinessEmail.setEnabled(true);
                binding.edtBusinessName.setEnabled(true);

                if (response.code() == 200) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    Gson gson = new Gson();
                    mData = gson.fromJson(jsonObj.get("data"), PanInfoModel.class);
                    if (mData.isIs_claim()) {
                        initClaimProfile(mData.getLegal_name());
                    } else {
                        binding.edtBusinessName.setText(mData.getLegal_name());
                        binding.edtBusinessEmail.setText(mData.getEmail());
                    }
                } else {
                    try {
                        message = response.errorBody().string();
                        new SessionHelper(BusinessDetailsActivity.this).requestErrorMessage(
                                message,
                                findViewById(android.R.id.content));
                    } catch (Exception ex) {
                        Logger.errorLogger(this.getClass().getName(), "response 2- " + ex.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.edtPanCard.setEnabled(true);
                binding.edtBusinessEmail.setEnabled(true);
                binding.edtBusinessName.setEnabled(true);
                new ButtonStyleHelper(BusinessDetailsActivity.this)
                        .initButton(true, binding.btnSaveBusinessDetail, "Continue");
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    public void initClaimProfile(String legalName) {
        Bundle bundle = new Bundle();
        bundle.putString("legal_name", legalName);
        ClaimProfileSheet fragment = new ClaimProfileSheet(this);
        fragment.setArguments(bundle);
        fragment.setCancelable(false);
        fragment.show(getSupportFragmentManager(), "");
    }

    public void claimProfile(boolean isClaim) {
        if (isClaim) {
            Intent intent = new Intent(this, ClaimMaskedDetailsActivity.class);
            intent.putExtra("data", new Gson().toJson(mData));
            intent.putExtra("pan_number", binding.edtPanCard.getText().toString());
            startActivity(intent);
        } else {
            binding.edtPanCard.setText("");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_close:
            case R.id.btnBack:
                this.finish();
                break;
            case R.id.btn_save_business_detail:
                if (mData != null) {
                    if (validate()) {
                        saveBusinessDetail();
                    }
                } else {
                    new SessionHelper(BusinessDetailsActivity.this).requestMessage(
                            message);
                }
                break;
        }
    }

    private boolean validate() {
        boolean temp = true;
        strPanNumber = binding.edtPanCard.getText().toString();
        strBusinessName = binding.edtBusinessName.getText().toString();
        strBusinessEmail = binding.edtBusinessEmail.getText().toString();
        if (StringUtils.isBlank(strPanNumber)) {
            Toast.makeText(this, "Enter Pan Number", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strBusinessName)) {
            Toast.makeText(this, "Enter Business Name", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strBusinessEmail)) {
            Toast.makeText(this, "Enter Business Email", Toast.LENGTH_SHORT).show();
            temp = false;
        }
        return temp;
    }

    private void saveBusinessDetail() {

        SaveOrganizationRequest saveOrganizationRequest = new SaveOrganizationRequest();
        if (!StringUtils.isBlank(strBusinessEmail)) {
            saveOrganizationRequest.setEmail(strBusinessEmail);
        }
        if (!StringUtils.isBlank(strBusinessName)) {
            saveOrganizationRequest.setLegalName(strBusinessName);
        }

        if (!StringUtils.isBlank(strPanNumber)) {
            saveOrganizationRequest.setPanId(strPanNumber);
        }

        Call<SaveOrganizationResponse> call = mApiInterface.saveOrganizationDetail(
                org_id, "Bearer " + SharedPref.getInstance().getString(TOKEN), saveOrganizationRequest);
        call.enqueue(new Callback<SaveOrganizationResponse>() {
            @Override
            public void onResponse(Call<SaveOrganizationResponse> call, Response<SaveOrganizationResponse> response) {
                if (response.code() == 200) {
                    SaveOrganizationResponse response1 = response.body();
                    Toast.makeText(BusinessDetailsActivity.this, response1.getMessage(), Toast.LENGTH_SHORT).show();
                    SharedPref.getInstance().putInt(ORG_ID, response1.getData().getId());
                    startActivity(new Intent(BusinessDetailsActivity.this, ChooseGstActivity.class));
                } else if (response.code() == 400) {
                    try {
                        String responseBody = response.errorBody().string();
                        Logger.errorLogger(this.getClass().getName(), responseBody + "");
                        new SessionHelper(BusinessDetailsActivity.this).requestMessage(
                                responseBody);
                    } catch (Exception ex) {
                        Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
                    }
                }
            }

            @Override
            public void onFailure(Call<SaveOrganizationResponse> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

}
