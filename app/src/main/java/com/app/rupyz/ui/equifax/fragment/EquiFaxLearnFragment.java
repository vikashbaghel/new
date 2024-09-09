package com.app.rupyz.ui.equifax.fragment;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.app.rupyz.R;
import com.app.rupyz.databinding.FragmentLearnListBinding;
import com.app.rupyz.generic.logger.FirebaseLogger;
import com.app.rupyz.generic.model.blog.BlogInfoModel;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.utils.AppConstant;
import com.app.rupyz.ui.home.fragment.LearnListFragment;

import java.util.ArrayList;
import java.util.List;

public class EquiFaxLearnFragment extends AppCompatActivity {
    FragmentLearnListBinding binding;
    private ApiInterface mApiInterface;
    private List<BlogInfoModel> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new FirebaseLogger(this).sendLog("Home Activity", "Home Activity");
        new FirebaseLogger(this).sendLog("Learn", "Learn");
        binding = FragmentLearnListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new LearnListFragment());

        binding.imgClose.setOnClickListener(v -> finish());
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment, AppConstant.TOTAL_AMOUNT_RECEIVE);
        ft.commit();
    }


//    private void initLayout() {
//        setupViewPager(binding.viewPager);
//        binding.tabLayout.setupWithViewPager(binding.viewPager);
//        binding.imgClose.setOnClickListener(v -> finish());
//    }
//
//    private void setupViewPager(ViewPager viewPager) {
//        Adapter adapter = new Adapter(getSupportFragmentManager());
//        adapter.addFragment(new YoutubePlaylistFragment(), "Video");
//        adapter.addFragment(new LearnListFragment(), "Articles");
//        viewPager.setAdapter(adapter);
//    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
