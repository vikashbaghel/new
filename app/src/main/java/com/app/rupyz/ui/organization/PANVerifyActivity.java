package com.app.rupyz.ui.organization;

import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;
import static com.app.rupyz.generic.utils.SharePrefConstant.USER_TYPE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.rupyz.R;
import com.app.rupyz.databinding.PanVerifyFragmentBinding;
import com.app.rupyz.generic.helper.ButtonStyleHelper;
import com.app.rupyz.generic.json.JsonHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.individual.ProfileUpdateActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PANVerifyActivity extends AppCompatActivity implements View.OnClickListener {
    private PanVerifyFragmentBinding binding;
    private ApiInterface mApiInterface;
    private String org_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = PanVerifyFragmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        org_type = SharedPref.getInstance().getString(USER_TYPE);
        new Utility(this);
        initLayout();
    }

    private void initLayout() {
        binding.btnNext.setOnClickListener(this);
        try {
            binding.txtPanCard.setText(getIntent().getExtras().getString("pan_number"));
        } catch (Exception ex) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                if (binding.txtPanCard.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(this, "Pan Number Required.", Toast.LENGTH_SHORT).show();
                } else if (binding.txtPanCard.getText().toString().length() != 10) {
                    Toast.makeText(this, "Valid Pan Number Required.", Toast.LENGTH_SHORT).show();
                } else {
                    new ButtonStyleHelper(this).initButton(false, binding.btnNext,
                            "Please wait...");
                    doRequest();
                }
                break;
        }
    }

    private void doRequest() {
        Call<String> call = mApiInterface.createOrganization(
                JsonHelper.getCreateOrgJson(binding.txtPanCard.getText().toString(), 0),
                "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                new ButtonStyleHelper(PANVerifyActivity.this)
                        .initButton(true, binding.btnNext, "Next");
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());
                    if (org_type.toLowerCase().equalsIgnoreCase(getResources().getString(R.string.individual))) {
                        Intent intent = new Intent(PANVerifyActivity.this, ProfileUpdateActivity.class);
                        intent.putExtra("data", response.body());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(PANVerifyActivity.this, GSTActivity.class);
                        intent.putExtra("data", response.body());
                        startActivity(intent);
                    }
                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        Logger.errorLogger(this.getClass().getName(), "response - " + responseBody);
                        new SessionHelper(PANVerifyActivity.this).requestErrorMessage(
                                responseBody,
                                findViewById(android.R.id.content));
                    } catch (Exception ex) {
                        Logger.errorLogger(this.getClass().getName(), "response - " + ex.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                new ButtonStyleHelper(PANVerifyActivity.this)
                        .initButton(true, binding.btnNext, "Next");
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }
}