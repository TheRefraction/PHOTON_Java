import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.opengl.Display;

import photon.audio.AudioMaster;
import photon.console.ConsoleGUI;
import photon.console.ConsoleLogManager;
import photon.scenes.SceneManager;
import photon.system.DisplayManager;
import photon.system.Loader;
import photon.system.LogManager;

public class Main {

	public static void main(String[] args) {

		ConsoleLogManager.init();
		ConsoleGUI.initGui();

		LogManager.info("Starting PHOTON...");

		DisplayManager.createDisplay();

		AudioMaster.init();
		AudioMaster.setListenerData(0, 0, 0);
		AL10.alDistanceModel(AL11.AL_LINEAR_DISTANCE_CLAMPED);

		Loader loader = new Loader();
		SceneManager sceneManager = new SceneManager(loader);

		while (!Display.isCloseRequested()) {
			try {
				sceneManager.getCurrentScene().update();
				DisplayManager.updateDisplay();
			} catch (Exception e) {
				LogManager.severe("An error occurred!");
				LogManager.severe(e.getMessage());
				e.printStackTrace();
			}
		}

		LogManager.info("Halting process!");

		sceneManager.cleanUp();
		loader.cleanUp();
		AudioMaster.cleanUp();
		ConsoleGUI.close();
		DisplayManager.closeDisplay();

		System.exit(0);
	}

}
