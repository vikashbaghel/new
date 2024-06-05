package com.app.rupyz.adapter.organization;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.databinding.GstItemViewInsideItemBinding;
import com.app.rupyz.generic.model.organization.GstinList;
import com.app.rupyz.ui.organization.AuthAccountActivity;
import com.app.rupyz.ui.organization.GSTActivity;

import java.util.List;

public class GstListAdapter extends RecyclerView.Adapter<GstListAdapter.MyViewHolder> {

    private Context mContext;
    private List<GstinList> mList;
    private int type = 0;
    int row_index = -1;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private GstItemViewInsideItemBinding binding;

        public MyViewHolder(GstItemViewInsideItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public GstListAdapter(Context mContext, List<GstinList> mList, int type) {
        this.mContext = mContext;
        this.mList = mList;
        this.type = type;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(GstItemViewInsideItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.txtGst.setText(
                mList.get(holder.getAdapterPosition()).getState()
                        + "-"
                        + mList.get(holder.getAdapterPosition()).getGstin()
        );

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index = position;
                notifyDataSetChanged();
                if (type == 0) {
                    ((GSTActivity) mContext).updateGSTNumber(position);
                } else {
                    ((AuthAccountActivity) mContext).updateAuthSignatory(position);
                }
            }
        });
        if (row_index == position) {
            holder.binding.imgCheck.setVisibility(View.VISIBLE);
        } else {
            holder.binding.imgCheck.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
