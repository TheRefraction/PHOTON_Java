package photon.scenes;

import photon.system.Loader;

public abstract class Scene {
	
	protected SceneManager sceneManager;
	
	public Scene() {
		
	}
	
	public abstract void init(Loader loader);
	public abstract void update();
	public abstract void cleanUp();
	
	public void setSceneManager(SceneManager sceneManager) {
		this.sceneManager = sceneManager;
	}
}
