package com.example.dengjx.openglvideo.fbo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dengjx.openglvideo.R;

import java.nio.ByteBuffer;


/**
 * Created by dengjx on 2017/11/13.
 */

public class FBOActivity extends Activity implements FBORender.Callback {
   private final static String TAG = "FBOActivity";
    private ImageView mImg;
    private GLSurfaceView mGLView;
    private FBORender mRender;
    private int mBmpWidth,mBmpHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbo);

        mGLView = findViewById(R.id.glview);
        mGLView.setEGLContextClientVersion(2);
        mRender = new FBORender(getResources());
        mRender.setmCallback(this);
        mGLView.setRenderer(mRender);
        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mImg = findViewById(R.id.mImage);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.zhang);
        mBmpWidth  = bitmap.getWidth();
        mBmpHeight = bitmap.getHeight();
        mRender.setmBitmap(bitmap);
    }

    @Override
    public void onCall(final ByteBuffer data) {
           new Thread(new Runnable() {
               @Override
               public void run() {
                   Log.d(TAG,"CALLBACK_SUCCESS");
                   final Bitmap bitmap = Bitmap.createBitmap(mBmpWidth,mBmpHeight,Bitmap.Config.ARGB_8888);
                   bitmap.copyPixelsFromBuffer(data);

                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           mImg.setImageBitmap(bitmap);
                       }
                   });

                   data.clear();
               }
           }).start();
    }
}
