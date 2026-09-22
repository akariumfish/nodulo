package aa_nodulo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.noodle.nodulo.GdxApp;
import com.noodle.nodulo.Main;

import app.App;
import app.AppConfig;
import box2d.pBox2d;
import data.sValueBloc;
import gui.nGUI;
import gui.nGUIBook;
import gui.nInterface;
import gui.nWidgetGroup;
import patch.pPatch;
import patch.pStandard;
import util.Utl;
import util.nMap;
import util.nPainting;
import util.nRun;
import util.nScripted;

public class PlaneApplet extends App {


	public static boolean OPENGLES3 = true; 
//	public static boolean OPENGLES3 = false;

//	public static boolean NETWORK = true; 
	public static boolean NETWORK = false;

//	public static boolean TITLE_SCREEN = true; 
	public static boolean TITLE_SCREEN = false;

//	public static boolean START_FULLSCREEN = true;
	public static boolean START_FULLSCREEN = false;

//	public static boolean RELEASE = true;
	public static boolean RELEASE = false;

//	public static boolean BLOCK_NDRAWER_FX = true;
	public static boolean BLOCK_NDRAWER_FX = false;

//	public static boolean USE_STEAM = true;
	public static boolean USE_STEAM = false;

//	public static boolean USE_GLPROFILER = true;
	public static boolean USE_GLPROFILER = false;

//	public static boolean PROFILER_FOCUS_VIEW = true;
	public static boolean PROFILER_FOCUS_VIEW = false;
	
//	public static boolean CATCH_THROW = true;
	public static boolean CATCH_THROW = false;

//	public static boolean PRINT_TIMETRACK = true;
	public static boolean PRINT_TIMETRACK = false;
	
	
	public static class AppletConfig {

		public AppletConfig() {}
		public AppletConfig(String s, String filename, boolean dark_theme) { 
			RELEASE = !dark_theme;
			STARTUP_MODEL_REF = s; 
			PATCH_BUILD = true;
			STARTUP_NEW_FILE = filename;
		}
		public AppletConfig(boolean client) {
			start_solo = false;
			start_as_server = !client;
			start_as_client = client;
		}
		public AppletConfig(boolean startup_load, String s, boolean dark_theme) { 
			RELEASE = !dark_theme;
			PATCH_BUILD = false;
			STARTUP_LOAD = true;
			STARTUP_LOAD_FILE = s; 
		}
		
		public boolean RELEASE = PlaneApplet.RELEASE;

//		public boolean STARTUP_LOAD = true;
		public boolean STARTUP_LOAD = false;

		public boolean PATCH_BUILD = true;
//		public boolean PATCH_BUILD = false; 

//		public boolean AUTO_CONNECT = true;
		public boolean AUTO_CONNECT = false;
		
		public boolean START_FX = true;
//		public boolean START_FX = false;
		
//		public boolean START_HELP = true;
		public boolean START_HELP = false; 

		public String STARTUP_MODEL_REF = "exemple";
//		public String STARTUP_MODEL_REF = "";

		public String STARTUP_MAP_PATH = "map.tmx";
//		public String STARTUP_MAP_PATH = "";

		public String STARTUP_LOAD_FILE = "";
		public String STARTUP_NEW_FILE = ""; 

		public boolean VIEW_START_WALLPAPER = true;
		public boolean VIEW_START_COLLAPSED = false;
		public boolean VIEW_START_GRID = false;
//		public float DEF_VIEW_ZOOM = 0.07f;
		public float DEF_VIEW_ZOOM = 0.25f;
		public Vector2 DEF_VIEW_POS = new Vector2(0f,0f);
		// DEFAULT
		public Vector2 DEF_VIEW_WIN_POS = new Vector2(370f,425f);
		public Vector2 DEF_VIEW_WIN_SZ = new Vector2(910f,370f);
		// SMALL
//		public Vector2 DEF_VIEW_WIN_POS = new Vector2(870f,425f);
//		public Vector2 DEF_VIEW_WIN_SZ = new Vector2(410f,370f);
		// BIG
//		public Vector2 DEF_VIEW_WIN_POS = new Vector2(370f,900f);
//		public Vector2 DEF_VIEW_WIN_SZ = new Vector2(910f,770f);
		public boolean PATCH_START_WALLPAPER = false;
		public boolean PATCH_START_COLLAPSED = true;
		public boolean PATCH_START_GRID = false;
		public float DEF_PATCH_ZOOM = 0.1f;
		public Vector2 DEF_PATCH_POS = new Vector2(0f,0f);
		public Vector2 DEF_PATCH_WIN_POS = new Vector2(370f,915f);
		public Vector2 DEF_PATCH_WIN_SZ = new Vector2(910f,450f);
//		public boolean PATCH_TOOL_AUTOCOLLAPSE = true;
		public boolean PATCH_SHEET_COLLAPSE = true;
		public boolean TOOLBOX_OPEN = true;
		public boolean DRAW_GROUND = true;
		public boolean DRAW_FOG = true;
		public boolean DRAW_VISION = true;

		public boolean POP_BODY_EDITOR = false;

		public boolean SCRIPT_ALL_FUNC = true;

		public boolean AVATAR_VIEW_MODE = false; //false = def
		public boolean AVATAR_CAM = true;
		
		public float DEF_TICK_BY_SEC = 60f;
		
		public boolean start_solo = true;
		public boolean start_as_server = false;
		public boolean start_as_client = false;

		public String player_ref = "player";

	}

	
	public static GdxApp make(Main m, AppConfig c) {
		return new GdxApp(m, c, new PlaneApplet(new AppletConfig())); }
	
	public static GdxApp make(Main m, AppConfig c, AppletConfig c2) {
		return new GdxApp(m, c, new PlaneApplet(c2)); }
	
	
	public PlaneApplet(AppletConfig c) { 
		config = c; RELEASE = c.RELEASE; app = this; Utl.plane = this; Utl.conf = c;
	}
	
	
	public static String[] getModels() {
		String[] l = new String[startupmodels.size()+1];
		l[0] = "empty";
		int i = 1;
		for (String s : startupmodels.allKey()) { l[i] = s; i++; }
		return l;
	}
	
	
	public static PlaneApplet app;
	
	public AppletConfig config;
	
	public nMenu menu;
	
	public pView view;
	public pTime time;
	public pPatch patch;
	public pSpace space;
	public pNet net;
	
	public pBox2d box;
	
//	public pTerm term;
//	
//	public Plane plane;

	public boolean NET_CTRL = false;
	
//	nPainting paint;
	
	public sValueBloc bloc;
	
	@Override
	public void setInputProcessor() {
//		if (term != null) term.setInputProcessor();
//		else 
			Gdx.input.setInputProcessor(input);
	}
	
	/* static construct :
	 * 
	 * static {
	 * 		...
	 * }
	 * */
	
	public static void build_setup() {
		pPatch.build_setup();

	}
	

	private static boolean has_build_statics = false;
	private static void build_help() {
		if (has_build_statics) return;
		nGUI.newHelp("help_1", "txt1")
		.text(" txt2")
		.line()
		.text("txt3")
		.link("help 2", "help_2")
		.text("txt4")
		.line()
		;
		
		nGUI.newHelp("help_2", "txt5")
		.line()
		.text("txt6")
		.text("txt7")
		.line()
		;
		has_build_statics = true;
	}
	
	
	@Override
	public void setup(GdxApp a) {
		super.setup(a);

		if (config.start_as_client) NET_CTRL = true;
		use_fx(config.START_FX); gui.val_fx.set(config.START_FX);
		
		if (config.STARTUP_LOAD) 
			data.val_root_savepath.set(config.STARTUP_LOAD_FILE);
		else if (TITLE_SCREEN) data.val_root_savepath.set(config.STARTUP_NEW_FILE);
		
		bloc = data.root_bloc;
		
		menu = new nMenu(this);
		
		
		build_help();
		
		init_inputs();
		
		
		gdx.exec_nothrow("pPatch.build(app)", new nRun() { public void run() {	
			pPatch.build(data, gui); }});

		
		bloc.addEventSave(new nRun() { public void run() {
			nRun.runEvents(eventSaveRun);
		}});

		bloc.addEventLoadParam(new nRun() { public void run() {
			nRun.runEvents(eventEmptyRun);
			app.addDelayEvent(8, new nRun() { public void run() {
				nRun.runEvents(eventLoadRun);
			}});
		}});

		if (config.PATCH_BUILD) {
			run_startupmodel_setup(config.STARTUP_MODEL_REF); }

//		addDelayEvent(2, new nRun() { public void run() {
//			if (!RELEASE) 
				tool_setup(true);
//		}});
		
		view = new pView(this);
		time = new pTime(this);
		patch = new pPatch(this);
		net = new pNet(this);
		space = new pSpace(this);
//		term = new pTerm(this);
//		
//		plane = new Plane(this);

		view.system_load();
		time.system_load();
		net.system_load();
		space.system_load();
		patch.system_load();
//		term.system_load();
//		
//		plane.finish();

//		paint = new nPainting();
//		
//		paint.rect(300,380,480,250);
//		paint.rect(150,300,300,250);
//		paint.stroke(255,255,0,255,8f);
//		paint.rect(300,250,350,300);
//		paint.rect(180,0,200,380);
//		
//		Object[] scr = paint.getScript();
//		
//		paint.clear();
//		
//		nScripted.buildScript(paint, scr);
//		
//		Utl.logn(nScripted.buildCodeFromScript(nPainting.class, scr));
		
		if (config.STARTUP_LOAD) addEventInitEnd(new nRun() { public void run(Object o) {
			sValueBloc b = (sValueBloc)o; data.full_load(); }});
		
		startup();

	}

	@Override
	public void closing() {
		super.closing();

//		plane.dispose();

		app = null; 
	}
	
	@Override 
	protected void gui_frame() { 
		float delta = Gdx.graphics.getDeltaTime();
		
		frame_inputs();

//		plane.frame_start(delta);
		
		net.frame(delta);
		time.do_frame(delta);
		patch.frame(delta);
		
		for (int prio = pSystem.max_frame_prio ; prio >= 0 ; prio--)
			for (pSystem sys : systems.all()) 
				if (sys.val_prio_frame.get() == prio) sys.do_frame(delta);
		
		nRun.runEvents(eventFrameRun);
		nRun.runEvents(eventFrameRun, delta);

		space.do_frame(delta);

//		plane.frame_end();
		
		view.frame(delta);
		
	}
	
	@Override 
	protected void gui_draw() { 
		
//		paint.draw(gdx.drawer);
		
	}

	@Override 
	public void draw_start() {
//		term.do_frame(1);
		super.draw_start();
	}
	@Override 
	public void draw_end() {
//		term.draw();
	}
	
	
	
	
	
	
	
	

	public void empty_plane() {
		nRun.runEvents(eventEmptyRun);
	}


	ArrayList<nRun> eventSaveRun = new ArrayList<nRun>();
	ArrayList<nRun> eventLoadRun = new ArrayList<nRun>();
	ArrayList<nRun> eventEmptyRun = new ArrayList<nRun>();
	
	public void addEventSave(nRun r) { eventSaveRun.add(r); }
	public void removeEventSave(nRun r) { eventSaveRun.remove(r); }
	public void addEventLoad(nRun r) { eventLoadRun.add(r); }
	public void removeEventLoad(nRun r) { eventLoadRun.remove(r); }
	public void addEventEmpty(nRun r) { eventEmptyRun.add(r); }
	public void removeEventEmpty(nRun r) { eventEmptyRun.remove(r); }
	
	ArrayList<nRun> eventFrameRun = new ArrayList<nRun>();
	
	public void addEventFrame(nRun r) { eventFrameRun.add(r); }
	public void removeEventFrame(nRun r) { eventFrameRun.remove(r); }
	public void clearEventFrame() { eventFrameRun.clear(); }
	
	
	public nMap<pSystem> systems = new nMap<pSystem>();
	public HashMap<Class<?>, String> system_refs = new HashMap<Class<?>, String>();
	public void storeSystemType(String r, Class<?> ct) {
		if (system_refs.get(ct) == null) system_refs.put(ct,r); }
	
	public <T extends pSystem> T getSystem(Class<T> ct) { 
		String ref = system_refs.get(ct);
		if (systems.get(ref) != null) return (T)systems.get(ref); else {
			Utl.logn("ERROR : PlaneApplet could not find system "+ct.getName());
			return null; }
		}

	
	
	
	
	
	

	public Random seed_rng;

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
			gui.add_shortcut_target(shortcut_name, key, run_clic, run_state);
			inputs.put(input_ref+"_state", new nRun() { public Object get() {
				return state; }});
			inputs.put(input_ref+"_click", new nRun() { public Object get() {
				return click; }});
			inputs.put(input_ref+"_unclick", new nRun() { public Object get() {
				return unclick; }});
		}
		public void frame_inputs() {
			state = input.getState(key);
		}
		public void tick_end_inputs() {
			click = false; unclick = false;
		}
	}
	
	public void init_inputs() {
		
		seed_rng = new Random();

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

		gui.add_shortcut_target("Input - Up", 'W', null, run_up);
		gui.add_shortcut_target("Input - Down", 'S', null, run_down);
		gui.add_shortcut_target("Input - Left", 'A', null, run_left);
		gui.add_shortcut_target("Input - Right", 'D', null, run_right);
		gui.add_shortcut_target("Input - CW", 'E', null, run_cw);
		gui.add_shortcut_target("Input - CCW", 'Q', null, run_ccw);
		
		new KeyInput("key_space", "Input - Space", ' ');
		new KeyInput("key_w", "Input - W", 'Z');
		new KeyInput("key_i", "Input - I", 'I');
		
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
			return view.mouse_is_hover_view();
		}});
		inputs.put("mouse_pos", new nRun() { public Object get() {
			if (!view.mouse_is_hover_view()) return new Vector2();
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
		

		inputs.put("cam_pos", new nRun() { public Object get() {
			return view.val_cam_pos.get(); }});
		inputs.put("cam_scale", new nRun() { public Object get() {
			return view.val_cam_scale.get(); }});
		inputs.put("cam_scale_inv", new nRun() { public Object get() {
			return 1f / view.val_cam_scale.get(); }});
		inputs.put("cam_rot", new nRun() { public Object get() {
			return view.val_cam_rot.get(); }});
		inputs.put("cam_width", new nRun() { public Object get() {
			return view.val_view_size.x(); }});
		inputs.put("cam_height", new nRun() { public Object get() {
			return view.val_view_size.y(); }});
		
		outputs.put("cam_pos", new nRun() { public void run() { 
			if (args.length < 1) return;
			Vector2 r = arg(0,Vector2.class); 
			if (r == null) return;
			view.val_cam_pos_target.set(-r.x,-r.y);
			view.got_cam_pos_target = true; 
		}});
		outputs.put("cam_scale", new nRun() { public void run() {
			if (args.length < 1) return;
			float r = arg(0,Float.class); 
			view.val_cam_scale_target.set(r);
			view.got_cam_scale_target = true;
		}});
		outputs.put("cam_rot", new nRun() { public void run() {
			if (args.length < 1) return;
			float r = arg(0,Float.class); 
			view.val_cam_rot_target.set(-r);
			view.got_cam_rot_target = true;
		}});

		outputs.put("screenshot", new nRun() { public void run() {
			gdx.screenshot();
		}});
		
	}

	private void frame_inputs() {
		mouse_left_state = input.mouseLeft.state; 
		mouse_left_click = input.mouseLeft.trigClick; 
		mouse_left_unclick = input.mouseLeft.trigUClick; 
		mouse_right_state = input.mouseRight.state; 
		mouse_right_click = input.mouseRight.trigClick; 
		mouse_right_unclick = input.mouseRight.trigUClick;
		key_shift_state = input.keyShift.state; 
		keycross_press = (key_up || key_down || key_left || key_right); 
		keyrot_press = (key_cw || key_ccw);
		for (KeyInput k : keyInputs) k.frame_inputs();
	}
	public void tick_end_inputs() {
		mouse_left_click = false; mouse_left_unclick = false; 
		mouse_right_click = false; mouse_right_unclick = false;
		for (KeyInput k : keyInputs) k.tick_end_inputs();
	}
	
	
	
	
	
	

	ArrayList<nRun> eventToolInitRun = new ArrayList<nRun>();
	
	public void addEventToolInit(nRun r) { eventToolInitRun.add(r); }
	public void removeEventToolInit(nRun r) { eventToolInitRun.remove(r); }

	public void tool_setup(boolean openning) {
		
		addDelayEvent(6, new nRun() { public void run() {
			nWidgetGroup sec = gui.toolbox
					.addSection("  pPlane  ", openning);
			nInterface interf = gui.addInterface();
			interf.pop(sec);
			interf.setContext(bloc);
			interf.add_row();
			interf.add_row_label(1,"");
			interf.add_row_trigg(7,"Screenshot", new nRun() { public void run() {
				gdx.screenshot(); }});
			interf.add_row_label(1,"");
			nRun.runEvents(eventToolInitRun, interf);
			if (GdxApp.USE_GLPROFILER) {
				interf.cmd_context(app.data.system_bloc.adress);
				interf.add_row();
				interf.add_row_watch(4,"batchCalls: ","val_batchCalls");
				interf.add_row_label(1,"");
				interf.add_row_watch(4,"glCalls: ","val_glCalls");
				interf.add_row();
				interf.add_row_watch(9,"textureBindings: ","val_textureBindings");
				interf.add_row();
				interf.add_row_watch(4,"drawCalls: ","val_drawCalls");
				interf.add_row_label(1,"");
				interf.add_row_watch(4,"shaderSwtch: ","val_shaderSwitch");
			}
		}});
		
	}
	

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

	
	
}
