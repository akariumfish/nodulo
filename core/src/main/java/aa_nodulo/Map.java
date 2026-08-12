package aa_nodulo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.Comparator;

public class Map {

	private final TiledMap map;
	public final OrthogonalTiledMapRenderer renderer;

	private final TiledMapTileLayer mapLayer;
	private TiledMapTileLayer lightingLayer;
	private final ArrayList<TiledMapTile> lightingTiles;

	private final Texture pixel;
	private final StaticTiledMapTile brush;

	private final SpriteBatch lightingBatch;
	private final FrameBuffer lightingFrameBuffer;
	private final TextureRegion lightingTexture;

	private final int maxCaveHeight;
	private final int lightingTickSpeed;

	private int currentLightingCoordinate = 0;
	
	public Map(String path) {
		this(path, 4, 10);
	}

	public Map(String path, int maxCaveHeight, int lightingTickSpeed) {
		this(new InternalFileHandleResolver(), path, maxCaveHeight, lightingTickSpeed);
	}

	public Map(FileHandleResolver resolver, String path, int maxCaveHeight, int lightingTickSpeed) {
		this.maxCaveHeight = maxCaveHeight;
		this.lightingTickSpeed = lightingTickSpeed;

		map = new TmxMapLoader(resolver).load(path);
		mapLayer = (TiledMapTileLayer) map.getLayers().get("Map");

		renderer = new OrthogonalTiledMapRenderer(map, 1f / mapLayer.getTileWidth());

		pixel = generatePixel(1, 1, Color.WHITE);

		brush = new StaticTiledMapTile(new TextureRegion(generatePixel(mapLayer.getTileWidth(), mapLayer.getTileHeight(), Color.TEAL)));

		lightingBatch = new SpriteBatch();
		lightingBatch.disableBlending();
		lightingBatch.getProjectionMatrix().setToOrtho2D(0, 0, mapLayer.getWidth(), mapLayer.getHeight());

		lightingFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, mapLayer.getWidth(), mapLayer.getHeight(), false);

		lightingTexture = new TextureRegion(lightingFrameBuffer.getColorBufferTexture());
		lightingTexture.flip(false, true);

		lightingTiles = generateLightingTiles(map.getTileSets().getTileSet("Lighting"));

		setupLightingLayer();
		lightingLayer.setVisible(false);
	}

	private void setupLightingLayer() {
		lightingLayer = new TiledMapTileLayer(mapLayer.getWidth(), mapLayer.getHeight(), mapLayer.getTileWidth(), mapLayer.getTileHeight());
		map.getLayers().add(lightingLayer);
	}

	// might be better to load a texture instead of creating one
	private Texture generatePixel(int width, int height, Color color) {
		Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.fill();
		return new Texture(pixmap);
	}

	// generate lighting during every frame for a certain amount of tiles to not overload the gpu. (=> basically "async")
	public void update() {
		lightingFrameBuffer.begin();
		lightingBatch.begin();

		for (int i = 0; i < lightingTickSpeed && currentLightingCoordinate < lightingLayer.getWidth() * lightingLayer.getHeight(); i++, currentLightingCoordinate++) {
			int x = currentLightingCoordinate / lightingLayer.getHeight();
			int invY = currentLightingCoordinate % lightingLayer.getHeight();
			int y = lightingLayer.getHeight() - 1 - invY;

			if (y <= maxCaveHeight || mapLayer.getCell(x, y) != null)
				setTileLighting(x, y);
		}

		lightingBatch.end();
		lightingFrameBuffer.end();
	}

	public void render(Matrix4 projectionMat, float viewboundsX, float viewboundsy, 
			float viewboundsWidth, float viewboundsHeight) {
		renderer.setView(projectionMat, viewboundsX, viewboundsy, 
				viewboundsWidth, viewboundsHeight);
		renderer.render();
	}

	public void delCell(float x, float y) {
		mapLayer.setCell((int)x, (int)y, null);
		checkAndRecalculateTileLighting((int)x, (int)y);
	}
	public void addCell(float x, float y) {
		TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
		cell.setTile(brush);
		mapLayer.setCell((int)x, (int)y, cell);
		checkAndRecalculateTileLighting((int)x, (int)y);
	}

	public void checkAndRecalculateTileLighting(int x, int y) {
		lightingFrameBuffer.begin();
		lightingBatch.begin();

		for (int dY = y - 1; dY <= y + 1; dY++) {
			for (int dX = x - 1; dX <= x + 1; dX++) {
				// when map layers cell got removed we need to update the lighting layer as well
				TiledMapTileLayer.Cell mapCell = mapLayer.getCell(dX, dY);
				TiledMapTileLayer.Cell lightingCell = lightingLayer.getCell(dX, dY);

				// map is null but lighting is set, get rid of that!
				if (dY > maxCaveHeight && mapCell == null && lightingCell != null) {
					lightingBatch.setColor(new Color(0, 0, 0, 0));
					lightingBatch.draw(pixel, dX, dY);

					lightingLayer.setCell(dX, dY, null);
				}
				else if (dY <= maxCaveHeight || mapCell != null) {
					setTileLighting(dX, dY);
				}
			}
		}
		lightingBatch.end();
		lightingFrameBuffer.end();
	}

	private void setTileLighting(int x, int y) {
		float average = calculateAverageLightingOfTile(mapLayer, x, y);

		lightingBatch.setColor(new Color(0, 0, 0, average));
		lightingBatch.draw(pixel, x, y);

		// since the average generates floats from 0.0 to 0.1 in steps of 0.1 (e.g. 0.1XXXX, 0.2XXXX, 0.3XXXX) we can clamp it and figure out the index via that
		int index = (int) (average * 10);

		// create the cell with the appropriately tinted tile
		TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
		cell.setTile(lightingTiles.get(index));
		lightingLayer.setCell(x, y, cell);
	}

	// calculates lighting average for this one specific tile according to https://gamedev.stackexchange.com/a/126165
	// calculate by how many tiles the current tile is surrounded. coords outside of the map also count as tiles
	private float calculateAverageLightingOfTile(TiledMapTileLayer layer, int x, int y) {
		int averageSum = 0;
		for (int dY = y - 1; dY <= y + 1; dY++) {
			for (int dX = x - 1; dX <= x + 1; dX++) {
				if (dX == x && dY == y) // ignore when it's the current tile
					continue;

				boolean foundTile = false;
				if (dX < 0 || dY < 0 || dX >= layer.getWidth() || dY >= layer.getHeight())
					foundTile = true; // out of bounds
				else if (layer.getCell(dX, dY) != null)
					foundTile = true;
				else if (y <= maxCaveHeight)
					foundTile = true;

				averageSum += foundTile ? 1 : 0;
			}
		}

		return averageSum / 9f;
	}

	// just creates a ordered list of tiles
	// each tile in the tileset has a "index" property, 0 means it's transparent, 1 means it's black.
	// and since there's 9 different possible combinations, the texture has 9 different tiles
	private ArrayList<TiledMapTile> generateLightingTiles(TiledMapTileSet tileset) {
		ArrayList<TiledMapTile> tiles = new ArrayList<>();
		tileset.iterator().forEachRemaining(tiles::add);
		tiles.sort(Comparator.comparing(o -> o.getProperties().get("index", Integer.class)));
		return tiles;
	}

	public TextureRegion getLightingTexture() {
		return lightingTexture;
	}

	public void toggleLightingLayerVisibility() {
		lightingLayer.setVisible(!lightingLayer.isVisible());
	}

	public boolean isLightingLayerVisible() {
		return lightingLayer.isVisible();
	}

	public int getWidth() {
		return lightingLayer.getWidth();
	}

	public int getHeight() {
		return lightingLayer.getHeight();
	}

	// note: instead of regenerating the whole lighting layer
	// only update the tiles surrounding the tile you updated
	// this would increase performance by quite a bit
	public void restartLightingGeneration() {
		// destroy the lighting layer and create a new one
		// also remember if the layer was visible or not
		boolean wasLightingLayerVisible = lightingLayer.isVisible();
		map.getLayers().remove(lightingLayer);
		setupLightingLayer();
		lightingLayer.setVisible(wasLightingLayerVisible);

		// clear the framebuffer with transparent
		lightingFrameBuffer.begin();
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		lightingFrameBuffer.end();

		// reset "async" coordinate
		currentLightingCoordinate = 0;
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
