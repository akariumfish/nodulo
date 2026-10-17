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
		
		private Body body;
		final Vector2 pos = new Vector2();
		float rot = 0f;
		float cos = 0f;
		float sin = 0f;
		
		int trigUse = 0;

		protected float[] patronX;
		protected float[] patronY;
		protected float[] patronC;

		Unit() {
			unitList.add(this);
			unit_nb++;
			build();
		}
		Unit init() {
			body = null;
			unitList.add(this);
			unit_nb++;
			trigUse = 0;
			return this;
		}

		public void build() {
			patronX = new float[unitTrig * 3];
			patronY = new float[unitTrig * 3];
			patronC = new float[unitTrig];
		}

		public void trig(float x1, float y1, float x2, float y2, float x3, float y3, 
				Color c) {
			patronX[trigUse*3] = x1; patronY[trigUse*3] = y1;
			patronX[trigUse*3+1] = x2; patronY[trigUse*3+1] = y2;
			patronX[trigUse*3+2] = x3; patronY[trigUse*3+2] = y3;
			patronC[trigUse] = c.toFloatBits();
			trigUse++;
		}

		@Override public void attachToBody(Body body, float x, float y) { attachToBody(body); }
		@Override public void attachToBody(Body body, float x, float y, float d) { attachToBody(body); }
		@Override public void attachToBody(Body body) { this.body = body; }
		@Override public void setActive(boolean active) { }
		
		@Override 
		public void remove() {
			unitList.removeValue(this, true);
			unit_nb--;
			freeUnit.add(this);
			body = null;
			trigUse = 0;
		}

		@Override
		public void remove(boolean b) {
			this.remove();
		}
		

		public void render() { }
		public void update() {
			if (body == null) {
				pos.set(0,0); rot = 0f;
			} else {
				pos.set(body.getPosition());
				rot = body.getAngle();	
			}
			cos = MathUtils.cos(rot);
			sin = MathUtils.sin(rot);
//			faceC[unit_cnt] = colorF;
			unitUse[unit_cnt] = trigUse;

			for (int i = 0 ; i < unitTrig && i < trigUse ; i++) {
				pind = unit_cnt * unitTrig + i;
				ind1 = i*3; ind2 = (i*3+1)%(trigUse*3); ind3 = (i*3+2)%(trigUse*3);
				point1X[pind] = pos.x + rotX(patronX[ind1], patronY[ind1]);
				point1Y[pind] = pos.y + rotY(patronX[ind1], patronY[ind1]);
				point2X[pind] = pos.x + rotX(patronX[ind2], patronY[ind2]);
				point2Y[pind] = pos.y + rotY(patronX[ind2], patronY[ind2]);
				point3X[pind] = pos.x + rotX(patronX[ind3], patronY[ind3]);
				point3Y[pind] = pos.y + rotY(patronX[ind3], patronY[ind3]);
				faceC[pind] = patronC[i];
				trig_cnt++;
			}
			unit_cnt++;
		}

		private float rotX(float x, float y) { return x * cos - y * sin; }
		private float rotY(float x, float y) { return x * sin + y * cos; }

	}
	
	public Unit newUnit() {
		if (freeUnit.size > 0) 
			return freeUnit.removeIndex(freeUnit.size - 1).init();
		else if (unit_nb >= unitMax) return null;
		else return new Unit();
	}
	
	

	public final Array<Unit> unitList;
	public final Array<Unit> freeUnit;

	private int pind = 0, ind1 = 0, ind2 = 0, ind3 = 0;
	
	protected int[] unitUse;
	protected float[] faceC;
	protected float[] point1X;
	protected float[] point1Y;
	protected float[] point2X;
	protected float[] point2Y;
	protected float[] point3X;
	protected float[] point3Y;
	
	final int unitMax;
	final int unitTrig;

	private int unit_nb = 0;
	private int unit_cnt = 0;
	private int trig_cnt = 0;

	protected float segments[];

	protected int faceNum;
	protected int vertexNum;

	protected Mesh lightMesh;
	
	public SolidLight(LightLayer l) { this(l, 16, 200); }
	public SolidLight(LightLayer layer, int trigs, int unit_max) {
		super(layer);
		this.unitMax = unit_max;
		
		if (trigs < MIN_RAYS) trigs = MIN_RAYS;

		unitTrig = trigs;

		faceNum = trigs * unit_max;
		vertexNum = trigs * unit_max + unit_max;

		segments = new float[vertexNum * 12];

		point1X = new float[faceNum];
		point1Y = new float[faceNum];
		point2X = new float[faceNum];
		point2Y = new float[faceNum];
		point3X = new float[faceNum];
		point3Y = new float[faceNum];
		faceC = new float[faceNum];
		unitUse = new int[unit_max];
		
		vertexNum = (vertexNum - 1) * 3;
		
		Mesh.VertexDataType vertexDataType = Mesh.VertexDataType.VertexArray;
		if (Gdx.gl30 != null) {
			vertexDataType = VertexDataType.VertexBufferObjectWithVAO;
		}

		lightMesh = new Mesh(
				vertexDataType, false, vertexNum, 0,
				new VertexAttribute(Usage.Position, 2, "vertex_positions"),
				new VertexAttribute(Usage.ColorPacked, 4, "quad_colors"),
				new VertexAttribute(Usage.Generic, 1, "s"));
		
		unitList = new Array<Unit>(false, unit_max);
		freeUnit = new Array<Unit>(false, (int)(unit_max / 3f));
		
		updateMesh();
	}
	
	
	@Override
	public void update() {
		unit_cnt = 0;
		trig_cnt = 0;
		for (Unit u : unitList) u.update();
		
		updateMesh();

//		Utl.logn("unit"+unitList.size);
//		Utl.logn("free"+freeUnit.size);
	}
	
	@Override
	public void render() {
		rayHandler.lightRenderedLastFrame++;
		lightMesh.render(
				rayHandler.lightShader, GL20.GL_TRIANGLES, 0, trig_cnt * 3);
	}

	private int ind = 0;
	protected void updateMesh() {

		int size = 0;
		for (int i = 0; i < unit_nb; i++) 
			for (int j = 0 ; j < unitUse[i] ; j++) {
				ind = i * unitTrig + j;
			segments[size++] = point1X[ind];
			segments[size++] = point1Y[ind];
			segments[size++] = faceC[ind];
			segments[size++] = 1;
			segments[size++] = point2X[ind];
			segments[size++] = point2Y[ind]; 
			segments[size++] = faceC[ind];
			segments[size++] = 1;
			segments[size++] = point3X[ind];
			segments[size++] = point3Y[ind]; 
			segments[size++] = faceC[ind];
			segments[size++] = 1;
		}
		lightMesh.setVertices(segments, 0, size);
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
