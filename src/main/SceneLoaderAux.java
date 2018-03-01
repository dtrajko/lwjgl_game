package main;

import animation.Animation;
import entities.Player;
import loaders.AnimatedModelLoader;
import loaders.AnimationLoader;
import loaders.LoaderSettings;
import loaders.SkyboxLoader;
import scene.ICamera;
import scene.Scene;
import skybox.Skybox;
import utils.MyFile;

public class SceneLoaderAux {

	/**
	 * Sets up the scene. Loads the entity, load the animation, tells the entity
	 * to do the animation, sets the light direction, creates the camera, etc...
	 * 
	 * @param resFolder
	 *            - the folder containing all the information about the animated entity
	 *            (mesh, animation, and texture info).
	 * @return The entire scene.
	 */
	public static Scene loadScene(MyFile resFolder) {
		Player animatedPlayer = AnimatedModelLoader.loadPlayer(new MyFile(resFolder, GeneralSettings.MODEL_FILE),
				new MyFile(resFolder, GeneralSettings.DIFFUSE_FILE));
		Animation animation = AnimationLoader.loadAnimation(new MyFile(resFolder, GeneralSettings.ANIM_FILE));
		animatedPlayer.doAnimation(animation);

		SkyboxLoader skyLoader = new SkyboxLoader();
		MyFile sceneFile = new MyFile(LoaderSettings.RES_FOLDER, "Socuwan Scene");
		Skybox sky = skyLoader.loadSkyBox(new MyFile(sceneFile, LoaderSettings.SKYBOX_FOLDER));
		Scene scene = new Scene(animatedPlayer, sky);
		scene.setLightDirection(GeneralSettings.LIGHT_DIR);
		return scene;
	}
}
