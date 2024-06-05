package com.app.rupyz.ui.account.ownership_mix;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.adapter.individual.TradeListAdapter;
import com.app.rupyz.adapter.individual.TradeListOwnershipMixAdapter;
import com.app.rupyz.databinding.OwernerShipMixFragmentLayoutBinding;
import com.app.rupyz.generic.model.individual.experian.Tradeline;
import com.app.rupyz.ui.account.OwnershipMixActivity;

import java.util.ArrayList;
import java.util.List;

public class JointFragment extends Fragment {
    OwernerShipMixFragmentLayoutBinding binding;
    public List<Tradeline> mData = new ArrayList<>();
    private TradeListOwnershipMixAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = OwernerShipMixFragmentLayoutBinding.inflate(getLayoutInflater());
        initLayout();
        return binding.getRoot();
    }

    private void initLayout() {
        mData = new ArrayList<>();
        for (Tradeline Item : OwnershipMixActivity.mData) {
            if (Item.getAccountHoldertypeCode().equalsIgnoreCase("joint")) {
                mData.add(Item);
            }
        }
        if (mData.size() > 0) {
            mAdapter = new TradeListOwnershipMixAdapter(this.mData, getContext());
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.recyclerView.setAdapter(mAdapter);
            binding.message.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        } else {
            binding.message.setVisibility(View.VISIBLE);
            binding.message.setText("There is no Joint/Guarantor account in your profile!");
            binding.recyclerView.setVisibility(View.GONE);
        }
    }

}
