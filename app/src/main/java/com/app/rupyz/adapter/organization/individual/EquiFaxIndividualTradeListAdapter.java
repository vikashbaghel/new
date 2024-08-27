package com.app.rupyz.adapter.organization.individual;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.TradeLineListInsideItemBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.helper.DateFormatHelper;
import com.app.rupyz.generic.model.organization.TradelinesItem;
import com.app.rupyz.generic.model.organization.individual.Tradeline;
import com.app.rupyz.generic.model.organization.individual.Tradelines;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.ui.equifax.EquiFaxIndividualMyAccount;
import com.app.rupyz.ui.equifax.EquiFaxMyAccount;

import java.util.List;

public class EquiFaxIndividualTradeListAdapter extends RecyclerView.Adapter<EquiFaxIndividualTradeListAdapter.ViewHolder> {
    private List<Tradeline> listdata;
    private TradeLineListInsideItemBinding notesBinding;
    private Context mContext;

    public EquiFaxIndividualTradeListAdapter(List<Tradeline> listdata, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        notesBinding = TradeLineListInsideItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(notesBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        notesBinding.txtSubscriberName.setText(listdata.get(position).getInstitution_name());

        if (listdata.get(position).getInstallment_amount() !=null && listdata.get(position).getInstallment_amount()!=0){
            notesBinding.txtEmiAmount.setText(mContext.getResources().getString(R.string.rs)+listdata.get(position).getInstallment_amount());
        }
        else {
            notesBinding.txtEmiAmount.setText("-");
        }
        if (listdata.get(position).getMonth_due_day() !=null && listdata.get(position).getMonth_due_day()!=0){
            notesBinding.txtEmiDate.setText(listdata.get(position).getMonth_due_day()+" of every month");
        }
        else {
            notesBinding.txtEmiDate.setText("-");
        }
        if (listdata.get(position).getRepayment_tenure()!=null && !StringUtils.isBlank(listdata.get(position).getRepayment_tenure())){
            notesBinding.txtTenure.setText(listdata.get(position).getRepayment_tenure()+" Month");
        }
        else {
            notesBinding.txtTenure.setText("-");
        }

        if (listdata.get(position).getInterest_rate()!=null && !StringUtils.isBlank(listdata.get(position).getInterest_rate())){
            notesBinding.txtInterestRate.setText(listdata.get(position).getInterest_rate()+"%");
        } else {
            notesBinding.txtInterestRate.setText("-");
        }
        /*if (listdata.get(position).getOverdue_amount() != null) {
            notesBinding.txtOverdueAmount.setText(listdata.get(position).getOverdue_amount());
        }*/
        int repayment = listdata.get(position).getRepayments_total()
                - listdata.get(position).getRepayments_missed();
        notesBinding.txtRepaymentAmount.setText(repayment + "/"
                + listdata.get(position).getRepayments_total() + " on time");
        notesBinding.txtAccountType.setText(listdata.get(position).getAccount_type());

        String sanctionAmount = mContext.getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
                listdata.get(position).getSanction_amount()+""));
        notesBinding.txtSanctionAmount.setText(sanctionAmount);

        String currentBalance = mContext.getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
                listdata.get(position).getCurrent_balance_amount()+""));
        notesBinding.txtBalanceAmount.setText(currentBalance);

        String paidAmount = mContext.getResources().getString(R.string.rs) +
                AmountHelper.getCommaSeptdAmount((AmountHelper.convertStringToDouble(listdata.get(position).getSanction_amount()+"") -
                        AmountHelper.convertStringToDouble(listdata.get(position).getCurrent_balance_amount()+"")));
        notesBinding.txtPaidAmount.setText(paidAmount);
        notesBinding.txtSanctionDate.setText(DateFormatHelper.convertSanctionDate(listdata.get(position).getDate_opened()));
        int maxAmount = AmountHelper.convertStringToInt(listdata.get(position).getSanction_amount()+"");
        int progressAmount = (int) (AmountHelper.convertStringToDouble(listdata.get(position).getSanction_amount()+"") -
                AmountHelper.convertStringToDouble(listdata.get(position).getCurrent_balance_amount()+""));
        notesBinding.progressBar.setMax(maxAmount);
        notesBinding.progressBar.setProgress(progressAmount);
        if (listdata.get(position).getAccount_status().equalsIgnoreCase("OPEN")) {
            notesBinding.statusLayout.setBackgroundResource(R.drawable.active_status_bg_style);
        } else {
            notesBinding.statusLayout.setBackgroundResource(R.drawable.inactive_status_bg_style);
        }
        notesBinding.txtAccountStatus.setText(listdata.get(position).getAccount_status());

        if(!StringUtils.isBlank(listdata.get(position).getAsset_classification())){
            if(listdata.get(position).getAsset_classification().equalsIgnoreCase("STD")){
                notesBinding.txtAssetClassification.setText(mContext.getResources().getString(R.string.standard));
            }

            else if(listdata.get(position).getAsset_classification().equalsIgnoreCase("DBT")){
                notesBinding.txtAssetClassification.setText(mContext.getResources().getString(R.string.doubtful));
            }

            else {
                notesBinding.txtAssetClassification.setText(listdata.get(position).getAsset_classification());
            }
        }

        notesBinding.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EquiFaxIndividualMyAccount) mContext).initBottomSheet(listdata.get(position));
            }
        });

        notesBinding.llAddEmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EquiFaxIndividualMyAccount) mContext).initAddEmiBottomSheet(listdata.get(position), position);
            }
        });

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
        TradeLineListInsideItemBinding binding;

        public ViewHolder(TradeLineListInsideItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}