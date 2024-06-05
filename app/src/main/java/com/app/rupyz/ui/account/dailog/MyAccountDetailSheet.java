package com.app.rupyz.ui.account.dailog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.app.rupyz.R;
import com.app.rupyz.databinding.MyAccountDetailSheetBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.helper.DateFormatHelper;
import com.app.rupyz.generic.model.individual.experian.CAISAccountHistory;
import com.app.rupyz.generic.model.individual.experian.Tradeline;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MyAccountDetailSheet extends BottomSheetDialogFragment {

    String myValue = "";
    private ImageButton mClose;
    private Tradeline mData;
    private MyAccountDetailSheetBinding binding;
    private List<String> mYearData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myValue = this.getArguments().getString("data");
        Gson gson = new Gson();
        mData = gson.fromJson(myValue, Tradeline.class);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        //Set the custom view
        binding = MyAccountDetailSheetBinding.inflate(getLayoutInflater());
        binding.txtSubscriberName.setText(mData.getSubscriber_Name());
        binding.txtAccountType.setText(mData.getAccount_Type());
        binding.txtAccountType.setText(mData.getAccount_Type());
        binding.txtAccountNumber.setText(mData.getAccount_Number());
        binding.txtAssetClassification.setText(mData.getAsset_classification());

        if (mData.getScheduled_Monthly_Payment_Amount() != null && mData.getScheduled_Monthly_Payment_Amount() != 0) {
            binding.txvEmiAmount.setText(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(mData.getScheduled_Monthly_Payment_Amount()));
        } else {
            binding.txvEmiAmount.setText("-");
        }

        if (mData.getMonthDueDay() != null && mData.getMonthDueDay() != 0) {
            binding.txvEmiDate.setText(mData.getMonthDueDay() + " of every month");
        } else {
            binding.txvEmiDate.setText("-");
        }

        if (mData.getRate_of_Interest() != null && mData.getRate_of_Interest() != 0) {
            binding.txvInterestRate.setText(mData.getRate_of_Interest() + " %");
        } else {
            binding.txvInterestRate.setText("-");
        }

        if (mData.getRepayment_Tenure() != null && mData.getRepayment_Tenure() != 0) {
            binding.txvEmiTenure.setText(mData.getRepayment_Tenure() + " months");
        } else {
            binding.txvEmiTenure.setText("-");
        }

        if (mData.getDate_Closed() != null) {
            binding.txtClosedDate.setText(DateFormatHelper.conUnSupportedDateToString(mData.getOpen_Date()));
        }
        if (mData.getDate_of_Last_Payment() != null) {
            binding.txtLastPaymentDate.setText(DateFormatHelper.conUnSupportedDateToString(mData.getDate_of_Last_Payment()));
        }
        if (mData.getDate_Reported() != null) {
            binding.txtLastUpdated.setText("Last updated by bureau on " + DateFormatHelper.conUnSupportedDateToString(mData.getDate_Reported()));
        }
        if (mData.getAccount_Status().equalsIgnoreCase("Active")) {
            binding.statusLayout.setBackgroundResource(R.drawable.active_status_bg_style);
        } else {
            binding.statusLayout.setBackgroundResource(R.drawable.inactive_status_bg_style);
        }
        binding.txtAccountStatus.setText(mData.getAccount_Status());
        try {
            String repayment = (Integer.parseInt(mData.getOntime_payment())
                    + Integer.parseInt(mData.getDelayed_payment())) + "";
            binding.txtRepayment.setText(mData.getOntime_payment() + "/" + repayment + " on time");
        } catch (Exception ex) {
        }
        try {
            String paidAmount = getResources().getString(R.string.rs) +
                    AmountHelper.getCommaSeptdAmount((Double.parseDouble(mData.getHighest_Credit_or_Original_Loan_Amount()) -
                            Double.parseDouble(mData.getCurrent_Balance())));
            binding.txtPaidAmount.setText(paidAmount);
            String currentBalance = getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(Double.parseDouble(
                    mData.getCurrent_Balance()));
            binding.txtBalanceAmount.setText(currentBalance + "");


            try {
                if (mData.getAccount_Type().equalsIgnoreCase("CREDIT CARD")) {
                    String sanctionAmount = getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
                            mData.getCredit_Limit_Amount()));
                    binding.txtSanctionAmount.setText(sanctionAmount);
                    binding.txtHighestCredit.setText(sanctionAmount);
                } else {
                    String sanctionAmount = getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
                            mData.getHighest_Credit_or_Original_Loan_Amount()));
                    binding.txtSanctionAmount.setText(sanctionAmount);
                    binding.txtHighestCredit.setText(sanctionAmount);
                }

            } catch (Exception Ex) {

            }
            binding.txtSanctionDate.setText(DateFormatHelper.conUnSupportedDateToString(mData.getOpen_Date()));
            if (mData.getAmount_Past_Due() != null) {
                binding.txtOverdueAmount.setText(mData.getAmount_Past_Due());
            }
            int maxAmount = Integer.parseInt(mData.getHighest_Credit_or_Original_Loan_Amount());
            int progressAmount = (int) (Double.parseDouble(mData.getHighest_Credit_or_Original_Loan_Amount()) -
                    Double.parseDouble(mData.getCurrent_Balance()));
            binding.progressBar.setMax(maxAmount);
            binding.progressBar.setProgress(progressAmount);
        } catch (Exception Ex) {

        }
        try {
            if (mData.getcAIS_Account_History().size() > 0) {
                mYearData = new ArrayList<>();
                for (CAISAccountHistory Item : mData.getcAIS_Account_History()) {
                    if (mYearData.size() > 0) {
                        boolean isYear = false;
                        for (String Year : mYearData) {
                            if (Year.equalsIgnoreCase(Item.getYear())) {
                                isYear = true;
                            }
                        }
                        if (!isYear) {
                            mYearData.add(Item.getYear());
                        }
                    } else {
                        mYearData.add(Item.getYear());
                    }
                }
            }
            if (mYearData.size() > 0) {
                binding.yearOneLayout.setVisibility(View.VISIBLE);
                binding.txtYearOne.setText(mYearData.get(0));
                for (CAISAccountHistory Item : mData.getcAIS_Account_History()) {
                    if (Item.getYear().equalsIgnoreCase(mYearData.get(0))) {
                        if (Item.getMonth().equalsIgnoreCase("01")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearOneOneImageView.setVisibility(View.VISIBLE);
                                binding.yearOneOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneOneImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("02")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearOneTwoImageView.setVisibility(View.VISIBLE);
                                binding.yearOneTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneTwoImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("03")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearOneThreeImageView.setVisibility(View.VISIBLE);
                                binding.yearOneThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneThreeImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("04")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearOneFourImageView.setVisibility(View.VISIBLE);
                                binding.yearOneFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneFourImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("05")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearOneFiveImageView.setVisibility(View.VISIBLE);
                                binding.yearOneFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneFiveImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("06")) {
                            if (Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearOneSixImageView.setVisibility(View.VISIBLE);
                                binding.yearOneSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneSixImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("07")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearOneSevenImageView.setVisibility(View.VISIBLE);
                                binding.yearOneSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneSevenImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("08")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearOneEightImageView.setVisibility(View.VISIBLE);
                                binding.yearOneEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneEightImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("09")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearOneNineImageView.setVisibility(View.VISIBLE);
                                binding.yearOneNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneNineImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("10")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearOneTenImageView.setVisibility(View.VISIBLE);
                                binding.yearOneTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneTenImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("11")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearOneElevenImageView.setVisibility(View.VISIBLE);
                                binding.yearOneElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneElevenImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("12")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearOneTwelveImageView.setVisibility(View.VISIBLE);
                                binding.yearOneTwelveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneTwelveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneOneImageView.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                }
            } else {
                binding.yearOneLayout.setVisibility(View.GONE);
            }
            if (mYearData.size() > 1) {
                binding.yearTwoLayout.setVisibility(View.VISIBLE);
                binding.txtYearTwo.setText(mYearData.get(1));
                for (CAISAccountHistory Item : mData.getcAIS_Account_History()) {
                    if (Item.getYear().equalsIgnoreCase(mYearData.get(1))) {
                        if (Item.getMonth().equalsIgnoreCase("01")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearTwoOneImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoOneImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("02")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearTwoTwoImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoTwoImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("03")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearTwoThreeImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoThreeImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("04")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearTwoFourImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoFourImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("05")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearTwoFiveImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoFiveImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("06")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearTwoSixImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoSixImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("07")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearTwoSevenImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoSevenImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("08")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearTwoEightImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoEightImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("09")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearTwoNineImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoNineImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("10")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearTwoTenImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoTenImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("11")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearTwoElevenImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoElevenImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("12")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearTwoTwelveImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoTwelveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoTwelveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoOneImageView.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                }
            } else {
                binding.yearTwoLayout.setVisibility(View.GONE);
            }
            if (mYearData.size() > 2) {
                binding.yearThreeLayout.setVisibility(View.VISIBLE);
                binding.txtYearThree.setText(mYearData.get(2));
                for (CAISAccountHistory Item : mData.getcAIS_Account_History()) {
                    if (Item.getYear().equalsIgnoreCase(mYearData.get(2))) {
                        if (Item.getMonth().equalsIgnoreCase("01")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearThreeOneImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeOneImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("02")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearThreeTwoImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeTwoImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("03")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearThreeThreeImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeThreeImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("04")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearThreeFourImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeFourImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("05")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearThreeFiveImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeFiveImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("06")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearThreeSixImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeSixImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("07")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearThreeSevenImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeSevenImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("08")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearThreeEightImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeEightImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("09")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearThreeNineImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeNineImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("10")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearThreeTenImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeTenImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("11")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearThreeElevenImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeElevenImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth().equalsIgnoreCase("12")) {
                            if (Item.getDays_Past_Due() == null || Item.getDays_Past_Due().equalsIgnoreCase("0")) {
                                binding.yearThreeTwelveImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeTwelveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeTwelveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeOneImageView.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                }
            } else {
                binding.yearThreeLayout.setVisibility(View.GONE);
            }
        } catch (Exception exception) {
        }
        try {
            binding.btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyAccountDetailSheet.this.dismiss();
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
}