package com.app.rupyz.ui.calculator.emi_cal;

import static com.app.rupyz.generic.utils.SharePrefConstant.EMI_OTHER_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.EMI_PROCESSING_TYPE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.R;
import com.app.rupyz.adapter.loan.CompareEmiListAdapter;
import com.app.rupyz.databinding.ActivityCompareEmiBinding;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.loan.EmiInfoModel;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.StringUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CompareEmiActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityCompareEmiBinding binding;
    private List<EmiInfoModel> mData = new ArrayList<>();
    private Context context;
    CompareEmiListAdapter adapter;
    String strPrinciple, strInterestRate, strTotalAmount, strTime, processingFee, insurance, advanceEmi, otherCharge, depositPer, otherValue, processingValue;
    String insuranceValue, depositValue, advanceValue, timeValue;
    String strOtherChargeType, processingType, depositType, insuranceType, loanTenureType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompareEmiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        //strEmi = getIntent().getStringExtra("emi");
        strPrinciple = getIntent().getStringExtra("principle");
        strInterestRate = getIntent().getStringExtra("interest_rate");
        strTotalAmount = getIntent().getStringExtra("total_amount");
        strTime = getIntent().getStringExtra("time");
        processingFee = getIntent().getStringExtra("processing_fee");
        insurance = getIntent().getStringExtra("insurance");
        advanceEmi = getIntent().getStringExtra("advance_emi");
        otherCharge = getIntent().getStringExtra("other_charge");
        depositPer = getIntent().getStringExtra("deposit");
        otherValue = getIntent().getStringExtra("otherValue");
        processingValue = getIntent().getStringExtra("processingValue");
        insuranceValue = getIntent().getStringExtra("insuranceValue");
        depositValue = getIntent().getStringExtra("depositValue");
        advanceValue = getIntent().getStringExtra("advanceValue");
        timeValue = getIntent().getStringExtra("timeValue");
        strOtherChargeType = getIntent().getStringExtra("other_charge_type");
        processingType = getIntent().getStringExtra("processing_type");
        depositType = getIntent().getStringExtra("deposit_type");
        insuranceType = getIntent().getStringExtra("insurance_type");
        loanTenureType = getIntent().getStringExtra("loan_tenure_type");

        createExampleList();
        initLayout();
        initToolbar();
    }

    private void initLayout() {
        binding.recyclerviewCompareLoan.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CompareEmiListAdapter(mData, context);
        binding.recyclerviewCompareLoan.setAdapter(adapter);
        binding.btnAdd.setOnClickListener(this);
        binding.btnCompare.setOnClickListener(this);
    }

    private void initToolbar() {
        /*Toolbar toolBar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("Compare Emi");
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
                //Toast.makeText(CompareLoanActivity.this, mData.get(i).getLoan_amount(), Toast.LENGTH_SHORT).show();

                for (EmiInfoModel Item :
                        mData) {
                    Logger.errorLogger(this.getClass().getName(), Item.getInterest_rate());
                    if (StringUtils.isBlank(Item.getLoan_amount())) {
                        new SessionHelper(CompareEmiActivity.this).messageToast(getResources().getString(R.string.enter_loan_amount));
                        return;
                    } else if (StringUtils.isBlank(Item.getInterest_rate())) {
                        new SessionHelper(CompareEmiActivity.this).messageToast(getResources().getString(R.string.enter_interest_rate));
                        return;
                    } else if (StringUtils.isBlank(Item.getLoan_tenure())) {
                        new SessionHelper(CompareEmiActivity.this).messageToast(getResources().getString(R.string.enter_loan_tenure));
                        return;
                    } else if (!StringUtils.isBlank(Item.getProcessingType()) && Item.getProcessingType().equalsIgnoreCase("Percentage") && !StringUtils.isBlank(Item.getProcessingValue()) && Double.parseDouble(Item.getProcessingValue()) >= 100) {
                        new SessionHelper(CompareEmiActivity.this).messageToast(getResources().getString(R.string.error_msg_processing_fee));
                        return;
                    } else if (!StringUtils.isBlank(Item.getOtherChargeType()) && Item.getOtherChargeType().equalsIgnoreCase("Percentage") && !StringUtils.isBlank(Item.getOtherValue()) && Double.parseDouble(Item.getOtherValue()) >= 100) {
                        new SessionHelper(CompareEmiActivity.this).messageToast(getResources().getString(R.string.error_msg_other_charge));
                        return;
                    } else if (!StringUtils.isBlank(Item.getInsuranceType()) && Item.getInsuranceType().equalsIgnoreCase("Percentage") && !StringUtils.isBlank(Item.getInsuranceValue()) && Double.parseDouble(Item.getInsuranceValue()) >= 100) {
                        new SessionHelper(CompareEmiActivity.this).messageToast(getResources().getString(R.string.error_msg_insurance));
                        return;
                    }

                }

                Intent intent = new Intent(CompareEmiActivity.this, EmiComparisonActivity.class);
                intent.putExtra("data", new Gson().toJson(mData));
                System.out.println("EMI DATA" + mData.toString());
                startActivity(intent);
                break;
        }
    }


    public void insertItem() {
        mData.add(new EmiInfoModel("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
        adapter.notifyItemInserted(mData.size() - 1);
        binding.recyclerviewCompareLoan.scrollToPosition(mData.size() - 1);
    }

    public void createExampleList() {
        //mData = new ArrayList<>();
        mData.add(new EmiInfoModel("", strPrinciple, "", strTime, otherCharge,
                advanceEmi, depositPer, strInterestRate, processingFee, insurance,
                SharedPref.getInstance().getString(EMI_OTHER_TYPE), otherValue, SharedPref.getInstance().getString(EMI_PROCESSING_TYPE),
                processingValue, insuranceValue, depositValue, advanceValue, timeValue,
                strOtherChargeType, processingType, depositType, insuranceType, loanTenureType));
    }

    public void initBottomSheet(EmiInfoModel emiInfoModel) {
        System.out.println("AAA : - " + new Gson().toJson(emiInfoModel));
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