package app;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.PixmapPacker.GuillotineStrategy;
import com.badlogic.gdx.graphics.g2d.PixmapPacker.PackStrategy;
import com.badlogic.gdx.graphics.g2d.PixmapPacker.PixmapPackerRectangle;
import com.badlogic.gdx.graphics.g2d.PixmapPacker.SkylineStrategy;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
//import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.noodle.nodulo.GdxApp;

import aa_nodulo.PlaneApplet;
import aa_nodulo.pView;
import box2d.LightLayer.MODE;
import box2d.VfxFrameBuffer;
import gui.nAlign;
import gui.nGUI;
import shaders.BlendFunc;
import shaders.DiffuseShader;
import shaders.DynamicShadowShader;
import shaders.Gaussian;
import shaders.LightShader;
import shaders.ShadowShader;
import shaders.WithoutShadowShader;
import space.earlygrey.shapedrawer.DefaultSideEstimator;
import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;
import util.Utl;
import util.nRun;
import util.nTransform;

public class nDrawer {


	public boolean USE_FX = true;

	public boolean vfx = false;
	public final BitmapFont bitmapfont, bitmapfont_2y;
	public BitmapFont font;
	public PolygonSpriteBatch spritebatch;
	public Texture texture;
	public ShapeDrawer drawer;
	
	public DrawContext context;

	static int LIGHT_PIX_SIZE = 2;
	
	public interface DrawContext {
		public nDrawer getDrawer();
		public Viewport getViewport();
		public Rectangle getScreenRect();
		public OrthographicCamera getCamera();
	}

	public void draw_begin() {
		transf.reset();
		ScreenUtils.clear(color_back);
		context.getViewport().apply(false);
		ready(); begin();
	}
	public void fx() { if (
			!PlaneApplet.BLOCK_NDRAWER_FX && 
			USE_FX) { end(); vfx = true; begin(); } }
	public void noFx() { if (vfx) { end(); vfx = false; begin(); } }

	public void pause_batch() { 
		end(); 
	}
	public void restart_batch() { 
		begin(); 
	}
	
	/** The maximum texture size allowed by generateData, when storing in a texture atlas. Multiple texture pages will be created
	 * if necessary. Default is 1024. */
	static private int maxTextureSize = 1024;
	PixmapPacker packer;
	
	public nDrawer(DrawContext a, boolean fx) {
		context = a; USE_FX = fx;

		spritebatch = new PolygonSpriteBatch();


		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = (int) basetxtSize;
		int size = maxTextureSize;
		PackStrategy packStrategy = new GuillotineStrategy();
		packer = new PixmapPacker(size, size, Format.RGBA8888, 1, false, packStrategy);
		packer.setTransparentColor(parameter.color);
		packer.getTransparentColor().a = 0;
		if (parameter.borderWidth > 0) {
			packer.setTransparentColor(parameter.borderColor);
			packer.getTransparentColor().a = 0; }
		parameter.packer = packer;
		
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE); pixmap.drawPixel(0, 0);
		PixmapPackerRectangle pr = packer.pack("pix",pixmap);
		Array<TextureRegion> regions = new Array<TextureRegion>();
		packer.updateTextureRegions(regions, parameter.minFilter, 
				parameter.magFilter, parameter.genMipMaps);
		TextureRegion region = new TextureRegion(pr.page.getTexture(), 
				pr.getX(), pr.getY(), 1, 1);
		regions.add(region);
		packer.updateTextureRegions(regions, parameter.minFilter, 
				parameter.magFilter, parameter.genMipMaps);
		pixmap.dispose();

		// curve sides :
		int minimumSides = 16;
		int maximumSides = 1000;
		float sideMultiplier = 0.5f;
		drawer = new ShapeDrawer(spritebatch, region, 
				new DefaultSideEstimator(minimumSides, maximumSides, sideMultiplier));
		
		FreeTypeFontGenerator fontgenerator1 = new FreeTypeFontGenerator(
				Gdx.files.internal("Mx437_IBM_BIOS.ttf"));
		bitmapfont = fontgenerator1.generateFont(parameter);
		bitmapfont.setUseIntegerPositions(false);
		bitmapfont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		FreeTypeFontGenerator fontgenerator2 = new FreeTypeFontGenerator(
				Gdx.files.internal("Mx437_IBM_BIOS-2y.ttf"));
		bitmapfont_2y = fontgenerator2.generateFont(parameter);
		bitmapfont_2y.setUseIntegerPositions(false);
		bitmapfont_2y.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		fontgenerator1.dispose();
		fontgenerator2.dispose();

		font = bitmapfont_2y;

		
//		light_constructor();
//		
//
//		render_buffer = new VfxFrameBuffer(Pixmap.Format.RGBA8888);
//		render_buffer.initialize((int)GdxApp.WIDTH,
//				(int)GdxApp.HEIGHT);
//		
//		if (GdxApp.app != null)
//			GdxApp.app.addEventScreen(new nRun() { public void run() {
//				render_buffer.reset();
//				render_buffer.initialize((int)GdxApp.app.getscreenwidth(),
//						(int)GdxApp.app.getscreenheight());
//			}});
//
//		
	}
	public Matrix4 getTransformMatrix() { return spritebatch.getTransformMatrix(); }
	public void dispose() {
		
//		light_dispose();
		
		packer.dispose();
		
		bitmapfont.dispose(); bitmapfont_2y.dispose();
		spritebatch.dispose();
		texture.dispose();
		transf.reset();
	}
	public void resize(int w, int h) {
		
	}
	public void flush() { spritebatch.flush(); }
	public void ready() { 
		
	}
	public void begin() {
		
		
		
		spritebatch.setProjectionMatrix(context.getViewport().getCamera().combined);
//		drawer.updatePixelSize(); 
		drawer.update(); 
//		if (vfx) {
//
//			removeScissors();
//						
//			frameBuffer.begin();
//
//			Gdx.gl.glClearColor(light_buffer_clear_color.r, light_buffer_clear_color.g, 
//					light_buffer_clear_color.b, light_buffer_clear_color.a);
//			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//		}

		//batch begin
		spritebatch.begin();
	}
	public void end() {
		//batch end
		spritebatch.end();
//		if (vfx) {
//			
//			frameBuffer.end();
//			
//			gaussianBlur(frameBuffer, blurNum);
//
//			restoreScissors();
//			
//			spritebatch.begin();
//
//			spritebatch.draw(frameBuffer.getTexture(), 0, 0, 
//					GdxApp.app.getscreenwidth(), 
//					GdxApp.app.getscreenheight(), 
//					0, 0, 1, 1);
//
//			spritebatch.end();
//			
//		}
	}
	public void draw_end() {
		end();

		// cancel all drawing transforms
		transf.reset();
		
//		Utl.logn(""+spritebatch.renderCalls);

	}

	
	
	
	


//	ShaderProgram lightShader;
//	
//	VfxFrameBuffer render_buffer;
//	
//	private ShaderProgram shadowShader;
//	VfxFrameBuffer frameBuffer;
//	private Mesh lightMapMesh;
//
//	private VfxFrameBuffer pingPongBuffer;
//	
//	private ShaderProgram withoutShadowShader;
//	private ShaderProgram blurShader;
//	private ShaderProgram diffuseShader;
//
//	VfxFrameBuffer shadowBuffer;
//
//	private int fboWidth, fboHeight;
//
//	/**
//	 * Blend function for lights rendering with both shadows and diffusion
//	 * <p>Default: (GL20.GL_DST_COLOR, GL20.GL_ZERO)
//	 */
//	public final BlendFunc diffuseBlendFunc =
//			new BlendFunc(GL20.GL_DST_COLOR, GL20.GL_ZERO);
//
//	/**
//	 * Blend function for lights rendering with shadows but without diffusion
//	 * <p>Default: (GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)
//	 */
//	public final BlendFunc shadowBlendFunc =
//			new BlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
//
//	/**
//	 * Blend function for lights rendering without shadows and diffusion 
//	 * <p>Default: (GL20.GL_SRC_ALPHA, GL20.GL_ONE)
//	 */
//	public final BlendFunc simpleBlendFunc =
//			new BlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
//
//	final Color ambientLight = new Color();
//	boolean solid = false;
//	boolean isDiffuse = false;
//	boolean shadows = true;
//	boolean blur = true;
//	int blurNum = 1;
//
//	public void light_constructor() {
//
//		int fboWidth = Gdx.graphics.getWidth() / LIGHT_PIX_SIZE; 
//		int fboHeight = Gdx.graphics.getHeight() / LIGHT_PIX_SIZE;
//		lightShader = LightShader.createLightShader();
//
//		if (fboWidth <= 0)
//			fboWidth = 1;
//		if (fboHeight <= 0)
//			fboHeight = 1;
//
//		this.fboWidth = fboWidth;
//		this.fboHeight = fboHeight;
//
//		frameBuffer = new VfxFrameBuffer(Format.RGBA8888);
//		frameBuffer.initialize(fboWidth, fboHeight);
//		pingPongBuffer = new VfxFrameBuffer(Format.RGBA8888);
//		pingPongBuffer.initialize(fboWidth, fboHeight);
//		shadowBuffer = new VfxFrameBuffer(Format.RGBA8888);
//		shadowBuffer.initialize(fboWidth, fboHeight);
//
//		lightMapMesh = createLightMapMesh();
//
//		createShaders();
//		
//		
//		
//		
////		setBlendDef();
////		setBlendLight();
////		setBlendAura();
////		setBlendColor();
////		setBlendVision();
//		
//		setShadows(false);
//		setAmbientLight(0.0f, 0.0f, 0.0f, 0f);
//		light_buffer_clear_color.set(def_light_buffer_clear_color);
//		setBlur(false);
////		setBlurNum(2);
//		
//		
//	}
//
//	
//	
//	public void test_light(pView v) {
//
//		setup_light(v);
//
//		Color c1 = Utl.color(180,255);
//		Color c2 = Utl.color(180,255);
//		for (int i = 1 ; i < 30 ; i += 2)
//			for (int j = 2 ; j < 30 ; j += 3) {
//				rect(50+i*200,10+j*200,		280,380,c1,c1,c2,c2);
//				rect(50+-i*200,10+j*200,	280,380,c1,c1,c2,c2);
//				rect(50+i*200,10+-j*200,	280,380,c1,c1,c2,c2);
//				rect(50+-i*200,10+-j*200,	280,380,c1,c1,c2,c2);
//			}
//
//		color();
//		
//		c1 = Utl.color(255,0,0,255);
//		c2 = Utl.color(255,0,0,0);
//		for (int i = 1 ; i < 30 ; i += 2)
//			for (int j = 2 ; j < 30 ; j += 3) {
//				rect(i*200,j*200,180,180,c1,c1,c2,c2);
//				rect(-i*200,j*200,180,180,c1,c1,c2,c2);
//				rect(i*200,-j*200,180,180,c1,c1,c2,c2);
//				rect(-i*200,-j*200,180,180,c1,c1,c2,c2);
//			}
//
//		light();
//		
//		c1 = Utl.color(255,255,255,120);
//		c2 = Utl.color(255,255,255,120);
//		for (int i = 1 ; i < 30 ; i += 2)
//			for (int j = 2 ; j < 30 ; j += 3) {
//				rect(i*200,j*200,180,180,c1,c1,c2,c2);
//				rect(-i*200,j*200,180,180,c1,c1,c2,c2);
//				rect(i*200,-j*200,180,180,c1,c1,c2,c2);
//				rect(-i*200,-j*200,180,180,c1,c1,c2,c2);
//			}
//		
//		render_light();
//		
//		begin();
//		
////		noFill(); stroke(0,255,255,255, 2f);
////		for (int i = 1 ; i < 30 ; i += 2)
////			for (int j = 2 ; j < 30 ; j += 3) {
////				rect(i*200,j*200,180,180);
////				rect(-i*200,j*200,180,180);
////				rect(i*200,-j*200,180,180);
////				rect(-i*200,-j*200,180,180);
////			}
//		
//	}
//
//	public void setup_light(pView pview) {
//		spritebatch.flush();
//		
//		spritebatch.end();
//
//		cam.prepareCombinedMatrix(pview.val_pos.get(), pview.val_view_size.get(), 
//				pview.val_cam_pos.get(), pview.val_cam_scale.get(), pview.val_cam_rot.get());
//		
//		render_buffer.begin(); 
//
//		Color c = Utl.color(0,0);
//		Gdx.gl.glClearColor(c.r,c.g,c.b,c.a);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//		render_buffer.end();
//
//		removeScissors();
//		
//		setBlendShape();
//		
//		begin_light();
//		
//		batch_bind_shape();
//		
//	}
//	public void begin_light() {
//		
//		Gdx.gl.glDepthMask(false);
//		Gdx.gl.glEnable(GL20.GL_BLEND); 
//
//		if (shadows) {
//			
//			frameBuffer.begin();
//			
//			Gdx.gl.glClearColor(light_buffer_clear_color.r, light_buffer_clear_color.g, 
//					light_buffer_clear_color.b, light_buffer_clear_color.a);
//			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//			
//		}
//		
//		if (solid) restoreScissors();
//		
//		spritebatch.begin();
//		
//		if (!solid) {
//			transf(false);
//			batch_bind_lights();
//		}
//		else {
//			transf(true);
//			batch_bind_shape();
//		}
//	}
//
//	public void end_light() {
//
////		transf(true);
//		
//		spritebatch.end();
//
//		if (solid) removeScissors();
//		
//		if (shadows) {
//			
//			frameBuffer.end();
//			
//			if (blur) gaussianBlur(frameBuffer, blurNum);	
//			
//		}
//		
//		render_buffer.begin();
//		
//		frameBuffer.getTexture().bind(0);
//		
//		if (shadows) {
//			final Color c = ambientLight;
//			ShaderProgram shader = shadowShader;
//			if (isDiffuse) {
//				shader = diffuseShader;
//				shader.bind();
//				diffuseBlendFunc.apply();
//				shader.setUniformf("ambient", c.r, c.g, c.b, c.a);
//			} else {
//				shader.bind();
//				shadowBlendFunc.apply();
//				shader.setUniformf("ambient", c.r * c.a, c.g * c.a,
//						c.b * c.a, 1f - c.a);
//			}
//
//			lightMapMesh.render(shader, GL20.GL_TRIANGLE_FAN);
//		} else {
//			simpleBlendFunc.apply();
//			withoutShadowShader.bind();
//
//			lightMapMesh.render(withoutShadowShader, GL20.GL_TRIANGLE_FAN);
//		}
//
////		Gdx.gl20.glDisable(GL20.GL_BLEND);
//		
//		render_buffer.end();
//
//	}
//
//	public void render_light() {
//
//		end_light();
//
//		restoreScissors();
//
//		batch_bind_shape();
//		transf(true);
//		
//		spritebatch.begin();
//
//		spritebatch.draw(render_buffer.getTexture(), 0, 0, 
//				GdxApp.app.getscreenwidth(), 
//				GdxApp.app.getscreenheight(), 
//				0, 0, 1, 1);
//
//		spritebatch.end();
//		
//	}
//	
//
//	public void batch_bind_lights() {
//
////		spritebatch.setShader(lightShader);
//		spritebatch.setBlendFunction(simpleBlendFunc.sfactor, 
//				simpleBlendFunc.dfactor);
//		
//		lightShader.bind();
//		lightShader.setUniformMatrix("u_projTrans", combined_mat);
//		simpleBlendFunc.apply();
//		
//	}
//	public void batch_bind_shape() {
//		spritebatch.setShader(null);
//		spritebatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//	}
//	
//	public void light() {
//		end_light();
//		setBlendLight();
//		begin_light();
//		batch_bind_lights();
//	}
//	public void aura() {
//		end_light();
//		setBlendAura();
//		begin_light();
//		batch_bind_lights();
//	}
//	public void color() {
//		end_light();
//		setBlendColor();
//		begin_light();
//		batch_bind_lights();
//	}
//	public void noLight() {
//		end_light();
//		setBlendShape();
//		begin_light();
//		batch_bind_shape();
//	}
//
//	public enum MODE { SHAPE, LIGHT, AURA, COLOR }
//	
//	public MODE mode = MODE.SHAPE;
//	
//	
//	public void setBlendDef() {
//		diffuseBlendFunc.set(GL20.GL_DST_COLOR, GL20.GL_ZERO);
//		shadowBlendFunc.set(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
//		simpleBlendFunc.set(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
//		setDiffuseLight(true);
//		setShadows(true);
//		setSolid(false);
//		setAmbientLight(0.0f, 0.0f, 0.0f, 0f);
//		light_buffer_clear_color.set(def_light_buffer_clear_color);
//		setBlur(true);
//		setBlurNum(2);
//	}
//
//	public void setBlendShape() {
//		setBlendDef();
//		mode = MODE.SHAPE;
//		setSolid(true);
//		setDiffuseLight(false);
//		setShadows(false);
//		setBlur(false);
//	}
//
//	public void setBlendLight() {
//		mode = MODE.LIGHT;
//		setBlendDef();
//		setAmbientLight(0.2f, 0.2f, 0.2f, 1f);
//	}
//
//	public void setBlendAura() {
//		mode = MODE.AURA;
//		setBlendDef();
//		setAmbientLight(0.1f, 0.1f, 0.1f, 1f);
//		shadowBlendFunc.set(GL20.GL_SRC_COLOR, GL20.GL_ONE);
//		setDiffuseLight(false);
//	}
//
//	public void setBlendColor() {
//		mode = MODE.COLOR;
//		setBlendDef();
//		setBlurNum(1);
//		setAmbientLight(0.1f, 0.1f, 0.1f, 1f);
//		setDiffuseLight(false);
//		shadowBlendFunc.set(GL20.GL_DST_COLOR, GL20.GL_ONE);
//	}
//	
//	public void setDiffuseLight(boolean useDiffuse) { isDiffuse = useDiffuse; }
//	public void setBlur(boolean blur) { this.blur = blur; }
//	public void setBlurNum(int blurNum) { this.blurNum = blurNum; }
//	public void setSolid(boolean s) { this.solid = s; }
//	public void setShadows(boolean shadows) { this.shadows = shadows; }
//	public void setAmbientLight(float r, float g, float b, float a) {
//		this.ambientLight.set(r, g, b, a); }
//	
//	
//
//	private final ArrayList<Rectangle> scissors = new ArrayList<Rectangle>();
//
//	public void removeScissors() {
//		for (Rectangle r : Utl.duplic(App.ap.gui.scissors)) {
//			scissors.add(r); ScissorStack.popScissors(); }
//		App.ap.gui.scissors.clear();
//	}
//	public void restoreScissors() {
//		for (Rectangle r : Utl.duplic(scissors)) {
//			App.ap.gui.scissors.add(r); ScissorStack.pushScissors(r); }
//		scissors.clear();
//	}
//
//	private Color light_buffer_clear_color = new Color(0f, 0f, 0f, 0f);
//	private final Color def_light_buffer_clear_color = new Color(0f, 0f, 0f, 0f);
//	
//	public void gaussianBlur(VfxFrameBuffer buffer, int blurNum) {
//		Gdx.gl20.glDisable(GL20.GL_BLEND);
//		for (int i = 0; i < blurNum; i++) {
//			buffer.getTexture().bind(0);
//			// horizontal
//			pingPongBuffer.begin();
//			{
//				blurShader.bind();
//				blurShader.setUniformf("dir", 1f, 0f);
//				lightMapMesh.render(blurShader, GL20.GL_TRIANGLE_FAN, 0, 4);
//
//			}
//			pingPongBuffer.end();
//
//			pingPongBuffer.getTexture().bind(0);
//			// vertical
//			buffer.begin();
//			{
//				blurShader.bind();
//				blurShader.setUniformf("dir", 0f, 1f);
//				lightMapMesh.render(blurShader, GL20.GL_TRIANGLE_FAN, 0, 4);
//			}
//			buffer.end();
//		}
//
//		Gdx.gl20.glEnable(GL20.GL_BLEND);
//	}
//
//	void light_dispose() {
//		disposeShaders();
//
//		lightMapMesh.dispose();
//
//		frameBuffer.dispose();
//		shadowBuffer.dispose();
//		pingPongBuffer.dispose();
//	}
//
//	private boolean shader_setup = false;
//	void createShaders() {
//		if (shader_setup) return;
//		shader_setup = true;
//		
//		disposeShaders();
//		
//		shadowShader = ShadowShader.createShadowShader();
//		
//		diffuseShader = DiffuseShader.createShadowShader();
//
//		withoutShadowShader = WithoutShadowShader.createShadowShader();
//
//		blurShader = Gaussian.createBlurShader(fboWidth, fboHeight);
//	}
//
//	private void disposeShaders() {
//		if (shadowShader != null)
//			shadowShader.dispose();
////		if (pseudo3dShader != null)
////			pseudo3dShader.dispose();
//		if (diffuseShader != null)
//			diffuseShader.dispose();
//		if (withoutShadowShader != null)
//			withoutShadowShader.dispose();
//		if (blurShader != null)
//			blurShader.dispose();
//	}
//
//	private Mesh createLightMapMesh() {
//		float[] verts = new float[VERT_SIZE];
//		// vertex coord
//		verts[X1] = -1;
//		verts[Y1] = -1;
//
//		verts[X2] = 1;
//		verts[Y2] = -1;
//
//		verts[X3] = 1;
//		verts[Y3] = 1;
//
//		verts[X4] = -1;
//		verts[Y4] = 1;
//
//		// tex coords
//		verts[U1] = 0f;
//		verts[V1] = 0f;
//
//		verts[U2] = 1f;
//		verts[V2] = 0f;
//
//		verts[U3] = 1f;
//		verts[V3] = 1f;
//
//		verts[U4] = 0f;
//		verts[V4] = 1f;
//
//		Mesh tmpMesh = new Mesh(true, 4, 0, new VertexAttribute(
//				Usage.Position, 2, "a_position"), new VertexAttribute(
//				Usage.TextureCoordinates, 2, "a_texCoord"));
//
//		tmpMesh.setVertices(verts);
//		return tmpMesh;
//
//	}
//
//	static public final int VERT_SIZE = 16;
//	static public final int X1 = 0;
//	static public final int Y1 = 1;
//	static public final int U1 = 2;
//	static public final int V1 = 3;
//	static public final int X2 = 4;
//	static public final int Y2 = 5;
//	static public final int U2 = 6;
//	static public final int V2 = 7;
//	static public final int X3 = 8;
//	static public final int Y3 = 9;
//	static public final int U3 = 10;
//	static public final int V3 = 11;
//	static public final int X4 = 12;
//	static public final int Y4 = 13;
//	static public final int U4 = 14;
//	static public final int V4 = 15;
//	
//	
//	
//	
//	
//	
//	
//	
//	private final Matrix4 combined_mat = new Matrix4();
//	private final FalseCam cam = new FalseCam();
//
//	private class FalseCam {
//		private final Vector2 position2 = new Vector2();
//		private final Vector3 position = new Vector3();
//		private final Vector3 direction = new Vector3(0, 0, -1);
//		private final Vector3 up = new Vector3(0, 1, 0);
//		private final Matrix4 projection = new Matrix4();
//		private final Matrix4 view = new Matrix4();
//		private final Matrix4 combined = new Matrix4();
//		private final float near = 0;
//		private final float far = 100;
//		private float viewportWidth = 0, viewportHeight = 0;
//		private float zoom = 1;
//		
//		public FalseCam() {
//			this.viewportWidth = GdxApp.WIDTH;
//			this.viewportHeight = GdxApp.HEIGHT;
//			direction.set(0f, 0f, -1f);
//		}
//		public void prepareCombinedMatrix( 
//				Vector2 view_pos, Vector2 view_size, 
//				Vector2 cam_pos, float cam_scale, float cam_rot) {
//			
//			zoom = 1f / cam_scale;
//			position2.set(view_pos);
//			position2.x += view_size.x / 2.0f;
//			position2.y -= view_size.y / 2.0f + nGUI.book.RS;
//			position2.sub(GdxApp.app.getscreenwidth() / 2.0f, GdxApp.app.getscreenheight() / 2.0f);
//			position2.scl(zoom).rotateRad(-cam_rot);
//			position2.add(cam_pos).scl(-1f);
//			
//			position.set(position2.x, position2.y, 0f);
//			Vector2 u = new Vector2(0f,1f).rotateRad(-cam_rot);
//			up.set(u.x, u.y, 0f);
//			projection.setToOrtho(zoom * -viewportWidth / 2, zoom * (viewportWidth / 2), zoom * -(viewportHeight / 2),
//					zoom * viewportHeight / 2, near, far);
//			view.setToLookAt(direction, up);
//			view.translate(-position.x, -position.y, -position.z);
//			combined.set(projection);
//			Matrix4.mul(combined.val, view.val);
//			System.arraycopy(combined.val, 0, combined_mat.val, 0, 16);
//		}
//	}
	
	
	
	
	
	
	
	


	

	public nTransform transf = new nTransform();
	public void transf(boolean b) { transf.setActive(b); }
	public void push() { transf.push(); }
	public void pop() { transf.pop(); }
	public void transf(nTransform t) { transf.transf(t); }
	public void translate(float x, float y) { transf.translate(x, y); }
	public void translate(Vector2 v) { transf.translate(v.x, v.y); }
	public void scale(float s) { transf.scale(s); }
	public void rotate(float s) { transf.rotate(s); }





	public static final char[] Alphabet = {'0','1','2','3','4','5','6','7','8','9',
			'A','B','C','D','E','F','G','H','I','J','K','L','M',
			'N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
			'a','b','c','d','e','f','g','h','i','j','k','l','m',
			'n','o','p','q','r','s','t','u','v','w','x','y','z'};

	private nAlign textAlignmentX = nAlign.CENTER;
	private nAlign textAlignmentY = nAlign.CENTER;
	private float txtSize = basetxtSize;
	private float txtSizeTransf = basetxtSize;
	private static final float basetxtSize = 64;
	// to redo correctly
	public float txtCharSize = txtSize/2f;
	// 	game.font.getBounds(t.subSequence(0,t.length()-1));
	public void setLargeFont() { font = bitmapfont; }
	public void setDefaultFont() { font = bitmapfont_2y; }
	public float textWidth(String t, float s) { 
		txtSizeTransf = s * transf.getScale();
		txtSize = txtSizeTransf;
		txtCharSize = txtSizeTransf/2f;
		if (font == bitmapfont_2y) return txtCharSize * t.length(); 
		else return txtCharSize * t.length() * 1.9f; }
	public float textWidth(char t, float s) { 
		txtSizeTransf = s * transf.getScale();
		txtSize = txtSizeTransf;
		txtCharSize = txtSizeTransf/2f;
		if (font == bitmapfont_2y) return txtCharSize; else return txtCharSize*1.9f; }
	public float textHeight() { return font.getLineHeight(); }
	public nDrawer textAlign(nAlign ax, nAlign ay) {
		textAlignmentX = ax;
		textAlignmentY = ay;
		return this;
	}
	public nDrawer text(String t, Vector2 v, float s) {
		return text(t,v.x,v.y,s,Color.WHITE); }
	public nDrawer text(String t, Vector2 v, float s, Color c) {
		return text(t,v.x,v.y,s,c); }
	public nDrawer text(String t, float x, float y, float s) {
		return text(t,x,y,s,Color.WHITE); }
	public nDrawer text(String t, float x, float y, float s, Color c) {
		txtSizeTransf = s * transf.getScale();
		txtSize = txtSizeTransf;
		txtCharSize = txtSizeTransf/2f;
		float f = txtSize/basetxtSize;
		font.getData().setScale(f);
		float alignmentOffsetX = 0;
		float alignmentOffsetY = 0;
		if (textAlignmentX == nAlign.CENTER) 
			alignmentOffsetX = -textWidth(t,s) / (2.0f * transf.getScale());
		else if (textAlignmentX == nAlign.RIGHT) 
			alignmentOffsetX = -textWidth(t,s) / transf.getScale();
		if (textAlignmentY == nAlign.CENTER) 
			alignmentOffsetY = font.getLineHeight() / (2.0f * transf.getScale());
		else if (textAlignmentY == nAlign.BOTTOM) 
			alignmentOffsetY = font.getLineHeight() / transf.getScale();
		x += alignmentOffsetX;
		y += alignmentOffsetY;
		Vector2 p = transf.transform(x, y);
		font.setColor(c);
		font.draw(spritebatch, t, p.x, p.y);
		return this;
	}





	public void halo(Vector2 p, float r, Color c1, Color c2) {
		int arc = 8;
		float arcrad = ((float)Math.PI) * 2f / (float)arc;
		Vector2 rz1 = new Vector2(r,0);
		Vector2 rz2 = new Vector2(r,0);
		for (int i = 0 ; i < arc ; i++) {
			rz1.set(r,0); rz2.set(r,0);
			rz1.rotateRad(arcrad * i).add(p);
			rz2.rotateRad(arcrad * (i+1)).add(p);
			face(rz1.x,rz1.y,rz2.x,rz2.y,p.x,p.y,c1,c1,c2);
		}
	}



	public nDrawer line(float x1, float y1, float x2, float y2, Color c1, Color c2) {
		Vector2 v1 = transf.transform(x1,y1);
		Vector2 v2 = transf.transform(x2,y2);
		float s = strokeW * transf.getScale();
		if (s > 1) drawer.line(v1.x, v1.y, v2.x, v2.y, s, false, c1, c2);
		else {
			Color a1 = new Color(c1);
			Color a2 = new Color(c2);
			a1.a = a1.a * s;
			a2.a = a2.a * s;
			drawer.line(v1.x, v1.y, v2.x, v2.y, 1f, true, a1, a2); }
		return this; }
	public nDrawer face(float x1, float y1, float x2, float y2, float x3, float y3, 
			Color c1, Color c2, Color c3) {
		Vector2 v1 = transf.transform(x1,y1);
		Vector2 v2 = transf.transform(x2,y2);
		Vector2 v3 = transf.transform(x3,y3);
		drawer.filledTriangle(v1, v2, v3, c1, c2, c3);
		return this; }
	public nDrawer quad(float x1, float y1, float x2, float y2, 
			float x3, float y3, float x4, float y4, 
			Color c1, Color c2, Color c3, Color c4) {
		Vector2 v1 = transf.transform(x1,y1);
		Vector2 v2 = transf.transform(x2,y2);
		Vector2 v3 = transf.transform(x3,y3);
		Vector2 v4 = transf.transform(x4,y4);
		drawer.filledTriangle(v1, v2, v3, c1, c2, c3);
		drawer.filledTriangle(v1, v3, v4, c1, c3, c4);
		return this; }
	public nDrawer rect(float x, float y, float w, float h, 
			Color c1, Color c2, Color c3, Color c4) {
		Vector2 v1 = transf.transform(x,y);
		Vector2 v2 = transf.transform(x+w,y);
		Vector2 v3 = transf.transform(x+w,y+h);
		Vector2 v4 = transf.transform(x,y+h);
		Color d1 = new Color(c1);
		Color d2 = new Color(c2);
		Color d3 = new Color(c3);
		Color d4 = new Color(c4);
		drawer.filledTriangle(v1, v2, v3, d1, d2, d3);
		drawer.filledTriangle(v1, v3, v4, d1, d3, d4);
		return this; }



	public void grid(int size, float cell, final Color[] cl) {
		if (cl.length < size*size) return;
		for (int x = 0 ; x < size - 1 ; x++)
			for (int y = 0 ; y < size - 1 ; y++) {
				face((x*cell), (y*cell), ((x+1)*cell), (y*cell), (x*cell), ((y+1)*cell), 
						cl[x+size*y], cl[(x+1)+size*y], cl[x+size*(y+1)]);
				face(((x+1)*cell), (y*cell), ((x+1)*cell), ((y+1)*cell), 
						(x*cell), ((y+1)*cell), 
						cl[(x+1)+size*y], cl[(x+1)+size*(y+1)], cl[x+size*(y+1)]);
			}
	}
//	public void grid(int size, float cell) {
//		if (color_stack.size() < size*size) return;
//		for (int x = 0 ; x < size - 1 ; x++)
//			for (int y = 0 ; y < size - 1 ; y++) {
//				face((x*cell), (y*cell), ((x+1)*cell), (y*cell), (x*cell), ((y+1)*cell), 
//						color_stack.get(x+size*y), 
//						color_stack.get((x+1)+size*y), 
//						color_stack.get(x+size*(y+1)));
//			}
//	}
//	public void putColor(final ArrayList<Color> cl) {
//		for (Color c : cl) color_stack.add(getColor(c)); }
//	public void putColor(final Color[] cl) {
//		for (Color c : cl) color_stack.add(getColor(c)); }
//	public void putColor(final Color c) { color_stack.add(getColor(c)); }
//	public void putPoint(final ArrayList<Vector2> cl) { for (Vector2 c : cl) point_stack.add(c); }
//	public void putPoint(final Vector2[] cl) { for (Vector2 c : cl) point_stack.add(c); }
//	public void putPoint(final Vector2 c) { point_stack.add(c); }
//	public void resetStack() { color_stack.clear(); point_stack.clear(); }
//
//	private ArrayList<Color> color_stack = new ArrayList<Color>();
//	private ArrayList<Vector2> point_stack = new ArrayList<Vector2>();
//
//	private static HashMap<Integer,Color> colors = new HashMap<Integer,Color>();
//	private Color getColor(Color c) {
//		int id = Utl.rgbToInt((int)(255.0f*c.r), (int)(255.0f*c.g), 
//				(int)(255.0f*c.b), (int)(255.0f*c.a));
//		if (colors.get(id) != null) return colors.get(id);
//		colors.put(id,c);
//		return c;
//	}






	public nDrawer rect(Rectangle n) {
		Rectangle r = transf.transform(n);
		if (do_fill) drawer.filledRectangle(r.x, r.y, r.width, r.height, color_fill);
		if (do_stroke) drawer.rectangle(r.x, r.y, r.width, r.height, color_stroke, strokeW * transf.getScale()); 
		return this; }
	public nDrawer rect(float x, float y, float w, float h) {
		rect(new Rectangle(x,y,w,h)); return this; }

	public nDrawer circle(Rectangle r) {
		r = transf.transform(r);
		circle(r.x+r.width/2f, r.y+r.height/2f, Math.min(r.width, r.height)/2f); return this; }
	public nDrawer circle(float x, float y, float r) {
		if (r <= 0) return this;
		Circle c = transf.transform(new Circle(x,y,r));
		if (do_fill) drawer.filledEllipse(c.x, c.y, c.radius, c.radius, 0, color_fill, color_fill);
		if (do_stroke) { drawer.setColor(color_stroke); drawer.circle(c.x, c.y, c.radius, strokeW * transf.getScale()); } 
		return this; }

	public nDrawer line(Vector2 p1, Vector2 p2) {
		line(p1.x, p1.y, p2.x, p2.y); return this; }
	public nDrawer line(float x1, float y1, float x2, float y2) {
		Vector2 v1 = transf.transform(x1,y1);
		Vector2 v2 = transf.transform(x2,y2);
		float s = strokeW * transf.getScale();
		if (s > 1) drawer.line(v1.x, v1.y, v2.x, v2.y, s, false, 
				color_stroke, color_stroke);
		else {
			Color c = new Color(color_stroke);
			c.a = c.a * s;
			drawer.line(v1.x, v1.y, v2.x, v2.y, 1, true, c, c); }
		return this; }

	public nDrawer polygon(Polygon p) {
		float[] v = p.getTransformedVertices(); polygon(v); return this; }
	public nDrawer polygon(Vector2 v0, Vector2 v1, Vector2 v2) {
		Vector2[] v = new Vector2[3]; v[0] = v0; v[1] = v1; v[2] = v2; polygon(v); return this; }
	public nDrawer polygon(Vector2 v0, Vector2 v1, Vector2 v2, Vector2 v3) {
		Vector2[] v = new Vector2[4]; v[0] = v0; v[1] = v1; v[2] = v2; v[3] = v3; polygon(v); return this; }
	public nDrawer polygon(Vector2[] v) {
		float[] f = new float[v.length * 2];
		for (int i = 0 ; i < v.length ; i++) { f[i*2] = v[i].x; f[(i*2)+1] = v[i].y; }
		polygon(f); return this; }
	public nDrawer polygon(float[] v) {
		for (int i = 0 ; i < v.length ; i += 2) {
			Vector2 p = transf.transform(v[i], v[i+1]); v[i] = p.x; v[i+1] = p.y; }
		if (do_fill) { drawer.setColor(color_fill); drawer.filledPolygon(v); }
		if (do_stroke) { 
			drawer.setColor(color_stroke); 
			drawer.polygon(v, strokeW * transf.getScale(), JoinType.SMOOTH); } 
		return this; }

	public nDrawer diamond(Rectangle r) {
		float[] v = new float[8];
		v[0] = r.x; v[1] = r.y + r.height / 2f;
		v[2] = r.x + r.width / 2f; v[3] = r.y + r.height; 
		v[4] = r.x + r.width; v[5] = r.y + r.height / 2f;
		v[6] = r.x + r.width / 2f; v[7] = r.y;
		polygon(v);
		return this; }

	public Color buffer_clear_color = Color.WHITE;
	public Color color_back = new Color(40);
	Color color_fill = new Color();
	Color color_stroke = new Color();
	private float strokeW = 2;
	private boolean do_fill = true, do_stroke = false;

	public nDrawer fill(Color c) {
		color_fill.set(c); do_fill = true; return this; }
	public nDrawer fill(int c) {
		color_fill.set(Utl.color(c)); do_fill = true; return this; }
	public nDrawer fill(int l, int a) {
		color_fill.set(Utl.color(l,a)); do_fill = true; return this; }
	public nDrawer fill(int r, int g, int b) {
		color_fill.set(Utl.color(r,g,b)); do_fill = true; return this; }
	public nDrawer fill(int r, int g, int b, int a) {
		color_fill.set(Utl.color(r,g,b,a)); do_fill = true; return this; }

	public nDrawer stroke(Color c) {
		color_stroke.set(c); do_stroke = true; return this; }
	public nDrawer stroke(Color c, float w) {
		color_stroke.set(c); strokeW = w; do_stroke = true; return this; }
	public nDrawer stroke(int c) {
		color_stroke.set(Utl.color(c)); do_stroke = true; return this; }
	public nDrawer stroke(int l, float w) {
		color_stroke.set(Utl.color(l,l,l)); strokeW = w; do_stroke = true; return this; }
	public nDrawer stroke(int r, int g, int b) {
		color_stroke.set(Utl.color(r,g,b)); do_stroke = true; return this; }
	public nDrawer stroke(int r, int g, int b, int a) {
		color_stroke.set(Utl.color(r,g,b, a)); do_stroke = true; return this; }
	public nDrawer stroke(int r, int g, int b, int a, float w) {
		color_stroke.set(Utl.color(r,g,b)); strokeW = w; do_stroke = true; return this; }

	public nDrawer noFill() { do_fill = false; return this; }
	public nDrawer noStroke() { do_stroke = false; return this; }
	public void strokeWeight(float strokeW) { this.strokeW = strokeW; }

	public static final float point_size = 1f;
	public nDrawer point(Vector2 v) { return point(v.x,v.y); }
	public nDrawer point(float x, float y) {
		fill(color_fill); noStroke(); circle(x,y,point_size); return this; }
	public nDrawer point(float x, float y, Color c) {
		fill(c); noStroke(); circle(x,y,point_size); return this; }



	public interface Drawer {


		public void flush();

		public void fx();
		public void noFx();

		public Matrix4 getTransformMatrix();
		
		public void transf(boolean b); 
		public void push();  
		public void pop();  
		public void transf(nTransform t);  
		public void translate(float x, float y);  
		public void translate(Vector2 v);  
		public void scale(float s);  
		public void rotate(float s);  

		public float textWidth(String t,float s);  
		public float textWidth(char t,float s);  
		public float textHeight();  
		public void textAlign(nAlign ax, nAlign ay); 
		public void text(String t, Vector2 v, float s); 
		public void text(String t, Vector2 v, float s, Color c); 
		public void text(String t, float x, float y, float s); 
		public void text(String t, float x, float y, float s, Color c); 
		
		public void line(float x1, float y1, float x2, float y2, Color c1, Color c2);
		public void face(float x1, float y1, float x2, float y2, float x3, float y3, 
				Color c1, Color c2, Color c3);

		public void halo(Vector2 p, float r, Color c1, Color c2);
		
		public void rect(Rectangle n); 
		public void rect(float x, float y, float w, float h); 

		public void circle(Rectangle r); 
		public void circle(float x, float y, float r); 

		public void line(Vector2 p1, Vector2 p2); 
		public void line(float x1, float y1, float x2, float y2); 

		public void polygon(Polygon p); 
		public void polygon(Vector2 v0, Vector2 v1, Vector2 v2); 
		public void polygon(Vector2 v0, Vector2 v1, Vector2 v2, Vector2 v3); 
		public void polygon(Vector2[] v); 
		public void polygon(float[] v); 

		public void diamond(Rectangle r); 

		public void fill(Color c); 
		public void fill(int c); 
		public void fill(int l, int a); 
		public void fill(int r, int g, int b); 
		public void fill(int r, int g, int b, int a); 

		public void stroke(Color c); 
		public void stroke(Color c, float w); 
		public void stroke(int c); 
		public void stroke(int l, float w); 
		public void stroke(int r, int g, int b); 
		public void stroke(int r, int g, int b, int a); 
		public void stroke(int r, int g, int b, int a, float w); 

		public void noFill();  
		public void noStroke();  
		public void strokeWeight(float strokeW);  

		public void point(Vector2 v);  
		public void point(float x, float y); 
		public void point(float x, float y, Color c);

	}
}
