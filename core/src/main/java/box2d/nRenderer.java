package box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.noodle.nodulo.GdxApp;

import aa_nodulo.PlaneApplet;
import aa_nodulo.pView;
import util.Utl;
import util.nRun;
import app.App;
import box2d.TileLayer.Cell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class nRenderer {
	
	
	
	
	
	
	public static class RunLayer extends Layer {

		ArrayList<nRun> runs = new ArrayList<nRun>();
		HashMap<nRun, Integer> prios = new HashMap<nRun, Integer>();
		private int max_prio = 0;
		
		public void addRun(nRun r) { addRun(0,r); }
		public void addRun(int prio, nRun r) { 
			runs.add(r); prios.put(r, prio); max_prio = Math.max(max_prio, prio); }
		public void removeRun(nRun r) { runs.remove(r); }
		public void clearRuns() { runs.clear(); }

		public RunLayer(nRenderer m) { this(m,0); }
		public RunLayer(nRenderer m, int p) {
			super(m,p);
		}
		
		public void render() {

			ArrayList<nRun> all = Utl.duplic(runs);
			
			for (int prio = 0 ; prio <= max_prio ; prio++)
				for (nRun d : runs) 
					if (prios.get(d) == prio) { d.run(); all.remove(d); }
			
			for (nRun d : all) d.run(); 
			
		}
	}

	

	public GroupLayer newGroupLayer() { return new GroupLayer(this); }
	public GroupLayer newGroupLayer(int p) { return new GroupLayer(this,p); }
	public RunLayer newRunLayer() { return new RunLayer(this); }
	public RunLayer newRunLayer(int p) { return new RunLayer(this,p); }
	public ThreeDLayer new3DLayer(int p) { return new ThreeDLayer(this,p); }
	public LightLayer newLightLayer(LightLayer.MODE m) { return new LightLayer(this, m); }
	public LightLayer newLightLayer(LightLayer.MODE m, int p) { return new LightLayer(this, m,p); }
	public TileLayer newTileLayer() { return new TileLayer(this); }
	public TileLayer newTileLayer(int p) { return new TileLayer(this,p); }
	
	public static class GroupLayer extends Layer {
		
		public ArrayList<Layer> layers = new ArrayList<Layer>();

		HashMap<Layer, Integer> prios = new HashMap<Layer, Integer>();
		private int max_prio = 0;
		
		public void addLayer(Layer r) { addLayer(0,r); }
		public void addLayer(int prio, Layer r) { 
			r.grouped = true; layers.add(r); 
			prios.put(r, prio); max_prio = Math.max(max_prio, prio); }
		public void removeLayer(Layer r) { r.grouped = false; layers.remove(r); }
		public void clearLayer() { for (Layer l : layers) l.grouped = false; layers.clear(); }
		
		public GroupLayer newGroupLayer() { return (GroupLayer) new GroupLayer(rend).addTo(this); }
		public GroupLayer newGroupLayer(int p) { return (GroupLayer) new GroupLayer(rend).addTo(this,p); }
		public RunLayer newRunLayer() { return (RunLayer) new RunLayer(rend).addTo(this); }
		public RunLayer newRunLayer(int p) { return (RunLayer) new RunLayer(rend).addTo(this,p); }
		public LightLayer newLightLayer(LightLayer.MODE m) { return (LightLayer) new LightLayer(rend, m).addTo(this); }
		public LightLayer newLightLayer(LightLayer.MODE m, int p) { return (LightLayer) new LightLayer(rend, m).addTo(this,p); }
		public TileLayer newTileLayer() { return (TileLayer) new TileLayer(rend).addTo(this); }
		public TileLayer newTileLayer(int p) { return (TileLayer) new TileLayer(rend).addTo(this,p); }

		public GroupLayer(nRenderer m) { super(m); }
		public GroupLayer(nRenderer m, int p) { super(m,p); }
		
		public void render() {

			ArrayList<Layer> all = Utl.duplic(layers);
			
			for (int prio = 0 ; prio <= max_prio ; prio++)
				for (Layer d : layers) 
					if (prios.get(d) == prio) { d.render(); all.remove(d); }
			
			for (Layer d : all) if (!d.grouped) d.render(); 
			
		}
	}
	
	
	
	
	public static abstract class Layer {

		public nRenderer rend;
		public int prio;
		public boolean grouped = false;

		public Layer(nRenderer m) { this(m,0); }
		public Layer(nRenderer m, int p) {
			rend = m;
			rend.layers.add(this);
			prio = p;
			rend.prios.put(this, p);
		}

		public abstract void render();

		public Layer addTo(GroupLayer g) { g.addLayer(0,this); return this; }
		public Layer addTo(GroupLayer g, int p) { g.addLayer(p,this); return this; }
		
	}
	
	public final ArrayList<Layer> layers = new ArrayList<Layer>();

	HashMap<Layer, Integer> prios = new HashMap<Layer, Integer>();
	private int max_prio = 0;
	

	public void render() {

		rayHandler.beginRender();
		
		ArrayList<Layer> all = Utl.duplic(layers);
		
		for (int prio = 0 ; prio <= max_prio ; prio++)
			for (Layer d : layers) if (!d.grouped)
				if (prios.get(d) == prio) { d.render(); all.remove(d); }
		
		for (Layer d : all) if (!d.grouped) d.render(); 
		
		rayHandler.endLayeredRender();
		
	}
	
	GroupLayer roomGroup;

	public LightLayer visionLayer;
	public LightLayer colorLayer;
	public LightLayer lightLayer;
	public LightLayer auraLayer;
	public LightLayer solidLayer;
	public TileLayer tileLayer;
	
	public void prepareLayers() {
		
		RunLayer updateLayer = newRunLayer(0);
		LightLayer groundLayer = newLightLayer(LightLayer.MODE.SOLID,1);
		
//		new3DLayer(2);
		
		roomGroup = newGroupLayer(3);
		LightLayer fogLayer = newLightLayer(LightLayer.MODE.SOLID,4);
		
		GroundLight grnd = new GroundLight(groundLayer, false);
		GroundLight fog = new GroundLight(fogLayer, true);
		
		updateLayer.addRun(new nRun(grnd,fog) { public void run() {
			GroundLight g = arg(0, GroundLight.class);
			GroundLight f = arg(1, GroundLight.class);
			Vector2 p = new Vector2(view.val_cam_pos.get());
			p.scl(-1f/4f); g.setPos(p);
			p.scl(2f); f.setPos(p);
			g.setActive(box.drawground());
			f.setActive(box.drawfog());
		}});
		
		tileLayer = roomGroup.newTileLayer(2);
		
		auraLayer = roomGroup.newLightLayer(LightLayer.MODE.AURA,3);
		
		solidLayer = roomGroup.newLightLayer(LightLayer.MODE.SOLID,5);
		
		colorLayer = roomGroup.newLightLayer(LightLayer.MODE.COLOR,6);
		lightLayer = roomGroup.newLightLayer(LightLayer.MODE.LIGHT,7);

		visionLayer = roomGroup.newLightLayer(LightLayer.MODE.VISION,8);

	}
	
	
	

	public void processMap(TiledMap tilemap) {
		int layer_cnt = tilemap.getLayers().getCount();
		
		for (int id = 0 ; id < layer_cnt ; id++) {
			MapLayer layer = map.getLayers().get(id);
			if (!layer.isVisible()) continue;
			MapProperties prop = layer.getProperties();
			if (!Utl.getBoo(prop,"flags") && (layer instanceof TiledMapTileLayer)) {
				TiledMapTileLayer tl = (TiledMapTileLayer) layer;
				tileLayer.loadMap(tl);
			} 
		}
		for (int id = 0 ; id < layer_cnt ; id++) {
			MapLayer layer = map.getLayers().get(id);
			if (!layer.isVisible()) continue;
			if (!(layer instanceof TiledMapTileLayer)) {
				loadLayerObject(layer); }
		}
		for (Body body : tileLayer.ground_bod) {
			visionLayer.light_blocker.add(body); }
		
		lightLayer.newCrossAmbiantLight(Utl.color(255), tileLayer);
		
		for (int id = 0 ; id < layer_cnt ; id++) {
			MapLayer layer = map.getLayers().get(id);
			if (!layer.isVisible()) continue;
			MapProperties prop = layer.getProperties();
			if (Utl.getBoo(prop,"flags") && (layer instanceof TiledMapTileLayer)) {
				TiledMapTileLayer tl = (TiledMapTileLayer) layer;
				for (int i = 0 ; i < tileLayer.map_width ; i++)
					for (int j = 0 ; j < tileLayer.map_height ; j++) {
						TiledMapTileLayer.Cell c = tl.getCell(i,j);
						if (c == null) continue;
						MapProperties prp = c.getTile().getProperties();
						if (prp.get("id", Integer.class) != null) {
							int cellid = prp.get("id", Integer.class);
							int dir = prp.get("dir", Integer.class);
							String col = prp.get("color", String.class);
							float rot = (float)Math.PI * 2f * dir / 360f;
							Vector2 pos = new Vector2(tileLayer.getCellPos(i,j))
									.add(tile_scale / 2f, tile_scale / 2f);
							if (cellid == 0 && col.equals("green")) 
								box.setAvatarSpawn(pos, rot);
							else box.addMobSpawn(pos, rot, cellid, col);
						}
					}
				
			} 
		}
	}

	public void loadLayerObject(MapLayer layer) {
		for (MapObject m : layer.getObjects()) {
			if (!m.isVisible()) continue;
			MapProperties prop = m.getProperties();
			if (Utl.getBoo(prop,"colorLayer")) {
				colorLayer.loadMapObject(prop);
			}
			if (Utl.getBoo(prop,"lightLayer")) {
				lightLayer.loadMapObject(prop);
			}
		}
	}

	public void clearMap() {
		visionLayer.light_blocker.clear();
		for (RayHandler.AbstractLight l : rayHandler.map_lights) {
			l.remove(true); }
		rayHandler.map_lights.clear();
		box.mobspawn.clear();
		for (Body b : tileLayer.map_bod) box.world.destroyBody(b);
		tileLayer.map_bod.clear();
		
		auraLayer.dispose();
		solidLayer.dispose();
		colorLayer.dispose();
		lightLayer.dispose();
		visionLayer.dispose();

	}
	
	
	
	
	private TiledMap map;
	
	public final float tile_scale = 200f;

	public pView view; 
	public OrthographicCamera cam;

	public RayHandler rayHandler;
	public pBox2d box;
	public PlaneApplet app;
	public World world;
	
	public ArrayList<String> map_files = new ArrayList<String>();
	
	public nRenderer(pBox2d b, World w) { //String path, 
		app = b.app;
		box = b;
		world = w;
		this.view = app.view; 
		this.cam = new OrthographicCamera(GdxApp.WIDTH, GdxApp.HEIGHT);

		rayHandler = new RayHandler(app, world);

		FileHandle[] files = Gdx.files.local("/").list();
		for(FileHandle fl : files) {
			if (fl.extension().equals("tmx")) {
				map_files.add(fl.name());
			}
		}
		
		prepareLayers();
		
//		map = new TmxMapLoader(new InternalFileHandleResolver()).load(path);
//		
//		processMap(map);
		
	}
	
	public boolean map_is_setup = false;
	
	public void setupMap(String path) {

		if (map_is_setup) clearMap();
		
		map_is_setup = true;
		
		map = new TmxMapLoader(new InternalFileHandleResolver()).load(path);
		
		processMap(map);
		
	}
	
	public void dispose() {
		rayHandler.dispose();
	}

	public PointLight newAuraLight(Color col, float dist, float x, float y) {
		return auraLayer.newPointLight(col, dist, x, y); }
	public PointLight newAuraLight(int ray, Color col, float dist, float x, float y) {
		return auraLayer.newPointLight(ray, col, dist, x, y); }

	public PointLight newLightLight(Color col, float dist, float x, float y) {
		return lightLayer.newPointLight(col, dist, x, y); }
	public PointLight newLightLight(int ray, Color col, float dist, float x, float y) {
		return lightLayer.newPointLight(ray, col, dist, x, y); }

	public void newVisionLight(Body body) {
		visionLayer.newVisionLight(body); }






}

