package com.app.rupyz.generic.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityBrowserBinding;

import java.util.Objects;

public class BrowserActivity extends AppCompatActivity {

    ActivityBrowserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBrowserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initLayout();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initLayout() {
        Toolbar toolBar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(Objects.requireNonNull(getIntent().getExtras()).getString("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.loadUrl(Objects.requireNonNull(getIntent().getExtras().getString("url")));

        binding.webView.setWebViewClient(new WebViewClient());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}