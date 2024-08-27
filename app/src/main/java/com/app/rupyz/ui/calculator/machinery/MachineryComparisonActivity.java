package com.app.rupyz.ui.calculator.machinery;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.R;
import com.app.rupyz.adapter.loan.ComparisonEmiListAdapter;
import com.app.rupyz.adapter.loan.ComparisonMachineryListAdapter;
import com.app.rupyz.databinding.ActivityComparisonEmiBinding;
import com.app.rupyz.databinding.ActivityComparisonMachineryBinding;
import com.app.rupyz.generic.model.loan.EmiInfoModel;
import com.app.rupyz.generic.model.loan.MachineryInfoModel;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public class MachineryComparisonActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityComparisonMachineryBinding binding;
    String myValue = "";
    private ImageButton mClose;
    private List<MachineryInfoModel> mData;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComparisonMachineryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolBar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void init(){
        context=this;
        myValue = getIntent().getStringExtra("data");
        System.out.println("EMI DATA" + myValue);
        binding.recyclerviewCompareLoan.setHasFixedSize(true);
        binding.recyclerviewCompareLoan.setLayoutManager(new LinearLayoutManager(context));
        Gson gson = new Gson();

        mData = Arrays.asList(gson.fromJson(myValue, MachineryInfoModel[].class));
        System.out.println("DATA :- "+mData.get(0).getEmi());
        System.out.println("DATA :- "+mData.get(0).getEmi());
        ComparisonMachineryListAdapter adapter = new ComparisonMachineryListAdapter(mData, context);
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