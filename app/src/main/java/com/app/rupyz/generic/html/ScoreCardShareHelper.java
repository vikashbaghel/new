package com.app.rupyz.generic.html;

import android.content.Context;

import com.app.rupyz.R;
import com.app.rupyz.generic.helper.DateFormatHelper;
import com.app.rupyz.generic.model.individual.experian.ExperianInfoModel;
import com.app.rupyz.generic.model.organization.EquiFaxInfoModel;
import com.app.rupyz.generic.model.organization.individual.EquiFaxIndividualInfoModel;
import com.app.rupyz.ui.home.HomeFragment;

public class ScoreCardShareHelper {

    public static String
    cardShareData(ExperianInfoModel mData, Context mContext) {
        String name = mData.getRelationship_details()
                .getCurrent_Applicant_Details().getFirst_Name() + " " + HomeFragment.mData.getRelationship_details()
                .getCurrent_Applicant_Details().getLast_Name().toLowerCase();
        String score_value = mData.getScore_value() + "";
        String risk_rank = "";
        String risk_rank_color_code = "";
        if (mData.getScore_comment().equalsIgnoreCase("H")) {
            risk_rank = mContext.getResources().getString(R.string.low_risk);
            risk_rank_color_code = "#66bb6a";
        } else if (mData.getScore_comment().equalsIgnoreCase("M")) {
            risk_rank = mContext.getResources().getString(R.string.medium_risk);
            risk_rank_color_code = "#F9A825";
        } else if (mData.getScore_comment().equalsIgnoreCase("L")) {
            risk_rank = mContext.getResources().getString(R.string.high_risk);
            risk_rank_color_code = "#ef5350";
        }

        String credit_age = mData.getCredit_age();
        String repayment = mData.getRepayments_ontime_count()
                + "/" + (mData.getRepayments_ontime_count() + mData.getRepayments_missed_count()) + " On time";
        String month = DateFormatHelper.getProfileDate(HomeFragment.mData.getUpdated_at());
        String score_text = "";
        int score_percentage = 0;
        int score = mData.getScore_value();
        if (score >= 300 && score <= 650) {
            score_text = "Poor";
            score_percentage = (((HomeFragment.mData.getScore_value() - 300) * 100) / 600);
        } else if (score >= 651 && score <= 770) {
            score_text = "Average";
            score_percentage = (((HomeFragment.mData.getScore_value() - 300) * 100) / 600);
        } else if (score >= 771 && score <= 850) {
            score_text = "Good";
            score_percentage = (((HomeFragment.mData.getScore_value() - 300) * 100) / 600);
        } else if (score >= 851 && score <= 900) {
            score_text = "Excellent ";
            score_percentage = ((HomeFragment.mData.getScore_value() - 300) * 100) / 600;
        }

        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "  <head>\n" +
                "    <meta charset=\"UTF-8\" />\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "    <link rel=\"preconnect\" href=\"https://fonts.googleapis.com\" />\n" +
                "    <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin />\n" +
                "    <link\n" +
                "      href=\"https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700;800;900&display=swap\"\n" +
                "      rel=\"stylesheet\"\n" +
                "    />\n" +
                "    <title>Document</title>\n" +
                "\n" +
                "    <style>\n" +
                "      body {\n" +
                "        font-family: \"Poppins\", sans-serif;\n" +
                "        background-image: url(\"file:///android_res/mipmap/home_bg.png\");\n" +
                "        margin: 0;\n" +
                "        padding: 0;\n" +
                "      }\n" +
                "\n" +
                "      main {\n" +
                "        display: flex;\n" +
                "        justify-content: center;\n" +
                "      }\n" +
                "\n" +
                "      .name {\n" +
                "        font-size: 20px;\n" +
                "      }\n" +
                "\n" +
                "      .detailsDiv {\n" +
                "        width: 350px;\n" +
                "        min-height: 200px;\n" +
                "        padding: 10px;\n" +
                "        background-color: rgba(255, 255, 255, 0.452);\n" +
                "        border-radius: 10px;\n" +
                "        border: 1px solid white;\n" +
                "      }\n" +
                "\n" +
                "      .detailsDiv .score {\n" +
                "        display: flex;\n" +
                "        justify-content: space-between;\n" +
                "      }\n" +
                "\n" +
                "      .bar {\n" +
                "        display: flex;\n" +
                "        position: relative;\n" +
                "      }\n" +
                "\n" +
                "      .bar div {\n" +
                "        background-color: black;\n" +
                "        width: 100px;\n" +
                "        margin-left: 2px;\n" +
                "      }\n" +
                "\n" +
                "      .bar .bar-1 {\n" +
                "        border-radius: 10px 0 0 10px;\n" +
                "        height: 8px;\n" +
                "        width: 233.2px;\n" +
                "        background: linear-gradient(#e57373, #ef5350);\n" +
                "      }\n" +
                "\n" +
                "      .bar .bar-2 {\n" +
                "        height: 8px;\n" +
                "        width: 80px;\n" +
                "        background: linear-gradient(#ffb74d, #ffa726);\n" +
                "      }\n" +
                "\n" +
                "      .bar .bar-3 {\n" +
                "        height: 8px;\n" +
                "        width: 53.2px;\n" +
                "        background: linear-gradient(#81c784, #a5d6a7);\n" +
                "      }\n" +
                "\n" +
                "      .bar .bar-4 {\n" +
                "        border-radius: 0 10px 10px 0;\n" +
                "        width: 33.2px;\n" +
                "        background: linear-gradient(#4caf50, #66bb6a);\n" +
                "      }\n" +
                "\n" +
                "      .rankAndAge {\n" +
                "        margin-top: 10px;\n" +
                "        display: flex;\n" +
                "        justify-content: space-between;\n" +
                "      }\n" +
                "\n" +
                "      .rankAndAge p {\n" +
                "        font-size: 12px;\n" +
                "      }\n" +
                "\n" +
                "      .repaymentsAndDate {\n" +
                "        display: flex;\n" +
                "        justify-content: space-between;\n" +
                "      }\n" +
                "\n" +
                "      .repaymentsAndDate p,\n" +
                "      h5 {\n" +
                "        margin: 0;\n" +
                "      }\n" +
                "\n" +
                "      .repaymentsAndDate p {\n" +
                "        font-size: 12px;\n" +
                "      }\n" +
                "\n" +
                "      .experianImageDiv {\n" +
                "        display: flex;\n" +
                "        align-items: center;\n" +
                "      }\n" +
                "\n" +
                "      .arrowImageDiv {\n" +
                "        display: flex;\n" +
                "        flex-direction: column;\n" +
                "        margin-left: 96%;\n" +
                "      }\n" +
                "\n" +
                "      .arrowImageText {\n" +
                "        font-size: 9px;\n" +
                "        position: relative;\n" +
                "        right: 5px;\n" +
                "        margin: 0;\n" +
                "      }\n" +
                "\n" +
                "      .arrowimage {\n" +
                "        margin-top: 0;\n" +
                "        height: 12px;\n" +
                "        width: 12px;\n" +
                "      }\n" +
                "\n" +
                "      .bar-1::after {\n" +
                "        font-size: 8px;\n" +
                "        content: \"300\";\n" +
                "      }\n" +
                "      .bar-2::after {\n" +
                "        position: relative;\n" +
                "        right: 8px;\n" +
                "        font-size: 8px;\n" +
                "        content: \"650\";\n" +
                "      }\n" +
                "      .bar-3::after {\n" +
                "        position: relative;\n" +
                "        right: 8px;\n" +
                "        font-size: 8px;\n" +
                "        content: \"770\";\n" +
                "      }\n" +
                "      .bar-4::after {\n" +
                "        position: absolute;\n" +
                "        right: 0;\n" +
                "        margin-top: 10px;\n" +
                "        font-size: 8px;\n" +
                "        content: \"900\";\n" +
                "      }\n" +
                "\n" +
                "      .date {\n" +
                "        display: flex;\n" +
                "        align-items: center;\n" +
                "        background-color: #00008b;\n" +
                "        color: white;\n" +
                "        padding: 5px 20px;\n" +
                "        border-radius: 30px;\n" +
                "      }\n" +
                "\n" +
                "      .date p {\n" +
                "        font-size: 10px;\n" +
                "      }\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <main>\n" +
                "      <div>\n" +
                "        <p class=\"name\">Hi, <strong>" + name + "</strong></p>\n" +
                "        <div class=\"detailsDiv\">\n" +
                "          <div class=\"score\">\n" +
                "            <h3 style=\"font-size: 32px; margin: 0\"><strong>" + score_value + "</strong></h3>\n" +
                "            <div class=\"experianImageDiv\">\n" +
                "              <img width=\"50\" height=\"16\" src=\"file:///android_res/mipmap/experian_logo.png\" alt=\"\" />\n" +
                "            </div>\n" +
                "          </div>\n" +
                "          <div style=\"margin-left: " + score_percentage + "%;\" class=\"arrowImageDiv\">\n" +
                "            <p class=\"arrowImageText\">" + score_text + "</p>\n" +
                "            <img class=\"arrowimage\" src=\"file:///android_res/mipmap/ic_down_arrow_fill.png\" alt=\"\" />\n" +
                "          </div>\n" +
                "          <div class=\"bar\">\n" +
                "            <div class=\"bar-1\"></div>\n" +
                "            <div class=\"bar-2\"></div>\n" +
                "            <div class=\"bar-3\"></div>\n" +
                "            <div class=\"bar-4\"></div>\n" +
                "          </div>\n" +
                "          <div class=\"rankAndAge\">\n" +
                "            <p >Risk rank - <span style=\"color: " + risk_rank_color_code + "\"> " + risk_rank + "</span></p>\n" +
                "            <p style=\"color:#6B679F\">Credit age - " + credit_age + "</p>\n" +
                "          </div>\n" +
                "          <div style=\"margin-top: 10px\" class=\"repaymentsAndDate\">\n" +
                "            <div>\n" +
                "              <p style=\"font-weight: 400\">Repayments</p>\n" +
                "              <p style=\"font-size: 10px\">" + repayment + "</p>\n" +
                "            </div>\n" +
                "            <div class=\"date\">\n" +
                "              <p>" + month + "</p>\n" +
                "            </div>\n" +
                "          </div>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "    </main>\n" +
                "  </body>\n" +
                "</html>\n";
    }

    public static String
    commercialCardShareData(EquiFaxInfoModel mData, Context mContext) {
        String name = mData.getReport().getLegalName();
        String score_value = mData.getReport().getScoreValue() + "";
        String risk_rank = "";
        String risk_rank_color_code = "";

        if (mData.getReport().getScoreComment().equalsIgnoreCase("Low Risk")) {
            risk_rank = mContext.getResources().getString(R.string.low_risk);
            risk_rank_color_code = "#66bb6a";
        } else if (mData.getReport().getScoreComment().equalsIgnoreCase("High Risk")) {
            risk_rank = mContext.getResources().getString(R.string.high_risk);
            risk_rank_color_code = "#ef5350";
        } else if (mData.getReport().getScoreComment().equalsIgnoreCase("Very High Risk")) {
            risk_rank = mContext.getResources().getString(R.string.very_high_risk);
            risk_rank_color_code = "#ef5350";
        } else if (mData.getReport().getScoreComment().equalsIgnoreCase("Medium Risk")) {
            risk_rank = mContext.getResources().getString(R.string.medium_risk);
            risk_rank_color_code = "#F4D700";
        }

        String credit_age = mData.getReport().getCreditAge();
        String repayment = mData.getReport().getRepaymentsTotalCount() - mData.getReport().getRepaymentsMissedCount()
                + "/" + mData.getReport().getRepaymentsTotalCount() + " On time";
        String month = DateFormatHelper.getProfileDate(mData.getReport().getUpdatedAt());
        String score_text = "";
        int score_percentage = 0;

        int score = mData.getReport().getScoreValue();
        if (score >= 1 && score <= 2) {
            score_text = "Excellent";
            score_percentage = (((mData.getReport().getScoreValue()) * 10));
        } else if (score >= 3 && score <= 4) {
            score_text = "Good";
            score_percentage = (((mData.getReport().getScoreValue()) * 10));
        } else if (score >= 5 && score <= 7) {
            score_text = "Average";
            score_percentage = (((mData.getReport().getScoreValue()) * 10));
        } else if (score >= 8 && score <= 10) {
            score_text = "Poor ";
            score_percentage = ((mData.getReport().getScoreValue()) * 10);
        }


        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "  <head>\n" +
                "    <meta charset=\"UTF-8\" />\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "    <link rel=\"preconnect\" href=\"https://fonts.googleapis.com\" />\n" +
                "    <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin />\n" +
                "    <link\n" +
                "      href=\"https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700;800;900&display=swap\"\n" +
                "      rel=\"stylesheet\"\n" +
                "    />\n" +
                "    <title>Document</title>\n" +
                "\n" +
                "    <style>\n" +
                "      body {\n" +
                "        font-family: \"Poppins\", sans-serif;\n" +
                "        background-image: url(\"file:///android_res/mipmap/home_bg.png\");\n" +
                "        margin: 0;\n" +
                "        padding: 0;\n" +
                "      }\n" +
                "\n" +
                "      main {\n" +
                "        display: flex;\n" +
                "        justify-content: center;\n" +
                "      }\n" +
                "\n" +
                "      .name {\n" +
                "        font-size: 20px;\n" +
                "      }\n" +
                "\n" +
                "      .detailsDiv {\n" +
                "        width: 350px;\n" +
                "        min-height: 200px;\n" +
                "        padding: 10px;\n" +
                "        background-color: rgba(255, 255, 255, 0.452);\n" +
                "        border-radius: 10px;\n" +
                "        border: 1px solid white;\n" +
                "      }\n" +
                "\n" +
                "      .detailsDiv .score {\n" +
                "        display: flex;\n" +
                "        justify-content: space-between;\n" +
                "      }\n" +
                "\n" +
                "      .bar {\n" +
                "        display: flex;\n" +
                "        position: relative;\n" +
                "      }\n" +
                "\n" +
                "      .bar div {\n" +
                "        background-color: black;\n" +
                "        width: 100px;\n" +
                "        margin-left: 2px;\n" +
                "      }\n" +
                "\n" +
                "      .bar .bar-1 {\n" +
                "        border-radius: 10px 0 0 10px;\n" +
                "        height: 8px;\n" +
                "        width: 233.2px;\n" +
                "        background: linear-gradient(#e57373, #ef5350);\n" +
                "      }\n" +
                "\n" +
                "      .bar .bar-2 {\n" +
                "        height: 8px;\n" +
                "        width: 80px;\n" +
                "        background: linear-gradient(#ffb74d, #ffa726);\n" +
                "      }\n" +
                "\n" +
                "      .bar .bar-3 {\n" +
                "        height: 8px;\n" +
                "        width: 53.2px;\n" +
                "        background: linear-gradient(#81c784, #a5d6a7);\n" +
                "      }\n" +
                "\n" +
                "      .bar .bar-4 {\n" +
                "        border-radius: 0 10px 10px 0;\n" +
                "        width: 33.2px;\n" +
                "        background: linear-gradient(#4caf50, #66bb6a);\n" +
                "      }\n" +
                "\n" +
                "      .rankAndAge {\n" +
                "        margin-top: 10px;\n" +
                "        display: flex;\n" +
                "        justify-content: space-between;\n" +
                "      }\n" +
                "\n" +
                "      .rankAndAge p {\n" +
                "        font-size: 12px;\n" +
                "      }\n" +
                "\n" +
                "      .repaymentsAndDate {\n" +
                "        display: flex;\n" +
                "        justify-content: space-between;\n" +
                "      }\n" +
                "\n" +
                "      .repaymentsAndDate p,\n" +
                "      h5 {\n" +
                "        margin: 0;\n" +
                "      }\n" +
                "\n" +
                "      .repaymentsAndDate p {\n" +
                "        font-size: 12px;\n" +
                "      }\n" +
                "\n" +
                "      .experianImageDiv {\n" +
                "        display: flex;\n" +
                "        align-items: center;\n" +
                "      }\n" +
                "\n" +
                "      .arrowImageDiv {\n" +
                "        display: flex;\n" +
                "        flex-direction: column;\n" +
                "        margin-left: 96%;\n" +
                "      }\n" +
                "\n" +
                "      .arrowImageText {\n" +
                "        font-size: 9px;\n" +
                "        position: relative;\n" +
                "        right: 5px;\n" +
                "        margin: 0;\n" +
                "      }\n" +
                "\n" +
                "      .arrowimage {\n" +
                "        margin-top: 0;\n" +
                "        height: 12px;\n" +
                "        width: 12px;\n" +
                "      }\n" +
                "\n" +
                "      .bar-1::after {\n" +
                "        font-size: 8px;\n" +
                "        content: \"300\";\n" +
                "      }\n" +
                "      .bar-2::after {\n" +
                "        position: relative;\n" +
                "        right: 8px;\n" +
                "        font-size: 8px;\n" +
                "        content: \"650\";\n" +
                "      }\n" +
                "      .bar-3::after {\n" +
                "        position: relative;\n" +
                "        right: 8px;\n" +
                "        font-size: 8px;\n" +
                "        content: \"770\";\n" +
                "      }\n" +
                "      .bar-4::after {\n" +
                "        position: absolute;\n" +
                "        right: 0;\n" +
                "        margin-top: 10px;\n" +
                "        font-size: 8px;\n" +
                "        content: \"900\";\n" +
                "      }\n" +
                "\n" +
                "      .date {\n" +
                "        display: flex;\n" +
                "        align-items: center;\n" +
                "        background-color: #00008b;\n" +
                "        color: white;\n" +
                "        padding: 5px 20px;\n" +
                "        border-radius: 30px;\n" +
                "      }\n" +
                "\n" +
                "      .date p {\n" +
                "        font-size: 10px;\n" +
                "      }\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <main>\n" +
                "      <div>\n" +
                "        <p class=\"name\">Hi, <strong>" + name + "</strong></p>\n" +
                "        <div class=\"detailsDiv\">\n" +
                "          <div class=\"score\">\n" +
                "            <h3 style=\"font-size: 32px; margin: 0\"><strong>" + score_value + "</strong></h3>\n" +
                "            <div class=\"experianImageDiv\">\n" +
                "              <img width=\"50\" height=\"16\" src=\"file:///android_res/mipmap/ic_equifax_logo.png\" alt=\"\" />\n" +
                "            </div>\n" +
                "          </div>\n" +
                "          <div style=\"margin-left: " + score_percentage + "%;\" class=\"arrowImageDiv\">\n" +
                "            <p class=\"arrowImageText\">" + score_text + "</p>\n" +
                "            <img class=\"arrowimage\" src=\"file:///android_res/mipmap/ic_down_arrow_fill.png\" alt=\"\" />\n" +
                "          </div>\n" +
                "          <div class=\"bar\">\n" +
                "            <div class=\"bar-1\"></div>\n" +
                "            <div class=\"bar-2\"></div>\n" +
                "            <div class=\"bar-3\"></div>\n" +
                "            <div class=\"bar-4\"></div>\n" +
                "          </div>\n" +
                "          <div class=\"rankAndAge\">\n" +
                "            <p >Risk rank - <span style=\"color: " + risk_rank_color_code + "\"> " + risk_rank + "</span></p>\n" +
                "            <p style=\"color:#6B679F\">Credit age - " + credit_age + "</p>\n" +
                "          </div>\n" +
                "          <div style=\"margin-top: 10px\" class=\"repaymentsAndDate\">\n" +
                "            <div>\n" +
                "              <p style=\"font-weight: 400\">Repayments</p>\n" +
                "              <p style=\"font-size: 10px\">" + repayment + "</p>\n" +
                "            </div>\n" +
                "            <div class=\"date\">\n" +
                "              <p>" + month + "</p>\n" +
                "            </div>\n" +
                "          </div>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "    </main>\n" +
                "  </body>\n" +
                "</html>\n";
    }

    public static String
    individualCardShareData(EquiFaxIndividualInfoModel mData, Context mContext) {
        String name = mData.getReport().getFull_name();
        String score_value = mData.getReport().getScore_value() + "";
        String risk_rank = "";
        String risk_rank_color_code = "";

        if (mData.getReport().getScore_comment().equalsIgnoreCase("Low Risk")) {
            risk_rank = mContext.getResources().getString(R.string.low_risk);
            risk_rank_color_code = "#66bb6a";
        } else if (mData.getReport().getScore_comment().equalsIgnoreCase("High Risk")) {
            risk_rank = mContext.getResources().getString(R.string.high_risk);
            risk_rank_color_code = "#ef5350";
        } else if (mData.getReport().getScore_comment().equalsIgnoreCase("Very High Risk")) {
            risk_rank = mContext.getResources().getString(R.string.very_high_risk);
            risk_rank_color_code = "#ef5350";
        } else if (mData.getReport().getScore_comment().equalsIgnoreCase("Medium Risk")) {
            risk_rank = mContext.getResources().getString(R.string.medium_risk);
            risk_rank_color_code = "#F4D700";
        }

        String credit_age = mData.getReport().getCredit_age();
        String repayment = mData.getReport().getRepayments_total_count() - mData.getReport().getRepayments_missed_count()
                + "/" + (mData.getReport().getRepayments_total_count() + " On time");
        String month = DateFormatHelper.getProfileDate(mData.getReport().getUpdated_at());
        String score_text = "";
        int score_percentage = 0;

        int score = mData.getReport().getScore_value();
        if (score >= 300 && score <= 650) {
            score_text = "Poor";
            score_percentage = (((mData.getReport().getScore_value() - 300) * 100) / 600);
        } else if (score >= 651 && score <= 770) {
            score_text = "Average";
            score_percentage = (((mData.getReport().getScore_value() - 300) * 100) / 600);
        } else if (score >= 771 && score <= 850) {
            score_text = "Good";
            score_percentage = (((mData.getReport().getScore_value() - 300) * 100) / 600);
        } else if (score >= 851 && score <= 900) {
            score_text = "Excellent ";
            score_percentage = ((mData.getReport().getScore_value() - 300) * 100) / 600;
        }


        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "  <head>\n" +
                "    <meta charset=\"UTF-8\" />\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "    <link rel=\"preconnect\" href=\"https://fonts.googleapis.com\" />\n" +
                "    <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin />\n" +
                "    <link\n" +
                "      href=\"https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700;800;900&display=swap\"\n" +
                "      rel=\"stylesheet\"\n" +
                "    />\n" +
                "    <title>Document</title>\n" +
                "\n" +
                "    <style>\n" +
                "      body {\n" +
                "        font-family: \"Poppins\", sans-serif;\n" +
                "        background-image: url(\"file:///android_res/mipmap/home_bg.png\");\n" +
                "        margin: 0;\n" +
                "        padding: 0;\n" +
                "      }\n" +
                "\n" +
                "      main {\n" +
                "        display: flex;\n" +
                "        justify-content: center;\n" +
                "      }\n" +
                "\n" +
                "      .name {\n" +
                "        font-size: 20px;\n" +
                "      }\n" +
                "\n" +
                "      .detailsDiv {\n" +
                "        width: 350px;\n" +
                "        min-height: 200px;\n" +
                "        padding: 10px;\n" +
                "        background-color: rgba(255, 255, 255, 0.452);\n" +
                "        border-radius: 10px;\n" +
                "        border: 1px solid white;\n" +
                "      }\n" +
                "\n" +
                "      .detailsDiv .score {\n" +
                "        display: flex;\n" +
                "        justify-content: space-between;\n" +
                "      }\n" +
                "\n" +
                "      .bar {\n" +
                "        display: flex;\n" +
                "        position: relative;\n" +
                "      }\n" +
                "\n" +
                "      .bar div {\n" +
                "        background-color: black;\n" +
                "        width: 100px;\n" +
                "        margin-left: 2px;\n" +
                "      }\n" +
                "\n" +
                "      .bar .bar-1 {\n" +
                "        border-radius: 10px 0 0 10px;\n" +
                "        height: 8px;\n" +
                "        width: 233.2px;\n" +
                "        background: linear-gradient(#e57373, #ef5350);\n" +
                "      }\n" +
                "\n" +
                "      .bar .bar-2 {\n" +
                "        height: 8px;\n" +
                "        width: 80px;\n" +
                "        background: linear-gradient(#ffb74d, #ffa726);\n" +
                "      }\n" +
                "\n" +
                "      .bar .bar-3 {\n" +
                "        height: 8px;\n" +
                "        width: 53.2px;\n" +
                "        background: linear-gradient(#81c784, #a5d6a7);\n" +
                "      }\n" +
                "\n" +
                "      .bar .bar-4 {\n" +
                "        border-radius: 0 10px 10px 0;\n" +
                "        width: 33.2px;\n" +
                "        background: linear-gradient(#4caf50, #66bb6a);\n" +
                "      }\n" +
                "\n" +
                "      .rankAndAge {\n" +
                "        margin-top: 10px;\n" +
                "        display: flex;\n" +
                "        justify-content: space-between;\n" +
                "      }\n" +
                "\n" +
                "      .rankAndAge p {\n" +
                "        font-size: 12px;\n" +
                "      }\n" +
                "\n" +
                "      .repaymentsAndDate {\n" +
                "        display: flex;\n" +
                "        justify-content: space-between;\n" +
                "      }\n" +
                "\n" +
                "      .repaymentsAndDate p,\n" +
                "      h5 {\n" +
                "        margin: 0;\n" +
                "      }\n" +
                "\n" +
                "      .repaymentsAndDate p {\n" +
                "        font-size: 12px;\n" +
                "      }\n" +
                "\n" +
                "      .experianImageDiv {\n" +
                "        display: flex;\n" +
                "        align-items: center;\n" +
                "      }\n" +
                "\n" +
                "      .arrowImageDiv {\n" +
                "        display: flex;\n" +
                "        flex-direction: column;\n" +
                "        margin-left: 96%;\n" +
                "      }\n" +
                "\n" +
                "      .arrowImageText {\n" +
                "        font-size: 9px;\n" +
                "        position: relative;\n" +
                "        right: 5px;\n" +
                "        margin: 0;\n" +
                "      }\n" +
                "\n" +
                "      .arrowimage {\n" +
                "        margin-top: 0;\n" +
                "        height: 12px;\n" +
                "        width: 12px;\n" +
                "      }\n" +
                "\n" +
                "      .bar-1::after {\n" +
                "        font-size: 8px;\n" +
                "        content: \"300\";\n" +
                "      }\n" +
                "      .bar-2::after {\n" +
                "        position: relative;\n" +
                "        right: 8px;\n" +
                "        font-size: 8px;\n" +
                "        content: \"650\";\n" +
                "      }\n" +
                "      .bar-3::after {\n" +
                "        position: relative;\n" +
                "        right: 8px;\n" +
                "        font-size: 8px;\n" +
                "        content: \"770\";\n" +
                "      }\n" +
                "      .bar-4::after {\n" +
                "        position: absolute;\n" +
                "        right: 0;\n" +
                "        margin-top: 10px;\n" +
                "        font-size: 8px;\n" +
                "        content: \"900\";\n" +
                "      }\n" +
                "\n" +
                "      .date {\n" +
                "        display: flex;\n" +
                "        align-items: center;\n" +
                "        background-color: #00008b;\n" +
                "        color: white;\n" +
                "        padding: 5px 20px;\n" +
                "        border-radius: 30px;\n" +
                "      }\n" +
                "\n" +
                "      .date p {\n" +
                "        font-size: 10px;\n" +
                "      }\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <main>\n" +
                "      <div>\n" +
                "        <p class=\"name\">Hi, <strong>" + name + "</strong></p>\n" +
                "        <div class=\"detailsDiv\">\n" +
                "          <div class=\"score\">\n" +
                "            <h3 style=\"font-size: 32px; margin: 0\"><strong>" + score_value + "</strong></h3>\n" +
                "            <div class=\"experianImageDiv\">\n" +
                "              <img width=\"50\" height=\"16\" src=\"file:///android_res/mipmap/ic_equifax_logo.png\" alt=\"\" />\n" +
                "            </div>\n" +
                "          </div>\n" +
                "          <div style=\"margin-left: " + score_percentage + "%;\" class=\"arrowImageDiv\">\n" +
                "            <p class=\"arrowImageText\">" + score_text + "</p>\n" +
                "            <img class=\"arrowimage\" src=\"file:///android_res/mipmap/ic_down_arrow_fill.png\" alt=\"\" />\n" +
                "          </div>\n" +
                "          <div class=\"bar\">\n" +
                "            <div class=\"bar-1\"></div>\n" +
                "            <div class=\"bar-2\"></div>\n" +
                "            <div class=\"bar-3\"></div>\n" +
                "            <div class=\"bar-4\"></div>\n" +
                "          </div>\n" +
                "          <div class=\"rankAndAge\">\n" +
                "            <p >Risk rank - <span style=\"color: " + risk_rank_color_code + "\"> " + risk_rank + "</span></p>\n" +
                "            <p style=\"color:#6B679F\">Credit age - " + credit_age + "</p>\n" +
                "          </div>\n" +
                "          <div style=\"margin-top: 10px\" class=\"repaymentsAndDate\">\n" +
                "            <div>\n" +
                "              <p style=\"font-weight: 400\">Repayments</p>\n" +
                "              <p style=\"font-size: 10px\">" + repayment + "</p>\n" +
                "            </div>\n" +
                "            <div class=\"date\">\n" +
                "              <p>" + month + "</p>\n" +
                "            </div>\n" +
                "          </div>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "    </main>\n" +
                "  </body>\n" +
                "</html>\n";
    }

}
