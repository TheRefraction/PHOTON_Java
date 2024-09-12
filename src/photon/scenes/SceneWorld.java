package photon.scenes;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import photon.entities.Camera;
import photon.entities.Entity;
import photon.entities.Light;
import photon.entities.Player;
import photon.font.FontType;
import photon.font.TextMaster;
import photon.models.OBJLoader;
import photon.models.RawModel;
import photon.models.TexturedModel;
import photon.models.obj.NormalMappedObjLoader;
import photon.particles.ParticleMaster;
import photon.particles.ParticleSystem;
import photon.physics.Collision;
import photon.renderer.GuiRenderer;
import photon.renderer.MasterRenderer;
import photon.renderer.postProcessing.Fbo;
import photon.renderer.postProcessing.PostProcessing;
import photon.system.Loader;
import photon.system.LogManager;
import photon.terrains.Terrain;
import photon.terrains.WaterTile;
import photon.texture.GuiTexture;
import photon.texture.ModelTexture;
import photon.texture.ParticleTexture;
import photon.texture.TerrainTexture;
import photon.texture.TerrainTexturePack;
import photon.utils.Maths;
import photon.utils.MousePicker;

public class SceneWorld extends Scene {

	private Loader loader;

	private List<Entity> entities = new ArrayList<Entity>();
	private List<Entity> normalMapEntities = new ArrayList<Entity>();
	private List<GuiTexture> guis = new ArrayList<GuiTexture>();
	private List<Light> lights = new ArrayList<Light>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private List<WaterTile> waters = new ArrayList<WaterTile>();
	private HashMap<Point, Integer> terrainGrid = new HashMap<>();

	private Terrain currentTerrain = null;
	private Terrain oldTerrain = null;
	private MousePicker picker;
	
	private Vector2f currentLookAt;

	private Player player;
	private Camera camera;
	private FontType font;
	private Light sun;
	private ParticleSystem particleSystem;

	private MasterRenderer renderer;
	private GuiRenderer guiRenderer;
	private Fbo fbo;

	private int seed;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;
	
	private Collision col;

	@Override
	public void init(Loader loader) {
		this.loader = loader;

		RawModel rmodel_player = OBJLoader.loadObjModel("player", loader);
		TexturedModel staticModel2 = new TexturedModel(rmodel_player,
				new ModelTexture(loader.loadTexture("default"), 10, 10));
		TexturedModel crateModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader),
				new ModelTexture(loader.loadTexture("crate")));
		crateModel.getTexture().setNormalMap(loader.loadTexture("crateNormal"));
		crateModel.getTexture().setShineDamper(10);
		crateModel.getTexture().setReflectivity(0.5f);

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("image"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("imag"));
		texturePack = new TerrainTexturePack(backgroundTexture, rTexture, rTexture, rTexture);
		blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("cosmic"), 4, true);

		player = new Player(staticModel2, new Vector3f(0, 0, 0), 0, 0, 0, 1);
		camera = new Camera(player);
		sun = new Light(new Vector3f(1000000, 1500000, 0), new Vector3f(1.3f, 1.3f, 1.3f));
		font = new FontType(loader.loadFontTextureAtlas("arial"), new File("res/arial.fnt"));

		renderer = new MasterRenderer(loader, camera);
		guiRenderer = new GuiRenderer(loader);
		fbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_RENDER_BUFFER);

		TextMaster.init(loader);
		ParticleMaster.init(loader, renderer.getProjectionMatrix());
		PostProcessing.init(loader);

		lights.add(sun);
		entities.add(player);
		entities.add(new Entity(crateModel, new Vector3f(65, 30, 75), 0, 0, 0, 0.04f));
		normalMapEntities.add(new Entity(crateModel, new Vector3f(65, 100, 75), 0, 0, 0, 0.04f));

		seed = Maths.generateSeed();
		generateTerrain(0, 0);

		particleSystem = new ParticleSystem(particleTexture, 40, 10, 0.1f, 1, 1.6f);
		particleSystem.setLifeError(0.1f);
		particleSystem.setSpeedError(0.25f);
		particleSystem.setScaleError(0.5f);
		particleSystem.randomizeRotation();
		
		col = new Collision();

	}
	
	private Vector2f getCameraLookAt() {
		float angle = (float) Math.toRadians(camera.getYaw() - 90);
		return new Vector2f(Math.round(Math.cos(angle)), Math.round(Math.sin(angle)));
	}

	private void generateTerrain(int x, int z) {
		LogManager.info("Generating terrain " + terrains.size() + " at (" + x + "," + z + ")");
		terrains.add(new Terrain(terrains.size(), x, z, seed, loader, texturePack, blendMap));
		waters.add(new WaterTile(Terrain.SIZE * (x + 0.5f), Terrain.SIZE * (z + 0.5f), 0));
		terrainGrid.put(new Point(x, z), 0);
	}

	private void updateTerrain() {
		for (Terrain terrain : terrains) {
			float dx = terrain.getX();
			float dz = terrain.getZ();
			if (dx <= player.getPosition().x && dz <= player.getPosition().z
					&& (dx + terrain.getSize()) >= player.getPosition().x
					&& (dz + terrain.getSize()) >= player.getPosition().z) {
				currentTerrain = terrain;
			}
		}
		
		currentLookAt = getCameraLookAt();
		int gx = currentTerrain.getGridX();
		int gz = currentTerrain.getGridZ();
		/*if (currentTerrain != oldTerrain) {
			for (int i = gx - 1; i <= gx + 1; i++) {
				for (int j = gz - 1; j <= gz + 1; j++) {
					if (terrainGrid.get(new Point(i, j)) == null) {
						generateTerrain(i, j);
					}
				}
			}
		} else*/ 
		int i = (int) (gx + currentLookAt.x);
		int j = (int) (gz + currentLookAt.y);
		if (terrainGrid.get(new Point(i, j)) == null) {
			generateTerrain(i, j);
		}

		oldTerrain = currentTerrain;
	}

	@Override
	public void update() {
		updateTerrain();		

		picker = new MousePicker(camera, renderer.getProjectionMatrix(), currentTerrain);
		player.move(currentTerrain);
		camera.move();
		picker.update();

		ParticleMaster.update(camera);
		renderer.renderShadowMap(entities, normalMapEntities, sun);
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

		renderer.renderAll(entities, normalMapEntities, terrains, lights, waters, camera, fbo);
		ParticleMaster.renderParticles(camera);
		fbo.unbindFrameBuffer();
		PostProcessing.doPostProcessing(fbo.getColourTexture());

		guiRenderer.render(guis);
		TextMaster.render();
	}

	@Override
	public void cleanUp() {
		PostProcessing.cleanUp();
		fbo.cleanUp();
		ParticleMaster.cleanUp();
		TextMaster.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
	}

}
