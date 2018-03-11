package factories;

import fontMeshCreator.FontType;

public class FontFactory {

	private static FontType candara = null;

	public static FontType getFont(String fontName) {
		FontType font = null;
		switch(fontName) {
			case "candara":
				if (candara == null) {
					candara = new FontType(LoaderFactory.getRawModelLoader().loadTexture(fontName, 0), fontName);
				}
				font = candara;	
				break;
		}
		return font;
	}
}
