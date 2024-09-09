package com.app.rupyz.ui.organization.profile.activity;

import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityOrgAddSocialLinkBinding;
import com.app.rupyz.generic.helper.UrlValidationHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileInfoModel;
import com.app.rupyz.generic.model.profile.profileInfo.createProfile.CreateProfileInfoModel;
import com.app.rupyz.generic.model.profile.profileInfo.createProfile.SocialMedia;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.generic.utils.Utility;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrgAddSocialLink extends AppCompatActivity implements View.OnClickListener {

    private ActivityOrgAddSocialLinkBinding binding;
    private EquiFaxApiInterface mEquiFaxApiInterface;
    private String facebook, twitter, instagram, linkedin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrgAddSocialLinkBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mEquiFaxApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface.class);
        initLayout();
        initData();
    }

    private void initLayout() {
        binding.btnAddUser.setOnClickListener(this);
        binding.imgClose.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
    }

    private void initData() {
        Intent i = getIntent();
        if (i != null) {
            facebook = i.getStringExtra("facebook");
            twitter = i.getStringExtra("twitter");
            instagram = i.getStringExtra("instagram");
            linkedin = i.getStringExtra("linkedin");
        }
        binding.facebook.setText(facebook);
        binding.twitter.setText(twitter);
        binding.instagram.setText(instagram);
        binding.linkedin.setText(linkedin);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_user:
                validateUrl();
                break;
            case R.id.img_close:
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    private void validateUrl() {
        if (!binding.facebook.getText().toString().equals("") && !UrlValidationHelper.INSTANCE.isValidUrl(binding.facebook.getText().toString())) {
            Toast.makeText(this, "Please enter valid facebook url", Toast.LENGTH_SHORT).show();
            return;
        }
//        if (!binding.instagram.getText().toString().equals("") && !UrlValidationHelper.INSTANCE.isValidUrl(binding.instagram.getText().toString())) {
//            Toast.makeText(this, "Please enter valid instagram url", Toast.LENGTH_SHORT).show();
//            return;
//        }
        if (!binding.linkedin.getText().toString().equals("") && !UrlValidationHelper.INSTANCE.isValidUrl(binding.linkedin.getText().toString())) {
            Toast.makeText(this, "Please enter valid linkedin url", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!binding.twitter.getText().toString().equals("") && !UrlValidationHelper.INSTANCE.isValidUrl(binding.twitter.getText().toString())) {
            Toast.makeText(this, "Please enter valid twitter url", Toast.LENGTH_SHORT).show();
        } else {
            addSocialLinks();
        }
    }

    private void addSocialLinks() {
        CreateProfileInfoModel createProfileInfoModel = new CreateProfileInfoModel();
        if (createProfileInfoModel.getSocialMedia() != null) {
            SocialMedia socialMedia = createProfileInfoModel.getSocialMedia();
            socialMedia.setFacebook(binding.facebook.getText().toString().replaceAll("\\s", ""));
            socialMedia.setInstagram(binding.instagram.getText().toString().replaceAll("\\s", ""));
            socialMedia.setTwitter(binding.twitter.getText().toString().replaceAll("\\s", ""));
            socialMedia.setLinkedin(binding.linkedin.getText().toString().replaceAll("\\s", ""));
            Logger.errorLogger("profileJson2", new Gson().toJson(createProfileInfoModel));
        } else {
            SocialMedia socialMedia = new SocialMedia();
            socialMedia.setFacebook(binding.facebook.getText().toString().replaceAll("\\s", ""));
            socialMedia.setInstagram(binding.instagram.getText().toString().replaceAll("\\s", ""));
            socialMedia.setTwitter(binding.twitter.getText().toString().replaceAll("\\s", ""));
            socialMedia.setLinkedin(binding.linkedin.getText().toString().replaceAll("\\s", ""));
            createProfileInfoModel.setSocialMedia(socialMedia);
            Logger.errorLogger("profileJson1", new Gson().toJson(createProfileInfoModel));
        }


        Call<OrgProfileInfoModel> call = mEquiFaxApiInterface.updateProfileInfo(
                SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN), createProfileInfoModel);
        call.enqueue(new Callback<OrgProfileInfoModel>() {
            @Override
            public void onResponse(Call<OrgProfileInfoModel> call, Response<OrgProfileInfoModel> response) {
                if (response.code() == 200) {
                    OrgProfileInfoModel response1 = response.body();
                    Toast.makeText(OrgAddSocialLink.this, response1.getMessage(), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    Toast.makeText(OrgAddSocialLink.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrgProfileInfoModel> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }
}