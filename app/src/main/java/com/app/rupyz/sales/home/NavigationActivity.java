package com.app.rupyz.sales.home;


import static com.app.rupyz.generic.utils.AppConstant.POLICY_URL;
import static com.app.rupyz.generic.utils.AppConstant.TERMS_URL;
import static com.app.rupyz.generic.utils.SharePrefConstant.LEGAL_NAME;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.BuildConfig;
import com.app.rupyz.R;
import com.app.rupyz.adapter.navigation_view.NavigationViewAdapter;
import com.app.rupyz.databinding.ActivityNavigationBinding;
import com.app.rupyz.generic.base.BaseActivity;
import com.app.rupyz.generic.base.BrowserActivity;
import com.app.rupyz.generic.navigation_view.NavigationViewData;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.utils.AppConstant;
import com.app.rupyz.generic.utils.RecyclerTouchListener;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.sales.staffactivitytrcker.FragmentContainerActivity;

public class NavigationActivity extends BaseActivity {

    private ActivityNavigationBinding binding;
    private NavigationViewAdapter mAdapter;
    private Utility mUtil;
    private ApiInterface mApiInterface;
    private boolean isDataUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        mUtil = new Utility(this);

        initToolbar();
        initNavigationView();
    }

    private void initToolbar() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }


    @SuppressLint("SetTextI18n")
    private void initNavigationView() {
        binding.toolbar.setTitle(SharedPref.getInstance().getString(LEGAL_NAME));

        binding.navList.setHasFixedSize(true);
        binding.navList.setLayoutManager(new LinearLayoutManager(this));
        binding.navList.addOnItemTouchListener(new RecyclerTouchListener(this, binding.navList, (view, position) -> {
            switch (position) {
                case 0:
                    if (hasInternetConnection()) {
                        someActivityResultLauncher.launch(new Intent(this, FragmentContainerActivity.class).putExtra(AppConstant.IMAGE_TYPE_PROFILE, true));
                    } else {
                        showToast(getResources().getString(R.string.this_feature_isn_t_available_offline));
                    }
                    break;
                case 1:
                    startActivity(new Intent(this, FragmentContainerActivity.class).putExtra(AppConstant.USER_PREFERENCE, true));
                    break;
                case 2:
                    Utility.shareApp(NavigationActivity.this, (BitmapDrawable) binding.imageShare.getDrawable());
                    break;
                case 3:
                    Utility.rateApp(NavigationActivity.this);
                    break;
                case 4:
                    if (hasInternetConnection()) {
                        initOpenBrowser(TERMS_URL, "Terms of Service");
                    } else {
                        showToast(getResources().getString(R.string.this_feature_isn_t_available_offline));
                    }
                    break;
                case 5:
                    if (hasInternetConnection()) {
                        initOpenBrowser(POLICY_URL, "Privacy Policy");
                    } else {
                        showToast(getResources().getString(R.string.this_feature_isn_t_available_offline));
                    }
                    break;
                case 6:
                    logout();
                    break;
            }
        }));
        mAdapter = new

                NavigationViewAdapter(new NavigationViewData().

                getNavigationData(), this);
        binding.navList.setVisibility(View.VISIBLE);
        binding.navList.setAdapter(mAdapter);

        binding.tvAppVersion.setText("V " + BuildConfig.VERSION_NAME);
    }

    private void initOpenBrowser(String url, String title) {
        Intent intent = new Intent(this, BrowserActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            isDataUpdated = true;
        }
    });

    @Override
    public void onBackPressed() {
        if (isDataUpdated) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }
}