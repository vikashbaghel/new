package com.app.rupyz.sales.organization;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.app.rupyz.generic.utils.SharePrefConstant.IS_EQUI_FAX;
import static com.app.rupyz.generic.utils.SharePrefConstant.IS_LOGIN;
import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_INFO;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;
import static com.app.rupyz.generic.utils.SharePrefConstant.USER_INFO;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityAddBusinessInfoBinding;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.organization.busineeaddress.BusinessInfo;
import com.app.rupyz.generic.model.organization.busineeaddress.businessupdate.BusinessInfoUpdate;
import com.app.rupyz.generic.model.organization.busineeaddress.businessupdate.UpdateBusinessInfo;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.OnboardingInterface;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.sales.home.SalesMainActivity;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBusinessInfoActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityAddBusinessInfoBinding binding;
    String strGst;
    private OnboardingInterface mApiInterface;
    String strIncorporationDate, strFirstAddress, strSecondAddress, strPincode, strState, strCity;
    DatePickerDialog.OnDateSetListener onDateSetListener;
    private Utility mUtil;
    private OrganizationViewModel organizationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBusinessInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mApiInterface = ApiClient.getRetrofit().create(OnboardingInterface.class);
        organizationViewModel = new ViewModelProvider(this).get(OrganizationViewModel.class);

        mUtil = new Utility(this);
        Intent intent = getIntent();
        if (intent != null) {
            strGst = intent.getStringExtra("gst_number");
            binding.txvGstNumber.setText("GST " + strGst);
        }

        initObservers();

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        binding.edtIncorporationDate.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddBusinessInfoActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, onDateSetListener, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });

        onDateSetListener = (datePicker, year1, month1, dayofmonth) -> {
            month1 = month1 + 1;
            String date = year1 + "-" + month1 + "-" + day;
            strIncorporationDate = year1 + "-" + month1 + "-" + day;
            binding.edtIncorporationDate.setText(date);
        };
        binding.spinnerState.setAdapter(new ArrayAdapter<>(this, R.layout.single_text_view_spinner,
                getResources().getStringArray(R.array.states)));
        getBusinessInfo();
        binding.btnAddBussinessInfo.setOnClickListener(this);
        binding.imgBack.setOnClickListener(this);
    }

    private void getBusinessInfo() {
        Call<BusinessInfo> call = mApiInterface.getBusinessAddress(SharedPref.getInstance().getInt(ORG_ID),
                "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call.enqueue(new Callback<BusinessInfo>() {
            @Override
            public void onResponse(Call<BusinessInfo> call, Response<BusinessInfo> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());
                    BusinessInfo response1 = response.body();
                    initData(response1);

                } else if (response.code() == 400) {
                    try {
                        String responseBody = response.errorBody().string();
                        Logger.errorLogger(this.getClass().getName(), responseBody + "");
                        new SessionHelper(AddBusinessInfoActivity.this).requestMessage(
                                responseBody);
                    } catch (Exception ex) {
                        Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
                    }
                }
            }

            @Override
            public void onFailure(Call<BusinessInfo> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    private void initData(BusinessInfo response1) {
        binding.edtIncorporationDate.setText(response1.getData().getIncorporationDate());
        binding.edtFirstAddressLine.setText(response1.getData().getAddressLine1());
        binding.edtSecondAddressLine.setText(response1.getData().getAddressLine2());
        binding.edtPincode.setText(response1.getData().getPincode());
        binding.edtCity.setText(response1.getData().getCity());
        binding.txvGstNumber.setText("GST " + response1.getData().getPrimaryGstin());
        try {
            int index = 0;
            int state_index = 0;
            for (String state : getResources().getStringArray(R.array.states)) {
                if (state.equalsIgnoreCase(response1.getData().getState())) {
                    state_index = index;
                }
                index++;
            }
            binding.spinnerState.setSelection(state_index);
        } catch (Exception ex) {

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
            case R.id.img_close:
                onBackPressed();
                break;
            case R.id.btn_add_bussiness_info:
                if (validate()) {
                    saveBusinessAddress();
                }
                break;
        }
    }

    private void saveBusinessAddress() {
        BusinessInfoUpdate businessInfoUpdate = new BusinessInfoUpdate();
        if (!StringUtils.isBlank(strIncorporationDate)) {
            businessInfoUpdate.setIncorporationDate(strIncorporationDate);
        }
        if (!StringUtils.isBlank(strFirstAddress)) {
            businessInfoUpdate.setAddressLine1(strFirstAddress);
        }
        if (!StringUtils.isBlank(strSecondAddress)) {
            businessInfoUpdate.setAddressLine2(strSecondAddress);
        }
        if (!StringUtils.isBlank(strPincode)) {
            businessInfoUpdate.setPincode(strPincode);
        }
        if (!StringUtils.isBlank(strState)) {
            businessInfoUpdate.setState(strState);
        }
        if (!StringUtils.isBlank(strCity)) {
            businessInfoUpdate.setCity(strCity);
        }

        Call<UpdateBusinessInfo> call = mApiInterface.updateBusinessAddress(SharedPref.getInstance().getInt(ORG_ID),
                "Bearer " + SharedPref.getInstance().getString(TOKEN), businessInfoUpdate);
        call.enqueue(new Callback<UpdateBusinessInfo>() {
            @Override
            public void onResponse(Call<UpdateBusinessInfo> call, Response<UpdateBusinessInfo> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());
                    UpdateBusinessInfo response1 = response.body();
                    Toast.makeText(AddBusinessInfoActivity.this, response1.getMessage(), Toast.LENGTH_SHORT).show();
                    SharedPref.getInstance().putBoolean(IS_LOGIN, true);
                   SharedPref.getInstance().putBoolean(IS_EQUI_FAX, true);

                    organizationViewModel.getProfileInfo();
                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        Logger.errorLogger(this.getClass().getName(), responseBody + "");
                        new SessionHelper(AddBusinessInfoActivity.this).requestMessage(
                                responseBody);
                    } catch (Exception ex) {
                        Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateBusinessInfo> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    private void initObservers() {
        organizationViewModel.getProfileLiveData().observe(this, data -> {
            if (data.getError() != null && !data.getError()) {
                if (data.getData() != null) {
                    data.getData().getOrgIds();
                    if (data.getData().getOrgIds().size() > 0) {
                        data.getData().getOrgIds().get(0).setSelected(true);
                        SharedPref.getInstance().putModelClass(ORG_INFO, data.getData().getOrgIds().get(0));
                    }
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

    private boolean validate() {
        boolean temp = true;
        strIncorporationDate = binding.edtIncorporationDate.getText().toString();
        strFirstAddress = binding.edtFirstAddressLine.getText().toString();
        strSecondAddress = binding.edtSecondAddressLine.getText().toString();
        strPincode = binding.edtPincode.getText().toString();
        strState = binding.spinnerState.getSelectedItem().toString().trim();
        strCity = binding.edtCity.getText().toString();

        if (StringUtils.isBlank(strIncorporationDate)) {
            Toast.makeText(this, "Enter Incorporation Date", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strFirstAddress)) {
            Toast.makeText(this, "Enter Address Line 1", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strPincode)) {
            Toast.makeText(this, "Enter Pincode", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strState)) {
            Toast.makeText(this, "Enter State", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strCity)) {
            Toast.makeText(this, "Enter City", Toast.LENGTH_SHORT).show();
            temp = false;
        }
        return temp;
    }
}