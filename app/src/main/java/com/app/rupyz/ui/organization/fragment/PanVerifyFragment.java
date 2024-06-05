package com.app.rupyz.ui.organization.fragment;

import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.rupyz.R;
import com.app.rupyz.databinding.PanVerifyFragmentBinding;
import com.app.rupyz.generic.json.JsonHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.organization.GstViewModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.organization.EntitySignUpActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PanVerifyFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private ApiInterface mApiInterface;
    private Utility mUtil;
    private GstViewModel mData;

    private PanVerifyFragmentBinding binding;
    private String org_type;

    public PanVerifyFragment(Context mContext, String org_type) {
        this.mContext = mContext;
        this.org_type = org_type;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PanVerifyFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        mUtil = new Utility(mContext);
        initLayout();
        return view;
    }

    private void initLayout() {
        binding.btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                doRequest();
                break;
        }
    }

    private void doRequest() {
        Call<String> call1 = mApiInterface.createOrganization(
                JsonHelper.getCreateOrgJson(binding.txtPanCard.getText().toString(), 0), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body());
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    EntitySignUpActivity activity = (EntitySignUpActivity) getActivity();
                    activity.receivedData(response.body());
                    Gson gson = new Gson();
                    mData = gson.fromJson(jsonObj.get("data"), GstViewModel.class);
                    ((EntitySignUpActivity) mContext).updateViewPager(1);
                }
                Logger.errorLogger(this.getClass().getName(), response.code() + "");

//                mUserData = gson.fromJson(jsonObj.get("data"), UserInfoModel.class);
//                Intent intent = new Intent(LoginWithMobileActivity.this, OtpVerificationActivity.class);
//                intent.putExtra("otp_ref", mUserData.getOtp_ref());
//                intent.putExtra("username", binding.txtMobileNumber.getText().toString());
//                intent.putExtra("org_type", getIntent().getExtras().getString("org_type"));
//                startActivity(intent);
//                Log.e("APIResponse", "onResponse: " + mUserData.getOtp_ref());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }
}
