package com.app.rupyz.ui.account.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.adapter.individual.TradeListAdapter;
import com.app.rupyz.databinding.RepaymentFragmentLayoutBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.model.individual.experian.Tradeline;
import com.app.rupyz.ui.account.MyAccount;

import java.util.ArrayList;
import java.util.List;

public class RepaymentFragment extends Fragment {
    private RepaymentFragmentLayoutBinding binding;
    public List<Tradeline> mData = new ArrayList<>();
    private TradeListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = RepaymentFragmentLayoutBinding.inflate(getLayoutInflater());
        initLayout();
        return binding.getRoot();
    }

    private void initLayout() {
        mData = new ArrayList<>();
        for (Tradeline Item : MyAccount.mData) {
            if (AmountHelper.convertStringToInt(Item.getDelayed_payment()) > 0) {
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
