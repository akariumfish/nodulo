package aa_nodulo;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.noodle.nodulo.Main;

import app.App;
import app.AppConfig;
import app.GdxApp;
import data.sValueBloc;
import gui.nGUI;
import gui.nGUIBook;
import gui.nInterface;
import gui.nMenu;
import gui.nWidgetGroup;
import patch.pPatch;
import patch.pStandard;
import util.Utl;
import util.nMap;
import util.nPainting;
import util.nRun;
import util.nScripted;

public class PlaneApplet extends App {

	
	public static boolean TITLE_SCREEN = true; 
//	public static boolean TITLE_SCREEN = false;

//	public static boolean START_FULLSCREEN = true;
	public static boolean START_FULLSCREEN = false;
	
	
	public static class AppletConfig {

		public AppletConfig() {}
		public AppletConfig(String s, boolean dark_theme) { 
			RELEASE = !dark_theme;
			STARTUP_MODEL_REF = s; 
		}
		
//		public boolean RELEASE = true;
		public boolean RELEASE = false;
		
		public boolean PATCH_BUILD = true;
//		public boolean PATCH_BUILD = false;

//		public boolean AUTO_CONNECT = true;
		public boolean AUTO_CONNECT = false;
		
//		public boolean START_FX = true;
		public boolean START_FX = false;
		
		public boolean START_HELP = true;
//		public boolean START_HELP = false;

//		public String STARTUP_MODEL_REF = "TEST";
//		public String STARTUP_MODEL_REF = "box2d_exemple";
		public String STARTUP_MODEL_REF = "patch_exemple";
//		public String STARTUP_MODEL_REF = "atom_game";
//		public String STARTUP_MODEL_REF = "";

		public float DEF_VIEW_ZOOM = 0.4f;
		public Vector2 DEF_VIEW_POS = new Vector2(0f,0f);
		public Vector2 DEF_VIEW_WIN_POS = new Vector2(370f,425f);
		public Vector2 DEF_VIEW_WIN_SZ = new Vector2(910f,370f);
		public float DEF_PATCH_ZOOM = 0.1f;
		public Vector2 DEF_PATCH_POS = new Vector2(0f,0f);
		public Vector2 DEF_PATCH_WIN_POS = new Vector2(370f,915f);
		public Vector2 DEF_PATCH_WIN_SZ = new Vector2(910f,450f);
		public boolean PATCH_TOOL_AUTOCOLLAPSE = true;
		public boolean PATCH_SHEET_COLLAPSE = false;
		public boolean TOOLBOX_OPEN = false;
		public float DEF_TICK_BY_SEC = 60f;
		
		public boolean start_solo = true;
		public boolean start_as_server = false;
		public boolean start_as_client = false;

		public String player_ref = "player";

	}

	
	// NoduloApplet.make(Main, new AppConfig());
	// NoduloApplet.make(Main, new AppConfig(), new AppletConfig());
	
	public static GdxApp make(Main m, AppConfig c) {
		return new GdxApp(m, c, new PlaneApplet(new AppletConfig())); }
	
	public static GdxApp make(Main m, AppConfig c, AppletConfig c2) {
		return new GdxApp(m, c, new PlaneApplet(c2)); }
	
	
	public static boolean RELEASE = false;
	
	public PlaneApplet(AppletConfig c) { 
		config = c; RELEASE = c.RELEASE; app = this; 
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

	public nGUI gui;
	public nMenu menu;

	Skin skin;
	Stage stage;
	
	public pView view;
	public pTime time;
	public pPatch patch;
	public pSpace space;
	public pNet net;

	public boolean NET_CTRL = false;
	
	nPainting paint;
	
	public sValueBloc bloc;
	
	public InputMultiplexer multiplexer;
	
	@Override
	public void setInputProcessor() {
		Gdx.input.setInputProcessor(multiplexer); }

	
	
	public static void build_setup() {
		pPatch.build_setup();
		pAtom.build_setup();
		pBox2d.build_setup();
	}
	

	private static boolean has_build_statics = false;
	private static void build_help() {
		if (has_build_statics) return;
		nMenu.newHelp("help_1", "txt1")
		.text(" txt2")
		.line()
		.text("txt3")
		.link("help 2", "help_2")
		.text("txt4")
		.line()
		;
		
		nMenu.newHelp("help_2", "txt5")
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
		use_fx(config.START_FX);
		
		bloc = data.root_bloc;
		
		skin = new Skin(Gdx.files.internal("ui/skin.json"));
		stage = new Stage(new ScreenViewport());
		
		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(stage);
		multiplexer.addProcessor(input);
		Gdx.input.setInputProcessor(multiplexer);
		
		gui = new nGUI(this);

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
		
//		menu.add_tool_menu_trigg("Empty Plane", new nRun() { public void run() {
//			empty_plane(); }});
		
		
		view = new pView(this);
		time = new pTime(this);
		patch = new pPatch(this);
		net = new pNet(this);
		space = new pSpace(this);

		view.system_load();
		time.system_load();
		net.system_load();
		space.system_load();
		patch.system_load();

		if (!RELEASE) tool_setup();
		
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
		
		startup();
		
	}

	@Override
	public void closing() {
		super.closing();

		stage.dispose();
		skin.dispose(); 
		
		gui.dispose();
		
	}
	
	@Override 
	protected void gui_frame() { 
		float delta = Gdx.graphics.getDeltaTime();

		gui.frame(); 
		
		frame_inputs();

		time.do_frame(delta);
		view.frame(delta);
		net.frame(delta);
		space.do_frame(delta);
		patch.frame(delta);
		
		for (int prio = pSystem.max_frame_prio ; prio >= 0 ; prio--)
			for (pSystem sys : systems.all()) 
				if (sys.val_prio_frame.get() == prio) sys.do_frame(delta);
		
		nRun.runEvents(eventFrameRun);
		nRun.runEvents(eventFrameRun, delta);
		
		view.frame(delta);
		
	}
	
	@Override 
	protected void gui_draw() { 

		gui.draw(); 

//		paint.draw(gdx.drawer);
		
	}

	@Override 
	public void draw_start() {

		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
		
	}
	@Override 
	public void draw_end() {
		
		stage.draw();
		
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
			menu.add_shortcut_target(shortcut_name, key, run_clic, run_state);
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

		menu.add_shortcut_target("Input - Up", 'W', null, run_up);
		menu.add_shortcut_target("Input - Down", 'S', null, run_down);
		menu.add_shortcut_target("Input - Left", 'A', null, run_left);
		menu.add_shortcut_target("Input - Right", 'D', null, run_right);
		menu.add_shortcut_target("Input - CW", 'E', null, run_cw);
		menu.add_shortcut_target("Input - CCW", 'Q', null, run_ccw);
		
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

	public void tool_setup() {
		
		addDelayEvent(1, new nRun() { public void run() {
			nWidgetGroup sec = menu.toolbox
					.addSection("  pPlane  ", true);;
			nInterface interf = gui.addInterface();
			interf.pop(sec);
			interf.setContext(bloc);
			nRun.runEvents(eventToolInitRun, interf);
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
