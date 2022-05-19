package com.duan.travelshare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.duan.travelshare.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageSlide extends PagerAdapter {
    Context context;
    ArrayList<String> list;
    LayoutInflater layoutInflater;


    public ImageSlide(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.image_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.ivPhong);

        container.addView(itemView);
        Picasso.with(context).load(list.get(position)).into(imageView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
