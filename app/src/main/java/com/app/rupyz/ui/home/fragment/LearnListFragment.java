package com.app.rupyz.ui.home.fragment;

import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.rupyz.R;
import com.app.rupyz.adapter.blogs.BlogListAdapter;
import com.app.rupyz.databinding.LearnFragmentBinding;
import com.app.rupyz.generic.logger.FirebaseLogger;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.blog.BlogInfoModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.utils.RecyclerTouchListener;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.blogs.BlogsActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LearnListFragment extends Fragment {
    LearnFragmentBinding binding;
    private ApiInterface mApiInterface;
    private List<BlogInfoModel> mData;


    public LearnListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = LearnFragmentBinding.inflate(getLayoutInflater());
        new FirebaseLogger(getContext()).sendLog("Learn", "Learn");
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        initLayout();
        return binding.getRoot();
    }

    private void initLayout() {
        getProfileData();
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), binding.recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getContext(), BlogsActivity.class);
                intent.putExtra("slug", mData.get(position).getSlug());
                startActivity(intent);
            }
        }));
    }

    private void getProfileData() {
        Logger.errorLogger("Profile", SharedPref.getInstance().getString(TOKEN));
        Call<String> call1 = mApiInterface.getBlogs("Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        Logger.errorLogger(this.getClass().getName(), response.body());
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                        Gson gson = new Gson();
                        mData = Arrays.asList(gson.fromJson(jsonObj.get("data"), BlogInfoModel[].class));
                        BlogListAdapter adapter = new BlogListAdapter(mData, getContext());
                        binding.recyclerView.setAdapter(adapter);
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
}