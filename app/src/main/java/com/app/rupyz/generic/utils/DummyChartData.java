package com.app.rupyz.generic.utils;

import android.graphics.Color;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;

public class DummyChartData {

    public void loadPieChartData(PieChart mPieChart, int loan_active, int loan_close) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (loan_active >= 1) {
            entries.add(new PieEntry(loan_active, ""));
        } else {
            entries.add(new PieEntry(1, ""));
        }
        if (loan_close >= 1) {
            entries.add(new PieEntry(loan_close, ""));
        } else {
            entries.add(new PieEntry(0, ""));
        }
        final int[] MY_COLORS = {Color.rgb(209, 157, 244),
                Color.rgb(44, 74, 162)};
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : MY_COLORS) colors.add(c);
        PieDataSet dataSet = new PieDataSet(entries, "EE");
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        data.setValueFormatter(new PercentFormatter(mPieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        mPieChart.setData(data);
        mPieChart.invalidate();
        mPieChart.animateY(1400, Easing.EaseInOutQuad);
    }

    public void loadPieChartScoreInsightsData(PieChart mPieChart) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(35, ""));
        entries.add(new PieEntry(30, ""));
        entries.add(new PieEntry(15, ""));
        entries.add(new PieEntry(10, ""));
        entries.add(new PieEntry(10, ""));

        final int[] MY_COLORS = {Color.rgb(153, 213, 94),
                Color.rgb(99, 107, 228),
                Color.rgb(147, 214, 231),
                Color.rgb(204, 135, 235),
                Color.rgb(150, 142, 224)};
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : MY_COLORS) colors.add(c);
        PieDataSet dataSet = new PieDataSet(entries, "EE");
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        data.setValueFormatter(new PercentFormatter(mPieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        mPieChart.setData(data);
        mPieChart.invalidate();
        mPieChart.animateY(1400, Easing.EaseInOutQuad);
    }


    public void loadOwnershipMax(PieChart mPieChart, int loan_active, int loan_close) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (loan_active >= 1) {
            entries.add(new PieEntry(loan_active, ""));
        } else {
            entries.add(new PieEntry(1, ""));
        }
        if (loan_close >= 1) {
            entries.add(new PieEntry(loan_close, ""));
        } else {
            entries.add(new PieEntry(0, ""));
        }

        final int[] MY_COLORS = {Color.rgb(178, 174, 249),
                Color.rgb(185, 140, 190)};
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : MY_COLORS) colors.add(c);
        PieDataSet dataSet = new PieDataSet(entries, "EE");
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        data.setValueFormatter(new PercentFormatter(mPieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        mPieChart.setData(data);
        mPieChart.invalidate();
        mPieChart.animateY(1400, Easing.EaseInOutQuad);
    }

    public void equiFaxLenderMax(PieChart mPieChart, int nbfc, int psu, int private_bank, int other) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(nbfc, ""));
        entries.add(new PieEntry(psu, ""));
        entries.add(new PieEntry(private_bank, ""));
        entries.add(new PieEntry(other, ""));

        final int[] MY_COLORS = {Color.rgb(254, 194, 136),
                Color.rgb(185, 140, 190), Color.rgb(178, 174, 249),
                Color.rgb(123, 228, 163)};
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : MY_COLORS) colors.add(c);
        PieDataSet dataSet = new PieDataSet(entries, "EE");
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        data.setValueFormatter(new PercentFormatter(mPieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        mPieChart.setData(data);
        mPieChart.invalidate();
        mPieChart.animateY(1400, Easing.EaseInOutQuad);
    }


    public void lenderPieChartData(PieChart mPieChart) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(10, ""));
        entries.add(new PieEntry(5, ""));
        entries.add(new PieEntry(15, ""));

        final int[] MY_COLORS = {Color.rgb(178, 174, 249),
                Color.rgb(185, 140, 190), Color.rgb(254, 194, 136)};
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : MY_COLORS) colors.add(c);

        //dataSet.setColors(colors);

        PieDataSet dataSet = new PieDataSet(entries, "EE");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        data.setValueFormatter(new PercentFormatter(mPieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        mPieChart.setData(data);
        mPieChart.invalidate();
        mPieChart.animateY(1400, Easing.EaseInOutQuad);
    }
}
