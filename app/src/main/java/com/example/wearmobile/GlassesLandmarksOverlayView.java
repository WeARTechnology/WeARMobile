package com.example.wearmobile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.google.common.collect.ImmutableList;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutions.facemesh.FaceMeshConnections;
import com.google.mediapipe.solutions.facemesh.FaceMeshResult;

import java.util.List;

public class GlassesLandmarksOverlayView extends View {

    //Define os atributos da classe
    private List<LandmarkProto.NormalizedLandmark> landmarks; //Lista com os valores retornados do mediapipe, com cordenadas dos locais da mão
    private int imageWidth, imageHeight; //Altura e largura da imagem

    //Getters e Setters, para permitir manipulação dos valores da altura e largura, considerando que são privados
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

    //Construtor, que pega o contexto, e os atributos da tela em que foi chamado
    public GlassesLandmarksOverlayView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }


    //Método que define os landmarks recebidos, como os landmarks da classe
    public void setLandmarks(List<LandmarkProto.NormalizedLandmark> landmarks) {
        this.landmarks = landmarks;
        invalidate(); //Chama o draw dessa View
    }

    private Paint linePaint;


    private void init() {

    }

    private PointF landmarkToScreenCoordinate(LandmarkProto.NormalizedLandmark landmark) {
        float x = landmark.getX() * imageWidth;
        float y = landmark.getY() * imageHeight;
        return new PointF(x, y);
    }



    //Método onDraw, que vai desenhar os landarmarks todo mudar para 3D
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        linePaint = new Paint();
        linePaint.setColor(Color.GREEN);
        linePaint.setStrokeWidth(2);


        if (landmarks == null) {
            return;
        }

        for (FaceMeshConnections.Connection connection : FaceMeshConnections.FACEMESH_CONTOURS) {
            PointF start = landmarkToScreenCoordinate(landmarks.get(connection.start()));
            PointF end = landmarkToScreenCoordinate(landmarks.get(connection.end()));
            canvas.drawLine(start.x, start.y, end.x, end.y, linePaint);
        }

        // Repeat the above loop for other face mesh connections like FACEMESH_LEFT_EYE, FACEMESH_RIGHT_EYE, etc.
        for (FaceMeshConnections.Connection connection : FaceMeshConnections.FACEMESH_LEFT_EYE) {
            PointF start = landmarkToScreenCoordinate(landmarks.get(connection.start()));
            PointF end = landmarkToScreenCoordinate(landmarks.get(connection.end()));
            canvas.drawLine(start.x, start.y, end.x, end.y, linePaint);
        }

        for (FaceMeshConnections.Connection connection : FaceMeshConnections.FACEMESH_RIGHT_EYE) {
            PointF start = landmarkToScreenCoordinate(landmarks.get(connection.start()));
            PointF end = landmarkToScreenCoordinate(landmarks.get(connection.end()));
            canvas.drawLine(start.x, start.y, end.x, end.y, linePaint);
        }
        for (FaceMeshConnections.Connection connection : FaceMeshConnections.FACEMESH_FACE_OVAL) {
            PointF start = landmarkToScreenCoordinate(landmarks.get(connection.start()));
            PointF end = landmarkToScreenCoordinate(landmarks.get(connection.end()));
            canvas.drawLine(start.x, start.y, end.x, end.y, linePaint);
        }
        for (FaceMeshConnections.Connection connection : FaceMeshConnections.FACEMESH_LEFT_EYEBROW) {
            PointF start = landmarkToScreenCoordinate(landmarks.get(connection.start()));
            PointF end = landmarkToScreenCoordinate(landmarks.get(connection.end()));
            canvas.drawLine(start.x, start.y, end.x, end.y, linePaint);
        }
        for (FaceMeshConnections.Connection connection : FaceMeshConnections.FACEMESH_RIGHT_EYEBROW) {
            PointF start = landmarkToScreenCoordinate(landmarks.get(connection.start()));
            PointF end = landmarkToScreenCoordinate(landmarks.get(connection.end()));
            canvas.drawLine(start.x, start.y, end.x, end.y, linePaint);
        }

        for (FaceMeshConnections.Connection connection : FaceMeshConnections.FACEMESH_LEFT_IRIS) {
            PointF start = landmarkToScreenCoordinate(landmarks.get(connection.start()));
            PointF end = landmarkToScreenCoordinate(landmarks.get(connection.end()));
            canvas.drawLine(start.x, start.y, end.x, end.y, linePaint);
        }
        for (FaceMeshConnections.Connection connection : FaceMeshConnections.FACEMESH_RIGHT_IRIS) {
            PointF start = landmarkToScreenCoordinate(landmarks.get(connection.start()));
            PointF end = landmarkToScreenCoordinate(landmarks.get(connection.end()));
            canvas.drawLine(start.x, start.y, end.x, end.y, linePaint);
        }

        for (FaceMeshConnections.Connection connection : FaceMeshConnections.FACEMESH_LIPS) {
            PointF start = landmarkToScreenCoordinate(landmarks.get(connection.start()));
            PointF end = landmarkToScreenCoordinate(landmarks.get(connection.end()));
            canvas.drawLine(start.x, start.y, end.x, end.y, linePaint);
        }

    }


}