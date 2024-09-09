package com.app.rupyz.ui.organization.profile;

import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.app.rupyz.R;
import com.app.rupyz.adapter.organization.profile.OrgImageAdapter;
import com.app.rupyz.databinding.FragmentOrgProfilePhotosBinding;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.org_image.ImageViewModel;
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileDetail;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.utils.AppConstant;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.model_kt.DeleteImageModel;
import com.app.rupyz.model_kt.GenericResponseModel;
import com.app.rupyz.model_kt.OrgImageListModel;
import com.app.rupyz.ui.organization.ProfilePhotosViewListener;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrgProfilePhotosFragment extends Fragment implements ProfilePhotosViewListener {
    FragmentOrgProfilePhotosBinding binding;

    private boolean isSlugAvailable;

    private OrgProfileDetail orgProfileDetail;
    private String slug;

    private OrgImageAdapter adapter;
    private ArrayList<ImageViewModel> photosList;
    private EquiFaxApiInterface mEquiFaxApiInterface;
    private OrgImageListModel imageListModel;

    public OrgProfilePhotosFragment(boolean isSlugAvailable, boolean isDataChange, OrgProfileDetail profileDetailModel, String slug) {
        // Required empty public constructor
        this.isSlugAvailable = isSlugAvailable;
        this.orgProfileDetail = profileDetailModel;
        this.slug = slug;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrgProfilePhotosBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEquiFaxApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface.class);
        binding.message.setVisibility(View.GONE);
        photosList = new ArrayList<>();
        if (isSlugAvailable) {
            getPhotos(slug);
        } else {
            getPhotos(slug);
        }
        initRecyclerView();

    }

    private void initRecyclerView() {
        GridLayoutManager staggeredGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        binding.recyclerView.setLayoutManager(staggeredGridLayoutManager);
        adapter = new OrgImageAdapter(photosList, requireContext(), this, isSlugAvailable);
        binding.recyclerView.setAdapter(adapter);
    }


    private void getPhotos(String slug) {

        Call<OrgImageListModel> call;
        if (slug.equals("")) {
            call = mEquiFaxApiInterface.getOrgImage(
                    SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        } else {
            call = mEquiFaxApiInterface.getSlugImage(
                    slug, "Bearer " + SharedPref.getInstance().getString(TOKEN));
        }

        call.enqueue(new Callback<OrgImageListModel>() {
            @Override
            public void onResponse(Call<OrgImageListModel> call, Response<OrgImageListModel> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());
                    imageListModel = response.body();
                    if (imageListModel.getData() != null && imageListModel.getData().size() > 0) {
                        binding.message.setVisibility(View.GONE);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        photosList.addAll(imageListModel.getData());
                        adapter.notifyDataSetChanged();
                    } else {
                        binding.message.setVisibility(View.VISIBLE);
                        binding.recyclerView.setVisibility(View.GONE);
                        Log.e("DEBUG", "DATA IS EMPTY");
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<OrgImageListModel> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    @Override
    public void onViewPhotos(int position) {
        startActivity(new Intent(requireContext(), OrgPhotosViewActivity.class)
                .putExtra(AppConstant.PRODUCT_INFO, imageListModel)
                .putExtra(AppConstant.IMAGE_POSITION, position));
    }

    @Override
    public void onDeletePhotos(int position, ImageViewModel imageViewModel) {
        showDeleteDialog(position, imageViewModel);
    }

    private void showDeleteDialog(int position, ImageViewModel imageViewModel) {
        final Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.custom_alert_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView txvDelete = (TextView) dialog.findViewById(R.id.txv_delete);
        TextView txvCancel = (TextView) dialog.findViewById(R.id.txv_cancel);
        txvDelete.setOnClickListener(v -> {
            dialog.dismiss();
            deletePhotos(imageViewModel, position);
        });
        txvCancel.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void deletePhotos(ImageViewModel imageViewModel, int position) {
        DeleteImageModel model = new DeleteImageModel(imageViewModel.getId());

        Call<GenericResponseModel> call = mEquiFaxApiInterface.deleteOrgImage(
                SharedPref.getInstance().getInt(ORG_ID), model, "Bearer " + SharedPref.getInstance().getString(TOKEN));

        call.enqueue(new Callback<GenericResponseModel>() {
            @Override
            public void onResponse(Call<GenericResponseModel> call, Response<GenericResponseModel> response) {
                if (response.code() == 200) {

                    photosList.remove(position);
                    initRecyclerView();
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponseModel> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }

}