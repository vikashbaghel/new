package com.app.rupyz.ui.overview.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.app.rupyz.R;

public class SliderItemFragment extends Fragment {
    private static final String ARG_POSITION = "slider-position";
    // prepare all title ids arrays
    @StringRes
    private static final int[] PAGE_TITLES =
            new int[]{R.string.overview_one_title, R.string.overview_two_title};
    // prepare all subtitle ids arrays
    @StringRes
    private static final int[] PAGE_HEADER_ONE =
            new int[]{
                    R.string.overview_one_heading_one,
                    R.string.overview_two_heading_one,
            };

    @StringRes
    private static final int[] PAGE_HEADER_TWO =
            new int[]{
                    R.string.overview_one_heading_two,
                    R.string.overview_two_heading_two,
            };

    @StringRes
    private static final int[] PAGE_HEADER_THREE =
            new int[]{
                    R.string.overview_one_heading_three,
                    R.string.overview_two_heading_three,
            };

    @StringRes
    private static final int[] PAGE_HEADER_FOUR =
            new int[]{
                    R.string.overview_one_heading_four,
                    R.string.overview_two_heading_four,
            };
    // prepare all subtitle images arrays
    @StringRes
    private static final int[] PAGE_IMAGE_ONE =
            new int[]{
                    R.mipmap.ic_wt_report_score,
                    R.mipmap.ic_wt_security
            };
    @StringRes
    private static final int[] PAGE_IMAGE_TWO =
            new int[]{
                    R.mipmap.ic_wt_alert,
                    R.mipmap.ic_wt_no_ads
            };
    @StringRes
    private static final int[] PAGE_IMAGE_THREE =
            new int[]{
                    R.mipmap.ic_wt_ai,
                    R.mipmap.ic_wt_no_offers
            };
    @StringRes
    private static final int[] PAGE_IMAGE_FOUR =
            new int[]{
                    R.mipmap.ic_wt_suggestion,
                    R.mipmap.ic_wt_app_permission
            };
    // prepare all background images arrays
//    @StringRes
//    private static final int[] BG_IMAGE = new int[]{
//            R.drawable.ic_launcher_background, R.mipmap.mobile_recharge, R.mipmap.datacard
//    };
    private int position;

    public SliderItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     *
     * @return A new instance of fragment SliderItemFragment.
     */
    public static SliderItemFragment newInstance(int position) {
        SliderItemFragment fragment = new SliderItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_slider_item, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView heading_one = view.findViewById(R.id.heading_one);
        TextView heading_two = view.findViewById(R.id.heading_two);
        TextView heading_three = view.findViewById(R.id.heading_three);
        TextView heading_four = view.findViewById(R.id.heading_four);

        TextView title = view.findViewById(R.id.title);
        ImageView image_one = view.findViewById(R.id.image_one);
        ImageView image_two = view.findViewById(R.id.image_two);
        ImageView image_three = view.findViewById(R.id.image_three);
        ImageView image_four = view.findViewById(R.id.image_four);


        title.setText(PAGE_TITLES[position]);
        heading_one.setText(PAGE_HEADER_ONE[position]);
        heading_two.setText(PAGE_HEADER_TWO[position]);
        heading_three.setText(PAGE_HEADER_THREE[position]);
        heading_four.setText(PAGE_HEADER_FOUR[position]);

        image_one.setImageResource(PAGE_IMAGE_ONE[position]);
        image_two.setImageResource(PAGE_IMAGE_TWO[position]);
        image_three.setImageResource(PAGE_IMAGE_THREE[position]);
        image_four.setImageResource(PAGE_IMAGE_FOUR[position]);


    }
}