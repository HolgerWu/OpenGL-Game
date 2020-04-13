package engineTester;

import entities.Camera;
import entities.Entity;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import models.RawModel;
import renderEngine.OBJLoader;
import renderEngine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

import java.io.FileNotFoundException;

public class MainGameLoop {
    /**
     * @param args
     */

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader();
        StaticShader shader = new StaticShader();
        Renderer renderer = new Renderer(shader);

        RawModel model = OBJLoader.loadObjModel("stall",loader);

        ModelTexture texture = null;
        texture = new ModelTexture(loader.loadTexture("stallTexture"));
        TexturedModel staticModel = null;
        staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("stallTexture")));

        //WARNING: if this Vector3f been inited as new Vector3f(0,0,0), there will be nothing to be seen.
        Entity entity = new Entity(staticModel,new Vector3f(0,0,-10),0,0,0,1);

        Camera camera = new Camera();

        while(!Display.isCloseRequested()){
            //game logic
//            entity.increasePosition(0,0,-0.01f);
            entity.increaseRotation(1,0,0);
            camera.move();
            renderer.prepare();
            shader.start();
            shader.loadviewMatrix(camera);
            renderer.render(entity,shader);
            shader.stop();
            DisplayManager.updateDisplay();
        }

        shader.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();

    }
}
