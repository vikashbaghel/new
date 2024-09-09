package com.app.rupyz.ui.equifax.fragment.alerts;

import static com.app.rupyz.generic.utils.SharePrefConstant.ACCOUNT_TYPE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.R;
import com.app.rupyz.adapter.itemdecorator.DividerItemDecorator;
import com.app.rupyz.adapter.organization.AlertListAdapter;
import com.app.rupyz.databinding.EquifaxCommercialAlertFragmentBinding;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.logger.FirebaseLogger;
import com.app.rupyz.generic.model.organization.AlertsItem;
import com.app.rupyz.generic.model.organization.EquiFaxInfoModel;
import com.app.rupyz.generic.model.organization.individual.Alert;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;

import java.util.List;

public class EquiFaxCommercialAlertFragment extends Fragment {

    private EquifaxCommercialAlertFragmentBinding binding;
    EquiFaxInfoModel mData;
    private List<Alert> mRetailAlertData;
    private List<AlertsItem> mCommercialAlertData;
    private Utility mUtil;
    private EquiFaxReportHelper mReportHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EquifaxCommercialAlertFragmentBinding.inflate(getLayoutInflater());
        mReportHelper = EquiFaxReportHelper.getInstance();
        mUtil = new Utility(getActivity());
        new FirebaseLogger(getContext()).sendLog("Alert", "Alert");
        initLayout();
        return binding.getRoot();
    }

    private void initLayout() {
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecorator itemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(getContext(), R.drawable.item_divider));
        binding.recyclerView.addItemDecoration(itemDecoration);
        if (SharedPref.getInstance().getString(ACCOUNT_TYPE).equalsIgnoreCase(getResources().getString(R.string.RETAIL))) {
            mRetailAlertData = mReportHelper.getRetailReport().getReport().getAlerts();
            if (mRetailAlertData != null && mRetailAlertData.size() > 0) {
                com.app.rupyz.adapter.organization.individual.AlertListAdapter adapter = new com.app.rupyz.adapter.organization.individual.AlertListAdapter(mRetailAlertData, getContext());
                binding.recyclerView.setAdapter(adapter);
                binding.message.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
            } else {
                binding.message.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
            }

        } else {
            mCommercialAlertData = mReportHelper.getCommercialReport().getReport().getAlerts();
            if (mCommercialAlertData != null && mCommercialAlertData.size() > 0) {
                AlertListAdapter adapter = new AlertListAdapter(mCommercialAlertData, getContext());
                binding.recyclerView.setAdapter(adapter);
                binding.message.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
            } else {
                binding.message.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
            }
        }
    }

}
