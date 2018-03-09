package entityRenderers;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import entities.Entity;
import interfaces.ICamera;
import models.RawModel;
import models.TexturedModel;
import openglObjects.Vao;
import renderEngine.MasterRenderer;
import scene.SceneEntity;
import scene.Skin;
import shaders.StaticShader;
import sunRenderer.Sun;
import textures.ModelTexture;
import utils.Light;
import utils.Maths;
import utils.OpenGlUtils;

public class EntityRenderer {
	
	private EntityShader shader;
	private StaticShader staticShader;

	public EntityRenderer() {
		this.shader = new EntityShader();
		this.staticShader = new StaticShader();
	}

	public void render(List<SceneEntity> sceneEntities, List<Entity> entities, ICamera camera, Sun sun, Vector4f clipPlane) {
		prepare(camera, sun, clipPlane);
		for(Entity entity:entities) {
			if (!entity.isRenderingEnabled()) {
				continue;
			}
			prepareTexturedModel(entity.getTexModel());
			prepareInstance(entity, camera);
			GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getTexModel().getRawModel().getVertexCount(), 
					GL11.GL_UNSIGNED_INT, 0);
			unbindTexturedModel();
		}
		for (SceneEntity sceneEntity : sceneEntities) {
			prepareSkin(sceneEntity.getSkin());
			Vao model = sceneEntity.getModel().getVao();
			model.bind(0, 1, 2);
			GL11.glDrawElements(GL11.GL_TRIANGLES, model.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
			model.unbind(0, 1, 2);
		}
		finish();
	}

	private void prepareTexturedModel(TexturedModel model) {
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		ModelTexture texture = model.getTexture();
		staticShader.loadNumberOfRows(texture.getNumberOfRows());
		if (texture.hasTransparency()) {
			MasterRenderer.disableCulling();
		}
		staticShader.loadFakeLightingVariable(texture.useFakeLighting());
		staticShader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
		staticShader.loadUseSpecularMap(texture.hasSpecularMap());
		if (texture.hasSpecularMap()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getSpecularMap());
		}
	}

	public void unbindTexturedModel() {
		MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	public void prepareInstance(Entity entity, ICamera camera) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		staticShader.loadTransformationMatrix(transformationMatrix);
		staticShader.loadProjectionMatrix(camera.getProjectionMatrix());
		staticShader.loadViewMatrix(camera);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, entity.getTexModel().getRawModel().getVaoID());
	}

	public void cleanUp(){
		shader.cleanUp();
		staticShader.cleanUp();
	}

	private void prepare(ICamera camera, Sun sun, Vector4f clipPlane) {
		shader.start();
		staticShader.start();
		shader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix());

		// it doesn't really make a difference
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(100, 20, 100), 0, 0, 0, 5);
		shader.loadTransformationMatrix(transformationMatrix);

		shader.lightDirection.loadVec3(sun.getLight().getDirection());
		shader.plane.loadVec4(clipPlane);
		List<Light> lights = new ArrayList<Light>();
		lights.add(sun.getLight());
		staticShader.loadLights(lights);
		OpenGlUtils.antialias(true);
		OpenGlUtils.disableBlending();
		OpenGlUtils.enableDepthTesting(true);
	}

	private void finish() {
		staticShader.stop();
		shader.stop();
	}

	private void prepareSkin(Skin skin) {
		skin.getDiffuseTexture().bindToUnit(0);
		if (skin.hasExtraMap()) {
			skin.getExtraInfoMap().bindToUnit(1);
		}
		shader.hasExtraMap.loadBoolean(skin.hasExtraMap());
		OpenGlUtils.cullBackFaces(!skin.hasTransparency());
	}
}
