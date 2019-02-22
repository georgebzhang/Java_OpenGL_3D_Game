package shaders;

public class StaticShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "src/shaders/vertexShader.txt";
	private static final String FRAGMENT_FILE = "src/shaders/fragmentShader.txt";

	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position"); // bind attribute (at index) 0 of VAO to position input in vertexShader
		super.bindAttribute(1, "textureCoords"); // bind attribute (at index) 1 of VAO to textureCoords input in vertexShader
	}
	
}
