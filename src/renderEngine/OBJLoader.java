package renderEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;

public class OBJLoader {
	// takes in .obj file, loads its data into VAO, returns it as RawModel
	// Loader is needed to load data into VAO
	public static RawModel loadObjModel(String fileName, Loader loader) {
		// open .obj file
		FileReader fr = null;
		try {
			fr = new FileReader(new File("res/" + fileName + ".obj"));
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file!");
			e.printStackTrace();
		}
		// read .obj file and store data
		BufferedReader reader = new BufferedReader(fr);
		String line;
		List<Vector3f> vertices = new ArrayList<Vector3f>(); // (x,y,z)
		List<Vector2f> textures = new ArrayList<Vector2f>(); // (u,v)
		List<Vector3f> normals = new ArrayList<Vector3f>(); // (x,y,z)
		List<Integer> indices = new ArrayList<Integer>(); // does not get added to until we reach "f "
		// eventually we need all data in float array for Loader to load to VAO
		float[] verticesArray = null;
		float[] normalsArray = null;
		float[] textureArray = null;
		int[] indicesArray = null;
		try {
			while (true) {
				line = reader.readLine();
				String[] currentLine = line.split(" ");
				if (line.startsWith("v ")) {
					// "v" stands for "vertex"
					// "v {x} {y} {z}"
					Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
					vertices.add(vertex);
				} else if (line.startsWith("vt ")) {
					// "t" stands for "texture"
					// "vt {u} {v}"
					Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]));
					textures.add(texture);
				} else if (line.startsWith("vn ")) {
					// "n" stands for "normal"
					// "vn {x} {y} {z}"
					Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
					normals.add(normal);
				} else if (line.startsWith("f ")) {
					// "f" stands for "face", meaning each line represents a triangular face
					// "f {Va/Tb/Nc} {Vd/Te/Nf} {Vg/Th/Ni}", where a,b,c,d,e,f,g,h,i are indices
					// vertices.size() = textures.size() = normals.size() now
					textureArray = new float[vertices.size()*2];
					normalsArray = new float[vertices.size()*3];
					break; // this block only runs once, exits to next while loop
				}
			}
			while (line != null) {
				if (!line.startsWith("f ")) {
					line = reader.readLine();
					continue;
				}
				String[] currentLine = line.split(" ");
				// [Va, Tb, Nc] indicates vertex position (x,y,z) at index a, texture coordinates (u,v) at index b, normal at index c belong to the same vertex. A vertex is not just an (x,y,z) position, but rather all of the data corresponding to that (x,y,z) position including color or texture coordinates, normals, etc...
				String[] vertex1 = currentLine[1].split("/"); // [Va, Tb, Nc]
				String[] vertex2 = currentLine[2].split("/"); // [Vd, Te, Nf]
				String[] vertex3 = currentLine[3].split("/"); // [Vg, Th, Ni]
				
				processVertex(vertex1, indices, textures, normals, textureArray, normalsArray);
				processVertex(vertex2, indices, textures, normals, textureArray, normalsArray);
				processVertex(vertex3, indices, textures, normals, textureArray, normalsArray);
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		verticesArray = new float[vertices.size()*3];
		indicesArray = new int[indices.size()];
		
		int vertexPointer = 0;
		for (Vector3f vertex:vertices) {
			verticesArray[vertexPointer++] = vertex.x;
			verticesArray[vertexPointer++] = vertex.y;
			verticesArray[vertexPointer++] = vertex.z;
		}
		
		for (int i = 0; i < indices.size(); i++) {
			indicesArray[i] = indices.get(i);
		}
		
		return loader.loadToVAO(verticesArray, textureArray, normalsArray, indicesArray);
	}
	
	// We assume that vertices are "in order", because logically we can assume that one of the sets is in order... However, textures and normals are not in the same order as vertices
	private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures, List<Vector3f> normals, float[] textureArray, float[] normalsArray) {
		int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1; // -1 since .obj files start at 1, arrays start at 0
		indices.add(currentVertexPointer);
		Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1]) - 1); // -1 since .obj files start at 1, arrays start at 0
		textureArray[currentVertexPointer*2] = currentTex.x;
		textureArray[currentVertexPointer*2 + 1] = 1 - currentTex.y; // 1 - since OpenGL starts at top left of texture, Blender starts at bottom left
		Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
		normalsArray[currentVertexPointer*3] = currentNorm.x;
		normalsArray[currentVertexPointer*3 + 1] = currentNorm.y;
		normalsArray[currentVertexPointer*3 + 2] = currentNorm.z;
	}
}
