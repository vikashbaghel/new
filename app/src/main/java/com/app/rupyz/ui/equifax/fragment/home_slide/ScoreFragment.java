package com.app.rupyz.ui.equifax.fragment.home_slide;

import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.app.rupyz.MainActivity;
import com.app.rupyz.R;
import com.app.rupyz.databinding.EquifaxRiskLayoutScoreBinding;
import com.app.rupyz.databinding.RiskLayoutScoreBinding;
import com.app.rupyz.generic.helper.DateFormatHelper;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.organization.EquiFaxInfoModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.common.CustomProgressDialog;
import com.app.rupyz.ui.equifax.EquiFaxMainActivity;
import com.app.rupyz.ui.equifax.bottomsheet.EquifaxCommercialCreditScoreInfoModal;
import com.app.rupyz.ui.home.dailog.score_insights.RefreshRemarkModal;
import com.app.rupyz.ui.organization.EquiFaxOtpActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScoreFragment extends Fragment {

    EquifaxRiskLayoutScoreBinding binding;
    private EquiFaxApiInterface mApiInterface;
    private EquiFaxReportHelper mReportHelper;
    public EquiFaxInfoModel mData;
    private boolean visible;

    public ScoreFragment(boolean visible) {
        this.visible = visible;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EquifaxRiskLayoutScoreBinding.inflate(getLayoutInflater());
        mApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface.class);
        mReportHelper = EquiFaxReportHelper.getInstance();
        mData = mReportHelper.getCommercialReport();
        initLayout();
        return binding.getRoot();
    }

    private void initLayout() {

       /* if ((mData.getMetadata().getCommercial_progress_step() + "").equalsIgnoreCase("3.1")) {
            binding.txvNoReport.setVisibility(View.VISIBLE);
            binding.rlCommercialRiskLayout.setVisibility(View.GONE);
        } else if ((mData.getMetadata().getCommercial_progress_step() + "").equalsIgnoreCase("2.2")) {
            binding.txvNoReport.setVisibility(View.VISIBLE);
            binding.rlCommercialRiskLayout.setVisibility(View.GONE);
        } else if ((mData.getMetadata().getCommercial_progress_step() + "").equalsIgnoreCase("3.0")) {
            binding.txvNoReport.setVisibility(View.GONE);
            binding.rlCommercialRiskLayout.setVisibility(View.VISIBLE);
            //initResponseData();
        } else {
            binding.rlCommercialRiskLayout.setVisibility(View.VISIBLE);
            binding.txvNoReport.setVisibility(View.GONE);
        }*/


        if (visible) {
            binding.btnMore.setVisibility(View.VISIBLE);
            binding.imgBtnCommercialShareCard.setVisibility(View.VISIBLE);
        } else {
            binding.btnMore.setVisibility(View.GONE);
            binding.imgBtnCommercialShareCard.setVisibility(View.GONE);
        }
//        binding.scoreSeekBar.setEnabled(false);
        binding.txtCreditAge.setText("Credit Age: " + mData.getReport().getCreditAge());
        binding.txtProfileDate.setText(DateFormatHelper.getProfileDate(
                mData.getReport().getUpdatedAt()
        ));
        binding.scoreValue.setText(mData.getReport().getScoreValue() + "");
//        binding.scoreSeekBar.setProgress(mData.getReport().getScoreValue());


        binding.btnCheckScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mData.getMetadata().getDays_remaining() <= 0) {
                    initEquifax();
                    binding.btnCheckScore.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.VISIBLE);
                } else {
                    initBottomSheet(mData.getMetadata().getDays_remaining() + "");
                }

            }
        });

        binding.riskLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //((EquiFaxMainActivity)getActivity()).openCommercial();
            }
        });


        binding.btnScoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initCreditScoreInfoSheet();
            }
        });


        if (mData.getReport().getScoreComment().equalsIgnoreCase("Low Risk")) {
            binding.riskType.setText(getResources().getString(R.string.low_risk));
            binding.riskType.setTextColor(getResources().getColor(R.color.light_green));
        } else if (mData.getReport().getScoreComment().equalsIgnoreCase("High Risk")) {
            binding.riskType.setText(getResources().getString(R.string.high_risk));
            binding.riskType.setTextColor(getResources().getColor(R.color.light_red));
        } else if (mData.getReport().getScoreComment().equalsIgnoreCase("Very High Risk")) {
            binding.riskType.setText(getResources().getString(R.string.very_high_risk));
            binding.riskType.setTextColor(getResources().getColor(R.color.light_red));
        } else if (mData.getReport().getScoreComment().equalsIgnoreCase("Medium Risk")) {
            binding.riskType.setText(getResources().getString(R.string.medium_risk));
            binding.riskType.setTextColor(getResources().getColor(R.color.yellow));
        }
        try {
            float score_percentage = ((mData.getReport().getScoreValue()) * 10);

            int score = mData.getReport().getScoreValue();
            if (score >= 1 && score <= 2) {
                binding.scoreScale.setText("Excellent");
                score_percentage = (((mData.getReport().getScoreValue()) * 10));
            } else if (score >= 3 && score <= 4) {
                binding.scoreScale.setText("Good");
                score_percentage = (((mData.getReport().getScoreValue()) * 10));
            } else if (score >= 5 && score <= 7) {
                binding.scoreScale.setText("Average");
                score_percentage = (((mData.getReport().getScoreValue()) * 10));
            } else if (score >= 8 && score <= 10) {
                binding.scoreScale.setText("Poor ");
                score_percentage = ((mData.getReport().getScoreValue()) * 10);
            }
            LinearLayout.LayoutParams scorePointerLayout = (LinearLayout.LayoutParams)
                    binding.scorePointerLayout.getLayoutParams();
            scorePointerLayout.weight = score_percentage;
            binding.scorePointerLayout.setLayoutParams(scorePointerLayout);

            if (score >= 8) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) binding.thumb.getLayoutParams();
                lp.gravity = Gravity.RIGHT;
                binding.thumb.setLayoutParams(lp);
            }


            LinearLayout.LayoutParams scorePointerSpaceLayout = (LinearLayout.LayoutParams)
                    binding.scorePointerSpaceLayout.getLayoutParams();
            scorePointerSpaceLayout.weight = 100 - score_percentage;
            binding.scorePointerSpaceLayout.setLayoutParams(scorePointerSpaceLayout);

        } catch (Exception ex) {

        }
    }

    private void initEquifax() {
        Call<String> call1 = mApiInterface.initEquiFaxOtp(SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                binding.btnCheckScore.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                Logger.errorLogger(this.getClass().getName(), response.code() + "");
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body());
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    Gson gson = new Gson();
                    mData = gson.fromJson(jsonObj.get("data"), EquiFaxInfoModel.class);
                    Intent intent = new Intent(getActivity(), EquiFaxOtpActivity.class);
                    intent.putExtra("org_id", "2");
                    intent.putExtra("is_otp", false);
                    startActivity(intent);
                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        Logger.errorLogger(this.getClass().getName(), responseBody + "");
                        new SessionHelper(getActivity()).requestMessage(
                                responseBody);
                    } catch (Exception ex) {
                        Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
                    }
                }
                try {
                    Logger.errorLogger(this.getClass().getName(), response.errorBody().string() + "");
                } catch (Exception ex) {
                    Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
                }
                Logger.errorLogger(this.getClass().getName(), response.body() + "");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                binding.btnCheckScore.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    public void initBottomSheet(String days) {
        Bundle bundle = new Bundle();
        bundle.putString("days", days);
        RefreshRemarkModal fragment = new RefreshRemarkModal();
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(), "");
    }

    public void initCreditScoreInfoSheet() {
        Bundle bundle = new Bundle();
        EquifaxCommercialCreditScoreInfoModal fragment = new EquifaxCommercialCreditScoreInfoModal();
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(), "");
    }
}
