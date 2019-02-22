// ALT + SHIFT + Y to enable word wrap in Eclipse

package engineTester;

import org.lwjgl.opengl.Display;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.RawModel;
import renderEngine.Renderer;

public class MainGameLoop {

	public static void main(String[] args) {
		
		DisplayManager.createDisplay();
		
		Loader loader = new Loader();
		Renderer renderer = new Renderer();
		
		float[] vertices = {
				// left bottom triangle
				-0.5f, 0.5f, 0f,
			    -0.5f, -0.5f, 0f,
			    0.5f, -0.5f, 0f,
			    // right top triangle
			    0.5f, -0.5f, 0f,
			    0.5f, 0.5f, 0f,
			    -0.5f, 0.5f, 0f
		};
		
		RawModel model = loader.loadToVAO(vertices); // loads vertices into a VBO, which is loaded into an attribute list in a VAO, whose ID is stored in the returned RawModel model
		
		while(!Display.isCloseRequested()) {
			renderer.prepare(); // called once every frame to prepare OpenGL to render game
			// game logic
			renderer.render(model); // renders model by drawing data from its VAO
			DisplayManager.updateDisplay();
			
		}
		
		loader.cleanUp(); // delete all VAOs and VBOs in Lists vaos and vbos
		
		DisplayManager.closeDisplay();

	}

}
