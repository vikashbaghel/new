package com.app.rupyz.adapter.blogs;

import static com.app.rupyz.generic.helper.DateFormatHelper.dateFormatEMI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.EmiReminderItemBinding;
import com.app.rupyz.databinding.MicroBlogItemBinding;
import com.app.rupyz.generic.model.blog.ExperianEmi;
import com.app.rupyz.generic.model.blog.Microblog;

import java.util.List;

public class EMIListAdapter extends RecyclerView.Adapter<EMIListAdapter.ViewHolder> {
    private List<ExperianEmi> listdata;
    private EmiReminderItemBinding binding;
    private Context mContext;
    EventListener listener;
    private boolean mExpanded;

    // RecyclerView recyclerView;
    public EMIListAdapter(List<ExperianEmi> listdata, Context mContext, EventListener listener) {
        this.listdata = listdata;
        this.mContext = mContext;
        this.listener = listener;
    }

    public interface EventListener {
        void onEvent();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = EmiReminderItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        System.out.println("TILE"+listdata.get(position).getAccountNumber());
        binding.txtBankName.setText(listdata.get(position).getSubscriberName());
        binding.txtEmiAmount.setText(mContext.getResources().getString(R.string.rs)+listdata.get(position).getScheduledMonthlyPaymentAmount()+"");
        binding.txtEmiDate.setText(dateFormatEMI(listdata.get(position).getMonthDueDay()));
        binding.txtAccountNumber.setText(listdata.get(position).getAccountNumber());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEvent();
            }
        });


    }

    @Override
    public int getItemCount() {
        if(listdata.size()>3){
            return mExpanded ? listdata.size() : 3;
        }
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EmiReminderItemBinding binding;

        public ViewHolder(EmiReminderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setExpanded(boolean expanded) {
        mExpanded = expanded;
        notifyDataSetChanged();
    }
}