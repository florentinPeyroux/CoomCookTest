package com.eservices.floamnx.coomcook.event.util;

import android.content.Context;
import android.util.AttributeSet;

import com.eservices.floamnx.coomcook.event.util.shape_project.ShaderHelper;
import com.eservices.floamnx.coomcook.event.util.shape_project.ShaderImageView;
import com.eservices.floamnx.coomcook.event.util.shape_project.SvgShader;


public class ShapeImageView extends ShaderImageView {

    public ShapeImageView(Context context) {
        super(context);
    }

    public ShapeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShapeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public ShaderHelper createImageViewHelper() {
        return new SvgShader();
    }
}
