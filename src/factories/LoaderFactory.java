package factories;

import loaders.RawModelLoader;

public class LoaderFactory {

	private static RawModelLoader rawModelLoader = null;

	public static RawModelLoader getRawModelLoader() {
		if (rawModelLoader == null) {
			rawModelLoader = new RawModelLoader();
		}
		return rawModelLoader;
	}
}
