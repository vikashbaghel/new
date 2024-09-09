package com.app.rupyz.ui.equifax.dailog;


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
import com.app.rupyz.databinding.EquifaxMyAccountDetailSheetBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.helper.DateFormatHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.organization.individual.History48Months;
import com.app.rupyz.generic.model.organization.individual.Tradeline;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class EquiFaxIndividualMyAccountDetailSheet extends BottomSheetDialogFragment {

    private String myValue = "";
    private ImageButton mClose;
    private Tradeline mData;
    private EquifaxMyAccountDetailSheetBinding binding;
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
        binding = EquifaxMyAccountDetailSheetBinding.inflate(getLayoutInflater());
        binding.txtSubscriberName.setText(mData.getInstitution_name());
        binding.txtAccountType.setText(mData.getAccount_type());
        binding.txtAccountNumber.setText(mData.getAccount_no());
        if (mData.getInstallment_amount() != null) {
            binding.txvEmiAmount.setText(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(mData.getInstallment_amount()));
        } else {
            binding.txvEmiAmount.setText("-");
        }

        if (mData.getMonth_due_day() != null) {
            binding.txvEmiDate.setText(mData.getMonth_due_day() + " of every month");
        } else {
            binding.txvEmiDate.setText("-");
        }

        if (mData.getInterest_rate() != null) {
            binding.txvInterestRate.setText(mData.getInterest_rate() + " %");
        } else {
            binding.txvInterestRate.setText("-");
        }

        if (mData.getRepayment_tenure() != null) {
            binding.txvEmiTenure.setText(mData.getRepayment_tenure() + " months");
        } else {
            binding.txvEmiTenure.setText("-");
        }

        if (mData.getAsset_classification() != null && !mData.getAsset_classification().equalsIgnoreCase("")) {
            if (mData.getAsset_classification().equalsIgnoreCase("STD")) {
                binding.txtAssetClassification.setText(getResources().getString(R.string.standard));
            } else if (mData.getAsset_classification().equalsIgnoreCase("DBT")) {
                binding.txtAssetClassification.setText(getResources().getString(R.string.doubtful));
            } else {
                binding.txtAssetClassification.setText(mData.getAsset_classification());
            }
        }


        if (mData.getDate_closed() != null && !mData.getDate_closed().equalsIgnoreCase("")) {
            binding.txtClosedDate.setText(DateFormatHelper.conUnSupportedDateToString(mData.getDate_opened()));
        }
        if (mData.getDate_last_reported() != null) {
            binding.txtLastPaymentDate.setText(DateFormatHelper.conUnSupportedDateToString(mData.getDate_last_reported()));
        }
        if (mData.getDate_last_reported() != null) {
            binding.txtLastUpdated.setText("Last updated by bureau on " + DateFormatHelper.convertSanctionDate(mData.getDate_last_reported()));
        }
        if (mData.getAccount_status().equalsIgnoreCase("open")) {
            binding.statusLayout.setBackgroundResource(R.drawable.active_status_bg_style);
        } else {
            binding.statusLayout.setBackgroundResource(R.drawable.inactive_status_bg_style);
        }
        binding.txtAccountStatus.setText(mData.getAccount_status());
        try {
            int repayment = mData.getRepayments_total() - mData.getRepayments_missed();
            binding.txtRepayment.setText(repayment + "/"
                    + mData.getRepayments_total() + " on time");
        } catch (Exception ex) {
        }
        try {
            String paidAmount = getResources().getString(R.string.rs) +
                    AmountHelper.getCommaSeptdAmount((Double.parseDouble(mData.getSanction_amount() + "") -
                            Double.parseDouble(mData.getCurrent_balance_amount() + "")));
            binding.txtPaidAmount.setText(paidAmount);
            String currentBalance = getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(Double.parseDouble(
                    mData.getCurrent_balance_amount() + ""));
            binding.txtBalanceAmount.setText(currentBalance + "");


            try {
                String sanctionAmount = getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(Double.parseDouble(
                        mData.getSanction_amount() + ""));
                binding.txtSanctionAmount.setText(sanctionAmount);
                binding.txtHighestCredit.setText(sanctionAmount);
//
            } catch (Exception Ex) {

            }

            binding.txtSanctionDate.setText(DateFormatHelper.convertSanctionDate(mData.getSanction_amount() + ""));
            /*if (mData.getOverdue_amount() != null) {
                binding.txtOverdueAmount.setText(mData.getOverdue_amount());
            }*/
            int maxAmount = mData.getSanction_amount();
            int progressAmount = (int) (Double.parseDouble(mData.getSanction_amount() + "") -
                    Double.parseDouble(mData.getCurrent_balance_amount() + ""));
            binding.progressBar.setMax(maxAmount);
            binding.progressBar.setProgress(progressAmount);
        } catch (Exception Ex) {

        }
        try {
            if (mData.getHistory48Months().size() > 0) {
                mYearData = new ArrayList<>();
                for (History48Months Item : mData.getHistory48Months()) {
                    Logger.errorLogger("getMonth", Item.getMonth()+"");
                    Logger.errorLogger("getMonth", Item.getYear() + "");
                    if (mYearData.size() > 0) {
                        boolean isYear = false;
                        for (String Year : mYearData) {
                            if (Year.equalsIgnoreCase(Item.getYear() + "")) {
                                isYear = true;
                            }
                        }
                        if (!isYear) {
                            mYearData.add(Item.getYear() + "");
                        }
                    } else {
                        mYearData.add(Item.getYear() + "");
                    }
                }
            }
            if (mYearData.size() > 0) {
                binding.yearOneLayout.setVisibility(View.VISIBLE);
                binding.txtYearOne.setText(mYearData.get(0));
                for (History48Months Item : mData.getHistory48Months()) {
                    if ((Item.getYear() + "").equalsIgnoreCase(mYearData.get(0))) {
                        if (Item.getMonth() == 1) {
                            if (!Item.isIs_missed()) {
                                binding.yearOneOneImageView.setVisibility(View.VISIBLE);
                                binding.yearOneOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneOneImageView.setVisibility(View.VISIBLE);
                            }

                        }
                        if (Item.getMonth() == 2) {
                            if (!Item.isIs_missed()) {
                                binding.yearOneTwoImageView.setVisibility(View.VISIBLE);
                                binding.yearOneTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneTwoImageView.setVisibility(View.VISIBLE);
                            }

                           /* if(!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())){
                                binding.amountOneTwoTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountOneTwoTxt.setText("-");
                            }*/
                        }
                        if (Item.getMonth() == 3) {
                            if (!Item.isIs_missed()) {
                                binding.yearOneThreeImageView.setVisibility(View.VISIBLE);
                                binding.yearOneThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneThreeImageView.setVisibility(View.VISIBLE);
                            }
                            /*if(!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())){
                                binding.amountOneThreeTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountOneThreeTxt.setText("-");
                            }*/
                        }
                        if (Item.getMonth() == 4) {
                            if (!Item.isIs_missed()) {
                                binding.yearOneFourImageView.setVisibility(View.VISIBLE);
                                binding.yearOneFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneFourImageView.setVisibility(View.VISIBLE);
                            }
                            /*f(!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())){
                                binding.amountOneFourTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountOneFourTxt.setText("-");
                            }*/
                        }
                        if (Item.getMonth() == 5) {
                            if (!Item.isIs_missed()) {
                                binding.yearOneFiveImageView.setVisibility(View.VISIBLE);
                                binding.yearOneFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneFiveImageView.setVisibility(View.VISIBLE);
                            }
                            /*if(!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())){
                                binding.amountOneFiveTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountOneFiveTxt.setText("-");
                            }*/
                        }
                        if (Item.getMonth() == 6) {
                            if (!Item.isIs_missed()) {
                                binding.yearOneSixImageView.setVisibility(View.VISIBLE);
                                binding.yearOneSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneSixImageView.setVisibility(View.VISIBLE);
                            }
                            /*if(!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())){
                                binding.amountOneSixTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountOneSixTxt.setText("-");
                            }*/
                        }
                        if (Item.getMonth() == 7) {
                            if (!Item.isIs_missed()) {
                                binding.yearOneSevenImageView.setVisibility(View.VISIBLE);
                                binding.yearOneSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneSevenImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 8) {
                            if (!Item.isIs_missed()) {
                                binding.yearOneEightImageView.setVisibility(View.VISIBLE);
                                binding.yearOneEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneEightImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 9) {
                            if (!Item.isIs_missed()) {
                                binding.yearOneNineImageView.setVisibility(View.VISIBLE);
                                binding.yearOneNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneNineImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 10) {
                            if (!Item.isIs_missed()) {
                                binding.yearOneTenImageView.setVisibility(View.VISIBLE);
                                binding.yearOneTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneTenImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 11) {
                            if (!Item.isIs_missed()) {
                                binding.yearOneElevenImageView.setVisibility(View.VISIBLE);
                                binding.yearOneElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneElevenImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 12) {
                            if (!Item.isIs_missed()) {
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
                for (History48Months Item : mData.getHistory48Months()) {
                    if ((Item.getYear() + "").equalsIgnoreCase(mYearData.get(1))) {
                        if (Item.getMonth() == 1) {
                            if (!Item.isIs_missed()) {
                                binding.yearTwoOneImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoOneImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 2) {
                            if (!Item.isIs_missed()) {
                                binding.yearTwoTwoImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoTwoImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 3) {
                            if (!Item.isIs_missed()) {
                                binding.yearTwoThreeImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoThreeImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 4) {
                            if (!Item.isIs_missed()) {
                                binding.yearTwoFourImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoFourImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 5) {
                            if (!Item.isIs_missed()) {
                                binding.yearTwoFiveImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoFiveImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 6) {
                            if (!Item.isIs_missed()) {
                                binding.yearTwoSixImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoSixImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 7) {
                            if (!Item.isIs_missed()) {
                                binding.yearTwoSevenImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoSevenImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 8) {
                            if (!Item.isIs_missed()) {
                                binding.yearTwoEightImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoEightImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 9) {
                            if (!Item.isIs_missed()) {
                                binding.yearTwoNineImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoNineImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 10) {
                            if (!Item.isIs_missed()) {
                                binding.yearTwoTenImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoTenImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 11) {
                            if (!Item.isIs_missed()) {
                                binding.yearTwoElevenImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoElevenImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 12) {
                            if (!Item.isIs_missed()) {
                                binding.yearTwoTwelveImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoTwelveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoTwelveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoTwelveImageView.setVisibility(View.VISIBLE);
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
                for (History48Months Item : mData.getHistory48Months()) {
                    if ((Item.getYear() + "").equalsIgnoreCase(mYearData.get(2))) {
                        if (Item.getMonth() == 1) {
                            if (!Item.isIs_missed()) {
                                binding.yearThreeOneImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeOneImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 2) {
                            if (!Item.isIs_missed()) {
                                binding.yearThreeTwoImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeTwoImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 3) {
                            if (!Item.isIs_missed()) {
                                binding.yearThreeThreeImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeThreeImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 4) {
                            if (!Item.isIs_missed()) {
                                binding.yearThreeFourImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeFourImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 5) {
                            if (!Item.isIs_missed()) {
                                binding.yearThreeFiveImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeFiveImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 6) {
                            if (!Item.isIs_missed()) {
                                binding.yearThreeSixImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeSixImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 7) {
                            if (!Item.isIs_missed()) {
                                binding.yearThreeSevenImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeSevenImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 8) {
                            if (!Item.isIs_missed()) {
                                binding.yearThreeEightImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeEightImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 9) {
                            if (!Item.isIs_missed()) {
                                binding.yearThreeNineImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeNineImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 10) {
                            if (!Item.isIs_missed()) {
                                binding.yearThreeTenImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeTenImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 11) {
                            if (!Item.isIs_missed()) {
                                binding.yearThreeElevenImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeElevenImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 12) {
                            if (!Item.isIs_missed()) {
                                binding.yearThreeTwelveImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeTwelveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeTwelveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeTwelveImageView.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                }
            } else {
                binding.yearThreeLayout.setVisibility(View.GONE);
            }
            if (mYearData.size() > 3) {
                binding.yearFourLayout.setVisibility(View.VISIBLE);
                binding.txtYearFour.setText(mYearData.get(3));
                for (History48Months Item : mData.getHistory48Months()) {
                    if ((Item.getYear() + "").equalsIgnoreCase(mYearData.get(3))) {
                        if (Item.getMonth() == 1) {
                            if (!Item.isIs_missed()) {
                                binding.yearFourOneImageView.setVisibility(View.VISIBLE);
                                binding.yearFourOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourOneImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 2) {
                            if (!Item.isIs_missed()) {
                                binding.yearFourTwoImageView.setVisibility(View.VISIBLE);
                                binding.yearFourTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourTwoImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 3) {
                            if (!Item.isIs_missed()) {
                                binding.yearFourThreeImageView.setVisibility(View.VISIBLE);
                                binding.yearFourThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourThreeImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 4) {
                            if (!Item.isIs_missed()) {
                                binding.yearFourFourImageView.setVisibility(View.VISIBLE);
                                binding.yearFourFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourFourImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 5) {
                            if (!Item.isIs_missed()) {
                                binding.yearFourFiveImageView.setVisibility(View.VISIBLE);
                                binding.yearFourFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourFiveImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 6) {
                            if (!Item.isIs_missed()) {
                                binding.yearFourSixImageView.setVisibility(View.VISIBLE);
                                binding.yearFourSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourSixImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 7) {
                            if (!Item.isIs_missed()) {
                                binding.yearFourSevenImageView.setVisibility(View.VISIBLE);
                                binding.yearFourSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourSevenImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 8) {
                            if (!Item.isIs_missed()) {
                                binding.yearFourEightImageView.setVisibility(View.VISIBLE);
                                binding.yearFourEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourEightImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 9) {
                            if (!Item.isIs_missed()) {
                                binding.yearFourNineImageView.setVisibility(View.VISIBLE);
                                binding.yearFourNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourNineImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 10) {
                            if (!Item.isIs_missed()) {
                                binding.yearFourTenImageView.setVisibility(View.VISIBLE);
                                binding.yearFourTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourTenImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 11) {
                            if (!Item.isIs_missed()) {
                                binding.yearFourElevenImageView.setVisibility(View.VISIBLE);
                                binding.yearFourElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourElevenImageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if (Item.getMonth() == 12) {
                            if (!Item.isIs_missed()) {
                                binding.yearFourTwelveImageView.setVisibility(View.VISIBLE);
                                binding.yearFourTwelveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourTwelveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourTwelveImageView.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                }
            } else {
                binding.yearFourLayout.setVisibility(View.GONE);
            }
        } catch (Exception exception) {
        }
        try {
            binding.btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EquiFaxIndividualMyAccountDetailSheet.this.dismiss();
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