package com.app.rupyz.sales.organization;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.app.rupyz.generic.utils.SharePrefConstant.IS_EQUI_FAX;
import static com.app.rupyz.generic.utils.SharePrefConstant.IS_LOGIN;
import static com.app.rupyz.generic.utils.SharePrefConstant.IS_SKIP_GSTIN;
import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_INFO;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;
import static com.app.rupyz.generic.utils.SharePrefConstant.USER_INFO;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityChooseGstBinding;
import com.app.rupyz.generic.helper.ButtonStyleHelper;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.json.JsonHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.organization.gstinfo.GSTInfo;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.OnboardingInterface;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.sales.home.SalesMainActivity;
import com.app.rupyz.ui.organization.onboarding.adapter.ChooseGstAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseGstActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityChooseGstBinding binding;
    private Utility mUtil;
    private EquiFaxReportHelper mReportHelper;
    private OnboardingInterface mApiInterface;
    private String gst_number = "";
    private OrganizationViewModel organizationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseGstBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        organizationViewModel = new ViewModelProvider(this).get(OrganizationViewModel.class);

        mReportHelper = EquiFaxReportHelper.getInstance();
        mApiInterface = ApiClient.getRetrofit().create(OnboardingInterface.class);
        binding.recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        mUtil = new Utility(this);

        initObservers();
        initLayout();
        getGstList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                initRequest();
                break;
            case R.id.btnBack:
                finish();
                break;
            case R.id.btn_skip:
                doRequest("", true);
                break;
        }
    }

    private void initLayout() {
        binding.btnSkip.setOnClickListener(this);
        binding.btnNext.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void initRequest() {
        if (binding.gstNumberLayout.getVisibility() == View.VISIBLE) {
            if (binding.txtGstNumber.getText().toString().length() == 15) {
                mReportHelper.setGstId(binding.txtGstNumber.getText().toString());
                doRequest(mReportHelper.getGstId(), false);
            } else {
                Toast.makeText(ChooseGstActivity.this, "Required Valid GST Number.", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (StringUtils.isBlank(mReportHelper.getGstId())) {
                Toast.makeText(ChooseGstActivity.this, "Please select a GST", Toast.LENGTH_SHORT).show();
            } else {
                doRequest(mReportHelper.getGstId(), false);
            }
        }
    }

    private void doRequest(String primary_gstin, boolean is_skip_gstin) {
        new ButtonStyleHelper(this).initButton(false, binding.btnNext,
                "Please wait...");
        Call<String> call1 = mApiInterface.gstin(SharedPref.getInstance().getInt(ORG_ID),
                JsonHelper.getSaveGSTJson(primary_gstin, is_skip_gstin), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                new ButtonStyleHelper(ChooseGstActivity.this).initButton(true, binding.btnNext, "Next");
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body());
                    SharedPref.getInstance().putBoolean(IS_SKIP_GSTIN, is_skip_gstin);
                    if (is_skip_gstin) {
                        SharedPref.getInstance().putBoolean(IS_LOGIN, true);
                        SharedPref.getInstance().putBoolean(IS_EQUI_FAX, true);

                        organizationViewModel.getProfileInfo();

                    } else {
                        Intent intent = new Intent(ChooseGstActivity.this, AddBusinessInfoActivity.class);
                        intent.putExtra("gst_number", gst_number);
                        startActivity(intent);
                    }
                }
                try {
                    try {
                        String responseBody = response.errorBody().string();
                        Logger.errorLogger(this.getClass().getName(), responseBody + "");
                        new SessionHelper(ChooseGstActivity.this).requestMessage(
                                responseBody);
                    } catch (Exception ex) {
                        Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    private void initObservers() {
        organizationViewModel.getProfileLiveData().observe(this, data -> {
            if (data.getError() != null && !data.getError()) {

                if (data.getData() != null && data.getData().getOrgIds() != null && data.getData().getOrgIds().size() > 0) {
                    data.getData().getOrgIds().get(0).setSelected(true);
                    SharedPref.getInstance().putModelClass(ORG_INFO, data.getData().getOrgIds().get(0));
                }
               SharedPref.getInstance().putModelClass(USER_INFO, data.getData());
                startActivity(
                        new Intent(this, SalesMainActivity.class).addFlags(
                                FLAG_ACTIVITY_CLEAR_TOP
                        )
                );
                finish();
            }
        });
    }

    private void getGstList() {
        Call<GSTInfo> call = mApiInterface.getGstList(SharedPref.getInstance().getInt(ORG_ID),
                "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call.enqueue(new Callback<GSTInfo>() {
            @Override
            public void onResponse(Call<GSTInfo> call, Response<GSTInfo> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());
                    GSTInfo response1 = response.body();
                    try {
                        if (response1.getData().getGstinList().size() > 0) {
                            binding.gstMessage.setText("Select GST Number");
                            binding.gstNumberLayout.setVisibility(View.GONE);
                            binding.recyclerView.setVisibility(View.VISIBLE);
                            binding.btnSkip.setVisibility(View.GONE);
                            binding.recyclerView.setAdapter(new ChooseGstAdapter(ChooseGstActivity.this, response1.getData().getGstinList()));
                        } else {
                            binding.gstNumberLayout.setVisibility(View.VISIBLE);
                            binding.gstMessage.setText("Enter GST Number");
                            binding.recyclerView.setVisibility(View.GONE);
                            binding.btnSkip.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception ex) {
                        binding.gstNumberLayout.setVisibility(View.VISIBLE);
                        binding.gstMessage.setText("Enter GST Number");
                        binding.recyclerView.setVisibility(View.GONE);
                        binding.btnSkip.setVisibility(View.VISIBLE);
                    }
                } else if (response.code() == 400) {
                    try {
                        String responseBody = response.errorBody().string();
                        Logger.errorLogger(this.getClass().getName(), responseBody + "");
                        new SessionHelper(ChooseGstActivity.this).requestMessage(
                                responseBody);
                    } catch (Exception ex) {
                        Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
                    }
                }
            }

            @Override
            public void onFailure(Call<GSTInfo> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    public void updateSelectedGST(String gst_number) {
        this.gst_number = gst_number;
    }
}