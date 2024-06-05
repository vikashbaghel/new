package com.app.rupyz.adapter.organization;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.DebtTradeLineListInsideItemBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.model.createemi.Datum;
import com.app.rupyz.generic.model.organization.TradelinesItem;
import com.app.rupyz.generic.model.organization.individual.Tradeline;

import java.util.List;

public class EquiFaxIndividualDebtTradeListAdapter extends RecyclerView.Adapter<EquiFaxIndividualDebtTradeListAdapter.ViewHolder> {
    private List<Tradeline> listdata;
    private DebtTradeLineListInsideItemBinding notesBinding;
    private Context mContext;
    public static List<Datum> mEmiData;
    private EquiFaxReportHelper mReportHelper;

    // RecyclerView recyclerView;
    public EquiFaxIndividualDebtTradeListAdapter(List<Tradeline> listdata, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        notesBinding = DebtTradeLineListInsideItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(notesBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        notesBinding.txtSubscriberName.setText(listdata.get(position).getInstitution_name());
        notesBinding.txtAccountType.setText(listdata.get(position).getAccount_type());
        String sanctionAmount = mContext.getResources().getString(R.string.rs)
                + AmountHelper.getCommaSeptdAmount(listdata.get(position).getSanction_amount());
        notesBinding.txtSanctionAmount.setText(sanctionAmount);
        String currentBalance = mContext.getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(
                listdata.get(position).getCurrent_balance_amount());
        notesBinding.txtOutstandingAmount.setText(currentBalance);
        notesBinding.txtSanctionDate.setText(listdata.get(position).getDate_opened());

        mReportHelper = EquiFaxReportHelper.getInstance();
        mEmiData = mReportHelper.getEquifaxIndividualEMI().getData();

        if(!mEmiData.isEmpty()){
            for (Datum Item : mEmiData) {
                for(Tradeline tradelinesItem : listdata){
                    if (Item.getAccountNo().equals(tradelinesItem.getAccount_no())) {
                        tradelinesItem.setInstallment_amount(Item.getInstallmentAmount());
                        tradelinesItem.setMonth_due_day(Item.getMonthDueDay());
                        tradelinesItem.setRepayment_tenure(Item.getRepaymentTenure()+"");
                        tradelinesItem.setInterest_rate(Item.getInterestRate()+"");
                    }
                }
            }
        }

        if(listdata.get(position).getInterest_rate()!=null){
            notesBinding.txvInterestRate.setText(listdata.get(position).getInterest_rate()+" %");
        }
        else {
            notesBinding.txvInterestRate.setText("-");
        }
        if(listdata.get(position).getMonth_due_day()!=null){
            notesBinding.txvEmiDate.setText(listdata.get(position).getMonth_due_day()+"");
        }
        else {
            notesBinding.txvEmiDate.setText("-");
        }

        if (listdata.get(position).getRepayment_tenure()!=null){
            notesBinding.txvTenure.setText(listdata.get(position).getRepayment_tenure()+" Month");
        }
        else {
            notesBinding.txvTenure.setText("-");
        }

        if(listdata.get(position).getInstallment_amount()!=null){
            notesBinding.txvEmi.setText(mContext.getResources().getString(R.string.rs)+listdata.get(position).getInstallment_amount()+"");
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