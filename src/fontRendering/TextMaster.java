package fontRendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontMeshCreator.TextMeshData;
import loaders.RawModelLoader;
import loaders.SceneLoader;
import racetrack.LapStopwatch;
import utils.DisplayManager;

public class TextMaster {
	
	private static RawModelLoader loader;
	private static Map<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();
	private static FontRenderer renderer;
	
	private static int currentlapTime = 0;
	private static int previousLapTime = -1;
	private static LapStopwatch stopwatch = null;
	private static List<GUIText> guiTexts;
	private static int currentUpdateTime = 0;
	private static int previousUpdateTime = -1;

	public static void init(RawModelLoader rawModelLoader) {
		renderer = new FontRenderer();
		loader = rawModelLoader;
		guiTexts = new ArrayList<GUIText>();
	}

	public static void render() {
		renderer.render(texts);
	}

	
	public static GUIText getGuiText(int index) {
		return guiTexts.get(index);
	}

	public static void setGuiText(int index, GUIText newGuiText) {
		guiTexts.add(index, newGuiText);
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

	public static void update() {
		// make sure that text is updated once per second
		currentUpdateTime = DisplayManager.getCurrentTimeSeconds();
		if (currentUpdateTime != previousUpdateTime) {
			updateLapInfo();
		}
		previousUpdateTime = currentUpdateTime;
	}

	public static void updateLapInfo() {
		if (stopwatch == null) {
			stopwatch = SceneLoader.getScene().getRacetrack().getStopwatch();
		}
		String textInfoString = 
			"Lap: " + stopwatch.getLapCount() + "    " + 
			"Lap time: " + stopwatch.getLapTime() + "    " +
			"Best lap: " + stopwatch.getBestLap();
		GUIText guiText = guiTexts.get(0);
		guiText.setTextString(textInfoString);
		loadText(guiText);
	}
}
