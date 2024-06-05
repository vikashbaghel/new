package com.app.rupyz.ui.organization.profile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.AchievementItemBinding;
import com.app.rupyz.generic.helper.DateFormatHelper;
import com.app.rupyz.generic.model.profile.achievement.AchievementData;
import com.app.rupyz.generic.utils.AppConstant;
import com.app.rupyz.generic.utils.ImageUtils;
import com.app.rupyz.ui.organization.profile.activity.OrgAddAchievementActivity;
import com.app.rupyz.ui.organization.profile.listener.AchievementActionListener;

import java.util.List;

public class AchievementListAdapter extends RecyclerView.Adapter<AchievementListAdapter.ViewHolder> {
    private List<AchievementData> listdata;
    private AchievementItemBinding binding;
    private Context mContext;
    private Boolean isSlugAvailable;
    private AchievementActionListener listener;

    // RecyclerView recyclerView;
    public AchievementListAdapter(List<AchievementData> listdata, Context mContext, boolean isSlugAvailable,
                                  AchievementActionListener listener) {
        this.listdata = listdata;
        this.mContext = mContext;
        this.isSlugAvailable = isSlugAvailable;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = AchievementItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        binding.txvClientName.setText(listdata.get(position).getClient());
        binding.txvTitle.setText(listdata.get(position).getTitle());
        binding.txvAchievementDescription.setText(listdata.get(position).getDescription());
        ImageUtils.INSTANCE.loadTeamImage(listdata.get(position).getImageUrl(), binding.userImage);
        if (listdata.get(position).getDate() != null) {
            binding.txvDate.setText(DateFormatHelper.getProfileDate(listdata.get(position).getDate()));
        }
        if (isSlugAvailable) {
            binding.ivMenu.setVisibility(View.GONE);
        }
        binding.ivMenu.setOnClickListener(v -> {
            //creating a popup menu
            PopupMenu popup = new PopupMenu(v.getContext(), holder.binding.ivMenu);
            //inflating menu from xml resource
            popup.inflate(R.menu.menu_edit_and_delete);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.delete_product:
                        listener.onDeleteAchievement(listdata.get(position), position);
                        return true;
                    case R.id.edit_product:
                        listener.onEditAchievement(listdata.get(position), position);
                        return true;
                    default:
                        return false;
                }
            });
            //displaying the popup
            popup.show();
        });

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSlugAvailable) {
                    Intent intent = new Intent(mContext, OrgAddAchievementActivity.class);
                    intent.putExtra(AppConstant.EDIT_ACHIEVEMENT, "true");
                    intent.putExtra("achievement_title", listdata.get(position).getTitle());
                    intent.putExtra("achievement_destination", listdata.get(position).getDescription());
                    intent.putExtra("achievement_date", listdata.get(position).getDate());
                    intent.putExtra("achievement_id", listdata.get(position).getId() + "");
                    intent.putExtra("achievement_client", listdata.get(position).getClient());
                    intent.putExtra("image_id", listdata.get(position).getImage() + "");
                    intent.putExtra("image_url", listdata.get(position).getImageUrl());
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AchievementItemBinding binding;

        public ViewHolder(AchievementItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}