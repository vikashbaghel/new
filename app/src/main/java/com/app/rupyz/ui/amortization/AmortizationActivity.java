package com.app.rupyz.ui.amortization;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.app.rupyz.R;
import com.app.rupyz.adapter.amortization.AmortizationListAdapter;
import com.app.rupyz.databinding.ActivityAmortizationBinding;
import com.app.rupyz.generic.model.amortization.AmortizationInfo;

import java.util.ArrayList;

public class AmortizationActivity extends AppCompatActivity {
    ActivityAmortizationBinding binding;
    private Context context;
    String myValue = "";
    String loanAmount, year, annualRate, emi;
    double timeInYear;
    ArrayList<AmortizationInfo> amortizationInfoArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAmortizationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolBar = this.findViewById(R.id.toolbar_my);
        ImageView imageViewBack = toolBar.findViewById(R.id.img_back);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void init() {
        context = this;
        myValue = getIntent().getStringExtra("data");
        loanAmount = getIntent().getStringExtra("loanAmount");
        year = getIntent().getStringExtra("years");
        annualRate = getIntent().getStringExtra("annualRate");
        emi = getIntent().getStringExtra("emi");
        timeInYear = Double.parseDouble(year) / 12;

        double monthlyRate = Double.parseDouble(annualRate) / 1200;

        // Calculat montly payment
        double monthlyPayment = Double.parseDouble(loanAmount) * monthlyRate / (1 - 1 /
                Math.pow(1 + monthlyRate, timeInYear * 12));

        // Display montly payment
        System.out.printf("Monthly Payment: %.2f\n", monthlyPayment);

        // Display total payment
        System.out.printf("Total Payment: %.2f\n", (monthlyPayment * 12) * timeInYear);

        // Create amortization schedule
        double balance = Double.parseDouble(loanAmount),
                principal, interest;
        System.out.println("Payment#     Interest     Principal     Balance");
        for (int i = 1; i <= timeInYear * 12; i++) {
            interest = monthlyRate * balance;
            principal = monthlyPayment - interest;
            balance = balance - principal;
            AmortizationInfo amortizationInfo = new AmortizationInfo();
            amortizationInfo.setPayment(String.valueOf(i));
            amortizationInfo.setBalance(balance);
            amortizationInfo.setInterest(interest);
            amortizationInfo.setPrincipal(principal);
            amortizationInfo.setEmi(Double.parseDouble(emi));
            amortizationInfoArrayList.add(amortizationInfo);
            System.out.printf("%-13d%-13.2f%-13.2f%.2f\n", i, interest,
                    principal, balance);
            System.out.println("INTEREST :- "+interest);
        }

        AmortizationListAdapter adapter = new AmortizationListAdapter(amortizationInfoArrayList, AmortizationActivity.this);


        binding.recyclerViewAmortization.setLayoutManager(new LinearLayoutManager(context));
        //adapter = new CompareEmiListAdapter(mData, context);
        binding.recyclerViewAmortization.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}