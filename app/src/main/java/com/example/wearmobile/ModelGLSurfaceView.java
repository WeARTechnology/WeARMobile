package com.example.wearmobile;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.example.wearmobile.ModelRenderer;

public class ModelGLSurfaceView extends GLSurfaceView {
    private final ModelRenderer mRenderer = new ModelRenderer(getContext());

    public ModelGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
    }
}
