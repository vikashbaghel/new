package com.app.rupyz.generic.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Represents the class that contains the utils methods
 *
 * @author yaya.ndongo
 */
public class Utils {

    public static final int PRECISION = 2;

    public static final int UNDEFINED = -1;


    public static double round(double value, int precision) {
        final String format = format(value, precision);
        try {
            return Double.valueOf(format).doubleValue();
        } catch (Exception e) {
            return Double.valueOf(format.replace(",", ".")).doubleValue();
        }

    }
    //return (double)( (int)(value * Math.pow(10,precision) + .5) ) / Math.pow(10,precision);

    /**
     * Formats the value to the given precision
     */
    public static String format(double value, int precision) {
        final StringBuilder pattern = new StringBuilder("########.");
        for (int i = 0; i < precision; i++) {
            pattern.append("0");
        }
        final DecimalFormat df = new DecimalFormat(pattern.toString());
        df.setMaximumFractionDigits(precision);
        final String format = df.format(value);
        return format;
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void openMap(Context context, Double geoLocationLat, Double geoLocationLong, String label) {
        if (isGoogleMapsInstalled(context)) {
            String strUri =
                    "http://maps.google.com/maps?q=loc:" + geoLocationLat + "," + geoLocationLong + " (" + label + ")";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUri));

            intent.setClassName(
                    "com.google.android.apps.maps",
                    "com.google.android.maps.MapsActivity"
            );

            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Google Maps is not installed. Please install it from the Play Store.", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isGoogleMapsInstalled(Context context) {
        try {
            // Check if Google Maps package is present on the device
            context.getPackageManager().getPackageInfo("com.google.android.apps.maps", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            // Google Maps is not installed
            return false;
        }
    }

    public static boolean isMockLocation(Location location) {
        return location != null && location.isFromMockProvider();
    }

    public static boolean isGpsOn(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isMockLocationEnabled(Context context,Location location) {
        return (Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("1") || ( location != null && location.isFromMockProvider()));
    }

}
