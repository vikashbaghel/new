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

public class OrgTabLayout extends FragmentPagerAdapter {

    private Context mContext;
    int mTotalTabs;
    boolean isSlugAvailable;
    private OrgProfileDetail profileDetailModel;
    private String slug;


    public OrgTabLayout(Context context, FragmentManager fragmentManager,
                        int totalTabs, boolean isSlugAvailable, OrgProfileDetail profileDetailModel,
                        String slug) {
        super(fragmentManager);
        mContext = context;
        mTotalTabs = totalTabs;
        this.isSlugAvailable = isSlugAvailable;
        this.profileDetailModel = profileDetailModel;
        this.slug = slug;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new OrgProfileAboutFragment(isSlugAvailable, false, profileDetailModel, slug);
            case 1:
                return new OrgProfileProductFragment(isSlugAvailable, false, profileDetailModel, slug);
            case 2:
                return new OrgTeamFragment(isSlugAvailable, false, profileDetailModel, slug);
            case 3:
                return new OrgProfilePhotosFragment(isSlugAvailable, false, profileDetailModel, slug);
            case 4:
                return new OrgAchievementFragment(isSlugAvailable, false, profileDetailModel, slug);
            default:
                return null;

        }
    }

    @Override
    public int getItemPosition(Object object) {
        // Causes adapter to reload all Fragments when
        // notifyDataSetChanged is called
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mTotalTabs;
    }
}