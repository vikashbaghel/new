package com.app.rupyz.adapter.organization;

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
import com.app.rupyz.generic.inteface.BottomSheetCallback;
import com.app.rupyz.generic.inteface.EventListener;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.createemi.CreateEMIResponse;
import com.app.rupyz.generic.model.organization.TradelinesItem;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.account.MyAccount;
import com.app.rupyz.ui.equifax.EquiFaxMyAccount;
import com.app.rupyz.ui.equifax.dailog.EquifaxCommercialAddEMIDetailSheet;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquiFaxTradeListAdapter extends RecyclerView.Adapter<EquiFaxTradeListAdapter.ViewHolder> {
    private List<TradelinesItem> listdata;
    private TradeLineListInsideItemBinding notesBinding;
    private Context mContext;

    public EquiFaxTradeListAdapter(List<TradelinesItem> listdata, Context mContext) {
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
        //emiDetails(position);
        notesBinding.txtSubscriberName.setText(listdata.get(position).getInstitutionName());

        if (listdata.get(position).getInstallmentAmount() !=null && listdata.get(position).getInstallmentAmount() !=null){
            notesBinding.txtEmiAmount.setText(mContext.getResources().getString(R.string.rs)+listdata.get(position).getInstallmentAmount()+"");
        }
        else {
            notesBinding.txtEmiAmount.setText("-");
        }
        if (listdata.get(position).getMonthDueDay() !=null && listdata.get(position).getMonthDueDay() !=0){
            notesBinding.txtEmiDate.setText(listdata.get(position).getMonthDueDay()+" of every month");
        }
        else {
            notesBinding.txtEmiDate.setText("-");
        }
        if (listdata.get(position).getRepaymentTenure()!=null && listdata.get(position).getRepaymentTenure()!=0){
            notesBinding.txtTenure.setText(listdata.get(position).getRepaymentTenure()+" Month");
        }
        else {
            notesBinding.txtTenure.setText("-");
        }
        if (listdata.get(position).getInterestRate()!=null && listdata.get(position).getInterestRate()!=0){
            notesBinding.txtInterestRate.setText(listdata.get(position).getInterestRate()+"%");
        } else {
            notesBinding.txtInterestRate.setText("-");
        }


        if (!StringUtils.isBlank(listdata.get(position).getOverdueAmount())) {
            notesBinding.txtOverdueAmount.setText(listdata.get(position).getOverdueAmount());
        }

        else {
            notesBinding.txtOverdueAmount.setText("-");
        }
        int repayment = listdata.get(position).getRepaymentsTotal()
                - listdata.get(position).getRepaymentsMissed();
        notesBinding.txtRepaymentAmount.setText(repayment + "/"
                + listdata.get(position).getRepaymentsTotal() + " on time");
        notesBinding.txtAccountType.setText(listdata.get(position).getCreditType());

        String sanctionAmount = mContext.getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
                listdata.get(position).getSanctionAmount()));
        notesBinding.txtSanctionAmount.setText(sanctionAmount);

        String currentBalance = mContext.getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
                listdata.get(position).getCurrentBalanceAmount()));
        notesBinding.txtBalanceAmount.setText(currentBalance);

        String paidAmount = mContext.getResources().getString(R.string.rs) +
                AmountHelper.getCommaSeptdAmount((AmountHelper.convertStringToDouble(listdata.get(position).getSanctionAmount()) -
                        AmountHelper.convertStringToDouble(listdata.get(position).getCurrentBalanceAmount())));
        notesBinding.txtPaidAmount.setText(paidAmount);
        notesBinding.txtSanctionDate.setText(DateFormatHelper.convertSanctionDate(listdata.get(position).getSanctionDate()));
        int maxAmount = AmountHelper.convertStringToInt(listdata.get(position).getSanctionAmount());
        int progressAmount = (int) (AmountHelper.convertStringToDouble(listdata.get(position).getSanctionAmount()) -
                AmountHelper.convertStringToDouble(listdata.get(position).getCurrentBalanceAmount()));
        notesBinding.progressBar.setMax(maxAmount);
        notesBinding.progressBar.setProgress(progressAmount);
        if (listdata.get(position).getAccountStatus().equalsIgnoreCase("OPEN")) {
            notesBinding.statusLayout.setBackgroundResource(R.drawable.active_status_bg_style);
        } else {
            notesBinding.statusLayout.setBackgroundResource(R.drawable.inactive_status_bg_style);
        }
        notesBinding.txtAccountStatus.setText(listdata.get(position).getAccountStatus());

        if(!StringUtils.isBlank(listdata.get(position).getAssetClassification())){
            if(listdata.get(position).getAssetClassification().equalsIgnoreCase("STD")){
                notesBinding.txtAssetClassification.setText(mContext.getResources().getString(R.string.standard));
            }

            else if(listdata.get(position).getAssetClassification().equalsIgnoreCase("DBT")){
                notesBinding.txtAssetClassification.setText(mContext.getResources().getString(R.string.doubtful));
            }

            else {
                notesBinding.txtAssetClassification.setText(listdata.get(position).getAssetClassification());
            }
        }

        notesBinding.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EquiFaxMyAccount) mContext).initBottomSheet(listdata.get(position));
            }
        });

        notesBinding.llAddEmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EquiFaxMyAccount) mContext).initAddEmiBottomSheet(listdata.get(position), position);
               // callback.callbackMethod(position);
            }
        });

    }

    /*private void emiDetails(int pos) {
        Call<CreateEMIResponse> call = mApiInterface.getEMIDetails(
                listdata.get(pos).getAccountNo(), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call.enqueue(new Callback<CreateEMIResponse>() {
            @Override
            public void onResponse(Call<CreateEMIResponse> call, Response<CreateEMIResponse> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());
                    CreateEMIResponse response1 = response.body();

                }
            }

            @Override
            public void onFailure(Call<CreateEMIResponse> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }*/

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

    public interface EventListener {
        void updateSingleItem(int pos);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TradeLineListInsideItemBinding binding;

        public ViewHolder(TradeLineListInsideItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}