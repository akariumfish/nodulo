package box2d;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import aa_nodulo.PlaneApplet;
import aa_nodulo.pBody;
import aa_nodulo.pFamily;
import aa_nodulo.pGeom;
import aa_nodulo.pParam;
import aa_nodulo.pProperty;
import aa_nodulo.pSpace;
import aa_nodulo.pSystem;
import aa_nodulo.pView;
import data.*;
import gui.*;
import util.Utl;
import util.nMap;
import util.nPool;
import util.nRun;

public class pBox2d extends pSystem {

	public static sBloc_Builder builder = null;

	public static void build(sData data) {

		if (builder == null) build_prop();

		builder = builder(data, "box2d", pBox2d.class, new nRun() { public void run(Object o) {
			sValueBloc b = (sValueBloc)o; newObject(b); }});

	}

	public static void dispose(PlaneApplet app) { pool.dispose(); }
	public static final nPool<pBox2d> pool = new nPool<pBox2d>() {
		protected pBox2d newObject() { return new pBox2d(); } };
		public static pBox2d newObject(sValueBloc b) {
			return pool.obtain().init(b); }
		


		public static void build_prop() {

			float RS = nGUI.book.RS;

			pProperty physic = pProperty.newGeneralProperty("physic");

			physic.addBodyInitRun(new nRun() {public void run() {
				pBody bod = arg(0,pBody.class);
				pBox2d b2d = PlaneApplet.app.getSystem(pBox2d.class);
				if (b2d == null || bod == null) return;
				b2d.init_body(bod);
			}});

			physic.addBodyClearRun(new nRun() {public void run() {
				pBody bod = arg(0,pBody.class);
				pBox2d box = bod.space.app.getSystem(pBox2d.class);
				if (box == null || bod == null) return;
				box.clear_body(bod);
			}});

			physic
			.addData("density", 0.000001f)
			.addData("friction", 0.0f)
			.addData("restitution", 0.0f)
			.addData("dynamic", false)
			.addData("kinematic", false)
			.addData("copy_geom", true)
			.addData("aura", false)
			.addData("view_light", false)
			.addData("contact_break", false)
			.addData("sensor", false)
			.addData("light", false)
			.addData("light_pos", new Vector2())
			.addData("light_dist", 300f)
			.addData("r", (int)255)
			.addData("g", (int)50)
			.addData("b", (int)50)
			.addData("a", (int)255)
			;

			physic.newLocalProperty("box_body")
			.addData("pos", new Vector2())
			.addData("rot", 0f)
			.addData("body_ref", "")
			;
			pFamily.newFamily("box_body")
			.addProp("box_body")
			;


			
			
			
			

			nRun run_ctrl_box = new nRun() { public void run(Object o) { 
				pBody bod = (pBody)o; if (bod == null) return;
				pBox2d box = PlaneApplet.app.getSystem(pBox2d.class);
				if (box != null && bod.hasParam("box_body") && 
						bod.hasParam("ctrl_box")) {
					float maccel = bod.getFlt("ctrl_box","move_strength");
					float raccel = bod.getFlt("ctrl_box","rot_strength");
					float max_speed = bod.getFlt("ctrl_box","max_speed");
					float max_rot = bod.getFlt("ctrl_box","max_rot");
					boolean glob = bod.getBoo("ctrl_box","global_ref");
					if (bod.getBoo("ctrl_box","accel_move")) {
						Vector2 dr = bod.getVec("ctrl_box","accel_dir");
						box.accel_body(bod,glob,dr.x*maccel,dr.y*maccel,max_speed); }
					if (bod.getBoo("ctrl_box","accel_up")) {
						box.accel_body(bod,glob,0,maccel,max_speed); }
					if (bod.getBoo("ctrl_box","accel_down")) {
						box.accel_body(bod,glob,0,-maccel,max_speed); }
					if (bod.getBoo("ctrl_box","accel_left")) {
						box.accel_body(bod,glob,-maccel,0,max_speed); }
					if (bod.getBoo("ctrl_box","accel_right")) {
						box.accel_body(bod,glob,maccel,0,max_speed); }
					if (bod.getBoo("ctrl_box","accel_cw")) {
						box.rot_body(bod,-raccel,max_rot); }
					if (bod.getBoo("ctrl_box","accel_ccw")) {
						box.rot_body(bod,raccel,max_rot); }
					if (bod.getBoo("ctrl_box","decel_move")) {
						box.decel_body_move(bod, maccel); }
					if (bod.getBoo("ctrl_box","decel_rot")) {
						box.decel_body_rot(bod, raccel); }
					if (bod.getBoo("ctrl_box","rot_to_target")) {
						float rot_target = bod.getFlt("ctrl_box","rot_target");
						box.rot_body_toward(bod,rot_target,raccel*3f,max_rot*3f); }
				}
			}};

			pGeom.newControlProp("box",physic,run_ctrl_box) 
			.setFullSync()
			.addData("global_ref", true)
			.addData("accel_move", false)
			.addData("accel_dir", new Vector2())
			.addData("accel_up", false)
			.addData("accel_down", false)
			.addData("accel_left", false)
			.addData("accel_right", false)
			.addData("accel_cw", false)
			.addData("accel_ccw", false)
			.addData("decel_move", false)
			.addData("decel_rot", false)
			.addData("rot_to_target", false)
			.addData("rot_target", 0f)
			.addData("move_strength", 4f)
			.addData("rot_strength", 1f/50f)
			.addData("max_speed", 90f)
			.addData("max_rot", 0.3f)
			;



		}
		
		
		
		
		

		ArrayList<nDrawable> drawRun = new ArrayList<nDrawable>();
		HashMap<nDrawable, Integer> drawPrio = new HashMap<nDrawable, Integer>();
		int max_prio = 0;
		
		public pBox2d addDrawable(nDrawable r) { addDrawable(0,r); return this; }
		public pBox2d addDrawable(int prio, nDrawable r) { 
			drawRun.add(r); drawPrio.put(r, prio); max_prio = Math.max(max_prio, prio); return this; }
		public pBox2d removeDrawable(nDrawable r) { drawRun.remove(r); return this; }
		public pBox2d clearDrawable() { drawRun.clear(); return this; }

		
		
		Vector2 avatarSpawn = new Vector2();
		public void setAvatarSpawn(Vector2 p) { avatarSpawn.set(p); }
		
		private class MobSpawn { 
			Vector2 p1,p2; 
			MobSpawn(Vector2 v1, Vector2 v2) { 
				p1 = new Vector2(v1); p2 = new Vector2(v2); } }
		int mobCnt = 0;
		ArrayList<MobSpawn> mobspawn = new ArrayList<MobSpawn>();
		public void addMobSpawn(Vector2 p1, Vector2 p2) { mobspawn.add(new MobSpawn(p1,p2)); }

		
		
		

		public pBox2d() { 
			super(); 
			tick_run = new nRun() { public void run(Object o) { tick((float)o); }};
			net_tick_run = new nRun() { public void run(Object o) { net_tick((float)o); }};
			draw_run = new nDrawable() { public void drawing() { draw(); }}; 
		}

		nRun tick_run, net_tick_run;
		nDrawable draw_run;
		
		public pBox2d init(sValueBloc b) { return (pBox2d) super.init(b); }
		
		public pSpace space;

		public sBoo val_draw_debug, val_draw_ray_debug, 
			val_draw_vision, val_draw_tile, val_draw_solid, 
			val_draw_light, val_draw_color, val_draw_aura, 
			val_draw_ground, val_draw_fog, 
			val_do_calc, val_edit_tile;
		
		public sInt val_body_nb, val_light_nb;
		
		public World world;
		public Box2DRenderer boxRenderer;

		nRenderer renderer;

		pView view;
		
		public void system_init() {
			bloc.addObject("box2d", this);

			app.storeSystemType(bloc.ref, this.getClass());

			useNetFrame();

			val_draw_debug = bloc.obtainBoo("val_draw_debug", false);
			val_draw_ray_debug = bloc.obtainBoo("val_draw_ray_debug", false);
			val_draw_vision = bloc.obtainBoo("val_draw_vision", true);
			val_draw_tile = bloc.obtainBoo("val_draw_tile", true);
			val_draw_solid = bloc.obtainBoo("val_draw_solid", true);
			val_draw_light = bloc.obtainBoo("val_draw_light", true);
			val_draw_aura = bloc.obtainBoo("val_draw_aura", true);
			val_draw_color = bloc.obtainBoo("val_draw_color", true);
			val_draw_ground = bloc.obtainBoo("val_draw_ground", app.config.DRAW_GROUND);
			val_draw_fog = bloc.obtainBoo("val_draw_fog", app.config.DRAW_FOG);
			val_do_calc = bloc.obtainBoo("val_do_calc", true);
			val_edit_tile = bloc.obtainBoo("val_edit_tile", false);
			val_body_nb = bloc.obtainInt("val_body_nb", 0);
			val_light_nb = bloc.obtainInt("val_light_nb", 0);
			

			app.outputs.put("reset_mob_spawn", new nRun() { public void run() {
				mobCnt = 0; }});
			app.outputs.put("next_mob_spawn", new nRun() { public void run() {
				mobCnt++; }});
			
			app.inputs.put("get_mob_spawn_p1", new nRun() { public Object get() {
				return mobspawn.get(mobCnt).p1; }});
			app.inputs.put("get_mob_spawn_p2", new nRun() { public Object get() {
				return mobspawn.get(mobCnt).p2; }});
			
			app.inputs.put("has_mob_spawn", new nRun() { public Object get() {
				return mobCnt < mobspawn.size(); }});
			
			app.inputs.put("avatar_spawn", new nRun() { public Object get() {
				return avatarSpawn; }});
			
			
			world = new World(new Vector2(0, 0), true);

			pBox2d box = this;
			world.setContactListener(new ContactListener() {
				@Override public void endContact(Contact contact) {
					box.endContact(contact); }
				@Override public void beginContact(Contact contact) {
					box.beginContact(contact); }
				@Override public void preSolve(Contact contact, Manifold oldManifold) { }
				@Override public void postSolve(Contact contact, ContactImpulse impulse) { }
			});

			//		boolean drawBodies, boolean drawJoints, 
			//		boolean drawAABBs, boolean drawInactiveBodies, 
			//		boolean drawVelocities, boolean drawContacts
			boxRenderer = new Box2DRenderer(app, true, true, true, true, true, true);
			
			renderer = new nRenderer("Map.tmx", this, world);
			
		}
		public void system_load() {


			space = app.space;
			view = app.view;
			
			app.time.addEventTick(tick_run);
			app.time.addEventNetTick(net_tick_run);
			
			view.addDrawable(10,draw_run);
			
			//		if (!app.RELEASE) 
			tool_setup(true);
			

//			app.term.register("sim", bloc, this);
//			
//			app.term.addExecutor(new CommandExecutor("sim", bloc, this) {
//				
//			});

		}
		public void system_clear() {
			app.time.removeEventTick(tick_run);
			app.time.removeEventNetTick(net_tick_run);
			app.view.removeDrawable(draw_run);

			renderer.dispose();
		}

		public void tool_init(nInterface interf) {

			interf.setContext(bloc);
			interf.add_row();
			interf.add_row_switch_boo(4, "debug", "val_draw_debug");
			interf.add_row_label(1, "");
			interf.add_row_switch_boo(4, "dbg_ray", "val_draw_ray_debug");
//			interf.add_row();
//			interf.add_row_label(10, "");
			interf.add_row();
			interf.add_row_switch_boo(4, "simu", "val_do_calc");
			interf.add_row_label(2, "");
			interf.add_row_switch_boo(4, "solid", "val_draw_solid");
//			interf.add_row();
//			interf.add_row_label(10, "");
			interf.add_row();
			interf.add_row_switch_boo(4, "ground", "val_draw_ground");
			interf.add_row_label(2, "");
			interf.add_row_switch_boo(4, "fog", "val_draw_fog");
			interf.add_row();
			interf.add_row_switch_boo(4, "vision", "val_draw_vision");
			interf.add_row_label(2, "");
			interf.add_row_switch_boo(4, "light", "val_draw_light");
			interf.add_row();
			interf.add_row_switch_boo(4, "aura", "val_draw_aura");
			interf.add_row_label(2, "");
			interf.add_row_switch_boo(4, "color", "val_draw_color");
			interf.add_row();
			interf.add_row_switch_boo(4, "tile", "val_draw_tile");
			interf.add_row_label(2, "");
			interf.add_row_switch_boo(4, "edit", "val_edit_tile");

			interf.add_row();
			interf.add_row_watch(5, "Body : ", "val_body_nb");
			interf.add_row_watch(5, "Light : ", "val_light_nb");

		}

		public void frame(float delta) { 
			val_body_nb.set(bodys.size());
			int c = 0;
			for (LightLayer l : renderer.rayHandler.layerList) {
				c += l.lightList.size;
			}
			val_light_nb.set(c);
		}

		private float accumulator = 0;
		private float TIME_STEP = 1/60f;
		private int VELOCITY_ITERATIONS = 6;
		private int POSITION_ITERATIONS = 2;
		private void doPhysicsStep(float deltaTime) {
			// fixed time step
			// max frame time to avoid spiral of death (on slow devices)
			float frameTime = Math.min(deltaTime, 0.25f);
			accumulator += frameTime;
			while (accumulator >= TIME_STEP) {
				world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
				accumulator -= TIME_STEP;
			}
		}

		public void tick(float delta) {
			if (val_do_calc.get()) {
				doPhysicsStep(delta);
				for (pBody b : Utl.duplic(space.familyMember("box_body"))) 
					update_body(b);
			}

		}

		public void net_frame(float delta) { 
			frame(delta);
		}
		public void net_tick(float delta) { 

		}

		public boolean drawground() { return val_draw_ground.get(); }
		public boolean drawfog() { return val_draw_fog.get(); }
		public boolean drawsolid() { return val_draw_solid.get(); }
		public boolean drawlight() { 
			return val_draw_light.get() && app.gdx.drawer.USE_FX; }
		public boolean drawaura() { 
			return val_draw_aura.get() && app.gdx.drawer.USE_FX; }
		public boolean drawcolor() { 
			return val_draw_color.get() && app.gdx.drawer.USE_FX; }
		public boolean drawvision() { return val_draw_vision.get(); }
		
		public void draw_drawer() {

			view.app.gdx.drawer.begin();
			
			ArrayList<nDrawable> alldraw = Utl.duplic(drawRun);
			
			for (int prio = 0 ; prio <= max_prio ; prio++)
				for (nDrawable d : drawRun) 
					if (drawPrio.get(d) == prio) { d.drawing(); alldraw.remove(d); } 
			
			for (nDrawable d : alldraw) d.drawing(); 

			view.app.gdx.drawer.end();
			
		}

		public void draw() { 
			
			if (val_edit_tile.get()) {
				if (app.input.mouseLeft.trigClick && 
						app.view.mouse_is_hover_view()) {
					renderer.tileLayer.addCell(app.view.mouse_in_view()); }
				if (app.input.mouseRight.trigClick && 
						app.view.mouse_is_hover_view()) {
					renderer.tileLayer.delCell(app.view.mouse_in_view()); }
			}
			
			renderer.render(); 

			if (val_draw_debug.get()) {
				boxRenderer.render(world);
			}
			if (val_draw_ray_debug.get()) {
				boxRenderer.render(renderer.rayHandler);
			}
		}

		public void endContact(Contact contact) {
			
		}
		public void beginContact(Contact contact) {
			Fixture fa = contact.getFixtureA();
			Fixture fb = contact.getFixtureB();
			Body ba = fa.getBody();
			Body bb = fb.getBody();
			
			if (break_bodys.contains(ba) && body_breaker.contains(bb)) {
				if (!clearing_bodys.contains(ba)) clearing_bodys.add(ba);
			}
			if (break_bodys.contains(bb) && body_breaker.contains(ba)) {
				if (!clearing_bodys.contains(bb)) clearing_bodys.add(bb);
			}
			if (ba.getUserData() != null && 
					(ba.getUserData() instanceof pBody) && 
					bb.getUserData() != null && 
					(bb.getUserData() instanceof pBody)) {
				pBody b1 = (pBody)ba.getUserData();
				pBody b2 = (pBody)bb.getUserData();

				if (!b1.hasParam("hp") || !b2.hasParam("hitzone")) {
					pBody t = b1; b1 = b2; b2 = t; 
					Body bt = ba; ba = bb; bb = bt; 
					Fixture ft = fa; fa = fb; fb = ft; }
				if (!b1.hasParam("hp") || !b2.hasParam("hitzone")) return;
				
				int damage = b2.getInt("hitzone", "damage");
				b1.setInt("hp", "hp", b1.getInt("hp", "hp") - damage);
				
				if (b1.getInt("hp", "hp") <= 0) {
					if (!clearing_bodys.contains(ba)) clearing_bodys.add(ba);
					if (b1.getBoo("hitpoint","avatar") && 
							app.getSystem(pGeom.class) != null) 
						app.getSystem(pGeom.class).game_over();
				}
				if (!clearing_bodys.contains(bb)) clearing_bodys.add(bb);
			}
		}
		
		public ArrayList<Body> break_bodys = new ArrayList<Body>();
		public ArrayList<Body> body_breaker = new ArrayList<Body>();

		public ArrayList<Body> clearing_bodys = new ArrayList<Body>();
		
		public nMap<Body> bodys = new nMap<Body>();
		private int get_free_bod_nb() {
			int n = 0; while (bodys.get(""+n) != null) n++; return n; }

		public nMap<Joint> joints = new nMap<Joint>();
		//		private int joint_nb = 0;
		
		public HashMap<Body,ArrayList<RayHandler.AbstractLight>> lights = 
				new HashMap<Body,ArrayList<RayHandler.AbstractLight>>();
		public void attachToBody(RayHandler.AbstractLight l , Body b) { attachToBody(l,b,0f,0f); }
		public void attachToBody(RayHandler.AbstractLight l , Body b, float x, float y) {
			if (lights.get(b) == null) lights.put(b, new ArrayList<RayHandler.AbstractLight>());
			lights.get(b).add(l);
			l.setIgnoreAttachedBody(true);
			l.attachToBody(b, x, y);
		}
		public void attachToBody(RayHandler.AbstractLight l , Body b, float x, float y, float d) {
			if (lights.get(b) == null) lights.put(b, new ArrayList<RayHandler.AbstractLight>());
			lights.get(b).add(l);
			l.setIgnoreAttachedBody(true);
			l.attachToBody(b, x, y, d);
		}
		
		public void init_body(pBody b) {
			app.addEventNextFrame(new nRun(b) {public void run() {
				pBody b = (pBody)builder;
				if (b.param("box_body") == null || b.param("physic") == null) return;

				float density = b.getFlt("physic", "density");
				float friction = b.getFlt("physic", "friction");
				float restitution = b.getFlt("physic", "restitution");

				// First we create a body definition
				BodyDef bodyDef = new BodyDef();
				// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
				if (b.getBoo("physic", "dynamic"))
					bodyDef.type = BodyType.DynamicBody;
				else if (b.getBoo("physic", "kinematic"))
					bodyDef.type = BodyType.KinematicBody;
				// Set our body's starting position in the world
				if (b.hasParam("ref"))
					bodyDef.position.set(b.getVec("ref", "pos"));
				else bodyDef.position.set(b.getVec("box_body", "pos"));
				

				// Create our body in the world using our body definition
				Body body = world.createBody(bodyDef);
				
				body.setUserData(b);
				
				renderer.lightLayer.transparent.add(body);
				renderer.visionLayer.transparent.add(body);
				
				if (b.getBoo("physic", "contact_break")) {
					break_bodys.add(body);
				} else {
					body_breaker.add(body);
				}

				if (b.getBoo("physic", "aura")) {
//					PointLight pl = renderer.newAuraLight(
//							new Color(1f,0.6f,0.4f,1f), 400, 0, 0);
//					attachToBody(pl, body);
					SwarmLight.Unit su = renderer.auraLayer.newSwarmLightUnit(
							new Color(1f,0.6f,0.4f,1f), 400);
					attachToBody(su, body);
				}

				if (b.getBoo("physic", "view_light")) {
					
					renderer.newVisionLight(body);
					
					
					
//					renderer.g.attachToBody(body, 0f,0f,0f);
					
					
					
					attachToBody(renderer.colorLayer.newRectLight(
							10, new Color(1f,0f,0f,1f), 0f, 0f, 200f, 40f), 
							body, 80f, -20f, 0f);
					
//					RectLight rl = renderer.auraLayer.newRectLight(
//							30, new Color(1f,0.5f,0.5f,1f), 0f, 0f, 100f, 100f);
//					rl.setSoftnessLength(50f);
//					attachToBody(rl, body, -40f, 50f, 180f);
					

				}

				if (b.getBoo("physic", "light")) {
//					Vector2 pos = b.getVec("physic", "light_pos");
					float dist = b.getFlt("physic", "light_dist");
					Color col = Utl.color(b.getInt("physic", "r"), b.getInt("physic", "g"), 
							b.getInt("physic", "b"), b.getInt("physic", "a"));
					
					renderer.rayHandler.transparent.add(body);
					
					SwarmLight.Unit su = renderer.auraLayer.newSwarmLightUnit(
							new Color(col.r,col.g,col.b,col.a), dist);
					attachToBody(su, body);
				}

				if (!b.getBoo("physic", "copy_geom") || !b.hasParam("geom")) {
					PolygonShape polygonshape = new PolygonShape();
					polygonshape.setAsBox(100,100);
					FixtureDef fixtureDef2 = new FixtureDef();
					fixtureDef2.shape = polygonshape;
					fixtureDef2.density = density;
					fixtureDef2.friction = friction;
					fixtureDef2.restitution = restitution;
					if (b.getBoo("physic", "sensor")) fixtureDef2.isSensor = true;
					Fixture fixture = body.createFixture(fixtureDef2);
//					fixture.setUserData(new LightData(10f));
				} else if (b.hasParam("geom")) {
					for (pParam p : b.params.all()) if (p.prop.ref.equals("geom")) {
						pParam geom = p;
						ArrayList<Vector2> point = geom.getCollecData("point", Vector2.class);
						ArrayList<Integer> color = geom.getCollecData("color", Integer.class);
						ArrayList<Integer> faceA = geom.getCollecData("faceA", Integer.class);
						ArrayList<Integer> faceB = geom.getCollecData("faceB", Integer.class);
						ArrayList<Integer> faceC = geom.getCollecData("faceC", Integer.class);
						if (faceA.size() != faceB.size() || faceA.size() != faceC.size() || 
								color.size() != point.size()) return;

						SolidLight.Unit su = renderer.solidLayer.newSolidLightUnit();
						attachToBody(su, body);
						
						for (int i = 0 ; i < faceA.size() ; i++) {
							int p1 = faceA.get(i), p2 = faceB.get(i), p3 = faceC.get(i);
							if (p1 < 0 || p1 >= point.size() || 
									p2 < 0 || p2 >= point.size() || 
									p3 < 0 || p3 >= point.size()) continue;
							Vector2[] pl = new Vector2[3];
							float s = b.getFlt("ref","scale");
							pl[0] = Utl.copy(point.get(p1)); 
							pl[1] = Utl.copy(point.get(p2)); 
							pl[2] = Utl.copy(point.get(p3));
							pl[0].scl(s); pl[1].scl(s); pl[2].scl(s);
							PolygonShape polygonshape = new PolygonShape();
							polygonshape.set(pl);
							FixtureDef fixtureDef2 = new FixtureDef();
							fixtureDef2.shape = polygonshape;
							fixtureDef2.density = density;
							fixtureDef2.friction = friction;
							fixtureDef2.restitution = restitution;
							if (b.getBoo("physic", "sensor")) fixtureDef2.isSensor = true;
							Fixture fixture = body.createFixture(fixtureDef2);
//							fixture.setUserData(new LightData(1f));

							s = 1.1f;
							pl[0].scl(s); pl[1].scl(s); pl[2].scl(s);
							su.trig(pl[0].x, pl[0].y, pl[1].x, pl[1].y, pl[2].x, pl[2].y, 
									new Color(0.1f,0.1f,0.1f,0.3f));
							s = 1f / 1.1f;
							pl[0].scl(s); pl[1].scl(s); pl[2].scl(s);
							su.trig(pl[0].x, pl[0].y, pl[1].x, pl[1].y, pl[2].x, pl[2].y, 
									Utl.intToColor(color.get(p1)));
						}
					}
				}

				int bod_nb = get_free_bod_nb();
				bodys.put(""+bod_nb, body);
				b.setStr("box_body", "body_ref", ""+bod_nb);
			}});
		}

		public void clear_body(pBody b) {
			if (b.param("box_body") == null) return;
			Body body = bodys.get(b.getStr("box_body", "body_ref"));
			if (body == null) return;
			bodys.remove(b.getStr("box_body", "body_ref"), body);
			world.destroyBody(body);
			if (lights.get(body) != null) {
				for (RayHandler.AbstractLight l : lights.get(body)) l.remove();
				lights.get(body).clear();
			}
			renderer.rayHandler.transparent.remove(body);
			renderer.visionLayer.transparent.remove(body);
			renderer.lightLayer.transparent.remove(body);
			renderer.auraLayer.transparent.remove(body);
			break_bodys.remove(body);
			clearing_bodys.remove(body);
		}

		public void move_body(pBody b, float x, float y) {
			if (b.param("box_body") == null) return;
			Body body = bodys.get(b.getStr("box_body", "body_ref"));
			if (body == null) return;
//			Vector2 pos = body.getPosition();
			float rot = body.getAngle();
			body.setTransform(x,y,rot);
		}

		public void accel_body(pBody b, boolean glob, float x, float y, float max) {
			if (b.param("box_body") == null) return;
			Body body = bodys.get(b.getStr("box_body", "body_ref"));
			if (body == null) return;
			Vector2 pos = body.getPosition();
			Vector2 vel = body.getLinearVelocity();
			Vector2 a = new Vector2(x,y).scl(body.getMass());
			if (!glob) a.rotateRad(body.getAngle()-(float)(Math.PI/2f));
			Vector2 futur = new Vector2(a).add(vel);
			float futl = futur.len();
			if (futl > max) { decel_body_move(b,futl-max); }
			body.applyLinearImpulse(a.x, a.y, pos.x, pos.y, true);
		}

		public void decel_body_move(pBody b, float s) {
			if (b.param("box_body") == null) return;
			Body body = bodys.get(b.getStr("box_body", "body_ref"));
			if (body == null) return;
			Vector2 pos = body.getPosition();
			Vector2 vel = body.getLinearVelocity();
			float vell = vel.len();
			if (vell > s) vel.nor().scl(s);
			vel.scl(-1f).scl(body.getMass());
			body.applyLinearImpulse(vel.x, vel.y, pos.x, pos.y, true);
		}

		public void rot_body_toward(pBody b, float targ, float r, float max) {
			if (b.param("box_body") == null) return;
			Body body = bodys.get(b.getStr("box_body", "body_ref"));
			if (body == null) return;
			float vel = body.getAngularVelocity();
			float rot = body.getAngle();
			float m = Utl.mapToCircularValuesDist(rot, targ, r, 
					-((float)Math.PI), ((float)Math.PI));
			float d = Utl.mapToCircularValuesDir(rot, targ, r, 
					-((float)Math.PI), ((float)Math.PI)); 
			float d2 = Utl.mapToCircularValuesDir(rot+vel, targ, r, 
					-((float)Math.PI), ((float)Math.PI)); 
			if ((d>0) == (d2>0) && m > 0.01f) {
				m *= d;
				rot_body(b, m, max);
			} else {
				decel_body_rot(b, r);
			}
		}

		public void rot_body(pBody b, float r, float max) {
			if (b.param("box_body") == null) return;
			Body body = bodys.get(b.getStr("box_body", "body_ref"));
			if (body == null) return;
			float vel = body.getAngularVelocity();
			if (vel > 0f && vel > max) return;
			if (vel < 0f && vel < -max) return;
			body.applyAngularImpulse(body.getInertia()*r, true);
		}

		public void decel_body_rot(pBody b, float s) {
			if (b.param("box_body") == null) return;
			Body body = bodys.get(b.getStr("box_body", "body_ref"));
			if (body == null) return;
			float vel = body.getAngularVelocity();
			if (vel > 0f && vel > s) vel = s;
			if (vel < 0f && vel < -s) vel = -s;
			body.applyAngularImpulse(body.getInertia()*-vel, true);
		}

		public void update_body(pBody b) {
			if (b.param("box_body") == null) return;
			Body body = bodys.get(b.getStr("box_body", "body_ref"));
			if (body == null) return;
			if (clearing_bodys.contains(body)) {
				clearing_bodys.remove(body);
				b.clear();
				return;
			}
			if (b.getBoo("physic", "dynamic") && b.hasParam("ref")) {
				Vector2 pos = body.getPosition();
				float rot = body.getAngle();
				b.setVec("box_body", "pos", pos);
				b.setFlt("box_body", "rot", rot);
				b.setVec("ref", "pos", pos);
				b.setFlt("ref", "rot", rot);
			}
			if (b.getBoo("physic", "kinematic") && b.hasParam("ref")) {
				Vector2 pos = b.getVec("ref", "pos");
				float rot = b.getFlt("ref", "rot");
				b.setVec("box_body", "pos", pos);
				b.setFlt("box_body", "rot", rot);
				body.setTransform(pos, rot);
			}
		}


}
