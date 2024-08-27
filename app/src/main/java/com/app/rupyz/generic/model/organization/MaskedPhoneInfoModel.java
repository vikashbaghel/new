package com.app.rupyz.generic.model.organization;

import java.util.List;

public class MaskedPhoneInfoModel {
    public List<String> getMasked_phone_list() {
        return masked_phone_list;
    }

    public void setMasked_phone_list(List<String> masked_phone_list) {
        this.masked_phone_list = masked_phone_list;
    }

    private List<String> masked_phone_list;

    public List<String> getRetail_masked_phone_list() {
        return retail_masked_phone_list;
    }

    public void setRetail_masked_phone_list(List<String> retail_masked_phone_list) {
        this.retail_masked_phone_list = retail_masked_phone_list;
    }

    private List<String> retail_masked_phone_list;
}
