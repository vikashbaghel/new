package com.app.rupyz.generic.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.app.rupyz.R;

public class ButtonStyleHelper {

    private Context mContext;

    public ButtonStyleHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void initButton(boolean isStatus, Button mButton, String value) {
        mButton.setEnabled(isStatus);
        if (isStatus) {
            mButton.setText(value);
            mButton.setBackgroundResource(R.drawable.btn_gredient);
        } else {
            mButton.setText(value);
            mButton.setBackgroundResource(R.drawable.check_score_button_style_disable);
        }
    }

    public void initDisableButton(boolean enable, Button mButton, String value) {
        mButton.setEnabled(enable);
        mButton.setText(value);
    }

    @SuppressLint("SetTextI18n")
    public void initCustomButton(boolean isEnable, Button mButton, String value, int resource) {
        mButton.setEnabled(isEnable);
        if (isEnable) {
            mButton.setVisibility(View.VISIBLE);
            mButton.setText(value);
            mButton.setBackgroundResource(resource);
        } else {
            mButton.setText("Please Wait..");
            mButton.setBackgroundResource(R.drawable.check_score_button_style_disable);
        }
    }
}
