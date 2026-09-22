package box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Mesh.VertexDataType;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

import app.nDrawer;


public class SolidLight extends RayHandler.BaseLight {

	public class Unit extends RayHandler.AbstractLight {
		
		public void setPos(Vector2 p, float r) {
			pos.set(p); rot = r; dirty = true; }
		
		private Body body;
		final Vector2 pos = new Vector2();
		float rot = 0f;
		float cos = 0f;
		float sin = 0f;
		boolean dirty = true;

		private byte ray = 0;
		private float line = 0, line_col;
		private float points[];
		private float axes[];
		private float center[];
		private short point_cnt = -1, axe_cnt = 0;

		public Unit pushPoint(float x, float y, float r, float g, float b, float a) {
			float c = new Color(r,g,b,a).toFloatBits(); pushPoint(x,y,c); return this; }
		public Unit pushPoint(float x, float y, float c) {
			if (point_cnt < 0 || point_cnt >= ray * 3) return this;
			points[point_cnt++] = x; points[point_cnt++] = y; points[point_cnt++] = c;
			tmpVec.set(x - center[0], y - center[1]).nor();
			axes[axe_cnt++] = tmpVec.x; axes[axe_cnt++] = tmpVec.y; 
			return this;
		}

		public Unit pushCenter(float x, float y, float r, float g, float b, float a) {
			float c = new Color(r,g,b,a).toFloatBits(); pushCenter(x,y,c); return this; }
		public Unit pushCenter(float x, float y, float c) {
			center[0] = x; center[1] = y; center[2] = c; point_cnt = 0; axe_cnt = 0; return this; }
		
		Unit(int r, float l, Color cl) { 
			points = new float[floatNum * rayMax]; 
			axes = new float[floatNum * rayMax]; 
			center = new float[3]; 
			init(r,l,cl); }
		Unit init(int r, float l, Color cl) {
			line_col = cl.toFloatBits();
			ray = (byte)r; line = l;
			unitList.add(this);
			point_cnt = -1; axe_cnt = 0;
			body = null; pos.set(0,0); rot = 0; dirty = true;
			return this; }

		@Override public void remove() { remove(true); }
		@Override public void remove(boolean b) {
			unitList.removeValue(this, true);
			freeUnit.add(this); }
		
		@Override public void attachToBody(Body body, float x, float y) { attachToBody(body); }
		@Override public void attachToBody(Body body, float x, float y, float r) { attachToBody(body); }
		@Override public void attachToBody(Body body) { this.body = body; }
		@Override public void setActive(boolean active) { }
		

		public void render() { }
		public void update() {
			if (point_cnt < ray * 3) return;
			if (body != null) {
				tmpVec.set(body.getPosition());
				tmpFlt = body.getAngle();
				if (tmpFlt != rot || !tmpVec.equals(pos)) {
					dirty = true; pos.set(tmpVec); rot = tmpFlt; }	
			}
			if (dirty) { dirty = false; cos = MathUtils.cos(rot); sin = MathUtils.sin(rot); }
			transform(pos.x,pos.y,cos,sin);
			tmps = pushVert(center[0], center[1], center[2], 1f);

			tmps2 = line > 0 ? (short)3 : (short)1;
			for (short i = 0 ; i < ray ; i++) {
				pushVert(points[(i*3)], points[(i*3)+1], points[(i*3)+2], 1f);
				if (line > 0) {
					pushVert(points[(i*3)], 
							points[(i*3)+1], line_col, 1f);
					pushVert(points[(i*3)]+(axes[(i*2)]*line), 
							points[(i*3)+1]+(axes[(i*2)+1]*line), line_col, 1f); }
			}
			for (short i = 0 ; i < ray ; i++) {
				pushTrig(tmps,(short)(tmps+(i*tmps2)+1),(short)(tmps+(((i+1)%ray)*tmps2)+1));
				if (line > 0) {
					pushTrig((short)(tmps+(i*tmps2)+2),(short)(tmps+(i*tmps2)+3),
							(short)(tmps+(((i+1)%ray)*tmps2)+2));
					pushTrig((short)(tmps+(((i+1)%ray)*tmps2)+3),(short)(tmps+(i*tmps2)+3),
							(short)(tmps+(((i+1)%ray)*tmps2)+2)); }
			}
		}
		
	}
	private Vector2 tmpVec = new Vector2();
	private float tmpFlt = 0;
	private short tmps = 0, tmps2 = 0;

	private Vector2 transf = new Vector2();
	private float trsin = 0, trcos = 0;

	private void transform(float x, float y, float cos, float sin) { 
		transf.set(x,y); trcos = cos; trsin = sin; }
	private float rotX(float x, float y) { return x * trcos - y * trsin; }
	private float rotY(float x, float y) { return x * trsin + y * trcos; }

	private short pushVert(float x, float y, float c, float f) {
		vertices[flt_cnt++] = rotX(x,y) + transf.x;
		vertices[flt_cnt++] = rotY(x,y) + transf.y;
		vertices[flt_cnt++] = c;
		vertices[flt_cnt++] = f;
		return vert_cnt++;
	}

	private void pushTrig(short p1, short p2, short p3) {
		indices[ind_cnt++] = p1;
		indices[ind_cnt++] = p2;
		indices[ind_cnt++] = p3;
	}
	
	
	public Unit newUnit(int ray, float line, Color cl) {
		reset_cnt();
		for (Unit u : unitList) u.update();
		int vertneed = ray * 3 + 1;
		int indneed = ray * 9;
		if (vert_cnt + vertneed >= vertexMax || ind_cnt + indneed >= indiceMax) return null;
		if (freeUnit.size > 0) 
			return freeUnit.removeIndex(freeUnit.size - 1).init(ray, line, cl);
		else return new Unit(ray, line, cl);
	}
	

	public final Array<Unit> unitList;
	public final Array<Unit> freeUnit;

	private int flt_cnt = 0;
	private short vert_cnt = 0;
	private int ind_cnt = 0;
	
	private void reset_cnt() { flt_cnt = 0; vert_cnt = 0; ind_cnt = 0; }//stack_cnt = 0;, trig_cnt = 0;

	protected float vertices[];
	protected short indices[];

	final int unitMax;
	final int rayMax;
	protected int floatNum;
	protected int vertexNum;
	protected int faceNum;
	protected int indiceNum;
	protected int vertexMax;
	protected int indiceMax;

	protected Mesh lightMesh;
	
	public SolidLight(LightLayer l) { this(l, 16, 64); }
	public SolidLight(LightLayer layer, int rays, int unit_max) {
		super(layer);
		this.unitMax = unit_max;
		
		if (rays < MIN_RAYS) rays = MIN_RAYS;

		rayMax = rays;

		// by unit
		faceNum = rays * 3;
		vertexNum = rays * 3 + 1;
		floatNum = rays * 3;
		indiceNum = faceNum * 3;
		// max
		vertexMax = vertexNum * unit_max;
		indiceMax = indiceNum * unit_max;

		vertices = new float[vertexMax * 4];
		indices = new short[indiceMax];
		
		Mesh.VertexDataType vertexDataType = Mesh.VertexDataType.VertexArray;
		if (Gdx.gl30 != null) {
			vertexDataType = VertexDataType.VertexBufferObjectWithVAO;
		}

		lightMesh = new Mesh(
				vertexDataType, false, vertexMax + 1, indiceMax + 1,
				new VertexAttribute(Usage.Position, 2, "vertex_positions"),
				new VertexAttribute(Usage.ColorPacked, 4, "quad_colors"),
				new VertexAttribute(Usage.Generic, 1, "s"));
		
		unitList = new Array<Unit>(false, unit_max);
		freeUnit = new Array<Unit>(false, (int)(unit_max / 3f));
		
	}
	
	
	@Override
	public void update() {
		reset_cnt();
		for (Unit u : unitList) u.update();
		lightMesh.setVertices(vertices, 0, flt_cnt);
		lightMesh.setIndices(indices, 0, ind_cnt);
	}
	
	@Override
	public void render() {
		rayHandler.lightRenderedLastFrame++;
		lightMesh.render(
				rayHandler.lightShader, GL20.GL_TRIANGLES, 0, ind_cnt);
	}

	public void debugRender(nDrawer.Drawer draw) {
		draw.stroke(0,255,255,255,8f); draw.fill(0,0);
		for (Unit u : unitList) draw.circle(u.pos.x, u.pos.y, 15f);
	}
	
	
	
	@Override public void attachToBody(Body body) {}
	@Override public boolean contains(float x, float y) { return true; }
	@Override public void setActive(boolean active) {
		if (active == this.active) return;
		this.active = active;
	}
	
}
