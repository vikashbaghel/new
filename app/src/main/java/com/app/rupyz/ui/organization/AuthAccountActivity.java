package com.app.rupyz.ui.organization;

import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.adapter.organization.EquiFaxAuthUserListAdapter;
import com.app.rupyz.adapter.organization.GstListAdapter;
import com.app.rupyz.databinding.ActivityAuthAccountBinding;
import com.app.rupyz.generic.helper.ButtonStyleHelper;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.helper.SortingHelper;
import com.app.rupyz.generic.json.JsonHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.organization.AuthSignaViewModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.toast.MessageHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityAuthAccountBinding binding;
    private ApiInterface mApiInterface;
    private Utility mUtil;
    private AuthSignaViewModel mData;
    private EquiFaxReportHelper mReportHelper;
    private String authorized_signatory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mReportHelper = EquiFaxReportHelper.getInstance();
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        mUtil = new Utility(this);
        binding.btnNext.setOnClickListener(this);
        Logger.errorLogger("AuthAccountActivity", getIntent().getExtras().getString("org_id"));
        if (getIntent().getExtras().getString("org_id").equalsIgnoreCase("")) {
            initData();
        } else {
            doAuthRequest();
        }
    }

    private void initLayout() {
        try {
            binding.btnNext.setOnClickListener(this);
            if (mData.getAuthorized_signatories().size() > 0) {
                binding.authLayout.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
                binding.recyclerView.setLayoutManager(mLayoutManager);
                binding.recyclerView.setAdapter(new EquiFaxAuthUserListAdapter(this, mData.getAuthorized_signatories(), 1));
            } else {
                binding.authLayout.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            binding.authLayout.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        }
    }

    private void initData() {
        try {
            Logger.errorLogger(this.getClass().getName(), getIntent().getExtras().getString("data"));
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObj = (JsonObject) jsonParser.parse(getIntent().getExtras().getString("data"));
            Gson gson = new Gson();
            mData = gson.fromJson(jsonObj.get("data"), AuthSignaViewModel.class);
            initLayout();
        } catch (Exception ex) {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                initRequest();
                break;
        }
    }

    private void initRequest() {
        if (binding.authLayout.getVisibility() == View.VISIBLE) {
            if (binding.txtName.getText().toString().trim().equalsIgnoreCase("")) {
                new MessageHelper().initMessage("Authorised Person Name Required.",
                        findViewById(android.R.id.content));
            } else {
                authorized_signatory = binding.txtName.getText().toString().trim();
                doRequest();
            }
        } else {
            if (authorized_signatory.equalsIgnoreCase("")) {
                new MessageHelper().initMessage("Please Select Authorised Person",
                        findViewById(android.R.id.content));
            } else {
                doRequest();
            }
        }
    }

    private void doRequest() {
        new ButtonStyleHelper(this).initButton(false, binding.btnNext,
                "Please wait...");
        Call<String> call1 = mApiInterface.authorizedSignatory(
                JsonHelper.getAuthSignatoryJson(authorized_signatory,
                        Integer.parseInt(getIntent().getExtras().getString("org_id"))), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                new ButtonStyleHelper(AuthAccountActivity.this).initButton(true, binding.btnNext, "Next");
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body());
                    mReportHelper.setOrgId(mData.getOrg_id());
                    Intent intent = new Intent(AuthAccountActivity.this, ReviewActivity.class);
                    intent.putExtra("org_id", (getIntent().getExtras().getString("org_id")));
                    startActivity(intent);
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


    private void doAuthRequest() {
        Call<String> call1 = mApiInterface.gstin(
                JsonHelper.getGSTJson(null, null,
                        Integer.parseInt(getIntent().getExtras().getString("org_id"))), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body());
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    Gson gson = new Gson();
                    mData = gson.fromJson(jsonObj.get("data"), AuthSignaViewModel.class);
                    initLayout();
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

    public void updateAuthSignatory(int index) {
        authorized_signatory = mData.getAuthorized_signatories().get(index);
    }
}