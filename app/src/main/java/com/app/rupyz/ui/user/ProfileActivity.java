package com.app.rupyz.ui.user;

import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.R;
import com.app.rupyz.adapter.individual.MyAddressListAdapter;
import com.app.rupyz.adapter.individual.MyBankListAdapter;
import com.app.rupyz.databinding.ActivityProfileBinding;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.individual.experian.ExperianInfoModel;
import com.app.rupyz.generic.model.user.UserViewModel;
import com.app.rupyz.generic.model.user.profile.UserProfileInfoModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityProfileBinding binding;
    private ApiInterface mApiInterface;
    private Utility mUtil;
    private UserViewModel mUserData;
    ExperianInfoModel mData;
    boolean addressVisibility = false, contactVisibility = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUtil = new Utility(this);
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initLayout();
    }

    private void initLayout() {
        initToolbar();
        getProfileDataUser();
        getMyContactData();
        binding.llExpandAddress.setOnClickListener(this);
        binding.llExpandContact.setOnClickListener(this);
        binding.recyclerviewContact.setHasFixedSize(true);
        binding.recyclerviewContact.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
        binding.recyclerviewAddress.setHasFixedSize(true);
        binding.recyclerviewAddress.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
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


    private void getProfileDataUser() {
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
                    mUserData = gson.fromJson(jsonObj.get("data"), UserViewModel.class);
                    try {
                        binding.userPrefix.setText(
                                getPrefix(mUserData.getFirst_name().substring(0, 1),
                                        mUserData.getLast_name().substring(0, 1)));
                        binding.txtMobileNumber.setText(mUserData.getMobile());
                        binding.txtName.setText(mUserData.getFirst_name()
                                + " " + mUserData.getLast_name());
                        binding.txtPanCard.setText(mUserData.getPan_id());
                        binding.txtDob.setText(mUserData.getDob());
                        binding.txtEmail.setText(mUserData.getEmail());
                        binding.txtGender.setText(mUserData.getGender());
                    } catch (Exception ex) {
                    }

                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        new SessionHelper(ProfileActivity.this).requestErrorMessage(
                                responseBody,
                                binding.getRoot().findViewById(android.R.id.content));
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

    private void getMyContactData() {
        Logger.errorLogger("Profile", SharedPref.getInstance().getString(TOKEN));
        Call<String> call1 = mApiInterface.getDashboardData("Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        Logger.errorLogger(this.getClass().getName(), response.body());
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                        Gson gson = new Gson();
                        mData = gson.fromJson(jsonObj.get("data"), ExperianInfoModel.class);
                        if (mData.getTradelines().size() > 0 && mData.getTradelines() != null) {
                            MyBankListAdapter adapter = new MyBankListAdapter(mData.getTradelines(), ProfileActivity.this);
                            MyAddressListAdapter addressListAdapter = new MyAddressListAdapter(mData.getTradelines(), ProfileActivity.this);
                            binding.recyclerviewContact.setAdapter(adapter);
                            binding.txvNoContact.setVisibility(View.GONE);
                            binding.recyclerviewContact.setVisibility(View.VISIBLE);
                            binding.recyclerviewAddress.setAdapter(addressListAdapter);
                            binding.txvNoAddress.setVisibility(View.GONE);
                            binding.recyclerviewAddress.setVisibility(View.VISIBLE);
                        } else {
                            binding.txvNoContact.setVisibility(View.VISIBLE);
                            binding.recyclerviewContact.setVisibility(View.GONE);
                            binding.recyclerviewAddress.setVisibility(View.GONE);
                            binding.txvNoAddress.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (response.code() == 403) {
                        mUtil.logout();
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

    private String getPrefix(String f_name, String l_name) {
        String fname = f_name.substring(0, 1);
        String lname = "";
        try {
            lname = l_name.substring(0, 1);
        } catch (Exception ex) {

        }
        return fname + lname;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_expand_address:
                if (addressVisibility) {
                    addressVisibility = false;
                    binding.rlAddress.setVisibility(View.GONE);
                } else {
                    addressVisibility = true;
                    binding.rlAddress.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.ll_expand_contact:
                if (contactVisibility) {
                    contactVisibility = false;
                    binding.rlContact.setVisibility(View.GONE);
                } else {
                    contactVisibility = true;
                    binding.rlContact.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}