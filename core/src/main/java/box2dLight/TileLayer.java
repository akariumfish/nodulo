package box2dLight;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import aa_nodulo.nTileMap;

public class TileLayer {

	public TiledMapTileLayer layer;
	
	public nTileMap map;
	
	public boolean ground_mask = false;
	
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
			if (prop.get("ground", Boolean.class) != null)
				ground = prop.get("ground", Boolean.class);
			if (prop.get("light", Boolean.class) != null) {
				wall = !prop.get("light", Boolean.class);
				if (!ground && prop.get("light", Boolean.class)) empty = true;
			}
			if (!wall) build = true;
		}
	}
	
	public Cell[][] cells;
	
	public final int width, height;
	
	public TileLayer(nTileMap tm, TiledMapTileLayer ml) {
		super();
		map = tm;
		layer = ml;

		layer.getProperties().put("tilelayer", this);
		
		width = map.getMapWidth();
		height = map.getMapHeight();
		
		cells = new Cell[width][height];
		
		for (int i = 0 ; i < width ; i++)
			for (int j = 0 ; j < height ; j++) {
				TiledMapTileLayer.Cell c = layer.getCell(i,j);
				cells[i][j] = new Cell(c);
			}

		MapProperties prop = ml.getProperties();
		if (prop.get("ground", Boolean.class) != null && 
			prop.get("ground", Boolean.class)) {
			ground_mask = true;
			for (int w = width ; w > 0 ; w--)
				for (int h = height ; h > 0 ; h--) {
					search_place(w,h,false);
					search_place(h,w,false);
				}
			for (int i = 0 ; i < width ; i++)
				for (int j = 0 ; j < height ; j++) {
					cells[i][j].build = !cells[i][j].empty;
				}
			for (int w = width ; w > 0 ; w--)
				for (int h = height ; h > 0 ; h--) {
					search_place(w,h,true);
					search_place(h,w,true);
				}
			
		}
	}
	
	public void search_place(int w, int h, boolean transp) {
		for (int i = 0 ; i < width - w ; i++)
			for (int j = 0 ; j < height - h ; j++) 
				build_wall(i,j,w,h,transp);
	}

	public boolean test_place(int x, int y, int w, int h) {
		for (int i = x ; i < x + w ; i++) for (int j = y ; j < y + h ; j++) 
			if (i >= width || j >= height || cells[i][j].build) return false;
		return true;
	}
	public void build_wall(int x, int y, int w, int h, boolean transp) {
		if (!test_place(x,y,w,h)) return;
		Vector2 p = map.getCellPos(x,y);
		p.add(w*map.tile_scale/2f,h*map.tile_scale/2f);
		BodyDef groundBodyDef = new BodyDef();  
		groundBodyDef.position.set(p);  
		Body groundBody = map.world.createBody(groundBodyDef);  
		if (transp) map.rayHandler.transparent.add(groundBody);
		PolygonShape groundBox = new PolygonShape();  
		groundBox.setAsBox(w*map.tile_scale/2f,h*map.tile_scale/2f);
		groundBody.createFixture(groundBox, 0.0f);
		groundBox.dispose();
		for (int i = x ; i < x + w ; i++)
			for (int j = y ; j < y + h ; j++) 
				cells[i][j].build = true;
	}
}
