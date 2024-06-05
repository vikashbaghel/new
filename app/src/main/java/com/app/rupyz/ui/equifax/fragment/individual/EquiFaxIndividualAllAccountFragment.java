package com.app.rupyz.ui.equifax.fragment.individual;

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
import com.app.rupyz.adapter.organization.individual.EquiFaxIndividualTradeListAdapter;
import com.app.rupyz.databinding.AllAccountFragmentLayoutBinding;
import com.app.rupyz.generic.model.createemi.EMIResponse;
import com.app.rupyz.generic.model.organization.TradelinesItem;
import com.app.rupyz.generic.model.organization.individual.Tradeline;
import com.app.rupyz.generic.model.organization.individual.Tradelines;
import com.app.rupyz.ui.equifax.EquiFaxIndividualMyAccount;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EquiFaxIndividualAllAccountFragment extends Fragment implements View.OnClickListener {
    AllAccountFragmentLayoutBinding binding;
    public static List<Tradeline> mData = new ArrayList<>();
    public List<Tradeline> mSearchData = new ArrayList<>();
    private static EquiFaxIndividualTradeListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = AllAccountFragmentLayoutBinding.inflate(getLayoutInflater());
        initLayout();
        return binding.getRoot();
    }

    private void initLayout() {
        mData = new ArrayList<>();
        this.mData = EquiFaxIndividualMyAccount.mCustomData;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_inside_item, getResources().getStringArray(R.array.sort_by));
//        adapter.setDropDownViewResource(R.layout.color_spinner_layout);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spnSortedBy.setAdapter(adapter);
        for (int i =0; i<mData.size(); i++){
            System.out.println("LIST DATA" +this.mData.get(i).getAccount_no());
        }
        mAdapter = new EquiFaxIndividualTradeListAdapter(this.mData, getContext());
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
            if (Item.getInstitution_name().toLowerCase().contains(searchValue.toLowerCase())) {
                mSearchData.add(Item);
            }
        }
        mAdapter = new EquiFaxIndividualTradeListAdapter(mSearchData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void sortByStatus() {
        mData = new ArrayList<>();
        for (Tradeline Item : EquiFaxIndividualMyAccount.mData) {
            if (Item.getAccount_status().equalsIgnoreCase("open")) {
                mData.add(Item);
            }
        }
        for (Tradeline Item : EquiFaxIndividualMyAccount.mData) {
            if (Item.getAccount_status().equalsIgnoreCase("closed")) {
                mData.add(Item);
            }
        }
        mAdapter = new EquiFaxIndividualTradeListAdapter(mData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void sortByLenderName() {
        Collections.sort(mData, new Comparator<Tradeline>() {
            public int compare(Tradeline obj1, Tradeline obj2) {
                // ## Ascending order
                return obj1.getInstitution_name().compareToIgnoreCase(obj2.getInstitution_name()); // To compare string values
                // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values

                // ## Descending order
                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
            }
        });
        mAdapter = new EquiFaxIndividualTradeListAdapter(mData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void sortByTypeLoan() {
        Collections.sort(mData, new Comparator<Tradeline>() {
            public int compare(Tradeline obj1, Tradeline obj2) {
                // ## Ascending order
                return obj1.getAccount_type().compareToIgnoreCase(obj2.getAccount_type()); // To compare string values
                // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values

                // ## Descending order
                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
            }
        });
        mAdapter = new EquiFaxIndividualTradeListAdapter(mData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void sortByOutstanding() {
        Collections.sort(mData, new Comparator<Tradeline>() {
            public int compare(Tradeline obj1, Tradeline obj2) {
                // ## Ascending order
                return String.valueOf(obj2.getCurrent_balance_amount()).compareToIgnoreCase(String.valueOf(obj1.getCurrent_balance_amount())); // To compare string values
            }
        });
        mAdapter = new EquiFaxIndividualTradeListAdapter(mData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void sortByDateAsc() {
        Collections.sort(mData, new Comparator<Tradeline>() {
            public int compare(Tradeline obj1, Tradeline obj2) {
                // ## Ascending order
                return obj2.getDate_opened().compareToIgnoreCase(obj1.getDate_opened()); // To compare string values
            }
        });
        mAdapter = new EquiFaxIndividualTradeListAdapter(mData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void sortByDateDesc() {
        Collections.sort(mData, new Comparator<Tradeline>() {
            public int compare(Tradeline obj1, Tradeline obj2) {
                // ## Ascending order
                return obj1.getDate_opened().compareToIgnoreCase(obj2.getDate_opened()); // To compare string values
            }
        });
        mAdapter = new EquiFaxIndividualTradeListAdapter(mData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public static void updateIndividualSingle(Context context, EMIResponse data, int pos) {
/*       // mAdapter.notifyDataSetChanged();
        mData.get(position).setAccountNo("5");
        mData.get(position).setAccountStatus("cancelled");
        mAdapter.notifyItemChanged(position);*/
        mData.get(pos).setRepayment_tenure(data.getRepaymentTenure()+"");
        mData.get(pos).setInterest_rate(data.getInterestRate()+"");
        mData.get(pos).setMonth_due_day(data.getMonthDueDay());
        mData.get(pos).setInstallment_amount(data.getInstallmentAmount());
        mAdapter.notifyItemChanged(pos);
    }
}
