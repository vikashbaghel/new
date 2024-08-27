package com.app.rupyz.ui.organization.profile;

import static com.app.rupyz.generic.utils.SharePrefConstant.LEGAL_NAME;
import static com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID;
import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.app.rupyz.R;
import com.app.rupyz.databinding.MyBusinessFragmentBinding;
import com.app.rupyz.generic.helper.EquiFaxReportHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileDetail;
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileInfoModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.EquiFaxApiInterface;
import com.app.rupyz.generic.utils.AppConstant;
import com.app.rupyz.generic.utils.ImageUtils;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.sales.product.AddProductActivity;
import com.app.rupyz.ui.common.UrlImageViewActivity;
import com.app.rupyz.ui.equifax.EquiFaxMainActivity;
import com.app.rupyz.ui.imageupload.ImageUploadBottomSheetDialogFragment;
import com.app.rupyz.ui.imageupload.ImageUploadListener;
import com.app.rupyz.ui.imageupload.ImageUploadViewModel;
import com.app.rupyz.ui.organization.profile.activity.OrgAddAchievementActivity;
import com.app.rupyz.ui.organization.profile.activity.OrgAddTeamActivity;
import com.app.rupyz.ui.organization.profile.activity.OrgEditIntroActivity;
import com.app.rupyz.ui.organization.profile.activity.OrgViewModel;
import com.app.rupyz.ui.organization.profile.activity.addphotos.OrgAddPhotoActivity;
import com.app.rupyz.ui.organization.profile.adapter.MyBusinessTabLayout;
import com.google.android.material.tabs.TabLayout;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBusinessFragment extends Fragment implements View.OnClickListener, ImageUploadListener {

    private MyBusinessFragmentBinding binding;
    private EquiFaxApiInterface mEquiFaxApiInterface;
    private EquiFaxReportHelper equiFaxReportHelper;
    private int value = 1;
    private String strBusinessName, strShortDescription, strAboutUs, no_of_employees,
            strFirstAddressLine, strCity, strState, strPinCode, strIncorporationDate, aggregated_turnover;
    private String businessNature;
    private OrgProfileInfoModel mData;
    boolean isDataChange;
    private String slug = "";
    private Integer imageUpload_type = 0;
    private ImageUploadViewModel imageUploadViewModel;
    private OrgViewModel orgViewModel;
    private OrgProfileDetail profileDetailModel = new OrgProfileDetail();
    private MyBusinessTabLayout adapter;
    private Utility mUtil;
    private String bannerUrl = "";
    private String profileUrl = "";
    private String fileNameLogo = "";
    private String fileNameBanner = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = MyBusinessFragmentBinding.inflate(getLayoutInflater());
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        equiFaxReportHelper = EquiFaxReportHelper.getInstance();
        mEquiFaxApiInterface = ApiClient.getRetrofit().create(EquiFaxApiInterface.class);
        mUtil = new Utility(getActivity());
        initLayout();
        initTabLayout();
        imageUploadViewModel = new ViewModelProvider(this).get(ImageUploadViewModel.class);
        orgViewModel = new ViewModelProvider(this).get(OrgViewModel.class);
        initObservers();
    }

    private void initLayout() {
        binding.imgEditProfile.setOnClickListener(this);
        binding.btnEditProfile.setOnClickListener(this);
        binding.btnWhatsappShare.setOnClickListener(this);
        binding.fabAdd.setOnClickListener(this);
        binding.btnBannerImage.setOnClickListener(this);
        binding.logoImageView.setOnClickListener(this);
        binding.btnShareProfile.setOnClickListener(this);
        binding.bannerImageView.setOnClickListener(this);
//        binding.imgBack.setOnClickListener(this);
    }


    private void initTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("About"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Product"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Team"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Photos"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Achievements"));
        adapter = new MyBusinessTabLayout(requireContext(), getChildFragmentManager(),
                binding.tabLayout.getTabCount(), isDataChange, profileDetailModel, "");
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));

        binding.fabAdd.setVisibility(View.GONE);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    binding.fabAdd.setVisibility(View.GONE);
                } else {
                    binding.fabAdd.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    value = 1;
                    binding.fabAdd.setVisibility(View.GONE);
                } else if (tab.getPosition() == 1) {
                    value = 1;
                    binding.fabAdd.setVisibility(View.VISIBLE);
                } else if (tab.getPosition() == 2) {
                    value = 2;
                    binding.fabAdd.setVisibility(View.VISIBLE);
                } else if (tab.getPosition() == 3) {
                    value = 3;
                    binding.fabAdd.setVisibility(View.VISIBLE);
                } else if (tab.getPosition() == 4) {
                    value = 4;
                    binding.fabAdd.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void profileInfo() {
        Logger.errorLogger(this.getClass().getName(), "profileInfo - " + SharedPref.getInstance().getString(TOKEN));
        Call<OrgProfileInfoModel> call = mEquiFaxApiInterface.getProfileInfo(
                SharedPref.getInstance().getInt(ORG_ID), "Bearer " + SharedPref.getInstance().getString(TOKEN));
        call.enqueue(new Callback<OrgProfileInfoModel>() {
            @Override
            public void onResponse(Call<OrgProfileInfoModel> call, Response<OrgProfileInfoModel> response) {
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), "response - " + response.body());
                    OrgProfileInfoModel response1 = response.body();
                    equiFaxReportHelper.setOrgProfile(response1);
                    if (response1.getData() != null) {
                        mData = response1;
                        strBusinessName = response1.getData().getLegalName();
                        strShortDescription = response1.getData().getShortDescription();
                        strFirstAddressLine = response1.getData().getAddressLine1();
                        strIncorporationDate = response1.getData().getIncorporationDate();
                        strPinCode = response1.getData().getPincode();
                        strCity = response1.getData().getCity();
                        strState = response1.getData().getState();
                        strAboutUs = response1.getData().getAboutUs();
                        binding.txvBusinessName.setText(response1.getData().getLegalName());
                        aggregated_turnover = response1.getData().getAggregatedTurnover();
                        no_of_employees = response1.getData().getNoOfEmployees() + "";
                        SharedPref.getInstance().putString(LEGAL_NAME, response1.getData().getLegalName());
                        initBadge(response1.getData().getComplianceRating());
                        try {
                            String address = response1.getData().getCity() + ", "
                                    + response1.getData().getState() + ", "
                                    + response1.getData().getPincode();
                            binding.txvAddress.setText(address);
                        } catch (Exception exception) {
                            binding.txvAddress.setText(response1.getData().getAddressLine1());
                        }
                        binding.txvShortDescription.setText(response1.getData().getShortDescription());

                        if (response1.getData().getBusinessNature() != null) {
                            businessNature = response1.getData().getBusinessNature();
                        }

                        bannerUrl = response1.getData().getBanner_image_url();
                        profileUrl = response1.getData().getLogo_image_url();

//                        if (fileNameLogo.equalsIgnoreCase("")) {
                            ImageUtils.INSTANCE.loadTeamImage(response1.getData().getLogo_image_url(),
                                    binding.logoImageView);
//                        } else {
//                            binding.logoImageView.setImageURI(Uri.fromFile(new File(fileNameLogo)));
//                        }
//                        if (fileNameBanner.equalsIgnoreCase("")) {
                            ImageUtils.INSTANCE.loadBannerImage(bannerUrl, binding.bannerImageView);
//                        } else {
//                            binding.bannerImageView.setImageURI(Uri.fromFile(new File(fileNameBanner)));
//                        }
                        try {
                            ((EquiFaxMainActivity) getContext()).initPrefix(response1.getData().getLegalName());
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    } else {

                    }
                }
            }

            @Override
            public void onFailure(Call<OrgProfileInfoModel> call, Throwable t) {
                Logger.errorLogger(this.getClass().getName(), t.getMessage());
                call.cancel();
            }
        });
    }

    private void initBadge(Double compliance_rating_dot) {
        try {
            int compliance_rating = compliance_rating_dot.intValue();
            binding.txtBadgeValue.setText(compliance_rating_dot + "/5");
            if (compliance_rating == 1) {
                binding.badge.setImageResource(R.mipmap.ic_badge_amateur);
                binding.txtBadgeTitle.setText("Amateur");
            } else if (compliance_rating == 2) {
                binding.badge.setImageResource(R.mipmap.ic_badge_basic);
                binding.txtBadgeTitle.setText("Basic");
            } else if (compliance_rating == 3) {
                binding.badge.setImageResource(R.mipmap.ic_badge_upcoming);
                binding.txtBadgeTitle.setText("Upcoming");
            } else if (compliance_rating == 4) {
                binding.badge.setImageResource(R.mipmap.ic_badge_respacted);
                binding.txtBadgeTitle.setText("Respected");
            } else if (compliance_rating == 5) {
                binding.badge.setImageResource(R.mipmap.ic_badge_iconic);
                binding.txtBadgeTitle.setText("Iconic");
            } else {
                binding.badge.setImageResource(R.mipmap.ic_badge_amateur);
                binding.txtBadgeTitle.setText("Amateur");
            }
            binding.badgeLayout.setVisibility(View.VISIBLE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_whatsapp_share:
                Utility.shareWhatsApp(getContext(), mData.getData().getLegalName(), mData.getData().getSlug());
                break;
            case R.id.btn_edit_profile:
            case R.id.img_edit_profile:
                Intent intent = new Intent(getActivity(), OrgEditIntroActivity.class);
                intent.putExtra("org_business_name", strBusinessName);
                intent.putExtra("org_incorporation_date", strIncorporationDate);
                intent.putExtra("org_city", strCity);
                intent.putExtra("org_pincode", strPinCode);
                intent.putExtra("org_short_description", strShortDescription);
                intent.putExtra("org_registered_address", strFirstAddressLine);
                intent.putExtra("business_nature", businessNature);
                intent.putExtra("about_us", strAboutUs);
                intent.putExtra("aggregated_turnover", aggregated_turnover);
                intent.putExtra("no_of_employees", no_of_employees);
                intent.putExtra("state", strState);
                startActivity(intent);
                break;
            case R.id.fab_add:
                openActivity();
                break;
            case R.id.btn_share_profile:
                Utility.shareMyProfileWithAll(requireContext(), mData.getData().getLegalName(), mData.getData().getSlug());
                break;

            case R.id.btnBannerImage:
            case R.id.bannerImageView:
                imageUpload_type = view.getId();
                ImageUploadBottomSheetDialogFragment fragment = ImageUploadBottomSheetDialogFragment.newInstance(this);
                if (bannerUrl != null && !bannerUrl.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(AppConstant.BOTTOM_SHEET_PREVIEW_TYPE, true);
                    bundle.putString(AppConstant.IMAGE_TYPE, AppConstant.IMAGE_TYPE_BANNER);
                    bundle.putString(AppConstant.IMAGE_URL, bannerUrl);
                    fragment.setArguments(bundle);
                }
                fragment.show(getChildFragmentManager(), "tag");
                break;

            case R.id.logoImageView:
                imageUpload_type = view.getId();
                ImageUploadBottomSheetDialogFragment fragment1 = ImageUploadBottomSheetDialogFragment.newInstance(this);
                if (profileUrl != null && !profileUrl.isEmpty()) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putBoolean(AppConstant.BOTTOM_SHEET_PREVIEW_TYPE, true);
                    bundle1.putString(AppConstant.IMAGE_TYPE, AppConstant.IMAGE_TYPE_PROFILE);
                    bundle1.putString(AppConstant.IMAGE_URL, profileUrl);
                    fragment1.setArguments(bundle1);
                }
                fragment1.show(getChildFragmentManager(), "tag");
                break;
        }
    }


    private void openActivity() {
        if (value == 1) {
            activityResultLauncher.launch(new Intent(getActivity(), AddProductActivity.class));
        } else if (value == 2) {
            activityResultLauncher.launch(new Intent(getActivity(), OrgAddTeamActivity.class));
        } else if (value == 4) {
            activityResultLauncher.launch(new Intent(getActivity(), OrgAddAchievementActivity.class));
        } else if (value == 3) {
            activityResultLauncher.launch(new Intent(getActivity(), OrgAddPhotoActivity.class));
        }
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        isDataChange = true;
                        adapter.notifyDataSetChanged();
                    }
                }
            });

    @Override
    public void onCameraUpload(@Nullable String fileName) {
        imageUploadViewModel.uploadCredentials(fileName);
        binding.progressBar.setVisibility(View.VISIBLE);
        if (imageUpload_type == R.id.btnBannerImage || imageUpload_type == R.id.bannerImageView ) {
            fileNameBanner = fileName;
            binding.bannerImageView.setImageURI(Uri.fromFile(new File(fileName)));
        } else if (imageUpload_type == R.id.logoImageView) {
            fileNameLogo = fileName;
            binding.logoImageView.setImageURI(Uri.fromFile(new File(fileName)));
        }
    }

    @Override
    public void onGalleryUpload(@Nullable String fileName) {
        imageUploadViewModel.uploadCredentials(fileName);
        binding.progressBar.setVisibility(View.VISIBLE);

        if (imageUpload_type == R.id.btnBannerImage || imageUpload_type == R.id.bannerImageView ) {
            fileNameBanner = fileName;
            binding.bannerImageView.setImageURI(Uri.fromFile(new File(fileName)));
        } else if (imageUpload_type == R.id.logoImageView) {
            fileNameLogo = fileName;
            binding.logoImageView.setImageURI(Uri.fromFile(new File(fileName)));
        }
    }

    private void initObservers() {
        imageUploadViewModel.getCredLiveData().observe(getViewLifecycleOwner(), genericResponseModel -> {
            binding.progressBar.setVisibility(View.GONE);
            OrgProfileDetail model = new OrgProfileDetail();

            if (genericResponseModel.getData() != null && genericResponseModel.getData().getId() != null) {
                if (imageUpload_type == R.id.btnBannerImage || imageUpload_type == R.id.bannerImageView ) {
                    model.setBannerImage(Integer.parseInt(genericResponseModel.getData().getId()));
                } else if (imageUpload_type == R.id.logoImageView) {
                    model.setLogoImage(Integer.parseInt(genericResponseModel.getData().getId()));
                }
                orgViewModel.updateInfo(model);
            }
        });

        orgViewModel.getLiveData().observe(getViewLifecycleOwner(), orgProfileInfoModel -> {

            bannerUrl = orgProfileInfoModel.getData().getBanner_image_url();
            profileUrl = orgProfileInfoModel.getData().getLogo_image_url();

            if (fileNameLogo.equalsIgnoreCase("") && fileNameBanner.equalsIgnoreCase("")) {
                ImageUtils.INSTANCE.loadImage(orgProfileInfoModel.getData().getLogo_image_url(), binding.logoImageView);
                ImageUtils.INSTANCE.loadImage(bannerUrl, binding.bannerImageView);
            } else {
                if (!fileNameLogo.equalsIgnoreCase("")) {
                    binding.logoImageView.setImageURI(Uri.fromFile(new File(fileNameLogo)));
                }
                if (!fileNameBanner.equalsIgnoreCase("")) {
                    binding.bannerImageView.setImageURI(Uri.fromFile(new File(fileNameBanner)));
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        profileInfo();
    }

    @Override
    public void onPreview(String image_type, String image_url) {
        String previewUrl = "";
        if (imageUpload_type == R.id.btnBannerImage || imageUpload_type == R.id.bannerImageView ) {
            previewUrl = bannerUrl;
        } else if (imageUpload_type == R.id.logoImageView) {
           previewUrl = profileUrl;
        }

        ImageUploadListener.super.onPreview(image_type, image_url);
        Intent viewBanner = new Intent(getActivity(), UrlImageViewActivity.class);
        viewBanner.putExtra(AppConstant.IMAGE_URL, previewUrl);
        viewBanner.putExtra(AppConstant.IMAGE_PREVIEW, true);
        viewBanner.putExtra(AppConstant.IMAGE_TYPE, image_type);
        startActivity(viewBanner);
    }
}

