package com.app.rupyz.generic.helper;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.rupyz.R;
import com.app.rupyz.generic.model.individual.experian.Tradeline;
import com.app.rupyz.generic.model.organization.TradelinesItem;

import java.util.Date;
import java.util.List;

public class EquifaxScoreInsightsHelper {

    public String getNegativeMessage(Context mContext, int negativeCount, int npaCount, int overdueAmount) {
        try {
            if (negativeCount > 0) {
                if (npaCount > 0) {
                    if (overdueAmount > 0) {
                        return negativeCount + " Negative a/c, " + npaCount + " defaults a/c and overdue of "
                                + mContext.getResources().getString(R.string.rs) + overdueAmount;
                    } else {
                        return negativeCount + " Negative a/c, " + npaCount + " defaults a/c and No overdue";
                    }
                } else {
                    if (overdueAmount > 0) {
                        return negativeCount + " Negative a/c, No defaults a/c and overdue of "
                                + mContext.getResources().getString(R.string.rs) + overdueAmount;
                    } else {
                        return negativeCount + " Negative a/c, No defaults a/c and No overdue";
                    }
                }
            } else if (npaCount > 0) {
                if (overdueAmount > 0) {
                    return "No Negative a/c, " + npaCount + " defaults a/c and overdue of "
                            + mContext.getResources().getString(R.string.rs) + overdueAmount;
                } else {
                    return "No Negative a/c, " + npaCount + " defaults a/c and No overdue";
                }
            } else if (overdueAmount > 0) {
                return "No Negative a/c, No defaults a/c and overdue of "
                        + mContext.getResources().getString(R.string.rs) + overdueAmount;
            } else {
                return "No Negative a/c, No defaults a/c and No overdue";
            }
        } catch (Exception ex) {
            return "No Negative a/c, No defaults a/c and No overdue";
        }
    }

    public void setNegativeStatus(int negativeCount, int npaCount, int overdueAmount, ImageView imageView,
                                  TextView textView, Context mContext) {
        try {
            int totalAccount = negativeCount + npaCount;
            if (totalAccount == 0 && overdueAmount == 0) {
                textView.setText(mContext.getResources().getString(R.string.excellent));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_excellent));
            } else if (overdueAmount <= 5000) {
                textView.setText(mContext.getResources().getString(R.string.average));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_average));
            } else {
                textView.setText(mContext.getResources().getString(R.string.poor));
                textView.setTextColor(mContext.getResources().getColor(R.color.light_red));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_poor));
            }
        } catch (Exception ex) {
            textView.setText(mContext.getResources().getString(R.string.excellent));
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_excellent));
        }
    }

    public String setRepaymentStatus(Context mContext, int timely_payment, int total_payment) {
        try {
            float repayment_percentage = (timely_payment * 100) / total_payment;
            return repayment_percentage + "% repayment on time in last 3 years";
        } catch (Exception ex) {
            return "100% repayment on time in last 3 years";
        }
    }

    public String setCreditCardStatus(Context mContext, List<TradelinesItem> mData) {
        try {
            double total_credit_card_amount = 0.00;
            double total_credit_card_utilization = 0.0;
            for (TradelinesItem Item : mData) {
                if (Item.getCreditType().equalsIgnoreCase("CREDIT CARD")) {
                }
            }
            double credit_utilization_percentage = Math.round((total_credit_card_utilization * 100) / total_credit_card_amount);
            return ("Peak credit card utilisation of " + credit_utilization_percentage + "%");
        } catch (Exception ex) {
            return ("Peak credit card utilisation of 100%");
        }
    }

    public String setCreditAgeStatus(Context mContext, String credit_age) {
        try {
            return "Credit age of " + credit_age;
        } catch (Exception ex) {
            return "Credit age of nill";
        }
    }

    public String setLoanType(Context mContext, int private_bank, int public_bank,
                              int nbfc) {
        try {
            return private_bank + " Loans from Pvt Bank " + public_bank
                    + " loans from Public banks and " + nbfc + " Loans from NBFCs.";
        } catch (Exception ex) {
            return "Not Available";
        }
    }

    public String setBusinessVintage(Context mContext, String incorporate_date, String industry_type) {
        try {
            Date inc_date = DateFormatHelper.convertStringToDate(incorporate_date);
            long time_difference = new Date().getTime() - inc_date.getTime();
            long years_difference = (time_difference / (1000l * 60 * 60 * 24 * 365));
            return years_difference + " years of business vintage, " + industry_type;
        } catch (Exception ex) {
            return "Not Available";
        }
    }

    public String setLoanStatus(Context mContext, List<TradelinesItem> mData, int loan_inquiry) {
        try {
            int loanCount = 0;
            for (TradelinesItem Item : mData) {
                if (DateFormatHelper.getDifferenceBetweenDates(Item.getSanctionDate()) <= 0.11) {
                    loanCount++;
                }
            }
            return loanCount + " new loan and " + loan_inquiry + " loan inquiries in last 12 month";
        } catch (Exception ex) {
            return "";
        }
    }


    public void setFacilityMixStatus(int secured_loan, int un_secured_loan, ImageView imageView,
                                     TextView txtStatus, TextView txtMessage, Context mContext) {
        try {
            txtMessage.setText(secured_loan + " secured loan Vs " + un_secured_loan + " unsecured loan");
            if (un_secured_loan <= 2) {
                txtStatus.setText(mContext.getResources().getString(R.string.good));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_green));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_good));
            } else {
                txtStatus.setText(mContext.getResources().getString(R.string.average));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_green));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_average));
            }
        } catch (Exception ex) {
            Log.e("TAG", "setCreditAgeStatus: " + ex.getMessage());
        }
    }

}
