package com.example.dengjx.openglvideo.camera;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by dengjx on 2017/11/16.
 */

public class TextureController implements GLSurfaceView.Renderer {
    private Object surface;
    private GLView mGLView;
    private Renderer mRenderer;                                 //用户附加的Renderer或用来监听Renderer



    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {

    }

    private class GLView extends GLSurfaceView{

        public GLView(Context context) {
            super(context);
        }
        private void init(){
            getHolder().addCallback(null);
            setEGLWindowSurfaceFactory(new GLSurfaceView.EGLWindowSurfaceFactory(){
                @Override
                public EGLSurface createWindowSurface(EGL10 egl10, EGLDisplay eglDisplay, EGLConfig eglConfig, Object o) {
                    return egl10.eglCreateWindowSurface(eglDisplay,eglConfig,surface,null);
                }

                @Override
                public void destroySurface(EGL10 egl10, EGLDisplay eglDisplay, EGLSurface eglSurface) {
                        egl10.eglDestroySurface(eglDisplay,eglSurface);
                }
            });
            setEGLContextClientVersion(2);
            setRenderer(TextureController.this);
            setRenderMode(RENDERMODE_WHEN_DIRTY);
            setPreserveEGLContextOnPause(true);

        }
        public void attachedToWindow(){
            super.onAttachedToWindow();
        }
        public void detachedFromWindow(){
            super.onDetachedFromWindow();
        }

    }

}
