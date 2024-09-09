package com.app.rupyz.adapter.loan;

import static com.app.rupyz.generic.utils.SharePrefConstant.EMI_DEPOSIT_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.EMI_INSURANCE_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.EMI_OTHER_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.EMI_PROCESSING_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.EMI_TENURE_TYPE;

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
import com.app.rupyz.databinding.CompareEmiListInsideItemBinding;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.loan.EmiInfoModel;
import com.app.rupyz.generic.utils.DecimalDigitsInputFilter;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.ui.calculator.emi_cal.CompareEmiActivity;

import java.util.List;

public class CompareEmiListAdapter extends RecyclerView.Adapter<CompareEmiListAdapter.ViewHolder> {
    private List<EmiInfoModel> listdata;
    private CompareEmiListInsideItemBinding binding;
    private Context mContext;

    public CompareEmiListAdapter(List<EmiInfoModel> listdata, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = CompareEmiListInsideItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        listdata.get(position).setOtherChargeType(SharedPref.getInstance().getString(EMI_OTHER_TYPE));
        listdata.get(position).setDepositType(SharedPref.getInstance().getString(EMI_DEPOSIT_TYPE));
        listdata.get(position).setInsuranceType(SharedPref.getInstance().getString(EMI_INSURANCE_TYPE));
        listdata.get(position).setProcessingType(SharedPref.getInstance().getString(EMI_PROCESSING_TYPE));
        listdata.get(position).setLoanTenureType(SharedPref.getInstance().getString(EMI_TENURE_TYPE));

        binding.txtLoanNumber.setText("Loan -" + String.valueOf(position + 1));
        binding.etLoanAmount.setText(listdata.get(position).getLoan_amount());
        binding.etInterestRate.setText(listdata.get(position).getInterest_rate());
        binding.etTenure.setText(listdata.get(position).getLoan_tenure());
        binding.etOtherCharge.setText(listdata.get(position).getOther_charge());
        binding.etAdvanceEmi.setText(listdata.get(position).getAdvance_emi());
        binding.etDeposit.setText(listdata.get(position).getDeposit());
        binding.etProcessingFees.setText(listdata.get(position).getProcessing_fee());
        binding.etInsurance.setText(listdata.get(position).getInsurance());

        setBackgroundToggle();

        holder.binding.loanTenureToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                Logger.errorLogger("Toggle CLick", checkedId + " " + rb.getText().toString());
//                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb != null) {
                    rb.setTextColor(Color.WHITE);
                    rb.setBackground(mContext.getDrawable(R.drawable.toggle_left_round));
//                    int selectedId = binding.loanTenureToggle.getCheckedRadioButtonId();
                    if (rb.getText().toString().equalsIgnoreCase("M")) {
                        listdata.get(position).setLoanTenureType("MONTH");
                        holder.binding.loanTenureMonth.setTextColor(Color.WHITE);
                        holder.binding.loanTenureYear.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
                        holder.binding.loanTenureMonth.setBackground(mContext.getDrawable(R.drawable.toggle_left_round));
                        holder.binding.loanTenureYear.setBackground(mContext.getDrawable(R.drawable.toggle_right_round_white));
                    } else {
                        listdata.get(position).setLoanTenureType("YEAR");
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
                        listdata.get(position).setInsuranceType("Percentage");
                        holder.binding.insurancePercent.setTextColor(Color.WHITE);
                        holder.binding.insuranceAmount.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
                        holder.binding.insurancePercent.setBackground(mContext.getDrawable(R.drawable.toggle_left_round));
                        holder.binding.insuranceAmount.setBackground(mContext.getDrawable(R.drawable.toggle_right_round_white));
                    } else {
                        listdata.get(position).setInsuranceType("Amount");
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
                        listdata.get(position).setProcessingType("Percentage");
                        holder.binding.processingPercent.setTextColor(Color.WHITE);
                        holder.binding.processingAmount.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
                        holder.binding.processingPercent.setBackground(mContext.getDrawable(R.drawable.toggle_left_round));
                        holder.binding.processingAmount.setBackground(mContext.getDrawable(R.drawable.toggle_right_round_white));
                    } else if (selectedId == R.id.processing_amount) {
                        listdata.get(position).setProcessingType("Amount");
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
                        listdata.get(position).setOtherChargeType("Percentage");
                        clickedToggle(holder.binding.otherChargePercent, holder.binding.otherChargeAmount);
                    } else {
                        listdata.get(position).setOtherChargeType("AMount");
                        unClickedToggle(holder.binding.otherChargeAmount, holder.binding.otherChargePercent);
                    }
                }
            }
        });

        if (SharedPref.getInstance().getString(EMI_OTHER_TYPE).equals("Amount")) {
            binding.txvOtherchargeType.setText(mContext.getResources().getString(R.string.rs));
            binding.etOtherCharge.setText(listdata.get(position).getOtherValue());
        } else {
            binding.txvOtherchargeType.setText("%");
            binding.etOtherCharge.setText(listdata.get(position).getOtherValue());
            binding.etOtherCharge.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
        }

        if (SharedPref.getInstance().getString(EMI_PROCESSING_TYPE).equals("Amount")) {
            binding.txvProcessingType.setText(mContext.getResources().getString(R.string.rs));
            binding.etProcessingFees.setText(listdata.get(position).getProcessingValue());
        } else {
            binding.txvProcessingType.setText("%");
            binding.etProcessingFees.setText(listdata.get(position).getProcessingValue());
            binding.etProcessingFees.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
        }

        if (SharedPref.getInstance().getString(EMI_TENURE_TYPE).equals("MONTH")) {
            binding.txvTimeType.setText("M");
            binding.etTenure.setText(listdata.get(position).getTimeValue());
        } else {
            binding.txvTimeType.setText("Y");
            binding.etTenure.setText(listdata.get(position).getTimeValue());
        }

        if (SharedPref.getInstance().getString(EMI_INSURANCE_TYPE).equals("Amount")) {
            binding.txvInsuranceType.setText(mContext.getResources().getString(R.string.rs));
            binding.etInsurance.setText(listdata.get(position).getInsuranceValue());
        } else {
            binding.txvInsuranceType.setText("%");
            binding.etInsurance.setText(listdata.get(position).getInsuranceValue());
            binding.etInsurance.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
        }

        if (SharedPref.getInstance().getString(EMI_DEPOSIT_TYPE).equals("Amount")) {
            binding.txvDepositType.setText(mContext.getResources().getString(R.string.rs));
            binding.etDeposit.setText(listdata.get(position).getDepositValue());
        } else {
            binding.txvDepositType.setText("%");
            binding.etDeposit.setText(listdata.get(position).getDepositValue());
            binding.etDeposit.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
        }

        binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listdata.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, listdata.size());
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
                listdata.get(position).setLoan_amount(s.toString());
                ((CompareEmiActivity) mContext).initBottomSheet(listdata.get(position));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // listdata.get(position).setDepositValue("");

        binding.etDeposit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (SharedPref.getInstance().getString(EMI_TENURE_TYPE).equals("Amount")) {
                    listdata.get(position).setDeposit(s.toString());
                    listdata.get(position).setDepositValue(s.toString());
                } else {
                    if (!StringUtils.isBlank(s.toString())) {
                        double processingValue = ParseDouble(s.toString()) * ParseDouble(binding.etLoanAmount.getText().toString()) / 100;
                        listdata.get(position).setDeposit(String.valueOf(processingValue));
                        listdata.get(position).setDepositValue(s.toString());
                    } else {
                        listdata.get(position).setDeposit("");
                        listdata.get(position).setDepositValue("");
                    }
                }
                ((CompareEmiActivity) mContext).initBottomSheet(listdata.get(position));

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
                listdata.get(position).setInterest_rate(s.toString());
                ((CompareEmiActivity) mContext).initBottomSheet(listdata.get(position));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listdata.get(position).setTimeValue("");

        binding.etTenure.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (SharedPref.getInstance().getString(EMI_TENURE_TYPE).equals("MONTH")) {
                    listdata.get(position).setLoan_tenure(s.toString());
                } else {
                    if (!StringUtils.isBlank(s.toString())) {
                        int time = 12 * Integer.parseInt(s.toString());
                        listdata.get(position).setLoan_tenure(String.valueOf(time));
                        listdata.get(position).setTimeValue(s.toString());
                    } else {
                        listdata.get(position).setLoan_tenure("");
                        listdata.get(position).setTimeValue("");
                    }
                }

                ((CompareEmiActivity) mContext).initBottomSheet(listdata.get(position));
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
                    listdata.get(position).setOther_charge(s.toString());
                    listdata.get(position).setOtherValue(s.toString());
                } else {
                    if (!StringUtils.isBlank(s.toString())) {
                        double processingValue = ParseDouble(s.toString()) * ParseDouble(binding.etLoanAmount.getText().toString()) / 100;
                        listdata.get(position).setOther_charge(String.valueOf(processingValue));
                        listdata.get(position).setOtherValue(s.toString());
                    } else {
                        listdata.get(position).setOther_charge("");
                        listdata.get(position).setOtherValue("");
                    }
                }

                ((CompareEmiActivity) mContext).initBottomSheet(listdata.get(position));

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
                    listdata.get(position).setInsurance(s.toString());
                    listdata.get(position).setInsuranceValue(s.toString());
                } else {
                    if (!StringUtils.isBlank(s.toString())) {
                        double processingValue = ParseDouble(s.toString()) * ParseDouble(binding.etLoanAmount.getText().toString()) / 100;
                        listdata.get(position).setInsurance(String.valueOf(processingValue));
                        listdata.get(position).setInsuranceValue(s.toString());
                    } else {
                        listdata.get(position).setInsurance("");
                        listdata.get(position).setInsuranceValue("");
                    }
                }

                ((CompareEmiActivity) mContext).initBottomSheet(listdata.get(position));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etProcessingFees.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (SharedPref.getInstance().getString(EMI_PROCESSING_TYPE).equals("Amount")) {
                    listdata.get(position).setProcessing_fee(s.toString());
                    listdata.get(position).setProcessingValue(s.toString());
                } else {
                    if (!StringUtils.isBlank(s.toString())) {
                        double processingValue = ParseDouble(s.toString()) * ParseDouble(binding.etLoanAmount.getText().toString()) / 100;
                        listdata.get(position).setProcessing_fee(String.valueOf(processingValue));
                        listdata.get(position).setProcessingValue(s.toString());
                    } else {
                        listdata.get(position).setProcessing_fee("");
                        listdata.get(position).setProcessingValue("");
                    }
                }

                ((CompareEmiActivity) mContext).initBottomSheet(listdata.get(position));
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
                listdata.get(position).setAdvance_emi(s.toString());
                ((CompareEmiActivity) mContext).initBottomSheet(listdata.get(position));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etInterestRate.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});

    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CompareEmiListInsideItemBinding binding;

        public ViewHolder(CompareEmiListInsideItemBinding binding) {
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
        if (SharedPref.getInstance().getString(EMI_TENURE_TYPE).equals("MONTH")) {
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

        if (SharedPref.getInstance().getString(EMI_INSURANCE_TYPE).equals("Percentage")) {
            binding.insurancePercent.setTextColor(Color.WHITE);
            binding.insuranceAmount.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
            binding.insurancePercent.setBackground(mContext.getDrawable(R.drawable.toggle_left_round));
            binding.insuranceAmount.setBackground(mContext.getDrawable(R.drawable.toggle_right_round_white));
        } else {
            //binding.insuranceToggle.check(R.id.insurance_amount);
            binding.insurancePercent.setTextColor(mContext.getResources().getColor(R.color.toogle_unselected_text_color));
            binding.insuranceAmount.setTextColor(Color.WHITE);
            binding.insuranceAmount.setBackground(mContext.getDrawable(R.drawable.toggle_right_round));
            binding.insurancePercent.setBackground(mContext.getDrawable(R.drawable.toggle_left_round_white));
        }

        if (SharedPref.getInstance().getString(EMI_OTHER_TYPE).equals("Percentage")) {
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

        if (SharedPref.getInstance().getString(EMI_DEPOSIT_TYPE).equals("Percentage")) {
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

        if (SharedPref.getInstance().getString(EMI_PROCESSING_TYPE).equals("Percentage")) {
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