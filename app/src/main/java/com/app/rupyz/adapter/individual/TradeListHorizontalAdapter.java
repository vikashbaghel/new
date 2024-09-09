package com.app.rupyz.adapter.individual;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.TradeLineListHorizontalInsideItemBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.helper.DateFormatHelper;
import com.app.rupyz.generic.model.individual.experian.Tradeline;

import java.util.List;

public class TradeListHorizontalAdapter extends RecyclerView.Adapter<TradeListHorizontalAdapter.ViewHolder> {
    private List<Tradeline> listdata;
    private TradeLineListHorizontalInsideItemBinding notesBinding;
    private Context mContext;

    public TradeListHorizontalAdapter(List<Tradeline> listdata, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        notesBinding = TradeLineListHorizontalInsideItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(notesBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        notesBinding.txtSubscriberName.setText(listdata.get(position).getSubscriber_Name());
        notesBinding.txtLoanType.setText(listdata.get(position).getAccount_Type());
        String sanctionAmount = mContext.getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
                listdata.get(position).getHighest_Credit_or_Original_Loan_Amount()));
        notesBinding.txtSanctionAmount.setText(sanctionAmount);
        notesBinding.txtSanctionDate.setText(DateFormatHelper.conUnSupportedDateToString(listdata.get(position).getOpen_Date()));
        String currentBalance = mContext.getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
                listdata.get(position).getCurrent_Balance()));
        notesBinding.txtBalanceAmount.setText(currentBalance);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TradeLineListHorizontalInsideItemBinding binding;

        public ViewHolder(TradeLineListHorizontalInsideItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}