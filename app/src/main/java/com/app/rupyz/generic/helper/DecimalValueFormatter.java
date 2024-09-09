package com.app.rupyz.generic.helper;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.Map;

public class DecimalValueFormatter extends PercentFormatter {

    protected DecimalFormat mFormat;

    public DecimalValueFormatter(DecimalFormat format) {
        this.mFormat = format;
    }

    public DecimalFormat getmFormat() {
        return mFormat;
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        if (value < 10) return "";
        return mFormat.format(value) + "";
    }

}