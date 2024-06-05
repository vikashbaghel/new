package com.app.rupyz.adapter.loan;

import static com.app.rupyz.generic.utils.Simulation.rateFun;

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
import com.app.rupyz.generic.model.loan.MachineryInfoModel;
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
import java.util.List;

public class ComparisonMachineryListAdapter extends RecyclerView.Adapter<ComparisonMachineryListAdapter.ViewHolder> {
    private List<MachineryInfoModel> listdata;
    private ComparisonLoanListItemBinding binding;
    private Context mContext;
    //String strLoanAmount;
    //String strLoanTenure;
    //String strEmi;

    public  ComparisonMachineryListAdapter(List<MachineryInfoModel> listdata, Context mContext) {
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
        double time=0, principal=0, rate=0, otherCharge, depositPer=0, processingFee, insurance, interestIncome, depositValue=0;




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

      /*  Toast.makeText(mContext, ""+listdata.get(position).getTimeValue(), Toast.LENGTH_SHORT).show();

        System.out.println("TENURE :_ "+listdata.get(position).getLoanTenureType());*/

        if(!StringUtils.isBlank(listdata.get(position).getLoan_amount())){
            principal = Double.parseDouble(listdata.get(position).getLoan_amount());
        }
        if(!StringUtils.isBlank(listdata.get(position).getInterest_rate())){
            rate = Double.parseDouble(listdata.get(position).getInterest_rate());
            rate = rate / (12 * 100);
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
        } /*if(!StringUtils.isBlank(listdata.get(position).getAdvance_emi())){
            advanceEmi = Double.parseDouble(listdata.get(position).getAdvance_emi());
        } else {
            advanceEmi = 0;
        }*/
        /*if (!StringUtils.isBlank(listdata.get(position).getDeposit())){
            depositPer = Double.parseDouble(listdata.get(position).getDeposit());
        } else {
            depositPer = 0;
        } *//*if (!StringUtils.isBlank(listdata.get(position).getProcessingValue())) {
            processingFee = Double.parseDouble(listdata.get(position).getProcessingValue());
        } else {
            processingFee = 0;
        }*/ /*if (!StringUtils.isBlank(listdata.get(position).getInsuranceValue())){
            insurance = Double.parseDouble(listdata.get(position).getInsuranceValue());
        } else {
            insurance = 0;
        }*/

        if(!StringUtils.isBlank(listdata.get(position).getDeposit())){
            if(!StringUtils.isBlank(listdata.get(position).getDepositType()) && listdata.get(position).getDepositType().equalsIgnoreCase("Amount")){
                depositPer = (Double.parseDouble(listdata.get(position).getDeposit())/principal)*100;
            }
            else if(!StringUtils.isBlank(listdata.get(position).getDeposit()) && listdata.get(position).getDepositType().equalsIgnoreCase("Percentage")){
                depositPer = Double.parseDouble(listdata.get(position).getDeposit());
            }
            else {
                depositPer=0;
            }
        }

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

        if (!StringUtils.isBlank(listdata.get(position).getInterest_income())){
            interestIncome = Double.parseDouble(listdata.get(position).getInterest_income());
        } else {
            interestIncome = 0;
        }


        double emi = (principal * rate * Math.pow(1 + rate, time)) / (Math.pow(1 + rate, time) - 1);
        depositValue = principal*depositPer/100;

        double totalAmout = time*emi;

        int type=0;

        double deductable = processingFee + insurance + otherCharge;

        double netCashInFlow = (principal) - deductable;

        double futureValue = (principal)*depositPer/100*(Math.pow(1 + interestIncome/100, time/12));

        double a = rateFun(time, emi, -netCashInFlow, futureValue, type);


        System.out.println("MACHINERY TIME" + time);
        System.out.println("MACHINERY OTHER CHARGE" + otherCharge);
        System.out.println("MACHINERY FUTURE VALUE" + futureValue);


        holder.binding.txvMonthlyEmi.setText("₹ "+String.format("%,.0f", emi));
        //binding.txvTotalInterest.setText(String.format("%.2f", listdata.get(position).getInterest_rate()) + "%");
        holder.binding.txvTotalInterest.setText(listdata.get(position).getInterest_rate() + "%");
        holder.binding.txvPrincipleAmount.setText("₹ "+AmountHelper.getCommaSeptdAmount(Double.parseDouble(listdata.get(position).getLoan_amount())));
        holder.binding.txvTotalAmount.setText("₹ "+AmountHelper.getCommaSeptdAmount(otherCharge+insurance+processingFee+principal+(int) ((emi * time) - principal)));
        holder.binding.txvOtherCharge.setText("₹ "+AmountHelper.getCommaSeptdAmount(otherCharge+insurance+processingFee));
        holder.binding.txvInterestAmount.setText("₹ "+ AmountHelper.getCommaSeptdAmount((emi * time) - principal));


        holder.binding.txvEffectiveRate.setText(String.format("%.2f", a) + "%");
        holder.binding.txvDeposit.setText("₹ "+AmountHelper.getCommaSeptdAmount(depositValue));
        holder.binding.txvMaturityValue.setText("₹ "+AmountHelper.getCommaSeptdAmount(futureValue));

        holder.binding.lenderPieChart.setDrawHoleEnabled(true);
        holder.binding.lenderPieChart.setUsePercentValues(false);
        holder.binding.lenderPieChart.setEntryLabelTextSize(12);
        holder.binding.lenderPieChart.setEntryLabelColor(Color.BLACK);
        holder.binding.lenderPieChart.setCenterText("");
        holder.binding.lenderPieChart.setCenterTextSize(18);
        holder.binding.lenderPieChart.setCenterTextColor(Color.WHITE);
        holder.binding.lenderPieChart.setDrawHoleEnabled(true);
        holder.binding.lenderPieChart.setHoleColor(Color.TRANSPARENT);
        holder.binding.lenderPieChart.setHoleRadius(60);

        holder.binding.lenderPieChart.setRotationAngle(90);
        holder.binding.lenderPieChart.setMinAngleForSlices(0);
        holder.binding.lenderPieChart.getDescription().setEnabled(false);
        holder.binding.lenderPieChart.setRotationEnabled(false);
        holder.binding.lenderPieChart.setTouchEnabled(false);
        Legend l = holder.binding.lenderPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);

        loadPieChartData(holder.binding.lenderPieChart, (int) ((emi * time) - principal), (int) principal, (int) (otherCharge+insurance+processingFee));

        holder.binding.btnViewAmortization.setOnClickListener(new View.OnClickListener() {
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