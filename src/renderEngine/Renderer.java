package renderEngine;

import org.lwjgl.opengl.Display;
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
	private static final float FOV = 70; // field of view
	private static final float NEAR_PLANE = 0.1f; // how close you can see
	private static final float FAR_PLANE = 1000; // how far you can see in the distance
	
	private Matrix4f projectionMatrix;
	
	// only need to load projection matrix once, so do it in constructor
	// need a shader to load projection matrix, so pass it into constructor
	public Renderer(StaticShader shader) {
		createProjectionMatrix();
		shader.start(); // need to start shader program before doing anything to it
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop(); // stop shader program when done
	}
	
	// called once every frame to prepare OpenGL to render game
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST); // tests which triangles are in front of each other
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // clears color and depth buffer from previous frame
		GL11.glClearColor(0, 0, 0, 1); // clear color from last frame, new color is (r,g,b,a)
	}
	
	// renders a RawModel
	//public void render(RawModel model) {
	// renders a TexturedModel
	//public void render(TexturedModel texturedModel) {
	// renders an Entity
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
		GL20.glDisableVertexAttribArray(0); // finished with the attribute list at index 0 of VAO containing vertex positions, 0 indicates index of VAO to disable
		GL20.glDisableVertexAttribArray(1); // finished with attribute list containing textureCoords
		GL30.glBindVertexArray(0); // unbind VAO (0 indicates unbinding currently bound VAO)
	}
	
	private void createProjectionMatrix() {
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE - NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
		
	}
}
