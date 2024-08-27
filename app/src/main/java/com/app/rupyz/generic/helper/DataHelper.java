package com.app.rupyz.generic.helper;

import java.util.ArrayList;
import java.util.List;

public class DataHelper {

    public List<String> getTurnOverData() {
        List<String> mData = new ArrayList<>();
        mData.add("Select Turnover");
        mData.add("Rs.0 to 40 lakhs");
        mData.add("Rs.40 lakhs to 1.5 Cr.");
        mData.add("Rs.1.5 Cr. to 5 Cr.");
        mData.add("Rs.5 Cr. to 25 Cr.");
        mData.add("Rs.25 Cr. to 100 Cr.");
        mData.add("Rs.100 Cr. to 500 Cr.");
        mData.add("Rs.500 Cr. and above");
        return mData;
    }
}
