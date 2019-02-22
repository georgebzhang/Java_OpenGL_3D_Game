package renderEngine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

// Now that we can load a model into a VAO, we need a class to render the model from the VAO, i.e. Renderer class
public class Renderer {

	// called once every frame to prepare OpenGL to render game
	public void prepare() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT); // clears color from previous frame
		GL11.glClearColor(1, 0, 0, 1); // clear color from last frame, new color is (r,g,b,a)
	}
	
	// renders a RawModel
	public void render(RawModel model) {
		GL30.glBindVertexArray(model.getVaoID()); // need to activate (bind) VAO before doing anything to it, model.getVaoID() returns the VAO that the RawModel model has been loaded to
		GL20.glEnableVertexAttribArray(0); // need to activate attribute list of VAO with the data, in this case we stored a VBO containing positional data in the attribute list at index 0 of the VAO
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount()); // renders model, GL_TRIANGLES to render as triangles, 2nd param specifies where in the data to start rendering from (beginning, so 0), 3rd param is the number of vertices
		GL20.glDisableVertexAttribArray(0); // finished with the attribute list at index 0 of VAO, 0 indicates index of VAO to disable
		GL30.glBindVertexArray(0); // unbind VAO (0 indicates unbinding currently bound VAO)
	}
	
}
