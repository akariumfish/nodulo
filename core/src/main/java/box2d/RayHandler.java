package box2d;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
//import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.noodle.nodulo.GdxApp;

import aa_nodulo.PlaneApplet;
import aa_nodulo.pView;
import gui.nGUI;
import shaders.BlendFunc;
import shaders.LightShader;
import util.Utl;
import util.nRun;

/**
 * Handler that manages everything related to lights updating and rendering
 * <p>Implements {@link Disposable}
 * @author kalle_h
 */
public class RayHandler implements Disposable {
	
	
	
	
	
	
	
	
	
	

	public static abstract class AbstractLight {

		protected boolean active = true;

		public AbstractLight() { }
		
		public void remove() {}		
		public void remove(boolean doDispose) {}
		public void dispose() {}
		public boolean isActive() { return active; }
		public abstract void setActive(boolean active);
		abstract void update();
		abstract void render();
		public void setIgnoreAttachedBody(boolean flag) { }
		public void attachToBody(Body body) { }
		public void attachToBody(Body body, float x, float y) { }
		public void attachToBody(Body body, float x, float y, float r) { }
		public boolean contains(float x, float y) { return false; }
	}
	public static abstract class BaseLight extends AbstractLight {
		static final Color DefaultColor = new Color(0.75f, 0.75f, 0.5f, 0.75f);
		static final float zeroColorBits = Color.toFloatBits(0f, 0f, 0f, 0f);
		static final float oneColorBits = Color.toFloatBits(1f, 1f, 1f, 1f);
		static final int MIN_RAYS = 3;
		
		public BaseLight(LightLayer layer) {
			this.layer = layer;
			layer.lightList.add(this);
			this.rayHandler = layer.rayHandler;
			rayHandler.lightList.add(this);
		}
		protected boolean ignoreBody = false;
		protected RayHandler rayHandler;
		public LightLayer layer;
		public void remove() { rayHandler.lightList.removeValue(this, true); }		
		public void remove(boolean doDispose) { rayHandler.lightList.removeValue(this, true); }
	}


	/** Gamma correction value used if enabled
	 * TODO: remove final modifier and provide method to change
	 * this default value if needed to anyone? */
	static final float GAMMA_COR = 0.625f;

	static boolean gammaCorrection = false;
	static float gammaCorrectionParameter = 1f;

	/**
	 * TODO: This could be made adaptive to ratio of camera sizes * zoom vs the
	 * CircleShape radius - thus will provide smooth radial shadows while
	 * resizing and zooming in and out
	 */
	static int CIRCLE_APPROX_POINTS = 32;

	static float dynamicShadowColorReduction = 1;

	static int MAX_SHADOW_VERTICES = 64;

	static boolean isDiffuse = false;
	/**
	 * Blend function for lights rendering with both shadows and diffusion
	 * <p>Default: (GL20.GL_DST_COLOR, GL20.GL_ZERO)
	 */
	public final BlendFunc diffuseBlendFunc =
			new BlendFunc(GL20.GL_DST_COLOR, GL20.GL_ZERO);

	/**
	 * Blend function for lights rendering with shadows but without diffusion
	 * <p>Default: (GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)
	 */
	public final BlendFunc shadowBlendFunc =
			new BlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);

	/**
	 * Blend function for lights rendering without shadows and diffusion 
	 * <p>Default: (GL20.GL_SRC_ALPHA, GL20.GL_ONE)
	 */
	public final BlendFunc simpleBlendFunc =
			new BlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

	final Matrix4 combined = new Matrix4();
	final Color ambientLight = new Color();

	/**
	 * This Array contain all the lights.
	 * 
	 * <p>NOTE: DO NOT MODIFY THIS LIST
	 */
//	public final Array<AbstractLight> lightList = new Array<AbstractLight>(false, 16);

	public ArrayList<AbstractLight> map_lights = new ArrayList<AbstractLight>();
	

	public final Array<LightLayer> layerList = new Array<LightLayer>(false, 16);

	/**
	 * This Array contain all the disabled lights.
	 * 
	 * <p>NOTE: DO NOT MODIFY THIS LIST
	 */
//	public final Array<AbstractLight> disabledLights = new Array<AbstractLight>(false, 16);

	LightMap lightMap;
	final ShaderProgram lightShader;
	ShaderProgram customLightShader = null;

	boolean culling = true;
	boolean shadows = true;
	boolean blur = true;

	/** Experimental mode */
	boolean pseudo3d = false;
	boolean shadowColorInterpolation = false;

	int blurNum = 1;

	boolean customViewport = false;
	int viewportX = 0;
	int viewportY = 0;
	int viewportWidth = Gdx.graphics.getWidth();
	int viewportHeight = Gdx.graphics.getHeight();

	/** How many lights passed culling and rendered to scene last time */
	int lightRenderedLastFrame = 0;

	/** camera matrix corners */
	float x1, x2, y1, y2;

	World world;

	VfxFrameBuffer render_buffer;

	PlaneApplet app;
	pBox2d box;
	pView view;
	FalseCam cam;

	static int LIGHT_PIX_SIZE = 2;
	static int LIGHT_DEG_SIZE = 8;
	static int LIGHT_AMB_DIV = 50;

	public final Array<RayHandler.AbstractLight> lightList = 
			new Array<RayHandler.AbstractLight>(false, 16);
	
	public RayHandler(PlaneApplet a, World world) {
		this(a, world, Gdx.graphics.getWidth() / LIGHT_PIX_SIZE, Gdx.graphics
				.getHeight() / LIGHT_PIX_SIZE);
	}

	public RayHandler(PlaneApplet a, World world, int fboWidth, int fboHeight) {
		this.world = world;
		this.app = a; 
		this.view = app.view;
		this.box = app.getSystem(pBox2d.class);
		this.cam = new FalseCam(GdxApp.WIDTH, GdxApp.HEIGHT, this);
		
		gammaCorrection = false;
		pseudo3d = false;
		shadowColorInterpolation = false;

		render_buffer = new VfxFrameBuffer(Pixmap.Format.RGBA8888);
		render_buffer.initialize((int)app.gdx.getscreenwidth(),
				(int)app.gdx.getscreenheight());

		app.gdx.addEventScreen(new nRun() { public void run() {
			render_buffer.reset();
			render_buffer.initialize((int)app.gdx.getscreenwidth(),
					(int)app.gdx.getscreenheight());
			cam.update(app.gdx.getscreenwidth(), app.gdx.getscreenheight());
			resizeFBO(Gdx.graphics.getWidth() / LIGHT_PIX_SIZE, Gdx.graphics
					.getHeight() / LIGHT_PIX_SIZE);
		}});


		resizeFBO(fboWidth, fboHeight);
		lightShader = LightShader.createLightShader();

		setCulling(false);

		setBlendDef();

	}

	/**
	 * Resize the FBO used for intermediate rendering.
	 */
	public void resizeFBO(int fboWidth, int fboHeight) {
		if (lightMap != null) {
			lightMap.dispose();
		}
		lightMap = new LightMap(this, fboWidth, fboHeight);
	}

	public void setBlendDef() {
		diffuseBlendFunc.set(GL20.GL_DST_COLOR, GL20.GL_ZERO);
		shadowBlendFunc.set(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
		simpleBlendFunc.set(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		setDiffuseLight(true);
		setShadows(true);
		setAmbientLight(0.0f, 0.0f, 0.0f, 0f);
		buffer_clear_color.set(def_buffer_clear_color);
		setBlur(true);
		setBlurNum(2);
		setPseudo3dLight(false, false);
	}

	public void setBlendLight() {
		setBlendDef();
		setAmbientLight(0.2f, 0.2f, 0.2f, 1f);
	}

	public void setBlendAura() {
		setBlendDef();
		setAmbientLight(0.1f, 0.1f, 0.1f, 1f);
		shadowBlendFunc.set(GL20.GL_SRC_COLOR, GL20.GL_ONE);
		setDiffuseLight(false);
		setPseudo3dLight(true, false);
	}

	public void setBlendVision() {
		setBlendDef();
	}

	public void setBlendColor() {
		setBlendDef();
		setBlurNum(1);
		setAmbientLight(0.1f, 0.1f, 0.1f, 1f);
		setDiffuseLight(false);
		shadowBlendFunc.set(GL20.GL_DST_COLOR, GL20.GL_ONE);
	}

	public void setBlendSolid() {
		setBlendDef();
		setDiffuseLight(false);
		setBlurNum(1);
		setShadows(false);
		simpleBlendFunc.set(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}




	public ArrayList<Body> transparent = new ArrayList<Body>();


	
	public void beginRender() { 

		app.gdx.drawer.pause_batch();

		prepareCombinedMatrix(view);

		removeScissors();

		render_buffer.begin(); 

		Color c = Utl.color(0,0);
		Gdx.gl.glClearColor(c.r,c.g,c.b,c.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//		//update all lights mesh vertices
//		for (AbstractLight light : lightList) light.update();
//		for (Light light : disabledLights) light.update();
		
	}
	
//	private ArrayList<AbstractLight> temp = new ArrayList<AbstractLight>();
	public void renderLayer(LightLayer layer) { 

		app.gdx.drawer.flush();
		
//		temp.clear();
//		for (AbstractLight l : lightList) temp.add(l);
//		for (AbstractLight l : temp) l.setActive(false);
//		lightList.clear();
//		temp.clear();

		layer.prepareRender();
		if (layer.active) {

////			temp.clear();
//			for (AbstractLight l : layer.lightList) 
////				temp.add(l);
////			for (VoidLight l : temp) 
//				{ l.setActive(true); lightList.add(l); }
////			temp.clear();

//			for (AbstractLight light : lightList) if (light.active) light.update();
			for (AbstractLight light : layer.lightList) light.update();
			
			render_buffer.end();

			lightRenderedLastFrame = 0;

			Gdx.gl.glDepthMask(false);
			Gdx.gl.glEnable(GL20.GL_BLEND); 

			boolean useLightMap = (shadows || blur);
			if (useLightMap) {
				lightMap.frameBuffer.begin();
				Gdx.gl.glClearColor(buffer_clear_color.r, buffer_clear_color.g, 
						buffer_clear_color.b, buffer_clear_color.a);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			}

			simpleBlendFunc.apply();

			lightShader.bind();
			lightShader.setUniformMatrix("u_projTrans", combined);
//			for (AbstractLight light : lightList)
			for (AbstractLight light : layer.lightList)
				if (light instanceof BaseLight) 
//					if (((BaseLight)light).active) 
						((BaseLight)light).render();

			if (useLightMap) {
				lightMap.frameBuffer.end();
			}

			if (useLightMap && pseudo3d) {
				lightMap.shadowBuffer.begin();
				Gdx.gl.glClearColor(buffer_clear_color.r, buffer_clear_color.g, 
						buffer_clear_color.b, buffer_clear_color.a);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//				for (AbstractLight light : lightList) 
				for (AbstractLight light : layer.lightList) 
					if (light instanceof Light) {
						((Light)light).dynamicShadowRender(); }

				lightMap.shadowBuffer.end();
			}

			boolean needed = lightRenderedLastFrame > 0;
			// this way lot less binding
			if (needed && blur)
				lightMap.gaussianBlur(lightMap.frameBuffer, blurNum);
			if (needed && blur && pseudo3d)
				lightMap.gaussianBlur(lightMap.shadowBuffer, blurNum);

			render_buffer.begin(); 
			
			lightMap.render();

		} 
	}

	public void endLayeredRender() { 

		render_buffer.end();

		restoreScissors();

		app.gdx.drawer.spritebatch.begin();

		app.gdx.drawer.spritebatch.draw(render_buffer.getTexture(), 0, 0, 
				app.gdx.getscreenwidth(), 
				app.gdx.getscreenheight(), 
				0, 0, 1, 1);

	}

	/**
	 * Prepare all lights for rendering.
	 */
	private Color buffer_clear_color = new Color(0f, 0f, 0f, 0f);
	private final Color def_buffer_clear_color = new Color(0f, 0f, 0f, 0f);
	public void prepareRender() {
		
	}

	
	private final ArrayList<Rectangle> scissors = new ArrayList<Rectangle>();

	public void removeScissors() {
		for (Rectangle r : Utl.duplic(app.gui.scissors)) {
			scissors.add(r); ScissorStack.popScissors(); }
		app.gui.scissors.clear();
	}
	public void restoreScissors() {
		for (Rectangle r : Utl.duplic(scissors)) {
			app.gui.scissors.add(r); ScissorStack.pushScissors(r); }
		scissors.clear();
	}

	public void prepareCombinedMatrix(pView pview) {
		cam.prepareCombinedMatrix(pview.val_pos.get(), pview.val_view_size.get(), 
				pview.val_cam_pos.get(), pview.val_cam_scale.get(), pview.val_cam_rot.get());
	}
	
	public Matrix4 getCombinedMatrix() { return cam.combined; }
	
	private class FalseCam {
		private final Vector2 position2 = new Vector2();
		private final Vector3 position = new Vector3();
		private final Vector3 direction = new Vector3(0, 0, -1);
		private final Vector3 up = new Vector3(0, 1, 0);
		private final Matrix4 projection = new Matrix4();
		private final Matrix4 view = new Matrix4();
		private final Matrix4 combined = new Matrix4();
		private final float near = 0;
		private final float far = 100;
		private float viewportWidth = 0, viewportHeight = 0;
		private float zoom = 1;
		
		private final RayHandler target;

		public FalseCam(float viewportWidth, float viewportHeight, final RayHandler target) {
			this.target = target;
			this.viewportWidth = viewportWidth;
			this.viewportHeight = viewportHeight;
			direction.set(0f, 0f, -1f);
		}
		public void update(float viewportWidth, float viewportHeight) {
			this.viewportWidth = viewportWidth;
			this.viewportHeight = viewportHeight;
		}
		public void prepareCombinedMatrix(
				Vector2 view_pos, Vector2 view_size, 
				Vector2 cam_pos, float cam_scale, float cam_rot) {
			
			zoom = 1f / cam_scale;
			position2.set(view_pos);
			position2.x += view_size.x / 2.0f;
			position2.y -= view_size.y / 2.0f + nGUI.book.RS;
			position2.sub(app.gdx.getscreenwidth() / 2.0f, app.gdx.getscreenheight() / 2.0f);
			position2.scl(zoom).rotateRad(-cam_rot);
			position2.add(cam_pos).scl(-1f);
			
			position.set(position2.x, position2.y, 0f);
			Vector2 u = new Vector2(0f,1f).rotateRad(-cam_rot);
			up.set(u.x, u.y, 0f);
			projection.setToOrtho(zoom * -viewportWidth / 2, zoom * (viewportWidth / 2), zoom * -(viewportHeight / 2),
					zoom * viewportHeight / 2, near, far);
			view.setToLookAt(direction, up);
			view.translate(-position.x, -position.y, -position.z);
			combined.set(projection);
			Matrix4.mul(combined.val, view.val);
			System.arraycopy(combined.val, 0, target.combined.val, 0, 16);
			final float halfViewPortWidth = app.gdx.getscreenwidth() * zoom * 0.5f;
			target.x1 = cam_pos.x - halfViewPortWidth;
			target.x2 = cam_pos.x + halfViewPortWidth;
			final float halfViewPortHeight = app.gdx.getscreenheight() * zoom * 0.5f;
			target.y1 = cam_pos.y - halfViewPortHeight;
			target.y2 = cam_pos.y + halfViewPortHeight;
		}
	}
	
	
	
	
	/**
	 * Utility method to check if light is on the screen
	 * @param x      - light center x-coord 
	 * @param y      - light center y-coord 
	 * @param radius - maximal light distance
	 * 
	 * @return true if camera screen intersects or contains provided
	 * light, represented by circle/box area
	 */
	boolean intersect(float x, float y, float radius) {
		return (x1 < (x + radius) && x2 > (x - radius) &&
				y1 < (y + radius) && y2 > (y - radius));
	}
	
	/**
	 * Called before light rendering start
	 *
	 * Override this if you are using custom light shader
	 */
	protected void updateLightShader () {

	}

	/**
	 * Called for custom light shader before each light is rendered
	 *
	 * Override this if you are using custom light shader
	 */
	protected void updateLightShaderPerLight (Light light) {

	}

//	/**
//	 * Checks whether the given point is inside of any light volume
//	 * 
//	 * @return true if point is inside of any light volume
//	 */
//	public boolean pointAtLight(float x, float y) {
//		for (AbstractLight light : lightList) {
//			if (light.contains(x, y)) return true;
//		}
//		return false;
//	}
//
//	/**
//	 * Checks whether the given point is outside of all light volumes
//	 * 
//	 * @return true if point is NOT inside of any light volume
//	 */
//	public boolean pointAtShadow(float x, float y) {
//		for (AbstractLight light : lightList) {
//			if (light.contains(x, y)) return false;
//		}
//		return true;
//	}

	/**
	 * Disposes all this rayHandler lights and resources
	 */
	public void dispose() {
		removeAll();
		if (lightMap != null) lightMap.dispose();
		if (lightShader != null) lightShader.dispose();
	}

	/**
	 * Removes and disposes both all active and disabled lights
	 */
	public void removeAll() {
		for (LightLayer l : layerList) {
			l.dispose();
		}
		layerList.clear();
//		for (AbstractLight light : lightList) {
//			light.dispose();
//		}
//		lightList.clear();
//
//		for (AbstractLight light : disabledLights) {
//			light.dispose();
//		}
//		disabledLights.clear();
	}

	/**
	 * Set custom light shader, null to reset to default
	 *
	 * Changes will take effect next time #render() is called
	 */
	public void setLightShader (ShaderProgram customLightShader) {
		this.customLightShader = customLightShader;
	}

	/**
	 * Enables/disables culling.
	 * 
	 * <p>This save CPU and GPU time when the world is bigger than the screen.
	 * 
	 * <p>Default = true
	 */
	public void setCulling(boolean culling) {
		this.culling = culling;
	}

	/**
	 * Enables/disables Gaussian blur.
	 * 
	 * <p>This make lights much more softer and realistic look but cost some
	 * precious shader time. With default FBO size on android cost around 1ms.
	 * 
	 * <p>Default = true
	 * 
	 * @see #setBlurNum(int)
	 */
	public void setBlur(boolean blur) {
		this.blur = blur;
	}

	/**
	 * Sets number of Gaussian blur passes.
	 * 
	 * <p>Blurring can be pretty heavy weight operation, 1-3 should be safe.
	 * Setting this to 0 is the same as disabling it.
	 * 
	 * <p>Default = 1
	 * 
	 * @see #setBlur(boolean)
	 */
	public void setBlurNum(int blurNum) {
		this.blurNum = blurNum;
	}

	/**
	 * Enables/disables shadows
	 */
	public void setShadows(boolean shadows) {
		this.shadows = shadows;
	}

	/**
	 * Sets ambient light brightness. Specifies shadows brightness.
	 * <p>Default = 0
	 * 
	 * @param ambientLight
	 *            shadows brightness value, clamped to [0f; 1f]
	 * 
	 * @see #setAmbientLight(Color)
	 * @see #setAmbientLight(float, float, float, float)
	 */
	public void setAmbientLight(float ambientLight) {
		this.ambientLight.a = MathUtils.clamp(ambientLight, 0f, 1f);
	}

	/**
	 * Sets ambient light color.
	 * Specifies how shadows colored and their brightness.
	 * 
	 * <p>Default = Color(0, 0, 0, 0)
	 * 
	 * @param r
	 *            shadows color red component
	 * @param g
	 *            shadows color green component
	 * @param b
	 *            shadows color blue component
	 * @param a
	 *            shadows brightness component
	 * 
	 * @see #setAmbientLight(float)
	 * @see #setAmbientLight(Color)
	 */
	public void setAmbientLight(float r, float g, float b, float a) {
		this.ambientLight.set(r, g, b, a);
	}

	/**
	 * Sets ambient light color.
	 * Specifies how shadows colored and their brightness.
	 * 
	 * <p>Default = Color(0, 0, 0, 0)
	 * 
	 * @param ambientLightColor
	 * 	          color whose RGB components specify the shadows coloring and
	 *            alpha specify shadows brightness 
	 * 
	 * @see #setAmbientLight(float)
	 * @see #setAmbientLight(float, float, float, float)
	 */
	public void setAmbientLight(Color ambientLightColor) {
		this.ambientLight.set(ambientLightColor);
	}

	/**
	 * Sets physics world to work with for this rayHandler
	 */
	public void setWorld(World world) {
		this.world = world;
	}

	/**
	 * @return if gamma correction is enabled or not
	 */
	public static boolean getGammaCorrection() {
		return gammaCorrection;
	}

	/**
	 * Enables/disables gamma correction.
	 * 
	 * <p><b>This need to be done before creating instance of rayHandler.</b>
	 * 
	 * <p>NOTE: To match the visuals with gamma uncorrected lights the light
	 * distance parameters is modified implicitly.
	 */
	public void applyGammaCorrection(boolean gammaCorrectionWanted) {
		gammaCorrection = gammaCorrectionWanted;
		gammaCorrectionParameter = gammaCorrection ? GAMMA_COR : 1f;
		lightMap.createShaders();
	}

	/**
	 * Enables/disables usage of diffuse algorithm.
	 * 
	 * <p>If set to true lights are blended using the diffuse shader. This is
	 * more realistic model than normally used as it preserve colors but might
	 * look bit darker and also it might improve performance slightly.
	 */
	public void setDiffuseLight(boolean useDiffuse) {
		isDiffuse = useDiffuse;
		lightMap.createShaders();
	}

	public static boolean isDiffuseLight() {
		return isDiffuse;
	}

	public static float getDynamicShadowColorReduction () {
		return dynamicShadowColorReduction;
	}

	/**
	 * Static setters are deprecated, use {@link RayHandlerOptions}
	 */
	@Deprecated
	public static void useDiffuseLight(boolean useDiffuse) {

	}

	/**
	 * Static setters are deprecated, use {@link RayHandlerOptions}
	 */
	@Deprecated
	public static void setGammaCorrection(boolean gammaCorrectionWanted) {

	}

	/**
	 * Sets rendering to custom viewport with specified position and size
	 * <p>Note: you will be responsible for update of viewport via this method
	 * in case of any changes (on resize)
	 */
	public void useCustomViewport(int x, int y, int width, int height) {
		customViewport = true;
		viewportX = x;
		viewportY = y;
		viewportWidth = width;
		viewportHeight = height;
	}

	/**
	 * Sets rendering to default viewport
	 * 
	 * <p>0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()
	 */
	public void useDefaultViewport() {
		customViewport = false;
	}

	/**
	 * /!\ Experimental mode with dynamic shadowing in pseudo-3d world
	 *
	 * @param flag enable pseudo 3d effect
	 */
	public void setPseudo3dLight(boolean flag) {
		setPseudo3dLight(flag, false);
	}

	/**
	 * /!\ Experimental mode with dynamic shadowing in pseudo-3d world
	 *
	 * @param flag enable pseudo 3d effect
	 * @param interpolateShadows interpolate shadow color
	 */
	public void setPseudo3dLight(boolean flag, boolean interpolateShadows) { 
		pseudo3d = flag;
		shadowColorInterpolation = interpolateShadows;

		lightMap.createShaders();
	}

	/**
	 * Enables/disables lightMap automatic rendering.
	 * 
	 * <p>If set to false user needs to use the {@link #getLightMapTexture()}
	 * and render that or use it as a light map when rendering. Example shader
	 * for spriteBatch is given. This is faster way to do if there is not that
	 * much overdrawing or if just couple object need light/shadows.
	 * 
	 * <p>Default = true
	 */
	public void setLightMapRendering(boolean isAutomatic) {
		lightMap.lightMapDrawingDisabled = !isAutomatic;
	}

	/**
	 * Expert functionality
	 * 
	 * @return Texture that contain lightmap texture that can be used as light
	 *         texture in your shaders
	 */
	public Texture getLightMapTexture() {
		return lightMap.frameBuffer.getTexture();
	}

	/**
	 * Expert functionality, no support given
	 * 
	 * @return FrameBuffer that contains lightMap
	 */
	public VfxFrameBuffer getLightMapBuffer() {
		return lightMap.frameBuffer;
	}
}
