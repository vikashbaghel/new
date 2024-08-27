package com.app.rupyz.ui.home;

import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;
import static com.app.rupyz.generic.utils.SharePrefConstant.USER_TYPE;
import static com.app.rupyz.generic.utils.SharePrefConstant.USER_TYPE_FM_EM;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.MainActivity;
import com.app.rupyz.R;
import com.app.rupyz.adapter.blogs.EMIListAdapter;
import com.app.rupyz.adapter.blogs.MicroblogListAdapter;
import com.app.rupyz.adapter.complaince.ComplianceCalendarListAdapter;
import com.app.rupyz.databinding.HomeFragmentBinding;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.helper.StringHelper;
import com.app.rupyz.generic.logger.FirebaseLogger;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.blog.HomeDataInfo;
import com.app.rupyz.generic.model.blog.Microblog;
import com.app.rupyz.generic.model.createemi.experian.Datum;
import com.app.rupyz.generic.model.createemi.experian.ExperianEMIResponse;
import com.app.rupyz.generic.model.individual.experian.ExperianInfoModel;
import com.app.rupyz.generic.model.individual.experian.Tradeline;
import com.app.rupyz.generic.model.user.UserViewModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.utils.RecyclerTouchListener;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.account.MyAccount;
import com.app.rupyz.ui.account.OwnershipMixActivity;
import com.app.rupyz.ui.blogs.MicroBlogsActivity;
import com.app.rupyz.ui.calculator.all_calculator.AllCalculatorActivity;
import com.app.rupyz.ui.equifax.activity.AllComplianceActivity;
import com.app.rupyz.ui.home.dailog.score_insights.ScoreInsightInfoModal;
import com.app.rupyz.ui.home.dailog.score_insights.ScoreInsightRemarkModal;
import com.app.rupyz.ui.home.home_slide.ScorePagerAdapter;
import com.app.rupyz.ui.individual.em.SmartMatchActivity;
import com.app.rupyz.ui.organization.PANVerifyActivity;
import com.app.rupyz.ui.user.ProfileActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements View.OnClickListener, EMIListAdapter.EventListener {

    private ApiInterface mApiInterface;
    private HomeFragmentBinding binding;
    private Utility mUtil;
    public static ExperianInfoModel mData;
    private UserViewModel mUserData;
    private EquiFaxReportHelper mReportHelper;
    public static List<Tradeline> mCustomData;
    public static List<Datum> mEMIData;
    private List<Microblog> mMicroBlog = new ArrayList<>();
    public boolean isViewAll = true;
    public boolean isViewAllEMI = true;
    private ComplianceCalendarListAdapter complianceCalendarListAdapter;
    private EMIListAdapter emiListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mUtil = new Utility(getActivity());
        mEMIData = new ArrayList<>();
        mReportHelper = EquiFaxReportHelper.getInstance();
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        new FirebaseLogger(getContext()).sendLog("Home Fragment", "Home Fragment");
        binding = HomeFragmentBinding.inflate(getLayoutInflater());
        binding.recyclerViewCompliance.setHasFixedSize(true);
        binding.recyclerViewCompliance.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewMicroBlog.setHasFixedSize(true);
        binding.recyclerViewMicroBlog.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewEmi.setHasFixedSize(true);
        binding.recyclerViewEmi.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.scoreNotFoundIndividual.btnInitScore.setOnClickListener(this);

//        ((MainActivity) getContext()).updatePrefix(Utility.getName());
        binding.recyclerViewMicroBlog.addOnItemTouchListener(new RecyclerTouchListener(getContext(), binding.recyclerViewMicroBlog, new RecyclerTouchListener.ClickListener() {
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
                initMyAccount(0);
            }
        });

        binding.txvViewAllCompliance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AllComplianceActivity.class);
                startActivity(intent);
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
                    ((EMIListAdapter) emiListAdapter).setExpanded(true);
                    binding.txvViewAllEmi.setText(getResources().getString(R.string.view_less));
                    binding.txvViewAllEmi.setPaintFlags(binding.txvViewAllEmi.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    isViewAllEMI = false;
                } else {
                    ((EMIListAdapter) emiListAdapter).setExpanded(false);
                    binding.txvViewAllEmi.setText(getResources().getString(R.string.view_more));
                    binding.txvViewAllEmi.setPaintFlags(binding.txvViewAllEmi.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    isViewAllEMI = true;
                }
            }
        });

        getProfileData();
        getHomeData();
        return binding.getRoot();
    }

    private void intiRiskSlider() {
        ScorePagerAdapter adapter = new ScorePagerAdapter(getChildFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, true);
        binding.dashboardRiskLayout.viewpager.setAdapter(adapter);
        binding.dashboardRiskLayout.tablayout.setupWithViewPager(binding.dashboardRiskLayout.viewpager, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loan_layout_root:
                initMyAccount(0);
                break;
            case R.id.btn_calculator:
                startActivity(new Intent(getActivity(), AllCalculatorActivity.class));
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
            case R.id.btn_score_insights_info:
                initScoreInsightsSheet();
                break;
            case R.id.btn_init_score:
                if (SharedPref.getInstance().getString(USER_TYPE_FM_EM).equalsIgnoreCase("EM")) {
                    startActivity(new Intent(getActivity(), SmartMatchActivity.class));
                } else {
                    Intent intent = new Intent(getActivity(), PANVerifyActivity.class);
                    SharedPref.getInstance().putString(USER_TYPE, getResources().getString(R.string.individual));
                    startActivity(intent);
                }
                break;

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
        intent.putExtra("emidata", gson.toJson(mEMIData));
        intent.putExtra("index", index);
        System.out.println("emiDATA :- " + mEMIData.toString());
        startActivity(intent);
    }

    private void navOwnershipMix() {
        Gson gson = new Gson();
        Intent intent = new Intent(getActivity(), OwnershipMixActivity.class);
        intent.putExtra("data", gson.toJson(mData.getTradelines()));
        startActivity(intent);
    }

    private void getProfileData() {
        Logger.errorLogger("Profile", SharedPref.getInstance().getString(TOKEN));
        Call<String> call1 = mApiInterface.getDashboardData("Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            mReportHelper.setExperianReport(response.body());
                            Logger.errorLogger(this.getClass().getName(), response.body());
                            JsonParser jsonParser = new JsonParser();
                            JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                            Gson gson = new Gson();
                            Logger.errorLogger(this.getClass().getName(), "Test");
                            try {
                                if (jsonObj.get("data").getAsJsonArray().size() == 0) {
                                    binding.scoreNotFoundLayout.setVisibility(View.VISIBLE);
                                    binding.experianScoreNullLayout.experianNoReport.setVisibility(View.GONE);
                                    binding.dashboardRiskLayout.mainContent.setVisibility(View.GONE);
                                }
                            } catch (Exception ex) {

                            }
                            Logger.errorLogger(this.getClass().getName(), "Test1");
                            mData = gson.fromJson(jsonObj.get("data"), ExperianInfoModel.class);
                            emiDetails(mData);
                            if (mData.getTradelines() != null && mData.getTradelines().size() > 0) {
                                binding.experianScoreNullLayout.experianNoReport.setVisibility(View.GONE);
                                binding.dashboardRiskLayout.mainContent.setVisibility(View.VISIBLE);
                                binding.scoreNotFoundLayout.setVisibility(View.GONE);
                                intiRiskSlider();
                                try {
                                    binding.txtUserName.setText(StringHelper.toCamelCase(
                                            mData.getRelationship_details()
                                                    .getCurrent_Applicant_Details().getFirst_Name() + " " + mData.getRelationship_details()
                                                    .getCurrent_Applicant_Details().getLast_Name().toLowerCase()));

                                } catch (Exception ex) {
                                    Logger.errorLogger(this.getClass().getName(), response.code() + "");
                                }
                            } else {
                                binding.experianScoreNullLayout.experianNoReport.setVisibility(View.VISIBLE);
                                binding.dashboardRiskLayout.mainContent.setVisibility(View.GONE);
                                binding.scoreNotFoundLayout.setVisibility(View.GONE);
                                getProfileDataUser();
                            }
                        }
                    } else {
                        if (response.code() == 403) {
                            mUtil.logout();
                        }
                    }
                } catch (Exception ex) {
                    Logger.errorLogger(this.getClass().getName(), "ExceptionTsest");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    private void emiDetails(ExperianInfoModel tradeline) {
        mCustomData = new ArrayList<>();
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


    private void getProfileDataUser() {
        Logger.errorLogger("Profile", SharedPref.getInstance().getString(TOKEN));
        Call<String> call1 = mApiInterface.getReviewData("Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    try {
                        Logger.errorLogger(this.getClass().getName(), response.body());
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                        Gson gson = new Gson();
                        mUserData = gson.fromJson(jsonObj.get("data"), UserViewModel.class);
                        binding.txtUserName.setText(mUserData.getFirst_name().substring(0, 1).toUpperCase()
                                + mUserData.getFirst_name().substring(1).toLowerCase()
                                + " " + mUserData.getLast_name());
                        ((MainActivity) getContext()).updatePrefix(mData.getRelationship_details()
                                .getCurrent_Applicant_Details().getFirst_Name().substring(0, 1).toUpperCase());
                    } catch (Exception ex) {

                    }
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


    private void getHomeData() {
        Logger.errorLogger("Profile", SharedPref.getInstance().getString(TOKEN));
        Call<HomeDataInfo> call1 = mApiInterface.getHomePageData1("INDIVIDUAL", "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<HomeDataInfo>() {
            @Override
            public void onResponse(Call<HomeDataInfo> call, Response<HomeDataInfo> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        mReportHelper.setCompliance(response.body());
                        HomeDataInfo resource = response.body();
                        if (resource.getData().getMicroblogs().size() > 0) {
                            mMicroBlog = resource.getData().getMicroblogs();
                            MicroblogListAdapter adapter = new MicroblogListAdapter(mMicroBlog, getContext());
                            binding.recyclerViewMicroBlog.setVisibility(View.VISIBLE);
                            binding.recyclerViewMicroBlog.setAdapter(adapter);
                        } else {
                            binding.recyclerViewMicroBlog.setVisibility(View.GONE);
                        }

                        if (resource.getData().getExperianEmi().size() > 0) {
                            emiListAdapter = new EMIListAdapter(resource.getData().getExperianEmi(), getContext(), HomeFragment.this);
                            binding.llViewEmi.setVisibility(View.VISIBLE);
                            binding.llEmi.setVisibility(View.VISIBLE);
                            binding.recyclerViewEmi.setAdapter(emiListAdapter);
                            binding.llAddEmi.setVisibility(View.GONE);
                            if (resource.getData().getExperianEmi().size() > 3) {
                                binding.txvViewAllEmi.setVisibility(View.VISIBLE);
                            } else {
                                binding.txvViewAllEmi.setVisibility(View.GONE);
                            }
                        } else {
                            binding.llViewEmi.setVisibility(View.GONE);
                            binding.llEmi.setVisibility(View.GONE);
                            try {
                                if (mData.getTradelines() != null && mData.getTradelines().size() > 0) {
                                    binding.llAddEmi.setVisibility(View.VISIBLE);
                                } else {
                                    binding.llAddEmi.setVisibility(View.GONE);
                                }
                            } catch (Exception Ex) {

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

    public void click() {
        Fragment fragment = new SettingFragment();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        getHomeData();
        getProfileData();
    }

    @Override
    public void onEvent() {
        initMyAccount(0);
    }
}
