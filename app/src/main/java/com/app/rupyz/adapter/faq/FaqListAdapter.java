package com.app.rupyz.adapter.faq;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.databinding.AlertListInsideItemBinding;
import com.app.rupyz.databinding.FaqListInsideItemBinding;
import com.app.rupyz.generic.model.faq.FaqInfoModel;
import com.app.rupyz.generic.model.individual.experian.Alert;

import java.util.List;

public class FaqListAdapter extends RecyclerView.Adapter<FaqListAdapter.ViewHolder> {
    private List<FaqInfoModel> listdata;
    private FaqListInsideItemBinding notesBinding;
    private Context mContext;

    // RecyclerView recyclerView;
    public FaqListAdapter(List<FaqInfoModel> listdata, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        notesBinding = FaqListInsideItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(notesBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        notesBinding.txtName.setText(listdata.get(position).getTitle());
        notesBinding.txtMessage.setText(listdata.get(position).getDesc());
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        FaqListInsideItemBinding binding;

        public ViewHolder(FaqListInsideItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}