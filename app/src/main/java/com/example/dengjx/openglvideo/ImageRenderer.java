package com.example.dengjx.openglvideo;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.example.dengjx.openglvideo.util.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 显示图片渲染器
 * Created by dengjx on 2017/11/2.
 */

public class ImageRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "ImageRenderer";
    private final float[] projectionMatrix = new float[16];

    private final float[] vetexData = {
            0f, 0f, 0f,
            1f, 1f, 0f,
            -1f, 1f, 0f,
            -1f, -1f, 0f,
            1f, -1f, 0f
    };

    private final short[] indexData = {
            0, 1, 2,
            0, 2, 3,
            0, 3, 4,
            0, 1, 4,
    };
    private final float[] textureVertexData = {
            0.5f, 0.5f,
            1f, 0f,
            0f, 0f,
            0f, 1f,
            1f, 1f
    };
    private Context context;
    private int programId;
    private int aPositionHandle;
    private int textureId;
    private int uMatrixHandle;
    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    private FloatBuffer textureVertexBuffer;
    private int uTextureSamplerHandle;
    private int aTextureCoordHandle;


    public ImageRenderer(Context context) {
        this.context = context;

        // 将顶点坐标，纹理坐标 、顶点索引保存到本地内存
        vertexBuffer = ByteBuffer.allocateDirect(vetexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vetexData);
        vertexBuffer.position(0);

        indexBuffer = ByteBuffer.allocateDirect(indexData.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(indexData);
        indexBuffer.position(0);

        textureVertexBuffer = ByteBuffer.allocateDirect(textureVertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureVertexData);
        textureVertexBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        Log.d(TAG, "onSurfaceCreated");
        // 从Raw文件读取Glsl语言程序代码
        String vetexShader = ShaderUtil.readTextFilerFormResource(context, R.raw.iamge_vertex_shader);
        String fragmentShader = ShaderUtil.readTextFilerFormResource(context, R.raw.image_fragment_shader);
        programId = ShaderUtil.createProgram(vetexShader, fragmentShader);
        Log.d(TAG, "programId:" + programId);
        // 获取 顶点着色器中的数据
        aPositionHandle = GLES20.glGetAttribLocation(programId, "aPosition");
        // 获取 纹理坐标的数据
        aTextureCoordHandle = GLES20.glGetAttribLocation(programId, "aTexCoord");
        // 获取为例数据数组
        uTextureSamplerHandle = GLES20.glGetUniformLocation(programId, "sTexture");

        uMatrixHandle = GLES20.glGetUniformLocation(programId, "uMatrix");
        textureId = TextureHelper.loadTexture(context);

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        Log.d(TAG, "onSurfaceChanged");
        float ratio = i > i1 ?
                (float) i / i1 :
                (float) i1 / i;
        if (i > i1) {
            Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, -1f, 1f);
        } else Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -ratio, ratio, -1f, 1f);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        Log.d(TAG, "onDrawFrame");
        // 清除深度缓存和颜色深度缓存
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(programId);
        GLES20.glUniformMatrix4fv(uMatrixHandle, 1, false, projectionMatrix, 0);
        // 启用顶点数组
        GLES20.glEnableVertexAttribArray(aPositionHandle);
        GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);

        // 启用纹理数组
        GLES20.glEnableVertexAttribArray(aTextureCoordHandle);
        GLES20.glVertexAttribPointer(aTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 8, textureVertexBuffer);

        // 启用一个纹理并绑定纹理ID
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(uTextureSamplerHandle, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexData.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

    }
}
