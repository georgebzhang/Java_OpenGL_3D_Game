package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {
	
	private static final int WIDTH = 1280;
	private static final int HEIGHT = 720;
	private static final int FPS_CAP = 120;
	
	public static void createDisplay() {
		
		ContextAttribs attribs = new ContextAttribs(3,2); // 3,2 for 3.2 (version of OpenGL)
		attribs.withForwardCompatible(true); // not important, just settings
		attribs.withProfileCore(true); // not important, just settings
		
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat(), attribs);
			Display.setTitle("Our First Display");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0, 0, WIDTH, HEIGHT); // tell OpenGL where (whole display) in the display to render the game. (0, 0) is bottom left of display, (WIDTH, HEIGHT) is top right of display.
		
	}
	
	public static void updateDisplay() {
		
		Display.sync(FPS_CAP); // synchronizes game to run at steady fps
		Display.update();
		
	}
	
	public static void closeDisplay() {
		
		Display.destroy();
		
	}

}
