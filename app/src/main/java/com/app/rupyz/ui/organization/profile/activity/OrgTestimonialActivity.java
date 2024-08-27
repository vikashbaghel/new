package com.app.rupyz.ui.organization.profile.activity;

import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityOrgAddAchievementBinding;
import com.app.rupyz.databinding.ActivityOrgTestimonialBinding;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.profile.testimonial.TestimonialData;
import com.app.rupyz.generic.model.profile.testimonial.TestimonialInfoModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.utils.AppConstant;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.organization.profile.TestimonialEditListener;
import com.app.rupyz.ui.organization.profile.adapter.TestimonialListAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrgTestimonialActivity extends AppCompatActivity implements View.OnClickListener, TestimonialEditListener {
    ActivityOrgTestimonialBinding binding;
    private EquiFaxApiInterface mEquiFaxApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrgTestimonialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mEquiFaxApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface.class);
        initLayout();
    }

    private void initLayout() {
        binding.fabAdd.setOnClickListener(this);
        binding.imgBack.setOnClickListener(this);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(OrgTestimonialActivity.this));
        getTestimonialsInfo();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                onBackPressed();
                break;

            case R.id.fab_add:
                Intent intent = new Intent(OrgTestimonialActivity.this, OrgAddTestimonialActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void getTestimonialsInfo() {
        Call<TestimonialInfoModel> call = mEquiFaxApiInterface.getTestimonials(
                SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call.enqueue(new Callback<TestimonialInfoModel>() {
            @Override
            public void onResponse(Call<TestimonialInfoModel> call, Response<TestimonialInfoModel> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());
                    TestimonialInfoModel response1 = response.body();
                    if (response1.getData() != null && response1.getData().size() > 0) {
                        TestimonialListAdapter adapter = new TestimonialListAdapter(response1.getData(), OrgTestimonialActivity.this, OrgTestimonialActivity.this, false);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        //binding.message.setVisibility(View.GONE);
                        binding.recyclerView.setAdapter(adapter);
                    } else {
                        binding.recyclerView.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(OrgTestimonialActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TestimonialInfoModel> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTestimonialsInfo();
    }

    @Override
    public void onEditTestimonials(@NonNull TestimonialData testimonial) {
        Intent intent = new Intent(this, OrgAddTestimonialActivity.class);
        intent.putExtra(AppConstant.EDIT_TESTIMONIAL, "true");
        intent.putExtra("user_name", testimonial.getUserName());
        intent.putExtra("designation", testimonial.getPosition());
        intent.putExtra("content", testimonial.getContent());
        intent.putExtra("rating", testimonial.getRating() + "");
        intent.putExtra("organization", testimonial.getCompany());
        intent.putExtra("user_id", testimonial.getId() + "");
        intent.putExtra("image_url", testimonial.getUser_pic_url() + "");
        intent.putExtra("image_id", testimonial.getUserPic() + "");
        startActivity(intent);
    }
}