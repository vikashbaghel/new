package com.app.rupyz.generic.model.individual.experian;
import com.google.gson.annotations.SerializedName;
public class Ownership {
    @SerializedName("Individual")
    public int individual;

    public int getIndividual() {
        return individual;
    }

    public void setIndividual(int individual) {
        this.individual = individual;
    }

    public int getJointGuarantor() {
        return jointGuarantor;
    }

    public void setJointGuarantor(int jointGuarantor) {
        this.jointGuarantor = jointGuarantor;
    }

    @SerializedName("Joint/Guarantor")
    public int jointGuarantor;
}
