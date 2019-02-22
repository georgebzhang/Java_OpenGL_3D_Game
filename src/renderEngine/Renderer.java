package renderEngine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import shaders.StaticShader;
import toolbox.Maths;

// Now that we can load a model into a VAO, we need a class to render the model from the VAO, i.e. Renderer class
public class Renderer {
	// called once every frame to prepare OpenGL to render game
	public void prepare() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT); // clears color from previous frame
		GL11.glClearColor(1, 0, 0, 1); // clear color from last frame, new color is (r,g,b,a)
	}
	
	// renders a RawModel
	//public void render(RawModel model) {
	// renders a TexturedModel
	//public void render(TexturedModel texturedModel) {
	// renderes a Entity
	public void render(Entity entity, StaticShader shader) {
		TexturedModel model = entity.getModel();
		//RawModel model = texturedModel.getRawModel();
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID()); // need to activate (bind) VAO before doing anything to it, model.getVaoID() returns the VAO that the RawModel model has been loaded to
		GL20.glEnableVertexAttribArray(0); // need to activate attribute list of VAO with the data, in this case we stored a VBO containing positional data in the attribute list at index 0 of the VAO
		GL20.glEnableVertexAttribArray(1); // need to activate attribute list 1 of VAO with textureCoords
		
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		
		// should tell OpenGL which texture we want to render by putting it in a texture banks provided by OpenGL (works without this, since sampler2D in fragmentShader uses texture bank GL_TEXTURE0 by default?)
		GL13.glActiveTexture(GL13.GL_TEXTURE0); // activate the first texture bank
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
		
		// GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount()); // renders model, GL_TRIANGLES to render as triangles, 2nd param specifies where in the data to start rendering from (beginning, so 0), 3rd param is the number of vertices
		GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0); // 3rd param is type (giving indices buffer, so GL_UNSIGNED_INT), 4th param specifies where in the data to start rendering from (beginning, so 0)
		GL20.glDisableVertexAttribArray(0); // finished with the attribute list at index 0 of VAO, 0 indicates index of VAO to disable
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0); // unbind VAO (0 indicates unbinding currently bound VAO)
	}
}
