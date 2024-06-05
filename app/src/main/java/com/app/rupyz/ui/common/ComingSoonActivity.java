package com.app.rupyz.ui.common;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityComingSoonBinding;

public class ComingSoonActivity extends AppCompatActivity {
    ActivityComingSoonBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComingSoonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initLayout();
    }

    private void initLayout() {
        Toolbar toolBar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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