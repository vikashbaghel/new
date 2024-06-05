package com.app.rupyz.ui.organization;

import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.rupyz.databinding.ActivityReviewBinding;
import com.app.rupyz.generic.helper.ButtonStyleHelper;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.organization.OrgReviewInfoModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.toast.MessageHelper;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.account.dailog.ExperianConsentModal;
import com.app.rupyz.ui.individual.ProfileUpdateActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.app.rupyz.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReviewActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityReviewBinding binding;
    private ApiInterface mApiInterface;
    private EquiFaxReportHelper mReportHelper;
    private Utility mUtil;
    private OrgReviewInfoModel mData;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        mReportHelper = EquiFaxReportHelper.getInstance();
        Logger.errorLogger("ORG_ID", mReportHelper.getOrgId() + "");
        mUtil = new Utility(this);
        initLayout();
        Logger.errorLogger("dfsds", binding.txtDob.getText().toString().trim());
    }

    private void openCalendar() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                ReviewActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day);
        dialog.updateDate(year - 32, month - 1, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                initProfile();
                break;
            case R.id.btn_terms:
                initBottomSheet();
                break;
            case R.id.img_calendar:
                openCalendar();
                break;
        }
    }

    public void initBottomSheet() {
        Bundle bundle = new Bundle();
        ExperianConsentModal fragment = new ExperianConsentModal();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "");
    }


    private void initLayout() {
        binding.btnTerms.setOnClickListener(this);
        binding.btnNext.setOnClickListener(this);
        binding.imgCalendar.setOnClickListener(this);
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel();
            }
        };
        binding.spinnerState.setAdapter(new ArrayAdapter<String>(this, R.layout.single_text_view_spinner,
                getResources().getStringArray(R.array.states)));
        binding.spinnerOrgState.setAdapter(new ArrayAdapter<String>(this, R.layout.single_text_view_spinner,
                getResources().getStringArray(R.array.states)));
        doRequest();
    }

    private void updateLabel() {
        try {
            String myFormat = "yyyy-MM-dd"; //In which you need put here
            String newFormat = "dd-MM-yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            SimpleDateFormat sdf2 = new SimpleDateFormat(newFormat, Locale.US);
            binding.txtDob.setText(sdf2.format(myCalendar.getTime()));
        } catch (Exception ex) {
        }
    }

    private void initData() {
        binding.txtPanCard.setText(mData.getOrganization().getPan_id());
        binding.txtOrgGst.setText(mData.getOrganization().getPrimary_gstin());
        binding.txtOrgLegalName.setText(mData.getOrganization().getLegal_name());
        binding.txtOrgAddressLineOne.setText(mData.getOrganization().getAddress_line_1());
        binding.txtOrgAddressLineTwo.setText(mData.getOrganization().getAddress_line_2());
        binding.txtOrgPinCode.setText(mData.getOrganization().getPincode());
        binding.txtOrgCity.setText(mData.getOrganization().getCity());
        binding.txtOrgMobile.setText(mData.getOrganization().getMobile());
        binding.txtOrgEmail.setText(mData.getOrganization().getEmail());

        binding.txtFirstName.setText(mData.getFirst_name());
        binding.txtLastName.setText(mData.getLast_name());
        binding.txtUserMobile.setText(mData.getMobile());
        binding.txtUserEmail.setText(mData.getEmail());
        binding.txtAddressLineOne.setText(mData.getAddress_line_1());
        binding.txtAddressLineTwo.setText(mData.getAddress_line_2());
        binding.txtPinCode.setText(mData.getPincode());
        binding.txtPanNumber.setText(mData.getPan_id());
        binding.txtCity.setText(mData.getCity());
        try {
            if (mData.getDob() != null && !mData.getDob().equalsIgnoreCase("")) {
                try {
                    String myFormat = "dd-MM-yyyy"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                    String dob = "";
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = dateFormat.parse(mData.getDob().trim());
                    dob = sdf.format(date);
                    binding.txtDob.setText(dob);
                } catch (Exception ex) {
                    Logger.errorLogger("Profile", ex.getMessage());
                }
            }
        } catch (Exception ex) {

        }

        try {
            int index = 0;
            int state_index = 0;
            for (String state : getResources().getStringArray(R.array.states)) {
                if (state.equalsIgnoreCase(mData.getState())) {
                    state_index = index;
                }
                index++;
            }
            binding.spinnerState.setSelection(state_index);
        } catch (Exception ex) {

        }

        try {
            int index = 0;
            int state_index = 0;
            for (String state : getResources().getStringArray(R.array.states)) {
                if (state.equalsIgnoreCase(mData.getOrganization().getState())) {
                    state_index = index;
                }
                index++;
            }
            binding.spinnerOrgState.setSelection(state_index);
        } catch (Exception ex) {

        }
    }

    private void initProfile() {
        if (binding.txtOrgAddressLineOne.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Organisation Address Line One Required", Toast.LENGTH_SHORT).show();
        } else if (binding.txtOrgPinCode.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Organisation Pin code Required!", Toast.LENGTH_SHORT).show();
        } else if (binding.txtOrgCity.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Organisation City Required!", Toast.LENGTH_SHORT).show();
        } else if (binding.spinnerOrgState.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Organisation State Required!", Toast.LENGTH_SHORT).show();
        } else if (binding.txtOrgEmail.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Organisation Email Required!", Toast.LENGTH_SHORT).show();
        } else if (binding.txtPanNumber.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "PAN number Required!", Toast.LENGTH_SHORT).show();
        } else if (binding.txtFirstName.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "First Name Required!", Toast.LENGTH_SHORT).show();
        } else if (binding.txtLastName.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Last Name Required!", Toast.LENGTH_SHORT).show();
        } else if (binding.txtAddressLineOne.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Address Line One Required!", Toast.LENGTH_SHORT).show();
        } else if (binding.txtPinCode.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Pin Code Required!", Toast.LENGTH_SHORT).show();
        } else if (binding.spinnerState.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "State Required!", Toast.LENGTH_SHORT).show();
        } else if (binding.txtDob.getText().toString().trim().equalsIgnoreCase("-  -")) {
            Toast.makeText(this, "Date of Birth Required!", Toast.LENGTH_SHORT).show();
        } else if (binding.txtUserMobile.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Mobile Number Required!", Toast.LENGTH_SHORT).show();
        } else if (binding.txtUserEmail.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Email Required!", Toast.LENGTH_SHORT).show();
        } else if (!binding.chbConsent.isChecked()) {
            new MessageHelper().initMessage(getResources().getString(R.string.equifax_consent_validation),
                    findViewById(android.R.id.content));
        } else {
            new ButtonStyleHelper(this).initButton(false, binding.btnNext, "Please wait...");
            String myFormat = "yyyy-MM-dd"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            String dob = "";
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date date = dateFormat.parse(binding.txtDob.getText().toString().trim());
                dob = sdf.format(date);
            } catch (Exception ex) {
                Logger.errorLogger("Profile", ex.getMessage());
                dob = sdf.format(new Date());
            }
            Logger.errorLogger("Profile", SharedPref.getInstance().getString(TOKEN));
            Logger.errorLogger("Profile", dob);
            mData.getOrganization().setEquifax_consent(true);
            mData.getOrganization().setAddress_line_1(binding.txtOrgAddressLineOne.getText().toString().trim());
            mData.getOrganization().setAddress_line_2(binding.txtOrgAddressLineTwo.getText().toString().trim());
            mData.getOrganization().setPincode(binding.txtOrgPinCode.getText().toString().trim());
            mData.getOrganization().setState(binding.spinnerOrgState.getSelectedItem().toString().trim());
            mData.getOrganization().setCity(binding.txtOrgCity.getText().toString().trim());
            mData.getOrganization().setMobile(binding.txtOrgMobile.getText().toString().trim());
            mData.getOrganization().setEmail(binding.txtOrgEmail.getText().toString().trim());
            mData.setPan_id(binding.txtPanNumber.getText().toString().trim());
            mData.setFirst_name(binding.txtFirstName.getText().toString().trim());
            mData.setLast_name(binding.txtLastName.getText().toString().trim());
            mData.setAddress_line_1(binding.txtAddressLineOne.getText().toString().trim());
            mData.setAddress_line_2(binding.txtAddressLineTwo.getText().toString().trim());
            mData.setPincode(binding.txtPinCode.getText().toString().trim());
            mData.setState(binding.spinnerState.getSelectedItem().toString().trim());
            mData.setCity(binding.txtCity.getText().toString().trim());
            mData.setDob(dob);
            mData.setMobile(binding.txtUserMobile.getText().toString().trim());
            mData.setEmail(binding.txtUserEmail.getText().toString().trim());
            mData.setGender(((RadioButton) findViewById(binding.rdgGender.getCheckedRadioButtonId())).getText().toString());
            mData.getOrganization().setEquifax_consent(true);
            doPostRequest();
        }
    }

    private void doRequest() {
        Call<String> call1 = mApiInterface.getReviewInformation(SharedPref.getInstance().getInt(ORG_ID),
                "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                new ButtonStyleHelper(ReviewActivity.this)
                        .initButton(true, binding.btnNext, "Next");
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body(), response.code());
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    Gson gson = new Gson();
                    mData = gson.fromJson(jsonObj.get("data"), OrgReviewInfoModel.class);
                    initData();
                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        new SessionHelper(ReviewActivity.this).requestErrorMessage(
                                responseBody,
                                findViewById(android.R.id.content));
                    } catch (Exception ex) {
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                new ButtonStyleHelper(ReviewActivity.this)
                        .initButton(true, binding.btnNext, "Next");
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    private void doPostRequest() {
        Logger.errorLogger(this.getClass().getName(), new Gson().toJson(mData));
        Call<String> call1 = mApiInterface.initUpdateInformation(new Gson().toJson(mData),
                "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body(), response.code());
                    Intent intent = new Intent(ReviewActivity.this, EquiFaxOtpActivity.class);
                    intent.putExtra("org_id", mData.getOrg_id());
                    intent.putExtra("is_otp", false);
                    startActivity(intent);
                } else {
                    Logger.errorLogger(this.getClass().getName(), response.errorBody() + "", response.code());
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

}