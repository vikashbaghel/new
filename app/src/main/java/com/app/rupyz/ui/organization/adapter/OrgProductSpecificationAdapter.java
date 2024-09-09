package com.app.rupyz.ui.organization.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.databinding.OrgSpecificationItemBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class OrgProductSpecificationAdapter extends RecyclerView.Adapter<OrgProductSpecificationAdapter.ViewHolder> {
    private OrgSpecificationItemBinding binding;
    HashMap<String,String> modelList;
    private ArrayList<String> mKeys;
    private ArrayList<String> value;

    // RecyclerView recyclerView;
    public OrgProductSpecificationAdapter(HashMap<String,String> modelList) {
        this.modelList = modelList;
        mKeys = new ArrayList<String>(modelList.keySet());
        value = new ArrayList<String>(modelList.values());
    }

    public String getKey(int position)
    {
        return (String) mKeys.get(position);
    }


    public String getValue(int position)
    {
        return (String) value.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = OrgSpecificationItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String key = getKey(position);
        String value = getValue(position);
        binding.txvSpecificationKey.setText(key);
        binding.txvDescription.setText(value);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        OrgSpecificationItemBinding binding;

        public ViewHolder(OrgSpecificationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}