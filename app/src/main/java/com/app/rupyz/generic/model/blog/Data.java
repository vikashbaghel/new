
package com.app.rupyz.generic.model.blog;

import java.util.List;

public class Data {

    private List<ComplianceCalender> compliance_calender = null;
    private List<ExperianEmi> experian_emi = null;
    private List<EquifaxEmi> equifax_emi = null;
    private List<Microblog> microblogs = null;

    public List<ComplianceCalender> getComplianceCalender() {
        return compliance_calender;
    }

    public void setComplianceCalender(List<ComplianceCalender> compliance_calender) {
        this.compliance_calender = compliance_calender;
    }

    public List<ExperianEmi> getExperianEmi() {
        return experian_emi;
    }

    public void setExperianEmi(List<ExperianEmi> experian_emi) {
        this.experian_emi = experian_emi;
    }

    public List<Microblog> getMicroblogs() {
        return microblogs;
    }

    public void setMicroblogs(List<Microblog> microblogs) {
        this.microblogs = microblogs;
    }

    public List<EquifaxEmi> getEquifaxEmi() {
        return equifax_emi;
    }

    public void setEquifaxEmi(List<EquifaxEmi> equifax_emi) {
        this.equifax_emi = equifax_emi;
    }

}
