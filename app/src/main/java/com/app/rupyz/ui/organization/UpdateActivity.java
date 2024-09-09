package com.app.rupyz.ui.organization;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.app.rupyz.databinding.ActivityReviewBinding;
import com.app.rupyz.databinding.ActivityUpdateBinding;

public class UpdateActivity extends AppCompatActivity {

    private ActivityUpdateBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
    }

    private void initLayout() {

    }
}