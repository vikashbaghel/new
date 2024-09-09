package com.app.rupyz.ui.organization.profile.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.TeamItemBinding;
import com.app.rupyz.generic.model.profile.profileInfo.createTeam.TeamInfoModel;
import com.app.rupyz.generic.utils.ImageUtils;
import com.app.rupyz.ui.organization.profile.OrgTeamEditListener;

import java.util.List;

public class TeamMemberListAdapter extends RecyclerView.Adapter<TeamMemberListAdapter.ViewHolder> {
    private List<TeamInfoModel> listdata;
    private TeamItemBinding binding;
    private Context mContext;
    private Boolean isSlugAvailable;
    private OrgTeamEditListener orgTeamEditListener;

    public TeamMemberListAdapter(List<TeamInfoModel> listdata, Context mContext,
                                 boolean isSlugAvailable, OrgTeamEditListener orgTeamEditListener1) {
        this.listdata = listdata;
        this.mContext = mContext;
        this.isSlugAvailable = isSlugAvailable;
        this.orgTeamEditListener = orgTeamEditListener1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = TeamItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        binding.txvUserName.setText(listdata.get(position).getName());
        binding.txvDescription.setText(listdata.get(position).getIntro());
        binding.txvDesignation.setText(listdata.get(position).getPosition());
        ImageUtils.INSTANCE.loadTeamImage(listdata.get(position).getProfilePicUrl(), binding.userImage);

        if (listdata.get(position).getSocialLinks() != null && listdata.get(position).getSocialLinks().getLinkedin() != null) {
            if (!listdata.get(position).getSocialLinks().getLinkedin().equalsIgnoreCase("")) {
                binding.ivLinkedIn.setVisibility(View.VISIBLE);
            } else {
                binding.ivLinkedIn.setVisibility(View.GONE);
            }
        } else {
            binding.ivLinkedIn.setVisibility(View.GONE);
        }

        if (!isSlugAvailable) {
            binding.ivMenu.setVisibility(View.VISIBLE);
        } else {
            binding.ivMenu.setVisibility(View.GONE);
        }

        binding.ivMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), holder.binding.ivMenu);
            popup.inflate(R.menu.menu_edit_and_delete);

            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.delete_product:
                        orgTeamEditListener.onDeleteTeam(listdata.get(position), holder.getAdapterPosition());
                        return true;
                    case R.id.edit_product:
                        orgTeamEditListener.onUpdateTeam(listdata.get(position), holder.getAdapterPosition());
                    default:
                        return false;
                }
            });
            popup.show();
        });

        holder.binding.ivLinkedIn.setOnClickListener(view -> {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        listdata.get(position).getSocialLinks().getLinkedin()));
                mContext.startActivity(browserIntent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        holder.binding.llMainLayout.setOnClickListener(view -> {
            if (!isSlugAvailable) {
                orgTeamEditListener.onUpdateTeam(listdata.get(position), position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TeamItemBinding binding;

        public ViewHolder(TeamItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}