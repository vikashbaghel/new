package com.app.rupyz.ui.organization.profile.activity;

import static com.app.rupyz.generic.utils.AppConstant.IMAGE_UPLOAD_TAG;
import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityOrgAddAchievementBinding;
import com.app.rupyz.databinding.ActivityOrgAddTeamBinding;
import com.app.rupyz.databinding.ActivityOrgAddTestimonialBinding;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.profile.product.ProductList;
import com.app.rupyz.generic.model.profile.profileInfo.createTeam.CreateTeamRequest;
import com.app.rupyz.generic.model.profile.profileInfo.createTeam.CreateTeamResponse;
import com.app.rupyz.generic.model.profile.testimonial.createTestimonial.CreateTestimonialRequest;
import com.app.rupyz.generic.model.profile.testimonial.createTestimonial.CreateTestimonialResponse;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.utils.AppConstant;
import com.app.rupyz.generic.utils.ImageUtils;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.imageupload.ImageUploadBottomSheetDialogFragment;
import com.app.rupyz.ui.imageupload.ImageUploadListener;
import com.app.rupyz.ui.imageupload.ImageUploadViewModel;
import com.google.gson.JsonObject;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrgAddTestimonialActivity extends AppCompatActivity implements View.OnClickListener, ImageUploadListener {
    private ActivityOrgAddTestimonialBinding binding;
    private ImageUploadViewModel imageUploadViewModel;
    private EquiFaxApiInterface mEquiFaxApiInterface;
    private String strUserName, strContent, strDesignation, strOrganizationName, strRating, strId, image_url;
    private String uploadedPicId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrgAddTestimonialBinding.inflate(getLayoutInflater());
        imageUploadViewModel = new ViewModelProvider(this).get(ImageUploadViewModel.class);
        setContentView(binding.getRoot());
        mEquiFaxApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface.class);
        binding.btnAddTestimonial.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
        binding.imgClose.setOnClickListener(this);

        Intent i = getIntent();

        if (i != null) {
            strUserName = i.getStringExtra("user_name");
            strDesignation = i.getStringExtra("designation");
            strContent = i.getStringExtra("content");
            strOrganizationName = i.getStringExtra("organization");
            strRating = i.getStringExtra("rating");
            strId = i.getStringExtra("user_id");
            image_url = i.getStringExtra("image_url");
            uploadedPicId = i.getStringExtra("image_id");
        }

        if (!StringUtils.isBlank(strUserName)) {
            binding.edtUserName.setText(strUserName);
        }
        if (!StringUtils.isBlank(strDesignation)) {
            binding.edtDesignation.setText(strDesignation);
        }
        if (!StringUtils.isBlank(strContent)) {
            binding.edtContent.setText(strContent);
        }
        if (!StringUtils.isBlank(strOrganizationName)) {
            binding.edtCompany.setText(strOrganizationName);
        }
        if (!StringUtils.isBlank(strRating)) {
            binding.ratingBar.setRating(Float.valueOf(strRating).floatValue());
        }
        if (getIntent() != null && getIntent().hasExtra(AppConstant.EDIT_TESTIMONIAL)) {
            binding.btnAddTestimonial.setText("Update");
            binding.tvToolbarTitle.setText("Update Testimonial");
            binding.imgDelete.setVisibility(View.VISIBLE);
        }

        binding.imgDelete.setOnClickListener(this);
        binding.ivCameraEdit.setOnClickListener(this);

        initObservers();
        initLayout();
    }

    private void initLayout() {
        if (!StringUtils.isBlank(image_url)) {
            ImageUtils.INSTANCE.loadTeamImage(image_url, binding.ivTeamMemberDp);
        }
        binding.ivTeamMemberDp.setOnClickListener(v ->
                ImageUploadBottomSheetDialogFragment.newInstance(this)
                        .show(getSupportFragmentManager(), IMAGE_UPLOAD_TAG));

        binding.ivCameraEdit.setOnClickListener(v ->
                ImageUploadBottomSheetDialogFragment.newInstance(this)
                        .show(getSupportFragmentManager(), IMAGE_UPLOAD_TAG));
    }

    private void initObservers() {
        imageUploadViewModel.getCredLiveData().observe(this, genericResponseModel -> {
            if (genericResponseModel.getData() != null && genericResponseModel.getData().getId() != null) {
                uploadedPicId = genericResponseModel.getData().getId();
            }
            Toast.makeText(this, genericResponseModel.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_close:
            case R.id.btn_cancel:
                onBackPressed();
                break;
            case R.id.btn_add_testimonial:
                if (validate()) {
                    addTestimonial();
                }
                break;
            case R.id.img_delete:
                showDeleteDialog();
                break;

        }
    }

    private void showDeleteDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_alert_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView txvDelete = (TextView) dialog.findViewById(R.id.txv_delete);
        TextView txvCancel = (TextView) dialog.findViewById(R.id.txv_cancel);
        txvDelete.setOnClickListener(v -> {
            dialog.dismiss();
            deleteTestimonials();
        });
        txvCancel.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void deleteTestimonials() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", Integer.parseInt(strId));
        Call<CreateTestimonialResponse> call = mEquiFaxApiInterface.deleteTestimonial(
                SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN), jsonObject);

        call.enqueue(new Callback<CreateTestimonialResponse>() {
            @Override
            public void onResponse(Call<CreateTestimonialResponse> call, Response<CreateTestimonialResponse> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());
                    CreateTestimonialResponse testimonialResponse = response.body();
                    Toast.makeText(OrgAddTestimonialActivity.this, testimonialResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra(AppConstant.TESTIMONIAL_INFO, testimonialResponse);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(OrgAddTestimonialActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateTestimonialResponse> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }


    private void addTestimonial() {

        CreateTestimonialRequest createTestimonialRequest = new CreateTestimonialRequest();
        if (!StringUtils.isBlank(strUserName)) {
            createTestimonialRequest.setUserName(strUserName);
        } else {
            createTestimonialRequest.setUserName("");
        }

        if (!StringUtils.isBlank(strDesignation)) {
            createTestimonialRequest.setPosition(strDesignation);
        } else {
            createTestimonialRequest.setPosition("");
        }

        if (!StringUtils.isBlank(strOrganizationName)) {
            createTestimonialRequest.setCompany(strOrganizationName);
        } else {
            createTestimonialRequest.setCompany("");
        }

        if (!StringUtils.isBlank(strContent)) {
            createTestimonialRequest.setContent(strContent);
        } else {
            createTestimonialRequest.setCompany("");
        }

        if (!StringUtils.isBlank(strRating)) {
            createTestimonialRequest.setRating(binding.ratingBar.getRating());
        } else {
            createTestimonialRequest.setRating(0.0);
        }

        if (!StringUtils.isBlank(strId)) {
            createTestimonialRequest.setId(Integer.parseInt(strId));
        }

        createTestimonialRequest.setPublished(true);
        if (uploadedPicId != null) {
            createTestimonialRequest.setUserPic(Integer.parseInt(uploadedPicId));
        }

        Call<CreateTestimonialResponse> call = mEquiFaxApiInterface.addTestimonial(
                SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN), createTestimonialRequest);
        call.enqueue(new Callback<CreateTestimonialResponse>() {
            @Override
            public void onResponse(Call<CreateTestimonialResponse> call, Response<CreateTestimonialResponse> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());
                    CreateTestimonialResponse response1 = response.body();
                    Toast.makeText(OrgAddTestimonialActivity.this, response1.getMessage(), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    Toast.makeText(OrgAddTestimonialActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateTestimonialResponse> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    private boolean validate() {
        boolean temp = true;
        strUserName = binding.edtUserName.getText().toString();
        strDesignation = binding.edtDesignation.getText().toString();
        strOrganizationName = binding.edtCompany.getText().toString();
        strContent = binding.edtContent.getText().toString();
        strRating = binding.ratingBar.getRating() + "";
        if (StringUtils.isBlank(strUserName)) {
            Toast.makeText(OrgAddTestimonialActivity.this, "User Name Required", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strDesignation)) {
            Toast.makeText(OrgAddTestimonialActivity.this, "Designation Required", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strOrganizationName)) {
            Toast.makeText(OrgAddTestimonialActivity.this, "Organization Name Required", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strContent)) {
            Toast.makeText(OrgAddTestimonialActivity.this, "Description Required", Toast.LENGTH_SHORT).show();
            temp = false;
        }
        return temp;
    }

    @Override
    public void onCameraUpload(@Nullable String fileName) {
        imageUploadViewModel.uploadCredentials(fileName);
        binding.ivTeamMemberDp.setImageURI(Uri.fromFile(new File(fileName)));
    }

    @Override
    public void onGalleryUpload(@Nullable String fileName) {
        imageUploadViewModel.uploadCredentials(fileName);
        binding.ivTeamMemberDp.setImageURI(Uri.fromFile(new File(fileName)));
    }
}