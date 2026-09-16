package box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Mesh.VertexDataType;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pools;

import app.nDrawer;

/**
 * A light whose ray starting points are evenly distributed along a chain of
 * vertices
 * 
 * <p> Extends {@link Light}
 * 
 * @author spruce
 */
public class ChainLight extends Light {
	
	public static float defaultRayStartOffset = 0.001f;
	public float rayStartOffset;
	public final FloatArray chain;

	protected int rayDirection;
//	boolean rayBidirectional = false;
	protected float bodyAngle;
	protected float bodyAngleOffset;

	protected Body body;

	protected final FloatArray segmentAngles = new FloatArray();
	protected final FloatArray segmentLengths = new FloatArray();
//	protected final FloatArray segmentAngles2 = new FloatArray();
//	protected final FloatArray segmentLengths2 = new FloatArray();

	protected final float[] startX;
	protected final float[] startY;
	protected final float[] endX;
	protected final float[] endY;
//	protected final float[] startX2;
//	protected final float[] startY2;
//	protected final float[] endX2;
//	protected final float[] endY2;
//	protected float segments2[];
//	protected int m_index2 = 0;
//	protected float[] mx2;
//	protected float[] my2;
//	protected float[] f2;
//	protected Mesh lightMesh2;
//	protected Mesh softShadowMesh2;

	protected final Vector2 bodyPosition = new Vector2();
	protected final Vector2 tmpEnd = new Vector2();
	protected final Vector2 tmpStart = new Vector2();
	protected final Vector2 tmpPerp = new Vector2();
//	protected final Vector2 tmpEnd2 = new Vector2();
//	protected final Vector2 tmpStart2 = new Vector2();
	protected final Vector2 tmpVec = new Vector2();

	protected final Matrix3 zeroPosition = new Matrix3();
	protected final Matrix3 rotateAroundZero = new Matrix3();
	protected final Matrix3 restorePosition = new Matrix3();

	protected final Rectangle chainLightBounds = new Rectangle();
	protected final Rectangle rayHandlerBounds = new Rectangle();
	
//	public ChainLight(LightLayer layer, int rays, Color color,
//			float distance, float[] chain) {
//		this(layer, rays, color, distance, 0, chain);
//	}

	/**
	 * Creates chain light from specified vertices
	 * 
	 * @param rayHandler
	 *            not {@code null} instance of RayHandler
	 * @param rays
	 *            number of rays - more rays make light to look more realistic
	 *            but will decrease performance, can't be less than MIN_RAYS
	 * @param color
	 *            color, set to {@code null} to use the default color
	 * @param distance
	 *            distance of light
	 * @param rayDirection
	 *            direction of rays
	 *            <ul>
	 *            <li>1 = left</li>
	 *            <li>-1 = right</li>
	 *            </ul>
	 * @param chain
	 *            float array of (x, y) vertices from which rays will be
	 *            evenly distributed
	 */
	public ChainLight(LightLayer layer, int rays, Color color,
			float distance, int rayDirection, float[] chain) {
		
		super(layer, rays, color, distance, 0f);
		rayStartOffset = ChainLight.defaultRayStartOffset;
		this.rayDirection = rayDirection;
//		if (rayDirection == 0) {
//			rayBidirectional = true; this.rayDirection = 1; }
		vertexNum = (vertexNum - 1) * 2;
		
		endX = new float[rays];
		endY = new float[rays];
		startX = new float[rays];
		startY = new float[rays];
//		endX2 = new float[rays];
//		endY2 = new float[rays];
//		startX2 = new float[rays];
//		startY2 = new float[rays];
		this.chain = (chain != null) ?
					 new FloatArray(chain) : new FloatArray();

		Mesh.VertexDataType vertexDataType = Mesh.VertexDataType.VertexArray;
		if (Gdx.gl30 != null) {
			vertexDataType = VertexDataType.VertexBufferObjectWithVAO;
		}

		lightMesh = new Mesh(
				vertexDataType, false, vertexNum, 0,
				new VertexAttribute(Usage.Position, 2, "vertex_positions"),
				new VertexAttribute(Usage.ColorPacked, 4, "quad_colors"),
				new VertexAttribute(Usage.Generic, 1, "s"));
		softShadowMesh = new Mesh(
				vertexDataType, false, vertexNum * 2,
				0, new VertexAttribute(Usage.Position, 2, "vertex_positions"),
				new VertexAttribute(Usage.ColorPacked, 4, "quad_colors"),
				new VertexAttribute(Usage.Generic, 1, "s"));
//		lightMesh2 = new Mesh(
//				vertexDataType, false, vertexNum, 0,
//				new VertexAttribute(Usage.Position, 2, "vertex_positions"),
//				new VertexAttribute(Usage.ColorPacked, 4, "quad_colors"),
//				new VertexAttribute(Usage.Generic, 1, "s"));
//		softShadowMesh2 = new Mesh(
//				vertexDataType, false, vertexNum * 2,
//				0, new VertexAttribute(Usage.Position, 2, "vertex_positions"),
//				new VertexAttribute(Usage.ColorPacked, 4, "quad_colors"),
//				new VertexAttribute(Usage.Generic, 1, "s"));
		updateChain();
		setMesh();
	}
	
	@Override
	public void update() {
		if (dirty) {
			updateChain();
			applyAttachment();
		} else {
			updateBody();
		}
		
		if (cull()) return;
		if (staticLight && !dirty) return;
		dirty = false;
		
		updateMesh();
	}
	
	@Override
	public void render() {
		if (rayHandler.culling && culled) return;
		
		rayHandler.lightRenderedLastFrame++;
		lightMesh.render(
			rayHandler.lightShader, GL20.GL_TRIANGLE_STRIP, 0, vertexNum);

//		if (rayBidirectional) {
//			lightMesh2.render(
//					rayHandler.lightShader, GL20.GL_TRIANGLE_STRIP, 0, vertexNum);	
//		}
		if (soft && !xray) {
			softShadowMesh.render(
				rayHandler.lightShader, GL20.GL_TRIANGLE_STRIP, 0, vertexNum);
//			if (rayBidirectional) {
//				softShadowMesh2.render(
//						rayHandler.lightShader, GL20.GL_TRIANGLE_STRIP, 0, vertexNum);
//			}
		}
	}
	
	/**
	 * Draws a polygon, using ray start and end points as vertices
	 */
	public void debugRender(nDrawer.Drawer draw) {
		draw.stroke(0,255,255,255,8f); draw.fill(0,0);
		FloatArray vertices = Pools.obtain(FloatArray.class);
		vertices.clear();
		for (int i = 0; i < rayNum; i++) {
			vertices.addAll(mx[i], my[i]);
		}
		for (int i = rayNum - 1; i > -1; i--) {
			vertices.addAll(startX[i], startY[i]);
		}
		draw.polygon(vertices.shrink());
//		if (rayBidirectional) {
//			vertices.clear();
//			for (int i = 0; i < rayNum; i++) {
//				vertices.addAll(mx2[i], my2[i]);
//			}
//			for (int i = rayNum - 1; i > -1; i--) {
//				vertices.addAll(startX2[i], startY2[i]);
//			}
//			draw.polygon(vertices.shrink());
//		}
		Pools.free(vertices);
	}
	
	@Override
	public void attachToBody(Body body) {
		attachToBody(body, 0f);
	}
	
	/**
	 * Attaches light to specified body with relative direction offset
	 * 
	 * @param body
	 *            that will be automatically followed, note that the body
	 *            rotation angle is taken into account for the light offset
	 *            and direction calculations
	 * @param degrees
	 *            directional relative offset in degrees 
	 */
	public void attachToBody(Body body, float degrees) {
		this.body = body;
		this.bodyPosition.set(body.getPosition());
		bodyAngleOffset = MathUtils.degreesToRadians * degrees;
		bodyAngle = body.getAngle();
		applyAttachment();
		if (staticLight) dirty = true;
	}
	
	@Override
	public Body getBody() {
		return body;
	}
	
	@Override
	public float getX() {
		return tmpPosition.x;
	}
	
	@Override
	public float getY() {
		return tmpPosition.y;
	}
	
	@Override
	public void setPosition(float x, float y) {
		tmpPosition.x = x;
		tmpPosition.y = y;
		if (staticLight) dirty = true;
	}
	
	@Override
	public void setPosition(Vector2 position) {
		tmpPosition.x = position.x;
		tmpPosition.y = position.y;
		if (staticLight) dirty = true;
	}
	
	@Override
	public boolean contains(float x, float y) {
		// fast fail
		if (!this.chainLightBounds.contains(x, y))
			return false;
		// actual check
		FloatArray vertices = Pools.obtain(FloatArray.class);
		vertices.clear();
		
		for (int i = 0; i < rayNum; i++) {
			vertices.addAll(mx[i], my[i]);
		}
		for (int i = rayNum - 1; i > -1; i--) {
			vertices.addAll(startX[i], startY[i]);
		}
//		if (rayBidirectional) {
//			for (int i = 0; i < rayNum; i++) {
//				vertices.addAll(mx2[i], my2[i]);
//			}
//			for (int i = rayNum - 1; i > -1; i--) {
//				vertices.addAll(startX2[i], startY2[i]);
//			}
//		}
		
		int intersects = 0;
		for (int i = 0; i < vertices.size; i += 2) {
			float x1 = vertices.items[i];
			float y1 = vertices.items[i + 1];
			float x2 = vertices.items[(i + 2) % vertices.size];
			float y2 = vertices.items[(i + 3) % vertices.size];
			if (((y1 <= y && y < y2) || (y2 <= y && y < y1)) &&
					x < ((x2 - x1) / (y2 - y1) * (y - y1) + x1))
				intersects++;
		}
		boolean result = (intersects & 1) == 1;

		Pools.free(vertices);
		return result;
	}
	/**
	 * Internal method for mesh update depending on ray number
	 */
	@Override
	void setRayNum(int rays) { 
		if (rays < MIN_RAYS)
			rays = MIN_RAYS;

		rayNum = rays;
		vertexNum = rays + 1;

		segments = new float[vertexNum * 8];
//		segments2 = new float[vertexNum * 8];
		mx = new float[vertexNum];
		my = new float[vertexNum];
		f = new float[vertexNum];
//		mx2 = new float[vertexNum];
//		my2 = new float[vertexNum];
//		f2 = new float[vertexNum];
	}
	
	/**
	 * Sets light distance
	 * 
	 * <p>MIN value capped to 0.1f meter
	 * <p>Actual recalculations will be done only on {@link #update()} call
	 */
	@Override
	public void setDistance(float dist) {
		dist *= RayHandler.gammaCorrectionParameter;
		this.distance = dist < 0.01f ? 0.01f : dist;
		dirty = true;
	}
	
	/** Not applicable for this light type **/
	@Deprecated
	@Override
	public void setDirection(float directionDegree) {
	}
	
	/**
	 * Calculates ray positions and angles along chain. This should be called
	 * any time the number or values of elements changes in {@link #chain}.
	 */
	public void updateChain() {
		Vector2 v1 = Pools.obtain(Vector2.class);
		Vector2 v2 = Pools.obtain(Vector2.class);
		Vector2 vSegmentStart = Pools.obtain(Vector2.class);
		Vector2 vDirection = Pools.obtain(Vector2.class);
		Vector2 vRayOffset = Pools.obtain(Vector2.class);
		Spinor tmpAngle = Pools.obtain(Spinor.class);
		// Spinors used to represent perpendicular angle of each segment
		Spinor previousAngle = Pools.obtain(Spinor.class);
		Spinor currentAngle = Pools.obtain(Spinor.class);
		Spinor nextAngle = Pools.obtain(Spinor.class);
		// Spinors used to represent start, end and interpolated ray
		// angles for a given segment
		Spinor startAngle = Pools.obtain(Spinor.class);
		Spinor endAngle = Pools.obtain(Spinor.class);
		Spinor rayAngle = Pools.obtain(Spinor.class);
		
		int segmentCount = chain.size / 2 - 1;

		segmentAngles.clear();
		segmentLengths.clear();
//		segmentAngles2.clear();
//		segmentLengths2.clear();
		float remainingLength = 0;
//		float remainingLength2 = 0;
		
		for (int i = 0, j = 0; i < chain.size - 2; i += 2, j++) {
			v1.set(chain.items[i + 2], chain.items[i + 3])
				.sub(chain.items[i], chain.items[i + 1]);
			segmentLengths.add(v1.len());
			segmentAngles.add(
				v1.rotate90(rayDirection).angleDeg() * MathUtils.degreesToRadians
			);
			remainingLength += segmentLengths.items[j];
		}
//		if (rayBidirectional) {
//			for (int i = 0, j = 0; i < chain.size - 2; i += 2, j++) {
//					v1.set(chain.items[i + 2], chain.items[i + 3])
//					.sub(chain.items[i], chain.items[i + 1]);
//				segmentLengths2.add(v1.len());
//				segmentAngles2.add(
//					v1.rotate90(-rayDirection).angleDeg() * MathUtils.degreesToRadians
//				);
//				remainingLength2 += segmentLengths2.items[j];
//			}
//		}
		
		int rayNumber = 0;
		int remainingRays = rayNum;
		
		for (int i = 0; i < segmentCount; i++) {
			// get this and adjacent segment angles
			previousAngle.set(
				(i == 0) ?
				segmentAngles.items[i] : segmentAngles.items[i - 1]);
			currentAngle.set(segmentAngles.items[i]);
			nextAngle.set(
				(i == segmentAngles.size - 1) ?
				segmentAngles.items[i] : segmentAngles.items[i + 1]);
			
			// interpolate to find actual start and end angles
			startAngle.set(previousAngle).slerp(currentAngle, 0.5f);
			endAngle.set(currentAngle).slerp(nextAngle, 0.5f);

			int segmentVertex = i * 2;
			vSegmentStart.set(
				chain.items[segmentVertex], chain.items[segmentVertex + 1]);
			vDirection.set(
				chain.items[segmentVertex + 2], chain.items[segmentVertex + 3]
			).sub(vSegmentStart).nor();

			float raySpacing = remainingLength / remainingRays;
			int segmentRays = (i == segmentCount - 1) ?
				remainingRays :
				(int) ((segmentLengths.items[i] / remainingLength) *
						remainingRays);
			
			for (int j = 0; j < segmentRays; j++) {
				float position = j * raySpacing;

				// interpolate ray angle based on position within segment
				rayAngle.set(startAngle).slerp(
					endAngle, position / segmentLengths.items[i]);
				float angle = rayAngle.angle();
				vRayOffset.set(this.rayStartOffset, 0).rotateRad(angle);
				v1.set(vDirection).scl(position).add(vSegmentStart).add(vRayOffset);
				
				this.startX[rayNumber] = v1.x;
				this.startY[rayNumber] = v1.y;
				v2.set(distance, 0).rotateRad(angle).add(v1);
				this.endX[rayNumber] = v2.x;
				this.endY[rayNumber] = v2.y;
				rayNumber++;
			}
			
			remainingRays -= segmentRays;
			remainingLength -= segmentLengths.items[i];
			
		}

//		if (rayBidirectional) {
//			rayNumber = 0;
//			remainingRays = rayNum;
//			
//			for (int i = 0; i < segmentCount; i++) {
//				// get this and adjacent segment angles
//				previousAngle.set(
//					(i == 0) ?
//					segmentAngles2.items[i] : segmentAngles2.items[i - 1]);
//				currentAngle.set(segmentAngles2.items[i]);
//				nextAngle.set(
//					(i == segmentAngles2.size - 1) ?
//					segmentAngles2.items[i] : segmentAngles2.items[i + 1]);
//				
//				// interpolate to find actual start and end angles
//				startAngle.set(previousAngle).slerp(currentAngle, 0.5f);
//				endAngle.set(currentAngle).slerp(nextAngle, 0.5f);
//
//				int segmentVertex = i * 2;
//				vSegmentStart.set(
//					chain.items[segmentVertex], chain.items[segmentVertex + 1]);
//				vDirection.set(
//					chain.items[segmentVertex + 2], chain.items[segmentVertex + 3]
//				).sub(vSegmentStart).nor();
//
//				float raySpacing = remainingLength2 / remainingRays;
//				int segmentRays = (i == segmentCount - 1) ?
//					remainingRays :
//					(int) ((segmentLengths2.items[i] / remainingLength2) *
//							remainingRays);
//				
//				for (int j = 0; j < segmentRays; j++) {
//					float position = j * raySpacing;
//
//					// interpolate ray angle based on position within segment
//					rayAngle.set(startAngle).slerp(
//						endAngle, position / segmentLengths2.items[i]);
//					float angle = rayAngle.angle();
//					vRayOffset.set(this.rayStartOffset, 0).rotateRad(angle);
//					v1.set(vDirection).scl(position).add(vSegmentStart).add(vRayOffset);
//					
//					this.startX2[rayNumber] = v1.x;
//					this.startY2[rayNumber] = v1.y;
//					v2.set(distance, 0).rotateRad(angle).add(v1);
//					this.endX2[rayNumber] = v2.x;
//					this.endY2[rayNumber] = v2.y;
//					rayNumber++;
//				}
//				
//				remainingRays -= segmentRays;
//				remainingLength2 -= segmentLengths2.items[i];
//				
//			}
//		}
		
		Pools.free(v1);
		Pools.free(v2);
		Pools.free(vSegmentStart);
		Pools.free(vDirection);
		Pools.free(vRayOffset);
		Pools.free(previousAngle);
		Pools.free(currentAngle);
		Pools.free(nextAngle);
		Pools.free(startAngle);
		Pools.free(endAngle);
		Pools.free(rayAngle);
		Pools.free(tmpAngle);
	}
	
	/**
	 * Applies attached body initial transform to all lights rays
	 */
	void applyAttachment() {
		if (body == null || staticLight) return;
		
		restorePosition.setToTranslation(bodyPosition);
		rotateAroundZero.setToRotationRad(bodyAngle + bodyAngleOffset);
		for (int i = 0; i < rayNum; i++) {
			tmpVec.set(startX[i], startY[i]).mul(rotateAroundZero).mul(restorePosition);
			startX[i] = tmpVec.x;
			startY[i] = tmpVec.y;
			tmpVec.set(endX[i], endY[i]).mul(rotateAroundZero).mul(restorePosition);
			endX[i] = tmpVec.x;
			endY[i] = tmpVec.y;
			
//			if (rayBidirectional) {
//				tmpVec.set(startX2[i], startY2[i]).mul(rotateAroundZero).mul(restorePosition);
//				startX2[i] = tmpVec.x;
//				startY2[i] = tmpVec.y;
//				tmpVec.set(endX2[i], endY2[i]).mul(rotateAroundZero).mul(restorePosition);
//				endX2[i] = tmpVec.x;
//				endY2[i] = tmpVec.y;
//			}
		}
	}
	
	protected boolean cull() {
		if (!rayHandler.culling) {
			culled = false;
		} else {
			updateBoundingRects();
			culled = chainLightBounds.width > 0 &&
					 chainLightBounds.height > 0 &&
					 !chainLightBounds.overlaps(rayHandlerBounds);
		}
		return culled;
	}
	
	void updateBody() {
		if (body == null || staticLight) return;
	
		final Vector2 vec = body.getPosition();
		tmpVec.set(0, 0).sub(bodyPosition);
		bodyPosition.set(vec);
		zeroPosition.setToTranslation(tmpVec);
		restorePosition.setToTranslation(bodyPosition);
		rotateAroundZero.setToRotationRad(bodyAngle).inv().rotateRad(body.getAngle());
		bodyAngle = body.getAngle();
		
		for (int i = 0; i < rayNum; i++) {
			tmpVec.set(startX[i], startY[i]).mul(zeroPosition).mul(rotateAroundZero)
				.mul(restorePosition);
			startX[i] = tmpVec.x;
			startY[i] = tmpVec.y;

			tmpVec.set(endX[i], endY[i]).mul(zeroPosition).mul(rotateAroundZero)
				.mul(restorePosition);
			endX[i] = tmpVec.x;
			endY[i] = tmpVec.y;

//			if (rayBidirectional) {
//				tmpVec.set(startX2[i], startY2[i]).mul(zeroPosition).mul(rotateAroundZero)
//					.mul(restorePosition);
//				startX2[i] = tmpVec.x;
//				startY2[i] = tmpVec.y;
//	
//				tmpVec.set(endX2[i], endY2[i]).mul(zeroPosition).mul(rotateAroundZero)
//					.mul(restorePosition);
//				endX2[i] = tmpVec.x;
//				endY2[i] = tmpVec.y;
//				
//			}
		}
	}
	
	protected void updateMesh() {
		for (int i = 0; i < rayNum; i++) {
			m_index = i;
			f[i] = 1f;
			tmpEnd.x = endX[i];
			mx[i] = tmpEnd.x;
			tmpEnd.y = endY[i];
			my[i] = tmpEnd.y;
			tmpStart.x = startX[i];
			tmpStart.y = startY[i];
			if (rayHandler.world != null && !xray && !layer.no_raycast) {
				rayHandler.world.rayCast(ray, tmpStart, tmpEnd);
			}
		}
//		if (rayBidirectional) {
//			for (int i = 0; i < rayNum; i++) {
//				m_index2 = i;
//				f2[i] = 1f;
//				tmpEnd2.x = endX2[i];
//				mx2[i] = tmpEnd2.x;
//				tmpEnd2.y = endY2[i];
//				my2[i] = tmpEnd2.y;
//				tmpStart2.x = startX2[i];
//				tmpStart2.y = startY2[i];
//				if (rayHandler.world != null && !xray) {
//					rayHandler.world.rayCast(ray, tmpStart2, tmpEnd2);
//				}			
//			}
//		}
		setMesh();
	}
	
	protected void setMesh() {
		int size = 0;
		for (int i = 0; i < rayNum; i++) {
			segments[size++] = startX[i];
			segments[size++] = startY[i];
			segments[size++] = colorF;
			segments[size++] = 1;
			segments[size++] = mx[i];
			segments[size++] = my[i];
			segments[size++] = colorF;
			segments[size++] = 1 - f[i];
		}
		lightMesh.setVertices(segments, 0, size);
//		if (rayBidirectional) {
//			size = 0;
//			for (int i = 0; i < rayNum; i++) {
//				segments2[size++] = startX2[i];
//				segments2[size++] = startY2[i];
//				segments2[size++] = colorF;
//				segments2[size++] = 1;
//				segments2[size++] = mx2[i];
//				segments2[size++] = my2[i];
//				segments2[size++] = colorF;
//				segments2[size++] = 1 - f2[i];
//			}
//			lightMesh2.setVertices(segments2, 0, size);
//		}
		
		if (!soft || xray) return;

		size = 0;
		for (int i = 0; i < rayNum; i++) {
			segments[size++] = mx[i];
			segments[size++] = my[i];
			segments[size++] = colorF;
			final float s = (1 - f[i]);
			segments[size++] = s;
			tmpPerp.set(mx[i], my[i]).sub(startX[i], startY[i]).nor()
				.scl(softShadowLength * s).add(mx[i], my[i]);
			segments[size++] = tmpPerp.x;
			segments[size++] = tmpPerp.y;
			segments[size++] = zeroColorBits;
			segments[size++] = 0f;
		}
		softShadowMesh.setVertices(segments, 0, size);
//		if (rayBidirectional) {
//			size = 0;
//			for (int i = 0; i < rayNum; i++) {
//				segments2[size++] = mx2[i];
//				segments2[size++] = my2[i];
//				segments2[size++] = colorF;
//				final float s = (1 - f2[i]);
//				segments2[size++] = s;
//				tmpPerp.set(mx2[i], my2[i]).sub(startX2[i], startY2[i]).nor()
//					.scl(softShadowLength * s).add(mx2[i], my2[i]);
//				segments2[size++] = tmpPerp.x;
//				segments2[size++] = tmpPerp.y;
//				segments2[size++] = zeroColorBits;
//				segments2[size++] = 0f;
//			}
//			softShadowMesh2.setVertices(segments2, 0, size);
//		}

		
	}
	
	/** Internal method for bounding rectangle recalculation **/
	protected void updateBoundingRects() {
		float maxX = startX[0];
		float minX = startX[0];
		float maxY = startY[0];
		float minY = startY[0];

		for (int i = 0; i < rayNum; i++) {
			maxX = maxX > startX[i] ? maxX : startX[i];
			maxX = maxX > mx[i] ? maxX : mx[i];
			minX = minX < startX[i] ? minX : startX[i];
			minX = minX < mx[i] ? minX : mx[i];
			maxY = maxY > startY[i] ? maxY : startY[i];
			maxY = maxY > my[i] ? maxY : my[i];
			minY = minY < startY[i] ? minY : startY[i];
			minY = minY < my[i] ? minY : my[i];
		}
//		if (rayBidirectional) {
//			for (int i = 0; i < rayNum; i++) {
//				maxX = maxX > startX2[i] ? maxX : startX2[i];
//				maxX = maxX > mx2[i] ? maxX : mx2[i];
//				minX = minX < startX2[i] ? minX : startX2[i];
//				minX = minX < mx2[i] ? minX : mx2[i];
//				maxY = maxY > startY2[i] ? maxY : startY2[i];
//				maxY = maxY > my2[i] ? maxY : my2[i];
//				minY = minY < startY2[i] ? minY : startY2[i];
//				minY = minY < my2[i] ? minY : my2[i];
//			}
//		}
		chainLightBounds.set(minX, minY, maxX - minX, maxY - minY);
		rayHandlerBounds.set(
			rayHandler.x1, rayHandler.y1,
			rayHandler.x2 - rayHandler.x1, rayHandler.y2 - rayHandler.y1);
	}
	
	
	public class Spinor {
		  float real;
		  float complex;

		  private static final float COSINE_THRESHOLD = 0.001f;

		  public Spinor() {

		  }

		  public Spinor(float angle) {
		    set(angle);
		  }

		  public Spinor(Spinor copyFrom) {
		    set(copyFrom);
		  }

		  public Spinor(float real, float complex) {
		    set(real, complex);
		  }

		  public Spinor set(float angle) {
		    angle /= 2;
		    set((float) Math.cos(angle), (float) Math.sin(angle));
		    return this;
		  }

		  public Spinor set(Spinor copyFrom) {
		    set(copyFrom.real, copyFrom.complex);
		    return this;
		  }

		  public Spinor set(float real, float complex) {
		    this.real = real;
		    this.complex = complex;

		    return this;
		  }

		  public Spinor scale(float t) {
		    real *= t;
		    complex *= t;
		    return this;
		  }

		  public Spinor invert() {
		    complex = -complex;
		    scale(len2());
		    return this;
		  }

		  public Spinor add(Spinor other) {
		    real += other.real;
		    complex += other.complex;
		    return this;
		  }
		  
		  public Spinor add(float angle) {
		    angle /= 2;
		    real += Math.cos(angle);
		    complex += Math.sin(angle);
		    return this;
		  }

		  public Spinor sub(Spinor other) {
		    real -= other.real;
		    complex -= other.complex;
		    return this;
		  }
		  
		  public Spinor sub(float angle) {
		    angle /= 2;
		    real -= Math.cos(angle);
		    complex -= Math.sin(angle);
		    return this;
		  }

		  public float len() {
		    return (float) Math.sqrt(real * real + complex * complex);
		  }

		  public float len2() {
		    return real * real + complex * complex;
		  }

		  public Spinor mul(Spinor other) {
		    set(real * other.real - complex * other.complex, real * other.complex
		        + complex * other.real);
		    return this;
		  }

		  public Spinor nor() {
		    float length = len();
		    real /= length;
		    complex /= length;
		    return this;
		  }

		  public float angle() {
		    return (float) Math.atan2(complex, real) * 2;
		  }

		  public Spinor lerp(Spinor end, float alpha, Spinor tmp) {
		    scale(1 - alpha);
		    tmp.set(end).scale(alpha);
		    add(tmp);
		    nor();
		    return this;
		  }

		  public Spinor slerp(Spinor dest, float t) {
		    float tr, tc, omega, cosom, sinom, scale0, scale1;

		    // cosine
		    cosom = real * dest.real + complex * dest.complex;

		    // adjust signs
		    if (cosom < 0) {
		      cosom = -cosom;
		      tc = -dest.complex;
		      tr = -dest.real;
		    } else {
		      tc = dest.complex;
		      tr = dest.real;
		    }

		    // coefficients
		    if (1f - cosom > COSINE_THRESHOLD) {
		      omega = (float) Math.acos(cosom);
		      sinom = (float) Math.sin(omega);
		      scale0 = (float) Math.sin((1f - t) * omega) / sinom;
		      scale1 = (float) Math.sin(t * omega) / sinom;
		    } else {
		      scale0 = 1f - t;
		      scale1 = t;
		    }

		    // final calculation
		    complex = scale0 * complex + scale1 * tc;
		    real = scale0 * real + scale1 * tr;

		    return this;
		  }
		  
		  @Override public String toString() {
		    StringBuilder result = new StringBuilder();
		    float radians = angle();
		    result.append("radians: ");
		    result.append(radians);
		    result.append(", degrees: ");
		    result.append(radians * MathUtils.radiansToDegrees);
		    return result.toString();
		  }
		}
}
