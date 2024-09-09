package com.app.rupyz.generic.logger;

import android.util.Log;

public class Logger {

    public static void warningLogger(String tagName, String log) {
        Log.w(tagName, "warningLogger: " + log);
    }

    public static void errorLogger(String tagName, String log) {
        Log.e(tagName, "errorLogger: " + log);
    }

    public static void errorLogger(String tagName, String log, int statusCode) {
        Log.e(tagName, "errorLogger: " + statusCode + ": " + log);
    }

    public void debugLogger(String log) {

    }

    public void verboseLogger(String log) {

    }

    public void infoLogger(String log) {

    }

}
