package postProcessing;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import gaussianBlur.HorizontalBlur;
import gaussianBlur.VerticalBlur;
import models.RawModel;
import renderEngine.Loader;

public class PostProcessing {

	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;
	private static ContrastChanger contrastChanger;
	private static HorizontalBlur hBlur1;
	private static VerticalBlur   vBlur1;
	private static HorizontalBlur hBlur2;
	private static VerticalBlur   vBlur2;
	private static int BLUR_COEF_1 = 4;
	private static int BLUR_COEF_2 = 8;

	public static void init(Loader loader){
		quad = loader.loadToVAO(POSITIONS, 2);
		contrastChanger = new ContrastChanger();
		int width1 = Display.getWidth();
		int heigt1 = Display.getHeight();
		int width2 = width1;
		int heigt2 = heigt1;
		if (BLUR_COEF_1 > 0 && BLUR_COEF_2 > 0) {
			width1 /= BLUR_COEF_1;
			heigt1 /= BLUR_COEF_1;
			width2 /= BLUR_COEF_2;
			heigt2 /= BLUR_COEF_2;
		}
		hBlur1 = new HorizontalBlur(width1, heigt1);
		vBlur1 = new VerticalBlur(width1, heigt1);
		hBlur2 = new HorizontalBlur(width2, heigt2);
		vBlur2 = new VerticalBlur(width2, heigt2);
	}

	public static void doPostProcessing(int colourTexture){
		start();
		if (BLUR_COEF_1 > 0 || BLUR_COEF_2 > 0) {
			colourTexture = hBlur1.render(colourTexture);
			colourTexture = vBlur1.render(colourTexture);
			colourTexture = hBlur2.render(colourTexture);
			colourTexture = vBlur2.render(colourTexture);
		}
		contrastChanger.render(colourTexture);
		end();
	}

	public static void cleanUp(){
		contrastChanger.cleanUp();
		hBlur1.cleanUp();
		vBlur1.cleanUp();
		hBlur2.cleanUp();
		vBlur2.cleanUp();
	}

	private static void start(){
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	private static void end(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
}
