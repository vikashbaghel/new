package com.app.rupyz.generic.model.organization.individual;

import com.app.rupyz.generic.model.organization.Metadata;

public class EquiFaxIndividualInfoModel {
    private Metadata metadata;
    private Report report;

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public String getReport_for() {
        return report_for;
    }

    public void setReport_for(String report_for) {
        this.report_for = report_for;
    }

    private String report_for;
}
