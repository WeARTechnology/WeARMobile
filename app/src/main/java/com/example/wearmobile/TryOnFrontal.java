package com.example.wearmobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutioncore.ResultListener;
import com.google.mediapipe.solutions.facemesh.FaceMesh;
import com.google.mediapipe.solutions.facemesh.FaceMeshOptions;
import com.google.mediapipe.solutions.facemesh.FaceMeshResult;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class TryOnFrontal extends AppCompatActivity {
    //Inicialização de variáveis globais
    private PreviewView previewView; //Objeto do preview da camera na tela
    private ProcessCameraProvider cameraProvider; //Objeto que cuida do lifecycle da camera
    private CameraSelector cameraSelector; // Objeto que seleciona qual camera será usada (frontal, traseira, etc..)
    private Preview preview; //Objeto que permite que o preview de o que a camera está capturando seja usado
    private FaceMesh facemesh; //Objeto de facemesh do Mediapipe, que acha os pontos em um rosto quando detectado na camera
    private GlassesLandmarksOverlayView glassesLandmarksOverlayView;/*Objeto da classe interna HandLandmarksOverlayView, que produz um View na activity,
                                                                que possui os pontos recebidos através dos dados da classe facemesh*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_on_frontal);

        //Atribuindo objetos aos views da activity
        previewView = findViewById(R.id.preview_view_frontal);
        glassesLandmarksOverlayView = findViewById(R.id.face_landmarks_overlay);

        //Cria as opções do facemesh, por exemplo, a confiança minima, e assim por diante
        FaceMeshOptions faceMeshOptions = FaceMeshOptions.builder()
                .setRunOnGpu(true)
                .setMinDetectionConfidence(0.95F) //Confinça minima de detecção
                .setMinTrackingConfidence(0.95F) //Confiança minima de rastreamento
                .setRefineLandmarks(true)
                .build();

        //Cria o objeto de facemesh com as opções criadas acima
        facemesh = new FaceMesh(this, faceMeshOptions);

        startCamera();
        initializateFace();


    }


    //Método que converte ImageProxy(Objeto que vem da classe ImageAnalysis) em Bitmap para enviar ao Mediapipe
    private Bitmap imageProxyToBitmap(ImageProxy image) {
        //Pega os valores de YUV do ImageProxy (YUV é um formato de imagem, que não é compatível com Mediapipe)
        ImageProxy.PlaneProxy yPlane = image.getPlanes()[0];
        ImageProxy.PlaneProxy uPlane = image.getPlanes()[1];
        ImageProxy.PlaneProxy vPlane = image.getPlanes()[2];

        //Salva os valores obtidos em buffers para transferir a informação
        ByteBuffer yBuffer = yPlane.getBuffer();
        ByteBuffer uBuffer = uPlane.getBuffer();
        ByteBuffer vBuffer = vPlane.getBuffer();

        //Pega o tamanho de cada buffer, ou seja, cada valor da imagem original
        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        //"Método" que cria um array de bytes no formato NV21 que é uma variação de YUV, e, dentro dele, armazena os valores de YUV
        byte[] nv21 = new byte[ySize + uSize + vSize];
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        //Cria efetivamente uma imagem YUV com os dados obtidos
        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);

        //"Metodo" para transformar esse YUV em jpeg, mantendo a qualidade da imagem em 100
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 100, out);

        //Pega os bytes da imagem, e decodifica eles em bitmap, para fazer o retorno do método
        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    private void startCamera() {
        /*Leitor assincrono(Executa no futuro, no momento em que a computação está completa) da classe ProcessCameraProvider, que é a classe que
         * cuida dos recursos da camera e do ciclo de vida, ao chamar ProcessCameraProvider.getInstance ele retonra uma instancia da imagem da
         * camera, mas é assincrono pois esse retorno pode demorar*/
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> { //Adiciona um listener, para, quando houver o retorno assincrono, rodar uma função
            try { //Utiliza-se try, pois, por ser assincrono, podem haver erros se rodar direto

                cameraProvider = cameraProviderFuture.get(); //Pega os dados da câmera "do futuro"

                //Cria a instancia do Preview, e, define as configurações dela, no caso, usar a câmera frontal
                preview = new Preview.Builder().build();
                cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                //Define para qual superficie a preview vai, que no caso, é para o previewView
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                //Instancia ImageAnalysis, classe que mantem o ultimo quadro do vídeo em buffer, e analisa ele, podendo realizar funções
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                //Define o analizador, ou seja, quando houver um quadro, o que será feito com ele
                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
                    @Override
                    public void analyze(@NonNull ImageProxy imageProxy) {
                        //Se as dimensões da imagem forem <= a 0, significa que a imagem não existe, pois suas dimensões são invalidas
                        if (imageProxy.getWidth() <= 0 || imageProxy.getHeight() <= 0) {
                            Log.e("TryOnTraseiro", "Dimensões da imagem inválidas, Largura=" + imageProxy.getWidth() + ", Altura=" + imageProxy.getHeight());
                            imageProxy.close(); //Fecha o analyzer
                            return;
                        } else { //Caso as dimensões estejam certas

                            //Cria um bitmap da imagem fornecida, utilizando o método interno da classe, e passando o imageProxy
                            Bitmap bitmap = TryOnFrontal.this.imageProxyToBitmap(imageProxy);

                            //Muda a escala desse bitmap, para um tamanho que o Mediapipe reconheça, que é 256x256px
                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 256, 256, true);

                            //Caso o tamanho do bitmap for invalido, <= 0, ele fecha o método
                            if (bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0 || scaledBitmap.getWidth() <= 0 || scaledBitmap.getHeight() <= 0) {
                                Log.e("TryOnTraseiro", "Dimensões da imagem inválidas, Largura=" + bitmap.getWidth() + ", Altura=" + bitmap.getHeight());
                                imageProxy.close(); //Fecha o método
                                return;
                            } else { //Caso tudo esteja certo, envia a imagem ao mediapipe
                                facemesh.send(scaledBitmap, imageProxy.getImageInfo().getTimestamp()); //Envia a imagem, e seu Timestamp
                            }
                            imageProxy.close(); //Ao fim da função, fecha o ImageProxy que é dado pelo Analyze
                        }
                    }
                });
                cameraProvider.unbindAll(); //Solta tudo do cameraProvider, para "limpa-lo"
                //Prende ao lifecycle do CameraProvider, os valores da camera, do preview, e da imagem analisada
                cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis);
            } catch (ExecutionException | InterruptedException e) { //Caso der erro, dá um catch e retorna a home
                e.printStackTrace();
                Intent it = new Intent(getApplicationContext(), Home.class);
                startActivity(it);
            }
        }, ContextCompat.getMainExecutor(this)); //Define que, quem faz essa ação é a Activity atual
    }

    private void initializateFace() {
        //Cria as opções do facemesh, por exemplo, a confiança minima, e assim por diante
        FaceMeshOptions faceMeshOptions = FaceMeshOptions.builder()
                .setRunOnGpu(true)
                .setMinDetectionConfidence(0.95F) //Confinça minima de detecção
                .setMinTrackingConfidence(0.95F) //Confiança minima de rastreamento
                .setRefineLandmarks(true)
                .build();

        //Cria o objeto de facemesh com as opções criadas acima
        facemesh = new FaceMesh(this, faceMeshOptions);

        facemesh.setResultListener(new ResultListener<FaceMeshResult>() {
            @Override
            public void run(FaceMeshResult result) {

                if(result.multiFaceLandmarks() != null){ //Se houver algum landmark de rosto
                    for(LandmarkProto.NormalizedLandmarkList landmarks : result.multiFaceLandmarks()){
                        Log.i("Resultados", "Landmarks: " +  landmarks.getLandmarkList());
                        glassesLandmarksOverlayView.setLandmarks(landmarks.getLandmarkList());
                        //todo MUDAR PARA 3D
                    }
                }
            }
        });

    }
}
