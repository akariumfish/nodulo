package box2d;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Mesh.VertexDataType;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;

import aa_nodulo.PlaneApplet;
import aa_nodulo.pBody;
import aa_nodulo.pFamily;
import aa_nodulo.pGeom;
import aa_nodulo.pParam;
import aa_nodulo.pProperty;
import aa_nodulo.pSpace;
import aa_nodulo.pSystem;
import aa_nodulo.pView;
import data.sBloc_Builder;
import data.sBoo;
import data.sData;
import data.sInt;
import data.sValueBloc;
import gui.nDrawable;
import gui.nGUI;
import gui.nInterface;
import shaders.BlendFunc;
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
			.addData("density", 10f).addCtrl("density", Float.class)
			.addData("friction", 0.0f).addCtrl("friction", Float.class)
			.addData("restitution", 0.0f).addCtrl("restitution", Float.class)
			.addData("filter", (int)0).addCtrl("filter", Integer.class)
			.addData("dynamic", false).addCtrl("dynamic", Boolean.class)
			.addData("kinematic", false).addCtrl("kinematic", Boolean.class)
			.addData("copy_geom", true)
			.addData("view_light", false).addCtrl("view_light", Boolean.class)
//			.addData("contact_break", false)
//			.addData("sensor", false)
//			.addData("light", false)
//			.addData("light_pos", new Vector2())
			.addData("aura", false).addCtrl("aura", Boolean.class)
			.addData("aura_dist", 600f).addCtrl("aura_dist", Float.class)
			.addData("light_dist", 400f).addCtrl("light_dist", Float.class)
			.addData("r", 1f).addCtrl("r", Float.class)
			.addData("g", 0.5f).addCtrl("g", Float.class)
			.addData("b", 0.3f).addCtrl("b", Float.class)
			;

			physic.newLocalProperty("box_body")
			.addData("pos", new Vector2())
			.addData("rot", 0f)
			.addData("body_ref", "")
			;
			pFamily.newFamily("box_body")
			.addProp("box_body")
			;


			pProperty bullet = pProperty.newGeneralProperty("bullet");
			
			bullet.newRun("pop",new nRun() {public void run() {
				pParam bullet = contextParam();
				pBox2d box = bullet.space.app.getSystem(pBox2d.class);
				if (box == null || bullet == null) return;
				if (args.length < 2) return;
				Vector2 pos = arg(0,Vector2.class);
				float rot = arg(1,Float.class);
				pParam par = bullet.space.new_param("bullet_unit");
				box.init_bullet(par,bullet,pos,rot);
			}});

			bullet
			.addData("name", "bullet_par").addCtrl("name", String.class)
			.addData("speed", 2.75f).addCtrl("speed", Float.class)
			.addData("len", 70f).addCtrl("len", Float.class)
			.addData("damage", (int)1).addCtrl("damage", Integer.class)
			.addData("filter", (int)0).addCtrl("filter", Integer.class)
			.addData("aura", true).addCtrl("aura", Boolean.class)
			.addData("aura_dist", 90f).addCtrl("aura_dist", Float.class)
			.addData("light", true).addCtrl("light", Boolean.class)
			.addData("light_dist", 180f).addCtrl("light_dist", Float.class)
			.addData("r", (int)255).addCtrl("r", Integer.class)
			.addData("g", (int)50).addCtrl("g", Integer.class)
			.addData("b", (int)50).addCtrl("b", Integer.class)
			.addData("a", (int)255).addCtrl("a", Integer.class)
			;

			pProperty.newProperty("bullet_unit")
			.setCommon()
			.addRef("def", "bullet")
			.addData("pos", new Vector2())
			.addData("rot", 0f)
			.addData("aura_id", (int)-1)
			.addData("light_id", (int)-1)
			;


			
			
			
			pProperty shape = pProperty.newGeneralProperty("shape");
			
			shape.newRun("pop",new nRun() {public Object get() {
				pParam shape = contextParam();
				pBox2d box = shape.space.app.getSystem(pBox2d.class);
				if (box == null || bullet == null) return null;
				if (args.length < 3) return null;
				Vector2 pos = arg(0,Vector2.class);
				float rot = arg(1,Float.class);
				pParam geom = arg(2,pParam.class);
				pParam par = shape.space.new_param("shape_unit");
				box.init_shape(par,shape,geom,pos,rot);
				return par;
			}});

			shape
			.addData("name", "shape_def")
			.addData("param_ctrl", false)
			.addData("dynamic", true).addCtrl("dynamic", Boolean.class)
			.addData("kinematic", false).addCtrl("kinematic", Boolean.class)
			.addData("static", false).addCtrl("static", Boolean.class)
			.addData("density", 1f).addCtrl("density", Float.class)
			.addData("friction", 0f).addCtrl("friction", Float.class)
			.addData("restitution", 0f).addCtrl("restitution", Float.class)
			.addData("sensor", false)
			.addData("transparent", false).addCtrl("transparent", Boolean.class)
			.addData("lightTransparent", true).addCtrl("lightTransparent", Boolean.class)
			.addData("visionTransparent", true).addCtrl("visionTransparent", Boolean.class)
			.addData("break_bodys", false)
			.addData("body_breaker", false).addCtrl("body_breaker", Boolean.class)
			;
			
			pProperty.newProperty("shape_unit")
			.setCommon()
			.addRef("def", "shape")
			.addData("pos", new Vector2())
			.addData("rot", 0f)
			.addData("body_id", (int)-1)
			.newRun("get_body", new nRun() { public Object get() {
				pParam par = contextParam();
				pBox2d box = par.space.app.getSystem(pBox2d.class);
				return box.getShapeBody(par);
			}})
			;
			

			
			
			
			pProperty member = pProperty.newGeneralProperty("member");

			member.addBodyInitRun(new nRun() {public void run() {
				pBody bod = arg(0,pBody.class);
				pBox2d b2d = PlaneApplet.app.getSystem(pBox2d.class);
				if (b2d == null || bod == null) return;
				PlaneApplet.app.addDelayEvent(2,new nRun() {public void run() {
					b2d.init_member(bod);
				}});
			}});

			member.addBodyClearRun(new nRun() {public void run() {
				pBody bod = arg(0,pBody.class);
				pBox2d box = bod.space.app.getSystem(pBox2d.class);
				if (box == null || bod == null) return;
				box.clear_member(bod);
			}});

			member.addData("shape_name","shape_def").addCtrl("shape_name", String.class)
			.addData("geom_name","geom_def").addCtrl("geom_name", String.class)
			.addData("pop_pos",new Vector2(-60,0)).addCtrl("pop_pos", Vector2.class)
			.addData("rot_pos",new Vector2(-30,0)).addCtrl("rot_pos", Vector2.class)
			;
			
			member.newLocalProperty("mem_unit")
			.addRef("shape","shape_unit")
			.addRef("shape1","shape_unit")
//			.addRef("shape2","shape_unit")
//			.addRef("shape3","shape_unit")
//			.addRef("shape4","shape_unit")
//			.addRef("shape5","shape_unit")
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
			.addData("move_strength", 70f)
			.addData("rot_strength", 1f/50f)
			.addData("max_speed", 140f)
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

		
		
		MobSpawn avatarSpawn;
		public void setAvatarSpawn(Vector2 p, float r) { avatarSpawn = new MobSpawn(p,r,0,""); }
		
		private class MobSpawn { 
			Vector2 pos; float rot; int id; String color;
			MobSpawn(Vector2 v, float r, int i, String c) { 
				pos = new Vector2(v); rot = r; id = i; color = Utl.copy(c); } }
		int mobCnt = 0;
		ArrayList<MobSpawn> mobspawn = new ArrayList<MobSpawn>();
		public void addMobSpawn(Vector2 v, float r, int i, String c) { 
			mobspawn.add(new MobSpawn(v,r,i,c)); }

		
		
		

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
		pGeom geo = null;

		public sBoo val_draw_debug, val_draw_ray_debug, 
			val_draw_vision, val_draw_tile, val_draw_solid, 
			val_draw_light, val_draw_color, val_draw_aura, 
			val_draw_ground, val_draw_fog, 
			val_do_calc, val_edit_tile;
		
		public sInt val_bullet_limit;
		
		public sInt val_body_nb, val_light_nb, val_bullet_nb, val_shape_nb;
		
		public World world;
		public Box2DRenderer boxRenderer;

		nRenderer renderer;

		pView view;
		
		public void system_init() {
			bloc.addObject("box2d", this);

			app.storeSystemType(bloc.ref, this.getClass());
			app.box = this;
			
			useNetFrame();

			val_draw_debug = bloc.obtainBoo("val_draw_debug", false);
			val_draw_ray_debug = bloc.obtainBoo("val_draw_ray_debug", false);
			val_draw_vision = bloc.obtainBoo("val_draw_vision", app.config.DRAW_VISION);
			val_draw_tile = bloc.obtainBoo("val_draw_tile", true);
			val_draw_solid = bloc.obtainBoo("val_draw_solid", true);
			val_draw_light = bloc.obtainBoo("val_draw_light", true);
			val_draw_aura = bloc.obtainBoo("val_draw_aura", true);
			val_draw_color = bloc.obtainBoo("val_draw_color", true);
			val_draw_ground = bloc.obtainBoo("val_draw_ground", app.config.DRAW_GROUND);
			val_draw_fog = bloc.obtainBoo("val_draw_fog", app.config.DRAW_FOG);
			val_do_calc = bloc.obtainBoo("val_do_calc", true);
			val_edit_tile = bloc.obtainBoo("val_edit_tile", false);
			val_bullet_limit = bloc.obtainInt("val_bullet_limit", 20000);
			val_body_nb = bloc.obtainInt("val_body_nb", 0);
			val_light_nb = bloc.obtainInt("val_light_nb", 0);
			val_bullet_nb = bloc.obtainInt("val_bullet_nb", 0);
			val_shape_nb = bloc.obtainInt("val_shape_nb", 0);
			

			app.outputs.put("reset_mob_spawn", new nRun() { public void run() {
				mobCnt = 0; }});
			app.outputs.put("next_mob_spawn", new nRun() { public void run() {
				mobCnt++; }});
			
			app.inputs.put("get_mob_spawn_pos", new nRun() { public Object get() {
				return mobspawn.get(mobCnt).pos; }});
			app.inputs.put("get_mob_spawn_rot", new nRun() { public Object get() {
				return mobspawn.get(mobCnt).rot; }});
			app.inputs.put("get_mob_spawn_id", new nRun() { public Object get() {
				return mobspawn.get(mobCnt).id; }});
			app.inputs.put("get_mob_spawn_print", new nRun() { public Object get() {
				if (mobspawn.get(mobCnt).color.equals("green")) return 1f;
				else if (mobspawn.get(mobCnt).color.equals("red")) return 2f;
				else if (mobspawn.get(mobCnt).color.equals("blue")) return 3f;
				else if (mobspawn.get(mobCnt).color.equals("yellow")) return 4f;
				return 0f; }});
			
			app.inputs.put("has_mob_spawn", new nRun() { public Object get() {
				return mobCnt < mobspawn.size(); }});
			
			app.inputs.put("avatar_spawn_pos", new nRun() { public Object get() {
				return (avatarSpawn != null ? avatarSpawn.pos : new Vector2()); }});
			app.inputs.put("avatar_spawn_rot", new nRun() { public Object get() {
				return (avatarSpawn != null ? avatarSpawn.rot : 0f); }});
			
			
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
			
			renderer = new nRenderer(this, world);
			
//			if (app.config.STARTUP_MAP_PATH.length() > 0)
//				renderer.setupMap(app.config.STARTUP_MAP_PATH);

//			app.outputs.put("load_map", new nRun() { public void run() {
//				String p = arg(0, String.class);
//				if (p != null) loadMap(p); }});
			
		}
		public void loadMap(String p) {
			renderer.setupMap(p);
		}
		public ArrayList<String> getMapFile() {
			return renderer.map_files;
		}
		public void system_load() {


			space = app.space;
			view = app.view;
			geo = app.getSystem(pGeom.class);
			
			app.time.addEventTick(tick_run);
			app.time.addEventNetTick(net_tick_run);
			
			view.addDrawable(10,draw_run);
			
			//		if (!app.RELEASE) 
			tool_setup(true);
			
			space.addEventSpaceStart(new nRun() { public void run() {
				space_start();
			}});
			

//			app.term.register("sim", bloc, this);
//			
//			app.term.addExecutor(new CommandExecutor("sim", bloc, this) {
//				
//			});

			
//			test_setup(); // TODO
			
			
		}
		public void system_clear() {
			app.time.removeEventTick(tick_run);
			app.time.removeEventNetTick(net_tick_run);
			app.view.removeDrawable(draw_run);

			renderer.dispose();
			
//			modelBatch.dispose();
//			model.dispose();
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
			interf.add_row();
			interf.add_row_watch(5, "Bullet : ", "val_bullet_nb");
			interf.add_row_watch(5, "Shape : ", "val_shape_nb");

		}

		public void frame(float delta) { 
			val_body_nb.set(bodys.size());
			val_bullet_nb.set(bullet_units.size());
			val_shape_nb.set(shape_units.size());
			int c = 0;
			for (LightLayer l : renderer.rayHandler.layerList) {
				c += l.lightList.size;
			}
			val_light_nb.set(c);
		}

		private float accumulator = 0;
		private float TIME_STEP = 1f/60f;
		private int VELOCITY_ITERATIONS = 6;
		private int POSITION_ITERATIONS = 2;
		private void doPhysicsStep(float deltaTime) {
//			Utl.logn(""+deltaTime);
			deltaTime = Math.min(deltaTime * 0.015f, 0.25f);
			accumulator += deltaTime;
			while (accumulator >= TIME_STEP) {
				world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
				for (pParam b : Utl.duplic(space.param_pools.get("bullet_unit").all())) 
					update_bullet(b);
				for (pParam b : Utl.duplic(space.param_pools.get("shape_unit").all())) 
					update_shape(b);
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
		public boolean drawvision() { return 
				has_vision_bod && val_draw_vision.get(); }
		
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
			

//			app.gdx.drawer.spritebatch.end();
//
////			mesh.setInstanceData(insts, 0, instSize);
//			
//			simpleBlendFunc.apply();
//			shader.bind();
//			shader.setUniformMatrix("u_projTrans", renderer.rayHandler.getCombinedMatrix());
//
//			mesh.render(shader, GL20.GL_TRIANGLES, 0, indSize, true);
//
//			app.gdx.drawer.spritebatch.begin();

		}
		
//		BlendFunc simpleBlendFunc = new BlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
//		Mesh mesh;
//		int vertexNum = 4;
//		int trigNum = 2;
//		int instNum = 1800;
//		protected float vertices[];
//		protected short indices[];
//		protected float insts[];
//		ShaderProgram shader;
//		private int vertSize = 0, indSize = 0, instSize = 0;
//		
//		void test_setup() {
//			
//			vertices = new float[vertexNum * 3];	
//			indices = new short[trigNum * 3];
//			insts = new float[instNum * 2];
//			vertSize = 0; indSize = 0; instSize = 0;
//
//			float c1 = new Color(1f,1f,1f,1f).toFloatBits();
//			float c2 = new Color(1f,1f,0f,1f).toFloatBits();
//
//			pushVert(0f,-20f,c1);
//			pushVert(0f,120f,c2);
//			pushVert(100f,100f,c1);
//			pushVert(100f,0f,c2);
//			pushIndice(0,1,2);
//			pushIndice(0,2,3);
//			
//			int col = 50;
//
//			float ix = -500, iy = 0, is = 200;
//			for (int i = 0 ; i < instNum ; i++)
//				pushInst(ix+is*(i%col),iy+((i-(i%col))/col)*is);
//
//			Mesh.VertexDataType vertexDataType = Mesh.VertexDataType.VertexArray;
////			Mesh.VertexDataType vertexDataType = Mesh.VertexDataType.VertexBufferObject;
////			if (Gdx.gl30 != null) { 
////				vertexDataType = VertexDataType.VertexBufferObjectWithVAO; }
//			if (PlaneApplet.OPENGLES3) 
//				vertexDataType = VertexDataType.VertexBufferObjectWithVAO;
//			
//			mesh = new Mesh(vertexDataType, false, vertexNum, trigNum * 3
//					, new VertexAttribute(Usage.Position, 2, "vertex_positions")
//					, new VertexAttribute(Usage.ColorPacked, 4, "quad_colors")
//					);
//			mesh.enableInstancedRendering (false, instNum
//					, new VertexAttribute(Usage.Position, 2, "i_position")
//					);
//
//			mesh.setVertices(vertices, 0, vertSize);
//			mesh.setIndices(indices, 0, indSize);
//			mesh.setInstanceData(insts, 0, instSize);
//
//			shader = createShader();
//			
////			mesh.bind(shader);
////			int loc = shader.getAttributeLocation("i_position");
////			Utl.logn("i_pos: "+loc);
////			loc = shader.getAttributeLocation("vertex_positions");
////			Utl.logn("v_pos: "+loc);
//			
//		}
//		private void pushVert(float x, float y, float c) {
//			vertices[vertSize++] = x; 
//			vertices[vertSize++] = y; 
//			vertices[vertSize++] = c;
//		}
//		private void pushIndice(int i1, int i2, int i3) {
//			indices[indSize++] = (short)i1; 
//			indices[indSize++] = (short)i2;
//			indices[indSize++] = (short)i3;
//		}
//		private void pushInst(float x, float y) {
//			insts[instSize++] = x; 
//			insts[instSize++] = y;
////			insts[instSize++] = 0f;
//		}
//		
//		
//
//		public static ShaderProgram createShader() {
//			final String vertexShader = "#version 330 core\n"
//				+ "attribute vec2 vertex_positions;\n" //
//				+ "attribute vec4 quad_colors;\n" //
//				+ "attribute vec2 i_position;\n" //
//				+ "uniform mat4 u_projTrans;\n" //
//				+ "varying vec4 v_color;\n" //				
//				+ "void main()\n" //
//				+ "{\n" //
//				+ "   v_color = quad_colors;\n" //		
////				+ "   vec4 v = vec4(-500 + vertex_positions.x + 200 * gl_InstanceID, "
////				+ "			vertex_positions.y, 0.0, 1.0);\n"	
//				+ "   vec4 v = vec4(i_position.x + vertex_positions.x, "
//				+ "			i_position.y + vertex_positions.y, 0.0, 1.0);\n"	
//				+ "   gl_Position = u_projTrans * v;\n" //
//				+ "}\n";
//			final String fragmentShader = "#version 330 core\n"
//				+ "#ifdef GL_ES\n" //
//				+ "precision lowp float;\n" //
//				+ "#define MED mediump\n"
//				+ "#else\n"
//				+ "#define MED \n"
//				+ "#endif\n" //
//				+ "varying vec4 v_color;\n" //
//				+ "void main()\n"//
//				+ "{\n" //
//				+ "  gl_FragColor = v_color;\n" //
//				+ "}";
//			ShaderProgram.pedantic = true;
//			ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
////			if (!shader.isCompiled()) {
////				shader = new ShaderProgram("#version 330 core\n" +vertexShader, "#version 330 core\n" +fragmentShader);
//				if(!shader.isCompiled()) { Utl.logn("ERROR : createShader : " + shader.getLog()); }
////			}
//			return shader;
//		}

		
		

		public void particle(float x, float y, float r, Color c, float d) {
			int l = (int)Utl.rng(10,40);
			float s = Utl.rng(2f,22f);
			ParticleLight.Unit p = renderer.colorLayer.newPartLightUnit(c,d);
			p.set(x,y,r,l,s);
			p = renderer.lightLayer.newPartLightUnit(c,d);
			p.set(x,y,r,l,s);
		}
		public void particle(float x, float y, float r, Color c) {
			float d = Utl.rng(10,30);
			particle(x,y,r,c,d);
		}
		public void particle(float x, float y, float r, float d) {
			float b = Utl.rng(0.7f,0.9f);
			Color c = new Color(b,b*0.75f,0.1f,1f);
			particle(x,y,r,c,d);
		}
		public void particle(float x, float y, float r) {
			float d = Utl.rng(10,30);
			particle(x,y,r,d);
		}
		public void particles(float x, float y, int n) {
			for (int i = 0 ; i < n ; i++) {
				float r = Utl.rng(-Utl.PI,Utl.PI);
				particle(x,y,r);
			}
		}
		public void particles(float x, float y, Color c, int n) {
			for (int i = 0 ; i < n ; i++) {
				float r = Utl.rng(-Utl.PI,Utl.PI);
				particle(x,y,r,c);
			}
		}
		
		
		
		
		
		
		
		
		
		public void space_start() {
			for (pParam p : app.space.param_pools.get("bullet_unit").temp_all())
				clear_bullet(p);
			for (pParam p : app.space.param_pools.get("shape_unit").temp_all())
				clear_shape(p);
		}
		
		

		public void shootBullet(String name, Vector2 pos, float rot) {
			for (pParam p : app.space.param_pools.get("bullet").all())
				if (p.getStr("name").equals(name)) { 
					p.run("pop",pos,rot); 
					particles(pos.x,pos.y,6);
					particles(pos.x,pos.y,new Color(1f,1f,1f,1f),6); 
					return; }
		}

		public pParam getBullet(String name) {
			for (pParam p : app.space.param_pools.get("bullet").all())
				if (p.getStr("name").equals(name)) return p;
			return null;
		}

		public pParam getShape(String name) {
			for (pParam p : app.space.param_pools.get("shape").all())
				if (p.getStr("name").equals(name)) return p;
			return null;
		}

		public pParam popShape(String name, pParam geom, Vector2 pos, float rot) {
			for (pParam p : app.space.param_pools.get("shape").all())
				if (p.getStr("name").equals(name)) return p.run_get("pop",pParam.class,pos,rot,geom);
			return null;
		}

		
		
		
		

		public void init_member(pBody bod) {
			pParam member = bod.param("member");
			pParam geom = geo.getGeom(member.getStr("geom_name"));
			Vector2 pos = bod.getVec("ref","pos");
			float rot = bod.getFlt("ref","rot");
			Vector2 poppos = member.getVec("pop_pos").add(0,60).rotateRad(rot).add(pos); 
			Vector2 rotpos = member.getVec("rot_pos").add(0,60).rotateRad(rot).add(pos);
			pParam newmem = geom.run_get("pop_shape",pParam.class, 
					member.getStr("shape_name"), poppos, rot);
			bod.param("mem_unit").setRef("shape", newmem);
			Body bodyB = getShapeBody(newmem);
			Body bodyA = getBody(bod);
			
//			DistanceJointDef jointDef = new DistanceJointDef ();
//			jointDef.initialize(bodyA, bodyB, pos, poppos);
////			jointDef.length = 300f;
//			jointDef.frequencyHz = 1f;
//			jointDef.dampingRatio = 0f;

//			RopeJointDef jointDef = new RopeJointDef ();
//			jointDef.maxLength = 200;
//			jointDef.collideConnected = true;
//			jointDef.bodyA = bodyA;
//			jointDef.bodyB = bodyB;
			
			float openning = 0.2f;
			
			RevoluteJointDef jointDef = new RevoluteJointDef();
			jointDef.initialize(bodyA, bodyB, rotpos);

			jointDef.lowerAngle = -0.1f * (float)Math.PI + rot - openning; 
			jointDef.upperAngle = 0.1f * (float)Math.PI + rot - openning; 

			jointDef.enableLimit = true;
			jointDef.enableMotor = true;

			jointDef.maxMotorTorque = 20.0f;
			jointDef.motorSpeed = 1.0f;
			
			world.createJoint(jointDef); 
			
			
			
			poppos = member.getVec("pop_pos").add(0,-60).rotateRad(rot).add(pos); 
			rotpos = member.getVec("rot_pos").add(0,-60).rotateRad(rot).add(pos);
			newmem = geom.run_get("pop_shape",pParam.class, 
					member.getStr("shape_name"), poppos, rot);
			bod.param("mem_unit").setRef("shape1", newmem);
			bodyB = getShapeBody(newmem);
			
			jointDef = new RevoluteJointDef();
			jointDef.initialize(bodyA, bodyB, rotpos);

			jointDef.lowerAngle = -0.1f * (float)Math.PI + rot + openning; 
			jointDef.upperAngle = 0.1f * (float)Math.PI + rot + openning; 

			jointDef.enableLimit = true;
			jointDef.enableMotor = true;

			jointDef.maxMotorTorque = 20.0f;
			jointDef.motorSpeed = 1.0f;
			
			world.createJoint(jointDef); 

			
			
			
			
//			new_mem_tail(bod,1,bodyB,poppos,rot);

		}
		
//		private void new_mem_tail(pBody bod, int i, Body bodyA, Vector2 ppos, float rot) {
//			pParam member = bod.param("member");
//			pParam geom = geo.getGeom(member.getStr("geom_name"));
//			Vector2 poppos = member.getVec("pop_pos").rotateRad(rot).add(ppos); 
//			Vector2 rotpos = member.getVec("rot_pos").rotateRad(rot).add(ppos);
//			pParam newmem = geom.run_get("pop_shape",pParam.class, 
//					member.getStr("shape_name"), poppos, rot);
//			bod.param("mem_unit").setRef("shape"+i, newmem);
//			Body bodyB = getShapeBody(newmem);
//			
//			RevoluteJointDef jointDef = new RevoluteJointDef();
//			jointDef.initialize(bodyA, bodyB, rotpos);
//
//			jointDef.lowerAngle = -0.1f * (float)Math.PI + rot; 
//			jointDef.upperAngle = 0.1f * (float)Math.PI + rot; 
//
//			jointDef.enableLimit = true;
//			jointDef.enableMotor = true;
//
//			jointDef.maxMotorTorque = 50.0f;
//			jointDef.motorSpeed = 1.0f;
//			
//			world.createJoint(jointDef); 
//
//			if (i < 6) { new_mem_tail(bod,i+1,bodyB,poppos,rot); }
//		}

		public void clear_member(pBody bod) {
			pParam mem = bod.param("mem_unit").getRef("shape");
			clear_shape(mem);
			for (int i = 1 ; i < 6 ; i++) {
				mem = bod.param("mem_unit").getRef("shape"+i);
				if (mem != null) clear_shape(mem);
			}
		}
		
		
		
		
		
		private int get_free_shape_nb() {
			int n = 0; while (shape_units.get(n) != null) n++; return n; }

		public HashMap<Integer,Body> shape_units = 
				new HashMap<Integer,Body>();
		
		public Body getShapeBody(pParam p) {
			return shape_units.get(p.getInt("body_id")); }

		private FixtureDef fixtDef = new FixtureDef();
		public void init_shape(pParam par, pParam shape, pParam geom, Vector2 pos, float r) {
			par.setRef("def",shape);
			
			BodyDef bodyDef = new BodyDef();
			if (shape.getBoo("dynamic"))
				bodyDef.type = BodyType.DynamicBody;
			else if (shape.getBoo("kinematic"))
				bodyDef.type = BodyType.KinematicBody;
			else if (shape.getBoo("static"))
				bodyDef.type = BodyType.StaticBody;
			bodyDef.position.set(pos); bodyDef.angle = r;
			par.set("pos",pos); par.set("rot", r);

			Body body = world.createBody(bodyDef);			
			body.setUserData(par);
			int nb = get_free_shape_nb();
			shape_units.put(nb, body);
			par.set("body_id", nb);
			
			if (shape.getBoo("transparent"))
				renderer.rayHandler.transparent.add(body);
			if (shape.getBoo("lightTransparent"))
				renderer.lightLayer.transparent.add(body);
			if (shape.getBoo("visionTransparent"))
				renderer.visionLayer.transparent.add(body);
			if (shape.getBoo("break_bodys"))
				break_bodys.add(body);
			if (shape.getBoo("body_breaker"))
				body_breaker.add(body);
			
			fixtDef.density = shape.getFlt("density");
			fixtDef.friction = shape.getFlt("friction");
			fixtDef.restitution = shape.getFlt("restitution");
			fixtDef.isSensor = shape.getBoo("sensor");
			PolygonShape polygonshape = new PolygonShape();
			Vector2[] pl = new Vector2[3];
			pl[0] = new Vector2(); pl[1] = new Vector2(); pl[2] = new Vector2();
			TrigBatchLight.Unit su = renderer.solidLayer.newTrigBatchUnit();
			attachToBody(su, body);
			
			Float[] verts = geom.run_get("get_flt_array", Float[].class);
			int faceNb = verts.length / 9;
			for (int i = 0 ; i < faceNb ; i++) {
				pl[0].set(verts[i],verts[i+1]); 
				pl[1].set(verts[i+3],verts[i+4]); 
				pl[2].set(verts[i+6],verts[i+7]);
				polygonshape.set(pl);
				fixtDef.shape = polygonshape;
				body.createFixture(fixtDef);
				su.trig(verts[i], verts[i+1], verts[i+3], verts[i+4], verts[i+6], verts[i+7], 
						verts[i+2], verts[i+5], verts[i+8]);
			}
		}
		
		public void clear_shape(pParam p) {
			Body body = shape_units.get(p.getInt("body_id"));
			shape_units.remove(p.getInt("body_id"),body);
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
			p.clear();
		}

		public void update_shape(pParam par) {
			Body su = shape_units.get(par.getInt("body_id"));
			if (clearing_bodys.contains(su)) {
				clearing_bodys.remove(su);
				par.clear();
				return;
			}
			if (par.getRef("def").getBoo("param_ctrl")) {
				Vector2 p = par.getVec("pos");
				su.setTransform(p.x,p.y,par.getFlt("rot"));
			} else {
				par.setVec("pos",su.getPosition());
				par.set("rot",su.getAngle());
			}
		}

//		public void move_shape(pParam b, float x, float y) {
//			Body body = shape_units.get(b.getInt("body_id"));
//			if (body == null) return;
//			float rot = body.getAngle();
//			body.setTransform(x,y,rot);
//		}
//
//		public void move_shape(pParam b, float x, float y, float r) {
//			Body body = shape_units.get(b.getInt("body_id"));
//			if (body == null) return;
//			body.setTransform(x,y,r);
//		}
//
//		public void accel_shape(pParam b, boolean glob, float x, float y, float max) {
//			Body body = shape_units.get(b.getInt("body_id"));
//			if (body == null) return;
//			Vector2 pos = body.getPosition();
//			Vector2 vel = body.getLinearVelocity();
//			Vector2 a = new Vector2(x,y).scl(body.getMass());
//			if (!glob) a.rotateRad(body.getAngle()-(float)(Math.PI/2f));
//			Vector2 futur = new Vector2(a).add(vel);
//			float futl = futur.len();
//			if (futl > max) { decel_shape_move(b,futl-max); }
//			body.applyLinearImpulse(a.x, a.y, pos.x, pos.y, true);
//		}
//
//		public void decel_shape_move(pParam b, float s) {
//			Body body = shape_units.get(b.getInt("body_id"));
//			if (body == null) return;
//			Vector2 pos = body.getPosition();
//			Vector2 vel = body.getLinearVelocity();
//			float vell = vel.len();
//			if (vell > s) vel.nor().scl(s);
//			vel.scl(-1f).scl(body.getMass());
//			body.applyLinearImpulse(vel.x, vel.y, pos.x, pos.y, true);
//		}
//
//		public void rot_shape_toward(pParam b, float targ, float r, float max) {
//			Body body = shape_units.get(b.getInt("body_id"));
//			if (body == null) return;
//			float vel = body.getAngularVelocity();
//			float rot = body.getAngle();
//			float m = Utl.mapToCircularValuesDist(rot, targ, r, 
//					-((float)Math.PI), ((float)Math.PI));
//			float d = Utl.mapToCircularValuesDir(rot, targ, r, 
//					-((float)Math.PI), ((float)Math.PI)); 
//			float d2 = Utl.mapToCircularValuesDir(rot+vel, targ, r, 
//					-((float)Math.PI), ((float)Math.PI)); 
//			if ((d>0) == (d2>0) && m > 0.01f) {
//				m *= d;
//				rot_shape(b, m, max);
//			} else {
//				decel_shape_rot(b, r);
//			}
//		}
//
//		public void rot_shape(pParam b, float r, float max) {
//			Body body = shape_units.get(b.getInt("body_id"));
//			if (body == null) return;
//			float vel = body.getAngularVelocity();
//			if (vel > 0f && vel > max) return;
//			if (vel < 0f && vel < -max) return;
//			body.applyAngularImpulse(body.getInertia()*r, true);
//		}
//
//		public void decel_shape_rot(pParam b, float s) {
//			Body body = shape_units.get(b.getInt("body_id"));
//			if (body == null) return;
//			float vel = body.getAngularVelocity();
//			if (vel > 0f && vel > s) vel = s;
//			if (vel < 0f && vel < -s) vel = -s;
//			body.applyAngularImpulse(body.getInertia()*-vel, true);
//		}

		
		
		
		
		
		
		private Vector2 end = new Vector2();
//		private float fract = 0;
		private Body coll = null;
		final RayCastCallback ray = new RayCastCallback() {
			@Override
			final public float reportRayFixture(Fixture fixture, Vector2 point,
					Vector2 normal, float fraction) {
				
				if (!body_breaker.contains(fixture.getBody())) return -1;
				
				end.set(point);
//				fract = fraction;
				coll = fixture.getBody();
				return fraction;
			}
		};
		
		private int get_free_bullet_nb() {
			int n = 0; while (bullet_units.get(n) != null) n++; return n; }

		public HashMap<Integer,SwarmLight.Unit> bullet_units = 
				new HashMap<Integer,SwarmLight.Unit>();
		
		public void init_bullet(pParam par, pParam b, Vector2 pos, float rot) {
			par.setRef("def",b);
			par.set("pos",pos); par.set("rot",rot);
			Color col = Utl.color(b.getInt("r"), b.getInt("g"), 
					b.getInt("b"), b.getInt("a"));
//			
			if (b.getBoo("aura")) {
				float adist = b.getFlt("aura_dist");
				SwarmLight.Unit su = renderer.auraLayer.newSwarmLightUnit(
						new Color(col), adist);
				int nb = get_free_bullet_nb();
				bullet_units.put(nb, su);
				par.set("aura_id", nb);
			}
			
			if (b.getBoo("light")) {
				float ldist = b.getFlt("light_dist");
				SwarmLight.Unit su = renderer.lightLayer.newSwarmLightUnit(
						new Color(col), ldist);
				int nb = get_free_bullet_nb();
				bullet_units.put(nb, su);
				par.set("light_id", nb);
			}
		}
		
		public void clear_bullet(pParam p) {
			pParam def = p.getRef("def");
			if (def.getBoo("light")) {
				SwarmLight.Unit su = bullet_units.get(p.getInt("light_id"));
				bullet_units.remove(p.getInt("light_id"),su);
				su.remove();
			}
			if (def.getBoo("aura")) {
				SwarmLight.Unit su = bullet_units.get(p.getInt("aura_id"));
				bullet_units.remove(p.getInt("aura_id"),su);
				su.remove();
			}
			p.clear();
		}

		private Vector2 ppos = new Vector2();
		private Vector2 m = new Vector2();
		private Vector2 m2 = new Vector2();
		public void update_bullet(pParam p) {
			Vector2 pos = p.getVec("pos");
			ppos.set(pos);
			float rot = p.getFlt("rot");
			pParam def = p.getRef("def");
			float speed = def.getFlt("speed");
			float len = def.getFlt("len");
			m.set(speed,0).rotateRad(rot);
			m2.set(len,0).rotateRad(rot);
			ppos.sub(m2);
			m.add(pos);
			int lim = val_bullet_limit.get();
			if (m.x > lim || m.x < -lim || m.y > lim || m.y < -lim) {
				clear_bullet(p);
				return;
			}
			coll = null; end.set(m); //fract = 1.0f;
			world.rayCast(ray, pos, m);
			if (coll != null) {

				particles(end.x,end.y,6);
				
				if (coll.getUserData() != null && 
						(coll.getUserData() instanceof pBody)) {
					pBody bod = (pBody)coll.getUserData();
					if (bod.hasParam("hp") && bod.hasParam("box_body")) {
						int damage = def.getInt("damage");
						int filterA = def.getInt("filter");
						int filterB = bod.getInt("physic","filter");
						if (filterA == filterB) {

							particles(end.x,end.y,12);
							particles(end.x,end.y,new Color(1f,0.4f,0.15f,1f),12);
							particles(end.x,end.y,new Color(1f,0f,0f,1f),12);
							
							bod.setInt("hp", "hp", bod.getInt("hp", "hp") - damage);
							if (bod.getInt("hp", "hp") <= 0) {
								Body body = bodys.get(bod.getStr("box_body", "body_ref"));
								if (body != null) {

									particles(end.x,end.y,60);
									particles(end.x,end.y,new Color(1f,0.4f,0.15f,1f),60);
									particles(end.x,end.y,new Color(1f,0f,0f,1f),60);
									
									if (!clearing_bodys.contains(body)) 
										clearing_bodys.add(body);
									if (bod.getBoo("hitpoint","avatar") && 
											app.getSystem(pGeom.class) != null) 
										app.getSystem(pGeom.class).game_over();
								}
							}
						}
					}
				}
				clear_bullet(p);
				return;
			} else {
				pos.set(m);
				p.setVec("pos",pos);
				if (def.getBoo("light")) {
					SwarmLight.Unit su = bullet_units.get(p.getInt("light_id"));
//					su.setPos(pos,rot);
					su.setPos(ppos,pos);
				}
				if (def.getBoo("aura")) {
					SwarmLight.Unit su = bullet_units.get(p.getInt("aura_id"));
//					su.setPos(pos,rot);
					su.setPos(ppos,pos);
				}
			}
		}
		
		
		
		

		public void endContact(Contact contact) {
			
		}
		public void beginContact(Contact contact) {
			Fixture fa = contact.getFixtureA();
			Fixture fb = contact.getFixtureB();
			Body ba = fa.getBody();
			Body bb = fb.getBody();

			test_collided(ba,bb);
			test_collided(bb,ba);
			
//			if (break_bodys.contains(ba) && body_breaker.contains(bb)) {
//				if (!clearing_bodys.contains(ba)) clearing_bodys.add(ba);
//			}
//			if (break_bodys.contains(bb) && body_breaker.contains(ba)) {
//				if (!clearing_bodys.contains(bb)) clearing_bodys.add(bb);
//			}
//			if (ba.getUserData() != null && 
//					(ba.getUserData() instanceof pBody) && 
//					bb.getUserData() != null && 
//					(bb.getUserData() instanceof pBody)) {
//				pBody b1 = (pBody)ba.getUserData();
//				pBody b2 = (pBody)bb.getUserData();
//
//				if (!b1.hasParam("hp") || !b2.hasParam("hitzone")) {
//					pBody t = b1; b1 = b2; b2 = t; 
//					Body bt = ba; ba = bb; bb = bt; 
//					Fixture ft = fa; fa = fb; fb = ft; }
//				if (!b1.hasParam("hp") || !b2.hasParam("hitzone")) return;
//				
//				int damage = b2.getInt("hitzone", "damage");
//				b1.setInt("hp", "hp", b1.getInt("hp", "hp") - damage);
//				
//				if (b1.getInt("hp", "hp") <= 0) {
//					if (!clearing_bodys.contains(ba)) clearing_bodys.add(ba);
//					if (b1.getBoo("hitpoint","avatar") && 
//							app.getSystem(pGeom.class) != null) 
//						app.getSystem(pGeom.class).game_over();
//				}
//				if (!clearing_bodys.contains(bb)) clearing_bodys.add(bb);
//			}
		}
		
		private void test_collided(Body ba, Body bb) {

			if (ba.getUserData() != null && 
					(ba.getUserData() instanceof pBody)) {
				pBody b1 = (pBody)ba.getUserData();
				if (!b1.hasParam("ctrl_mob") || 
						b1.getInt("ctrl_mob","collision_tmp") > 0) return;
				b1.setBoo("ctrl_mob", "direction", 
						!b1.getBoo("ctrl_mob", "direction"));
				b1.setInt("ctrl_mob","collision_tmp",(int)10);
			}
		}
		
		public ArrayList<Body> break_bodys = new ArrayList<Body>();
		public ArrayList<Body> body_breaker = new ArrayList<Body>();

		public ArrayList<Body> clearing_bodys = new ArrayList<Body>();
		
		public boolean has_vision_bod = false;
		
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
				
				bodyDef.linearDamping = 1f;
				

				// Create our body in the world using our body definition
				Body body = world.createBody(bodyDef);
				
				body.setUserData(b);
				
				renderer.lightLayer.transparent.add(body);
				renderer.visionLayer.transparent.add(body);
				
//				if (b.getBoo("physic", "contact_break")) {
//					break_bodys.add(body);
//				} else {
					body_breaker.add(body);
//				}

				if (b.getBoo("physic", "aura")) {
					Color col = new Color(b.getFlt("physic", "r"), 
							b.getFlt("physic", "g"), 
							b.getFlt("physic", "b"), 1f);
					SwarmLight.Unit su = renderer.auraLayer.newSwarmLightUnit(
							col, b.getFlt("physic", "aura_dist")); 
					attachToBody(su, body);
					col.a = 0.5f;
					su = renderer.lightLayer.newSwarmLightUnit(
							col, b.getFlt("physic", "light_dist"));
					attachToBody(su, body);
				}

				if (b.getBoo("physic", "view_light")) {
					has_vision_bod = true;
					
					renderer.newVisionLight(body);
					
					attachToBody(renderer.colorLayer.newRectLight(
							10, new Color(1f,0f,0f,1f), 0f, 0f, 200f, 40f), 
							body, 80f, -20f, 0f);
					
				}

//				if (b.getBoo("physic", "light")) {
////					Vector2 pos = b.getVec("physic", "light_pos");
//					float dist = b.getFlt("physic", "light_dist");
//					Color col = Utl.color(b.getInt("physic", "r"), b.getInt("physic", "g"), 
//							b.getInt("physic", "b"), b.getInt("physic", "a"));
//					
//					renderer.rayHandler.transparent.add(body);
//
//					SwarmLight.Unit su = renderer.auraLayer.newSwarmLightUnit(
//							new Color(col.r,col.g,col.b,col.a), dist);
//					attachToBody(su, body);
//					su = renderer.lightLayer.newSwarmLightUnit(
//							new Color(1f,0f,0f,0.7f), dist*0.5f);
//					attachToBody(su, body);
//				} 

				float density = b.getFlt("physic", "density");
				float friction = b.getFlt("physic", "friction");
				float restitution = b.getFlt("physic", "restitution");

//				if (!b.getBoo("physic", "copy_geom") || !b.hasParam("geom")) {
//					PolygonShape polygonshape = new PolygonShape();
//					polygonshape.setAsBox(100,100);
//					FixtureDef fixtureDef2 = new FixtureDef();
//					fixtureDef2.shape = polygonshape;
//					fixtureDef2.density = density;
//					fixtureDef2.friction = friction;
//					fixtureDef2.restitution = restitution;
//					if (b.getBoo("physic", "sensor")) fixtureDef2.isSensor = true;
//					Fixture fixture = body.createFixture(fixtureDef2);
////					fixture.setUserData(new LightData(10f));
//				} else 
				if (b.hasParam("geom")) {
					for (pParam p : b.params.all()) if (p.prop.ref.equals("geom")) {
						pParam geom = p;
						ArrayList<Vector2> point = geom.getCollecData("point", Vector2.class);
						ArrayList<Integer> color = geom.getCollecData("color", Integer.class);
						ArrayList<Integer> faceA = geom.getCollecData("faceA", Integer.class);
						ArrayList<Integer> faceB = geom.getCollecData("faceB", Integer.class);
						ArrayList<Integer> faceC = geom.getCollecData("faceC", Integer.class);
						if (faceA.size() != faceB.size() || faceA.size() != faceC.size() || 
								color.size() != point.size()) return;

						TrigBatchLight.Unit su = renderer.solidLayer.newTrigBatchUnit();
						attachToBody(su, body);
						
						for (int i = 0 ; i < faceA.size() ; i++) {
							int p1 = faceA.get(i), p2 = faceB.get(i), p3 = faceC.get(i);
							if (p1 < 0 || p1 >= point.size() || 
									p2 < 0 || p2 >= point.size() || 
									p3 < 0 || p3 >= point.size()) continue;
							Vector2[] pl = new Vector2[3];
//							float s = b.getFlt("ref","scale");
							pl[0] = Utl.copy(point.get(p1)); 
							pl[1] = Utl.copy(point.get(p2)); 
							pl[2] = Utl.copy(point.get(p3));
//							pl[0].scl(s); pl[1].scl(s); pl[2].scl(s);
							PolygonShape polygonshape = new PolygonShape();
							polygonshape.set(pl);
							FixtureDef fixtureDef2 = new FixtureDef();
							fixtureDef2.shape = polygonshape;
							fixtureDef2.density = density;
							fixtureDef2.friction = friction;
							fixtureDef2.restitution = restitution;
//							if (b.getBoo("physic", "sensor")) fixtureDef2.isSensor = true;
							Fixture fixture = body.createFixture(fixtureDef2);
//							fixture.setUserData(new LightData(1f));

							float s = 1.1f;
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
		
		public Body getBody(pBody b) {
			if (b.param("box_body") == null) return null;
			return bodys.get(b.getStr("box_body", "body_ref"));
		}

		public void clear_body(pBody b) {
			if (b.param("box_body") == null) return;
			Body body = bodys.get(b.getStr("box_body", "body_ref"));
			if (body == null) return;
			if (b.getBoo("physic", "view_light")) has_vision_bod = false;
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

		public void move_body(pBody b, float x, float y, float r) {
			if (b.param("box_body") == null) return;
			Body body = bodys.get(b.getStr("box_body", "body_ref"));
			if (body == null) return;
			body.setTransform(x,y,r);
		}

		public void accel_body(pBody b, boolean glob, float x, float y, float max) {
			if (b.param("box_body") == null) return;
			Body body = bodys.get(b.getStr("box_body", "body_ref"));
			if (body == null) return;
//			Vector2 pos = body.getPosition();
			Vector2 vel = body.getLinearVelocity();
//			Utl.logn(""+vel.len());
			Vector2 a = new Vector2(x,y); 
			if (!glob) a.rotateRad(body.getAngle()-(float)(Math.PI/2f));
			float d = (a.angleRad() - vel.angleRad());
			boolean go = false;
			if (d <= 0.1f && d >= -0.1f) go = true;
			d = (a.angleRad() + vel.angleRad());
			if (d <= 0.1f && d >= -0.1f) go = true;
			if (go) {
				float futl = vel.len();
				if (futl < max) {
					a.scl( (1f - (futl/(max*1f))) * body.getMass()*15f);
					body.applyForceToCenter(a.x, a.y, true);
				} 
			} else {
				a.scl(body.getMass()*15f);
				body.applyForceToCenter(a.x, a.y, true);
			}
////			body.applyLinearImpulse(a.x, a.y, pos.x, pos.y, true);
//			else body.applyForceToCenter(a.x, a.y, true);
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
//			body.applyForceToCenter(vel.x, vel.y, true);
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
