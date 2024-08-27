package com.app.rupyz.adapter.individual;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.DebtTradeLineListInsideItemBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.helper.DateFormatHelper;
import com.app.rupyz.generic.model.createemi.experian.Datum;
import com.app.rupyz.generic.model.individual.experian.Tradeline;

import java.util.List;

public class DebtTradeListAdapter extends RecyclerView.Adapter<DebtTradeListAdapter.ViewHolder> {
    private List<Tradeline> listdata;
    private List<Tradeline> datumList;
    private DebtTradeLineListInsideItemBinding notesBinding;
    private Context mContext;

    // RecyclerView recyclerView;
    public DebtTradeListAdapter(List<Tradeline> listdata, List<Tradeline> datumList, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;
        this.datumList = datumList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        notesBinding = DebtTradeLineListInsideItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(notesBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        notesBinding.txtSubscriberName.setText(listdata.get(position).getSubscriber_Name());
        notesBinding.txtAccountType.setText(listdata.get(position).getAccount_Type());
        String sanctionAmount = mContext.getResources().getString(R.string.rs)
                + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
                listdata.get(position).getHighest_Credit_or_Original_Loan_Amount()));
        notesBinding.txtSanctionAmount.setText(sanctionAmount);
        String currentBalance = mContext.getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
                listdata.get(position).getCurrent_Balance()));
        notesBinding.txtOutstandingAmount.setText(currentBalance);
        notesBinding.txtSanctionDate.setText(DateFormatHelper.conUnSupportedDateToString(listdata.get(position).getOpen_Date()));
        if(datumList.get(position).getRate_of_Interest()!=null){
            notesBinding.txvInterestRate.setText(datumList.get(position).getRate_of_Interest()+" %");
        }
        else {
            notesBinding.txvInterestRate.setText("-");
        }
        if(datumList.get(position).getMonthDueDay()!=null){
            notesBinding.txvEmiDate.setText(datumList.get(position).getMonthDueDay()+"");
        }
        else {
            notesBinding.txvEmiDate.setText("-");
        }

        if (datumList.get(position).getRepayment_Tenure()!=null){
            notesBinding.txvTenure.setText(datumList.get(position).getRepayment_Tenure()+" Month");
        }
        else {
            notesBinding.txvTenure.setText("-");
        }

        if(datumList.get(position).getScheduled_Monthly_Payment_Amount()!=null){
            notesBinding.txvEmi.setText(mContext.getResources().getString(R.string.rs)+datumList.get(position).getScheduled_Monthly_Payment_Amount()+"");
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