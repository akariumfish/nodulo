package app;

import java.lang.reflect.Field;

import com.badlogic.gdx.math.Vector2;
import com.noodle.nodulo.Main;

import util.Utl;
import util.nRun;

public class Conf {
	
	public Conf set(String r, Object d) {
		Field[] fields = this.getClass().getFields();
		for (Field f : fields) {
			String n = f.getName();
			if (n.equals(r)) {
				try {
					f.set(this,d);
					break;
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return this;
	}

	public Conf() {}

	public Conf(Conf c) {
		Field[] fields = this.getClass().getFields();
		for (Field f : fields) {
			try {
				f.set(this,Utl.copy(f.get(c)));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Conf(String t, int w, int h, boolean f, nRun n) {
		window_title = t; 
		WIDTH = w; 
		HEIGHT = h; 
		START_FULLSCREEN = f; 
		app_run = n; 
	}
	
	public Conf(String t, int w, int h, boolean f, nRun n, 
			String s, String filename, boolean dark_theme) { 
		this(t,w,h,f,n);
		RELEASE = !dark_theme;
		STARTUP_MODEL_REF = s; 
		PATCH_BUILD = true;
		STARTUP_NEW_FILE = filename;
	}
	public Conf(String t, int w, int h, boolean f, nRun n, 
			boolean startup_load, String s, boolean dark_theme) { 
		this(t,w,h,f,n);
		RELEASE = !dark_theme;
		PATCH_BUILD = false;
		STARTUP_LOAD = true;
		STARTUP_LOAD_FILE = s; 
	}

//	public boolean CATCH_THROW = true;
	public boolean CATCH_THROW = false;

//	public boolean PRINT_TIMETRACK = true;
	public boolean PRINT_TIMETRACK = false;
	
	public int WIDTH = 500;
	public int HEIGHT = 500;
	
	public String window_title = "";
	public nRun app_run;
//	public Main.Launch launcher;
	
//	public boolean TITLE_SCREEN = true; 
	public boolean TITLE_SCREEN = false;

//	public boolean START_FULLSCREEN = true;
	public boolean START_FULLSCREEN = false;

//	public boolean RELEASE = true;
	public boolean RELEASE = false;
	
//	public boolean BLOCK_NDRAWER_FX = true;
	public boolean BLOCK_NDRAWER_FX = false;

//	public boolean STARTUP_LOAD = true;
	public boolean STARTUP_LOAD = false;

	public boolean PATCH_BUILD = true;
//	public boolean PATCH_BUILD = false; 

//	public boolean AUTO_CONNECT = true;
	public boolean AUTO_CONNECT = false;
	
	public boolean START_FX = true;
//	public boolean START_FX = false;
	
//	public boolean START_HELP = true;
	public boolean START_HELP = false; 

	public String STARTUP_MODEL_REF = "exemple";
//	public String STARTUP_MODEL_REF = "";

	public String STARTUP_MAP_PATH = "Map2.tmx";
//	public String STARTUP_MAP_PATH = "";

	public String STARTUP_LOAD_FILE = "";
	public String STARTUP_NEW_FILE = ""; 

	public boolean VIEW_START_WALLPAPER = true;
	public boolean VIEW_START_COLLAPSED = false;
	public boolean VIEW_START_GRID = false;
//	public float DEF_VIEW_ZOOM = 0.07f;
	public float DEF_VIEW_ZOOM = 0.3f;
	public Vector2 DEF_VIEW_POS = new Vector2(0f,0f);
	// DEFAULT
//	public Vector2 DEF_VIEW_WIN_POS = new Vector2(370f,425f);
//	public Vector2 DEF_VIEW_WIN_SZ = new Vector2(910f,370f);
	// SMALL
//	public Vector2 DEF_VIEW_WIN_POS = new Vector2(870f,425f);
//	public Vector2 DEF_VIEW_WIN_SZ = new Vector2(410f,370f);
	// BIG
	public Vector2 DEF_VIEW_WIN_POS = new Vector2(370f,900f);
	public Vector2 DEF_VIEW_WIN_SZ = new Vector2(910f,770f);
	public boolean PATCH_START_WALLPAPER = false;
	public boolean PATCH_START_COLLAPSED = true;
	public boolean PATCH_START_GRID = true;
	public float DEF_PATCH_ZOOM = 0.1f;
	public Vector2 DEF_PATCH_POS = new Vector2(0f,0f);
	public Vector2 DEF_PATCH_WIN_POS = new Vector2(370f,915f);
	public Vector2 DEF_PATCH_WIN_SZ = new Vector2(910f,450f);
//	public boolean PATCH_TOOL_AUTOCOLLAPSE = true;
	public boolean PATCH_SHEET_COLLAPSE = true;
	public boolean TOOLBOX_OPEN = true;
	public boolean DRAW_GROUND = true;
	public boolean DRAW_FOG = true;
	public boolean DRAW_VISION = true;
	
	public boolean POP_BODY_EDITOR = false;

	public boolean AVATAR_VIEW_MODE = false; //false = def
	public boolean AVATAR_CAM = true;
	
	public float DEF_TICK_BY_SEC = 60f;
	
	public boolean start_solo = true;
	public boolean start_as_server = false;
	public boolean start_as_client = false;

	public String player_ref = "player";

}
