package com.app.rupyz.generic.model.organization;

import java.util.List;

public class GstViewModel {
    private int org_id;

    public int getOrg_id() {
        return org_id;
    }

    public void setOrg_id(int org_id) {
        this.org_id = org_id;
    }

    public List<GstinList> getGstin_list() {
        return gstin_list;
    }

    public void setGstin_list(List<GstinList> gstin_list) {
        this.gstin_list = gstin_list;
    }

    private List<GstinList> gstin_list;

    public String getLegal_name() {
        return legal_name;
    }

    public void setLegal_name(String legal_name) {
        this.legal_name = legal_name;
    }

    private String legal_name;
}
