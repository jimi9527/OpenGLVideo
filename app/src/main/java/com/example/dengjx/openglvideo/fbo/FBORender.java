package com.example.dengjx.openglvideo.fbo;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import com.example.dengjx.openglvideo.filter.AFilter;
import com.example.dengjx.openglvideo.filter.GrayFilter;
import com.example.dengjx.openglvideo.util.Gl2Utils;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by dengjx on 2017/11/13.
 */

public class FBORender implements GLSurfaceView.Renderer {
    private Resources res;
    private AFilter mFilter;
    private Bitmap mBitmap;
    private ByteBuffer mBuffer;

    private int[] fFrame = new int[1];
    private int[] fRender = new int[1];
    private int[] fTexture = new int[2];

    private Callback mCallback;

    public FBORender(Resources res) {
       mFilter = new GrayFilter(res);
    }
    public void setmCallback(Callback mCallback ){
        this.mCallback = mCallback;
    }
    public void setmBitmap(Bitmap mBitmap){
        this.mBitmap = mBitmap;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        mFilter.create();
        mFilter.setMatrix(Gl2Utils.flip(Gl2Utils.getOriginalMatrix(),false,true));
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        if(mBitmap != null && !mBitmap.isRecycled()){
            createEnvi();
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,fFrame[0]);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D,fTexture[0],0);
            GLES20.glViewport(0,0,mBitmap.getWidth(),mBitmap.getHeight());

            mFilter.setTextureId(fTexture[0]);
            mFilter.draw();
            GLES20.glReadPixels(0,0,mBitmap.getWidth(),mBitmap.getHeight(),GLES20.GL_RGBA,GLES20.
                    GL_UNSIGNED_BYTE,mBuffer);
            if(mCallback != null){
                mCallback.onCall(mBuffer);
            }
            deleteEnvi();
            mBitmap.recycle();
        }
    }
    private void deleteEnvi() {
        GLES20.glDeleteTextures(2, fTexture, 0);
        GLES20.glDeleteRenderbuffers(1, fRender, 0);
        GLES20.glDeleteFramebuffers(1, fFrame, 0);
    }
    private void createEnvi() {
        GLES20.glGenFramebuffers(1,fFrame,0);
        //生成Render Buffer
        GLES20.glGenRenderbuffers(1,fRender,0);
        //绑定Render Buffer
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER,fRender[0]);
        //设置为深度的Render Buffer，并传入大小
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER,GLES20.GL_DEPTH_COMPONENT16,
                mBitmap.getWidth(),mBitmap.getHeight());
        //为FrameBuffer挂载fRender[0]来存储深度
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER
        ,fRender[0]);
        //解绑Render Buffer
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER,0);

        //生成纹理
        GLES20.glGenTextures(2,fTexture,0);
        for(int i = 0; i < 2 ; i++){
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,fTexture[i]);
            if( i == 0){
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,GLES20.GL_RGBA,mBitmap,0);
            }else {
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,0,GLES20.GL_RGBA,mBitmap.getWidth(),mBitmap.getHeight(),
                        0,GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE,null);
            }
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        }
        mBuffer = ByteBuffer.allocate(mBitmap.getWidth() * mBitmap.getHeight() * 4);
    }


    interface Callback{
        void onCall(ByteBuffer data);
    }
}
