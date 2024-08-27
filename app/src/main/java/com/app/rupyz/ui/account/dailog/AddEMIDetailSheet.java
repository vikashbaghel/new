package com.app.rupyz.ui.account.dailog;

import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;
import static com.app.rupyz.ui.account.fragment.AllAccountFragment.updateExperianSingle;
import static com.app.rupyz.ui.account.ownership_mix.IndividualFragment.updateOwnership;
import static com.app.rupyz.ui.equifax.fragment.my_account.EquiFaxAllAccountFragment.updateSingle;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.app.rupyz.R;
import com.app.rupyz.databinding.AddEmiDetailSheetBinding;
import com.app.rupyz.databinding.MyAccountDetailSheetBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.helper.ButtonStyleHelper;
import com.app.rupyz.generic.helper.DateFormatHelper;
import com.app.rupyz.generic.json.JsonHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.createemi.CreateEMI;
import com.app.rupyz.generic.model.createemi.CreateEMIResponse;
import com.app.rupyz.generic.model.createemi.CreateEMIResponse11;
import com.app.rupyz.generic.model.createemi.experian.ExperianCreateEMIResponse;
import com.app.rupyz.generic.model.individual.experian.CAISAccountHistory;
import com.app.rupyz.generic.model.individual.experian.Tradeline;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.calculator.loan_amount.LoanAmountCalActivity;
import com.app.rupyz.ui.individual.ProfileUpdateActivity;
import com.app.rupyz.ui.organization.GSTActivity;
import com.app.rupyz.ui.organization.PANVerifyActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEMIDetailSheet extends BottomSheetDialogFragment {

    String myValue = "";
    private ImageButton mClose;
    private Tradeline mData;
    private AddEmiDetailSheetBinding binding;
    private List<String> mYearData;
    String strInterestRate, strLoanTenure, strEmiAmount, strEmiDate;
    private ApiInterface mApiInterface;
    private int pos, type;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myValue = this.getArguments().getString("data");
        pos = this.getArguments().getInt("pos");
        type = this.getArguments().getInt("type");
        Gson gson = new Gson();
        mData = gson.fromJson(myValue, Tradeline.class);
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        //Set the custom view
        binding = AddEmiDetailSheetBinding.inflate(getLayoutInflater());

        binding.btnAddEmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    doRequest(mData.account_Number, mData.subscriber_Name, addChar(mData.open_Date, '-', 4, 7), pos);
                }
            }
        });

        if(mData.getRepayment_Tenure()!=null && mData.getRepayment_Tenure()!=0){
            binding.etTenure.setText(mData.getRepayment_Tenure()+"");
        }

        if(mData.getRate_of_Interest()!=null && mData.getRate_of_Interest()!=0){
            binding.etInterestRate.setText(mData.getRate_of_Interest()+"");
        }

        if(mData.getMonthDueDay()!=null && mData.getMonthDueDay()!=0){
            binding.etEmiDate.setText(mData.getMonthDueDay()+"");
        }

        if(mData.getScheduled_Monthly_Payment_Amount()!=null && mData.getScheduled_Monthly_Payment_Amount()!=0){
            binding.etEmiAmount.setText(mData.getScheduled_Monthly_Payment_Amount()+"");
        }

        binding.etEmiDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               /* String strEnteredVal = binding.etEmiDate.getText().toString();

                if(!strEnteredVal.equals("")){
                    int num=Integer.parseInt(strEnteredVal);
                    if(num<60){
                        binding.etEmiDate.setText(""+num);
                    }else{
                        binding.etEmiDate.setText("");
                    }
                }*/
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String added_number = binding.etEmiDate.getText().toString();
                if (added_number.length() != 0) {
                    int number  = Integer.parseInt(added_number);

                    if (number > 31 || number==0){
                        Toast.makeText(getContext(), "Not more than 31 or 0", Toast.LENGTH_SHORT).show();
                        binding.etEmiDate.setText("");
                    }


                }
            }
        });

/*        binding.etEmiDate.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            public void onTextChanged(CharSequence s, int start, int before, int count){
                String strEnteredVal = binding.etEmiDate.getText().toString();

                if(!strEnteredVal.equals("")){
                    int num=Integer.parseInt(strEnteredVal);
                    if(num<60){
                        binding.etEmiDate.setText(""+num);
                    }else{
                        binding.etEmiDate.setText("");
                    }
                }

            });*/


       /* binding.etEmiDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                strEmiDate = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.etEmiAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                strEmiAmount = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.etTenure.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                strLoanTenure = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.etInterestRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                strInterestRate = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
*/
        try {
            binding.btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddEMIDetailSheet.this.dismiss();
                }
            });
        } catch (Exception ex) {

        }
        dialog.setContentView(binding.getRoot());
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.getRoot().getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    String state = "";

                    switch (newState) {
                        case BottomSheetBehavior.STATE_DRAGGING: {
                            state = "DRAGGING";
                            break;
                        }
                        case BottomSheetBehavior.STATE_SETTLING: {
                            state = "SETTLING";
                            break;
                        }
                        case BottomSheetBehavior.STATE_EXPANDED: {
                            state = "EXPANDED";
                            break;
                        }
                        case BottomSheetBehavior.STATE_COLLAPSED: {
                            state = "COLLAPSED";
                            break;
                        }
                        case BottomSheetBehavior.STATE_HIDDEN: {
                            dismiss();
                            state = "HIDDEN";
                            break;
                        }
                    }

                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        }
    }

    private boolean validate() {
        boolean temp = true;
        strEmiAmount = binding.etEmiAmount.getText().toString();
        strInterestRate = binding.etInterestRate.getText().toString();
        strLoanTenure = binding.etTenure.getText().toString();
        strEmiDate = binding.etEmiDate.getText().toString();

        if (StringUtils.isBlank(strEmiAmount) && StringUtils.isBlank(strInterestRate) && StringUtils.isBlank(strLoanTenure) && StringUtils.isBlank(strEmiDate)) {
            Toast.makeText(getActivity(), "Enter EMI", Toast.LENGTH_SHORT).show();
            temp = false;
        }
        return temp;
    }

    private void doRequest(String accountNumber, String subscriber_name, String open_date, int pos) {

        CreateEMI createEMI = new CreateEMI();
        if(!StringUtils.isBlank(strInterestRate)){
            createEMI.setRate_of_interest(Double.valueOf(strInterestRate));
        }
        else {
            createEMI.setRate_of_interest(0.0);
        }

        if(!StringUtils.isBlank(strEmiDate)){
            createEMI.setMonth_due_day(Integer.parseInt(strEmiDate));
        }
        else {
            createEMI.setMonth_due_day(0);
        }

        if(!StringUtils.isBlank(strLoanTenure)){
            createEMI.setRepayment_tenure(Integer.parseInt(strLoanTenure));
        }
        else {
            createEMI.setRepayment_tenure(0);
        }

        if(!StringUtils.isBlank(strEmiAmount)){
            createEMI.setScheduled_monthly_payment_amount(Double.valueOf(strEmiAmount));
        }
        else {
            createEMI.setScheduled_monthly_payment_amount(0.0);
        }

        createEMI.setAccount_number(accountNumber);
        createEMI.setSubscriber_name(subscriber_name);
        createEMI.setOpen_date(open_date);

        Call<ExperianCreateEMIResponse> call = mApiInterface.createEMI(
                createEMI, "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call.enqueue(new Callback<ExperianCreateEMIResponse>() {
            @Override
            public void onResponse(Call<ExperianCreateEMIResponse> call, Response<ExperianCreateEMIResponse> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());

                    ExperianCreateEMIResponse response1 = response.body();
                    Toast.makeText(getContext(), response1.getMessage(), Toast.LENGTH_SHORT).show();
                    if(type==2){
                        updateExperianSingle(getActivity(), response1.getCreateEMI(), pos);
                    }
                    else {
                        updateOwnership(getActivity(), response1.getCreateEMI(), pos);
                    }

                    AddEMIDetailSheet.this.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ExperianCreateEMIResponse> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    public String addChar(String str, char ch, int position, int secondPosition) {
        StringBuilder sb = new StringBuilder(str);
        sb.insert(position, ch);
        sb.insert(secondPosition, ch);
        return sb.toString();
    }
}