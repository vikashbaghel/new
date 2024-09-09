package com.app.rupyz.generic.model.organization.individual;

import com.google.gson.annotations.SerializedName;

public class OwnershipMix {
    @SerializedName("Individual")
    private Individual individual;

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    public JointGuarantor getJointGuarantor() {
        return jointGuarantor;
    }

    public void setJointGuarantor(JointGuarantor jointGuarantor) {
        this.jointGuarantor = jointGuarantor;
    }

    @SerializedName("Joint/Guarantor")
    private JointGuarantor jointGuarantor;

}
