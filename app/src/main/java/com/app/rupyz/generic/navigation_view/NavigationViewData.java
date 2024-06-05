package com.app.rupyz.generic.navigation_view;

import com.app.rupyz.R;

import java.util.ArrayList;
import java.util.List;

public class NavigationViewData {
    private List<NavigationViewModel> mData;

    public List<NavigationViewModel> getNavigationData() {
        mData = new ArrayList<>();
        NavigationViewModel profile = new NavigationViewModel();
        profile.setName("Profile");
        profile.setImageId(R.drawable.ic_user_profile);
        mData.add(profile);

        NavigationViewModel pref = new NavigationViewModel();
        pref.setName("User Preference");
        pref.setImageId(R.mipmap.nav_ic_settings);
        mData.add(pref);

        NavigationViewModel share = new NavigationViewModel();
        share.setName("Share App");
        share.setImageId(R.mipmap.nav_ic_share);
        mData.add(share);

        // set the rate in the navigation view title & icon
        NavigationViewModel rate = new NavigationViewModel();
        rate.setName("Rate App");
        rate.setImageId(R.mipmap.nav_ic_rate);
        mData.add(rate);

        // set the Terms & Conditions in the navigation view title & icon
        NavigationViewModel terms = new NavigationViewModel();
        terms.setName("Terms & Conditions");
        terms.setImageId(R.mipmap.nav_ic_terms);
        mData.add(terms);

        // set the Privacy Policy in the navigation view title & icon
        NavigationViewModel privacy = new NavigationViewModel();
        privacy.setName("Privacy Policy");
        privacy.setImageId(R.mipmap.nav_ic_privacy_policy);
        mData.add(privacy);


        // set the Logout in the navigation view title & icon
        NavigationViewModel logout = new NavigationViewModel();
        logout.setName("Logout");
        logout.setImageId(R.mipmap.nav_ic_logout);
        mData.add(logout);


        return mData;
    }
}
