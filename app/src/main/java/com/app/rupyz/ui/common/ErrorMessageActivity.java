package com.app.rupyz.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.app.rupyz.InitialActivity;
import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityErrorMessageBinding;
import com.app.rupyz.generic.base.BaseActivity;

public class ErrorMessageActivity extends BaseActivity implements View.OnClickListener {
    ActivityErrorMessageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityErrorMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initLayout();
    }

    private void initLayout() {
        binding.btnRetry.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_retry:
                if(hasInternetConnection()){
                    Intent mainIntent = new Intent(ErrorMessageActivity.this, InitialActivity.class);
                    ErrorMessageActivity.this.startActivity(mainIntent);
                    finish();
                }
                break;
        }
    }
}