package com.app.rupyz.adapter.organization.individual;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.databinding.AlertListInsideItemBinding;
import com.app.rupyz.generic.model.organization.AlertsItem;
import com.app.rupyz.generic.model.organization.individual.Alert;

import java.util.List;

public class AlertListAdapter extends RecyclerView.Adapter<AlertListAdapter.ViewHolder> {
    private List<Alert> listdata;
    private AlertListInsideItemBinding notesBinding;
    private Context mContext;

    // RecyclerView recyclerView;
    public AlertListAdapter(List<Alert> listdata, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        notesBinding = AlertListInsideItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(notesBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        notesBinding.txtBank.setText(listdata.get(position).getBank());
        notesBinding.txtName.setText(listdata.get(position).getTitle());
        notesBinding.txtMessage.setText(listdata.get(position).getMessage());
//        notesBinding.txtBankType.setText(listdata.get(position).getBank_type());
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AlertListInsideItemBinding binding;

        public ViewHolder(AlertListInsideItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}