package com.app.rupyz.ui.organization.onboarding.sheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.app.rupyz.databinding.ClaimProfileSheetBinding;
import com.app.rupyz.databinding.EquifaxScoreInsightsInfoSheetBinding;
import com.app.rupyz.generic.utils.DummyChartData;
import com.app.rupyz.ui.equifax.dailog.EquifaxIndividualAddEMIDetailSheet;
import com.app.rupyz.ui.organization.PANVerifyActivity;
import com.app.rupyz.ui.organization.onboarding.activity.BusinessDetailsActivity;
import com.github.mikephil.charting.components.Legend;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ClaimProfileSheet extends BottomSheetDialogFragment {

    ClaimProfileSheetBinding binding;
    private String mRemark;
    private Context mContext;
    private String legalName = "";

    public ClaimProfileSheet(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        legalName = this.getArguments().getString("legal_name");
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        //Set the custom view
        binding = ClaimProfileSheetBinding.inflate(getLayoutInflater());
        binding.txtOrgLegalName.setText(legalName);
        binding.btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BusinessDetailsActivity) mContext).claimProfile(true);
                ClaimProfileSheet.this.dismiss();
            }
        });
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BusinessDetailsActivity) mContext).claimProfile(false);
                ClaimProfileSheet.this.dismiss();
            }
        });
        try {
            binding.btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((BusinessDetailsActivity) mContext).claimProfile(false);
                    ClaimProfileSheet.this.dismiss();
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