package com.app.rupyz.generic.helper;

import java.text.DecimalFormat;

public class AmountHelper {
    public static String getCommaSeptdAmount(Double amount) {
        if (amount < 1000) {
            return format("###", amount);
        } else {
            double hundreds = amount % 1000;
            int other = (int) (amount / 1000);
            return format(",##", other) + ',' + format("000", hundreds);
        }
    }

    public static String getCommaSeptdAmount(int amount) {
        if (amount < 1000) {
            return format("###", amount);
        } else {
            double hundreds = amount % 1000;
            int other = (int) (amount / 1000);
            return format(",##", other) + ',' + format("000", hundreds);
        }
    }

    private static String format(String pattern, Object value) {
        return new DecimalFormat(pattern).format(value);
    }

    public static Double convertStringToDouble(String value) {
        try {
            if (value.equalsIgnoreCase("") || value == null) {
                return 0.0;

            } else {
                return Double.parseDouble(value);
            }
        } catch (Exception ex) {
            return 0.0;
        }
    }

    public static int convertStringToInt(String value) {
        try {
            if (value.equalsIgnoreCase("") || value == null) {
                return 0;

            } else {
                return Integer.parseInt(value);
            }
        } catch (Exception ex) {
            return 0;
        }
    }

    public static String convertInLac(double count) {
        try {
            if (count < 10000) {
                return String.format("%.0f", count);
            } else if (count >= 10000 && count < 10000000) {
                return String.format("%.2f", count / 100000) + " Lacs";
            } else {
                return String.format("%.2f", (count / 10000000)) + " Cr";
            }
        } catch (Exception e) {
            return "";
        }
    }
}
