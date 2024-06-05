package com.app.rupyz.ui.organization.profile;

import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.app.Activity;
import android.content.Intent;
import android.graphics.text.LineBreaker;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.rupyz.R;
import com.app.rupyz.databinding.FragmentOrgProfileAboutBinding;
import com.app.rupyz.generic.helper.DateFormatHelper;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileDetail;
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileInfoModel;
import com.app.rupyz.generic.model.profile.testimonial.TestimonialData;
import com.app.rupyz.generic.model.profile.testimonial.TestimonialInfoModel;
import com.app.rupyz.generic.model.profile.testimonial.createTestimonial.CreateTestimonialResponse;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.utils.AppConstant;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.StringUtils;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.organization.profile.activity.OrgAddSocialLink;
import com.app.rupyz.ui.organization.profile.activity.OrgAddTestimonialActivity;
import com.app.rupyz.ui.organization.profile.activity.OrgTestimonialActivity;
import com.app.rupyz.ui.organization.profile.adapter.TestimonialListAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrgProfileAboutFragment extends Fragment implements View.OnClickListener, TestimonialEditListener {

    private FragmentOrgProfileAboutBinding binding;
    private EquiFaxReportHelper equiFaxReportHelper;
    private EquiFaxApiInterface mEquiFaxApiInterface;
    private boolean isSlugAvailable;
    private boolean isDataChange;
    private OrgProfileDetail orgProfileDetail;
    private String slug;
    private boolean isCheck = false;

    public OrgProfileAboutFragment(boolean isSlugAvailable, boolean isDataChange, OrgProfileDetail profileDetailModel,
                                   String slug) {
        this.isSlugAvailable = isSlugAvailable;
        this.isDataChange = isDataChange;
        this.orgProfileDetail = profileDetailModel;
        this.slug = slug;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrgProfileAboutBinding.inflate(getLayoutInflater());
        equiFaxReportHelper = EquiFaxReportHelper.getInstance();
        mEquiFaxApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface.class);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (isSlugAvailable) {
            binding.btnSocialLinkUpdate.setVisibility(View.GONE);
            binding.txvViewAllTestimonial.setVisibility(View.GONE);
            initLayout();
            getTestimonialsInfo();
        } else {
            profileInfo();
            getTestimonialsInfo();
        }
        binding.txvViewAllTestimonial.setOnClickListener(this);
        binding.btnSocialLinkUpdate.setOnClickListener(this);
        return binding.getRoot();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txv_view_all_testimonial:
                startActivity(new Intent(getActivity(), OrgTestimonialActivity.class));
                break;
            case R.id.imgFacebook:
                initOpenBrowser(orgProfileDetail.getSocialMedia().getFacebook(), "");
                break;
            case R.id.imgTwitter:
                initOpenBrowser(orgProfileDetail.getSocialMedia().getTwitter(), "");
                break;
            case R.id.imgLinkedIn:
                initOpenBrowser(orgProfileDetail.getSocialMedia().getLinkedin(), "");
                break;
            case R.id.btn_social_link_update:
                Intent intent = new Intent(getActivity(), OrgAddSocialLink.class);
                intent.putExtra("facebook", orgProfileDetail.getSocialMedia().getFacebook());
                intent.putExtra("instagram", orgProfileDetail.getSocialMedia().getInstagram());
                intent.putExtra("linkedin", orgProfileDetail.getSocialMedia().getLinkedin());
                intent.putExtra("twitter", orgProfileDetail.getSocialMedia().getTwitter());
                startActivity(intent);
                break;
        }
    }

    private void initOpenBrowser(String url, String title) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void profileInfo() {
        Call<OrgProfileInfoModel> call = mEquiFaxApiInterface.getProfileInfo(
                SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call.enqueue(new Callback<OrgProfileInfoModel>() {
            @Override
            public void onResponse(Call<OrgProfileInfoModel> call, Response<OrgProfileInfoModel> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());
                    OrgProfileInfoModel response1 = response.body();
                    equiFaxReportHelper.setOrgProfile(response1);
                    if (response1 != null && response1.getData() != null) {
                        orgProfileDetail = response1.getData();
                        initLayout();
                    } else {
                        Log.e("DEBUG", "DATA IS EMPTY");
                    }
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrgProfileInfoModel> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    private void initLayout() {
        if (!StringUtils.isBlank(orgProfileDetail.getSocialMedia().getFacebook())) {
            binding.imgFacebook.setVisibility(View.VISIBLE);
            binding.imgFacebook.setOnClickListener(this);
        }
        if (!StringUtils.isBlank(orgProfileDetail.getSocialMedia().getTwitter())) {
            binding.imgTwitter.setVisibility(View.VISIBLE);
            binding.imgTwitter.setOnClickListener(this);
        }
        if (!StringUtils.isBlank(orgProfileDetail.getSocialMedia().getLinkedin())) {
            binding.imgLinkedIn.setVisibility(View.VISIBLE);
            binding.imgLinkedIn.setOnClickListener(this);
        }
        if (!StringUtils.isBlank(orgProfileDetail.getAboutUs())) {
            binding.txvAboutUs.setText(orgProfileDetail.getAboutUs());
            binding.txvAboutUs.post(new Runnable() {
                @Override
                public void run() {
                    int lineCount = binding.txvAboutUs.getLineCount();
                    if (lineCount > 10) {
                        binding.btnReadMore.setVisibility(View.VISIBLE);
                        binding.btnReadMore.setText(getResources().getString(R.string.read_more));
                        binding.txvAboutUs.setLines(10);
                    } else {
                        binding.btnReadMore.setVisibility(View.GONE);
                        binding.txvAboutUs.setLines(binding.txvAboutUs.getLineCount());
                    }

                    // Use lineCount here
                }
            });
        }
        if (!StringUtils.isBlank(orgProfileDetail.getAggregatedTurnover())) {
            binding.txvTotalTurnover.setText(orgProfileDetail.getAggregatedTurnover());
        }

        if (!StringUtils.isBlank(orgProfileDetail.getIncorporationDate())) {
            binding.txvIncorporationDate.setText(DateFormatHelper.getProfileDate(
                    orgProfileDetail.getIncorporationDate()));
        }
        if (orgProfileDetail.getNoOfEmployees() != null) {
            binding.txvTotalEmployees.setText(orgProfileDetail.getNoOfEmployees() + "");
        }
        if (orgProfileDetail.getBusinessNature() != null) {
            binding.txvBusinessType.setText(orgProfileDetail.getBusinessNature());
        }

        binding.btnReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCheck) {
                    isCheck = true;
                    binding.txvAboutUs.setLines(binding.txvAboutUs.getLineCount());
                    binding.btnReadMore.setText(getResources().getString(R.string.show_less));
                } else {
                    isCheck = false;
                    binding.txvAboutUs.setLines(10);
                    binding.btnReadMore.setText(getResources().getString(R.string.read_more));
                }
            }
        });

    }

    private void getTestimonialsInfo(String slug) {
        Call<TestimonialInfoModel> call;
        if (slug.equals("")) {
            call = mEquiFaxApiInterface.getTestimonials(
                    SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        } else {
            call = mEquiFaxApiInterface.getSlugTestimonials(slug);
        }
    }

    private void getTestimonialsInfo() {
        Call<TestimonialInfoModel> call;
        if (slug.equals("")) {
            call = mEquiFaxApiInterface.getTestimonials(
                    SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        } else {
            call = mEquiFaxApiInterface.getSlugTestimonials(slug);
        }
        call.enqueue(new Callback<TestimonialInfoModel>() {
            @Override
            public void onResponse(Call<TestimonialInfoModel> call, Response<TestimonialInfoModel> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "testimonialInfoModel - " + response.body());
                    TestimonialInfoModel response1 = response.body();
                    if (response1 != null && response1.getData() != null && response1.getData().size() > 0) {
                        TestimonialListAdapter adapter = new TestimonialListAdapter(
                                response1.getData(), getContext(), OrgProfileAboutFragment.this, isSlugAvailable);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        binding.recyclerView.setAdapter(adapter);
                    } else {
                        binding.recyclerView.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
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
    public void onResume() {
        super.onResume();
        if (isSlugAvailable) {
            Log.e("DEBUG", "SlugAvailable");
        } else {
            profileInfo();
            getTestimonialsInfo();
        }
    }

    @Override
    public void onEditTestimonials(@NonNull TestimonialData testimonial) {
        Intent intent = new Intent(requireContext(), OrgAddTestimonialActivity.class);
        intent.putExtra(AppConstant.EDIT_TESTIMONIAL, "true");
        intent.putExtra("user_name", testimonial.getUserName());
        intent.putExtra("designation", testimonial.getPosition());
        intent.putExtra("content", testimonial.getContent());
        intent.putExtra("rating", testimonial.getRating() + "");
        intent.putExtra("organization", testimonial.getCompany());
        intent.putExtra("user_id", testimonial.getId() + "");
        intent.putExtra("image_url", testimonial.getUser_pic_url() + "");
        intent.putExtra("image_id", testimonial.getUserPic() + "");
        activityResultLauncher.launch(intent);
    }


    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null && result.getData().hasExtra(AppConstant.TESTIMONIAL_INFO)) {
                        CreateTestimonialResponse testimonialResponse = result.getData().getParcelableExtra(AppConstant.TESTIMONIAL_INFO);
                    }
                }
            });

}
