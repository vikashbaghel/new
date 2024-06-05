package com.app.rupyz.ui.organization.profile.activity;

import static com.app.rupyz.generic.utils.AppConstant.IMAGE_UPLOAD_TAG;
import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityOrgAddAchievementBinding;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.profile.achievement.createAchievement.CreateAchievementRequest;
import com.app.rupyz.generic.model.profile.achievement.createAchievement.CreateAchievementResponse;
import com.app.rupyz.generic.model.profile.achievement.deleteAchievement.DeleteAchievementRequest;
import com.app.rupyz.generic.model.profile.achievement.deleteAchievement.DeleteAchievementResponse;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.utils.AppConstant;
import com.app.rupyz.generic.utils.ImageUtils;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.ui.imageupload.ImageUploadBottomSheetDialogFragment;
import com.app.rupyz.ui.imageupload.ImageUploadListener;
import com.app.rupyz.ui.imageupload.ImageUploadViewModel;

import java.io.File;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrgAddAchievementActivity extends AppCompatActivity implements View.OnClickListener, ImageUploadListener {
    private ActivityOrgAddAchievementBinding binding;
    private EquiFaxApiInterface mEquiFaxApiInterface;
    private String strTitle, strDescription, strDate, strClient, strId, image_url;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private ImageUploadViewModel imageUploadViewModel;
    private String uploadedPicId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrgAddAchievementBinding.inflate(getLayoutInflater());
        imageUploadViewModel = new ViewModelProvider(this).get(ImageUploadViewModel.class);
        setContentView(binding.getRoot());
        mEquiFaxApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface.class);
        initLayout();
        initObservers();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_achievement:
                if (validate()) {
                    addAchievement();
                }
                break;
//            case R.id.img_delete_achievement:
//                showDialog();
//                break;
            case R.id.btn_cancel:
            case R.id.img_close:
                onBackPressed();
                break;

        }
    }

    private void initLayout() {
        binding.btnAddAchievement.setOnClickListener(this);
//        binding.imgDeleteAchievement.setOnClickListener(this);
        binding.imgClose.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
        Intent i = getIntent();
        if (i != null) {
            strTitle = i.getStringExtra("achievement_title");
            strDescription = i.getStringExtra("achievement_destination");
            strDate = i.getStringExtra("achievement_date");
            strId = i.getStringExtra("achievement_id");
            strClient = i.getStringExtra("achievement_client");
            image_url = i.getStringExtra("image_url");
            uploadedPicId = i.getStringExtra("image_id");
        }
        if (getIntent() != null && getIntent().hasExtra(AppConstant.EDIT_ACHIEVEMENT)) {
            binding.btnAddAchievement.setText("Update");
        }

        if (!StringUtils.isBlank(strTitle)) {
            binding.edtTitle.setText(strTitle);
        }
        if (!StringUtils.isBlank(strDate)) {
            binding.edtDate.setText(strDate);
        }
        if (!StringUtils.isBlank(strClient)) {
            binding.edtClient.setText(strClient);
        }
        if (!StringUtils.isBlank(strDescription)) {
            binding.edtDescription.setText(strDescription);
        }

//        if (!StringUtils.isBlank(strId)) {
//            binding.imgDeleteAchievement.setVisibility(View.VISIBLE);
//            binding.imgDeleteAchievement.setClickable(true);
//        } else {
//            binding.imgDeleteAchievement.setVisibility(View.INVISIBLE);
//            binding.imgDeleteAchievement.setClickable(false);
//        }
        if (!StringUtils.isBlank(image_url)) {
            ImageUtils.INSTANCE.loadTeamImage(image_url, binding.ivTeamMemberDp);
        }
        binding.ivTeamMemberDp.setOnClickListener(v -> {
            ImageUploadBottomSheetDialogFragment.newInstance(this).show(getSupportFragmentManager(), IMAGE_UPLOAD_TAG);
        });

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        binding.edtDate.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    OrgAddAchievementActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, onDateSetListener, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });
        onDateSetListener = (datePicker, year1, month1, dayofmonth) -> {
            month1 = month1 + 1;
            String date = day + "-" + month1 + "-" + year1;
            strDate = year1 + "-" + month1 + "-" + day;
            binding.edtDate.setText(date);
        };
    }

    private boolean validate() {
        boolean temp = true;
        strTitle = binding.edtTitle.getText().toString();
        strDescription = binding.edtDescription.getText().toString();
        strClient = binding.edtClient.getText().toString();

        if (StringUtils.isBlank(strTitle)) {
            Toast.makeText(OrgAddAchievementActivity.this, "Project Name Required.", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strDescription)) {
            Toast.makeText(OrgAddAchievementActivity.this, "Description Required", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strDate)) {
            Toast.makeText(OrgAddAchievementActivity.this, "Date Required", Toast.LENGTH_SHORT).show();
            temp = false;
        } else if (StringUtils.isBlank(strClient)) {
            Toast.makeText(OrgAddAchievementActivity.this, "Associate Client Name Required", Toast.LENGTH_SHORT).show();
            temp = false;
        }
        return temp;
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

    private void showDialog() {
        final Dialog dialog = new Dialog(OrgAddAchievementActivity.this);
        dialog.setContentView(R.layout.custom_alert_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView txvDelete = (TextView) dialog.findViewById(R.id.txv_delete);
        TextView txvCancel = (TextView) dialog.findViewById(R.id.txv_cancel);
        txvDelete.setOnClickListener(v -> {
            dialog.dismiss();
            deleteAchievement();
        });
        txvCancel.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void deleteAchievement() {

        DeleteAchievementRequest deleteAchievementRequest = new DeleteAchievementRequest();
        if (!StringUtils.isBlank(strId)) {
            deleteAchievementRequest.setId(Integer.parseInt(strId));
        }

        Call<DeleteAchievementResponse> call = mEquiFaxApiInterface.deleteAchievement(
                SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN), deleteAchievementRequest);
        call.enqueue(new Callback<DeleteAchievementResponse>() {
            @Override
            public void onResponse(Call<DeleteAchievementResponse> call, Response<DeleteAchievementResponse> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());
                    DeleteAchievementResponse response1 = response.body();
                    Toast.makeText(OrgAddAchievementActivity.this, response1.getMessage(), Toast.LENGTH_SHORT).show();
//                    onBackPressed();
                } else {
                    Toast.makeText(OrgAddAchievementActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeleteAchievementResponse> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });

    }

    private void addAchievement() {

        CreateAchievementRequest createAchievementRequest = new CreateAchievementRequest();
        if (!StringUtils.isBlank(strClient)) {
            createAchievementRequest.setClient(strClient);
        } else {
            createAchievementRequest.setClient("");
        }
        if (!StringUtils.isBlank(strDate)) {
            createAchievementRequest.setDate(strDate);
        } else {
            createAchievementRequest.setClient("");
        }
        if (!StringUtils.isBlank(strTitle)) {
            createAchievementRequest.setTitle(strTitle);
        } else {
            createAchievementRequest.setTitle("");
        }
        if (!StringUtils.isBlank(strDescription)) {
            createAchievementRequest.setDescription(strDescription);
        } else {
            createAchievementRequest.setDescription("");
        }
        if (!StringUtils.isBlank(strId)) {
            createAchievementRequest.setId(Integer.parseInt(strId));
        }
        if (!uploadedPicId.equalsIgnoreCase("")) {
            try {
                createAchievementRequest.setImage(Integer.parseInt(uploadedPicId));
            } catch (Exception Ex) {

            }
        }
        Call<CreateAchievementResponse> call = mEquiFaxApiInterface.createAchievement(
                SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN), createAchievementRequest);
        call.enqueue(new Callback<CreateAchievementResponse>() {
            @Override
            public void onResponse(Call<CreateAchievementResponse> call, Response<CreateAchievementResponse> response) {
                if (response.code() == 200) {
                    CreateAchievementResponse response1 = response.body();
                    Toast.makeText(OrgAddAchievementActivity.this, response1.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(OrgAddAchievementActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateAchievementResponse> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                Toast.makeText(OrgAddAchievementActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }

    @Override
    public void onCameraUpload(@Nullable String fileName) {
        imageUploadViewModel.uploadCredentials(fileName);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.ivTeamMemberDp.setImageURI(Uri.fromFile(new File(fileName)));
    }

    @Override
    public void onGalleryUpload(@Nullable String fileName) {
        imageUploadViewModel.uploadCredentials(fileName);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.ivTeamMemberDp.setImageURI(Uri.fromFile(new File(fileName)));
    }
}