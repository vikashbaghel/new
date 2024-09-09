package com.app.rupyz.ui.calculator.emi_cal;

import static com.app.rupyz.generic.utils.SharePrefConstant.EMI_ADVANCE_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.EMI_DEPOSIT_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.EMI_INSURANCE_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.EMI_OTHER_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.EMI_PROCESSING_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.EMI_PROCESSING_VALUE;
import static com.app.rupyz.generic.utils.SharePrefConstant.EMI_TENURE_TYPE;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityEmiCalBinding;
import com.app.rupyz.generic.utils.DecimalDigitsInputFilter;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.ui.calculator.calculator_detail.CalculatorDetailActivity;

public class EmiCalActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityEmiCalBinding binding;
    String strLoanAmount;
    String strLoanTenure;
    String strInterestRate, strTime, strProcessingFee, strDeposit, strOtherCharge, strAdvanceEmi, strInsurance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmiCalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initLayout();
        initToolbar();
    }

    private void initToolbar() {

        Toolbar toolBar = this.findViewById(R.id.toolbar_my);
        ImageView imageViewBack = toolBar.findViewById(R.id.img_back);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");
        imageViewBack.setOnClickListener(view -> onBackPressed());
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
        binding.btnCalculate.setOnClickListener(this);
        binding.btnCompare.setOnClickListener(this);

        SharedPref.getInstance().putString(EMI_TENURE_TYPE, "YEAR");
        SharedPref.getInstance().putString(EMI_INSURANCE_TYPE, "Amount");
        SharedPref.getInstance().putString(EMI_PROCESSING_TYPE, "Amount");
        SharedPref.getInstance().putString(EMI_OTHER_TYPE, "Amount");
        SharedPref.getInstance().putString(EMI_DEPOSIT_TYPE,"Amount");

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
                        SharedPref.getInstance().putString(EMI_TENURE_TYPE, strTime);
                        binding.tenureMonth.setTextColor(Color.WHITE);
                        binding.tenureYear.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.tenureMonth.setBackground(getDrawable(R.drawable.toggle_left_round));
                        binding.tenureYear.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                    } else if (selectedId == R.id.tenure_year) {
                        strTime = "YEAR";
                        SharedPref.getInstance().putString(EMI_TENURE_TYPE, strTime);
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
                        SharedPref.getInstance().putString(EMI_DEPOSIT_TYPE,strDeposit);
                        binding.depositPercent.setTextColor(Color.WHITE);
                        binding.depositAmount.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.depositPercent.setBackground(getDrawable(R.drawable.toggle_left_round));
                        binding.depositAmount.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                    } else if (selectedId == R.id.deposit_amount) {
                        strDeposit = "Amount";
                        SharedPref.getInstance().putString(EMI_DEPOSIT_TYPE,strDeposit);
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
                        SharedPref.getInstance().putString(EMI_INSURANCE_TYPE,strInsurance);
                        binding.etInsurance.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
                        binding.insurancePercent.setTextColor(Color.WHITE);
                        binding.insuranceAmount.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.insurancePercent.setBackground(getDrawable(R.drawable.toggle_left_round));
                        binding.insuranceAmount.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                    } else if (selectedId == R.id.insurance_amount) {
                        strInsurance = "Amount";
                        SharedPref.getInstance().putString(EMI_INSURANCE_TYPE,strInsurance);
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
                        SharedPref.getInstance().putString(EMI_PROCESSING_TYPE, strProcessingFee);
                        binding.etProcessingFees.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
                        binding.processingPercent.setTextColor(Color.WHITE);
                        binding.processingAmount.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.processingPercent.setBackground(getDrawable(R.drawable.toggle_left_round));
                        binding.processingAmount.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                    } else if (selectedId == R.id.processing_amount) {
                        strProcessingFee = "Amount";
                        SharedPref.getInstance().putString(EMI_PROCESSING_TYPE, strProcessingFee);
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
                        SharedPref.getInstance().putString(EMI_OTHER_TYPE, strOtherCharge);
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
                        SharedPref.getInstance().putString(EMI_ADVANCE_TYPE, strAdvanceEmi);
                        binding.advanceEmiAmount.setTextColor(Color.WHITE);
                        binding.advanceEmiMonth.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.advanceEmiAmount.setBackground(getDrawable(R.drawable.toggle_left_round));
                        binding.advanceEmiMonth.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                    } else if (selectedId == R.id.advance_emi_month) {
                        strAdvanceEmi = "Month";
                        SharedPref.getInstance().putString(EMI_ADVANCE_TYPE,strAdvanceEmi);
                        binding.advanceEmiAmount.setTextColor(getResources().getColor(R.color.toogle_unselected_text_color));
                        binding.advanceEmiMonth.setTextColor(Color.WHITE);
                        binding.advanceEmiMonth.setBackground(getDrawable(R.drawable.toggle_right_round));
                        binding.advanceEmiAmount.setBackground(getDrawable(R.drawable.toggle_left_round_white));
                    }
                }
            }
        });

      /*  binding.spinnerAdvanceEmi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    strAdvanceEmi = "Percentage";
                    SharedPref.getInstance().setEmiAdvanceType(strAdvanceEmi);
                } else {
                    strAdvanceEmi = "Amount";
                    SharedPref.getInstance().setEmiAdvanceType(strAdvanceEmi);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });

        binding.etInterestRate.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});


        binding.spinnerTenure.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    strTime = "MONTH";
                    SharedPref.getInstance().setEmiTenureType(strTime);

                } else {
                    strTime = "YEAR";
                    SharedPref.getInstance().setEmiTenureType(strTime);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.spinnerDeposit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    strDeposit = "Amount";
                    SharedPref.getInstance().setEmiDepositType(strDeposit);
                } else {
                    strDeposit = "Percentage";
                    SharedPref.getInstance().setEmiDepositType(strDeposit);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });
        binding.spinnerAdvanceEmi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    strAdvanceEmi = "Percentage";
                    SharedPref.getInstance().setEmiAdvanceType(strAdvanceEmi);
                } else {
                    strAdvanceEmi = "Amount";
                    SharedPref.getInstance().setEmiAdvanceType(strAdvanceEmi);
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
                    SharedPref.getInstance().setEmiInsuranceType(strInsurance);
                    binding.etInsurance.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
                } else {
                    strInsurance = "Amount";
                    SharedPref.getInstance().setEmiInsuranceType(strInsurance);
                    int maxLength = binding.etLoanAmount.getText().length();
                    binding.etInsurance.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
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
                    SharedPref.getInstance().setEmiProcessingType(strProcessingFee);
                    binding.etProcessingFees.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
                } else {
                    strProcessingFee = "Amount";
                    SharedPref.getInstance().setEmiProcessingType(strProcessingFee);
                    int maxLength = binding.etLoanAmount.getText().length();
                    binding.etProcessingFees.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
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
                if (i == 0) {
                    strOtherCharge = "Amount";
                    SharedPref.getInstance().setEmiOtherType(strOtherCharge);
                    int maxLength = binding.etLoanAmount.getText().length();
                    if (maxLength > 0) {
                        binding.etOtherCharge.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                    } else {
                        binding.etOtherCharge.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
                    }

                } else {
                    strOtherCharge = "Percentage";
                    SharedPref.getInstance().setEmiOtherType(strOtherCharge);
                    binding.etOtherCharge.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });
*/
        binding.etInterestRate.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
    }

    private void calculateLoanAmount(int loanAmount) {
        double principal = loanAmount;
        double rate = Double.parseDouble(binding.etInterestRate.getText().toString());
        double time = Double.parseDouble(binding.etTenure.getText().toString());

        double otherCharge, advanceEmi, depositPer, processingFee, insurance;


        if (strTime.equals("MONTH")) {
            time = time;
        } else {
            time = time * 12;
        }

        rate = rate / (12 * 100);

        double emi = (principal * rate * Math.pow(1 + rate, time)) / (Math.pow(1 + rate, time) - 1);

        if (!StringUtils.isBlank(binding.etOtherCharge.getText().toString()) && strOtherCharge.equalsIgnoreCase("Amount")) {
            otherCharge = Double.parseDouble((binding.etOtherCharge.getText().toString()));
        } else if (!StringUtils.isBlank(binding.etOtherCharge.getText().toString()) && strOtherCharge.equalsIgnoreCase("Percentage")) {
            if (Double.parseDouble(binding.etOtherCharge.getText().toString()) < 100) {
                otherCharge = principal * Double.parseDouble((binding.etOtherCharge.getText().toString())) / 100;
            } else {
                otherCharge = 0;
                Toast.makeText(EmiCalActivity.this, getResources().getString(R.string.error_msg_other_charge), Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            otherCharge = 0;
        }
        if (!StringUtils.isBlank(binding.etAdvanceEmi.getText().toString()) && strAdvanceEmi.equalsIgnoreCase("Month")) {
            advanceEmi = emi * Double.parseDouble(binding.etAdvanceEmi.getText().toString());
        } else if (!StringUtils.isBlank(binding.etAdvanceEmi.getText().toString()) && strAdvanceEmi.equalsIgnoreCase("Amount")) {
            advanceEmi = Double.parseDouble(binding.etAdvanceEmi.getText().toString());
        } else {
            advanceEmi = 0;
        }

        if (!StringUtils.isBlank(binding.etDepositPercentage.getText().toString()) && strDeposit.equals("Percentage")) {
            if (Double.parseDouble(binding.etDepositPercentage.getText().toString()) < 100) {
                depositPer = principal * Double.parseDouble((binding.etDepositPercentage.getText().toString())) / 100;
            } else {
                depositPer = 0;
                Toast.makeText(EmiCalActivity.this, getResources().getString(R.string.error_msg_deposit), Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (!StringUtils.isBlank(binding.etDepositPercentage.getText().toString()) && strDeposit.equals("Amount")) {
            depositPer = Double.parseDouble(binding.etDepositPercentage.getText().toString());
        } else {
            depositPer = 0;
        }
        if (!StringUtils.isBlank(binding.etProcessingFees.getText().toString()) && strProcessingFee.equals("Percentage")) {
            if (Double.parseDouble(binding.etProcessingFees.getText().toString()) < 100) {
                processingFee = principal * Double.parseDouble((binding.etProcessingFees.getText().toString())) / 100;
                SharedPref.getInstance().putString(EMI_PROCESSING_VALUE, binding.etProcessingFees.getText().toString());
            } else {
                processingFee = 0;
                SharedPref.getInstance().putString(EMI_PROCESSING_VALUE, "");
                Toast.makeText(EmiCalActivity.this, getResources().getString(R.string.error_msg_processing_fee), Toast.LENGTH_SHORT).show();
                return;
            }

        } else if (!StringUtils.isBlank(binding.etProcessingFees.getText().toString()) && strProcessingFee.equals("Amount")) {
            processingFee = Double.parseDouble((binding.etProcessingFees.getText().toString()));
            SharedPref.getInstance().putString(EMI_PROCESSING_VALUE, binding.etProcessingFees.getText().toString());
        } else {
            processingFee = 0;
            SharedPref.getInstance().putString(EMI_PROCESSING_VALUE,"");
        }
        if (!StringUtils.isBlank(binding.etInsurance.getText().toString()) && strInsurance.equals("Percentage")) {
            if (Double.parseDouble(binding.etInsurance.getText().toString()) < 100) {
                insurance = principal * Double.parseDouble((binding.etInsurance.getText().toString())) / 100;
            } else {
                insurance = 0;
                Toast.makeText(EmiCalActivity.this, getResources().getString(R.string.error_msg_insurance), Toast.LENGTH_SHORT).show();
                return;
            }

        } else if (!StringUtils.isBlank(binding.etInsurance.getText().toString()) && strInsurance.equals("Amount")) {
            insurance = Double.parseDouble((binding.etInsurance.getText().toString()));
        } else {
            insurance = 0;
        }

        double effectiveInterestRate = effectiveRate(time, emi, Double.parseDouble(strLoanAmount), otherCharge, advanceEmi, depositPer, insurance, processingFee);

        if (effectiveInterestRate < 0) {
            Toast.makeText(EmiCalActivity.this, "Please enter valid data", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(EmiCalActivity.this, CalculatorDetailActivity.class);
            intent.putExtra("emi", String.valueOf(emi));
            intent.putExtra("principle", strLoanAmount);
            intent.putExtra("interest_rate", strInterestRate);
            intent.putExtra("effective_interest_rate", String.format("%.2f", effectiveInterestRate));
            intent.putExtra("total_amount", String.format("%,.0f", emi * time));
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

    public void compare(int loanAmount, double otherCharge, double advanceEmi, double depositPer, double processingFee,
                        double insurance, double otherValue, double processingValue, double insuranceValue, double depositValue, double advanceValue, int timeValue) {

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

        Intent intent = new Intent(EmiCalActivity.this, CompareEmiActivity.class);
        intent.putExtra("time", String.format("%.0f", time));
//        intent.putExtra("emi", String.format("%.0f", emi));
        intent.putExtra("principle", strLoanAmount);
        intent.putExtra("interest_rate", strInterestRate);
        intent.putExtra("total_amount", String.format("%,.0f", emi * time));
        intent.putExtra("pie_data", (int) ((emi * time) - principal));
        intent.putExtra("pie_data1", (int) principal);
        intent.putExtra("processing_fee", String.valueOf(processingFee));
        intent.putExtra("insurance", String.valueOf(insurance));
        intent.putExtra("advance_emi", String.valueOf(advanceEmi));
        intent.putExtra("other_charge", String.valueOf(otherCharge));
        intent.putExtra("deposit", String.valueOf(depositPer));
        intent.putExtra("otherValue", String.valueOf(otherValue));
        intent.putExtra("processingValue", String.valueOf(processingValue));
        intent.putExtra("insuranceValue", String.valueOf(insuranceValue));
        intent.putExtra("depositValue", String.valueOf(depositValue));
        intent.putExtra("advanceValue", String.valueOf(advanceValue));
        intent.putExtra("timeValue", String.valueOf(timeValue));
        intent.putExtra("other_charge_type", SharedPref.getInstance().getString(EMI_OTHER_TYPE));
        intent.putExtra("processing_type", SharedPref.getInstance().getString(EMI_PROCESSING_TYPE));
        intent.putExtra("deposit_type", SharedPref.getInstance().getString(EMI_DEPOSIT_TYPE));
        intent.putExtra("insurance_type", SharedPref.getInstance().getString(EMI_INSURANCE_TYPE));
        intent.putExtra("loan_tenure_type", SharedPref.getInstance().getString(EMI_TENURE_TYPE));


        //intent.putExtra("interest_rate", String.format("%.2f", a) + "%");
        //startActivity(new Intent(InterestRateCalActivity.this, CompareLoanActivity.class));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_calculate:
                if (validate()) {

                    double otherCharge, advanceEmi, depositPer, processingFee, insurance, interestRate;
                    int principal;
                    double time = Double.parseDouble(strLoanTenure);
                    if (strTime.equals("MONTH")) {
                        time = time;
                    } else {
                        time = time * 12;
                    }

                    interestRate = Double.parseDouble(strInterestRate);
                    principal = Integer.parseInt(strLoanAmount);
                    calculateLoanAmount(principal);
                }
                break;
            case R.id.btn_compare:
                if (validate()) {
                    int principal;
                    int timeValue;
                    double otherCharge, advanceEmi, depositPer, processingFee, insurance, totalAmout;
                    double otherValue, processingValue, insuranceValue, depositValue, advanceValue;
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

                    if (!StringUtils.isBlank(binding.etAdvanceEmi.getText().toString()) && strAdvanceEmi.equalsIgnoreCase("Month")) {
                        advanceEmi = totalAmout * Double.parseDouble(binding.etAdvanceEmi.getText().toString()) / 100;
                        //advanceEmi = emi * Double.parseDouble(binding.etAdvanceEmi.getText().toString());
                        advanceValue = Double.parseDouble(binding.etAdvanceEmi.getText().toString());
                    } else if (!StringUtils.isBlank(binding.etAdvanceEmi.getText().toString()) && strAdvanceEmi.equalsIgnoreCase("Amount")) {
                        advanceEmi = Double.parseDouble(binding.etAdvanceEmi.getText().toString());
                        advanceValue = Double.parseDouble(binding.etAdvanceEmi.getText().toString());
                    } else {
                        advanceEmi = 0;
                        advanceValue = 0;
                    }
                    if (!StringUtils.isBlank(binding.etDepositPercentage.getText().toString()) && strDeposit.equals("Percentage")) {
                        depositPer = totalAmout * Double.parseDouble((binding.etDepositPercentage.getText().toString())) / 100;
                        depositValue = Double.parseDouble(binding.etDepositPercentage.getText().toString());
                    } else if (!StringUtils.isBlank(binding.etDepositPercentage.getText().toString()) && strDeposit.equals("Amount")) {
                        depositPer = Double.parseDouble(binding.etDepositPercentage.getText().toString());
                        depositValue = Double.parseDouble(binding.etDepositPercentage.getText().toString());
                    } else {
                        depositPer = 0;
                        depositValue = 0;
                    }
                    if (!StringUtils.isBlank(binding.etOtherCharge.getText().toString()) && strOtherCharge.equalsIgnoreCase("Amount")) {
                        otherCharge = Double.parseDouble((binding.etOtherCharge.getText().toString()));
                        otherValue = Double.parseDouble((binding.etOtherCharge.getText().toString()));
                    } else if (!StringUtils.isBlank(binding.etOtherCharge.getText().toString()) && strOtherCharge.equalsIgnoreCase("Percentage")) {
                        if (Double.parseDouble(binding.etOtherCharge.getText().toString()) < 100) {
                            otherCharge = principal * Double.parseDouble((binding.etOtherCharge.getText().toString())) / 100;
                            otherValue = Double.parseDouble((binding.etOtherCharge.getText().toString()));
                        } else {
                            otherCharge = 0;
                            otherValue = 0;
                            Toast.makeText(EmiCalActivity.this, getResources().getString(R.string.error_msg_other_charge), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        otherCharge = 0;
                        otherValue = 0;
                    }
                    if (!StringUtils.isBlank(binding.etProcessingFees.getText().toString()) && strProcessingFee.equals("Percentage")) {
                        if (Double.parseDouble(binding.etProcessingFees.getText().toString()) < 100) {
                            processingFee = principal * Double.parseDouble((binding.etProcessingFees.getText().toString())) / 100;
                            processingValue = Double.parseDouble(binding.etProcessingFees.getText().toString());
                        } else {
                            processingFee = 0;
                            processingValue = 0;
                            Toast.makeText(EmiCalActivity.this, "Please Check Processing Charge", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else if (!StringUtils.isBlank(binding.etProcessingFees.getText().toString()) && strProcessingFee.equals("Amount")) {
                        processingFee = Double.parseDouble((binding.etProcessingFees.getText().toString()));
                        processingValue = Double.parseDouble((binding.etProcessingFees.getText().toString()));

                    } else {
                        processingFee = 0;
                        processingValue = 0;
                    }
                    if (!StringUtils.isBlank(binding.etInsurance.getText().toString()) && strInsurance.equals("Percentage")) {
                        if (Double.parseDouble(binding.etInsurance.getText().toString()) < 100) {
                            insurance = principal * Double.parseDouble((binding.etInsurance.getText().toString())) / 100;
                            insuranceValue = Double.parseDouble((binding.etInsurance.getText().toString()));
                        } else {
                            insurance = 0;
                            insuranceValue = 0;
                            Toast.makeText(EmiCalActivity.this, "Check In", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } else if (!StringUtils.isBlank(binding.etInsurance.getText().toString()) && strInsurance.equals("Amount")) {
                        insurance = Double.parseDouble((binding.etInsurance.getText().toString()));
                        insuranceValue = Double.parseDouble((binding.etInsurance.getText().toString()));

                    } else {
                        insurance = 0;
                        insuranceValue = 0;
                    }

                    compare(principal, otherCharge, advanceEmi, depositPer, processingFee, insurance, otherValue, processingValue, insuranceValue, depositValue, advanceValue, timeValue);
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
            Toast.makeText(EmiCalActivity.this, getResources().getString(R.string.enter_loan_amount), Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strInterestRate)) {
            Toast.makeText(EmiCalActivity.this, getResources().getString(R.string.enter_emi), Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strLoanTenure)) {
            Toast.makeText(EmiCalActivity.this, getResources().getString(R.string.enter_loan_tenure), Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (!StringUtils.isBlank(strInterestRate) && Double.parseDouble(strInterestRate) >= 100) {
            Toast.makeText(EmiCalActivity.this, "Interest rate can't be greater than or equal to 100", Toast.LENGTH_SHORT).show();
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