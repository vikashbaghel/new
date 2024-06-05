package com.app.rupyz.generic.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validations {

    private String regex = "\\d{10}";

    Pattern panPattern = Pattern.compile("[A-Za-z]{5}[0-9]{4}[A-Za-z]{1}");
    Pattern namePattern = Pattern.compile("^[a-zA-z\u0900-\u097F ]*");
    Pattern productCodePattern = Pattern.compile("^[A-Za-z0-9-_.()&#]+$");
    Pattern phoneNamePattern = Pattern.compile("[0-4]");

    Pattern gstNumberPattern = Pattern.compile("[0-9]{2}[A-Za-z]{5}[0-9]{4}[A-Za-z]{1}[1-9A-Za-z]{1}Z[0-9A-Za-z]{1}");

    public boolean mobileNumberValidation(String mobileNumber) {
        return mobileNumber.matches(regex);
    }

    public boolean panValidation(String pan) {
        Matcher matcher = panPattern.matcher(pan);
        // Check if pattern matches
        return matcher.matches();
    }

    public boolean gstValidation(String gst) {
        Matcher matcher = gstNumberPattern.matcher(gst);
        // Check if pattern matches
        return matcher.matches();
    }

    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public boolean isValidMobileNumber(CharSequence target) {
        return (!TextUtils.isEmpty(target) && target.toString().length() == 10
                && !phoneNamePattern.matcher(String.valueOf(target.toString().charAt(0))).matches());
    }

    public boolean isValidName(String target) {
        Matcher matcher = namePattern.matcher(target);
        // Check if pattern matches
        return matcher.matches();
    }

    public boolean isValidProductCode(String target) {
        Matcher matcher = productCodePattern.matcher(target);
        // Check if pattern matches
        return matcher.matches();
    }
}
