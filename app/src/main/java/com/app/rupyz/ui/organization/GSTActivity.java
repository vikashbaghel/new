package com.app.rupyz.ui.organization;

import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.adapter.organization.GstListAdapter;
import com.app.rupyz.databinding.ActivityGstBinding;
import com.app.rupyz.generic.helper.ButtonStyleHelper;
import com.app.rupyz.generic.helper.SortingHelper;
import com.app.rupyz.generic.json.JsonHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.organization.AuthSignaViewModel;
import com.app.rupyz.generic.model.organization.GstViewModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.toast.MessageHelper;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GSTActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityGstBinding binding;
    private ApiInterface mApiInterface;
    private Utility mUtil;
    private GstViewModel mData;
    private AuthSignaViewModel mAuthSignData;
    private String primary_gstin = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGstBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        mUtil = new Utility(this);
        initLayout();
        initData();
    }

    private void initData() {
        try {
            Logger.errorLogger(this.getClass().getName(), getIntent().getExtras().getString("data"));
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObj = (JsonObject) jsonParser.parse(getIntent().getExtras().getString("data"));
            Gson gson = new Gson();
            mData = gson.fromJson(jsonObj.get("data"), GstViewModel.class);
            if (mData.getLegal_name() != null) {
                binding.txtOrgLegalName.setText(mData.getLegal_name());
                binding.txtOrgLegalName.setEnabled(false);
            } else {
                binding.txtOrgLegalName.setEnabled(true);
            }
            try {
                if (mData.getGstin_list().size() > 0) {
                    binding.gstMessage.setText("Select GST Number");
                    binding.gstNumberLayout.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.recyclerView.setAdapter(new GstListAdapter(this, SortingHelper.sortByGSTName(mData.getGstin_list()), 0));
                } else {
                    binding.gstNumberLayout.setVisibility(View.VISIBLE);
                    binding.gstMessage.setText("Enter GST Number");
                    binding.recyclerView.setVisibility(View.GONE);
                }
            } catch (Exception ex) {
                binding.gstNumberLayout.setVisibility(View.VISIBLE);
                binding.gstMessage.setText("Enter GST Number");
                binding.recyclerView.setVisibility(View.GONE);
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                try {

                    if (mData.getLegal_name() == null) {
                        if (binding.txtOrgLegalName.getText().toString().equalsIgnoreCase("")) {
                            new MessageHelper().initMessage("Please Enter Legal Name",
                                    findViewById(android.R.id.content));
                        } else if (!binding.txtGstNumber.getText().toString().trim().equalsIgnoreCase("")) {
                            if (primary_gstin.equalsIgnoreCase("")) {
                                primary_gstin = binding.txtGstNumber.getText().toString().trim();
                            } else {
                                mData.setLegal_name(binding.txtOrgLegalName.getText().toString());
                                doRequest();
                            }
                        } else if (primary_gstin.equalsIgnoreCase("")) {
                            new MessageHelper().initMessage("Please Enter/Select GST Number",
                                    findViewById(android.R.id.content));
                        } else {
                            mData.setLegal_name(binding.txtOrgLegalName.getText().toString());
                            doRequest();
                        }
                    } else {
                        if (!binding.txtGstNumber.getText().toString().trim().equalsIgnoreCase("")) {
                            Logger.errorLogger("hello1", mData.getLegal_name());
                            if (primary_gstin.equalsIgnoreCase("")) {
                                primary_gstin = binding.txtGstNumber.getText().toString().trim();
                            } else {
                                mData.setLegal_name(binding.txtOrgLegalName.getText().toString());
                                doRequest();
                            }
                        } else {
                            if (primary_gstin.equalsIgnoreCase("")) {
                                primary_gstin = binding.txtGstNumber.getText().toString().trim();
                            } else {
                                mData.setLegal_name(binding.txtOrgLegalName.getText().toString());
                                doRequest();
                            }
                            Logger.errorLogger("hello", mData.getLegal_name());
                        }
                    }
                } catch (Exception ex) {
                    Logger.errorLogger("GSTActivity", ex.getMessage());
                }
                break;
        }

    }

    private void initLayout() {
        binding.btnNext.setOnClickListener(this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        binding.recyclerView.setLayoutManager(mLayoutManager);
    }

    private void doRequest() {
        new ButtonStyleHelper(this).initButton(false, binding.btnNext,
                "Please wait...");
        Call<String> call1 = mApiInterface.gstin(
                JsonHelper.getGSTJson(primary_gstin, mData.getLegal_name(),
                        mData.getOrg_id()), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                new ButtonStyleHelper(GSTActivity.this).initButton(true, binding.btnNext, "Next");
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body());
                    Intent intent = new Intent(GSTActivity.this, AuthAccountActivity.class);
                    intent.putExtra("data", response.body());
                    intent.putExtra("org_id", "");
                    startActivity(intent);
                }
                Logger.errorLogger(this.getClass().getName(), response.code() + "");
                try {
                    try {
                        String responseBody = response.errorBody().string();
                        Logger.errorLogger(this.getClass().getName(), responseBody + "");
                        new SessionHelper(GSTActivity.this).requestMessage(
                                responseBody);
                    } catch (Exception ex) {
                        Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
                    }
                } catch (Exception ex) {

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    public void updateGSTNumber(int index) {
        primary_gstin = mData.getGstin_list().get(index).getGstin();
    }
}