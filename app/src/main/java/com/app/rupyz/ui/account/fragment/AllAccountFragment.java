package com.app.rupyz.ui.account.fragment;

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
import com.app.rupyz.databinding.AllAccountFragmentLayoutBinding;
import com.app.rupyz.generic.model.createemi.EMIResponse;
import com.app.rupyz.generic.model.createemi.experian.Datum;
import com.app.rupyz.generic.model.individual.experian.Tradeline;
import com.app.rupyz.ui.account.MyAccount;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AllAccountFragment extends Fragment implements View.OnClickListener {
    AllAccountFragmentLayoutBinding binding;
    public static List<Tradeline> mData = new ArrayList<>();
    public List<Tradeline> mSearchData = new ArrayList<>();
    private static TradeListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = AllAccountFragmentLayoutBinding.inflate(getLayoutInflater());
        initLayout();
        return binding.getRoot();
    }

    private void initLayout() {
        mData = new ArrayList<>();
        this.mData = MyAccount.mData;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_inside_item, getResources().getStringArray(R.array.sort_by));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spnSortedBy.setAdapter(adapter);
        mAdapter = new TradeListAdapter(this.mData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        binding.txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchByLenderName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.spnSortedBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position) {
                    case 0:
                        sortByStatus();
                        break;
                    case 1:
                        sortByLenderName();
                        break;
                    case 2:
                        sortByTypeLoan();
                        break;
                    case 3:
                        sortByOutstanding();
                        break;
                    case 4:
                        sortByDateAsc();
                        break;
                    case 5:
                        sortByDateDesc();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    private void searchByLenderName(String searchValue) {
        mSearchData = new ArrayList<>();
        for (Tradeline Item : this.mData) {
            if (Item.getSubscriber_Name().toLowerCase().contains(searchValue.toLowerCase())) {
                mSearchData.add(Item);
            }
        }
        mAdapter = new TradeListAdapter(mSearchData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void sortByStatus() {
        mData = new ArrayList<>();
        for (Tradeline Item : MyAccount.mData) {
            if (Item.getAccount_Status().equalsIgnoreCase("active")) {
                mData.add(Item);
            }
        }
        for (Tradeline Item : MyAccount.mData) {
            if (Item.getAccount_Status().equalsIgnoreCase("closed")) {
                mData.add(Item);
            }
        }
        mAdapter = new TradeListAdapter(mData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void sortByLenderName() {
        Collections.sort(mData, new Comparator<Tradeline>() {
            public int compare(Tradeline obj1, Tradeline obj2) {
                return obj1.getSubscriber_Name().compareToIgnoreCase(obj2.getSubscriber_Name()); // To compare string values
            }
        });
        mAdapter = new TradeListAdapter(mData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void sortByTypeLoan() {
        Collections.sort(mData, new Comparator<Tradeline>() {
            public int compare(Tradeline obj1, Tradeline obj2) {
                // ## Ascending order
                return obj1.getAccount_Type().compareToIgnoreCase(obj2.getAccount_Type()); // To compare string values
                // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values

                // ## Descending order
                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
            }
        });
        mAdapter = new TradeListAdapter(mData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void sortByOutstanding() {
        Collections.sort(mData, new Comparator<Tradeline>() {
            public int compare(Tradeline obj1, Tradeline obj2) {
                // ## Ascending order
                return obj2.getCurrent_Balance().compareToIgnoreCase(obj1.getCurrent_Balance()); // To compare string values
            }
        });
        mAdapter = new TradeListAdapter(mData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void sortByDateAsc() {
        Collections.sort(mData, new Comparator<Tradeline>() {
            public int compare(Tradeline obj1, Tradeline obj2) {
                // ## Ascending order
                return obj2.getOpen_Date().compareToIgnoreCase(obj1.getOpen_Date()); // To compare string values
            }
        });
        mAdapter = new TradeListAdapter(mData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void sortByDateDesc() {
        Collections.sort(mData, new Comparator<Tradeline>() {
            public int compare(Tradeline obj1, Tradeline obj2) {
                // ## Ascending order
                return obj1.getOpen_Date().compareToIgnoreCase(obj2.getOpen_Date()); // To compare string values
            }
        });
        mAdapter = new TradeListAdapter(mData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public static void updateExperianSingle(Context context, Datum data, int pos) {
        mData.get(pos).setRepayment_Tenure(data.getRepaymentTenure());
        mData.get(pos).setRate_of_Interest(data.getRateOfInterest());
        mData.get(pos).setMonthDueDay(data.getMonthDueDay());
        mData.get(pos).setScheduled_Monthly_Payment_Amount(data.getScheduledMonthlyPaymentAmount());
        mAdapter.notifyItemChanged(pos);
    }
}
