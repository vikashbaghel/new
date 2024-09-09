package com.app.rupyz.ui.equifax.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;


import com.app.rupyz.R;

import com.app.rupyz.databinding.EquifaxHomeIndividualFragmentBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;

import com.app.rupyz.generic.helper.ScoreInsightsHelper;
import com.app.rupyz.generic.helper.StringHelper;
import com.app.rupyz.generic.logger.FirebaseLogger;

import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.organization.individual.EquiFaxIndividualInfoModel;

import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.utils.DummyChartData;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.Utility;

import com.app.rupyz.ui.account.OwnershipMixActivity;
import com.app.rupyz.ui.calculator.all_calculator.AllCalculatorActivity;

import com.app.rupyz.ui.equifax.EquiFaxIndividualMyAccount;

import com.app.rupyz.ui.equifax.fragment.home_slide_individual.ScorePagerAdapter;
import com.app.rupyz.ui.home.dailog.score_insights.ScoreInsightInfoModal;
import com.app.rupyz.ui.home.dailog.score_insights.ScoreInsightRemarkModal;
import com.app.rupyz.ui.user.ProfileActivity;
import com.github.mikephil.charting.components.Legend;
import com.google.gson.Gson;


public class EquiFaxIndividualFragment extends Fragment implements View.OnClickListener {

    private EquifaxHomeIndividualFragmentBinding binding;
    public EquiFaxIndividualInfoModel mData;
    private EquiFaxReportHelper mReportHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mReportHelper = EquiFaxReportHelper.getInstance();

        new FirebaseLogger(getContext()).sendLog("Home Fragment", "Home Fragment");
        binding = EquifaxHomeIndividualFragmentBinding.inflate(getLayoutInflater());
        binding.loanLayout.loanLayoutRoot.setOnClickListener(this);
        binding.repaymentsOverdueLayout.overdueRootLayout.setOnClickListener(this);
        binding.repaymentsOverdueLayout.repaymentRootLayout.setOnClickListener(this);
        binding.negativeNpaLayout.negativeRootLayout.setOnClickListener(this);
        binding.negativeNpaLayout.npaRootLayout.setOnClickListener(this);
        binding.loanLayout.btnMore.setOnClickListener(this);
        binding.loanLayout.btnMoreDetails.setOnClickListener(this);
        binding.dashboardRiskLayoutNull.btnConcern.setOnClickListener(this);
        binding.ownershipMixLayout.ownershipMixRootLayout.setOnClickListener(this);
        initData();
        initToolbar();
        return binding.getRoot();
    }

    private void initToolbar() {
        /*Toolbar toolBar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("Calculator");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/


/*        Toolbar toolBar = this.findViewById(R.id.toolbar_my);
        ImageView imageViewBack = toolBar.findViewById(R.id.img_back);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");*/
        binding.headerText.setText(mData.getReport().getFull_name());
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void intiRiskSlider() {
        ScorePagerAdapter adapter = new ScorePagerAdapter(getChildFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, false);
        binding.dashboardRiskLayout.viewpager.setAdapter(adapter);
        binding.dashboardRiskLayout.tablayout.setupWithViewPager(binding.dashboardRiskLayout.viewpager, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loan_layout_root:
                initMyAccount(0);
                break;
/*            case R.id.btn_calculator:
                startActivity(new Intent(getActivity(), AllCalculatorActivity.class));
                break;*/
            case R.id.overdue_root_layout:
                initMyAccount(1);
                break;
            case R.id.repayment_root_layout:
                initMyAccount(2);
                break;
            case R.id.negative_root_layout:
                initMyAccount(3);
                break;
            case R.id.npa_root_layout:
                initMyAccount(3);
                break;
            case R.id.btn_more:
                initMyAccount(0);
                break;
            case R.id.ownership_mix_root_layout:
                // navOwnershipMix();
                break;
            case R.id.btn_profile:
                startActivity(new Intent(getActivity(), ProfileActivity.class));
                break;
            case R.id.btn_more_details:
                initMyAccount(0);
                break;
            case R.id.btn_concern:
                new SessionHelper(getActivity()).messageToast("Our Expert will connect with you shorty.");
                break;
            case R.id.btn_negative:
                initScoreInsightRemark(getResources().getString(R.string.negative), binding.scoreInsights.txtNegativeStatus.getText().toString());
                break;
            case R.id.btn_credit_card:
                initScoreInsightRemark(getResources().getString(R.string.credit_card), binding.scoreInsights.txtCreditCardStatus.getText().toString());
                break;
            case R.id.btn_credit_age:
                initScoreInsightRemark(getResources().getString(R.string.credit_age), binding.scoreInsights.txtCreditAgeStatus.getText().toString());
                break;
            case R.id.btn_repayment:
                initScoreInsightRemark(getResources().getString(R.string.repayment), binding.scoreInsights.txtRepaymentStatus.getText().toString());
                break;
            case R.id.btn_new_loan:
                initScoreInsightRemark(getResources().getString(R.string.new_loan), binding.scoreInsights.txtLoanStatus.getText().toString());
                break;
            case R.id.btn_secured:
                initScoreInsightRemark(getResources().getString(R.string.secured), binding.scoreInsights.txtSecuredLoanStatus.getText().toString());
                break;
            case R.id.btn_score_insights_info:
                initScoreInsightsSheet();
                break;
        }
    }

    private void initData() {

        try {

            mData = mReportHelper.getRetailReport();
            Logger.errorLogger(this.getClass().getName(), mData.getReport_for() + "");
            if (mData != null) {
                if ((mData.getMetadata().getRetail_progress_step() + "").equalsIgnoreCase("3.1")) {
                    binding.nullProfileLayout.setVisibility(View.VISIBLE);
                    binding.fillProfileLayout.setVisibility(View.GONE);
                } else if ((mData.getMetadata().getRetail_progress_step() + "").equalsIgnoreCase("2.2")) {
                    binding.nullProfileLayout.setVisibility(View.VISIBLE);
                    binding.fillProfileLayout.setVisibility(View.GONE);
                } else if ((mData.getMetadata().getRetail_progress_step() + "").equalsIgnoreCase("3.0")) {
                    binding.nullProfileLayout.setVisibility(View.GONE);
                    binding.fillProfileLayout.setVisibility(View.VISIBLE);
                    initResponseData();
                } else {
                    binding.nullProfileLayout.setVisibility(View.VISIBLE);
                    binding.fillProfileLayout.setVisibility(View.GONE);
                }

            } else {
                binding.nullProfileLayout.setVisibility(View.VISIBLE);
                binding.fillProfileLayout.setVisibility(View.GONE);
            }
            if (binding.nullProfileLayout.getVisibility() == View.VISIBLE) {
                binding.dashboardRiskLayoutNull.imgCreditBureau.setImageDrawable(getResources().getDrawable(R.mipmap.ic_equifax_logo));
            }
        } catch (Exception ex) {
            Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
        }
    }

    private void initResponseData() {
        intiRiskSlider();
        initScoreInsights();
        try {
            /*binding.txtUserName.setText(StringHelper.toCamelCase(
                    mData.getReport().getFull_name()));*/
            initLoanData();
            initRepayments();
            initOverdue();
            initNegative();
            initFacilityMix();
            initOwnershipMix();
            initNPA();
        } catch (Exception ex) {
        }
    }

    private void initLoanData() {
        binding.loanLayout.loanActive.setText(mData.getReport().getLoans_active_count() + "");
        binding.loanLayout.loanClose.setText(mData.getReport().getLoans_closed_count() + "");
        binding.loanLayout.loanChart.setDrawHoleEnabled(true);
        binding.loanLayout.loanChart.setUsePercentValues(false);
        binding.loanLayout.loanChart.setEntryLabelTextSize(12);
        binding.loanLayout.loanChart.setEntryLabelColor(Color.BLACK);
        binding.loanLayout.loanChart.setCenterText(mData.getReport().getLoans_total_count() + "");
        binding.loanLayout.loanChart.setCenterTextSize(18);
        binding.loanLayout.loanChart.setCenterTextColor(Color.BLACK);
        binding.loanLayout.loanChart.setDrawHoleEnabled(true);
        binding.loanLayout.loanChart.setHoleColor(Color.TRANSPARENT);
        binding.loanLayout.loanChart.setHoleRadius(60);
        binding.loanLayout.loanChart.setMaxAngle(180);
        binding.loanLayout.loanChart.setRotationAngle(90);
        binding.loanLayout.loanChart.setMinAngleForSlices(0);
        binding.loanLayout.loanChart.getDescription().setEnabled(false);
        binding.loanLayout.loanChart.setRotationEnabled(false);
        binding.loanLayout.loanChart.setTouchEnabled(false);
        Legend l = binding.loanLayout.loanChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
        new DummyChartData().loadPieChartData(binding.loanLayout.loanChart, mData.getReport().getLoans_active_count(),
                mData.getReport().getLoans_closed_count());
    }

    private void initRepayments() {
        binding.repaymentsOverdueLayout.txtRepaymentOnTime.setText(
                mData.getReport().getRepayments_total_count() - mData.getReport().getRepayments_missed_count()
                        + "/" + (mData.getReport().getRepayments_total_count() + " On time"));
        binding.repaymentsOverdueLayout.repaymentSeekBar.setMax(mData.getReport().getRepayments_total_count());
        binding.repaymentsOverdueLayout.repaymentSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        binding.repaymentsOverdueLayout.repaymentSeekBar.setProgress(mData.getReport().getRepayments_total_count() - mData.getReport().getRepayments_missed_count());
        if (mData.getReport().getRepayments_total_count() - mData.getReport().getRepayments_missed_count() == (mData.getReport().getRepayments_total_count())) {
            binding.repaymentsOverdueLayout.repaymentAward.setVisibility(View.VISIBLE);
        } else {
            binding.repaymentsOverdueLayout.repaymentAward.setVisibility(View.GONE);
        }
    }

    private void initOverdue() {
        if (mData.getReport().getAggregated_overdue_amount() == 0) {
            binding.repaymentsOverdueLayout.overdueProgressBar.setBackground(
                    getResources().getDrawable(R.drawable.overdue_green_style));
            binding.repaymentsOverdueLayout.txtOverdueAccount.setVisibility(View.GONE);
            binding.repaymentsOverdueLayout.txtOverdueAmount.setVisibility(View.GONE);
            binding.repaymentsOverdueLayout.overdueValue.setVisibility(View.GONE);
            binding.repaymentsOverdueLayout.overdueDesign.setVisibility(View.VISIBLE);
            binding.repaymentsOverdueLayout.overdueAward.setVisibility(View.VISIBLE);
        } else {
            binding.repaymentsOverdueLayout.overdueProgressBar.setBackground(
                    getResources().getDrawable(R.drawable.overdue_red_style));
            binding.repaymentsOverdueLayout.txtOverdueAccount.setText(
                    mData.getReport().getOverdue_count() + " - Accounts"
            );
            binding.repaymentsOverdueLayout.txtOverdueAmount.setText(
                    getResources().getString(R.string.rs) + mData.getReport().getAggregated_overdue_amount()
            );
            binding.repaymentsOverdueLayout.overdueValue.setVisibility(View.VISIBLE);
            binding.repaymentsOverdueLayout.overdueDesign.setVisibility(View.GONE);
            binding.repaymentsOverdueLayout.overdueAward.setVisibility(View.GONE);
        }
    }

    private void initNegative() {
        if (mData.getReport().getNegative_accounts_count() == 0) {
            binding.negativeNpaLayout.negativeProgressBar.setBackground(
                    getResources().getDrawable(R.drawable.negative_account_green_style));
            binding.negativeNpaLayout.txtNegativeAccount.setVisibility(View.GONE);
            binding.negativeNpaLayout.txtNegativeAmount.setText("No Negative Account");
            binding.negativeNpaLayout.txtNegativeAmount.setTextColor(
                    getResources().getColor(R.color.light_green));
            binding.negativeNpaLayout.txtNegativeAmount.setTextSize(12f);
            binding.negativeNpaLayout.negativeAward.setVisibility(View.VISIBLE);
        } else {
            binding.negativeNpaLayout.txtNegativeAccount.setText(
                    mData.getReport().getNegative_accounts_count() + " - Accounts"
            );
            binding.negativeNpaLayout.txtNegativeAmount.setText(
                    getResources().getString(R.string.rs) + mData.getReport().getNegative_accounts_amount()
            );
            binding.negativeNpaLayout.negativeAward.setVisibility(View.GONE);
        }

    }

    private void initOwnershipMix() {

        binding.ownershipMixLayout.txtIndividual.setText(
                getResources().getString(R.string.ownership_mix_individual)
                        + " (" + getResources().getString(R.string.rs)
                        + AmountHelper.getCommaSeptdAmount((double) mData.getReport().getOwnership_mix().getIndividual().getAmount()) + ")"
        );
        binding.ownershipMixLayout.txtJoint.setText(
                getResources().getString(R.string.ownership_mix_joint_guarantor)
                        + " (" + getResources().getString(R.string.rs)
                        + AmountHelper.getCommaSeptdAmount((double) mData.getReport().getOwnership_mix().getJointGuarantor().getAmount()) + ")"
        );

        binding.ownershipMixLayout.ownershipPieChart.setDrawHoleEnabled(true);
        binding.ownershipMixLayout.ownershipPieChart.setHoleRadius(70);
        binding.ownershipMixLayout.ownershipPieChart.setTouchEnabled(false);
        binding.ownershipMixLayout.ownershipPieChart.setUsePercentValues(false);
        binding.ownershipMixLayout.ownershipPieChart.setEntryLabelTextSize(12);
        binding.ownershipMixLayout.ownershipPieChart.setEntryLabelColor(Color.BLACK);
        binding.ownershipMixLayout.ownershipPieChart.setCenterText(getResources().getString(R.string.rs)
                + AmountHelper.getCommaSeptdAmount((double) (mData.getReport().getOwnership_mix().getIndividual().getAmount()
                + mData.getReport().getOwnership_mix().getJointGuarantor().getAmount())) + "");
        binding.ownershipMixLayout.ownershipPieChart.setCenterTextSize(8);
        binding.ownershipMixLayout.ownershipPieChart.setCenterTextColor(Color.BLACK);
        binding.ownershipMixLayout.ownershipPieChart.setDrawHoleEnabled(true);
        binding.ownershipMixLayout.ownershipPieChart.setHoleColor(Color.TRANSPARENT);
        binding.ownershipMixLayout.ownershipPieChart.setHoleRadius(60);
        Typeface typeface = ResourcesCompat.getFont(getActivity(), R.font.roboto_regular);
        binding.ownershipMixLayout.ownershipPieChart.setCenterTextTypeface(typeface);
        binding.ownershipMixLayout.ownershipPieChart.getDescription().setEnabled(false);

        Legend l = binding.ownershipMixLayout.ownershipPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
        new DummyChartData().loadOwnershipMax(binding.ownershipMixLayout.ownershipPieChart,
                mData.getReport().getOwnership_mix().getIndividual().getAmount(),
                mData.getReport().getOwnership_mix().getJointGuarantor().getAmount());
    }

    private void initFacilityMix() {

        binding.facilityMixLayout.txtSecuredLoan.setText(
                getResources().getString(R.string.secured_loan) + " - "
                        + mData.getReport().getFacility_mix().getsECUREDLOAN());
        binding.facilityMixLayout.txtUnSecuredLoan.setText(
                getResources().getString(R.string.unsecured_loan) + " - "
                        + mData.getReport().getFacility_mix().getuNSECUREDLOAN());

        int total_mix = mData.getReport().getFacility_mix().getsECUREDLOAN()
                + mData.getReport().getFacility_mix().getuNSECUREDLOAN();

        if (mData.getReport().getFacility_mix().getsECUREDLOAN() == 0) {
            binding.facilityMixLayout.facilityMixSecuredPro.setVisibility(View.GONE);
            binding.facilityMixLayout.securedLayout.setVisibility(View.GONE);
        } else {
            LinearLayout.LayoutParams workingCapitalProLayoutParams = (LinearLayout.LayoutParams)
                    binding.facilityMixLayout.facilityMixSecuredPro.getLayoutParams();
            workingCapitalProLayoutParams.weight = (mData.getReport().getFacility_mix().getsECUREDLOAN() * 100) / total_mix;
            binding.facilityMixLayout.facilityMixSecuredPro.setLayoutParams(workingCapitalProLayoutParams);
        }


        if (mData.getReport().getFacility_mix().getuNSECUREDLOAN() == 0) {
            binding.facilityMixLayout.facilityMixUnsecuredPro.setVisibility(View.GONE);
            binding.facilityMixLayout.unsecuredLayout.setVisibility(View.GONE);
        } else {
            LinearLayout.LayoutParams personalLoanProLayoutParams = (LinearLayout.LayoutParams)
                    binding.facilityMixLayout.facilityMixUnsecuredPro.getLayoutParams();
            personalLoanProLayoutParams.weight = (mData.getReport().getFacility_mix().getuNSECUREDLOAN() * 100) / total_mix;
            binding.facilityMixLayout.facilityMixUnsecuredPro.setLayoutParams(personalLoanProLayoutParams);
        }
    }

    private void initNPA() {
        if (mData.getReport().getDefaults_and_npa_count() == 0) {
            binding.negativeNpaLayout.npaProgressBar.setBackground(
                    getResources().getDrawable(R.drawable.negative_account_green_style));
            binding.negativeNpaLayout.txtNpaAccount.setVisibility(View.GONE);
            binding.negativeNpaLayout.txtNpaAmount.setText(getResources().getString(R.string.no_defaults_npa_account));
            binding.negativeNpaLayout.txtNpaAmount.setTextColor(
                    getResources().getColor(R.color.light_green));
            binding.negativeNpaLayout.txtNpaAmount.setTextSize(12f);
            binding.negativeNpaLayout.npaAward.setVisibility(View.VISIBLE);
        } else {

            String npa_account_count = mData.getReport().getNegative_accounts_count() + " - Accounts";
            binding.negativeNpaLayout.txtNpaAccount.setText(npa_account_count);
            String npa_total_amount = getResources().getString(R.string.rs) + mData.getReport().getAggregated_defaults_and_npa_amount();
            binding.negativeNpaLayout.txtNpaAmount.setText(npa_total_amount);
            binding.negativeNpaLayout.negativeAward.setVisibility(View.GONE);
        }
    }

    private void initScoreInsights() {

        // Negative tile data binding & Calculation
        int total_npa = mData.getReport().getDefaults_and_npa_count();
        binding.scoreInsights.negativeMessage.setText(new ScoreInsightsHelper().getNegativeMessage(
                mData.getReport().getNegative_accounts_count(), total_npa, (int) mData.getReport().getAggregated_overdue_amount()));
        new ScoreInsightsHelper().setNegativeStatus(mData.getReport().getNegative_accounts_count(), total_npa,
                (int) mData.getReport().getAggregated_overdue_amount(),
                binding.scoreInsights.imgNegative, binding.scoreInsights.txtNegativeStatus, getActivity());


        // repayment tile data binding & calculation
        new ScoreInsightsHelper().setRepaymentStatus(mData.getReport().getRepayments_total_count()
                        - mData.getReport().getRepayments_missed_count(),
                mData.getReport().getRepayments_total_count(),
                binding.scoreInsights.imgRepayment, binding.scoreInsights.txtRepaymentStatus,
                binding.scoreInsights.repaymentMessage, getActivity());

        //credit card utilization data binding & calculation

        new ScoreInsightsHelper().setCreditCardStatusEquiFax(mData.getReport().getTradelines(),
                binding.scoreInsights.imgCreditCard, binding.scoreInsights.txtCreditCardStatus,
                binding.scoreInsights.creditCardMessage, getActivity());


        //Credit Age data binding & calculation

        new ScoreInsightsHelper().setCreditAgeStatus(mData.getReport().getCredit_age(),
                binding.scoreInsights.imgCreditAge, binding.scoreInsights.txtCreditAgeStatus,
                binding.scoreInsights.creditAgeMessage, getActivity());


        //New Loan & New Inquiries in last 12 months data binding & calculation


        new ScoreInsightsHelper().setLoanStatusEquiFax(mData.getReport().getTradelines(), mData.getReport().getEnq_past12months() + "",
                binding.scoreInsights.imgLoan, binding.scoreInsights.txtLoanStatus,
                binding.scoreInsights.loanMessage, getActivity());


        //secured & un-secured in last 12 months data binding & calculation

        new ScoreInsightsHelper().setFacilityMixStatus(
                mData.getReport().getFacility_mix().getsECUREDLOAN(),
                mData.getReport().getFacility_mix().getuNSECUREDLOAN(),
                binding.scoreInsights.imgSecuredLoan, binding.scoreInsights.txtSecuredLoanStatus,
                binding.scoreInsights.securedLoanMessage, getActivity());


        binding.scoreInsights.btnNegative.setOnClickListener(this);
        binding.scoreInsights.btnCreditAge.setOnClickListener(this);
        binding.scoreInsights.btnCreditCard.setOnClickListener(this);
        binding.scoreInsights.btnNewLoan.setOnClickListener(this);
        binding.scoreInsights.btnRepayment.setOnClickListener(this);
        binding.scoreInsights.btnSecured.setOnClickListener(this);
        binding.btnScoreInsightsInfo.setOnClickListener(this);
    }

    private void navOwnershipMix() {
        Gson gson = new Gson();
        Intent intent = new Intent(getActivity(), OwnershipMixActivity.class);
        intent.putExtra("data", gson.toJson(mData.getReport().getTradelines()));
        startActivity(intent);
    }

    private void initMyAccount(int index) {
        Gson gson = new Gson();
        Intent intent = new Intent(getActivity(), EquiFaxIndividualMyAccount.class);
        intent.putExtra("data", gson.toJson(mData.getReport().getTradelines()));
        intent.putExtra("index", index);
        startActivity(intent);
    }

    private void initScoreInsightRemark(String type, String value) {
        if (type.equalsIgnoreCase(getResources().getString(R.string.negative))) {
            if (value.equalsIgnoreCase(getResources().getString(R.string.poor))) {
                initBottomSheet(getResources().getString(R.string.negative_poor)
                        , getResources().getString(R.string.poor));
            } else if (value.equalsIgnoreCase(getResources().getString(R.string.average))) {
                initBottomSheet(getResources().getString(R.string.negative_average),
                        getResources().getString(R.string.average));
            } else if (value.equalsIgnoreCase(getResources().getString(R.string.excellent))) {
                initBottomSheet(getResources().getString(R.string.negative_excellent),
                        getResources().getString(R.string.excellent));
            }
        } else if (type.equalsIgnoreCase(getResources().getString(R.string.repayment))) {
            if (value.equalsIgnoreCase(getResources().getString(R.string.poor))) {
                initBottomSheet(getResources().getString(R.string.repayment_poor),
                        getResources().getString(R.string.poor));
            } else if (value.equalsIgnoreCase(getResources().getString(R.string.good))) {
                initBottomSheet(getResources().getString(R.string.repayment_good),
                        getResources().getString(R.string.good));
            } else if (value.equalsIgnoreCase(getResources().getString(R.string.average))) {
                initBottomSheet(getResources().getString(R.string.repayment_average),
                        getResources().getString(R.string.average));
            } else if (value.equalsIgnoreCase(getResources().getString(R.string.excellent))) {
                initBottomSheet(getResources().getString(R.string.repayment_excellent),
                        getResources().getString(R.string.excellent));
            }
        } else if (type.equalsIgnoreCase(getResources().getString(R.string.credit_card))) {
            if (value.equalsIgnoreCase(getResources().getString(R.string.poor))) {
                initBottomSheet(getResources().getString(R.string.credit_card_poor),
                        getResources().getString(R.string.poor));
            } else if (value.equalsIgnoreCase(getResources().getString(R.string.good))) {
                initBottomSheet(getResources().getString(R.string.credit_card_good),
                        getResources().getString(R.string.good));
            } else if (value.equalsIgnoreCase(getResources().getString(R.string.average))) {
                initBottomSheet(getResources().getString(R.string.credit_card_average),
                        getResources().getString(R.string.average));
            } else if (value.equalsIgnoreCase(getResources().getString(R.string.excellent))) {
                initBottomSheet(getResources().getString(R.string.credit_card_excellent),
                        getResources().getString(R.string.excellent));
            }
        } else if (type.equalsIgnoreCase(getResources().getString(R.string.credit_age))) {
            if (value.equalsIgnoreCase(getResources().getString(R.string.poor))) {
                initBottomSheet(getResources().getString(R.string.credit_age_poor),
                        getResources().getString(R.string.poor));
            } else if (value.equalsIgnoreCase(getResources().getString(R.string.good))) {
                initBottomSheet(getResources().getString(R.string.credit_age_good),
                        getResources().getString(R.string.good));
            } else if (value.equalsIgnoreCase(getResources().getString(R.string.average))) {
                initBottomSheet(getResources().getString(R.string.credit_age_average),
                        getResources().getString(R.string.average));
            } else if (value.equalsIgnoreCase(getResources().getString(R.string.excellent))) {
                initBottomSheet(getResources().getString(R.string.credit_age_excellent),
                        getResources().getString(R.string.excellent));
            }
        } else if (type.equalsIgnoreCase(getResources().getString(R.string.new_loan))) {
            if (value.equalsIgnoreCase(getResources().getString(R.string.poor))) {
                initBottomSheet(getResources().getString(R.string.new_loan_poor),
                        getResources().getString(R.string.poor));
            } else if (value.equalsIgnoreCase(getResources().getString(R.string.average))) {
                initBottomSheet(getResources().getString(R.string.new_loan_average),
                        getResources().getString(R.string.average));
            } else if (value.equalsIgnoreCase(getResources().getString(R.string.excellent))) {
                initBottomSheet(getResources().getString(R.string.new_loan_excellent),
                        getResources().getString(R.string.excellent));
            }
        } else if (type.equalsIgnoreCase(getResources().getString(R.string.secured))) {
            if (value.equalsIgnoreCase(getResources().getString(R.string.good))) {
                initBottomSheet(getResources().getString(R.string.secured_good),
                        getResources().getString(R.string.good));
            } else if (value.equalsIgnoreCase(getResources().getString(R.string.average))) {
                initBottomSheet(getResources().getString(R.string.secured_average),
                        getResources().getString(R.string.average));
            }
        }
    }

    public void initScoreInsightsSheet() {
        Bundle bundle = new Bundle();
        bundle.putString("data", "");
        ScoreInsightInfoModal fragment = new ScoreInsightInfoModal();
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(), "");
    }

    public void initBottomSheet(String text, String type) {
        Bundle bundle = new Bundle();
        bundle.putString("data", text);
        bundle.putString("type", type);
        ScoreInsightRemarkModal fragment = new ScoreInsightRemarkModal();
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(), "");
    }

}
