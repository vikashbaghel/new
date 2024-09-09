package com.app.rupyz.generic.helper;

import android.content.Context;
import android.widget.Toast;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.app.rupyz.generic.utils.Utility.getDateInMilliSeconds;

public class XAxisValueFormatter extends ValueFormatter {
    ArrayList<String> datesList;

    public XAxisValueFormatter(ArrayList<String> arrayOfDates) {
        this.datesList = arrayOfDates;
    }


    @Override
    public String getAxisLabel(float value, AxisBase axis) {
/*
Depends on the position number on the X axis, we need to display the label, Here, this is the logic to convert the float value to integer so that I can get the value from array based on that integer and can convert it to the required value here, month and date as value. This is required for my data to show properly, you can customize according to your needs.
*/
        Integer position = Math.round(value);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM");
        /*if (value > 1 && value < 2) {
            position = 0;
        } else if (value > 2 && value < 3) {
            position = 1;
        } else if (value > 3 && value < 4) {
            position = 2;
        } else if (value > 4 && value <= 5) {
            position = 3;
        }*/

        if (position < datesList.size())
            return sdf.format(new Date((getDateInMilliSeconds(datesList.get(position), "MMM"))));
        return "";
    }
}
