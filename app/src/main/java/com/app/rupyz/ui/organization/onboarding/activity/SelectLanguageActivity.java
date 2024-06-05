package com.app.rupyz.ui.organization.onboarding.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityAuthAccountBinding;
import com.app.rupyz.databinding.ActivitySelectLanguageBinding;
import com.app.rupyz.ui.organization.onboarding.adapter.LanguageAdapter;
import com.app.rupyz.ui.organization.onboarding.model.LanguageListData;

import org.apache.poi.sl.draw.geom.Context;

public class SelectLanguageActivity extends AppCompatActivity implements LanguageAdapter.ItemClickListener {

    ActivitySelectLanguageBinding binding;
    LanguageAdapter languageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectLanguageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LanguageListData[] languageListData = new LanguageListData[]{
                new LanguageListData("हिंदी", "En"),
                new LanguageListData("English", "En"),
        };

        languageAdapter = new LanguageAdapter(languageListData, this, this);
        binding.recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(languageAdapter);
    }

    @Override
    public void onItemClick(LanguageListData languageListData) {
        Intent intent = new Intent(SelectLanguageActivity.this, MobileNumberActivity.class);
        startActivity(intent);
    }
}