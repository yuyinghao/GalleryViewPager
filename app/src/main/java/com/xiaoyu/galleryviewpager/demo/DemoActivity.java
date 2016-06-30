package com.xiaoyu.galleryviewpager.demo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xiaoyu.galleryviewpager.R;
import com.xiaoyu.galleryviewpager.library.GalleryViewPager;
import com.xiaoyu.galleryviewpager.library.adapter.AbstractAdapter;

import java.util.ArrayList;
import java.util.List;

public class DemoActivity extends Activity {

    private GalleryViewPager galleryDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        galleryDemo = (GalleryViewPager) findViewById(R.id.gallerdemo);

        MyAdapter adapter = new MyAdapter(this);

        galleryDemo.setAdapter(adapter);

        List<Integer> list = new ArrayList<>();
        list.add(R.mipmap.a1);
        list.add(R.mipmap.a2);
        list.add(R.mipmap.a3);
        list.add(R.mipmap.a4);
        
        adapter.setContent(list);
        galleryDemo.setCurrentItem(5000 * galleryDemo.getTruthCount());
    }

    class MyAdapter extends AbstractAdapter<Integer> {
        public MyAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        protected View createView(int position, Integer obj, ViewGroup parent) {

            ImageView imageView = new ImageView(parent.getContext());
            
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            imageView.setImageBitmap(decodeSampledBitmapFromResource(parent.getResources(), obj, 364, 364));

            return imageView;
        }
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res,
                                                         int resId, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			 final int halfHeight = height / 2;
			 final int halfWidth = width / 2;
			 while ((halfHeight / inSampleSize) > reqHeight  && (halfWidth / inSampleSize) > reqWidth) {
			    inSampleSize *= 2;
			 }

		}

        return inSampleSize;
    }

}
