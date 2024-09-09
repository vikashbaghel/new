package com.app.rupyz.adapter.individual;

import static com.app.rupyz.generic.helper.AmountHelper.convertInLac;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.DebtOverdueListInsideItemBinding;
import com.app.rupyz.databinding.DebtTradeLineListInsideItemBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.helper.DateFormatHelper;
import com.app.rupyz.generic.model.individual.experian.Tradeline;

import java.util.List;

public class OverdueTradeListAdapter extends RecyclerView.Adapter<OverdueTradeListAdapter.ViewHolder> {
    private List<Tradeline> listdata;
    private DebtOverdueListInsideItemBinding notesBinding;
    private Context mContext;

    // RecyclerView recyclerView;
    public OverdueTradeListAdapter(List<Tradeline> listdata, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        notesBinding = DebtOverdueListInsideItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(notesBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        notesBinding.txvLenderName.setText(listdata.get(position).getSubscriber_Name());
        notesBinding.txvLoanType.setText(listdata.get(position).getAccount_Type());
        String sanctionAmount = mContext.getResources().getString(R.string.rs)+" "
                + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
                listdata.get(position).getHighest_Credit_or_Original_Loan_Amount()));

        String strSanctionAmount = mContext.getResources().getString(R.string.rs)+" "+convertInLac(Double.parseDouble(listdata.get(position).getHighest_Credit_or_Original_Loan_Amount()+""));

        notesBinding.txvSanctionAmount.setText(strSanctionAmount);
        String currentBalance = mContext.getResources().getString(R.string.rs)+" " + convertInLac(
                Double.parseDouble(listdata.get(position).getCurrent_Balance()));
        notesBinding.txvBalance.setText(currentBalance);
        notesBinding.txvOverdueAmount.setText(mContext.getResources().getString(R.string.rs)+" " +convertInLac(Double.parseDouble(listdata.get(position).getAmount_Past_Due())));
        String paidAmount = mContext.getResources().getString(R.string.rs) +
                AmountHelper.getCommaSeptdAmount((AmountHelper.convertStringToDouble(listdata.get(position).getHighest_Credit_or_Original_Loan_Amount()) -
                        AmountHelper.convertStringToDouble(listdata.get(position).getCurrent_Balance())));
        int strPaid = Integer.parseInt(listdata.get(position).getHighest_Credit_or_Original_Loan_Amount()) - Integer.parseInt(listdata.get(position).getCurrent_Balance());
        notesBinding.txvPaid.setText(mContext.getResources().getString(R.string.rs)+" " + convertInLac(strPaid)+"");
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        DebtOverdueListInsideItemBinding binding;

        public ViewHolder(DebtOverdueListInsideItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}