package box2d;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Mesh.VertexDataType;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Pools;

import app.nDrawer;
import util.Utl;

/**
 * Light is data container for all the light parameters. When created lights
 * are automatically added to rayHandler and could be removed by calling
 * {@link #remove()} and added manually by calling {@link #add(RayHandler)}.
 * 
 * <p>Implements {@link Disposable}
 * 
 * @author kalle_h
 */
public class Geom implements Disposable {
	
	static final int MIN_RAYS = 3;
	
	protected GeomLayer layer;

	protected boolean active = true;

	protected Mesh geomMesh;

	protected final Color color = new Color(0.8f,0.8f,0.8f,0.8f);
	protected float colorF;
	
	protected int vertexNum;
	protected float segments[];
	protected float[] mx, nx;
	protected float[] my, ny;
	protected float[] f;

	protected Body body;
	protected float bodyOffsetX;
	protected float bodyOffsetY;
	protected float bodyAngleOffset;

	protected final Vector2 start = new Vector2();
	protected float start_r = 0f;
	protected float cos;
	protected float sin;
	
	public Geom(GeomLayer layer) {
		this.layer = layer;
		layer.geomList.add(this);

		cos = MathUtils.cos(0f);
		sin = MathUtils.sin(0f);
		
	}
	
	
	
	
	private boolean finish = false;
	private ArrayList<Float> verts = new ArrayList<Float>();
	
	void begin() {
		if (finish) return;
		verts.clear();
		vertexNum = 0;
	}
	
	void add(float x1, float y1, float x2, float y2) {
		if (finish) return;
		verts.add(x1); verts.add(y1); verts.add(x2);verts.add(y2);
		vertexNum++;
	}
	
	void end() {
		if (finish) return;

		if (vertexNum < MIN_RAYS) return;

		segments = new float[vertexNum * 8];
		mx = new float[vertexNum];
		my = new float[vertexNum];
		nx = new float[vertexNum];
		ny = new float[vertexNum];
		f = new float[vertexNum];
		
		Mesh.VertexDataType vertexDataType = Mesh.VertexDataType.VertexArray;
		if (Gdx.gl30 != null) {
			vertexDataType = VertexDataType.VertexBufferObjectWithVAO;
		}
		geomMesh = new Mesh(vertexDataType, false, vertexNum * 2, 0, 
				new VertexAttribute(Usage.Position, 2, "vertex_positions"), 
				new VertexAttribute(Usage.ColorPacked, 4, "quad_colors"),
				new VertexAttribute(Usage.Generic, 1, "s"));
		
		
		for (int i = 0 ; i < vertexNum ; i++) {
			mx[i] = verts.get(i*4);
			my[i] = verts.get(i*4+1);
			nx[i] = verts.get(i*4+2);
			ny[i] = verts.get(i*4+3);
		}
		
		finish = true;

		update();
		
	}
	
	
	

	
	
	/**
	 * Updates this geometry
	 */
	void update() {
		if (!finish) return;
		colorF = color.toFloatBits();
		if (updateBody()) {
			for (int i = 0 ; i < vertexNum ; i++) {
				mx[i] = start.x + toAngleX(verts.get(i*4),verts.get(i*4+1));
				my[i] = start.y + toAngleY(verts.get(i*4),verts.get(i*4+1));
				nx[i] = start.x + toAngleX(verts.get(i*4+2),verts.get(i*4+3));
				ny[i] = start.y + toAngleY(verts.get(i*4+2),verts.get(i*4+3));
			}
		}
		int size = 0;
		for (int i = 0; i < vertexNum; i++) {
			segments[size++] = mx[i];
			segments[size++] = my[i];
			segments[size++] = colorF;
			segments[size++] = 0.5f;
			segments[size++] = nx[i];
			segments[size++] = ny[i];
			segments[size++] = colorF;
			segments[size++] = 1;
		}
		geomMesh.setVertices(segments, 0, size);
	}

	/**
	 * Render this geometry
	 */
	void render() {
		if (!finish) return;
		
		geomMesh.render(
				layer.geomShader, GL20.GL_TRIANGLE_STRIP, 0, vertexNum * 2);

	}
	
	/**
	 * Disposes all resources
	 */
	public void dispose() {
		geomMesh.dispose();
		
	}
	

	
	public void debugRender(nDrawer.Drawer draw) {
		draw.stroke(255,0,255,255,8f); draw.fill(55,0,55,255);
		for (int i = 0 ; i < vertexNum ; i++) {
			draw.circle(mx[i], my[i], 10f);
			draw.circle(nx[i], ny[i], 10f);
		}
	}
	
	

	public void attachToBody(Body body, float offsetX, float offsetY, float degrees) {
		this.body = body;
		bodyOffsetX = offsetX;
		bodyOffsetY = offsetY;
		bodyAngleOffset = degrees;
	}

	public Body getBody() {
		return body;
	}

	protected boolean updateBody() {
		if (body == null) return false;
		
		boolean change = false;
		final Vector2 vec = body.getPosition();
		float angle = body.getAngle();
		cos = MathUtils.cos(angle);
		sin = MathUtils.sin(angle);
		final float dX = bodyOffsetX * cos - bodyOffsetY * sin;
		final float dY = bodyOffsetX * sin + bodyOffsetY * cos;
		if (start.x != vec.x + dX || start.y != vec.y + dY || 
				start_r != bodyAngleOffset + angle) change = true;
		start.x = vec.x + dX; 
		start.y = vec.y + dY;
		start_r = bodyAngleOffset + angle;
		cos = MathUtils.cos(start_r);
		sin = MathUtils.sin(start_r);
		return change;
	}

	private float toAngleX(float x, float y) {
		return x * cos - y * sin; }
	private float toAngleY(float x, float y) {
		return x * sin + y * cos; }
	
	

	/**
	 * @return if this geom is active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Enables/disables this geom update and rendering
	 */
	public void setActive(boolean active) {
		if (active == this.active)
			return;

		this.active = active;
	}

	/**
	 * Sets light color
	 * 
	 * <p>NOTE: you can also use colorless light with shadows, e.g. (0,0,0,1)
	 * 
	 * @param r
	 *            lights color red component
	 * @param g
	 *            lights color green component
	 * @param b
	 *            lights color blue component
	 * @param a
	 *            lights shadow intensity
	 * 
	 * @see #setColor(Color)
	 */
	public void setColor(float r, float g, float b, float a) {
		color.set(r, g, b, a);
	}
	
	
	
	
	
	
	
	/** 
	 * Checks if given point is inside of this geometry
	 */
	public boolean contains(float x, float y) {
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	

}
