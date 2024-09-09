package com.app.rupyz.ui.calculator.compare_loan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.rupyz.R;
import com.app.rupyz.adapter.loan.CompareLoanListAdapter;
import com.app.rupyz.databinding.ActivityCompareLoanBinding;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.loan.LoanInfoModel;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.ui.account.dailog.MyAccountDetailSheet;
import com.app.rupyz.ui.calculator.emi_cal.CompareEmiActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CompareLoanActivity extends AppCompatActivity implements View.OnClickListener{
    ActivityCompareLoanBinding binding;
    private List<LoanInfoModel> mData = new ArrayList<>();
    private Context context;
    CompareLoanListAdapter adapter;
    String strEmi, strPrinciple, strInterestRate, strTotalAmount, strTime,
            processingFee, insurance, advanceEmi, otherCharge,
            depositPer;
    String strOtherChargeType, processingType, depositType, insuranceType, loanTenureType, advanceEmiType;

    String processingValue, tenureValue, insuranceValue, depositValue, timeValue;
    String otherValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompareLoanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        strEmi = getIntent().getStringExtra("emi");
        strPrinciple = getIntent().getStringExtra("principle");
        strInterestRate = getIntent().getStringExtra("interest_rate");
        strTotalAmount = getIntent().getStringExtra("total_amount");
        strTime = getIntent().getStringExtra("time");
        processingFee = getIntent().getStringExtra("processing_fee");
        insurance = getIntent().getStringExtra("insurance");
        advanceEmi = getIntent().getStringExtra("advance_emi");
        otherCharge = getIntent().getStringExtra("other_charge");
        depositPer = getIntent().getStringExtra("deposit");
        processingValue = getIntent().getStringExtra("processingValue");
        tenureValue = getIntent().getStringExtra("timeValue");
        insuranceValue = getIntent().getStringExtra("insuranceValue");
        otherValue = getIntent().getStringExtra("otherValue");
        depositValue = getIntent().getStringExtra("depositValue");
        strOtherChargeType = getIntent().getStringExtra("other_charge_type");
        processingType = getIntent().getStringExtra("processing_type");
        depositType = getIntent().getStringExtra("deposit_type");
        insuranceType = getIntent().getStringExtra("insurance_type");
        loanTenureType = getIntent().getStringExtra("loan_tenure_type");
        advanceEmiType = getIntent().getStringExtra("advance_emi_type");
        createExampleList();
        initLayout();
        initToolbar();
    }

    private void initLayout() {
        binding.recyclerviewCompareLoan.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CompareLoanListAdapter(mData, context);
        binding.recyclerviewCompareLoan.setAdapter(adapter);
        binding.btnAdd.setOnClickListener(this);
        binding.btnCompare.setOnClickListener(this);
    }

    private void initToolbar() {
        /*Toolbar toolBar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("Compare");
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                insertItem();
                break;

            case R.id.btn_compare:
                //double totalAmout = time*Double.parseDouble(strEmi);

                for (LoanInfoModel Item :
                        mData) {
                    if(StringUtils.isBlank(Item.getLoan_amount())){
                        new SessionHelper(CompareLoanActivity.this).messageToast(getResources().getString(R.string.enter_loan_amount));
                        return;
                    } else if(StringUtils.isBlank(Item.getEmi())){
                        new SessionHelper(CompareLoanActivity.this).messageToast(getResources().getString(R.string.enter_emi));
                        return;
                    } else if(StringUtils.isBlank(Item.getLoan_tenure())){
                        new SessionHelper(CompareLoanActivity.this).messageToast(getResources().getString(R.string.enter_loan_tenure));
                        return;
                    } else if(!StringUtils.isBlank(Item.getProcessingType()) && Item.getProcessingType().equalsIgnoreCase("Percentage") && !StringUtils.isBlank(Item.getProcessingValue()) && Double.parseDouble(Item.getProcessingValue())>=100){
                        new SessionHelper(CompareLoanActivity.this).messageToast(getResources().getString(R.string.error_msg_processing_fee));
                        return;
                    } else if(!StringUtils.isBlank(Item.getOtherChargeType()) && Item.getOtherChargeType().equalsIgnoreCase("Percentage") && !StringUtils.isBlank(Item.getOtherValue()) && Double.parseDouble(Item.getOtherValue())>=100){
                        new SessionHelper(CompareLoanActivity.this).messageToast(getResources().getString(R.string.error_msg_other_charge));
                        return;
                    } else if(!StringUtils.isBlank(Item.getInsuranceType()) && Item.getInsuranceType().equalsIgnoreCase("Percentage") && !StringUtils.isBlank(Item.getInsuranceValue()) && Double.parseDouble(Item.getInsuranceValue())>=100){
                        new SessionHelper(CompareLoanActivity.this).messageToast(getResources().getString(R.string.error_msg_insurance));
                        return;
                    } else if(!StringUtils.isBlank(Item.getEmi()) && !StringUtils.isBlank(Item.getTimeValue()) && !StringUtils.isBlank(Item.getLoan_amount())){
                        if(Double.parseDouble(Item.getEmi())*Double.parseDouble(Item.getTimeValue())<Double.parseDouble(Item.getLoan_amount())){
                            new SessionHelper(CompareLoanActivity.this).messageToast("Sum of EMI is less than Loan Amount. Please check Loan Tenure and EMI");
                            return;
                        }
                    }
                }



                Intent intent = new Intent(CompareLoanActivity.this, ComparisonActivity.class);
                intent.putExtra("data", new Gson().toJson(mData));
                startActivity(intent);
                break;
        }
    }


    public void insertItem() {
        //mData.add(new LoanInfoModel("","", "", "", "", "", "", "", "", "", 0, "", 0, "", 0, "", "",strDepositType, 0 ));
        mData.add(new LoanInfoModel("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
        adapter.notifyItemInserted(mData.size()-1);
        binding.recyclerviewCompareLoan.scrollToPosition(mData.size()-1);
    }

    public void createExampleList() {
        mData.add(new LoanInfoModel("", strPrinciple, strEmi, strTime, otherCharge, advanceEmi, depositPer, insurance, processingFee,  processingValue, tenureValue,  insuranceValue,  otherValue,  depositValue, "", strOtherChargeType, processingType, depositType, insuranceType, loanTenureType, advanceEmiType));
    }

    public void initBottomSheet(LoanInfoModel loanInfoModel) {
        System.out.println("AAA : - "+ new Gson().toJson(loanInfoModel));
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
}