package com.app.rupyz.ui.home;

import static android.os.Build.VERSION.SDK_INT;

import static com.app.rupyz.generic.helper.AmountHelper.convertInLac;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.R;
import com.app.rupyz.adapter.individual.DebtTradeListAdapter;
import com.app.rupyz.adapter.individual.OverdueTradeListAdapter;
import com.app.rupyz.databinding.DebtProfileFragmentBinding;
import com.app.rupyz.databinding.FragmentLearnListBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.helper.DateFormatHelper;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.logger.FirebaseLogger;
import com.app.rupyz.generic.model.createemi.experian.Datum;
import com.app.rupyz.generic.model.createemi.experian.ExperianEMIResponse;
import com.app.rupyz.generic.model.individual.experian.ExperianInfoModel;
import com.app.rupyz.generic.model.individual.experian.Tradeline;
import com.app.rupyz.generic.utils.Utility;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class DebtProfileFragment extends Fragment implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    DebtProfileFragmentBinding binding;
    public List<Tradeline> mActiveData = new ArrayList<>();
    public List<Tradeline> mCloseData = new ArrayList<>();
    private int total_sanction_amount = 0;
    private int total_paid = 0;
    private int total_balance = 0;
    private int total_overdue = 0;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private ExperianInfoModel mIndividualData;
    private ExperianEMIResponse mEMIResponse;
    private EquiFaxReportHelper mReportHelper;
    private String strRiskRank;
    public List<Tradeline> mData;
    public List<Tradeline> overdueList;
    public List<Tradeline> dataEMI;
    public List<Tradeline> closeDataEMI;
    private String userName = "";
    private static final String[] STORAGE_PERMISSION =
            {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int RC_STORAGE_PERM = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DebtProfileFragmentBinding.inflate(getLayoutInflater());
        new Utility(getActivity());
        mReportHelper = EquiFaxReportHelper.getInstance();
        initLayout();
        return binding.getRoot();
    }


    @AfterPermissionGranted(RC_STORAGE_PERM)
    public void storageTask() {
        if (hasStoragePermission()) {
            excelData();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                    this,
                    "Please provide the permission to storage the file.",
                    RC_STORAGE_PERM,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private boolean hasStoragePermission() {
        return EasyPermissions.hasPermissions(getActivity(), STORAGE_PERMISSION);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_individual_debt_excel_download:
                storageTask();
//                if (checkPermission()) {
//                    excelData();
//                } else {
//                    requestPermission();
//                }:qq
        }
    }


    private void initLayout() {

        mIndividualData = mReportHelper.getExperianReport();
        mEMIResponse = mReportHelper.getExperianEMI();

        if (HomeFragment.mData.getScore_comment().equalsIgnoreCase("H")) {
            strRiskRank = getResources().getString(R.string.low_risk);
        } else if (HomeFragment.mData.getScore_comment().equalsIgnoreCase("M")) {
            strRiskRank = getResources().getString(R.string.medium_risk);
        } else if (HomeFragment.mData.getScore_comment().equalsIgnoreCase("L")) {
            strRiskRank = getResources().getString(R.string.high_risk);
        }


        mData = new ArrayList<>();
        for (Tradeline Item : mIndividualData.getTradelines()) {
            if (AmountHelper.convertStringToInt(Item.getAmount_Past_Due()) > 0) {
                mData.add(Item);
            }
        }

        dataEMI = new ArrayList<>();
        if (mEMIResponse.getData() != null) {
            for (Datum Item : mEMIResponse.getData()) {
                for (Tradeline tradelinesItem : mIndividualData.getTradelines()) {
                    if (Item.getAccountNumber().equals(tradelinesItem.getAccount_Number())) {
                        tradelinesItem.setScheduled_Monthly_Payment_Amount(Item.getScheduledMonthlyPaymentAmount());
                        tradelinesItem.setMonthDueDay(Item.getMonthDueDay());
                        tradelinesItem.setRepayment_Tenure(Item.getRepaymentTenure());
                        tradelinesItem.setRate_of_Interest(Item.getRateOfInterest());
                        dataEMI.add(tradelinesItem);
                    } else {
                        dataEMI.add(tradelinesItem);
                        break;
                    }
                }
            }
        }

        binding.activeLayout.setOnClickListener(this);
        binding.closeLayout.setOnClickListener(this);
        binding.imgIndividualDebtExcelDownload.setOnClickListener(this);
        binding.btnCloseLoanSummary.setOnClickListener(this);
        binding.btnActiveLoanSummary.setOnClickListener(this);
        if (HomeFragment.mData.getTradelines().size() > 0 && HomeFragment.mData.getTradelines() != null) {
            for (Tradeline Item : HomeFragment.mData.getTradelines()) {
                if (Item.getAccount_Status().equalsIgnoreCase("active")) {
                    total_sanction_amount = total_sanction_amount + Integer.parseInt(Item.getHighest_Credit_or_Original_Loan_Amount());
                    total_balance = total_balance + Integer.parseInt(Item.getCurrent_Balance());
                    total_paid = total_paid + (Integer.parseInt(Item.getHighest_Credit_or_Original_Loan_Amount()) - Integer.parseInt(Item.getCurrent_Balance()));
                    try {
                        total_overdue = total_overdue + Integer.parseInt(Item.getAmount_Past_Due());
                    } catch (Exception ex) {

                    }
                    mActiveData.add(Item);
                } else {
                    mCloseData.add(Item);
                }
            }

            overdueList = new ArrayList<>();
            for (Tradeline Item : HomeFragment.mData.getTradelines()) {
                if (AmountHelper.convertStringToInt(Item.getAmount_Past_Due()) > 0) {
                    overdueList.add(Item);
                }
            }
            binding.txvActiveLoanSanctionAmount.setText(getResources().getString(R.string.rs) + " " + convertInLac(total_sanction_amount) + "");
            binding.txvActiveLoansPaidAmount.setText(getResources().getString(R.string.rs) + " " + convertInLac(total_paid) + "");
            binding.txvActiveLoansBalance.setText(getResources().getString(R.string.rs) + " " + convertInLac(total_balance) + "");
            binding.txvTotalActiveLoans.setText(mActiveData.size() + "");
            DebtTradeListAdapter activeAdapter = new DebtTradeListAdapter(mActiveData, dataEMI, getActivity());
            binding.recyclerView.setHasFixedSize(true);
            binding.recyclerView.setNestedScrollingEnabled(false);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.recyclerView.setAdapter(activeAdapter);

            DebtTradeListAdapter adapter = new DebtTradeListAdapter(mCloseData, closeDataEMI, getActivity());
            binding.recyclerViewClose.setHasFixedSize(true);
            binding.recyclerViewClose.setNestedScrollingEnabled(false);
            binding.recyclerViewClose.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.recyclerViewClose.setAdapter(adapter);


            if (overdueList != null && overdueList.size() > 0) {
                OverdueTradeListAdapter overdueAdapter = new OverdueTradeListAdapter(overdueList, getActivity());
                binding.recyclerViewOverdue.setHasFixedSize(true);
                binding.recyclerViewOverdue.setNestedScrollingEnabled(false);
                binding.recyclerViewOverdue.setLayoutManager(new LinearLayoutManager(getActivity()));
                binding.recyclerViewOverdue.setAdapter(overdueAdapter);
                binding.recyclerViewOverdue.setVisibility(View.VISIBLE);
                binding.txvOverdueTitle.setVisibility(View.VISIBLE);
            } else {
                binding.recyclerViewOverdue.setVisibility(View.GONE);
                binding.txvOverdueTitle.setVisibility(View.GONE);
            }

        }
    }

    // checking permission To WRITE
    private boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
    }

    // request permission for WRITE Access
    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getActivity().getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(getActivity(), "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }

    }


    private void excelData() {
        try {
            userName = HomeFragment.mData.getRelationship_details()
                    .getCurrent_Applicant_Details().getFirst_Name() + " " + HomeFragment.mData.getRelationship_details()
                    .getCurrent_Applicant_Details().getLast_Name();
        } catch (Exception ex) {
        }
        Calendar c = Calendar.getInstance();
        String[] monthName = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November",
                "December"};
        String month = monthName[c.get(Calendar.MONTH)];
        int year = c.get(Calendar.YEAR);
        File filePath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "" + "/" + userName + "_Debt_Profile" + "_" + month + "_" + year + ".xls");
        } else {
            filePath = new File(Environment.getExternalStorageDirectory() + "" + "/" + userName + "_Debt_Profile" + "_" + month + "_" + year + ".xls");
        }
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet("Loan Summary Data");
        HSSFSheet activeLoanSheet = hssfWorkbook.createSheet("Active Loan");
        HSSFSheet closeLoanSheet = hssfWorkbook.createSheet("Close Loan Sheet");

        HSSFRow hssfRow = hssfSheet.createRow(0);
        HSSFCell sanctionCell = hssfRow.createCell(0);
        sanctionCell.getCellStyle().setWrapText(true);
        sanctionCell.setCellValue("Total Sanction Amount");
        HSSFCell totalSanctionAmount = hssfRow.createCell(1);
        totalSanctionAmount.setCellValue(AmountHelper.getCommaSeptdAmount(total_sanction_amount));

        HSSFRow rows = hssfSheet.createRow(0);
        HSSFCell nameCell = rows.createCell(0);
        HSSFCell nameValueCell = rows.createCell(1);
        nameCell.setCellValue("Name");
        nameCell.getCellStyle().setWrapText(true);
        nameValueCell.setCellValue(userName);

        HSSFRow row1 = hssfSheet.createRow(1);
        HSSFCell scoreCell = row1.createCell(0);
        HSSFCell scoreValueCell = row1.createCell(1);
        scoreCell.setCellValue("Score" + " (Experian)");
        scoreCell.getCellStyle().setWrapText(true);
        scoreValueCell.setCellValue(HomeFragment.mData.getScore_value() + "");

        HSSFRow row2 = hssfSheet.createRow(2);
        HSSFCell riskCell = row2.createCell(0);
        HSSFCell riskValueCell = row2.createCell(1);
        riskCell.setCellValue("Risk Rank");
        riskValueCell.setCellValue(strRiskRank);

        HSSFRow row3 = hssfSheet.createRow(4);
        HSSFCell activeLoanHeading = row3.createCell(0);
        activeLoanHeading.setCellValue("Active Loan Summary");

        HSSFRow row4 = hssfSheet.createRow(6);
        HSSFCell particularHeading = row4.createCell(0);
        HSSFCell numberHeading = row4.createCell(1);
        HSSFCell sanctionHeading = row4.createCell(2);
        HSSFCell paidHeading = row4.createCell(3);
        HSSFCell balanceHeading = row4.createCell(4);
        particularHeading.setCellValue("Particular");
        numberHeading.setCellValue("# Number");
        sanctionHeading.setCellValue("Sanction");
        paidHeading.setCellValue("Paid");
        balanceHeading.setCellValue("Balance");


        HSSFRow row5 = hssfSheet.createRow(7);
        HSSFCell activeLoans = row5.createCell(0);
        HSSFCell activeLoanValue = row5.createCell(1);
        HSSFCell activeLoanSanctionAmount = row5.createCell(2);
        HSSFCell activeLoanPaidAmount = row5.createCell(3);
        HSSFCell activeLoanBalanceAmount = row5.createCell(4);
        activeLoans.setCellValue("Total Active Loans");
        activeLoanValue.setCellValue(mActiveData.size() + "");
        activeLoanSanctionAmount.setCellValue(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(total_sanction_amount) + "");
        activeLoanPaidAmount.setCellValue(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(total_paid) + "");
        activeLoanBalanceAmount.setCellValue(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(total_balance) + "");


        HSSFRow row6 = hssfSheet.createRow(9);
        HSSFCell totalMonthlyEmiHeading = row6.createCell(0);
        HSSFCell totalMonthlyEMI = row6.createCell(1);
        HSSFCell totalMonthlyEMIAmount = row6.createCell(2);
        totalMonthlyEmiHeading.setCellValue("Total Monthly EMI");
        totalMonthlyEMI.setCellValue("# Number");
        totalMonthlyEMIAmount.setCellValue("Amount");

        HSSFRow row7 = hssfSheet.createRow(10);
        HSSFCell totalMonthlyEmiAmount = row7.createCell(0);
        HSSFCell totalMonths = row7.createCell(1);
        HSSFCell sumMonthlyEMI = row7.createCell(2);
        totalMonthlyEmiAmount.setCellValue("-");
        totalMonths.setCellValue("-");
        sumMonthlyEMI.setCellValue("-");

        HSSFRow row8 = hssfSheet.createRow(11);
        HSSFCell totalYearEmiHeading = row8.createCell(0);
        HSSFCell totalYearEMI = row8.createCell(1);
        HSSFCell totalYearEMIAmount = row8.createCell(2);
        totalYearEmiHeading.setCellValue("Total Annual EMI");
        totalYearEMI.setCellValue("# Number");
        totalYearEMIAmount.setCellValue("Amount");

        HSSFRow row9 = hssfSheet.createRow(12);
        HSSFCell totalYearEmiAmount = row9.createCell(0);
        HSSFCell totalYears = row9.createCell(1);
        HSSFCell sumYearEMI = row9.createCell(2);
        totalYearEmiAmount.setCellValue("-");
        totalYears.setCellValue("-");
        sumYearEMI.setCellValue("-");

        HSSFRow row10 = hssfSheet.createRow(14);
        HSSFCell overdueTitle = row10.createCell(0);
        overdueTitle.setCellValue("Overdue Summary");

        HSSFRow row11 = hssfSheet.createRow(15);
        HSSFCell lenderTitle = row11.createCell(0);
        HSSFCell loanType = row11.createCell(1);
        HSSFCell sanctioned = row11.createCell(2);
        HSSFCell paid = row11.createCell(3);
        HSSFCell balance = row11.createCell(4);
        HSSFCell overdue = row11.createCell(5);
        lenderTitle.setCellValue("Lender");
        loanType.setCellValue("Loan Type");
        sanctioned.setCellValue("Sanctioned");
        paid.setCellValue("Paid");
        balance.setCellValue("Balance");
        overdue.setCellValue("Overdue Amount");

        for (int i = 0; i < mData.size(); i++) {
            HSSFRow overdueSummery = hssfSheet.createRow(16 + i);
            HSSFCell subscriberCell = overdueSummery.createCell(0);
            HSSFCell accountTypeCell = overdueSummery.createCell(1);
            HSSFCell loanAmountCell = overdueSummery.createCell(2);
            HSSFCell outstandingAmountCell = overdueSummery.createCell(3);
            HSSFCell balanceAmount = overdueSummery.createCell(4);
            HSSFCell overdueAmount = overdueSummery.createCell(5);
            String currentBalance = getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
                    mData.get(i).getCurrent_Balance()));
            subscriberCell.setCellValue(mActiveData.get(i).subscriber_Name);
            accountTypeCell.setCellValue(mActiveData.get(i).account_Type);
            loanAmountCell.setCellValue(AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(mActiveData.get(i).highest_Credit_or_Original_Loan_Amount)));
            outstandingAmountCell.setCellValue(AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(mActiveData.get(i).current_Balance)));
            balanceAmount.setCellValue(currentBalance);
            overdueAmount.setCellValue(AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(mActiveData.get(i).getAmount_Past_Due())));
        }
        HSSFRow activeLoanSheetRow = activeLoanSheet.createRow(0);
        HSSFCell activeCell1 = activeLoanSheetRow.createCell(0);
        HSSFCell activeCell2 = activeLoanSheetRow.createCell(1);
        HSSFCell activeCell3 = activeLoanSheetRow.createCell(2);
        HSSFCell activeCell4 = activeLoanSheetRow.createCell(3);
        HSSFCell activeCell5 = activeLoanSheetRow.createCell(4);
        HSSFCell activeCell6 = activeLoanSheetRow.createCell(5);

        activeCell1.setCellValue("Bank Name");
        activeCell2.setCellValue("Loan Type");
        activeCell3.setCellValue("Loan Amount");
        activeCell4.setCellValue("Outstanding Amount");
        activeCell5.setCellValue("Loan Santioned Date");
        activeCell6.setCellValue("Rate");


        HSSFRow closeLoanSheetRow1 = closeLoanSheet.createRow(0);
        HSSFCell closeCell1 = closeLoanSheetRow1.createCell(0);
        HSSFCell closeCell2 = closeLoanSheetRow1.createCell(1);
        HSSFCell closeCell3 = closeLoanSheetRow1.createCell(2);
        HSSFCell closeCell4 = closeLoanSheetRow1.createCell(3);
        HSSFCell closeCell5 = closeLoanSheetRow1.createCell(4);
        HSSFCell closeCell6 = closeLoanSheetRow1.createCell(5);

        closeCell1.setCellValue("Bank Name");
        closeCell2.setCellValue("Loan Type");
        closeCell3.setCellValue("Loan Amount");
        closeCell4.setCellValue("Outstanding Amount");
        closeCell5.setCellValue("Loan Santioned Date");
        closeCell6.setCellValue("Rate");

        for (int i = 0; i < mActiveData.size(); i++) {
            HSSFRow activeLoan = activeLoanSheet.createRow(i + 1);
            HSSFCell subscriberCell = activeLoan.createCell(0);
            HSSFCell accountTypeCell = activeLoan.createCell(1);
            HSSFCell loanAmountCell = activeLoan.createCell(2);
            HSSFCell outstandingAmountCell = activeLoan.createCell(3);
            HSSFCell loanSanctionDate = activeLoan.createCell(4);
            subscriberCell.setCellValue(mActiveData.get(i).subscriber_Name);
            accountTypeCell.setCellValue(mActiveData.get(i).account_Type);
            loanAmountCell.setCellValue(AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(mActiveData.get(i).highest_Credit_or_Original_Loan_Amount)));
            outstandingAmountCell.setCellValue(AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(mActiveData.get(i).current_Balance)));
            loanSanctionDate.setCellValue(DateFormatHelper.conUnSupportedDateToString(mActiveData.get(i).open_Date));
        }

        for (int i = 0; i < mCloseData.size(); i++) {
            HSSFRow closeLoanSheetRow = closeLoanSheet.createRow(i + 1);
            HSSFCell subscriberCell = closeLoanSheetRow.createCell(0);
            HSSFCell accountTypeCell = closeLoanSheetRow.createCell(1);
            HSSFCell loanAmountCell = closeLoanSheetRow.createCell(2);
            HSSFCell outstandingAmountCell = closeLoanSheetRow.createCell(3);
            HSSFCell loanSanctionDate = closeLoanSheetRow.createCell(4);
            subscriberCell.setCellValue(mCloseData.get(i).subscriber_Name);
            accountTypeCell.setCellValue(mCloseData.get(i).account_Type);
            loanAmountCell.setCellValue(AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(mCloseData.get(i).highest_Credit_or_Original_Loan_Amount)));
            outstandingAmountCell.setCellValue(AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(mCloseData.get(i).current_Balance)));
            loanSanctionDate.setCellValue(DateFormatHelper.conUnSupportedDateToString(mCloseData.get(i).open_Date));
        }
        try {
            if (!filePath.exists()) {
                filePath.createNewFile();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            hssfWorkbook.write(fileOutputStream);

            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            openXLS(filePath.toString());

        } catch (Exception e) {
//            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void openXLS(final String path) {
        File file = new File(path);
        Uri uri;
        if (SDK_INT >= Build.VERSION_CODES.M) {
            uri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", file);
        } else {
            uri = Uri.fromFile(file);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "You don't have application to open this Excel file.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                } else {
                    Toast.makeText(getActivity(), "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
