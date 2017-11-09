package com.example.dengjx.openglvideo.filter;

import android.content.res.Resources;

/**
 * Created by dengjx on 2017/11/9.
 */

public class GrayFilter extends AFilter {

    public GrayFilter(Resources resources) {
        super(resources);
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("shader/base_vertex.sh","shader/color/gray_fragment.frag");
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }
}
