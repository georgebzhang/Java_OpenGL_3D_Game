package renderEngine;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

// loads 3D models into memory by storing positional data about the model in a VAO
public class Loader {
	
	// for memory management, store all VAOs and VBOs in Lists, and delete all VAOs and VBOs when closing down game
	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();

	// takes in positions of model's vertices, loads into a VAO, then returns info about the VAO as a variable in RawModel object
	public RawModel loadToVAO(float[] positions) {
		int vaoID = createVAO();
		storeDataInAttributeList(0, positions); // store positional data into attribute list 0 of the VAO
		unbindVAO(); // finished with VAO
		return new RawModel(vaoID, positions.length/3); // positions.length/3 is the number of vertices of the model, 3 since each vertex has 3 floats (x,y,z)
	}
	
	// when closing down game, delete all VAOs and VBOs in Lists vaos and vbos
	public void cleanUp() {
		for (int vao:vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		for (int vbo:vbos) {
			GL15.glDeleteBuffers(vbo);
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
	private void storeDataInAttributeList(int attributeNumber, float[] data) {
		// we have to store data into VAO's attribute lists as VBOs
		int vboID = GL15.glGenBuffers(); // creates empty VBO and returns its ID
		vbos.add(vboID); // toring all VBOs into a List for easy memory management/deleting later
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID); // need to activate (bind) VBO before doing anything to it, GL_ARRAY_BUFFER is a type of VBO
		FloatBuffer buffer = storeDataInFloatBuffer(data); // data has to be stored into a VBO as a FloatBuffer, so we must convert our float array of data into a FloatBuffer of data
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); // stores buffer data into VBO, GL_ARRAY_BUFFER is the type of VBO, 3rd param specifies what the data is going to be used for, GL_STATIC_DRAW indicates that we won't edit the data once it's stored in the VBO
		GL20.glVertexAttribPointer(attributeNumber, 3, GL11.GL_FLOAT, false, 0, 0); // stores VBO into one of VAO's attribute lists, 1st param is the index of VAO to store VBO, 2nd param is length of each vertex (3 for x,y,z), 3rd param is type of data, 4th param specifies whether data is normalized (false for no), 5th param is distance between each of the vertices (any other data between them? in this case no, so 0), 6th param is offset (should we start at beginning of data? yes, so 0)
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // finished using VBO, so unbind it (0 indicates unbinding currently bound VBO)
	}
	
	// must unbind VAO when finished using it
	private void unbindVAO() {
		GL30.glBindVertexArray(0); // 0 indicates unbinding currently bound VAO
	}
	
	// data has to be stored into a VBO as a FloatBuffer, so we must convert our float array of data into a FloatBuffer of data
	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length); // create empty FloatBuffer
		buffer.put(data); // puts float[] data into FloatBuffer
		buffer.flip(); // prepares FloatBuffer to be read from, previously expected to be written to. buffer.flip() indicates that we are finished writing to buffer and are ready to read
		return buffer;
	}
	
}
