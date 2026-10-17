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
		
		public void setPos(Vector2 p, float r) {
			pos.set(p); pos2.set(p); rot = r; dirty = true; }
		public void setPos(Vector2 p1, Vector2 p2) {
			pos.set(p1); pos2.set(p2); dirty = true; }
		
		final Color color = new Color(0f,0f,0f,0f); 
		float colorF;
		float distance;
		private Body body;
		final Vector2 pos = new Vector2();
		final Vector2 pos2 = new Vector2();
		float rot = 0f;
		float cos = 0f;
		float sin = 0f;
		boolean dirty = true;

		Unit(Color col, float dist) {
			color.set(col);
			colorF = color.toFloatBits();
			distance = dist;
			unitList.add(this);
			unit_nb++;
			pos.set(0,0); pos2.set(0,0); rot = 0;
			dirty = true;
		}
		Unit init(Color col, float dist) {
			body = null;
			color.set(col);
			colorF = color.toFloatBits();
			distance = dist;
			unitList.add(this);
			unit_nb++;
			pos.set(0,0); pos2.set(0,0); rot = 0;
			dirty = true;
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
			if (body != null) {
				tmpVec.set(body.getPosition());
				tmpFlt = body.getAngle();
				if (tmpFlt != rot || !tmpVec.equals(pos)) {
					dirty = true; pos.set(tmpVec); pos2.set(tmpVec); rot = tmpFlt; }	
			}
			if (pos2.equals(pos)) {
				if (dirty) {
					cos = MathUtils.cos(rot);
					sin = MathUtils.sin(rot); 
					dirty = false;
				}
				for (int i = 0 ; i < unitRay ; i++) {
					addTrig(pos.x, pos.y, 
							pos.x + rotX(patronX[i] * distance, patronY[i] * distance), 
							pos.y + rotY(patronX[i] * distance, patronY[i] * distance), 
							pos.x + rotX(patronX[(i+1)%unitRay] * distance, 
									patronY[(i+1)%unitRay] * distance), 
							pos.y + rotY(patronX[(i+1)%unitRay] * distance, 
									patronY[(i+1)%unitRay] * distance), 
							colorF, 1, 0, 0);
				}
			} else {
				if (dirty) {
					tmpVec.set(pos2).sub(pos);
					rot = (tmpVec.angleRad() + ((float)Math.PI / 2f));
					if (rot > ((float)Math.PI)) rot -= ((float)Math.PI * 2f);
					cos = MathUtils.cos(rot);
					sin = MathUtils.sin(rot); 
					dirty = false;
				}
				addTrig(pos.x, pos.y, pos2.x, pos2.y, 
						pos.x + rotX(distance, 0), pos.y + rotY(distance, 0), 
						colorF, 1, 1, 0);
				addTrig(pos2.x, pos2.y, 
						pos2.x + rotX(distance, 0), pos2.y + rotY(distance, 0),
						pos.x + rotX(distance, 0), pos.y + rotY(distance, 0), 
						colorF, 1, 0, 0);
				addTrig(pos.x, pos.y, pos2.x, pos2.y, 
						pos.x + rotX(-distance, 0), pos.y + rotY(-distance, 0), 
						colorF, 1, 1, 0);
				addTrig(pos2.x, pos2.y, 
						pos2.x + rotX(-distance, 0), pos2.y + rotY(-distance, 0),
						pos.x + rotX(-distance, 0), pos.y + rotY(-distance, 0), 
						colorF, 1, 0, 0);
				for (int i = 0 ; i < unitRayHalf ; i++) {
					addTrig(pos.x, pos.y, 
							pos.x + rotX(patronX[i] * distance, patronY[i] * distance), 
							pos.y + rotY(patronX[i] * distance, patronY[i] * distance), 
							pos.x + rotX(patronX[(i+1)%unitRay] * distance, 
									patronY[(i+1)%unitRay] * distance), 
							pos.y + rotY(patronX[(i+1)%unitRay] * distance, 
									patronY[(i+1)%unitRay] * distance), 
							colorF, 1, 0, 0);
				}
				for (int i = unitRayHalf ; i < unitRay ; i++) {
					addTrig(pos2.x, pos2.y, 
							pos2.x + rotX(patronX[i] * distance, patronY[i] * distance), 
							pos2.y + rotY(patronX[i] * distance, patronY[i] * distance), 
							pos2.x + rotX(patronX[(i+1)%unitRay] * distance, 
									patronY[(i+1)%unitRay] * distance), 
							pos2.y + rotY(patronX[(i+1)%unitRay] * distance, 
									patronY[(i+1)%unitRay] * distance), 
							colorF, 1, 0, 0);
				}
				
			}
		}

		private float rotX(float x, float y) { return x * cos - y * sin; }
		private float rotY(float x, float y) { return x * sin + y * cos; }

	}
	private Vector2 tmpVec = new Vector2();
	private float tmpFlt = 0;
	
	private void addTrig(float x1, float y1, float x2, float y2, float x3, float y3, 
			float color, float f1, float f2, float f3) {
//		if (trig_cnt >= vertexNum - 1) return;
		segments[unit_cnt++] = x1;
		segments[unit_cnt++] = y1;
		segments[unit_cnt++] = color;
		segments[unit_cnt++] = f1;
		segments[unit_cnt++] = x2;
		segments[unit_cnt++] = y2;
		segments[unit_cnt++] = color;
		segments[unit_cnt++] = f2;
		segments[unit_cnt++] = x3;
		segments[unit_cnt++] = y3; 
		segments[unit_cnt++] = color;
		segments[unit_cnt++] = f3;
		trig_cnt++;
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
	
	final int unitMax;
	final int unitRay;
	final int unitRayHalf;

	private int unit_nb = 0;
	private int unit_cnt = 0;
	private int trig_cnt = 0;

	protected float segments[];

	protected int rayNum;
	protected int vertexNum;

	protected Mesh lightMesh;
	
	public SwarmLight(LightLayer l) { this(l, 12, 256); }
	public SwarmLight(LightLayer layer, int rays, int unit_max) {
		super(layer);
		this.unitMax = unit_max;
		
		if (rays < MIN_RAYS) rays = MIN_RAYS;

		unitRay = rays;
		unitRayHalf = rays / 2;
		patronX = new float[rays];
		patronY = new float[rays];
		for (int i = 0 ; i < rays ; i++) {
			final float angle = i * (float)(Math.PI * 2f) / (float)rays;
			patronX[i] = MathUtils.cos(angle);
			patronY[i] = MathUtils.sin(angle);
		}

		rayNum = rays * unit_max;
		vertexNum = rays * unit_max + unit_max * 5;

		segments = new float[vertexNum * 12];
		
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
		
	}
	
	
	@Override
	public void update() {
		unit_cnt = 0; trig_cnt = 0;
		for (Unit u : unitList) u.update();
		lightMesh.setVertices(segments, 0, unit_cnt);
	}
	
	@Override
	public void render() {
		rayHandler.lightRenderedLastFrame++;
//		lightMesh.render(
//				rayHandler.lightShader, GL20.GL_TRIANGLES, 0, (unit_nb * unitRay) * 3);
		lightMesh.render(
				rayHandler.lightShader, GL20.GL_TRIANGLES, 0, trig_cnt * 3);
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
