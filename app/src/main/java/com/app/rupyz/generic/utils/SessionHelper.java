package com.app.rupyz.generic.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class SessionHelper {
    private Context mContext;


    public SessionHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void sessionExpired(View view) {
        Toast.makeText(mContext, "Session has Expired ,Please login again", Toast.LENGTH_LONG).show();
    }

    public void sessionExpired() {
        Toast.makeText(mContext, "Session has Expired ,Please login again", Toast.LENGTH_LONG).show();
    }

    public void initMessage(String message, View mView) {
        Snackbar snackbar = Snackbar
                .make(mView, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void messageToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

    public void requestErrorMessage(String response) {
        try {
            JsonParser parser = new JsonParser();
            JsonObject jsonObj = null;
            try {
                jsonObj = (JsonObject) parser.parse(response);
                Toast.makeText(mContext, jsonObj.get("Message").getAsString(), Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                try {
                    Toast.makeText(mContext, jsonObj.get("details").getAsString(), Toast.LENGTH_SHORT).show();
                } catch (Exception exs) {
                }
            }
        } catch (Exception Ex) {

        }
    }

    public void requestErrorMessage(String response, View mView) {
        try {
            JsonParser parser = new JsonParser();
            JsonObject jsonObj = null;
            try {
                jsonObj = (JsonObject) parser.parse(response);
                Snackbar snackbar = Snackbar
                        .make(mView, jsonObj.get("message").getAsString(), Snackbar.LENGTH_LONG);
                snackbar.show();
            } catch (Exception ex) {
                try {
                    Toast.makeText(mContext, jsonObj.get("details").getAsString(), Toast.LENGTH_SHORT).show();
                } catch (Exception exs) {
                }
            }
        } catch (Exception Ex) {

        }
    }

    public void requestMessage(String response) {
        try {
            JsonParser parser = new JsonParser();
            JsonObject jsonObj = null;
            try {
                jsonObj = (JsonObject) parser.parse(response);
                Toast.makeText(mContext, jsonObj.get("message").getAsString(), Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                try {
                    Toast.makeText(mContext, jsonObj.get("details").getAsString(), Toast.LENGTH_SHORT).show();
                } catch (Exception exs) {
                }
            }
        } catch (Exception Ex) {

        }
    }

    public void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

}
