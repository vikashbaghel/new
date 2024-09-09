package com.app.rupyz.ui.home;

import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.R;
import com.app.rupyz.adapter.individual.AlertListAdapter;
import com.app.rupyz.adapter.itemdecorator.DividerItemDecorator;
import com.app.rupyz.databinding.AlertFragmentBinding;
import com.app.rupyz.databinding.FragmentLearnListBinding;
import com.app.rupyz.generic.logger.FirebaseLogger;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.individual.experian.ExperianInfoModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.SimpleDividerItemDecoration;
import com.app.rupyz.generic.utils.Utility;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlertFragment extends Fragment {

    AlertFragmentBinding binding;
    private ApiInterface mApiInterface;
    ExperianInfoModel mData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        binding = AlertFragmentBinding.inflate(getLayoutInflater());
        new FirebaseLogger(getContext()).sendLog("Alert", "Alert");
        initLayout();
        return binding.getRoot();
    }

//    private void initToolbar() {
//        Toolbar toolBar = this.findViewById(R.id.toolbar_my);
//        ImageView imageViewBack = toolBar.findViewById(R.id.img_back);
//        setSupportActionBar(toolBar);
//        getSupportActionBar().setTitle("");
//        imageViewBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });
//    }


    private void getProfileData() {
        Logger.errorLogger("Profile", SharedPref.getInstance().getString(TOKEN));
        Call<String> call1 = mApiInterface.getDashboardData("Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        Logger.errorLogger(this.getClass().getName(), response.body());
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                        Gson gson = new Gson();
                        mData = gson.fromJson(jsonObj.get("data"), ExperianInfoModel.class);
                        if (mData.getAlerts().size() > 0 && mData.getAlerts() != null) {
                            AlertListAdapter adapter = new AlertListAdapter(mData.getAlerts(), getActivity());
                            binding.recyclerView.setAdapter(adapter);
                            DividerItemDecorator itemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(getActivity(), R.drawable.item_divider));
                            binding.recyclerView.addItemDecoration(itemDecoration);
                            binding.message.setVisibility(View.GONE);
                            binding.recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            binding.message.setVisibility(View.VISIBLE);
                            binding.recyclerView.setVisibility(View.GONE);
                        }
                    }
                } else {
                    if (response.code() == 403) {
                    }
                }
                try {
                    Logger.errorLogger(this.getClass().getName(), response.errorBody().string() + "");
                } catch (Exception ex) {
                    Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
                }
                Logger.errorLogger(this.getClass().getName(), response.code() + "");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    private void initLayout() {
        getProfileData();
        binding.recyclerView.setHasFixedSize(true);
        // binding.recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
