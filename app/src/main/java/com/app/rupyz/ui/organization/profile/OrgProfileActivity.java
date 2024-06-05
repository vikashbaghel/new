package com.app.rupyz.ui.organization.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.app.rupyz.R;
import com.app.rupyz.databinding.ActivityOrgProfileBinding;
import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileDetail;
import com.app.rupyz.generic.utils.AppConstant;
import com.app.rupyz.generic.utils.ImageUtils;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.model_kt.NetWorkConnectModel;
import com.app.rupyz.sales.product.AddProductActivity;
import com.app.rupyz.ui.common.UrlImageViewActivity;
import com.app.rupyz.ui.imageupload.ImageUploadBottomSheetDialogFragment;
import com.app.rupyz.ui.imageupload.ImageUploadListener;
import com.app.rupyz.ui.imageupload.ImageUploadViewModel;
import com.app.rupyz.ui.network.views.NetworkViewModel;
import com.app.rupyz.ui.organization.profile.activity.OrgAddAchievementActivity;
import com.app.rupyz.ui.organization.profile.activity.OrgAddTeamActivity;
import com.app.rupyz.ui.organization.profile.activity.OrgEditIntroActivity;
import com.app.rupyz.ui.organization.profile.activity.OrgViewModel;
import com.app.rupyz.ui.organization.profile.adapter.OrgTabLayout;
import com.google.android.material.tabs.TabLayout;

import java.io.File;

public class OrgProfileActivity extends AppCompatActivity implements View.OnClickListener, ImageUploadListener {

    private ActivityOrgProfileBinding binding;
    private OrgTabLayout adapter;
    int value;
    private String bannerUrl, logoUrl, legalName = "";

    private ImageUploadViewModel imageUploadViewModel;
    private OrgViewModel orgViewModel;
    private OrgProfileDetail profileDetailModel = new OrgProfileDetail();

    boolean isSlugAvailable = false;
    private String slug = "";
    private Integer imageUpload_type = 0;

    private NetworkViewModel networkViewModel;
    private PopupMenu connectionPopupMenu;
    private String connectionAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrgProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        imageUploadViewModel = new ViewModelProvider(this).get(ImageUploadViewModel.class);
        networkViewModel = new ViewModelProvider(this).get(NetworkViewModel.class);
        orgViewModel = new ViewModelProvider(this).get(OrgViewModel.class);

        initLayout();
        initObservers();

        if (getIntent().hasExtra(AppConstant.PROFILE_SLUG)) {
            isSlugAvailable = true;
            slug = getIntent().getStringExtra(AppConstant.PROFILE_SLUG);
            orgViewModel.getInfoUsingSlug(slug);
            binding.fabAdd.setVisibility(View.GONE);
            binding.btnBannerImage.setVisibility(View.GONE);
        } else {
            orgViewModel.getInfo();
            initTabLayout();
        }
        binding.btnShareProfile.setOnClickListener(this);
        binding.imgBack.setOnClickListener(v -> finish());

        binding.btnConnection.setOnClickListener(view -> {
            if (profileDetailModel.getStatus().equals(AppConstant.ACCEPTED)) {
                Toast.makeText(this, "Coming soon!!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initLayout() {
//        binding.imgEditProfile.setOnClickListener(this);
        binding.fabAdd.setOnClickListener(this);
        binding.btnBannerImage.setOnClickListener(this);
        binding.ivProfile.setOnClickListener(this);
        binding.ivProfileBanner.setOnClickListener(this);
    }


    private void initTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("About"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Product"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Team"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Photos"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Achievements"));
        adapter = new OrgTabLayout(this, getSupportFragmentManager(),
                binding.tabLayout.getTabCount(), isSlugAvailable, profileDetailModel, slug);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 1) {
                    value = 1;
                } else if (tab.getPosition() == 2) {
                    value = 2;
                } else if (tab.getPosition() == 3) {
                    value = 3;
                } else if (tab.getPosition() == 4) {
                    value = 4;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_edit_profile:
                startActivity(new Intent(OrgProfileActivity.this, OrgEditIntroActivity.class));
                break;
            case R.id.fab_add:
                openActivity();
                break;
            case R.id.btn_share_profile:
                Utility.shareOthersProfileWithAll(this, legalName, slug);
                break;
            case R.id.iv_profile_banner:
                Intent intent = new Intent(OrgProfileActivity.this, UrlImageViewActivity.class);
                intent.putExtra("image_url", bannerUrl);
                startActivity(intent);
                break;
            case R.id.btnBannerImage:
            case R.id.iv_profile:
                if (!isSlugAvailable) {
                    imageUpload_type = view.getId();
                    ImageUploadBottomSheetDialogFragment.newInstance(this).show(getSupportFragmentManager(), "tag");
                } else {
                    Intent logoIntent = new Intent(OrgProfileActivity.this, UrlImageViewActivity.class);
                    logoIntent.putExtra("image_url", logoUrl);
                    startActivity(logoIntent);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }


    private void openActivity() {
        if (value == 1) {
            startActivity(new Intent(OrgProfileActivity.this, AddProductActivity.class));
        } else if (value == 2) {
            startActivity(new Intent(OrgProfileActivity.this, OrgAddTeamActivity.class));
        } else if (value == 4) {
            startActivity(new Intent(OrgProfileActivity.this, OrgAddAchievementActivity.class));
        }
    }

    @Override
    public void onCameraUpload(@Nullable String fileName) {
        imageUploadViewModel.uploadCredentials(fileName);
        binding.progressBar.setVisibility(View.VISIBLE);
        if (imageUpload_type == R.id.btnBannerImage) {
            binding.ivProfileBanner.setImageURI(Uri.fromFile(new File(fileName)));
        } else if (imageUpload_type == R.id.iv_profile) {
            binding.ivProfile.setImageURI(Uri.fromFile(new File(fileName)));
        }
    }

    @Override
    public void onGalleryUpload(@Nullable String fileName) {
        imageUploadViewModel.uploadCredentials(fileName);
        binding.progressBar.setVisibility(View.VISIBLE);

        if (imageUpload_type == R.id.btnBannerImage) {
            binding.ivProfileBanner.setImageURI(Uri.fromFile(new File(fileName)));
        } else if (imageUpload_type == R.id.iv_profile) {
            binding.ivProfile.setImageURI(Uri.fromFile(new File(fileName)));
        }

    }


    @SuppressLint("SetTextI18n")
    private void initObservers() {
        imageUploadViewModel.getCredLiveData().observe(this, genericResponseModel -> {
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(this, genericResponseModel.getMessage(), Toast.LENGTH_SHORT).show();
        });

        orgViewModel.getLiveData().observe(this, orgProfileInfoModel -> {
            binding.progressBar.setVisibility(View.GONE);

            profileDetailModel = orgProfileInfoModel.getData();
            if (isSlugAvailable) {
                initTabLayout();
                if (getIntent().hasExtra(AppConstant.PRODUCT_INFO)) {
                    binding.viewPager.setCurrentItem(1);
                }
            }
            legalName = orgProfileInfoModel.getData().getLegalName();
            slug = orgProfileInfoModel.getData().getSlug();
            binding.txvBusinessName.setText(orgProfileInfoModel.getData().getLegalName());
            binding.headerBusinessName.setText(orgProfileInfoModel.getData().getLegalName());

            if (orgProfileInfoModel.getData().getStatus() != null) {
                if (orgProfileInfoModel.getData().getStatus().isEmpty() || orgProfileInfoModel.getData().getStatus().equals(AppConstant.DECLINED)) {
                    binding.btnConnection.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0, 0,
                            0
                    );
                    binding.btnConnection.setText("connect");
                    binding.btnConnection.setBackgroundResource(R.drawable.connect_background_with_border);
                    binding.btnConnection.setTextColor(getColor(R.color.theme_purple));

                    binding.btnConnection.setOnClickListener(view -> {
                        connectionAction = AppConstant.SEND_CONNECTION_REQUEST;
                        onConnectConnection(AppConstant.SEND_CONNECTION_REQUEST, orgProfileInfoModel.getData().getId());
                    });
                } else if (orgProfileInfoModel.getData().getStatus().equals(AppConstant.PENDING)) {
                    binding.btnConnection.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0, 0,
                            0
                    );
                    binding.btnConnection.setText("Pending");
                    binding.btnConnection.setBackgroundResource(R.drawable.network_pending_button_style);
                    binding.btnConnection.setTextColor(getColor(R.color.white));
                } else if (orgProfileInfoModel.getData().getStatus().equals(AppConstant.ACCEPTED)) {
                    binding.btnConnection.setText("Chat");
                    binding.btnConnection.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_chatbubbles,
                            0, 0,
                            0
                    );
                    binding.btnConnection.setBackgroundResource(R.drawable.connect_background_with_border);
                    binding.btnConnection.setTextColor(getColor(R.color.theme_purple));
                }
            }
            try {
                String address = orgProfileInfoModel.getData().getCity() + ", "
                        + orgProfileInfoModel.getData().getState() + ", "
                        + orgProfileInfoModel.getData().getPincode();
                binding.txvAddress.setText(address);
                bannerUrl = orgProfileInfoModel.getData().getBanner_image_url();
                logoUrl = orgProfileInfoModel.getData().getLogo_image_url();
            } catch (Exception exception) {
                binding.txvAddress.setText(orgProfileInfoModel.getData().getAddressLine1());
            }
            if (orgProfileInfoModel.getData().getShortDescription() != null &&
                    !orgProfileInfoModel.getData().getShortDescription().isEmpty()) {
                binding.txvShortDescription.setText(orgProfileInfoModel.getData().getShortDescription());
            } else {
                binding.txvShortDescription.setVisibility(View.GONE);
            }

            ImageUtils.INSTANCE.loadTeamImage(orgProfileInfoModel.getData().getLogo_image_url(),
                    binding.ivProfile);
            ImageUtils.INSTANCE
                    .loadBannerImage(orgProfileInfoModel.getData().getBanner_image_url(),
                            binding.ivProfileBanner);
            initBadge(orgProfileInfoModel.getData().getComplianceRating());

        });

        networkViewModel.getFollowLiveData().observe(this, networkConnectResponseModel -> {

            if (connectionAction.equals(AppConstant.SEND_CONNECTION_REQUEST)) {

                profileDetailModel.setStatus(AppConstant.PENDING);
                binding.btnConnection.setText("Pending");
                binding.btnConnection.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0, 0,
                        0
                );

                binding.btnConnection.setEnabled(false);

                binding.btnConnection.setBackgroundResource(R.drawable.network_pending_button_style);
                binding.btnConnection.setTextColor(getColor(R.color.white));

            } else if (connectionAction.equals(AppConstant.REMOVE_CONNECTION_REQUEST)) {
                profileDetailModel.setStatus("");
                binding.btnConnection.setEnabled(true);
                binding.btnConnection.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0, 0,
                        0
                );
                binding.btnConnection.setText("connect");
                binding.btnConnection.setBackgroundResource(R.drawable.connect_background_with_border);
                binding.btnConnection.setTextColor(getColor(R.color.theme_purple));

                binding.btnConnection.setOnClickListener(view -> {
                    connectionAction = AppConstant.SEND_CONNECTION_REQUEST;
                    onConnectConnection(AppConstant.SEND_CONNECTION_REQUEST, profileDetailModel.getId());
                });
            }
        });

        binding.ivMenu.setOnClickListener(view -> {

            connectionPopupMenu = new PopupMenu(this, binding.ivMenu);
            connectionPopupMenu.inflate(R.menu.connection_action_menu);

            if (profileDetailModel.getStatus() != null && !profileDetailModel.getStatus().isEmpty()) {
                if (profileDetailModel.getStatus().equals(AppConstant.PENDING)) {
                    connectionPopupMenu.getMenu().getItem(1).setVisible(false);
                    connectionPopupMenu.getMenu().getItem(2).setVisible(true);
                } else if (profileDetailModel.getStatus().equals(AppConstant.ACCEPTED)) {
                    connectionPopupMenu.getMenu().getItem(1).setVisible(true);
                    connectionPopupMenu.getMenu().getItem(2).setVisible(false);
                } else if (profileDetailModel.getStatus().equals(AppConstant.DECLINED)) {
                    connectionPopupMenu.getMenu().getItem(1).setVisible(false);
                    connectionPopupMenu.getMenu().getItem(2).setVisible(false);
                }

            } else {
                connectionPopupMenu.getMenu().getItem(1).setVisible(false);
                connectionPopupMenu.getMenu().getItem(2).setVisible(false);
            }


            invalidateOptionsMenu();
            connectionPopupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.share_connection:
                        Utility.shareOthersProfileWithAll(OrgProfileActivity.this, legalName, slug);
                        break;
                    case R.id.remove_connection:
                    case R.id.cancel_request:
                        connectionAction = AppConstant.REMOVE_CONNECTION_REQUEST;
                        onConnectConnection(AppConstant.REMOVE_CONNECTION_REQUEST, profileDetailModel.getId());
                        break;
                }
                return true;
            });

            connectionPopupMenu.show();
        });
    }

    private void onConnectConnection(String connection_request, Integer id) {
        NetWorkConnectModel netWorkConnectModel = new NetWorkConnectModel();
        netWorkConnectModel.setTarget_id(id);
        netWorkConnectModel.setAction(connection_request);
        networkViewModel.onConnect(netWorkConnectModel);
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

}