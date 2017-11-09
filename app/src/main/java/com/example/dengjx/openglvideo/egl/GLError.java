package com.example.dengjx.openglvideo.egl;

/**
 * Created by dengjx on 2017/11/9.
 */

public enum  GLError{
    OK(0,"ok"),
    ConfigErr(101,"config not support");

    int code;
    String msg;
    GLError(int code, String msg){
        this.code=code;
        this.msg=msg;
    }
    public int value(){
        return code;
    }
    @Override
    public String toString() {
        return msg;
    }
}
