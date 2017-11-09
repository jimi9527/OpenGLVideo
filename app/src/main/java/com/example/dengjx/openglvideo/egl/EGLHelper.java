package com.example.dengjx.openglvideo.egl;


import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by dengjx on 2017/11/9.
 */

public class EGLHelper {
    public EGL10 mGl10;
    public EGLDisplay mGlDisplay;
    public EGLConfig mGlConfig;
    public EGLSurface mGlSurface;
    public EGLContext mGlContext;
    public GL10 mGl;

    private static final int EGL_CONTEXT_CLIENT_VERSION=0x3098;
    public static final int SURFACE_PBUFFER=1;
    public static final int SURFACE_PIM=2;
    public static final int SURFACE_WINDOW=3;

    private int surfaceType=SURFACE_PBUFFER;
    private Object surface_native_obj;

    private int red=8;
    private int green=8;
    private int blue=8;
    private int alpha=8;
    private int depth=16;
    private int renderType=4;

    private int bufferType=EGL10.EGL_SINGLE_BUFFER;
    private EGLContext shareContext=EGL10.EGL_NO_CONTEXT;

    public void config(int red,int green,int blue,int alpha,int depth,int renderType){
        this.red=red;
        this.green=green;
        this.blue=blue;
        this.alpha=alpha;
        this.depth=depth;
        this.renderType=renderType;
    }

    public void setSurfaceType(int type,Object ... objects){
        this.surfaceType=type;
        if(objects!=null){
            this.surface_native_obj=objects[0];
        }
    }

     public GLError eglInit(int width , int height){
         int [] attributes = new int[]{
             EGL10.EGL_RED_SIZE,red,
             EGL10.EGL_GREEN_SIZE,green,
                 EGL10.EGL_BLUE_SIZE, blue,  //指定B大小
                 EGL10.EGL_ALPHA_SIZE, alpha, //指定Alpha大小，以上四项实际上指定了像素格式
                 EGL10.EGL_DEPTH_SIZE, depth, //指定深度缓存(Z Buffer)大小
                 EGL10.EGL_RENDERABLE_TYPE, renderType, //指定渲染api版本, EGL14.EGL_OPENGL_ES2_BIT
                 EGL10.EGL_NONE
         };

         mGl10 = (EGL10) EGLContext.getEGL();
         mGlDisplay = mGl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
         // 主版本号和副版本号
         int[] version = new int[2];
         mGl10.eglInitialize(mGlDisplay,version);

         // 选择Config
         int [] configNum = new int[1];
         mGl10.eglChooseConfig(mGlDisplay,attributes,null,0,configNum);
         if(configNum[0] == 0){
             return GLError.ConfigErr;
         }
         EGLConfig[] c = new EGLConfig[configNum[0]];
         mGl10.eglChooseConfig(mGlDisplay,attributes,c,configNum[0],configNum);
         mGlConfig = c[0];

         // 创建surface
         int [] surattr = new int[]{
                 EGL10.EGL_WIDTH,width,
                 EGL10.EGL_HEIGHT,height,
                 EGL10.EGL_NONE
         };
         mGlSurface =createSurface(surattr);

         // 创建Context
         int[] contextAttr=new int[]{
                 EGL_CONTEXT_CLIENT_VERSION,2,
                 EGL10.EGL_NONE
         };
         mGlContext = mGl10.eglCreateContext(mGlDisplay,mGlConfig,shareContext,contextAttr);
         makeCurrent();
         return GLError.OK;
     }

     public void makeCurrent(){
         mGl10.eglMakeCurrent(mGlDisplay,mGlSurface,mGlSurface,mGlContext);
         mGl = (GL10) mGlContext.getGL();
     }


     private EGLSurface createSurface(int[] attr){
        switch (surfaceType){
            case SURFACE_WINDOW:
                return mGl10.eglCreateWindowSurface(mGlDisplay,mGlConfig,surface_native_obj,attr);
            case SURFACE_PIM:
                return mGl10.eglCreatePixmapSurface(mGlDisplay,mGlConfig,surface_native_obj,attr);
            default:
                return mGl10.eglCreatePbufferSurface(mGlDisplay,mGlConfig,attr);
        }
     }

     public void destroy(){
         mGl10.eglMakeCurrent(mGlDisplay,EGL10.EGL_NO_SURFACE,EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
         mGl10.eglDestroySurface(mGlDisplay,mGlSurface);
         mGl10.eglDestroyContext(mGlDisplay,mGlContext);
         mGl10.eglTerminate(mGlDisplay);
     }

}
