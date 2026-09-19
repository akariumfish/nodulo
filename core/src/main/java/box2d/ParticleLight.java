package box2d;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Mesh.VertexDataType;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

import aa_nodulo.PlaneApplet;
import app.nDrawer;
import util.Utl;


public class ParticleLight extends RayHandler.BaseLight {
	
	private static int life_def = 100;
	
	public class Unit extends RayHandler.AbstractLight {

		public void set(float x, float y, float r, int l, float s) {
			pos.set(x,y); rot = r; life = l; speed = s; dirty = true; }
		
		final Color color = new Color(0f,0f,0f,0f); 
		float colorF;

		float distance;
		float speed = 5f;
		int life = life_def;
		final Vector2 pos = new Vector2();
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
			pos.set(0,0); 
			rot = 0;
			dirty = true;
			life = life_def;
		}
		Unit init(Color col, float dist) {
			color.set(col);
			colorF = color.toFloatBits();
			distance = dist;
			unitList.add(this);
			unit_nb++;
			pos.set(0,0); 
			rot = 0;
			dirty = true;
			life = life_def;
			return this;
		}
		
		@Override public void setActive(boolean active) { }
		
		@Override public void remove() { remove(true); }
		@Override public void remove(boolean b) {
			unitList.removeValue(this, true);
			unit_nb--;
			freeUnit.add(this);
		}
		

		public void render() { }
		public void update() {
			if (dirty) {
				cos = MathUtils.cos(rot);
				sin = MathUtils.sin(rot); 
				dirty = false;
			} else {
				pos.add(rotX(speed,0),rotY(speed,0));
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
			life--;
		}

		private float rotX(float x, float y) { return x * cos - y * sin; }
		private float rotY(float x, float y) { return x * sin + y * cos; }

	}
	private void addTrig(float x1, float y1, float x2, float y2, float x3, float y3, 
			float color, float f1, float f2, float f3) {
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

//	Mesh mesh;
////	int vertexNum = 4;
//	int trigNum = 2;
//	int instNum = 1800;
//	protected float vertices[];
//	protected short indices[];
//	protected float insts[];
//	private int vertSize = 0, indSize = 0, instSize = 0;
	
	public ParticleLight(LightLayer l) { this(l, 12, 256); }
	public ParticleLight(LightLayer layer, int rays, int unit_max) {
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
		vertexNum = rays * unit_max + unit_max;

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
		
		
		
		
		
//		if (PlaneApplet.OPENGLES3) 
//			vertexDataType = VertexDataType.VertexBufferObjectWithVAO;
//		
//		mesh = new Mesh(vertexDataType, false, vertexNum, trigNum * 3
//				, new VertexAttribute(Usage.Position, 2, "vertex_positions")
//				, new VertexAttribute(Usage.ColorPacked, 4, "quad_colors")
//				);
//		mesh.enableInstancedRendering (false, instNum
//				, new VertexAttribute(Usage.Position, 2, "i_position")
//				);

		
		
		
		
		unitList = new Array<Unit>(false, unit_max);
		freeUnit = new Array<Unit>(false, (int)(unit_max / 3f));
		
//		if (shader == null) shader = createShader();
	}
	
	private static ArrayList<Unit> tmp_unit = new ArrayList<Unit>();
	@Override
	public void update() {
		tmp_unit.clear();
		for (Unit u : unitList) if (u.life <= 0) tmp_unit.add(u);
		for (Unit u : tmp_unit) u.remove();
		unit_cnt = 0; trig_cnt = 0;
		for (Unit u : unitList) u.update();
		lightMesh.setVertices(segments, 0, unit_cnt);
	}
	
	@Override
	public void render() {
		rayHandler.lightRenderedLastFrame++;
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

//	private static ShaderProgram shader = null;
	public static ShaderProgram createShader() {
		final String vertexShader = "#version 330 core\n"
			+ "attribute vec2 vertex_positions;\n" //
			+ "attribute vec4 quad_colors;\n" //
			+ "attribute vec2 i_position;\n" //
			+ "uniform mat4 u_projTrans;\n" //
			+ "varying vec4 v_color;\n" //				
			+ "void main()\n" //
			+ "{\n" //
			+ "   v_color = quad_colors;\n" //		
//			+ "   vec4 v = vec4(-500 + vertex_positions.x + 200 * gl_InstanceID, "
//			+ "			vertex_positions.y, 0.0, 1.0);\n"	
			+ "   vec4 v = vec4(i_position.x + vertex_positions.x, "
			+ "			i_position.y + vertex_positions.y, 0.0, 1.0);\n"	
			+ "   gl_Position = u_projTrans * v;\n" //
			+ "}\n";
		final String fragmentShader = "#version 330 core\n"
			+ "#ifdef GL_ES\n" //
			+ "precision lowp float;\n" //
			+ "#define MED mediump\n"
			+ "#else\n"
			+ "#define MED \n"
			+ "#endif\n" //
			+ "varying vec4 v_color;\n" //
			+ "void main()\n"//
			+ "{\n" //
			+ "  gl_FragColor = v_color;\n" //
			+ "}";
		ShaderProgram.pedantic = true;
		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
//		if (!shader.isCompiled()) {
//			shader = new ShaderProgram("#version 330 core\n" +vertexShader, "#version 330 core\n" +fragmentShader);
			if(!shader.isCompiled()) { Utl.logn("ERROR : ParticleLight.createShader : " + shader.getLog()); }
//		}
		return shader;
	}
}
