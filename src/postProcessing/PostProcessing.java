package postProcessing;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import bloom.BrightFilter;
import bloom.CombineFilter;
import gaussianBlur.HorizontalBlur;
import gaussianBlur.VerticalBlur;
import models.RawModel;
import renderEngine.Loader;

public class PostProcessing {

	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;
	private static ContrastChanger contrastChanger;
	private static BrightFilter brightFilter;
	private static CombineFilter combineFilter;

	private static HorizontalBlur hBlur1;
	private static VerticalBlur   vBlur1;
	private static HorizontalBlur hBlur2;
	private static VerticalBlur   vBlur2;
	private static int BLUR_COEF_1 = 0;
	private static int BLUR_COEF_2 = 0;
	private static int BRIGHT_COEF = 0;

	public static void init(Loader loader){
		quad = loader.loadToVAO(POSITIONS, 2);
		contrastChanger = new ContrastChanger();
		combineFilter = new CombineFilter();

		// bright filter
		int widthBright = Display.getWidth();
		int heigtBright = Display.getHeight();
		if (BRIGHT_COEF > 0) {
			widthBright /= BRIGHT_COEF;
			heigtBright /= BRIGHT_COEF;
		}
		brightFilter = new BrightFilter(widthBright, heigtBright);

		// blur filter
		int widthBlur1 = Display.getWidth();
		int heigtBlur1 = Display.getHeight();
		int widthBlur2 = widthBlur1;
		int heigtBlur2 = heigtBlur1;
		if (BLUR_COEF_1 > 0 && BLUR_COEF_2 > 0) {
			widthBlur1 /= BLUR_COEF_1;
			heigtBlur1 /= BLUR_COEF_1;
			widthBlur2 /= BLUR_COEF_2;
			heigtBlur2 /= BLUR_COEF_2;
		}
		hBlur1 = new HorizontalBlur(widthBlur1, heigtBlur1);
		vBlur1 = new VerticalBlur(widthBlur1, heigtBlur1);
		hBlur2 = new HorizontalBlur(widthBlur2, heigtBlur2);
		vBlur2 = new VerticalBlur(widthBlur2, heigtBlur2);
	}

	public static void doPostProcessing(int colourTexture, int brightTexture){
		start();
		if (BLUR_COEF_1 > 0 || BLUR_COEF_2 > 0) {
			brightTexture = hBlur1.render(brightTexture);
			brightTexture = vBlur1.render(brightTexture);
			brightTexture = hBlur2.render(brightTexture);
			brightTexture = vBlur2.render(brightTexture);
		}
		colourTexture = brightFilter.render(colourTexture);
		contrastChanger.render(colourTexture);
		combineFilter.render(colourTexture, brightTexture); // disable brightTexture
		end();
	}

	public static void cleanUp(){
		contrastChanger.cleanUp();
		brightFilter.cleanUp();
		combineFilter.cleanUp();
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
