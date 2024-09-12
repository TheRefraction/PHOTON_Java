package photon.texture;

public class ModelTexture {

	private int textureID;
	private int normalMap;

	private float shineDamper = 1;
	private float reflectivity = 0;

	private boolean hasTransparancy = false;
	private boolean useFakeLightning = false;

	private int numberOfRows = 1;

	public ModelTexture(int id) {
		this.textureID = id;
	}

	public ModelTexture(int id, float shine, float reflec) {
		this.textureID = id;
		this.shineDamper = shine;
		this.reflectivity = reflec;
	}

	public ModelTexture(int id, float shine, float reflec, boolean transparancy) {
		this.textureID = id;
		this.shineDamper = shine;
		this.reflectivity = reflec;
		this.hasTransparancy = transparancy;
	}

	public int getID() {
		return this.textureID;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}
	
	public int getNormalMap() {
		return normalMap;
	}

	public void setNormalMap(int normalMap) {
		this.normalMap = normalMap;
	}

	public boolean isHasTransparancy() {
		return hasTransparancy;
	}

	public void setHasTransparancy(boolean hasTransparancy) {
		this.hasTransparancy = hasTransparancy;
	}

	public boolean isUseFakeLightning() {
		return useFakeLightning;
	}

	public void setUseFakeLightning(boolean useFakeLightning) {
		this.useFakeLightning = useFakeLightning;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}
}
