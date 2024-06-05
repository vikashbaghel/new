package com.app.rupyz.ui.equifax.dailog;

import static com.app.rupyz.generic.helper.AmountHelper.convertInLac;

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
import com.app.rupyz.databinding.MyAccountDetailSheetBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.helper.DateFormatHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.individual.experian.CAISAccountHistory;
import com.app.rupyz.generic.model.individual.experian.Tradeline;
import com.app.rupyz.generic.model.organization.History48MonthsItem;
import com.app.rupyz.generic.model.organization.TradelinesItem;
import com.app.rupyz.generic.model.organization.individual.History48Months;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.ui.account.dailog.MyAccountDetailSheet;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class EquiFaxMyAccountDetailSheet extends BottomSheetDialogFragment {

    String myValue = "";
    private ImageButton mClose;
    private TradelinesItem mData;
    private EquifaxMyAccountDetailSheetBinding binding;
    private List<String> mYearData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myValue = this.getArguments().getString("data");
        Gson gson = new Gson();
        mData = gson.fromJson(myValue, TradelinesItem.class);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        //Set the custom view
        binding = EquifaxMyAccountDetailSheetBinding.inflate(getLayoutInflater());
        binding.txtSubscriberName.setText(mData.getInstitutionName());
        binding.txtAccountType.setText(mData.getCreditType());
        binding.txtAccountType.setText(mData.getCreditType());
        binding.txtAccountNumber.setText(mData.getAccountNo());

        if (mData.getAssetClassification() != null && !mData.getAssetClassification().equalsIgnoreCase("")) {
            if (mData.getAssetClassification().equalsIgnoreCase("STD")) {
                binding.txtAssetClassification.setText(getResources().getString(R.string.standard));
            } else if (mData.getAssetClassification().equalsIgnoreCase("DBT")) {
                binding.txtAssetClassification.setText(getResources().getString(R.string.doubtful));
            } else {
                binding.txtAssetClassification.setText(mData.getAssetClassification());
            }
        }

        if (mData.getInstallmentAmount() != null) {
            binding.txvEmiAmount.setText(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(mData.getInstallmentAmount()));
        } else {
            binding.txvEmiAmount.setText("-");
        }

        if (mData.getMonthDueDay() != null) {
            binding.txvEmiDate.setText(mData.getMonthDueDay() + " Of Every month");
        } else {
            binding.txvEmiDate.setText("-");
        }

        if (mData.getInterestRate() != null) {
            binding.txvInterestRate.setText(mData.getInterestRate() + " %");
        } else {
            binding.txvInterestRate.setText("-");
        }

        if (mData.getRepaymentTenure() != null) {
            binding.txvEmiTenure.setText(mData.getRepaymentTenure() + " months");
        } else {
            binding.txvEmiTenure.setText("-");
        }

        if (mData.getClosedDate() != null && !mData.getClosedDate().equalsIgnoreCase("")) {
            binding.txtClosedDate.setText(DateFormatHelper.conUnSupportedDateToString(mData.getSanctionDate()));
        }
        if (mData.getLastReportedDate() != null) {
            binding.txtLastPaymentDate.setText(DateFormatHelper.conUnSupportedDateToString(mData.getLastReportedDate()));
        }
        if (mData.getLastReportedDate() != null) {
            binding.txtLastUpdated.setText("Last updated by bureau on " + DateFormatHelper.convertSanctionDate(mData.getLastReportedDate()));
        }
        if (mData.getAccountStatus().equalsIgnoreCase("open")) {
            binding.statusLayout.setBackgroundResource(R.drawable.active_status_bg_style);
        } else {
            binding.statusLayout.setBackgroundResource(R.drawable.inactive_status_bg_style);
        }
        binding.txtAccountStatus.setText(mData.getAccountStatus());
        try {
            int repayment = mData.getRepaymentsTotal() - mData.getRepaymentsMissed();
            binding.txtRepayment.setText(repayment + "/"
                    + mData.getRepaymentsTotal() + " on time");
        } catch (Exception ex) {
        }
        try {
            String paidAmount = getResources().getString(R.string.rs) +
                    AmountHelper.getCommaSeptdAmount((Double.parseDouble(mData.getSanctionAmount()) -
                            Double.parseDouble(mData.getCurrentBalanceAmount())));
            binding.txtPaidAmount.setText(paidAmount);
            String currentBalance = getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(Double.parseDouble(
                    mData.getCurrentBalanceAmount()));
            binding.txtBalanceAmount.setText(currentBalance + "");


            try {
                String sanctionAmount = getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(Double.parseDouble(
                        mData.getSanctionAmount()));
                binding.txtSanctionAmount.setText(sanctionAmount);
                binding.txtHighestCredit.setText(sanctionAmount);
//
            } catch (Exception Ex) {

            }

            binding.txtSanctionDate.setText(DateFormatHelper.convertSanctionDate(mData.getSanctionDate()));
            if (mData.getOverdueAmount() != null) {
                binding.txtOverdueAmount.setText(mData.getOverdueAmount());
            }
            int maxAmount = Integer.parseInt(mData.getSanctionAmount());
            int progressAmount = (int) (Double.parseDouble(mData.getSanctionAmount()) -
                    Double.parseDouble(mData.getCurrentBalanceAmount()));
            binding.progressBar.setMax(maxAmount);
            binding.progressBar.setProgress(progressAmount);
        } catch (Exception Ex) {

        }
        try {
            if (mData.getHistory48Months().size() > 0) {
                mYearData = new ArrayList<>();
                for (History48MonthsItem Item : mData.getHistory48Months()) {
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
                for (History48MonthsItem Item : mData.getHistory48Months()) {
                    Log.e("TAG", "setupDialog" + Item.getMonth());
                    Logger.errorLogger("MissedPayment", Item.isMissed() + "");
                    if ((Item.getYear() + "").equalsIgnoreCase(mYearData.get(0))) {
                        if (Item.getMonth() == 1) {
                            if (!Item.isMissed()) {
                                binding.yearOneOneImageView.setVisibility(View.VISIBLE);
                                binding.yearOneOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneOneImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountOneOneTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountOneOneTxt.setText("-");
                            }

                        }
                        if (Item.getMonth() == 2) {

                            if (!Item.isMissed()) {
                                binding.yearOneTwoImageView.setVisibility(View.VISIBLE);
                                binding.yearOneTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneTwoImageView.setVisibility(View.VISIBLE);
                            }

                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountOneTwoTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountOneTwoTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 3) {
                            if (!Item.isMissed()) {
                                binding.yearOneThreeImageView.setVisibility(View.VISIBLE);
                                binding.yearOneThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneThreeImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountOneThreeTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountOneThreeTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 4) {
                            if (!Item.isMissed()) {
                                binding.yearOneFourImageView.setVisibility(View.VISIBLE);
                                binding.yearOneFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneFourImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountOneFourTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountOneFourTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 5) {
                            if (!Item.isMissed()) {
                                binding.yearOneFiveImageView.setVisibility(View.VISIBLE);
                                binding.yearOneFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneFiveImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountOneFiveTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountOneFiveTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 6) {
                            if (!Item.isMissed()) {
                                binding.yearOneSixImageView.setVisibility(View.VISIBLE);
                                binding.yearOneSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneSixImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountOneSixTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountOneSixTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 7) {
                            if (!Item.isMissed()) {
                                binding.yearOneSevenImageView.setVisibility(View.VISIBLE);
                                binding.yearOneSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneSevenImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountOneSevenTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountOneSevenTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 8) {
                            if (!Item.isMissed()) {
                                binding.yearOneEightImageView.setVisibility(View.VISIBLE);
                                binding.yearOneEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneEightImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountOneEightTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountOneEightTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 9) {
                            if (!Item.isMissed()) {
                                binding.yearOneNineImageView.setVisibility(View.VISIBLE);
                                binding.yearOneNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneNineImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountOneNineTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountOneNineTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 10) {
                            if (!Item.isMissed()) {
                                binding.yearOneTenImageView.setVisibility(View.VISIBLE);
                                binding.yearOneTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneTenImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountOneTenTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountOneTenTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 11) {
                            if (!Item.isMissed()) {
                                binding.yearOneElevenImageView.setVisibility(View.VISIBLE);
                                binding.yearOneElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneElevenImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountOneElevenTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountOneElevenTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 12) {
                            if (!Item.isMissed()) {
                                binding.yearOneTwelveImageView.setVisibility(View.VISIBLE);
                                binding.yearOneTwelveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearOneTwelveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearOneOneImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountOneTwelveTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountOneTwelveTxt.setText("-");
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
                for (History48MonthsItem Item : mData.getHistory48Months()) {
                    if ((Item.getYear() + "").equalsIgnoreCase(mYearData.get(1))) {
                        if (Item.getMonth() == 1) {
                            if (!Item.isMissed()) {
                                binding.yearTwoOneImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoOneImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountTwoOneTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountTwoOneTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 2) {
                            if (!Item.isMissed()) {
                                binding.yearTwoTwoImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoTwoImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountTwoTwoTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountTwoTwoTxt.setText("-");
                            }

                        }
                        if (Item.getMonth() == 3) {
                            if (!Item.isMissed()) {
                                binding.yearTwoThreeImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoThreeImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountTwoThreeTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountTwoThreeTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 4) {
                            if (!Item.isMissed()) {
                                binding.yearTwoFourImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoFourImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountTwoFourTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountTwoFourTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 5) {
                            if (!Item.isMissed()) {
                                binding.yearTwoFiveImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoFiveImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountTwoFiveTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountTwoFiveTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 6) {
                            if (!Item.isMissed()) {
                                binding.yearTwoSixImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoSixImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountTwoSixTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountTwoSixTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 7) {
                            if (!Item.isMissed()) {
                                binding.yearTwoSevenImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoSevenImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountTwoSevenTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountTwoSevenTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 8) {
                            if (!Item.isMissed()) {
                                binding.yearTwoEightImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoEightImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountTwoEightTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountTwoEightTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 9) {
                            if (!Item.isMissed()) {
                                binding.yearTwoNineImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoNineImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountTwoNineTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountTwoNineTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 10) {
                            if (!Item.isMissed()) {
                                binding.yearTwoTenImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoTenImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountTwoTenTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountTwoTenTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 11) {
                            if (!Item.isMissed()) {
                                binding.yearTwoElevenImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoElevenImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountTwoElevenTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountTwoElevenTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 12) {
                            if (!Item.isMissed()) {
                                binding.yearTwoTwelveImageView.setVisibility(View.VISIBLE);
                                binding.yearTwoTwelveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearTwoTwelveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearTwoTwelveImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountTwoTwelveTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountTwoTwelveTxt.setText("-");
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
                for (History48MonthsItem Item : mData.getHistory48Months()) {
                    if ((Item.getYear() + "").equalsIgnoreCase(mYearData.get(2))) {
                        if (Item.getMonth() == 1) {
                            if (!Item.isMissed()) {
                                binding.yearThreeOneImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeOneImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountThreeOneTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountThreeOneTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 2) {
                            if (!Item.isMissed()) {
                                binding.yearThreeTwoImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeTwoImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountThreeTwoTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountThreeTwoTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 3) {
                            if (!Item.isMissed()) {
                                binding.yearThreeThreeImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeThreeImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountThreeThreeTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountThreeThreeTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 4) {
                            if (!Item.isMissed()) {
                                binding.yearThreeFourImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeFourImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountThreeFourTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountThreeFourTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 5) {
                            if (!Item.isMissed()) {
                                binding.yearThreeFiveImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeFiveImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountThreeFiveTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountThreeFiveTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 6) {
                            if (!Item.isMissed()) {
                                binding.yearThreeSixImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeSixImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountThreeSixTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountThreeSixTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 7) {
                            if (!Item.isMissed()) {
                                binding.yearThreeSevenImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeSevenImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountThreeSevenTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountThreeSevenTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 8) {
                            if (!Item.isMissed()) {
                                binding.yearThreeEightImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeEightImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountThreeEightTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountThreeEightTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 9) {
                            if (!Item.isMissed()) {
                                binding.yearThreeNineImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeNineImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountThreeNineTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountThreeNineTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 10) {
                            if (!Item.isMissed()) {
                                binding.yearThreeTenImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeTenImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountThreeTenTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountThreeTenTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 11) {
                            if (!Item.isMissed()) {
                                binding.yearThreeElevenImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeElevenImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountThreeElevenTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountThreeElevenTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 12) {
                            if (!Item.isMissed()) {
                                binding.yearThreeTwelveImageView.setVisibility(View.VISIBLE);
                                binding.yearThreeTwelveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearThreeTwelveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearThreeTwelveImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountThreeTwelveTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountThreeTwelveTxt.setText("-");
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
                for (History48MonthsItem Item : mData.getHistory48Months()) {
                    if ((Item.getYear() + "").equalsIgnoreCase(mYearData.get(3))) {
                        if (Item.getMonth() == 1) {
                            if (!Item.isMissed()) {
                                binding.yearFourOneImageView.setVisibility(View.VISIBLE);
                                binding.yearFourOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourOneImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourOneImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountFourOneTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountFourOneTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 2) {
                            if (!Item.isMissed()) {
                                binding.yearFourTwoImageView.setVisibility(View.VISIBLE);
                                binding.yearFourTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourTwoImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourTwoImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountFourTwoTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountFourTwoTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 3) {
                            if (!Item.isMissed()) {
                                binding.yearFourThreeImageView.setVisibility(View.VISIBLE);
                                binding.yearFourThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourThreeImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourThreeImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountFourThreeTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountFourThreeTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 4) {
                            if (!Item.isMissed()) {
                                binding.yearFourFourImageView.setVisibility(View.VISIBLE);
                                binding.yearFourFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourFourImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourFourImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountFourFourTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountFourFourTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 5) {
                            if (!Item.isMissed()) {
                                binding.yearFourFiveImageView.setVisibility(View.VISIBLE);
                                binding.yearFourFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourFiveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourFiveImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountFourFiveTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountFourFiveTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 6) {
                            if (!Item.isMissed()) {
                                binding.yearFourSixImageView.setVisibility(View.VISIBLE);
                                binding.yearFourSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourSixImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourSixImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountFourSixTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountFourSixTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 7) {
                            if (!Item.isMissed()) {
                                binding.yearFourSevenImageView.setVisibility(View.VISIBLE);
                                binding.yearFourSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourSevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourSevenImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountFourSevenTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountFourSevenTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 8) {
                            if (!Item.isMissed()) {
                                binding.yearFourEightImageView.setVisibility(View.VISIBLE);
                                binding.yearFourEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourEightImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourEightImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountFourEightTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountFourEightTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 9) {
                            if (!Item.isMissed()) {
                                binding.yearFourNineImageView.setVisibility(View.VISIBLE);
                                binding.yearFourNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourNineImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourNineImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountFourNineTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountFourNineTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 10) {
                            if (!Item.isMissed()) {
                                binding.yearFourTenImageView.setVisibility(View.VISIBLE);
                                binding.yearFourTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourTenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourTenImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountFourTenTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountFourTenTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 11) {
                            if (!Item.isMissed()) {
                                binding.yearFourElevenImageView.setVisibility(View.VISIBLE);
                                binding.yearFourElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourElevenImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourElevenImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountFourElevenTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountFourElevenTxt.setText("-");
                            }
                        }
                        if (Item.getMonth() == 12) {
                            if (!Item.isMissed()) {
                                binding.yearFourTwelveImageView.setVisibility(View.VISIBLE);
                                binding.yearFourTwelveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_check));
                            } else {
                                binding.yearFourTwelveImageView.setBackground(getResources().getDrawable(R.mipmap.ic_close_red));
                                binding.yearFourTwelveImageView.setVisibility(View.VISIBLE);
                            }
                            if (!StringUtils.isBlank(Item.getCurrentBalanceLimitUtilizedMarktomarket())) {
                                binding.amountFourTwelveTxt.setText(convertInLac(Double.parseDouble(Item.getCurrentBalanceLimitUtilizedMarktomarket())));
                            } else {
                                binding.amountFourTwelveTxt.setText("-");
                            }
                        }

                    }
                }
            } else {
                binding.yearFourLayout.setVisibility(View.GONE);
            }
        } catch (Exception exception) {
            Logger.errorLogger("summaryException", exception.getMessage());
        }
        try {
            binding.btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EquiFaxMyAccountDetailSheet.this.dismiss();
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