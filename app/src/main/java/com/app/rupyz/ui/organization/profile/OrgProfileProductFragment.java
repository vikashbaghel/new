package com.app.rupyz.ui.organization.profile;

import static android.app.Activity.RESULT_OK;
import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.R;
import com.app.rupyz.databinding.FragmentOrgProfileProductBinding;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.profile.product.ProductInfoModel;
import com.app.rupyz.generic.model.profile.product.ProductList;
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileDetail;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.utils.AppConstant;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.model_kt.AddProductResponseModel;
import com.app.rupyz.sales.product.AddProductActivity;
import com.app.rupyz.sales.product.ProductDetailsActivity;
import com.app.rupyz.ui.organization.profile.adapter.ProductListAdapter;
import com.app.rupyz.ui.organization.profile.adapter.ProductListGridAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrgProfileProductFragment extends Fragment implements ProductActionListener {

    FragmentOrgProfileProductBinding binding;
    private EquiFaxApiInterface mEquiFaxApiInterface;
    ProductListAdapter adapter;
    ProductListGridAdapter productListGridAdapter;
    GridLayoutManager layoutManager;

    boolean isSlugAvailable;
    boolean isDataChange;
    private OrgProfileDetail profileDetailModel;
    private String slug;
    private List<ProductList> productLists;
    private int editProductPos = -1;

    public OrgProfileProductFragment(boolean isSlugAvailable, boolean isDataChange, OrgProfileDetail profileDetailModel, String slug) {
        this.isSlugAvailable = isSlugAvailable;
        this.isDataChange = isDataChange;
        this.profileDetailModel = profileDetailModel;
        this.slug = slug;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrgProfileProductBinding.inflate(getLayoutInflater());
        mEquiFaxApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        productLists = new ArrayList<>();

        productList(1, slug);
        binding.imgGridView.setOnClickListener(view -> productList(2, slug));
        binding.imgListView.setOnClickListener(view -> productList(1, slug));
        return binding.getRoot();
    }

    private void productList(int value, String slug) {
        Call<ProductInfoModel> call;
        if (slug.equals("")) {
            call = mEquiFaxApiInterface.getProductList(
                    SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN), "", 1);
        } else {
            call = mEquiFaxApiInterface.getSlugProductList(
                    slug, "Bearer " + SharedPref.getInstance().getString(TOKEN));
        }

        call.enqueue(new Callback<ProductInfoModel>() {
            @Override
            public void onResponse(Call<ProductInfoModel> call, Response<ProductInfoModel> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.code() == 200) {
                    ProductInfoModel response1 = response.body();
                    if (response1.getData() != null && response1.getData().size() > 0) {
                        if (value == 1) {
                            productLists.addAll(response1.getData());
                            binding.recyclerView.setHasFixedSize(true);
                            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            adapter = new ProductListAdapter(productLists, getContext(), OrgProfileProductFragment.this, isSlugAvailable, slug);
                        } else {
                            layoutManager = new GridLayoutManager(getContext(), 2);
                            binding.recyclerView.setLayoutManager(layoutManager);
                            binding.recyclerView.setHasFixedSize(true);
                            productListGridAdapter = new ProductListGridAdapter(response1.getData(), getContext());
                        }
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        binding.message.setVisibility(View.GONE);
                        binding.recyclerView.setAdapter(adapter);

                    } else {
                        binding.recyclerView.setVisibility(View.GONE);
                        binding.message.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductInfoModel> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                //Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }

    public void changeLayoutGrid() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);
    }

    private void changeLayoutLinear() {
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onEditProduct(@NonNull ProductList product, int position) {
        editProductPos = position;
        activityResultLauncher.launch(new Intent(requireContext(), AddProductActivity.class)
                .putExtra(AppConstant.EDIT_PRODUCT, "true")
                .putExtra(AppConstant.PRODUCT_ID, product.getId()));
    }


    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData().hasExtra(AppConstant.PRODUCT_INFO)) {
                            AddProductResponseModel productResponseModel = result.getData().getParcelableExtra(AppConstant.PRODUCT_INFO);
                            productLists.get(editProductPos).setCategory(productResponseModel.getData().getCategory());
                            productLists.get(editProductPos).setName(productResponseModel.getData().getName());
                            productLists.get(editProductPos).setMinPrice(productResponseModel.getData().getMinPrice());
                            productLists.get(editProductPos).setMaxPrice(productResponseModel.getData().getMaxPrice());
                            adapter.notifyItemChanged(editProductPos);
                        }
                    }
                }
            });

    @Override
    public void onDeleteProduct(@NonNull ProductList product, int position) {
        showDeleteDialog(product, position);
    }


    private void showDeleteDialog(ProductList product, int position) {
        final Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.custom_alert_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView txvDelete = (TextView) dialog.findViewById(R.id.txv_delete);
        TextView txvCancel = (TextView) dialog.findViewById(R.id.txv_cancel);
        txvDelete.setOnClickListener(v -> {
            dialog.dismiss();
            deleteProduct(product, position);
        });
        txvCancel.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void deleteProduct(ProductList product, int position) {
        Call<String> call = mEquiFaxApiInterface.deleteProduct(
                "" + SharedPref.getInstance().getInt(ORG_ID), product.getId(), "Bearer " + SharedPref.getInstance().getString(TOKEN));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    productLists.remove(position);
                    adapter.notifyItemRemoved(position);
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                //Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }

    @Override
    public void onShareProduct(@NonNull ProductList product, int position) {
        Utility.shareMyProductWithAll(getContext(), product.getName(), product.getProductUrl());
    }

    @Override
    public void getProductDetails(@NonNull ProductList product, int position) {
        Intent intent = new Intent(requireContext(), ProductDetailsActivity.class);
        intent.putExtra("product_id", product.getId());
        intent.putExtra("product_name", product.getName());
        someActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    productLists = new ArrayList<>();
                    productList(1, slug);
                }
            });

    @Override
    public void getPackagingLevelInfo(@NonNull ProductList model) {

    }
}