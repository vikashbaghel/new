package com.app.rupyz.ui.calculator.machinery;

import static com.app.rupyz.generic.utils.SharePrefConstant.EMI_ADVANCE_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.EMI_TENURE_TYPE;
import static com.app.rupyz.generic.utils.Simulation.rateFun;

import android.content.Context;
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
import com.app.rupyz.databinding.ActivityMachineryLoanCalBinding;
import com.app.rupyz.generic.utils.DecimalDigitsInputFilter;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.generic.utils.ToogleHelper;
import com.app.rupyz.ui.calculator.calculator_detail.CalculatorDetailActivity;

public class MachineryLoanCalActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityMachineryLoanCalBinding binding;
    String[] advanceEmiArray = {"Month", "₹"};
    String strLoanAmount;
    String strLoanTenure;
    String strInterestRate, strTime,  strAdvanceEmi, strInterestIncome;
    private Context context;
    ToogleHelper toogleHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        binding = ActivityMachineryLoanCalBinding.inflate(getLayoutInflater());
        toogleHelper = ToogleHelper.getInstance();
        setContentView(binding.getRoot());
        initLayout();
        initToolbar();
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

    private void initLayout() {
        ArrayAdapter advanceEmiAdapter = new ArrayAdapter(this, R.layout.spinner_inside_item, advanceEmiArray);
        advanceEmiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerAdvanceEmi.setAdapter(advanceEmiAdapter);
        binding.btnCalculate.setOnClickListener(this);
        binding.btnCompare.setOnClickListener(this);

        toogleHelper.setTenureType("YEAR");
        toogleHelper.setInsuranceType("Amount");
        toogleHelper.setProcessingType("Amount");
        toogleHelper.setOtherChargeType("Amount");
        toogleHelper.setDepositType("Amount");
        strTime = "YEAR";
        binding.tenureToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb) {
                    rb.setTextColor(Color.WHITE);
                    int selectedId = binding.tenureToggle.getCheckedRadioButtonId();
                    if (selectedId == R.id.tenure_month) {
                        toogleHelper.setTenureType("MONTH");
                        binding.tenureMonth.setTextColor(Color.WHITE);
                        binding.tenureYear.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.tenureMonth.setBackground(getDrawable(R.drawable.toggle_left_round));
                        binding.tenureYear.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                        strTime = "MONTH";
                        SharedPref.getInstance().putString(EMI_TENURE_TYPE, strTime);
                    } else if (selectedId == R.id.tenure_year) {
                        toogleHelper.setTenureType("YEAR");
                        strTime = "YEAR";
                        binding.tenureMonth.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.tenureYear.setTextColor(Color.WHITE);
                        binding.tenureYear.setBackground(getDrawable(R.drawable.toggle_right_round));
                        binding.tenureMonth.setBackground(getDrawable(R.drawable.toggle_left_round_white));
                        SharedPref.getInstance().putString(EMI_TENURE_TYPE, strTime);
                    }
                }
            }
        });

        binding.depositToggle.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = (RadioButton) group.findViewById(checkedId);
            if (null != rb) {
                rb.setTextColor(Color.WHITE);
                int selectedId = binding.depositToggle.getCheckedRadioButtonId();
                if (selectedId == R.id.deposit_percent) {
                    toogleHelper.setDepositType("Percentage");
                    binding.depositPercent.setTextColor(Color.WHITE);
                    binding.depositAmount.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                    binding.depositPercent.setBackground(getDrawable(R.drawable.toggle_left_round));
                    binding.depositAmount.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                } else if (selectedId == R.id.deposit_amount) {
                    toogleHelper.setDepositType("Amount");
                    binding.depositPercent.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                    binding.depositAmount.setTextColor(Color.WHITE);
                    binding.depositAmount.setBackground(getDrawable(R.drawable.toggle_right_round));
                    binding.depositPercent.setBackground(getDrawable(R.drawable.toggle_left_round_white));
                }
            }
        });

        binding.insuranceToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb) {
                    rb.setTextColor(Color.WHITE);
                    int selectedId = binding.insuranceToggle.getCheckedRadioButtonId();
                    if (selectedId == R.id.insurance_percent) {
                        toogleHelper.setInsuranceType("Percentage");
                        binding.insurancePercent.setTextColor(Color.WHITE);
                        binding.insuranceAmount.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.insurancePercent.setBackground(getDrawable(R.drawable.toggle_left_round));
                        binding.insuranceAmount.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                    } else if (selectedId == R.id.insurance_amount) {
                        toogleHelper.setInsuranceType("Amount");
                        binding.insurancePercent.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.insuranceAmount.setTextColor(Color.WHITE);
                        binding.insuranceAmount.setBackground(getDrawable(R.drawable.toggle_right_round));
                        binding.insurancePercent.setBackground(getDrawable(R.drawable.toggle_left_round_white));
                    }
                }
            }
        });

        binding.processingToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb) {
                    rb.setTextColor(Color.WHITE);
                    int selectedId = binding.processingToggle.getCheckedRadioButtonId();
                    if (selectedId == R.id.processing_percent) {
                        toogleHelper.setProcessingType("Percentage");
                        binding.processingPercent.setTextColor(Color.WHITE);
                        binding.processingAmount.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.processingPercent.setBackground(getDrawable(R.drawable.toggle_left_round));
                        binding.processingAmount.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                    } else if (selectedId == R.id.processing_amount) {
                        toogleHelper.setProcessingType("Amount");
                        binding.processingPercent.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.processingAmount.setTextColor(Color.WHITE);
                        binding.processingAmount.setBackground(getDrawable(R.drawable.toggle_right_round));
                        binding.processingPercent.setBackground(getDrawable(R.drawable.toggle_left_round_white));
                    }
                }
            }
        });

        binding.otherChargeToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb) {
                    rb.setTextColor(Color.WHITE);
                    int selectedId = binding.otherChargeToggle.getCheckedRadioButtonId();
                    if (selectedId == R.id.other_charge_percent) {
                        toogleHelper.setOtherChargeType("Percentage");
                        binding.otherChargePercent.setTextColor(Color.WHITE);
                        binding.otherChargeAmount.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.otherChargePercent.setBackground(getDrawable(R.drawable.toggle_left_round));
                        binding.otherChargeAmount.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                    } else if (selectedId == R.id.other_charge_amount) {
                        toogleHelper.setOtherChargeType("Amount");
                        binding.otherChargePercent.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.otherChargeAmount.setTextColor(Color.WHITE);
                        binding.otherChargeAmount.setBackground(getDrawable(R.drawable.toggle_right_round));
                        binding.otherChargePercent.setBackground(getDrawable(R.drawable.toggle_left_round_white));
                    }
                }
            }
        });

        binding.spinnerAdvanceEmi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    strAdvanceEmi = "Percentage";
                    SharedPref.getInstance().putString(EMI_ADVANCE_TYPE,strAdvanceEmi);
                } else {
                    strAdvanceEmi = "Amount";
                    SharedPref.getInstance().putString(EMI_ADVANCE_TYPE,strAdvanceEmi);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });

        binding.etInterestRate.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});


        binding.etDepositPercentage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str;
                if (!StringUtils.isBlank(charSequence.toString())) {
                    int n = charSequence.length();
                    char first = charSequence.charAt(0);
                    Character c1 = new Character(first);
                    Character c2 = new Character('.');

                    if (c1.equals(c2)) {
                        str = "0" + charSequence;
                        toogleHelper.setDepositValue(str);
                    } else {
                        toogleHelper.setDepositValue(charSequence.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.etProcessingFees.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String str;
                if (!StringUtils.isBlank(charSequence.toString())) {
                    int n = charSequence.length();
                    char first = charSequence.charAt(0);
                    Character c1 = new Character(first);
                    Character c2 = new Character('.');

                    if (c1.equals(c2)) {
                        System.out.println("0" + charSequence);
                        str = "0" + charSequence;
                        toogleHelper.setProcessingValue(str);
                    } else {
                        toogleHelper.setProcessingValue(charSequence.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.etInsurance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String str;
                if (!StringUtils.isBlank(charSequence.toString())) {
                    int n = charSequence.length();
                    char first = charSequence.charAt(0);
                    Character c1 = new Character(first);
                    Character c2 = new Character('.');

                    if (c1.equals(c2)) {
                        System.out.println("0" + charSequence);
                        str = "0" + charSequence;
                        toogleHelper.setInsuranceValue(str);
                    } else {
                        toogleHelper.setInsuranceValue(charSequence.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        binding.etOtherCharge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str;
                if (!StringUtils.isBlank(charSequence.toString())) {
                    int n = charSequence.length();
                    char first = charSequence.charAt(0);
                    Character c1 = new Character(first);
                    Character c2 = new Character('.');

                    if (c1.equals(c2)) {
                        System.out.println("0" + charSequence);
                        str = "0" + charSequence;
                        toogleHelper.setOtherChargeValue(str);
                    } else {
                        toogleHelper.setOtherChargeValue(charSequence.toString());
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.etInterestIncome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!StringUtils.isBlank(charSequence.toString())) {

                }

                String str;
                if (!StringUtils.isBlank(charSequence.toString())) {
                    int n = charSequence.length();
                    char first = charSequence.charAt(0);
                    Character c1 = new Character(first);
                    Character c2 = new Character('.');

                    if (c1.equals(c2)) {
                        System.out.println("0" + charSequence);
                        str = "0" + charSequence;
                        toogleHelper.setInterestIncomeValue(str);
                    } else {
                        toogleHelper.setInterestIncomeValue(charSequence.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void calculateLoanAmount(int loanAmount) {
        double principal = loanAmount;
        double rate = Double.parseDouble(binding.etInterestRate.getText().toString());
        //double time = Double.parseDouble(binding.etTenure.getText().toString());
        double time = Double.parseDouble(binding.etTenure.getText().toString());

        double otherCharge = 0, advanceEmi, depositPer = 0, processingFee = 0, insurance = 0, interestIncome = 0;

        time = toogleHelper.getTenureValue();

        rate = rate / (12 * 100);

        double emi = (principal * rate * Math.pow(1 + rate, time)) / (Math.pow(1 + rate, time) - 1);

        if (!StringUtils.isBlank(binding.etAdvanceEmi.getText().toString()) && binding.spinnerAdvanceEmi.getSelectedItemPosition() == 0) {
            advanceEmi = emi * Double.parseDouble(binding.etAdvanceEmi.getText().toString());
        } else if (!StringUtils.isBlank(binding.etAdvanceEmi.getText().toString()) && binding.spinnerAdvanceEmi.getSelectedItemPosition() == 1) {
            advanceEmi = Double.parseDouble(binding.etAdvanceEmi.getText().toString());
        } else {
            advanceEmi = 0;
        }

        if (!StringUtils.isBlank(binding.etInterestIncome.getText().toString())) {
            if (Double.parseDouble(binding.etInterestIncome.getText().toString()) < 100) {
                interestIncome = principal * Double.parseDouble((binding.etInterestIncome.getText().toString())) / 100;
            } else {
                interestIncome = 0;
                Toast.makeText(context, getResources().getString(R.string.error_msg_insurance), Toast.LENGTH_SHORT).show();
                return;
            }
        }


        if (StringUtils.isBlank(binding.etOtherCharge.getText().toString())) {
            toogleHelper.setOtherChargeValue("0");
        }

        if (StringUtils.isBlank(binding.etProcessingFees.getText().toString())) {
            toogleHelper.setProcessingValue("0");
        }

        if (StringUtils.isBlank(binding.etInsurance.getText().toString())) {
            toogleHelper.setInsuranceValue("0");
        }

        int type = 0;

        double deductable = toogleHelper.getProcessingValue() + toogleHelper.getInsuranceValue() + toogleHelper.getOtherChargeValue();

        double netCashInFlow = Double.parseDouble(toogleHelper.getPrincipal()) - deductable;

        double futureValue = (Double.parseDouble(toogleHelper.getPrincipal()) * toogleHelper.getDepositValue() / 100 * (Math.pow(1 + toogleHelper.getInterestIncomeValue() / 100, toogleHelper.getTenureValue() / 12)));

        double effectiveRate = rateFun(toogleHelper.getTenureValue(), emi, -netCashInFlow, futureValue, type);

        System.out.println("DATAAA" + time + "_" + emi + "_" + netCashInFlow + "_" + futureValue);

        double depositAmount = Double.parseDouble(toogleHelper.getPrincipal()) * toogleHelper.getDepositValue() / 100;

        if (effectiveRate < 0) {
            Toast.makeText(context, "Please enter valid data", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(context, CalculatorDetailActivity.class);
            intent.putExtra("emi", String.valueOf(emi));
            intent.putExtra("principle", strLoanAmount);
            intent.putExtra("interest_rate", strInterestRate);
            intent.putExtra("effective_interest_rate", String.format("%.2f", effectiveRate));
            intent.putExtra("total_amount", String.format("%,.0f", emi * time));
            intent.putExtra("pie_data", (int) ((emi * time) - principal));
            intent.putExtra("pie_data1", (int) principal);
            intent.putExtra("other_charge", otherCharge);
            intent.putExtra("insurance", insurance);
            intent.putExtra("processing_fee", toogleHelper.getProcessingValue());
            intent.putExtra("deposit", depositPer);
            intent.putExtra("time", String.valueOf(toogleHelper.getTenureValue()));
            intent.putExtra("SHOW_MATURITY", "TRUE");
            intent.putExtra("maturity", futureValue);
            intent.putExtra("deposit_value", depositAmount);
            startActivity(intent);
        }
    }

    public void compare(int loanAmount, double otherCharge, double advanceEmi, double depositPer,
                        double processingFee, double insurance, double otherValue, double processingValue,
                        double insuranceValue, double depositValue, double advanceValue, int timeValue,
                        double interest_income) {

        double principal = loanAmount;
        double rate = Double.parseDouble(binding.etInterestRate.getText().toString());
        double time = Double.parseDouble(binding.etTenure.getText().toString());

        if (strTime.equals("MONTH")) {
            time = time;
        } else {
            time = time * 12;
        }

        rate = rate / (12 * 100);

        double emi = (principal * rate * Math.pow(1 + rate, time)) / (Math.pow(1 + rate, time) - 1);


        System.out.println("ADVANCE EMI " + advanceEmi);
        System.out.println("OTHER CHARGE " + otherCharge);
        System.out.println("OTHER CHARGE " + depositPer);

        Intent intent = new Intent(context, CompareMachinaryActivity.class);
        intent.putExtra("time", String.format("%.0f", time));
//        intent.putExtra("emi", String.format("%.0f", emi));
        intent.putExtra("principle", strLoanAmount);
        intent.putExtra("interest_rate", strInterestRate);
        intent.putExtra("total_amount", String.format("%,.0f", emi * time));
        intent.putExtra("pie_data", (int) ((emi * time) - principal));
        intent.putExtra("pie_data1", (int) principal);
        intent.putExtra("processing_fee", String.valueOf(processingFee));
        intent.putExtra("insurance", toogleHelper.getShowInsurance());
        intent.putExtra("advance_emi", String.valueOf(advanceEmi));
        intent.putExtra("other_charge", String.valueOf(otherCharge));
        intent.putExtra("deposit", String.valueOf(depositPer));
        intent.putExtra("otherValue", String.valueOf(otherValue));
        intent.putExtra("processingValue", String.valueOf(processingValue));
        intent.putExtra("insuranceValue", String.valueOf(insuranceValue));
        intent.putExtra("depositValue", String.valueOf(depositValue));
        intent.putExtra("advanceValue", String.valueOf(advanceValue));
        intent.putExtra("timeValue", String.valueOf(timeValue));
        intent.putExtra("interest_income", String.valueOf(interest_income));
        intent.putExtra("other_charge_type", toogleHelper.getOtherChargeType());
        intent.putExtra("processing_type", toogleHelper.getProcessingType());
        intent.putExtra("deposit_type", toogleHelper.getDepositType());
        intent.putExtra("insurance_type", toogleHelper.getInsuranceType());
        intent.putExtra("loan_tenure_type", toogleHelper.getTenureType());

        //intent.putExtra("interest_rate", String.format("%.2f", a) + "%");
        //startActivity(new Intent(InterestRateCalActivity.this, CompareLoanActivity.class));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_calculate:
                if (validate()) {

                    toogleHelper.setTenureValue(binding.etTenure.getText().toString());
                    toogleHelper.setPrincipal(binding.etLoanAmount.getText().toString());

                    double otherCharge, advanceEmi, depositPer, processingFee, insurance, interestRate;
                    int principal;
                    double time = Double.parseDouble(strLoanTenure);
                    if (strTime.equals("MONTH")) {
                        time = time;
                    } else {
                        time = time * 12;
                    }


                    time = toogleHelper.getTenureValue();

                    interestRate = Double.parseDouble(strInterestRate);
                    principal = Integer.parseInt(toogleHelper.getPrincipal());


                    calculateLoanAmount(principal);
                }
                break;
            case R.id.btn_compare:
                if (validate()) {
                    int principal;
                    int timeValue;
                    double otherCharge = 0, advanceEmi, depositPer, processingFee = 0, insurance = 0, totalAmout;
                    double otherValue = 0, processingValue = 0, insuranceValue = 0, depositValue = 0, advanceValue, interest_income;

                    double time = Double.parseDouble(strLoanTenure);
                    if (strTime.equals("MONTH")) {
                        time = time;
                        timeValue = (int) time;
                    } else {
                        timeValue = (int) time;
                        time = time * 12;
                    }

                    principal = Integer.parseInt(strLoanAmount);
                    totalAmout = Double.parseDouble(strLoanAmount);

                    if (!StringUtils.isBlank(binding.etAdvanceEmi.getText().toString()) && binding.spinnerAdvanceEmi.getSelectedItemPosition() == 0) {
                        advanceEmi = totalAmout * Double.parseDouble(binding.etAdvanceEmi.getText().toString()) / 100;
                        advanceValue = Double.parseDouble(binding.etAdvanceEmi.getText().toString());
                    } else if (!StringUtils.isBlank(binding.etAdvanceEmi.getText().toString()) && binding.spinnerAdvanceEmi.getSelectedItemPosition() == 1) {
                        advanceEmi = Double.parseDouble(binding.etAdvanceEmi.getText().toString());
                        advanceValue = Double.parseDouble(binding.etAdvanceEmi.getText().toString());
                    } else {
                        advanceEmi = 0;
                        advanceValue = 0;
                    }
                    if (!StringUtils.isBlank(binding.etDepositPercentage.getText().toString()) && toogleHelper.getDepositType().equalsIgnoreCase("Percentage")) {
                        depositPer = totalAmout * Double.parseDouble((binding.etDepositPercentage.getText().toString())) / 100;
                        depositValue = Double.parseDouble(binding.etDepositPercentage.getText().toString());
                    } else if (!StringUtils.isBlank(binding.etDepositPercentage.getText().toString()) && toogleHelper.getDepositType().equalsIgnoreCase("Amount")) {
                        depositPer = Double.parseDouble(binding.etDepositPercentage.getText().toString());
                        depositValue = Double.parseDouble(binding.etDepositPercentage.getText().toString());
                    } else {
                        depositPer = 0;
                        depositValue = 0;
                    }
                    /*if (!StringUtils.isBlank(binding.etOtherCharge.getText().toString()) && binding.spinnerOtherCharge.getSelectedItemPosition() == 0) {
                        otherCharge = Double.parseDouble((binding.etOtherCharge.getText().toString()));
                        otherValue = Double.parseDouble((binding.etOtherCharge.getText().toString()));
                    } else if (!StringUtils.isBlank(binding.etOtherCharge.getText().toString()) && binding.spinnerOtherCharge.getSelectedItemPosition() == 1) {
                        if (Double.parseDouble(binding.etOtherCharge.getText().toString()) < 100) {
                            otherCharge = principal * Double.parseDouble((binding.etOtherCharge.getText().toString())) / 100;
                            otherValue = Double.parseDouble((binding.etOtherCharge.getText().toString()));
                        } else {
                            otherCharge = 0;
                            otherValue = 0;
                            Toast.makeText(context, getResources().getString(R.string.error_msg_other_charge), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        otherCharge = 0;
                        otherValue = 0;
                    }*/
                    /*if (!StringUtils.isBlank(binding.etProcessingFees.getText().toString()) && strProcessingFee.equals("Percentage")) {
                        if (Double.parseDouble(binding.etProcessingFees.getText().toString()) < 100) {
                            processingFee = principal * Double.parseDouble((binding.etProcessingFees.getText().toString())) / 100;
                            processingValue = Double.parseDouble(binding.etProcessingFees.getText().toString());
                        } else {
                            processingFee = 0;
                            processingValue = 0;
                            Toast.makeText(context, "Please Check Processing Charge", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else if (!StringUtils.isBlank(binding.etProcessingFees.getText().toString()) && strProcessingFee.equals("Amount")) {
                        processingFee = Double.parseDouble((binding.etProcessingFees.getText().toString()));
                        processingValue = Double.parseDouble((binding.etProcessingFees.getText().toString()));

                    } else {
                        processingFee = 0;
                        processingValue =0;
                    }*/
                    /*if (!StringUtils.isBlank(binding.etInsurance.getText().toString()) && strInsurance.equals("Percentage")) {
                        if (Double.parseDouble(binding.etInsurance.getText().toString()) < 100) {
                            insurance = principal * Double.parseDouble((binding.etInsurance.getText().toString())) / 100;
                            insuranceValue = Double.parseDouble((binding.etInsurance.getText().toString()));
                        } else {
                            insurance = 0;
                            insuranceValue = 0;
                            Toast.makeText(context, "Check In", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } else if (!StringUtils.isBlank(binding.etInsurance.getText().toString()) && strInsurance.equals("Amount")) {
                        insurance = Double.parseDouble((binding.etInsurance.getText().toString()));
                        insuranceValue = Double.parseDouble((binding.etInsurance.getText().toString()));

                    } else {
                        insurance = 0;
                        insuranceValue=0;
                    }*/

                    if (!StringUtils.isBlank(binding.etInterestIncome.getText().toString())) {
                        if (Double.parseDouble(binding.etInterestIncome.getText().toString()) < 100) {
                            interest_income = principal * Double.parseDouble((binding.etInterestIncome.getText().toString())) / 100;
                        } else {
                            interest_income = 0;
                            return;
                        }

                    } else if (!StringUtils.isBlank(binding.etInterestIncome.getText().toString()) && strInterestIncome.equals("Amount")) {
                        interest_income = Double.parseDouble((binding.etInterestIncome.getText().toString()));
                    } else {
                        interest_income = 0;
                    }

                   /* double nper = 120;
                    double pmt = 121327;
                    double pv = 10000000;
                    double deposit_per = 30;
                    int type=0;
                    double guess=0.1;
                    double interest_income = 5;

                    double deductable = processingFee + insurance + otherCharge;



                    double netCashInFlow = principal - deductable;



                    double futureValue = (pv*deposit_per/100*(Math.pow(1 + interest_income/100, nper/12)));

                    double effectiveRate = rateFun(nper, emi, -netCashInFlow, futureValue, type);*/


                    compare(principal, otherCharge, advanceEmi, depositValue,
                            processingFee, insurance, toogleHelper.getShowOtherCharge(), toogleHelper.getShowProcessingFee(), toogleHelper.getShowInsurance(),
                            depositValue, advanceValue, timeValue, toogleHelper.getInterestIncomeValue());
                }
                break;
        }
    }

    private boolean validate() {
        boolean temp = true;
        strLoanAmount = binding.etLoanAmount.getText().toString();
        strInterestRate = binding.etInterestRate.getText().toString();
        strLoanTenure = binding.etTenure.getText().toString();


        if (StringUtils.isBlank(strLoanAmount)) {
            Toast.makeText(context, getResources().getString(R.string.enter_loan_amount), Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strInterestRate)) {
            Toast.makeText(context, getResources().getString(R.string.enter_emi), Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strLoanTenure)) {
            Toast.makeText(context, getResources().getString(R.string.enter_loan_tenure), Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (!StringUtils.isBlank(strInterestRate) && Double.parseDouble(strInterestRate) >= 100) {
            Toast.makeText(context, "Interest rate can't be greater than or equal to 100", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strTime)) {
            Toast.makeText(context, getResources().getString(R.string.enter_loan_tenure), Toast.LENGTH_SHORT).show();
            temp = false;
        }
        return temp;
    }

    public static double effectiveRate(double nper, double pmt, double pv, double otherCharge, double advanceEmi, double depositPer, double insurance, double processingFee) {
        double error = 0.0000001;
        double high = 1.00;
        double low = 0.00;

        double deductive = otherCharge + advanceEmi + processingFee + insurance + depositPer;
        pv = pv - deductive;

        double rate = (2.0 * (nper * pmt - pv)) / (pv * nper);

        int itercount = 0;
        while (true) {
            itercount = itercount + 1;
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
            if (itercount >= 100) {
                return -1;
            }
        }
        return rate * 12 * 100;
    }


}