package com.app.rupyz.ui.equifax.activity;

import static com.app.rupyz.generic.utils.AppConstant.POLICY_URL;
import static com.app.rupyz.generic.utils.AppConstant.TERMS_URL;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.app.rupyz.adapter.navigation_view.NavigationViewAdapter;
import com.app.rupyz.databinding.ActivityNavigationBinding;
import com.app.rupyz.generic.base.BrowserActivity;
import com.app.rupyz.generic.helper.StringHelper;
import com.app.rupyz.generic.json.JsonHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.navigation_view.NavigationViewData;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.utils.RecyclerTouchListener;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavigationActivity extends AppCompatActivity {
    private ActivityNavigationBinding binding;
    private NavigationViewAdapter mAdapter;
    private Utility mUtil;
    private ApiInterface mApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        mUtil = new Utility(this);
        initLayout();
        initNavigationView();
    }

    private void initLayout() {
//        binding.navHeaderView.txtUserName.setText(Utility.getLegalName());
//        binding.navHeaderView.txtEmail.setText(Utility.getOrgEmail());
//        binding.navHeaderView.txtMobile.setText(Utility.getOrgMobile());
//        binding.navHeaderView.txtPan.setText(Utility.getOrgPan());
//        binding.navHeaderView.txtGst.setText(Utility.getOrgGst());
//        try {
//            binding.navHeaderView.userPrefix.setText(StringHelper.printName(Utility.getLegalName()).trim().substring(0, 1));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initNavigationView() {
        binding.navList.setHasFixedSize(true);
        binding.navList.setLayoutManager(new LinearLayoutManager(this));
        binding.navList.addOnItemTouchListener(new RecyclerTouchListener(this, binding.navList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                switch (position) {
                    case 0:
                        Utility.shareApp(NavigationActivity.this, (BitmapDrawable) binding.imageShare.getDrawable());
                        break;
                    case 1:
                        Utility.rateApp(NavigationActivity.this);
                        break;
                    case 2:
                        initOpenBrowser(TERMS_URL, "Terms of Service");
                        break;
                    case 3:
                        initOpenBrowser(POLICY_URL, "Privacy Policy");
                        break;
                    case 4:
                        doLogout();
                        break;
                }
            }
        }));
        mAdapter = new NavigationViewAdapter(new NavigationViewData().getNavigationData(), this);
        binding.navList.setVisibility(View.VISIBLE);
        binding.navList.setAdapter(mAdapter);
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

    private void doLogout() {
        Call<String> call1 = mApiInterface.logout(JsonHelper.getLogoutJson(
                "Android"), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Logger.errorLogger(this.getClass().getName(), response.code() + "");
                mUtil.logout();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                mUtil.logout();
            }
        });
    }
}