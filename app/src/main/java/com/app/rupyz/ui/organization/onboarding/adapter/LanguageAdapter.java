package com.app.rupyz.ui.organization.onboarding.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.databinding.ItemLanguageBinding;
import com.app.rupyz.ui.organization.onboarding.model.LanguageListData;


public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {

    private LanguageListData[] languageListData;
    private FragmentManager fragmentManager;
    private Context context;
    int row_index = -1;
    ItemClickListener itemClickListener;
    ItemLanguageBinding binding;
    public boolean isClickedFirstTime = true;


    public LanguageAdapter(LanguageListData[] languageListData, Context context, ItemClickListener itemClickListener) {
        this.languageListData = languageListData;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemLanguageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final LanguageListData listData = languageListData[position];

        binding.txvLanguage.setText(listData.getLanguageName());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index = position;
                notifyDataSetChanged();
                itemClickListener.onItemClick(listData);
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
        return languageListData == null ? 0 : languageListData.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemLanguageBinding binding;

        public ViewHolder(ItemLanguageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface ItemClickListener {
        void onItemClick(LanguageListData languageListData);
    }
}