package com.app.rupyz.adapter.individual;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.TradeLineListInsideItemBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.helper.DateFormatHelper;
import com.app.rupyz.generic.model.individual.experian.Tradeline;
import com.app.rupyz.ui.account.MyAccount;
import com.app.rupyz.ui.account.OwnershipMixActivity;

import java.util.List;

public class TradeListOwnershipMixAdapter extends RecyclerView.Adapter<TradeListOwnershipMixAdapter.ViewHolder> {
    private List<Tradeline> listdata;
    private TradeLineListInsideItemBinding notesBinding;
    private Context mContext;

    public TradeListOwnershipMixAdapter(List<Tradeline> listdata, Context mContext) {
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
        notesBinding.txtSubscriberName.setText(listdata.get(position).getSubscriber_Name());
        if (listdata.get(position).getAmount_Past_Due() != null) {
            notesBinding.txtOverdueAmount.setText(listdata.get(position).getAmount_Past_Due());
        }
        String repayment = (AmountHelper.convertStringToInt(listdata.get(position).getOntime_payment())
                + AmountHelper.convertStringToInt(listdata.get(position).getDelayed_payment())) + "";
        notesBinding.txtRepaymentAmount.setText(listdata.get(position).getOntime_payment() + "/" + repayment + " on time");
        notesBinding.txtAccountType.setText(listdata.get(position).getAccount_Type());

        String sanctionAmount = mContext.getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
                listdata.get(position).getHighest_Credit_or_Original_Loan_Amount()));
        notesBinding.txtSanctionAmount.setText(sanctionAmount);

//        if (listdata.get(position).getAccount_Type().equalsIgnoreCase("CREDIT CARD")) {
//            String sanctionAmount = mContext.getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
//                    listdata.get(position).getCredit_Limit_Amount()));
//            notesBinding.txtSanctionAmount.setText(sanctionAmount);
//        } else {
//            String sanctionAmount = mContext.getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
//                    listdata.get(position).getHighest_Credit_or_Original_Loan_Amount()));
//            notesBinding.txtSanctionAmount.setText(sanctionAmount);
//        }
        String currentBalance = mContext.getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
                listdata.get(position).getCurrent_Balance()));
        notesBinding.txtBalanceAmount.setText(currentBalance);

        String paidAmount = mContext.getResources().getString(R.string.rs) +
                AmountHelper.getCommaSeptdAmount((AmountHelper.convertStringToDouble(listdata.get(position).getHighest_Credit_or_Original_Loan_Amount()) -
                        AmountHelper.convertStringToDouble(listdata.get(position).getCurrent_Balance())));
        notesBinding.txtPaidAmount.setText(paidAmount);
        notesBinding.txtSanctionDate.setText(DateFormatHelper.conUnSupportedDateToString(listdata.get(position).getOpen_Date()));
        int maxAmount = AmountHelper.convertStringToInt(listdata.get(position).getHighest_Credit_or_Original_Loan_Amount());
        int progressAmount = (int) (AmountHelper.convertStringToDouble(listdata.get(position).getHighest_Credit_or_Original_Loan_Amount()) -
                AmountHelper.convertStringToDouble(listdata.get(position).getCurrent_Balance()));
        notesBinding.progressBar.setMax(maxAmount);
        notesBinding.progressBar.setProgress(progressAmount);
        if (listdata.get(position).getAccount_Status().equalsIgnoreCase("Active")) {
            notesBinding.statusLayout.setBackgroundResource(R.drawable.active_status_bg_style);
        } else {
            notesBinding.statusLayout.setBackgroundResource(R.drawable.inactive_status_bg_style);
        }
        notesBinding.txtAccountStatus.setText(listdata.get(position).getAccount_Status());
        notesBinding.txtAssetClassification.setText(listdata.get(position).getAsset_classification());
        notesBinding.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OwnershipMixActivity) mContext).initBottomSheet(listdata.get(position));
            }
        });

        notesBinding.llAddEmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OwnershipMixActivity) mContext).initAddEmiBottomSheet(listdata.get(position), position, 1);
            }
        });

        if (listdata.get(position).getScheduled_Monthly_Payment_Amount() !=null){
            notesBinding.txtEmiAmount.setText(mContext.getResources().getString(R.string.rs)+listdata.get(position).getScheduled_Monthly_Payment_Amount()+"");
        }
        else {
            notesBinding.txtEmiAmount.setText("-");
        }
        if (listdata.get(position).getMonthDueDay() !=null){
            notesBinding.txtEmiDate.setText(listdata.get(position).getMonthDueDay()+"");
        }
        else {
            notesBinding.txtEmiDate.setText("-");
        }
        if (listdata.get(position).getRepayment_Tenure()!=null){
            notesBinding.txtTenure.setText(listdata.get(position).getRepayment_Tenure()+" Month");
        }
        else {
            notesBinding.txtTenure.setText("-");
        }
        if (listdata.get(position).getRate_of_Interest()!=null){
            notesBinding.txtInterestRate.setText(listdata.get(position).getRate_of_Interest()+"%");
        } else {
            notesBinding.txtInterestRate.setText("-");
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