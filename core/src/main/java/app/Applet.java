package app;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamLibraryLoader;
import com.codedisaster.steamworks.SteamLibraryLoaderGdx;
import com.codedisaster.steamworks.SteamLibraryLoaderLwjgl3;

import data.*;
import gui.*;
import net.nNetwork;
import plane.pPlane;

public class Applet extends GDXApplet {


	
	
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
	
//	private boolean use_fx = true;
	private boolean use_fx = false;
	
//	private boolean start_fullscreen = true;
	private boolean start_fullscreen = false;
	
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
	public Applet() {}
	
	public static String setting_file = "setting"+sData.setting_extension;
	public Applet(String title, String sf) {
		super(title); 
		setting_file = copy(sf)+sData.setting_extension;

	}
	
	// start network right away
	public boolean start_solo = true;
	public boolean start_as_server = false;
	public boolean start_as_client = false;
	public Applet(String title, String sf, boolean autol, boolean autob, boolean isserver) {
		super(title);
		setting_file = copy(sf)+sData.setting_extension;
		if (isserver) start_as_server = true; 
		else start_as_client = true;
		start_solo = false;
		STARTUP_LOAD = STARTUP_LOAD && autol;
		PATCH_BUILD = PATCH_BUILD && autob;
		player_ref = title+"_player";
	}
	
	
	public String player_ref = "player";

	public int LOADING_SCREEN_DELAY = 1;
	
	
	public static Applet app = null;
	public static Applet get() { return app; }
	
	
	public nInput input;
	public sData data;
	public nGUI gui;
	public nMenu menu;
	
	
	ArrayList<nRun> pref_runs = new ArrayList<nRun>();
	
	public void addPrefRun(nRun n) { pref_runs.add(n); }

	public void setup() {
		
		newPref("default");
		
		newPref("focus_space");

		newPref("release");
		newPref("release_FS");

		for (nRun n : pref_runs) n.run();

		for (Preferences n : prefs.all()) n.flush();
		
		setCurrentPref(PREFERENCE_REF);

		if (getPref("start_fullscreen", Boolean.class)) START_FULLSCREEN = true;
		USE_FX = getPref("use_fx", Boolean.class); 
		if (getPref("RELEASE", Boolean.class)) { cursor(true); USE_FX = true; }
		
		
//		try {
//			// with libGDX - requires steamworks4j-gdx
////			SteamLibraryLoader loader = new SteamLibraryLoaderGdx();
//
//			// .. or via LWJGL3 - requires steamworks4j-lwjgl3
//			SteamLibraryLoader loader = new SteamLibraryLoaderLwjgl3();
//
//			// optionally, tell the loader where to find binaries
//			loader.setLibraryPath("bin");
//
//			SteamAPI.loadLibraries(loader);
//			
//		    if (!SteamAPI.loadLibraries(loader)) {
//		    		log("STEAM : Failed to load native libraries");
//		    }
//		    if (!SteamAPI.init()) {
//		    		log("STEAM : Steamworks initialization error, e.g. Steam client not running");
//		    }
//		} catch (SteamException e) {
//			log("STEAM : You probably messed up the call order somehow");
//			e.printStackTrace(System.out);
//		}
		
		
		
		
		
		
		
		app = this;

	    data = new sData(this);
	    
		input = new nInput(this);
		
		gui = new nGUI(this, camera, input.mouse, screenrect);
		
		menu = new nMenu(this);

		pPlane.build(app); 

		if (BUILD_DEF_FILES) {
			data.build_def_files();
		}
		
		menu.build_title_screen();
		
	}
	
	public void closing() {
		
		pPlane.dispose(this);
		
		gui.dispose();
		data.dispose();
		
//		SteamAPI.shutdown();
	}
	

	public sValueBloc startup_buildbloc() {
		return data.setting_space.buildRootBloc("plane", "plane");
	}
	
	public void startup() { do_startup = true; }
	private boolean do_startup = false;
	private void do_startup() {
		do_startup = false;
		
		
		startup_buildbloc();
		

		addDelayEvent(3, new nRun() { public void run() {
			boolean tr = STARTUP_LOAD;
			if (tr) {
				tr = tr && exec_nothrow("data.setting_load()", new nRun() { public void run() {	
					data.setting_load(); 
				}});
			}
			if (tr) {
				tr = tr && exec_nothrow("data.full_load()", new nRun() { public void run() {	
					data.full_load(); 
				}});
			} 
		}});
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
		
		LOADING_SCREEN_DELAY = 1;
		
		addDelayEvent(1, new nRun() { public void run() {
			add_nodraw_frame(40); }});
	}
	
	
	public void pre_draw() {
		
		if (SteamAPI.isSteamRunning()) {
		    SteamAPI.runCallbacks();
		}
		
		exec_nothrow("input.frame_str()", new nRun() { public void run() {	
			//sInput
			input.frame_str();
		}});
		
		exec_nothrow("data.frame_start()", new nRun() { public void run() {	
			//data update
			data.frame_start();
		}});

		exec_nothrow("runEvents(eventsFrame)", new nRun() { public void run() {	
			// frame event
			nRun.runEvents(eventsFrame);
		}});

		exec_nothrow("runEvents(eventsNextFrame)", new nRun() { public void run() {	
			if (!active_nxtfrm_pile) { nRun.runEvents(eventsNextFrame1); eventsNextFrame1.clear(); } 
		    else { nRun.runEvents(eventsNextFrame2); eventsNextFrame2.clear(); } 
		    active_nxtfrm_pile = !active_nxtfrm_pile;
		}});

		exec_nothrow("runEvents(delay_events)", new nRun() { public void run() {	
		    for (int i = delay_events.size() - 1 ; i >= 0 ; i--) {
		    		DelayEvent d = delay_events.get(i);
		    		d.delay -= 1;
		    		if (d.delay <= 0) {
		    			d.event.run();
		    			delay_events.remove(d);
		    		}
		    }
		}});
		
		exec_nothrow("gui.frame()", new nRun() { public void run() {	
			//nGUI update
			gui.frame();
		}});

		exec_nothrow("runEvents(runFrame, delta)", new nRun() { public void run() {	
			//update
			nRun.runEvents(runFrame, Gdx.graphics.getDeltaTime());
		}});
		
	}

	public void screen_draw() {
		
		exec_nothrow("gui.draw()", new nRun() { public void run() {	
			//screen GUI drawing
			gui.draw();
		}});
		
		if (LOADING_SCREEN_DELAY > 0) {
			LOADING_SCREEN_DELAY--;
			noStroke(); fill(20,255);
			rect(screenrect);
			textAlign(nAlign.CENTER, nAlign.CENTER);
			text("LOADING Please wait ...", screenrect.width / 2f, screenrect.height / 2f, 20);
		}
		
//		//mouse pointer
//		drawer.filledCircle(input.mouse.x, input.mouse.y, 2);
//		
	}
	
	public void post_draw() {

		// gui debug
//		if (input.getClick('P')) gui.print_state();

		exec_nothrow("runEvents(eventsFrameEnd)", new nRun() { public void run() {	
			// frame event
			nRun.runEvents(eventsFrameEnd);
		}});

		exec_nothrow("data.frame_end()", new nRun() { public void run() {	
			//data update
			data.frame_end();
		}});

		exec_nothrow("input.frame_end()", new nRun() { public void run() {	
			//sInput
			input.frame_end();
		}});
		
		if (do_startup) do_startup();
	}


	
	

	public <T> T getPref(String r, Class<T> cl) {
		if (cl == Boolean.class) {
			return (T)(Object)current_pref.getBoolean(r);
		} else if (cl == Float.class) {
			return (T)(Object)current_pref.getFloat(r);
		} else if (cl == Integer.class) {
			return (T)(Object)current_pref.getInteger(r);
		} else if (cl == String.class) {
			return (T)(Object)current_pref.getString(r);
		} else if (cl == Vector2.class) {
			return (T)(Object)(new Vector2(
					current_pref.getFloat(r+"_x"), current_pref.getFloat(r+"_y")));
		} else {
			return null;
		}
	}

	public void setPref(String r, Object d) {
		if (current_pref == null) return;
		setPref(current_pref, r, d);
	}
	public void setPref(String pref_ref, String r, Object d) {
		Preferences pref = prefs.get(pref_ref);
		if (pref == null) return;
		setPref(pref, r, d);
	}
	public void setPref(Preferences pref, String r, Object d) {
		if (pref == null) return;
		Class<?> cl = d.getClass();
		if (cl == Boolean.class) {
			pref.putBoolean(r, (boolean)d);
		} else if (cl == Float.class) {
			pref.putFloat(r, (float)d);
		} else if (cl == Integer.class) {
			pref.putInteger(r, (int)d);
		} else if (cl == String.class) {
			pref.putString(r, (String)d);
		} else if (cl == Vector2.class) {
			pref.putFloat(r+"_x", ((Vector2)d).x);
			pref.putFloat(r+"_y", ((Vector2)d).y);
		} else {
			return;
		}
		pref.flush();
	}
	
	public nMap<Preferences> prefs = new nMap<Preferences>();
	public Preferences current_pref;
	
	public void setCurrentPref(String r) {
		if (prefs.hasKey(r)) current_pref = prefs.get(r);
	}
	
	public Preferences newPref(String r) {
		Preferences p = Gdx.app.getPreferences(r);
		prefs.put(r,p);
		
		p.putBoolean("TITLE_SCREEN", TITLE_SCREEN);
		p.putBoolean("RELEASE", RELEASE);
		p.putBoolean("BUILD_DEF_FILES", BUILD_DEF_FILES);
		p.putBoolean("STARTUP_LOAD", STARTUP_LOAD);
		p.putBoolean("STARTUP_MODEL_USE", STARTUP_MODEL_USE);
		p.putBoolean("PATCH_BUILD", PATCH_BUILD);
		p.putBoolean("AUTO_CONNECT", AUTO_CONNECT);
		p.putBoolean("use_fx", use_fx);
		p.putBoolean("start_fullscreen", start_fullscreen);
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
	
	
	
	
	
	
	
	
	//	------------   EVENT   ------------
	
	public Applet clearAllEvent() { 
		eventsNextFrame1.clear(); 
		eventsNextFrame2.clear();
		delay_events.clear();
		return this; 
	}
	
	ArrayList<nRun> runFrame = new ArrayList<nRun>();
	
	public Applet addRunFrame(nRun r) { runFrame.add(r); return this; }
	public Applet removeRunFrame(nRun r) { runFrame.remove(r); return this; }

	public ArrayList<nRun> eventsFrame = new ArrayList<nRun>();
	public ArrayList<nRun> eventsFrameEnd = new ArrayList<nRun>();
	ArrayList<nRun> eventsNextFrame1 = new ArrayList<nRun>();
	ArrayList<nRun> eventsNextFrame2 = new ArrayList<nRun>();
	boolean active_nxtfrm_pile = false;

	public Applet addEventFrame(nRun r) { eventsFrame.add(r); return this; }
	public Applet removeEventFrame(nRun r) { eventsFrame.remove(r); return this; }
	public Applet addEventFrameEnd(nRun r) { eventsFrameEnd.add(r); return this; }
	public Applet removeEventFrameEnd(nRun r) { eventsFrameEnd.remove(r); return this; }
	public Applet addEventNextFrame(nRun r) { 
		if (active_nxtfrm_pile) eventsNextFrame1.add(r); else eventsNextFrame2.add(r); return this; }

	private class DelayEvent {
		public nRun event;
		public int delay = 0; 
		public DelayEvent(int d, nRun r) { delay = d; event = r; } }

	ArrayList<DelayEvent> delay_events = new ArrayList<DelayEvent>();
	public Applet addDelayEvent(int delay, nRun r) { 
		if (delay <= 0) { r.run(); return this; }
		delay_events.add(new DelayEvent(delay, r)); return this; }
	
	
	
	
	
	
}

