package com.example.wearmobile;
import android.content.Context;
import android.view.MotionEvent;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutions.hands.HandLandmark;

import org.rajawali3d.Object3D;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.Renderer;

import java.io.InputStream;
import java.util.List;

public class RingRender extends Renderer {
    private Object3D ring;

    public RingRender(Context context) {
        super(context);
    }

    @Override
    protected void initScene() { //Método que inicia a renderização
        setupModels(); //Chama o método para configurar os modelos
    }

    //Método que configura os modelos, carrega eles, etc..
    private void setupModels() {
        ring = loadModel("thePinkPeachTree/thePinkPeachTree.obj", "thePinkPeachTree/marble_pink_diffuse.jpg");
        getCurrentScene().addChild(ring);
    }

    //Método que recebe strings com o local dos modelos, e devolve um objeto 3D
    private Object3D loadModel(String modelFileName, String textureFileName) {
        int modelResId = mContext.getResources().getIdentifier(modelFileName.split("\\.")[0], "raw", mContext.getPackageName());
        int textureResId = mContext.getResources().getIdentifier(textureFileName.split("\\.")[0], "raw", mContext.getPackageName());
        LoaderOBJ loader = new LoaderOBJ(mContext.getResources(), mTextureManager, modelResId);
        try {
            loader.parse();
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        Object3D model = loader.getParsedObject();

        // Apply texture to the model
        Material material = new Material();
        try {
            material.addTexture(new Texture("texture", textureResId));
        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }
        model.setMaterial(material);

        return model;
    }


    // Atualiza as posições do anel, como sua posição, orientação e escala
    public void updateModelPositions(Vector3 ringPosition, Quaternion ringRotation, float ringScale) {
        ring.setPosition(ringPosition);
        ring.setOrientation(ringRotation);
        ring.setScale(ringScale);
    }

    public Vector3 calculateRingPosition(List<LandmarkProto.Landmark> handLandmarks) {
        // Choose the landmarks for the tip and base of the finger
        LandmarkProto.Landmark tip = handLandmarks.get(HandLandmark.RING_FINGER_TIP);
        LandmarkProto.Landmark base = handLandmarks.get(HandLandmark.RING_FINGER_MCP);

        // Calculate the average position
        float x = (tip.getX() + base.getX()) / 2.0f;
        float y = (tip.getY() + base.getY()) / 2.0f;
        float z = (tip.getZ() + base.getZ()) / 2.0f;

        return new Vector3(x, y, z);
    }


    public Quaternion calculateRingRotation(List<LandmarkProto.Landmark> handLandmarks) {
        LandmarkProto.Landmark tip = handLandmarks.get(HandLandmark.RING_FINGER_TIP);
        LandmarkProto.Landmark base = handLandmarks.get(HandLandmark.RING_FINGER_MCP);

        Vector3 direction = new Vector3(tip.getX() - base.getX(), tip.getY() - base.getY(), tip.getZ() - base.getZ());
        Vector3 up = new Vector3(0, 1, 0);

        return new Quaternion().fromRotationBetween(up, direction);
    }


    public float calculateRingScale(List<LandmarkProto.Landmark> handLandmarks) {
        LandmarkProto.Landmark tip = handLandmarks.get(HandLandmark.RING_FINGER_TIP);
        LandmarkProto.Landmark base = handLandmarks.get(HandLandmark.RING_FINGER_MCP);

        Vector3 tipPosition = new Vector3(tip.getX(), tip.getY(), tip.getZ());
        Vector3 basePosition = new Vector3(base.getX(), base.getY(), base.getZ());

        float distance = (float) basePosition.distanceTo(tipPosition);
        float scaleFactor = 1.0f; // Adjust this value based on your 3D model dimensions

        return distance * scaleFactor;
    }





    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

    }

    @Override
    public void onTouchEvent(MotionEvent event) {

    }
}
