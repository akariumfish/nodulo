package plane;

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
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.ScreenUtils;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer.Renderer;
import app.Applet;
import app.nMap;
import app.nPool;
import app.nRun;
import app.nDrawer.PolygonSpriteBatchRendererAdapter;

import box2dLight.*;
import data.*;
import gui.*;
import patch.*;
import patch.pMacro.Macro;
import patch.pMacro.MacroScript;

public class pBox2d extends pSystem {

	public static sBloc_Builder builder = null;
	
	public static void build(Applet app) {
		builder = builder(app, "box2d", pBox2d.class, new nRun() { public void run(Object o) {
			sValueBloc b = (sValueBloc)o; newObject(b); }});
		
		build_prop(app);
	}

	public static void dispose(Applet app) { pool.dispose(); }
	public static final nPool<pBox2d> pool = new nPool<pBox2d>() {
		protected pBox2d newObject() { return new pBox2d(); } };
	public static pBox2d newObject(sValueBloc b) {
		return pool.obtain().init(b); }

	public static void build_game(Applet app) {
		
		float RS = app.gui.book.RS;
		

		Macro b2d_tile = new Macro("b2d_tile")
		.addMacro("tile", pMacro.getMacro("executor"), 		0f, 	0f)
		.addSetVar("tile_exec", "target_ref", "b2d_move")
		;
		
		Macro main_sheet = new Macro("main_sheet_b2d")
		.addMacro("b2d_tile", b2d_tile, 	600f, 	0f)
		.addNode("from1", "from", 		0f, -60f)
		.addSetVar("this_ref", "fp1").addSetVar("target_ref", "tp1").getMacro()
		.addLink("b2d_tile_tile_exec", "co_reg", "from1", "out")
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
			app.addDelayEvent(3, new nRun() { public void run() {
//				list.get("func_p").setVar("script", true);
			}});
		}})
		;

		Macro b2d_player = new Macro("b2d_player")
		.addNode("sel_body", "sel_body", 	180f, 	60f).getMacro()
		.addNode("register", "register", 	780f, 	-300f).getMacro()
		.addNode("reg_in_bod", "reg_in", 	600f,	-60f).addSetVar("reg_ref", "body").getMacro()
		.addLink("sel_body", "co_sel_bod", "reg_in_bod", "co_in")
		.addLink("reg_in_bod", "co_reg", "register", "co_reg")
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
		}})
		;

		Macro init_space = new Macro("init_space_b2d")
		.addMacro("b2d_player", b2d_player, 	1200f, 	-150f)
		.addMacro("construct1", "constructor", 	0f, 	-150f)
		.addSetVar("construct1_const", "print_name", "b2d_print")
		.addNode("space_init", "space_init", 		0f, 120f).getMacro()
		.addNode("to1", "to", 		3000f, -150f)
		.addSetVar("this_ref", "tp1").addSetVar("target_ref", "fp1").getMacro()
		.addLink("b2d_player_register", "co_register", "to1", "in")
		.addLink("construct1_const", "co_run", "space_init", "start_run")
		.addLink("b2d_player_sel_body", "in", "construct1_const", "co_body")
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
			
			list.get("construct1_ank").setVar("view_ank", false);
			list.get("construct1_ank").setVar("ank_pos", new Vector2(0,110));
			
//			sValue v = list.get("construct1_ank").patch.plane.getSystem(pView.class)
//				.bloc.getValue("val_grid");
//			if (v != null) ((sBoo)v).set(false);
		}})
		;

		Macro b2d_blueprint = new Macro("b2d_blueprint")
		.addNode("blueprint", "blueprint", 	0f, 		0f).getMacro()
		.addNode("physic", "physic", 		-600f, 	0f)
			.addSetVar("use_ctrl_box", true).getMacro()
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

		pPlane.newStartupModel("box2d_exemple")
		.setSetupRun(new nRun() { public void run() {
			
			pSheet.setDefMacro("main", main_sheet);
			pSheet.setDefMacro("init_space", init_space);
			pSheet.setDefMacro("common_param", common_param);
			pSheet.setDefMacro("function", common_func);

			pSheet.setDefCollapse("init_space", false);
			pSheet.setDefCollapse("common_param", false);
			pSheet.setDefCollapse("main", false);
			pSheet.setDefCollapse("function", false);
		}})
		;
		
		
	}

	public static void build_prop(Applet app) {
		
		float RS = app.gui.book.RS;
		
		pProperty physic = pProperty.newGeneralProperty("physic");
//		physic
//		.addData("limit", true)
//		.addData("limit_dist", 20000f)
//		;

		physic.addBodyInitRun(new nRun() {public void run() {
			pBody bod = arg(0,pBody.class);
			pBox2d b2d = bod.space.plane.getSystem(pBox2d.class);
			if (b2d == null || bod == null) return;
			b2d.init_body(bod);
		}});

//		physic.addBodyClearRun(new nRun() {public void run() {
//			pBody bod = arg(0,pBody.class);
//			pBox2d b2d = bod.space.plane.getSystem(pBox2d.class);
//			if (b2d == null || bod == null) return;
//			b2d.clear_body(bod);
//		}});
		
		physic.newLocalProperty("coord")
		.addData("pos", new Vector2())
		.addData("rot", 0f)
		;
		physic.newLocalProperty("box_body")
		.addData("body_ref", "")
		;
		pFamily.newFamily("box_body")
		.addProp("coord")
		.addProp("box_body")
		;
		

		nRun run_ctrl_box = new nRun() { public void run(Object o) { 
			pBody bod = (pBody)o; if (bod == null) return;
			pBox2d box = bod.space.plane.getSystem(pBox2d.class);
			if (box != null && bod.hasParam("coord") && bod.hasParam("ctrl_box")) {
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

	public pBox2d() { super(); 
		tick_run = new nRun() { public void run(Object o) { tick((float)o); }};
		net_tick_run = new nRun() { public void run(Object o) { net_tick((float)o); }};
		draw_run = new nDrawable() { public void drawing() { draw(); }}; }

	nRun tick_run, net_tick_run;
	nDrawable draw_run;
	
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

		plane.storeSystemType(bloc.ref, this.getClass());

		useNetFrame();
		
		val_do_draw = bloc.obtainBoo("val_do_draw", true);
		val_draw_debug = bloc.obtainBoo("val_draw_debug", false);
		val_do_ray = bloc.obtainBoo("val_do_ray", false);
		val_do_calc = bloc.obtainBoo("val_do_calc", true);

		
		cam = new OrthographicCamera(Applet.WIDTH, Applet.HEIGHT);
		
		world = new World(new Vector2(0, 0), true);

		//boolean drawBodies, boolean drawJoints, 
//		boolean drawAABBs, boolean drawInactiveBodies, 
//		boolean drawVelocities, boolean drawContacts
		boxRenderer = new Box2DRenderer(app, true, true, true, true, true, true);
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
		buffer.initialize(Applet.WIDTH,Applet.HEIGHT);
		
		rayHandler = new RayHandler(world);
		
		rayHandler.setAmbientLight(1f, 1f, 1f, 0.6f);
	    rayHandler.setBlurNum(3);
	    rayHandler.setCulling(false);
	    rayHandler.shadowBlendFunc.set(GL20.GL_DST_COLOR, GL20.GL_SRC_COLOR);
	    rayHandler.diffuseBlendFunc.set(GL20.GL_DST_COLOR, GL20.GL_SRC_COLOR);

        int rays = 64;
	    new PointLight(rayHandler, rays, new Color(1,1,1,1), 2000, -1000, 500);
	    new PointLight(rayHandler, rays, new Color(1,1,1,1), 2000, 0, 500);
	    new PointLight(rayHandler, rays, new Color(1,1,1,1), 2000, 1000, 500);

//	    new PointLight(rayHandler, rays, new Color(1,1,1,1), 1000, 0, -500);
		
	}
	public void system_load() {

		plane.getSystem(pTime.class).addEventTick(tick_run);
		plane.getSystem(pTime.class).addEventNetTick(net_tick_run);
		plane.getSystem(pView.class).addDrawable(10,draw_run);
		space = plane.getSystem(pSpace.class);
		view = plane.getSystem(pView.class);
//		if (!app.RELEASE) 
			tool_setup(true);
		
	}
	public void system_clear() {
		if (plane.getSystem(pTime.class) != null) 
			plane.getSystem(pTime.class).removeEventTick(tick_run);
		if (plane.getSystem(pTime.class) != null) 
			plane.getSystem(pTime.class).removeEventNetTick(net_tick_run);
		if (plane.getSystem(pView.class) != null) 
			plane.getSystem(pView.class).removeDrawable(draw_run);
		
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
	public void draw() { 
		
		if (val_do_draw.get()) {

			boxRenderer.render(world);
			
			if (val_do_ray.get()) {

				app.pause_batch();

				cam.setToOrtho(false, (int)(app.getscreenwidth()), 
						(int)(app.getscreenheight()));
				Vector2 view_center = new Vector2(view.val_pos.get());
				view_center.x += view.val_view_size.x() / 2.0f;
				view_center.y -= view.val_view_size.y() / 2.0f + app.gui.book.RS;
				Vector2 m = new Vector2(view_center)
						.sub(app.getscreenwidth() / 2.0f, app.getscreenheight() / 2.0f);
				m.scl(1/view.val_cam_scale.get()).rotateRad(-view.val_cam_rot.get());
				m.add(view.val_cam_pos.get()).scl(-1f);
				cam.zoom = 1 / view.val_cam_scale.get();
				cam.position.set(m.x, m.y, 0);
				cam.direction.set(0, 0, -1f);
				Vector2 u = new Vector2(0,1).rotateRad(-view.val_cam_rot.get());
				cam.up.set(u.x, u.y, 0);
				cam.update();

				app.flush();
				for (Rectangle r : Applet.duplic(app.gui.scissors)) {
					scissors.add(r); ScissorStack.popScissors(); }
				app.gui.scissors.clear();
				
				rayHandler.setCombinedMatrix(cam.combined,
						m.x, m.y, app.getscreenwidth(), app.getscreenheight()); 
				rayHandler.update();
				rayHandler.prepareRender();

				app.flush();
				for (Rectangle r : Applet.duplic(scissors)) {
					app.gui.scissors.add(r); ScissorStack.pushScissors(r); }
				scissors.clear();

				buffer.begin();
				ScreenUtils.clear(app.buffer_clear_color);
				rayHandler.renderOnly();
				buffer.end();

		        app.drawer.spritebatch.begin();
		        app.drawer.spritebatch.draw(buffer.getTexture(), 0, 0, 
						Applet.WIDTH,Applet.HEIGHT, 
						0, 0, 1, 1);
		        app.drawer.spritebatch.end();

				if (val_draw_debug.get()) 
					debugRenderer.render(world, cam.combined);

				app.restart_batch();

			}

			if (val_draw_debug.get() && !val_do_ray.get()) {
				app.pause_batch();

				cam.setToOrtho(false, (int)(app.getscreenwidth()), 
						(int)(app.getscreenheight()));
				Vector2 view_center = new Vector2(view.val_pos.get());
				view_center.x += view.val_view_size.x() / 2.0f;
				view_center.y -= view.val_view_size.y() / 2.0f + app.gui.book.RS;
				Vector2 m = new Vector2(view_center)
						.sub(app.getscreenwidth() / 2.0f, app.getscreenheight() / 2.0f);
				m.scl(1/view.val_cam_scale.get()).rotateRad(-view.val_cam_rot.get());
				m.add(view.val_cam_pos.get()).scl(-1f);
				cam.zoom = 1 / view.val_cam_scale.get();
				cam.position.set(m.x, m.y, 0);
				cam.direction.set(0, 0, -1f);
				Vector2 u = new Vector2(0,1).rotateRad(-view.val_cam_rot.get());
				cam.up.set(u.x, u.y, 0);
				cam.update();

				debugRenderer.render(world, cam.combined);
				
				app.restart_batch();
			}

		}
	}
	
	public final ArrayList<Rectangle> scissors = new ArrayList<Rectangle>();
	

	public nMap<Body> bodys = new nMap<Body>();
	private int bod_nb = 0;
	public void init_body(pBody b) {
		if (b.param("coord") == null || b.param("box_body") == null) return;

		// First we create a body definition
		BodyDef bodyDef = new BodyDef();
		// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
		bodyDef.type = BodyType.DynamicBody;
		// Set our body's starting position in the world
		bodyDef.position.set(b.getVec("coord", "pos"));

		// Create our body in the world using our body definition
		Body body = world.createBody(bodyDef);

		PolygonShape polygonshape = new PolygonShape();
		polygonshape.setAsBox(100,100);
		FixtureDef fixtureDef2 = new FixtureDef();
		fixtureDef2.shape = polygonshape;
		fixtureDef2.density = 0.0001f;
		fixtureDef2.friction = 01.0f;
		fixtureDef2.restitution = 0.0f; // Make it bounce a little bit
		Fixture fixture2 = body.createFixture(fixtureDef2);

		bodys.put(""+bod_nb, body);
		b.setStr("box_body", "body_ref", ""+bod_nb);
		
		bod_nb++;
	}

	public void clear_body(pBody b) {
		//TODO
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
		if (b.param("box_body") == null || b.param("coord") == null) return;
		Body body = bodys.get(b.getStr("box_body", "body_ref"));
		if (body == null) return;
		Vector2 pos = body.getPosition();
		b.setVec("coord", "pos", pos);
		b.setFlt("coord", "rot", body.getAngle());
	}
	

}
