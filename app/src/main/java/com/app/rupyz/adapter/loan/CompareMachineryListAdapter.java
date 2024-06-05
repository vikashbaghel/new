package com.app.rupyz.adapter.loan;

import static com.app.rupyz.generic.utils.SharePrefConstant.EMI_INSURANCE_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.EMI_OTHER_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.EMI_PROCESSING_TYPE;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.CompareMachineryListInsideItemBinding;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.loan.MachineryInfoModel;
import com.app.rupyz.generic.utils.DecimalDigitsInputFilter;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.generic.utils.ToogleHelper;
import com.app.rupyz.ui.calculator.machinery.CompareMachinaryActivity;

import java.util.List;

public class CompareMachineryListAdapter extends RecyclerView.Adapter<CompareMachineryListAdapter.ViewHolder> {
    private List<MachineryInfoModel> listData;
    private CompareMachineryListInsideItemBinding binding;
    private Context mContext;
    private ToogleHelper toogleHelper;


    public CompareMachineryListAdapter(List<MachineryInfoModel> listData, Context mContext) {
        this.listData = listData;
        this.mContext = mContext;
        toogleHelper = ToogleHelper.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = CompareMachineryListInsideItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String loanNumber = "Loan -" + (position + 1);
        listData.get(position).setOtherChargeType(toogleHelper.getOtherChargeType());
        listData.get(position).setDepositType(toogleHelper.getDepositType());
        listData.get(position).setInsuranceType(toogleHelper.getInsuranceType());
        listData.get(position).setProcessingType(toogleHelper.getProcessingType());
        listData.get(position).setLoanTenureType(toogleHelper.getTenureType());
        binding.txtLoanNumber.setText(loanNumber);
        binding.etLoanAmount.setText(listData.get(position).getLoan_amount());
        binding.etInterestRate.setText(listData.get(position).getInterest_rate());
        binding.etAdvanceEmi.setText(listData.get(position).getAdvance_emi());
        binding.etDeposit.setText(listData.get(position).getDeposit());
        binding.etInsurance.setText(listData.get(position).getInsurance());
        binding.etInterestIncome.setText(listData.get(position).getInterest_income());
        binding.etTenure.setText(listData.get(position).getTimeValue());
        binding.etInsurance.setText(listData.get(position).getInsuranceValue());
        binding.etProcessingFees.setText(listData.get(position).getProcessingValue());
        binding.etOtherCharge.setText(listData.get(position).getOtherValue());
        setBackgroundToggle();
        binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listData.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, listData.size());
            }
        });

        binding.txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.llAddCancel.setVisibility(View.GONE);
            }
        });

        binding.etLoanAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listData.get(position).setLoan_amount(s.toString());
                ((CompareMachinaryActivity) mContext).initBottomSheet(listData.get(position));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        holder.binding.etDeposit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (listData.get(position).getDeposit().equals("Amount")) {
                    listData.get(position).setDeposit(s.toString());
                } else {
                    if (!StringUtils.isBlank(s.toString())) {
                        double depositValue = ParseDouble(s.toString()) * ParseDouble(binding.etLoanAmount.getText().toString()) / 100;
                        listData.get(position).setDeposit(s.toString());
                    } else {
                        listData.get(position).setDeposit("");
                        listData.get(position).setDepositType("");
                    }
                }
                ((CompareMachinaryActivity) mContext).initBottomSheet(listData.get(position));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etInterestRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listData.get(position).setInterest_rate(s.toString());
                ((CompareMachinaryActivity) mContext).initBottomSheet(listData.get(position));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etTenure.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (listData.get(position).getLoanTenureType().equals("MONTH")) {
                    listData.get(position).setLoan_tenure(s.toString());
                    listData.get(position).setTimeValue(s.toString());
                } else {
                    if (!StringUtils.isBlank(s.toString())) {
                        int time = 12 * Integer.parseInt(s.toString());
                        listData.get(position).setLoan_tenure(String.valueOf(time));
                        listData.get(position).setTimeValue(s.toString());
                    } else {
                        listData.get(position).setLoan_tenure("");
                        listData.get(position).setTimeValue("");
                    }
                }

                ((CompareMachinaryActivity) mContext).initBottomSheet(listData.get(position));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.etOtherCharge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (SharedPref.getInstance().getString(EMI_OTHER_TYPE).equals("Amount")) {
                    listData.get(position).setOther_charge(s.toString());
                } else {
                    if (!StringUtils.isBlank(s.toString())) {
                        double processingValue = ParseDouble(s.toString()) * ParseDouble(binding.etLoanAmount.getText().toString()) / 100;
                        listData.get(position).setOther_charge(String.valueOf(processingValue));
                        listData.get(position).setOtherValue(s.toString());
                    } else {
                        listData.get(position).setOther_charge("");
                        listData.get(position).setOtherValue("");
                    }
                }

                ((CompareMachinaryActivity) mContext).initBottomSheet(listData.get(position));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.etInsurance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (SharedPref.getInstance().getString(EMI_INSURANCE_TYPE).equals("Amount")) {
                    listData.get(position).setInsurance(s.toString());
                } else {
                    if (!StringUtils.isBlank(s.toString())) {
                        double processingValue = ParseDouble(s.toString()) * ParseDouble(binding.etLoanAmount.getText().toString()) / 100;
                        listData.get(position).setInsurance(String.valueOf(processingValue));
                        listData.get(position).setInsuranceValue(s.toString());
                    } else {
                        listData.get(position).setInsurance("");
                        listData.get(position).setInsuranceValue("");
                    }
                }

                ((CompareMachinaryActivity) mContext).initBottomSheet(listData.get(position));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.binding.etProcessingFees.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (SharedPref.getInstance().getString(EMI_PROCESSING_TYPE).equals("Amount")) {
                    listData.get(position).setProcessing_fee(s.toString());
                } else {
                    if (!StringUtils.isBlank(s.toString())) {
                        double processingValue = ParseDouble(s.toString()) * ParseDouble(binding.etLoanAmount.getText().toString()) / 100;
                        listData.get(position).setProcessing_fee(String.valueOf(processingValue));
                        listData.get(position).setProcessingValue(s.toString());
                    } else {
                        listData.get(position).setProcessing_fee("");
                        listData.get(position).setProcessingValue("");
                    }
                }

                ((CompareMachinaryActivity) mContext).initBottomSheet(listData.get(position));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etAdvanceEmi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listData.get(position).setAdvance_emi(s.toString());
                ((CompareMachinaryActivity) mContext).initBottomSheet(listData.get(position));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etInterestRate.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
        binding.etInterestIncome.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});

        binding.etInterestIncome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listData.get(position).setInterest_income(s.toString());
                ((CompareMachinaryActivity) mContext).initBottomSheet(listData.get(position));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.binding.loanTenureToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                Logger.errorLogger("Toggle CLick", checkedId + " " + rb.getText().toString());
                if (rb != null) {
                    rb.setTextColor(Color.WHITE);
                    rb.setBackground(mContext.getDrawable(R.drawable.toggle_left_round));
                    if (rb.getText().toString().equalsIgnoreCase("M")) {
                        listData.get(position).setLoanTenureType("MONTH");
                        holder.binding.loanTenureMonth.setTextColor(Color.WHITE);
                        holder.binding.loanTenureYear.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
                        holder.binding.loanTenureMonth.setBackground(mContext.getDrawable(R.drawable.toggle_left_round));
                        holder.binding.loanTenureYear.setBackground(mContext.getDrawable(R.drawable.toggle_right_round_white));
                    } else {
                        listData.get(position).setLoanTenureType("YEAR");
                        holder.binding.loanTenureMonth.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
                        holder.binding.loanTenureYear.setTextColor(Color.WHITE);
                        holder.binding.loanTenureYear.setBackground(mContext.getDrawable(R.drawable.toggle_right_round));
                        holder.binding.loanTenureMonth.setBackground(mContext.getDrawable(R.drawable.toggle_left_round_white));
                    }
                }
            }
        });

        holder.binding.insuranceToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb != null) {
                    if (rb.getText().toString().equalsIgnoreCase("%")) {
                        listData.get(position).setInsuranceType("Percentage");
                        holder.binding.insurancePercent.setTextColor(Color.WHITE);
                        holder.binding.insuranceAmount.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
                        holder.binding.insurancePercent.setBackground(mContext.getDrawable(R.drawable.toggle_left_round));
                        holder.binding.insuranceAmount.setBackground(mContext.getDrawable(R.drawable.toggle_right_round_white));
                    } else {
                        listData.get(position).setInsuranceType("Amount");
                        holder.binding.insurancePercent.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
                        holder.binding.insuranceAmount.setTextColor(Color.WHITE);
                        holder.binding.insuranceAmount.setBackground(mContext.getDrawable(R.drawable.toggle_right_round));
                        holder.binding.insurancePercent.setBackground(mContext.getDrawable(R.drawable.toggle_left_round_white));
                    }
                }
            }
        });

        holder.binding.processingToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb) {
                    rb.setTextColor(Color.WHITE);
                    int selectedId = binding.processingToggle.getCheckedRadioButtonId();
                    if (selectedId == R.id.processing_percent) {
                        listData.get(position).setProcessingType("Percentage");
                        holder.binding.processingPercent.setTextColor(Color.WHITE);
                        holder.binding.processingAmount.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
                        holder.binding.processingPercent.setBackground(mContext.getDrawable(R.drawable.toggle_left_round));
                        holder.binding.processingAmount.setBackground(mContext.getDrawable(R.drawable.toggle_right_round_white));
                    } else if (selectedId == R.id.processing_amount) {
                        listData.get(position).setProcessingType("Amount");
                        holder.binding.processingPercent.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
                        holder.binding.processingAmount.setTextColor(Color.WHITE);
                        holder.binding.processingAmount.setBackground(mContext.getDrawable(R.drawable.toggle_right_round));
                        holder.binding.processingPercent.setBackground(mContext.getDrawable(R.drawable.toggle_left_round_white));
                    }
                }
            }
        });

        holder.binding.otherChargeToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb != null) {
                    if (rb.getText().toString().equalsIgnoreCase("%")) {
                        listData.get(position).setOtherChargeType("Percentage");
                        clickedToggle(holder.binding.otherChargePercent, holder.binding.otherChargeAmount);
                    } else {
                        listData.get(position).setOtherChargeType("AMount");
                        unClickedToggle(holder.binding.otherChargeAmount, holder.binding.otherChargePercent);
                    }
                }
            }
        });

        holder.binding.depositToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton) radioGroup.findViewById(i);
                if (rb != null) {
                    if (rb.getText().toString().equalsIgnoreCase("%")) {
                        listData.get(position).setDepositType("Percentage");
                        clickedToggle(holder.binding.depositPercent, holder.binding.depositAmount);
                    } else {
                        listData.get(position).setDepositType("Amount");
                        unClickedToggle(holder.binding.depositAmount, holder.binding.depositPercent);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CompareMachineryListInsideItemBinding binding;

        public ViewHolder(CompareMachineryListInsideItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch (Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        } else return 0;
    }


    private void setBackgroundToggle() {
        if (toogleHelper.getTenureType().equals("MONTH")) {
            binding.loanTenureMonth.setTextColor(Color.WHITE);
            binding.loanTenureYear.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
            binding.loanTenureMonth.setBackground(mContext.getDrawable(R.drawable.toggle_left_round));
            binding.loanTenureYear.setBackground(mContext.getDrawable(R.drawable.toggle_right_round_white));
        } else {
            binding.loanTenureMonth.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
            binding.loanTenureYear.setTextColor(Color.WHITE);
            binding.loanTenureYear.setBackground(mContext.getDrawable(R.drawable.toggle_right_round));
            binding.loanTenureMonth.setBackground(mContext.getDrawable(R.drawable.toggle_left_round_white));
        }

        if (toogleHelper.getInsuranceType().equals("Percentage")) {
            binding.insurancePercent.setTextColor(Color.WHITE);
            binding.insuranceAmount.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
            binding.insurancePercent.setBackground(mContext.getDrawable(R.drawable.toggle_left_round));
            binding.insuranceAmount.setBackground(mContext.getDrawable(R.drawable.toggle_right_round_white));
        } else {
            binding.insurancePercent.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
            binding.insuranceAmount.setTextColor(Color.WHITE);
            binding.insuranceAmount.setBackground(mContext.getDrawable(R.drawable.toggle_right_round));
            binding.insurancePercent.setBackground(mContext.getDrawable(R.drawable.toggle_left_round_white));
        }

        if (toogleHelper.getOtherChargeType().equals("Percentage")) {
            binding.otherChargePercent.setTextColor(Color.WHITE);
            binding.otherChargeAmount.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
            binding.otherChargePercent.setBackground(mContext.getDrawable(R.drawable.toggle_left_round));
            binding.otherChargeAmount.setBackground(mContext.getDrawable(R.drawable.toggle_right_round_white));
        } else {
            binding.otherChargePercent.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
            binding.otherChargeAmount.setTextColor(Color.WHITE);
            binding.otherChargeAmount.setBackground(mContext.getDrawable(R.drawable.toggle_right_round));
            binding.otherChargePercent.setBackground(mContext.getDrawable(R.drawable.toggle_left_round_white));
        }

        if (toogleHelper.getDepositType().equals("Percentage")) {
            binding.depositPercent.setTextColor(Color.WHITE);
            binding.depositAmount.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
            binding.depositPercent.setBackground(mContext.getDrawable(R.drawable.toggle_left_round));
            binding.depositAmount.setBackground(mContext.getDrawable(R.drawable.toggle_right_round_white));
        } else {
            binding.depositPercent.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
            binding.depositAmount.setTextColor(Color.WHITE);
            binding.depositAmount.setBackground(mContext.getDrawable(R.drawable.toggle_right_round));
            binding.depositPercent.setBackground(mContext.getDrawable(R.drawable.toggle_left_round_white));
        }

        if (toogleHelper.getProcessingType().equals("Percentage")) {
            binding.processingPercent.setTextColor(Color.WHITE);
            binding.processingAmount.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
            binding.processingPercent.setBackground(mContext.getDrawable(R.drawable.toggle_left_round));
            binding.processingAmount.setBackground(mContext.getDrawable(R.drawable.toggle_right_round_white));
        } else {
            binding.processingPercent.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
            binding.processingAmount.setTextColor(Color.WHITE);
            binding.processingAmount.setBackground(mContext.getDrawable(R.drawable.toggle_right_round));
            binding.processingPercent.setBackground(mContext.getDrawable(R.drawable.toggle_left_round_white));
        }

    }

    private void clickedToggle(TextView textView1, TextView textView2) {
        textView1.setTextColor(Color.WHITE);
        textView1.setBackground(mContext.getDrawable(R.drawable.toggle_left_round));
        textView2.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
        textView2.setBackground(mContext.getDrawable(R.drawable.toggle_right_round_white));
    }

    private void unClickedToggle(TextView textView1, TextView textView2) {
        textView2.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
        textView2.setBackground(mContext.getDrawable(R.drawable.toggle_left_round_white));
        textView1.setTextColor(Color.WHITE);
        textView1.setBackground(mContext.getDrawable(R.drawable.toggle_right_round));
    }

}