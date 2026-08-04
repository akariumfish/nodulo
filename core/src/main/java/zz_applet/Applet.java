package zz_applet;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import com.noodle.nodulo.Main;

import app.App;
import app.AppConfig;
import app.GdxApp;
import gui.nGUI;
import util.nRun;
import zz_plane.pPlane;

public class Applet extends App implements nPref.PrefAccess {

	// Applet.make(Main, new AppConfig());
	// Applet.make(Main, new AppConfig(), new AppletConfig());
	
	public static GdxApp make(Main m, AppConfig c) {
		return new GdxApp(m, c, new Applet(new AppletConfig())); }
	
	public static GdxApp make(Main m, AppConfig c, AppletConfig c2) {
		return new GdxApp(m, c, new Applet(c2)); }
	
	
	
	public static class AppletConfig {
		public boolean TITLE_SCREEN = false;
		
//		public boolean RELEASE = true;
		public boolean RELEASE = false;
		
//		public boolean BUILD_DEF_FILES = true;
		public boolean BUILD_DEF_FILES = false;

//		public boolean STARTUP_LOAD = true;
		public boolean STARTUP_LOAD = false;

		public boolean STARTUP_MODEL_USE = true;
//		public boolean STARTUP_MODEL_USE = false;
		
		public boolean PATCH_BUILD = true;
//		public boolean PATCH_BUILD = false;

//		public boolean AUTO_CONNECT = true;
		public boolean AUTO_CONNECT = false;
		
//		public boolean USE_FX = true;
		public boolean USE_FX = false;
		
//		public boolean START_FULLSCREEN = true;
		public boolean START_FULLSCREEN = false;
		
		public boolean HELP = true;
//		public boolean HELP = false;

//		public String STARTUP_MODEL_REF = "TEST";
//		public String STARTUP_MODEL_REF = "box2d_exemple";
		public String STARTUP_MODEL_REF = "patch_exemple";
//		public String STARTUP_MODEL_REF = "atom_game";
//		public String STARTUP_MODEL_REF = "";

		public float DEF_VIEW_ZOOM = 0.4f;
//		public float DEF_VIEW_ZOOM = 0.1f;
		public Vector2 DEF_VIEW_POS = new Vector2(0f,0f);
		public Vector2 DEF_VIEW_WIN_POS = new Vector2(370f,445f);
		public Vector2 DEF_VIEW_WIN_SZ = new Vector2(910f,350f);
//		public Vector2 DEF_VIEW_WIN_POS = new Vector2(370f,915f);
//		public Vector2 DEF_VIEW_WIN_SZ = new Vector2(910f,800f);
		public float DEF_PATCH_ZOOM = 0.1f;
		public Vector2 DEF_PATCH_POS = new Vector2(0f,0f);
		public Vector2 DEF_PATCH_WIN_POS = new Vector2(370f,915f);
		public Vector2 DEF_PATCH_WIN_SZ = new Vector2(910f,430f);
//		public Vector2 DEF_PATCH_WIN_POS = new Vector2(10f,915f);
//		public Vector2 DEF_PATCH_WIN_SZ = new Vector2(500f,400f);
		public boolean PATCH_TOOL_AUTOCOLLAPSE = true;
		public boolean PATCH_SHEET_COLLAPSE = false;
		public boolean TOOLBOX_OPEN = false;
		public float DEF_TICK_BY_SEC = 60f;
	}

	public boolean USE_FX = false;

	public AppletConfig config;

	public String PREFERENCE_REF = "default";
//	public String PREFERENCE_REF = "focus_space";
//	public String PREFERENCE_REF = "release";
//	public String PREFERENCE_REF = "release_FS";
	

	public Applet(AppletConfig c) {
		config = c; }
	
	// start network right away
	public boolean start_solo = true;
	public boolean start_as_server = false;
	public boolean start_as_client = false;
	
//	public Applet(String title, String sf, boolean autol, boolean autob, boolean isserver) {

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


	public nGUI gui;
	public nMenu menu;
	
	public static Applet app;
	
	@Override
	public void setup(GdxApp a) {
		gdx = a; app = this;

		super.setup(a);

		pref = new nPref();
		
		newPref("default");
		
		newPref("focus_space");

		newPref("release");
		newPref("release_FS");

		for (nRun n : pref_runs) n.run();

		for (Preferences n : pref.prefs.all()) n.flush();
		
		pref.setCurrentPref(PREFERENCE_REF);

		if (getPref("start_fullscreen", Boolean.class)) GdxApp.START_FULLSCREEN = true;
		USE_FX = getPref("use_fx", Boolean.class); 
		if (getPref("RELEASE", Boolean.class)) { gdx.cursor(false); USE_FX = true; }
		
		gui = new nGUI(this);
		
		nGUIBook.build_book(gui.book, this);
		
		menu = new nMenu(this);

		pPlane.build(this); 

		if (menu != null) menu.build_title_screen();
		
	}

	@Override
	public void closing() {
		super.closing();

		pPlane.dispose(this);

		gui.dispose();
		
	}

	@Override
	protected void do_startup() {
		
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
		
		LOADING_SCREEN_FRAME = 3;
		
		addDelayEvent(1, new nRun() { public void run() {
			gdx.add_nodraw_frame(40); }});
	}

	@Override
	protected void gui_frame() {
		//nGUI update
		gui.frame(); }
	@Override
	protected void gui_draw() {
		//screen GUI drawing
		gui.draw(); }
	

	public nPref pref;
	public <T> T getPref(String r, Class<T> cl) {
		return pref.getPref(r,cl);
	}
	public void setPref(String p, String r, Object d) {
		pref.setPref(p,r,d); }
	
	
	public Preferences newPref(String r) {
		Preferences p = Gdx.app.getPreferences(r);
		pref.prefs.put(r,p);
		
		p.putBoolean("TITLE_SCREEN", config.TITLE_SCREEN);
		p.putBoolean("RELEASE", config.RELEASE);
		p.putBoolean("BUILD_DEF_FILES", config.BUILD_DEF_FILES);
		p.putBoolean("STARTUP_LOAD", config.STARTUP_LOAD);
		p.putBoolean("STARTUP_MODEL_USE", config.STARTUP_MODEL_USE);
		p.putBoolean("PATCH_BUILD", config.PATCH_BUILD);
		p.putBoolean("AUTO_CONNECT", config.AUTO_CONNECT);
		p.putBoolean("use_fx", config.USE_FX);
		p.putBoolean("start_fullscreen", config.START_FULLSCREEN);
		p.putBoolean("HELP", config.HELP);

		p.putString("STARTUP_MODEL_REF", config.STARTUP_MODEL_REF);
		p.putFloat("DEF_VIEW_ZOOM", config.DEF_VIEW_ZOOM);
		p.putFloat("DEF_VIEW_POS_x", config.DEF_VIEW_POS.x);
		p.putFloat("DEF_VIEW_POS_y", config.DEF_VIEW_POS.y);
		p.putFloat("DEF_VIEW_WIN_POS_x", config.DEF_VIEW_WIN_POS.x);
		p.putFloat("DEF_VIEW_WIN_POS_y", config.DEF_VIEW_WIN_POS.y);
		p.putFloat("DEF_VIEW_WIN_SZ_x", config.DEF_VIEW_WIN_SZ.x);
		p.putFloat("DEF_VIEW_WIN_SZ_y", config.DEF_VIEW_WIN_SZ.y);
		p.putFloat("DEF_PATCH_ZOOM", config.DEF_PATCH_ZOOM);
		p.putFloat("DEF_PATCH_POS_x", config.DEF_PATCH_POS.x);
		p.putFloat("DEF_PATCH_POS_y", config.DEF_PATCH_POS.y);
		p.putFloat("DEF_PATCH_WIN_POS_x", config.DEF_PATCH_WIN_POS.x);
		p.putFloat("DEF_PATCH_WIN_POS_y", config.DEF_PATCH_WIN_POS.y);
		p.putFloat("DEF_PATCH_WIN_SZ_x", config.DEF_PATCH_WIN_SZ.x);
		p.putFloat("DEF_PATCH_WIN_SZ_y", config.DEF_PATCH_WIN_SZ.y);
		p.putBoolean("PATCH_TOOL_AUTOCOLLAPSE", config.PATCH_TOOL_AUTOCOLLAPSE);
		p.putBoolean("PATCH_SHEET_COLLAPSE", config.PATCH_SHEET_COLLAPSE);
		p.putBoolean("TOOLBOX_OPEN", config.TOOLBOX_OPEN);
		p.putFloat("DEF_TICK_BY_SEC", config.DEF_TICK_BY_SEC);
		
		p.flush();
		
		return p;
	}
	
}
