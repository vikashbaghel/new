package com.app.rupyz.ui.equifax.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.app.rupyz.R;
import com.app.rupyz.adapter.complaince.ComplianceCalendarListAdapter;
import com.app.rupyz.databinding.ActivityAllComplianceBinding;
import com.app.rupyz.databinding.ActivityEquifaxCommercialDetailsBinding;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;

public class AllComplianceActivity extends AppCompatActivity {

    private ActivityAllComplianceBinding binding;
    private EquiFaxReportHelper mReportHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllComplianceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mReportHelper = EquiFaxReportHelper.getInstance();
        binding.recyclerViewCompliance.setHasFixedSize(true);
        binding.recyclerViewCompliance.setLayoutManager(new LinearLayoutManager(this));

        if (mReportHelper.getHomeDataInfo().getData().getComplianceCalender()!=null && mReportHelper.getHomeDataInfo().getData().getComplianceCalender().size() > 0) {
            ComplianceCalendarListAdapter complianceCalendarListAdapter = new ComplianceCalendarListAdapter(mReportHelper.getHomeDataInfo().getData().getComplianceCalender(), this, true);
            binding.recyclerViewCompliance.setVisibility(View.VISIBLE);
            binding.recyclerViewCompliance.setAdapter(complianceCalendarListAdapter);
        } else {
            binding.recyclerViewCompliance.setVisibility(View.GONE);
        }
    }
}