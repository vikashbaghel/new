package com.app.rupyz.ui.organization.profile;

import static android.app.Activity.RESULT_OK;

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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.R;
import com.app.rupyz.databinding.FragmentOrgTeamBinding;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.profile.achievement.AchievementData;
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileDetail;
import com.app.rupyz.generic.model.profile.profileInfo.createTeam.TeamInfoModel;
import com.app.rupyz.generic.model.profile.profileInfo.deleteProfile.DeleteTeamRequest;
import com.app.rupyz.generic.model.profile.profileInfo.deleteProfile.DeleteTeamResponse;
import com.app.rupyz.generic.model.profile.profileInfo.team.TeamInfo;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.utils.AppConstant;
import com.app.rupyz.generic.utils.DeleteDialog;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.organization.profile.activity.OrgAddTeamActivity;
import com.app.rupyz.ui.organization.profile.adapter.TeamMemberListAdapter;
import com.google.android.gms.auth.api.phone.SmsRetriever;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrgTeamFragment extends Fragment implements OrgTeamEditListener {
    FragmentOrgTeamBinding binding;
    private EquiFaxApiInterface mEquiFaxApiInterface;
    boolean isSlugAvailable;
    boolean isDataChange;
    private OrgProfileDetail profileDetailModel;
    private String slug;
    private List<TeamInfoModel> teamList;
    private TeamMemberListAdapter adapter;

    public OrgTeamFragment(boolean isSlugAvailable, boolean isDataChange, OrgProfileDetail profileDetailModel, String slug) {
        this.isSlugAvailable = isSlugAvailable;
        this.isDataChange = isDataChange;
        this.profileDetailModel = profileDetailModel;
        this.slug = slug;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrgTeamBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEquiFaxApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface.class);
        teamList = new ArrayList<>();

        initRecyclerView();

        teamList(slug);
    }

    private void initRecyclerView() {
        binding.recyclerviewTeam.setHasFixedSize(true);
        binding.recyclerviewTeam.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TeamMemberListAdapter(teamList, getContext(), isSlugAvailable, OrgTeamFragment.this);
        binding.recyclerviewTeam.setAdapter(adapter);
    }

    private void teamList(String slug) {

        Call<TeamInfo> call;
        if (slug.equals("")) {
            call = mEquiFaxApiInterface.getTeamMemberList(
                    SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        } else {
            call = mEquiFaxApiInterface.getSlugTeamMemberList(
                    slug, "Bearer " + SharedPref.getInstance().getString(TOKEN));
        }

        call.enqueue(new Callback<TeamInfo>() {
            @Override
            public void onResponse(Call<TeamInfo> call, Response<TeamInfo> response) {
                if (response.code() == 200) {
                    teamList.clear();
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());
                    TeamInfo response1 = response.body();
                    if (response1 != null && response1.getData() != null && response1.getData().size() > 0) {
                        teamList.addAll(response1.getData());
                        adapter.notifyDataSetChanged();
                        binding.recyclerviewTeam.setVisibility(View.VISIBLE);
                        binding.message.setVisibility(View.GONE);
                    } else {
                        binding.recyclerviewTeam.setVisibility(View.GONE);
                        binding.message.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TeamInfo> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                //Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }

    private void showDeleteDialog(TeamInfoModel product, int position) {
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

    private void deleteProduct(TeamInfoModel datum, int position) {
        Log.e("DEBUG", "Position = " + position);

        DeleteTeamRequest deleteTeamRequest = new DeleteTeamRequest();

        deleteTeamRequest.setId(datum.getId());

        Call<DeleteTeamResponse> call = mEquiFaxApiInterface.deleteTeamMember(
                SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN), deleteTeamRequest);
        call.enqueue(new Callback<DeleteTeamResponse>() {
            @Override
            public void onResponse(Call<DeleteTeamResponse> call, Response<DeleteTeamResponse> response) {
                if (response.code() == 200) {
                    teamList.remove(position);
                    adapter.notifyItemRemoved(position);
                } else {
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeleteTeamResponse> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    @Override
    public void onDeleteTeam(@NonNull TeamInfoModel datum, int position) {
        showDeleteDialog(datum, position);
        //DeleteDialog.showDeleteDialog(requireActivity(),datum,position,"", requireActivity().getString(R.string.alert_msg_delete),this))


    }

    @Override
    public void onUpdateTeam(@NonNull TeamInfoModel datum, int position) {
        Intent intent = new Intent(requireContext(), OrgAddTeamActivity.class);
        intent.putExtra(AppConstant.EDIT_TEAM, "true");
        intent.putExtra("user_name", datum.getName());
        intent.putExtra("user_destination", datum.getPosition());
        intent.putExtra("user_description", datum.getIntro());
        intent.putExtra("user_id", datum.getId());
        intent.putExtra("user_pic", datum.getProfilePicUrl());
        intent.putExtra("profile_pic_id", datum.getProfilePic());
        if (datum.getSocialLinks() != null && datum.getSocialLinks().getLinkedin() != null) {
            intent.putExtra("social_link", datum.getSocialLinks().getLinkedin());
        }
        someActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    TeamInfoModel teamInfoModel = result.getData().getParcelableExtra(AppConstant.TEAM_INFO);
                    for (int i = 0; i < teamList.size(); i++) {
                        if (teamList.get(i).getId() == teamInfoModel.getId()) {
                            teamList.set(i, teamInfoModel);
                            adapter.notifyItemChanged(i);
                            break;
                        }
                    }
                }
            });
}