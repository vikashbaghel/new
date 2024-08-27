package com.app.rupyz.generic.model.organization.onboarding;

import java.util.List;

public class PanInfoModel {
    private boolean is_claim ;
    private String primary_gstin ;
    private String legal_name;

    public String getLegal_name() {
        return legal_name;
    }

    public void setLegal_name(String legal_name) {
        this.legal_name = legal_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String email;
    private int id;

    public String getPan_id() {
        return pan_id;
    }

    public void setPan_id(String pan_id) {
        this.pan_id = pan_id;
    }

    private String pan_id;

    public boolean isIs_claim() {
        return is_claim;
    }

    public void setIs_claim(boolean is_claim) {
        this.is_claim = is_claim;
    }

    public String getPrimary_gstin() {
        return primary_gstin;
    }

    public void setPrimary_gstin(String primary_gstin) {
        this.primary_gstin = primary_gstin;
    }

    public List<String> getAuthorized_signatories() {
        return authorized_signatories;
    }

    public void setAuthorized_signatories(List<String> authorized_signatories) {
        this.authorized_signatories = authorized_signatories;
    }

    private List<String> authorized_signatories ;
}
