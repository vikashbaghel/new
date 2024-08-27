package com.app.rupyz.ui.equifax.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.app.rupyz.databinding.EquifaxCreditScoreInfoSheetBinding;
import com.app.rupyz.databinding.EquifaxScoreInsightsInfoSheetBinding;
import com.app.rupyz.databinding.ScoreInsightsInfoSheetBinding;
import com.app.rupyz.generic.utils.DummyChartData;
import com.github.mikephil.charting.components.Legend;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class EquiFaxScoreInsightInfoModal extends BottomSheetDialogFragment {

    EquifaxScoreInsightsInfoSheetBinding binding;
    private String mRemark;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRemark = this.getArguments().getString("data");

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        //Set the custom view
        binding = EquifaxScoreInsightsInfoSheetBinding.inflate(getLayoutInflater());
        binding.scoreInsightsGraph.setDrawHoleEnabled(true);
        binding.scoreInsightsGraph.setUsePercentValues(false);
        binding.scoreInsightsGraph.setEntryLabelTextSize(12);
        binding.scoreInsightsGraph.setEntryLabelColor(Color.BLACK);
        binding.scoreInsightsGraph.setCenterText("");
        binding.scoreInsightsGraph.setCenterTextSize(18);
        binding.scoreInsightsGraph.setRotationAngle(90);
        binding.scoreInsightsGraph.setMinAngleForSlices(0);
        binding.scoreInsightsGraph.getDescription().setEnabled(false);
        binding.scoreInsightsGraph.setRotationEnabled(false);
        binding.scoreInsightsGraph.setTouchEnabled(false);
        Legend l = binding.scoreInsightsGraph.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
        new DummyChartData().loadPieChartScoreInsightsData(binding.scoreInsightsGraph);
        try {
            binding.btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EquiFaxScoreInsightInfoModal.this.dismiss();
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