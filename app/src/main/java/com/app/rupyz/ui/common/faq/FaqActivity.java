package com.app.rupyz.ui.common.faq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.app.rupyz.R;
import com.app.rupyz.adapter.faq.FaqListAdapter;
import com.app.rupyz.adapter.individual.AlertListAdapter;
import com.app.rupyz.databinding.ActivityFaqBinding;
import com.app.rupyz.generic.faq.FaqDataHelper;
import com.app.rupyz.generic.logger.FirebaseLogger;

public class FaqActivity extends AppCompatActivity {

    ActivityFaqBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFaqBinding.inflate(getLayoutInflater());
        new FirebaseLogger(this).sendLog("FAQ", "FAQ");
        setContentView(binding.getRoot());
        initToolbar();
        initLayout();
    }

    private void initLayout() {
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FaqListAdapter adapter = new FaqListAdapter(FaqDataHelper.getFaqData(), this);
        binding.recyclerView.setAdapter(adapter);
        binding.message.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);
    }


    private void initToolbar() {
        Toolbar toolBar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("FAQ");
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