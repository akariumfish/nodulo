package aa_nodulo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.noodle.nodulo.GdxApp;

import app.App;
import box2dLight.*;
import data.*;
import gui.*;
import util.Utl;
import util.nMap;
import util.nPool;
import util.nRun;
import patch.*;
import patch.pMacro.Macro;
import patch.pMacro.MacroScript;

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
				if (bod.hasParam("dynamic")) b2d.init_dyna_body(bod);
				else if (bod.hasParam("kinematic")) b2d.init_kinematic_body(bod);
//				if (bod.hasParam("light")) b2d.init_light_body(bod);
			}});

			physic.addClearRun(new nRun() {public void run() {
				pBody bod = arg(0,pBody.class);
				pBox2d box = bod.space.app.getSystem(pBox2d.class);
				if (box == null || bod == null) return;
				box.clear_body(bod);
			}});


			physic.newOptionalLocalProperty("dynamic")
			.addData("ctrl_ref", true)
			;
			physic.newOptionalLocalProperty("kinematic")
			.addData("follow_ref", true)
			.addData("rad", 60f)
			//			.addData("joint_ref", "")
			;
			physic.newLocalProperty("box_body")
			.addData("pos", new Vector2())
			.addData("rot", 0f)
			.addData("body_ref", "")
			.addData("copy_geom", true)
			;
			pFamily.newFamily("box_body")
			.addProp("box_body")
			;


			physic.newOptionalLocalProperty("light")
			.addData("follow_ref", true)
			.addData("pos", new Vector2())
			.addData("dist", 300f)
			.addData("r", (int)255)
			.addData("g", (int)50)
			.addData("b", (int)50)
			.addData("a", (int)255)
			;

			physic.newOptionalLocalProperty("cone_light")
			;

			physic.newOptionalLocalProperty("view_light")
			;

			physic.newOptionalLocalProperty("contact_break")
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

		public pBox2d() { 
			super(); 
			tick_run = new nRun() { public void run(Object o) { tick((float)o); }};
			net_tick_run = new nRun() { public void run(Object o) { net_tick((float)o); }};
			draw_run = new nDrawable() { public void drawing() { draw(); }}; 
			tile_draw_run = new nDrawable() { public void drawing() { draw_tile(); }}; 
			pre_draw_run = new nDrawable() { public void drawing() { pre_draw(); }}; 
			//			post_draw_run = new nDrawable() { public void drawing() { post_draw(); }}; 
			draw_ray_run = new nDrawable() { public void drawing() { draw_ray(); }}; 
		}

		nRun tick_run, net_tick_run;
		nDrawable draw_run, pre_draw_run, 
		//			post_draw_run, 
		tile_draw_run, 
		draw_ray_run
		;

		OrthographicCamera cam;

		public pBox2d init(sValueBloc b) { return (pBox2d) super.init(b); }

		public pSpace space;

		public sBoo val_do_draw, val_do_viewfilter, 
//		val_draw_debug, 
		val_do_ray, val_do_calc, 
		val_do_tile, val_edit_tile;

		public World world;
		public Box2DRenderer boxRenderer;

		nTileMap tilemap;

		pView view;
		
		public void system_init() {
			bloc.addObject("box2d", this);

			app.storeSystemType(bloc.ref, this.getClass());

			useNetFrame();

			val_do_draw = bloc.obtainBoo("val_do_draw", false);
			val_do_viewfilter = bloc.obtainBoo("val_do_viewfilter", false);
//			val_draw_debug = bloc.obtainBoo("val_draw_debug", false);
			val_do_ray = bloc.obtainBoo("val_do_ray", true);
			val_do_calc = bloc.obtainBoo("val_do_calc", true);
			val_do_tile = bloc.obtainBoo("val_do_tile", true);
			val_edit_tile = bloc.obtainBoo("val_edit_tile", false);
			
			
			
			world = new World(new Vector2(0, 0), true);

			world.setContactListener(new ContactListener() {
				@Override public void endContact(Contact contact) {
					Fixture fa = contact.getFixtureA();
					Fixture fb = contact.getFixtureB();
					Body ba = fa.getBody();
					Body bb = fb.getBody();
				}
				@Override public void beginContact(Contact contact) {
					Fixture fa = contact.getFixtureA();
					Fixture fb = contact.getFixtureB();
					Body ba = fa.getBody();
					Body bb = fb.getBody();
					
					if (break_bodys.contains(ba)) {
						clearing_bodys.add(ba);
					}
					if (break_bodys.contains(bb)) {
						clearing_bodys.add(bb);
					}
				}
				@Override public void preSolve(Contact contact, Manifold oldManifold) { }
				@Override public void postSolve(Contact contact, ContactImpulse impulse) { }
			});

			//		boolean drawBodies, boolean drawJoints, 
			//		boolean drawAABBs, boolean drawInactiveBodies, 
			//		boolean drawVelocities, boolean drawContacts
			boxRenderer = new Box2DRenderer(app, true, true, true, true, true, true);
			
			cam = new OrthographicCamera(GdxApp.WIDTH, GdxApp.HEIGHT);


			tilemap = new nTileMap("Map.tmx", this, world);
			
		}
		public void system_load() {

			space = app.space;
			view = app.view;
			
			app.time.addEventTick(tick_run);
			app.time.addEventNetTick(net_tick_run);

			view.addPreDrawable(0,pre_draw_run);
			view.addDrawable(1,tile_draw_run);
			view.addDrawable(6,draw_run);
			view.addDrawable(11,draw_ray_run);
			//			app.view.addPostDrawable(22,post_draw_run);
			//		if (!app.RELEASE) 
			tool_setup(true);

		}
		public void system_clear() {
			app.time.removeEventTick(tick_run);
			app.time.removeEventNetTick(net_tick_run);
			app.view.removeDrawable(draw_run);
			app.view.removeDrawable(tile_draw_run);
			app.view.removeDrawable(pre_draw_run);
			//			app.view.removeDrawable(post_draw_run);
			app.view.removeDrawable(draw_ray_run);

			tilemap.dispose();
		}

		public void tool_init(nInterface interf) {

			interf.setContext(bloc);
			interf.add_row();
			interf.add_row_switch_boo(4, "draw", "val_do_draw");
			interf.add_row_label(2, "");
			interf.add_row_switch_boo(4, "physic", "val_do_calc");
			interf.add_row();
			interf.add_row_switch_boo(4, "filter", "val_do_viewfilter");
			interf.add_row_label(2, "");
			interf.add_row_switch_boo(4, "light", "val_do_ray");
			interf.add_row();
			interf.add_row_switch_boo(4, "tile", "val_do_tile");
			interf.add_row_label(2, "");
			interf.add_row_switch_boo(4, "edit", "val_edit_tile");

		}

		public void frame(float delta) { 

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
				for (pBody b : space.familyMember("box_body")) update_body(b);
			}

		}

		public void net_frame(float delta) { 
			frame(delta);
		}
		public void net_tick(float delta) { 

		}

		public final ArrayList<Rectangle> scissors = new ArrayList<Rectangle>();

		public void pre_draw() { 
			tilemap.beginRender();
		}
		public void draw_tile() { 

			if (val_edit_tile.get() && val_do_tile.get()) {
				if (app.input.mouseLeft.trigClick && 
						app.view.mouse_is_hover_view()) {
					tilemap.addCell(app.view.mouse_in_view()); }
				if (app.input.mouseRight.trigClick && 
						app.view.mouse_is_hover_view()) {
					tilemap.delCell(app.view.mouse_in_view()); }
			}

			tilemap.renderBack();

			// WORKING
			//			app.getSystem(pGeom.class).draw_shadow();

		}

		public void draw() { 
			
		}

		public boolean drawtile() { return val_do_tile.get(); }
		public boolean drawlight() { 
			return val_do_ray.get() && app.gdx.drawer.USE_FX; }
		public boolean drawviewfilter() { return val_do_viewfilter.get(); }
		
		public void draw_ray() { 

			tilemap.renderFront();

			tilemap.endRender();
			
			if (val_do_draw.get()) {
				boxRenderer.render(world);
				
				app.stroke(255,0,255,255,5f); app.fill(55,0,55,255);
				for (Light light : tilemap.rayHandler.lightList) {
					app.circle(light.getPosition().x, light.getPosition().y, 12f);
				}
				
			}
		}

		
		
		
		
		
		public void new_ground_box(float x, float y, float w, float h) {

			// Create our body definition
			BodyDef groundBodyDef = new BodyDef();  
			// Set its world position
			groundBodyDef.position.set(new Vector2(x, y));  

			// Create a body from the definition and add it to the world
			Body groundBody = world.createBody(groundBodyDef);  

			// Create a polygon shape
			PolygonShape groundBox = new PolygonShape();  
			// Set the polygon shape as a box which is twice the size of our view port and 20 high
			// (setAsBox takes half-width and half-height as arguments)
			groundBox.setAsBox(w/2f,h/2f);
			// Create a fixture from our polygon shape and add it to our ground body  
			groundBody.createFixture(groundBox, 0.0f);
			// Clean up after ourselves
			groundBox.dispose();

		}

		public ArrayList<Body> break_bodys = new ArrayList<Body>();

		public ArrayList<Body> clearing_bodys = new ArrayList<Body>();
		
		public nMap<Body> bodys = new nMap<Body>();
		private int get_free_bod_nb() {
			int n = 0; while (bodys.get(""+n) != null) n++; return n; }

		public nMap<Joint> joints = new nMap<Joint>();
		//		private int joint_nb = 0;
		
		public HashMap<Body,ArrayList<Light>> lights = new HashMap<Body,ArrayList<Light>>();
		public void attachToBody(PointLight l , Body b) { attachToBody(l,b,0f,0f); }
		public void attachToBody(PointLight l , Body b, float x, float y) {
			if (lights.get(b) == null) lights.put(b, new ArrayList<Light>());
			lights.get(b).add(l);
			l.setIgnoreAttachedBody(true);
			l.attachToBody(b, x, y);
		}
		
		public void init_dyna_body(pBody b) {
			app.addEventNextFrame(new nRun(b) {public void run() {
				pBody b = (pBody)builder;
				if (b.param("box_body") == null || b.param("dynamic") == null) return;

				// First we create a body definition
				BodyDef bodyDef = new BodyDef();
				// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
				bodyDef.type = BodyType.DynamicBody;
				// Set our body's starting position in the world
				if (b.hasParam("ref") && b.getBoo("dynamic", "ctrl_ref"))
					bodyDef.position.set(b.getVec("ref", "pos"));
				else bodyDef.position.set(b.getVec("box_body", "pos"));
				

				// Create our body in the world using our body definition
				Body body = world.createBody(bodyDef);
				
				tilemap.rayHandler.transparent.add(body);
				
				if (b.hasParam("contact_break")) {
					break_bodys.add(body);
				}

				if (b.hasParam("cone_light")) {

					PointLight pl = tilemap.newPointLight(90, 
							new Color(1f,0.6f,0.4f,1f), 400, 0, 0);
					attachToBody(pl, body);

					PointLight pl2 = new PointLight(tilemap.groundLayer, 60, 
							new Color(1f,1f,1f,0.8f), 900, 0, 0);
					attachToBody(pl2, body);
					
				}

				if (b.hasParam("view_light")) {
					PointLight pl2 = new PointLight(tilemap.viewLayer, 720, 
							new Color(1f,1f,1f,0.9f), 15000, 0, 0);
					attachToBody(pl2, body);
				}

				if (b.hasParam("light")) {
					int rays = 120;
					Vector2 pos = b.getVec("light", "pos");
					float dist = b.getFlt("light", "dist");
					Color col = Utl.color(b.getInt("light", "r"), b.getInt("light", "g"), 
							b.getInt("light", "b"), b.getInt("light", "a"));
					PointLight l = tilemap.newPointLight(rays, col, dist, 0, 0);
					l.setColor(col.r,col.g,col.b,col.a);
					attachToBody(l, body, pos.x, pos.y);
					PointLight l2 = new PointLight(tilemap.groundLayer, 
							rays, col, dist, 0, 0);
					l2.setColor(col.r,col.g,col.b,col.a);
					attachToBody(l2, body, pos.x, pos.y);
				}

				if (!b.getBoo("box_body", "copy_geom") || !b.hasParam("geom")) {
					PolygonShape polygonshape = new PolygonShape();
					polygonshape.setAsBox(100,100);
					FixtureDef fixtureDef2 = new FixtureDef();
					fixtureDef2.shape = polygonshape;
					fixtureDef2.density = 0.0001f;
					fixtureDef2.friction = 1.0f;
					fixtureDef2.restitution = 0.0f; // Make it bounce a little bit
					body.createFixture(fixtureDef2);
				} else if (b.hasParam("geom")) {
					for (pParam p : b.params.all()) if (p.prop.ref.equals("geom")) {
						pParam geom = p;
						ArrayList<Vector2> point = geom.getCollecData("point", Vector2.class);
						ArrayList<Integer> faceA = geom.getCollecData("faceA", Integer.class);
						ArrayList<Integer> faceB = geom.getCollecData("faceB", Integer.class);
						ArrayList<Integer> faceC = geom.getCollecData("faceC", Integer.class);
						if (faceA.size() != faceB.size() || faceA.size() != faceC.size() || 
								faceC.size() != faceB.size()) return;

						for (int i = 0 ; i < faceA.size() ; i++) {
							int p1 = faceA.get(i), p2 = faceB.get(i), p3 = faceC.get(i);
							if (p1 < 0 || p1 >= point.size() || 
									p2 < 0 || p2 >= point.size() || 
									p3 < 0 || p3 >= point.size()) continue;
							Vector2[] pl = new Vector2[3];
							float s = b.getFlt("scale","scale");
							pl[0] = Utl.copy(point.get(p1)); 
							pl[1] = Utl.copy(point.get(p2)); 
							pl[2] = Utl.copy(point.get(p3));
							pl[0].scl(s); pl[1].scl(s); pl[2].scl(s);
							PolygonShape polygonshape = new PolygonShape();
							polygonshape.set(pl);
							FixtureDef fixtureDef2 = new FixtureDef();
							fixtureDef2.shape = polygonshape;
							fixtureDef2.density = 0.0001f;
							fixtureDef2.friction = 1.0f;
							fixtureDef2.restitution = 0.0f; // Make it bounce a little bit
							body.createFixture(fixtureDef2);
						}
					}
				}

				int bod_nb = get_free_bod_nb();
				bodys.put(""+bod_nb, body);
				b.setStr("box_body", "body_ref", ""+bod_nb);
			}});
		}


		public void init_kinematic_body(pBody b) {
			app.addEventNextFrame(new nRun(b) {public void run() {
				pBody b = (pBody)builder;
				if (b.param("box_body") == null || b.param("kinematic") == null) return;

				// First we create a body definition
				BodyDef bodyDef = new BodyDef();
				// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
				bodyDef.type = BodyType.KinematicBody;
				// Set our body's starting position in the world
				if (b.hasParam("ref") && b.getBoo("kinematic", "follow_ref"))
					bodyDef.position.set(b.getVec("ref", "pos"));
				else bodyDef.position.set(b.getVec("box_body", "pos"));
				
				// Create our body in the world using our body definition
				Body body = world.createBody(bodyDef);

				tilemap.rayHandler.transparent.add(body);

				if (b.hasParam("cone_light")) {
					
					PointLight pl = tilemap.newPointLight(120, 
							new Color(1f,0.6f,0.4f,1f), 600, 0, 0);
					attachToBody(pl, body);
					
				}

				if (b.hasParam("light")) {
					int rays = 120;
					Vector2 pos = b.getVec("light", "pos");
					float dist = b.getFlt("light", "dist");
					Color col = Utl.color(b.getInt("light", "r"), b.getInt("light", "g"), 
							b.getInt("light", "b"), b.getInt("light", "a"));
					PointLight l = tilemap.newPointLight(rays, col, dist, 0, 0);
					l.setColor(col.r,col.g,col.b,col.a);
					attachToBody(l, body, pos.x, pos.y);
					PointLight l2 = new PointLight(tilemap.groundLayer, 
							rays, col, dist, 0, 0);
					l2.setColor(col.r,col.g,col.b,col.a);
					attachToBody(l2, body, pos.x, pos.y);
				}

				if (!b.getBoo("box_body", "copy_geom") || !b.hasParam("geom")) {
					CircleShape circlenshape = new CircleShape();
					circlenshape.setRadius(b.getFlt("kinematic", "rad"));
					FixtureDef fixtureDef2 = new FixtureDef();
					fixtureDef2.shape = circlenshape;
					fixtureDef2.density = 0.0f;
					fixtureDef2.friction = 0.0f;
					fixtureDef2.restitution = 0.0f; // Make it bounce a little bit
					body.createFixture(fixtureDef2);
				} else if (b.hasParam("geom")) {
					for (pParam p : b.params.all()) if (p.prop.ref.equals("geom")) {
						pParam geom = p;
						ArrayList<Vector2> point = geom.getCollecData("point", Vector2.class);
						ArrayList<Integer> faceA = geom.getCollecData("faceA", Integer.class);
						ArrayList<Integer> faceB = geom.getCollecData("faceB", Integer.class);
						ArrayList<Integer> faceC = geom.getCollecData("faceC", Integer.class);
						if (faceA.size() != faceB.size() || faceA.size() != faceC.size() || 
								faceC.size() != faceB.size()) return;

						for (int i = 0 ; i < faceA.size() ; i++) {
							int p1 = faceA.get(i), p2 = faceB.get(i), p3 = faceC.get(i);
							if (p1 < 0 || p1 >= point.size() || 
									p2 < 0 || p2 >= point.size() || 
									p3 < 0 || p3 >= point.size()) continue;
							Vector2[] pl = new Vector2[3];
							float s = b.getFlt("scale","scale");
							pl[0] = Utl.copy(point.get(p1)); 
							pl[1] = Utl.copy(point.get(p2)); 
							pl[2] = Utl.copy(point.get(p3));
							pl[0].scl(s); pl[1].scl(s); pl[2].scl(s);
							PolygonShape polygonshape = new PolygonShape();
							polygonshape.set(pl);
							FixtureDef fixtureDef2 = new FixtureDef();
							fixtureDef2.shape = polygonshape;
							fixtureDef2.density = 0.0001f;
							fixtureDef2.friction = 1.0f;
							fixtureDef2.restitution = 0.0f; // Make it bounce a little bit
							body.createFixture(fixtureDef2);
						}
					}
				}

				int bod_nb = get_free_bod_nb();
				bodys.put(""+bod_nb, body); 
				b.setStr("box_body", "body_ref", ""+bod_nb);

			}});
			//			MouseJointDef defJoint = new MouseJointDef();
			//			defJoint.target.set(b.getVec("box_body", "pos"));
			//			
			//			MouseJoint joint = (MouseJoint)world.createJoint(defJoint); // Returns subclass Joint.
			//
			//			joints.put(""+joint_nb, joint); joint_nb++;
			//			b.setStr("kinematic", "joint_ref", ""+joint_nb);

		}

//		public void init_light_body(pBody b) {
//			app.addEventNextFrame(new nRun(b) {public void run() {
//				pBody b = (pBody)builder;
//				if (b.param("box_body") == null || b.param("light") == null) return;
//
//				Body body = bodys.get(b.getStr("box_body", "body_ref"));
//				if (body == null) {
//					// First we create a body definition
//					BodyDef bodyDef = new BodyDef();
//					// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
//					bodyDef.type = BodyType.KinematicBody;
//					// Set our body's starting position in the world
//					bodyDef.position.set(b.getVec("box_body", "pos"));
//
//					// Create our body in the world using our body definition
//					body = world.createBody(bodyDef);
//
//					if (b.hasParam("contact_break")) {
//						break_bodys.add(body);
//					}
//
//					int bod_nb = get_free_bod_nb();
//					bodys.put(""+bod_nb, body); 
//					b.setStr("box_body", "body_ref", ""+bod_nb);
//				}
//
//				int rays = 120;
//				Vector2 pos = b.getVec("light", "pos");
//				float dist = b.getFlt("light", "dist");
//				Color col = Utl.color(b.getInt("light", "r"), b.getInt("light", "g"), 
//						b.getInt("light", "b"), b.getInt("light", "a"));
//				PointLight l = tilemap.newPointLight(rays, col, dist, 0, 0);
//				l.setColor(col.r,col.g,col.b,col.a);
//				attachToBody(l, body, pos.x, pos.y);
//				PointLight l2 = new PointLight(tilemap.groundLayer, 
//						rays, col, dist, 0, 0);
//				l2.setColor(col.r,col.g,col.b,col.a);
//				attachToBody(l2, body, pos.x, pos.y);
//			}});
//		}

		public void clear_body(pBody b) {
			if (b.param("box_body") == null) return;
			Body body = bodys.get(b.getStr("box_body", "body_ref"));
			if (body == null) return;
			bodys.remove(b.getStr("box_body", "body_ref"), body);
			world.destroyBody(body);
			if (lights.get(body) != null) {
				for (Light l : lights.get(body)) l.remove();
				lights.get(body).clear();
			}
			tilemap.rayHandler.transparent.remove(body);
			break_bodys.remove(body);
			clearing_bodys.remove(body);
		}

		public void accel_body(pBody b, boolean glob, float x, float y, float max) {
			if (b.param("box_body") == null) return;
			Body body = bodys.get(b.getStr("box_body", "body_ref"));
			if (body == null) return;
			Vector2 pos = body.getPosition();
			Vector2 vel = body.getLinearVelocity();
			Vector2 a = new Vector2(x,y);
			Vector2 futur = new Vector2(vel).add(a);
			float vell = futur.len();
			if (vell > max) {
				decel_body_move(b,a.len());
				return; }
			if (!glob) a.rotateRad(body.getAngle()-(float)(Math.PI/2f));
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
			vel.scl(-1f);
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
			Vector2 pos = body.getPosition();
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
				clear_body(b);
				return;
			}
			if (b.hasParam("dynamic")) {
				Vector2 pos = body.getPosition();
				float rot = body.getAngle();
				b.setVec("box_body", "pos", pos);
				b.setFlt("box_body", "rot", rot);
				if (b.getBoo("dynamic", "ctrl_ref") && b.hasParam("ref")) {
					b.setVec("ref", "pos", pos);
					b.setFlt("ref", "rot", rot);
				}
			}
			if (b.hasParam("kinematic")) {
				if (b.getBoo("kinematic", "follow_ref") && b.hasParam("ref")) {
					Vector2 pos = b.getVec("ref", "pos");
					float rot = b.getFlt("ref", "rot");
					b.setVec("box_body", "pos", pos);
					b.setFlt("box_body", "rot", rot);
					body.setTransform(pos, rot);
				}
			}
//			if (b.hasParam("light")) {
//				if (b.getBoo("light", "follow_ref") && b.hasParam("ref")) {
//					Vector2 pos = b.getVec("ref", "pos");
//					float rot = b.getFlt("ref", "rot");
//					b.setVec("box_body", "pos", pos);
//					b.setFlt("box_body", "rot", rot);
//					body.setTransform(pos, rot);
//				}
//			}
		}


}
