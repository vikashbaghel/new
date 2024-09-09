package com.app.rupyz.ui.equifax.dailog;

import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;
import static com.app.rupyz.ui.equifax.fragment.individual.EquiFaxIndividualAllAccountFragment.updateIndividualSingle;
import static com.app.rupyz.ui.equifax.fragment.my_account.EquiFaxAllAccountFragment.updateSingle;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.app.rupyz.databinding.AddEmiDetailSheetBinding;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.createemi.CreateEMIResponse;
import com.app.rupyz.generic.model.createemi.CreateEMIResponse11;
import com.app.rupyz.generic.model.createemi.CreateEquifaxIndividualEMI;
import com.app.rupyz.generic.model.organization.TradelinesItem;
import com.app.rupyz.generic.model.organization.individual.Tradeline;
import com.app.rupyz.generic.model.organization.individual.Tradelines;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.generic.utils.Utility;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquifaxIndividualAddEMIDetailSheet extends BottomSheetDialogFragment {

    String myValue = "";
    private ImageButton mClose;
    private Tradeline mData;
    AddEmiDetailSheetBinding binding;
    private List<String> mYearData;
    String strInterestRate, strLoanTenure, strEmiAmount, strEmiDate;
    private EquiFaxApiInterface mApiInterface;
    int pos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myValue = this.getArguments().getString("data");
        pos = this.getArguments().getInt("pos");
        Gson gson = new Gson();
        mData = gson.fromJson(myValue, Tradeline.class);
        mApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface.class);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        //Set the custom view
        binding = AddEmiDetailSheetBinding.inflate(getLayoutInflater());

        if(mData.getRepayment_tenure()!=null){
            binding.etTenure.setText(mData.getRepayment_tenure()+"");
        }

        if(mData.getInterest_rate()!=null){
            binding.etInterestRate.setText(mData.getInterest_rate()+"");
        }

        if(mData.getMonth_due_day()!=null && mData.getMonth_due_day()!=0){
            binding.etEmiDate.setText(mData.getMonth_due_day()+"");
        }

        if(mData.getInstallment_amount()!=null){
            binding.etEmiAmount.setText(mData.getInstallment_amount()+"");
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

        binding.btnAddEmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    doRequest(mData.getAccount_no(), mData.getInstitution_name(), mData.getDate_opened(), mData.getAccount_type(), pos);
                }
            }
        });

        try {
            binding.btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EquifaxIndividualAddEMIDetailSheet.this.dismiss();
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

    private void doRequest(String accountNumber, String subscriber_name, String open_date, String credit_type, int pos) {

        CreateEquifaxIndividualEMI createEMI = new CreateEquifaxIndividualEMI();


        if(!StringUtils.isBlank(strInterestRate)){
            createEMI.setInterest_rate(Double.valueOf(strInterestRate));
        }
        else {
            createEMI.setInterest_rate(0.0);
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
            createEMI.setInstallment_amount(Double.valueOf(strEmiAmount));
        }
        else {
            createEMI.setInstallment_amount(0.0);
        }

        //createEMI.setInterest_rate(Double.valueOf(strInterestRate));
        createEMI.setAccount_no(accountNumber);
        createEMI.setInstitution_name(subscriber_name);
        //createEMI.setMonth_due_day(Integer.parseInt(strEmiDate));
        createEMI.setDate_opened(open_date);
        //createEMI.setRepayment_tenure(Integer.parseInt(strLoanTenure));
        //createEMI.setInstallment_amount(Double.valueOf(strEmiAmount));
        createEMI.setAccount_type(credit_type);
        createEMI.setOrg_id(0);


        Call<CreateEMIResponse11> call = mApiInterface.createEMI(
                createEMI, "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call.enqueue(new Callback<CreateEMIResponse11>() {
            @Override
            public void onResponse(Call<CreateEMIResponse11> call, Response<CreateEMIResponse11> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());
                    CreateEMIResponse11 response1 = response.body();
                    Toast.makeText(getContext(), response1.getMessage(), Toast.LENGTH_SHORT).show();
                    updateIndividualSingle(getActivity(), response1.getCreateEMI(), pos);
                    EquifaxIndividualAddEMIDetailSheet.this.dismiss();
                }
            }

            @Override
            public void onFailure(Call<CreateEMIResponse11> call, Throwable t) {
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