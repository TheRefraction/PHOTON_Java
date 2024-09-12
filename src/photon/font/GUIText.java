package photon.font;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class GUIText {

	private String textString;
	private float fontSize;

	private int textMeshVao;
	private int vertexCount;
	private Vector3f colour = new Vector3f(0f, 0f, 0f);
	
	private Vector2f position;
	private float lineMaxSize;
	private int numberOfLines;
	
	private float width = 0.5f;
	private float edge = 0.1f;
	private float borderWidth = 0.7f;
	private float borderEdge = 0.1f;
	private Vector2f offset = new Vector2f(0f, 0f);
	private Vector3f outlineColor = new Vector3f(0f, 0f, 0f);

	private FontType font;

	private boolean centerText = false;

	public GUIText(String text, float fontSize, FontType font, Vector2f position, float maxLineLength,
			boolean centered) {
		this.textString = text;
		this.fontSize = fontSize;
		this.font = font;
		this.position = position;
		this.lineMaxSize = maxLineLength;
		this.centerText = centered;
		TextMaster.loadText(this);
	}

	public void remove() {
		TextMaster.removeText(this);
	}

	public FontType getFont() {
		return font;
	}
	
	public void setText(String text) {
		this.textString = text;
		TextMaster.loadText(this);
	}

	public void setColor(float r, float g, float b) {
		colour.set(r, g, b);
	}

	public Vector3f getColor() {
		return colour;
	}

	public int getNumberOfLines() {
		return numberOfLines;
	}

	public Vector2f getPosition() {
		return position;
	}

	public int getMesh() {
		return textMeshVao;
	}

	public void setMeshInfo(int vao, int verticesCount) {
		this.textMeshVao = vao;
		this.vertexCount = verticesCount;
	}

	public int getVertexCount() {
		return this.vertexCount;
	}

	protected float getFontSize() {
		return fontSize;
	}

	protected void setNumberOfLines(int number) {
		this.numberOfLines = number;
	}

	protected boolean isCentered() {
		return centerText;
	}

	protected float getMaxLineSize() {
		return lineMaxSize;
	}

	protected String getTextString() {
		return textString;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getEdge() {
		return edge;
	}

	public void setEdge(float edge) {
		this.edge = edge;
	}

	public float getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(float borderWidth) {
		this.borderWidth = borderWidth;
	}

	public float getBorderEdge() {
		return borderEdge;
	}

	public void setBorderEdge(float borderEdge) {
		this.borderEdge = borderEdge;
	}

	public Vector2f getOffset() {
		return offset;
	}

	public void setOffset(Vector2f offset) {
		this.offset = offset;
	}

	public Vector3f getOutlineColor() {
		return outlineColor;
	}

	public void setOutlineColor(Vector3f outlineColor) {
		this.outlineColor = outlineColor;
	}

}
