package com.example.wearmobile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsOptions;
import com.google.mediapipe.solutions.hands.HandsResult;

import org.rajawali3d.view.SurfaceView;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;


public class TryOn extends AppCompatActivity {
    //Inicialização de variáveis globais
    private PreviewView previewView; //Objeto do preview da camera na tela
    private ProcessCameraProvider cameraProvider; //Objeto que cuida do lifecycle da camera
    private CameraSelector cameraSelector; // Objeto que seleciona qual camera será usada (frontal, traseira, etc..)
    private Preview preview; //Objeto que permite que o preview de o que a camera está capturando seja usado

    private FaceMesh facemesh; //Objeto de facemesh do Mediapipe, que acha os pontos em um rosto quando detectado na camera
    private Hands hands; // Objeto da classe hands do mediapipe que recebe a imagem e acha a mão

    private boolean frontal = true;
    private Button switchCamera;
    private ImageView modelo1, modelo2, modelo3;
    private SurfaceView rajawaliSurface;
    private ModelsRenderer modelsRenderer;
    private int id = 7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_on);
        Intent intent = getIntent();
        if(intent.getExtras() != null) {
            frontal = intent.getExtras().getBoolean("Glass", true);
            id = intent.getExtras().getInt("ID");
        }


        switchCamera = findViewById(R.id.btnVirar);
        previewView = findViewById(R.id.preview_view);

        //Adicionando botões inferiores da tela, e seus onClicks
        modelo1 = findViewById(R.id.imgModelo3D1);
        modelo2 = findViewById(R.id.imgModelo3D2);
        modelo3 = findViewById(R.id.imgModelo3D3);
        defineBottomScroll(frontal);


        //Definindo a classe hands e as suas opções, por exemplo ,usar a GPU
        HandsOptions handsOptions = HandsOptions.builder()
                .setRunOnGpu(true)
                .setMaxNumHands(1) //Número máximo de mãos que ele consegue detectar
                .setMinDetectionConfidence(0.95F) //A confiança minima na deteção (0-1)
                .setMinTrackingConfidence(0.95F) //A confiança minima no rastreamento (0-1)
                .build();
        hands = new Hands(getApplicationContext(), handsOptions);

        //Cria as opções do facemesh, por exemplo, a confiança minima, e assim por diante
        FaceMeshOptions faceMeshOptions = FaceMeshOptions.builder()
                .setRunOnGpu(true)
                .setMinDetectionConfidence(0.95F) //Confinça minima de detecção
                .setMinTrackingConfidence(0.95F) //Confiança minima de rastreamento
                .setRefineLandmarks(true)
                .build();

        //Cria o objeto de facemesh com as opções criadas acima
        facemesh = new FaceMesh(getApplicationContext(), faceMeshOptions);

        //Listener do clique do botão que troca de câmera
        switchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (frontal) { //Quando o botão for clicado, inverte do frontal para o traseiro, definindo o frontal como true, o que fará que na proxima vez ele vá para o traseiro
                    initializeModel(12,false,false);
                    requestCameraPermission(); //Método que pede a permissão de câmera caso ela não exista
                    frontal = false;
                    defineBottomScroll(false);

                } else {
                    initializeModel(7,false,true);
                    requestCameraPermission(); //Método que pede a permissão de câmera caso ela não exista
                    frontal = true;
                    defineBottomScroll(true);
                }
            }
        });

        //Chamando os outros métodos da clase para rodarem
        requestCameraPermission(); //Método que pede a permissão de câmera caso ela não exista
        initializeModel(id,true,frontal);
        initializeHands(); //Método que inicializa o desenho da mão
        initializateFace(); //Método que inicializa o desenho do rosto


    }

    //Método que define quais imagens estarão no scroll view dos outros modelos, e, define qual modelo renderização ao serem clicados
    private void defineBottomScroll(boolean frontal){
        if (frontal) {
            modelo1.setOnClickListener(v -> initializeModel(7, false, true));
            modelo2.setOnClickListener(v -> initializeModel(5, false, true));
            modelo3.setOnClickListener(v -> initializeModel(6, false, true));
            modelo1.setImageResource(R.drawable.deaardappeletersround);
            modelo2.setImageResource(R.drawable.cipresteround);
            modelo3.setImageResource(R.drawable.maratusconstellatusround);
        } else {
            modelo1.setOnClickListener(v -> initializeModel(12,false,false));
            modelo2.setOnClickListener(v -> initializeModel(13,false,false));
            modelo3.setOnClickListener(v -> initializeModel(14,false,false));
            modelo1.setImageResource(R.drawable.girassoisdevangoghround);
            modelo2.setImageResource(R.drawable.oxcartround);
            modelo3.setImageResource(R.drawable.thepinkpeachtreeround);

        }
    }

    private void initializeModel(int i, boolean isFirst, boolean isGlass) {
        if(isFirst) {
            rajawaliSurface = findViewById(R.id.rajawali_surface);
            rajawaliSurface.setTransparent(true);
            rajawaliSurface.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            rajawaliSurface.setZOrderOnTop(true);
            rajawaliSurface.setElevation(10);
            modelsRenderer = new ModelsRenderer(this, i);
            modelsRenderer.setGlass(isGlass);
            rajawaliSurface.setSurfaceRenderer(modelsRenderer);
        }
        else {
            modelsRenderer.setGlass(isGlass);
            modelsRenderer.renderModel(i);
        }
    }

    private void requestCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) { //Checka se a permissão foi dada
            startCamera(); //Caso sim, inicializa a camera
        } else {
            // Caso não, pede a permissão da câmera
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100); //Dá um request para a permissão de código 100 (Camera)
        }
    }


    //Sobreescrevendo o método onRequestPermission result, para que, caso a permissão seja dada, inicalize a câmera, senão, avisa que é necessário faze-lo
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Descobre se a permissão foi dada
            startCamera(); //Inicializa a câmera
        } else {
            Toast.makeText(this, "É necessario habilitar a câmera pra usar o TryON", Toast.LENGTH_SHORT).show(); //Mensagem para o usuário
        }
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

        //Função que cria um array de bytes no formato NV21 que é uma variação de YUV, e, dentro dele, armazena os valores de YUV
        byte[] nv21 = new byte[ySize + uSize + vSize];
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        //Cria efetivamente uma imagem YUV com os dados obtidos
        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);

        //Função para transformar esse YUV em jpeg, mantendo a qualidade da imagem em 100
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

                //Cria a instancia do Preview, e, define as configurações dela
                preview = new Preview.Builder().build();
                cameraSelector = new CameraSelector.Builder()
                        //Utilização de operador ternário para, se a frontal for true definir a camêra frontal, senão a traseira
                        .requireLensFacing(frontal ? CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK)
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
                            imageProxy.close(); //Fecha o analyzer
                            return;
                        } else { //Caso as dimensões estejam certas

                            //Cria um bitmap da imagem fornecida, utilizando o método interno da classe, e passando o imageProxy
                            Bitmap bitmap = TryOn.this.imageProxyToBitmap(imageProxy);

                            //Caso o tamanho do bitmap for invalido, <= 0, ele fecha o método
                            if (bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
                                imageProxy.close(); //Fecha o método
                                return;
                            } else { //Caso tudo esteja certo, envia a imagem ao mediapipe
                                if (frontal) {
                                    facemesh.send(bitmap, imageProxy.getImageInfo().getTimestamp()); //Envia a imagem, e seu Timestamp
                                } else {
                                    hands.send(bitmap, imageProxy.getImageInfo().getTimestamp());
                                }
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
                Intent it = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(it);
            }
        }, ContextCompat.getMainExecutor(this)); //Define que, quem faz essa ação é a Activity atual
    }

    private void initializateFace() {
        requestCameraPermission(); //Método que pede a permissão de câmera caso ela não exista
        facemesh.setResultListener(new ResultListener<FaceMeshResult>() {
            @Override
            public void run(FaceMeshResult result) {
                if (result.multiFaceLandmarks() != null) {
                    for (LandmarkProto.NormalizedLandmarkList landmarks : result.multiFaceLandmarks()) {
                        modelsRenderer.updateGlassesPosition(landmarks.getLandmarkList());
                    }
                }
            }
        });


    }

    //Método que inicializa as mãos, pegando um listener do resultado do objeto de Hands, e, dentro dele, desenhando os landmarks da mão
    private void initializeHands() {
        requestCameraPermission(); //Método que pede a permissão de câmera caso ela não exista

        //Inicia o resultListener, ou seja, toda vez que ele enviar uma informação para a Classe, retorna um resultado, que é pego por esse Listener
        hands.setResultListener(new ResultListener<HandsResult>() {
            @Override
            public void run(HandsResult result) {

                if (result.multiHandLandmarks() != null) { //Se houver a detecção de alguma mão, ou seja, for diferente de null
                    for (LandmarkProto.NormalizedLandmarkList landmarks : result.multiHandLandmarks()) { //Para cada landmark dentro do resultado
                        modelsRenderer.updateRingPosition(landmarks.getLandmarkList());
                    }
                }


            }
        });
    }


}
