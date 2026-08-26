package box2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import app.nDrawer;

/**
 * Light shaped as a circle's sector with given radius, direction and angle
 * 
 * <p>Extends {@link PositionalLight}
 * 
 * @author kalle_h
 */
public class ConeLight extends PositionalLight {

	float coneDegree;

	/**
	 * Creates light shaped as a circle's sector with given radius, direction and arc angle
	 * 
	 * @param rayHandler
	 *            not {@code null} instance of RayHandler
	 * @param rays
	 *            number of rays - more rays make light to look more realistic
	 *            but will decrease performance, can't be less than MIN_RAYS
	 * @param color
	 *            color, set to {@code null} to use the default color
	 * @param distance
	 *            distance of cone light, soft shadow length is set to distance * 0.1f
	 * @param x
	 *            axis position
	 * @param y
	 *            axis position
	 * @param directionDegree
	 *            direction of cone light
	 * @param coneDegree
	 *            half-size of cone light, centered over direction
	 */
	public ConeLight(LightLayer layer, Color color,
			float distance, float x, float y, float directionDegree,
			float coneDegree) {

		super(layer, 
				(int)(Math.PI * 2f * distance * (coneDegree / 360f) * RayHandler.LIGHT_DEG_SIZE / 360f)
				, color, distance, x, y, directionDegree);
		setConeDegree(coneDegree);
	}
	public ConeLight(LightLayer layer, int rays, Color color,
			float distance, float x, float y, float directionDegree,
			float coneDegree) {

		super(layer, rays, color, distance, x, y, directionDegree);
		setConeDegree(coneDegree);
	}
	
	@Override
	public void update () {
		if (rayHandler.pseudo3d) {
			prepareFixtureData();
			updateDynamicShadowMeshes();
		}

		updateBody();
		if (dirty) setEndPoints();
		
		if (cull()) return;
		if (staticLight && !dirty) return;
		
		dirty = false;
		updateMesh();
	}

	/**
	 * Sets light direction
	 * <p>Actual recalculations will be done only on {@link #update()} call
	 */
	public void setDirection(float direction) {
		this.direction = direction;
		dirty = true;
	}

	/**
	 * @return this lights cone degree
	 */
	public float getConeDegree() {
		return coneDegree;
	}

	/**
	 * How big is the arc of cone
	 * 
	 * <p>Arc angle = coneDegree * 2, centered over direction angle
	 * <p>Actual recalculations will be done only on {@link #update()} call
	 * 
	 */
	public void setConeDegree(float coneDegree) {
		this.coneDegree = MathUtils.clamp(coneDegree, 0f, 180f);
		dirty = true;
	}

	/**
	 * Sets light distance
	 * 
	 * <p>MIN value capped to 0.1f meter
	 * <p>Actual recalculations will be done only on {@link #update()} call
	 */
	public void setDistance(float dist) {
		dist *= RayHandler.gammaCorrectionParameter;
		this.distance = dist < 0.01f ? 0.01f : dist;
		dirty = true;
	}
	
	/** Updates lights sector basing on distance, direction and coneDegree **/
	protected void setEndPoints() {
		for (int i = 0; i < rayNum; i++) {
			float angle = direction + coneDegree - 2f * coneDegree * i
					/ (rayNum - 1f);
			final float s = sin[i] = MathUtils.sinDeg(angle);
			final float c = cos[i] = MathUtils.cosDeg(angle);
			endX[i] = distance * c;
			endY[i] = distance * s;
		}
	}

	public void debugRender(nDrawer.Drawer draw) {
		draw.stroke(0,255,255,255,8f); draw.fill(55,0,55,255);
		draw.circle(getPosition().x, getPosition().y, 20f);
		Vector2 p1 = new Vector2(distance,0);
		p1.rotateRad(direction * MathUtils.degreesToRadians + 
				coneDegree * MathUtils.degreesToRadians / 2f);
		p1.add(getPosition().x, getPosition().y);
		draw.line(getPosition().x, getPosition().y, p1.x, p1.y);
		Vector2 p2 = new Vector2(distance,0);
		p2.rotateRad(direction * MathUtils.degreesToRadians - 
				coneDegree * MathUtils.degreesToRadians / 2f);
		p2.add(getPosition().x, getPosition().y);
		draw.line(getPosition().x, getPosition().y, p2.x, p2.y);
		draw.line(p1.x, p1.y, p2.x, p2.y);
	}
	
}
