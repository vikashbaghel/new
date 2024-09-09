package com.app.rupyz.generic.model.organization;

public class OrganizationList {
    private int id;
    private String legal_name, selected_authorized_signatory, slug;
    private int reg_step;
    private String primary_gstin;
    private String pan_id;


    public String getSelected_authorized_signatory() {
        return selected_authorized_signatory;
    }

    public void setSelected_authorized_signatory(String selected_authorized_signatory) {
        this.selected_authorized_signatory = selected_authorized_signatory;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getPrimary_gstin() {
        return primary_gstin;
    }

    public void setPrimary_gstin(String primary_gstin) {
        this.primary_gstin = primary_gstin;
    }

    public String getLegal_name() {
        return legal_name;
    }

    public void setLegal_name(String legal_name) {
        this.legal_name = legal_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReg_step() {
        return reg_step;
    }

    public void setReg_step(int reg_step) {
        this.reg_step = reg_step;
    }

    public String getPan_id() {
        return pan_id;
    }

    public void setPan_id(String pan_id) {
        this.pan_id = pan_id;
    }
}
