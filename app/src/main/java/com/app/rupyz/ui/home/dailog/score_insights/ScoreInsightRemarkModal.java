package com.app.rupyz.ui.home.dailog.score_insights;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ExperianConsentSheetBinding;
import com.app.rupyz.databinding.ScoreInsightsRemarkSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ScoreInsightRemarkModal extends BottomSheetDialogFragment {

    ScoreInsightsRemarkSheetBinding binding;
    private String mRemark;
    private String mType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRemark = this.getArguments().getString("data");
        mType = this.getArguments().getString("type");
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        //Set the custom view
        binding = ScoreInsightsRemarkSheetBinding.inflate(getLayoutInflater());
        binding.txtRemark.setText(mRemark);
        if (mType.equalsIgnoreCase(getResources().getString(R.string.poor))) {
            binding.imgScoreInsight.setImageDrawable(getResources().getDrawable(R.mipmap.ic_score_insight_poor));
            binding.progressBar.setBackground(
                    getResources().getDrawable(R.drawable.score_insights_poor_style));
        } else if (mType.equalsIgnoreCase(getResources().getString(R.string.good))) {
            binding.imgScoreInsight.setImageDrawable(getResources().getDrawable(R.mipmap.ic_score_insight_good));
            binding.progressBar.setBackground(
                    getResources().getDrawable(R.drawable.score_insights_good_style));
        } else if (mType.equalsIgnoreCase(getResources().getString(R.string.average))) {
            binding.imgScoreInsight.setImageDrawable(getResources().getDrawable(R.mipmap.ic_score_insight_average));
            binding.progressBar.setBackground(
                    getResources().getDrawable(R.drawable.score_insights_average_style));
        } else if (mType.equalsIgnoreCase(getResources().getString(R.string.excellent))) {
            binding.imgScoreInsight.setImageDrawable(getResources().getDrawable(R.mipmap.ic_score_insight_excellent));
            binding.progressBar.setBackground(
                    getResources().getDrawable(R.drawable.score_insights_excellent_style));
        } else {
            binding.imgScoreInsight.setImageDrawable(getResources().getDrawable(R.mipmap.ic_score_insight_excellent));
            binding.progressBar.setBackground(

            getResources().getDrawable(R.drawable.score_insights_excellent_style));
        }
        try {
            binding.btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ScoreInsightRemarkModal.this.dismiss();
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