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

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

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

public class ScenePhysics extends Scene {

	private Loader loader;

	private List<Entity> entities = new ArrayList<Entity>();
	private List<Entity> normalMapEntities = new ArrayList<Entity>();
	private List<Entity> colEntities = new ArrayList<Entity>();
	private List<GuiTexture> guis = new ArrayList<GuiTexture>();
	private List<Light> lights = new ArrayList<Light>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private List<WaterTile> waters = new ArrayList<WaterTile>();
	private List<Entity> newEntities;

	private Terrain currentTerrain = null;
	private MousePicker picker;

	private Player player;
	private Camera camera;
	private FontType font;
	private Light sun;

	private MasterRenderer renderer;
	private GuiRenderer guiRenderer;
	private Fbo fbo;

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
		texturePack = new TerrainTexturePack(backgroundTexture, backgroundTexture, backgroundTexture, backgroundTexture);
		blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		player = new Player(staticModel2, new Vector3f(0, 0, 0), 0, 0, 0, 1);
		camera = new Camera(player);
		sun = new Light(new Vector3f(1000, 1000, 0), new Vector3f(1f, 1f, 1f));
		font = new FontType(loader.loadFontTextureAtlas("arial"), new File("res/arial.fnt"));
		col = new Collision();

		renderer = new MasterRenderer(loader, camera);
		guiRenderer = new GuiRenderer(loader);
		fbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_RENDER_BUFFER);

		TextMaster.init(loader);
		ParticleMaster.init(loader, renderer.getProjectionMatrix());
		PostProcessing.init(loader);

		Entity ent1 = new Entity(crateModel, new Vector3f(100, 0, 5), 0, 0, 0, 0.04f);
		Entity ent2 = new Entity(crateModel, new Vector3f(65, 0, 75), 0, 0, 0, 0.04f);
		
		lights.add(sun);
		entities.add(player);
		
		entities.add(ent1);
		entities.add(ent2);
		colEntities.add(ent1);
		colEntities.add(ent2);
		
		newEntities = col.checkProximity(colEntities, player);

		LogManager.info("Generating terrain " + terrains.size() + " at (" + 0 + "," + 0 + ")");
		currentTerrain = new Terrain(0, 0, 0, -1, loader, texturePack, blendMap);
		terrains.add(currentTerrain);
		waters.add(new WaterTile(400, 400, -10));
		
		
	}

	@Override
	public void update() {	
		
		picker = new MousePicker(camera, renderer.getProjectionMatrix(), currentTerrain);
		if(!col.collision_detection(newEntities, player))  {
			player.move(currentTerrain);
		}
		newEntities = col.checkProximity(colEntities, player);
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
