package main;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import extra.Camera;
import loaders.LoaderSettings;
import loaders.SceneLoader;
import loaders.SceneLoaderFactory;
import renderEngine.RenderEngine;
import scene.Scene;
import utils.MyFile;

public class MainApp {

	public static void main(String[] args) {

		RenderEngine engine = RenderEngine.init();
		SceneLoader loader = SceneLoaderFactory.createSceneLoader();
		Scene scene = loader.loadScene(new MyFile(LoaderSettings.RES_FOLDER, "Socuwan Scene"));

		engine.renderEnvironmentMap(scene.getEnvironmentMap(), scene, new Vector3f(0, 2, 0));

		while(!Display.isCloseRequested()){
			((Camera) scene.getCamera()).move();
			engine.renderScene(scene);
			engine.update();
		}

		scene.delete();
		engine.close();
		
	}
}
