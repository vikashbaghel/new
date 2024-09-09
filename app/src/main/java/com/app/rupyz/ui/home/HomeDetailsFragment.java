package com.app.rupyz.ui.home;

import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.app.rupyz.R;
import com.app.rupyz.databinding.FragmentHomeDetailsBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.helper.ScoreInsightsHelper;
import com.app.rupyz.generic.helper.StringHelper;
import com.app.rupyz.generic.logger.FirebaseLogger;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.createemi.experian.Datum;
import com.app.rupyz.generic.model.createemi.experian.ExperianEMIResponse;
import com.app.rupyz.generic.model.individual.experian.ExperianInfoModel;
import com.app.rupyz.generic.model.individual.experian.Tradeline;
import com.app.rupyz.generic.model.user.UserViewModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.utils.DummyChartData;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.account.MyAccount;
import com.app.rupyz.ui.account.OwnershipMixActivity;
import com.app.rupyz.ui.common.CustomProgressDialog;
import com.app.rupyz.ui.home.dailog.score_insights.ScoreInsightInfoModal;
import com.app.rupyz.ui.home.dailog.score_insights.ScoreInsightRemarkModal;
import com.app.rupyz.ui.home.home_slide.ScorePagerAdapter;
import com.app.rupyz.ui.user.ProfileActivity;
import com.github.mikephil.charting.components.Legend;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeDetailsFragment extends Fragment implements View.OnClickListener {
    private ApiInterface mApiInterface;
    private FragmentHomeDetailsBinding binding;
    private Utility mUtil;
    public static ExperianInfoModel mData;
    private UserViewModel mUserData;
    public static List<Tradeline> mCustomData;
    public static List<Datum> mEMIData;
    private EquiFaxReportHelper mReportHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mUtil = new Utility(getActivity());
        mEMIData = new ArrayList<>();
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        mCustomData = new ArrayList<>();
        mReportHelper = EquiFaxReportHelper.getInstance();
        new FirebaseLogger(getContext()).sendLog("Home Fragment", "Home Fragment");
        binding = FragmentHomeDetailsBinding.inflate(getLayoutInflater());

        binding.loanLayout.loanLayoutRoot.setOnClickListener(this);
        binding.repaymentsOverdueLayout.overdueRootLayout.setOnClickListener(this);
        binding.repaymentsOverdueLayout.repaymentRootLayout.setOnClickListener(this);
        binding.negativeNpaLayout.negativeRootLayout.setOnClickListener(this);
        binding.negativeNpaLayout.npaRootLayout.setOnClickListener(this);
        binding.mainContent.setVisibility(View.GONE);
        binding.loanLayout.btnMore.setOnClickListener(this);
        binding.loanLayout.btnMoreDetails.setOnClickListener(this);
        binding.dashboardRiskLayoutNull.btnConcern.setOnClickListener(this);
        binding.ownershipMixLayout.ownershipMixRootLayout.setOnClickListener(this);
        getProfileData();
        return binding.getRoot();
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
                navOwnershipMix();
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
            case R.id.dashboard_risk_layout:
                new SessionHelper(getActivity()).messageToast("A");
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

    public void initBottomSheet(String text, String type) {
        Bundle bundle = new Bundle();
        bundle.putString("data", text);
        bundle.putString("type", type);
        ScoreInsightRemarkModal fragment = new ScoreInsightRemarkModal();
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(), "");
    }

    public void initScoreInsightsSheet() {
        Bundle bundle = new Bundle();
        bundle.putString("data", "");
        ScoreInsightInfoModal fragment = new ScoreInsightInfoModal();
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(), "");
    }

    private void initMyAccount(int index) {
        Gson gson = new Gson();
        Intent intent = new Intent(getActivity(), MyAccount.class);
        intent.putExtra("data", gson.toJson(mData.getTradelines()));
        intent.putExtra("mEMIData", gson.toJson(mEMIData));
        intent.putExtra("index", index);
        startActivity(intent);
    }

    private void navOwnershipMix() {
        Gson gson = new Gson();
        Intent intent = new Intent(getActivity(), OwnershipMixActivity.class);
        intent.putExtra("data", gson.toJson(mData.getTradelines()));
        startActivity(intent);
    }


    private void getProfileData() {
        binding.mainContent.setVisibility(View.VISIBLE);
        mData = mReportHelper.getExperianReport();
        emiDetails(mData);
        if (mData.getTradelines() != null && mData.getTradelines().size() > 0) {
            binding.nullProfileLayout.setVisibility(View.GONE);
            binding.fillProfileLayout.setVisibility(View.VISIBLE);
            intiRiskSlider();
            initScoreInsights();
            initToolbar(StringHelper.toCamelCase(
                    mData.getRelationship_details()
                            .getCurrent_Applicant_Details().getFirst_Name() + " " + mData.getRelationship_details()
                            .getCurrent_Applicant_Details().getLast_Name().toLowerCase()));
            try {
                initLoanData();
                initRepayments();
                initOverdue();
                initNegative();
                initFacilityMix();
                initOwnershipMix();
                initNPA();
            } catch (Exception ex) {
            }
        } else {
            binding.nullProfileLayout.setVisibility(View.VISIBLE);
            binding.fillProfileLayout.setVisibility(View.GONE);
            getProfileDataUser();
        }
    }

    private void initLoanData() {
        binding.loanLayout.loanActive.setText(mData.getLoans_active_count() + "");
        binding.loanLayout.loanClose.setText(mData.getLoans_closed_count() + "");
        binding.loanLayout.loanChart.setDrawHoleEnabled(true);
        binding.loanLayout.loanChart.setUsePercentValues(false);
        binding.loanLayout.loanChart.setEntryLabelTextSize(12);
        binding.loanLayout.loanChart.setEntryLabelColor(Color.BLACK);
        binding.loanLayout.loanChart.setCenterText(mData.getLoans_total_count() + "");
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
        new DummyChartData().loadPieChartData(binding.loanLayout.loanChart, mData.getLoans_active_count(),
                mData.getLoans_closed_count());
    }

    private void initRepayments() {
        binding.repaymentsOverdueLayout.txtRepaymentOnTime.setText(mData.getRepayments_ontime_count()
                + "/" + (mData.getRepayments_ontime_count() + mData.getRepayments_missed_count()) + " On time");
        binding.repaymentsOverdueLayout.repaymentSeekBar.setMax(mData.getRepayments_ontime_count() + mData.getRepayments_missed_count());
        binding.repaymentsOverdueLayout.repaymentSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        binding.repaymentsOverdueLayout.repaymentSeekBar.setProgress(mData.getRepayments_ontime_count());
        if (mData.getRepayments_ontime_count() == (mData.getRepayments_ontime_count() + mData.getRepayments_missed_count())) {
            binding.repaymentsOverdueLayout.repaymentAward.setVisibility(View.VISIBLE);
        } else {
            binding.repaymentsOverdueLayout.repaymentAward.setVisibility(View.GONE);
        }
    }

    private void initOverdue() {
        if (mData.getOverdue_amount() == 0) {
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
                    mData.getOverdue_count() + " - Accounts"
            );
            binding.repaymentsOverdueLayout.txtOverdueAmount.setText(
                    getResources().getString(R.string.rs) + mData.getOverdue_amount()
            );
            binding.repaymentsOverdueLayout.overdueValue.setVisibility(View.VISIBLE);
            binding.repaymentsOverdueLayout.overdueDesign.setVisibility(View.GONE);
            binding.repaymentsOverdueLayout.overdueAward.setVisibility(View.GONE);
        }
    }

    private void initNegative() {
        if (mData.getSuit_case_count() == 0) {
            binding.negativeNpaLayout.negativeProgressBar.setBackground(
                    getResources().getDrawable(R.drawable.negative_account_green_style));
            binding.negativeNpaLayout.txtNegativeAccount.setVisibility(View.GONE);
            binding.negativeNpaLayout.txtNegativeAmount.setText("No Negative Account");
            binding.negativeNpaLayout.txtNegativeAmount.setTextColor(getResources().getColor(R.color.light_green));
            binding.negativeNpaLayout.txtNegativeAmount.setTextSize(12f);
            binding.negativeNpaLayout.negativeAward.setVisibility(View.VISIBLE);
        } else {
            binding.negativeNpaLayout.txtNegativeAccount.setText(
                    mData.getSuit_case_count() + " - Accounts"
            );
            binding.negativeNpaLayout.txtNegativeAmount.setText(
                    getResources().getString(R.string.rs) + mData.getSuit_case_amount()
            );
            binding.negativeNpaLayout.negativeAward.setVisibility(View.GONE);
        }

    }

    private void initOwnershipMix() {
        binding.ownershipMixLayout.txtIndividual.setText(
                getResources().getString(R.string.ownership_mix_individual) + " (" + getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount((double) mData.getOwnership().getIndividual()) + ")"
        );
        binding.ownershipMixLayout.txtJoint.setText(
                getResources().getString(R.string.ownership_mix_joint_guarantor) + " (" + getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount((double) mData.getOwnership().getJointGuarantor()) + ")"
        );
        binding.ownershipMixLayout.ownershipPieChart.setDrawHoleEnabled(true);
        binding.ownershipMixLayout.ownershipPieChart.setHoleRadius(70);

        binding.ownershipMixLayout.ownershipPieChart.setCenterTextColor(Color.BLACK);
        binding.ownershipMixLayout.ownershipPieChart.setDrawHoleEnabled(true);
        binding.ownershipMixLayout.ownershipPieChart.setHoleColor(Color.TRANSPARENT);
        binding.ownershipMixLayout.ownershipPieChart.setHoleRadius(60);
        binding.ownershipMixLayout.ownershipPieChart.setTouchEnabled(false);
        binding.ownershipMixLayout.ownershipPieChart.setUsePercentValues(false);
        binding.ownershipMixLayout.ownershipPieChart.setEntryLabelTextSize(12);
        binding.ownershipMixLayout.ownershipPieChart.setEntryLabelColor(Color.BLACK);
        binding.ownershipMixLayout.ownershipPieChart.setCenterText(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount((double) (mData.getOwnership().getIndividual() + mData.getOwnership().getJointGuarantor())) + "");
        binding.ownershipMixLayout.ownershipPieChart.setCenterTextSize(8);
        Typeface typeface = ResourcesCompat.getFont(getActivity(), R.font.roboto_regular);
        binding.ownershipMixLayout.ownershipPieChart.setCenterTextTypeface(typeface);
        binding.ownershipMixLayout.ownershipPieChart.getDescription().setEnabled(false);

        Legend l = binding.ownershipMixLayout.ownershipPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
        new DummyChartData().loadOwnershipMax(binding.ownershipMixLayout.ownershipPieChart, mData.getOwnership().getIndividual(),
                mData.getOwnership().getJointGuarantor());
    }

    private void initFacilityMix() {

        binding.facilityMixLayout.txtSecuredLoan.setText(
                getResources().getString(R.string.secured_loan) + " - " + mData.getFacility_mix().getSecuredLoan());
        binding.facilityMixLayout.txtUnSecuredLoan.setText(
                getResources().getString(R.string.unsecured_loan) + " - " + mData.getFacility_mix().getUnsecuredLoan());

        int total_mix = mData.getFacility_mix().getSecuredLoan() + mData.getFacility_mix().getUnsecuredLoan();

        if (mData.getFacility_mix().getSecuredLoan() == 0) {
            binding.facilityMixLayout.facilityMixSecuredPro.setVisibility(View.GONE);
            binding.facilityMixLayout.securedLayout.setVisibility(View.GONE);
        } else {
            LinearLayout.LayoutParams workingCapitalProLayoutParams = (LinearLayout.LayoutParams)
                    binding.facilityMixLayout.facilityMixSecuredPro.getLayoutParams();
            workingCapitalProLayoutParams.weight = (mData.getFacility_mix().getSecuredLoan() * 100) / total_mix;
            binding.facilityMixLayout.facilityMixSecuredPro.setLayoutParams(workingCapitalProLayoutParams);
        }


        if (mData.getFacility_mix().getUnsecuredLoan() == 0) {
            binding.facilityMixLayout.facilityMixUnsecuredPro.setVisibility(View.GONE);
            binding.facilityMixLayout.unsecuredLayout.setVisibility(View.GONE);
        } else {
            LinearLayout.LayoutParams personalLoanProLayoutParams = (LinearLayout.LayoutParams)
                    binding.facilityMixLayout.facilityMixUnsecuredPro.getLayoutParams();
            personalLoanProLayoutParams.weight = (mData.getFacility_mix().getUnsecuredLoan() * 100) / total_mix;
            binding.facilityMixLayout.facilityMixUnsecuredPro.setLayoutParams(personalLoanProLayoutParams);
        }
    }

    private void initNPA() {
        binding.negativeNpaLayout.txtNpaDpd.setText(
                mData.getDefaults_and_npa().getdPD() + " " + getResources().getString(R.string.npa_dps));
        binding.negativeNpaLayout.txtNpaSma.setText(
                mData.getDefaults_and_npa().getsMA() + " " + getResources().getString(R.string.npa_sma));
        binding.negativeNpaLayout.txtNpaLoss.setText(
                mData.getDefaults_and_npa().getLoss() + " " + getResources().getString(R.string.npa_loss));
        binding.negativeNpaLayout.txtNpaDoubtful.setText(
                mData.getDefaults_and_npa().getDoubtful() + " " + getResources().getString(R.string.npa_doubtful));
        binding.negativeNpaLayout.txtNpaSubStandard.setText(
                mData.getDefaults_and_npa().getSubStandard() + " " + getResources().getString(R.string.npa_sub_standard));


        int total_npa = mData.getDefaults_and_npa().getdPD() + mData.getDefaults_and_npa().getsMA()
                + mData.getDefaults_and_npa().getLoss() + mData.getDefaults_and_npa().getDoubtful()
                + mData.getDefaults_and_npa().getSubStandard();
        if (total_npa == 0) {
            binding.negativeNpaLayout.npaAward.setVisibility(View.VISIBLE);
            binding.negativeNpaLayout.npaDpsPro.setBackground(getResources().getDrawable(R.drawable.negative_account_green_style));
            LinearLayout.LayoutParams npaDpsPro = (LinearLayout.LayoutParams)
                    binding.negativeNpaLayout.npaDpsPro.getLayoutParams();
            npaDpsPro.weight = 100;
            binding.negativeNpaLayout.npaDpsPro.setLayoutParams(npaDpsPro);
            binding.negativeNpaLayout.npaDoubtfulPro.setVisibility(View.GONE);
            binding.negativeNpaLayout.npaLossPro.setVisibility(View.GONE);
            binding.negativeNpaLayout.npaSmaPro.setVisibility(View.GONE);
            binding.negativeNpaLayout.npaSubStandardPro.setVisibility(View.GONE);
            binding.negativeNpaLayout.npaDpdLayout.setVisibility(View.GONE);
            binding.negativeNpaLayout.npaSmaLayout.setVisibility(View.GONE);
            binding.negativeNpaLayout.npaLossLayout.setVisibility(View.GONE);
            binding.negativeNpaLayout.npaDoubtfulLayout.setVisibility(View.GONE);
            binding.negativeNpaLayout.npaSubStandardLayout.setVisibility(View.GONE);
            binding.negativeNpaLayout.txtNpaMessage.setVisibility(View.VISIBLE);
        } else {
            binding.negativeNpaLayout.txtNpaMessage.setVisibility(View.GONE);
            binding.negativeNpaLayout.npaAward.setVisibility(View.GONE);
            if (mData.getDefaults_and_npa().getdPD() == 0) {
                binding.negativeNpaLayout.npaDpdLayout.setVisibility(View.GONE);
                binding.negativeNpaLayout.npaDpsPro.setVisibility(View.GONE);
            } else {
                LinearLayout.LayoutParams npaDpsProLayoutParams = (LinearLayout.LayoutParams)
                        binding.negativeNpaLayout.npaDpsPro.getLayoutParams();
                npaDpsProLayoutParams.weight = (mData.getDefaults_and_npa().getdPD() * 100) / total_npa;
                binding.negativeNpaLayout.npaDpsPro.setLayoutParams(npaDpsProLayoutParams);
            }

            if (mData.getDefaults_and_npa().getsMA() == 0) {
                binding.negativeNpaLayout.npaSmaLayout.setVisibility(View.GONE);
                binding.negativeNpaLayout.npaSmaPro.setVisibility(View.GONE);
            } else {
                LinearLayout.LayoutParams npaSmaProLayoutParams = (LinearLayout.LayoutParams)
                        binding.negativeNpaLayout.npaSmaPro.getLayoutParams();
                npaSmaProLayoutParams.weight = (mData.getDefaults_and_npa().getsMA() * 100) / total_npa;
                binding.negativeNpaLayout.npaSmaPro.setLayoutParams(npaSmaProLayoutParams);
            }


            if (mData.getDefaults_and_npa().getLoss() == 0) {
                binding.negativeNpaLayout.npaLossLayout.setVisibility(View.GONE);
                binding.negativeNpaLayout.npaLossPro.setVisibility(View.GONE);
            } else {
                LinearLayout.LayoutParams npaLossProLayoutParams = (LinearLayout.LayoutParams)
                        binding.negativeNpaLayout.npaLossPro.getLayoutParams();
                npaLossProLayoutParams.weight = (mData.getDefaults_and_npa().getLoss() * 100) / total_npa;
                binding.negativeNpaLayout.npaLossPro.setLayoutParams(npaLossProLayoutParams);
            }

            if (mData.getDefaults_and_npa().getDoubtful() == 0) {
                binding.negativeNpaLayout.npaDoubtfulLayout.setVisibility(View.GONE);
                binding.negativeNpaLayout.npaDoubtfulPro.setVisibility(View.GONE);
            } else {
                LinearLayout.LayoutParams npaDoubtfulProLayoutParams = (LinearLayout.LayoutParams)
                        binding.negativeNpaLayout.npaDoubtfulPro.getLayoutParams();
                npaDoubtfulProLayoutParams.weight = (mData.getDefaults_and_npa().getDoubtful() * 100) / total_npa;
                binding.negativeNpaLayout.npaDoubtfulPro.setLayoutParams(npaDoubtfulProLayoutParams);
            }

            if (mData.getDefaults_and_npa().getSubStandard() == 0) {
                binding.negativeNpaLayout.npaSubStandardLayout.setVisibility(View.GONE);
                binding.negativeNpaLayout.npaSubStandardLayout.setVisibility(View.GONE);
            } else {
                LinearLayout.LayoutParams npaSubStandardProLayoutParams = (LinearLayout.LayoutParams)
                        binding.negativeNpaLayout.npaSubStandardPro.getLayoutParams();
                npaSubStandardProLayoutParams.weight = (mData.getDefaults_and_npa().getSubStandard() * 100) / total_npa;
                binding.negativeNpaLayout.npaSubStandardPro.setLayoutParams(npaSubStandardProLayoutParams);
            }
        }
    }

    private void getProfileDataUser() {
        Logger.errorLogger("Profile", SharedPref.getInstance().getString(TOKEN));
        Call<String> call1 = mApiInterface.getReviewData("Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body());
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    Gson gson = new Gson();
                    mUserData = gson.fromJson(jsonObj.get("data"), UserViewModel.class);
                    binding.txtUserName.setText(mUserData.getFirst_name().substring(0, 1).toUpperCase()
                            + mUserData.getFirst_name().substring(1).toLowerCase()
                            + " " + mUserData.getLast_name());
                    // binding.dashboardUserProfileLayout.userPrefix.setText(mUserData.getFirst_name().substring(0, 1));
                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        new SessionHelper(getActivity()).requestErrorMessage(
                                responseBody,
                                binding.getRoot().findViewById(android.R.id.content));
                    } catch (Exception ex) {
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

    private void initScoreInsights() {

        // Negative tile data binding & Calculation
        int total_npa = mData.getDefaults_and_npa().getdPD() + mData.getDefaults_and_npa().getsMA()
                + mData.getDefaults_and_npa().getLoss() + mData.getDefaults_and_npa().getDoubtful()
                + mData.getDefaults_and_npa().getSubStandard();
        binding.scoreInsights.negativeMessage.setText(new ScoreInsightsHelper().getNegativeMessage(mData.getSuit_case_count(), total_npa, mData.getOverdue_amount()));
        new ScoreInsightsHelper().setNegativeStatus(mData.getSuit_case_count(), total_npa, mData.getOverdue_amount(),
                binding.scoreInsights.imgNegative, binding.scoreInsights.txtNegativeStatus, getActivity());


        // repayment tile data binding & calculation
        new ScoreInsightsHelper().setRepaymentStatus(mData.getRepayments_ontime_count(),
                mData.getRepayments_ontime_count() + mData.getRepayments_missed_count(),
                binding.scoreInsights.imgRepayment, binding.scoreInsights.txtRepaymentStatus,
                binding.scoreInsights.repaymentMessage, getActivity());

        //credit card utilization data binding & calculation

        new ScoreInsightsHelper().setCreditCardStatus(mData.getTradelines(),
                binding.scoreInsights.imgCreditCard, binding.scoreInsights.txtCreditCardStatus,
                binding.scoreInsights.creditCardMessage, getActivity());


        //Credit Age data binding & calculation

        new ScoreInsightsHelper().setCreditAgeStatus(mData.getCredit_age(),
                binding.scoreInsights.imgCreditAge, binding.scoreInsights.txtCreditAgeStatus,
                binding.scoreInsights.creditAgeMessage, getActivity());


        //New Loan & New Inquiries in last 12 months data binding & calculation

        if (mData.getTotal_caps_data() != null) {
            new ScoreInsightsHelper().setLoanStatus(mData.getTradelines(), mData.getTotal_caps_data().getTotalCAPSLast180Days(),
                    binding.scoreInsights.imgLoan, binding.scoreInsights.txtLoanStatus,
                    binding.scoreInsights.loanMessage, getActivity());
        }


        //secured & un-secured in last 12 months data binding & calculation

        new ScoreInsightsHelper().setFacilityMixStatus(
                mData.getFacility_mix().getSecuredLoan(),
                mData.getFacility_mix().getUnsecuredLoan(),
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


    private void initToolbar(String name) {
        binding.headerText.setText(name);
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }


    private void emiDetails(ExperianInfoModel tradeline) {
        Call<ExperianEMIResponse> call = mApiInterface.getEMIList(
                1, "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call.enqueue(new Callback<ExperianEMIResponse>() {
            @Override
            public void onResponse(Call<ExperianEMIResponse> call, Response<ExperianEMIResponse> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());
                    ExperianEMIResponse response1 = response.body();
                    mReportHelper.setExperianEMI(response.body());
                    if (response1.getData().size() > 0) {
                        for (Datum Item : response1.getData()) {
                            for (Tradeline tradelinesItem : tradeline.getTradelines()) {
                                if (Item.getAccountNumber().equals(tradelinesItem.getAccount_Number())) {
                                    tradelinesItem.setScheduled_Monthly_Payment_Amount(Item.getScheduledMonthlyPaymentAmount());
                                    tradelinesItem.setMonthDueDay(Item.getMonthDueDay());
                                    tradelinesItem.setRepayment_Tenure(Item.getRepaymentTenure());
                                    tradelinesItem.setRate_of_Interest(Item.getRateOfInterest());
                                    mEMIData.add(Item);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ExperianEMIResponse> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }
}