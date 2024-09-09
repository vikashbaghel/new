package com.app.rupyz.adapter.loan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.databinding.ComparisonLoanListItemBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.loan.EmiInfoModel;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComparisonEmiListAdapter extends RecyclerView.Adapter<ComparisonEmiListAdapter.ViewHolder> {
    private List<EmiInfoModel> listdata;
    private ComparisonLoanListItemBinding binding;
    private Context mContext;
    //String strLoanAmount;
    //String strLoanTenure;
    //String strEmi;

    public ComparisonEmiListAdapter(List<EmiInfoModel> listdata, Context mContext) {
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
        Logger.errorLogger("ADAPTER", listdata.get(position).getLoan_amount());

        double time=0, principal=0, rate=0, otherCharge, advanceEmi, depositPer=0, processingFee, insurance;
        /*if(!StringUtils.isBlank(listdata.get(position).getLoan_tenure())){
            time = Double.parseDouble(listdata.get(position).getLoan_tenure());
        }*/

        if(!StringUtils.isBlank(listdata.get(position).getLoan_tenure())){
            if(!StringUtils.isBlank(listdata.get(position).getLoanTenureType()) && listdata.get(position).getLoanTenureType().equalsIgnoreCase("MONTH")){
                time = Double.parseDouble(listdata.get(position).getLoan_tenure());
            }
            else if(!StringUtils.isBlank(listdata.get(position).getLoanTenureType()) && listdata.get(position).getLoanTenureType().equalsIgnoreCase("YEAR")){
                time = 12*Double.parseDouble(listdata.get(position).getLoan_tenure());
            }
            else {
                time = 0;
            }
        }

        if(!StringUtils.isBlank(listdata.get(position).getLoan_amount())){
            principal = Double.parseDouble(listdata.get(position).getLoan_amount());
        }
        if(!StringUtils.isBlank(listdata.get(position).getInterest_rate())){
            rate = Double.parseDouble(listdata.get(position).getInterest_rate());
            rate = rate / (12 * 100);
        }

        if(!StringUtils.isBlank(listdata.get(position).getAdvance_emi())){
            advanceEmi = Double.parseDouble(listdata.get(position).getAdvance_emi());
        } else {
            advanceEmi = 0;
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

        if(!StringUtils.isBlank(listdata.get(position).getDepositValue())){
            if(!StringUtils.isBlank(listdata.get(position).getDepositType()) && listdata.get(position).getDepositType().equalsIgnoreCase("Amount")){
                depositPer = Double.parseDouble(listdata.get(position).getDepositValue());
            }
            else if(!StringUtils.isBlank(listdata.get(position).getDeposit()) && listdata.get(position).getDepositType().equalsIgnoreCase("Percentage")){
               // depositPer = (Double.parseDouble(listdata.get(position).getDepositValue())/principal)*100;
                depositPer = principal*(Double.parseDouble(listdata.get(position).getDepositValue()))/100;
            }
            else {
                depositPer=0;
            }
        }

        //Toast.makeText(mContext, listdata.get(position).getDepositValue()+"", Toast.LENGTH_SHORT).show();

        if(!StringUtils.isBlank(listdata.get(position).getProcessingValue())){
            if(!StringUtils.isBlank(listdata.get(position).getProcessingType()) && listdata.get(position).getProcessingType().equalsIgnoreCase("AMount")){
                processingFee = Double.parseDouble(listdata.get(position).getProcessingValue());
            }
            else if(!StringUtils.isBlank(listdata.get(position).getInsuranceType()) && listdata.get(position).getInsuranceType().equalsIgnoreCase("Percentage")){
                processingFee = principal*Double.parseDouble(listdata.get(position).getProcessingValue())/100;
            }
            else {
                processingFee=0;
            }
        }
        else {
            processingFee = 0;
        }


        if(!StringUtils.isBlank(listdata.get(position).getInsuranceValue())){
            if(!StringUtils.isBlank(listdata.get(position).getInsuranceType()) && listdata.get(position).getInsuranceType().equalsIgnoreCase("AMount")){
                insurance = Double.parseDouble(listdata.get(position).getInsuranceValue());
            }
            else if(!StringUtils.isBlank(listdata.get(position).getInsuranceType()) && listdata.get(position).getInsuranceType().equalsIgnoreCase("Percentage")){
                insurance = principal*Double.parseDouble(listdata.get(position).getInsuranceValue())/100;
            }
            else {
                insurance=0;
            }
        }
        else {
            insurance = 0;
        }


        double emi = (principal * rate * Math.pow(1 + rate, time)) / (Math.pow(1 + rate, time) - 1);

        double totalAmout = time*emi;

        binding.txvMonthlyEmi.setText("₹ "+String.format("%,.0f", emi));
        //binding.txvTotalInterest.setText(String.format("%.2f", listdata.get(position).getInterest_rate()) + "%");
        binding.txvTotalInterest.setText(listdata.get(position).getInterest_rate() + "%");
        binding.txvPrincipleAmount.setText("₹ "+AmountHelper.getCommaSeptdAmount(Double.parseDouble(listdata.get(position).getLoan_amount())));
        binding.txvTotalAmount.setText("₹ "+AmountHelper.getCommaSeptdAmount(otherCharge+insurance+processingFee+principal+(int) ((emi * time) - principal)));
        binding.txvOtherCharge.setText("₹ "+AmountHelper.getCommaSeptdAmount(otherCharge+insurance+processingFee+depositPer));
        binding.txvInterestAmount.setText("₹ "+ AmountHelper.getCommaSeptdAmount((emi * time) - principal));

        double a = rate(time, emi, principal, otherCharge, advanceEmi, depositPer, insurance, processingFee);

        binding.txvEffectiveRate.setText(String.format("%.2f", a) + "%");


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

        binding.btnViewAmortization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AmortizationActivity.class);
                intent.putExtra("loanAmount", listdata.get(position).getLoan_amount());
                intent.putExtra("years", listdata.get(position).getLoan_tenure());
                intent.putExtra("annualRate", String.valueOf(listdata.get(position).getInterest_rate()));
                intent.putExtra("emi", String.valueOf(emi));
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

        while(true) {
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