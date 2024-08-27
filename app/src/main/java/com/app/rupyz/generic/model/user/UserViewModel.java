package com.app.rupyz.generic.model.user;

import android.os.Parcel;
import android.os.Parcelable;
import com.app.rupyz.model_kt.OrganizationInfoModel;
import java.util.List;

public class UserViewModel implements Parcelable {
    private String auth;
    private String user_id;
    private boolean login;
    private String first_name;
    private String last_name;
    private String username;
    private ExperianReportInfo experian_report_info;
    private String email, checkin_time, checkout_time;
    private String mobile;
    private String user_type;
    private boolean is_active;
    private boolean is_admin;
    private String profile_pic;
    private String rupyz_id;
    private String pan_id;
    private String dob;
    private List<OrganizationInfoModel> org_ids;
    private boolean experian_generated;
    private int reg_step;
    private String city;
    private String state;
    private String pincode;
    private String address_line_2;
    private String address_line_1;
    private String last_login;
    private Credentials credentials;

    protected UserViewModel(Parcel in) {
        auth = in.readString();
        user_id = in.readString();
        login = in.readByte() != 0;
        first_name = in.readString();
        last_name = in.readString();
        username = in.readString();
        email = in.readString();
        mobile = in.readString();
        user_type = in.readString();
        is_active = in.readByte() != 0;
        is_admin = in.readByte() != 0;
        profile_pic = in.readString();
        rupyz_id = in.readString();
        pan_id = in.readString();
        dob = in.readString();
        org_ids = in.createTypedArrayList(OrganizationInfoModel.CREATOR);
        experian_generated = in.readByte() != 0;
        reg_step = in.readInt();
        city = in.readString();
        state = in.readString();
        pincode = in.readString();
        address_line_2 = in.readString();
        address_line_1 = in.readString();
        last_login = in.readString();
        equifax_generated = in.readByte() != 0;
        gender = in.readString();
        middle_name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(auth);
        dest.writeString(user_id);
        dest.writeByte((byte) (login ? 1 : 0));
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(mobile);
        dest.writeString(user_type);
        dest.writeByte((byte) (is_active ? 1 : 0));
        dest.writeByte((byte) (is_admin ? 1 : 0));
        dest.writeString(profile_pic);
        dest.writeString(rupyz_id);
        dest.writeString(pan_id);
        dest.writeString(dob);
        dest.writeTypedList(org_ids);
        dest.writeByte((byte) (experian_generated ? 1 : 0));
        dest.writeInt(reg_step);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(pincode);
        dest.writeString(address_line_2);
        dest.writeString(address_line_1);
        dest.writeString(last_login);
        dest.writeByte((byte) (equifax_generated ? 1 : 0));
        dest.writeString(gender);
        dest.writeString(middle_name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserViewModel> CREATOR = new Creator<UserViewModel>() {
        @Override
        public UserViewModel createFromParcel(Parcel in) {
            return new UserViewModel(in);
        }

        @Override
        public UserViewModel[] newArray(int size) {
            return new UserViewModel[size];
        }
    };

    public ExperianReportInfo getExperian_report_info() {
        return experian_report_info;
    }

    public void setExperian_report_info(ExperianReportInfo experian_report_info) {
        this.experian_report_info = experian_report_info;
    }

    public boolean isExperian_generated() {
        return experian_generated;
    }

    public void setExperian_generated(boolean experian_generated) {
        this.experian_generated = experian_generated;
    }

    public boolean isEquifax_generated() {
        return equifax_generated;
    }

    public void setEquifax_generated(boolean equifax_generated) {
        this.equifax_generated = equifax_generated;
    }

    private boolean equifax_generated;

    public int getReg_step() {
        return reg_step;
    }

    public void setReg_step(int reg_step) {
        this.reg_step = reg_step;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getAddress_line_2() {
        return address_line_2;
    }

    public void setAddress_line_2(String address_line_2) {
        this.address_line_2 = address_line_2;
    }

    public String getAddress_line_1() {
        return address_line_1;
    }

    public void setAddress_line_1(String address_line_1) {
        this.address_line_1 = address_line_1;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    private String gender;

    public String getPan_id() {
        return pan_id;
    }

    public void setPan_id(String pan_id) {
        this.pan_id = pan_id;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    private String middle_name;

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public boolean isIs_admin() {
        return is_admin;
    }

    public void setIs_admin(boolean is_admin) {
        this.is_admin = is_admin;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getRupyz_id() {
        return rupyz_id;
    }

    public void setRupyz_id(String rupyz_id) {
        this.rupyz_id = rupyz_id;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public List<OrganizationInfoModel> getOrgIds() {
        return org_ids;
    }

    public void setOrgIds(List<OrganizationInfoModel> org_ids) {
        this.org_ids = org_ids;
    }

    public String getCheckin_time() {
        return checkin_time;
    }

    public void setCheckin_time(String checkin_time) {
        this.checkin_time = checkin_time;
    }

    public String getCheckout_time() {
        return checkout_time;
    }

    public void setCheckout_time(String checkout_time) {
        this.checkout_time = checkout_time;
    }

    public List<OrganizationInfoModel> getOrg_ids() {
        return org_ids;
    }

    public void setOrg_ids(List<OrganizationInfoModel> org_ids) {
        this.org_ids = org_ids;
    }
}
