package com.app.rupyz.ui.organization.onboarding.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.databinding.ItemGstBinding;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.model.organization.gstinfo.GstinList;
import com.app.rupyz.sales.organization.ChooseGstActivity;

import java.util.List;


public class ChooseGstAdapter extends RecyclerView.Adapter<ChooseGstAdapter.ViewHolder> {

    private List<GstinList> mList;
    private FragmentManager fragmentManager;
    private Context context;
    int row_index = -1;
    // ItemClickListener itemClickListener;
    ItemGstBinding binding;
    public boolean isClickedFirstTime = true;
    private EquiFaxReportHelper mReportHelper;


    public ChooseGstAdapter(Context mContext, List<GstinList> mList) {
        this.mList = mList;
        this.context = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemGstBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        mReportHelper = EquiFaxReportHelper.getInstance();
        final GstinList listData = mList.get(position);
        binding.txvGstNumber.setText(listData.getGstin());
        binding.txvState.setText("( " + listData.getState() + " )");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index = position;
                notifyDataSetChanged();
                ((ChooseGstActivity) context).updateSelectedGST(listData.getGstin());
                mReportHelper.setGstId(listData.getGstin());
                //itemClickListener.onItemClick(listData, position);
            }
        });


        if (row_index == position) {
            holder.binding.imgCheck.setVisibility(View.VISIBLE);
        } else {
            holder.binding.imgCheck.setVisibility(View.GONE);
        }


        /*if(isClickedFirstTime)
        {
            isClickedFirstTime = false;
            holder.binding.imgCheck.setVisibility(View.VISIBLE);
        }
        else
        {
            isClickedFirstTime = true;
            holder.binding.imgCheck.setVisibility(View.GONE);
        }*/

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemGstBinding binding;

        public ViewHolder(ItemGstBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

/*    public interface ItemClickListener {
        void onItemClick(GstinList gstinList, int pos);
    }*/
}