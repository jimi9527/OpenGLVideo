package com.example.dengjx.openglvideo.egl;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.example.dengjx.openglvideo.R;
import com.example.dengjx.openglvideo.filter.GrayFilter;

/**
 * Created by dengjx on 2017/11/9.
 */

public class EGLBackEnvActivity extends Activity {
    private ImageView mImg;
    private int mBmpWidth,mBmpHeight;
    private GLES20BackEnv mBackEnv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egl_back);
        mImg = findViewById(R.id.image);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.zhang);
        mBmpWidth = bitmap.getWidth();
        mBmpHeight = bitmap.getHeight();

        mBackEnv = new GLES20BackEnv(mBmpWidth,mBmpHeight);
        mBackEnv.setThreadOwner(getMainLooper().getThread().getName());
        mBackEnv.setFilter(new GrayFilter(getResources()));
        mBackEnv.setInput(bitmap);

        mImg.setImageBitmap(mBackEnv.getBitmap());
    }



}
