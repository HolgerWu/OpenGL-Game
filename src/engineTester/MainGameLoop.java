package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
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

public class MainGameLoop {
    /**
     * @param args
     */

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader();
        StaticShader shader = new StaticShader();
        Renderer renderer = new Renderer(shader);

        RawModel model = OBJLoader.loadObjModel("dragon",loader);
        TexturedModel staticModel = null;
        staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("stallTexture")));
        ModelTexture texture = staticModel.getTexture();
        texture.setShineDamper(10);
        texture.setReflectivity(1);

        //WARNING: if this Vector3f been inited as new Vector3f(0,0,0), there will be nothing to be seen.
        Entity entity = new Entity(staticModel,new Vector3f(0,0,-20),0,0,0,0.5f);
        Light light = new Light(new Vector3f(20,20,0),new Vector3f(1,1,1));

        Camera camera = new Camera();

        while(!Display.isCloseRequested()){
            //game logic
            entity.increaseRotation(0.1f,0,0);
            camera.move();
            renderer.prepare();
            shader.start();
            shader.loadLight(light);
            shader.loadViewMatrix(camera);
            renderer.render(entity,shader);
            shader.stop();
            DisplayManager.updateDisplay();
        }

        shader.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();

    }
}
