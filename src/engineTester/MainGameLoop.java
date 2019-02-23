// ALT + SHIFT + Y to enable word wrap in Eclipse

package engineTester;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.OBJLoader;
import renderEngine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

public class MainGameLoop {

	public static void main(String[] args) {
		
		DisplayManager.createDisplay();
		
		Loader loader = new Loader();
		StaticShader shader = new StaticShader();
		Renderer renderer = new Renderer(shader);
		
		// RawModel model = loader.loadToVAO(vertices, textureCoords, indices); // loads vertices into a VBO, which is loaded into an attribute list in a VAO, whose ID is stored in the returned RawModel model
		RawModel model = OBJLoader.loadObjModel("dragon", loader);
		ModelTexture texture = new ModelTexture(loader.loadTexture("stallTexture"));
		TexturedModel staticModel = new TexturedModel(model, texture);
		Entity entity = new Entity(staticModel, new Vector3f(0,0,-50), 0,0,0, 1); // rendering Vector3f(...) with scale 1
		Light light = new Light(new Vector3f(0,0,-20), new Vector3f(1,1,1)); // 1st param is position, 2nd param is rgb color
		Camera camera = new Camera();
		
		while(!Display.isCloseRequested()) {
			entity.increaseRotation(0,1,0); // increase rotation along x and y axes
			camera.move();
			renderer.prepare(); // called once every frame to prepare OpenGL to render game
			shader.start(); // start program to use it
			shader.loadLight(light); // load light to shader
			shader.loadViewMatrix(camera); // moves world in opposite direction camera to simulate camera movement (every frame)
			//renderer.render(model); // renders model by drawing data from its VAO
			renderer.render(entity, shader);
			shader.stop(); // done using program
			DisplayManager.updateDisplay();
		}
		
		shader.cleanUp(); // detach and delete vertex and fragment shaders, delete program 
		loader.cleanUp(); // delete all VAOs and VBOs in Lists vaos and vbos
		DisplayManager.closeDisplay();
	}
}
