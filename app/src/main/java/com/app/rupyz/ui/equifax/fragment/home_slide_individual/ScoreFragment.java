package com.app.rupyz.ui.equifax.fragment.home_slide_individual;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.app.rupyz.R;
import com.app.rupyz.databinding.RiskLayoutScoreBinding;
import com.app.rupyz.generic.helper.DateFormatHelper;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.model.organization.individual.EquiFaxIndividualInfoModel;
import com.app.rupyz.ui.equifax.bottomsheet.EquifaxCreditScoreInfoModal;
import com.app.rupyz.ui.home.dailog.score_insights.RefreshRemarkModal;


public class ScoreFragment extends Fragment {

    RiskLayoutScoreBinding binding;
    private EquiFaxReportHelper mReportHelper;
    public EquiFaxIndividualInfoModel mData;
    private boolean visible;

    public ScoreFragment(boolean visible) {
        this.visible=visible;
    }

    public ScoreFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = RiskLayoutScoreBinding.inflate(getLayoutInflater());
        mReportHelper = EquiFaxReportHelper.getInstance();
        mData = mReportHelper.getRetailReport();
        initLayout();
        return binding.getRoot();
    }

    private void initLayout() {
        if(visible){
            binding.btnMore.setVisibility(View.VISIBLE);
            binding.imgBtnShareCard.setVisibility(View.VISIBLE);
        }
        else {
            binding.btnMore.setVisibility(View.GONE);
            binding.imgBtnShareCard.setVisibility(View.GONE);
        }

        binding.imgCreditBureau.setImageDrawable(getResources().getDrawable(R.mipmap.ic_equifax_logo));
        binding.scoreSeekBar.setEnabled(false);
        binding.txtCreditAge.setText("Credit Age: " + mData.getReport().getCredit_age());
        binding.txtProfileDate.setText(DateFormatHelper.getProfileDate(
                mData.getReport().getUpdated_at()
        ));
        binding.btnScoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initCreditScoreInfoSheet();
            }
        });
        binding.scoreValue.setText(mData.getReport().getScore_value() + "");
        binding.scoreSeekBar.setProgress(mData.getReport().getScore_value());
        binding.txtScorePauseDays.setText("Report update in "
                + mData.getMetadata().getDays_remaining() + " days");

        binding.btnCheckScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mData.getMetadata().getDays_remaining() <= 0) {
//                    initEquifax();
                    binding.btnCheckScore.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.VISIBLE);
                } else {
                    initBottomSheet(mData.getMetadata().getDays_remaining() + "");
                }

            }
        });

        if (mData.getReport().getScore_comment().equalsIgnoreCase("Low Risk")) {
            binding.riskType.setText(getResources().getString(R.string.low_risk));
            binding.riskType.setTextColor(getResources().getColor(R.color.light_green));
        } else if (mData.getReport().getScore_comment().equalsIgnoreCase("High Risk")) {
            binding.riskType.setText(getResources().getString(R.string.high_risk));
            binding.riskType.setTextColor(getResources().getColor(R.color.light_red));
        } else if (mData.getReport().getScore_comment().equalsIgnoreCase("Very Low Risk")) {
            binding.riskType.setText(getResources().getString(R.string.very_low_risk));
            binding.riskType.setTextColor(getResources().getColor(R.color.light_green));
        } else if (mData.getReport().getScore_comment().equalsIgnoreCase("Medium Risk")) {
            binding.riskType.setText(getResources().getString(R.string.medium_risk));
            binding.riskType.setTextColor(getResources().getColor(R.color.yellow));
        }
        initScoreValue();
        try {
            float score_percentage = ((mData.getReport().getScore_value() - 300) * 100) / 600;

            int score = mData.getReport().getScore_value();
            if (score >= 300 && score <= 650) {
                binding.scoreScale.setText("Poor");
                score_percentage = (((mData.getReport().getScore_value() - 300) * 100) / 600);
            } else if (score >= 651 && score <= 770) {
                binding.scoreScale.setText("Average");
                score_percentage = (((mData.getReport().getScore_value() - 300) * 100) / 600);
            } else if (score >= 771 && score <= 850) {
                binding.scoreScale.setText("Good");
                score_percentage = (((mData.getReport().getScore_value() - 300) * 100) / 600);
            } else if (score >= 851 && score <= 900) {
                binding.scoreScale.setText("Excellent ");
                score_percentage = ((mData.getReport().getScore_value() - 300) * 100) / 600;
            }
            LinearLayout.LayoutParams scorePointerLayout = (LinearLayout.LayoutParams)
                    binding.scorePointerLayout.getLayoutParams();
            scorePointerLayout.weight = score_percentage;
            binding.scorePointerLayout.setLayoutParams(scorePointerLayout);

            if (score > 850) {
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

    private void initScoreValue() {
        try {
            if (mData.getReport().getGraph_data().getScore().size() > 1) {
                int current_score = mData.getReport().getGraph_data().getScore().get(mData.getReport().getGraph_data().getScore().size() - 1);
                int previous_score = mData.getReport().getGraph_data().getScore().get(mData.getReport().getGraph_data().getScore().size() - 2);
                if (current_score > previous_score) {
                    binding.scoreDownValue.setText((current_score - previous_score) + "");
                    binding.scoreDownValue.setTextColor(getActivity().getResources().getColor(R.color.light_green));
                    binding.scoreDownImage.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.ic_score_up));
                } else {
                    binding.scoreDownValue.setText((previous_score - current_score) + "");
                    binding.scoreDownValue.setTextColor(getActivity().getResources().getColor(R.color.light_red));
                    binding.scoreDownImage.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.ic_score_down));
                }
            }
        } catch (Exception ex) {

        }
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
        EquifaxCreditScoreInfoModal fragment = new EquifaxCreditScoreInfoModal();
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(), "");
    }
}
