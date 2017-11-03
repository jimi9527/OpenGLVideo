package com.example.dengjx.openglvideo;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;

import com.example.dengjx.openglvideo.util.PathUtil;
import com.example.dengjx.openglvideo.util.ShaderUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.example.dengjx.openglvideo.util.PathUtil.VIDEO_RECORDER_FOLDER;
import static com.example.dengjx.openglvideo.util.PathUtil.VIDEO_RECORDER_TEMP_FILE;

/**
 * Created by dengjx on 2017/11/3.
 */

public class MediaRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener,
        MediaPlayer.OnVideoSizeChangedListener {
    private static final String TAG = "MediaRenderer";
    private Context context;

    private SurfaceTexture surfaceTexture;
    private MediaPlayer mediaplayer;


    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private float[] vertexData = {
            1f, -1f, 0f,
            -1f, -1f, 0f,
            1f, 1f, 0f,
            -1f, 1f, 0f,
    };
    private final float[] textureVertexData = {
            1f, 0f,
            0f, 0f,
            1f, 1f,
            0f, 1f

    };
    private float[] mSTMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];

    private int programId;
    private int aPositionHandle;
    private int textureId;
    private int uMatrixHandle;
    private int uTextureSamplerHandle;
    private int aTextureCoordHandle;
    private int uSTMMatrixHandle;

    private boolean playerPrepared;
    private boolean updateSurface;

    private int screenWidth, screenHeight;

    public MediaRenderer(Context context) {
        this.context = context;

        mediaplayer = new MediaPlayer();
        playerPrepared = false;
        synchronized (this) {
            updateSurface = false;
        }

        try {
            mediaplayer.setDataSource(PathUtil.getExistFilePath(VIDEO_RECORDER_FOLDER, VIDEO_RECORDER_TEMP_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaplayer.setLooping(true);
        mediaplayer.setOnVideoSizeChangedListener(this);

        // 将顶点坐标，纹理坐标 、顶点索引保存到本地内存
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);
        textureBuffer = ByteBuffer.allocateDirect(textureVertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureVertexData);
        textureBuffer.position(0);

    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        Log.d(TAG, "onSurfaceCreated");
        // 从Raw文件读取Glsl语言程序代码
        String vetexShader = ShaderUtil.readTextFilerFormResource(context, R.raw.media_vertex_shader);
        String fragmentShader = ShaderUtil.readTextFilerFormResource(context, R.raw.media_fragment_shader);
        programId = ShaderUtil.createProgram(vetexShader, fragmentShader);
        Log.d(TAG, "programId:" + programId);
        // 获取 顶点着色器中的数据
        aPositionHandle = GLES20.glGetAttribLocation(programId, "aPosition");
        // 获取 纹理坐标的数据
        aTextureCoordHandle = GLES20.glGetAttribLocation(programId, "aTexCoord");
        // 获取纹理数据数组
        uTextureSamplerHandle = GLES20.glGetUniformLocation(programId, "sTexture");

        uMatrixHandle = GLES20.glGetUniformLocation(programId, "uMatrix");
        uSTMMatrixHandle = GLES20.glGetUniformLocation(programId, "uSTMatrix");


        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        if (textures[0] == 0) {
            Log.d(TAG, "生成纹理对象失败");
        }
        textureId = textures[0];

        Log.d(TAG, "textureId");
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        surfaceTexture = new SurfaceTexture(textureId);
        surfaceTexture.setOnFrameAvailableListener(this);

        Surface surface = new Surface(surfaceTexture);
        mediaplayer.setSurface(surface);
        surface.release();

        if (!playerPrepared) {
            try {
                mediaplayer.prepare();
                playerPrepared = true;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "prepare失败");
            }
            mediaplayer.start();
            playerPrepared = true;
            Log.d(TAG, "prepare开始");
        }


    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        screenWidth = i;
        screenHeight = i1;
        float ratio = screenWidth > screenHeight ? screenWidth / screenHeight : screenHeight / screenWidth;
        if (screenWidth > screenHeight) {
            Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, -1f, 1f);
        } else {
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -ratio, ratio, -1f, 1f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        Log.d(TAG, "onDrawFrame");
        // 清除深度缓存和颜色深度缓存
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        synchronized (this) {
            if (updateSurface) {
                surfaceTexture.updateTexImage();
                surfaceTexture.getTransformMatrix(mSTMatrix);
                updateSurface = false;
            }
        }
        GLES20.glUseProgram(programId);
        GLES20.glUniformMatrix4fv(uMatrixHandle, 1, false, projectionMatrix, 0);
        GLES20.glUniformMatrix4fv(uSTMMatrixHandle, 1, false, mSTMatrix, 0);

        // 启用顶点数组
        GLES20.glEnableVertexAttribArray(aPositionHandle);
        GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);

        // 启用纹理数组
        GLES20.glEnableVertexAttribArray(aTextureCoordHandle);
        GLES20.glVertexAttribPointer(aTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 8, textureBuffer);


        // 启用一个纹理并绑定纹理ID
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glUniform1i(uTextureSamplerHandle, 0);
        GLES20.glViewport(0, 0, screenWidth, screenHeight);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        updateSurface = true;

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
        Log.d(TAG, "onVideoSizeChanged");
        Log.d(TAG, "i:" + i);
        Log.d(TAG, "i1:" + i1);
        updateProject(i, i1);
    }

    private void updateProject(int videoWidth, int videoHeight) {
        float screenratio =(float)(screenWidth > screenHeight ? screenWidth / screenHeight : screenHeight / screenWidth);
        float videoratio = (float) (videoWidth > videoHeight ? videoWidth / videoHeight : videoHeight / videoWidth);
        Log.d(TAG, "screenWidth:" + screenWidth + "screenHeight:" + screenHeight);
        Log.d(TAG, "screenratio:" + screenratio);
        Log.d(TAG, "videoratio:" + videoratio);
        if (videoratio > screenratio) {
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -videoratio / screenratio, videoratio / screenratio, -1f, 1f);
        } else {
            Matrix.orthoM(projectionMatrix, 0, -screenratio / videoratio, screenratio / videoratio, -1f, 1f, -1f, 1f);
        }
    }
}
