package box_render;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import box2d.Light;
import box2d.pBox2d;
import box_render.nBatch.Unit;
import util.Utl;

public class nEngine {
	
	public static void build() {
		
	}

	
	

	public nBatch.Unit newUnit(String model, float...args) {
		return render.newUnit(model,args); }
	
	
	

	public nBatch.Unit newParticle(String model, float x, float y, float rot, float radius, float len, float color) {
		return render.newUnit(model,x,y,rot,radius,len,color,0); }
	
	public ParticleModel newParticleModel(String ref) {
		render.addModel(ref, new ParticleModel(12)); return render.getModel(ref, ParticleModel.class); }
	
	
	
	
	
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
		
		newParticleModel("part");
	}

	public void dispose() {
		
	}
	public void empty() {
		for (EngineModel p : enginemodels) p.empty(); 
	}
	public void restart() {
		pcnt = pper;
	}
	private int pcnt = 0, pper = 100;
	public void frame() {
		for (EngineModel p : enginemodels) p.frame(); 
		pcnt++;
		if (pcnt >= pper) {
			pcnt = 0; 
			newParticle("part", 0, 0, 0, 50, 200, Color.RED.toFloatBits());
			newParticle("part", 0, 400, -0.5f, 50, 200, Color.GREEN.toFloatBits());
			newParticle("part", 400, 400, 1, 50, 200, Color.BLUE.toFloatBits());
			newParticle("part", 400, 0, 0.5f, 50, 200, Color.YELLOW.toFloatBits());
		}
	}
	public void tick() {
		for (TickedModel p : tickmodels) p.tick(); 
	}
	
	

	class LightModel extends EngineModel {
		private final int segmentNb;
		float ang;
		ArrayList<nBatch.Unit> units = new ArrayList<nBatch.Unit>();
		public LightModel(int _segmentNb) {
			super(_segmentNb + 4, (_segmentNb + 4) * 3, 7); 
			segmentNb = _segmentNb;
			useGroup(render.COLOR,render.LIGHT);
			ang = -Utl.DPI / segmentNb;
		}
		public void frame() {
			
		}
		@Override public void dispose() { 
			super.dispose();
		}
		@Override public void update(Unit u) {
			
		}
		@Override public void make(Unit u) {
			super.make(u);
		}
		
	}
	

	public Ray newRay(int raynb, boolean normal) {
		if (freeRay.size > 0) 
			return freeRay.removeIndex(freeRay.size - 1).init(raynb, normal);
		else return new Ray().init(raynb, normal); 
	}
	
	void cast_rays() {
		for (Ray r : rayList) r.cast();
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
			useGroup(render.AURA,render.LIGHT);
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
