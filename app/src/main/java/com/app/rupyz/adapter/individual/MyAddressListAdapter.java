package com.app.rupyz.adapter.individual;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.databinding.MyAddressListInsideItemBinding;
import com.app.rupyz.generic.model.individual.experian.Tradeline;

import java.util.List;

public class MyAddressListAdapter extends RecyclerView.Adapter<MyAddressListAdapter.ViewHolder> {
    private List<Tradeline> listdata;
    private MyAddressListInsideItemBinding binding;
    private Context mContext;

    public MyAddressListAdapter(List<Tradeline> listdata, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = MyAddressListInsideItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        binding.txtBankName.setText(listdata.get(position).getSubscriber_Name());
        binding.txtAddress.setText(listdata.get(position).getcAIS_Holder_Address_Details().get(0).getFirst_Line_Of_Address_non_normalized() + listdata.get(position).getcAIS_Holder_Address_Details().get(0).getSecond_Line_Of_Address_non_normalized());
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MyAddressListInsideItemBinding binding;

        public ViewHolder(MyAddressListInsideItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}