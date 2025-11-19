package com.dev.birdie.helpers;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

public class PhotoDisplayHelper {
    private final Context context;

    public PhotoDisplayHelper(Context context) {
        this.context = context;
    }

    public ImageView displayPhotoInContainer(LinearLayout container, String imageData) {
        container.removeAllViews();

        ImageView imageView = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        container.addView(imageView);

        Glide.with(context)
                .load(imageData)
                .centerCrop()
                .into(imageView);

        return imageView;
    }
}