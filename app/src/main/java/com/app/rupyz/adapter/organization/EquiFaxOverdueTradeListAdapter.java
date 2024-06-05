package com.app.rupyz.adapter.organization;

import static com.app.rupyz.generic.helper.AmountHelper.convertInLac;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.DebtOverdueListInsideItemBinding;
import com.app.rupyz.databinding.DebtTradeLineListInsideItemBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.model.organization.TradelinesItem;

import java.util.List;

public class EquiFaxOverdueTradeListAdapter extends RecyclerView.Adapter<EquiFaxOverdueTradeListAdapter.ViewHolder> {
    private List<TradelinesItem> listdata;
    private DebtOverdueListInsideItemBinding notesBinding;
    private Context mContext;

    // RecyclerView recyclerView;
    public EquiFaxOverdueTradeListAdapter(List<TradelinesItem> listdata, Context mContext) {
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
        notesBinding.txvLenderName.setText(listdata.get(position).getInstitutionName());
        notesBinding.txvLoanType.setText(listdata.get(position).getCreditType());
        String sanctionAmount = mContext.getResources().getString(R.string.rs)
                + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
                listdata.get(position).getSanctionAmount()));
        String overdueAmount = mContext.getResources().getString(R.string.rs) + " "
                + convertInLac(Double.parseDouble(listdata.get(position).getOverdueAmount()));
        String strSanctionAmount = mContext.getResources().getString(R.string.rs)+" "+convertInLac(Double.parseDouble(listdata.get(position).getSanctionAmount()));
        notesBinding.txvSanctionAmount.setText(strSanctionAmount);
        String currentBalance = mContext.getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
                listdata.get(position).getCurrentBalanceAmount()));
        String strCurrentBalance = mContext.getResources().getString(R.string.rs) + " "+ convertInLac(Double.parseDouble(listdata.get(position).getCurrentBalanceAmount()));
        notesBinding.txvBalance.setText(strCurrentBalance);
        notesBinding.txvOverdueAmount.setText(overdueAmount);

        int paidAmount = Integer.parseInt(listdata.get(position).getSanctionAmount()) - Integer.parseInt(listdata.get(position).getCurrentBalanceAmount());
        String strPaidAmount = mContext.getResources().getString(R.string.rs) + " " + convertInLac(paidAmount);
        notesBinding.txvPaid.setText(strPaidAmount);
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