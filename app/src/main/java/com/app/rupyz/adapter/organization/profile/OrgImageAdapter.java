package com.app.rupyz.adapter.organization.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.OrgProfilImageItemBinding;
import com.app.rupyz.generic.model.org_image.ImageViewModel;
import com.app.rupyz.generic.utils.ImageUtils;
import com.app.rupyz.ui.organization.ProfilePhotosViewListener;

import java.util.List;

public class OrgImageAdapter extends RecyclerView.Adapter<OrgImageAdapter.ViewHolder> {
    private List<ImageViewModel> listdata;
    private OrgProfilImageItemBinding binding;
    private Context mContext;
    private ProfilePhotosViewListener listener;
    private boolean isSlugAvailable;

    // RecyclerView recyclerView;
    public OrgImageAdapter(List<ImageViewModel> listdata, Context mContext, ProfilePhotosViewListener listener,
                           boolean isSlugAvailable) {
        this.listdata = listdata;
        this.mContext = mContext;
        this.listener = listener;
        this.isSlugAvailable = isSlugAvailable;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = OrgProfilImageItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ImageUtils.INSTANCE.loadImage(listdata.get(position).getImage_url(), binding.imageView);
        holder.itemView.setOnClickListener(v -> {
            listener.onViewPhotos(position);
        });
        if (isSlugAvailable) {
            binding.ivMenu.setVisibility(View.GONE);
        }

        binding.ivMenu.setOnClickListener(v -> {
            //creating a popup menu
            PopupMenu popup = new PopupMenu(v.getContext(), holder.binding.ivMenu);
            //inflating menu from xml resource
            popup.inflate(R.menu.menu_edit_and_delete);
            Menu menu = popup.getMenu();
            menu.findItem(R.id.edit_product).setVisible(false);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.delete_product:
                        listener.onDeletePhotos(position, listdata.get(position));
                        return true;
                    default:
                        return false;
                }
            });
            //displaying the popup
            popup.show();
        });

    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        OrgProfilImageItemBinding binding;

        public ViewHolder(OrgProfilImageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}