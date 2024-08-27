package com.app.rupyz.ui.organization.profile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.databinding.SpecificationItemBinding;
import com.app.rupyz.model_kt.AddSpecificationModel;

import java.util.ArrayList;
import java.util.HashMap;

public class SpecificationListAdapter extends RecyclerView.Adapter<SpecificationListAdapter.ViewHolder> {
    private SpecificationItemBinding binding;
    private Context mContext;
    HashMap<String, String> modelList;
    private ArrayList<String> mKeys;
    private ArrayList<String> value;
    private ArrayList<AddSpecificationModel> specificationModelArrayList;
    private final OnDelete listener;

    // RecyclerView recyclerView;
    public SpecificationListAdapter(Context mContext, ArrayList<AddSpecificationModel> modelList, OnDelete listener) {
        this.specificationModelArrayList = modelList;
        this.mContext = mContext;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = SpecificationItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        AddSpecificationModel model = specificationModelArrayList.get(position);
        int number = position + 1;
        binding.txvSpecificationNumber.setText("" + number);
        binding.txvSpecificationKey.setText(model.getKey());
        binding.txvDescription.setText(model.getDescription());
        binding.imgDeleteSpecification.setOnClickListener(view -> listener.onItemDelete(model.getKey(), position));
    }

    @Override
    public int getItemCount() {
        return specificationModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        SpecificationItemBinding binding;

        public ViewHolder(SpecificationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnDelete {
        void onItemDelete(String key, int position);
    }
}