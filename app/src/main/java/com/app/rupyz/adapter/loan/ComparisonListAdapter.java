package com.app.rupyz.adapter.loan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.CompareLoanListInsideItemBinding;
import com.app.rupyz.databinding.ComparisonLoanListItemBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.loan.LoanInfoModel;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComparisonListAdapter extends RecyclerView.Adapter<ComparisonListAdapter.ViewHolder> {
    private List<LoanInfoModel> listdata;
    private ComparisonLoanListItemBinding binding;
    private Context mContext;
    //String strLoanAmount;
    //String strLoanTenure;
    //String strEmi;

    public ComparisonListAdapter(List<LoanInfoModel> listdata, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = ComparisonLoanListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Logger.errorLogger("ADAPTER", listdata.get(position).getInsurance());


        double interest;
        double time = 0, totalAmout=0, emi=0, principal=0, otherCharge, advanceEmi=0, depositPer, processingFee=0, insurance=0;

        if(!StringUtils.isBlank(listdata.get(position).getLoan_tenure())){
            if(!StringUtils.isBlank(listdata.get(position).getLoanTenureType()) && listdata.get(position).getLoanTenureType().equalsIgnoreCase("MONTH")){
                time = Double.parseDouble(listdata.get(position).getTimeValue());
            }
            else if(!StringUtils.isBlank(listdata.get(position).getLoanTenureType()) && listdata.get(position).getLoanTenureType().equalsIgnoreCase("YEAR")){
                time = 12*Double.parseDouble(listdata.get(position).getTimeValue());
            }
            else {
                time = 0;
            }
        }

        if(!StringUtils.isBlank(listdata.get(position).getEmi())){
            totalAmout = time*Double.parseDouble(listdata.get(position).getEmi());
        }
        if(!StringUtils.isBlank(listdata.get(position).getEmi())){
            emi = Double.parseDouble(listdata.get(position).getEmi());
        }
        if(!StringUtils.isBlank(listdata.get(position).getLoan_amount())){
           principal = Double.parseDouble(listdata.get(position).getLoan_amount());
        }

        if(!StringUtils.isBlank(listdata.get(position).getAdvance_emi())){
            if(!StringUtils.isBlank(listdata.get(position).getAdvanceEmiType()) && listdata.get(position).getAdvanceEmiType().equalsIgnoreCase("AMOUNT")){
                advanceEmi = Double.parseDouble(listdata.get(position).getAdvance_emi());
            }
            else if(!StringUtils.isBlank(listdata.get(position).getAdvanceEmiType()) && listdata.get(position).getAdvanceEmiType().equalsIgnoreCase("MONTH")){
                advanceEmi = emi*Double.parseDouble(listdata.get(position).getAdvance_emi());
            }
        } else {
            advanceEmi = 0;
        }

        if (!StringUtils.isBlank(listdata.get(position).getDeposit())){
            depositPer = Double.parseDouble(listdata.get(position).getDeposit());
        } else {
            depositPer = 0;
        } if (!StringUtils.isBlank(listdata.get(position).getProcessing_fee())) {
            processingFee = Double.parseDouble(listdata.get(position).getProcessing_fee());
        } else {
            processingFee = 0;
        } if (!StringUtils.isBlank(listdata.get(position).getInsurance())) {
            insurance = Double.parseDouble(listdata.get(position).getInsurance());
        } else {
            insurance = 0;
        }

        if(!StringUtils.isBlank(listdata.get(position).getOtherValue())){
            if(!StringUtils.isBlank(listdata.get(position).getOtherChargeType()) && listdata.get(position).getOtherChargeType().equalsIgnoreCase("AMount")){
                otherCharge = Double.parseDouble(listdata.get(position).getOtherValue());
            }
            else if(!StringUtils.isBlank(listdata.get(position).getOtherChargeType()) && listdata.get(position).getOtherChargeType().equalsIgnoreCase("Percentage")){
                otherCharge = principal*Double.parseDouble(listdata.get(position).getOtherValue())/100;
            }
            else {
                otherCharge=0;
            }
        }
        else {
            otherCharge = 0;
        }

        interest = rate(time, emi, principal, 0, 0, 0, 0, 0);


        if(totalAmout > principal) {
            double effectiveRateInterest = rate(time, emi, principal, otherCharge, advanceEmi, depositPer, insurance, processingFee);
            //binding.txvTotalPercentage.setText(String.format("%.2f", a) + "%");

            if(effectiveRateInterest<0){

            }

            else {
                binding.txvMonthlyEmi.setText(mContext.getResources().getString(R.string.rs)+AmountHelper.getCommaSeptdAmount(Double.parseDouble(listdata.get(position).getEmi())));
                binding.txvTotalInterest.setText(String.format("%.2f", interest) + "%");
                binding.txvPrincipleAmount.setText(mContext.getResources().getString(R.string.rs)+AmountHelper.getCommaSeptdAmount(principal));
                binding.txvTotalAmount.setText("₹ "+AmountHelper.getCommaSeptdAmount(otherCharge+insurance+processingFee+principal+(int) ((emi * time) - principal)));
                binding.txvInterestAmount.setText("₹ "+ AmountHelper.getCommaSeptdAmount((emi * time) - principal));
                binding.txvEffectiveRate.setText(String.format("%.2f", effectiveRateInterest) + "%");
                binding.txvOtherCharge.setText(mContext.getResources().getString(R.string.rs)+ AmountHelper.getCommaSeptdAmount(otherCharge+insurance+processingFee));
                //String.valueOf((int) ((emi * time) - principal))

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

                loadPieChartData(binding.lenderPieChart, (int) ((emi * time) - principal), (int) principal, (int) (otherCharge+insurance+processingFee));
            }

        }

        else {
            Toast.makeText(mContext, "Please Enter Valid Data", Toast.LENGTH_SHORT).show();
        }

        binding.btnViewAmortization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AmortizationActivity.class);
                intent.putExtra("loanAmount", listdata.get(position).getLoan_amount());
                intent.putExtra("years", listdata.get(position).getLoan_tenure());
                intent.putExtra("annualRate", String.valueOf(interest));
                intent.putExtra("emi", listdata.get(position).getEmi());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ComparisonLoanListItemBinding binding;

        public ViewHolder(ComparisonLoanListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static double rate(double nper, double pmt, double pv, double otherCharge, double advanceEmi, double depositPer, double insurance, double processingFee) {
        double error = 0.0000001;
        double high =  1.00;
        double low = 0.00;

        double deductive = otherCharge + advanceEmi + processingFee + insurance + depositPer;
        pv = pv-deductive;

        double rate = (2.0 * (nper * pmt - pv)) / (pv * nper);

        int itercount=0;
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

    public void loadPieChartData(PieChart mPieChart, int totalInterest, int principal, int otherCharge) {
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
        mPieChart.setDrawSliceText(false);

        mPieChart.animateY(1400, Easing.EaseInOutQuad);
    }
}