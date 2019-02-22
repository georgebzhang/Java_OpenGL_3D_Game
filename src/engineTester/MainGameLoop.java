// ALT + SHIFT + Y to enable word wrap in Eclipse

package engineTester;

import org.lwjgl.opengl.Display;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.RawModel;
import renderEngine.Renderer;
import shaders.StaticShader;

public class MainGameLoop {

	public static void main(String[] args) {
		
		DisplayManager.createDisplay();
		
		Loader loader = new Loader();
		Renderer renderer = new Renderer();
		StaticShader shader = new StaticShader();
		
		float[] vertices = {
				-0.5f, 0.5f, 0f, // V0
			    -0.5f, -0.5f, 0f, // V1
			    0.5f, -0.5f, 0f, // V2
			    0.5f, 0.5f, 0f, // V3
		};
		
		int[] indices = {
				0,1,3, // top left triangle (V0, V1, V3)
				3,1,2 // bottom right triangle (V3, V1, V2)
		};
		
		RawModel model = loader.loadToVAO(vertices, indices); // loads vertices into a VBO, which is loaded into an attribute list in a VAO, whose ID is stored in the returned RawModel model
		
		while(!Display.isCloseRequested()) {
			// game logic
			renderer.prepare(); // called once every frame to prepare OpenGL to render game
			shader.start(); // start program to use it
			renderer.render(model); // renders model by drawing data from its VAO
			shader.stop(); // done using program
			DisplayManager.updateDisplay();
		}
		
		shader.cleanUp(); // detach and delete vertex and fragment shaders, delete program 
		loader.cleanUp(); // delete all VAOs and VBOs in Lists vaos and vbos
		DisplayManager.closeDisplay();
	}
}
