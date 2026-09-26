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
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

import box2d.RayHandler;
import box2d.VfxFrameBuffer;
import box2d.pBox2d;
import box_render.nBatch.Unit;
import shaders.BlendFunc;
import util.Utl;
import util.nRun;

public class nRender {

	static int LIGHT_PIX_SIZE = 2;
	static int LIGHT_DEG_SIZE = 8;
	static int LIGHT_AMB_DIV = 50;
	

	public final BlendFunc diffuseBlendFunc =
			new BlendFunc(GL20.GL_DST_COLOR, GL20.GL_ZERO);
	public final BlendFunc shadowBlendFunc =
			new BlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
	public final BlendFunc simpleBlendFunc =
			new BlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

	final Matrix4 combined = new Matrix4();
	int viewportX = 0;
	int viewportY = 0;
	int viewportWidth = Gdx.graphics.getWidth();
	int viewportHeight = Gdx.graphics.getHeight();

	/** camera matrix corners */
	float x1, x2, y1, y2;

	private Color buffer_clear_color = new Color(0f, 0f, 0f, 0f);


	pBox2d box;

	private final nBatch batch;
	private final int auraGroup, solidGroup, colorGroup, lightGroup;
	private final Mesh screenMesh;
	private final ShaderProgram lightShader;
	private final ShaderProgram shadowShader;
	private final ShaderProgram diffuseShader;
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
		lightShader.dispose();
		shadowShader.dispose();
		diffuseShader.dispose();
		blurShader.dispose();
	}

	public nRender(pBox2d _box) {
		box = _box;

		// unitCapacity, maxVertices, maxTriangles
		batch = new nBatch(256, 4096, 2048)
				.positionAttribute("vertex_positions")
				.colorAttribute("quad_colors")
				.genericAttribute("s")
				.finish();
		
		auraGroup = batch.newGroup(); solidGroup = batch.newGroup(); 
		colorGroup = batch.newGroup(); lightGroup = batch.newGroup(); 
		
		lightShader = createLightShader();
		shadowShader = createShadowShader();
		diffuseShader = createDiffuseShader();
		screenMesh = createScreenMesh();
		
		create_buffers(Gdx.graphics.getWidth() / LIGHT_PIX_SIZE, Gdx.graphics
				.getHeight() / LIGHT_PIX_SIZE);

		box.app.gdx.addEventScreen(new nRun() { public void run() {
			resize(Gdx.graphics.getWidth() / LIGHT_PIX_SIZE, 
					Gdx.graphics.getHeight() / LIGHT_PIX_SIZE); }});
		
		test_part();

	}

	void create_buffers(int fboWidth, int fboHeight) {
		blurShader = createBlurShader(fboWidth, fboHeight);
		pingPongBuffer = new VfxFrameBuffer(Format.RGBA8888);
		pingPongBuffer.initialize(fboWidth, fboHeight);
		fxBuffer = new VfxFrameBuffer(Format.RGBA8888);
		fxBuffer.initialize(fboWidth, fboHeight);
		frameBuffer = new VfxFrameBuffer(Pixmap.Format.RGBA8888);
		frameBuffer.initialize((int)box.app.gdx.getscreenwidth(),
				(int)box.app.gdx.getscreenheight());
	}

	public void resize(int fboWidth, int fboHeight) {
		frameBuffer.resize((int)box.app.gdx.getscreenwidth(),
				(int)box.app.gdx.getscreenheight());
		blurShader = createBlurShader(fboWidth, fboHeight);
		pingPongBuffer.resize(fboWidth, fboHeight);
		fxBuffer.resize(fboWidth, fboHeight);
	}

	private Mesh createScreenMesh() {
		float[] verts = new float[VERT_SIZE];
		verts[X1] = -1; verts[Y1] = -1;	verts[U1] = 0f; verts[V1] = 0f;
		verts[X2] = 1; verts[Y2] = -1;	verts[U2] = 1f; verts[V2] = 0f;
		verts[X3] = 1; verts[Y3] = 1;	verts[U3] = 1f; verts[V3] = 1f;
		verts[X4] = -1; verts[Y4] = 1;	verts[U4] = 0f; verts[V4] = 1f;
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
	
	
	private final ArrayList<Rectangle> scissors = new ArrayList<Rectangle>();
	public void removeScissors() {
		for (Rectangle r : Utl.duplic(box.app.gui.scissors)) {
			scissors.add(r); ScissorStack.popScissors(); }
		box.app.gui.scissors.clear();
	}
	public void restoreScissors() {
		for (Rectangle r : Utl.duplic(scissors)) {
			box.app.gui.scissors.add(r); ScissorStack.pushScissors(r); }
		scissors.clear();
	}

	public void shadowrender(VfxFrameBuffer buffer, Color ambientLight) {
		buffer.getTexture().bind(0);

		final Color c = ambientLight;
		shadowShader.bind();
		shadowBlendFunc.apply();
		shadowShader.setUniformf("ambient", c.r * c.a, c.g * c.a,
				c.b * c.a, 1f - c.a);

		screenMesh.render(shadowShader, GL20.GL_TRIANGLE_FAN); 
		
		Gdx.gl20.glDisable(GL20.GL_BLEND);
	}

	public void diffuserender(VfxFrameBuffer buffer, Color ambientLight) {
		buffer.getTexture().bind(0);

		final Color c = ambientLight;
		diffuseShader.bind();
		diffuseBlendFunc.apply();
		diffuseShader.setUniformf("ambient", c.r, c.g, c.b, c.a);
		
		screenMesh.render(diffuseShader, GL20.GL_TRIANGLE_FAN); 
		
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
				screenMesh.render(blurShader, GL20.GL_TRIANGLE_FAN, 0, 4);
			}
			pingPongBuffer.end();

			pingPongBuffer.getTexture().bind(0);
			// vertical
			buffer.begin();
			{
				blurShader.bind();
				blurShader.setUniformf("dir", 0f, 1f);
				screenMesh.render(blurShader, GL20.GL_TRIANGLE_FAN, 0, 4);
			}
			buffer.end();
			
		}

		Gdx.gl20.glEnable(GL20.GL_BLEND);
	}

	public void render() {
		box.app.gdx.drawer.pause_batch();

//		prepareCombinedMatrix(view);

		removeScissors();

		frameBuffer.begin(); 

		Color c = Utl.color(0,0);
		Gdx.gl.glClearColor(c.r,c.g,c.b,c.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		

		box.app.gdx.drawer.flush();
		
//		layer.prepareRender();
//		if (layer.active) {
			
//			for (AbstractLight light : layer.lightList) light.update();
			
			frameBuffer.end();

			Gdx.gl.glDepthMask(false);
			Gdx.gl.glEnable(GL20.GL_BLEND); 

//			boolean useLightMap = (shadows || blur);
//			if (useLightMap) {
//				lightMap.frameBuffer.begin();
				Gdx.gl.glClearColor(buffer_clear_color.r, buffer_clear_color.g, 
						buffer_clear_color.b, buffer_clear_color.a);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//			}

			simpleBlendFunc.apply();

			lightShader.bind();
			lightShader.setUniformMatrix("u_projTrans", combined);
			
//			for (AbstractLight light : layer.lightList)
//				if (light instanceof BaseLight) 
//						((BaseLight)light).render();
			
//			box.renderer.batch.setRenderGroup(layer.unitList);
//			box.renderer.batch.render(lightShader);
			
//			if (useLightMap) {
//				lightMap.frameBuffer.end();
//			}

//			if (useLightMap && pseudo3d) {
//				lightMap.shadowBuffer.begin();
//				Gdx.gl.glClearColor(buffer_clear_color.r, buffer_clear_color.g, 
//						buffer_clear_color.b, buffer_clear_color.a);
//				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
////				for (AbstractLight light : lightList) 
//				for (AbstractLight light : layer.lightList) 
//					if (light instanceof Light) {
//						((Light)light).dynamicShadowRender(); }
//
//				lightMap.shadowBuffer.end();
//			}

//			boolean needed = lightRenderedLastFrame > 0;
//			// this way lot less binding
//			if (needed && blur)
//				lightMap.gaussianBlur(lightMap.frameBuffer, blurNum);
//			if (needed && blur && pseudo3d)
//				lightMap.gaussianBlur(lightMap.shadowBuffer, blurNum);

			frameBuffer.begin(); 
			
//			lightMap.render();

//		} 

		frameBuffer.end();

		restoreScissors();

		box.app.gdx.drawer.spritebatch.begin();

		box.app.gdx.drawer.spritebatch.draw(frameBuffer.getTexture(), 0, 0, 
				box.app.gdx.getscreenwidth(), 
				box.app.gdx.getscreenheight(), 
				0, 0, 1, 1);

		

//		app.gdx.drawer.spritebatch.end();
//
////		mesh.setInstanceData(insts, 0, instSize);
//		
//		simpleBlendFunc.apply();
//		shader.bind();
//		shader.setUniformMatrix("u_projTrans", renderer.rayHandler.getCombinedMatrix());
//
//		mesh.render(shader, GL20.GL_TRIANGLES, 0, indSize, true);
//
//		app.gdx.drawer.spritebatch.begin();

	}
	

//	BlendFunc simpleBlendFunc = new BlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
//	Mesh mesh;
//	int vertexNum = 4;
//	int trigNum = 2;
//	int instNum = 1800;
//	protected float vertices[];
//	protected short indices[];
//	protected float insts[];
//	ShaderProgram shader;
//	private int vertSize = 0, indSize = 0, instSize = 0;
//	
//	void test_setup() {
//		
//		vertices = new float[vertexNum * 3];	
//		indices = new short[trigNum * 3];
//		insts = new float[instNum * 2];
//		vertSize = 0; indSize = 0; instSize = 0;
//
//		float c1 = new Color(1f,1f,1f,1f).toFloatBits();
//		float c2 = new Color(1f,1f,0f,1f).toFloatBits();
//
//		pushVert(0f,-20f,c1);
//		pushVert(0f,120f,c2);
//		pushVert(100f,100f,c1);
//		pushVert(100f,0f,c2);
//		pushIndice(0,1,2);
//		pushIndice(0,2,3);
//		
//		int col = 50;
//
//		float ix = -500, iy = 0, is = 200;
//		for (int i = 0 ; i < instNum ; i++)
//			pushInst(ix+is*(i%col),iy+((i-(i%col))/col)*is);
//
//		Mesh.VertexDataType vertexDataType = Mesh.VertexDataType.VertexArray;
////		Mesh.VertexDataType vertexDataType = Mesh.VertexDataType.VertexBufferObject;
////		if (Gdx.gl30 != null) { 
////			vertexDataType = VertexDataType.VertexBufferObjectWithVAO; }
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
//
//		mesh.setVertices(vertices, 0, vertSize);
//		mesh.setIndices(indices, 0, indSize);
//		mesh.setInstanceData(insts, 0, instSize);
//
//		shader = createShader();
//		
////		mesh.bind(shader);
////		int loc = shader.getAttributeLocation("i_position");
////		Utl.logn("i_pos: "+loc);
////		loc = shader.getAttributeLocation("vertex_positions");
////		Utl.logn("v_pos: "+loc);
//		
//	}
//	private void pushVert(float x, float y, float c) {
//		vertices[vertSize++] = x; 
//		vertices[vertSize++] = y; 
//		vertices[vertSize++] = c;
//	}
//	private void pushIndice(int i1, int i2, int i3) {
//		indices[indSize++] = (short)i1; 
//		indices[indSize++] = (short)i2;
//		indices[indSize++] = (short)i3;
//	}
//	private void pushInst(float x, float y) {
//		insts[instSize++] = x; 
//		insts[instSize++] = y;
////		insts[instSize++] = 0f;
//	}
//	
	

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
			if(!shader.isCompiled()) { Utl.logn("ERROR : createShader : " + shader.getLog()); }
//		}
		return shader;
	}

	
	
	

	public void test_part() {

		newParticleModel("part");

	}
		
	public void space_start() {
		newParticle("part", 0, 400, 0, Color.RED.toFloatBits());
		
	}
	
	public nBatch.Unit newParticle(String model, float x, float y, float r, float c) {
		return batch.newUnit(model,x,y,r,c,0); }
	
	public ParticleModel newParticleModel(String ref) {
		batch.addModel(ref, new ParticleModel(this)); return batch.getModel(ref, ParticleModel.class); }
	
	public static class ParticleModel extends nBatch.Model {
		private nRender rend;
		public int life = 50;
		public float speed = 1;
		public ParticleModel(nRender _rend) {
			super(3, 3, 5); rend = _rend;
			useGroup(rend.auraGroup, rend.colorGroup, rend.lightGroup);
		}

		@Override public void update(Unit u) {
			if (u.a(4,u.a(4)+1) > life) { u.clear(); return; }
			u.setTransform(u.a(0),u.a(1),u.a(2));
			u.setTransform(u.a(0)+u.rX(speed,0)*u.a(4),u.a(1)+u.rY(speed,0)*u.a(4),u.a(2));
		}
		@Override public void make(Unit u) {
			u.beginPush();
			u.pushVert(0,0,u.a(3),1f);
			u.pushVert(0,200,u.a(3),1f);
			u.pushVert(200,0,u.a(3),1f);
			u.pushTrig(0,1,2);
		}

		@Override public void destroy(Unit u) {
			
		}
		
	}
	
	
	
	
	
	

	public static ShaderProgram createBlurShader(int width, int heigth) {
		final String FBO_W = Integer.toString(width);
		final String FBO_H = Integer.toString(heigth);
		final String rgb = RayHandler.isDiffuseLight()  ? ".rgb" : "";
		final String vertexShader = "attribute vec4 a_position;\n" //
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
		final String fragmentShader = "#ifdef GL_ES\n" //
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
		ShaderProgram blurShader = new ShaderProgram(vertexShader,
				fragmentShader);
		if (!blurShader.isCompiled()) {
			blurShader = new ShaderProgram("#version 330 core\n" +vertexShader,
					"#version 330 core\n" +fragmentShader);
			if(!blurShader.isCompiled()){
				Gdx.app.log("ERROR", blurShader.getLog());
			}
		}

		return blurShader;
	}

	static final public ShaderProgram createShadowShader() {
		final String vertexShader = "attribute vec4 a_position;\n" //
				+ "attribute vec2 a_texCoord;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "\n" //
				+ "void main()\n" //
				+ "{\n" //
				+ "   v_texCoords = a_texCoord;\n" //
				+ "   gl_Position = a_position;\n" //
				+ "}\n";
		final String fragmentShader = "#ifdef GL_ES\n" //
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
				+ "vec4 c = texture2D(u_texture, v_texCoords);\n"//
				+ "gl_FragColor.rgb = c.rgb * c.a + ambient.rgb;\n"//
				+ "gl_FragColor.a = ambient.a - c.a;\n"//				
				+ "}\n";
		ShaderProgram.pedantic = false;
		ShaderProgram shadowShader = new ShaderProgram(vertexShader,
				fragmentShader);
		if (!shadowShader.isCompiled()) {
			shadowShader = new ShaderProgram("#version 330 core\n" +vertexShader,
					"#version 330 core\n" +fragmentShader);
			if(!shadowShader.isCompiled()){
				Gdx.app.log("ERROR", shadowShader.getLog());
			}
		}

		return shadowShader;
	}
	

	static final public ShaderProgram createDiffuseShader() {
		final String vertexShader = "attribute vec4 a_position;\n" //
				+ "attribute vec2 a_texCoord;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "\n" //
				+ "void main()\n" //
				+ "{\n" //
				+ "   v_texCoords = a_texCoord;\n" //
				+ "   gl_Position = a_position;\n" //
				+ "}\n";

		// this is always perfect precision
		final String fragmentShader = "#ifdef GL_ES\n" //
				+ "precision lowp float;\n" //
				+ "#define MED mediump\n"				
				+ "#else\n"				
				+ "#define MED \n"
				+ "#endif\n" //
				+ "varying MED vec2 v_texCoords;\n" //
				+ "uniform sampler2D u_texture;\n" //
				+ "uniform  vec4 ambient;\n"
					+ "void main()\n"//
				+ "{\n" //
				+ "gl_FragColor.rgb = (ambient.rgb + texture2D(u_texture, v_texCoords).rgb);\n"
				+ "gl_FragColor.a = 1.0;\n"
					+ "}\n";
		ShaderProgram.pedantic = false;
		ShaderProgram shadowShader = new ShaderProgram(vertexShader,
					fragmentShader);
		if (!shadowShader.isCompiled()) {
			shadowShader = new ShaderProgram("#version 330 core\n" +vertexShader,
					"#version 330 core\n" +fragmentShader);
			if(!shadowShader.isCompiled()){
				Gdx.app.log("ERROR", shadowShader.getLog());
			}
		}

		return shadowShader;
	}

	static final public ShaderProgram createLightShader() {
		String gamma = ""; 
		if (RayHandler.getGammaCorrection())
			gamma = "sqrt";
		
		final String vertexShader = 
				"attribute vec4 vertex_positions;\n" //
				+ "attribute vec4 quad_colors;\n" //
				+ "attribute float s;\n"
				+ "uniform mat4 u_projTrans;\n" //
				+ "varying vec4 v_color;\n" //				
				+ "void main()\n" //
				+ "{\n" //
				+ "   v_color = s * quad_colors;\n" //				
				+ "   gl_Position =  u_projTrans * vertex_positions;\n" //
				+ "}\n";
		final String fragmentShader = "#ifdef GL_ES\n" //
			+ "precision lowp float;\n" //
			+ "#define MED mediump\n"
			+ "#else\n"
			+ "#define MED \n"
			+ "#endif\n" //
				+ "varying vec4 v_color;\n" //
				+ "void main()\n"//
				+ "{\n" //
				+ "  gl_FragColor = "+gamma+"(v_color);\n" //
				+ "}";

		ShaderProgram.pedantic = false;
		ShaderProgram lightShader = new ShaderProgram(vertexShader,
				fragmentShader);
		if (!lightShader.isCompiled()) {
			lightShader = new ShaderProgram("#version 330 core\n" +vertexShader,
					"#version 330 core\n" +fragmentShader);
			if(!lightShader.isCompiled()){
				Gdx.app.log("ERROR", lightShader.getLog());
			}
		}

		return lightShader;
	}
	
}
