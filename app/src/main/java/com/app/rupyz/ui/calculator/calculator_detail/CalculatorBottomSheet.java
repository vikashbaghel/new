package com.app.rupyz.ui.calculator.calculator_detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.CalculatorBottomSheetBinding;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.ui.account.dailog.MyAccountDetailSheet;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CalculatorBottomSheet extends BottomSheetDialogFragment {
    CalculatorBottomSheetBinding binding;
    private Context context;
    String strEmi, strPrinciple, strEffectiveInterestRate, strInterestRate, strTotalAmount;
    int pieData, pieData1;
    double otherCharge, insurance, processingFee, depositPer;

    public CalculatorBottomSheet() {

    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CalculatorBottomSheetBinding.inflate(getLayoutInflater());
        strEmi = this.getArguments().getString("emi");
        strPrinciple = this.getArguments().getString("principle");
        strInterestRate = this.getArguments().getString("interest_rate");
        strEffectiveInterestRate = this.getArguments().getString("effective_interest_rate");
        strTotalAmount = this.getArguments().getString("total_amount");
        pieData = this.getArguments().getInt("pie_data");
        pieData1 = this.getArguments().getInt("pie_data1");
        otherCharge = this.getArguments().getDouble("other_charge");
        insurance = this.getArguments().getDouble("insurance");
        processingFee = this.getArguments().getDouble("processing_fee");
        depositPer = this.getArguments().getDouble("deposit");

        Logger.errorLogger(this.getClass().getName(), String.valueOf(depositPer));
        Logger.errorLogger(this.getClass().getName(), String.valueOf(processingFee));
        Logger.errorLogger(this.getClass().getName(), String.valueOf(insurance));
        Logger.errorLogger(this.getClass().getName(), String.valueOf(otherCharge));


        binding.txvMonthlyEmi.setText(strEmi);
        binding.txvPrincipleAmount.setText(strPrinciple);
        binding.txvTotalInterest.setText(strInterestRate);
        binding.txvTotalAmount.setText(strTotalAmount);
        binding.txvEffectiveRate.setText(strEffectiveInterestRate);
        binding.txvOtherCharge.setText(String.valueOf(otherCharge+insurance+processingFee+depositPer));
        loadPieChartData(binding.lenderPieChart, pieData, pieData1);
        binding.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalculatorBottomSheet.this.dismiss();
            }
        });
        return binding.getRoot();
    }

    public void loadPieChartData(PieChart mPieChart, int totalInterest, int principal) {
        mPieChart.getDescription().setEnabled(false);
        ArrayList<PieEntry> entries = new ArrayList<>();
//        entries.add(new PieEntry(principal, ""));
//        entries.add(new PieEntry(totalInterest, ""));

        Map<String, Integer> typeAmountMap = new HashMap<>();
        typeAmountMap.put("Principal (" + String.format("%,d", principal) + ")", principal);
        typeAmountMap.put("Total Interest (" + String.format("%,d", totalInterest) + ")", totalInterest);

        for (String type : typeAmountMap.keySet()) {
            entries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        final int[] MY_COLORS = {Color.rgb(209, 157, 244),
                Color.rgb(44, 74, 162)};
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
        mPieChart.setDrawSliceText(false);

        mPieChart.animateY(1400, Easing.EaseInOutQuad);
    }
}


