package com.app.rupyz.ui.calculator.interest_rate;

import static com.app.rupyz.generic.utils.SharePrefConstant.EMI_OTHER_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.INTEREST_RATE_ADVANCE_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.INTEREST_RATE_DEPOSIT_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.INTEREST_RATE_INSURANCE_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.INTEREST_RATE_OTHER_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.INTEREST_RATE_PROCESSING_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.INTEREST_RATE_TENURE_TYPE;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityInterestRateCalBinding;
import com.app.rupyz.generic.utils.DecimalDigitsInputFilter;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.ui.calculator.calculator_detail.CalculatorDetailActivity;
import com.app.rupyz.ui.calculator.compare_loan.CompareLoanActivity;

public class InterestRateCalActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityInterestRateCalBinding binding;
    String[] tenure = {"Month", "Year"};
    String[] deposit = {"%", "₹"};
    String[] other = {"₹", "%"};
    String[] advanceEmiArray = {"Month", "₹"};
    String strLoanAmount;
    String strLoanTenure;
    String strEmi;
    String strTime, strProcessingFee, strDeposit, strOtherCharge, strAdvanceEmi, strInsurance;
    int maxLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInterestRateCalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initLayout();
        initToolbar();
    }

    private void initLayout() {
        ArrayAdapter tenureAdapter = new ArrayAdapter(this, R.layout.spinner_calculator_item, tenure);
        tenureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter depositAdapter = new ArrayAdapter(this,  R.layout.spinner_inside_item, deposit);
        depositAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter advanceEmiAdapter = new ArrayAdapter(this,  R.layout.spinner_inside_item, advanceEmiArray);
        advanceEmiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter otherAdapter = new ArrayAdapter(this,  R.layout.spinner_inside_item, other);
        otherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTenure.setAdapter(tenureAdapter);
        binding.spinnerDeposit.setAdapter(otherAdapter);
        binding.spinnerAdvanceEmi.setAdapter(advanceEmiAdapter);
        binding.spinnerInsurance.setAdapter(depositAdapter);
        binding.spinnerProcessingFee.setAdapter(depositAdapter);
        binding.spinnerOtherCharge.setAdapter(otherAdapter);
        binding.btnCalculate.setOnClickListener(this);
        binding.btnCompare.setOnClickListener(this);

        binding.etLoanAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                maxLength = charSequence.length();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        /*binding.spinnerTenure.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    strTime = "MONTH";
                    SharedPref.getInstance().putString(INTEREST_RATE_TENURE_TYPE, strTime);

                } else {
                    strTime = "YEAR";
                    SharedPref.getInstance().putString(INTEREST_RATE_TENURE_TYPE, strTime);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/
        binding.spinnerDeposit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    strDeposit = "Amount";
                    SharedPref.getInstance().putString(INTEREST_RATE_DEPOSIT_TYPE, strDeposit);
                } else {
                    strDeposit = "Percentage";
                    SharedPref.getInstance().putString(INTEREST_RATE_DEPOSIT_TYPE, strDeposit);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });

        binding.spinnerInsurance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    strInsurance = "Percentage";
                    SharedPref.getInstance().putString(INTEREST_RATE_INSURANCE_TYPE, strInsurance);
                    binding.etInsurance.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,2)});
                } else {
                    strInsurance = "Amount";
                    SharedPref.getInstance().putString(INTEREST_RATE_INSURANCE_TYPE, strInsurance);
                    binding.etInsurance.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });
        binding.spinnerProcessingFee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    strProcessingFee = "Percentage";
                    SharedPref.getInstance().putString(INTEREST_RATE_PROCESSING_TYPE, strProcessingFee);
                    binding.etProcessingFees.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,2)});
                } else {
                    strProcessingFee = "Amount";
                    SharedPref.getInstance().putString(INTEREST_RATE_PROCESSING_TYPE, strProcessingFee);
                    binding.etProcessingFees.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });

        binding.spinnerOtherCharge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    strOtherCharge = "Amount";
                    SharedPref.getInstance().putString(INTEREST_RATE_OTHER_TYPE, strOtherCharge);
                    if(maxLength>0){
                        binding.etOtherCharge.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                    }
                    else {
                        binding.etOtherCharge.setFilters(new InputFilter[] {new InputFilter.LengthFilter(5)});
                    }
                }
                else {
                    strOtherCharge = "Percentage";
                    SharedPref.getInstance().putString(INTEREST_RATE_OTHER_TYPE, strOtherCharge);
                    binding.etOtherCharge.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,2)});
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });

        SharedPref.getInstance().putString(INTEREST_RATE_TENURE_TYPE, "YEAR");
        SharedPref.getInstance().putString(INTEREST_RATE_INSURANCE_TYPE, "Amount");
        SharedPref.getInstance().putString(INTEREST_RATE_PROCESSING_TYPE, "Amount");
        SharedPref.getInstance().putString(INTEREST_RATE_OTHER_TYPE, "Amount");
        SharedPref.getInstance().putString(INTEREST_RATE_DEPOSIT_TYPE, "Amount");
        SharedPref.getInstance().putString(INTEREST_RATE_ADVANCE_TYPE, "MONTH");

        strTime = "YEAR";
        binding.tenureToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb) {
                    rb.setTextColor(Color.WHITE);
                    int selectedId = binding.tenureToggle.getCheckedRadioButtonId();
                    if (selectedId == R.id.tenure_month) {
                        strTime = "MONTH";
                        SharedPref.getInstance().putString(INTEREST_RATE_TENURE_TYPE, strTime);
                        binding.tenureMonth.setTextColor(Color.WHITE);
                        binding.tenureYear.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.tenureMonth.setBackground(getDrawable(R.drawable.toggle_left_round));
                        binding.tenureYear.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                    } else if (selectedId == R.id.tenure_year) {
                        strTime = "YEAR";
                        SharedPref.getInstance().putString(INTEREST_RATE_TENURE_TYPE, strTime);
                        binding.tenureMonth.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.tenureYear.setTextColor(Color.WHITE);
                        binding.tenureYear.setBackground(getDrawable(R.drawable.toggle_right_round));
                        binding.tenureMonth.setBackground(getDrawable(R.drawable.toggle_left_round_white));
                    }
                }
            }
        });

        strDeposit = "Amount";
        binding.depositToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb) {
                    rb.setTextColor(Color.WHITE);
                    int selectedId = binding.depositToggle.getCheckedRadioButtonId();
                    if (selectedId == R.id.deposit_percent) {
                        strDeposit = "Percentage";
                        SharedPref.getInstance().putString(INTEREST_RATE_DEPOSIT_TYPE, strDeposit);
                        binding.depositPercent.setTextColor(Color.WHITE);
                        binding.depositAmount.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.depositPercent.setBackground(getDrawable(R.drawable.toggle_left_round));
                        binding.depositAmount.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                    } else if (selectedId == R.id.deposit_amount) {
                        strDeposit = "Amount";
                        SharedPref.getInstance().putString(INTEREST_RATE_DEPOSIT_TYPE, strDeposit);
                        binding.depositPercent.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.depositAmount.setTextColor(Color.WHITE);
                        binding.depositAmount.setBackground(getDrawable(R.drawable.toggle_right_round));
                        binding.depositPercent.setBackground(getDrawable(R.drawable.toggle_left_round_white));
                    }
                }
            }
        });

        strInsurance = "Amount";
        binding.insuranceToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb) {
                    rb.setTextColor(Color.WHITE);
                    int selectedId = binding.insuranceToggle.getCheckedRadioButtonId();
                    if (selectedId == R.id.insurance_percent) {
                        strInsurance = "Percentage";
                        SharedPref.getInstance().putString(INTEREST_RATE_INSURANCE_TYPE, strInsurance);
                        binding.etInsurance.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
                        binding.insurancePercent.setTextColor(Color.WHITE);
                        binding.insuranceAmount.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.insurancePercent.setBackground(getDrawable(R.drawable.toggle_left_round));
                        binding.insuranceAmount.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                    } else if (selectedId == R.id.insurance_amount) {
                        strInsurance = "Amount";
                        SharedPref.getInstance().putString(INTEREST_RATE_INSURANCE_TYPE, strInsurance);
                        int maxLength = binding.etLoanAmount.getText().length();
                        binding.etInsurance.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                        binding.insurancePercent.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.insuranceAmount.setTextColor(Color.WHITE);
                        binding.insuranceAmount.setBackground(getDrawable(R.drawable.toggle_right_round));
                        binding.insurancePercent.setBackground(getDrawable(R.drawable.toggle_left_round_white));
                    }
                }
            }
        });

        strProcessingFee = "Amount";
        binding.processingToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb) {
                    rb.setTextColor(Color.WHITE);
                    int selectedId = binding.processingToggle.getCheckedRadioButtonId();
                    if (selectedId == R.id.processing_percent) {
                        strProcessingFee = "Percentage";
                        SharedPref.getInstance().putString(INTEREST_RATE_PROCESSING_TYPE, strProcessingFee);
                        binding.etProcessingFees.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
                        binding.processingPercent.setTextColor(Color.WHITE);
                        binding.processingAmount.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.processingPercent.setBackground(getDrawable(R.drawable.toggle_left_round));
                        binding.processingAmount.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                    } else if (selectedId == R.id.processing_amount) {
                        strProcessingFee = "Amount";
                        SharedPref.getInstance().putString(INTEREST_RATE_PROCESSING_TYPE, strProcessingFee);
                        int maxLength = binding.etLoanAmount.getText().length();
                        binding.etProcessingFees.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                        binding.processingPercent.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.processingAmount.setTextColor(Color.WHITE);
                        binding.processingAmount.setBackground(getDrawable(R.drawable.toggle_right_round));
                        binding.processingPercent.setBackground(getDrawable(R.drawable.toggle_left_round_white));
                    }
                }
            }
        });

        strOtherCharge = "Amount";
        binding.otherChargeToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb) {
                    rb.setTextColor(Color.WHITE);
                    int selectedId = binding.otherChargeToggle.getCheckedRadioButtonId();
                    if (selectedId == R.id.other_charge_percent) {
                        strOtherCharge = "Percentage";
                        SharedPref.getInstance().putString(EMI_OTHER_TYPE, strOtherCharge);
                        binding.etOtherCharge.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
                        binding.otherChargePercent.setTextColor(Color.WHITE);
                        binding.otherChargeAmount.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.otherChargePercent.setBackground(getDrawable(R.drawable.toggle_left_round));
                        binding.otherChargeAmount.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                    } else if (selectedId == R.id.other_charge_amount) {
                        strOtherCharge = "Amount";
                        SharedPref.getInstance().putString(INTEREST_RATE_OTHER_TYPE, strOtherCharge);
                        int maxLength = binding.etLoanAmount.getText().length();
                        if (maxLength > 0) {
                            binding.etOtherCharge.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                        } else {
                            binding.etOtherCharge.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
                        }
                        binding.otherChargePercent.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.otherChargeAmount.setTextColor(Color.WHITE);
                        binding.otherChargeAmount.setBackground(getDrawable(R.drawable.toggle_right_round));
                        binding.otherChargePercent.setBackground(getDrawable(R.drawable.toggle_left_round_white));
                    }
                }
            }
        });

        strAdvanceEmi = "Month";
        binding.advanceEmiToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb) {
                    rb.setTextColor(Color.WHITE);
                    int selectedId = binding.advanceEmiToggle.getCheckedRadioButtonId();
                    if (selectedId == R.id.advance_emi_amount) {
                        strAdvanceEmi = "Amount";
                        SharedPref.getInstance().putString(INTEREST_RATE_ADVANCE_TYPE, strAdvanceEmi);
                        binding.advanceEmiAmount.setTextColor(Color.WHITE);
                        binding.advanceEmiMonth.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.advanceEmiAmount.setBackground(getDrawable(R.drawable.toggle_left_round));
                        binding.advanceEmiMonth.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                    } else if (selectedId == R.id.advance_emi_month) {
                        strAdvanceEmi = "Month";
                        SharedPref.getInstance().putString(INTEREST_RATE_ADVANCE_TYPE, strAdvanceEmi);
                        binding.advanceEmiAmount.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.advanceEmiMonth.setTextColor(Color.WHITE);
                        binding.advanceEmiMonth.setBackground(getDrawable(R.drawable.toggle_right_round));
                        binding.advanceEmiAmount.setBackground(getDrawable(R.drawable.toggle_left_round_white));
                    }
                }
            }
        });

    }

    private void initToolbar() {
        /*Toolbar toolBar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

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
            case R.id.btn_calculate:
                if (validate()) {

                    double otherCharge, advanceEmi, depositPer, processingFee, insurance;

                    double time = Double.parseDouble(strLoanTenure);
                    if(strTime.equals("MONTH")){
                        time = Double.parseDouble(strLoanTenure);
                    } else {
                        time = time*12;

                    }

                    double totalAmout = time*Double.parseDouble(strEmi);
                    double emi = Double.parseDouble(strEmi);
                    double principal = Double.parseDouble(strLoanAmount);

                    if(!StringUtils.isBlank(binding.etOtherCharge.getText().toString()) && strOtherCharge.equals("Amount")){
                        otherCharge = Double.parseDouble((binding.etOtherCharge.getText().toString()));
                    } else if(!StringUtils.isBlank(binding.etOtherCharge.getText().toString()) && strOtherCharge.equals("Percentage")){
                        if(Double.parseDouble(binding.etOtherCharge.getText().toString())<100){
                            otherCharge = principal*Double.parseDouble((binding.etOtherCharge.getText().toString()))/100;
                        }
                        else {
                            otherCharge=0;
                            Toast.makeText(InterestRateCalActivity.this, getResources().getString(R.string.error_msg_other_charge), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        otherCharge = 0;
                    }
                    if(!StringUtils.isBlank(binding.etAdvanceEmi.getText().toString()) && binding.spinnerAdvanceEmi.getSelectedItemPosition()==0){
                        advanceEmi = emi*Double.parseDouble(binding.etAdvanceEmi.getText().toString());
                    } else if(!StringUtils.isBlank(binding.etAdvanceEmi.getText().toString()) && binding.spinnerAdvanceEmi.getSelectedItemPosition()==1){
                        advanceEmi = Double.parseDouble(binding.etAdvanceEmi.getText().toString());
                    } else {
                        advanceEmi = 0;
                    }
                    if (!StringUtils.isBlank(binding.etDepositPercentage.getText().toString()) && strDeposit.equals("Percentage")){
                        if(Double.parseDouble(binding.etDepositPercentage.getText().toString())<100){
                            depositPer = principal*Double.parseDouble((binding.etDepositPercentage.getText().toString()))/100;
                        }
                        else {
                            depositPer=0;
                            Toast.makeText(InterestRateCalActivity.this, getResources().getString(R.string.error_msg_deposit), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else if (!StringUtils.isBlank(binding.etDepositPercentage.getText().toString()) && strDeposit.equals("Amount")){
                        depositPer = Double.parseDouble(binding.etDepositPercentage.getText().toString());
                    } else {
                        depositPer = 0;
                    } if (!StringUtils.isBlank(binding.etProcessingFees.getText().toString()) && strProcessingFee.equals("Percentage")){
                        if(Double.parseDouble(binding.etProcessingFees.getText().toString())<100){
                            processingFee = principal*Double.parseDouble((binding.etProcessingFees.getText().toString()))/100;
                        }
                        else {
                            processingFee=0;
                            Toast.makeText(InterestRateCalActivity.this, getResources().getString(R.string.error_msg_processing_fee), Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } else if (!StringUtils.isBlank(binding.etProcessingFees.getText().toString()) && strProcessingFee.equals("Amount")){
                        processingFee = Double.parseDouble((binding.etProcessingFees.getText().toString()));
                    } else {
                        processingFee = 0;
                    } if (!StringUtils.isBlank(binding.etInsurance.getText().toString()) && strInsurance.equals("Percentage")){
                        if(Double.parseDouble(binding.etInsurance.getText().toString())<100){
                            insurance = principal*Double.parseDouble((binding.etInsurance.getText().toString()))/100;
                        }
                        else {
                            insurance=0;
                            Toast.makeText(InterestRateCalActivity.this, getResources().getString(R.string.error_msg_insurance), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else if (!StringUtils.isBlank(binding.etInsurance.getText().toString()) && strInsurance.equals("Amount")){
                        insurance = Double.parseDouble((binding.etInsurance.getText().toString()));
                    } else {
                        insurance = 0;
                    }

                    if(Double.parseDouble(strEmi)<Double.parseDouble(strLoanAmount)){
                        if(totalAmout > Double.parseDouble(strLoanAmount)){
                            double interestRate = rate(time, Double.parseDouble(strEmi), Double.parseDouble(strLoanAmount), 0, 0, 0, 0, 0);
                            double effectiveInterestRate = rate(time, Double.parseDouble(strEmi), Double.parseDouble(strLoanAmount), otherCharge, advanceEmi, depositPer, insurance, processingFee);
                            binding.txvTotalPercentage.setText(String.format("%.2f", interestRate) + "%");


                            if(effectiveInterestRate<0){
                                Toast.makeText(InterestRateCalActivity.this, "Please enter valid data", Toast.LENGTH_SHORT).show();
                            }

                            else {
                                Intent intent = new Intent(InterestRateCalActivity.this, CalculatorDetailActivity.class);
                                intent.putExtra("emi", strEmi);
                                intent.putExtra("principle", strLoanAmount);
                                intent.putExtra("interest_rate", String.valueOf(interestRate));
                                intent.putExtra("effective_interest_rate", String.format("%.2f", effectiveInterestRate));
                                intent.putExtra("total_amount", String.format("%,.0f", totalAmout));
                                intent.putExtra("pie_data", (int) ((emi * time) - principal));
                                intent.putExtra("pie_data1", (int) principal);
                                intent.putExtra("other_charge", otherCharge);
                                intent.putExtra("insurance", insurance);
                                intent.putExtra("processing_fee", processingFee);
                                intent.putExtra("SHOW_MATURITY", "FALSE");
                                intent.putExtra("deposit", depositPer);
                                intent.putExtra("time", String.valueOf(time));
                                startActivity(intent);
                            }
                        }

                        else {
                            Toast.makeText(InterestRateCalActivity.this, "Sum of EMI is less than Loan Amount. Please check Loan Tenure and EMI", Toast.LENGTH_SHORT).show();
                        }
                    }

                    else {
                        Toast.makeText(InterestRateCalActivity.this, "EMI can't be greater than Loan Amount", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case R.id.btn_compare:
                if(validate()){
                    double otherCharge, advanceEmi, depositPer, processingFee, insurance;
                    double processingValue, insuranceValue, otherValue, depositValue, advanceEmiValue;
                    int timeValue;
                    double time = Double.parseDouble(strLoanTenure);
                    if(strTime.equals("MONTH")){
                        time = Double.parseDouble(strLoanTenure);
                        //loanTenureValue = Double.parseDouble(strLoanTenure);
                        timeValue = (int) time;
                    } else {
                        timeValue =(int) time;
                        time = time*12;
                        //loanTenureValue = Double.parseDouble(strLoanTenure);

                    }

                    double totalAmout = time*Double.parseDouble(strEmi);
                    double emi = Double.parseDouble(strEmi);
                    double principal = Double.parseDouble(strLoanAmount);

                    if(!StringUtils.isBlank(binding.etOtherCharge.getText().toString()) && strOtherCharge.equals("Amount")){
                        otherCharge = Double.parseDouble((binding.etOtherCharge.getText().toString()));
                        otherValue = Double.parseDouble((binding.etOtherCharge.getText().toString()));
                    } else if(!StringUtils.isBlank(binding.etOtherCharge.getText().toString()) && strOtherCharge.equals("Percentage")){
                        if(Double.parseDouble(binding.etOtherCharge.getText().toString())<100){
                            otherCharge = principal*Double.parseDouble((binding.etOtherCharge.getText().toString()))/100;
                            otherValue = Double.parseDouble((binding.etOtherCharge.getText().toString()));
                        }
                        else {
                            otherCharge=0;
                            otherValue=0;
                            Toast.makeText(InterestRateCalActivity.this, getResources().getString(R.string.error_msg_other_charge), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        otherCharge = 0;
                        otherValue=0;
                    }
                    if(!StringUtils.isBlank(binding.etAdvanceEmi.getText().toString()) && binding.spinnerAdvanceEmi.getSelectedItemPosition()==0){
                        advanceEmi = emi*Double.parseDouble(binding.etAdvanceEmi.getText().toString());
                        advanceEmiValue = Double.parseDouble(binding.etAdvanceEmi.getText().toString());
                    } else if(!StringUtils.isBlank(binding.etAdvanceEmi.getText().toString()) && binding.spinnerAdvanceEmi.getSelectedItemPosition()==1){
                        advanceEmi = Double.parseDouble(binding.etAdvanceEmi.getText().toString());
                        advanceEmiValue = Double.parseDouble(binding.etAdvanceEmi.getText().toString());
                    } else {
                        advanceEmi = 0;
                        advanceEmiValue = 0;
                    }
                    if (!StringUtils.isBlank(binding.etDepositPercentage.getText().toString()) && strDeposit.equals("Percentage")){
                        if(Double.parseDouble(binding.etDepositPercentage.getText().toString())<100){
                            depositPer = principal*Double.parseDouble((binding.etDepositPercentage.getText().toString()))/100;
                            depositValue = Double.parseDouble(binding.etDepositPercentage.getText().toString());
                        }
                        else {
                            depositPer=0;
                            depositValue=0;
                            Toast.makeText(InterestRateCalActivity.this, getResources().getString(R.string.error_msg_deposit), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else if (!StringUtils.isBlank(binding.etDepositPercentage.getText().toString()) && strDeposit.equals("Amount")){
                        depositPer = Double.parseDouble(binding.etDepositPercentage.getText().toString());
                        depositValue = Double.parseDouble(binding.etDepositPercentage.getText().toString());
                    } else {
                        depositPer = 0;
                        depositValue=0;
                    }

                    if (!StringUtils.isBlank(binding.etProcessingFees.getText().toString()) && strProcessingFee.equals("Percentage")){
                        if(Double.parseDouble(binding.etProcessingFees.getText().toString())<100){
                            processingFee = principal*Double.parseDouble((binding.etProcessingFees.getText().toString()))/100;
                            processingValue = Double.parseDouble((binding.etProcessingFees.getText().toString()));
                        }
                        else {
                            processingFee=0;
                            processingValue = 0;
                            Toast.makeText(InterestRateCalActivity.this, getResources().getString(R.string.error_msg_processing_fee), Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } else if (!StringUtils.isBlank(binding.etProcessingFees.getText().toString()) && strProcessingFee.equals("Amount")){
                        processingFee = Double.parseDouble((binding.etProcessingFees.getText().toString()));
                        processingValue = Double.parseDouble((binding.etProcessingFees.getText().toString()));

                    } else {
                        processingFee = 0;
                        processingValue = 0;
                    }

                    if (!StringUtils.isBlank(binding.etInsurance.getText().toString()) && strInsurance.equals("Percentage")){
                        if(Double.parseDouble(binding.etInsurance.getText().toString())<100){
                            insurance = principal*Double.parseDouble((binding.etInsurance.getText().toString()))/100;
                             insuranceValue = Double.parseDouble((binding.etInsurance.getText().toString()));
                        }
                        else {
                            insurance=0;
                            insuranceValue =0;
                            Toast.makeText(InterestRateCalActivity.this, getResources().getString(R.string.error_msg_insurance), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else if (!StringUtils.isBlank(binding.etInsurance.getText().toString()) && strInsurance.equals("Amount")){
                        insurance = Double.parseDouble((binding.etInsurance.getText().toString()));
                        insuranceValue = Double.parseDouble((binding.etInsurance.getText().toString()));
                    } else {
                        insurance = 0;
                        insuranceValue = 0;
                    }

                    if(Double.parseDouble(strEmi)<Double.parseDouble(strLoanAmount)){
                        if(totalAmout > Double.parseDouble(strLoanAmount)){
                            double a = rate(time, Double.parseDouble(strEmi), Double.parseDouble(strLoanAmount), otherCharge, advanceEmi, depositPer, insurance, processingFee);
                            double effectiveInterestRate = rate(time, Double.parseDouble(strEmi), Double.parseDouble(strLoanAmount), otherCharge, advanceEmi, depositPer, insurance, processingFee);
                            binding.txvTotalPercentage.setText(String.format("%.2f", a) + "%");

                            if(effectiveInterestRate>0){
                                Intent intent = new Intent(InterestRateCalActivity.this, CompareLoanActivity.class);
                                intent.putExtra("time", String.format("%.0f", time));
                                intent.putExtra("emi", strEmi);
                                intent.putExtra("principle", strLoanAmount);
                                intent.putExtra("interest_rate", String.valueOf(a));
                                intent.putExtra("total_amount", String.format("%.0f", totalAmout));
                                intent.putExtra("pie_data", (int) ((emi * time) - principal));
                                intent.putExtra("pie_data1", (int) principal);
                                intent.putExtra("processing_fee", String.valueOf(processingFee));
                                intent.putExtra("insurance", String.valueOf(insurance));
                                intent.putExtra("advance_emi", String.valueOf(advanceEmiValue));
                                intent.putExtra("other_charge", String.valueOf(otherCharge));
                                intent.putExtra("deposit", String.valueOf(depositPer));
                                intent.putExtra("processingValue", String.valueOf(processingValue));
                                intent.putExtra("timeValue", String.valueOf(timeValue));
                                intent.putExtra("insuranceValue", String.valueOf(insuranceValue));
                                intent.putExtra("otherValue", String.valueOf(otherValue));
                                intent.putExtra("depositValue", String.valueOf(depositValue));
                                intent.putExtra("other_charge_type", SharedPref.getInstance().getString(INTEREST_RATE_OTHER_TYPE));
                                intent.putExtra("processing_type", SharedPref.getInstance().getString(INTEREST_RATE_PROCESSING_TYPE));
                                intent.putExtra("deposit_type", SharedPref.getInstance().getString(INTEREST_RATE_DEPOSIT_TYPE));
                                intent.putExtra("insurance_type", SharedPref.getInstance().getString(INTEREST_RATE_INSURANCE_TYPE));
                                intent.putExtra("loan_tenure_type", SharedPref.getInstance().getString(INTEREST_RATE_TENURE_TYPE));
                                intent.putExtra("advance_emi_type", SharedPref.getInstance().getString(INTEREST_RATE_ADVANCE_TYPE));
                                //intent.putExtra("interest_rate", String.format("%.2f", a) + "%");
                                //startActivity(new Intent(InterestRateCalActivity.this, CompareLoanActivity.class));
                                startActivity(intent);
                            }

                            else {
                                Toast.makeText(InterestRateCalActivity.this, "Please Enter Valid Data", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else {
                            Toast.makeText(InterestRateCalActivity.this, "Sum of EMI is less than Loan Amount. Please check Loan Tenure and EMI", Toast.LENGTH_SHORT).show();
                        }
                    }

                    else {
                        Toast.makeText(InterestRateCalActivity.this, "EMI can't be greater than Loan Amount", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private boolean validate() {
        boolean temp = true;
        strLoanAmount = binding.etLoanAmount.getText().toString();
        strEmi = binding.etEmi.getText().toString();
        strLoanTenure = binding.etTenure.getText().toString();

        if (StringUtils.isBlank(strLoanAmount)) {
            Toast.makeText(InterestRateCalActivity.this, getResources().getString(R.string.enter_loan_amount), Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strEmi)) {
            Toast.makeText(InterestRateCalActivity.this, getResources().getString(R.string.enter_emi), Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strLoanTenure)) {
            Toast.makeText(InterestRateCalActivity.this, getResources().getString(R.string.enter_loan_tenure), Toast.LENGTH_SHORT).show();
            temp = false;
        }

        return temp;
    }

    public static double rate(double nper, double pmt, double pv, double otherCharge, double advanceEmi, double depositPer, double insurance, double processingFee) {
        double error = 0.0000001;
        double high =  1.00;
        double low = 0.00;

        double deductive = otherCharge + advanceEmi + processingFee + insurance + depositPer;
        pv = pv-deductive;

        double rate = (2.0 * (nper * pmt - pv)) / (pv * nper);

        int itercount = 0;

        while(true) {
            itercount = itercount+1;
            double calc = Math.pow(1 + rate, nper);
            calc = (rate * calc) / (calc - 1.0);
            calc -= pmt / pv;

            if (calc > error) {
                high = rate;
                rate = (high + low) / 2;
            } else if (calc < -error) {
                low = rate;
                rate = (high + low) / 2;
            } else {
                break;
            }
            if(itercount>=100){
                return -1;
            }
        }
        return rate*12*100;
    }
}