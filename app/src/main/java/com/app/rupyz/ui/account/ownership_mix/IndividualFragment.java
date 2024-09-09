package com.app.rupyz.ui.account.ownership_mix;

import android.content.Context;
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

import com.app.rupyz.R;
import com.app.rupyz.adapter.individual.TradeListAdapter;
import com.app.rupyz.adapter.individual.TradeListOwnershipMixAdapter;
import com.app.rupyz.databinding.AllAccountFragmentLayoutBinding;
import com.app.rupyz.databinding.OwernerShipMixFragmentLayoutBinding;
import com.app.rupyz.generic.model.createemi.experian.Datum;
import com.app.rupyz.generic.model.individual.experian.Tradeline;
import com.app.rupyz.ui.account.MyAccount;
import com.app.rupyz.ui.account.OwnershipMixActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IndividualFragment extends Fragment {
    OwernerShipMixFragmentLayoutBinding binding;
    public static List<Tradeline> mData = new ArrayList<>();
    private static TradeListOwnershipMixAdapter mAdapter;

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
            if (!Item.getAccountHoldertypeCode().equalsIgnoreCase("joint")) {
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
            binding.message.setText("There is no individual account in your profile!");
            binding.recyclerView.setVisibility(View.GONE);
        }
    }

    public static void updateOwnership(Context context, Datum data, int pos) {
/*       // mAdapter.notifyDataSetChanged();
        mData.get(position).setAccountNo("5");
        mData.get(position).setAccountStatus("cancelled");
        mAdapter.notifyItemChanged(position);*/
        mData.get(pos).setRepayment_Tenure(data.getRepaymentTenure());
        mData.get(pos).setRate_of_Interest(data.getRateOfInterest());
        mData.get(pos).setMonthDueDay(data.getMonthDueDay());
        mData.get(pos).setScheduled_Monthly_Payment_Amount(data.getScheduledMonthlyPaymentAmount());
        mAdapter.notifyItemChanged(pos);
    }

}
