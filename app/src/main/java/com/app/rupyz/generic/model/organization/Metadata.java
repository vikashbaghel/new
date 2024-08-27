package com.app.rupyz.generic.model.organization;

public class Metadata {
    private String ref_uuid;

    public String getRef_uuid() {
        return ref_uuid;
    }

    public void setRef_uuid(String ref_uuid) {
        this.ref_uuid = ref_uuid;
    }

    public float getCommercial_progress_step() {
        return commercial_progress_step;
    }

    public void setCommercial_progress_step(float commercial_progress_step) {
        this.commercial_progress_step = commercial_progress_step;
    }

    public float getRetail_progress_step() {
        return retail_progress_step;
    }

    public void setRetail_progress_step(float retail_progress_step) {
        this.retail_progress_step = retail_progress_step;
    }

    public int getDays_remaining() {
        return days_remaining;
    }

    public void setDays_remaining(int days_remaining) {
        this.days_remaining = days_remaining;
    }

    private float commercial_progress_step;
    private float retail_progress_step;
    private int days_remaining;

    public boolean isIs_retail_masked() {
        return is_retail_masked;
    }

    public void setIs_retail_masked(boolean is_retail_masked) {
        this.is_retail_masked = is_retail_masked;
    }

    private boolean is_retail_masked;
}
