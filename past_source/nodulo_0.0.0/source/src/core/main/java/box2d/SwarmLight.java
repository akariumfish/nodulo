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


public class SwarmLight extends RayHandler.BaseLight {
	
	public class Unit extends RayHandler.AbstractLight {
		
		final Color color = new Color(0f,0f,0f,0f); 
		float colorF;
		float distance;
		private Body body;
		final Vector2 pos = new Vector2();
		float rot = 0f;
		float cos = 0f;
		float sin = 0f;

		Unit(Color col, float dist) {
			color.set(col);
			colorF = color.toFloatBits();
			distance = dist;
			unitList.add(this);
			unit_nb++;
		}
		Unit init(Color col, float dist) {
			body = null;
			color.set(col);
			colorF = color.toFloatBits();
			distance = dist;
			unitList.add(this);
			unit_nb++;
			return this;
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
		}

		@Override
		public void remove(boolean b) {
			unitList.removeValue(this, true);
			unit_nb--;
			freeUnit.add(this);
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
			centerX[unit_cnt] = pos.x;
			centerY[unit_cnt] = pos.y;
			centerC[unit_cnt] = colorF;

			for (int i = 0 ; i < unitRay ; i++) {
				startX[unit_cnt * unitRay + i] = pos.x + rotX(patronX[i] * distance, patronY[i] * distance);
				startY[unit_cnt * unitRay + i] = pos.y + rotY(patronX[i] * distance, patronY[i] * distance);
				endX[unit_cnt * unitRay + i] = pos.x + rotX(patronX[(i+1)%unitRay] * distance, patronY[(i+1)%unitRay] * distance);
				endY[unit_cnt * unitRay + i] = pos.y + rotY(patronX[(i+1)%unitRay] * distance, patronY[(i+1)%unitRay] * distance);
			}
			unit_cnt++;
		}


		private float rotX(float x, float y) { return x * cos - y * sin; }
		private float rotY(float x, float y) { return x * sin + y * cos; }

	}
	
	public Unit newUnit(Color c, float d) {
		if (freeUnit.size > 0) 
			return freeUnit.removeIndex(freeUnit.size - 1).init(c, d);
		else if (unit_nb >= unitMax) return null;
		else return new Unit(c, d);
	}
	
	

	public final Array<Unit> unitList;
	public final Array<Unit> freeUnit;

	protected float[] patronX;
	protected float[] patronY;
	
	protected float[] centerX;
	protected float[] centerY;
	protected float[] centerC;
	protected float[] startX;
	protected float[] startY;
	protected float[] endX;
	protected float[] endY;
	
	final int unitMax;
	final int unitRay;

	private int unit_nb = 0;
	private int unit_cnt = 0;

	protected float segments[];

	protected int rayNum;
	protected int vertexNum;

	protected Mesh lightMesh;
	
	public SwarmLight(LightLayer l) { this(l, 16, 200); }
	public SwarmLight(LightLayer layer, int rays, int unit_max) {
		super(layer);
		this.unitMax = unit_max;
		
		if (rays < MIN_RAYS) rays = MIN_RAYS;

		unitRay = rays;
		patronX = new float[rays];
		patronY = new float[rays];
		for (int i = 0 ; i < rays ; i++) {
			final float angle = i * (float)(Math.PI * 2f) / (float)rays;
			patronX[i] = MathUtils.cos(angle);
			patronY[i] = MathUtils.sin(angle);
		}

		rayNum = rays * unit_max;
		vertexNum = rays * unit_max + unit_max;

		segments = new float[vertexNum * 12];
		
		endX = new float[rayNum];
		endY = new float[rayNum];
		startX = new float[rayNum];
		startY = new float[rayNum];
		centerX = new float[unit_max];
		centerY = new float[unit_max];
		centerC = new float[unit_max];
		
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
		for (Unit u : unitList) u.update();
		
		updateMesh();

//		Utl.logn("unit"+unitList.size);
//		Utl.logn("free"+freeUnit.size);
	}
	
	@Override
	public void render() {
		rayHandler.lightRenderedLastFrame++;
		lightMesh.render(
				rayHandler.lightShader, GL20.GL_TRIANGLES, 0, (unit_nb * unitRay) * 3);
	}

	protected void updateMesh() {

		int size = 0;
		for (int i = 0; i < unit_nb; i++) 
			for (int j = 0 ; j < unitRay ; j++) {
			segments[size++] = centerX[i];
			segments[size++] = centerY[i];
			segments[size++] = centerC[i];
			segments[size++] = 1;
			segments[size++] = startX[i * unitRay + j];
			segments[size++] = startY[i * unitRay + j]; 
			segments[size++] = centerC[i];
			segments[size++] = 0;//1 - f[i];
			segments[size++] = endX[i * unitRay + j];
			segments[size++] = endY[i * unitRay + j]; 
			segments[size++] = centerC[i];
			segments[size++] = 0;//1 - f[i];
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
