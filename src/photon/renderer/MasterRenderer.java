package photon.renderer;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import photon.shaders.StaticShader;
import photon.shaders.TerrainShader;
import photon.shaders.WaterShader;
import photon.system.Loader;
import photon.entities.Camera;
import photon.entities.Entity;
import photon.entities.Light;

import photon.models.TexturedModel;
import photon.particles.ParticleMaster;
import photon.renderer.postProcessing.Fbo;
import photon.terrains.Terrain;
import photon.terrains.WaterFrameBuffers;
import photon.terrains.WaterTile;

public class MasterRenderer {

	public static final float FOV = 70;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 2000.0f;
	
	public static final float RED = 0.6f;
	public static final float GREEN = 0.6f;
	public static final float BLUE = 0.8f;

	private Matrix4f projectionMatrix;

	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;

	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	private WaterRenderer waterRenderer;
	private WaterShader waterShader = new WaterShader();
	private WaterFrameBuffers waterBuffers;

	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private Map<TexturedModel, List<Entity>> normalMapEntities = new HashMap<TexturedModel, List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	
	private NormalMappingRenderer normalMapRenderer;
	
	private SkyboxRenderer skyboxRenderer;
	private ShadowMapMasterRenderer shadowMapRenderer;

	public MasterRenderer(Loader loader, Camera camera) {
		enableCulling();
		createProjectionMatrix();
		waterBuffers = new WaterFrameBuffers();
		renderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
		waterRenderer = new WaterRenderer(loader, waterShader, projectionMatrix, waterBuffers);
		normalMapRenderer = new NormalMappingRenderer(projectionMatrix);
		this.shadowMapRenderer = new ShadowMapMasterRenderer(camera);
	}
	
	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(RED, GREEN, BLUE, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMaptexture());
	}

	public void render(List<Light> lights, Camera camera, Vector4f clipPlane) {
		prepare();

		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColor(RED, GREEN, BLUE);
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);
		renderer.render(entities);
		shader.stop();
		normalMapRenderer.render(normalMapEntities, clipPlane, lights, camera);
		terrainShader.start();
		terrainShader.loadClipPlane(clipPlane);
		terrainShader.loadSkyColor(RED, GREEN, BLUE);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());
		terrainShader.stop();
		skyboxRenderer.render(camera);

		terrains.clear();
		entities.clear();
		normalMapEntities.clear();
	}
	
	public void renderAll(List<Entity> entities, List<Entity> normalEntities, List<Terrain> terrains, List<Light> lights, List<WaterTile> waters,
			Camera camera, Fbo fbo) {
		
		WaterTile water = waters.get(0);
		
		waterBuffers.bindReflectionFrameBuffer();
		float distance = 2 * (camera.getPosition().y - water.getHeight());
		camera.getPosition().y -= distance;
		camera.invertPitch();
		renderScene(entities, normalEntities, terrains, lights, camera, new Vector4f(0, 1, 0, -water.getHeight()));
		camera.getPosition().y += distance;
		camera.invertPitch();
		
		waterBuffers.bindRefractionFrameBuffer();
		renderScene(entities, normalEntities, terrains, lights, camera, new Vector4f(0, -1, 0, water.getHeight()));
		
		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
		waterBuffers.unbindCurrentFrameBuffer();
		
		fbo.bindFrameBuffer();
		renderScene(entities, normalEntities, terrains, lights, camera, new Vector4f(0, 0, 0, 0));
		
		Light sun = lights.get(0);
		waterRenderer.render(waters, camera, sun);
	}
	
	public void renderScene(List<Entity> entities, List<Entity> normalEntities, List<Terrain> terrains, List<Light> lights,
			Camera camera, Vector4f clipPlane) {
		for(Terrain terrain:terrains) {
			processTerrain(terrain);
		}
		for(Entity entity:entities) {
			processEntity(entity);
		}
		for(Entity entity : normalEntities){
			processNormalMapEntity(entity);
		}
		render(lights, camera, clipPlane);
		//ParticleMaster.renderParticles(camera);
	}

	public void processTerrain(Terrain terrain) {
		terrains.add(terrain);
	}

	public void processEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	
	public void processNormalMapEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = normalMapEntities.get(entityModel);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			normalMapEntities.put(entityModel, newBatch);
		}
	}
	
	public void renderShadowMap(List<Entity> entityList, List<Entity> normalMapEntityList, Light sun) {
		for(Entity entity : entityList) {
			processEntity(entity);
		}
		for(Entity entity : normalMapEntityList) {
			processEntity(entity);
		}
		shadowMapRenderer.render(entities, sun);
		entities.clear();
	}
	
	public int getShadowMaptexture() {
		return shadowMapRenderer.getShadowMap();
	}

	public void cleanUp() {
		shader.cleanUp();
		terrainShader.cleanUp();
		normalMapRenderer.cleanUp();
		waterBuffers.cleanUp();
		waterShader.cleanUp();
		shadowMapRenderer.cleanUp();
	}

	private void createProjectionMatrix(){
    	projectionMatrix = new Matrix4f();
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
    }
	
	public Matrix4f getProjectionMatrix() {
		return this.projectionMatrix;
	}
}
