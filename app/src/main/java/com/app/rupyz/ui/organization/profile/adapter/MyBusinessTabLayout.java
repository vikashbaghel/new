package com.app.rupyz.ui.organization.profile.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.app.rupyz.generic.model.profile.profileInfo.OrgProfileDetail;
import com.app.rupyz.ui.organization.profile.OrgAchievementFragment;
import com.app.rupyz.ui.organization.profile.OrgProfileAboutFragment;
import com.app.rupyz.ui.organization.profile.OrgProfilePhotosFragment;
import com.app.rupyz.ui.organization.profile.OrgProfileProductFragment;
import com.app.rupyz.ui.organization.profile.OrgTeamFragment;

public class MyBusinessTabLayout extends FragmentPagerAdapter {

    int mTotalTabs;
    boolean isDataChange;
    private OrgProfileDetail profileDetailModel;
    private String slug;

    public MyBusinessTabLayout(Context context, FragmentManager fragmentManager,
                               int totalTabs, boolean isDataChange, OrgProfileDetail profileDetailModel,
                               String slug) {
        super(fragmentManager);
        mTotalTabs = totalTabs;
        this.isDataChange = isDataChange;
        this.profileDetailModel = profileDetailModel;
        this.slug = slug;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new OrgProfileAboutFragment(false, isDataChange, profileDetailModel, slug);
            case 1:
                return new OrgTeamFragment(false,isDataChange, profileDetailModel, slug);
            case 2:
                return new OrgProfilePhotosFragment(false,isDataChange, profileDetailModel, slug);
            case 3:
                return new OrgAchievementFragment(false,isDataChange, profileDetailModel, slug);
            default:
                return null;

        }
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mTotalTabs;
    }
}