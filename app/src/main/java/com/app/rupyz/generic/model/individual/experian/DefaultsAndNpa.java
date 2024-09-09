package com.app.rupyz.generic.model.individual.experian;

import com.google.gson.annotations.SerializedName;

public class DefaultsAndNpa {
    @SerializedName("DPD")
    private int dPD;
    @SerializedName("SMA")
    private int sMA;
    @SerializedName("Loss")
    private int loss;

    public int getdPD() {
        return dPD;
    }

    public void setdPD(int dPD) {
        this.dPD = dPD;
    }

    public int getsMA() {
        return sMA;
    }

    public void setsMA(int sMA) {
        this.sMA = sMA;
    }

    public int getLoss() {
        return loss;
    }

    public void setLoss(int loss) {
        this.loss = loss;
    }

    public int getDoubtful() {
        return doubtful;
    }

    public void setDoubtful(int doubtful) {
        this.doubtful = doubtful;
    }

    public int getStandard() {
        return standard;
    }

    public void setStandard(int standard) {
        this.standard = standard;
    }

    public int getSubStandard() {
        return subStandard;
    }

    public void setSubStandard(int subStandard) {
        this.subStandard = subStandard;
    }

    @SerializedName("Doubtful")
    private int doubtful;
    @SerializedName("Standard")
    private int standard;
    @SerializedName("Sub Standard")
    private int subStandard;
}
