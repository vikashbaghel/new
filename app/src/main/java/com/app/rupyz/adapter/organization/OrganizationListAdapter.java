package com.app.rupyz.adapter.organization;

import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_STEP;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.OrganizationItemViewInsideItemBinding;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.model_kt.OrganizationInfoModel;
import com.app.rupyz.sales.organization.ChooseOrganizationActivity;

import java.util.List;

public class OrganizationListAdapter extends RecyclerView.Adapter<OrganizationListAdapter.MyViewHolder> {

    private Context mContext;
    private List<OrganizationInfoModel> mList;
    private Utility mUtil;
    int row_index = -1;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private OrganizationItemViewInsideItemBinding binding;

        public MyViewHolder(OrganizationItemViewInsideItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public OrganizationListAdapter(Context mContext, List<OrganizationInfoModel> mList) {
        this.mContext = mContext;
        this.mList = mList;
        mUtil = new Utility(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(OrganizationItemViewInsideItemBinding.
                inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            if (!mList.get(position).getLegalName().equalsIgnoreCase("")
                    && mList.get(position).getLegalName() != null) {
                holder.binding.txtOrgLegalName.setText(mList.get(holder.getAdapterPosition()).getLegalName());
                holder.binding.txtPanId.setText(mList.get(holder.getAdapterPosition()).getPanId());
            } else {
                holder.binding.txtOrgLegalName.setText(mList.get(holder.getAdapterPosition()).getPanId());
            }
        } catch (Exception ex) {
            holder.binding.txtOrgLegalName.setText(mList.get(holder.getAdapterPosition()).getPanId());
        }

        holder.itemView.setOnClickListener(view -> {
            row_index = position;
            notifyDataSetChanged();
            SharedPref.getInstance().putString(ORG_STEP, mList.get(position).getRegStep() + "");

            ((ChooseOrganizationActivity) mContext).updatePanNumber(position);

        });

        if (row_index == position) {
            mList.get(position).setSelected(true);
            holder.binding.imgCheck.setImageResource(R.drawable.check);
        } else {
            mList.get(position).setSelected(false);
            holder.binding.imgCheck.setImageResource(R.drawable.check_disable);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
