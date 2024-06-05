package com.app.rupyz.ui.individual;

import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityProfileUpdateBinding;
import com.app.rupyz.generic.helper.ButtonStyleHelper;
import com.app.rupyz.generic.json.JsonHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.individual.ExpInfoModel;
import com.app.rupyz.generic.model.user.UserViewModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.toast.MessageHelper;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.account.dailog.ExperianConsentModal;
import com.app.rupyz.ui.individual.experian.OtpVerifyActivity;
import com.app.rupyz.ui.organization.PANVerifyActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityProfileUpdateBinding binding;
    private UserViewModel mData;
    private ApiInterface mApiInterface;
    final Calendar myCalendar = Calendar.getInstance();
    private Utility mUtil;
    private static final char SEPERATOR = '-';
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private ExpInfoModel mExpData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        binding = ActivityProfileUpdateBinding.inflate(getLayoutInflater());
        mUtil = new Utility(this);
        setContentView(binding.getRoot());
        initLayout();
        initData();
        getProfileData();
    }

    private void initLayout() {
        binding.btnTerms.setOnClickListener(this);
        binding.spinnerState.setAdapter(new ArrayAdapter<String>(this, R.layout.single_text_view_spinner,
                getResources().getStringArray(R.array.states)));
        binding.btnNext.setOnClickListener(this);
        binding.imgCalendar.setOnClickListener(this);
        binding.btnEditPan.setOnClickListener(this);

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel();
            }
        };
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                initProfile();
                break;
            case R.id.btn_terms:
                initBottomSheet();
                break;
            case R.id.img_calendar:
                openCalendar();
                break;
            case R.id.btn_edit_pan:
                Intent intent = new Intent(this, PANVerifyActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void openCalendar() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                ProfileUpdateActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day);
        dialog.updateDate(year - 32, month - 1, day);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void initBottomSheet() {
        Bundle bundle = new Bundle();
        ExperianConsentModal fragment = new ExperianConsentModal();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "");
    }

    private void initProfile() {
        if (mData.getFirst_name().equalsIgnoreCase("")) {
            mData.setFirst_name(binding.txtName.getText().toString().trim());
        }
        if (binding.txtName.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Name Required!", Toast.LENGTH_SHORT).show();
        } else if (binding.txtEmail.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Email Required!", Toast.LENGTH_SHORT).show();
        } else if (binding.txtDob.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "DOB Required!", Toast.LENGTH_SHORT).show();
        } else if (binding.spinnerState.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "State Required!", Toast.LENGTH_SHORT).show();
        } else if (binding.txtCity.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "City Required!", Toast.LENGTH_SHORT).show();
        } else if (binding.txtPinCode.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Pincode Required!", Toast.LENGTH_SHORT).show();
        } else if (binding.txtOrgAddressLineOne.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Address Required!", Toast.LENGTH_SHORT).show();
        } else if (!binding.chbConsent.isChecked()) {
            new MessageHelper().initMessage(getResources().getString(R.string.experian_consent_validation),
                    findViewById(android.R.id.content));
        } else {
            new ButtonStyleHelper(this).initButton(false, binding.btnNext, "Please wait...");
            updateProfile();
        }
    }


    private void initExperian() {

        Call<String> call1 = mApiInterface.initExperian("Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Logger.errorLogger(this.getClass().getName(), response.code() + "");
                new ButtonStyleHelper(ProfileUpdateActivity.this).initButton(true, binding.btnNext,
                        "Next");
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body());
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    Gson gson = new Gson();
                    mExpData = gson.fromJson(jsonObj.get("data"), ExpInfoModel.class);
                    Intent intent = new Intent(ProfileUpdateActivity.this, OtpVerifyActivity.class);
                    intent.putExtra("session_id", mExpData.getExp_data().getSession_id());
                    intent.putExtra("id", mExpData.getId());
                    startActivity(intent);

                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        Logger.errorLogger(this.getClass().getName(), responseBody + "");
                        new SessionHelper(ProfileUpdateActivity.this).requestErrorMessage(
                                responseBody,
                                findViewById(android.R.id.content));
                    } catch (Exception ex) {
                    }
                }
                try {
                    Logger.errorLogger(this.getClass().getName(), response.errorBody().string() + "");
                } catch (Exception ex) {
                    Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
                }
                Logger.errorLogger(this.getClass().getName(), response.body() + "");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                new ButtonStyleHelper(ProfileUpdateActivity.this)
                        .initButton(true, binding.btnNext, "Next");
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    private void initData() {
        try {
            Logger.errorLogger(this.getClass().getName(), getIntent().getExtras().getString("data"));
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObj = (JsonObject) jsonParser.parse(getIntent().getExtras().getString("data"));
            Gson gson = new Gson();
            mData = gson.fromJson(jsonObj.get("data"), UserViewModel.class);
            binding.txtPanCard.setText(mData.getPan_id());
            if (mData.getFirst_name().equalsIgnoreCase("")) {
                binding.txtName.setEnabled(true);
            } else {
                binding.txtName.setText(mData.getFirst_name() + " " + mData.getMiddle_name() + " " + mData.getLast_name());
                binding.txtName.setEnabled(false);
            }
        } catch (Exception ex) {
            binding.txtName.setEnabled(true);
        }
    }


    private void getProfileData() {
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
                    mData = gson.fromJson(jsonObj.get("data"), UserViewModel.class);
                    mData = gson.fromJson(jsonObj.get("data"), UserViewModel.class);
                    binding.txtPanCard.setText(mData.getPan_id());
                    String full_name = mData.getFirst_name() + " " + mData.getMiddle_name() + " " + mData.getLast_name();
                    binding.txtName.setText(full_name.trim());
                    binding.txtEmail.setText(mData.getEmail().trim());
                    try {
                        if (mData.getCity() != null && !mData.getCity().equalsIgnoreCase("")) {
                            binding.txtCity.setText(mData.getCity().trim());
                        }
                        if (mData.getPincode() != null && !mData.getPincode().equalsIgnoreCase("")) {
                            binding.txtPinCode.setText(mData.getPincode().trim());
                        }
                        if (mData.getAddress_line_1() != null && !mData.getAddress_line_1().equalsIgnoreCase("")) {
                            binding.txtOrgAddressLineOne.setText(mData.getAddress_line_1().trim());
                        }
                        if (mData.getAddress_line_2() != null && !mData.getAddress_line_2().equalsIgnoreCase("")) {
                            binding.txtOrgAddressLineTwo.setText(mData.getAddress_line_2().trim());
                        }
                    } catch (Exception ex) {

                    }
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
                        if (mData.getGender().equalsIgnoreCase("Male")) {
                            binding.rdbFemale.setChecked(false);
                            binding.rdbMale.setChecked(true);

                        } else {
                            binding.rdbMale.setChecked(false);
                            binding.rdbFemale.setChecked(true);
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
                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        new SessionHelper(ProfileUpdateActivity.this).requestErrorMessage(
                                responseBody,
                                findViewById(android.R.id.content));
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

    private void updateProfile() {
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
        Call<String> call1 = mApiInterface.profileUpdate(
                JsonHelper.getUpdateProfile(mData.getFirst_name().trim(), mData.getLast_name().trim(), binding.txtEmail.getText().toString().trim(),
                        mData.getPan_id(), mData.getMiddle_name().trim(), dob, binding.txtCity.getText().toString().trim(), binding.spinnerState.getSelectedItem().toString().trim(),
                        binding.txtPinCode.getText().toString().trim(), binding.txtOrgAddressLineOne.getText().toString().trim(),
                        binding.txtOrgAddressLineTwo.getText().toString(), ((RadioButton) findViewById(binding.rdgGender.getCheckedRadioButtonId())).getText().toString(), true), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                new ButtonStyleHelper(ProfileUpdateActivity.this)
                        .initButton(true, binding.btnNext, "Next");
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body());
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    Gson gson = new Gson();
                    mData = gson.fromJson(jsonObj.get("data"), UserViewModel.class);
                    initExperian();
                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        new SessionHelper(ProfileUpdateActivity.this).requestErrorMessage(
                                responseBody,
                                findViewById(android.R.id.content));
                    } catch (Exception ex) {
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                new ButtonStyleHelper(ProfileUpdateActivity.this)
                        .initButton(true, binding.btnNext, "Next");
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }


    private void automateDateEntry() {
        binding.txtDob.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 10) {
                    charSequence = charSequence.subSequence(0, 10);
                    binding.txtDob.removeTextChangedListener(this);
                    binding.txtDob.setText(charSequence);
                    binding.txtDob.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String currText = editable.toString();
                if (currText.isEmpty())
                    return;
                int separatorCount = getOcurrence(currText, SEPERATOR);

                if (currText.charAt(currText.length() - 1) == SEPERATOR) {
                    return;
                }
                int lastSeperatorPos = currText.lastIndexOf(SEPERATOR);
                String previousTxt = null;
                if (lastSeperatorPos > -1) {
                    previousTxt = currText.substring(0, lastSeperatorPos + 1);
                    Log.d("curr text", currText);
                    currText = currText.substring(lastSeperatorPos + 1);
                }

                if (!currText.isEmpty()) {
                    switch (separatorCount) {
                        case 0:
                            int date = Integer.parseInt(currText);
                            if (date < 0 || date > 31) {
                                binding.txtDob.setError("Enter proper date");
                                return;
                            }
                            if (currText.length() == 2 || date >= 4) {
                                currText = String.format(Locale.getDefault(), "%02d", date);
                                currText += SEPERATOR;
                            }
                            break;
                        case 1:
                            int month = Integer.parseInt(currText);
                            if (month < 0 || month > 31) {
                                binding.txtDob.setError("Enter proper month");
                                return;
                            }
                            if (currText.length() == 2 || month >= 2) {
                                currText = String.format(Locale.getDefault(), "%02d", month);
                                currText += SEPERATOR;
                            }
                            break;
                        case 2:
                            int year = Integer.parseInt(currText);
                            if (year < 0) {
                                binding.txtDob.setError("Enter proper year");
                                return;
                            }
                            break;
                    }
                }
                if (previousTxt != null) {
                    currText = previousTxt + currText;
                }
                binding.txtDob.removeTextChangedListener(this);
                binding.txtDob.setText(currText);
                int textLength = currText.length();
                binding.txtDob.setSelection(textLength, textLength);
                binding.txtDob.addTextChangedListener(this);
            }
        });
    }

    private int getOcurrence(String string, char ch) {
        int count = 0;
        int len = string.length();
        for (int i = 0; i < len; i++) {
            if (string.charAt(i) == ch)
                ++count;
        }
        return count;
    }

}