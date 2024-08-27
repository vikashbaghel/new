package com.app.rupyz.generic.faq;

import com.app.rupyz.generic.model.faq.FaqInfoModel;
import com.app.rupyz.ui.common.faq.FaqActivity;

import java.util.ArrayList;
import java.util.List;

public class FaqDataHelper {


    public static List<FaqInfoModel> getFaqData() {
        List<FaqInfoModel> mData = new ArrayList<>();

        FaqInfoModel one = new FaqInfoModel();
        one.setTitle("How many credit bureaus are there in India?  ");
        one.setDesc("There are 4 credit bureaus in India - Experian, Equifax, Transunion cibil, and Crif.");
        mData.add(one);

        FaqInfoModel two = new FaqInfoModel();
        two.setTitle("Will my score decline if I check my credit report every month?  ");
        two.setDesc("No. Checking your credit score on Rupyz App does not reduce or negatively affect the score. The score tracking on Rupyz App is consent-based (Soft pull) report. ");
        mData.add(two);

        FaqInfoModel three = new FaqInfoModel();
        three.setTitle("What is the difference between an Individual credit report and a commercial credit report?  ");
        three.setDesc("An individual or a retail credit report has details about the personal borrowings, loans & credit facility availed in an individual or personal capacity. whereas a commercial or a business credit report has details of borrowings, loans & credit facilities availed by a business entity. ");
        mData.add(three);


        FaqInfoModel four = new FaqInfoModel();
        four.setTitle("What does the credit score and credit rating indicate? ");
        four.setDesc("A credit score or a credit ranking is a numerical measure allocated by the respective bureau based on certain parameters. \n" +
                "\n" +
                "For an individual or a retail credit report, the score range is 300 to 900, where 300 indicates a low score and high risk, whereas 900 indicates a high score and low risk. The score provided by respective bureaus is based on proprietary calculations. These are usually based on factors like age of your credit history, repayment history, no of inquiries, recency of any loan or facility, portfolio mix, etc. \n" +
                "\n" +
                "For a business or a commercial credit report, the score ranking is typically 1 to 10, where 1 indicates high credit and low risk and 10 indicates low credit and high risk. The risk rank provided by respective bureaus is based on their proprietary calculations which are typically based on factors like age of your credit history, repayment history, no of inquiries, recency of any loan or facility, portfolio mix, etc.  ");
        mData.add(four);

        FaqInfoModel five = new FaqInfoModel();
        five.setTitle("For where do credit bureaus get my loan and repayment history data? ");
        five.setDesc("All 4 bureaus get data related to your loan and repayment history from the respective banks, NBFCs, and financial institutions.   ");
        mData.add(five);

        FaqInfoModel six = new FaqInfoModel();
        six.setTitle("Is it necessary for a bank or lender to report my loan to the credit bureaus? If yes, how do they do it?  ");
        six.setDesc("Yes, all the banks, NBFCs, and financial institutions must report the details of loans and credit facilities to all 4 bureaus periodically.  ");
        mData.add(six);


        FaqInfoModel seven = new FaqInfoModel();
        seven.setTitle("How can I increase my credit score?  ");
        seven.setDesc("one can increase or maintain a good credit score by having good credit behavior. Always pay your EMI, interest, and credit card payments on or before the due date. by optimum usage of Credit cards and other revolving credit limits. a healthy mix of secured and unsecured borrowings. By avoiding applying for too many loans. for more information, read our blog (hyperlink) on this. ");
        mData.add(seven);


        FaqInfoModel eight = new FaqInfoModel();
        eight.setTitle("Is the data reported by banks and reflected in my credit report 100% accurate and true. ");
        eight.setDesc("No, there are chances of misreporting in the credit report. This may be due to unintentional error or omission by a bank or financial institution or some system error on the part of bureaus.   ");
        mData.add(eight);


        FaqInfoModel nine = new FaqInfoModel();
        nine.setTitle("How can I rectify any inaccuracies in my credit report?  ");
        nine.setDesc("You report to the respective bureaus of any inaccuracies or misreporting from the Rupyz app. You can also email the bureau and your bank from the Rupyz app and request to rectify the errors.  ");
        mData.add(nine);


        FaqInfoModel ten = new FaqInfoModel();
        ten.setTitle("How can I improve my credit report? How can Rupyz help with this?  ");
        ten.setDesc("Rupyz provides a detailed analysis of your report and insight and tips to improve your score over time.  ");
        mData.add(ten);


        FaqInfoModel eleven = new FaqInfoModel();
        eleven.setTitle("How can I get my latest credit score? Is it chargeable?  ");
        eleven.setDesc("You can simply refresh your report from the Rupyz app every month. It is free of cost.  ");
        mData.add(eleven);


        FaqInfoModel twelve = new FaqInfoModel();
        twelve.setTitle("What are the benefits of maintaining a good credit score?  ");
        twelve.setDesc("The benefits of maintaining a good credit score are many. one can get a high and better credit term from banks and financial institutions with a high and consistent score. Many banks and financial institutions offer a better rate of interest to high credit score individuals and businesses.   ");
        mData.add(twelve);


        FaqInfoModel thirteen = new FaqInfoModel();
        thirteen.setTitle("What are the hard pull and soft pull of credit reports? How are they different?  ");
        thirteen.setDesc("A hard pull is basically when a bank or a financial institution fetches your credit report based on a loan application. Since this is considered a credit inquiry, your score will impact based on your credit profile & no of inquiries. \n" +
                "\n" +
                "On the contrary, when you get your credit report from the Rupyz app or any similar App it is called a soft pull. Such consent-based soft pull does not impact your score as it is not a credit inquiry.   ");
        mData.add(thirteen);


        FaqInfoModel fourteen = new FaqInfoModel();
        fourteen.setTitle("What are the factors that impact my score adversely?  ");
        fourteen.setDesc("Factors like delaying or not paying your EMI, Credit card dues or interest on the due dates, high credit card usage, too many borrowings, inquiries, and any settlement with the banks affect your score adversely.   ");
        mData.add(fourteen);

        return mData;
    }
}
