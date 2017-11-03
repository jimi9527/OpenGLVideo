package com.example.dengjx.openglvideo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * 创建纹理
 * Created by dengjx on 2017/11/2.
 */

public class TextureHelper {
    private static final String TAG = "TextureHelper";

    public static int loadTexture(Context context ){
        final int [] textureObjectId = new int[1];
        GLES20.glGenTextures(1,textureObjectId,0);
        if(textureObjectId[0] == 0){
            Log.d(TAG,"生成纹理对象失败");
            return 0;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.liang,options);
        if (bitmap==null){
            Log.d(TAG,"加载位图失败");
            GLES20.glDeleteTextures(1,textureObjectId,0);
            return 0;
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureObjectId[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmap,0);
        bitmap.recycle();
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
        return textureObjectId[0];
    }

}
