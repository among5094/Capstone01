package com.example.withpet_login;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class ImageGridAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mImageUrls;

    public ImageGridAdapter(Context context, ArrayList<String> imageUrls) {
        mContext = context;
        mImageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        return mImageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return mImageUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(320, 320)); // 그리드뷰에 나올 크기
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        // Glide를 사용하여 이미지 로드
        Glide.with(mContext)
                .load(mImageUrls.get(position))
                .placeholder(R.drawable.pet_logo) // 로드 중일 때 표시할 이미지
                .error(R.drawable.ic_launcher_background) // 오류 발생 시 표시할 이미지
                .into(imageView);

        return imageView;
    }
}