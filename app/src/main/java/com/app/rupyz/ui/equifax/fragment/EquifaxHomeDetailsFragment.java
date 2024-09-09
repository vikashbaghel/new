package com.app.rupyz.ui.equifax.fragment;

import static com.app.rupyz.generic.helper.AmountHelper.convertInLac;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.rupyz.R;
import com.app.rupyz.adapter.blogs.EMIListAdapter;
import com.app.rupyz.adapter.blogs.MicroblogListAdapter;
import com.app.rupyz.adapter.complaince.ComplianceCalendarListAdapter;
import com.app.rupyz.databinding.EquifaxHomeCommercialFragmentBinding;
import com.app.rupyz.databinding.FragmentEquifaxHomeDetailsBinding;
import com.app.rupyz.databinding.FragmentHomeDetailsBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.helper.EquifaxScoreInsightsHelper;
import com.app.rupyz.generic.helper.StringHelper;
import com.app.rupyz.generic.logger.FirebaseLogger;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.blog.HomeDataInfo;
import com.app.rupyz.generic.model.organization.EquiFaxInfoModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.utils.DummyChartData;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.calculator.interest_rate.InterestRateCalActivity;
import com.app.rupyz.ui.equifax.EquiFaxMyAccount;
import com.app.rupyz.ui.equifax.bottomsheet.EquiFaxScoreInsightInfoModal;
import com.app.rupyz.ui.equifax.dailog.EquiFaxScoreInsightRemarkModal;
import com.app.rupyz.ui.equifax.fragment.home_slide.ScorePagerAdapter;
import com.app.rupyz.ui.user.ProfileActivity;
import com.github.mikephil.charting.components.Legend;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquifaxHomeDetailsFragment extends Fragment implements View.OnClickListener{

    private FragmentEquifaxHomeDetailsBinding binding;
    private EquiFaxInfoModel mData;
    private EquiFaxReportHelper mReportHelper;
    private ApiInterface mApiInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mReportHelper = EquiFaxReportHelper.getInstance();
        new FirebaseLogger(getContext()).sendLog("Home Fragment", "Home Fragment");
        binding = FragmentEquifaxHomeDetailsBinding.inflate(getLayoutInflater());
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        binding.loanLayout.loanLayoutRoot.setOnClickListener(this);
        binding.repaymentsOverdueLayout.overdueRootLayout.setOnClickListener(this);
        binding.repaymentsOverdueLayout.repaymentRootLayout.setOnClickListener(this);
        binding.negativeNpaLayout.negativeRootLayout.setOnClickListener(this);
        binding.negativeNpaLayout.npaRootLayout.setOnClickListener(this);
        binding.loanLayout.btnMore.setOnClickListener(this);
        binding.loanLayout.btnMoreDetails.setOnClickListener(this);
        binding.dashboardRiskLayoutNull.btnConcern.setOnClickListener(this);
        binding.lenderMixLayout.ownershipMixRootLayout.setOnClickListener(this);
        binding.btnScoreInsightsInfo.setOnClickListener(this);
        initData();
/*        binding.recyclerViewComplaince.setHasFixedSize(true);
        binding.recyclerViewComplaince.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewMicroblog.setHasFixedSize(true);
        binding.recyclerViewMicroblog.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewEmi.setHasFixedSize(true);
        binding.recyclerViewEmi.setLayoutManager(new LinearLayoutManager(getActivity()));*/
        /*getComplianceData();
        getMicroBlocks();
        getHomeData();*/
        initToolbar();
        return binding.getRoot();
    }

    private void intiRiskSlider() {
        ScorePagerAdapter adapter = new ScorePagerAdapter(getChildFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, false);
        binding.dashboardRiskLayout.viewpager.setAdapter(adapter);
        binding.dashboardRiskLayout.tablayout.setupWithViewPager(binding.dashboardRiskLayout.viewpager, true);
    }

    private void initToolbar() {
        /*Toolbar toolBar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("Calculator");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/


        /*Toolbar toolBar = this.findViewById(R.id.toolbar_my);
        ImageView imageViewBack = toolBar.findViewById(R.id.img_back);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");*/
        binding.headerText.setText(mData.getReport().getLegalName());
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loan_layout_root:
                initMyAccount(0);
                break;
          /*  case R.id.btn_calculator:
                startActivity(new Intent(getActivity(), InterestRateCalActivity.class));
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
//                navOwnershipMix();
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
                initScoreInsightRemark(getResources().getString(R.string.equifax_negative));
                break;
            case R.id.btn_loan_count:
                initScoreInsightRemark(getResources().getString(R.string.equifax_loan_count));
                break;
            case R.id.btn_credit_age:
                initScoreInsightRemark(getResources().getString(R.string.equifax_credit_age));
                break;
            case R.id.btn_repayment:
                initScoreInsightRemark(getResources().getString(R.string.equifax_repayment));
                break;
            case R.id.btn_new_loan:
                initScoreInsightRemark(getResources().getString(R.string.equifax_new_loan));
                break;
            case R.id.btn_secured:
                initScoreInsightRemark(getResources().getString(R.string.equifax_business_vintage));
                break;
            case R.id.btn_score_insights_info:
                initScoreInsightsSheet();
                break;
        }
    }

    private void initScoreInsightRemark(String value) {
        initBottomSheet(value);
    }

    public void initBottomSheet(String text) {
        Bundle bundle = new Bundle();
        bundle.putString("data", text);
        EquiFaxScoreInsightRemarkModal fragment = new EquiFaxScoreInsightRemarkModal();
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(), "");
    }

    public void initScoreInsightsSheet() {
        Bundle bundle = new Bundle();
        bundle.putString("data", "");
        EquiFaxScoreInsightInfoModal fragment = new EquiFaxScoreInsightInfoModal();
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(), "");
    }

    private void initMyAccount(int index) {
        Gson gson = new Gson();
        Intent intent = new Intent(getActivity(), EquiFaxMyAccount.class);
        intent.putExtra("data", gson.toJson(mData.getReport().getTradelines()));
        intent.putExtra("index", index);
        startActivity(intent);
    }

    private void initData() {
        if (mReportHelper.getCommercialReport() != null) {
            mData = mReportHelper.getCommercialReport();
            if ((mData.getMetadata().getCommercial_progress_step() + "").equalsIgnoreCase("3.1")) {
                binding.nullProfileLayout.setVisibility(View.VISIBLE);
                binding.fillProfileLayout.setVisibility(View.GONE);
            } else if ((mData.getMetadata().getCommercial_progress_step() + "").equalsIgnoreCase("2.2")) {
                binding.nullProfileLayout.setVisibility(View.VISIBLE);
                binding.fillProfileLayout.setVisibility(View.GONE);
            } else if ((mData.getMetadata().getCommercial_progress_step() + "").equalsIgnoreCase("3.0")) {
                binding.nullProfileLayout.setVisibility(View.GONE);
                binding.fillProfileLayout.setVisibility(View.VISIBLE);
                initResponseData();
            } else {
                binding.nullProfileLayout.setVisibility(View.VISIBLE);
                binding.fillProfileLayout.setVisibility(View.GONE);
            }
            if (binding.nullProfileLayout.getVisibility() == View.VISIBLE) {
                binding.dashboardRiskLayoutNull.imgCreditBureau.setImageDrawable(getResources().getDrawable(R.mipmap.ic_equifax_logo));
            }
        }
    }

    private void initResponseData() {
        intiRiskSlider();
        initScoreInsights();
        /*try {
            String[] legalNameArr = mData.getReport().getLegalName().split(" ");
            String legalName = StringHelper.toCamelCase(legalNameArr[0] + " " + legalNameArr[1]);
            binding.txtUserName.setText(legalName);
        } catch (Exception ex) {
            binding.txtUserName.setText(StringHelper.toCamelCase(
                    mData.getReport().getLegalName()));
        }*/
        try {
            initLoanData();
            initRepayments();
            initOverdue();
            initNegative();
            initFacilityMix();
            initOwnershipMix();
            initNpa();
        } catch (Exception ex) {
        }
    }


    private void initScoreInsights() {

        // Negative tile data binding & Calculation
        binding.scoreInsights.negativeMessage.setText(
                new EquifaxScoreInsightsHelper().getNegativeMessage(getContext(),
                        mData.getReport().getNegativeAccountsCount(),
                        mData.getReport().getDefaultsAndNpaCount(),
                        (int) mData.getReport().getAggregatedOverdueAmount()));


        // repayment tile data binding & calculation
        binding.scoreInsights.repaymentMessage.setText(
                new EquifaxScoreInsightsHelper().setRepaymentStatus(getActivity(), (mData.getReport().getRepaymentsTotalCount() -
                                mData.getReport().getRepaymentsMissedCount()),
                        mData.getReport().getRepaymentsTotalCount()));

        //credit card utilization data binding & calculation

        new EquifaxScoreInsightsHelper().setCreditCardStatus(getActivity(), mData.getReport().getTradelines());


        //Credit Age data binding & calculation
        binding.scoreInsights.creditAgeMessage.setText(
                new EquifaxScoreInsightsHelper().setCreditAgeStatus(getActivity(), mData.getReport().getCreditAge()));


        //New Loan & New Inquiries in last 12 months data binding & calculation

        if (mData.getReport().getTradelines() != null) {
            binding.scoreInsights.loanMessage.setText(new EquifaxScoreInsightsHelper().setLoanStatus(getActivity(),
                    mData.getReport().getTradelines(),
                    mData.getReport().getEnqPast12months()));
        }

        //secured & un-secured in last 12 months data binding & calculation

        /*new ScoreInsightsHelper().setFacilityMixStatus(
                mData.getFacility_mix().getSecuredLoan(),
                mData.getFacility_mix().getUnsecuredLoan(),
                binding.scoreInsights.imgSecuredLoan, binding.scoreInsights.txtSecuredLoanStatus,
                binding.scoreInsights.securedLoanMessage, getActivity());*/


        //Business vintage & Industry Type message
        try {
            binding.scoreInsights.businessVintageMessage.setText(
                    new EquifaxScoreInsightsHelper().setBusinessVintage(getActivity(), mData.getReport().getIncorporation_date(),
                            mData.getReport().getNature_of_business().get(0)));
        } catch (Exception ex) {

        }

        binding.scoreInsights.loanCountMessage.setText(
                new EquifaxScoreInsightsHelper().setLoanType(getActivity(),
                        mData.getReport().getLenderMix().getPrivateBank().getCount(),
                        mData.getReport().getLenderMix().getPSUBank().getCount(),
                        mData.getReport().getLenderMix().getNBFC().getCount()));


        binding.scoreInsights.btnNegative.setOnClickListener(this);
        binding.scoreInsights.btnCreditAge.setOnClickListener(this);
        binding.scoreInsights.btnCreditCard.setOnClickListener(this);
        binding.scoreInsights.btnNewLoan.setOnClickListener(this);
        binding.scoreInsights.btnRepayment.setOnClickListener(this);
        binding.scoreInsights.btnSecured.setOnClickListener(this);
        binding.btnScoreInsightsInfo.setOnClickListener(this);
    }

    private void initLoanData() {
        binding.loanLayout.loanActive.setText(mData.getReport().getLoansActiveCount() + "");
        binding.loanLayout.loanClose.setText(mData.getReport().getLoansClosedCount() + "");
        binding.loanLayout.loanChart.setDrawHoleEnabled(true);
        binding.loanLayout.loanChart.setUsePercentValues(false);
        binding.loanLayout.loanChart.setEntryLabelTextSize(12);
        binding.loanLayout.loanChart.setEntryLabelColor(Color.BLACK);
        binding.loanLayout.loanChart.setCenterText(mData.getReport().getLoansTotalCount() + "");
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
        new DummyChartData().loadPieChartData(binding.loanLayout.loanChart, mData.getReport().getLoansActiveCount(),
                mData.getReport().getLoansClosedCount());
    }

    private void initRepayments() {
        binding.repaymentsOverdueLayout.txtRepaymentOnTime.setText(
                mData.getReport().getRepaymentsTotalCount() - mData.getReport().getRepaymentsMissedCount()
                        + "/" + mData.getReport().getRepaymentsTotalCount() + " On time");
        binding.repaymentsOverdueLayout.repaymentSeekBar.setMax(mData.getReport().getRepaymentsTotalCount());
        binding.repaymentsOverdueLayout.repaymentSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        binding.repaymentsOverdueLayout.repaymentSeekBar.setProgress(mData.getReport().getRepaymentsTotalCount() - mData.getReport().getRepaymentsMissedCount());
        if (mData.getReport().getRepaymentsTotalCount() - mData.getReport().getRepaymentsMissedCount() == (mData.getReport().getRepaymentsTotalCount())) {
            binding.repaymentsOverdueLayout.repaymentAward.setVisibility(View.VISIBLE);
        } else {
            binding.repaymentsOverdueLayout.repaymentAward.setVisibility(View.GONE);
        }
    }

    private void initOverdue() {
        if ((int) mData.getReport().getAggregatedOverdueAmount() == 0) {
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
                    mData.getReport().getOverdueCount() + " - Accounts"
            );
            binding.repaymentsOverdueLayout.txtOverdueAmount.setText(
                    getResources().getString(R.string.rs) + convertInLac(mData.getReport().getAggregatedOverdueAmount())
            );
            binding.repaymentsOverdueLayout.overdueValue.setVisibility(View.VISIBLE);
            binding.repaymentsOverdueLayout.overdueDesign.setVisibility(View.GONE);
            binding.repaymentsOverdueLayout.overdueAward.setVisibility(View.GONE);
        }
    }

    private void initNegative() {
        if (mData.getReport().getNegativeAccountsCount() == 0) {
            binding.negativeNpaLayout.negativeProgressBar.setBackground(
                    getResources().getDrawable(R.drawable.negative_account_green_style));
            binding.negativeNpaLayout.txtNegativeAccount.setVisibility(View.GONE);
            binding.negativeNpaLayout.txtNegativeAmount.setText("No Negative Account");
            binding.negativeNpaLayout.txtNegativeAmount.setTextColor(getResources().getColor(R.color.light_green));
            binding.negativeNpaLayout.txtNegativeAmount.setTextSize(12f);
            binding.negativeNpaLayout.negativeAward.setVisibility(View.VISIBLE);
        } else {
            binding.negativeNpaLayout.txtNegativeAccount.setText(
                    mData.getReport().getNegativeAccountsCount() + " - Accounts"
            );
            binding.negativeNpaLayout.txtNegativeAmount.setText(
                    getResources().getString(R.string.rs) + convertInLac(Double.parseDouble(mData.getReport().getNegativeAccountsAmount()))
            );
            binding.negativeNpaLayout.negativeAward.setVisibility(View.GONE);
        }

    }

    private void initNpa() {
        if (mData.getReport().getDefaultsAndNpaCount() == 0) {
            binding.negativeNpaLayout.npaProgressBar.setBackground(
                    getResources().getDrawable(R.drawable.negative_account_green_style));
            binding.negativeNpaLayout.txtNpaAccount.setVisibility(View.GONE);
            binding.negativeNpaLayout.txtNpaAmount.setText("No Default & NPA Account");
            binding.negativeNpaLayout.txtNpaAmount.setTextColor(getResources().getColor(R.color.light_green));
            binding.negativeNpaLayout.txtNpaAmount.setTextSize(12f);
            binding.negativeNpaLayout.npaAward.setVisibility(View.VISIBLE);
        } else {
            binding.negativeNpaLayout.txtNpaAccount.setText(
                    mData.getReport().getDefaultsAndNpaCount() + " - Accounts"
            );
            binding.negativeNpaLayout.txtNpaAmount.setText(
                    getResources().getString(R.string.rs) + convertInLac(Double.parseDouble(mData.getReport().getAggregatedDefaultsAndNpaAmount()))
            );
            binding.negativeNpaLayout.npaAward.setVisibility(View.GONE);
        }

    }

    private void initOwnershipMix() {
        String nbfc = getResources().getString(R.string.equifax_nbfc)
                + " (" + getResources().getString(R.string.rs)
                + AmountHelper.getCommaSeptdAmount((double) mData.getReport().getLenderMix().getNBFC().getCurrentBalance()) + ")";
        binding.lenderMixLayout.txtNbfc.setText(nbfc);
        String private_bank = getResources().getString(R.string.equifax_private_bank)
                + " (" + getResources().getString(R.string.rs)
                + AmountHelper.getCommaSeptdAmount((double) mData.getReport().getLenderMix().getPrivateBank().getCurrentBalance()) + ")";
        binding.lenderMixLayout.txtPrivateBank.setText(private_bank);
        String psu_bank = getResources().getString(R.string.equifax_psu_bank)
                + " (" + getResources().getString(R.string.rs)
                + AmountHelper.getCommaSeptdAmount((double) mData.getReport().getLenderMix().getPSUBank().getCurrentBalance()) + ")";
        binding.lenderMixLayout.txtPsuBank.setText(psu_bank);
        String Others = getResources().getString(R.string.equifax_others)
                + " (" + getResources().getString(R.string.rs)
                + AmountHelper.getCommaSeptdAmount((double) mData.getReport().getLenderMix().getOthers().getCurrentBalance()) + ")";
        binding.lenderMixLayout.txtOthers.setText(Others);

        int total = mData.getReport().getLenderMix().getNBFC().getCurrentBalance()
                + mData.getReport().getLenderMix().getPSUBank().getCurrentBalance()
                + mData.getReport().getLenderMix().getOthers().getCurrentBalance()
                + mData.getReport().getLenderMix().getPrivateBank().getCurrentBalance();
        binding.lenderMixLayout.ownershipPieChart.setDrawHoleEnabled(true);
        binding.lenderMixLayout.ownershipPieChart.setHoleRadius(70);
        binding.lenderMixLayout.ownershipPieChart.setTouchEnabled(false);
        binding.lenderMixLayout.ownershipPieChart.setUsePercentValues(false);
        binding.lenderMixLayout.ownershipPieChart.setEntryLabelTextSize(12);
        binding.lenderMixLayout.ownershipPieChart.setEntryLabelColor(Color.BLACK);
        binding.lenderMixLayout.ownershipPieChart.setCenterText(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount((double) total) + "");
        binding.lenderMixLayout.ownershipPieChart.setCenterTextSize(8);
        binding.lenderMixLayout.ownershipPieChart.setCenterTextColor(Color.BLACK);
        binding.lenderMixLayout.ownershipPieChart.setDrawHoleEnabled(true);
        binding.lenderMixLayout.ownershipPieChart.setHoleColor(Color.TRANSPARENT);
        binding.lenderMixLayout.ownershipPieChart.setHoleRadius(60);
        Typeface typeface = ResourcesCompat.getFont(getActivity(), R.font.roboto_regular);
        binding.lenderMixLayout.ownershipPieChart.setCenterTextTypeface(typeface);
        binding.lenderMixLayout.ownershipPieChart.getDescription().setEnabled(false);

        Legend l = binding.lenderMixLayout.ownershipPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
        new DummyChartData().equiFaxLenderMax(binding.lenderMixLayout.ownershipPieChart,
                mData.getReport().getLenderMix().getNBFC().getCurrentBalance(),
                mData.getReport().getLenderMix().getPSUBank().getCurrentBalance(),
                mData.getReport().getLenderMix().getPrivateBank().getCurrentBalance(),
                mData.getReport().getLenderMix().getOthers().getCurrentBalance());
    }

    private void initFacilityMix() {
        binding.facilityMixLayout.txtForex.setText(
                getResources().getString(R.string.forex) + " - " + mData.getReport().getFacilityMix().getForex());
        binding.facilityMixLayout.txtNonFundBased.setText(
                getResources().getString(R.string.non_fund_based) + " - " + mData.getReport().getFacilityMix().getNonFundBased());
        binding.facilityMixLayout.txtOthers.setText(
                getResources().getString(R.string.others) + " - " + mData.getReport().getFacilityMix().getOthers());
        binding.facilityMixLayout.txtTermLoans.setText(
                getResources().getString(R.string.term_loans) + " - " + mData.getReport().getFacilityMix().getTermLoans());
        binding.facilityMixLayout.txtWorkingCapital.setText(
                getResources().getString(R.string.working_capital) + " - " + mData.getReport().getFacilityMix().getWorkingCapital());

        int total_mix = mData.getReport().getFacilityMix().getForex()
                + mData.getReport().getFacilityMix().getNonFundBased()
                + mData.getReport().getFacilityMix().getWorkingCapital()
                + mData.getReport().getFacilityMix().getOthers()
                + mData.getReport().getFacilityMix().getTermLoans();
        Logger.errorLogger("total", total_mix + "");
        if (mData.getReport().getFacilityMix().getForex() == 0) {
            binding.facilityMixLayout.facilityMixForexPro.setVisibility(View.GONE);
            binding.facilityMixLayout.forexLayout.setVisibility(View.GONE);
        } else {
            Logger.errorLogger("forex", (mData.getReport().getFacilityMix().getForex() * 100) / total_mix + "");
            LinearLayout.LayoutParams workingCapitalProLayoutParams = (LinearLayout.LayoutParams)
                    binding.facilityMixLayout.facilityMixForexPro.getLayoutParams();
            workingCapitalProLayoutParams.weight = (float) ((mData.getReport().getFacilityMix().getForex() * 100) / total_mix);
            binding.facilityMixLayout.facilityMixForexPro.setLayoutParams(workingCapitalProLayoutParams);
        }


        if (mData.getReport().getFacilityMix().getOthers() == 0) {
            binding.facilityMixLayout.facilityMixOthersPro.setVisibility(View.GONE);
            binding.facilityMixLayout.othersLayout.setVisibility(View.GONE);
        } else {
            double other = Double.valueOf((mData.getReport().getFacilityMix().getOthers() * 100)) / total_mix;
            LinearLayout.LayoutParams personalLoanProLayoutParams = (LinearLayout.LayoutParams)
                    binding.facilityMixLayout.facilityMixOthersPro.getLayoutParams();
            personalLoanProLayoutParams.weight = (float) other;
            binding.facilityMixLayout.facilityMixOthersPro.setLayoutParams(personalLoanProLayoutParams);
        }

        if (mData.getReport().getFacilityMix().getTermLoans() == 0) {
            binding.facilityMixLayout.facilityMixTermsLoanPro.setVisibility(View.GONE);
            binding.facilityMixLayout.termLoansLayout.setVisibility(View.GONE);
        } else {
            double terms = Double.valueOf((mData.getReport().getFacilityMix().getTermLoans() * 100)) / total_mix;
            LinearLayout.LayoutParams personalLoanProLayoutParams = (LinearLayout.LayoutParams)
                    binding.facilityMixLayout.facilityMixTermsLoanPro.getLayoutParams();
            personalLoanProLayoutParams.weight = (float) terms;
            binding.facilityMixLayout.facilityMixTermsLoanPro.setLayoutParams(personalLoanProLayoutParams);
        }


        if (mData.getReport().getFacilityMix().getNonFundBased() == 0) {
            binding.facilityMixLayout.facilityMixNonFundBasedPro.setVisibility(View.GONE);
            binding.facilityMixLayout.nonFundBasedLayout.setVisibility(View.GONE);
        } else {
            double non_fund_based = Double.valueOf((mData.getReport().getFacilityMix().getNonFundBased() * 100)) / total_mix;
            LinearLayout.LayoutParams personalLoanProLayoutParams = (LinearLayout.LayoutParams)
                    binding.facilityMixLayout.facilityMixNonFundBasedPro.getLayoutParams();
            personalLoanProLayoutParams.weight = (float) non_fund_based;
            binding.facilityMixLayout.facilityMixNonFundBasedPro.setLayoutParams(personalLoanProLayoutParams);
        }


        if (mData.getReport().getFacilityMix().getWorkingCapital() == 0) {
            binding.facilityMixLayout.facilityMixWorkingCapitalPro.setVisibility(View.GONE);
            binding.facilityMixLayout.workingCapitalLayout.setVisibility(View.GONE);
        } else {
            double working_capital = Double.valueOf((mData.getReport().getFacilityMix().getWorkingCapital() * 100)) / total_mix;
            LinearLayout.LayoutParams personalLoanProLayoutParams = (LinearLayout.LayoutParams)
                    binding.facilityMixLayout.facilityMixWorkingCapitalPro.getLayoutParams();
            personalLoanProLayoutParams.weight = (float) working_capital;
            binding.facilityMixLayout.facilityMixWorkingCapitalPro.setLayoutParams(personalLoanProLayoutParams);
        }
    }

/*    private void getHomeData() {
        Logger.errorLogger("Profile", SharedPref.getInstance().getString(TOKEN));
        Call<String> call1 = mApiInterface.getHomePageData("Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        Logger.errorLogger("RESPONSE", response.toString());
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                        Gson gson = new Gson();
                        homeData = gson.fromJson(jsonObj.get("data"), Data.class);
                        MicroblogListAdapter adapter = new MicroblogListAdapter(homeData.getMicroblogs(), getContext());
                        binding.recyclerViewMicroblog.setAdapter(adapter);
                        ComplianceCalendarListAdapter complianceCalendarListAdapter = new ComplianceCalendarListAdapter(homeData.getComplianceCalender(), getContext());
                        binding.recyclerViewComplaince.setAdapter(complianceCalendarListAdapter);
                        EMIListAdapter emiListAdapter = new EMIListAdapter(homeData.getExperianEmi(), getContext());
                        binding.recyclerViewEmi.setAdapter(emiListAdapter);
                    }
                } else {
                    if (response.code() == 403) {

                    }
                }
                try {
                    Logger.errorLogger(this.getClass().getName(), response.errorBody().string() + "");
                } catch (Exception ex) {
                    Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
                }
                Logger.errorLogger(this.getClass().getName(), response.code() + "");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }


    private void getHomeData() {
        Logger.errorLogger("Profile", SharedPref.getInstance().getString(TOKEN));
        Call<HomeDataInfo> call1 = mApiInterface.getHomePageData1("Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<HomeDataInfo>() {
            @Override
            public void onResponse(Call<HomeDataInfo> call, Response<HomeDataInfo> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        HomeDataInfo resource = response.body();
                        MicroblogListAdapter adapter = new MicroblogListAdapter(resource.getData().getMicroblogs(), getContext());
                        binding.recyclerViewMicroblog.setAdapter(adapter);
                        ComplianceCalendarListAdapter complianceCalendarListAdapter = new ComplianceCalendarListAdapter(resource.getData().getComplianceCalender(), getContext());
                        binding.recyclerViewComplaince.setAdapter(complianceCalendarListAdapter);
                        EMIListAdapter emiListAdapter = new EMIListAdapter(resource.getData().getExperianEmi(), getContext());
                        binding.recyclerViewEmi.setAdapter(emiListAdapter);
                    }
                } else {
                    if (response.code() == 403) {

                    }
                }
                try {
                    Logger.errorLogger(this.getClass().getName(), response.errorBody().string() + "");
                } catch (Exception ex) {
                    Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
                }
                Logger.errorLogger(this.getClass().getName(), response.code() + "");
            }

            @Override
            public void onFailure(Call<HomeDataInfo> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }*/

}