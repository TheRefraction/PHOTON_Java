package photon.system;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;

import de.matthiasmann.twl.utils.PNGDecoder;

public class DisplayManager {
	
	private static long lastFrameTime;
	private static float delta;
	
	public static void createDisplay() {
		ContextAttribs attribs = new ContextAttribs(Config.GL_MAJOR_VERSION, Config.GL_MINOR_VERSION).withForwardCompatible(true).withProfileCore(true);
		
		try {
			Display.setDisplayMode(new DisplayMode(Config.WIDTH, Config.HEIGHT));
			Display.create(new PixelFormat().withDepthBits(24).withSamples(8), attribs);
			Display.setTitle(Config.GAME_TITLE);
			try {
				Display.setIcon(new ByteBuffer[] {
				        loadIcon("res/icon16.png"),
				        loadIcon("res/icon32.png")
				    });
			} catch (IOException e) {
				e.printStackTrace();
			}
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0, 0, Config.WIDTH, Config.HEIGHT);
		lastFrameTime = getCurrentTime();
		
		try {
			Mouse.create();
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Mouse.setGrabbed(true);
		
		LogManager.info("OS: " + System.getProperty("os.name"));
		LogManager.info("OS Version: " + System.getProperty("os.version"));
		LogManager.info("LWJGL Version: " + org.lwjgl.Sys.getVersion());
		LogManager.info("OpenGL Version: " + GL11.glGetString(GL11.GL_VERSION));
		LogManager.info("OpenGL initialized!");
	}
	
	private static ByteBuffer loadIcon(String path) throws IOException {
        InputStream inputStream = new FileInputStream(path);
        try {
            PNGDecoder decoder = new PNGDecoder(inputStream);
            ByteBuffer bytebuf = ByteBuffer.allocateDirect(decoder.getWidth()*decoder.getHeight()*4);
            decoder.decode(bytebuf, decoder.getWidth()*4, PNGDecoder.Format.RGBA);
            bytebuf.flip();
            return bytebuf;
        } finally {
            inputStream.close();
        }
    }
	
	public static void updateDisplay() {
		Display.sync(Config.FPS_CAP);
		Display.update();
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime)/1000f;
		lastFrameTime = currentFrameTime;
	}
	
	public static void closeDisplay() {
		Mouse.destroy();
		Display.destroy();
	}
	
	public static float getFrameTimeSeconds() {
		return delta;
	}
	
	private static long getCurrentTime() {
		return Sys.getTime()*1000/Sys.getTimerResolution();
	}
}
