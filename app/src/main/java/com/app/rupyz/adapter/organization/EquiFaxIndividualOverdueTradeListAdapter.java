package com.app.rupyz.adapter.organization;

import static com.app.rupyz.generic.helper.AmountHelper.convertInLac;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.DebtOverdueListInsideItemBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.model.organization.TradelinesItem;
import com.app.rupyz.generic.model.organization.individual.Tradeline;

import java.util.List;

public class EquiFaxIndividualOverdueTradeListAdapter extends RecyclerView.Adapter<EquiFaxIndividualOverdueTradeListAdapter.ViewHolder> {
    private List<Tradeline> listdata;
    private DebtOverdueListInsideItemBinding notesBinding;
    private Context mContext;

    // RecyclerView recyclerView;
    public EquiFaxIndividualOverdueTradeListAdapter(List<Tradeline> listdata, Context mContext) {
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
        notesBinding.txvLenderName.setText(listdata.get(position).getInstitution_name());
        notesBinding.txvLoanType.setText(listdata.get(position).getAccount_type());

        String overdueAmount = mContext.getResources().getString(R.string.rs) + " "
                + convertInLac(Double.parseDouble(listdata.get(position).getOverdue_amount()+""));
        String strSanctionAmount = mContext.getResources().getString(R.string.rs)+" "+convertInLac(Double.parseDouble(listdata.get(position).getSanction_amount()+""));
        notesBinding.txvSanctionAmount.setText(strSanctionAmount);
        String strCurrentBalance = mContext.getResources().getString(R.string.rs) + " "+ convertInLac(Double.parseDouble(listdata.get(position).getCurrent_balance_amount()+""));
        notesBinding.txvBalance.setText(strCurrentBalance);
        notesBinding.txvOverdueAmount.setText(overdueAmount);

        int paidAmount = listdata.get(position).getSanction_amount() - listdata.get(position).getCurrent_balance_amount();
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