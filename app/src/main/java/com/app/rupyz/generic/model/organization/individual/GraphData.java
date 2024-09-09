package com.app.rupyz.generic.model.organization.individual;

import java.util.List;

public class GraphData {
    private List<String> date;
    private List<Integer> score;
    private List<Integer> active_accounts;

    public List<String> getDate() {
        return date;
    }

    public void setDate(List<String> date) {
        this.date = date;
    }

    public List<Integer> getScore() {
        return score;
    }

    public void setScore(List<Integer> score) {
        this.score = score;
    }

    public List<Integer> getActive_accounts() {
        return active_accounts;
    }

    public void setActive_accounts(List<Integer> active_accounts) {
        this.active_accounts = active_accounts;
    }

    public List<Integer> getCurrent_balance() {
        return current_balance;
    }

    public void setCurrent_balance(List<Integer> current_balance) {
        this.current_balance = current_balance;
    }

    private List<Integer> current_balance;
}
