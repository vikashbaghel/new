package com.app.rupyz.adapter.youtube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.app.rupyz.databinding.YoutubeVideoPlaylistBinding;
import com.app.rupyz.generic.helper.DateFormatHelper;
import com.app.rupyz.generic.model.youtube.Item;
import com.squareup.picasso.Picasso;

import java.util.List;

public class YoutubeListAdapter extends RecyclerView.Adapter<YoutubeListAdapter.ViewHolder> {
    private List<Item> listdata;
    private YoutubeVideoPlaylistBinding binding;
    private Context mContext;

    // RecyclerView recyclerView;
    public YoutubeListAdapter(List<Item> listdata, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = YoutubeVideoPlaylistBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        binding.txvTitle.setText(listdata.get(position).getSnippet().getTitle());
        binding.txvChannelName.setText(listdata.get(position).getSnippet().getChannelTitle());
        try {
            String customDate = DateFormatHelper.getYoutubeVideoDate(listdata.get(position).getSnippet().getPublishTime());
            binding.txvDate.setText(customDate);
        } catch (Exception ex) {

        }
        Picasso.get().load(listdata.get(position).getSnippet().getThumbnails().getHigh().getUrl()).into(binding.imgThumbnail);
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        YoutubeVideoPlaylistBinding binding;

        public ViewHolder(YoutubeVideoPlaylistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}