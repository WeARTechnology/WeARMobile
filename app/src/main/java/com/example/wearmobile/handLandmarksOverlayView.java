package com.example.wearmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;


import com.google.mediapipe.formats.proto.LandmarkProto;

import java.util.List;

public class handLandmarksOverlayView extends View {

    private List<LandmarkProto.NormalizedLandmark> landmarks;
    private int imageWidth, imageHeight;

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public handLandmarksOverlayView(Context context, int imageWidth, int imageHeight, AttributeSet attrs) {
            super(context,attrs);
            this.imageWidth = imageWidth;
            this.imageHeight = imageHeight;
        }

        public void setLandmarks(List<LandmarkProto.NormalizedLandmark> landmarks) {
            this.landmarks = landmarks;
            invalidate();
        }


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (landmarks != null) {
                Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.FILL);

                for (LandmarkProto.NormalizedLandmark landmark : landmarks) {
                    float x = landmark.getX() * imageWidth;
                    float y = landmark.getY() * imageHeight;
                    canvas.drawCircle(x, y, 10, paint);
                }
            }
        }
}