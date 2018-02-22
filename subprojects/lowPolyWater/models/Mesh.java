package models;

import renderEngine.Loader;

public class Mesh {
	
	private static final int VERTEX_COUNT = 100;
	private static final float SIZE = 0.11f;
	
	private int vao;
	private int indexCount = 0;
	
	public Mesh(int vaoID, int indexCount){
		this.vao = vaoID;
		this.indexCount = indexCount;
	}

	public int getVaoID() {
		return vao;
	}

	public int getIndexCount() {
		return indexCount;
	}
	
	public static Mesh create(Loader loader){
		float[] positions = generatePositions();
		int[] indices = generateIndices();
		return loader.loadData(positions, indices);
	}
	
	private static float[] generatePositions(){
		float[] positions = new float[VERTEX_COUNT * VERTEX_COUNT * 2];
		int pointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				positions[pointer++] = j * SIZE;
				positions[pointer++] = i * SIZE;
			}
		}
		return positions;
	}
	
	private static int[] generateIndices(){
		int pointer = 0;
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return indices;
	}
	

}
