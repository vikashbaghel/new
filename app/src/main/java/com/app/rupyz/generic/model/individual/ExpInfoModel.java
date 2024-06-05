package com.app.rupyz.generic.model.individual;

public class ExpInfoModel {
    private ExpData exp_data;
    private int step;

    public ExpData getExp_data() {
        return exp_data;
    }

    public void setExp_data(ExpData exp_data) {
        this.exp_data = exp_data;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
}
