package com.app.rupyz.ui.equifax.fragment.individual;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.adapter.organization.EquiFaxTradeListAdapter;
import com.app.rupyz.adapter.organization.individual.EquiFaxIndividualTradeListAdapter;
import com.app.rupyz.databinding.OverdueFragmentLayoutBinding;
import com.app.rupyz.generic.model.organization.TradelinesItem;
import com.app.rupyz.generic.model.organization.individual.Tradeline;
import com.app.rupyz.generic.model.organization.individual.Tradelines;
import com.app.rupyz.ui.equifax.EquiFaxIndividualMyAccount;
import com.app.rupyz.ui.equifax.EquiFaxMyAccount;

import java.util.ArrayList;
import java.util.List;

public class EquiFaxIndividualOverdueFragment extends Fragment {
    public List<Tradeline> mData = new ArrayList<>();
    private EquiFaxIndividualTradeListAdapter mAdapter;
    OverdueFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = OverdueFragmentLayoutBinding.inflate(getLayoutInflater());
        initLayout();
        return binding.getRoot();
    }

    private void initLayout() {
        mData = new ArrayList<>();
        for (Tradeline Item : EquiFaxIndividualMyAccount.mData) {
            if (Item.isIs_overdue()) {
                mData.add(Item);
            }
        }
        if (mData.size() > 0) {
            mAdapter = new EquiFaxIndividualTradeListAdapter(this.mData, getContext());
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
