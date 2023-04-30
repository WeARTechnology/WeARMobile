    package com.example.wearmobile;

    import android.content.Context;
    import android.graphics.Canvas;
    import android.graphics.Color;
    import android.graphics.Paint;
    import android.util.AttributeSet;
    import android.util.Log;
    import android.view.View;


    import com.google.mediapipe.formats.proto.LandmarkProto;

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
                //Pega a altura e largura da tela com base no Canvas que é passado no método, Canvas representa o tamanho total daquela View na Acitivity
                 imageWidth = canvas.getWidth();
                 imageHeight = canvas.getHeight();


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
                            {0, 1}, {1, 2}, {2, 3}, {3, 4},
                            {0, 5}, {5, 6}, {6, 7}, {7, 8},
                            {0, 9}, {9, 10}, {10, 11}, {11, 12},
                            {0, 13}, {13, 14}, {14, 15}, {15, 16},
                            {0, 17}, {17, 18}, {18, 19}, {19, 20}
                    };

                    //Para cada valor de conexão dentro da matriz
                    for (int[] connection : connections) {
                        float x1 = landmarks.get(connection[0]).getX() * imageWidth ; //Define a linha como a multiplicação entre o ponto da conexão e a largura
                        float y1 = landmarks.get(connection[0]).getY() * imageHeight ;//Define a linha como a multiplicação entre o ponto da conexão e a altura
                        float x2 = landmarks.get(connection[1]).getX() * imageWidth;//Define a linha como a multiplicação entre o ponto da conexão e a largura
                        float y2 = landmarks.get(connection[1]).getY() * imageHeight;//Define a linha como a multiplicação entre o ponto da conexão e a altura
                        canvas.drawLine(x1, y1, x2, y2, paint); //Desenha a linha
                    }
                }
            }
    }