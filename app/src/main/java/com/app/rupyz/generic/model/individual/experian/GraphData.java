package com.app.rupyz.generic.model.individual.experian;

import java.util.List;

public class GraphData {
    private List<String> date;
    private List<Double> amount;

    public List<String> getScore() {
        return score;
    }

    public void setScore(List<String> score) {
        this.score = score;
    }

    private List<String> score;

    public List<String> getDate() {
        return date;
    }

    public void setDate(List<String> date) {
        this.date = date;
    }

    public List<Double> getAmount() {
        return amount;
    }

    public void setAmount(List<Double> amount) {
        this.amount = amount;
    }

    public List<String> getAccount() {
        return account;
    }

    public void setAccount(List<String> account) {
        this.account = account;
    }

    private List<String> account;
}
