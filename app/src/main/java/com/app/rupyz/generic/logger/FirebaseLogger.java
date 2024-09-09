package com.app.rupyz.generic.logger;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class FirebaseLogger {

    FirebaseAnalytics mFirebaseAnalytics;

    public FirebaseLogger(Context mContext) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
    }

    public void sendLog(String eventName, String value) {
        Bundle params = new Bundle();
        params.putString("function_name", value);
        mFirebaseAnalytics.logEvent(eventName, params);
    }
}
