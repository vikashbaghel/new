package com.app.rupyz.adapter.navigation_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.app.rupyz.databinding.NavigationViewInsideItemBinding;
import com.app.rupyz.generic.navigation_view.NavigationViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NavigationViewAdapter extends RecyclerView.Adapter<NavigationViewAdapter.ViewHolder> {
    private List<NavigationViewModel> listdata;
    private NavigationViewInsideItemBinding notesBinding;
    private Context mContext;

    // RecyclerView recyclerView;
    public NavigationViewAdapter(List<NavigationViewModel> listdata, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;
    }

    @Override
    public NavigationViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        notesBinding = NavigationViewInsideItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(notesBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        notesBinding.txtName.setText(listdata.get(position).getName());
        Picasso.get().load(listdata.get(position).getImageId()).into(notesBinding.imageView);
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        NavigationViewInsideItemBinding binding;

        public ViewHolder(NavigationViewInsideItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}