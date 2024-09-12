package photon.scenes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

import photon.entities.Camera;
import photon.font.FontType;
import photon.font.GUIText;
import photon.font.TextMaster;
import photon.renderer.GuiRenderer;
import photon.renderer.MasterRenderer;
import photon.system.Loader;
import photon.system.LogManager;
import photon.texture.GuiTexture;

public class SceneTitle extends Scene {
	
	private Loader loader;
	
	private List<GuiTexture> guis = new ArrayList<GuiTexture>();
	
	private Camera camera;
	private FontType font;
	
	private MasterRenderer renderer;
	private GuiRenderer guiRenderer;

	public SceneTitle() {
		
	}

	@Override
	public void init(Loader loader) {
		this.loader = loader;
		
		camera = new Camera();
		font = new FontType(loader.loadFontTextureAtlas("arial"), new File("res/arial.fnt"));
		
		renderer = new MasterRenderer(loader, camera);
		guiRenderer = new GuiRenderer(loader);
		
		TextMaster.init(loader);
		
		GUIText text = new GUIText("PHOTON Engine", 2, font, new Vector2f(0, 0), 1f, true);
		text.setColor(1.0f, 0.5f, 0.2f);
		GUIText text2 = new GUIText("E - World scene", 1, font, new Vector2f(0, -0.5f), 1f, true);
		text2.setColor(1.0f, 0.5f, 0.2f);
		GUIText text3 = new GUIText("P - Physics scene", 1, font, new Vector2f(0, -0.7f), 1f, true);
		text3.setColor(1.0f, 0.5f, 0.2f);
		guis.add(new GuiTexture(loader.loadTexture("splash"), new Vector2f(1, -0.5f), new Vector2f(2, 1.5f)));
		
		LogManager.info("Title screen loaded successfully!");
	}

	@Override
	public void update() {
		if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
			sceneManager.changeScene(1);
			return;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
			sceneManager.changeScene(2);
			return;
		}
		
		guiRenderer.render(guis);
		TextMaster.render();
	}

	@Override
	public void cleanUp() {
		TextMaster.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
	}

}
