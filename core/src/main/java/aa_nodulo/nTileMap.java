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
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;

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

import java.util.ArrayList;
import java.util.Comparator;

public class nTileMap {

	private final TiledMap map;
	public final RendererOrtho renderer;

	public final TiledMapTileLayer mapLayer; 
//	TiledMapTileLayer lightingLayer;
//	private final ArrayList<TiledMapTile> lightingTiles;

	private final Texture pixel;
	private final StaticTiledMapTile brush;

//	private final SpriteBatch lightingBatch;
//	private final VfxFrameBuffer lightingFrameBuffer;
//	private final TextureRegion lightingTexture;

	private final int maxCaveHeight;
	private int lightingTickSpeed;

	public final float tile_scale = 250f;

	private int currentLightingCoordinate = 0;

	private pView view; 
	private OrthographicCamera cam;

	public nTileMap(String path, Batch batch, 
			pView v, OrthographicCamera c) {
		this(path, -1, 10, batch, v, c);
	}

	public nTileMap(String path, int maxCaveHeight, int lightingTickSpeed, Batch batch, 
			pView v, OrthographicCamera c) {
		this(new InternalFileHandleResolver(), path, maxCaveHeight, lightingTickSpeed, 
				batch, v, c);
	}

	public nTileMap(FileHandleResolver resolver, String path, 
			int maxCaveHeight, int lightingTickSpeed, Batch batch, 
			pView v, OrthographicCamera c) {
		this.maxCaveHeight = maxCaveHeight;
		this.lightingTickSpeed = lightingTickSpeed;
		this.view = v; 
		this.cam = c;

		map = new TmxMapLoader(resolver).load(path);
		mapLayer = (TiledMapTileLayer) map.getLayers().get("Map");

		renderer = new RendererOrtho(map, 1f / mapLayer.getTileWidth());

		pixel = generatePixel(1, 1, Color.WHITE);

		brush = new StaticTiledMapTile(new TextureRegion(generatePixel(mapLayer.getTileWidth(), mapLayer.getTileHeight(), Color.TEAL)));

//		lightingBatch = new SpriteBatch();
//		lightingBatch.disableBlending();
//		lightingBatch.getProjectionMatrix().setToOrtho2D(0, 0, mapLayer.getWidth(), mapLayer.getHeight());
//
//		lightingFrameBuffer = new VfxFrameBuffer(Pixmap.Format.RGBA8888);
//		lightingFrameBuffer.initialize(mapLayer.getWidth(), mapLayer.getHeight());
//
//		lightingTexture = new TextureRegion(lightingFrameBuffer.getTexture());
//		lightingTexture.flip(false, true);
//
//		lightingTiles = generateLightingTiles(map.getTileSets().getTileSet("Lighting"));
//
//		setupLightingLayer();
//		lightingLayer.setVisible(false);


//		toggleLightingLayerVisibility();
//		restartLightingGeneration();

//		updateAll();

	}

//	private void setupLightingLayer() {
//		lightingLayer = new TiledMapTileLayer(mapLayer.getWidth(), mapLayer.getHeight(), mapLayer.getTileWidth(), mapLayer.getTileHeight());
//		map.getLayers().add(lightingLayer);
//	}

	// might be better to load a texture instead of creating one
	private Texture generatePixel(int width, int height, Color color) {
		Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.fill();
		return new Texture(pixmap);
	}

//	public void updateAll() {
//		int t = lightingTickSpeed;
//		int c = currentLightingCoordinate;
//		currentLightingCoordinate = 0;
//		lightingTickSpeed = lightingLayer.getWidth() * lightingLayer.getHeight() + 1;
//		update();
//		currentLightingCoordinate = c;
//		lightingTickSpeed = t;
//	}
//
//	// generate lighting during every frame for a certain amount of tiles to not overload the gpu. (=> basically "async")
//	public void update() {
//		lightingFrameBuffer.begin();
//		lightingBatch.begin();
//
//		for (int i = 0; 
//				i < lightingTickSpeed && 
//				currentLightingCoordinate < 
//				lightingLayer.getWidth() * lightingLayer.getHeight(); 
//				i++, currentLightingCoordinate++) {
//
//			int x = currentLightingCoordinate / lightingLayer.getHeight();
//			int invY = currentLightingCoordinate % lightingLayer.getHeight();
//			int y = lightingLayer.getHeight() - 1 - invY;
//
//			setTileLighting(x, y);
//		}
//
//		lightingBatch.end();
//		lightingFrameBuffer.end();
//	}

	public void render() {

		view.app.gdx.drawer.end();

//		update();

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
	public float getTileWidth() {
		return tile_scale;//*mapLayer.getTileWidth();
	}
	public float getTileHeight() {
		return tile_scale;//*mapLayer.getTileHeight();
	}
	public Vector2 getCellPos(int x, int y) {
		final int layerWidth = mapLayer.getWidth();
		final int layerHeight = mapLayer.getHeight();
		Vector2 p = new Vector2(x,y)
				.sub(layerWidth/2f,layerHeight/2f)
				.scl(getTileWidth(),getTileHeight());
		return p;
	}
	public <T> T getCellProp(float x, float y, String r, Class<T> ct) {
		return getCell(x, y).getTile()
				.getProperties().get(r,ct);
	}
	public TiledMapTileLayer.Cell getCell(float x, float y) {
		Vector2 s = new Vector2(x,y);
		s.scl(1f/tile_scale);
		s.add(mapLayer.getWidth()/2f, 
				mapLayer.getHeight()/2f);
		return mapLayer.getCell((int)(s.x), (int)(s.y));
	}
	public void delCell(Vector2 v) { delCell(v.x,v.y); }
	public void delCell(float x, float y) {
		Vector2 s = new Vector2(x,y);
		s.scl(1f/tile_scale);
		s.add(mapLayer.getWidth()/2f, 
				mapLayer.getHeight()/2f);
		mapLayer.setCell((int)(s.x), (int)(s.y), null);
//		checkAndRecalculateTileLighting((int)(s.x), (int)(s.y));
//		updateAll();
	}
	public void addCell(Vector2 v) { addCell(v.x,v.y); }
	public void addCell(float x, float y) {
		Vector2 s = new Vector2(x,y);
		s.scl(1f/tile_scale);
		s.add(mapLayer.getWidth()/2f, 
				mapLayer.getHeight()/2f);
		TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
		cell.setTile(brush);
		mapLayer.setCell((int)(s.x), (int)(s.y), cell);
//		checkAndRecalculateTileLighting((int)(s.x), (int)(s.y));
//		updateAll();
	}

//	public void checkAndRecalculateTileLighting(int x, int y) {
//		lightingFrameBuffer.begin();
//		lightingBatch.begin();
//		for (int dY = y - 1; dY <= y + 1; dY++) {
//			for (int dX = x - 1; dX <= x + 1; dX++) {
//				setTileLighting(dX, dY);
//			}
//		}
//		lightingBatch.end();
//		lightingFrameBuffer.end();
//	}

//	private void setTileLighting(int x, int y) {
//		TiledMapTileLayer.Cell mapCell = mapLayer.getCell(x, y);
//
//		float average = 0f;
//		if (mapCell != null) {
//			average = calculateAverageLightingOfTile(mapLayer, x, y);
//		} else {
//			average = 9f * calculateAverageLightingOfTile(mapLayer, x, y);
//			if (average > 1f) average = 1f + (average - 1f) / 2f;
//			average /= 9f;
//		}
//
//		lightingBatch.setColor(new Color(0, 0, 0, average));
//		lightingBatch.draw(pixel, x, y);
//
//		// since the average generates floats from 0.0 to 0.1 in steps of 0.1 (e.g. 0.1XXXX, 0.2XXXX, 0.3XXXX) we can clamp it and figure out the index via that
//		int index = (int) (average * 10);
//
//		// create the cell with the appropriately tinted tile
//		TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
//		cell.setTile(lightingTiles.get(index));
//		lightingLayer.setCell(x, y, cell);
//	}
//
//	// calculates lighting average for this one specific tile according to https://gamedev.stackexchange.com/a/126165
//	// calculate by how many tiles the current tile is surrounded. coords outside of the map also count as tiles
//	private float calculateAverageLightingOfTile(TiledMapTileLayer layer, int x, int y) {
//		int averageSum = 0;
//		for (int dY = y - 1; dY <= y + 1; dY++) {
//			for (int dX = x - 1; dX <= x + 1; dX++) {
//				if (dX == x && dY == y) // ignore when it's the current tile
//					continue;
//
//				boolean foundTile = false;
//				//				if (dX < 0 || dY < 0 || dX >= layer.getWidth() || dY >= layer.getHeight())
//				//					foundTile = true; // out of bounds
//				//				else 
//				if (!(dX < 0 || dY < 0 || 
//						dX >= layer.getWidth() || dY >= layer.getHeight()) && 
//						layer.getCell(dX, dY) != null && 
//						(layer.getCell(dX, dY).getTile()
//								.getProperties().get("light", Boolean.class) != null && 
//								!layer.getCell(dX, dY).getTile()
//								.getProperties().get("light", Boolean.class)))
//					foundTile = true;
//				//				else if (y <= maxCaveHeight)
//				//					foundTile = true;
//
//				if (dX == x || dY == y)
//					averageSum += foundTile ? 1f : 0f;
//				else averageSum += foundTile ? 0.9f : 0f;
//			}
//		}
//
//		return averageSum / 9f;
//	}
//
//	// just creates a ordered list of tiles
//	// each tile in the tileset has a "index" property, 0 means it's transparent, 1 means it's black.
//	// and since there's 9 different possible combinations, the texture has 9 different tiles
//	private ArrayList<TiledMapTile> generateLightingTiles(TiledMapTileSet tileset) {
//		ArrayList<TiledMapTile> tiles = new ArrayList<>();
//		tileset.iterator().forEachRemaining(tiles::add);
//		tiles.sort(Comparator.comparing(o -> o.getProperties().get("index", Integer.class)));
//		return tiles;
//	}
//
//	public TextureRegion getLightingTexture() {
//		return lightingTexture;
//	}
//
//	public void toggleLightingLayerVisibility() {
//		lightingLayer.setVisible(!lightingLayer.isVisible());
//	}
//
//	public boolean isLightingLayerVisible() {
//		return lightingLayer.isVisible();
//	}
//
//	public int getWidth() {
//		return lightingLayer.getWidth();
//	}
//
//	public int getHeight() {
//		return lightingLayer.getHeight();
//	}
//
//	// note: instead of regenerating the whole lighting layer
//	// only update the tiles surrounding the tile you updated
//	// this would increase performance by quite a bit
//	public void restartLightingGeneration() {
//		// destroy the lighting layer and create a new one
//		// also remember if the layer was visible or not
//		boolean wasLightingLayerVisible = lightingLayer.isVisible();
//		map.getLayers().remove(lightingLayer);
//		setupLightingLayer();
//		lightingLayer.setVisible(wasLightingLayerVisible);
//
//		// clear the framebuffer with transparent
//		lightingFrameBuffer.begin();
//		Gdx.gl.glClearColor(0, 0, 0, 0);
//		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
//		lightingFrameBuffer.end();
//
//		// reset "async" coordinate
//		currentLightingCoordinate = 0;
//	}






	public class RendererOrtho extends BatchTiledMapRenderer {

		public RendererOrtho (TiledMap map) {
			super(map);
		}

		public RendererOrtho (TiledMap map, Batch batch) {
			super(map, batch);
		}

		public RendererOrtho (TiledMap map, float unitScale) {
			super(map, unitScale);
		}

		public RendererOrtho (TiledMap map, float unitScale, Batch batch) {
			super(map, unitScale, batch);
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

		@Override
		public void render() {
			beginRender();

			tmp_transf.set(batch.getTransformMatrix());
			batch.setTransformMatrix(transform);

			for (MapLayer layer : map.getLayers()) {
				renderMapLayer(layer);
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
				if (layer instanceof TiledMapTileLayer) {
					renderTileLayer((TiledMapTileLayer)layer);
				} else if (layer instanceof TiledMapImageLayer) {
					renderImageLayer((TiledMapImageLayer)layer);
				} else {
					renderObjects(layer);
				}
			}
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

/*
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Map map;

    private Viewport gameViewport;

    private Stage ui;

    @Override
    public void create() {
        VisUI.load();
        this.batch = new SpriteBatch();

        map = new Map("Map.tmx");

        this.gameViewport = new ExtendViewport(map.getWidth(), map.getHeight());
        this.ui = new Stage(new ExtendViewport(1280, 720));

        initUi();
    }

    // setup cool button to toggle mode
    private void initUi() {
        Gdx.input.setInputProcessor(ui);

        VisTable root = new VisTable();
        root.setFillParent(true);

        VisTable buttonbar = new VisTable();

        VisTextButton changeModeButton = new VisTextButton("Change Lighting to Tilemap Layer");
        changeModeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeModeButton.setText("Change Lighting to " + (map.isLightingLayerVisible() ? "Tilemap Layer" : "Smooth texture"));
                map.toggleLightingLayerVisibility();
            }
        });
        buttonbar.add(changeModeButton);

        VisTextButton restartLightGenerationButton = new VisTextButton("Restart Light generation");
        restartLightGenerationButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                map.restartLightingGeneration();
            }
        });
        buttonbar.add(restartLightGenerationButton).padLeft(12);

        root.add(buttonbar).top().left().expand().padTop(8).padLeft(8);
        ui.addActor(root);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.706f * 0.25f, 0.851f * 0.25f, 0.847f * 0.25f, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        // render map
        gameViewport.apply();
        map.update();
        map.render((OrthographicCamera) gameViewport.getCamera());

        // render pixmap/texture
        if (!map.isLightingLayerVisible()) {
            gameViewport.apply();
            batch.setProjectionMatrix(gameViewport.getCamera().combined);
            batch.begin();
            batch.draw(map.getLightingTexture(), 0, 0, map.getWidth(), map.getHeight());
            batch.end();
        }

        // render cool button
        ui.getViewport().apply();
        ui.act();
        ui.draw();
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        ui.getViewport().update(width, height, true);
    }
} 
 */
