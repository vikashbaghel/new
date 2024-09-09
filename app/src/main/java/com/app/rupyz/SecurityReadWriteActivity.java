package com.app.rupyz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.app.rupyz.databinding.ActivityErrorMessageBinding;
import com.app.rupyz.databinding.ActivitySecurityReadWriteBinding;

public class SecurityReadWriteActivity extends AppCompatActivity implements View.OnClickListener {
    ActivitySecurityReadWriteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecurityReadWriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initLayout();
    }

    private void initLayout() {
        binding.btnTryAgain.setOnClickListener(this);
        binding.btnReTry.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_retry:
            case R.id.btn_re_try:
            case R.id.btn_try_again:
                Intent intent = new Intent(SecurityReadWriteActivity.this, InitialActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}