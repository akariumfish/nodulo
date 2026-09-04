package box2d;

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

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.noodle.nodulo.GdxApp;

import aa_nodulo.pView;
import gui.nGUI;
import util.Utl;

public class TileLayer extends nRenderer.Layer {

	public final TiledMapTileLayer mapLayer;
	
	public class Cell {
		public TiledMapTileLayer.Cell cell;
		public TiledMapTile tile;
		public MapProperties prop;
		public boolean wall = false;
		public boolean ground = false;
		public boolean empty = false;
		public boolean build = false;
		public Cell(TiledMapTileLayer.Cell c) {
			cell = c;
			if (c == null) return;
			tile = c.getTile();
			prop = tile.getProperties();
			ground = Utl.getBoo(prop,"ground");
			wall = !Utl.getBoo(prop,"light");
			empty = !ground && !wall;
			
			if (!wall) build = true;
		}
	}

	public final RendererOrtho renderer;

	public Cell[][] cells;
	
	public final int map_width, map_height;
	public int tile_width = 0, tile_height = 0;

	public ArrayList<Body> ground_bod = new ArrayList<Body>();

	private final StaticTiledMapTile brush;
	private final pView view;
	private final OrthographicCamera cam;


	public TileLayer(nRenderer tm, TiledMapTileLayer ml) { this(tm,ml,0); }
	public TileLayer(nRenderer tm, TiledMapTileLayer ml, int p) {
		super(tm,p);
		mapLayer = ml;
		view = tm.view;
		cam = tm.cam;

		map_width = ml.getWidth();
		map_height = ml.getHeight();
		tile_width = ml.getTileWidth();
		tile_height = ml.getTileHeight();
		
		
		renderer = new RendererOrtho(1f / tile_width, GdxApp.app.drawer.spritebatch);
		
		mapLayer.getProperties().put("tilelayer", this);
		
		brush = new StaticTiledMapTile(new TextureRegion(generatePixel(
				tile_width, tile_height, Color.TEAL)));
		
		cells = new Cell[map_width][map_height];
		
		for (int i = 0 ; i < map_width ; i++)
			for (int j = 0 ; j < map_height ; j++) {
				TiledMapTileLayer.Cell c = mapLayer.getCell(i,j);
				cells[i][j] = new Cell(c);
			}
		
		if (rend.tileLayer == null) {
			for (int w = map_width ; w > 0 ; w--)
				for (int h = map_height ; h > 0 ; h--) {
					search_place(w,h,false);
					search_place(h,w,false);
				}
			for (int i = 0 ; i < map_width ; i++)
				for (int j = 0 ; j < map_height ; j++) {
					cells[i][j].build = !cells[i][j].empty;
				}
			for (int w = map_width ; w > 0 ; w--)
				for (int h = map_height ; h > 0 ; h--) {
					search_place(w,h,true);
					search_place(h,w,true);
				}
			
		}
	}
	
	public void search_place(int w, int h, boolean transp) {
		for (int i = 0 ; i < map_width - w ; i++)
			for (int j = 0 ; j < map_height - h ; j++) 
				build_wall(i,j,w,h,transp);
	}

	public boolean test_place(int x, int y, int w, int h) {
		for (int i = x ; i < x + w ; i++) for (int j = y ; j < y + h ; j++) 
			if (i >= map_width || j >= map_height || cells[i][j].build) return false;
		return true;
	}
	public void build_wall(int x, int y, int w, int h, boolean transp) {
		if (!test_place(x,y,w,h)) return;
		Vector2 p = getCellPos(x,y);
		p.add(w*rend.tile_scale/2f,h*rend.tile_scale/2f);
		BodyDef groundBodyDef = new BodyDef();  
		groundBodyDef.position.set(p);  
		Body groundBody = rend.world.createBody(groundBodyDef);  
		
		if (!transp) ground_bod.add(groundBody);
		if (!transp) rend.box.body_breaker.add(groundBody);
		if (transp) rend.lightLayer.transparent.add(groundBody);
		if (transp) rend.visionLayer.transparent.add(groundBody);
		if (transp) rend.colorLayer.transparent.add(groundBody);
		if (transp) rend.auraLayer.transparent.add(groundBody);
		
		PolygonShape groundBox = new PolygonShape();  
		groundBox.setAsBox(w*rend.tile_scale/2f,h*rend.tile_scale/2f);
		Fixture fixture = groundBody.createFixture(groundBox, 0.0f);
//		fixture.setUserData(new LightData(1f, true));
		
		groundBox.dispose();
		for (int i = x ; i < x + w ; i++)
			for (int j = y ; j < y + h ; j++) 
				cells[i][j].build = true;
	}

	@Override
	public void render() {
		if (rend.box.val_draw_tile.get()) {
			prepareRenderer();
			renderer.render(this);
		}
	}
	

	public void prepareRenderer() {

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
		sv.scl(1f/rend.tile_scale);
		renderer.transform.translate(sv.x,sv.y,0f);

		renderer.transform.rotateRad(0f,0f,-1f, -view.val_cam_rot.get());

		Vector2 m = new Vector2();
		m.add(view.val_cam_pos.get());
		m.scl(1/rend.tile_scale);
		renderer.transform.translate(m.x,m.y,0f);

		cam.setToOrtho(false, (int)(view.app.gdx.getscreenwidth()), 
				(int)(view.app.gdx.getscreenheight()));
		cam.zoom = sclinv / rend.tile_scale;
		cam.position.set(0f, 0f, 0f);
		cam.direction.set(0f, 0f, -1f);
		Vector2 u = new Vector2(0f,1f);
		cam.up.set(u.x, u.y, 0f);
		cam.update();

		renderer.setView(cam.projection, 
				-view.app.gdx.getscreenwidth() / 2f, 
				-view.app.gdx.getscreenheight() / 2f,
				view.app.gdx.getscreenwidth(), 
				view.app.gdx.getscreenheight());
	}
	
	

	public float getWidth() {
		return map_width * rend.tile_scale;
	}
	public float getHeight() {
		return map_height * rend.tile_scale;
	}
	public int getMapWidth() {
		return map_width;
	}
	public int getMapHeight() {
		return map_height;
	}
	public float getTileWidth() {
		return rend.tile_scale;
	}
	public float getTileHeight() {
		return rend.tile_scale;
	}
	public int getMapTileWidth() {
		return tile_width;
	}
	public int getMapTileHeight() {
		return tile_height;
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
		s.scl(1f/rend.tile_scale);
		s.add(getWidth()/2f, 
				getHeight()/2f);
		
		//TODO
//		mapLayer.setCell((int)(s.x), (int)(s.y), null);
	}
	public void addCell(Vector2 v) { addCell(v.x,v.y); }
	public void addCell(float x, float y) {
		Vector2 s = new Vector2(x,y);
		s.scl(1f/rend.tile_scale);
		s.add(getWidth()/2f, 
				getHeight()/2f);
		TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
		cell.setTile(brush);
		
		//TODO
//		mapLayer.setCell((int)(s.x), (int)(s.y), cell);
	}

	
	

	// might be better to load a texture instead of creating one
	public Texture generatePixel(int width, int height, Color color) {
		Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.fill();
		return new Texture(pixmap);
	}

	
	
	

	public class RendererOrtho extends BatchTiledMapRenderer {
		
		public RendererOrtho (float unitScale, Batch b) {
			super(null, unitScale, b);
		}

		public void render(TileLayer layer) { 

			tmp_transf.set(batch.getTransformMatrix());
			tmp_proj.set(batch.getProjectionMatrix());
			batch.setTransformMatrix(transform);
			batch.setProjectionMatrix(projection);
			
			batch.begin();

			renderTileLayer(layer.mapLayer);

			batch.end();
			
			transform.setToTranslation(0f,0f,0f);
			batch.setTransformMatrix(tmp_transf);
			batch.setProjectionMatrix(tmp_proj);
			
		}

		public Matrix4 transform = new Matrix4().setToTranslation(0f,0f,0f);
		public Matrix4 projection = new Matrix4().setToTranslation(0f,0f,0f);
		public Matrix4 tmp_proj = new Matrix4().setToTranslation(0f,0f,0f);
		public Matrix4 tmp_transf = new Matrix4().setToTranslation(0f,0f,0f);
		@Override
		public void setView (Matrix4 proj, float x, float y, float width, float height) {
			projection.set(proj);
			viewBounds.set(x, y, width, height);
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

					if (tile != null && !cells[col][row].empty) {
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
							case TiledMapTileLayer.Cell.ROTATE_90: {
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
							case TiledMapTileLayer.Cell.ROTATE_180: {
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
							case TiledMapTileLayer.Cell.ROTATE_270: {
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
