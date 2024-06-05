package com.app.rupyz.ui.common;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityUrlImageViewBinding;
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileDetail;
import com.app.rupyz.generic.model.profile.profileInfo.createTeam.TeamInfoModel;
import com.app.rupyz.generic.utils.AppConstant;
import com.app.rupyz.generic.utils.ImageUtils;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.organization.profile.activity.OrgViewModel;
import com.squareup.picasso.Picasso;

import java.io.File;

public class UrlImageViewActivity extends AppCompatActivity {
    ActivityUrlImageViewBinding binding;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private OrgViewModel orgViewModel;
    private String imageType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUrlImageViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orgViewModel = new ViewModelProvider(this).get(OrgViewModel.class);

        if (getIntent() != null) {
            if (getIntent().hasExtra(AppConstant.IMAGE_PREVIEW)) {
                binding.ivDelete.setVisibility(View.VISIBLE);
            } else {
                binding.ivDelete.setVisibility(View.GONE);
            }

            if (getIntent().hasExtra(AppConstant.IMAGE_TYPE)) {
                imageType = getIntent().getStringExtra(AppConstant.IMAGE_TYPE);
            }
        }

        initLayout();
        initToolbar();
        initObservers();

        binding.ivDelete.setOnClickListener(view -> {
            showDeleteDialog();
        });
    }


    private void initObservers() {

        orgViewModel.getLiveData().observe(this, orgProfileInfoModel -> {
            if (orgProfileInfoModel.getData().getLogo_image_url() == null) {
                ImageUtils.INSTANCE.loadTeamImage("", binding.imageView);
            }
            if (orgProfileInfoModel.getData().getBanner_image_url() == null) {
                ImageUtils.INSTANCE.loadBannerImage("", binding.imageView);
            }
        });
    }

    private void showDeleteDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_alert_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView txvDelete = dialog.findViewById(R.id.txv_delete);
        TextView txvCancel = dialog.findViewById(R.id.txv_cancel);
        txvDelete.setOnClickListener(v -> {
            OrgProfileDetail model = new OrgProfileDetail();

            if (imageType != null && imageType.equals(AppConstant.IMAGE_TYPE_BANNER)) {
                model.setBannerImage(0);
            } else {
                model.setLogoImage(0);
            }
            orgViewModel.updateInfo(model);
            dialog.dismiss();
        });
        txvCancel.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void initLayout() {
        try {
            mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
            if (imageType.equals(AppConstant.IMAGE_TYPE_BANNER)) {
                ImageUtils.INSTANCE.loadBannerImage(getIntent().getExtras().getString(AppConstant.IMAGE_URL), binding.imageView);
            } else {
                ImageUtils.INSTANCE.loadTeamImage(getIntent().getExtras().getString(AppConstant.IMAGE_URL), binding.imageView);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    // this redirects all touch events in the activity to the gesture detector
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mScaleGestureDetector.onTouchEvent(event);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        // when a scale gesture is detected, use it to resize the image
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            binding.imageView.setScaleX(mScaleFactor);
            binding.imageView.setScaleY(mScaleFactor);
            return true;
        }
    }

    private void initToolbar() {
        setSupportActionBar(binding.toolbar);
        if (imageType.equals(AppConstant.IMAGE_TYPE_BANNER)) {
            getSupportActionBar().setTitle("Banner Image");
        } else {
            getSupportActionBar().setTitle("Profile Image");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}