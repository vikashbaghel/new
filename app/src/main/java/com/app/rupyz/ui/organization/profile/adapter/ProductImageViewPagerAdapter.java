package com.app.rupyz.ui.organization.profile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.rupyz.R;
import com.app.rupyz.generic.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductImageViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<String> Images;
    private ProductImageClickListener listener;

    public ProductImageViewPagerAdapter(Context context, List<String> Images, ProductImageClickListener listener) {
        this.context = context;
        this.Images = Images;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return Images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.product_image_viewer_inside_item, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        ImageUtils.INSTANCE.loadImage(Images.get(position), imageView);

        if (listener != null) {
            imageView.setOnClickListener(v -> {
                listener.onImageClick(position);
            });
        }

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }

    public interface ProductImageClickListener{
        void onImageClick(int position);
        default void onPdfClick(int position){}
    }
}