package com.example.dengjx.openglvideo;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * 显示图片的view
 * Created by dengjx on 2017/11/2.
 */

public class ImageOpenGlActivity extends Activity {
    private GLSurfaceView mGLSurfaceView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_opengl);
        mGLSurfaceView = findViewById(R.id.surfaceview_image);

        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(new ImageRenderer(this));
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}
