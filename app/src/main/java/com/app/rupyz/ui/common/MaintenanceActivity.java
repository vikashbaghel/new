package com.app.rupyz.ui.common;

import android.os.Bundle;
import android.view.View;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityMaintenanceBinding;
import com.app.rupyz.generic.base.BaseActivity;

public class MaintenanceActivity extends BaseActivity implements View.OnClickListener{

    ActivityMaintenanceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMaintenanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                if(hasInternetConnection()){
                    finish();
                }
                break;
        }
    }
}