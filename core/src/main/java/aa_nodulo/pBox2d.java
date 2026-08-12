package aa_nodulo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.ScreenUtils;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer.Renderer;
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



		public static void build_setup() {

			PlaneApplet.newStartupModel("box2d_exemple")
			.setSetupRun(new nRun() { public void run() {

				pSheet.setDefMacro("main", "main_sheet_b2d");
				pSheet.setDefMacro("blueprint", "common_param_b2d");
				pSheet.setDefMacro("function", "common_func_b2d");

				pSheet.setDefCollapse("blueprint", false);
				pSheet.setDefCollapse("main", false);
				pSheet.setDefCollapse("function", false);
			}})
			;

		}


		private static boolean has_build = false;

		public static void build_game() {
			if (has_build) return;
			has_build = true;

			float RS = nGUI.book.RS;

			
			Macro main_sheet = new Macro("main_sheet_b2d")
			.addMacro("exac", pMacro.getMacro("exec_actor"), 		0f,	0f)
			.addSetVar("exac_exec", "target_ref", "b2d_move")
			.addSetVar("exac_actor", "pop_pos", new Vector2(0,0)) 
			.addSetVar("exac_actor", "print_name", "b2d_print")
			.addRun(new nRun() { public void run() {
				nMap<pInstance> list = arg(0, nMap.class);

			}})
			;

			new MacroScript("b2d_move") 

			.com("add_set_param", "ctrl_box", "accel_left")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_get_input_at", "data", "keycross_left_state")
			.com("add_set_param", "ctrl_box", "accel_right")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_get_input_at", "data", "keycross_right_state")
			.com("add_set_param", "ctrl_box", "accel_up")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_get_input_at", "data", "keycross_up_state")
			.com("add_set_param", "ctrl_box", "accel_down")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_get_input_at", "data", "keycross_down_state")
			.com("add_set_param", "ctrl_box", "accel_cw")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_get_input_at", "data", "keycross_cw_state")
			.com("add_set_param", "ctrl_box", "accel_ccw")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_get_input_at", "data", "keycross_ccw_state")

			.com("add_set_output", "cam_pos")
			.com("add_branch_at", "data", "get_body_param")
			.com("add_arr", "arg", "coord", "pos").com("get_last")

			.com("add_set_output", "cam_rot")
			.com("add_branch_at", "data", "get_body_param")
			.com("add_arr", "arg", "coord", "rot").com("get_last")

			;

			Macro common_func = new Macro("common_func_b2d")
					.addNode("set_body_param", "function", 	-1500f,	-600f)
					.addSetVar("func_ref", "set_body_param").addTileScript("set_body_param").getMacro()
					.addNode("get_body_param", "branch", 	0f,		-600f)
					.addSetVar("branch_ref", "get_body_param").addTileScript("get_body_param").getMacro()
					.addNode("func_p", "function", 		1200f, 	0f).addSetVar("func_ref", "b2d_move").getMacro()
					.addTileScript("func_p", "b2d_move")
					.addRun(new nRun() { public void run() {
						nMap<pInstance> list = arg(0, nMap.class);
						App.ap.addDelayEvent(3, new nRun() { public void run() {
							//				list.get("func_p").setVar("script", true);
						}});
					}})
					;

			Macro b2d_blueprint = new Macro("b2d_blueprint")
					.addNode("blueprint", "blueprint", 	0f, 		0f).getMacro()
					.addNode("physic", "physic", 		-600f, 	0f)
					.addSetVar("use_ctrl_box", true).addSetVar("use_dynamic", true).getMacro()
					.addNode("ownable", "ownable", 		-600f, 	-600f)
					.addSetVar("acquire", true).getMacro()
					.addLink("physic", "param", "blueprint", "param_in")
					.addLink("ownable", "param", "blueprint", "param_in")
					.addRun(new nRun() { public void run() {
						nMap<pInstance> list = arg(0, nMap.class);

						list.get("blueprint").setVar("name", "b2d_print");

					}})
					;

			Macro common_param = new Macro("common_param_b2d")
					.addMacro("b2d_blueprint", b2d_blueprint, 0f, 0f)
					.addRun(new nRun() { public void run() {
						nMap<pInstance> list = arg(0, nMap.class);
					}})
					;



		}



		public static void build_prop() {

			float RS = nGUI.book.RS;

			pProperty physic = pProperty.newGeneralProperty("physic");
			
			
			physic.addBodyInitRun(new nRun() {public void run() {
				pBody bod = arg(0,pBody.class);
				pBox2d b2d = PlaneApplet.app.getSystem(pBox2d.class);
				if (b2d == null || bod == null) return;
				if (bod.hasParam("dynamic")) b2d.init_dyna_body(bod);
				else if (bod.hasParam("kinematic")) b2d.init_obscure_body(bod);
				else if (bod.hasParam("light")) b2d.init_light_body(bod);
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
			.addData("dist", 120f)
			.addData("r", (int)255)
			.addData("g", (int)10)
			.addData("b", (int)10)
			.addData("a", (int)255)
			;

			physic.newOptionalLocalProperty("cone_light")
			;
			
			
			
			
			nRun run_ctrl_box = new nRun() { public void run(Object o) { 
				pBody bod = (pBody)o; if (bod == null) return;
				pBox2d box = PlaneApplet.app.getSystem(pBox2d.class);
				if (box != null && bod.hasParam("box_body") && 
						bod.hasParam("ctrl_box")) {
					float accel = bod.getFlt("ctrl_box","accel_strength");
					if (bod.getBoo("ctrl_box","accel_up")) {
						box.accel_body(bod,0,accel); }
					if (bod.getBoo("ctrl_box","accel_down")) {
						box.accel_body(bod,0,-accel); }
					if (bod.getBoo("ctrl_box","accel_left")) {
						box.accel_body(bod,-accel,0); }
					if (bod.getBoo("ctrl_box","accel_right")) {
						box.accel_body(bod,accel,0); }
					if (bod.getBoo("ctrl_box","accel_cw")) {
						box.rot_body(bod,-accel); }
					if (bod.getBoo("ctrl_box","accel_ccw")) {
						box.rot_body(bod,accel); }
				}
			}};

			pGeom.newControlProp("box",physic,run_ctrl_box) 
			.setFullSync()
			.addData("accel_up", false)
			.addData("accel_down", false)
			.addData("accel_left", false)
			.addData("accel_right", false)
			.addData("accel_cw", false)
			.addData("accel_ccw", false)
			.addData("accel_strength", 1f)
			;



		}

		public pBox2d() { 
			super(); 
			tick_run = new nRun() { public void run(Object o) { tick((float)o); }};
			net_tick_run = new nRun() { public void run(Object o) { net_tick((float)o); }};
			draw_run = new nDrawable() { public void drawing() { draw(); }}; 
			pre_draw_run = new nDrawable() { public void drawing() { pre_draw(); }}; 
			post_draw_run = new nDrawable() { public void drawing() { post_draw(); }}; 
			draw_ray_run = new nDrawable() { public void drawing() { draw_ray(); }}; 
			draw_debug_run = new nDrawable() { public void drawing() { draw_debug(); }}; 
		}

		nRun tick_run, net_tick_run;
		nDrawable draw_run, pre_draw_run, post_draw_run, draw_ray_run, draw_debug_run;

		OrthographicCamera cam;

		public pBox2d init(sValueBloc b) { return (pBox2d) super.init(b); }

		public pSpace space;

		public sBoo val_do_draw, val_draw_debug, val_do_ray, val_do_calc;

		public World world;
		public RayHandler rayHandler;
		public Box2DRenderer boxRenderer;
		public Box2DDebugRenderer debugRenderer;
		private VfxFrameBuffer buffer;

		pView view;

		public void system_init() {
			bloc.addObject("box2d", this);

			app.storeSystemType(bloc.ref, this.getClass());

			useNetFrame();

			val_do_draw = bloc.obtainBoo("val_do_draw", false);
			val_draw_debug = bloc.obtainBoo("val_draw_debug", false);
			val_do_ray = bloc.obtainBoo("val_do_ray", true);
			val_do_calc = bloc.obtainBoo("val_do_calc", true);

			cam = new OrthographicCamera(GdxApp.WIDTH, GdxApp.HEIGHT);

			world = new World(new Vector2(0, 0), true);

			//boolean drawBodies, boolean drawJoints, 
			//		boolean drawAABBs, boolean drawInactiveBodies, 
			//		boolean drawVelocities, boolean drawContacts
			boxRenderer = new Box2DRenderer(app, true, true, false, true, true, true);
			debugRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);


			//		// Create our body definition
			//		BodyDef groundBodyDef = new BodyDef();  
			//		// Set its world position
			//		groundBodyDef.position.set(new Vector2(0, -10));  
			//
			//		// Create a body from the definition and add it to the world
			//		Body groundBody = world.createBody(groundBodyDef);  
			//
			//		// Create a polygon shape
			//		PolygonShape groundBox = new PolygonShape();  
			//		// Set the polygon shape as a box which is twice the size of our view port and 20 high
			//		// (setAsBox takes half-width and half-height as arguments)
			//		groundBox.setAsBox(app.camera.viewportWidth * 2f, 10.0f);
			//		// Create a fixture from our polygon shape and add it to our ground body  
			//		groundBody.createFixture(groundBox, 0.0f);
			//		// Clean up after ourselves
			//		groundBox.dispose();





			buffer = new VfxFrameBuffer(Pixmap.Format.RGBA8888);		        
			//		Renderer batchRenderer = new PolygonSpriteBatchRendererAdapter(spritebatch);
			//		buffer.addRenderer(batchRenderer);
			buffer.initialize((int)app.gdx.getscreenwidth(),
					(int)app.gdx.getscreenheight());


			app.gdx.addEventScreen(new nRun() { public void run() {
				buffer.reset();
				buffer.initialize((int)app.gdx.getscreenwidth(),
						(int)app.gdx.getscreenheight());
			}});
			
//			RayHandler.useDiffuseLight(true);
			
			rayHandler = new RayHandler(world);

			rayHandler.setAmbientLight(1f, 1f, 1f, 0f);
			rayHandler.setBlurNum(5);
			rayHandler.setCulling(false);
			rayHandler.setBlur(true);
			
			rayHandler.shadowBlendFunc.set(GL20.GL_SRC_ALPHA, 
					GL20.GL_ONE);

//			rayHandler.diffuseBlendFunc.set(GL20.GL_SRC_COLOR, 
//					GL20.GL_ONE);

			int rays = 180;
			float dist = 3000f;
			float spc = dist * 0.9f;
			pGeom geo = app.getSystem(pGeom.class);
			float lim = geo.val_limit_dist.get();
			Color lc = new Color(1,1,1,0.6f);
			new PointLight(rayHandler, rays, lc, dist, 0, 0);
			for (float x = spc ; x <= lim ; x += spc) 
					if (x < lim - dist) {
						float f = 1.2f * ((lim - dist)-x) / (lim - dist);
						new PointLight(rayHandler, rays, lc, f*dist, x, 0);
						new PointLight(rayHandler, rays, lc, f*dist, 0, x);
						new PointLight(rayHandler, rays, lc, f*dist, -x, 0);
						new PointLight(rayHandler, rays, lc, f*dist, 0, -x);
			}
			for (float x = spc ; x <= lim ; x += spc) 
				for (float y = spc ; y <= lim ; y += spc) {
					float l = new Vector2(x,y).len();
					if (l < lim - dist) {
						float f = 1.2f * ((lim - dist)-l) / (lim - dist);
						new PointLight(rayHandler, rays, lc, f*dist, x, y);
						new PointLight(rayHandler, rays, lc, f*dist, -x, y);
						new PointLight(rayHandler, rays, lc, f*dist, x, -y);
						new PointLight(rayHandler, rays, lc, f*dist, -x, -y);
					}
				}
		}
		public void system_load() {

			app.time.addEventTick(tick_run);
			app.time.addEventNetTick(net_tick_run);
			app.view.addDrawable(6,draw_run);
			app.view.addPreDrawable(0,pre_draw_run);
			app.view.addPostDrawable(22,post_draw_run);
			app.view.addDrawable(11,draw_ray_run);
			app.view.addDrawable(19,draw_debug_run);
			space = app.space;
			view = app.view;
			//		if (!app.RELEASE) 
			tool_setup(false);

		}
		public void system_clear() {
			app.time.removeEventTick(tick_run);
			app.time.removeEventNetTick(net_tick_run);
			app.view.removeDrawable(draw_run);
			app.view.removeDrawable(pre_draw_run);
			app.view.removeDrawable(post_draw_run);
			app.view.removeDrawable(draw_ray_run);
			app.view.removeDrawable(draw_debug_run);

			rayHandler.dispose();
		}

		public void tool_init(nInterface interf) {

			interf.setContext(bloc);
			interf.add_row();
			interf.add_row_switch_boo(4, "draw", "val_do_draw");
			interf.add_row_label(2, "");
			interf.add_row_switch_boo(4, "calc", "val_do_calc");
			interf.add_row();
			interf.add_row_switch_boo(4, "debug", "val_draw_debug");
			interf.add_row_label(2, "");
			interf.add_row_switch_boo(4, "ray", "val_do_ray");

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
		
		public void draw() { 

			if (val_do_draw.get()) {
				boxRenderer.render(world);
			}
		}
		public void pre_draw() { 

			if (val_do_ray.get() && app.gdx.drawer.USE_FX) {

				app.gdx.drawer.pause_batch();
				
				buffer.begin(); 
				
				ScreenUtils.clear(app.gdx.drawer.buffer_clear_color);
				
//				Color c = app.gdx.drawer.buffer_clear_color;
//		        Gdx.gl.glClearColor(c.r,c.g,c.b,c.a);
//		        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//		        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
//		        Gdx.gl20.glEnable(GL20.GL_BLEND);
		        
				app.gdx.drawer.restart_batch();
				
			}
		}
		public void draw_ray() { 

			if (val_do_ray.get() && app.gdx.drawer.USE_FX) {

				app.gdx.drawer.pause_batch();
				
//		        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
				
		        buffer.end();
		        
				cam.setToOrtho(false, (int)(app.gdx.getscreenwidth()), 
						(int)(app.gdx.getscreenheight()));
				Vector2 view_center = new Vector2(view.val_pos.get());
				view_center.x += view.val_view_size.x() / 2.0f;
				view_center.y -= view.val_view_size.y() / 2.0f + nGUI.book.RS;
				float scale = view.val_cam_scale.get();
				float sclinv = 1f / scale;
				Vector2 m = new Vector2(view_center)
						.sub(app.gdx.getscreenwidth() / 2.0f, app.gdx.getscreenheight() / 2.0f);
				m.scl(sclinv).rotateRad(-view.val_cam_rot.get());
				m.add(view.val_cam_pos.get()).scl(-1f);
				cam.zoom = sclinv;
				cam.position.set(m.x, m.y, 0f);
				cam.direction.set(0f, 0f, -1f);
				Vector2 u = new Vector2(0f,1f).rotateRad(-view.val_cam_rot.get());
				cam.up.set(u.x, u.y, 0f);
				cam.update();

//				app.gdx.drawer.flush();
				for (Rectangle r : Utl.duplic(app.gui.scissors)) {
					scissors.add(r); ScissorStack.popScissors(); }
				app.gui.scissors.clear();

				rayHandler.setCombinedMatrix(cam.combined,
						m.x, m.y, app.gdx.getscreenwidth(), app.gdx.getscreenheight()); 
				rayHandler.update();
				rayHandler.prepareRender();

//				app.gdx.drawer.flush();
				for (Rectangle r : Utl.duplic(scissors)) {
					app.gui.scissors.add(r); ScissorStack.pushScissors(r); }
				scissors.clear();

				buffer.begin(); 
				
				rayHandler.renderOnly();
				
		        buffer.end();

				app.gdx.drawer.spritebatch.begin();
				
				app.gdx.drawer.spritebatch.draw(buffer.getTexture(), 0, 0, 
						app.gdx.getscreenwidth(), 
						app.gdx.getscreenheight(), 
						0, 0, 1, 1);
				app.gdx.drawer.spritebatch.end();

				if (val_draw_debug.get()) 
					debugRenderer.render(world, cam.combined);
				
				app.gdx.drawer.spritebatch.begin();
				
			}
		}
		public void post_draw() { 
			
		}
		public void draw_debug() { 

			if (val_draw_debug.get()) {
				app.gdx.drawer.end();

				cam.setToOrtho(false, (int)(app.gdx.getscreenwidth()), 
						(int)(app.gdx.getscreenheight()));
				Vector2 view_center = new Vector2(view.val_pos.get());
				view_center.x += view.val_view_size.x() / 2.0f;
				view_center.y -= view.val_view_size.y() / 2.0f + app.gui.book.RS;
				Vector2 m = new Vector2(view_center)
						.sub(app.gdx.getscreenwidth() / 2.0f, app.gdx.getscreenheight() / 2.0f);
				m.scl(1/view.val_cam_scale.get()).rotateRad(-view.val_cam_rot.get());
				m.add(view.val_cam_pos.get()).scl(-1f);
				cam.zoom = 1 / view.val_cam_scale.get();
				cam.position.set(m.x, m.y, 0);
				cam.direction.set(0, 0, -1f);
				Vector2 u = new Vector2(0,1).rotateRad(-view.val_cam_rot.get());
				cam.up.set(u.x, u.y, 0);
				cam.update();

				debugRenderer.render(world, cam.combined);

				app.gdx.drawer.begin();
			}
		}


		public nMap<Body> bodys = new nMap<Body>();
		private int get_free_bod_nb() {
			int n = 0; while (bodys.get(""+n) != null) n++; return n; }
		
		public nMap<Joint> joints = new nMap<Joint>();
//		private int joint_nb = 0;
		public void init_dyna_body(pBody b) {
			app.addEventNextFrame(new nRun(b) {public void run() {
				pBody b = (pBody)builder;
				if (b.param("box_body") == null || b.param("dynamic") == null) return;
	
				// First we create a body definition
				BodyDef bodyDef = new BodyDef();
				// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
				bodyDef.type = BodyType.DynamicBody;
				// Set our body's starting position in the world
				bodyDef.position.set(b.getVec("box_body", "pos"));
	
				// Create our body in the world using our body definition
				Body body = world.createBody(bodyDef);
				
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
		

		public void init_obscure_body(pBody b) {
			app.addEventNextFrame(new nRun(b) {public void run() {
				pBody b = (pBody)builder;
				if (b.param("box_body") == null || b.param("kinematic") == null) return;
	
				// First we create a body definition
				BodyDef bodyDef = new BodyDef();
				// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
				bodyDef.type = BodyType.KinematicBody;
				// Set our body's starting position in the world
				bodyDef.position.set(b.getVec("box_body", "pos"));
	
				// Create our body in the world using our body definition
				Body body = world.createBody(bodyDef);
				
				if (b.hasParam("cone_light")) {
					ConeLight coneLight = new ConeLight(rayHandler, 
					120, new Color(1f,0.6f,0.4f,0.6f), 2400f, 0f, 0f, 0f, 45f);
					coneLight.attachToBody(body, 0f, 0f);
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

		public void init_light_body(pBody b) {
			app.addEventNextFrame(new nRun(b) {public void run() {
				pBody b = (pBody)builder;
				if (b.param("box_body") == null || b.param("light") == null) return;

				Body body = bodys.get(b.getStr("box_body", "body_ref"));
				if (body == null) {
					// First we create a body definition
					BodyDef bodyDef = new BodyDef();
					// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
					bodyDef.type = BodyType.KinematicBody;
					// Set our body's starting position in the world
					bodyDef.position.set(b.getVec("box_body", "pos"));
		
					// Create our body in the world using our body definition
					body = world.createBody(bodyDef);
		
					int bod_nb = get_free_bod_nb();
					bodys.put(""+bod_nb, body); 
					b.setStr("box_body", "body_ref", ""+bod_nb);
				}
				
				int rays = 50;
				Vector2 pos = b.getVec("light", "pos");
				float dist = b.getFlt("light", "dist");
				Color col = Utl.color(b.getInt("light", "r"), b.getInt("light", "g"), 
						b.getInt("light", "b"), b.getInt("light", "a"));
				PointLight l = new PointLight(rayHandler, rays, col, dist, 0, 0);
				l.setColor(col.r,col.g,col.b,col.a);
				l.attachToBody(body, pos.x, pos.y);
			}});
		}

		public void clear_body(pBody b) {
			if (b.param("box_body") == null) return;
			Body body = bodys.get(b.getStr("box_body", "body_ref"));
			if (body == null) return;
			bodys.remove(b.getStr("box_body", "body_ref"), body);
			world.destroyBody(body);
		}

		public void accel_body(pBody b, float x, float y) {
			if (b.param("box_body") == null) return;
			Body body = bodys.get(b.getStr("box_body", "body_ref"));
			if (body == null) return;
			Vector2 pos = body.getPosition();
			Vector2 a = new Vector2(x,y);
			a.rotateRad(body.getAngle());
			body.applyLinearImpulse(a.x, a.y, pos.x, pos.y, true);
		}

		public void rot_body(pBody b, float r) {
			if (b.param("box_body") == null) return;
			Body body = bodys.get(b.getStr("box_body", "body_ref"));
			if (body == null) return;
			Vector2 pos = body.getPosition();
			body.applyLinearImpulse(0, r, pos.x+50f, pos.y, true);
		}

		public void update_body(pBody b) {
			if (b.param("box_body") == null) return;
			Body body = bodys.get(b.getStr("box_body", "body_ref"));
			if (body == null) return;
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
			if (b.hasParam("light")) {
				if (b.getBoo("light", "follow_ref") && b.hasParam("ref")) {
					Vector2 pos = b.getVec("ref", "pos");
					float rot = b.getFlt("ref", "rot");
					b.setVec("box_body", "pos", pos);
					b.setFlt("box_body", "rot", rot);
					body.setTransform(pos, rot);
				}
			}
		}


}
