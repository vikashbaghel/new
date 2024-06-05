package com.app.rupyz.adapter.individual;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.databinding.AccountHistoryListInsideItemBinding;
import com.app.rupyz.generic.model.individual.experian.CAISAccountHistory;

import java.util.List;

public class AccountHistoryListAdapter extends RecyclerView.Adapter<AccountHistoryListAdapter.ViewHolder> {
    private List<CAISAccountHistory> listdata;
    private AccountHistoryListInsideItemBinding notesBinding;
    private Context mContext;

    // RecyclerView recyclerView;
    public AccountHistoryListAdapter(List<CAISAccountHistory> listdata, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        notesBinding = AccountHistoryListInsideItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(notesBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        notesBinding.imageView.setText(listdata.get(position).getSubscriber_Name());
        notesBinding.txtMonthName.setText(listdata.get(position).getMonth() + "");
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AccountHistoryListInsideItemBinding binding;

        public ViewHolder(AccountHistoryListInsideItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}