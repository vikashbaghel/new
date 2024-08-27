package com.app.rupyz.ui.organization.profile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ProductGridItemBinding;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.profile.product.ProductList;
import com.app.rupyz.generic.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductListGridAdapter extends RecyclerView.Adapter<ProductListGridAdapter.ViewHolder> {
    private List<ProductList> listdata;
    private ProductGridItemBinding binding;
    private Context mContext;

    // RecyclerView recyclerView;
    public ProductListGridAdapter(List<ProductList> listdata, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = ProductGridItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        binding.txvProductName.setText(listdata.get(position).getName());
        binding.txvPrice.setText(mContext.getResources().getString(R.string.rs) + listdata.get(position).getMinPrice() + " to " + listdata.get(position).getMaxPrice());
        try {

            if (listdata.get(position).getDisplayPicUrl() != null && !listdata.get(position).getDisplayPicUrl().equals("")) {
                Picasso.get().load(listdata.get(position).getDisplayPicUrl()).into(binding.imgThumbnail);
            } else if (listdata.get(position).getPics_urls().size() > 0) {
                Picasso.get().load(listdata.get(position).getPics_urls().get(0)).into(binding.imgThumbnail);
            }
        } catch (Exception Ex) {
            Logger.errorLogger("ProductAdapter", Ex.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ProductGridItemBinding binding;

        public ViewHolder(ProductGridItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}