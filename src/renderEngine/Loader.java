package renderEngine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import models.RawModel;

// loads 3D models into memory by storing positional data about the model in a VAO
public class Loader {
	
	// for memory management, store all VAOs and VBOs in Lists, and delete all VAOs and VBOs when closing down game
	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	// memory management for textures too
	private List<Integer> textures = new ArrayList<Integer>();

	// takes in positions of model's vertices, loads into a VAO, then returns info about the VAO as a variable in RawModel object
	public RawModel loadToVAO(float[] positions, float[] textureCoords, int[] indices) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices); // bind indices VBO to VAO automatically without GL20.glVertexAttribPointer(...), since VBO of type GL_ELEMENT_ARRAY_BUFFER is stored in the VAO's "state vector", note that we bind VBO after binding VAO (in createVao())
		storeDataInAttributeList(0, 3, positions); // store positional data into attribute list 0 of the VAO, 3 is for x,y,z
		storeDataInAttributeList(1, 2, textureCoords); // store texture coordinates data into attribute list 1 of the VAO, 2 is for u,v
		unbindVAO(); // finished with VAO
		//return new RawModel(vaoID, positions.length/3); // positions.length/3 is the number of vertices of the model, 3 since each vertex has 3 floats (x,y,z)
		return new RawModel(vaoID, indices.length); // indices.length is the number of vertices of the model
	}
	
	// load a texture into memory, 1st param is filename of the texture, returns ID of texture
	public int loadTexture(String fileName) {
		Texture texture = null; // Texture from Slick-Utils
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream("res/" + fileName + ".png"));
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file!");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Could not read file!");
			e.printStackTrace();
		}
		int textureID = texture.getTextureID();
		textures.add(textureID); // storing all textures into a List for easy memory management/deleting later
		return textureID;
	}
	
	// when closing down game, delete all VAOs, VBOs, textures in Lists
	public void cleanUp() {
		for (int vao:vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		for (int vbo:vbos) {
			GL15.glDeleteBuffers(vbo);
		}
		for (int texture:textures) {
			GL11.glDeleteTextures(texture);
		}
	}
	
	// create a new VAO for loadToVAO(), returns ID of VAO created
	private int createVAO() {
		int vaoID = GL30.glGenVertexArrays(); // creates empty VAO and returns its ID
		vaos.add(vaoID); // storing all VAOs into a List for easy memory management/deleting later
		GL30.glBindVertexArray(vaoID); // need to activate (bind) VAO before doing anything to it
		return vaoID;
	}
	
	// store data into one of the attribute lists of the VAO (in this case, the attribute list at index attributeNumber of VAO)
	// 2nd param is number of elements per vertex
	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
		// we have to store data into VAO's attribute lists as VBOs
		int vboID = GL15.glGenBuffers(); // creates empty VBO and returns its ID
		vbos.add(vboID); // storing all VBOs into a List for easy memory management/deleting later
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID); // need to activate (bind) VBO before doing anything to it, GL_ARRAY_BUFFER is a type of VBO
		FloatBuffer buffer = storeDataInFloatBuffer(data); // data has to be stored into a VBO as a FloatBuffer, so we must convert our float array of data into a FloatBuffer of data
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); // stores buffer data into VBO, GL_ARRAY_BUFFER is the type of VBO, 3rd param specifies what the data is going to be used for, GL_STATIC_DRAW indicates that we won't edit the data once it's stored in the VBO
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0); // stores VBO into one of VAO's attribute lists, 1st param is the index of VAO to store VBO, 2nd param is number of elements per vertex (3 for x,y,z), 3rd param is type of data, 4th param specifies whether data is normalized (false for no), 5th param is distance between each of the vertices (any other data between them? in this case no, so 0), 6th param is offset (should we start at beginning of data? yes, so 0)
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // finished using VBO, so unbind it (0 indicates unbinding currently bound VBO)
	}
	
	// must unbind VAO when finished using it
	private void unbindVAO() {
		GL30.glBindVertexArray(0); // 0 indicates unbinding currently bound VAO
	}
	
	// load indices buffer and bind to VAO (to use less data in the case of shared vertices among triangles), note that we don't use GL20.glVertexAttribPointer(...), since VBO of type GL_ELEMENT_ARRAY_BUFFER is stored in the VAO's "state vector", note that we bind VBO after binding VAO (in createVAO()), "Don't unbind the index buffer anywhere! Each VAO has one special slot for an index buffer, and unbinding the index buffer will remove it from that slot"
	private void bindIndicesBuffer(int[] indices) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID); // GL_ELEMENT_ARRAY_BUFFER tells OpenGL to use this VBO as an indices buffer
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	// data has to be stored into a VBO as an IntBuffer, so we must convert our int array of data into an IntBuffer of data
	private IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	// data has to be stored into a VBO as a FloatBuffer, so we must convert our float array of data into a FloatBuffer of data
	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length); // create empty FloatBuffer
		buffer.put(data); // puts float[] data into FloatBuffer
		buffer.flip(); // prepares FloatBuffer to be read from, previously expected to be written to. buffer.flip() indicates that we are finished writing to buffer and are ready to read
		return buffer;
	}
}
