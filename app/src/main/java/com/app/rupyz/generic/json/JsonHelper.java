package com.app.rupyz.generic.json;

import android.os.Build;
import android.util.Log;

import com.google.gson.JsonObject;

import java.util.List;

public class JsonHelper {
    public static String getCreateOrgJson(String pan_id, int org_id) {
        JsonObject json = new JsonObject();
        json.addProperty("pan_id", pan_id);
        json.addProperty("org_id", org_id);
        return json.toString();
    }

    public static String getFCMJson(String device_type, String device_manufacture, String os_type,
                                    String device_model, String fcm_token) {
        JsonObject json = new JsonObject();
        json.addProperty("device_type", device_type);
        json.addProperty("device_manufacture", device_manufacture);
        json.addProperty("os_type", os_type);
        json.addProperty("device_model", device_model);
        json.addProperty("fcm_token", fcm_token);
        return json.toString();
    }


    public static String getLogoutJson(String device_type) {
        JsonObject json = new JsonObject();
        json.addProperty("device_type", device_type);
        json.addProperty("device_model", Build.MODEL);
        return json.toString();
    }

    public static String getGenerateExpStep2Json(String session_id) {
        JsonObject json = new JsonObject();
        json.addProperty("session_id", session_id);
        return json.toString();
    }

    public static String getGenerateExpJson(String session_id, int id, String otp) {
        JsonObject json = new JsonObject();
        json.addProperty("session_id", session_id);
        json.addProperty("id", id);
        json.addProperty("otp", otp);
        return json.toString();
    }

    public static String getGenerateExpJson2Point1(String session_id, String mobile, String email) {
        JsonObject json = new JsonObject();
        json.addProperty("session_id", session_id);
        json.addProperty("mobile", mobile);
        json.addProperty("email", email);
        return json.toString();
    }

    public static String getEquiFaxOtpJson(int org_id, String otp, String otp_ref, Boolean is_retail_masked) {
        JsonObject json = new JsonObject();
        json.addProperty("org_id", org_id);
        json.addProperty("otp", otp);
        json.addProperty("otp_ref", otp_ref);
        json.addProperty("is_retail_masked", is_retail_masked);
        return json.toString();
    }


    public static String getUpdateProfile(String first_name, String last_name, String email,
                                          String pan_id, String middle_name, String dob,
                                          String city, String state, String pincode, String address_line_1,
                                          String address_line_2, String gender, boolean experian_consent) {
        JsonObject json = new JsonObject();
        json.addProperty("first_name", first_name);
        json.addProperty("last_name", last_name);
        json.addProperty("email", email);
        json.addProperty("pan_id", pan_id);
        json.addProperty("middle_name", middle_name);
        json.addProperty("dob", dob);
        json.addProperty("city", city);
        json.addProperty("state", state);
        json.addProperty("pincode", pincode);
        json.addProperty("address_line_1", address_line_1);
        json.addProperty("address_line_2", address_line_2);
        json.addProperty("gender", gender);
        json.addProperty("experian_consent", experian_consent);
        json.addProperty("equifax_consent", false);
        Log.e("TAG", "getUpdateProfile: " + json);

        return json.toString();
    }

    public static String getSmartMatch(String first_name, String last_name, String email,
                                       String middle_name, boolean experian_consent) {
        JsonObject json = new JsonObject();
        json.addProperty("first_name", first_name);
        json.addProperty("last_name", last_name);
        json.addProperty("email", email);
        json.addProperty("middle_name", middle_name);
        json.addProperty("experian_consent", experian_consent);
        json.addProperty("equifax_consent", false);
        Log.e("TAG", "getUpdateProfile: " + json);

        return json.toString();
    }

    public static String getGSTJson(String primary_gstin, String legal_name, int org_id) {
        JsonObject json = new JsonObject();
        json.addProperty("primary_gstin", primary_gstin);
        json.addProperty("legal_name", legal_name);
        json.addProperty("org_id", org_id);
        return json.toString();
    }

    public static String getAuthSignatoryJson(String selected_authorized_signatory, int org_id) {
        JsonObject json = new JsonObject();
        json.addProperty("selected_authorized_signatory", selected_authorized_signatory);
        json.addProperty("org_id", org_id);
        return json.toString();
    }

    public static String getClaimProfileJson(String pan_id, String primary_gstin, List<String> authorized_signatories) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("pan_id", pan_id);
        jsonObject.addProperty("primary_gstin", primary_gstin);
        jsonObject.addProperty("authorized_signatories", authorized_signatories.toString());
        return jsonObject.toString();
    }

    public static String getSaveGSTJson(String primary_gstin, boolean is_skip_gstin) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("primary_gstin", primary_gstin);
        jsonObject.addProperty("is_skip_gstin", is_skip_gstin);
        return jsonObject.toString();
    }

    public static String getCommercialMaskedPhoneJson(int org_id, String correct_mobile) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("org_id", org_id);
        JsonObject additional_info_json = new JsonObject();
        additional_info_json.addProperty("correct_mobile", correct_mobile);
        jsonObject.add("additional_info_json", additional_info_json);
        return jsonObject.toString();
    }

    public static String getAddStaffJson(String name, String mobile, String employee_id, String designation
            , String department) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("mobile", mobile);
        jsonObject.addProperty("employee_id", employee_id);
        jsonObject.addProperty("designation", designation);
        jsonObject.addProperty("department", department);
        return jsonObject.toString();
    }

    public static String getRecordPaymentJson(int customer, double amount, String payment_mode, String transaction_ref_no
            , String comment) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("customer", customer);
        jsonObject.addProperty("amount", amount);
        jsonObject.addProperty("payment_mode", payment_mode);
        jsonObject.addProperty("transaction_ref_no", transaction_ref_no);
        jsonObject.addProperty("comment", comment);
        return jsonObject.toString();
    }

}
