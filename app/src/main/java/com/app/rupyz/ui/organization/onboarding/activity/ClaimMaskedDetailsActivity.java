package com.app.rupyz.ui.organization.onboarding.activity;

import static com.app.rupyz.generic.utils.SharePrefConstant.IS_LOGIN;
import static com.app.rupyz.generic.utils.SharePrefConstant.LEGAL_NAME;
import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.IS_EQUI_FAX;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityClaimMaskedDetailsBinding;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.organization.onboarding.PanInfoModel;
import com.app.rupyz.generic.model.organization.saveorganization.SaveOrganizationRequest;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.OnboardingInterface;
import com.app.rupyz.generic.toast.MessageHelper;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.equifax.EquiFaxMainActivity;
import com.app.rupyz.ui.organization.adapter.AuthorizedSignatoriesListAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClaimMaskedDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityClaimMaskedDetailsBinding binding;
    private PanInfoModel mData;
    private PanInfoModel mRequestData;
    private List<String> mAuthorizedData = new ArrayList<>();
    private String pan_number = "";
    private OnboardingInterface mApiInterface;
    private SaveOrganizationRequest mClaimData;
    private Utility mUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClaimMaskedDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mUtil = new Utility(this);
        mApiInterface = ApiClient.getRetrofit().create(OnboardingInterface.class);
        initData();
    }

    private void initData() {
        mData = new Gson().fromJson(getIntent().getExtras().getString("data"), PanInfoModel.class);
        pan_number = getIntent().getExtras().getString("pan_number");
        binding.txtMaskedGst.setText("GST - " + mData.getPrimary_gstin());
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setVisibility(View.VISIBLE);
        binding.recyclerView.setAdapter(new AuthorizedSignatoriesListAdapter(
                mData.getAuthorized_signatories(), this));
        binding.btnNext.setOnClickListener(this);
        mAuthorizedData = new ArrayList<>();
        for (String name : mData.getAuthorized_signatories()) {
            mAuthorizedData.add("");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                validation();
                break;
        }
    }

    private void validation() {
        if (binding.etMaskedGst.getText().toString().trim().equalsIgnoreCase("")) {
            new MessageHelper().initMessage("Please Enter GST Number",
                    findViewById(android.R.id.content));
        } else {
            mRequestData = new PanInfoModel();
            mRequestData.setAuthorized_signatories(mAuthorizedData);
            mRequestData.setPrimary_gstin(binding.etMaskedGst.getText().toString());
            mRequestData.setPan_id(pan_number);
            boolean isSave = true;
            for (String name : mAuthorizedData) {
                if (name.equalsIgnoreCase("")) {
                    isSave = false;
                }
            }
            if (isSave) {
                doClaim(new Gson().toJson(mRequestData));
            } else {
                new MessageHelper().initMessage("Required Authorized Signatories Name",
                        findViewById(android.R.id.content));
            }
        }
    }

    public void updateAuthorized(int position, String name) {
        mAuthorizedData.set(position, name);
    }

    private void doClaim(String requestBody) {
        Logger.errorLogger("requestBody", requestBody);
        Call<String> call1 = mApiInterface.claimProfile(requestBody, "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body() + "");
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    mClaimData = new Gson().fromJson(jsonObj.get("data"), SaveOrganizationRequest.class);
                    SharedPref.getInstance().putBoolean(IS_LOGIN, true);
                    SharedPref.getInstance().putInt(ORG_ID, mClaimData.getId());
                    SharedPref.getInstance().putString(LEGAL_NAME,mClaimData.getLegalName());
                   SharedPref.getInstance().putBoolean(IS_EQUI_FAX, true);
                    Intent intent = new Intent(ClaimMaskedDetailsActivity.this, EquiFaxMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        new SessionHelper(ClaimMaskedDetailsActivity.this).requestErrorMessage(
                                responseBody,
                                findViewById(android.R.id.content));
                    } catch (Exception ex) {
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                call.cancel();
            }
        });
    }
}