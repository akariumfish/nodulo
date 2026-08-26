package box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Mesh.VertexDataType;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import aa_nodulo.pView;
import app.nDrawer;
import gui.nGUI;
import util.Utl;

/**
 * Abstract base class for all positional lights
 * 
 * <p>Extends {@link Light}
 * 
 * @author kalle_h
 */
public class RectLight extends Light {

	Color tmpColor = new Color();

	protected final Vector2 tmpEnd = new Vector2();
	protected final Vector2 start_pos = new Vector2();
	protected final Vector2 size = new Vector2();
	protected final Vector2 start[];
	protected final Vector2 end[];
	
	protected Body body;
	protected float bodyOffsetX;
	protected float bodyOffsetY;
	protected float bodyAngleOffset;
	public RectLight(LightLayer layer, int rays, Color color, 
			float x, float y, float w, float h) {
		this(layer, rays, color, x, y, w, h, 0f);
	}
	public RectLight(LightLayer layer, int rays, Color color, 
			float x, float y, float w, float h, float dir) {
		super(layer, rays, color, Float.POSITIVE_INFINITY, 0f);
		bodyOffsetX = x;
		bodyOffsetY = y;
		bodyAngleOffset = dir;
		size.set(w,h);
		setSoftnessLength(Math.min(w,h)*0.2f);

		vertexNum = (vertexNum - 1) * 2;
		start = new Vector2[rayNum];
		end = new Vector2[rayNum];
		for (int i = 0; i < rayNum; i++) {
			start[i] = new Vector2();
			end[i] = new Vector2();
		}

		Mesh.VertexDataType vertexDataType = Mesh.VertexDataType.VertexArray;
		if (Gdx.gl30 != null) {
			vertexDataType = VertexDataType.VertexBufferObjectWithVAO;
		}
		lightMesh = new Mesh(vertexDataType, false, vertexNum, 0, 
				new VertexAttribute(Usage.Position, 2, "vertex_positions"), 
				new VertexAttribute(Usage.ColorPacked, 4, "quad_colors"),
				new VertexAttribute(Usage.Generic, 1, "s"));
		softShadowMesh = new Mesh(vertexDataType, false, vertexNum * 2, 0, 
				new VertexAttribute(Usage.Position, 2, "vertex_positions"), 
				new VertexAttribute(Usage.ColorPacked, 4, "quad_colors"),
				new VertexAttribute(Usage.Generic, 1, "s"));
		
		updateBody();
		setMesh();
		
	}
	

	@Override
	public void update () {
		
		updateBody();
		setMesh();
		
//		if (cull()) return;
//		if (staticLight && !dirty) return;
		
		dirty = false;
	}
	
	
	void setMesh() {
		
		float angle = direction * MathUtils.degreesToRadians;
		
		float x = start_pos.x;
		float y = start_pos.y;
		
		Vector2 axelOffSet = new Vector2(size.x / 2f, 0f);
		axelOffSet.rotateRad(angle);
		
		float xAxelOffSet = axelOffSet.x;
		float yAxelOffSet = axelOffSet.y;
		
		x += xAxelOffSet;
		y += yAxelOffSet;
		
		Vector2 portion = new Vector2(0f, size.y / (rayNum - 1));
		portion.rotateRad(angle);
		
		final float portionX = portion.x;
		final float portionY = portion.y;
		
		for (int i = 0; i < rayNum; i++) {
			final float steppedX = i * portionX + x;
			final float steppedY = i * portionY + y;
			m_index = i;
			start[i].x = steppedX - xAxelOffSet;
			start[i].y = steppedY - yAxelOffSet;

			mx[i] = end[i].x = steppedX + xAxelOffSet;
			my[i] = end[i].y = steppedY + yAxelOffSet;

			if (rayHandler.world != null && !xray) {// && !rayHandler.pseudo3d) {
				rayHandler.world.rayCast(ray, start[i], end[i]);
			}
		}

		// update light mesh
		// ray starting point
		int size = 0;
		final int arraySize = rayNum;

		for (int i = 0; i < arraySize; i++) {
			segments[size++] = start[i].x;
			segments[size++] = start[i].y;
			segments[size++] = colorF;
			segments[size++] = 1f;
			segments[size++] = mx[i];
			segments[size++] = my[i];
			segments[size++] = colorF;
			segments[size++] = 1f;
		}
		lightMesh.setVertices(segments, 0, size);

		if (!soft || xray) {// || rayHandler.pseudo3d) {
			return;
		}

		Vector2 softS = new Vector2(softShadowLength, 0f);
		softS.rotateRad(angle);
		
		size = 0;
		for (int i = 0; i < arraySize; i++) {
			segments[size++] = mx[i];
			segments[size++] = my[i];
			segments[size++] = colorF;
			segments[size++] = 1f;

			segments[size++] = mx[i] + softS.x;// + softShadowLength * cos;
			segments[size++] = my[i] + softS.y;// + softShadowLength * sin;
			segments[size++] = zeroColorBits;
			segments[size++] = 1f;
		}
		softShadowMesh.setVertices(segments, 0, size);
	}

	
	
	
	
	


	@Override
	void render() {
		rayHandler.lightRenderedLastFrame++;
		rayHandler.simpleBlendFunc.apply();

		lightMesh.render(
				rayHandler.lightShader, GL20.GL_TRIANGLE_STRIP, 0, vertexNum);

		if (soft && !xray) {// && !rayHandler.pseudo3d) {
			softShadowMesh.render(
					rayHandler.lightShader, GL20.GL_TRIANGLE_STRIP, 0, vertexNum);
		}
	}

	
	
	/**
	 * Sets light direction
	 * <p>Actual recalculations will be done only on {@link #update()} call
	 */

	@Override
	public void setDirection(float direction) {
		this.direction = direction;
		dirty = true;
	}


	/**
	 * Sets light distance
	 * 
	 * <p>MIN value capped to 0.1f meter
	 * <p>Actual recalculations will be done only on {@link #update()} call
	 */
	@Override
	public void setDistance(float dist) {
//		dist *= RayHandler.gammaCorrectionParameter;
//		this.distance = dist < 0.01f ? 0.01f : dist;
//		dirty = true;
	}
	
	
	@Override
	public void attachToBody(Body body) {
		attachToBody(body, 0f, 0f, 0f);
	}
	
	/**
	 * Attaches light to specified body with relative offset
	 * 
	 * @param body
	 *            that will be automatically followed, note that the body
	 *            rotation angle is taken into account for the light offset
	 *            and direction calculations
	 * @param offsetX
	 *            horizontal relative offset in world coordinates
	 * @param offsetY
	 *            vertical relative offset in world coordinates
	 * 
	 */
	public void attachToBody(Body body, float offsetX, float offsetY) {
		attachToBody(body, offsetX, offsetY, 0f);
	}
	
	/**
	 * Attaches light to specified body with relative offset and direction
	 * 
	 * @param body
	 *            that will be automatically followed, note that the body
	 *            rotation angle is taken into account for the light offset
	 *            and direction calculations
	 * @param offsetX
	 *            horizontal relative offset in world coordinates
	 * @param offsetY
	 *            vertical relative offset in world coordinates
	 * @param degrees
	 *            directional relative offset in degrees 
	 */
	public void attachToBody(Body body, float offsetX, float offsetY, float degrees) {
		this.body = body;
		bodyOffsetX = offsetX;
		bodyOffsetY = offsetY;
		bodyAngleOffset = degrees;
		if (staticLight) dirty = true;
	}

	@Override
	public void debugRender(nDrawer.Drawer draw) {
		draw.stroke(0,255,255,255,8f); draw.fill(0,0);
		draw.push(); draw.rotate(direction * MathUtils.degreesToRadians);
		draw.translate(getPosition().x, getPosition().y);
		draw.rect(0, 0, size.x, size.y);
		draw.pop();
	}
	
	@Override
	public Vector2 getPosition() {
		tmpPosition.x = start_pos.x;
		tmpPosition.y = start_pos.y;
		return tmpPosition;
	}

	public Body getBody() {
		return body;
	}

	/** @return horizontal starting position of light in world coordinates **/
	@Override
	public float getX() {
		return start_pos.x;
	}

	/** @return vertical starting position of light in world coordinates **/
	@Override
	public float getY() {
		return start_pos.y;
	}

	@Override
	public void setPosition(float x, float y) {
		start_pos.x = x;
		start_pos.y = y;
		if (staticLight) dirty = true;
	}

	@Override
	public void setPosition(Vector2 position) {
		start_pos.x = position.x;
		start_pos.y = position.y;
		if (staticLight) dirty = true;
	}

	public boolean contains(Vector2 pos) {
		return contains(pos.x, pos.y);
	}

	@Override
	public boolean contains(float x, float y) {
		// fast fail
		final float x_d = start_pos.x - x;
		final float y_d = start_pos.y - y;
		final float dst2 = x_d * x_d + y_d * y_d;
		if (distance * distance <= dst2) return false; 

		// actual check
		boolean oddNodes = false;
		float x2 = mx[rayNum] = start_pos.x;
		float y2 = my[rayNum] = start_pos.y;
		float x1, y1;
		for (int i = 0; i <= rayNum; x2 = x1, y2 = y1, ++i) {
			x1 = mx[i];
			y1 = my[i];
			if (((y1 < y) && (y2 >= y)) || (y1 >= y) && (y2 < y)) {
				if ((y - y1) / (y2 - y1) * (x2 - x1) < (x - x1)) oddNodes = !oddNodes;
			}
		}
		return oddNodes;
	}
	
	@Override
	protected void setRayNum(int rays) {
		super.setRayNum(rays);
	}
	
	protected boolean cull() {
		culled = rayHandler.culling && !rayHandler.intersect(
					start_pos.x, start_pos.y, distance + softShadowLength);
		return culled;
	}
	
	protected void updateBody() {
		if (body == null || staticLight) {
			start_pos.x = bodyOffsetX;
			start_pos.y = bodyOffsetY;
			setDirection(bodyAngleOffset);
			return;
		}
		
		final Vector2 vec = body.getPosition();
		final float angle = body.getAngle();
		final float cos = MathUtils.cos(angle);
		final float sin = MathUtils.sin(angle);
		final float dX = bodyOffsetX * cos - bodyOffsetY * sin;
		final float dY = bodyOffsetX * sin + bodyOffsetY * cos;
		start_pos.x = vec.x + dX;
		start_pos.y = vec.y + dY;
		setDirection(bodyAngleOffset + angle * MathUtils.radiansToDegrees);
	}

	public float getBodyOffsetX() {
		return bodyOffsetX;
	}

	public float getBodyOffsetY() {
		return bodyOffsetY;
	}

	public float getBodyAngleOffset() {
		return bodyAngleOffset;
	}

	public void setBodyOffsetX(float bodyOffsetX) {
		this.bodyOffsetX = bodyOffsetX;
	}

	public void setBodyOffsetY(float bodyOffsetY) {
		this.bodyOffsetY = bodyOffsetY;
	}

	public void setBodyAngleOffset(float bodyAngleOffset) {
		this.bodyAngleOffset = bodyAngleOffset;
	}
}
