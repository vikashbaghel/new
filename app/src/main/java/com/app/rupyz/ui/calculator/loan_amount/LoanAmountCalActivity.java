package com.app.rupyz.ui.calculator.loan_amount;

import static com.app.rupyz.generic.utils.SharePrefConstant.EMI_PROCESSING_VALUE;
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
import com.app.rupyz.databinding.ActivityLoanAmountCalBinding;
import com.app.rupyz.generic.utils.DecimalDigitsInputFilter;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.ui.calculator.calculator_detail.CalculatorDetailActivity;

public class LoanAmountCalActivity extends AppCompatActivity implements View.OnClickListener{
    ActivityLoanAmountCalBinding binding;
    String strEmi;
    String strLoanTenure;
    String strInterestRate, strTime, strProcessingFee, strDeposit, strOtherCharge, strAdvanceEmi, strInsurance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoanAmountCalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initLayout();
        initToolbar();
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

    private void initLayout() {

        binding.btnCalculate.setOnClickListener(this);
        binding.btnCompare.setOnClickListener(this);

        strTime = "YEAR";

        binding.tenureToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb) {
                    rb.setTextColor(Color.WHITE);
                    int selectedId = binding.tenureToggle.getCheckedRadioButtonId();
                    if (selectedId == R.id.tenure_month) {
                        binding.tenureMonth.setTextColor(Color.WHITE);
                        binding.tenureYear.setTextColor(Color.BLACK);
                        binding.tenureMonth.setBackground(getDrawable(R.drawable.toggle_left_round));
                        binding.tenureYear.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                        strTime = "MONTH";
                    } else if (selectedId == R.id.tenure_year) {
                        strTime = "YEAR";
                        binding.tenureMonth.setTextColor(Color.BLACK);
                        binding.tenureYear.setTextColor(Color.WHITE);
                        binding.tenureYear.setBackground(getDrawable(R.drawable.toggle_right_round));
                        binding.tenureMonth.setBackground(getDrawable(R.drawable.toggle_left_round_white));
                    }
                }
            }
        });

        /*binding.spinnerAdvanceEmi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
        });*/

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
                        binding.processingPercent.setTextColor(Color.WHITE);
                        binding.processingAmount.setTextColor(Color.BLACK);
                        binding.processingPercent.setBackground(getDrawable(R.drawable.toggle_left_round));
                        binding.processingAmount.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                    } else if (selectedId == R.id.processing_amount) {
                        strProcessingFee = "Amount";
                        binding.processingPercent.setTextColor(Color.BLACK);
                        binding.processingAmount.setTextColor(Color.WHITE);
                        binding.processingAmount.setBackground(getDrawable(R.drawable.toggle_right_round));
                        binding.processingPercent.setBackground(getDrawable(R.drawable.toggle_left_round_white));
                    }
                }
            }
        });

        binding.etInterestRate.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,2)});
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
                        binding.insurancePercent.setTextColor(Color.WHITE);
                        binding.insuranceAmount.setTextColor(Color.BLACK);
                        binding.insurancePercent.setBackground(getDrawable(R.drawable.toggle_left_round));
                        binding.insuranceAmount.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                    } else if (selectedId == R.id.insurance_amount) {
                        strInsurance = "Amount";
                        binding.insurancePercent.setTextColor(Color.BLACK);
                        binding.insuranceAmount.setTextColor(Color.WHITE);
                        binding.insuranceAmount.setBackground(getDrawable(R.drawable.toggle_right_round));
                        binding.insurancePercent.setBackground(getDrawable(R.drawable.toggle_left_round_white));
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
                        binding.otherChargePercent.setTextColor(Color.WHITE);
                        binding.otherChargeAmount.setTextColor(Color.BLACK);
                        binding.otherChargePercent.setBackground(getDrawable(R.drawable.toggle_left_round));
                        binding.otherChargeAmount.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                    } else if (selectedId == R.id.other_charge_amount) {
                        strOtherCharge = "Amount";
                        binding.otherChargePercent.setTextColor(Color.BLACK);
                        binding.otherChargeAmount.setTextColor(Color.WHITE);
                        binding.otherChargeAmount.setBackground(getDrawable(R.drawable.toggle_right_round));
                        binding.otherChargePercent.setBackground(getDrawable(R.drawable.toggle_left_round_white));
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
                        binding.depositPercent.setTextColor(Color.WHITE);
                        binding.depositAmount.setTextColor(Color.BLACK);
                        binding.depositPercent.setBackground(getDrawable(R.drawable.toggle_left_round));
                        binding.depositAmount.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                    } else if (selectedId == R.id.deposit_amount) {
                         strDeposit = "Amount";
                        binding.depositPercent.setTextColor(Color.BLACK);
                        binding.depositAmount.setTextColor(Color.WHITE);
                        binding.depositAmount.setBackground(getDrawable(R.drawable.toggle_right_round));
                        binding.depositPercent.setBackground(getDrawable(R.drawable.toggle_left_round_white));
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
                        binding.advanceEmiAmount.setTextColor(Color.WHITE);
                        binding.advanceEmiMonth.setTextColor(Color.BLACK);
                        binding.advanceEmiAmount.setBackground(getDrawable(R.drawable.toggle_left_round));
                        binding.advanceEmiMonth.setBackground(getDrawable(R.drawable.toggle_left_round_white));
                    } else if (selectedId == R.id.advance_emi_month) {
                        strAdvanceEmi = "Month";
                        binding.advanceEmiAmount.setTextColor(Color.BLACK);
                        binding.advanceEmiMonth.setTextColor(Color.WHITE);
                        binding.advanceEmiMonth.setBackground(getDrawable(R.drawable.toggle_right_round));
                        binding.advanceEmiAmount.setBackground(getDrawable(R.drawable.toggle_right_round_white));
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.btn_calculate:
                if (validate()) {
                    double tenure = Double.parseDouble(strLoanTenure);
                    if (strTime.equals("MONTH")) {
                        tenure = tenure;
                    } else {
                        tenure = tenure * 12;
                    }

                    double rate = Double.parseDouble(strInterestRate);
                    rate = rate / (12 * 100);
                    double emi = Double.parseDouble(strEmi);


                    System.out.println("YA"+ payout(rate, 1, emi));

                    int principal = (int) payout(rate, tenure, emi);

                    calculateLoanAmount(principal, emi, tenure);
                }
                break;
        }
    }


    private boolean validate() {
        boolean temp = true;
        strEmi = binding.etEmi.getText().toString();
        strInterestRate = binding.etInterestRate.getText().toString();
        strLoanTenure = binding.etTenure.getText().toString();

        if (StringUtils.isBlank(strEmi)) {
            Toast.makeText(LoanAmountCalActivity.this, getResources().getString(R.string.enter_emi), Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strInterestRate)) {
            Toast.makeText(LoanAmountCalActivity.this, getResources().getString(R.string.enter_interest_rate), Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strLoanTenure)) {
            Toast.makeText(LoanAmountCalActivity.this, getResources().getString(R.string.enter_loan_tenure), Toast.LENGTH_SHORT).show();
            temp = false;
        } else if(!StringUtils.isBlank(strInterestRate) && Double.parseDouble(strInterestRate)>=100){
            Toast.makeText(LoanAmountCalActivity.this, "Interest rate can't be greater than or equal to 100", Toast.LENGTH_SHORT).show();
            temp = false;
        }
        return temp;
    }

    public static double payout(double rate, double time, double emi){
        //double principal = (principal * rate * Math.pow(1 + rate, time)) / (Math.pow(1 + rate, time) - 1);
        double principal = (emi*(Math.pow(1 + rate, time) - 1)) / ((Math.pow(1 + rate, time))*rate);
        return principal;
    }

    private void calculateLoanAmount(int loanAmount, double emiAmount, double tenure) {
        double principal = loanAmount;
        double emi = emiAmount;
        double rate = Double.parseDouble(binding.etInterestRate.getText().toString());
        double time = tenure;

        double otherCharge, advanceEmi, depositPer, processingFee, insurance;

        if (!StringUtils.isBlank(binding.etOtherCharge.getText().toString()) && binding.spinnerOtherCharge.getSelectedItemPosition() == 0) {
            otherCharge = Double.parseDouble((binding.etOtherCharge.getText().toString()));
        } else if (!StringUtils.isBlank(binding.etOtherCharge.getText().toString()) && binding.spinnerOtherCharge.getSelectedItemPosition() == 1) {
            if (Double.parseDouble(binding.etOtherCharge.getText().toString()) < 100) {
                otherCharge = principal * Double.parseDouble((binding.etOtherCharge.getText().toString())) / 100;
            } else {
                otherCharge = 0;
                Toast.makeText(LoanAmountCalActivity.this, getResources().getString(R.string.error_msg_other_charge), Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            otherCharge = 0;
        }
        if (!StringUtils.isBlank(binding.etAdvanceEmi.getText().toString()) && binding.spinnerAdvanceEmi.getSelectedItemPosition() == 0) {
            advanceEmi = emi * Double.parseDouble(binding.etAdvanceEmi.getText().toString());
        } else if (!StringUtils.isBlank(binding.etAdvanceEmi.getText().toString()) && binding.spinnerAdvanceEmi.getSelectedItemPosition() == 1) {
            advanceEmi = Double.parseDouble(binding.etAdvanceEmi.getText().toString());
        } else {
            advanceEmi = 0;
        }


        if (!StringUtils.isBlank(binding.etDepositPercentage.getText().toString()) && strDeposit.equals("Percentage")) {
            if (Double.parseDouble(binding.etDepositPercentage.getText().toString()) < 100) {
                depositPer = principal * Double.parseDouble((binding.etDepositPercentage.getText().toString())) / 100;
            } else {
                depositPer = 0;
                Toast.makeText(LoanAmountCalActivity.this, getResources().getString(R.string.error_msg_deposit), Toast.LENGTH_SHORT).show();
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
                SharedPref.getInstance().putString(EMI_PROCESSING_VALUE,binding.etProcessingFees.getText().toString());
            } else {
                processingFee = 0;
                SharedPref.getInstance().putString(EMI_PROCESSING_VALUE,"");
                Toast.makeText(LoanAmountCalActivity.this, getResources().getString(R.string.error_msg_processing_fee), Toast.LENGTH_SHORT).show();
                return;
            }

        } else if (!StringUtils.isBlank(binding.etProcessingFees.getText().toString()) && strProcessingFee.equals("Amount")) {
            processingFee = Double.parseDouble((binding.etProcessingFees.getText().toString()));
            SharedPref.getInstance().putString(EMI_PROCESSING_VALUE,binding.etProcessingFees.getText().toString());
        } else {
            processingFee = 0;
            SharedPref.getInstance().putString(EMI_PROCESSING_VALUE, "");
        }
        if (!StringUtils.isBlank(binding.etInsurance.getText().toString()) && strInsurance.equals("Percentage")) {
            if (Double.parseDouble(binding.etInsurance.getText().toString()) < 100) {
                insurance = principal * Double.parseDouble((binding.etInsurance.getText().toString())) / 100;
            } else {
                insurance = 0;
                Toast.makeText(LoanAmountCalActivity.this, getResources().getString(R.string.error_msg_insurance), Toast.LENGTH_SHORT).show();
                return;
            }

        } else if (!StringUtils.isBlank(binding.etInsurance.getText().toString()) && strInsurance.equals("Amount")) {
            insurance = Double.parseDouble((binding.etInsurance.getText().toString()));
        } else {
            insurance = 0;
        }

        double effectiveInterestRate = effectiveRate(time, emi, loanAmount, otherCharge, advanceEmi, depositPer, insurance, processingFee);

        if (effectiveInterestRate < 0) {
            Toast.makeText(LoanAmountCalActivity.this, ""+loanAmount, Toast.LENGTH_SHORT).show();
            Toast.makeText(LoanAmountCalActivity.this, ""+effectiveInterestRate, Toast.LENGTH_SHORT).show();
            Toast.makeText(LoanAmountCalActivity.this, "Please enter valid data", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(LoanAmountCalActivity.this, CalculatorDetailActivity.class);
            intent.putExtra("emi", String.valueOf(emi));
            intent.putExtra("principle", String.valueOf(loanAmount));
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