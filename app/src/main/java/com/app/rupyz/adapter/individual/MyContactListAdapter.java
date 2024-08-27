package com.app.rupyz.adapter.individual;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.databinding.MyBankListInsideItemBinding;
import com.app.rupyz.databinding.MyContactListInsideItemBinding;
import com.app.rupyz.generic.model.individual.experian.CAISHolderPhoneDetails;
import com.app.rupyz.generic.model.individual.experian.Tradeline;

import java.util.List;

public class MyContactListAdapter extends RecyclerView.Adapter<MyContactListAdapter.ViewHolder> {
    private List<CAISHolderPhoneDetails> listdata;
    private MyContactListInsideItemBinding binding;
    private Context mContext;

    // RecyclerView recyclerView;
    public MyContactListAdapter(List<CAISHolderPhoneDetails> listdata, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = MyContactListInsideItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (null != listdata.get(position).getTelephone_Number() && !listdata.get(position).getTelephone_Number().isEmpty()) {
            binding.rlTelephone.setVisibility(View.VISIBLE);
            binding.txtTelephone.setText(listdata.get(position).getTelephone_Number());
        }
        else {
            binding.rlTelephone.setVisibility(View.GONE);
        }
        if (null != listdata.get(position).getEMailId() && !listdata.get(position).getEMailId().isEmpty()) {
            binding.rlEmail.setVisibility(View.VISIBLE);
            binding.txtContactEmail.setText(listdata.get(position).getEMailId());
        }
        else {
            binding.rlEmail.setVisibility(View.GONE);
        }
        if (null != listdata.get(position).getMobile_Telephone_Number() && !listdata.get(position).getMobile_Telephone_Number().isEmpty()) {
            binding.rlPhone.setVisibility(View.VISIBLE);
            binding.txtContactNumber.setText(listdata.get(position).getMobile_Telephone_Number());
        }
        else {
            binding.rlPhone.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MyContactListInsideItemBinding binding;

        public ViewHolder(MyContactListInsideItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static boolean isNullEmpty(String str) {

        // check if string is null
        if (str == null) {
            return true;
        } else if (str.equals("null")) {
            return true;
        }

        // check if string is empty
        else if (str.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
}