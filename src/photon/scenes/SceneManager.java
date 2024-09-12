package photon.scenes;

import photon.system.Loader;
import photon.system.LogManager;

public class SceneManager {

	private int currentSceneId;
	private Scene currentScene;
	private Loader loader;

	public SceneManager(Loader loader) {
		this.loader = loader;
		currentSceneId = 0;
		changeScene(0);
	}

	public void changeScene(int sceneId) {
		if (currentScene != null) {
			cleanUp();
		}
		LogManager.info("Switching to scene " + sceneId);
		switch (sceneId) {
		default:
			System.exit(0);
			break;
		case 0:
			currentScene = new SceneTitle();
			break;
		case 1:
			currentScene = new SceneWorld();
			break;
		case 2:
			currentScene = new ScenePhysics();
			break;
		}
		currentScene.init(loader);
		currentScene.setSceneManager(this);
	}
	
	public Scene getCurrentScene() {
		return currentScene;
	}
	
	public int getCurrentSceneId() {
		return currentSceneId;
	}
	
	public void cleanUp() {
		currentScene.cleanUp();
	}

}
