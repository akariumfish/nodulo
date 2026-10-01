package box_render;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.noodle.nodulo.GdxApp;

import aa_nodulo.PlaneApplet;
import aa_nodulo.pView;
import box2d.VfxFrameBuffer;
import box2d.pBox2d;
import gui.nGUI;
import shaders.BlendFunc;
import util.Utl;
import util.iMap;
import util.nMap;
import util.nRun;

public class nRender {
	

	static private int n = 0;
	static private int n() { n++; return n-1; }
	static private final int NUM_VERTICES = 20;
	static private final int X1 = n(), Y1 = n(), C1 = n(), U1 = n(), V1 = n();
	static private final int X2 = n(), Y2 = n(), C2 = n(), U2 = n(), V2 = n();
	static private final int X3 = n(), Y3 = n(), C3 = n(), U3 = n(), V3 = n();
	static private final int X4 = n(), Y4 = n(), C4 = n(), U4 = n(), V4 = n();
	
	public class TileLayer extends Layer {
	
		class Tile {
			public int id;
			public TiledMapTile maptile;
			public MapProperties prop;
			private TextureRegion region;
			private Texture texture;
			private float vertices[] = new float[NUM_VERTICES];
			private float x1,y1,x2,y2,u1,v1,u2,v2;
			
			public void clear() { prop = null; region = null; texture = null; maptile = null; }	
			public Tile(TiledMapTile t) {
				maptile = t; id = t.getId();
				prop = maptile.getProperties();
				region = maptile.getTextureRegion();
				texture = region.getTexture();
				
				x1 = maptile.getOffsetX() * unitScale - getMapWidth() / 2f;
				y1 = maptile.getOffsetY() * unitScale - getMapHeight() / 2f;
				x2 = x1 + region.getRegionWidth() * unitScale;
				y2 = y1 + region.getRegionHeight() * unitScale;
				u1 = region.getU(); v1 = region.getV2();
				u2 = region.getU2(); v2 = region.getV();
	
				float color = new Color(1f,1f,1f,1f).toFloatBits();
				vertices[C1] = color; vertices[U1] = u1; vertices[V1] = v1;
				vertices[C2] = color; vertices[U2] = u1; vertices[V2] = v2;
				vertices[C3] = color; vertices[U3] = u2; vertices[V3] = v2;
				vertices[C4] = color; vertices[U4] = u2; vertices[V4] = v1;
			}
			
			public void setVert(float x, float y) {
				vertices[X1] = x1 + x; vertices[Y1] = y1 + y;
				vertices[X2] = x1 + x; vertices[Y2] = y2 + y;
				vertices[X3] = x2 + x; vertices[Y3] = y2 + y;
				vertices[X4] = x2 + x; vertices[Y4] = y1 + y;
			}
			public void render(float x, float y) {
				setVert(x,y);
				renderbatch.draw(texture, vertices, 0, NUM_VERTICES);
			}
			
		}
		Tile getTile(TiledMapTile mt) {
			int i = mt.getId();
			if (tiles.hasKey(i)) return tiles.get(i);
			Tile t = new Tile(mt);
			tiles.put(i,t);
			return t;
		}
		
		private iMap<Tile> tiles = new iMap<Tile>();
		
		public class Cell {
			private Tile tile;
			public boolean wall = false;
			public boolean light = false;
			public boolean ground = false;
			public boolean empty = false;
			public boolean build = false;
			public int x, y;
			public Cell(int i, int j, TiledMapTileLayer.Cell c) { 
				x = i; y = j;
				if (c == null) return;
				all_cells.add(this);
				tile = getTile(c.getTile());
				ground = Utl.getBoo(tile.prop,"ground");
				light = Utl.getBoo(tile.prop,"light");
				wall = Utl.getBoo(tile.prop,"wall");
				empty = !ground && !wall;
	
				if (!wall) build = true;
				if (ground) build = true;
				if (light) build = true;
			}
			
			public void render() {
				if (empty) return;
				this.tile.render(x,y);
			}
		}
	
		TiledMapTileLayer mapLayer;
		public final ArrayList<Cell> all_cells = new ArrayList<Cell>();
		
		Cell[][] cells;
		public Cell getcell(int i, int j) { return ((i >= 0 && j >= 0 && i < map_width && j < map_height) ? cells[i][j] : null); }
		
		public int map_width = 0, map_height = 0;
		public int tile_width = 0, tile_height = 0;
		public final int tile_scale = 200;
	
		private final pView view;
		private final OrthographicCamera cam;
		private final Batch renderbatch;
		private float unitScale;
		
		@Override public void dispose() {
			super.dispose();
		}

//		@Override void resize() {
//			this.cam.setToOrtho(false, (int)(box.app.gdx.getscreenwidth()),
//					(int)(box.app.gdx.getscreenheight())); 
//		}

		public TileLayer() {
			super(0);
			view = PlaneApplet.app.view;
			this.cam = new OrthographicCamera(GdxApp.WIDTH, GdxApp.HEIGHT);
			renderbatch = GdxApp.app.drawer.spritebatch;
			blur = 0;
		}
	
		public void clear_cells() {
			for (Tile t : tiles.all()) t.clear();
			tiles.clear();
			all_cells.clear();
		}
		
		public void loadMap(TiledMapTileLayer ml) {
			mapLayer = ml;
			map_width = ml.getWidth(); map_height = ml.getHeight();
			tile_width = ml.getTileWidth(); tile_height = ml.getTileHeight();
			unitScale = 1f / tile_width;
			loadCell();
		}
		
		public void loadCell() {
			clear_cells();
			cells = new Cell[map_width][map_height];
			for (int i = 0 ; i < map_width ; i++)
				for (int j = 0 ; j < map_height ; j++) {
					cells[i][j] = new Cell(i,j,mapLayer.getCell(i,j)); }
		}
		
		@Override public void buffer() { }		
		@Override public void blur() { }
		@Override public void render() {
			if (box.val_draw_tile.get()) {
				prepareRenderer();

				tmp_transf.set(renderbatch.getTransformMatrix());
				tmp_proj.set(renderbatch.getProjectionMatrix());
				renderbatch.setTransformMatrix(transform);
				renderbatch.setProjectionMatrix(projection);
				
				renderbatch.begin();

				for (Cell c : all_cells) if (!c.empty) c.render();

				renderbatch.end();
				
//				transform.setToTranslation(0f,0f,0f);
				renderbatch.setTransformMatrix(tmp_transf);
				renderbatch.setProjectionMatrix(tmp_proj);

				Gdx.gl.glEnable(GL20.GL_BLEND); 
				
			}
		}

		Matrix4 transform = new Matrix4().setToTranslation(0f,0f,0f);
		Matrix4 projection = new Matrix4().setToTranslation(0f,0f,0f);
		Matrix4 tmp_proj = new Matrix4().setToTranslation(0f,0f,0f);
		Matrix4 tmp_transf = new Matrix4().setToTranslation(0f,0f,0f);
		private void prepareRenderer() {
			float scale = view.val_cam_scale.get();
			float sclinv = 1f / scale;
	
			Vector2 screen_center = new Vector2(view.app.gdx.getscreenwidth() / 2f, 
					view.app.gdx.getscreenheight() / 2f);
			Vector2 view_center = new Vector2(view.val_pos.get());
			view_center.x += view.val_view_size.x() / 2.0f;
			view_center.y -= view.val_view_size.y() / 2.0f + nGUI.book.RS;
	
			transform = new Matrix4()
					.setToTranslation(0f,0f,0f);
	
			Vector2 sv = new Vector2(view_center).sub(screen_center);
			sv.scl(1f/view.val_cam_scale.get());
			sv.scl(1f/tile_scale);
			transform.translate(sv.x,sv.y,0f);
	
			transform.rotateRad(0f,0f,-1f, -view.val_cam_rot.get());
	
			Vector2 m = new Vector2();
			m.add(view.val_cam_pos.get());
			m.scl(1f/tile_scale);
			transform.translate(m.x,m.y,0f);
	
			cam.setToOrtho(false, (int)(view.app.gdx.getscreenwidth()), 
					(int)(view.app.gdx.getscreenheight()));
			cam.zoom = sclinv / tile_scale;
			cam.position.set(0f, 0f, 0f);
			cam.direction.set(0f, 0f, -1f);
			Vector2 u = new Vector2(0f,1f);
			cam.up.set(u.x, u.y, 0f);
			cam.update();
			projection.set(cam.projection);
		}
		
		
	
		public float getWidth() { return map_width * tile_scale; }
		public float getHeight() { return map_height * tile_scale; }
		public int getMapWidth() { return map_width; }
		public int getMapHeight() { return map_height; }
		public float getTileWidth() { return tile_scale; }
		public float getTileHeight() { return tile_scale; }
		public int getMapTileWidth() { return tile_width; }
		public int getMapTileHeight() { return tile_height; }
	
		
	
		public Vector2 getCellPos(int x, int y) {
			final int layerWidth = getMapWidth();
			final int layerHeight = getMapHeight();
			Vector2 p = new Vector2(x,y)
					.sub(layerWidth/2f,layerHeight/2f)
					.scl(getTileWidth(),getTileHeight());
			return p;
		}
		public Vector2 mapToSpace(float x, float y) { return mapToSpace(new Vector2(x,y)); }
		public Vector2 mapToSpace(Vector2 v) {
			Vector2 p = new Vector2(v)
					.scl(getTileWidth(),getTileHeight())
					.scl(1f/getMapTileWidth(),1f/getMapTileHeight())
					.sub(getWidth()/2f,getHeight()/2f);
			return p;
		}
		
	}

	public void processMap(TiledMapTileLayer tilemap) {
		tile.loadMap(tilemap);
	}

	public void clearMap() {
		tile.clear_cells();
	}

	public nBatch.Unit newUnit(String model, float...a) {
		return batch.newUnit(model,a); }

	public <K extends nBatch.Model> void addModel(String ref, K mod) {
		batch.addModel(ref, mod); }
	public <K extends nBatch.Model> K getModel(String ref, Class<K> cl) {
		return batch.getModel(ref,cl); }
	public <K extends nBatch.Model> K getModel(String ref) {
		return batch.getModel(ref); }
	
	
	
	
	
	private ArrayList<Layer> layers = new ArrayList<Layer>();
	class Layer {
		private BlendFunc unitblend = unitBlend;
		private BlendFunc renderblend;
		private Shader rendershader;
		protected int blur = 2;
		protected VfxFrameBuffer buffer;
		private int pix_size = BUFFER_PIX_SIZE;
		private int group;
		
		Layer setup(final BlendFunc u, final Shader s, final BlendFunc b) {
			unitblend = u; renderblend = b; rendershader = s; return this; }
		Layer blur(int b) { blur = b; return this; }
		int group() { return group; }

		Layer() { this(BUFFER_PIX_SIZE); }
		Layer(int pix) {
			group = batch.newGroup();
			layers.add(this);
			pix_size = pix; 
			if (pix > 0) buffer = new VfxFrameBuffer(Pixmap.Format.RGBA8888);
			resize();
		}

		void dispose() {
			if (pix_size > 0) buffer.dispose();
		}

		void resize() {
			if (pix_size > 0) buffer.resize((int)(box.app.gdx.getscreenwidth() / pix_size),
					(int)(box.app.gdx.getscreenheight() / pix_size)); }

		public void buffer() {
			if (pix_size <= 0) return; 
			buffer.begin();
			Gdx.gl.glClearColor(buffer_clear_color.r, buffer_clear_color.g, 
					buffer_clear_color.b, buffer_clear_color.a);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			unitblend.apply();
			batch.render(group, unitShader.shader);
			buffer.end();
		}

		public void blur() { 
			if (pix_size <= 0) return; if (blur > 0) gaussianBlur(buffer,blur); }
		public void render() {
			if (pix_size <= 0) return;
			buffer.getTexture().bind(0);
			renderblend.apply();
			rendershader.bind();
			screenMesh.render(rendershader.shader, GL20.GL_TRIANGLE_FAN); 
		}
	}
	
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

	private final Color buffer_clear_color = new Color(0f, 0f, 0f, 0f);

	
	pBox2d box;

	public final nBatch batch;
	public final int AURA,SOLID,COLOR,LIGHT;
	public final TileLayer tile;
	private final Mesh screenMesh;
	
	private final Shader unitShader = new Shader().unitShader();
	
	private final Shader colorShader = new Shader().colorShader()
			.setUniform("ambient", new Color(0.1f, 0.1f, 0.1f, 1f));
	
	private final Shader lightShader = new Shader().lightShader()
			.setUniform("ambient", new Color(0.2f, 0.2f, 0.2f, 1f));
	
//	private final Shader darkShader = new Shader().darkShader()
//			.setUniform("ambient", new Color(1f, 1f, 1f, 1f));
	
	private final Shader auraShader = new Shader().auraShader()
			.setUniform("ambient", new Color(0.1f, 0.1f, 0.1f, 1f));
	
	private final Shader solidShader = new Shader().solidShader();
	
	private ShaderProgram blurShader;
	private VfxFrameBuffer frameBuffer;
	private VfxFrameBuffer pingPongBuffer;

	public void dispose() {
		batch.dispose();
		screenMesh.dispose();
		frameBuffer.dispose();
		pingPongBuffer.dispose();
		unitShader.dispose();
		colorShader.dispose();
		lightShader.dispose();
		blurShader.dispose();
		for (Layer l : layers) l.dispose(); layers.clear();
	}

	public nRender(pBox2d _box) {
		box = _box;
		cam = new FalseCam(GdxApp.WIDTH, GdxApp.HEIGHT, this);
		
		// unitCapacity, maxVertices, maxTriangles, maxInstances
		batch = new nBatch(256, 4096, 2048, 0)
				.positionAttribute("pos")
				.colorAttribute("quad_colors")
				.genericAttribute("s")
				.finish();

		tile = new TileLayer(); 
		AURA = new Layer(2).setup(unitBlend, auraShader, auraRenderBlend)
				.group(); 
		
		SOLID = new Layer(2).setup(solidUnitBlend, solidShader, solidRenderBlend)
				.blur(1).group(); 
		
		COLOR = new Layer(2).setup(unitBlend, colorShader, colorRenderBlend)
				.blur(1).group(); 
		
		LIGHT = new Layer(2).setup(unitBlend, lightShader, lightRenderBlend)
				.group(); 
		
		screenMesh = createScreenMesh();
		
		create_buffers(Gdx.graphics.getWidth() / BUFFER_PIX_SIZE, Gdx.graphics
				.getHeight() / BUFFER_PIX_SIZE);

		box.app.gdx.addEventScreen(new nRun() { public void run() {
			resize(Gdx.graphics.getWidth() / BUFFER_PIX_SIZE, 
					Gdx.graphics.getHeight() / BUFFER_PIX_SIZE); 
			for (Layer l : layers) l.resize(); }});
		
	}

	private void create_buffers(int fboWidth, int fboHeight) {
		blurShader = createBlurShader(DIFFUSE_BLUR, fboWidth, fboHeight);
		pingPongBuffer = new VfxFrameBuffer(Format.RGBA8888);
		pingPongBuffer.initialize(fboWidth, fboHeight);
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
	}

	private Mesh createScreenMesh() {
		float[] verts = new float[VERT_SIZE];
		verts[TX1] = -1; 	verts[TY1] = -1;		verts[TU1] = 0f; verts[TV1] = 0f;
		verts[TX2] = 1; 		verts[TY2] = -1;		verts[TU2] = 1f; verts[TV2] = 0f;
		verts[TX3] = 1; 		verts[TY3] = 1;		verts[TU3] = 1f; verts[TV3] = 1f;
		verts[TX4] = -1; 	verts[TY4] = 1;		verts[TU4] = 0f; verts[TV4] = 1f;
		return new Mesh(true, 4, 0, 
				new VertexAttribute(Usage.Position, 2, "a_position"), 
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoord"))
			.setVertices(verts);
	}

	static public final int VERT_SIZE = 16;
	static public final int TX1 = 0, TY1 = 1, TU1 = 2, TV1 = 3;
	static public final int TX2 = 4, TY2 = 5, TU2 = 6, TV2 = 7;
	static public final int TX3 = 8, TY3 = 9, TU3 = 10, TV3 = 11;
	static public final int TX4 = 12, TY4 = 13, TU4 = 14, TV4 = 15;
	
	public void render() {
		box.app.gdx.drawer.pause_batch();
		
		batch.update();
		
		prepareCombinedMatrix(box.view);
		removeScissors();

		Gdx.gl.glDepthMask(false);
		Gdx.gl.glEnable(GL20.GL_BLEND); 

		unitShader.setUniform("u_projTrans", combined);
		unitShader.bind();
//		unitShader.setUniformMatrix("u_projTrans", combined);
////		unitShader.setUniformf("u_trans", u_trans);
////		unitShader.setUniformf("u_x", u_x);
////		unitShader.setUniformf("u_y", u_y);
////		unitShader.setUniformf("u_cos", u_cos);
////		unitShader.setUniformf("u_sin", u_sin);
////		unitShader.setUniformf("u_scale", u_scale);
		for (Layer l : layers) l.buffer();
		for (Layer l : layers) l.blur();

		frameBuffer.begin(); 

		Gdx.gl20.glDisable(GL20.GL_BLEND);
		Gdx.gl.glClearColor(0f,0f,0f,0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_BLEND); 
		
		for (Layer l : layers) l.render();

		frameBuffer.end();

		Gdx.gl20.glDisable(GL20.GL_BLEND);
		
		restoreScissors();

		box.app.gdx.drawer.spritebatch.begin();

		box.app.gdx.drawer.spritebatch.draw(frameBuffer.getTexture(), 0, 0, 
				box.app.gdx.getscreenwidth(), 
				box.app.gdx.getscreenheight(), 
				0, 0, 1, 1);

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

//	private final Vector2 u_trans = new Vector2();
//	private float u_x, u_y, u_cos, u_sin, u_scale;
	
	private final Matrix4 combined = new Matrix4();
	private final FalseCam cam;
	private void prepareCombinedMatrix(pView pview) {
		cam.prepareCombinedMatrix(pview.val_pos.get(), pview.val_view_size.get(), 
				pview.val_cam_pos.get(), pview.val_cam_scale.get(), pview.val_cam_rot.get()); 
		combined.set(cam.combined); 
		
//		u_x = pview.val_cam_pos.x();
//		u_y = pview.val_cam_pos.y();
//		u_scale = pview.val_cam_scale.get();
//		u_cos = (float) Math.cos(pview.val_cam_rot.get());
//		u_sin = (float) Math.sin(pview.val_cam_rot.get());
//		u_trans.set(pview.val_pos.get());
//		u_trans.x += pview.val_view_size.x() / 2.0f;
//		u_trans.y -= pview.val_view_size.y() / 2.0f + nGUI.book.RS;
//		u_trans.sub(box.app.gdx.getscreenwidth() / 2.0f, box.app.gdx.getscreenheight() / 2.0f);
		
	}
//	private Matrix4 getCombinedMatrix() { return cam.combined; }
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
	
	
	
	
	
	
	
	
	
	
	
	
	public static class Shader {

		private nMap<Uniform> uniforms = new nMap<Uniform>();

		private abstract class Uniform {
			String ref; Uniform(String _ref) { ref = _ref; }
			abstract void bind(ShaderProgram p); 
			abstract void set(Object o); }
		private class UniformFloat extends Uniform {
			float val; UniformFloat(String _ref, float def) {
				super(_ref); val = def; }
			void bind(ShaderProgram p) { p.setUniformf(ref,val); }
			void set(Object o) { if (o instanceof Float) val = (float)o; } }
		private class UniformInt extends Uniform {
			int val; UniformInt(String _ref, int def) {
				super(_ref); val = def; }
			void bind(ShaderProgram p) { p.setUniformi(ref,val); }
			void set(Object o) { if (o instanceof Integer) val = (int)o; } }
		private class UniformVec2 extends Uniform {
			Vector2 val = new Vector2();
			UniformVec2(String _ref, Vector2 def) {
				super(_ref); val.set(def); }
			void bind(ShaderProgram p) { p.setUniformf(ref,val); }
			void set(Object o) { if (o instanceof Vector2) val.set((Vector2)o); } }
		private class UniformColor extends Uniform {
			Color val = new Color();
			UniformColor(String _ref, Color def) {
				super(_ref); val.set(def); }
			void bind(ShaderProgram p) { p.setUniformf(ref,val.r,val.g,val.b,val.a); }
			void set(Object o) { if (o instanceof Color) val.set((Color)o); } }
		private class UniformMat4 extends Uniform {
			Matrix4 val = new Matrix4();
			UniformMat4(String _ref, Matrix4 def) {
				super(_ref); val.set(def); }
			void bind(ShaderProgram p) { p.setUniformMatrix(ref,val); }
			void set(Object o) { if (o instanceof Matrix4) val.set((Matrix4)o); } }

		public Shader addUniformFloat(String ref) {
			uniforms.put(ref, new UniformFloat(ref,0f)); return this; }
		public Shader addUniformFloat(String ref, float def) {
			uniforms.put(ref, new UniformFloat(ref,def)); return this; }
		public Shader addUniformInt(String ref) {
			uniforms.put(ref, new UniformInt(ref,(int)0)); return this; }
		public Shader addUniformInt(String ref, int def) {
			uniforms.put(ref, new UniformInt(ref,def)); return this; }
		public Shader addUniformVec2(String ref) {
			uniforms.put(ref, new UniformVec2(ref,new Vector2())); return this; }
		public Shader addUniformVec2(String ref, Vector2 def) {
			uniforms.put(ref, new UniformVec2(ref,def)); return this; }
		public Shader addUniformColor(String ref) {
			uniforms.put(ref, new UniformColor(ref,new Color())); return this; }
		public Shader addUniformColor(String ref, Color def) {
			uniforms.put(ref, new UniformColor(ref,def)); return this; }
		public Shader addUniformMatrix4(String ref) {
			uniforms.put(ref, new UniformMat4(ref,new Matrix4())); return this; }
		public Shader addUniformMatrix4(String ref, Matrix4 def) {
			uniforms.put(ref, new UniformMat4(ref,def)); return this; }

		public Shader setUniform(String ref, Object val) {
			if (uniforms.hasKey(ref)) uniforms.get(ref).set(val); return this; }
		
		public void bind() {
			shader.bind(); for (Uniform u : uniforms.all()) u.bind(shader); }
		
		private String vertexShader, fragShader;
		public Shader vertexShader(String s) { vertexShader = Utl.copy(s); return this; }
		public Shader fragShader(String s) { fragShader = Utl.copy(s); compute(); return this; }
		public ShaderProgram shader;
		private void compute() {
			ShaderProgram.pedantic = false;
			shader = new ShaderProgram(vertexShader, fragShader);
			if(!shader.isCompiled()) { Gdx.app.log("ERROR : shader not compiled", shader.getLog()); }
		}
		public void dispose() {
			shader.dispose();
		}

		public Shader unitShader() {
			vertexShader = "#version 330 core\n"
					+ "attribute vec4 pos;\n" //
					+ "attribute vec4 quad_colors;\n" //
					+ "attribute float s;\n"
					+ "uniform mat4 u_projTrans;\n" //
					+ "varying vec4 v_color;\n" //				
					+ "void main()\n" //
					+ "{\n" //
					+ "   v_color = s * quad_colors;\n" //				
					+ "   gl_Position =  u_projTrans * pos;\n" //
					+ "}\n";
			fragShader = "#version 330 core\n"
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
			compute();
			addUniformMatrix4("u_projTrans", new Matrix4());
			return this;
		}

		public Shader fxVertexShader() {
			vertexShader = "#version 330 core\n"
				+ "attribute vec4 a_position;\n" //
				+ "attribute vec2 a_texCoord;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "\n" //
				+ "void main()\n" //
				+ "{\n" //
				+ "   v_texCoords = a_texCoord;\n" //
				+ "   gl_Position = a_position;\n" //
				+ "}\n"; return this; }

		public Shader solidShader() {
			fxVertexShader();
			fragShader = "#version 330 core\n"
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
			compute();
			addUniformInt("u_texture", 0);
			return this;
		}

		public Shader colorShader() {
			fxVertexShader();
			fragShader = "#version 330 core\n"
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
			compute();
			addUniformColor("ambient", new Color());
			addUniformInt("u_texture", 0);
			return this;
		}

//		public Shader darkShader() {
//			fxVertexShader();
//			fragShader = "#version 330 core\n"
//				+ "#ifdef GL_ES\n" //
//				+ "precision lowp float;\n" //
//				+ "#define MED mediump\n"				
//				+ "#else\n"				
//				+ "#define MED \n"
//				+ "#endif\n" //
//				+ "varying MED vec2 v_texCoords;\n" //
//				+ "uniform sampler2D u_texture;\n" //
//				+ "uniform vec4 ambient;\n"
//				+ "void main()\n"//
//				+ "{\n" //
//				+ "  gl_FragColor.rgb = (ambient.rgb - texture2D(u_texture, v_texCoords).rgb);\n"
//				+ "  gl_FragColor.a = 1.0;\n"
//				+ "}\n";
//			compute();
//			addUniformColor("ambient", new Color());
//			addUniformInt("u_texture", 0);
//			return this;
//		}

		public Shader lightShader() {
			fxVertexShader();
			fragShader = "#version 330 core\n"
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
			compute();
			addUniformColor("ambient", new Color());
			addUniformInt("u_texture", 0);
			return this;
		}

		public Shader auraShader() {
			fxVertexShader();
			fragShader = "#version 330 core\n"
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
				+ "    gl_FragColor.rgb = ambient.rgb + c.rgb * c.a * c.a;\n"//
				+ "    gl_FragColor.a = 1.0 - ambient.a - c.a;\n"//
				+ "}\n";
			compute();
			addUniformColor("ambient", new Color());
			addUniformInt("u_texture", 0);
			return this;
		}
		public Shader fxShader() {
			fxVertexShader();
			fragShader = "#version 330 core\n"
				+ "#ifdef GL_ES\n" //
				+ "precision lowp float;\n" //
				+ "#define MED mediump\n"
				+ "#else\n"
				+ "#define MED \n"
				+ "#endif\n" //
				+ "varying MED vec2 v_texCoords;\n" //
				+ "uniform sampler2D u_texture;\n" //
				+ "uniform vec4 ambient;\n"		
				+ "uniform float mode;\n"				
				+ "void main() {\n" //
				+ "  vec4 c = texture2D(u_texture, v_texCoords);\n"//
				+ "  if (mode == 1) {\n" //COLOR
				+ "    gl_FragColor.rgb = c.rgb * c.a + ambient.rgb;\n"//
				+ "    gl_FragColor.a = ambient.a - c.a;\n"//	
				+ "  }\n"
				+ "  else if (mode == 2) {\n" //LIGHT
				+ "    gl_FragColor.rgb = (ambient.rgb + c.rgb);\n"
				+ "    gl_FragColor.a = 1.0;\n"
				+ "  }\n"
				+ "  else if (mode == 3) {\n" //AURA
				+ "    gl_FragColor.rgb = ambient.rgb - c.rgb;\n"//
				+ "    gl_FragColor.a = ambient.a;\n"//
				+ "  }\n"
				+ "  else {\n" //SOLID
				+ "    gl_FragColor = c;\n"//
				+ "  }\n"			
				+ "}\n";
			compute();
			addUniformFloat("mode", 0f);
			addUniformColor("ambient", new Color());
			addUniformInt("u_texture", 0);
			return this;
		}
	}
	
	
	

	private static final ShaderProgram createHaloShader() {
		final String vertexShader = "#version 330 core\n"
			+ "attribute vec4 a_pos;\n" //
			+ "attribute vec4 a_summit;\n"
			+ "attribute vec4 inst_pos;\n" //
			+ "attribute float inst_rad;\n" //
			+ "attribute vec4 inst_color;\n" //
			+ "uniform mat4 u_transf;\n" //
			+ "uniform mat4 u_proj;\n" //	
			+ "varying vec4 v_color;\n" //
			+ "varying vec4 v_summit;\n" //			
			+ "void main()\n" //
			+ "{\n" //
			+ "   v_color = inst_color;\n" //		
			+ "   v_summit = a_summit;\n" //		
			+ "   vec4 v = (a_pos * inst_rad) + inst_pos;\n" //				
			+ "   gl_Position =  u_proj * (u_transf * v);\n" //
			+ "}\n";
		final String fragmentShader = "#version 330 core\n"
			+ "#ifdef GL_ES\n" //
			+ "precision lowp float;\n" //
			+ "#define MED mediump\n"
			+ "#else\n"
			+ "#define MED \n"
			+ "#endif\n" //
			+ "varying vec4 v_color;\n" //
			+ "varying vec4 v_summit;\n" //
			+ "void main()\n"//
			+ "{\n" //
			+ "  float s1 = v_summit.x;\n" //
			+ "  float s2 = v_summit.y;\n" //
			+ "  float s3 = v_summit.z;\n" //
			+ "  float fact = 1.0 - ( abs(s1-0.3) + abs(s2-0.3) + abs(s3-0.3) ) / 1.2;\n" //
			+ "  gl_FragColor.rgb = v_color.rgb;\n" //
			+ "  gl_FragColor.a = fact;\n" //
			+ "}";
		ShaderProgram.pedantic = false;
		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
		if(!shader.isCompiled()){ Gdx.app.log("ERROR : shader not compiled", shader.getLog()); }
		return shader;
	}

	
	
	
	
//	private static final ShaderProgram createRenderShader() {
//		final String vertexShader = "#version 330 core\n"
//			+ "attribute vec2 pos;\n" //
//			+ "attribute vec4 quad_colors;\n" //
//			+ "attribute float s;\n"
//			+ "uniform vec2 u_trans;\n" //
//			+ "uniform float u_x;\n" //
//			+ "uniform float u_y;\n" //
//			+ "uniform float u_cos;\n" //
//			+ "uniform float u_sin;\n" //
//			+ "uniform float u_scale;\n" //
//			+ "varying vec4 v_color;\n" //				
//			+ "void main()\n" //
//			+ "{\n" //
//			+ "   v_color = s * quad_colors;\n" //	
//			+ "   vec2 v = u_scale * vec2((pos.x + u_x) * u_cos - (pos.y + u_y) * u_sin, (pos.x + u_x) * u_sin + (pos.y + u_y) * u_cos);\n" //	
//			+ "   gl_Position =  vec4(v.x + u_trans.x,v.y + u_trans.y,0.0,0.0);\n" //
//			+ "}\n";
//		final String fragmentShader = "#version 330 core\n"
//			+ "#ifdef GL_ES\n" //
//			+ "precision lowp float;\n" //
//			+ "#define MED mediump\n"
//			+ "#else\n"
//			+ "#define MED \n"
//			+ "#endif\n" //
//			+ "varying vec4 v_color;\n" //
//			+ "void main()\n"//
//			+ "{\n" //
//			+ "  gl_FragColor = v_color;\n" //
//			+ "}";
//		ShaderProgram.pedantic = false;
//		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
//		if(!shader.isCompiled()){ Gdx.app.log("ERROR : shader not compiled", shader.getLog()); }
//		return shader;
//	}

//	private static final ShaderProgram createFXShader() {
//		final String vertexShader = "#version 330 core\n"
//			+ "attribute vec4 a_position;\n" //
//			+ "attribute vec2 a_texCoord;\n" //
//			+ "varying vec2 v_texCoords;\n" //
//			+ "\n" //
//			+ "void main()\n" //
//			+ "{\n" //
//			+ "   v_texCoords = a_texCoord;\n" //
//			+ "   gl_Position = a_position;\n" //
//			+ "}\n";
//		final String fragmentShader = "#version 330 core\n"
//			+ "#ifdef GL_ES\n" //
//			+ "precision lowp float;\n" //
//			+ "#define MED mediump\n"
//			+ "#else\n"
//			+ "#define MED \n"
//			+ "#endif\n" //
//			+ "varying MED vec2 v_texCoords;\n" //
//			+ "uniform sampler2D u_texture;\n" //
//			+ "uniform vec4 ambient;\n"		
//			+ "uniform float mode;\n"				
//			+ "void main()\n"//
//			+ "{\n" //
//			+ "  vec4 c = texture2D(u_texture, v_texCoords);\n"//
//			+ "  if (mode == 0)\n"// SOLID
//			+ "  {\n" //
//			+ "    gl_FragColor = c;\n"
//			+ "  }\n"
//			+ "  else if (mode == 1)\n"//COLOR
//			+ "  {\n" //
//			+ "    gl_FragColor.rgb = c.rgb * c.a + ambient.rgb;\n"//
//			+ "    gl_FragColor.a = ambient.a - c.a;\n"//	
//			+ "  }\n"
//			+ "  else if (mode == 2)\n"//LIGHT
//			+ "  {\n" //
//			+ "    gl_FragColor.rgb = (ambient.rgb + c.rgb);\n"
//			+ "    gl_FragColor.a = 1.0;\n"
//			+ "  }\n"
//			+ "  else if (mode == 3)\n"//AURA
//			+ "  {\n" //
//			+ "    gl_FragColor.rgb = ambient.rgb - c.rgb;\n"//
//			+ "    gl_FragColor.a = ambient.a;\n"//
//			+ "  }\n"
//			+ "  else\n"//
//			+ "  {\n" //
//			+ "    gl_FragColor.rgb = ambient.rgb;\n"//
//			+ "    gl_FragColor.a = 1.0;\n"//
//			+ "  }\n"			
//			+ "}\n";
//		ShaderProgram.pedantic = false;
//		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
//		if(!shader.isCompiled()) { Gdx.app.log("ERROR : shader not compiled", shader.getLog()); }
//		return shader;
//	}

	
	
	
	
	
	
	
	
	
	
	
	
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
