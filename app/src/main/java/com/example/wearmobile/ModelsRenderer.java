package com.example.wearmobile;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;

import com.google.mediapipe.formats.proto.LandmarkProto;

import org.rajawali3d.Object3D;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.methods.SpecularMethod;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.Renderer;

import java.util.List;

public class ModelsRenderer extends Renderer {

    //Objetos do modelo 3d e o inteiro que define o modelo
    Object3D model = null;
    int modelId = 0;
    float scaleFactorModel = 0 , xOffSet = 0, yOffset = 0;
    boolean isGlass = true;
    float rotationDegrees;

    //Construtor que pega o contexto e o modelo que deve ser renderizado
    public ModelsRenderer(Context context, int model) {
        super(context);
        this.modelId = model;
    }

    public boolean isGlass() {
        return isGlass;
    }

    public void setGlass(boolean glass) {
        isGlass = glass;
    }

    @Override
    protected void initScene() {
        renderModel(modelId);
    }

    public void renderModel(int model) {
        getCurrentScene().clearChildren();
        getCurrentCamera().setPosition(0, 0, 20);
        getCurrentCamera().setLookAt(0, 0, 0);
        if(isGlass) {
            switch (model) {
                case 7:
                case 0:
                    this.model = loadModel(R.raw.deaardappeleters);
                    scaleFactorModel = 0.9f;
                    yOffset = 0.8f;
                    xOffSet = 0.35f;
                    break;
                case 5:
                    this.model = loadModel(R.raw.cipreste);
                    scaleFactorModel = 4f;
                    xOffSet = -2.8f;
                    yOffset =  0.9f;
                    break;
                case 6:
                    this.model = loadModel(R.raw.maratusconstelatus);
                    scaleFactorModel = 7.5f;
                    yOffset = 2.65f;
                    xOffSet = 0.3f;
                    break;
            }
        }
        else
        {
            switch (model) {
                case 12:
                case 0:
                    this.model = loadModel(R.raw.girassoisdevangogh);
                    scaleFactorModel = 0.5f;
                    rotationDegrees = 0;
                    break;
                case 13:
                    this.model = loadModel(R.raw.oxcart);
                    scaleFactorModel = 0.09f;
                    rotationDegrees = -110;
                    break;
                case 14:
                    this.model = loadModel(R.raw.thepinkpeachtree);
                    scaleFactorModel = 2f;
                    rotationDegrees = -90;
                    break;
            }
        }
        getCurrentScene().addChild(this.model);
    }


    @Override
    protected void onRender(long elapsedTime, double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
    }
    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
    }
    @Override
    public void onTouchEvent(MotionEvent event) {
    }

    private Object3D loadModel(int modelResId){
        //Carrega o modelo 3d com o id passado pelo método
        LoaderOBJ loader = new LoaderOBJ(mContext.getResources(), mTextureManager, modelResId);
        //Faz um parse nesse modelo
        try {
            loader.parse();
        } catch (ParsingException e) {
            e.printStackTrace();

        }
        //Pega o modelo 3d através do loader
        Object3D object3D = loader.getParsedObject();

        //Cria um material padrão, branco, sem influência de luz, etc..
        Material material = new Material();
        material.setDiffuseMethod(new DiffuseMethod.Lambert());
        material.setSpecularMethod(new SpecularMethod.Phong());
        material.setColorInfluence(0);
        material.setColor(Color.WHITE);

        //Coloca o material no objeto 3d, e o retorna
        object3D.setMaterial(material);
        return object3D;
    }


    //Método que atualiza as posições do óculos com base nos landmarks
    public void updateGlassesPosition(List<LandmarkProto.NormalizedLandmark> landmarks) {
        //Se houverem landmarks, e um óculos
        if(landmarks!= null && model != null) {
            Vector3 position = calculatePositionGlass(landmarks);
            Vector3 scale = calculateScaleGlass(landmarks);
            Vector3 rotation = calculateRotationGlass(landmarks);

            model.setPosition(position);
            model.setScale(scale);
            model.setRotation(rotation);
        }


    }

    public void updateRingPosition(List<LandmarkProto.NormalizedLandmark> landmarks) {
        //Se houverem landmarks, e um óculos
        if(landmarks!= null && model != null) {
            Vector3 position = calculatePositionRing(landmarks);
            Vector3 scale = calculateScaleRing(landmarks);
            Vector3 rotation = calculateRotationRing(landmarks);

            model.setPosition(position);
            model.setScale(scale);
            model.setRotation(rotation);
        }


    }


    private Vector3 calculatePositionGlass(List<LandmarkProto.NormalizedLandmark> landmarks) {
        // Identifique os landmarks relevantes
        LandmarkProto.NormalizedLandmark leftEye = landmarks.get(33) ;
        LandmarkProto.NormalizedLandmark rightEye = landmarks.get(263) ;

        // Calcule a posição média entre os dois olhos
        float x = (leftEye.getX() + rightEye.getX()) / 2;
        float y = (leftEye.getY() + rightEye.getY()) / 2;
        float z = (leftEye.getZ() + rightEye.getZ()) / 2;

        return new Vector3(x - xOffSet, y - yOffset, z);
    }


    private Vector3 calculateScaleGlass(List<LandmarkProto.NormalizedLandmark> landmarks) {
        // Identifique os landmarks relevantes
        LandmarkProto.NormalizedLandmark leftEye = landmarks.get(33);
        LandmarkProto.NormalizedLandmark rightEye = landmarks.get(263);

        // Calcule a distância entre os dois olhos
        float distance = (float) Math.sqrt(Math.pow(leftEye.getX() - rightEye.getX(), 2) +
                Math.pow(leftEye.getY() - rightEye.getY(), 2) +
                Math.pow(leftEye.getZ() - rightEye.getZ(), 2));

        // Ajuste a escala do modelo de óculos com base na distância entre os olhos
        float scale = distance * scaleFactorModel;

        return new Vector3(scale, scale, scale);
    }




    private Vector3 calculateRotationGlass(List<LandmarkProto.NormalizedLandmark> landmarks) {
        // Identifique os landmarks relevantes
        LandmarkProto.NormalizedLandmark leftEye = landmarks.get(33);
        LandmarkProto.NormalizedLandmark rightEye = landmarks.get(263);

        // Calcule a inclinação e a rotação da face usando os dois olhos
        float pitch = (float) Math.atan2(leftEye.getY() - rightEye.getY(), leftEye.getX() - rightEye.getX());
        float yaw = (float) Math.atan2(leftEye.getZ() - rightEye.getZ(), leftEye.getX() - rightEye.getX());

        return new Vector3(pitch, yaw, 0);
    }







    private Vector3 calculatePositionRing(List<LandmarkProto.NormalizedLandmark> landmarks) {
        // Identifique os landmarks relevantes
        LandmarkProto.NormalizedLandmark ringBase = landmarks.get(15);
        LandmarkProto.NormalizedLandmark ringTip = landmarks.get(19);

        // Calcule a posição média entre a base e a ponta do dedo anelar
        float x = (ringBase.getX() + ringTip.getX()) / 2;
        float y = (ringBase.getY() + ringTip.getY()) / 2;
        float z = (ringBase.getZ() + ringTip.getZ()) / 2;

        return new Vector3(x, y, z);
    }


    private Vector3 calculateScaleRing(List<LandmarkProto.NormalizedLandmark> landmarks) {
        // Identifique os landmarks relevantes
        LandmarkProto.NormalizedLandmark ringBase = landmarks.get(15);
        LandmarkProto.NormalizedLandmark ringTip = landmarks.get(19);

        // Calcule a distância entre a base e a ponta do dedo anelar
        float distance = (float) Math.sqrt(Math.pow(ringBase.getX() - ringTip.getX(), 2) +
                Math.pow(ringBase.getY() - ringTip.getY(), 2) +
                Math.pow(ringBase.getZ() - ringTip.getZ(), 2));

        // Ajuste a escala do modelo de anel com base na distância entre a base e a ponta do dedo anelar
        float scale = distance * scaleFactorModel;

        return new Vector3(scale, scale, scale);
    }


    private Vector3 calculateRotationRing(List<LandmarkProto.NormalizedLandmark> landmarks) {
        // Identifique os landmarks relevantes
        LandmarkProto.NormalizedLandmark ringBase = landmarks.get(15);
        LandmarkProto.NormalizedLandmark ringTip = landmarks.get(19);

        // Calcule a inclinação e a rotação do dedo anelar usando a base e a ponta do dedo
        float pitch = (float) Math.atan2(ringBase.getY() - ringTip.getY(), ringBase.getX() - ringTip.getX());
        float yaw = (float) Math.atan2(ringBase.getZ() - ringTip.getZ(), ringBase.getX() - ringTip.getX());


        return new Vector3(pitch, yaw, rotationDegrees);
    }







}
