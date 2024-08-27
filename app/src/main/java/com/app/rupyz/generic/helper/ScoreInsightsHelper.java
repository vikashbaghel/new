package com.app.rupyz.generic.helper;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.rupyz.R;
import com.app.rupyz.generic.model.individual.experian.Tradeline;

import java.util.Date;
import java.util.List;

public class ScoreInsightsHelper {

    public String getNegativeMessage(int negativeCount, int npaCount, int overdueAmount) {
        try {
            int totalAccount = negativeCount + npaCount;
            if (totalAccount > 0) {
                if (overdueAmount > 0) {
                    return totalAccount + " Negative and default a/c with overdue " + overdueAmount;
                } else {
                    return totalAccount + " Negative and default a/c with Nil overdue";
                }
            } else {
                if (overdueAmount > 0) {
                    return "No Negative and default a/c with overdue " + overdueAmount;
                } else {
                    return "No Negative and default a/c with Nil overdue";
                }
            }
        } catch (Exception ex) {
            return "No Negative and default a/c with Nil overdue";
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

    public void setRepaymentStatus(int timely_payment, int total_payment, ImageView imageView,
                                   TextView txtStatus, TextView txtMessage, Context mContext) {
        try {
            float repayment_percentage = (timely_payment * 100) / total_payment;
            txtMessage.setText(repayment_percentage + "% repayment on time in last 3 years");
            if (repayment_percentage < 95) {
                txtStatus.setText(mContext.getResources().getString(R.string.poor));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_red));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_poor));
            } else if (repayment_percentage >= 95 && repayment_percentage < 97) {
                txtStatus.setText(mContext.getResources().getString(R.string.average));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_yellow));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_average));
            } else if (repayment_percentage >= 97 && repayment_percentage < 99.999) {
                txtStatus.setText(mContext.getResources().getString(R.string.good));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_green));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_good));
            } else if (repayment_percentage == 100) {
                txtStatus.setText(mContext.getResources().getString(R.string.excellent));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_green));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_excellent));
            }
        } catch (Exception ex) {

        }
    }

    public void setCreditCardStatus(List<Tradeline> mData, ImageView imageView,
                                    TextView txtStatus, TextView txtMessage, Context mContext) {
        try {
            double total_credit_card_amount = 0.00;
            double total_credit_card_utilization = 0.0;
            for (Tradeline Item : mData) {
                if (Item.getAccount_Type().equalsIgnoreCase("CREDIT CARD")) {
                    double highest_value = AmountHelper.convertStringToDouble(
                            Item.getHighest_Credit_or_Original_Loan_Amount());
                    if (highest_value == 0.0 || highest_value == 0) {
                        total_credit_card_amount = total_credit_card_amount + AmountHelper.convertStringToDouble(
                                Item.getCurrent_Balance());
                    } else {
                        total_credit_card_amount = total_credit_card_amount + AmountHelper.convertStringToDouble(
                                Item.getHighest_Credit_or_Original_Loan_Amount());
                    }
                    total_credit_card_utilization = total_credit_card_utilization + AmountHelper.convertStringToDouble(
                            Item.getCurrent_Balance());
                }
            }
            double credit_utilization_percentage = Math.round((total_credit_card_utilization * 100) / total_credit_card_amount);
            txtMessage.setText("Peak credit card utilisation of " + credit_utilization_percentage + "%");
            if (credit_utilization_percentage >= 70) {
                txtStatus.setText(mContext.getResources().getString(R.string.poor));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_red));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_poor));
            } else if (credit_utilization_percentage >= 41 && credit_utilization_percentage < 70) {
                txtStatus.setText(mContext.getResources().getString(R.string.average));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_green));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_average));
            } else if (credit_utilization_percentage >= 21 && credit_utilization_percentage < 40) {
                txtStatus.setText(mContext.getResources().getString(R.string.good));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_green));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_good));
            } else if (credit_utilization_percentage >= 0 && credit_utilization_percentage < 20) {
                txtStatus.setText(mContext.getResources().getString(R.string.excellent));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_green));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_excellent));
            }
        } catch (Exception ex) {

        }
    }

    public void setCreditCardStatusEquiFax(List<com.app.rupyz.generic.model.organization.individual.Tradeline> mData, ImageView imageView,
                                           TextView txtStatus, TextView txtMessage, Context mContext) {
        try {
            double total_credit_card_amount = 0.00;
            double total_credit_card_utilization = 0.0;
            for (com.app.rupyz.generic.model.organization.individual.Tradeline Item : mData) {
                if (Item.getAccount_type().equalsIgnoreCase("CREDIT CARD")) {
                    double highest_value =
                            Item.getSanction_amount();
                    if (highest_value == 0.0 || highest_value == 0) {
                        total_credit_card_amount = total_credit_card_amount +
                                Item.getCurrent_balance_amount();
                    } else {
                        total_credit_card_amount = total_credit_card_amount +
                                Item.getSanction_amount();
                    }
                    total_credit_card_utilization = total_credit_card_utilization +
                            Item.getCurrent_balance_amount();
                }
            }
            double credit_utilization_percentage = Math.round((total_credit_card_utilization * 100) / total_credit_card_amount);
            txtMessage.setText("Peak credit card utilisation of " + credit_utilization_percentage + "%");
            if (credit_utilization_percentage >= 70) {
                txtStatus.setText(mContext.getResources().getString(R.string.poor));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_red));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_poor));
            } else if (credit_utilization_percentage >= 41 && credit_utilization_percentage < 70) {
                txtStatus.setText(mContext.getResources().getString(R.string.average));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_green));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_average));
            } else if (credit_utilization_percentage >= 21 && credit_utilization_percentage < 40) {
                txtStatus.setText(mContext.getResources().getString(R.string.good));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_green));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_good));
            } else if (credit_utilization_percentage >= 0 && credit_utilization_percentage < 20) {
                txtStatus.setText(mContext.getResources().getString(R.string.excellent));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_green));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_excellent));
            }
        } catch (Exception ex) {

        }
    }

    public void setCreditAgeStatus(String credit_age, ImageView imageView,
                                   TextView txtStatus, TextView txtMessage, Context mContext) {
        try {
            txtMessage.setText("Credit age of " + credit_age);
            int age_limit = Integer.parseInt(credit_age.split(",")[0].replace("y", ""));
            int month = Integer.parseInt(credit_age.split(",")[1].replace("m", "").replace(" ", ""));
            Log.e("TAG", "setCreditAgeStatus1: " + age_limit + month);
            double age = Double.parseDouble(age_limit + "." + month);
            if (age < 1) {
                txtStatus.setText(mContext.getResources().getString(R.string.poor));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_red));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_poor));
            } else if (age > 1 && age <= 3) {
                txtStatus.setText(mContext.getResources().getString(R.string.average));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_green));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_average));
            } else if (age > 3 && age <= 5) {
                txtStatus.setText(mContext.getResources().getString(R.string.good));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_green));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_good));
            } else if (age > 5) {
                txtStatus.setText(mContext.getResources().getString(R.string.excellent));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_green));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_excellent));
            }
        } catch (Exception ex) {
            Log.e("TAG", "setCreditAgeStatus: " + ex.getMessage());
        }
    }

    public void setLoanStatus(List<Tradeline> mData, String loan_inquiry, ImageView imageView,
                              TextView txtStatus, TextView txtMessage, Context mContext) {
        try {
            int loanCount = 0;
            for (Tradeline Item : mData) {
                if (DateFormatHelper.getDifferenceBetweenDates(Item.getOpen_Date()) <= 0.6) {
                    loanCount++;
                }
            }
            int enquiry_count = Integer.parseInt(loan_inquiry) + loanCount;
            txtMessage.setText(loanCount + " new loan and " + loan_inquiry + " loan inquiries in last 6 month");
            if (enquiry_count <= 1) {
                txtStatus.setText(mContext.getResources().getString(R.string.excellent));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_green));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_excellent));
            } else if (enquiry_count >= 2 && enquiry_count <= 3) {
                txtStatus.setText(mContext.getResources().getString(R.string.average));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_yellow));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_average));
            } else if (enquiry_count > 3) {
                txtStatus.setText(mContext.getResources().getString(R.string.poor));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_red));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_poor));
            }
        } catch (Exception ex) {

        }
    }

    public void setLoanStatusEquiFax(List<com.app.rupyz.generic.model.organization.individual.Tradeline> mData, String loan_inquiry, ImageView imageView,
                                     TextView txtStatus, TextView txtMessage, Context mContext) {
        try {
            int loanCount = 0;
            for (com.app.rupyz.generic.model.organization.individual.Tradeline Item : mData) {
                if (DateFormatHelper.getDifferenceBetweenDates(Item.getDate_opened()) <= 0.6) {
                    loanCount++;
                }
            }
            int enquiry_count = Integer.parseInt(loan_inquiry) + loanCount;
            txtMessage.setText(loanCount + " new loan and " + loan_inquiry + " loan inquiries in last 6 month");
            if (enquiry_count <= 1) {
                txtStatus.setText(mContext.getResources().getString(R.string.excellent));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_green));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_excellent));
            } else if (enquiry_count >= 2 && enquiry_count <= 3) {
                txtStatus.setText(mContext.getResources().getString(R.string.average));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_yellow));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_average));
            } else if (enquiry_count > 3) {
                txtStatus.setText(mContext.getResources().getString(R.string.poor));
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.light_red));
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_poor));
            }
        } catch (Exception ex) {

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
