package box_render;

import java.util.ArrayList;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import aa_nodulo.PlaneApplet;
import aa_nodulo.pBody;
import aa_nodulo.pFamily;
import aa_nodulo.pProperty;
import box2d.pBox2d;
import box_render.nBatch.Unit;
import box_render.nRender.TileLayer.Cell;
import util.Utl;
import util.nRun;

public class nEngine {
	
	public static void build() {

//		pProperty def = pProperty.newGeneralProperty("deff");
//
//		def.addBodyInitRun(new nRun() {public void run() {
////			pBody bod = arg(0,pBody.class);
////			pBox2d b2d = PlaneApplet.app.getSystem(pBox2d.class);
////			if (b2d == null || bod == null) return;
////			b2d.init_body(bod);
//		}});
//
//		def.addBodyClearRun(new nRun() {public void run() {
////			pBody bod = arg(0,pBody.class);
////			pBox2d box = bod.space.app.getSystem(pBox2d.class);
////			if (box == null || bod == null) return;
////			box.clear_body(bod);
//		}});

//		def
//		.addData("density", 10f).addCtrl("density", Float.class)
//		.addData("filter", (int)0).addCtrl("filter", Integer.class)
//		.addData("aura", false).addCtrl("aura", Boolean.class)
//		;
//
//		def.newLocalProperty("def_bod")
//		.addData("pos", new Vector2())
//		.addData("rot", 0f)
//		;
		
	}

	
	
	
	
	
	
	
	public Cell[][] cells;
	public int map_width = 0, map_height = 0;
	public int tile_width = 0, tile_height = 0;

	public ArrayList<Body> ground_bod = new ArrayList<Body>();
	public ArrayList<Body> map_bod = new ArrayList<Body>();

//	public void buildBodys() {
//		for (int w = map_width ; w > 0 ; w--)
//			for (int h = map_height ; h > 0 ; h--) {
//				search_place(w,h,false,true); search_place(h,w,false,true); }
//		for (int i = 0 ; i < map_width ; i++)
//			for (int j = 0 ; j < map_height ; j++) {
//				cells[i][j].build = true;
//				if (cells[i][j].wall && cells[i][j].light) cells[i][j].build = false; }
//		for (int w = map_width ; w > 0 ; w--)
//			for (int h = map_height ; h > 0 ; h--) {
//				search_place(w,h,false,false); search_place(h,w,false,false); }
//		for (int i = 0 ; i < map_width ; i++)
//			for (int j = 0 ; j < map_height ; j++) {
//				cells[i][j].build = !cells[i][j].empty; }
//		for (int w = map_width ; w > 0 ; w--)
//			for (int h = map_height ; h > 0 ; h--) {
//				search_place(w,h,true,false); search_place(h,w,true,false); }
//	}
//	private void search_place(int w, int h, boolean transp, boolean blocview) {
//		for (int i = 0 ; i < map_width ; i++) for (int j = 0 ; j < map_height ; j++) 
//			build_wall(i,j,w,h,transp,blocview); }
//	private boolean test_place(int x, int y, int w, int h) {
//		for (int i = x ; i < x + w ; i++) for (int j = y ; j < y + h ; j++) 
//			if (i >= map_width || j >= map_height || cells[i][j].build) return false;
//		return true; }
//	private void build_wall(int x, int y, int w, int h, boolean transp, boolean blocview) {
//		if (!test_place(x,y,w,h)) return;
//		Vector2 p = render.tile.getCellPos(x,y);
//		p.add(w*render.tile.tile_scale/2f,h*render.tile.tile_scale/2f);
//		BodyDef groundBodyDef = new BodyDef();  
//		groundBodyDef.position.set(p);  
//		Body groundBody = world.createBody(groundBodyDef);  
//		map_bod.add(groundBody);
//		
//		if (!transp && blocview) ground_bod.add(groundBody); //bloc vision
//		if (!transp) box.body_breaker.add(groundBody);
//
////		if (!transp && !blocview) rend.visionLayer.transparent.add(groundBody);
////		if (!transp && !blocview) rend.lightLayer.transparent.add(groundBody);
////		if (!transp && !blocview) rend.colorLayer.transparent.add(groundBody);
////		
////		if (transp) rend.lightLayer.transparent.add(groundBody);
////		if (transp) rend.visionLayer.transparent.add(groundBody);
////		if (transp) rend.colorLayer.transparent.add(groundBody);
////		if (transp) rend.auraLayer.transparent.add(groundBody);
//		
//		PolygonShape groundBox = new PolygonShape();  
//		groundBox.setAsBox(w*render.tile.tile_scale/2f,h*render.tile.tile_scale/2f);
//		Fixture fixture = groundBody.createFixture(groundBox, 0.0f);
////		fixture.setUserData(new LightData(1f, true));
//		
//		groundBox.dispose();
//		for (int i = x ; i < x + w ; i++) for (int j = y ; j < y + h ; j++) 
//			cells[i][j].build = true;
//	}

	private void processMap(TiledMap tilemap) {
		int layer_cnt = tilemap.getLayers().getCount();
		for (int id = 0 ; id < layer_cnt ; id++) {
			MapLayer layer = tilemap.getLayers().get(id);
			if (!layer.isVisible()) continue;
			MapProperties prop = layer.getProperties();
			if (!Utl.getBoo(prop,"flags") && (layer instanceof TiledMapTileLayer)) {
				render.processMap((TiledMapTileLayer)layer); } 
		}

		cells = render.tile.cells;
		map_width = render.tile.map_width; map_height = render.tile.map_height;
		tile_width = render.tile.tile_width; tile_height = render.tile.tile_height;
//
//		buildBodys();
//		
//		for (int id = 0 ; id < layer_cnt ; id++) {
//			MapLayer layer = map.getLayers().get(id);
//			if (!layer.isVisible()) continue;
//			if (!(layer instanceof TiledMapTileLayer)) {
//				loadLayerObject(layer); }
//		}
////		for (Body body : tileLayer.ground_bod) {
////			visionLayer.light_blocker.add(body); }
////		
////		lightLayer.newCrossAmbiantLight(Utl.color(255), tileLayer);
//		
//		for (int id = 0 ; id < layer_cnt ; id++) {
//			MapLayer layer = map.getLayers().get(id);
//			if (!layer.isVisible()) continue;
//			MapProperties prop = layer.getProperties();
//			if (Utl.getBoo(prop,"flags") && (layer instanceof TiledMapTileLayer)) {
//				TiledMapTileLayer tl = (TiledMapTileLayer) layer;
//				for (int i = 0 ; i < map_width ; i++)
//					for (int j = 0 ; j < map_height ; j++) {
//						TiledMapTileLayer.Cell c = tl.getCell(i,j);
//						if (c == null) continue;
//						MapProperties prp = c.getTile().getProperties();
//						if (prp.get("id", Integer.class) != null) {
//							int cellid = prp.get("id", Integer.class);
//							int dir = prp.get("dir", Integer.class);
//							String col = prp.get("color", String.class);
//							float rot = (float)Math.PI * 2f * dir / 360f;
//							Vector2 pos = new Vector2(render.tile.getCellPos(i,j))
//									.add(render.tile.tile_scale / 2f, render.tile.tile_scale / 2f);
//							if (cellid == 0 && col.equals("green")) 
//								box.setAvatarSpawn(pos, rot);
//							else box.addMobSpawn(pos, rot, cellid, col);
//						}
//					}
//				
//			} 
//		}
	}

	private MapLayer objectLayer;
	private void loadLayerObject(MapLayer layer) {
		objectLayer = layer;
		for (MapObject m : layer.getObjects()) {
			if (!m.isVisible()) continue;
			MapProperties prop = m.getProperties();
			loadMapObject(prop);
		}
	}
	private void loadMapObject(MapProperties prop) {
		if (Utl.getBoo(prop,"pointlight")) {
			float dist = prop.get("dist", Float.class);
			Color col = prop.get("color", Color.class);
			Vector2 pos = render.tile.mapToSpace(prop.get("x", Float.class), 
					prop.get("y", Float.class));
			if (Utl.getBoo(prop,"colorLayer")) {
				newUnit("point_color",pos.x,pos.y,0f,dist,col.toFloatBits()); }
			if (Utl.getBoo(prop,"lightLayer")) {
				newUnit("point_light",pos.x,pos.y,0f,dist,col.toFloatBits()); }
		}
		if (Utl.getBoo(prop,"conelight")) {
			float dir = prop.get("dir", Float.class) - 0.25f; // 0 = 0deg, 0.5 = 180deg
			float cone = prop.get("cone", Float.class); // 0 = 0deg, 0.5 = 180deg
			float dist = prop.get("dist", Float.class);
			Color col = prop.get("color", Color.class);
			Vector2 pos = render.tile.mapToSpace(prop.get("x", Float.class), 
					prop.get("y", Float.class));
			if (Utl.getBoo(prop,"colorLayer")) {
				newUnit("spot_color",pos.x,pos.y,dir*Utl.DPI,cone,dist,col.toFloatBits()); }
			if (Utl.getBoo(prop,"lightLayer")) {
				newUnit("spot_light",pos.x,pos.y,dir*Utl.DPI,cone,dist,col.toFloatBits()); }
		}
	}

	public boolean map_is_setup = false;
	public String current_map_path = "";

	private TiledMap map;
	
	public void setupMap(String path) {
		if (current_map_path.equals(path)) return;
		current_map_path = Utl.copy(path);
		if (map_is_setup) clearMap();
		map_is_setup = true;
		map = new TmxMapLoader(new InternalFileHandleResolver()).load(path);
		processMap(map);
	}

	public void clearMap() {
		render.clearMap();
		box.mobspawn.clear();
		for (Body b : map_bod) world.destroyBody(b);
		map_bod.clear();
		ground_bod.clear();
	}

	
	
	
	
	
	
	
	
	
	

	

	public nBatch.Unit newParticle(String model, float x, float y, float rot, float radius, float len, float color) {
		return render.newUnit(model,x,y,rot,radius,len,color,0); }
	
	public ParticleModel newParticleModel(String ref) {
		render.addModel(ref, new ParticleModel(12)); return render.getModel(ref, ParticleModel.class); }

	

	public nBatch.Unit newUnit(String model, float...args) {
		return render.newUnit(model,args); }
	
	public <M extends nBatch.Model> M newModel(String ref, M mod) {
		render.addModel(ref, mod); return render.getModel(ref); }


	
	public nBatch.Unit newInst(String model, float...args) {
		return render.newInst(model,args); }
	
	public <M extends nBatch.Model> M newInstModel(String ref, M mod) {
		render.addInstModel(ref, mod); return render.getInstModel(ref); }
	
	
	
	
	
	
	
	
	
	
	
	
	
	public pBox2d box;
	public nRender render;
	public World world;
	
	private static final int rayCapacity = 128;
	
	public nEngine(pBox2d _box) {
		box = _box;
		render = box.nrend;
		world = box.world;
		rayList = new Array<Ray>(false, rayCapacity);
		freeRay = new Array<Ray>(false, rayCapacity);

		newParticleModel("paura").useGroup(render.AURA,render.LIGHT);
		newParticleModel("pcolor").useGroup(render.COLOR,render.LIGHT);
		newParticleModel("plight").useGroup(render.LIGHT);
		newParticleModel("psolid").useGroup(render.SOLID);

//		newModel("spot", new LightModel(64,0.25f))
//			.useGroup(render.COLOR,render.LIGHT);
		
		newModel("point_light", new LightModel(128,1f)).useGroup(render.LIGHT);
		newModel("point_color", new LightModel(128,1f)).useGroup(render.COLOR);
		newModel("spot_light", new LightModel(64)).useGroup(render.LIGHT);
		newModel("spot_color", new LightModel(64)).useGroup(render.COLOR);

//		newInstModel("halo", new HaloModel()).useGroup(render.HALO);
		
	}

	public void dispose() {
		empty();
		for (EngineModel p : Utl.duplic(enginemodels)) p.dispose(); 
	}
	public void empty() {
		for (EngineModel p : enginemodels) p.empty(); 
	}
	public void restart() {
		pcnt = pper;
		
		
//		newInst("halo", -800, 800, 100, Color.YELLOW.toFloatBits());
		
		
//		newUnit("spot",100f,200f,0f,3000f,Color.WHITE.toFloatBits());
		if (objectLayer != null) loadLayerObject(objectLayer);
	}
	private int pcnt = 0, pper = 200;
	public void frame() {
		for (EngineModel p : enginemodels) p.frame(); 
		if (pcnt >= pper) {
			pcnt = 0; 
			newParticle("paura", 0, 800, 0, 40, 100, Color.RED.toFloatBits());
			newParticle("pcolor", 0, 400, -0.5f, 40, 100, Color.GREEN.toFloatBits());
			newParticle("plight", 400, 400, 1, 40, 100, Color.WHITE.toFloatBits());
			newParticle("psolid", 400, 800, 0.5f, 40, 100, Color.YELLOW.toFloatBits());
		}
	}
	public void tick() {
		for (TickedModel p : tickmodels) p.tick(); 
		pcnt++;
	}
	


	class HaloModel extends EngineModel {
		
		public HaloModel() {
			super(1, 0, 4); 
			
		}
		public void frame() { }
		@Override public void update(Unit u) {
			u.setTransform(u.a(0),u.a(1));
		}
		private float rad,col;
		@Override public void make(Unit u) {
			super.make(u);
			u.setTransform(u.a(0),u.a(1));
			rad = u.a(2); col = u.a(3); 
			u.pushVert(0,0,rad,col);
		}
	}
	
	
	
	
	
	
	
	

	class ShapeModel extends EngineModel {
		
		public ShapeModel(int trignb) {
			super(3*trignb, 3*trignb, 3); 
		}
		@Override public void dispose() { 
			super.dispose();
		}
		@Override public void frame() {
			for (Unit u : units) {
				
			}
		}
		@Override public void update(Unit u) {
			
		}
		@Override public void make(Unit u) {
			super.make(u);
		}
		
	}	
	
	
	
	
	
	
	
	
	

	class LightModel extends EngineModel {
		private final int rayNb;
		private final boolean iscone;
		float ang, strt_ang, open; 
		private final Ray ray;
		public LightModel(int _rayNb) {
			super(_rayNb + 2, _rayNb * 3, 6); 
			rayNb = _rayNb; iscone = true;
			ray = newRay(rayNb+1,false);
		}
		public LightModel(int _rayNb, float _open) {
			super(_rayNb + 2, _rayNb * 3, 5); 
			rayNb = _rayNb; open = _open; iscone = false;
			strt_ang = (open * Utl.DPI) / 2f;
			ang = -(open * Utl.DPI) / rayNb;
			ray = newRay(rayNb+1,false);
		}
		@Override public void dispose() { 
			super.dispose();
			ray.clear();
		}
		private final Vector2 tvec = new Vector2(), pvec = new Vector2();
		private int p1,pp,p;
		private float px,py,rot,con,rad,col;
		@Override public void frame() {}
		@Override public void update(Unit u) {
			px = u.a(0); py = u.a(1); rot = u.a(2); 
			if (iscone) {
				con = u.a(3); rad = u.a(4); col = u.a(5); tvec.set(0,rad);
				strt_ang = (con * Utl.DPI);
				ang = -(con * Utl.DPI * 2f) / rayNb; } 
			else { rad = u.a(3); col = u.a(4); tvec.set(0,rad); }
			tvec.rotateRad(rot);
			tvec.rotateRad(strt_ang);
			ray.beginPush();
			ray.pushRay(px,py,px+tvec.x,py+tvec.y);
			for (int i = 0 ; i < rayNb ; i++) {
				tvec.rotateRad(ang);
				ray.pushRay(px,py,px+tvec.x,py+tvec.y); }
			ray.cast();
			u.setTransform(u.a(0),u.a(1),u.a(2));
			tvec.set(0,rad); 
			tvec.rotateRad(strt_ang);
			u.beginPush(); 
			p1 = u.pushVert(0,0,col,1f);
			if (ray.hasHit(0)) {
				pvec.set(tvec).scl(ray.fract(0));
				pp = u.pushVert(pvec.x,pvec.y,col,1f-ray.fract(0));
			} else pp = u.pushVert(tvec.x,tvec.y,col,0f);
			for (int i = 0 ; i < rayNb ; i++) {
				tvec.rotateRad(ang);
				if (ray.hasHit(i+1)) {
					pvec.set(tvec).scl(ray.fract(i+1));
					p = u.pushVert(pvec.x,pvec.y,col,1f-ray.fract(i+1));
				} else p = u.pushVert(tvec.x,tvec.y,col,0f);
				u.pushTrig(p1,pp,p); pp = p; }
		}
		@Override public void make(Unit u) {
			super.make(u);
//			u.setTransform(u.a(0),u.a(1),u.a(2));
		}
		
	}
	

	public Ray newRay(int raynb, boolean normal) {
		if (freeRay.size > 0) 
			return freeRay.removeIndex(freeRay.size - 1).init(raynb, normal);
		else return new Ray().init(raynb, normal); 
	}
	
	class Ray {
		// collide if index are equals
		public Ray setContactFilter(short groupIndex) {
			if (filter == null) filter = new Filter(); 
			filter.groupIndex = groupIndex; return this; }
		
		public void beginPush() { r_cnt = 0; }
		public int pushRay(float startx, float starty, float endx, float endy) {
			tmpi = r_cnt; 
			startX[r_cnt] = startx; startY[r_cnt] = starty;
			endX[r_cnt] = endx; endY[r_cnt] = endy;
			r_cnt++; return tmpi; }
		
		public boolean hasHit(int i) { return fract[i] < 1.0f; }
		public float fract(int i) { return fract[i]; }
		public Vector2 hit(int i) { return new Vector2(hitX[i],hitY[i]); }
		public Vector2 hit(int i, Vector2 v) { 
			if (v != null) return v.set(hitX[i],hitY[i]);
			else return new Vector2(hitX[i],hitY[i]); }
		public Vector2 norm(int i) { return new Vector2(normX[i],normY[i]); }
		public Vector2 norm(int i, Vector2 v) { 
			if (v != null) return v.set(normX[i],normY[i]);
			else return new Vector2(normX[i],normY[i]); }
		
		private int tmpi;
		private int r_cnt = 0;

		private Filter filter = null;
		private int rayNb = 0;
		private float startX[], startY[];
		private float endX[], endY[];
		private float hitX[], hitY[];
		private float normX[], normY[];
		private float fract[];
		private boolean storeNormal = false;
		Ray init(int ray, boolean _storeNormal) {
			rayList.add(this);
			storeNormal = _storeNormal;
			r_cnt = 0;
			if (rayNb < ray) {
				rayNb = ray;
				startX = new float[ray]; startY = new float[ray];
				endX = new float[ray]; endY = new float[ray];
				hitX = new float[ray]; hitY = new float[ray];
				fract = new float[ray];
				if (storeNormal && (normX == null || normX.length < ray)) {
					normX = new float[ray]; normY = new float[ray]; }
			}
			return this;
		}
		void clear() {
			rayList.removeValue(this, true);
			freeRay.add(this);
		}
		void cast() {
			for (int i = 0 ; i < r_cnt ; i++) {
				start.set(startX[i], startY[i]);
				end.set(endX[i], endY[i]);
				globalFilter = filter; fraction = 1.0f; 
				world.rayCast(ray,start,end);
				fract[i] = fraction;
				if (fraction < 1.0f) {
					hitX[i] = end.x; hitY[i] = end.y;
					if (storeNormal) { normX[i] = normal.x; normY[i] = normal.y; } }
				else { hitX[i] = endX[i]; hitY[i] = endY[i]; }
			}
		}
	}

	private final Array<Ray> rayList;
	private final Array<Ray> freeRay;

	private boolean globalContactFilter(Fixture fixtureB) { //fixture.setFilterData(Filter);
		return globalFilter.groupIndex == fixtureB.getFilterData().groupIndex; }
	
	private Filter globalFilter = null;
	private final Vector2 start = new Vector2();
	private final Vector2 end = new Vector2();
	private final Vector2 normal = new Vector2();
	private float fraction = 0;
	
	private final RayCastCallback ray = new RayCastCallback() {
		@Override
		final public float reportRayFixture(Fixture fixture, Vector2 point,
				Vector2 _normal, float _fraction) {
			if ((globalFilter != null) && !globalContactFilter(fixture)) return -1;
			fraction = _fraction;
			end.set(point); normal.set(_normal);
			return _fraction;
		}
	};
	
	
	
	
	
	
	
	
	

	ArrayList<EngineModel> enginemodels = new ArrayList<EngineModel>();
	abstract class EngineModel extends nBatch.Model {
		ArrayList<nBatch.Unit> units = new ArrayList<nBatch.Unit>();
		public EngineModel(int vertNb, int indNb, int argNb) {
			super(vertNb, indNb, argNb); enginemodels.add(this); }
		@Override public void dispose() { 
			enginemodels.remove(this); super.dispose(); }
		public void empty() { for (Unit u : units) u.clear(); }
		@Override public void make(Unit u) {
			u.beginPush(); if (!units.contains(u)) units.add(u); }
		@Override public void destroy(Unit u) {
			u.beginPush(); if (units.contains(u)) units.remove(u); }
		public abstract void frame();
	}

	ArrayList<TickedModel> tickmodels = new ArrayList<TickedModel>();
	abstract class TickedModel extends EngineModel {
		public TickedModel(int vertNb, int indNb, int argNb) {
			super(vertNb, indNb, argNb); tickmodels.add(this); }
		@Override public void dispose() { tickmodels.remove(this); super.dispose(); }
		public abstract void tick();
	}
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	class ParticleModel extends TickedModel {
		public ParticleModel speed(float s) { speed = s; return this; }
		public ParticleModel life(int s) { life = s; return this; }
		private final int segmentNb;
		int life = 1000; float speed = 1, ang;
		public ParticleModel(int _segmentNb) {
			super(_segmentNb + 4, (_segmentNb + 4) * 3, 7); 
			segmentNb = _segmentNb;
			ang = -Utl.DPI / segmentNb;
		}
		public void frame() { }
		@Override public void tick() {
			for (Unit u : units) {
				u.a(6, u.a(6) + 1);
				u.setTransform(u.a(0)+u.rX(speed,0)*u.a(6), 
						u.a(1)+u.rY(speed,0)*u.a(6), u.a(2)); } }
		@Override public void update(Unit u) {
			if (u.a(6) > life) { u.clear(); return; } }
		private final Vector2 tvec = new Vector2();
		private int p1,p2,pp,p; 
		private float rad,len,col;
		@Override public void make(Unit u) {
			super.make(u);
			u.setTransform(u.a(0),u.a(1),u.a(2));
			rad = u.a(3); len = u.a(4); col = u.a(5); tvec.set(0,rad);
			p1 = u.pushVert(0,0,col,1f);
			pp = u.pushVert(tvec.x,tvec.y,col,0f);
			for (int i = 0 ; i < segmentNb / 2 ; i++) {
				tvec.rotateRad(ang);
				p = u.pushVert(tvec.x,tvec.y,col,0f);
				u.pushTrig(p1,pp,p); pp = p; }
			tvec.set(0,rad); 
			p2 = u.pushVert(-len,0,col,1f);
			pp = u.pushVert(-len+tvec.x,tvec.y,col,0f);
			for (int i = 0 ; i < segmentNb / 2 ; i++) {
				tvec.rotateRad(-ang);
				p = u.pushVert(-len+tvec.x,tvec.y,col,0f);
				u.pushTrig(p2,pp,p); pp = p; }
			u.pushTrig(p2,p1,p1+1); u.pushTrig(p2,p1,p2-1);
			u.pushTrig(p2+1,p2,p1+1); u.pushTrig(p2,pp,p2-1);
		}
	}
	
	
	
	
}
