package com.app.rupyz.sales.organization;

import static com.app.rupyz.generic.utils.SharePrefConstant.IS_LOGIN;
import static com.app.rupyz.generic.utils.SharePrefConstant.NAME;
import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_INFO;
import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_STEP;
import static com.app.rupyz.generic.utils.SharePrefConstant.STAFF_AND_CUSTOMER_MAPPING;
import static com.app.rupyz.generic.utils.SharePrefConstant.USER_INFO;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.R;
import com.app.rupyz.adapter.itemdecorator.DividerItemDecorator;
import com.app.rupyz.adapter.organization.OrganizationListAdapter;
import com.app.rupyz.databinding.ActivityChooseOrganizationBinding;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.model_kt.UserInfoData;
import com.app.rupyz.sales.home.SalesMainActivity;
import com.app.rupyz.ui.more.MoreViewModel;
import com.google.gson.Gson;


public class ChooseOrganizationActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityChooseOrganizationBinding binding;
    private UserInfoData mData;
    private EquiFaxReportHelper mReportHelper;
    private MoreViewModel moreViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseOrganizationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        moreViewModel = new ViewModelProvider(this).get(MoreViewModel.class);
        initObservers();

        mReportHelper = EquiFaxReportHelper.getInstance();

        initLayout();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_next) {
            doRequest();
        }
    }

    private void initLayout() {
        binding.btnNext.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecorator itemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(this, R.drawable.item_divider_gray));
        binding.recyclerView.addItemDecoration(itemDecoration);
        try {
            mData = new Gson().fromJson(SharedPref.getInstance().getString(USER_INFO), UserInfoData.class);
            if (mData != null && mData.getOrgIds().size() > 0) {
                binding.recyclerView.setAdapter(new OrganizationListAdapter(this, mData.getOrgIds()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void doRequest() {
        SharedPref.getInstance().putModelClass(USER_INFO, mData);

        if (SharedPref.getInstance().getInt(ORG_ID) == 0) {
            Toast.makeText(ChooseOrganizationActivity.this, "Please Select an Organization", Toast.LENGTH_SHORT).show();
        } else if (SharedPref.getInstance().getString(ORG_STEP).equalsIgnoreCase("1")) {
            Intent intent = new Intent(ChooseOrganizationActivity.this, ChooseGstActivity.class);
            intent.putExtra("org_id", mReportHelper.getOrgId());
            startActivity(intent);
        } else if (SharedPref.getInstance().getString(ORG_STEP).equalsIgnoreCase("2")) {
            startActivity(new Intent(ChooseOrganizationActivity.this, AddBusinessInfoActivity.class));
        } else {
            SharedPref.getInstance().putBoolean(IS_LOGIN, true);
            Intent intent = new Intent(ChooseOrganizationActivity.this, SalesMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();
        }
    }

    public void updatePanNumber(int index) {

        mData.getOrgIds();
        if (mData.getOrgIds().get(index) != null && mData.getOrgIds().get(index).getId() != null) {
            mReportHelper.setOrgId(mData.getOrgIds().get(index).getId());
            SharedPref.getInstance().putInt(ORG_ID, mData.getOrgIds().get(index).getId());
        }
        SharedPref.getInstance().putString(NAME, mData.getOrgIds().get(index).getLegalName());
        moreViewModel.getPreferencesInfo();

        SharedPref.getInstance().putModelClass(ORG_INFO, mData.getOrgIds().get(index));
    }


    private void initObservers() {
        moreViewModel.getPreferenceLiveData().observe(this, data -> {
            if (data.getData() != null && data.getData().getStaffCustomerMapping() != null) {
                SharedPref.getInstance().putBoolean(STAFF_AND_CUSTOMER_MAPPING, data.getData().getStaffCustomerMapping());
            }
        });
    }

}