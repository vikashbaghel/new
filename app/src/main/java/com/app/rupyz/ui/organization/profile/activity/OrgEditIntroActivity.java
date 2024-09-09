package com.app.rupyz.ui.organization.profile.activity;

import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityOrgEditIntroBinding;
import com.app.rupyz.generic.helper.DataHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileInfoModel;
import com.app.rupyz.generic.model.profile.profileInfo.createProfile.CreateProfileInfoModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.utils.AppConstant;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.generic.utils.Utility;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrgEditIntroActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ActivityOrgEditIntroBinding binding;
    private String strTurnOver;
    private String strBusinessName, strShortDescription, strBusinessNature, strState,
            no_of_employees, strAboutUs, strFirstAddressLine, strCity, strPinCode,
            strIncorporationDate, aggregated_turnover;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private ArrayList<String> natureOfBusiness;
    private EquiFaxApiInterface mEquiFaxApiInterface;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrgEditIntroBinding.inflate(getLayoutInflater());
        mEquiFaxApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface.class);
        setContentView(binding.getRoot());
        binding.spinnerState.setAdapter(new ArrayAdapter<String>(this, R.layout.single_text_view_spinner_black,
                getResources().getStringArray(R.array.states)));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.single_text_view_spinner_black, new DataHelper().getTurnOverData());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTurnover.setAdapter(dataAdapter);
        binding.spinnerTurnover.setOnItemSelectedListener(this);
        natureOfBusiness = new ArrayList<>();
        natureOfBusiness.add("Select nature of business");
        natureOfBusiness.addAll(AppConstant.INSTANCE.getNatureOfBusinessList());

        Intent i = getIntent();
        if (i != null) {
            strBusinessName = i.getStringExtra("org_business_name");
            strIncorporationDate = i.getStringExtra("org_incorporation_date");
            strCity = i.getStringExtra("org_city");
            strPinCode = i.getStringExtra("org_pincode");
            strShortDescription = i.getStringExtra("org_short_description");
            strFirstAddressLine = i.getStringExtra("org_registered_address");
            strAboutUs = i.getStringExtra("about_us");
            strBusinessNature = i.getStringExtra("business_nature");
            aggregated_turnover = i.getStringExtra("aggregated_turnover");
            no_of_employees = i.getStringExtra("no_of_employees");
            strState = i.getStringExtra("state");
            try {
                Logger.errorLogger("StateEx", strState);
                int index = 0;
                int state_index = 0;
                for (String state : getResources().getStringArray(R.array.states)) {
                    if (state.equalsIgnoreCase(strState)) {
                        state_index = index;
                    }
                    index++;
                }
                binding.spinnerState.setSelection(state_index);
            } catch (Exception ex) {
                Logger.errorLogger("StateEx", ex.getMessage());
            }
            try {
                Logger.errorLogger("TurnException", aggregated_turnover);
                if (!aggregated_turnover.equalsIgnoreCase("")) {
                    int index = 0;
                    int to_index = 0;
                    for (String to : new DataHelper().getTurnOverData()) {
                        Logger.errorLogger("TurnException", to);
                        if (to.equalsIgnoreCase(aggregated_turnover)) {
                            to_index = index;
                            Logger.errorLogger("TurnException0001", to_index + "");
                        }
                        index++;
                    }
                    Logger.errorLogger("TurnException000", index + "");
                    binding.spinnerTurnover.setSelection(to_index);
                }
            } catch (Exception ex) {
                Logger.errorLogger("TurnException111111", ex.getMessage());
            }
        }

        initLayout();

        if (!StringUtils.isBlank(strBusinessName)) {
            binding.edtBusinessName.setText(strBusinessName);
        }
        if (!StringUtils.isBlank(strIncorporationDate)) {
            binding.edtEstablishDate.setText(strIncorporationDate);
        }
        if (!StringUtils.isBlank(strCity)) {
            binding.edtCity.setText(strCity);
        }
        if (!StringUtils.isBlank(strPinCode)) {
            binding.edtPincode.setText(strPinCode);
        }
        if (!StringUtils.isBlank(strShortDescription)) {
            binding.edtOrganizationHeadline.setText(strShortDescription);
        }
        if (!StringUtils.isBlank(strFirstAddressLine)) {
            binding.edtFirstAddressLine.setText(strFirstAddressLine);
        }
        if (!StringUtils.isBlank(strAboutUs)) {
            binding.edtAboutUs.setText(strAboutUs);
        }
        binding.noOfEmployees.setText(no_of_employees);

        binding.spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strState = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinnerNatureOfBusiness.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    strBusinessNature = "";
                } else {
                    strBusinessNature = AppConstant.INSTANCE.getNatureOfBusinessModelList().get(i - 1).getAction();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.edtAboutUs.setOnTouchListener((view, motionEvent) -> {

            view.getParent().requestDisallowInterceptTouchEvent(true);
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }

            return false;
        });

        binding.edtFirstAddressLine.setOnTouchListener((view, motionEvent) -> {

            view.getParent().requestDisallowInterceptTouchEvent(true);
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }

            return false;
        });

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        binding.edtEstablishDate.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    OrgEditIntroActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, onDateSetListener, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });
        onDateSetListener = (datePicker, year1, month1, dayofmonth) -> {
            month1 = month1 + 1;
            String date = day + "-" + month1 + "-" + year1;
            strIncorporationDate = year1 + "-" + month1 + "-" + day;
            binding.edtEstablishDate.setText(date);
        };


        binding.imgClose.setOnClickListener(view -> finish());
        binding.btnCancel.setOnClickListener(view -> finish());

        binding.btnUpdateIntro.setOnClickListener(view -> {
            if (validate()) {
                updateProfileInfo();
            }
        });


    }

    private void initLayout() {
        binding.spinnerNatureOfBusiness.setAdapter(new ArrayAdapter<>(this, R.layout.single_text_view_spinner_black,
                natureOfBusiness));
        try {
            if (!strBusinessNature.isEmpty()) {
                int index = 0;
                int index_to = 0;
                for (String item : natureOfBusiness) {
                    if (item.contains(strBusinessNature)) {
                        index_to = index;
                    }
                    index++;
                }
                binding.spinnerNatureOfBusiness.setSelection(index_to);
            }
        } catch (Exception exception) {
            strBusinessNature = "";
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        strTurnOver = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private boolean validate() {
        boolean temp = true;
        strBusinessName = binding.edtBusinessName.getText().toString();
        strShortDescription = binding.edtOrganizationHeadline.getText().toString();
        strFirstAddressLine = binding.edtFirstAddressLine.getText().toString();
        strCity = binding.edtCity.getText().toString();
        strPinCode = binding.edtPincode.getText().toString();
        strAboutUs = binding.edtAboutUs.getText().toString();
        aggregated_turnover = binding.spinnerTurnover.getSelectedItem().toString();
        no_of_employees = binding.noOfEmployees.getText().toString();
        if (StringUtils.isBlank(strBusinessName)) {
            Toast.makeText(OrgEditIntroActivity.this, "Enter Business Name", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strShortDescription)) {
            Toast.makeText(OrgEditIntroActivity.this, "Enter Description", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strCity)) {
            Toast.makeText(OrgEditIntroActivity.this, "Enter City Name", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strFirstAddressLine)) {
            Toast.makeText(OrgEditIntroActivity.this, "Enter Address", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strPinCode)) {
            Toast.makeText(OrgEditIntroActivity.this, "Enter Pin Code", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strBusinessNature)) {
            Toast.makeText(OrgEditIntroActivity.this, "Select Nature of Business", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strState)) {
            Toast.makeText(OrgEditIntroActivity.this, "Select State", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(aggregated_turnover) ||
                aggregated_turnover.equalsIgnoreCase("Select Turnover")) {
            Toast.makeText(OrgEditIntroActivity.this, "Select Turnover", Toast.LENGTH_SHORT).show();
            temp = false;
        }
        return temp;
    }

    private void updateProfileInfo() {
        CreateProfileInfoModel createProfileInfoModel = new CreateProfileInfoModel();
        createProfileInfoModel.setLegalName(strBusinessName);
        if (!StringUtils.isBlank(strCity)) {
            createProfileInfoModel.setCity(strCity);
        } else {
            createProfileInfoModel.setCity("");
        }
        if (!StringUtils.isBlank(strShortDescription)) {
            createProfileInfoModel.setShortDescription(strShortDescription);
        } else {
            createProfileInfoModel.setShortDescription("");
        }
        if (!StringUtils.isBlank(strFirstAddressLine)) {
            createProfileInfoModel.setAddressLine1(strFirstAddressLine);
        } else {
            createProfileInfoModel.setAddressLine1("");
        }
        if (!StringUtils.isBlank(strPinCode)) {
            createProfileInfoModel.setPincode(strPinCode);
        } else {
            createProfileInfoModel.setPincode("");
        }
        if (!StringUtils.isBlank(strIncorporationDate)) {
            createProfileInfoModel.setIncorporationDate(strIncorporationDate);
        }
        if (!StringUtils.isBlank(no_of_employees)) {
            createProfileInfoModel.setNoOfEmployees(Integer.valueOf(no_of_employees));
        } else {
            createProfileInfoModel.setNoOfEmployees(0);
        }
        createProfileInfoModel.setState(binding.spinnerState.getSelectedItem().toString());
        createProfileInfoModel.setAboutUs(strAboutUs);
        createProfileInfoModel.setAggregatedTurnover(aggregated_turnover);
        createProfileInfoModel.setBusinessNature(strBusinessNature);

        Call<OrgProfileInfoModel> call = mEquiFaxApiInterface.updateProfileInfo(
                SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN), createProfileInfoModel);
        call.enqueue(new Callback<OrgProfileInfoModel>() {
            @Override
            public void onResponse(Call<OrgProfileInfoModel> call, Response<OrgProfileInfoModel> response) {
                if (response.code() == 200) {
                    OrgProfileInfoModel response1 = response.body();
                    Toast.makeText(OrgEditIntroActivity.this, response1.getMessage(), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    Toast.makeText(OrgEditIntroActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrgProfileInfoModel> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }
}