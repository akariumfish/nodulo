package app;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
//import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;

import box2d.VfxFrameBuffer;
import shaders.BlendFunc;
import shaders.DiffuseShader;
import shaders.DynamicShadowShader;
import shaders.Gaussian;
import shaders.ShadowShader;
import shaders.WithoutShadowShader;
import util.Utl;

class nLightMap {
	private ShaderProgram shadowShader;
	private ShaderProgram pseudo3dShader;
	VfxFrameBuffer frameBuffer;
	private Mesh lightMapMesh;

	private VfxFrameBuffer pingPongBuffer;
	
	private ShaderProgram withoutShadowShader;
	private ShaderProgram blurShader;
	private ShaderProgram diffuseShader;

	VfxFrameBuffer shadowBuffer;

	boolean lightMapDrawingDisabled;

	private final int fboWidth, fboHeight;

	public boolean isDiffuse = false;
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

	final Color ambientLight = new Color();

	boolean shadows = true;

	boolean blur = true;

	int blurNum = 1;


	public void setBlendDef() {
		diffuseBlendFunc.set(GL20.GL_DST_COLOR, GL20.GL_ZERO);
		shadowBlendFunc.set(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
		simpleBlendFunc.set(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		setDiffuseLight(true);
		setShadows(true);
		setAmbientLight(0.0f, 0.0f, 0.0f, 0f);
		light_buffer_clear_color.set(def_light_buffer_clear_color);
		setBlur(true);
		setBlurNum(2);
//		setPseudo3dLight(false, false);
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
//		setPseudo3dLight(true, true);
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
	
	public void setDiffuseLight(boolean useDiffuse) {
		isDiffuse = useDiffuse;
		createShaders();
	}
	
	public void setBlur(boolean blur) {
		this.blur = blur;
	}

	public void setBlurNum(int blurNum) {
		this.blurNum = blurNum;
	}

	public void setShadows(boolean shadows) {
		this.shadows = shadows;
	}

	public void setAmbientLight(float r, float g, float b, float a) {
		this.ambientLight.set(r, g, b, a);
	}
	
	public nLightMap(int fboWidth, int fboHeight) {

		if (fboWidth <= 0)
			fboWidth = 1;
		if (fboHeight <= 0)
			fboHeight = 1;

		this.fboWidth = fboWidth;
		this.fboHeight = fboHeight;

		frameBuffer = new VfxFrameBuffer(Format.RGBA8888);
		frameBuffer.initialize(fboWidth, fboHeight);
		pingPongBuffer = new VfxFrameBuffer(Format.RGBA8888);
		pingPongBuffer.initialize(fboWidth, fboHeight);
		shadowBuffer = new VfxFrameBuffer(Format.RGBA8888);
		shadowBuffer.initialize(fboWidth, fboHeight);

		lightMapMesh = createLightMapMesh();

		createShaders();
		
		
		
		
//		setBlendDef();
//		setBlendLight();
//		setBlendAura();
//		setBlendColor();
//		setBlendVision();
		
		setShadows(false);
		setAmbientLight(0.0f, 0.0f, 0.0f, 0f);
		light_buffer_clear_color.set(def_light_buffer_clear_color);
		setBlur(false);
//		setBlurNum(2);
		
		
	}
	private final ArrayList<Rectangle> scissors = new ArrayList<Rectangle>();

	public void removeScissors() {
		for (Rectangle r : Utl.duplic(App.ap.gui.scissors)) {
			scissors.add(r); ScissorStack.popScissors(); }
		App.ap.gui.scissors.clear();
	}
	public void restoreScissors() {
		for (Rectangle r : Utl.duplic(scissors)) {
			App.ap.gui.scissors.add(r); ScissorStack.pushScissors(r); }
		scissors.clear();
	}

	private Color light_buffer_clear_color = new Color(0f, 0f, 0f, 0f);
	private final Color def_light_buffer_clear_color = new Color(0f, 0f, 0f, 0f);
	public void begin() {

		removeScissors();
		
		Gdx.gl.glDepthMask(false);
		Gdx.gl.glEnable(GL20.GL_BLEND); 

		if (shadows) {
			
			frameBuffer.begin();
			
			Gdx.gl.glClearColor(light_buffer_clear_color.r, light_buffer_clear_color.g, 
					light_buffer_clear_color.b, light_buffer_clear_color.a);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
		}

		simpleBlendFunc.apply();
		
	}
	public void end() {
		if (shadows) {
			
			frameBuffer.end();
			
			if (blur) gaussianBlur(frameBuffer, blurNum);	
			
		}

		restoreScissors();
		
	}

	public void render() {
//		boolean needed = rayHandler.lightRenderedLastFrame > 0;

		if (lightMapDrawingDisabled)
			return;

//		if (rayHandler.pseudo3d) {
//			frameBuffer.getTexture().bind(1);
//			shadowBuffer.getTexture().bind(0);
//		} else {
			frameBuffer.getTexture().bind(0);
//		}

		// at last lights are rendered over scene
		if (shadows) {
			final Color c = ambientLight;
			ShaderProgram shader = shadowShader;
//			if (rayHandler.pseudo3d) {
////				shader = pseudo3dShader;
//				shader.bind();
//				if (RayHandler.isDiffuse) {
//					rayHandler.diffuseBlendFunc.apply();
//					shader.setUniformf("ambient", c.r, c.g, c.b, c.a);
//				} else {
//					rayHandler.shadowBlendFunc.apply();
//					shader.setUniformf("ambient", c.r * c.a, c.g * c.a,
//							c.b * c.a, 1f - c.a);
//				}
//				shader.setUniformi("isDiffuse", RayHandler.isDiffuse ? 1 : 0);
//				shader.setUniformi("u_texture", 1);
//				shader.setUniformi("u_shadows", 0);
//			} else 
			if (isDiffuse) {
				shader = diffuseShader;
				shader.bind();
				diffuseBlendFunc.apply();
				shader.setUniformf("ambient", c.r, c.g, c.b, c.a);
			} else {
				shader.bind();
				shadowBlendFunc.apply();
				shader.setUniformf("ambient", c.r * c.a, c.g * c.a,
						c.b * c.a, 1f - c.a);
			}

			lightMapMesh.render(shader, GL20.GL_TRIANGLE_FAN);
		} else 
//			if (needed) 
			{
			simpleBlendFunc.apply();
			withoutShadowShader.bind();

			lightMapMesh.render(withoutShadowShader, GL20.GL_TRIANGLE_FAN);
		}

		Gdx.gl20.glDisable(GL20.GL_BLEND);
	}

	public void gaussianBlur(VfxFrameBuffer buffer, int blurNum) {
		Gdx.gl20.glDisable(GL20.GL_BLEND);
		for (int i = 0; i < blurNum; i++) {
			buffer.getTexture().bind(0);
			// horizontal
			pingPongBuffer.begin();
			{
				blurShader.bind();
				blurShader.setUniformf("dir", 1f, 0f);
				lightMapMesh.render(blurShader, GL20.GL_TRIANGLE_FAN, 0, 4);

			}
			pingPongBuffer.end();

			pingPongBuffer.getTexture().bind(0);
			// vertical
			buffer.begin();
			{
				blurShader.bind();
				blurShader.setUniformf("dir", 0f, 1f);
				lightMapMesh.render(blurShader, GL20.GL_TRIANGLE_FAN, 0, 4);
			}
//			if (rayHandler.customViewport) {
//				buffer.end();
////				buffer.end(
////					rayHandler.viewportX,
////					rayHandler.viewportY,
////					rayHandler.viewportWidth,
////					rayHandler.viewportHeight);
//			} else {
				buffer.end();
//			}
		}

		Gdx.gl20.glEnable(GL20.GL_BLEND);
	}

	void dispose() {
		disposeShaders();

		lightMapMesh.dispose();

		frameBuffer.dispose();
		shadowBuffer.dispose();
		pingPongBuffer.dispose();
	}

	private boolean shader_setup = false;
	void createShaders() {
		if (shader_setup) return;
		shader_setup = true;
		
		disposeShaders();

//		shadowShader = rayHandler.pseudo3d ? DynamicShadowShader.createShadowShader() : 
//				ShadowShader.createShadowShader();
		
		shadowShader = ShadowShader.createShadowShader();
		pseudo3dShader = DynamicShadowShader.createShadowShader();
		
		diffuseShader = DiffuseShader.createShadowShader();

		withoutShadowShader = WithoutShadowShader.createShadowShader();

		blurShader = Gaussian.createBlurShader(fboWidth, fboHeight);
	}

	private void disposeShaders() {
		if (shadowShader != null)
			shadowShader.dispose();
		if (pseudo3dShader != null)
			pseudo3dShader.dispose();
		if (diffuseShader != null)
			diffuseShader.dispose();
		if (withoutShadowShader != null)
			withoutShadowShader.dispose();
		if (blurShader != null)
			blurShader.dispose();
	}

	private Mesh createLightMapMesh() {
		float[] verts = new float[VERT_SIZE];
		// vertex coord
		verts[X1] = -1;
		verts[Y1] = -1;

		verts[X2] = 1;
		verts[Y2] = -1;

		verts[X3] = 1;
		verts[Y3] = 1;

		verts[X4] = -1;
		verts[Y4] = 1;

		// tex coords
		verts[U1] = 0f;
		verts[V1] = 0f;

		verts[U2] = 1f;
		verts[V2] = 0f;

		verts[U3] = 1f;
		verts[V3] = 1f;

		verts[U4] = 0f;
		verts[V4] = 1f;

		Mesh tmpMesh = new Mesh(true, 4, 0, new VertexAttribute(
				Usage.Position, 2, "a_position"), new VertexAttribute(
				Usage.TextureCoordinates, 2, "a_texCoord"));

		tmpMesh.setVertices(verts);
		return tmpMesh;

	}

	static public final int VERT_SIZE = 16;
	static public final int X1 = 0;
	static public final int Y1 = 1;
	static public final int U1 = 2;
	static public final int V1 = 3;
	static public final int X2 = 4;
	static public final int Y2 = 5;
	static public final int U2 = 6;
	static public final int V2 = 7;
	static public final int X3 = 8;
	static public final int Y3 = 9;
	static public final int U3 = 10;
	static public final int V3 = 11;
	static public final int X4 = 12;
	static public final int Y4 = 13;
	static public final int U4 = 14;
	static public final int V4 = 15;
}
