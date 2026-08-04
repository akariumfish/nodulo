package app;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import com.noodle.nodulo.Main;

import data.sData;
import gui.nGUI;
import plane.pPlane;
import util.nMap;
import util.nRun;

public class Applet extends App implements nPref.PrefAccess {
	// Applet.make(new AppConfig());
	public static GdxApp make(Main m, AppConfig c) {
		return new GdxApp(m, c, new Applet()); }
	
	public nPref pref;
	public <T> T getPref(String r, Class<T> cl) {
		return pref.getPref(r,cl);
	}
	public void setPref(String p, String r, Object d) {
		pref.setPref(p,r,d); }
	

//	private boolean TITLE_SCREEN = true;
	private boolean TITLE_SCREEN = false;
		
//	private boolean RELEASE = true;
	private boolean RELEASE = false;
	
//	private boolean BUILD_DEF_FILES = true;
	private boolean BUILD_DEF_FILES = false;

//	private boolean STARTUP_LOAD = true;
	private boolean STARTUP_LOAD = false;

	private boolean STARTUP_MODEL_USE = true;
//	private boolean STARTUP_MODEL_USE = false;
	
	private boolean PATCH_BUILD = true;
//	private boolean PATCH_BUILD = false;

//	private boolean AUTO_CONNECT = true;
	private boolean AUTO_CONNECT = false;
	
//	public boolean USE_FX = true;
	public boolean USE_FX = false;
	
//	public boolean START_FULLSCREEN = true;
	public boolean START_FULLSCREEN = false;
	
	private boolean HELP = true;
//	private boolean HELP = false;

//	private String STARTUP_MODEL_REF = "TEST";
//	private String STARTUP_MODEL_REF = "box2d_exemple";
	private String STARTUP_MODEL_REF = "patch_exemple";
//	private String STARTUP_MODEL_REF = "atom_game";
//	private String STARTUP_MODEL_REF = "";

	private float DEF_VIEW_ZOOM = 0.4f;
//	private float DEF_VIEW_ZOOM = 0.1f;
	private Vector2 DEF_VIEW_POS = new Vector2(0f,0f);
	private Vector2 DEF_VIEW_WIN_POS = new Vector2(370f,445f);
	private Vector2 DEF_VIEW_WIN_SZ = new Vector2(910f,350f);
//	private Vector2 DEF_VIEW_WIN_POS = new Vector2(370f,915f);
//	private Vector2 DEF_VIEW_WIN_SZ = new Vector2(910f,800f);
	private float DEF_PATCH_ZOOM = 0.1f;
	private Vector2 DEF_PATCH_POS = new Vector2(0f,0f);
	private Vector2 DEF_PATCH_WIN_POS = new Vector2(370f,915f);
	private Vector2 DEF_PATCH_WIN_SZ = new Vector2(910f,430f);
//	private Vector2 DEF_PATCH_WIN_POS = new Vector2(10f,915f);
//	private Vector2 DEF_PATCH_WIN_SZ = new Vector2(500f,400f);
	private boolean PATCH_TOOL_AUTOCOLLAPSE = true;
	private boolean PATCH_SHEET_COLLAPSE = false;
	private boolean TOOLBOX_OPEN = false;
	private float DEF_TICK_BY_SEC = 60f;
	

	

	public String PREFERENCE_REF = "default";
//	public String PREFERENCE_REF = "focus_space";
//	public String PREFERENCE_REF = "release";
//	public String PREFERENCE_REF = "release_FS";
	
	
	//default
//	public Applet() {}
	
	public static String setting_file = "setting"+sData.setting_extension;
//	public Applet(String title, String sf) {
//		super(title); 
//		setting_file = copy(sf)+sData.setting_extension;
//
//	}
	
	// start network right away
	public boolean start_solo = true;
	public boolean start_as_server = false;
	public boolean start_as_client = false;
//	public Applet(String title, String sf, boolean autol, boolean autob, boolean isserver) {
//		super(title);
//		setting_file = copy(sf)+sData.setting_extension;
//		if (isserver) start_as_server = true; 
//		else start_as_client = true;
//		start_solo = false;
//		STARTUP_LOAD = STARTUP_LOAD && autol;
//		PATCH_BUILD = PATCH_BUILD && autob;
//		player_ref = title+"_player";
//	}
	
	
	public String player_ref = "player";

	

	ArrayList<nRun> pref_runs = new ArrayList<nRun>();
	
	public void addPrefRun(nRun n) { pref_runs.add(n); }

	
	public nMenu menu;
	
	public static Applet app;
	
	@Override
	public void setup(GdxApp a) {
		gdx = a; app = this;

		pref = new nPref();
		
		newPref("default");
		
		newPref("focus_space");

		newPref("release");
		newPref("release_FS");

		for (nRun n : pref_runs) n.run();

		for (Preferences n : pref.prefs.all()) n.flush();
		
		pref.setCurrentPref(PREFERENCE_REF);

		if (getPref("start_fullscreen", Boolean.class)) START_FULLSCREEN = true;
		USE_FX = getPref("use_fx", Boolean.class); 
		if (getPref("RELEASE", Boolean.class)) { gdx.cursor(false); USE_FX = true; }
		
		super.setup(a);

//	    data = new sData(this);
//	    
//		input = new nInput(this);
//		
		gui = new nGUI(this, gdx, gdx, input, data, this);
		
		
		menu = new nMenu(this);

		pPlane.build(this); 

		if (menu != null) menu.build_title_screen();
		
	}

	@Override
	public void closing() {
		super.closing();

		pPlane.dispose(this);
		
	}

	protected void do_startup() {
		super.do_startup();
		
		
		data.setting_space.buildRootBloc("plane", "plane");
		

//		addDelayEvent(3, new nRun() { public void run() {
//			boolean tr = STARTUP_LOAD;
//			if (tr) {
//				tr = tr && gdx.exec_nothrow("data.setting_load()", new nRun() { public void run() {	
//					data.setting_load(); 
//				}});
//			}
//			if (tr) {
//				tr = tr && gdx.exec_nothrow("data.full_load()", new nRun() { public void run() {	
//					data.full_load(); 
//				}});
//			} 
//		}});
//		if (!tr && BLOC_BUILD) {
//			addDelayEvent(2, new nRun() { public void run() {
//				exec_nothrow("data.buildRootBloc(plane)", new nRun() { public void run() {	
//					
////					data.buildRootBloc("plane", "plane");
//					
////					data.buildRootBloc("file_explorer", "file_explorer");
//				}});
//			}});
//		}
		
		LOADING_SCREEN_FRAME = 1;
		
		addDelayEvent(1, new nRun() { public void run() {
			gdx.add_nodraw_frame(40); }});
	}


	public Preferences newPref(String r) {
		Preferences p = Gdx.app.getPreferences(r);
		pref.prefs.put(r,p);
		
		p.putBoolean("TITLE_SCREEN", TITLE_SCREEN);
		p.putBoolean("RELEASE", RELEASE);
		p.putBoolean("BUILD_DEF_FILES", BUILD_DEF_FILES);
		p.putBoolean("STARTUP_LOAD", STARTUP_LOAD);
		p.putBoolean("STARTUP_MODEL_USE", STARTUP_MODEL_USE);
		p.putBoolean("PATCH_BUILD", PATCH_BUILD);
		p.putBoolean("AUTO_CONNECT", AUTO_CONNECT);
		p.putBoolean("use_fx", USE_FX);
		p.putBoolean("start_fullscreen", START_FULLSCREEN);
		p.putBoolean("HELP", HELP);

		p.putString("STARTUP_MODEL_REF", STARTUP_MODEL_REF);
		p.putFloat("DEF_VIEW_ZOOM", DEF_VIEW_ZOOM);
		p.putFloat("DEF_VIEW_POS_x", DEF_VIEW_POS.x);
		p.putFloat("DEF_VIEW_POS_y", DEF_VIEW_POS.y);
		p.putFloat("DEF_VIEW_WIN_POS_x", DEF_VIEW_WIN_POS.x);
		p.putFloat("DEF_VIEW_WIN_POS_y", DEF_VIEW_WIN_POS.y);
		p.putFloat("DEF_VIEW_WIN_SZ_x", DEF_VIEW_WIN_SZ.x);
		p.putFloat("DEF_VIEW_WIN_SZ_y", DEF_VIEW_WIN_SZ.y);
		p.putFloat("DEF_PATCH_ZOOM", DEF_PATCH_ZOOM);
		p.putFloat("DEF_PATCH_POS_x", DEF_PATCH_POS.x);
		p.putFloat("DEF_PATCH_POS_y", DEF_PATCH_POS.y);
		p.putFloat("DEF_PATCH_WIN_POS_x", DEF_PATCH_WIN_POS.x);
		p.putFloat("DEF_PATCH_WIN_POS_y", DEF_PATCH_WIN_POS.y);
		p.putFloat("DEF_PATCH_WIN_SZ_x", DEF_PATCH_WIN_SZ.x);
		p.putFloat("DEF_PATCH_WIN_SZ_y", DEF_PATCH_WIN_SZ.y);
		p.putBoolean("PATCH_TOOL_AUTOCOLLAPSE", PATCH_TOOL_AUTOCOLLAPSE);
		p.putBoolean("PATCH_SHEET_COLLAPSE", PATCH_SHEET_COLLAPSE);
		p.putBoolean("TOOLBOX_OPEN", TOOLBOX_OPEN);
		p.putFloat("DEF_TICK_BY_SEC", DEF_TICK_BY_SEC);
		
		p.flush();
		
		return p;
	}
	
}
