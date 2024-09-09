package com.app.rupyz.ui.home.home_slide;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.app.rupyz.R;
import com.app.rupyz.databinding.LoanSummaryFragmentBinding;
import com.app.rupyz.generic.helper.DateFormatHelper;
import com.app.rupyz.generic.helper.DecimalValueFormatter;
import com.app.rupyz.ui.home.HomeFragment;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LoanSummaryFragment extends Fragment {
    private List<String> xAxisValues = new ArrayList<>();
    LoanSummaryFragmentBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = LoanSummaryFragmentBinding.inflate(getLayoutInflater());
        initLayout();
        return binding.getRoot();
    }

    private void initLayout() {
        initData();
    }

    private void initData() {
        List<Entry> yVals1 = new ArrayList<>();
        for (int i = 0; i < HomeFragment.mData.getGraph_data().getAmount().size(); i++) {
            yVals1.add(new BarEntry(i, (int) (double) HomeFragment.mData.getGraph_data().getAmount().get(i)));
        }

        List<BarEntry> yVals2 = new ArrayList<>();
        for (int i = 0; i < HomeFragment.mData.getGraph_data().getAccount().size(); i++) {
            yVals2.add(new BarEntry(i, Integer.valueOf(HomeFragment.mData.getGraph_data().getAccount().get(i))));
        }

        LineDataSet set1 = new LineDataSet(yVals1, "Loan Amount");
//        set1.setDrawValues(false);
        LineData linearData = new LineData(set1);
        set1.setColor((getResources().getColor(R.color.theme_green)));//Line color
        set1.setCircleColor((getResources().getColor(R.color.theme_green)));//Line color
        set1.setDrawIcons(false);
        set1.setLineWidth(5f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);


        // Set you LinearData
        BarDataSet set2 = new BarDataSet(yVals2, "Active Loans");
//        set2.setDrawValues(false);
        BarData barData = new BarData(set2);
        barData.setValueTextColor(Color.RED);
        barData.setDrawValues(false);
        barData.setBarWidth(0.1f);
        barData.setValueFormatter(new DecimalValueFormatter(new DecimalFormat("###,###,###")));

        set2.setBarBorderColor((getResources().getColor(R.color.theme_purple)));//Line color
        set2.setColor((getResources().getColor(R.color.theme_purple)));//Line color
        set2.setFormLineWidth(2.0f);
        set2.setBarShadowColor((getResources().getColor(R.color.theme_green)));//Line color
        set2.setDrawValues(false);

        set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
        // Set you LinearData

        CombinedData data = new CombinedData();
        data.setData(linearData);
        data.setData(barData);

        XAxis xAxis = binding.loanSummaryChart.getXAxis();
        xAxisValues = new ArrayList<>();
        for (String date : HomeFragment.mData.getGraph_data().getDate()) {
            xAxisValues.add(DateFormatHelper.getGraphDate(date));
        }

        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        XAxis.XAxisPosition position = XAxis.XAxisPosition.BOTTOM;
        xAxis.setGranularityEnabled(true);
        xAxis.setGridColor(getResources().getColor(R.color.gray));
        xAxis.setPosition(position);
        xAxis.setGranularity(1f); // interval 1
        binding.loanSummaryChart.setData(data);
        binding.loanSummaryChart.getDescription().setEnabled(false);
        binding.loanSummaryChart.setScaleEnabled(false);
        binding.loanSummaryChart.getAxisLeft().setDrawGridLines(false);
        binding.loanSummaryChart.getXAxis().setGridColor(getResources().getColor(R.color.gray));
        YAxis rightYAxis = binding.loanSummaryChart.getAxisRight();
        binding.loanSummaryChart.getAxisLeft().setDrawLabels(false);
        binding.loanSummaryChart.getAxisRight().setDrawLabels(false);

    }
}