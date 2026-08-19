package aa_nodulo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.noodle.nodulo.GdxApp;

import box2dLight.LightLayer;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import box2dLight.TileLayer;
import gui.nGUI;

import static com.badlogic.gdx.graphics.g2d.Batch.C1;
import static com.badlogic.gdx.graphics.g2d.Batch.C2;
import static com.badlogic.gdx.graphics.g2d.Batch.C3;
import static com.badlogic.gdx.graphics.g2d.Batch.C4;
import static com.badlogic.gdx.graphics.g2d.Batch.U1;
import static com.badlogic.gdx.graphics.g2d.Batch.U2;
import static com.badlogic.gdx.graphics.g2d.Batch.U3;
import static com.badlogic.gdx.graphics.g2d.Batch.U4;
import static com.badlogic.gdx.graphics.g2d.Batch.V1;
import static com.badlogic.gdx.graphics.g2d.Batch.V2;
import static com.badlogic.gdx.graphics.g2d.Batch.V3;
import static com.badlogic.gdx.graphics.g2d.Batch.V4;
import static com.badlogic.gdx.graphics.g2d.Batch.X1;
import static com.badlogic.gdx.graphics.g2d.Batch.X2;
import static com.badlogic.gdx.graphics.g2d.Batch.X3;
import static com.badlogic.gdx.graphics.g2d.Batch.X4;
import static com.badlogic.gdx.graphics.g2d.Batch.Y1;
import static com.badlogic.gdx.graphics.g2d.Batch.Y2;
import static com.badlogic.gdx.graphics.g2d.Batch.Y3;
import static com.badlogic.gdx.graphics.g2d.Batch.Y4;

public class nTileMap {
	
	private final TiledMap map;
	public final RendererOrtho renderer;

	private final StaticTiledMapTile brush;

	public final float tile_scale = 200f;

	private pView view; 
	private OrthographicCamera cam;

	public LightLayer viewLayer;
	public LightLayer groundLayer;
	public LightLayer spaceLayer;

	public RayHandler rayHandler;
	public pBox2d box;
	public PlaneApplet app;
	public World world;

	public int map_width = 0, map_height = 0;
	public int tile_width = 0, tile_height = 0;
	
	public nTileMap(String path, pBox2d b, World w) {
		app = b.app;
		box = b;
		world = w;
		this.view = app.view; 
		this.cam = new OrthographicCamera(GdxApp.WIDTH, GdxApp.HEIGHT);

		rayHandler = new RayHandler(app, cam, world);
		
		map = new TmxMapLoader(new InternalFileHandleResolver()).load(path);
		int layer_cnt = map.getLayers().getCount();

		for (int i = 0 ; i < layer_cnt ; i++) {
			MapLayer layer = map.getLayers().get(i);
			if (!layer.isVisible()) continue;
			if (layer instanceof TiledMapTileLayer) {
				TiledMapTileLayer tl = (TiledMapTileLayer) layer;
				if (tl.getWidth() > map_width) map_width = tl.getWidth();
				if (tl.getHeight() > map_height) map_height = tl.getHeight();
				if (tl.getTileWidth() > tile_width) tile_width = tl.getTileWidth();
				if (tl.getTileHeight() > tile_height) tile_height = tl.getTileHeight();
			}
		}
		
		renderer = new RendererOrtho(map, 1f / tile_width);

		for (int id = 0 ; id < layer_cnt ; id++) {
			MapLayer layer = map.getLayers().get(id);
			if (!layer.isVisible()) continue;
			MapProperties prop = layer.getProperties();
			if (prop.get("tile", Boolean.class) != null && 
					prop.get("tile", Boolean.class) && 
					(layer instanceof TiledMapTileLayer)) {
				TiledMapTileLayer tl = (TiledMapTileLayer) layer;
				TileLayer ll = new TileLayer(this, tl);
			}
			if (prop.get("view", Boolean.class) != null && 
					prop.get("view", Boolean.class)) {
				LightLayer ll = new LightLayer(this, layer);
				if (viewLayer == null) viewLayer = ll;
			}
			if (prop.get("light", Boolean.class) != null && 
					prop.get("light", Boolean.class)) {
				LightLayer ll = new LightLayer(this, layer);
				if (groundLayer == null) groundLayer = ll;
			}
			if (prop.get("space", Boolean.class) != null && 
					prop.get("space", Boolean.class)) {
				LightLayer ll = new LightLayer(this, layer);
				if (spaceLayer == null) spaceLayer = ll;
			}
		}
		
		brush = new StaticTiledMapTile(new TextureRegion(generatePixel(
				tile_width, tile_height, Color.TEAL)));

	}
	
	public void dispose() {
		rayHandler.dispose();
	}

	public float getWidth() {
		return getMapWidth() * tile_scale;
	}
	public float getHeight() {
		return getMapHeight() * tile_scale;
	}
	public int getMapWidth() {
		return map_width;
	}
	public int getMapHeight() {
		return map_height;
	}
	public float getTileWidth() {
		return tile_scale;
	}
	public float getTileHeight() {
		return tile_scale;
	}
	public int getMapTileWidth() {
		return tile_width;
	}
	public int getMapTileHeight() {
		return tile_height;
	}

	// might be better to load a texture instead of creating one
	private Texture generatePixel(int width, int height, Color color) {
		Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.fill();
		return new Texture(pixmap);
	}

	public PointLight newSpaceLight(int ray, Color col, float dist, float x, float y) {
		return new PointLight(spaceLayer, ray, col, dist, x, y);
	}

	public PointLight newGroundLight(int ray, Color col, float dist, float x, float y) {
		return new PointLight(groundLayer, ray, col, dist, x, y);
	}

	public PointLight newViewLight() {
		PointLight p = new PointLight(viewLayer, 720, 
				new Color(1f,1f,1f,0.85f), 15000, 0, 0);
		p.setSoft(false);
		return p;
	}

	public void beginRender() {
		rayHandler.beginRender();
	}
	public void renderFront() {
		render();
	}
	public void renderBack() {
		render();
	}
	public void endRender() { 
		
		rayHandler.endLayeredRender();

	}
	public void render() {

		view.app.gdx.drawer.end();
		
		float scale = view.val_cam_scale.get();
		float sclinv = 1f / scale;

		Vector2 screen_center = new Vector2(view.app.gdx.getscreenwidth() / 2f, 
				view.app.gdx.getscreenheight() / 2f);
		Vector2 view_center = new Vector2(view.val_pos.get());
		view_center.x += view.val_view_size.x() / 2.0f;
		view_center.y -= view.val_view_size.y() / 2.0f + nGUI.book.RS;

		renderer.transform = new Matrix4()
				.setToTranslation(0f,0f,0f);

		Vector2 sv = new Vector2(view_center).sub(screen_center);
		sv.scl(1f/view.val_cam_scale.get());
		sv.scl(1f/tile_scale);
		renderer.transform.translate(sv.x,sv.y,0f);

		renderer.transform.rotateRad(0f,0f,-1f, -view.val_cam_rot.get());

		Vector2 m = new Vector2();
		m.add(view.val_cam_pos.get());
		m.scl(1/tile_scale);
		renderer.transform.translate(m.x,m.y,0f);

		cam.setToOrtho(false, (int)(view.app.gdx.getscreenwidth()), 
				(int)(view.app.gdx.getscreenheight()));
		cam.zoom = sclinv / tile_scale;
		cam.position.set(0f, 0f, 0f);
		cam.direction.set(0f, 0f, -1f);
		Vector2 u = new Vector2(0f,1f);
		cam.up.set(u.x, u.y, 0f);
		cam.update();

		render(cam.projection, 
				-view.app.gdx.getscreenwidth() / 2f, 
				-view.app.gdx.getscreenheight() / 2f,
				view.app.gdx.getscreenwidth(), 
				view.app.gdx.getscreenheight());

		view.app.gdx.drawer.begin();

	}

	public void render(Matrix4 projectionMat, float viewboundsX, float viewboundsy, 
			float viewboundsWidth, float viewboundsHeight) {
		renderer.setView(projectionMat, viewboundsX, viewboundsy, 
				viewboundsWidth, viewboundsHeight);
		renderer.render();
	}
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
//	public <T> T getCellProp(float x, float y, String r, Class<T> ct) {
//		return getCell(x, y).getTile()
//				.getProperties().get(r,ct);
//	}
//	public TiledMapTileLayer.Cell getCell(float x, float y) {
//		Vector2 s = new Vector2(x,y);
//		s.scl(1f/tile_scale);
//		s.add(mapLayer.getWidth()/2f, 
//				mapLayer.getHeight()/2f);
//		return mapLayer.getCell((int)(s.x), (int)(s.y));
//	}
	public void delCell(Vector2 v) { delCell(v.x,v.y); }
	public void delCell(float x, float y) {
		Vector2 s = new Vector2(x,y);
		s.scl(1f/tile_scale);
		s.add(getWidth()/2f, 
				getHeight()/2f);
		
		//TODO
//		mapLayer.setCell((int)(s.x), (int)(s.y), null);
	}
	public void addCell(Vector2 v) { addCell(v.x,v.y); }
	public void addCell(float x, float y) {
		Vector2 s = new Vector2(x,y);
		s.scl(1f/tile_scale);
		s.add(getWidth()/2f, 
				getHeight()/2f);
		TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
		cell.setTile(brush);
		
		//TODO
//		mapLayer.setCell((int)(s.x), (int)(s.y), cell);
	}






	public class RendererOrtho extends BatchTiledMapRenderer {

		public RendererOrtho (TiledMap map) {
			super(map);
		}

		public RendererOrtho (TiledMap map, float unitScale) {
			super(map, unitScale);
		}

		public Matrix4 transform = new Matrix4().setToTranslation(0f,0f,0f);
		public Matrix4 tmp_proj = new Matrix4().setToTranslation(0f,0f,0f);
		public Matrix4 tmp_transf = new Matrix4().setToTranslation(0f,0f,0f);
		@Override
		public void setView (Matrix4 projection, float x, float y, float width, float height) {
			tmp_proj.set(batch.getProjectionMatrix());
			batch.setProjectionMatrix(projection);
			viewBounds.set(x, y, width, height);
		}
		
		private MapLayer space = null;

		@Override
		public void render() {
			beginRender();

			tmp_transf.set(batch.getTransformMatrix());
			batch.setTransformMatrix(transform);


			for (MapLayer layer : map.getLayers()) {
				if (!layer.isVisible()) continue;
				if (space == null) {
					if (layer.getProperties().get("space", Boolean.class) != null && 
							layer.getProperties().get("space", Boolean.class)) {
						space = layer; break; }
					renderMapLayer(layer);
				} else {
					if (layer.getProperties().get("space", Boolean.class) != null && 
							layer.getProperties().get("space", Boolean.class)) {
						space = null; }
					if (space == null) renderMapLayer(layer);
				}
			}
			
			endRender();
			transform.setToTranslation(0f,0f,0f);
			batch.setTransformMatrix(tmp_transf);
			batch.setProjectionMatrix(tmp_proj);
		}

		/** Called before the rendering of all layers starts. */
		@Override
		protected void beginRender () {
			AnimatedTiledMapTile.updateAnimationBaseTime();
			batch.begin();
		}

		/** Called after the rendering of all layers ended. */
		@Override
		protected void endRender () {
			batch.end();
		}

		@Override
		public void renderMapLayer(MapLayer layer) {
			if (!layer.isVisible()) return;
			if (layer instanceof MapGroupLayer) {
				MapLayers childLayers = ((MapGroupLayer)layer).getLayers();
				for (int i = 0; i < childLayers.size(); i++) {
					MapLayer childLayer = childLayers.get(i);
					if (!childLayer.isVisible()) continue;
					renderMapLayer(childLayer);
				}
			} else {
				if ((layer instanceof TiledMapTileLayer) && box.drawtile()) {
					renderTileLayer((TiledMapTileLayer)layer);
				} else if (layer instanceof TiledMapImageLayer) {
					renderImageLayer((TiledMapImageLayer)layer);
				} else {
					renderObjects(layer);
				}
			}
		}

		@Override
		public void renderObjects (MapLayer layer) {
			if (box.drawlight() && 
					layer.getProperties().get("lightlayer", LightLayer.class) != null) {
				LightLayer ll = layer.getProperties()
						.get("lightlayer", LightLayer.class);
				batch.end();
				rayHandler.renderLayer(ll);
				batch.begin();
			} else {
				for (MapObject object : layer.getObjects()) {
					renderObject(object);
				}
			}
		}

		@Override
		public void renderObject (MapObject object) {

		}

		@Override
		public void renderTileLayer(TiledMapTileLayer layer) {
			final Color batchColor = batch.getColor();
			final float color = getTileLayerColor(layer, batchColor);

			final int layerWidth = layer.getWidth();
			final int layerHeight = layer.getHeight();

			final float layerTileWidth = layer.getTileWidth() * unitScale;
			final float layerTileHeight = layer.getTileHeight() * unitScale;

			final float layerOffsetX = 
					-layerWidth / 2f + 
					layer.getRenderOffsetX() * unitScale - viewBounds.x * (layer.getParallaxX() - 1);

			// offset in tiled is y down, so we flip it
			final float layerOffsetY = 
					-layerHeight / 2f + 
					-layer.getRenderOffsetY() * unitScale - viewBounds.y * (layer.getParallaxY() - 1);

			final int col1 = Math.max(0, (int)((viewBounds.x - layerOffsetX) / layerTileWidth));
			final int col2 = Math.min(layerWidth,
					(int)((viewBounds.x + viewBounds.width + layerTileWidth - layerOffsetX) / layerTileWidth));

			final int row1 = Math.max(0, (int)((viewBounds.y - layerOffsetY) / layerTileHeight));
			final int row2 = Math.min(layerHeight,
					(int)((viewBounds.y + viewBounds.height + layerTileHeight - layerOffsetY) / layerTileHeight));

			float y = row2 * layerTileHeight + layerOffsetY;
			float xStart = col1 * layerTileWidth + layerOffsetX;
			final float[] vertices = this.vertices;

			for (int row = row2; row >= row1; row--) {
				float x = xStart;
				for (int col = col1; col < col2; col++) {
					final TiledMapTileLayer.Cell cell = layer.getCell(col, row);
					if (cell == null) {
						x += layerTileWidth;
						continue;
					}
					final TiledMapTile tile = cell.getTile();

					if (tile != null) {
						final boolean flipX = cell.getFlipHorizontally();
						final boolean flipY = cell.getFlipVertically();
						final int rotations = cell.getRotation();

						TextureRegion region = tile.getTextureRegion();

						float x1 = x + tile.getOffsetX() * unitScale;
						float y1 = y + tile.getOffsetY() * unitScale;
						float x2 = x1 + region.getRegionWidth() * unitScale;
						float y2 = y1 + region.getRegionHeight() * unitScale;

						float u1 = region.getU();
						float v1 = region.getV2();
						float u2 = region.getU2();
						float v2 = region.getV();

						vertices[X1] = x1;
						vertices[Y1] = y1;
						vertices[C1] = color;
						vertices[U1] = u1;
						vertices[V1] = v1;

						vertices[X2] = x1;
						vertices[Y2] = y2;
						vertices[C2] = color;
						vertices[U2] = u1;
						vertices[V2] = v2;

						vertices[X3] = x2;
						vertices[Y3] = y2;
						vertices[C3] = color;
						vertices[U3] = u2;
						vertices[V3] = v2;

						vertices[X4] = x2;
						vertices[Y4] = y1;
						vertices[C4] = color;
						vertices[U4] = u2;
						vertices[V4] = v1;

						if (flipX) {
							float temp = vertices[U1];
							vertices[U1] = vertices[U3];
							vertices[U3] = temp;
							temp = vertices[U2];
							vertices[U2] = vertices[U4];
							vertices[U4] = temp;
						}
						if (flipY) {
							float temp = vertices[V1];
							vertices[V1] = vertices[V3];
							vertices[V3] = temp;
							temp = vertices[V2];
							vertices[V2] = vertices[V4];
							vertices[V4] = temp;
						}
						if (rotations != 0) {
							switch (rotations) {
							case Cell.ROTATE_90: {
								float tempV = vertices[V1];
								vertices[V1] = vertices[V2];
								vertices[V2] = vertices[V3];
								vertices[V3] = vertices[V4];
								vertices[V4] = tempV;

								float tempU = vertices[U1];
								vertices[U1] = vertices[U2];
								vertices[U2] = vertices[U3];
								vertices[U3] = vertices[U4];
								vertices[U4] = tempU;
								break;
							}
							case Cell.ROTATE_180: {
								float tempU = vertices[U1];
								vertices[U1] = vertices[U3];
								vertices[U3] = tempU;
								tempU = vertices[U2];
								vertices[U2] = vertices[U4];
								vertices[U4] = tempU;
								float tempV = vertices[V1];
								vertices[V1] = vertices[V3];
								vertices[V3] = tempV;
								tempV = vertices[V2];
								vertices[V2] = vertices[V4];
								vertices[V4] = tempV;
								break;
							}
							case Cell.ROTATE_270: {
								float tempV = vertices[V1];
								vertices[V1] = vertices[V4];
								vertices[V4] = vertices[V3];
								vertices[V3] = vertices[V2];
								vertices[V2] = tempV;

								float tempU = vertices[U1];
								vertices[U1] = vertices[U4];
								vertices[U4] = vertices[U3];
								vertices[U3] = vertices[U2];
								vertices[U2] = tempU;
								break;
							}
							}
						}
						batch.draw(region.getTexture(), vertices, 0, NUM_VERTICES);
					}
					x += layerTileWidth;
				}
				y -= layerTileHeight;
			}
		}
	}




}

