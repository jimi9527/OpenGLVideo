package com.example.dengjx.openglvideo.camera;

import android.Manifest;
import android.app.Activity;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.dengjx.openglvideo.R;
import com.example.dengjx.openglvideo.util.PermissionUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by dengjx on 2017/11/14.
 */

public class Camera2Activity  extends Activity implements FrameCallback{

    private GLSurfaceView.Renderer mRenderer;
    private int cameraId = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.askPermission(this,new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE},10,initViewRunnable);
        setContentView(R.layout.activity_camera2);

    }

    @Override
    public void onFrame(byte[] bytes, long time) {

    }

    private Runnable initViewRunnable = new Runnable() {
        @Override
        public void run() {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            }else {

            }
        }
    };



    private class CameraRenderer implements Renderer{
        private Camera mCamera;

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            destroyCamera();
            mCamera = Camera.open(cameraId);


        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int i, int i1) {

        }

        @Override
        public void onDrawFrame(GL10 gl10) {

        }

        @Override
        public void onDestroy() {
            destroyCamera();
        }

        void destroyCamera(){
            if(mCamera != null){
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        }
    }
}
