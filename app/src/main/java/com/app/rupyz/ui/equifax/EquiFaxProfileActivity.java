package com.app.rupyz.ui.equifax;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.app.rupyz.R;
import com.app.rupyz.adapter.individual.MyAddressListAdapter;
import com.app.rupyz.adapter.individual.MyBankListAdapter;
import com.app.rupyz.databinding.ActivityEquiFaxProfileBinding;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.individual.experian.ExperianInfoModel;
import com.app.rupyz.generic.model.organization.AlertsItem;
import com.app.rupyz.generic.model.organization.EquiFaxInfoModel;
import com.app.rupyz.generic.model.organization.individual.Alert;
import com.app.rupyz.generic.model.organization.individual.EquiFaxIndividualInfoModel;
import com.app.rupyz.generic.model.user.UserViewModel;
import com.app.rupyz.generic.model.user.profile.UserProfileInfoModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.user.ProfileActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquiFaxProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityEquiFaxProfileBinding binding;
    private EquiFaxIndividualInfoModel mRetailerData;
    private EquiFaxInfoModel mCommercialData;
    private ApiInterface mApiInterface;
    private EquiFaxReportHelper mReportHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEquiFaxProfileBinding.inflate(getLayoutInflater());
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        mReportHelper = EquiFaxReportHelper.getInstance();
        setContentView(binding.getRoot());
        new Utility(this);
        initLayout();
        initToolbar();
    }

    private void initLayout() {
        initToolbar();
        //getProfileDataUser();
//        getUserProfileData();
        binding.llExpandAddress.setOnClickListener(this);
        binding.llExpandContact.setOnClickListener(this);
        /*binding.recyclerviewContact.setHasFixedSize(true);
        binding.recyclerviewContact.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerviewAddress.setHasFixedSize(true);
        binding.recyclerviewAddress.setLayoutManager(new LinearLayoutManager(this));*/
    }

    private void getProfileDataUser() {
        try {
            mRetailerData = mReportHelper.getRetailReport();
            if (mRetailerData != null) {
                if (!StringUtils.isBlank(mRetailerData.getReport().getIndividual_additional_info().getIdentity_info()
                        .getName().getLastName())) {
                    binding.userPrefix.setText(
                            getPrefix(mRetailerData.getReport().getIndividual_additional_info().getIdentity_info()
                                            .getName().getFirstName().substring(0, 1),
                                    mRetailerData.getReport().getIndividual_additional_info().getIdentity_info()
                                            .getName().getLastName().substring(0, 1)));
                } else {
                    binding.userPrefix.setText(
                            getPrefix(mRetailerData.getReport().getIndividual_additional_info().getIdentity_info()
                                    .getName().getFirstName().substring(0, 1)));
                }
                if (mRetailerData.getReport().getIndividual_additional_info().getPhone_info() != null) {
                    binding.txtMobileNumber.setText(mRetailerData.getReport().getIndividual_additional_info().getPhone_info()
                            .get(0).getNumber());
                }
                binding.txtName.setText(mRetailerData.getReport().getIndividual_additional_info().getIdentity_info()
                        .getName().getFullName());
                binding.txtDob.setText(mRetailerData.getReport().getIndividual_additional_info().getIdentity_info()
                        .getDateOfBirth());
                binding.txtGender.setText(mRetailerData.getReport().getIndividual_additional_info().getIdentity_info()
                        .getGender());
                binding.txtPanCard.setText(mRetailerData.getReport().getIndividual_additional_info().getPersonal_info().getpANId().get(0).getIdNumber());
                binding.txtUserEmail.setText(mRetailerData.getReport().getIndividual_additional_info().getEmail_info().get(0).getEmail());
            } else {
                binding.userPrefix.setText(
                        getPrefix(mReportHelper.getCommercialReport().getReport().getAuthorizedSignatory().substring(0, 1)));
                binding.txtName.setText(
                        mReportHelper.getCommercialReport().getReport().getAuthorizedSignatory());
            }

        } catch (Exception ex) {
            binding.userPrefix.setText(
                    getPrefix(mReportHelper.getCommercialReport().getReport().getAuthorizedSignatory().substring(0, 1)));
            binding.txtName.setText(
                    mReportHelper.getCommercialReport().getReport().getAuthorizedSignatory());
        }
    }

//    private void getUserProfileData() {
//        Logger.errorLogger("Profile", SharedPref.getInstance().getString(TOKEN));
//        Call<UserProfileInfoModel> call1 = mApiInterface.getUserProfileInformation("Bearer " + SharedPref.getInstance().getString(TOKEN));
//        call1.enqueue(new Callback<UserProfileInfoModel>() {
//            @Override
//            public void onResponse(Call<UserProfileInfoModel> call, Response<UserProfileInfoModel> response) {
//                if (response.code() == 200) {
//                    UserProfileInfoModel userProfileInfoModel = response.body();
//                    try {
//                        binding.userPrefix.setText(
//                                getPrefix(userProfileInfoModel.getData().getFirstName().substring(0, 1),
//                                        userProfileInfoModel.getData().getLastName().substring(0, 1)));
//                        binding.txtMobileNumber.setText(userProfileInfoModel.getData().getMobile());
//                        binding.txtName.setText(userProfileInfoModel.getData().getFirstName()
//                                + " " + userProfileInfoModel.getData().getLastName());
//                        binding.txtPanCard.setText(userProfileInfoModel.getData().getPanId());
//                        binding.txtDob.setText(userProfileInfoModel.getData().getDob());
//                        binding.txtUserEmail.setText(userProfileInfoModel.getData().getEmail());
//                        binding.txtGender.setText(userProfileInfoModel.getData().getGender());
//                        binding.txtAddress.setText(userProfileInfoModel.getData().getAddressLine1() + " "+ userProfileInfoModel.getData().getAddressLine2()
//                                +" "+ userProfileInfoModel.getData().getCity()+ " "+ userProfileInfoModel.getData().getState() +" "+userProfileInfoModel.getData().getPincode());
//                    } catch (Exception ex) {
//
//                    }
//
//                } else {
//                    try {
//                        String responseBody = response.errorBody().string();
//                        new SessionHelper(EquiFaxProfileActivity.this).requestErrorMessage(
//                                responseBody,
//                                binding.getRoot().findViewById(android.R.id.content));
//                    } catch (Exception ex) {
//                    }
//                }
//                try {
//                    Logger.errorLogger(this.getClass().getName(), response.errorBody().string() + "");
//                } catch (Exception ex) {
//                    Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
//                }
//                Logger.errorLogger(this.getClass().getName(), response.code() + "");
//            }
//
//            @Override
//            public void onFailure(Call<UserProfileInfoModel> call, Throwable t) {
//                Logger.errorLogger(this.getClass().getName(), t.getMessage());
//                call.cancel();
//            }
//        });
//    }

    private String getPrefix(String f_name, String l_name) {
        String fname = f_name.substring(0, 1);
        String lname = "";
        try {
            lname = l_name.substring(0, 1);
        } catch (Exception ex) {

        }
        return fname + lname;
    }

    private String getPrefix(String f_name) {
        String fname = f_name.substring(0, 1);
        return fname;
    }

    private void initToolbar() {
        Toolbar toolBar = this.findViewById(R.id.toolbar_my);
        ImageView imageViewBack = toolBar.findViewById(R.id.img_back);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_expand_address:
                if (binding.rlAddress.getVisibility() == View.VISIBLE) {
                    binding.rlAddress.setVisibility(View.GONE);
                } else {
                    binding.rlAddress.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.ll_expand_contact:
                if (binding.rlContact.getVisibility() == View.VISIBLE) {
                    binding.rlContact.setVisibility(View.GONE);
                } else {
                    binding.rlContact.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}