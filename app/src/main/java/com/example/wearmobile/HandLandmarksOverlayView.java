    package com.example.wearmobile;

    import android.content.Context;
    import android.graphics.Canvas;
    import android.graphics.Color;
    import android.graphics.Paint;
    import android.util.AttributeSet;
    import android.util.Log;
    import android.view.View;


    import com.google.mediapipe.formats.proto.LandmarkProto;
    import com.google.mediapipe.solutions.hands.HandLandmark;
    import com.google.mediapipe.solutions.hands.HandsOptions;

    import java.util.List;

    public class HandLandmarksOverlayView extends View {

        //Define os atributos da classe
        private List<LandmarkProto.NormalizedLandmark> landmarks; //Lista com os valores retornados do mediapipe, com cordenadas dos locais da mão
        private int imageWidth, imageHeight; //Altura e largura da imagem
        private RingRender ring;

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
        public HandLandmarksOverlayView(Context context, AttributeSet attrs) {
                super(context,attrs);
                ring = new RingRender(context);
            }


            //Método que define os landmarks recebidos, como os landmarks da classe
            public void setLandmarks(List<LandmarkProto.NormalizedLandmark> landmarks) {
                this.landmarks = landmarks;
                invalidate(); //Chama o draw dessa View
            }


            //Método onDraw, que vai desenhar os landarmarks todo mudar para 3D
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                ring.initScene();
            }
    }