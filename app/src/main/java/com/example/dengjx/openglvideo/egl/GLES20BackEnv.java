package com.example.dengjx.openglvideo.egl;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.example.dengjx.openglvideo.filter.AFilter;

import java.nio.IntBuffer;

/**
 * Created by dengjx on 2017/11/9.
 */

public class GLES20BackEnv {
    private int mWidth;
    private int mHeight;
    private EGLHelper mEGLHelper;

    final static String TAG = "GLES20BackEnv";
    final static boolean LIST_CONFIGS = false;

    private AFilter mFilter;
    Bitmap mBitmap;
    String mThreadOwner;

    public GLES20BackEnv(int width ,int height){
        this.mWidth = width;
        this.mHeight = height;
        mEGLHelper = new EGLHelper();
        mEGLHelper.eglInit(width,height);
    }
    public void setThreadOwner(String threadOwner){
        this.mThreadOwner=threadOwner;
    }
    public void setFilter(final AFilter filter){
        mFilter = filter;
        if(!Thread.currentThread().getName().equals(mThreadOwner)){
            Log.e(TAG, "setRenderer: This thread does not own the OpenGL context.");
            return;
        }
        // Call the renderer initialization routines
        mFilter.create();
        mFilter.setSize(mWidth, mHeight);
    }
    public Bitmap getBitmap(){
        if (mFilter == null) {
            Log.e(TAG, "getBitmap: Renderer was not set.");
            return null;
        }
        if (!Thread.currentThread().getName().equals(mThreadOwner)) {
            Log.e(TAG, "getBitmap: This thread does not own the OpenGL context.");
            return null;
        }
      mFilter.setTextureId(createTexture(mBitmap));
      mFilter.draw();
        return convertToBitmap();
    }
    public void destroy() {
        mEGLHelper.destroy();
    }

    private Bitmap convertToBitmap(){
        int[] iat = new int[mWidth * mHeight];
        IntBuffer intBuffer = IntBuffer.allocate(mWidth * mHeight);
        mEGLHelper.mGl.glReadPixels(0,0,mWidth,mHeight,GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE,intBuffer);
        int[] ia = intBuffer.array();

        for(int i = 0 ; i < mHeight ; i++ ){
            System.arraycopy(ia,i * mWidth , iat,(mHeight - i - 1) * mWidth ,mWidth);
        }
        Bitmap bitmap = Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(IntBuffer.wrap(iat));
        return bitmap;
    }
    public void setInput(Bitmap bitmap){
        this.mBitmap=bitmap;
    }


    private int createTexture(Bitmap bmp){
        int[] texture=new int[1];
        if(bmp != null && !bmp.isRecycled()){
            GLES20.glGenTextures(1,texture,0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bmp,0);
            return texture[0];
        }
        return 0;
    }

}
