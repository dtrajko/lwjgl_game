package lensFlare;

import textures.Texture;
import utils.MyFile;

public class FlareFactory {

	public static FlareManager createLensFlare() {

		MyFile flareFolder = new MyFile("res", "lensFlare");

		//loading textures for lens flare
		Texture texture1 = Texture.newTexture(new MyFile(flareFolder, "tex1.png")).normalMipMap().create();
		Texture texture2 = Texture.newTexture(new MyFile(flareFolder, "tex2.png")).normalMipMap().create();
		Texture texture3 = Texture.newTexture(new MyFile(flareFolder, "tex3.png")).normalMipMap().create();
		Texture texture4 = Texture.newTexture(new MyFile(flareFolder, "tex4.png")).normalMipMap().create();
		Texture texture5 = Texture.newTexture(new MyFile(flareFolder, "tex5.png")).normalMipMap().create();
		Texture texture6 = Texture.newTexture(new MyFile(flareFolder, "tex6.png")).normalMipMap().create();
		Texture texture7 = Texture.newTexture(new MyFile(flareFolder, "tex7.png")).normalMipMap().create();
		Texture texture8 = Texture.newTexture(new MyFile(flareFolder, "tex8.png")).normalMipMap().create();
		Texture texture9 = Texture.newTexture(new MyFile(flareFolder, "tex9.png")).normalMipMap().create();

		//set up lens flare
		FlareManager lensFlare = new FlareManager(0.16f, 
			new FlareTexture(texture6, 1f),
			new FlareTexture(texture4, 0.46f),
			new FlareTexture(texture2, 0.2f),
			new FlareTexture(texture7, 0.1f),
			new FlareTexture(texture1, 0.04f),
			new FlareTexture(texture3, 0.12f),
			new FlareTexture(texture9, 0.24f),
			new FlareTexture(texture5, 0.14f),
			new FlareTexture(texture1, 0.024f),
			new FlareTexture(texture7, 0.4f),
			new FlareTexture(texture9, 0.2f),
			new FlareTexture(texture3, 0.14f),
			new FlareTexture(texture5, 0.6f),
			new FlareTexture(texture4, 0.8f),
			new FlareTexture(texture8, 1.2f));

		return lensFlare;
	}	
}
