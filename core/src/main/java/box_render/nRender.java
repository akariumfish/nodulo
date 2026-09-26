package box_render;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.noodle.nodulo.GdxApp;

import aa_nodulo.pView;
import box2d.VfxFrameBuffer;
import box2d.pBox2d;
import gui.nGUI;
import shaders.BlendFunc;
import util.Utl;
import util.nRun;

public class nRender {
	
	static int BUFFER_PIX_SIZE = 2;
	
	static final boolean DIFFUSE_BLUR = false;
	
	public final BlendFunc unitBlend =
			new BlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
	public final BlendFunc solidUnitBlend =
			new BlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	
	public final BlendFunc solidRenderBlend =
			new BlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
	public final BlendFunc lightRenderBlend =
			new BlendFunc(GL20.GL_DST_COLOR, GL20.GL_ZERO);
	public final BlendFunc colorRenderBlend =
			new BlendFunc(GL20.GL_DST_COLOR, GL20.GL_ONE);
	public final BlendFunc auraRenderBlend =
			new BlendFunc(GL20.GL_SRC_COLOR, GL20.GL_ONE);

	private final Color colorAmbiant = new Color(0.1f, 0.1f, 0.1f, 1f);
	private final Color lightAmbiant = new Color(0.2f, 0.2f, 0.2f, 1f);
	private final Color auraAmbiant = new Color(0.1f, 0.1f, 0.1f, 1f);
	private final Color solidAmbiant = new Color(0f, 0f, 0f, 0f);

	private final Color buffer_clear_color = new Color(0f, 0f, 0f, 0f);

	
	
	private final Matrix4 combined = new Matrix4();
	private final FalseCam cam;
	private void prepareCombined() {
		prepareCombinedMatrix(box.view);
		combined.set(getCombinedMatrix()); }
	private void prepareCombinedMatrix(pView pview) {
		cam.prepareCombinedMatrix(pview.val_pos.get(), pview.val_view_size.get(), 
				pview.val_cam_pos.get(), pview.val_cam_scale.get(), pview.val_cam_rot.get()); }
	private Matrix4 getCombinedMatrix() { return cam.combined; }
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
		
		private final nRender target;

		FalseCam(float viewportWidth, float viewportHeight, final nRender target) {
			this.target = target;
			this.viewportWidth = viewportWidth;
			this.viewportHeight = viewportHeight;
			direction.set(0f, 0f, -1f);
		}
		void update(float viewportWidth, float viewportHeight) {
			this.viewportWidth = viewportWidth;
			this.viewportHeight = viewportHeight;
		}
		void prepareCombinedMatrix(
				Vector2 view_pos, Vector2 view_size, 
				Vector2 cam_pos, float cam_scale, float cam_rot) {
			
			zoom = 1f / cam_scale;
			position2.set(view_pos);
			position2.x += view_size.x / 2.0f;
			position2.y -= view_size.y / 2.0f + nGUI.book.RS;
			position2.sub(box.app.gdx.getscreenwidth() / 2.0f, box.app.gdx.getscreenheight() / 2.0f);
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
		}
	}
	

	pBox2d box;

	public final nBatch batch;
	public final int AURA,SOLID,COLOR,LIGHT;
	private final Mesh screenMesh;
	private final ShaderProgram unitShader = createUnitShader();
	private final ShaderProgram colorShader = createColorShader();
	private final ShaderProgram lightShader = createLightShader();
	private final ShaderProgram auraShader = createAuraShader();
	private final ShaderProgram solidShader = createSolidShader();
	private ShaderProgram blurShader;
	private VfxFrameBuffer frameBuffer;
	private VfxFrameBuffer fxBuffer;
	private VfxFrameBuffer pingPongBuffer;

	public void dispose() {
		batch.dispose();
		screenMesh.dispose();
		frameBuffer.dispose();
		fxBuffer.dispose();
		pingPongBuffer.dispose();
		unitShader.dispose();
		colorShader.dispose();
		lightShader.dispose();
		blurShader.dispose();
	}

	public nRender(pBox2d _box) {
		box = _box;
		cam = new FalseCam(GdxApp.WIDTH, GdxApp.HEIGHT, this);
		
		// unitCapacity, maxVertices, maxTriangles
		batch = new nBatch(256, 4096, 2048)
				.positionAttribute("vertex_positions")
				.colorAttribute("quad_colors")
				.genericAttribute("s")
				.finish();
		
		AURA = batch.newGroup(); SOLID = batch.newGroup(); 
		COLOR = batch.newGroup(); LIGHT = batch.newGroup(); 
		
		screenMesh = createScreenMesh();
		
		create_buffers(Gdx.graphics.getWidth() / BUFFER_PIX_SIZE, Gdx.graphics
				.getHeight() / BUFFER_PIX_SIZE);

		box.app.gdx.addEventScreen(new nRun() { public void run() {
			resize(Gdx.graphics.getWidth() / BUFFER_PIX_SIZE, 
					Gdx.graphics.getHeight() / BUFFER_PIX_SIZE); }});
		
	}

	private void create_buffers(int fboWidth, int fboHeight) {
		blurShader = createBlurShader(DIFFUSE_BLUR, fboWidth, fboHeight);
		pingPongBuffer = new VfxFrameBuffer(Format.RGBA8888);
		pingPongBuffer.initialize(fboWidth, fboHeight);
		fxBuffer = new VfxFrameBuffer(Format.RGBA8888);
		fxBuffer.initialize(fboWidth, fboHeight);
		frameBuffer = new VfxFrameBuffer(Pixmap.Format.RGBA8888);
		frameBuffer.initialize((int)box.app.gdx.getscreenwidth(),
				(int)box.app.gdx.getscreenheight());
		cam.update((int)box.app.gdx.getscreenwidth(),
			(int)box.app.gdx.getscreenheight());
	}

	public void resize(int fboWidth, int fboHeight) {
		frameBuffer.resize((int)box.app.gdx.getscreenwidth(),
				(int)box.app.gdx.getscreenheight());
		blurShader = createBlurShader(DIFFUSE_BLUR, fboWidth, fboHeight);
		pingPongBuffer.resize(fboWidth, fboHeight);
		fxBuffer.resize(fboWidth, fboHeight);
	}

	private Mesh createScreenMesh() {
		float[] verts = new float[VERT_SIZE];
		verts[X1] = -1; 	verts[Y1] = -1;	verts[U1] = 0f; verts[V1] = 0f;
		verts[X2] = 1; 	verts[Y2] = -1;	verts[U2] = 1f; verts[V2] = 0f;
		verts[X3] = 1; 	verts[Y3] = 1;	verts[U3] = 1f; verts[V3] = 1f;
		verts[X4] = -1; 	verts[Y4] = 1;	verts[U4] = 0f; verts[V4] = 1f;
		return new Mesh(true, 4, 0, 
				new VertexAttribute(Usage.Position, 2, "a_position"), 
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoord"))
			.setVertices(verts);
	}

	static public final int VERT_SIZE = 16;
	static public final int X1 = 0, Y1 = 1, U1 = 2, V1 = 3;
	static public final int X2 = 4, Y2 = 5, U2 = 6, V2 = 7;
	static public final int X3 = 8, Y3 = 9, U3 = 10, V3 = 11;
	static public final int X4 = 12, Y4 = 13, U4 = 14, V4 = 15;
	
	public void render() {
		box.app.gdx.drawer.pause_batch();

		batch.begin();
		batch.update();
		
		prepareCombined();
		removeScissors();
		
		frameBuffer.begin(); 
		Gdx.gl.glClearColor(0f,0f,0f,0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//ground
		
		frameBuffer.end();
		
		Gdx.gl.glDepthMask(false);
		Gdx.gl.glEnable(GL20.GL_BLEND); 

		groupRender(AURA, unitBlend, auraShader, auraRenderBlend, 
				auraAmbiant, 2);
		groupRender(SOLID, solidUnitBlend, solidShader, solidRenderBlend, 
				solidAmbiant, 1);
		groupRender(COLOR, unitBlend, colorShader, colorRenderBlend, 
				colorAmbiant, 1);
		groupRender(LIGHT, unitBlend, lightShader, lightRenderBlend, 
				lightAmbiant, 2);
		
		Gdx.gl20.glDisable(GL20.GL_BLEND);
		
		restoreScissors();

		box.app.gdx.drawer.spritebatch.begin();

		box.app.gdx.drawer.spritebatch.draw(frameBuffer.getTexture(), 0, 0, 
				box.app.gdx.getscreenwidth(), 
				box.app.gdx.getscreenheight(), 
				0, 0, 1, 1);

	}
	
	private void groupRender(int g, final BlendFunc unitblend,
			final ShaderProgram rendshader, final BlendFunc rendblend, 
			final Color ambiant, int blur) {
		renderGroupToBuffer(g, unitblend, buffer_clear_color);
		if (blur > 0) gaussianBlur(fxBuffer,blur);
		frameBuffer.begin(); 
		fxrender(rendshader, rendblend, ambiant);
		frameBuffer.end();
	}

	private void renderGroupToBuffer(int g, final BlendFunc blend, final Color buff_clr_color) {
		fxBuffer.begin();
		Gdx.gl.glClearColor(buff_clr_color.r, buff_clr_color.g, 
				buff_clr_color.b, buff_clr_color.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		blend.apply();
		unitShader.bind();
		unitShader.setUniformMatrix("u_projTrans", combined);
		batch.push(g);
		batch.render(unitShader);
		fxBuffer.end();
	}

	private void fxrender(final ShaderProgram shader, 
			final BlendFunc blend, final Color c) {
		fxBuffer.getTexture().bind(0);
		blend.apply();
		shader.bind();
		if (shader == colorShader) 
			colorShader.setUniformf("ambient", c.r * c.a, c.g * c.a,
				c.b * c.a, 1f - c.a);
		if (shader == lightShader) 
			lightShader.setUniformf("ambient", c.r * c.a, c.g * c.a,
				c.b * c.a, 1f - c.a);
		screenMesh.render(shader, GL20.GL_TRIANGLE_FAN); 
	}

	private void gaussianBlur(VfxFrameBuffer buffer, int blurNum) {
		Gdx.gl20.glDisable(GL20.GL_BLEND);
		for (int i = 0; i < blurNum; i++) {
			// horizontal
			buffer.getTexture().bind(0);
			pingPongBuffer.begin(); {
				blurShader.bind();
				blurShader.setUniformf("dir", 1f, 0f);
				screenMesh.render(blurShader, GL20.GL_TRIANGLE_FAN, 0, 4);
			} pingPongBuffer.end();
			// vertical
			pingPongBuffer.getTexture().bind(0);
			buffer.begin(); {
				blurShader.bind();
				blurShader.setUniformf("dir", 0f, 1f);
				screenMesh.render(blurShader, GL20.GL_TRIANGLE_FAN, 0, 4);
			} buffer.end();
		}
		Gdx.gl20.glEnable(GL20.GL_BLEND);
	}

	private final ArrayList<Rectangle> scissors = new ArrayList<Rectangle>();
	private void removeScissors() {
		for (Rectangle r : Utl.duplic(box.app.gui.scissors)) {
			scissors.add(r); ScissorStack.popScissors(); }
		box.app.gui.scissors.clear();
	}
	private void restoreScissors() {
		for (Rectangle r : Utl.duplic(scissors)) {
			box.app.gui.scissors.add(r); ScissorStack.pushScissors(r); }
		scissors.clear();
	}

	private static final ShaderProgram createUnitShader() {
		final String vertexShader = "#version 330 core\n"
			+ "attribute vec4 vertex_positions;\n" //
			+ "attribute vec4 quad_colors;\n" //
			+ "attribute float s;\n"
			+ "uniform mat4 u_projTrans;\n" //
			+ "varying vec4 v_color;\n" //				
			+ "void main()\n" //
			+ "{\n" //
			+ "   v_color = s * quad_colors;\n" //				
			+ "   gl_Position =  u_projTrans * vertex_positions;\n" //
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
		ShaderProgram.pedantic = false;
		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
		if(!shader.isCompiled()){ Gdx.app.log("ERROR : shader not compiled", shader.getLog()); }
		return shader;
	}
	
	private static final String vertexShader() {
		return new String("#version 330 core\n"
			+ "attribute vec4 a_position;\n" //
			+ "attribute vec2 a_texCoord;\n" //
			+ "varying vec2 v_texCoords;\n" //
			+ "\n" //
			+ "void main()\n" //
			+ "{\n" //
			+ "   v_texCoords = a_texCoord;\n" //
			+ "   gl_Position = a_position;\n" //
			+ "}\n"); }

	private static final ShaderProgram createSolidShader() {
		final String fragmentShader = "#version 330 core\n"
			+ "#ifdef GL_ES\n" //
			+ "precision lowp float;\n" //
			+ "#define MED mediump\n"
			+ "#else\n"
			+ "#define MED \n"
			+ "#endif\n" //
			+ "varying MED vec2 v_texCoords;\n" //
			+ "uniform sampler2D u_texture;\n" //
			+ "void main()\n"//
			+ "{\n" //
			+ "    gl_FragColor = texture2D(u_texture, v_texCoords);\n"				
			+ "}\n";
		ShaderProgram.pedantic = false;
		ShaderProgram shader = new ShaderProgram(vertexShader(), fragmentShader);
		if(!shader.isCompiled()){ Gdx.app.log("ERROR : shader not compiled", shader.getLog()); }
		return shader;
	}

	private static final ShaderProgram createColorShader() {
		final String fragmentShader = "#version 330 core\n"
			+ "#ifdef GL_ES\n" //
			+ "precision lowp float;\n" //
			+ "#define MED mediump\n"
			+ "#else\n"
			+ "#define MED \n"
			+ "#endif\n" //
			+ "varying MED vec2 v_texCoords;\n" //
			+ "uniform sampler2D u_texture;\n" //
			+ "uniform vec4 ambient;\n"				
			+ "void main()\n"//
			+ "{\n" //
			+ "  vec4 c = texture2D(u_texture, v_texCoords);\n"//
			+ "  gl_FragColor.rgb = c.rgb * c.a + ambient.rgb;\n"//
			+ "  gl_FragColor.a = ambient.a - c.a;\n"//				
			+ "}\n";
		ShaderProgram.pedantic = false;
		ShaderProgram shader = new ShaderProgram(vertexShader(), fragmentShader);
		if(!shader.isCompiled()) { Gdx.app.log("ERROR : shader not compiled", shader.getLog()); }
		return shader;
	}
	

	private static final ShaderProgram createLightShader() {
		// this is always perfect precision
		final String fragmentShader = "#version 330 core\n"
			+ "#ifdef GL_ES\n" //
			+ "precision lowp float;\n" //
			+ "#define MED mediump\n"				
			+ "#else\n"				
			+ "#define MED \n"
			+ "#endif\n" //
			+ "varying MED vec2 v_texCoords;\n" //
			+ "uniform sampler2D u_texture;\n" //
			+ "uniform vec4 ambient;\n"
			+ "void main()\n"//
			+ "{\n" //
			+ "  gl_FragColor.rgb = (ambient.rgb + texture2D(u_texture, v_texCoords).rgb);\n"
			+ "  gl_FragColor.a = 1.0;\n"
			+ "}\n";
		ShaderProgram.pedantic = false;
		ShaderProgram shader = new ShaderProgram(vertexShader(), fragmentShader);
		if(!shader.isCompiled()) { Gdx.app.log("ERROR : shader not compiled", shader.getLog()); }
		return shader;
	}

	static final public ShaderProgram createAuraShader() {
		// this is always perfect precision
		final String fragmentShader = "#version 330 core\n"
			+ "#ifdef GL_ES\n" //
			+ "precision lowp float;\n" //
			+ "#define MED mediump\n"
			+ "#else\n"
			+ "#define MED \n"
			+ "#endif\n" //
			+ "varying MED vec2 v_texCoords;\n" //
			+ "uniform sampler2D u_texture;\n" //
			+ "uniform vec4 ambient;\n"
			+ "void main()\n"//
			+ "{\n" //
			+ "    vec4 c = texture2D(u_texture, v_texCoords);\n"//
			+ "    gl_FragColor.rgb = ambient.rgb - c.rgb;\n"//
			+ "    gl_FragColor.a = ambient.a;\n"//
			+ "}\n";
		ShaderProgram.pedantic = false;
		ShaderProgram shader = new ShaderProgram(vertexShader(), fragmentShader);
		if(!shader.isCompiled()){ Gdx.app.log("ERROR : shader not compiled", shader.getLog()); }
		return shader;
	}

	private static final ShaderProgram createBlurShader(boolean diffuse, int width, int heigth) {
		final String FBO_W = Integer.toString(width);
		final String FBO_H = Integer.toString(heigth);
		final String rgb = diffuse ? ".rgb" : "";
		final String vertexShader = "#version 330 core\n"
			+ "attribute vec4 a_position;\n" //
			+ "uniform vec2  dir;\n" //
			+ "attribute vec2 a_texCoord;\n" //
			+ "varying vec2 v_texCoords0;\n" //
			+ "varying vec2 v_texCoords1;\n" //
			+ "varying vec2 v_texCoords2;\n" //
			+ "varying vec2 v_texCoords3;\n" //
			+ "varying vec2 v_texCoords4;\n" //
			+ "#define FBO_W "
			+ FBO_W
			+ ".0\n"//
			+ "#define FBO_H "
			+ FBO_H
			+ ".0\n"//
			+ "const vec2 futher = vec2(3.2307692308 / FBO_W, 3.2307692308 / FBO_H );\n" //
			+ "const vec2 closer = vec2(1.3846153846 / FBO_W, 1.3846153846 / FBO_H );\n" //
			+ "void main()\n" //
			+ "{\n" //
			+ "vec2 f = futher * dir;\n" //
			+ "vec2 c = closer * dir;\n" //
			+ "v_texCoords0 = a_texCoord - f;\n" //
			+ "v_texCoords1 = a_texCoord - c;\n" //
			+ "v_texCoords2 = a_texCoord;\n" //
			+ "v_texCoords3 = a_texCoord + c;\n" //
			+ "v_texCoords4 = a_texCoord + f;\n" //
			+ "gl_Position = a_position;\n" //
			+ "}\n";
		final String fragmentShader = "#version 330 core\n"
			+ "#ifdef GL_ES\n" //
			+ "precision lowp float;\n" //
			+ "#define MED mediump\n"
			+ "#else\n"
			+ "#define MED \n"
			+ "#endif\n" //
			+ "uniform sampler2D u_texture;\n" //
			+ "varying MED vec2 v_texCoords0;\n" //
			+ "varying MED vec2 v_texCoords1;\n" //
			+ "varying MED vec2 v_texCoords2;\n" //
			+ "varying MED vec2 v_texCoords3;\n" //
			+ "varying MED vec2 v_texCoords4;\n" //
			+ "const float center = 0.2270270270;\n" //
			+ "const float close  = 0.3162162162;\n" //
			+ "const float far    = 0.0702702703;\n" //
			+ "void main()\n" //
			+ "{	 \n" //
			+ "gl_FragColor"+rgb+" = far    * texture2D(u_texture, v_texCoords0)"+rgb+"\n" //
			+ "	      		+ close  * texture2D(u_texture, v_texCoords1)"+rgb+"\n" //
			+ "				+ center * texture2D(u_texture, v_texCoords2)"+rgb+"\n" //
			+ "				+ close  * texture2D(u_texture, v_texCoords3)"+rgb+"\n" //
			+ "				+ far    * texture2D(u_texture, v_texCoords4)"+rgb+";\n"//
			+ "}\n";
		ShaderProgram.pedantic = false;
		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
		if(!shader.isCompiled()) { Gdx.app.log("ERROR : shader not compiled", shader.getLog()); }
		return shader;
	}

}
