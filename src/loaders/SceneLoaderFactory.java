package loaders;

public class SceneLoaderFactory {

	public static SceneLoader createSceneLoader() {
		/*
		 * Would be useful if there were various loaders to choose from, or if
		 * the loaders had settings. For example, if there were different model
		 * formats supported a different type of ModelLoader could be created
		 * here. Kind of pointless and excessive in this case, but I was doing
		 * it this way as an exercise for myself :P
		 */
		ConfigsLoader  configsLoader = new ConfigsLoader();
		ModelLoader    modelLoader = new ModelLoader();
		RawModelLoader rawModelLoader = new RawModelLoader();
		SkinLoader     skinLoader = new SkinLoader();
		EntityLoader   entityLoader = new EntityLoader(modelLoader, skinLoader, configsLoader);
		SkyboxLoader   skyLoader = new SkyboxLoader();
		return new SceneLoader(entityLoader, skyLoader, rawModelLoader);
	}
}
