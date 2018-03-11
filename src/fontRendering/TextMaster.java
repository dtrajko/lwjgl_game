package fontRendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontMeshCreator.TextMeshData;
import loaders.RawModelLoader;

public class TextMaster {
	
	private static RawModelLoader loader;
	private static Map<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();
	private static FontRenderer renderer;

	public static void init(RawModelLoader rawModelLoader) {
		renderer = new FontRenderer();
		loader = rawModelLoader;
	}

	public static void render() {
		renderer.render(texts);
	}

	public static void loadText(GUIText text) {
		FontType font = text.getFont();
		TextMeshData textMeshData = font.loadText(text);
		int vao = loader.loadToVAO(textMeshData.getVertexPositions(), textMeshData.getTextureCoords());
		text.setMeshInfo(vao, textMeshData.getVertexCount());
		List<GUIText> textBatch = texts.get(font);
		if (textBatch == null) {
			textBatch = new ArrayList<GUIText>();
			texts.put(font, textBatch);
		}
		textBatch.add(text);
	}

	public static void removeText(GUIText text) {
		List<GUIText> textBatch = texts.get(text.getFont());
		textBatch.remove(text);
		if (textBatch.isEmpty()) {
			texts.remove(text.getFont());
		}
	}

	public static void cleanUp() {
		renderer.cleanUp();
	}
}
