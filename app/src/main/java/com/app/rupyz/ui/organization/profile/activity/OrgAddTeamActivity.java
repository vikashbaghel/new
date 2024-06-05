package com.app.rupyz.ui.organization.profile.activity;

import static com.app.rupyz.generic.utils.AppConstant.IMAGE_UPLOAD_TAG;
import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityOrgAddTeamBinding;
import com.app.rupyz.generic.helper.UrlValidationHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.profile.profileInfo.createTeam.CreateTeamRequest;
import com.app.rupyz.generic.model.profile.profileInfo.createTeam.CreateTeamResponse;
import com.app.rupyz.generic.model.profile.profileInfo.createTeam.SocialLinks;
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

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrgAddTeamActivity extends AppCompatActivity implements View.OnClickListener, ImageUploadListener {
    private ActivityOrgAddTeamBinding binding;
    private EquiFaxApiInterface mEquiFaxApiInterface;
    private String strName, strDesignation, strIntroduction,
            strLinkedin, strUserPic;
    private Integer strId;
    private ImageUploadViewModel imageUploadViewModel;

    private String uploadedPicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrgAddTeamBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mEquiFaxApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface.class);
        imageUploadViewModel = new ViewModelProvider(this).get(ImageUploadViewModel.class);
        binding.btnAddUser.setOnClickListener(this);
        binding.imgClose.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(AppConstant.EDIT_TEAM)) {
            strName = intent.getStringExtra("user_name");
            strLinkedin = intent.getStringExtra("social_link");
            strDesignation = intent.getStringExtra("user_destination");
            strIntroduction = intent.getStringExtra("user_description");
            strId = intent.getIntExtra("user_id", 0);
            strUserPic = intent.getStringExtra("user_pic");
            uploadedPicId = intent.getStringExtra("profile_pic_id");

        }

        if (getIntent() != null && getIntent().hasExtra(AppConstant.EDIT_TEAM)) {
            binding.btnAddUser.setText("Update");
            binding.tvToolbarTitle.setText("Update Team");
        }
        binding.edtLinkedinLink.setText(strLinkedin);
        if (!StringUtils.isBlank(strName)) {
            binding.edtUserName.setText(strName);
        }

        if (!StringUtils.isBlank(strUserPic)) {
            ImageUtils.INSTANCE.loadImage(strUserPic, binding.ivTeamMemberDp);
        }

        if (!StringUtils.isBlank(strDesignation)) {
            binding.edtDestination.setText(strDesignation);
        }
        if (!StringUtils.isBlank(strIntroduction)) {
            binding.edtIntroduction.setText(strIntroduction);
        }
        binding.ivTeamMemberDp.setOnClickListener(v -> {
            ImageUploadBottomSheetDialogFragment.newInstance(this).show(getSupportFragmentManager(), IMAGE_UPLOAD_TAG);
        });

        initObservers();
    }

    private void initObservers() {
        imageUploadViewModel.getCredLiveData().observe(this, genericResponseModel -> {
            if (genericResponseModel.getData() != null && genericResponseModel.getData().getId() != null) {
                binding.progressBar.setVisibility(View.GONE);
                uploadedPicId = genericResponseModel.getData().getId();
            }

            Toast.makeText(this, genericResponseModel.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_user:
                if (validate()) {
                    addUser();
                }
                break;
            case R.id.btn_cancel:
            case R.id.img_close:
                onBackPressed();
                break;
        }
    }


    private void addUser() {
        CreateTeamRequest createTeamRequest = new CreateTeamRequest();
        if (createTeamRequest.getSocialLinks() != null) {
            SocialLinks socialLinks = createTeamRequest.getSocialLinks();
            socialLinks.setLinkedin(binding.edtLinkedinLink.getText().toString());
        } else {
            SocialLinks socialLinks = new SocialLinks();
            socialLinks.setLinkedin(binding.edtLinkedinLink.getText().toString().replaceAll("\\s", ""));
            createTeamRequest.setSocialLinks(socialLinks);
        }
        if (!StringUtils.isBlank(strIntroduction)) {
            createTeamRequest.setIntro(strIntroduction);
        } else {
            createTeamRequest.setIntro("");
        }
        if (!StringUtils.isBlank(strName)) {
            createTeamRequest.setName(strName);
        } else {
            createTeamRequest.setName("");
        }

        if (!StringUtils.isBlank(strDesignation)) {
            createTeamRequest.setPosition(strDesignation);
        } else {
            createTeamRequest.setPosition("");
        }
        if (uploadedPicId != null && !uploadedPicId.equals("")) {
            createTeamRequest.setProfilePic(Integer.parseInt(uploadedPicId));
        }

        if (strId != null && strId != 0) {
            createTeamRequest.setId(strId);
        }


        Call<CreateTeamResponse> call = mEquiFaxApiInterface.createTeamMember(
                SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN), createTeamRequest);
        call.enqueue(new Callback<CreateTeamResponse>() {
            @Override
            public void onResponse(Call<CreateTeamResponse> call, Response<CreateTeamResponse> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());
                    CreateTeamResponse response1 = response.body();
                    Toast.makeText(OrgAddTeamActivity.this, response1.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra(AppConstant.TEAM_INFO, response1.getData());
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(OrgAddTeamActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateTeamResponse> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                Toast.makeText(OrgAddTeamActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }

    private boolean validate() {
        boolean temp = true;
        strName = binding.edtUserName.getText().toString();
        strDesignation = binding.edtDestination.getText().toString();
        strIntroduction = binding.edtIntroduction.getText().toString();
        strLinkedin = binding.edtLinkedinLink.getText().toString();
        if (StringUtils.isBlank(strName)) {
            Toast.makeText(OrgAddTeamActivity.this, "First Name Required", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strDesignation)) {
            Toast.makeText(OrgAddTeamActivity.this, "Designation Required", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strIntroduction)) {
            Toast.makeText(OrgAddTeamActivity.this, "Introduction Required", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (!StringUtils.isBlank(strLinkedin)) {
            if (!UrlValidationHelper.INSTANCE.isValidUrl(strLinkedin)) {
                Toast.makeText(OrgAddTeamActivity.this, "Invalid Linkedin Profile Link.", Toast.LENGTH_SHORT).show();
                temp = false;
            }
        }
        return temp;
    }

    @Override
    public void onCameraUpload(@Nullable String fileName) {
        imageUploadViewModel.uploadCredentials(fileName);
        binding.ivTeamMemberDp.setImageURI(Uri.fromFile(new File(fileName)));
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGalleryUpload(@Nullable String fileName) {
        imageUploadViewModel.uploadCredentials(fileName);
        binding.ivTeamMemberDp.setImageURI(Uri.fromFile(new File(fileName)));
        binding.progressBar.setVisibility(View.VISIBLE);
    }
}