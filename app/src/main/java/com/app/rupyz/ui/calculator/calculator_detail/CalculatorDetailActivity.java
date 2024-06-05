package com.app.rupyz.ui.calculator.calculator_detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.app.rupyz.R;
import com.app.rupyz.adapter.loan.ComparisonEmiListAdapter;
import com.app.rupyz.databinding.ActivityCalculatorDetailBinding;
import com.app.rupyz.databinding.ActivityComparisonBinding;
import com.app.rupyz.databinding.CalculatorBottomSheetBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.loan.EmiInfoModel;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.ui.amortization.AmortizationActivity;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CalculatorDetailActivity extends AppCompatActivity {
    ActivityCalculatorDetailBinding binding;
    private Context context;
    String strEmi, strPrinciple, strEffectiveInterestRate, strInterestRate, strTotalAmount, strTime, strShowMaturity;
    int pieData, pieData1;
    double otherCharge, insurance, processingFee, depositPer, maturityValue, depositValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalculatorDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
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

    private void init(){
        strEmi = this.getIntent().getStringExtra("emi");
        strShowMaturity = this.getIntent().getStringExtra("SHOW_MATURITY");
        strPrinciple = this.getIntent().getStringExtra("principle");
        strInterestRate = this.getIntent().getStringExtra("interest_rate");
        strEffectiveInterestRate = this.getIntent().getStringExtra("effective_interest_rate");
        strTotalAmount = this.getIntent().getStringExtra("total_amount");
        pieData = this.getIntent().getIntExtra("pie_data", 0);
        pieData1 = this.getIntent().getIntExtra("pie_data1", 0);
        otherCharge = this.getIntent().getDoubleExtra("other_charge", 0);
        insurance = this.getIntent().getDoubleExtra("insurance", 0);
        processingFee = this.getIntent().getDoubleExtra("processing_fee",0);
        depositPer = this.getIntent().getDoubleExtra("deposit", 0);
        depositValue = this.getIntent().getDoubleExtra("deposit_value", 0);

        //double b = Double.parseDouble(strPrinciple)*depositValue/100;

        strTime = this.getIntent().getStringExtra("time");
        if(strShowMaturity.equalsIgnoreCase("TRUE")){
            maturityValue = this.getIntent().getDoubleExtra("maturity", 0);
            binding.rlDeposit.setVisibility(View.VISIBLE);
            binding.rlMaturity.setVisibility(View.VISIBLE);
            binding.txvDeposit.setText(getResources().getString(R.string.rs)+AmountHelper.getCommaSeptdAmount(depositValue));
            binding.txvMaturityValue.setText(getResources().getString(R.string.rs)+AmountHelper.getCommaSeptdAmount(maturityValue));
        }
        else {
            binding.rlDeposit.setVisibility(View.GONE);
            binding.rlMaturity.setVisibility(View.GONE);
        }

        otherCharge = otherCharge+insurance+processingFee+depositPer;

        binding.txvMonthlyEmi.setText(getResources().getString(R.string.rs)+ AmountHelper.getCommaSeptdAmount(Double.parseDouble(strEmi)));
        binding.txvPrincipleAmount.setText(getResources().getString(R.string.rs)+AmountHelper.getCommaSeptdAmount(Double.parseDouble(strPrinciple)));
        binding.txvTotalInterest.setText(String.format("%.2f", Double.parseDouble(strInterestRate))+"%");
        //binding.txvTotalInterest.setText(strInterestRate);
        binding.txvTotalAmount.setText(getResources().getString(R.string.rs)+AmountHelper.getCommaSeptdAmount(Double.parseDouble(strPrinciple)+otherCharge+pieData));
        binding.txvEffectiveRate.setText(strEffectiveInterestRate+"%");
        binding.txvInterestAmount.setText(getResources().getString(R.string.rs)+AmountHelper.getCommaSeptdAmount((double) pieData));
        //binding.txvOtherCharge.setText("₹ "+String.valueOf(otherCharge+insurance+processingFee));
        binding.txvOtherCharge.setText(getResources().getString(R.string.rs)+AmountHelper.getCommaSeptdAmount(otherCharge));


        binding.lenderPieChart.setDrawHoleEnabled(true);
        binding.lenderPieChart.setUsePercentValues(false);
        binding.lenderPieChart.setEntryLabelTextSize(12);
        binding.lenderPieChart.setEntryLabelColor(Color.BLACK);
        binding.lenderPieChart.setCenterText("");
        binding.lenderPieChart.setCenterTextSize(18);
        binding.lenderPieChart.setCenterTextColor(Color.WHITE);
        binding.lenderPieChart.setDrawHoleEnabled(true);
        binding.lenderPieChart.setHoleColor(Color.TRANSPARENT);
        binding.lenderPieChart.setHoleRadius(60);
        binding.lenderPieChart.setRotationAngle(90);
        binding.lenderPieChart.setMinAngleForSlices(0);
        binding.lenderPieChart.getDescription().setEnabled(false);
        binding.lenderPieChart.setRotationEnabled(false);
        binding.lenderPieChart.setTouchEnabled(false);
        Legend l = binding.lenderPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);

        loadPieChartData(binding.lenderPieChart, pieData, pieData1, (int) otherCharge);

        binding.btnViewAmortization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalculatorDetailActivity.this, AmortizationActivity.class);
                intent.putExtra("loanAmount", strPrinciple);
                intent.putExtra("years", strTime);
                intent.putExtra("annualRate", strInterestRate);
                intent.putExtra("emi", strEmi);
                startActivity(intent);
            }
        });
    }

    public void loadPieChartData(PieChart mPieChart, int totalInterest, int principal,
                                 int otherCharge) {
        mPieChart.getDescription().setEnabled(false);
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(totalInterest, ""));
        entries.add(new PieEntry(principal, ""));
        entries.add(new PieEntry(otherCharge, ""));

        final int[] MY_COLORS = {Color.rgb(105, 185, 221),
                Color.rgb(146, 99, 234),
                Color.rgb( 156, 206, 45)};

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : MY_COLORS) colors.add(c);

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        data.setValueFormatter(new PercentFormatter(mPieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        mPieChart.setData(data);
        mPieChart.invalidate();

        mPieChart.animateY(1400, Easing.EaseInOutQuad);
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