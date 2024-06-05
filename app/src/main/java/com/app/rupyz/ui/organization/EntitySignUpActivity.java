package com.app.rupyz.ui.organization;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.app.rupyz.R;
import com.app.rupyz.ui.organization.adapter.ViewPagerAdapter;
import com.app.rupyz.ui.organization.fragment.AuthAccountFragment;
import com.app.rupyz.ui.organization.fragment.DOBFragment;
import com.app.rupyz.ui.organization.fragment.GSTFragment;
import com.app.rupyz.ui.organization.fragment.PanVerifyFragment;
import com.google.android.material.tabs.TabLayout;

public class EntitySignUpActivity extends AppCompatActivity {
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TextView tabs_count;
    private String json_response;

    public String sendData() {
        return json_response;
    }

    public void receivedData(String json_response) {
        this.json_response = json_response;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_sign_up);
        initLayout();
    }

    private void initLayout() {
        tabs_count = findViewById(R.id.tabs_count);
        tabs_count.setText("1/4");
        TabLayout tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);
        // setting up the adapter
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        // add the fragments
        viewPagerAdapter.add(new PanVerifyFragment(this, getIntent().getExtras().getString("org_type")), "");
        viewPagerAdapter.add(new GSTFragment(this), "");
        viewPagerAdapter.add(new AuthAccountFragment(this), "");
        viewPagerAdapter.add(new DOBFragment(this), "");
//        viewPagerAdapter.add(new PreviewFragment(), "");
        tabLayout.setupWithViewPager(viewPager);
        // Set the adapter
        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(viewPagerAdapter);

    }

    public void updateViewPager(int index) {
        viewPager.setCurrentItem(index);
        viewPagerAdapter.notifyDataSetChanged();
        tabs_count.setText(index + 1 + "/4");
    }
}