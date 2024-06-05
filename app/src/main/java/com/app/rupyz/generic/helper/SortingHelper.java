package com.app.rupyz.generic.helper;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.adapter.individual.TradeListAdapter;
import com.app.rupyz.generic.model.individual.experian.Tradeline;
import com.app.rupyz.generic.model.organization.GstinList;
import com.app.rupyz.generic.model.organization.TradelinesItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortingHelper {

    public static List<GstinList> sortByGSTName(List<GstinList> mData) {
        Collections.sort(mData, new Comparator<GstinList>() {
            public int compare(GstinList obj1, GstinList obj2) {
                // ## Ascending order
                return obj1.getState().compareToIgnoreCase(obj2.getState()); // To compare string values
                // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values

                // ## Descending order
                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
            }
        });
        return mData;
    }
}
