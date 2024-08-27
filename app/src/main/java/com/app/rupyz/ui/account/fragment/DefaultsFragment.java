package com.app.rupyz.ui.account.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.adapter.individual.TradeListAdapter;
import com.app.rupyz.databinding.DefaultsFragmentLayoutBinding;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.individual.experian.Tradeline;
import com.app.rupyz.ui.account.MyAccount;

import java.util.ArrayList;
import java.util.List;

public class DefaultsFragment extends Fragment {
    public List<Tradeline> mData = new ArrayList<>();
    private TradeListAdapter mAdapter;
    DefaultsFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DefaultsFragmentLayoutBinding.inflate(getLayoutInflater());
        initLayout();
        return binding.getRoot();
    }

    private void initLayout() {
        mData = new ArrayList<>();
        for (Tradeline Item : MyAccount.mData) {
            if (!Item.getAsset_classification().equalsIgnoreCase("") && Item.getAsset_classification() != null)
                Logger.errorLogger("Default", Item.getAsset_classification());
            Logger.errorLogger("Default", Item.isNegative_account() + "");
            if (Item.getAsset_classification().equalsIgnoreCase("DPD") ||
                    Item.getAsset_classification().equalsIgnoreCase("SMA") ||
                    Item.getAsset_classification().equalsIgnoreCase("Loss") ||
                    Item.getAsset_classification().equalsIgnoreCase("Doubtful") ||
                    Item.getAsset_classification().equalsIgnoreCase("Sub Standard") ||
                    Item.isNegative_account()
            ) {
                Logger.errorLogger("Default", Item.isNegative_account() + "");
                mData.add(Item);
            }
        }
        if (mData.size() > 0) {
            mAdapter = new TradeListAdapter(this.mData, getContext());
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.recyclerView.setAdapter(mAdapter);
            binding.messageLayout.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        } else {
            binding.messageLayout.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        }
    }
}
