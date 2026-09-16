package box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Mesh.VertexDataType;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;


public class GridLight extends RayHandler.BaseLight {
	
	protected float[] pointC;
	protected float[] pointX;
	protected float[] pointY;
	protected int pointCnt = 0;
	
	final int width;
	final float scale;

	protected float segments[];

	protected int cellNum;
	protected int vertexNum;

	protected Mesh lightMesh;
	
	protected boolean finished = false;

	private Vector2 pos = new Vector2();
	private boolean dirty = false;

	public GridLight(LightLayer layer, int wdth, float scl) {
		super(layer);
		this.width = wdth;
		this.scale = scl;

		cellNum = width * width;
		vertexNum = cellNum * 2 * 3;

		segments = new float[vertexNum * 4];

		pointX = new float[vertexNum];
		pointY = new float[vertexNum];
		pointC = new float[vertexNum];
		
//		vertexNum = (vertexNum - 1) * 3;
		
		Mesh.VertexDataType vertexDataType = Mesh.VertexDataType.VertexArray;
		if (Gdx.gl30 != null) {
			vertexDataType = VertexDataType.VertexBufferObjectWithVAO;
		}

		lightMesh = new Mesh(
				vertexDataType, false, vertexNum, 0,
				new VertexAttribute(Usage.Position, 2, "vertex_positions"),
				new VertexAttribute(Usage.ColorPacked, 4, "quad_colors"),
				new VertexAttribute(Usage.Generic, 1, "s"));
		
	}
	
	public void setPos(Vector2 v) { setPos(v.x,v.y); }
	public void setPos(float x, float y) {
		if (pos.x != x || pos.y != y) dirty = true; pos.set(x,y); 
	}

	public void grid(final Color[] cl) {
		if (cl.length < width*width) return;
		for (int x = 0 ; x < width - 1 ; x++) for (int y = 0 ; y < width - 1 ; y++) {
			face((x*scale), (y*scale), ((x+1)*scale), (y*scale), (x*scale), ((y+1)*scale), 
					cl[x+width*y], cl[(x+1)+width*y], cl[x+width*(y+1)]);
			face(((x+1)*scale), (y*scale), ((x+1)*scale), ((y+1)*scale), 
					(x*scale), ((y+1)*scale), 
					cl[(x+1)+width*y], cl[(x+1)+width*(y+1)], cl[x+width*(y+1)]); } 
		finished = true; 
	}
	public void face(float x1, float y1, float x2, float y2, float x3, float y3, 
			Color c1, Color c2, Color c3) {
		point(x1,y1,c1); point(x2,y2,c2); point(x3,y3,c3); }
	public void point(float x, float y, Color c) {
		pointX[pointCnt] = x - width*scale/2f; pointY[pointCnt] = y - width*scale/2f; 
		pointC[pointCnt] = c.toFloatBits(); pointCnt++; }
	
	@Override
	public void update() {
		if (!finished || !dirty) return;
		updateMesh();
		dirty = false;
	}
	
	@Override
	public void render() {
		if (!finished || !active) return;
		rayHandler.lightRenderedLastFrame++;
		lightMesh.render(
				rayHandler.lightShader, GL20.GL_TRIANGLES, 0, pointCnt);
	}

	protected void updateMesh() {
		if (!finished) return;
		int size = 0;
		for (int i = 0; i < pointCnt; i++) {
			segments[size++] = pos.x + pointX[i];
			segments[size++] = pos.y + pointY[i];
			segments[size++] = pointC[i];
			segments[size++] = 1;
		}
		lightMesh.setVertices(segments, 0, size);
	}
	
	
	@Override public void attachToBody(Body body) {}
	@Override public boolean contains(float x, float y) { return true; }
	@Override public void setActive(boolean active) {
		if (active == this.active) return;
		this.active = active; dirty = true; }
	
}
