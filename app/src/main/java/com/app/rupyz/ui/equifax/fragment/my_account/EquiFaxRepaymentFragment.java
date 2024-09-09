package com.app.rupyz.ui.equifax.fragment.my_account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.adapter.individual.TradeListAdapter;
import com.app.rupyz.adapter.organization.EquiFaxTradeListAdapter;
import com.app.rupyz.databinding.RepaymentFragmentLayoutBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.model.individual.experian.Tradeline;
import com.app.rupyz.generic.model.organization.TradelinesItem;
import com.app.rupyz.ui.account.MyAccount;
import com.app.rupyz.ui.equifax.EquiFaxMyAccount;

import java.util.ArrayList;
import java.util.List;

public class EquiFaxRepaymentFragment extends Fragment {
    private RepaymentFragmentLayoutBinding binding;
    public List<TradelinesItem> mData = new ArrayList<>();
    private EquiFaxTradeListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = RepaymentFragmentLayoutBinding.inflate(getLayoutInflater());
        initLayout();
        return binding.getRoot();
    }

    private void initLayout() {
        mData = new ArrayList<>();
        for (TradelinesItem Item : EquiFaxMyAccount.mData) {
            if (Item.isIsMissedRepayments()) {
                mData.add(Item);
            }
        }
        if (mData.size() > 0) {
            mAdapter = new EquiFaxTradeListAdapter(this.mData, getContext());
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
