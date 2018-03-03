package openglObjects;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import utils.DataUtils;

public class Vao {
	
	private static final int BYTES_PER_FLOAT = 4;
	private static final int BYTES_PER_INT = 4;
	public final int id;
	private List<Vbo> dataVbos = new ArrayList<Vbo>();
	private List<Vbo> relatedVbos = new ArrayList<Vbo>();
	private List<Attribute> attributes = new ArrayList<Attribute>();
	private Vbo dataVbo;
	private Vbo indexVbo;
	private int indexCount;

	public static Vao create() {
		int id = GL30.glGenVertexArrays();
		return new Vao(id);
	}

	private Vao(int id) {
		this.id = id;
	}
	
	public int getIndexCount(){
		return indexCount;
	}

	public void bind(int... attributes){
		bind();
		for (int i : attributes) {
			GL20.glEnableVertexAttribArray(i);
		}
	}

	public void unbind(int... attributes){
		for (int i : attributes) {
			GL20.glDisableVertexAttribArray(i);
		}
		unbind();
	}
	
	public void createIndexBuffer(int[] indices){
		this.indexVbo = Vbo.create(GL15.GL_ELEMENT_ARRAY_BUFFER);
		indexVbo.bind();
		indexVbo.storeData(indices);
		this.indexCount = indices.length;
	}

	public Vbo createIndexBuffer(IntBuffer indices) {
		this.indexVbo = Vbo.create(GL15.GL_ELEMENT_ARRAY_BUFFER, GL15.GL_STATIC_DRAW);
		indexVbo.allocateData(indices.limit() * DataUtils.BYTES_IN_INT);
		indexVbo.storeData(0, indices);
		return indexVbo;
	}

	public void createAttribute(int attribute, float[] data, int attrSize){
		Vbo dataVbo = Vbo.create(GL15.GL_ARRAY_BUFFER);
		dataVbo.bind();
		dataVbo.storeData(data);
		GL20.glVertexAttribPointer(attribute, attrSize, GL11.GL_FLOAT, false, attrSize * BYTES_PER_FLOAT, 0);
		dataVbo.unbind();
		dataVbos.add(dataVbo);
	}

	public Vbo initDataFeed(ByteBuffer data, int usage, Attribute... newAttributes) {
		int bytesPerVertex = getVertexDataTotalBytes(newAttributes);
		Vbo vbo = Vbo.create(GL15.GL_ARRAY_BUFFER, usage);
		relatedVbos.add(vbo);
		vbo.allocateData(data.limit());
		vbo.storeData(0, data);
		linkAttributes(bytesPerVertex, newAttributes);
		vbo.unbind();
		return vbo;
	}

	private void linkAttributes(int bytesPerVertex, Attribute... newAttributes) {
		int offset = 0;
		for (Attribute attribute : newAttributes) {
			attribute.link(offset, bytesPerVertex);
			offset += attribute.bytesPerVertex;
			attribute.enable(true);
			attributes.add(attribute);
		}
	}

	private int getVertexDataTotalBytes(Attribute... newAttributes) {
		int total = 0;
		for (Attribute attribute : newAttributes) {
			total += attribute.bytesPerVertex;
		}
		return total;
	}

	public void createIntAttribute(int attribute, int[] data, int attrSize){
		Vbo dataVbo = Vbo.create(GL15.GL_ARRAY_BUFFER);
		dataVbo.bind();
		dataVbo.storeData(data);
		GL30.glVertexAttribIPointer(attribute, attrSize, GL11.GL_INT, attrSize * BYTES_PER_INT, 0);
		dataVbo.unbind();
		dataVbos.add(dataVbo);
	}
	
	public void delete() {
		GL30.glDeleteVertexArrays(id);
		for(Vbo vbo : dataVbos){
			vbo.delete();
		}
		indexVbo.delete();
	}

	private void bind() {
		GL30.glBindVertexArray(id);
	}

	private void unbind() {
		GL30.glBindVertexArray(0);
	}

	public void storeData(int vertexCount, float[]... data) {
		float[] interleavedData = interleaveFloatData(vertexCount, data);
		int[] lengths = getAttributeLengths(data, vertexCount);
		storeInterleavedData(interleavedData, lengths);
	}

	private float[] interleaveFloatData(int count, float[]... data) {
		int totalSize = 0;
		int[] lengths = new int[data.length];
		for (int i = 0; i < data.length; i++) {
			int elementLength = data[i].length / count;
			lengths[i] = elementLength;
			totalSize += data[i].length;
		}
		float[] interleavedBuffer = new float[totalSize];
		int pointer = 0;
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < data.length; j++) {
				int elementLength = lengths[j];
				for (int k = 0; k < elementLength; k++) {
					interleavedBuffer[pointer++] = data[j][i * elementLength + k];
				}
			}
		}
		return interleavedBuffer;
	}

	private int[] getAttributeLengths(float[][] data, int vertexCount){
		int[] lengths = new int[data.length];
		for (int i = 0; i < data.length; i++) {
			lengths[i] = data[i].length / vertexCount;
		}
		return lengths;
	}

	private void storeInterleavedData(float[] data, int... lengths) {
		dataVbo = Vbo.create(GL15.GL_ARRAY_BUFFER);
		dataVbo.bind();
		dataVbo.storeData(data);
		int bytesPerVertex = calculateBytesPerVertex(lengths);
		linkVboDataToAttributes(lengths, bytesPerVertex);
		dataVbo.unbind();
	}

	private int calculateBytesPerVertex(int[] lengths){
		int total = 0;
		for (int i = 0; i < lengths.length; i++) {
			total += lengths[i];
		}
		return BYTES_PER_FLOAT * total;
	}

	private void linkVboDataToAttributes(int[] lengths, int bytesPerVertex){
		int total = 0;
		for (int i = 0; i < lengths.length; i++) {
			GL20.glVertexAttribPointer(i, lengths[i], GL11.GL_FLOAT, false, bytesPerVertex, BYTES_PER_FLOAT * total);
			total += lengths[i];
		}
	}

	public void storeData(int[] indices, int vertexCount, float[]... data){
		bind();
		storeData(vertexCount, data);
		createIndexBuffer(indices);
		unbind();
	}
}
