package com.app.rupyz.generic.model.organization.individual;

import com.google.gson.annotations.SerializedName;

public class AmountWorkingcapital {

    @SerializedName("count")
    private int count;

    @SerializedName("overdue_amount")
    private int overdueAmount;

    @SerializedName("current_balance")
    private Integer currentBalance;

    @SerializedName("sanctioned_amount")
    private Integer sanctionedAmount;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getOverdueAmount() {
        return overdueAmount;
    }

    public void setOverdueAmount(int overdueAmount) {
        this.overdueAmount = overdueAmount;
    }

    public Integer getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Integer currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Integer getSanctionedAmount() {
        return sanctionedAmount;
    }

    public void setSanctionedAmount(Integer sanctionedAmount) {
        this.sanctionedAmount = sanctionedAmount;
    }
}
