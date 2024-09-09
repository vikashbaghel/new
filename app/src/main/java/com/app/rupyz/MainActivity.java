package com.app.rupyz;

import static com.app.rupyz.generic.utils.SharePrefConstant.NAME;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.app.rupyz.databinding.ActivityMainBinding;
import com.app.rupyz.generic.helper.StringHelper;
import com.app.rupyz.generic.logger.FirebaseLogger;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.home.AlertFragment;
import com.app.rupyz.ui.home.DebtProfileFragment;
import com.app.rupyz.ui.home.HomeFragment;
import com.app.rupyz.ui.home.LearnFragment;
import com.app.rupyz.ui.home.SettingFragment;
import com.app.rupyz.ui.home.activity.HomeDetailActivity;
import com.app.rupyz.ui.user.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {

    private BottomNavigationView mBottomNavigationBar;
    private ConstraintLayout container;
    boolean doubleBackToExitPressedOnce = false;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new FirebaseLogger(this).sendLog("Home Activity", "Home Activity");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        updatePrefix(SharedPref.getInstance().getString(NAME));
        setContentView(binding.getRoot());
        initLayout();
    }

    private void initLayout() {
        mBottomNavigationBar = findViewById(R.id.bottomNavigationView);
        mBottomNavigationBar.setOnNavigationItemSelectedListener(this);
        container = findViewById(R.id.container);

        loadFragment(new HomeFragment());
    }

    public void updatePrefix(String userName) {
        try {
            binding.navHeaderView.userPrefix.setText(StringHelper.printName(userName).trim().substring(0, 1));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_accept:
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragment = new HomeFragment();
                break;
            case R.id.navigation_alert:
                fragment = new AlertFragment();
                break;
            case R.id.navigation_learn:
                fragment = new LearnFragment();
                break;
            case R.id.navigation_debt_profile:
                fragment = new DebtProfileFragment();
                break;
            case R.id.navigation_menu:
                fragment = new SettingFragment();
                break;
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (getVisibleFragment() instanceof HomeFragment) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Snackbar snackbar = Snackbar
                    .make(container, getResources().getString(R.string.alert_press_again), Snackbar.LENGTH_SHORT);
            snackbar.show();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            loadFragment(new HomeFragment());
            mBottomNavigationBar.setSelectedItemId(R.id.navigation_home);
        }
    }

    private Fragment getVisibleFragment() {
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }

}