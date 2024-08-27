package com.app.rupyz.adapter.complaince;

import static com.app.rupyz.generic.helper.DateFormatHelper.dateFormatEMI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.databinding.BlogListInsideItemBinding;
import com.app.rupyz.databinding.ComplianceCalendarItemBinding;
import com.app.rupyz.generic.model.blog.BlogInfoModel;
import com.app.rupyz.generic.model.blog.ComplianceCalender;
import com.app.rupyz.generic.model.complaincecalender.ComplianceInfoModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ComplianceCalendarListAdapter extends RecyclerView.Adapter<ComplianceCalendarListAdapter.ViewHolder> {
    private List<ComplianceCalender> listData;
    private ComplianceCalendarItemBinding binding;
    private Context mContext;
    private boolean mExpanded;

    public ComplianceCalendarListAdapter(List<ComplianceCalender> listData, Context mContext, boolean viewAll) {
        this.listData = listData;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = ComplianceCalendarItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        binding.txtComplianceTitle.setText(listData.get(position).getTitle());
        binding.txtComplianceDate.setText(dateFormatEMI(listData.get(position).getDueDate()));
    }

    @Override
    public int getItemCount() {
        if(listData.size()>3){
            return mExpanded ? listData.size() : 3;
        }
        return listData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ComplianceCalendarItemBinding binding;
        public ViewHolder(ComplianceCalendarItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setExpanded(boolean expanded) {
        mExpanded = expanded;
        notifyDataSetChanged();
    }
}