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

                if (landmarks != null) { //Se não forem nulos os landmarks
                    //Cria um objeto de Paint, definindo a cor como vermelho, e como cheios
                    Paint paint = new Paint();
                    paint.setColor(Color.RED);
                    paint.setStyle(Paint.Style.FILL);


                    // Para cada valor de landmark na lista recebida
                    for (LandmarkProto.NormalizedLandmark landmark : landmarks) {
                        float x =  imageWidth * landmark.getX(); //Define a cordenada X, como a multiplicação do valor recebido pela largura da tela
                        float y =  imageHeight * landmark.getY();//Define a cordenada Y, como a multiplicação do valor recebido pela altura da tela
                        canvas.drawCircle(x, y, 15, paint); //Desenha circulos de raio 15, usando as configurações acima

                    }

                    // Define o paint agora como azul, e como uma linha de grossura 8
                    paint.setColor(Color.BLUE);
                    paint.setStrokeWidth(8);

                    // Defina as conexões entre pontos de referência (por exemplo, ponta do polegar para polegar)
                    int[][] connections = {
                            {HandLandmark.WRIST, HandLandmark.THUMB_CMC},
                            {HandLandmark.THUMB_CMC, HandLandmark.THUMB_MCP},
                            {HandLandmark.THUMB_MCP, HandLandmark.THUMB_IP},
                            {HandLandmark.THUMB_IP, HandLandmark.THUMB_TIP},
                            {HandLandmark.WRIST, HandLandmark.INDEX_FINGER_MCP},
                            {HandLandmark.INDEX_FINGER_MCP, HandLandmark.INDEX_FINGER_PIP},
                            {HandLandmark.INDEX_FINGER_PIP, HandLandmark.INDEX_FINGER_DIP},
                            {HandLandmark.INDEX_FINGER_DIP, HandLandmark.INDEX_FINGER_TIP},
                            {HandLandmark.WRIST, HandLandmark.MIDDLE_FINGER_MCP},
                            {HandLandmark.MIDDLE_FINGER_MCP, HandLandmark.MIDDLE_FINGER_PIP},
                            {HandLandmark.MIDDLE_FINGER_PIP, HandLandmark.MIDDLE_FINGER_DIP},
                            {HandLandmark.MIDDLE_FINGER_DIP, HandLandmark.MIDDLE_FINGER_TIP},
                            {HandLandmark.WRIST, HandLandmark.RING_FINGER_MCP},
                            {HandLandmark.RING_FINGER_MCP, HandLandmark.RING_FINGER_PIP},
                            {HandLandmark.RING_FINGER_PIP, HandLandmark.RING_FINGER_DIP},
                            {HandLandmark.RING_FINGER_DIP, HandLandmark.RING_FINGER_TIP},
                            {HandLandmark.WRIST, HandLandmark.PINKY_MCP},
                            {HandLandmark.PINKY_MCP, HandLandmark.PINKY_PIP},
                            {HandLandmark.PINKY_PIP, HandLandmark.PINKY_DIP},
                            {HandLandmark.PINKY_DIP, HandLandmark.PINKY_TIP}
                    };


                    //Para cada valor de conexão dentro da matriz
                    for (int[] connection : connections) {
                        LandmarkProto.NormalizedLandmark startLandmark = landmarks.get(connection[0]);
                        LandmarkProto.NormalizedLandmark endLandmark = landmarks.get(connection[1]);
                        float startX = imageWidth * startLandmark.getX();
                        float startY = imageHeight * startLandmark.getY();
                        float endX = imageWidth * endLandmark.getX();
                        float endY = imageHeight * endLandmark.getY();
                        canvas.drawLine(startX, startY, endX, endY, paint);
                    }
                }
            }
    }