package com.app.rupyz.adapter.blogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.MicroBlogItemBinding;
import com.app.rupyz.generic.model.blog.Microblog;
import com.app.rupyz.generic.utils.StringUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MicroblogListAdapter extends RecyclerView.Adapter<MicroblogListAdapter.ViewHolder> {
    private List<Microblog> listdata;
    private MicroBlogItemBinding microBlogItemBinding;
    private Context mContext;

    // RecyclerView recyclerView;
    public MicroblogListAdapter(List<Microblog> listdata, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        microBlogItemBinding = MicroBlogItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(microBlogItemBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        microBlogItemBinding.txtName.setText(listdata.get(position).getTitle());
        microBlogItemBinding.txtMessage.setText(listdata.get(position).getSubtitle());
        if (!StringUtils.isBlank(listdata.get(position).getIconImageUrl())) {
            Picasso.get().load(listdata.get(position).getIconImageUrl()).into(microBlogItemBinding.image);
        } else {
            Picasso.get().load(R.drawable.component).into(microBlogItemBinding.image);
        }
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MicroBlogItemBinding binding;

        public ViewHolder(MicroBlogItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}