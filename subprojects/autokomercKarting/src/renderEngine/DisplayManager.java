package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {

	private static final int WIDTH = 1280; // 1920, 1280, 640
	private static final int HEIGHT = 720; // 1080, 720, 480
	private static final int WIDTH_FHD = 1920;
	private static final int HEIGHT_FHD = 1080;
	private static final int FPS_CAP = 120;
	private static boolean fullscreenMode = false;

	private static long lastFrameTime;
	private static float delta;

	private static int displayFps;
	private static int currentFps;
	private static long lastFPS;
	
	private static String title = "LWJGL Game";

	public static void startFPS() {
		lastFPS = getCurrentTime();
	}

	public static void updateFPS() {
	    if (getCurrentTime() - lastFPS > 1000) {
	    	displayFps = currentFps;
	    	Display.setTitle(title + " | FPS: " + displayFps);
	    	currentFps = 0; //reset the FPS counter
	        lastFPS += 1000; //add one second
	    }
	    currentFps++;
	}

	public static int getFPS() {
		return displayFps;
	}

	public static void createDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			ContextAttribs attribs = new ContextAttribs(3, 3).withProfileCore(true).withForwardCompatible(true);
			Display.create(new PixelFormat().withDepthBits(24).withSamples(4), attribs);
			Display.setTitle(title);
			Display.setInitialBackground(1, 1, 1);
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.err.println("Couldn't create display!");
			System.exit(-1);
		}
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		lastFrameTime = getCurrentTime();
	}

	public static void updateDisplay(){
		Display.sync(FPS_CAP);
		Display.update();
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
	}

	public static void updateDisplayMode() {
		if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
			toggleFullScreen();
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			if (fullscreenMode) {
				DisplayManager.setDisplayMode(WIDTH, HEIGHT, false);
			}
			fullscreenMode = !fullscreenMode;
		}
	}

	public static void toggleFullScreen() {
		if (fullscreenMode) {
			DisplayManager.setDisplayMode(WIDTH, HEIGHT, false);
		} else {
			DisplayManager.setDisplayMode(WIDTH_FHD, HEIGHT_FHD, true);
		}
		fullscreenMode = !fullscreenMode;
	}

	public static float getFrameTimeSeconds() {
		return delta;
	}
	
	private static long getCurrentTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}

	public static void closeDisplay(){
		Display.destroy();
	}

	/**
	 * Set the display mode to be used
	 * http://wiki.lwjgl.org/wiki/LWJGL_Basics_5_(Fullscreen).html
	 * 
	 * @param width The width of the display required
	 * @param height The height of the display required
	 * @param fullscreen True if we want fullscreen mode
	 */
	public static void setDisplayMode(int width, int height, boolean fullscreen) {

		// return if requested DisplayMode is already set
		if ((Display.getDisplayMode().getWidth() == width) && 
			(Display.getDisplayMode().getHeight() == height) && 
		(Display.isFullscreen() == fullscreen)) {
			return;
		}
	 
		try {
			DisplayMode targetDisplayMode = null;
			 
		if (fullscreen) {
			DisplayMode[] modes = Display.getAvailableDisplayModes();
			int freq = 0;
					 
			for (int i=0;i<modes.length;i++) {
				DisplayMode current = modes[i];
						 
			if ((current.getWidth() == width) && (current.getHeight() == height)) {
				if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
					if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
					targetDisplayMode = current;
					freq = targetDisplayMode.getFrequency();
							}
						}
	 
				// if we've found a match for bpp and frequence against the 
				// original display mode then it's probably best to go for this one
				// since it's most likely compatible with the monitor
				if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
							(current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
								targetDisplayMode = current;
								break;
						}
					}
				}
			} else {
				targetDisplayMode = new DisplayMode(width,height);
			}
	 
			if (targetDisplayMode == null) {
				System.out.println("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
				return;
			}

			GL11.glViewport(0, 0, width, height);
			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);

		} catch (LWJGLException e) {
			System.out.println("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + e);
		}
	}
}
