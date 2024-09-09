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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.R;
import com.app.rupyz.databinding.FragmentOrgAchievementBinding;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.profile.achievement.AchievementData;
import com.app.rupyz.generic.model.profile.achievement.AchievementInfoModel;
import com.app.rupyz.generic.model.profile.achievement.deleteAchievement.DeleteAchievementRequest;
import com.app.rupyz.generic.model.profile.achievement.deleteAchievement.DeleteAchievementResponse;
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileDetail;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.utils.AppConstant;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.organization.profile.activity.OrgAddAchievementActivity;
import com.app.rupyz.ui.organization.profile.adapter.AchievementListAdapter;
import com.app.rupyz.ui.organization.profile.listener.AchievementActionListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrgAchievementFragment extends Fragment implements AchievementActionListener {

    private EquiFaxApiInterface mEquiFaxApiInterface;
    FragmentOrgAchievementBinding binding;
    boolean isSlugAvailable;
    boolean isDataChange;
    private OrgProfileDetail profileDetailModel;
    private String slug;
    private int editItemPos = -1;
    private AchievementListAdapter adapter;
    private List<AchievementData> productLists;

    public OrgAchievementFragment(boolean isSlugAvailable, boolean isDataChange, OrgProfileDetail profileDetailModel, String slug) {
        this.isSlugAvailable = isSlugAvailable;
        this.isDataChange = isDataChange;
        this.profileDetailModel = profileDetailModel;
        this.slug = slug;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrgAchievementBinding.inflate(getLayoutInflater());
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEquiFaxApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface.class);
        getAchievementInfo(slug);
        return binding.getRoot();
    }

    private void getAchievementInfo(String slug) {
        Call<AchievementInfoModel> call;
        if (slug.equals("")) {
            call = mEquiFaxApiInterface.getAchievement(
                    SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        } else {
            call = mEquiFaxApiInterface.getSlugAchievement(
                    slug, "Bearer " + SharedPref.getInstance().getString(TOKEN));
        }

        call.enqueue(new Callback<AchievementInfoModel>() {

            @Override
            public void onResponse(Call<AchievementInfoModel> call, Response<AchievementInfoModel> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "Achievement " + response.body());
                    AchievementInfoModel response1 = response.body();
                    if (response1.getData() != null && response1.getData().size() > 0) {
                        productLists = response1.getData();
                        adapter = new AchievementListAdapter(productLists, getContext(), isSlugAvailable,
                                OrgAchievementFragment.this);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        binding.message.setVisibility(View.GONE);
                        binding.recyclerView.setAdapter(adapter);
                    } else {
                        binding.recyclerView.setVisibility(View.GONE);
                        binding.message.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AchievementInfoModel> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isSlugAvailable) {
            Log.e("DEBUG", "SlugAvailable");
        } else {
            getAchievementInfo(slug);
        }
    }

    @Override
    public void onDeleteAchievement(@NonNull AchievementData product, int position) {
        showDeleteDialog(product, position);
    }

    private void showDeleteDialog(AchievementData product, int position) {
        final Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.custom_alert_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView txvDelete = (TextView) dialog.findViewById(R.id.txv_delete);
        TextView txvCancel = (TextView) dialog.findViewById(R.id.txv_cancel);
        txvDelete.setOnClickListener(v -> {
            dialog.dismiss();
            deleteProduct(product, position);
        });
        txvCancel.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onEditAchievement(@NonNull AchievementData listdata, int position) {
        editItemPos = position;
        Intent intent = new Intent(getActivity(), OrgAddAchievementActivity.class);
        intent.putExtra(AppConstant.EDIT_ACHIEVEMENT, "true");
        intent.putExtra("achievement_title", listdata.getTitle());
        intent.putExtra("achievement_destination", listdata.getDescription());
        intent.putExtra("achievement_date", listdata.getDate());
        intent.putExtra("achievement_id", listdata.getId() + "");
        intent.putExtra("achievement_client", listdata.getClient());
        intent.putExtra("image_id", listdata.getImage() + "");
        intent.putExtra("image_url", listdata.getImageUrl());
        getActivity().startActivity(intent);
    }

    private void deleteProduct(AchievementData product, int position) {
        DeleteAchievementRequest deleteAchievementRequest = new DeleteAchievementRequest();
        deleteAchievementRequest.setId(product.getId());


        Call<DeleteAchievementResponse> call = mEquiFaxApiInterface.deleteAchievement(
                SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN), deleteAchievementRequest);

        call.enqueue(new Callback<DeleteAchievementResponse>() {
            @Override
            public void onResponse(Call<DeleteAchievementResponse> call, Response<DeleteAchievementResponse> response) {
                if (response.code() == 200) {
                    productLists.remove(position);
                    adapter.notifyItemRemoved(position);
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<DeleteAchievementResponse> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                //Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }

}