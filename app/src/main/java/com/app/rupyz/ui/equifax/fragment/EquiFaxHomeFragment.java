package com.app.rupyz.ui.equifax.fragment;

import static com.app.rupyz.generic.utils.SharePrefConstant.ACCOUNT_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.IS_EQUI_FAX;
import static com.app.rupyz.generic.utils.SharePrefConstant.IS_SKIP_GSTIN;
import static com.app.rupyz.generic.utils.SharePrefConstant.LEGAL_NAME;
import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;
import static com.app.rupyz.generic.utils.SharePrefConstant.USER_TYPE;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.R;
import com.app.rupyz.adapter.blogs.EquifaxEMIListAdapter;
import com.app.rupyz.adapter.blogs.MicroblogListAdapter;
import com.app.rupyz.adapter.complaince.ComplianceCalendarListAdapter;
import com.app.rupyz.databinding.EquifaxHomeFragmentBinding;
import com.app.rupyz.generic.helper.DateFormatHelper;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.helper.StringHelper;
import com.app.rupyz.generic.html.ScoreCardShareHelper;
import com.app.rupyz.generic.logger.FirebaseLogger;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.blog.HomeDataInfo;
import com.app.rupyz.generic.model.blog.Microblog;
import com.app.rupyz.generic.model.createemi.CreateEMIResponse;
import com.app.rupyz.generic.model.createemi.Datum;
import com.app.rupyz.generic.model.organization.EquiFaxInfoModel;
import com.app.rupyz.generic.model.organization.TradelinesItem;
import com.app.rupyz.generic.model.organization.individual.EquiFaxIndividualInfoModel;
import com.app.rupyz.generic.model.organization.individual.Tradeline;
import com.app.rupyz.generic.model.report_download.DownloadInfo;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.utils.AppConstant;
import com.app.rupyz.generic.utils.RecyclerTouchListener;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.blogs.MicroBlogsActivity;
import com.app.rupyz.ui.calculator.all_calculator.AllCalculatorActivity;
import com.app.rupyz.ui.equifax.EquiFaxIndividualMyAccount;
import com.app.rupyz.ui.equifax.EquiFaxMyAccount;
import com.app.rupyz.ui.equifax.activity.CommercialMaskedActivity;
import com.app.rupyz.ui.equifax.activity.EquifaxCommercialDetailsActivity;
import com.app.rupyz.ui.equifax.activity.EquifaxIndividualDetailsActivity;
import com.app.rupyz.ui.equifax.bottomsheet.EquifaxCommercialCreditScoreInfoModal;
import com.app.rupyz.ui.equifax.bottomsheet.EquifaxCreditScoreInfoModal;
import com.app.rupyz.ui.home.AlertFragment;
import com.app.rupyz.ui.home.DebtProfileFragment;
import com.app.rupyz.ui.home.dailog.score_insights.RefreshRemarkModal;
import com.app.rupyz.ui.organization.AuthAccountActivity;
import com.app.rupyz.ui.organization.EquiFaxOtpActivity;
import com.google.gson.Gson;
import com.izettle.html2bitmap.Html2Bitmap;
import com.izettle.html2bitmap.content.WebViewContent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquiFaxHomeFragment extends Fragment implements View.OnClickListener, EquifaxEMIListAdapter.EventListener {

    private EquiFaxApiInterface mEquiFaxApiInterface;
    private ApiInterface mApiInterface;
    private EquiFaxReportHelper mReportHelper;
    private EquifaxHomeFragmentBinding binding;
    private Utility mUtil;
    public static EquiFaxInfoModel mData;
    private List<Microblog> mMicroBlog = new ArrayList<>();
    private EquifaxEMIListAdapter emiListAdapter;
    private ComplianceCalendarListAdapter complianceCalendarListAdapter;
    public EquiFaxIndividualInfoModel mEquiFaxIndividualData;
    public boolean isViewAll = true;
    public boolean isViewAllEMI = true;
    public static List<TradelinesItem> mCustomData;
    public static List<Datum> mEMIData;
    private Context mContext;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mEMIData = new ArrayList<>();
        mUtil = new Utility(mContext);

        SharedPref.getInstance().putString(ACCOUNT_TYPE, getResources().getString(R.string.COMMERCIAL));

        mEquiFaxApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface.class);
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        mReportHelper = EquiFaxReportHelper.getInstance();
        new FirebaseLogger(getContext()).sendLog("Home Fragment", "Home Fragment");
        binding = EquifaxHomeFragmentBinding.inflate(getLayoutInflater());
        binding.scoreNotFound.btnInitScore.setOnClickListener(this);
        binding.scoreNotFound.btnInitScore.setOnClickListener(v -> {
            if (SharedPref.getInstance().getBoolean(IS_SKIP_GSTIN, false)) {
                Intent intent = new Intent(getContext(), AuthAccountActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getContext(), AuthAccountActivity.class);
                intent.putExtra("org_id", SharedPref.getInstance().getInt(ORG_ID) + "");
                startActivity(intent);
            }
        });
        binding.mainContent.setVisibility(View.GONE);
        getCommercialReport();
        return binding.getRoot();
    }

    private void initLayout() {
        binding.featuredOptionLayout.layoutAlerts.setOnClickListener(this);
        binding.featuredOptionLayout.layoutCalculator.setOnClickListener(this);
        binding.featuredOptionLayout.layoutDebtProfile.setOnClickListener(this);
        binding.featuredOptionLayout.layoutDownloadReport.setOnClickListener(this);

        binding.dashboardRiskLayoutIndividual.btnConcern.setOnClickListener(this);
        binding.dashboardRiskLayoutIndividual.imgBtnShareCard.setOnClickListener(this);
        binding.dashboardRiskLayout.imgBtnCommercialShareCard.setOnClickListener(this);
        binding.recyclerViewCompliance.setHasFixedSize(true);
        binding.recyclerViewCompliance.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewMicroblog.setHasFixedSize(true);
        binding.recyclerViewMicroblog.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewEmi.setHasFixedSize(true);
        binding.recyclerViewEmi.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.dashboardRiskLayoutIndividual.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //((EquiFaxMainActivity) getActivity()).openIndividual();
                Intent intent = new Intent(getActivity(), EquifaxIndividualDetailsActivity.class);
                startActivity(intent);
            }
        });

        binding.dashboardRiskLayout.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //((EquiFaxMainActivity) getActivity()).openCommercial();
                // startActivity(new Intent(getActivity(), EquifaxCommercialDetailsActivity.class));
                Intent intent = new Intent(getActivity(), EquifaxCommercialDetailsActivity.class);
                startActivity(intent);
            }
        });


        binding.layoutCommercialReport.btnReportDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportDownload();
            }
        });
        binding.recyclerViewMicroblog.addOnItemTouchListener(new RecyclerTouchListener(getContext(), binding.recyclerViewMicroblog, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getContext(), MicroBlogsActivity.class);
                intent.putExtra("slug", mMicroBlog.get(position).getSlug() + "");
                startActivity(intent);
            }

        }));

        binding.llAddEmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((mData.getMetadata().getCommercial_progress_step() + "").equalsIgnoreCase("3.0")) {
                    initMyAccount(0);
                } else if ((mData.getMetadata().getRetail_progress_step() + "").equalsIgnoreCase("3.0")) {
                    initMyAccountRetail(0);
                }
            }
        });

        binding.txvViewAllCompliance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isViewAll) {
                    ((ComplianceCalendarListAdapter) complianceCalendarListAdapter).setExpanded(true);
                    binding.txvViewAllCompliance.setText("Less");
                    isViewAll = false;
                } else {
                    ((ComplianceCalendarListAdapter) complianceCalendarListAdapter).setExpanded(false);
                    binding.txvViewAllCompliance.setText("View All");
                    isViewAll = true;
                }
            }
        });


        binding.txvViewAllEmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isViewAllEMI) {
                    ((EquifaxEMIListAdapter) emiListAdapter).setExpanded(true);
                    binding.txvViewAllEmi.setText(getResources().getString(R.string.view_less));
                    binding.txvViewAllEmi.setPaintFlags(binding.txvViewAllEmi.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    isViewAllEMI = false;
                } else {
                    ((EquifaxEMIListAdapter) emiListAdapter).setExpanded(false);
                    binding.txvViewAllEmi.setText(getResources().getString(R.string.view_more));
                    binding.txvViewAllEmi.setPaintFlags(binding.txvViewAllEmi.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    isViewAllEMI = true;
                }
            }
        });

        getHomeData();
        initCommercialData();
        commercialEMIDetails();
        //emiDetails();
    }

    private void initLayoutData() {
        binding.dashboardRiskLayout.txtCreditAge.setText("Credit Age: " + mData.getReport().getCreditAge());
        binding.dashboardRiskLayout.txtProfileDate.setText(DateFormatHelper.getProfileDate(
                mData.getReport().getUpdatedAt()
        ));
        binding.dashboardRiskLayout.scoreValue.setText(mData.getReport().getScoreValue() + "");
        binding.dashboardRiskLayout.txtOrgName.setText(mData.getReport().getLegalName());
        binding.dashboardRiskLayout.btnCheckScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mData.getMetadata().getDays_remaining() <= 0) {
                    initEquifax();
                    binding.dashboardRiskLayout.btnCheckScore.setVisibility(View.GONE);
                    binding.dashboardRiskLayout.progressBar.setVisibility(View.VISIBLE);
                } else {
                    initCommercialBottomSheet(mData.getMetadata().getDays_remaining() + "");
                }
            }
        });

        binding.dashboardRiskLayout.riskLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //((EquiFaxMainActivity) getActivity()).openCommercial();
            }
        });

        binding.dashboardRiskLayout.btnScoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initCommercialCreditScoreInfoSheet();
            }
        });
        Logger.errorLogger("scoreComment", mData.getReport().getScoreComment());

        if (mData.getReport().getScoreComment().equalsIgnoreCase("Low Risk")) {
            binding.dashboardRiskLayout.riskType.setText(getResources().getString(R.string.low_risk));
            binding.dashboardRiskLayout.riskType.setTextColor(getResources().getColor(R.color.light_green));
        } else if (mData.getReport().getScoreComment().equalsIgnoreCase("Very Low Risk")) {
            binding.dashboardRiskLayout.riskType.setText(getResources().getString(R.string.very_low_risk));
            binding.dashboardRiskLayout.riskType.setTextColor(getResources().getColor(R.color.light_green));
        } else if (mData.getReport().getScoreComment().equalsIgnoreCase("High Risk")) {
            binding.dashboardRiskLayout.riskType.setText(getResources().getString(R.string.high_risk));
            binding.dashboardRiskLayout.riskType.setTextColor(getResources().getColor(R.color.light_red));
        } else if (mData.getReport().getScoreComment().equalsIgnoreCase("Very High Risk")) {
            binding.dashboardRiskLayout.riskType.setText(getResources().getString(R.string.very_high_risk));
            binding.dashboardRiskLayout.riskType.setTextColor(getResources().getColor(R.color.light_red));
        } else if (mData.getReport().getScoreComment().equalsIgnoreCase("Medium Risk")) {
            binding.dashboardRiskLayout.riskType.setText(getResources().getString(R.string.medium_risk));
            binding.dashboardRiskLayout.riskType.setTextColor(getResources().getColor(R.color.yellow));
        }
        try {
            float score_percentage = ((mData.getReport().getScoreValue()) * 10);

            int score = mData.getReport().getScoreValue();
            if (score >= 1 && score <= 2) {
                binding.dashboardRiskLayout.scoreScale.setText("Excellent");
                score_percentage = (((mData.getReport().getScoreValue()) * 10));
            } else if (score >= 3 && score <= 4) {
                binding.dashboardRiskLayout.scoreScale.setText("Good");
                score_percentage = (((mData.getReport().getScoreValue()) * 10));
            } else if (score >= 5 && score <= 7) {
                binding.dashboardRiskLayout.scoreScale.setText("Average");
                score_percentage = (((mData.getReport().getScoreValue()) * 10));
            } else if (score >= 8 && score <= 10) {
                binding.dashboardRiskLayout.scoreScale.setText("Poor ");
                score_percentage = ((mData.getReport().getScoreValue()) * 10);
            }
            LinearLayout.LayoutParams scorePointerLayout = (LinearLayout.LayoutParams)
                    binding.dashboardRiskLayout.scorePointerLayout.getLayoutParams();
            scorePointerLayout.weight = score_percentage;
            binding.dashboardRiskLayout.scorePointerLayout.setLayoutParams(scorePointerLayout);

            if (score >= 8) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) binding.dashboardRiskLayout.thumb.getLayoutParams();
                lp.gravity = Gravity.RIGHT;
                binding.dashboardRiskLayout.thumb.setLayoutParams(lp);
            }


            LinearLayout.LayoutParams scorePointerSpaceLayout = (LinearLayout.LayoutParams)
                    binding.dashboardRiskLayout.scorePointerSpaceLayout.getLayoutParams();
            scorePointerSpaceLayout.weight = 100 - score_percentage;
            binding.dashboardRiskLayout.scorePointerSpaceLayout.setLayoutParams(scorePointerSpaceLayout);

        } catch (Exception ex) {

        }
    }


    private void initUsername() {
        try {
            String[] legalNameArr = mData.getReport().getLegalName().split(" ");
            String legalName = StringHelper.toCamelCase(legalNameArr[0] + " " + legalNameArr[1]);
            binding.dashboardUserProfileLayout.userPrefix.setText(legalName.substring(0, 1));
            SharedPref.getInstance().putString(LEGAL_NAME, mData.getReport().getLegalName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initCommercialData() {
        if (mReportHelper.getCommercialReport() != null) {
            mData = mReportHelper.getCommercialReport();
            initUsername();
            if ((mData.getMetadata().getCommercial_progress_step() + "").equalsIgnoreCase("3.1")) {
                binding.dashboardRiskLayout.rlNoReport.setVisibility(View.VISIBLE);
                binding.dashboardRiskLayout.rlCommercialRiskLayout.setVisibility(View.GONE);
            } else if ((mData.getMetadata().getCommercial_progress_step() + "").equalsIgnoreCase("2.2")) {
                binding.dashboardRiskLayout.rlNoReport.setVisibility(View.VISIBLE);
                binding.dashboardRiskLayout.rlCommercialRiskLayout.setVisibility(View.GONE);
            } else if ((mData.getMetadata().getCommercial_progress_step() + "").equalsIgnoreCase("1.1")) {
                binding.dashboardRiskLayout.rlNoReport.setVisibility(View.VISIBLE);
                binding.dashboardRiskLayout.btnConcern.setOnClickListener(this);
                binding.dashboardRiskLayout.txvNoResult.setText(getResources().getString(R.string.message_commercial_1_1));
                binding.dashboardRiskLayout.btnConcernTxt.setText(getResources().getString(R.string.generate));
                binding.dashboardRiskLayout.rlCommercialRiskLayout.setVisibility(View.GONE);
            } else if (mData.getMetadata().isIs_retail_masked()) {
                binding.dashboardRiskLayout.rlNoReport.setVisibility(View.VISIBLE);
                binding.dashboardRiskLayout.btnConcern.setOnClickListener(this);
                binding.dashboardRiskLayout.txvNoResult.setText(getResources().getString(R.string.message_commercial_1_1));
                binding.dashboardRiskLayout.btnConcernTxt.setText(getResources().getString(R.string.generate));
                binding.dashboardRiskLayout.rlCommercialRiskLayout.setVisibility(View.GONE);
            } else if ((mData.getMetadata().getCommercial_progress_step() + "").equalsIgnoreCase("0.0")) {
                binding.scoreLayout.setVisibility(View.GONE);
                binding.scoreNotFoundLayout.setVisibility(View.VISIBLE);
                binding.mainContent.setVisibility(View.VISIBLE);
            } else if ((mData.getMetadata().getCommercial_progress_step() + "").equalsIgnoreCase("2.1")) {
                binding.scoreLayout.setVisibility(View.GONE);
                binding.scoreNotFoundLayout.setVisibility(View.VISIBLE);
                binding.mainContent.setVisibility(View.VISIBLE);
            } else if ((mData.getMetadata().getCommercial_progress_step() + "").equalsIgnoreCase("3.0")) {
                binding.dashboardRiskLayout.rlNoReport.setVisibility(View.GONE);
                binding.dashboardRiskLayout.rlCommercialRiskLayout.setVisibility(View.VISIBLE);
                initResponseData();
            } else {
                binding.dashboardRiskLayout.rlCommercialRiskLayout.setVisibility(View.VISIBLE);
                binding.dashboardRiskLayout.rlNoReport.setVisibility(View.GONE);
                initResponseData();
            }
        }
        initRetailData();
    }

    private void initRetailData() {
        mEquiFaxIndividualData = mReportHelper.getRetailReport();
        if (mEquiFaxIndividualData != null) {
            if ((mEquiFaxIndividualData.getMetadata().getRetail_progress_step() + "").equalsIgnoreCase("3.1")) {
                binding.dashboardRiskLayoutIndividual.rlNoReport.setVisibility(View.VISIBLE);
                binding.dashboardRiskLayoutIndividual.rlIndividualRiskLayout.setVisibility(View.GONE);
            } else if ((mEquiFaxIndividualData.getMetadata().getRetail_progress_step() + "").equalsIgnoreCase("2.2")) {
                binding.dashboardRiskLayoutIndividual.rlNoReport.setVisibility(View.VISIBLE);
                binding.dashboardRiskLayoutIndividual.rlIndividualRiskLayout.setVisibility(View.GONE);
            } else if ((mEquiFaxIndividualData.getMetadata().getRetail_progress_step() + "").equalsIgnoreCase("3.0")) {
                binding.dashboardRiskLayoutIndividual.rlNoReport.setVisibility(View.GONE);
                binding.dashboardRiskLayoutIndividual.rlIndividualRiskLayout.setVisibility(View.VISIBLE);
                initIndividualData();
            } else {
                binding.dashboardRiskLayoutIndividual.rlNoReport.setVisibility(View.GONE);
                binding.dashboardRiskLayoutIndividual.rlIndividualRiskLayout.setVisibility(View.VISIBLE);
                initIndividualData();
            }
        } else {
            binding.dashboardRiskLayoutIndividual.rlNoReport.setVisibility(View.VISIBLE);
            binding.dashboardRiskLayoutIndividual.rlIndividualRiskLayout.setVisibility(View.GONE);
        }

        if (binding.dashboardRiskLayoutIndividual.rlNoReport.getVisibility() == View.VISIBLE) {
            if (binding.dashboardRiskLayout.rlNoReport.getVisibility() == View.VISIBLE) {
                binding.dashboardRiskLayoutIndividual.mainContent.setVisibility(View.GONE);
            }
        }

        emiDetails(mEquiFaxIndividualData);
    }

    private void initResponseData() {
        initLayoutData();
    }

    public void initIndividualData() {
        mData = mReportHelper.getCommercialReport();
        binding.dashboardRiskLayoutIndividual.imgCreditBureau.setImageDrawable(getResources().getDrawable(R.mipmap.ic_equifax_logo));
        binding.dashboardRiskLayoutIndividual.scoreSeekBar.setEnabled(false);
        binding.dashboardRiskLayoutIndividual.txtCreditAge.setText("Credit Age: " + mEquiFaxIndividualData.getReport().getCredit_age());
        binding.dashboardRiskLayoutIndividual.txtUserName.setText(mEquiFaxIndividualData.getReport().getFull_name());
        binding.dashboardRiskLayoutIndividual.txtProfileDate.setText(DateFormatHelper.getProfileDate(
                mEquiFaxIndividualData.getReport().getUpdated_at()
        ));
        binding.dashboardRiskLayoutIndividual.scoreValue.setText(mEquiFaxIndividualData.getReport().getScore_value() + "");
        binding.dashboardRiskLayoutIndividual.scoreSeekBar.setProgress(mEquiFaxIndividualData.getReport().getScore_value());
        binding.dashboardRiskLayoutIndividual.txtScorePauseDays.setText("Report update in "
                + mEquiFaxIndividualData.getMetadata().getDays_remaining() + " days");

        binding.dashboardRiskLayoutIndividual.btnCheckScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mData.getMetadata().getDays_remaining() <= 0) {
                    binding.dashboardRiskLayoutIndividual.btnCheckScore.setVisibility(View.GONE);
                    binding.dashboardRiskLayoutIndividual.progressBar.setVisibility(View.VISIBLE);
                    initEquifax();
                } else {
                    initBottomSheet2(mEquiFaxIndividualData.getMetadata().getDays_remaining() + "");
                }

            }
        });

        binding.dashboardRiskLayoutIndividual.btnScoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initCreditScoreInfoSheet();
            }
        });

        if (mEquiFaxIndividualData.getReport().getScore_comment().equalsIgnoreCase("Low Risk")) {
            binding.dashboardRiskLayoutIndividual.riskType.setText(getResources().getString(R.string.low_risk));
            binding.dashboardRiskLayoutIndividual.riskType.setTextColor(getResources().getColor(R.color.light_green));
        } else if (mEquiFaxIndividualData.getReport().getScore_comment().equalsIgnoreCase("High Risk")) {
            binding.dashboardRiskLayoutIndividual.riskType.setText(getResources().getString(R.string.high_risk));
            binding.dashboardRiskLayoutIndividual.riskType.setTextColor(getResources().getColor(R.color.light_red));
        } else if (mEquiFaxIndividualData.getReport().getScore_comment().equalsIgnoreCase("Very High Risk")) {
            binding.dashboardRiskLayoutIndividual.riskType.setText(getResources().getString(R.string.very_high_risk));
            binding.dashboardRiskLayoutIndividual.riskType.setTextColor(getResources().getColor(R.color.light_red));
        } else if (mEquiFaxIndividualData.getReport().getScore_comment().equalsIgnoreCase("Medium Risk")) {
            binding.dashboardRiskLayoutIndividual.riskType.setText(getResources().getString(R.string.medium_risk));
            binding.dashboardRiskLayoutIndividual.riskType.setTextColor(getResources().getColor(R.color.yellow));
        }
        initScoreValue();
        try {
            float score_percentage = ((mEquiFaxIndividualData.getReport().getScore_value() - 300) * 100) / 600;

            int score = mEquiFaxIndividualData.getReport().getScore_value();
            if (score >= 300 && score <= 650) {
                binding.dashboardRiskLayoutIndividual.scoreScale.setText("Poor");
                score_percentage = (((mEquiFaxIndividualData.getReport().getScore_value() - 300) * 100) / 600);
            } else if (score >= 651 && score <= 770) {
                binding.dashboardRiskLayoutIndividual.scoreScale.setText("Average");
                score_percentage = (((mEquiFaxIndividualData.getReport().getScore_value() - 300) * 100) / 600);
            } else if (score >= 771 && score <= 850) {
                binding.dashboardRiskLayoutIndividual.scoreScale.setText("Good");
                score_percentage = (((mEquiFaxIndividualData.getReport().getScore_value() - 300) * 100) / 600);
            } else if (score >= 851 && score <= 900) {
                binding.dashboardRiskLayoutIndividual.scoreScale.setText("Excellent ");
                score_percentage = ((mEquiFaxIndividualData.getReport().getScore_value() - 300) * 100) / 600;
            }
            LinearLayout.LayoutParams scorePointerLayout = (LinearLayout.LayoutParams)
                    binding.dashboardRiskLayoutIndividual.scorePointerLayout.getLayoutParams();
            scorePointerLayout.weight = score_percentage;
            binding.dashboardRiskLayoutIndividual.scorePointerLayout.setLayoutParams(scorePointerLayout);

            if (score > 850) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) binding.dashboardRiskLayoutIndividual.thumb.getLayoutParams();
                lp.gravity = Gravity.RIGHT;
                binding.dashboardRiskLayoutIndividual.thumb.setLayoutParams(lp);
            }


            LinearLayout.LayoutParams scorePointerSpaceLayout = (LinearLayout.LayoutParams)
                    binding.dashboardRiskLayoutIndividual.scorePointerSpaceLayout.getLayoutParams();
            scorePointerSpaceLayout.weight = 100 - score_percentage;
            binding.dashboardRiskLayoutIndividual.scorePointerSpaceLayout.setLayoutParams(scorePointerSpaceLayout);

        } catch (Exception ex) {

        }
    }

    private void initEquifax() {
        Intent intent = new Intent(getActivity(), EquiFaxOtpActivity.class);
        intent.putExtra("org_id", SharedPref.getInstance().getInt(ORG_ID));
        intent.putExtra("is_otp", false);
        startActivity(intent);
    }

    private void getHomeData() {
        Logger.errorLogger("profileData", SharedPref.getInstance().getString(USER_TYPE));
        Call<HomeDataInfo> call1 = mApiInterface.getHomePageData1(getResources().getString(R.string.COMPANY), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<HomeDataInfo>() {
            @Override
            public void onResponse(Call<HomeDataInfo> call, Response<HomeDataInfo> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {

                        mReportHelper.setCompliance(response.body());
                        HomeDataInfo resource = response.body();
                        mMicroBlog = resource.getData().getMicroblogs();
                        if (resource.getData().getMicroblogs().size() > 0) {
                            MicroblogListAdapter adapter = new MicroblogListAdapter(mMicroBlog, getContext());
                            binding.recyclerViewMicroblog.setVisibility(View.VISIBLE);
                            binding.recyclerViewMicroblog.setAdapter(adapter);
                        } else {
                            binding.recyclerViewMicroblog.setVisibility(View.GONE);
                        }

                        if (resource.getData().getEquifaxEmi().size() > 0) {
                            emiListAdapter = new EquifaxEMIListAdapter(resource.getData().getEquifaxEmi(), getContext(), EquiFaxHomeFragment.this);
                            binding.llEmi.setVisibility(View.VISIBLE);
                            binding.llViewEmi.setVisibility(View.VISIBLE);
                            binding.recyclerViewEmi.setAdapter(emiListAdapter);
                            binding.llAddEmi.setVisibility(View.GONE);
                            if (resource.getData().getEquifaxEmi().size() > 3) {
                                binding.txvViewAllEmi.setVisibility(View.VISIBLE);
                            } else {
                                binding.txvViewAllEmi.setVisibility(View.GONE);
                            }
                        } else {
                            binding.llEmi.setVisibility(View.GONE);
                            binding.llEmi.setVisibility(View.GONE);
                            if ((mData.getMetadata().getCommercial_progress_step() + "").equalsIgnoreCase("3.0")) {
                                binding.llAddEmi.setVisibility(View.VISIBLE);
                            } else if ((mData.getMetadata().getRetail_progress_step() + "").equalsIgnoreCase("3.0")) {
                                binding.llAddEmi.setVisibility(View.VISIBLE);
                            } else {
                                binding.llAddEmi.setVisibility(View.GONE);
                            }
                        }

                        if (resource.getData().getComplianceCalender().size() > 0) {
                            complianceCalendarListAdapter = new ComplianceCalendarListAdapter(resource.getData().getComplianceCalender(), getContext(), false);
                            binding.txvUpcoming.setVisibility(View.VISIBLE);
                            binding.llViewCompliance.setVisibility(View.VISIBLE);
                            binding.recyclerViewCompliance.setAdapter(complianceCalendarListAdapter);
                            if (resource.getData().getComplianceCalender().size() > 3) {
                                binding.txvViewAllCompliance.setVisibility(View.VISIBLE);
                            } else {
                                binding.txvViewAllCompliance.setVisibility(View.GONE);
                            }
                        } else {
                            binding.txvUpcoming.setVisibility(View.GONE);
                            binding.llViewCompliance.setVisibility(View.GONE);
                        }
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
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_btn_commercial_share_card:
                shareCard();
                break;
            case R.id.btn_concern:
                Intent intent = new Intent(getActivity(), CommercialMaskedActivity.class);
                if (mData.getMetadata().isIs_retail_masked()) {
                    intent.putExtra(AppConstant.IS_RETAIL_MASKED, true);
                } else {
                    intent.putExtra(AppConstant.IS_RETAIL_MASKED, false);
                }
                startActivity(intent);
                break;
            case R.id.img_btn_share_card:
                shareIndividualCard();
                break;
            case R.id.layoutAlerts:
                if (SharedPref.getInstance().getBoolean(IS_EQUI_FAX, false)) {
                    startActivity(new Intent(getActivity(), EquiFaxAlertFragment.class));
                } else {
                    startActivity(new Intent(getActivity(), AlertFragment.class));
                }
                break;
            case R.id.layoutCalculator:
                startActivity(new Intent(getActivity(), AllCalculatorActivity.class));
                break;
            case R.id.layoutDebtProfile:
                if (SharedPref.getInstance().getBoolean(IS_EQUI_FAX, false)) {
                    startActivity(new Intent(getActivity(), EquiFaxDebtProfileFragment.class));
                } else {
                    startActivity(new Intent(getActivity(), DebtProfileFragment.class));
                }
                break;
            case R.id.layoutDownloadReport:
                reportDownload();
                break;
        }
    }

    private void shareIndividualCard() {
        binding.dashboardRiskLayoutIndividual.progressBarCardShareIndividual.setVisibility(View.VISIBLE);
        binding.dashboardRiskLayoutIndividual.imgBtnShareCard.setVisibility(View.GONE);
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                String html = ScoreCardShareHelper.individualCardShareData(mReportHelper.getRetailReport(), getContext());
                return new Html2Bitmap.Builder().setContext(getActivity()).setContent(WebViewContent.html(html)).build().getBitmap();
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    binding.dashboardRiskLayoutIndividual.progressBarCardShareIndividual.setVisibility(View.GONE);
                    binding.dashboardRiskLayoutIndividual.imgBtnShareCard.setVisibility(View.VISIBLE);
                    shareResultAsImage(bitmap);
//                              imageView.setImageBitmap(bitmap);
                }
            }
        }.execute();
//                    initCreditScoreInfoSheet();

    }

    private void shareCard() {
        binding.dashboardRiskLayout.progressBarCardShare.setVisibility(View.VISIBLE);
        binding.dashboardRiskLayout.imgBtnCommercialShareCard.setVisibility(View.GONE);
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                String html = ScoreCardShareHelper.commercialCardShareData(mReportHelper.getCommercialReport(), getContext());
                return new Html2Bitmap.Builder().setContext(getActivity()).setContent(WebViewContent.html(html)).build().getBitmap();
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    shareResultAsImage(bitmap);
                    binding.dashboardRiskLayout.progressBarCardShare.setVisibility(View.GONE);
                    binding.dashboardRiskLayout.imgBtnCommercialShareCard.setVisibility(View.VISIBLE);
//                              imageView.setImageBitmap(bitmap);
                }
            }
        }.execute();
//                    initCreditScoreInfoSheet();

    }

    private void shareResultAsImage(Bitmap bitmap) {
        try {
            String pathOfBmp = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
                    bitmap, UUID.randomUUID().toString() + ".png", null);
            Logger.errorLogger("Bitmap Crash", pathOfBmp);
            if (pathOfBmp != "") {
                Uri bmpUri = Uri.parse(pathOfBmp);
                final Intent emailIntent1 = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent1.setType("image/png");
                emailIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri);
                startActivity(emailIntent1);
            } else {
                Logger.errorLogger("Bitmap Crash", "null path ");
            }
        } catch (Exception ex) {
            Logger.errorLogger("Bitmap Crash", ex.getMessage());
            Logger.errorLogger("Bitmap Crash", ex.getLocalizedMessage());
        }
    }

    private void initCommercialData(String response) {
        mReportHelper.setEquiFaxCommercial(response);
        getIndividualReport();
    }

    private void initRetailData(String response) {
        mReportHelper.setEquiFaxRetail(response);
        binding.mainContent.setVisibility(View.VISIBLE);
        initLayout();
    }

    private void getCommercialReport() {
        Logger.errorLogger("Token", SharedPref.getInstance().getString(TOKEN));
        Logger.errorLogger("Org Id", SharedPref.getInstance().getInt(ORG_ID) + "");
        Call<String> call1 = mEquiFaxApiInterface.getReport(getResources().getString(R.string.COMMERCIAL), SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    int responseCode = response.code();
                    if (response.isSuccessful()) {
                        if (responseCode == 200) {
                            binding.scoreNotFoundLayout.setVisibility(View.GONE);
                            initCommercialData(response.body());
                        } else if (responseCode == 202) {
                            binding.scoreLayout.setVisibility(View.GONE);
                            binding.scoreNotFoundLayout.setVisibility(View.VISIBLE);
                            binding.mainContent.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (responseCode == 403) {
                            mUtil.logout();
                        }
                    }
                    try {
                        Logger.errorLogger(this.getClass().getName(), response.errorBody().string() + "");
                    } catch (Exception ex) {
                        Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
                    }
                    Logger.errorLogger(this.getClass().getName(), response.code() + "");
                } catch (Exception ex) {
//
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    private void getIndividualReport() {
        Logger.errorLogger("Token", SharedPref.getInstance().getString(TOKEN));
        Call<String> call1 = mEquiFaxApiInterface.getReport(getResources().getString(R.string.RETAIL), SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    Logger.errorLogger("Retailer Response Code", "");
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            Logger.errorLogger("Retailer Response", response.body() + "");
                            initRetailData(response.body());
                        }
                    } else {
                        if (response.code() == 403) {
                            mUtil.logout();
                        } else if (response.code() == 500) {
                        }
                    }
                    try {
                        Logger.errorLogger(this.getClass().getName(), response.errorBody().string() + "");
                    } catch (Exception ex) {
                        Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
                    }
                    Logger.errorLogger(this.getClass().getName(), response.code() + "");
                } catch (Exception ex) {
//
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    @Override
    public void onEvent() {
        if ((mData.getMetadata().getCommercial_progress_step() + "").equalsIgnoreCase("3.0")) {
            initMyAccount(0);
        } else if ((mData.getMetadata().getRetail_progress_step() + "").equalsIgnoreCase("3.0")) {
            initMyAccountRetail(0);
        }
    }

    private void initMyAccount(int index) {
        Gson gson = new Gson();
        Intent intent = new Intent(getActivity(), EquiFaxMyAccount.class);
        intent.putExtra("data", gson.toJson(mData.getReport().getTradelines()));
        intent.putExtra("index", index);
        startActivity(intent);
    }

    private void initMyAccountRetail(int index) {
        Gson gson = new Gson();
        Intent intent = new Intent(getActivity(), EquiFaxIndividualMyAccount.class);
        intent.putExtra("data", gson.toJson(mEquiFaxIndividualData.getReport().getTradelines()));
        intent.putExtra("index", index);
        startActivity(intent);
    }

    private void reportDownload() {
        String userName = StringHelper.toCamelCase(mData.getReport().getLegalName());
        Call<DownloadInfo> call = mEquiFaxApiInterface.downloadReport(SharedPref.getInstance().getInt(ORG_ID), "commercial", "equifax", "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call.enqueue(new Callback<DownloadInfo>() {
            @Override
            public void onResponse(Call<DownloadInfo> call, Response<DownloadInfo> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());
                    DownloadInfo response1 = response.body();
                    //if (response1.getData().size() > 0) {
                    if (response1.getData().getIsGenerated() || response1.getData().getStatus().equalsIgnoreCase("COMPLETED")) {
                        String url = response1.getData().getReportFileInfo().getUrl();
                        Uri uri = Uri.parse(url);

                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setDescription(" being downloaded");
                        request.allowScanningByMediaScanner();
                        request.setMimeType("application/pdf");
                        request.setTitle(userName + " Your Rupyz Equifax Report");
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalFilesDir(getContext(), Environment.DIRECTORY_DOCUMENTS, userName + "." + "pdf");
                        DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                        downloadManager.enqueue(request);
                        Toast.makeText(getActivity(), "Your Report is Downloading", Toast.LENGTH_SHORT).show();
                        openFile();
                    } else {
                        Toast.makeText(getActivity(), response1.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    //}
                } else if (response.code() == 400) {
                    try {
                        String responseBody = response.errorBody().string();
                        Logger.errorLogger(this.getClass().getName(), responseBody + "");
                        new SessionHelper(getActivity()).requestMessage(
                                responseBody);
                    } catch (Exception ex) {
                        Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
                    }
                }
            }

            @Override
            public void onFailure(Call<DownloadInfo> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    private void commercialEMIDetails() {
        mCustomData = new ArrayList<>();
        Call<CreateEMIResponse> call = mEquiFaxApiInterface.getEMIList(
                1, "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call.enqueue(new Callback<CreateEMIResponse>() {
            @Override
            public void onResponse(Call<CreateEMIResponse> call, Response<CreateEMIResponse> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());
                    CreateEMIResponse response1 = response.body();
                    mReportHelper.setEquifaxCommercialEMI(response.body());
                    if (response1.getData().size() > 0) {
                        for (Datum Item : response1.getData()) {
                            for (TradelinesItem tradelinesItem : mCustomData) {
                                if (Item.getAccountNo().equals(tradelinesItem.getAccountNo())) {
                                    tradelinesItem.setInstallmentAmount(Item.getInstallmentAmount());
                                    tradelinesItem.setMonthDueDay(Item.getMonthDueDay());
                                    tradelinesItem.setRepaymentTenure(Item.getRepaymentTenure());
                                    tradelinesItem.setInterestRate(Item.getInterestRate());
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CreateEMIResponse> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    public void initCommercialBottomSheet(String days) {
        Bundle bundle = new Bundle();
        bundle.putString("days", days);
        RefreshRemarkModal fragment = new RefreshRemarkModal();
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(), "");
    }

    public void initCommercialCreditScoreInfoSheet() {
        Bundle bundle = new Bundle();
        EquifaxCommercialCreditScoreInfoModal fragment = new EquifaxCommercialCreditScoreInfoModal();
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(), "");
    }

    public void initCreditScoreInfoSheet() {
        Bundle bundle = new Bundle();
        EquifaxCreditScoreInfoModal fragment = new EquifaxCreditScoreInfoModal();
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(), "");
    }

    public void initBottomSheet2(String days) {
        Bundle bundle = new Bundle();
        bundle.putString("days", days);
        RefreshRemarkModal fragment = new RefreshRemarkModal();
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(), "");
    }

    private void emiDetails(EquiFaxIndividualInfoModel equiFaxIndividualInfoModel) {
        Call<CreateEMIResponse> call = mEquiFaxApiInterface.getEMIList(
                1, "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call.enqueue(new Callback<CreateEMIResponse>() {
            @Override
            public void onResponse(Call<CreateEMIResponse> call, Response<CreateEMIResponse> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());
                    CreateEMIResponse response1 = response.body();
                    mReportHelper.setEquifaxIndividualEMI(response.body());
                    if (response1.getData() != null && response1.getData().size() > 0 && equiFaxIndividualInfoModel.getReport().getTradelines() != null) {
                        for (Datum Item : response1.getData()) {
                            for (Tradeline tradelinesItem : equiFaxIndividualInfoModel.getReport().getTradelines()) {
                                if (Item.getAccountNo().equals(tradelinesItem.getAccount_no())) {
                                    tradelinesItem.setInstallment_amount(Item.getInstallmentAmount());
                                    tradelinesItem.setMonth_due_day(Item.getMonthDueDay());
                                    tradelinesItem.setRepayment_tenure(Item.getRepaymentTenure() + "");
                                    tradelinesItem.setInterest_rate(Item.getInterestRate() + "");
                                    mEMIData.add(Item);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CreateEMIResponse> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    private void openFile() {
        Intent i = new Intent();
        i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        startActivity(i);
    }

    private void initScoreValue() {
        try {
            if (mEquiFaxIndividualData.getReport().getGraph_data().getScore().size() > 1) {
                int current_score = mEquiFaxIndividualData.getReport().getGraph_data().getScore().get(mEquiFaxIndividualData.getReport().getGraph_data().getScore().size() - 1);
                int previous_score = mEquiFaxIndividualData.getReport().getGraph_data().getScore().get(mEquiFaxIndividualData.getReport().getGraph_data().getScore().size() - 2);
                if (current_score > previous_score) {
                    binding.dashboardRiskLayoutIndividual.scoreDownValue.setText((current_score - previous_score) + "");
                    binding.dashboardRiskLayoutIndividual.scoreDownValue.setTextColor(getActivity().getResources().getColor(R.color.light_green));
                    binding.dashboardRiskLayoutIndividual.scoreDownImage.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.ic_score_up));
                } else {
                    binding.dashboardRiskLayoutIndividual.scoreDownValue.setText((previous_score - current_score) + "");
                    binding.dashboardRiskLayoutIndividual.scoreDownValue.setTextColor(getActivity().getResources().getColor(R.color.light_red));
                    binding.dashboardRiskLayoutIndividual.scoreDownImage.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.ic_score_down));
                }
            }
        } catch (Exception ex) {

        }
    }
}

