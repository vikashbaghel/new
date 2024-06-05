package com.app.rupyz.ui.equifax.fragment.debtprofile;

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
import android.provider.ContactsContract;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.R;
import com.app.rupyz.adapter.organization.EquiFaxDebtTradeListAdapter;
import com.app.rupyz.adapter.organization.EquiFaxOverdueTradeListAdapter;
import com.app.rupyz.databinding.EquifaxCommercialDebtProfileFragmentBinding;
import com.app.rupyz.generic.helper.AmountHelper;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.helper.StringHelper;
import com.app.rupyz.generic.logger.FirebaseLogger;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.createemi.CreateEMIResponse;
import com.app.rupyz.generic.model.createemi.Datum;
import com.app.rupyz.generic.model.organization.EquiFaxInfoModel;
import com.app.rupyz.generic.model.organization.TradelinesItem;
import com.app.rupyz.generic.model.organization.individual.EquiFaxIndividualInfoModel;
import com.app.rupyz.generic.model.organization.individual.Tradeline;
import com.app.rupyz.ui.equifax.EquiFaxMyAccount;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EquiFaxCommercialDebtProfileFragment extends Fragment implements View.OnClickListener {

    EquifaxCommercialDebtProfileFragmentBinding binding;
    public List<TradelinesItem> mActiveData = new ArrayList<>();
    public List<TradelinesItem> mCloseData = new ArrayList<>();


    private static final int PERMISSION_REQUEST_CODE = 100;
    public List<Tradeline> mActiveIndividualData = new ArrayList<>();
    public List<Tradeline> mCloseIndividualData = new ArrayList<>();

    private int total_sanction_amount = 0;
    private int total_paid = 0;
    private int total_balance = 0;
    private int total_overdue = 0;
    private EquiFaxInfoModel mData;
    private EquiFaxIndividualInfoModel mIndividualData;
    private EquiFaxReportHelper mReportHelper;
    private EquiFaxIndividualInfoModel equiFaxIndividualInfoModel;
    public List<TradelinesItem> overdueList;
    public List<TradelinesItem> dataEMI;
    private CreateEMIResponse mEMIResponse;

    //CreateEMIResponse


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EquifaxCommercialDebtProfileFragmentBinding.inflate(getLayoutInflater());
        mReportHelper = EquiFaxReportHelper.getInstance();
        equiFaxIndividualInfoModel = mReportHelper.getRetailReport();
        mData = mReportHelper.getCommercialReport();
        new FirebaseLogger(getContext()).sendLog(getResources().getString(R.string.loan_summary),
                getResources().getString(R.string.loan_summary));
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
            case R.id.img_debt_excel_download:
                storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                break;
        }
    }

    private void downloadSummery() {
        if (checkPermission()) {
            try {
                createCommercialExcelData();
            } catch (Exception Ex) {
            }
        } else {
            requestPermission();
        }
    }

    private ActivityResultLauncher<String> storagePermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result) {
                    createCommercialExcelData();
                } else {
                    Toast.makeText(
                            requireContext(),
                            "Storage Permission is required to perform this action.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });

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
        binding.imgDebtExcelDownload.setOnClickListener(this);
        binding.btnCloseLoanSummary.setOnClickListener(this);
        binding.btnActiveLoanSummary.setOnClickListener(this);
        mData = mReportHelper.getCommercialReport();
        if ((mData.getMetadata().getCommercial_progress_step() + "").equalsIgnoreCase("3.0")) {
            binding.rootLayoutDebtCommercial.setVisibility(View.VISIBLE);
            initCommercialData();
        } else {
            binding.rootLayoutDebtCommercial.setVisibility(View.GONE);
        }
    }

    private void initCommercialData() {
        try {
            mEMIResponse = mReportHelper.getEquifaxCommercialEMI();
            dataEMI = new ArrayList<>();
            if (mEMIResponse.getData() != null && mData.getReport().getTradelines() != null) {
                for (Datum Item : mEMIResponse.getData()) {
                    for (TradelinesItem tradelinesItem : mData.getReport().getTradelines()) {
                        if (Item.getAccountNo().equals(tradelinesItem.getAccountNo())) {
                            tradelinesItem.setInstallmentAmount(Item.getInstallmentAmount());
                            tradelinesItem.setMonthDueDay(Item.getMonthDueDay());
                            tradelinesItem.setRepaymentTenure(Item.getRepaymentTenure());
                            tradelinesItem.setInterestRate(Item.getInterestRate());
                            dataEMI.add(tradelinesItem);
                        } else {
                            dataEMI.add(tradelinesItem);
                            break;
                        }
                    }
                }

                for (int i = 0; i < dataEMI.size(); i++) {
                    System.out.println("Repayment Tenure :- " + dataEMI.get(i).getRepaymentTenure());
                }
            }

            if (mData.getReport().getTradelines() != null && mData.getReport().getTradelines().size() > 0) {
                for (TradelinesItem Item : mData.getReport().getTradelines()) {
                    if (Item.getAccountStatus().equalsIgnoreCase("OPEN")) {
                        total_sanction_amount = total_sanction_amount + Integer.parseInt(Item.getSanctionAmount());
                        total_balance = total_balance + Integer.parseInt(Item.getCurrentBalanceAmount());
                        total_paid = total_paid + (Integer.parseInt(Item.getSanctionAmount()) - Integer.parseInt(Item.getCurrentBalanceAmount()));
                        try {
                            total_overdue = total_overdue + Integer.parseInt(Item.getOverdueAmount());
                        } catch (Exception ex) {
                        }
                        mActiveData.add(Item);
                    } else {
                        mCloseData.add(Item);
                    }
                }
            /*binding.txtSanctionAmount.setText(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(total_sanction_amount) + "");
            binding.txtPaidAmount.setText(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(total_paid) + "");
            binding.txtBalanceAmount.setText(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(total_balance) + "");*/

                overdueList = new ArrayList<>();
                for (TradelinesItem Item : mData.getReport().getTradelines()) {
                    if (Item.getAccountStatus().equalsIgnoreCase("OPEN")) {
                        if (Item.isIsOverdue()) {
                            overdueList.add(Item);
                        }
                    }
                }

                binding.txvTotalActiveLoans.setText(mActiveData.size() + "");
                //binding.txvActiveSanction.setText(AmountHelper.getCommaSeptdAmount(total_sanction_amount) + "");
                binding.txvActiveLoanSanctionAmount.setText(getResources().getString(R.string.rs) + " " + convertInLac(Double.parseDouble(String.valueOf(total_sanction_amount))));
                binding.txvActiveLoansPaidAmount.setText(getResources().getString(R.string.rs) + " " + convertInLac(Double.parseDouble(total_paid + "")));
                binding.txvActiveLoansBalance.setText(getResources().getString(R.string.rs) + " " + convertInLac(Double.parseDouble(total_balance + "")));

                int totalWorkingCapitalPaid = 0;

                try {
                    totalWorkingCapitalPaid = mData.getReport().getFacilityMix().getAmountWorkingcapital().getSanctionedAmount() - mData.getReport().getFacilityMix().getAmountWorkingcapital().getCurrentBalance();
                } catch (Exception e) {

                }

                if (mActiveData != null && mActiveData.size() > 0) {
                    binding.llActiveLoans.setVisibility(View.VISIBLE);
                } else {
                    binding.llActiveLoans.setVisibility(View.GONE);
                }

                if (mData.getReport().getFacilityMix().getForex() != null && mData.getReport().getFacilityMix().getForex() != 0) {
                    binding.llForex.setVisibility(View.VISIBLE);
                } else {
                    binding.llForex.setVisibility(View.GONE);
                }

                if (mData.getReport().getFacilityMix().getOthers() != null && mData.getReport().getFacilityMix().getOthers() != 0) {
                    binding.llOtherLoan.setVisibility(View.VISIBLE);
                } else {
                    binding.llOtherLoan.setVisibility(View.GONE);
                }

                if (mData.getReport().getFacilityMix().getNonFundBased() != null && mData.getReport().getFacilityMix().getNonFundBased() != 0) {
                    binding.llNonfundBasedLoans.setVisibility(View.VISIBLE);
                } else {
                    binding.llNonfundBasedLoans.setVisibility(View.GONE);
                }

                if (mData.getReport().getFacilityMix().getTermLoans() != null && mData.getReport().getFacilityMix().getTermLoans() != 0) {
                    binding.llTermsLoans.setVisibility(View.VISIBLE);
                } else {
                    binding.llTermsLoans.setVisibility(View.GONE);
                }

                if (mData.getReport().getFacilityMix().getWorkingCapital() != null && mData.getReport().getFacilityMix().getWorkingCapital() != 0) {
                    binding.llWorkingCapital.setVisibility(View.VISIBLE);
                } else {
                    binding.llWorkingCapital.setVisibility(View.GONE);
                }
                try {

                    binding.txvTotalWorkingCapitalLoans.setText(mData.getReport().getFacilityMix().getWorkingCapital() + "");
                    binding.txvWorkingCapitalBalance.setText(getResources().getString(R.string.rs) + " " + convertInLac(Double.parseDouble(mData.getReport().getFacilityMix().getAmountWorkingcapital().getCurrentBalance() + "")));
                    binding.txvWorkingCapitalSanctionAmount.setText(getResources().getString(R.string.rs) + " " + convertInLac(Double.parseDouble(mData.getReport().getFacilityMix().getAmountWorkingcapital().getSanctionedAmount() + "")));
                    binding.txvWorkingCapitalPaidAmount.setText(getResources().getString(R.string.rs) + " " + convertInLac(Double.parseDouble(totalWorkingCapitalPaid + "")));

                    int forexPaid = mData.getReport().getFacilityMix().getAmountForex().getSanctionedAmount() - mData.getReport().getFacilityMix().getAmountForex().getCurrentBalance();

                    binding.txvTotalForexLoans.setText(mData.getReport().getFacilityMix().getForex() + "");
                    binding.txvForexLoanBalance.setText(getResources().getString(R.string.rs) + " " + convertInLac(Double.parseDouble(mData.getReport().getFacilityMix().getAmountForex().getCurrentBalance() + "")));
                    binding.txvForexLoanSanctionAmount.setText(getResources().getString(R.string.rs) + " " + convertInLac(Double.parseDouble(mData.getReport().getFacilityMix().getAmountForex().getSanctionedAmount() + "")));
                    binding.txvForexLoanPaidAmount.setText(getResources().getString(R.string.rs) + " " + convertInLac(Double.parseDouble(forexPaid + "")));

                    int nonfundPaid = mData.getReport().getFacilityMix().getAmountNonfundbased().getSanctionedAmount() - mData.getReport().getFacilityMix().getAmountNonfundbased().getCurrentBalance();

                    binding.txvTotalNonFundBasedLoans.setText(mData.getReport().getFacilityMix().getNonFundBased() + "");
                    binding.txvNonFundBasedBalance.setText(getResources().getString(R.string.rs) + " " + convertInLac(Double.parseDouble(mData.getReport().getFacilityMix().getAmountNonfundbased().getCurrentBalance() + "")));
                    binding.txvNonFundBasedSanctionAmount.setText(getResources().getString(R.string.rs) + " " + convertInLac(Double.parseDouble(mData.getReport().getFacilityMix().getAmountNonfundbased().getSanctionedAmount() + "")));
                    binding.txvNonFundBasedPaidAmount.setText(getResources().getString(R.string.rs) + " " + convertInLac(Double.parseDouble(nonfundPaid + "")));

                    int otherPaid = mData.getReport().getFacilityMix().getAmountOthers().getSanctionedAmount() - mData.getReport().getFacilityMix().getAmountOthers().getCurrentBalance();

                    binding.txvTotalOthersLoans.setText(mData.getReport().getFacilityMix().getOthers() + "");
                    binding.txvOthersBalance.setText(getResources().getString(R.string.rs) + " " + convertInLac(Double.parseDouble(mData.getReport().getFacilityMix().getAmountOthers().getCurrentBalance() + "")));
                    binding.txvOthersSanctionAmount.setText(getResources().getString(R.string.rs) + " " + convertInLac(Double.parseDouble(mData.getReport().getFacilityMix().getAmountOthers().getSanctionedAmount() + "")));
                    binding.txvOthersPaidAmount.setText(getResources().getString(R.string.rs) + " " + convertInLac(Double.parseDouble(otherPaid + "")));

                    int termLoanPaid = mData.getReport().getFacilityMix().getAmountTermloans().getSanctionedAmount() - mData.getReport().getFacilityMix().getAmountTermloans().getCurrentBalance();

                    binding.txvTotalTermLoans.setText(mData.getReport().getFacilityMix().getTermLoans() + "");
                    binding.txvTermLoanBalance.setText(getResources().getString(R.string.rs) + " " + convertInLac(Double.parseDouble(mData.getReport().getFacilityMix().getAmountTermloans().getCurrentBalance() + "")));
                    binding.txvTermLoanSanctionAmount.setText(getResources().getString(R.string.rs) + " " + convertInLac(Double.parseDouble(mData.getReport().getFacilityMix().getAmountTermloans().getSanctionedAmount() + "")));
                    binding.txvTermLoanPaidAmount.setText(getResources().getString(R.string.rs) + " " + convertInLac(Double.parseDouble(termLoanPaid + "")));
                } catch (Exception ex) {

                }
                int maxAmount = total_sanction_amount;
                int progressAmount = (int) (total_sanction_amount - total_balance);

                EquiFaxDebtTradeListAdapter activeAdapter = new EquiFaxDebtTradeListAdapter(mActiveData, dataEMI, getContext());
                binding.recyclerView.setHasFixedSize(true);
                binding.recyclerView.setNestedScrollingEnabled(false);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                binding.recyclerView.setAdapter(activeAdapter);

                EquiFaxDebtTradeListAdapter adapter = new EquiFaxDebtTradeListAdapter(mCloseData, dataEMI, getContext());
                binding.recyclerViewClose.setHasFixedSize(true);
                binding.recyclerViewClose.setNestedScrollingEnabled(false);
                binding.recyclerViewClose.setLayoutManager(new LinearLayoutManager(getActivity()));
                binding.recyclerViewClose.setAdapter(adapter);

            /*EquiFaxOverdueTradeListAdapter overdueAdapter = new EquiFaxOverdueTradeListAdapter(overdueList, getContext());
            binding.recyclerViewOverdue.setHasFixedSize(true);
            binding.recyclerViewOverdue.setNestedScrollingEnabled(false);
            binding.recyclerViewOverdue.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.recyclerViewOverdue.setAdapter(overdueAdapter);*/

                if (overdueList != null && overdueList.size() > 0) {
                    EquiFaxOverdueTradeListAdapter overdueAdapter = new EquiFaxOverdueTradeListAdapter(overdueList, getContext());
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


            } else {
                //binding.rootLayout.setVisibility(View.GONE);
            }
            //}
        } catch (Exception ex) {
            Logger.errorLogger("Commercial Report", ex.getMessage());
        }

    }

    private void createCommercialExcelData() {
        String strRiskType = "";
        String userName = StringHelper.toCamelCase(mData.getReport().getLegalName());
        mIndividualData = mReportHelper.getRetailReport();
        mActiveData = new ArrayList<>();
        mCloseData = new ArrayList<>();

        if (mData.getReport().getScoreComment().equalsIgnoreCase("Low Risk")) {
            strRiskType = getResources().getString(R.string.low_risk);
        } else if (mData.getReport().getScoreComment().equalsIgnoreCase("High Risk")) {
            strRiskType = getResources().getString(R.string.high_risk);
        } else if (mData.getReport().getScoreComment().equalsIgnoreCase("Very High Risk")) {
            strRiskType = getResources().getString(R.string.very_high_risk);
        } else if (mData.getReport().getScoreComment().equalsIgnoreCase("Medium Risk")) {
            strRiskType = getResources().getString(R.string.medium_risk);
        }

        if (mData.getReport().getTradelines().size() > 0 && mData.getReport().getTradelines() != null) {
            for (TradelinesItem Item : mData.getReport().getTradelines()) {
                if (Item.getAccountStatus().equalsIgnoreCase("OPEN")) {
                    /*total_sanction_amount = total_sanction_amount + Integer.parseInt(Item.getSanctionAmount());
                    total_balance = total_balance + Integer.parseInt(Item.getCurrentBalanceAmount());
                    total_paid = total_paid + (Integer.parseInt(Item.getSanctionAmount()) - Integer.parseInt(Item.getCurrentBalanceAmount()));
                    try {
                        total_overdue = total_overdue + Integer.parseInt(Item.getOverdueAmount());
                    } catch (Exception ex) {
                    }*/
                    mActiveData.add(Item);
                } else {
                    mCloseData.add(Item);
                }
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

        HSSFRow rows = hssfSheet.createRow(0);
        HSSFCell nameCell = rows.createCell(0);
        HSSFCell nameValueCell = rows.createCell(1);
        nameCell.setCellValue("Name");
        nameValueCell.setCellValue(userName);

        HSSFRow row1 = hssfSheet.createRow(1);
        HSSFCell scoreCell = row1.createCell(0);
        HSSFCell scoreValueCell = row1.createCell(1);
        scoreCell.setCellValue("Commercial rank");
        scoreValueCell.setCellValue(mData.getReport().getScoreValue() + " (Equifax)");

        HSSFRow row2 = hssfSheet.createRow(2);
        HSSFCell riskCell = row2.createCell(0);
        HSSFCell riskValueCell = row2.createCell(1);
        riskCell.setCellValue("Risk Rank");
        riskValueCell.setCellValue(strRiskType);

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


        HSSFRow working = hssfSheet.createRow(8);
        HSSFCell workingCapital = working.createCell(0);
        HSSFCell workingCapitalValue = working.createCell(1);
        HSSFCell workingCapitalSanctionAmount = working.createCell(2);
        HSSFCell workingCapitalPaidAmount = working.createCell(3);
        HSSFCell workingCapitalBalanceAmount = working.createCell(4);
        workingCapital.setCellValue("Working capital");
        workingCapitalValue.setCellValue(mData.getReport().getFacilityMix().getWorkingCapital() + "");
        workingCapitalSanctionAmount.setCellValue(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(mData.getReport().getFacilityMix().getAmountWorkingcapital().getSanctionedAmount()));
        workingCapitalPaidAmount.setCellValue(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(mData.getReport().getFacilityMix().getAmountWorkingcapital().getSanctionedAmount() - mData.getReport().getFacilityMix().getAmountWorkingcapital().getCurrentBalance()));
        workingCapitalBalanceAmount.setCellValue(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(mData.getReport().getFacilityMix().getAmountWorkingcapital().getCurrentBalance()));

        HSSFRow terms = hssfSheet.createRow(9);
        HSSFCell termsLoan = terms.createCell(0);
        HSSFCell termsLoanValue = terms.createCell(1);
        HSSFCell termsLoanSanctionAmount = terms.createCell(2);
        HSSFCell termsLoanPaidAmount = terms.createCell(3);
        HSSFCell termsLoanBalanceAmount = terms.createCell(4);
        termsLoan.setCellValue("Term Loans");
        termsLoanValue.setCellValue(mData.getReport().getFacilityMix().getTermLoans() + "");
        termsLoanSanctionAmount.setCellValue(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(mData.getReport().getFacilityMix().getAmountTermloans().getSanctionedAmount()));
        termsLoanPaidAmount.setCellValue(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(mData.getReport().getFacilityMix().getAmountTermloans().getSanctionedAmount() - mData.getReport().getFacilityMix().getAmountTermloans().getCurrentBalance()));
        termsLoanBalanceAmount.setCellValue(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(mData.getReport().getFacilityMix().getAmountTermloans().getCurrentBalance()));


        HSSFRow nonfund = hssfSheet.createRow(10);
        HSSFCell nonfundLoan = nonfund.createCell(0);
        HSSFCell nonfundLoanValue = nonfund.createCell(1);
        HSSFCell nonfundLoanSanctionAmount = nonfund.createCell(2);
        HSSFCell nonfundLoanPaidAmount = nonfund.createCell(3);
        HSSFCell nonfundLoanBalanceAmount = nonfund.createCell(4);
        nonfundLoan.setCellValue("Non Fund based");
        nonfundLoanValue.setCellValue(mData.getReport().getFacilityMix().getNonFundBased() + "");
        nonfundLoanSanctionAmount.setCellValue(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(mData.getReport().getFacilityMix().getAmountNonfundbased().getSanctionedAmount()));
        nonfundLoanPaidAmount.setCellValue(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(mData.getReport().getFacilityMix().getAmountNonfundbased().getSanctionedAmount() - mData.getReport().getFacilityMix().getAmountNonfundbased().getCurrentBalance()));
        nonfundLoanBalanceAmount.setCellValue(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(mData.getReport().getFacilityMix().getAmountNonfundbased().getCurrentBalance()));

        HSSFRow forex = hssfSheet.createRow(11);
        HSSFCell forexLoan = forex.createCell(0);
        HSSFCell forexLoanValue = forex.createCell(1);
        HSSFCell forexLoanSanctionAmount = forex.createCell(2);
        HSSFCell forexLoanPaidAmount = forex.createCell(3);
        HSSFCell forexLoanBalanceAmount = forex.createCell(4);
        forexLoan.setCellValue("Forex");
        forexLoanValue.setCellValue(mData.getReport().getFacilityMix().getForex() + "");
        forexLoanSanctionAmount.setCellValue(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(mData.getReport().getFacilityMix().getAmountForex().getSanctionedAmount()));
        forexLoanPaidAmount.setCellValue(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(mData.getReport().getFacilityMix().getAmountForex().getSanctionedAmount() - mData.getReport().getFacilityMix().getAmountForex().getCurrentBalance()));
        forexLoanBalanceAmount.setCellValue(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(mData.getReport().getFacilityMix().getAmountForex().getCurrentBalance()));

        HSSFRow other = hssfSheet.createRow(12);
        HSSFCell otherLoan = other.createCell(0);
        HSSFCell otherLoanValue = other.createCell(1);
        HSSFCell otherLoanSanctionAmount = other.createCell(2);
        HSSFCell otherLoanPaidAmount = other.createCell(3);
        HSSFCell otherLoanBalanceAmount = other.createCell(4);
        otherLoan.setCellValue("Others");
        otherLoanValue.setCellValue(mData.getReport().getFacilityMix().getOthers() + "");
        otherLoanSanctionAmount.setCellValue(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(mData.getReport().getFacilityMix().getAmountOthers().getSanctionedAmount()));
        otherLoanPaidAmount.setCellValue(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(mData.getReport().getFacilityMix().getAmountOthers().getSanctionedAmount() - mData.getReport().getFacilityMix().getAmountOthers().getCurrentBalance()));
        otherLoanBalanceAmount.setCellValue(getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(mData.getReport().getFacilityMix().getAmountOthers().getCurrentBalance()));


        HSSFRow row6 = hssfSheet.createRow(14);
        HSSFCell totalMonthlyEmiHeading = row6.createCell(0);
        HSSFCell totalMonthlyEMI = row6.createCell(1);
        HSSFCell totalMonthlyEMIAmount = row6.createCell(2);
        totalMonthlyEmiHeading.setCellValue("Total Monthly EMI");
        totalMonthlyEMI.setCellValue("# Number");
        totalMonthlyEMIAmount.setCellValue("Amount");

        HSSFRow row7 = hssfSheet.createRow(15);
        HSSFCell totalMonthlyEmiAmount = row7.createCell(0);
        HSSFCell totalMonths = row7.createCell(1);
        HSSFCell sumMonthlyEMI = row7.createCell(2);
        totalMonthlyEmiAmount.setCellValue("-");
        totalMonths.setCellValue("-");
        sumMonthlyEMI.setCellValue("-");

        HSSFRow row8 = hssfSheet.createRow(16);
        HSSFCell totalYearEmiHeading = row8.createCell(0);
        HSSFCell totalYearEMI = row8.createCell(1);
        HSSFCell totalYearEMIAmount = row8.createCell(2);
        totalYearEmiHeading.setCellValue("Total Annual EMI");
        totalYearEMI.setCellValue("# Number");
        totalYearEMIAmount.setCellValue("Amount");

        HSSFRow row9 = hssfSheet.createRow(17);
        HSSFCell totalYearEmiAmount = row9.createCell(0);
        HSSFCell totalYears = row9.createCell(1);
        HSSFCell sumYearEMI = row9.createCell(2);
        totalYearEmiAmount.setCellValue("-");
        totalYears.setCellValue("-");
        sumYearEMI.setCellValue("-");

        HSSFRow row10 = hssfSheet.createRow(19);
        HSSFCell overdueTitle = row10.createCell(0);
        overdueTitle.setCellValue("Overdue Summary");

        HSSFRow row11 = hssfSheet.createRow(20);
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


        overdueList = new ArrayList<>();
        for (TradelinesItem Item : mData.getReport().getTradelines()) {
            if (Item.isIsOverdue()) {
                overdueList.add(Item);
            }
        }

        System.out.println("EQUIFAX :- " + EquiFaxMyAccount.mData);

        for (int i = 0; i < overdueList.size(); i++) {
            System.out.println("MDATA :- " + overdueList.get(i).getInstitutionName());
            HSSFRow overdueSummery = hssfSheet.createRow(21 + i);
            HSSFCell subscriberCell = overdueSummery.createCell(0);
            HSSFCell accountTypeCell = overdueSummery.createCell(1);
            HSSFCell loanAmountCell = overdueSummery.createCell(2);
            HSSFCell outstandingAmountCell = overdueSummery.createCell(3);
            HSSFCell balanceAmount = overdueSummery.createCell(4);
            HSSFCell overdueAmount = overdueSummery.createCell(5);
            /*String currentBalance = getContext().getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(
                    tradelineList.get(i).getCurrent_balance_amount()));*/

            String currentBalance = getContext().getResources().getString(R.string.rs) + AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble("" +
                    overdueList.get(i).getCurrentBalanceAmount()));

            subscriberCell.setCellValue(overdueList.get(i).getInstitutionName());
            accountTypeCell.setCellValue(overdueList.get(i).getCreditType());
            loanAmountCell.setCellValue(AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble("" + overdueList.get(i).getSanctionAmount())));
            outstandingAmountCell.setCellValue(AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(overdueList.get(i).getCurrentBalanceAmount())));
            balanceAmount.setCellValue(currentBalance);
            overdueAmount.setCellValue(AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(overdueList.get(i).getOverdueAmount())));
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
            subscriberCell.setCellValue(mActiveData.get(i).getInstitutionName());
            accountTypeCell.setCellValue(mActiveData.get(i).getCreditType());
            loanAmountCell.setCellValue(AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(mActiveData.get(i).getSanctionAmount())));
            outstandingAmountCell.setCellValue(AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(mActiveData.get(i).getCurrentBalanceAmount())));
            loanSanctionDate.setCellValue((mActiveData.get(i).getSanctionDate()));
        }

        for (int i = 0; i < mCloseData.size(); i++) {
            HSSFRow closeLoanSheetRow = closeLoanSheet.createRow(i + 1);
            HSSFCell subscriberCell = closeLoanSheetRow.createCell(0);
            HSSFCell accountTypeCell = closeLoanSheetRow.createCell(1);
            HSSFCell loanAmountCell = closeLoanSheetRow.createCell(2);
            HSSFCell outstandingAmountCell = closeLoanSheetRow.createCell(3);
            HSSFCell loanSanctionDate = closeLoanSheetRow.createCell(4);
            subscriberCell.setCellValue(mCloseData.get(i).getInstitutionName());
            accountTypeCell.setCellValue(mCloseData.get(i).getCreditType());
            loanAmountCell.setCellValue(AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(mCloseData.get(i).getSanctionAmount())));
            outstandingAmountCell.setCellValue(AmountHelper.getCommaSeptdAmount(AmountHelper.convertStringToDouble(mCloseData.get(i).getCurrentBalanceAmount())));
            loanSanctionDate.setCellValue((mCloseData.get(i).getSanctionDate()));
        }


        try {
            if (!filePath.exists()) {
                filePath.createNewFile();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            hssfWorkbook.write(fileOutputStream);

            fileOutputStream.flush();
            fileOutputStream.close();
            openXLS(filePath.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openXLS(final String path) {
        File file = new File(path);
        Uri uri;
        uri = FileProvider.getUriForFile(requireContext(), getContext().getPackageName() + ".provider", file);

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
