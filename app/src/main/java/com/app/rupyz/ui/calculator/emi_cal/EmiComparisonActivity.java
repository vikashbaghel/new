package com.app.rupyz.ui.calculator.emi_cal;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.R;
import com.app.rupyz.adapter.loan.ComparisonEmiListAdapter;
import com.app.rupyz.databinding.ActivityComparisonEmiBinding;
import com.app.rupyz.generic.model.loan.EmiInfoModel;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public class EmiComparisonActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityComparisonEmiBinding binding;
    String myValue = "";
    private ImageButton mClose;
    private List<EmiInfoModel> mData;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComparisonEmiBinding.inflate(getLayoutInflater());
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

    private void init(){
        context=this;
        myValue = getIntent().getStringExtra("data");
        System.out.println("EMI DATA" + myValue);
        binding.recyclerviewCompareLoan.setHasFixedSize(true);
        binding.recyclerviewCompareLoan.setLayoutManager(new LinearLayoutManager(context));
        Gson gson = new Gson();
        // mData = gson.fromJson(myValue, LoanInfoModel.class);

        mData = Arrays.asList(gson.fromJson(myValue, EmiInfoModel[].class));
        ComparisonEmiListAdapter adapter = new ComparisonEmiListAdapter(mData, context);
        binding.recyclerviewCompareLoan.setAdapter(adapter);
       // binding.btnAmortization.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}