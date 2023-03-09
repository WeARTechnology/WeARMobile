package com.example.wearmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Camera;
import android.hardware.camera2.CameraCaptureSession;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import com.example.wearmobile.mediapipe.components.CameraHelper;
import com.example.wearmobile.mediapipe.components.CameraXPreviewHelper;
import com.example.wearmobile.mediapipe.components.ExternalTextureConverter;
import com.example.wearmobile.mediapipe.components.FrameProcessor;
import com.example.wearmobile.mediapipe.components.PermissionHelper;
import com.example.wearmobile.mediapipe.framework.AndroidAssetUtil;
import com.example.wearmobile.mediapipe.glutil.EglManager;

public class TryOnTraseiro extends AppCompatActivity {

    static {
        // Load all native libraries needed by the app.
        System.loadLibrary("mediapipe_jni");
        try {
            System.loadLibrary("opencv_java3");
        } catch (java.lang.UnsatisfiedLinkError e) {
            // Some example apps (e.g. template matching) require OpenCV 4.
            System.loadLibrary("opencv_java4");
        }
    }

    private CameraXPreviewHelper camera;
    private SurfaceHolder tela;
    private ViewGroup view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_on_traseiro);


        view = findViewById(R.id.camFrame);

        


    }

}