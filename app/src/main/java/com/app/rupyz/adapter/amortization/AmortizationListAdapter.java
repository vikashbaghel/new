package com.app.rupyz.adapter.amortization;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.AmortizationListInsideItemBinding;
import com.app.rupyz.databinding.ComparisonLoanListItemBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.amortization.AmortizationInfo;
import com.app.rupyz.generic.model.loan.LoanInfoModel;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.ui.amortization.AmortizationActivity;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmortizationListAdapter extends RecyclerView.Adapter<AmortizationListAdapter.ViewHolder> {
    private List<AmortizationInfo> listdata;
    private AmortizationListInsideItemBinding binding;
    private Context mContext;
    //String strLoanAmount;
    //String strLoanTenure;
    //String strEmi;

    public AmortizationListAdapter(List<AmortizationInfo> listdata, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = AmortizationListInsideItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Logger.errorLogger("ADAPTER", listdata.get(position).getInterest());
        binding.txvPayment.setText(listdata.get(position).getPayment());
       // binding.txvBalance.setText(String.format("%,.0f", listdata.get(position).getBalance()));
        binding.txvBalance.setText(AmountHelper.getCommaSeptdAmount(listdata.get(position).getBalance()));
        binding.txvInterest.setText(AmountHelper.getCommaSeptdAmount(listdata.get(position).getInterest()));
        binding.txvAmount.setText(AmountHelper.getCommaSeptdAmount(listdata.get(position).getEmi()));
        binding.txvPrincipal.setText(AmountHelper.getCommaSeptdAmount(listdata.get(position).getPrincipal()));

       /* if(position %2 == 1) {
            *//*holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            //  holder.imageView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            final int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.itemView.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.facility_mix_four_style) );
            } else {
                holder.itemView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.facility_mix_four_style));
            }*//*

            binding.txvPrincipal.setTextColor(Color.parseColor("#42B645"));
            binding.txvAmount.setTextColor(Color.parseColor("#42B645"));
            binding.txvInterest.setTextColor(Color.parseColor("#42B645"));
            binding.txvBalance.setTextColor(Color.parseColor("#42B645"));
            binding.txvPayment.setTextColor(Color.parseColor("#42B645"));
        }
        else
        {
            *//*holder.itemView.setBackgroundColor(Color.parseColor("#FFFAF8FD"));
            //  holder.imageView.setBackgroundColor(Color.parseColor("#FFFAF8FD"));
            final int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.itemView.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.facility_mix_five_style) );
            } else {
                holder.itemView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.facility_mix_five_style));
            }*//*
            binding.txvPrincipal.setTextColor(Color.parseColor("#312B81"));
            binding.txvAmount.setTextColor(Color.parseColor("#312B81"));
            binding.txvInterest.setTextColor(Color.parseColor("#312B81"));
            binding.txvBalance.setTextColor(Color.parseColor("#312B81"));
            binding.txvPayment.setTextColor(Color.parseColor("#312B81"));
        }*/
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AmortizationListInsideItemBinding binding;

        public ViewHolder(AmortizationListInsideItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}