package com.app.rupyz.generic.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Button;
import android.widget.EditText;

public class OTPReceiver extends BroadcastReceiver {
    private  static EditText editText;
    private  static Button button;

    public void setEditText(EditText editText, Button button) {
        OTPReceiver.editText=editText;
        OTPReceiver.button=button;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for(SmsMessage sms : messages) {
            String msg = sms.getMessageBody();
            String firstFourChars = "";
            if (sms.getDisplayOriginatingAddress().contains("RUPYZZ") && msg.length() > 4) {
                firstFourChars = msg.substring(0, 4);
                editText.setText(firstFourChars);
                button.performClick();
            }
            else {
                editText.setText("");
            }
        }
    }
}