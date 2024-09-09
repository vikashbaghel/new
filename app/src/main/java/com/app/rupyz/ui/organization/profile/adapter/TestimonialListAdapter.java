package com.app.rupyz.ui.organization.profile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.databinding.TestimonialListItemBinding;
import com.app.rupyz.generic.model.profile.testimonial.TestimonialData;
import com.app.rupyz.generic.utils.ImageUtils;
import com.app.rupyz.ui.organization.profile.TestimonialEditListener;

import java.util.List;

public class TestimonialListAdapter extends RecyclerView.Adapter<TestimonialListAdapter.ViewHolder> {
    private List<TestimonialData> listdata;
    private TestimonialListItemBinding binding;
    private Context mContext;
    private boolean isSlugAvailable;
    private TestimonialEditListener listener;


    // RecyclerView recyclerView;
    public TestimonialListAdapter(List<TestimonialData> listdata, Context mContext,
                                  TestimonialEditListener listener, boolean isSlugAvailable) {
        this.listdata = listdata;
        this.mContext = mContext;
        this.listener = listener;
        this.isSlugAvailable = isSlugAvailable;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = TestimonialListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        binding.txvReviewedBy.setText(listdata.get(position).getUserName());
        binding.txvReview.setText(listdata.get(position).getContent());
        binding.txvOrg.setText(listdata.get(position).getCompany());
        if (listdata.get(position).getRating().intValue() > 0) {
            binding.txvRatting.setText(listdata.get(position).getRating() + "/5");
        }
        ImageUtils.INSTANCE.loadTeamImage(listdata.get(position).getUser_pic_url(), binding.imgThumbnail);

        holder.binding.getRoot().setOnClickListener(view -> {
            if (!isSlugAvailable) {
                listener.onEditTestimonials(listdata.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TestimonialListItemBinding binding;

        public ViewHolder(TestimonialListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}