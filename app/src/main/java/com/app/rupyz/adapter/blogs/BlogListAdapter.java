package com.app.rupyz.adapter.blogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.databinding.BlogListInsideItemBinding;
import com.app.rupyz.generic.model.blog.BlogInfoModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BlogListAdapter extends RecyclerView.Adapter<BlogListAdapter.ViewHolder> {
    private List<BlogInfoModel> listdata;
    private BlogListInsideItemBinding notesBinding;
    private Context mContext;

    // RecyclerView recyclerView;
    public BlogListAdapter(List<BlogInfoModel> listdata, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        notesBinding = BlogListInsideItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(notesBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        notesBinding.txtName.setText(listdata.get(position).getTitle());
        notesBinding.txtMessage.setText(listdata.get(position).getSubtitle());
        Picasso.get().load(listdata.get(position).getThumbnail_image_url()).into(notesBinding.imageView);
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        BlogListInsideItemBinding binding;

        public ViewHolder(BlogListInsideItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}