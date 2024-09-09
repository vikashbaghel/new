package com.app.rupyz.generic.model.user;

public class ExperianReportInfo {
    private String user_type;
    private String last_report;

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getLast_report() {
        return last_report;
    }

    public void setLast_report(String last_report) {
        this.last_report = last_report;
    }

    public String getStage_one_id() {
        return stage_one_id;
    }

    public void setStage_one_id(String stage_one_id) {
        this.stage_one_id = stage_one_id;
    }

    private String stage_one_id;
}
