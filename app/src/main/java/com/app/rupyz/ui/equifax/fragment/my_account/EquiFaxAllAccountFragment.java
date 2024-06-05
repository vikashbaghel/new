package com.app.rupyz.ui.equifax.fragment.my_account;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.R;
import com.app.rupyz.adapter.organization.EquiFaxTradeListAdapter;
import com.app.rupyz.databinding.AllAccountFragmentLayoutBinding;
import com.app.rupyz.generic.inteface.BottomSheetCallback;
import com.app.rupyz.generic.model.createemi.EMIResponse;
import com.app.rupyz.generic.model.organization.TradelinesItem;
import com.app.rupyz.ui.equifax.EquiFaxMyAccount;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EquiFaxAllAccountFragment extends Fragment implements View.OnClickListener, BottomSheetCallback {
    AllAccountFragmentLayoutBinding binding;
    public static List<TradelinesItem> mData = new ArrayList<>();
    public List<TradelinesItem> mSearchData = new ArrayList<>();
    private static EquiFaxTradeListAdapter mAdapter;
    private BottomSheetCallback callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = AllAccountFragmentLayoutBinding.inflate(getLayoutInflater());
        callback = this;
        initLayout();
        return binding.getRoot();
    }

    private void initLayout() {
        mData = new ArrayList<>();
        this.mData = EquiFaxMyAccount.mData;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_inside_item, getResources().getStringArray(R.array.sort_by));
//        adapter.setDropDownViewResource(R.layout.color_spinner_layout);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spnSortedBy.setAdapter(adapter);
        mAdapter = new EquiFaxTradeListAdapter(this.mData, getContext());
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
        for (TradelinesItem Item : this.mData) {
            if (Item.getInstitutionName().toLowerCase().contains(searchValue.toLowerCase())) {
                mSearchData.add(Item);
            }
        }
        mAdapter = new EquiFaxTradeListAdapter(mSearchData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void sortByStatus() {
        mData = new ArrayList<>();
        for (TradelinesItem Item : EquiFaxMyAccount.mData) {
            if (Item.getAccountStatus().equalsIgnoreCase("open")) {
                mData.add(Item);
            }
        }
        for (TradelinesItem Item : EquiFaxMyAccount.mData) {
            if (Item.getAccountStatus().equalsIgnoreCase("closed")) {
                mData.add(Item);
            }
        }
        mAdapter = new EquiFaxTradeListAdapter(mData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void sortByLenderName() {
        Collections.sort(mData, new Comparator<TradelinesItem>() {
            public int compare(TradelinesItem obj1, TradelinesItem obj2) {
                // ## Ascending order
                return obj1.getInstitutionName().compareToIgnoreCase(obj2.getInstitutionName()); // To compare string values
                // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values

                // ## Descending order
                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
            }
        });
        mAdapter = new EquiFaxTradeListAdapter(mData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void sortByTypeLoan() {
        Collections.sort(mData, new Comparator<TradelinesItem>() {
            public int compare(TradelinesItem obj1, TradelinesItem obj2) {
                // ## Ascending order
                return obj1.getCreditType().compareToIgnoreCase(obj2.getCreditType()); // To compare string values
                // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values

                // ## Descending order
                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
            }
        });
        mAdapter = new EquiFaxTradeListAdapter(mData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void sortByOutstanding() {
        Collections.sort(mData, new Comparator<TradelinesItem>() {
            public int compare(TradelinesItem obj1, TradelinesItem obj2) {
                // ## Ascending order
                return obj2.getCurrentBalanceAmount().compareToIgnoreCase(obj1.getCurrentBalanceAmount()); // To compare string values
            }
        });
        mAdapter = new EquiFaxTradeListAdapter(mData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void sortByDateAsc() {
        Collections.sort(mData, new Comparator<TradelinesItem>() {
            public int compare(TradelinesItem obj1, TradelinesItem obj2) {
                // ## Ascending order
                return obj2.getSanctionDate().compareToIgnoreCase(obj1.getSanctionDate()); // To compare string values
            }
        });
        mAdapter = new EquiFaxTradeListAdapter(mData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void sortByDateDesc() {
        Collections.sort(mData, new Comparator<TradelinesItem>() {
            public int compare(TradelinesItem obj1, TradelinesItem obj2) {
                // ## Ascending order
                return obj1.getSanctionDate().compareToIgnoreCase(obj2.getSanctionDate()); // To compare string values
            }
        });
        mAdapter = new EquiFaxTradeListAdapter(mData, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public static void updateSingle(Context context, EMIResponse data, int pos) {
/*       // mAdapter.notifyDataSetChanged();
        mData.get(position).setAccountNo("5");
        mData.get(position).setAccountStatus("cancelled");
        mAdapter.notifyItemChanged(position);*/
        mData.get(pos).setRepaymentTenure(data.getRepaymentTenure());
        mData.get(pos).setInterestRate(data.getInterestRate());
        mData.get(pos).setMonthDueDay(data.getMonthDueDay());
        mData.get(pos).setInstallmentAmount(data.getInstallmentAmount());
        mAdapter.notifyItemChanged(pos);
    }

    @Override
    public void callbackMethod(EMIResponse emiResponse) {
       // Toast.makeText(getContext(), "Good Morning Piyush", Toast.LENGTH_SHORT).show();
    }
}
