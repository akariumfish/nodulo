package plane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;

import app.Applet;
import data.*;
import gui.*;
import patch.pAnk;
import patch.pInstance;
import patch.pPatch;
import util.nMap;
import util.nPool;
import util.nRun;

public class pPlane {
	
	
	public static sBloc_Builder builder = null;
	
	public static void build(Applet app) {
		
		builder = new sBloc_Builder(app.data, "plane")
		.setSolo(true)
		.setInitRun(new nRun() { public void run(Object o) {
			sValueBloc b = (sValueBloc)o; b.addObject("plane", pPlane.newObject(b)); }})
		.setLoadRun(new nRun() { public void run(Object o) {
			sValueBloc b = (sValueBloc)o; b.object("plane", pPlane.class).init_load(); }})
		.setClearRun(new nRun() { public void run(Object o) {
			sValueBloc b = (sValueBloc)o; b.run("clearing"); }});
		
//		app.addRootBlocBuilder(builder);
		
		app.data.setting_space.addRootBlocBuilder(builder);

//		app.exec_nothrow("pSystem.build(app)", new nRun() { public void run() {	
//			pSystem.build(app);
//		}});

		app.gdx.exec_nothrow("pNet.build(app)", new nRun() { public void run() {	
			pNet.build(app);
		}});

		app.gdx.exec_nothrow("pView.build(app)", new nRun() { public void run() {	
			pView.build(app);
		}});
		app.gdx.exec_nothrow("pTime.build(app)", new nRun() { public void run() {	
			pTime.build(app);
		}});
		app.gdx.exec_nothrow("pSpace.build(app)", new nRun() { public void run() {	
			pSpace.build(app);
		}});
		
		app.gdx.exec_nothrow("pBody.build(app)", new nRun() { public void run() {	
			pBody.build(app);
		}});
		app.gdx.exec_nothrow("pProperty.build(app)", new nRun() { public void run() {	
			pProperty.build(app);
		}});
		app.gdx.exec_nothrow("pFamily.build(app)", new nRun() { public void run() {	
			pFamily.build(app);
		}});

		app.gdx.exec_nothrow("pGeom.build(app)", new nRun() { public void run() {	
			pGeom.build(app);
		}});

		app.gdx.exec_nothrow("pAtom.build(app)", new nRun() { public void run() {	
			pAtom.build(app);
		}});

		app.gdx.exec_nothrow("pBox2d.build(app)", new nRun() { public void run() {	
			pBox2d.build(app);
		}});

		app.gdx.exec_nothrow("pPatch.build(app)", new nRun() { public void run() {	
			pPatch.build(app);
		}});

		app.gdx.exec_nothrow("pTime.build_node(app)", new nRun() { public void run() {	
			pTime.build_node(app);
		}});
		app.gdx.exec_nothrow("pView.build_nodes(app)", new nRun() { public void run() {	
			pView.build_nodes(app);
		}});

		app.gdx.exec_nothrow("pBox2d.build_game(app)", new nRun() { public void run() {	
			pBox2d.build_game(app);
		}});

		app.gdx.exec_nothrow("pAtom.build_game(app)", new nRun() { public void run() {	
			pAtom.build_game(app);
		}});
 
	}
	
	public static void dispose(Applet app) {

		pool.dispose();
		
		pPatch.dispose();
		
	}

	public static final nPool<pPlane> pool = new nPool<pPlane>() {
		protected pPlane newObject() { return new pPlane(); } };

	public static pPlane newObject(sValueBloc b) {
		return pool.obtain().init(b); }
	
	
	
	
	
	
	

	public static void run_startupmodel_setup(String r) {
		if (startupmodels.get(r) == null || 
				startupmodels.get(r).setup_run == null) return;
		startupmodels.get(r).setup_run.run();
	}
	
	public static StartupModel newStartupModel(String r) {
		StartupModel sm = new StartupModel(r);
		startupmodels.put(r,sm);
		return sm;
	}
	public static nMap<StartupModel> startupmodels = new nMap<StartupModel>();
	public static class StartupModel {
		public String ref;
		public StartupModel(String r) { ref = r; }
		nRun setup_run = null;
		public StartupModel setSetupRun(nRun n) { setup_run = n; return this; }
	}

	
	
	
	
	

	public sValueBloc bloc = null;
	pPlane plan;
	public Applet app;

	public Random seed_rng;

	public boolean NET_CTRL = false;
	
	
	nRun run_frame;
	
	pPlane() {}
	
	public pPlane init(sValueBloc b) {
		bloc = b; app = Applet.app; plan = this;

		if (app.start_as_client) NET_CTRL = true;
		
		bloc.addMetode("clearing", new nRun() { public void run() { 
			bloc.object("plane", pPlane.class).clear(); }}); 

		seed_rng = new Random(123456789);
		
//		app.addDelayEvent(2, new nRun() { public void run() {			
//			bloc.run("add_menu"); 
//			if (bloc.is_new_bloc) {
//				sValueBloc menu_bloc = bloc.getBloc("blocmenu");
////				menu_bloc.getValue("val_tab_sel", sInt.class).set(1);
//				menu_bloc.getValue("val_collapse", sBoo.class).set(true);
//			}
//		}});

		run_frame = new nRun() { public void run(Object o) {
			float b = (float)o; frame(b); }};
		
		app.addRunFrame(run_frame);

		bloc.addEventSave(new nRun() { public void run() {
			nRun.runEvents(eventSaveRun);
		}});

		bloc.addEventLoadParam(new nRun() { public void run() {
			nRun.runEvents(eventEmptyRun);
			app.addDelayEvent(8, new nRun() { public void run() {
				nRun.runEvents(eventLoadRun);
			}});
		}});

		if (app.getPref("STARTUP_MODEL_USE", Boolean.class)) {
			run_startupmodel_setup(app.getPref("STARTUP_MODEL_REF", String.class)); }
		
		app.menu.add_tool_menu_trigg("Empty Plane", new nRun() { public void run() {
			empty_plane(); }});
		
		init_inputs();
		
		return this;
	}

	public void init_load() {

		app.addEventNextFrame(new nRun() { public void run() {
//			if (!app.RELEASE) 
				tool_setup();
		}});
		
	}
	
	public void empty_plane() {
		nRun.runEvents(eventEmptyRun);
	}

	ArrayList<nRun> eventToolInitRun = new ArrayList<nRun>();
	
	public pPlane addEventToolInit(nRun r) { eventToolInitRun.add(r); return this; }
	public pPlane removeEventToolInit(nRun r) { eventToolInitRun.remove(r); return this; }
	
	public void tool_init(nInterface interf) {
		
	}
	
	public void clear() {
		
		bloc.clear();

		app.removeRunFrame(run_frame);
		
		systems.clear();
		
	}
	
	public void frame(float delta) {
		
		frame_inputs();
		
		for (int prio = pSystem.max_frame_prio ; prio >= 0 ; prio--)
			for (pSystem sys : systems.all()) 
				if (sys.val_prio_frame.get() == prio) sys.do_frame(delta);
		
		nRun.runEvents(eventFrameRun);
		nRun.runEvents(eventFrameRun, delta);
	}
	
	ArrayList<nRun> eventSaveRun = new ArrayList<nRun>();
	ArrayList<nRun> eventLoadRun = new ArrayList<nRun>();
	ArrayList<nRun> eventEmptyRun = new ArrayList<nRun>();
	
	public pPlane addEventSave(nRun r) { eventSaveRun.add(r); return this; }
	public pPlane removeEventSave(nRun r) { eventSaveRun.remove(r); return this; }
	public pPlane addEventLoad(nRun r) { eventLoadRun.add(r); return this; }
	public pPlane removeEventLoad(nRun r) { eventLoadRun.remove(r); return this; }
	public pPlane addEventEmpty(nRun r) { eventEmptyRun.add(r); return this; }
	public pPlane removeEventEmpty(nRun r) { eventEmptyRun.remove(r); return this; }
	
	ArrayList<nRun> eventFrameRun = new ArrayList<nRun>();
	
	public pPlane addEventFrame(nRun r) { eventFrameRun.add(r); return this; }
	public pPlane removeEventFrame(nRun r) { eventFrameRun.remove(r); return this; }
	public pPlane clearEventFrame() { eventFrameRun.clear(); return this; }
	
	
	public nMap<pSystem> systems = new nMap<pSystem>();
	public HashMap<Class<?>, String> system_refs = new HashMap<Class<?>, String>();
	public void storeSystemType(String r, Class<?> ct) {
		if (system_refs.get(ct) == null) system_refs.put(ct,r); }
	
	public <T extends pSystem> T getSystem(Class<T> ct) { 
		String ref = system_refs.get(ct);
		if (systems.get(ref) != null) return (T)systems.get(ref); else return null; }

	
	
	
	
	
	
	

	public nMap<nRun> inputs = new nMap<nRun>();

	public nMap<nRun> outputs = new nMap<nRun>();
	
	private boolean key_up = false, key_down = false, key_left = false, 
			key_right = false, key_cw = false, key_ccw = false;
	private boolean mouse_left_state = false, mouse_left_click = false, 
			mouse_left_unclick = false, mouse_right_state = false, 
			mouse_right_click = false, mouse_right_unclick = false;
	private boolean keycross_press = false, keyrot_press = false;
	private boolean key_shift_state = false;
	
	ArrayList<KeyInput> keyInputs = new ArrayList<KeyInput>();
	
	class KeyInput {
		String input_ref, shortcut_name;
		char key;
		boolean state = false, click = false, unclick = false;
		KeyInput(String r, String s, char k) {
			keyInputs.add(this);
			input_ref = r; shortcut_name = s; key = k;
			nRun run_state = new nRun() { public void run(Object o) {
				boolean b = (boolean)o; state = b; }};
			nRun run_clic = new nRun() { public void run(Object o) {
				boolean b = (boolean)o; if (b) click = true; else unclick = true; }};
			app.menu.add_shortcut_target(shortcut_name, key, run_clic, run_state);
			inputs.put(input_ref+"_state", new nRun() { public Object get() {
				return state; }});
			inputs.put(input_ref+"_click", new nRun() { public Object get() {
				return click; }});
			inputs.put(input_ref+"_unclick", new nRun() { public Object get() {
				return unclick; }});
		}
		public void frame_inputs() {
			state = app.input.getState(key);
		}
		public void tick_end_inputs() {
			click = false; unclick = false;
		}
	}
	
	public void init_inputs() {

		nRun run_up = new nRun() { public void run(Object o) { 
			boolean b = (boolean)o; key_up = b; }};
		nRun run_down = new nRun() { public void run(Object o) { 
			boolean b = (boolean)o; key_down = b; }};
		nRun run_left = new nRun() { public void run(Object o) { 
			boolean b = (boolean)o; key_left = b; }};
		nRun run_right = new nRun() { public void run(Object o) { 
			boolean b = (boolean)o; key_right = b; }};
		nRun run_cw = new nRun() { public void run(Object o) {
			boolean b = (boolean)o; key_cw = b; }};
		nRun run_ccw = new nRun() { public void run(Object o) {
			boolean b = (boolean)o; key_ccw = b; }};

		app.menu.add_shortcut_target("Input - Up", 'W', null, run_up);
		app.menu.add_shortcut_target("Input - Down", 'S', null, run_down);
		app.menu.add_shortcut_target("Input - Left", 'A', null, run_left);
		app.menu.add_shortcut_target("Input - Right", 'D', null, run_right);
		app.menu.add_shortcut_target("Input - CW", 'E', null, run_cw);
		app.menu.add_shortcut_target("Input - CCW", 'Q', null, run_ccw);
		
		new KeyInput("key_space", "Input - Space", ' ');
		new KeyInput("key_w", "Input - W", 'Z');
		
		inputs.put("keycross_up_state", new nRun() { public Object get() {
			return key_up; }});
		inputs.put("keycross_down_state", new nRun() { public Object get() {
			return key_down; }});
		inputs.put("keycross_left_state", new nRun() { public Object get() {
			return key_left; }});
		inputs.put("keycross_right_state", new nRun() { public Object get() {
			return key_right; }});
		inputs.put("keycross_cw_state", new nRun() { public Object get() {
			return key_cw; }});
		inputs.put("keycross_ccw_state", new nRun() { public Object get() {
			return key_ccw; }});
		
		inputs.put("keycross_press", new nRun() { public Object get() {
			return keycross_press; }});
		inputs.put("keycross_dir", new nRun() { public Object get() {
			if (key_up || key_down || key_left || key_right) {
				Vector2 m = new Vector2();
				if (key_up) m.add(0,1); if (key_left) m.add(-1,0);
				if (key_down) m.add(0,-1); if (key_right) m.add(1,0);
				m.nor();
				return m;
			} else return new Vector2();
		}});
		inputs.put("keyrot_press", new nRun() { public Object get() { 
			return keyrot_press; }});
		inputs.put("keyrot_dir", new nRun() { public Object get() {
			if (key_cw || key_ccw) {
				float m = 0f;
				if (key_ccw) m += 0.1f; if (key_cw) m -= 0.1f;
				return m;
			} else return 0f;
		}});
		inputs.put("mouse_hover_view", new nRun() { public Object get() { 
			pView view = getSystem(pView.class);
			if (view == null) return false;
			return view.mouse_is_hover_view();
		}});
		inputs.put("mouse_pos", new nRun() { public Object get() {
			pView view = getSystem(pView.class);
			if (view == null || !view.mouse_is_hover_view()) return new Vector2();
			return view.mouse_in_view();
		}});
		inputs.put("mouse_left_click", new nRun() { public Object get() {
			return mouse_left_click; }});
		inputs.put("mouse_left_unclick", new nRun() { public Object get() {
			return mouse_left_unclick; }});
		inputs.put("mouse_left_state", new nRun() { public Object get() {
			return mouse_left_state; }});
		inputs.put("mouse_right_click", new nRun() { public Object get() {
			return mouse_right_click; }});
		inputs.put("mouse_right_unclick", new nRun() { public Object get() {
			return mouse_right_unclick; }});
		inputs.put("mouse_right_state", new nRun() { public Object get() {
			return mouse_right_state; }});
		inputs.put("key_shift_state", new nRun() { public Object get() {
			return key_shift_state; }});
		
		

		outputs.put("cam_pos", new nRun() { public void run() { 
			if (args.length < 1) return;
			Vector2 r = arg(0,Vector2.class); 
			if (r == null) return;
			pView view = getSystem(pView.class);
			view.val_cam_pos_target.set(-r.x,-r.y);
			view.got_cam_pos_target = true; 
		}});
		outputs.put("cam_scale", new nRun() { public void run() {
			if (args.length < 1) return;
			float r = arg(0,Float.class); 
			pView view = getSystem(pView.class);
			view.val_cam_scale_target.set(r);
			view.got_cam_scale_target = true;
		}});
		outputs.put("cam_rot", new nRun() { public void run() {
			if (args.length < 1) return;
			float r = arg(0,Float.class); 
			pView view = getSystem(pView.class);
			view.val_cam_rot_target.set(-r);
			view.got_cam_rot_target = true;
		}});
		
		
	}

	private void frame_inputs() {
		mouse_left_state = app.input.mouseLeft.state; 
		mouse_left_click = app.input.mouseLeft.trigClick; 
		mouse_left_unclick = app.input.mouseLeft.trigUClick; 
		mouse_right_state = app.input.mouseRight.state; 
		mouse_right_click = app.input.mouseRight.trigClick; 
		mouse_right_unclick = app.input.mouseRight.trigUClick;
		key_shift_state = app.input.keyShift.state; 
		keycross_press = (key_up || key_down || key_left || key_right); 
		keyrot_press = (key_cw || key_ccw);
		for (KeyInput k : keyInputs) k.frame_inputs();
	}
	public void tick_end_inputs() {
		mouse_left_click = false; mouse_left_unclick = false; 
		mouse_right_click = false; mouse_right_unclick = false;
		for (KeyInput k : keyInputs) k.tick_end_inputs();
	}

	
	
	
	

	
	
	
	
	

	public void tool_setup() {
		
		app.addDelayEvent(1, new nRun() { public void run() {
			nWidgetGroup sec = app.menu.toolbox
					.addSection("  pPlane  ", true);;
			nInterface interf = app.gui.addInterface();
			interf.pop(sec);
			interf.setContext(bloc);
			tool_init(interf);
			nRun.runEvents(eventToolInitRun, interf);
		}});
		
//		app.addDelayEvent(1, new nRun() { public void run() {
//			nInterface interf = app.gui.addInterface();
//			nWidget w = app.gui.addWidget();
//			interf.pop(w);
//			
////			interf.cmd_context(bloc.adress);
//			tool_init(interf);
//			nRun.runEvents(eventToolInitRun, interf);
//			
//			nInterfModel interf_model = interf.create_model("tool");
//			sTab val_interf_model_default = 
//					bloc.obtainTab("val_quickt_interf_model_default");
//			if (val_interf_model_default != null) {
//				interf_model.save_to(val_interf_model_default);}
//			
//			bloc.run("add_menu"); 
//			
//			app.addDelayEvent(1, new nRun() { public void run() {			
//				sTab val_interf_model = 
//						bloc.getValue("val_quickt_interf_model", sTab.class);
//				if (val_interf_model != null && val_interf_model.width() <= 1) {
//					interf_model.save_to(val_interf_model);
//					val_interf_model.doChange(); }
//				w.clear();
//			}}); 
//		}});
	}
	
	
	
	
	
	
}
