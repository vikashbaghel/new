package com.app.rupyz.ui.equifax.fragment.debtprofile;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.app.rupyz.R;
import com.app.rupyz.adapter.organization.EquiFaxDebtTradeListAdapter;
import com.app.rupyz.adapter.organization.EquiFaxIndividualDebtTradeListAdapter;
import com.app.rupyz.adapter.organization.EquiFaxIndividualOverdueTradeListAdapter;
import com.app.rupyz.adapter.organization.EquiFaxOverdueTradeListAdapter;
import com.app.rupyz.databinding.EquifaxDebtProfileFragmentBinding;
import com.app.rupyz.databinding.EquifaxIndividualDebtProfileFragmentBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.helper.StringHelper;
import com.app.rupyz.generic.logger.FirebaseLogger;
import com.app.rupyz.generic.model.organization.EquiFaxInfoModel;
import com.app.rupyz.generic.model.organization.TradelinesItem;
import com.app.rupyz.generic.model.organization.individual.EquiFaxIndividualInfoModel;
import com.app.rupyz.generic.model.organization.individual.Tradeline;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.equifax.EquiFaxIndividualMyAccount;
import com.app.rupyz.ui.equifax.fragment.alerts.EquiFaxCommercialAlertFragment;
import com.app.rupyz.ui.equifax.fragment.alerts.EquiFaxIndividualAlertFragment;
import com.app.rupyz.ui.home.HomeFragment;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EquiFaxIndividualDebtProfileFragment extends Fragment implements View.OnClickListener {

    EquifaxIndividualDebtProfileFragmentBinding binding;
    private static final int PERMISSION_REQUEST_CODE = 100;
    public List<Tradeline> mActiveIndividualData = new ArrayList<>();
    public List<Tradeline> mCloseIndividualData = new ArrayList<>();
    public List<Tradeline> tradelineList;
    private int total_sanction_amount = 0;
    private int total_paid = 0;
    private int total_balance = 0;
    private int total_overdue = 0;
    private EquiFaxInfoModel mData;
    private String userName = "";
    private EquiFaxIndividualInfoModel mIndividualData;
    private EquiFaxReportHelper mReportHelper;
    private EquiFaxIndividualInfoModel equiFaxIndividualInfoModel;
    public List<Tradeline> overdueList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EquifaxIndividualDebtProfileFragmentBinding.inflate(getLayoutInflater());
        mReportHelper = EquiFaxReportHelper.getInstance();
        equiFaxIndividualInfoModel = mReportHelper.getRetailReport();
        mData = mReportHelper.getCommercialReport();
        new Utility(getActivity());
        new FirebaseLogger(getContext()).sendLog(getResources().getString(R.string.loan_summary)
                , getResources().getString(R.string.loan_summary));
        initLayout();
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.active_layout:
                initActiveSummary();
                break;
            case R.id.close_layout:
                initCloseSummary();
                break;
            case R.id.btn_active_loan_summary:
                initActiveSummary();
                break;
            case R.id.btn_close_loan_summary:
                initCloseSummary();
                break;

            case R.id.img_individual_debt_excel_download:
                storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                break;
        }
    }

    private ActivityResultLauncher<String> storagePermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result) {
                    createIndividualExcelData();
                } else {
                    Toast.makeText(
                            requireContext(),
                            "Storage Permission is required to perform this action.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });

    private void downloadSummery() {
        if (checkPermission()) {
            createIndividualExcelData();
        } else {
            requestPermission();
        }
    }

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
                Toast.makeText(getContext(), "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }


    private void initActiveSummary() {
        if (binding.recyclerView.getVisibility() == View.VISIBLE) {
            binding.recyclerView.setVisibility(View.GONE);
            binding.btnActiveLoanSummary.setImageDrawable(getResources().getDrawable(R.mipmap.ic_expand));
        } else {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.btnActiveLoanSummary.setImageDrawable(getResources().getDrawable(R.mipmap.ic_collapse));
        }

    }

    private void initCloseSummary() {
        if (binding.recyclerViewClose.getVisibility() == View.VISIBLE) {
            binding.recyclerViewClose.setVisibility(View.GONE);
            binding.btnCloseLoanSummary.setImageDrawable(getResources().getDrawable(R.mipmap.ic_expand));
        } else {
            binding.recyclerViewClose.setVisibility(View.VISIBLE);
            binding.btnCloseLoanSummary.setImageDrawable(getResources().getDrawable(R.mipmap.ic_collapse));
        }
    }

    private void initLayout() {
        binding.activeLayout.setOnClickListener(this);
        binding.closeLayout.setOnClickListener(this);
        binding.imgIndividualDebtExcelDownload.setOnClickListener(this);
        binding.btnCloseLoanSummary.setOnClickListener(this);
        binding.btnActiveLoanSummary.setOnClickListener(this);
        mIndividualData = mReportHelper.getRetailReport();
        if ((mIndividualData.getMetadata().getRetail_progress_step() + "").equalsIgnoreCase("3.0")) {
            binding.rootLayoutDebtIndividual.setVisibility(View.VISIBLE);
            initRetailData();
        } else {
            binding.rootLayoutDebtIndividual.setVisibility(View.GONE);
        }
    }

    private void initRetailData() {
        if (mIndividualData.getReport().getTradelines() != null && mIndividualData.getReport().getTradelines().size() > 0) {
            try {

            } catch (Exception ex) {

            }
            for (Tradeline Item : mIndividualData.getReport().getTradelines()) {
                if (Item.getAccount_status().equalsIgnoreCase("OPEN")) {
                    total_sanction_amount = total_sanction_amount + Item.getSanction_amount();
                    total_balance = total_balance + Item.getCurrent_balance_amount();
                    total_paid = total_paid
                            + (Item.getSanction_amount() - Item.getCurrent_balance_amount());
                    try {
                        total_overdue = total_overdue + Item.getOverdue_amount();
                    } catch (Exception ex) {
                    }
                    mActiveIndividualData.add(Item);
                } else {
                    mCloseIndividualData.add(Item);
                }

            }
            overdueList = new ArrayList<>();
            for (Tradeline Item : mIndividualData.getReport().getTradelines()) {
                if (Item.isIs_overdue()) {
                    overdueList.add(Item);
                }
            }

            binding.txvActiveLoanSanctionAmount.setText(getResources().getString(R.string.rs) + " " + AmountHelper.getCommaSeptdAmount(total_sanction_amount) + "");
            binding.txvActiveLoansPaidAmount.setText(getResources().getString(R.string.rs) + " " + AmountHelper.getCommaSeptdAmount(total_paid) + "");
            binding.txvActiveLoansBalance.setText(getResources().getString(R.string.rs) + " " + AmountHelper.getCommaSeptdAmount(total_balance) + "");
            binding.txvTotalActiveLoans.setText(mActiveIndividualData.size() + "");

            EquiFaxIndividualDebtTradeListAdapter activeAdapter
                    = new EquiFaxIndividualDebtTradeListAdapter(mActiveIndividualData, getContext());
            binding.recyclerView.setHasFixedSize(true);
            binding.recyclerView.setNestedScrollingEnabled(false);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.recyclerView.setAdapter(activeAdapter);

            EquiFaxIndividualDebtTradeListAdapter adapter
                    = new EquiFaxIndividualDebtTradeListAdapter(mCloseIndividualData, getContext());
            binding.recyclerViewClose.setHasFixedSize(true);
            binding.recyclerViewClose.setNestedScrollingEnabled(false);
            binding.recyclerViewClose.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.recyclerViewClose.setAdapter(adapter);


            if (overdueList != null && overdueList.size() > 0) {
                EquiFaxIndividualOverdueTradeListAdapter overdueAdapter = new EquiFaxIndividualOverdueTradeListAdapter(overdueList, getContext());
                binding.recyclerViewOverdue.setHasFixedSize(true);
                binding.recyclerViewOverdue.setNestedScrollingEnabled(false);
                binding.recyclerViewOverdue.setLayoutManager(new LinearLayoutManager(getActivity()));
                binding.recyclerViewOverdue.setAdapter(overdueAdapter);
                binding.txvOverdueTitle.setVisibility(View.VISIBLE);
            } else {
                binding.recyclerViewOverdue.setVisibility(View.GONE);
                binding.txvOverdueTitle.setVisibility(View.GONE);
            }
        }
    }

    private void createIndividualExcelData() {
        try {
            userName = mIndividualData.getReport().getFull_name();
        } catch (Exception ex) {
        }

        String strRiskRank = "";
        mIndividualData = mReportHelper.getRetailReport();
        mActiveIndividualData = new ArrayList<>();
        mCloseIndividualData = new ArrayList<>();

        System.out.println("Individual Age" + mIndividualData.getReport().getCredit_age());

        tradelineList = new ArrayList<>();
        for (Tradeline Item : mIndividualData.getReport().getTradelines()) {
            if (Item.isIs_overdue()) {
                tradelineList.add(Item);
            }
        }


        if (mIndividualData.getReport().getScore_comment().equalsIgnoreCase("Low Risk")) {
            strRiskRank = getResources().getString(R.string.low_risk);
        } else if (mIndividualData.getReport().getScore_comment().equalsIgnoreCase("High Risk")) {
            strRiskRank = (getResources().getString(R.string.high_risk));
        } else if (mIndividualData.getReport().getScore_comment().equalsIgnoreCase("Very Low Risk")) {
            strRiskRank = (getResources().getString(R.string.very_low_risk));
        } else if (mIndividualData.getReport().getScore_comment().equalsIgnoreCase("Medium Risk")) {
            strRiskRank = (getResources().getString(R.string.medium_risk));
        }

        /*individualOverdueList = new ArrayList<>();
        for (Tradelines Item : mIndividualData.getReport().getTradelines()) {
            if (Item.isIsOverdue()) {
                individualOverdueList.add(Item);
            }
        }*/


        for (Tradeline Item : mIndividualData.getReport().getTradelines()) {
            if (Item.getAccount_status().equalsIgnoreCase("OPEN")) {
                mActiveIndividualData.add(Item);
            } else {
                mCloseIndividualData.add(Item);
            }
        }

        Calendar c = Calendar.getInstance();
        String[] monthName = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November",
                "December"};
        String month = monthName[c.get(Calendar.MONTH)];
        System.out.println("Month name:" + month);
        int year = c.get(Calendar.YEAR);
        File filePath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "" + "/" + userName + "_Loan_Summary" + "_" + month + "_" + year + ".xls");
        } else {
            filePath = new File(Environment.getExternalStorageDirectory() + "" + "/" + userName + "_Loan_Summary" + "_" + month + "_" + year + ".xls");
        }
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet("Loan Summary Data");
        HSSFSheet activeLoanSheet = hssfWorkbook.createSheet("Active Loan");
        HSSFSheet closeLoanSheet = hssfWorkbook.createSheet("Close Loan Sheet");

        HSSFRow hssfRow = hssfSheet.createRow(0);
        HSSFCell sanctionCell = hssfRow.createCell(0);
        sanctionCell.setCellValue("Total Sanction Amount");
        HSSFCell totalSanctionAmount = hssfRow.createCell(1);
        totalSanctionAmount.setCellValue(AmountHelper.getCommaSeptdAmount(total_sanction_amount));

        /*HSSFRow paidRow = hssfSheet.createRow(1);
        HSSFCell paidRowCell = paidRow.createCell(1);
        paidRowCell.setCellValue("Paid Amount");*/


        HSSFRow rows = hssfSheet.createRow(0);
        HSSFCell nameCell = rows.createCell(0);
        HSSFCell nameValueCell = rows.createCell(1);
        nameCell.setCellValue("Name");
        nameValueCell.setCellValue(userName);


        HSSFRow row1 = hssfSheet.createRow(1);
        HSSFCell scoreCell = row1.createCell(0);
        HSSFCell scoreValueCell = row1.createCell(1);
        scoreCell.setCellValue("Score");
        scoreValueCell.setCellValue(mIndividualData.getReport().getScore_value() + " (Equifax)");

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
        activeLoanValue.setCellValue(mActiveIndividualData.size() + "");
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


       /* for (int i = 0; i < individualOverdueList.size(); i++) {
            System.out.println("MDATA :- " + individualOverdueList.get(i).getInstitutionName());
            HSSFRow overdueSummery = hssfSheet.createRow(16 + i);
            HSSFCell subscriberCell = overdueSummery.createCell(0);
            HSSFCell accountTypeCell = overdueSummery.createCell(1);
            HSSFCell loanAmountCell = overdueSummery.createCell(2);
            HSSFCell outstandingAmountCell = overdueSummery.createCell(3);
            HSSFCell balanceAmount = overdueSummery.createCell(4);
            HSSFCell overdueAmount = overdueSummery.createCell(5);
            *//*String currentBalance = getContext().getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
                    tradelineList.get(i).getCurrent_balance_amount()));*//*

            String currentBalance = getContext().getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(""+
                    individualOverdueList.get(i).getCurrentBalanceAmount()));

            subscriberCell.setCellValue(individualOverdueList.get(i).getInstitutionName());
            accountTypeCell.setCellValue(individualOverdueList.get(i).getCreditType());
            loanAmountCell.setCellValue(AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(""+individualOverdueList.get(i).getSanctionAmount())));
            outstandingAmountCell.setCellValue(AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(""+individualOverdueList.get(i).getCurrentBalanceAmount())));
            balanceAmount.setCellValue(currentBalance);
            overdueAmount.setCellValue(AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(""+individualOverdueList.get(i).getOverdueAmount())));
        }*/


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

        for (int i = 0; i < mActiveIndividualData.size(); i++) {
            HSSFRow activeLoan = activeLoanSheet.createRow(i + 1);
            HSSFCell subscriberCell = activeLoan.createCell(0);
            HSSFCell accountTypeCell = activeLoan.createCell(1);
            HSSFCell loanAmountCell = activeLoan.createCell(2);
            HSSFCell outstandingAmountCell = activeLoan.createCell(3);
            HSSFCell loanSanctionDate = activeLoan.createCell(4);
            subscriberCell.setCellValue(mActiveIndividualData.get(i).getInstitution_name());
            accountTypeCell.setCellValue(mActiveIndividualData.get(i).getAccount_type());
            loanAmountCell.setCellValue(AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble("" + mActiveIndividualData.get(i).getSanction_amount())));
            outstandingAmountCell.setCellValue(AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble("" + mActiveIndividualData.get(i).getCurrent_balance_amount())));
            loanSanctionDate.setCellValue((mActiveIndividualData.get(i).getDate_opened()));
        }

        for (int i = 0; i < mCloseIndividualData.size(); i++) {
            HSSFRow closeLoanSheetRow = closeLoanSheet.createRow(i + 1);
            HSSFCell subscriberCell = closeLoanSheetRow.createCell(0);
            HSSFCell accountTypeCell = closeLoanSheetRow.createCell(1);
            HSSFCell loanAmountCell = closeLoanSheetRow.createCell(2);
            HSSFCell outstandingAmountCell = closeLoanSheetRow.createCell(3);
            HSSFCell loanSanctionDate = closeLoanSheetRow.createCell(4);
            subscriberCell.setCellValue(mCloseIndividualData.get(i).getInstitution_name());
            accountTypeCell.setCellValue(mCloseIndividualData.get(i).getAccount_type());
            loanAmountCell.setCellValue(AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble("" + mCloseIndividualData.get(i).getSanction_amount())));
            outstandingAmountCell.setCellValue(AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble("" + mCloseIndividualData.get(i).getCurrent_balance_amount())));
            loanSanctionDate.setCellValue((mCloseIndividualData.get(i).getDate_opened()));
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
            e.printStackTrace();
        }
    }

    private void openXLS(final String path) {
        File file = new File(path);
        Uri uri;
        if (SDK_INT >= Build.VERSION_CODES.M) {
            uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file);
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
            Toast.makeText(getContext(), "Application not found", Toast.LENGTH_SHORT).show();
        }
    }
}
