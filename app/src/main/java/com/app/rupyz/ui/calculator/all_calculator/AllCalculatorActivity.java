package com.app.rupyz.ui.calculator.all_calculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityAllCalculatorBinding;
import com.app.rupyz.ui.calculator.emi_cal.EmiCalActivity;
import com.app.rupyz.ui.calculator.interest_rate.InterestRateCalActivity;
import com.app.rupyz.ui.calculator.loan_amount.LoanAmountCalActivity;
import com.app.rupyz.ui.calculator.machinery.MachineryLoanCalActivity;
import com.google.gson.Gson;

import java.util.Arrays;

public class AllCalculatorActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityAllCalculatorBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllCalculatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
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

    private void init() {
        context = this;
        binding.llEmiCalculator.setOnClickListener(this);
        binding.llLoanAmountCalculator.setOnClickListener(this);
        binding.llLoanTenureCalculator.setOnClickListener(this);
        binding.llInterestRateCalculator.setOnClickListener(this);
        binding.llMachineryCalculator.setOnClickListener(this);
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
            case R.id.ll_emi_calculator:
                Intent emiCalIntent = new Intent(AllCalculatorActivity.this, EmiCalActivity.class);
                startActivity(emiCalIntent);
                break;

            case R.id.ll_loan_amount_calculator:
                Intent loanAmountIntent = new Intent(AllCalculatorActivity.this, LoanAmountCalActivity.class);
                startActivity(loanAmountIntent);
                break;

            case R.id.ll_loan_tenure_calculator:
                break;

            case R.id.ll_interest_rate_calculator:
                Intent intent = new Intent(AllCalculatorActivity.this, InterestRateCalActivity.class);
                startActivity(intent);
                break;

            case R.id.ll_machinery_calculator:
                Intent machineryCalIntent = new Intent(AllCalculatorActivity.this, MachineryLoanCalActivity.class);
                startActivity(machineryCalIntent);
                break;
        }
    }
}