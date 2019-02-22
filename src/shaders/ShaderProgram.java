package shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

// represents generic shader program containing all attributes and methods every shader program must have
public abstract class ShaderProgram {
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16); // FloatBuffer we can reuse every time we want to load a 4x4 matrix to a uniform location
	
	public ShaderProgram(String vertexFile, String fragmentFile) {
		// get IDs of vertex shader and fragment shader
		vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		// program ties vertex shader and fragment shader together
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		bindAttributes(); // make sure bindAttributes() is called before program is linked
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		getAllUniformLocations();
	}
	
	// makes sure all shader program classes have a method to get all uniform locations
	protected abstract void getAllUniformLocations();
	
	// gets location (int) of a uniform variable in shader code
	protected int getUniformLocation(String uniformName) {
		return GL20.glGetUniformLocation(programID, uniformName);
	}
	
	// have to start program in order to use it
	public void start() {
		GL20.glUseProgram(programID);
	}
	
	// stop program when no longer in use
	public void stop() {
		GL20.glUseProgram(0); // 0 to stop using current program
	}
	
	// memory management
	public void cleanUp() {
		stop(); // check that no program is currently running
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteProgram(programID);
	}
	
	// classes implementing ShaderProgram must define bindAttributes()
	// links inputs to shader programs to one of the attributes of the VAO
	protected abstract void bindAttributes();
	
	// methods to load values to uniform locations
	protected void loadFloat(int location, float value) {
		GL20.glUniform1f(location, value);
	}
	
	protected void loadVector(int location, Vector3f value) {
		GL20.glUniform3f(location, value.x, value.y, value.z);
	}
	
	protected void loadBoolean(int location, boolean value) {
		float toLoad = 0;
		if (value)
			toLoad = 1;
		GL20.glUniform1f(location, toLoad);
	}
	
	protected void loadMatrix(int location, Matrix4f matrix) {
		matrix.store(matrixBuffer); // stores Matrix4f into FloatBuffer
		matrixBuffer.flip(); // switch from write to read
		GL20.glUniformMatrix4(location, false, matrixBuffer); // 2nd param is transpose
	}
	
	// need this method, cannot be done outside this class since programID is private
	// binds attribute list at index attribute of currently bound VAO to variable name in shader code
	protected void bindAttribute(int attribute, String variableName) {
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}
	
	// load shader source code files, 1st param is filename of source code file, 2nd param indicates whether shader is vertex or fragment shader
	private static int loadShader(String file, int type) {
		StringBuilder shaderSource = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
		} catch (IOException e) {
			System.err.println("Could not read file!");
			e.printStackTrace();
			System.exit(-1);
		}
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		if (GL20.glGetShader(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader!");
			System.exit(-1);
		}
		return shaderID;
	}
}
