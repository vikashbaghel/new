package com.app.rupyz.generic.model.organization.individual;

import com.google.gson.annotations.SerializedName;

public class AmountNonfundbased {

    @SerializedName("count")
    private int count;

    @SerializedName("overdue_amount")
    private int overdueAmount;

    @SerializedName("current_balance")
    private int currentBalance;

    @SerializedName("sanctioned_amount")
    private int sanctionedAmount;

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

    public int getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(int currentBalance) {
        this.currentBalance = currentBalance;
    }

    public int getSanctionedAmount() {
        return sanctionedAmount;
    }

    public void setSanctionedAmount(int sanctionedAmount) {
        this.sanctionedAmount = sanctionedAmount;
    }
}
