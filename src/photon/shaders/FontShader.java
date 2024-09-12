package photon.shaders;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class FontShader extends ShaderProgram{

	private static final String VERTEX_FILE = "src/photon/shaders/fontVertex.txt";
	private static final String FRAGMENT_FILE = "src/photon/shaders/fontFragment.txt";
	
	private int location_color;
	private int location_translation;
	private int location_width;
	private int location_edge;
	private int location_borderWidth;
	private int location_borderEdge;
	private int location_offset;
	private int location_outlineColor;
	
	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_color = super.getUniformLocation("color");
		location_translation = super.getUniformLocation("translation");
		location_width = super.getUniformLocation("width");
		location_edge = super.getUniformLocation("edge");
		location_borderWidth = super.getUniformLocation("borderWidth");
		location_borderEdge = super.getUniformLocation("borderEdge");
		location_offset = super.getUniformLocation("offset");
		location_outlineColor = super.getUniformLocation("outlineColor");
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
	
	public void loadOutlineColor(Vector3f color) {
		super.loadVector(location_outlineColor, color);
	}
	
	public void loadColor(Vector3f color) {
		super.loadVector(location_color, color);
	}
	
	public void loadTranslation(Vector2f position) {
		super.load2DVector(location_translation, position);
	}
	
	public void loadOffset(Vector2f offset) {
		super.load2DVector(location_offset, offset);
	}
	
	public void loadSizeValues(float width, float edge) {
		super.loadFloat(location_width, width);
		super.loadFloat(location_edge, edge);
	}
	
	public void loadBorderValues(float borderWidth, float borderEdge) {
		super.loadFloat(location_borderWidth, borderWidth);
		super.loadFloat(location_borderEdge, borderEdge);
	}


}