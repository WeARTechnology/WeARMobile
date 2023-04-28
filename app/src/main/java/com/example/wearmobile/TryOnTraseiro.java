    package com.example.wearmobile;
    import androidx.annotation.OptIn;
    import android.media.Image;
    import android.os.Bundle;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.camera.core.CameraSelector;
    import androidx.camera.core.ImageAnalysis;
    import androidx.camera.core.Preview;
    import androidx.camera.lifecycle.ProcessCameraProvider;
    import androidx.camera.view.PreviewView;
    import androidx.core.content.ContextCompat;
    import com.google.common.util.concurrent.ListenableFuture;
    import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmarkList;
    import com.google.mediapipe.framework.TextureFrame;
    import com.google.mediapipe.solutioncore.ResultListener;
    import com.google.mediapipe.solutions.hands.HandsResult;
    import com.google.mediapipe.solutions.hands.Hands;
    import com.google.mediapipe.solutions.hands.HandsOptions;
    import com.google.mlkit.vision.common.InputImage;
    import java.util.concurrent.ExecutionException;

    public class TryOnTraseiro extends AppCompatActivity {
        private PreviewView previewView;
        private ProcessCameraProvider cameraProvider;
        private CameraSelector cameraSelector;
        private Preview preview;
        private Hands hands;
        private handLandmarksOverlayView handLandmarksOverlayView;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_try_on_traseiro);


            handLandmarksOverlayView = findViewById(R.id.hand_landmarks_overlay);
            handLandmarksOverlayView.setImageWidth(640);
            handLandmarksOverlayView.setImageHeight(480);

            HandsOptions handsOptions = HandsOptions.builder().setRunOnGpu(true).build();

            hands = new Hands(this, handsOptions);

            previewView = findViewById(R.id.preview_view);
            startCamera();
            // ...
        }
        @OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
        private void startCamera() {
            ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
            cameraProviderFuture.addListener(() -> {
                try {
                    cameraProvider = cameraProviderFuture.get();
                    preview = new Preview.Builder().build();

                    cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build();

                    preview.setSurfaceProvider(previewView.getSurfaceProvider());

                    ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build();

                    imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageProxy -> {
                        Image mediaImage = imageProxy.getImage();
                        if (mediaImage != null) {
                            InputImage inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

                            // Process the inputImage with MediaPipe and get the landmarks.
                            hands.send((TextureFrame) inputImage);
                        }
                        imageProxy.close();
                    });

                    cameraProvider.unbindAll();
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }, ContextCompat.getMainExecutor(this));

        }


        private void initializeHands() {
            HandsOptions handsOptions = HandsOptions.builder()
                    .setRunOnGpu(true)
                    .build();
            hands = new Hands(this, handsOptions);
            hands.setResultListener(new ResultListener<HandsResult>() {
                @Override
                public void run(HandsResult result) {
                    if (result.multiHandLandmarks() != null) {
                        for (NormalizedLandmarkList landmarks : result.multiHandLandmarks()) {
                            handLandmarksOverlayView.setLandmarks(landmarks.getLandmarkList());

                        }
                    }
                }
            });
        }




    }