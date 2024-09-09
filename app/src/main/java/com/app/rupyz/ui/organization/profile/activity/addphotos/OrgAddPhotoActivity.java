package com.app.rupyz.ui.organization.profile.activity.addphotos;

import static com.app.rupyz.generic.utils.AppConstant.IMAGE_UPLOAD_TAG;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityOrgAddPhotoBinding;
import com.app.rupyz.model_kt.AddPhotoModel;
import com.app.rupyz.model_kt.AddedPhotoModel;
import com.app.rupyz.ui.imageupload.ImageUploadViewModel;
import com.app.rupyz.ui.imageupload.MultipleImageUploadBottomSheetDialogFragment;
import com.app.rupyz.ui.imageupload.MultipleImageUploadListener;

import java.util.ArrayList;
import java.util.List;

public class OrgAddPhotoActivity extends AppCompatActivity implements View.OnClickListener,
        MultipleImageUploadListener, AddPhotoListAdapter.OnImageDeleteListener {
    private ActivityOrgAddPhotoBinding binding;
    private int image_upload_id;
    private ImageUploadViewModel imageUploadViewModel;
    private AddPhotoViewModel addPhotoViewModel;
    private List<String> image_url = new ArrayList<>();
    private AddPhotoListAdapter addPhotoListAdapter;
    private ArrayList<AddedPhotoModel> photoModelList;
    int count = 0;
    int multiplePicCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrgAddPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        photoModelList = new ArrayList<>();
        imageUploadViewModel = new ViewModelProvider(this).get(ImageUploadViewModel.class);
        addPhotoViewModel = new ViewModelProvider(this).get(AddPhotoViewModel.class);
        initObservers();
        initLayout();
    }

    private void initLayout() {
        binding.clAddImage1.setOnClickListener(this);
        binding.btnAddProduct.setOnClickListener(this);
        binding.clAddImage1.setOnClickListener(this);
        binding.ivProductImage1.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
        binding.imgClose.setOnClickListener(this);

        initRecyclerView();
    }

    private void initRecyclerView() {
        binding.rvImages.setLayoutManager(new GridLayoutManager(this, 3));
        addPhotoListAdapter = new AddPhotoListAdapter(photoModelList, this, true);
        binding.rvImages.setAdapter(addPhotoListAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cl_add_image_1:
            case R.id.iv_product_image_1:
                image_upload_id = view.getId();
                MultipleImageUploadBottomSheetDialogFragment.newInstance(this)
                        .show(getSupportFragmentManager(), IMAGE_UPLOAD_TAG);
                break;
            case R.id.btn_add_product:
                validateProduct();
                break;
            case R.id.btn_cancel:
            case R.id.img_close:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    private void validateProduct() {
        if (image_url.isEmpty()) {
            Toast.makeText(OrgAddPhotoActivity.this, "Photo Required !",
                    Toast.LENGTH_SHORT).show();
        } else {
            addProduct();
        }
    }

    private void addProduct() {
        binding.progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            for (int i = 0; i < image_url.size(); i++) {
                AddPhotoModel addPhotoModel = new AddPhotoModel();
                addPhotoModel.setImageUrl(Integer.valueOf(image_url.get(i)));
                addPhotoViewModel.addPhoto(addPhotoModel);
            }
        }).start();

    }

    @Override
    public void onCameraUpload(@Nullable String fileName) {
        AddedPhotoModel addedPhotoModel = new AddedPhotoModel();
        addedPhotoModel.setImagePath(fileName);
        addedPhotoModel.setUploading(false);
        addedPhotoModel.setTimeStamp(System.currentTimeMillis());
        photoModelList.add(addedPhotoModel);
        addPhotoListAdapter.notifyDataSetChanged();

        new Thread(() -> {
            runOnUiThread(() -> binding.progressBar.setVisibility(View.VISIBLE));

            imageUploadViewModel.uploadCredentials(fileName);
        }).start();
        binding.btnAddProduct.setEnabled(false);
    }

    @Override
    public void onGallerySingleUpload(@Nullable String fileName) {
        AddedPhotoModel addedPhotoModel = new AddedPhotoModel();
        addedPhotoModel.setImagePath(fileName);
        addedPhotoModel.setUploading(false);
        addedPhotoModel.setTimeStamp(System.currentTimeMillis());
        photoModelList.add(addedPhotoModel);
        addPhotoListAdapter.notifyDataSetChanged();
        new Thread(() -> {
            runOnUiThread(() -> binding.progressBar.setVisibility(View.VISIBLE));
            imageUploadViewModel.uploadCredentials(fileName);

        }).start();

        binding.btnAddProduct.setEnabled(false);
    }

    @Override
    public void onGalleryMultipleUpload(@Nullable List<String> fileList) {
        for (int i = 0; i < fileList.size(); i++) {
            AddedPhotoModel addedPhotoModel = new AddedPhotoModel();
            addedPhotoModel.setImagePath(fileList.get(i));
            addedPhotoModel.setUploading(false);
            addedPhotoModel.setTimeStamp(System.currentTimeMillis());
            photoModelList.add(addedPhotoModel);
            addPhotoListAdapter.notifyItemInserted(i);
        }

        new Thread(() -> {
            runOnUiThread(() -> binding.progressBar.setVisibility(View.VISIBLE));
            for (int i = 0; i < photoModelList.size(); i++) {
                imageUploadViewModel.uploadCredentials(photoModelList.get(i).getImagePath());
            }
        }).start();

    }

    private void initObservers() {
        imageUploadViewModel.getCredLiveData().observe(this, genericResponseModel -> {
            if (genericResponseModel.getData() != null && genericResponseModel.getData().getId() != null) {
                multiplePicCount++;
                image_url.add(genericResponseModel.getData().getId());
            }

            if (multiplePicCount == photoModelList.size() - 1) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnAddProduct.setEnabled(true);
            }
        });

        addPhotoViewModel.getPhotoLiveData().observe(this, addPhotoResponseModel -> {
            count++;
            if (count == image_url.size() - 1) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(OrgAddPhotoActivity.this, addPhotoResponseModel.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

        });
    }

    @Override
    public void onDeleteImage(int position, @org.jetbrains.annotations.Nullable Long timeStamp) {

    }

    @Override
    public void onImageSelect(@NonNull AddedPhotoModel model, int position) {

    }

    @Override
    public void onUploadPdf() {
    }
}