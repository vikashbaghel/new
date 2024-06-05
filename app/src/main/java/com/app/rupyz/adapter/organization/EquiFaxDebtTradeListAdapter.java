package com.app.rupyz.adapter.organization;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.DebtTradeLineListInsideItemBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.helper.DateFormatHelper;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.model.createemi.Datum;
import com.app.rupyz.generic.model.individual.experian.Tradeline;
import com.app.rupyz.generic.model.organization.TradelinesItem;

import java.util.List;

public class EquiFaxDebtTradeListAdapter extends RecyclerView.Adapter<EquiFaxDebtTradeListAdapter.ViewHolder> {
    private List<TradelinesItem> listdata;
    private List<TradelinesItem> dataEMI;
    private DebtTradeLineListInsideItemBinding notesBinding;
    private Context mContext;
    public static List<Datum> mEmiData;
    private EquiFaxReportHelper mReportHelper;


    // RecyclerView recyclerView;
    public EquiFaxDebtTradeListAdapter(List<TradelinesItem> listdata,  List<TradelinesItem> dataEMI, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;
        //this.dataEMI = dataEMI;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        notesBinding = DebtTradeLineListInsideItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(notesBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        notesBinding.txtSubscriberName.setText(listdata.get(position).getInstitutionName());
        notesBinding.txtAccountType.setText(listdata.get(position).getCreditType());
        String sanctionAmount = mContext.getResources().getString(R.string.rs)
                + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
                listdata.get(position).getSanctionAmount()));
        notesBinding.txtSanctionAmount.setText(sanctionAmount);
        String currentBalance = mContext.getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
                listdata.get(position).getCurrentBalanceAmount()));
        notesBinding.txtOutstandingAmount.setText(currentBalance);
        notesBinding.txtSanctionDate.setText(listdata.get(position).getSanctionDate());

        mReportHelper = EquiFaxReportHelper.getInstance();

        mEmiData = mReportHelper.getEquifaxCommercialEMI().getData();

        for (Datum Item : mEmiData) {
            for(TradelinesItem tradelinesItem : listdata){
                if (Item.getAccountNo().equals(tradelinesItem.getAccountNo())) {
                    tradelinesItem.setInstallmentAmount(Item.getInstallmentAmount());
                    tradelinesItem.setMonthDueDay(Item.getMonthDueDay());
                    tradelinesItem.setRepaymentTenure(Item.getRepaymentTenure());
                    tradelinesItem.setInterestRate(Item.getInterestRate());
                }
            }
        }

        if(listdata.get(position).getInterestRate()!=null){
            notesBinding.txvInterestRate.setText(listdata.get(position).getInterestRate()+" %");
        }
        else {
            notesBinding.txvInterestRate.setText("-");
        }
        if(listdata.get(position).getMonthDueDay()!=null){
            notesBinding.txvEmiDate.setText(listdata.get(position).getMonthDueDay()+"");
        }
        else {
            notesBinding.txvEmiDate.setText("-");
        }

        if (listdata.get(position).getRepaymentTenure()!=null){
            notesBinding.txvTenure.setText(listdata.get(position).getRepaymentTenure()+" Month");
        }
        else {
            notesBinding.txvTenure.setText("-");
        }

        if(listdata.get(position).getInstallmentAmount()!=null){
            notesBinding.txvEmi.setText(mContext.getResources().getString(R.string.rs)+listdata.get(position).getInstallmentAmount()+"");
        }
        else {
            notesBinding.txvEmi.setText("-");
        }

    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        DebtTradeLineListInsideItemBinding binding;

        public ViewHolder(DebtTradeLineListInsideItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}