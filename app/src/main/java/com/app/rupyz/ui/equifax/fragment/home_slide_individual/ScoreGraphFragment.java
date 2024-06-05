package com.app.rupyz.ui.equifax.fragment.home_slide_individual;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.app.rupyz.databinding.ScoreGraphFragmentBinding;
import com.app.rupyz.generic.helper.XAxisValueFormatter;
import com.app.rupyz.ui.equifax.fragment.EquiFaxHomeFragment;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScoreGraphFragment extends Fragment {
    private List<String> xAxisValues = new ArrayList<>();
    ScoreGraphFragmentBinding binding;
    LineData lineData;
    LineDataSet lineDataSet;
    ArrayList lineEntries;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ScoreGraphFragmentBinding.inflate(getLayoutInflater());
        initLayout();
        return binding.getRoot();
    }

    private void initLayout() {
        initData();
    }

    private void initData() {
        getEntries();
        lineDataSet = new LineDataSet(lineEntries, "");
        lineData = new LineData(lineDataSet);
        binding.scoreChart.setData(lineData);
        lineDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(18f);
        YAxis rightYAxis = binding.scoreChart.getAxisRight();
        rightYAxis.setEnabled(false);
        binding.scoreChart.getDescription().setEnabled(false);
        binding.scoreChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    private void getEntries() {
        lineEntries = new ArrayList<>();
       // lineEntries.add(new Entry(1, HomeFragment.mData.getReport().getScore_value()));
        lineEntries.add(new Entry(1, 870));
       // yourAxis.setGridColor()

        YAxis left = binding.scoreChart.getAxisLeft();
        //left.setValueFormatter(new DecimalValueFormatter(new DecimalFormat("###,##0.0")));
        //left.setGridColor(getResources().getColor(R.color.red));

        left.setLabelCount(6, true);
//        left.setAxisMinimum(EquiFaxHomeFragment.mData.getReport().getScoreValue());
        left.setAxisMaximum(900f);

        binding.scoreChart.getAxisLeft().setDrawGridLines(false);
        binding.scoreChart.getXAxis().setDrawGridLines(false);
        binding.scoreChart.getLegend().setEnabled(false);


        ArrayList<String> dates = new ArrayList<>();

        Calendar cal= Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");

        for(int i=0;i<6;i++){
            cal.add(Calendar.MONTH, 1);
            dates.add(month_date.format(cal.getTime()));
        }

        binding.scoreChart.getXAxis().setGranularity(0.2f);
        binding.scoreChart.getXAxis().setGranularityEnabled(true);
        binding.scoreChart.getXAxis().setLabelCount(dates.size(),false);
        binding.scoreChart.getXAxis().setValueFormatter(new XAxisValueFormatter(dates));
        binding.scoreChart.getXAxis().setCenterAxisLabels(true);
        binding.scoreChart.setEnabled(false);
        binding.scoreChart.setPinchZoom(false);
        binding.scoreChart.setScaleEnabled(false);
    }

}