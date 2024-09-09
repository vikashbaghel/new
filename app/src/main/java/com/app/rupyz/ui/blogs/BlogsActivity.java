package com.app.rupyz.ui.blogs;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityBlogsBinding;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.blog.BlogInfoModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogsActivity extends AppCompatActivity {

    ActivityBlogsBinding binding;
    private ApiInterface mApiInterface;
    private BlogInfoModel mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        binding = ActivityBlogsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initLayout();
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolBar = this.findViewById(R.id.toolbar_my);
        ImageView imageViewBack = toolBar.findViewById(R.id.img_back);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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

    private void initLayout() {
        getProfileData();
    }

    private void getProfileData() {
        Call<String> call1 = mApiInterface.getBlogsById(getIntent().getExtras().getString("slug"));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        Logger.errorLogger(this.getClass().getName(), response.body());
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                        Gson gson = new Gson();
                        mData = gson.fromJson(jsonObj.get("data"), BlogInfoModel.class);
                        binding.txtCategory.setText(mData.getCategory().toUpperCase());
                        binding.txtTitle.setText(mData.getTitle());
                        binding.txtContent.setText(mData.getContent());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            binding.txtContent.setText(Html.fromHtml(mData.getContent(), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            binding.txtContent.setText(Html.fromHtml(mData.getContent()));
                        }
                        Picasso.get().load(mData.getFeature_image_url()).into(binding.imageView);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

}