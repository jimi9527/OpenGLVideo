package com.example.dengjx.openglvideo.util;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by dengjx on 2017/10/10.
 */

public class ShaderUtil {
    private static final String TAG = "ShaderUtil";

    //从资源文件读入文本
    public static String readTextFilerFormResource(Context context,int resourceId){
           StringBuilder body = new StringBuilder();
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String nextLine;
            while ((nextLine = bufferedReader.readLine()) != null){
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body.toString();
    }
    //编译着色器源码
    public static int loadShader(int shaderType,String source){
        // 创建 着色器对象
        int shader = GLES20.glCreateShader(shaderType);
        Log.d(TAG,"shader:"+shader);
        if(shader != 0){
            // 编译
            GLES20.glShaderSource(shader,source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            // 验证
            GLES20.glGetShaderiv(shader,GLES20.GL_COMPILE_STATUS,compiled,0);
            if(compiled[0] == 0){
                Log.d(TAG,"Could not compile shader:"+shaderType);
                Log.d(TAG,"GLES20 Error:"+ GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader=0;
            }
        }
        return shader;
    }

    public static int createProgram(String vertexSource , String fragmentSource){
        int vertex = loadShader(GLES20.GL_VERTEX_SHADER , vertexSource);
        Log.d(TAG,"vertex:"+vertex);
        if(vertex == 0) return 0;
        int framgent = loadShader(GLES20.GL_FRAGMENT_SHADER , fragmentSource);
        Log.d(TAG,"framgent:"+framgent);
        if(framgent == 0) return 0;
        // 创建程序
        int program = GLES20.glCreateProgram();
        if(program != 0){
            // 将着色器和程序绑定
            GLES20.glAttachShader(program,vertex);
            checkGLError("Attach Vertex Shader");
            GLES20.glAttachShader(program,framgent);
            checkGLError("Attach Fragment Shader");
            // 链接
            GLES20.glLinkProgram(program);
            int [] linkStatus = new int[1];
            // 验证
            GLES20.glGetProgramiv(program,GLES20.GL_LINK_STATUS,linkStatus,0);
            if(linkStatus[0] != GLES20.GL_TRUE){
                Log.e(TAG,"Could not link program:"+ GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program=0;
            }
        }
        return program;
    }

    public static void checkGLError(String op){
        Log.d(TAG,op);
    }

}
