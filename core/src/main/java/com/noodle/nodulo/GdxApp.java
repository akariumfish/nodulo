package com.noodle.nodulo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.zip.Deflater;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import app.AppConfig;
import app.nDrawer;
import app.nDrawer.DrawContext;
import util.*;

//public class GdxApp implements ApplicationListener {
public class GdxApp implements Screen ,nDrawer.DrawContext {
	
	
	
	public interface nAppListener {

		public void setup(GdxApp a);
		public void closing();
		public void pre_draw();
		public void draw_start();
		public void drawer_draw();
		public void draw_end();
		public void post_draw();
		public void setInputProcessor();
		
	}
	
//	public static boolean CATCH_THROW = true;
	public static boolean CATCH_THROW = false;

//	public static boolean PRINT_TIMETRACK = true;
	public static boolean PRINT_TIMETRACK = false;
	
	public GdxApp(Main m, AppConfig c) { this(m,c,null); }
	public GdxApp(Main m, AppConfig c, nAppListener l) { 
		Utl.gdx = this;
		listener = l; main = m; window_title = c.window_title; 
		WIDTH = c.WIDTH; HEIGHT = c.HEIGHT; START_FULLSCREEN = c.START_FULLSCREEN; 
		create(); }

	public static boolean START_FULLSCREEN = false;

	public static int WIDTH = 500;
	public static int HEIGHT = 500;
	
	public static GdxApp app;

	public Main main;
	
	public String window_title = "";

    public OrthographicCamera camera; 
	public ScreenViewport viewport; 

	public long frame_counter = 0;
	public Rectangle screenrect = new Rectangle();//0,0,GdxApp.WIDTH,GdxApp.HEIGHT

	public nDrawer drawer;
	
	nAppListener listener;
	
	public void setInputProcessor() {
		if (listener != null) listener.setInputProcessor();
	}
	
	public void create() {
		
		camera = main.camera;
		viewport = main.viewport;

		camera.position.set(WIDTH / 2, HEIGHT / 2, 0);
		camera.update();
		
		app = this;
		
		drawer = new nDrawer(this, false);
		
//		Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
//		Gdx.graphics.setFullscreenMode(currentMode);
//		Gdx.graphics.setWindowedMode(WIDTH, HEIGHT);
		
		setup();
		if (listener != null) listener.setup(this);

		Gdx.graphics.setTitle(window_title);
		
		cursor(false);
	}
	
	public void setup() {}

	public void pre_draw() {}
	public void post_draw() {}
	public void screen_draw() {}

	private boolean close_app_flag = false;
	public void close_app() { close_app_flag = true; }

	private boolean to_title_flag = false, to_title_flag2 = false;
	public void to_title() { to_title_flag = true; }

	public void closing() {}

	@Override
	public void dispose() {
		 
		if (Gdx.graphics.isFullscreen()) {
	            Gdx.graphics.setWindowedMode(WIDTH, HEIGHT);
	            screenwidth = WIDTH; screenheight = HEIGHT;
		        resize(screenwidth, screenheight); }
		
		closing();
		if (listener != null) listener.closing();
		 
		drawer.dispose();
		
	}

	@Override
	public void resize(int width, int height) {
		if(width <= 0 || height <= 0) return;
		
		screenwidth = width; screenheight = height;
		screenrect.set(0, 0, width, height);
		
		viewport.update(width, height, false); 

		camera.setToOrtho(false, width, height);
		camera.position.set(width / 2, height / 2, 0);
		camera.update();

        drawer.resize(width, height);
        
        nRun.runEvents(eventsScreen); 
	}
	@Override
	public void pause() { }
	@Override
	public void resume() { }
	@Override
	public void show() { }
	@Override
	public void hide() { }
	@Override
	public nDrawer getDrawer() { return drawer; }
	@Override
	public Viewport getViewport() { return viewport; }
	@Override
	public Rectangle getScreenRect() { return screenrect; }
	@Override
	public OrthographicCamera getCamera() { return camera; }
	@Override
	public void render(float delta) {
		
		Utl.log_pref1 = window_title+":"+frame_counter;
		
		try_nodraw_frame();
		
		if (close_app_flag) main.exit();
		
		frame_counter++;
		
		javaHeap = Gdx.app.getJavaHeap();
		nativeHeap = Gdx.app.getNativeHeap();

//		try {
//			setHWCursorVisible(hwVisible);
//		} catch (LWJGLException e) {
//			throw new GdxRuntimeException(e);
//		}

		test_interupt();
		
		if (!block_custom_metodes) {
			pre_draw(); if (listener != null) listener.pre_draw(); }

		test_interupt();

		//Draw

		if (!block_custom_metodes && listener != null) listener.draw_start();
		
		drawer.draw_begin();
		
		test_interupt();
		
		if (!block_custom_metodes) {
			screen_draw(); if (listener != null) listener.drawer_draw(); }

		test_interupt();
		
		//draw debug shapes
		drawDebug();
		
		//color screen red if something crash
		if (something_crashed) {
			drawer.fill(255,0,0,30); drawer.noStroke();
			drawer.rect(screenrect); }

//		// draw custom cursor
		if (!show_cursor) {
			drawer.fill(255); drawer.stroke(0,3f);
			drawer.push();
			drawer.translate(Gdx.input.getX(), screenrect.height-Gdx.input.getY());
			drawer.polygon(new Vector2(0,2),new Vector2(0,-24),new Vector2(12,-18)); 
			drawer.pop();
		} 
		
		drawer.draw_end();

		if (!block_custom_metodes && listener != null) listener.draw_end();
        
		test_interupt();
		
		if (!block_custom_metodes) {
			post_draw(); if (listener != null) listener.post_draw(); }
		
		if (ask_screenshot) {
			ask_screenshot = false;
			
			Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
			ByteBuffer pixels = pixmap.getPixels();

			// This loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing
			int size = Gdx.graphics.getBackBufferWidth() * Gdx.graphics.getBackBufferHeight() * 4;
			for (int i = 3; i < size; i += 4) {
				pixels.put(i, (byte) 255);
			}

			PixmapIO.writePNG(Gdx.files.local(screenshot_path), pixmap, Deflater.DEFAULT_COMPRESSION, true);
			pixmap.dispose();
			
		}

		test_interupt();
		
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			block_custom_metodes = false; }

		if (frame_counter < 2) stopscreen();
		if (START_FULLSCREEN && frame_counter - forced_frame == 3) ask_fs = true;
		if (ask_fs) do_fullscreen();
		if (ask_wn) do_window();
		if (ask_sw) do_switchscreen();

		if (to_title_flag2) { to_title_flag2 = false; main.close_app(); }
		if (to_title_flag) { to_title_flag = false; to_title_flag2 = true; }
		
	}
	
	
	public long javaHeap = 0;
	public long nativeHeap = 0;
	
	
	
	

	private int forced_frame = 0;
	public void force_nodraw_frame(int n) {
		for (int i = 0 ; i < n ; i++) { 

			frame_counter++; 
			forced_frame++;
			
			test_interupt();
			
			if (!block_custom_metodes) {
				pre_draw();
				if (listener != null) listener.pre_draw();
			}
			
			test_interupt();
			
			if (!block_custom_metodes) {
				post_draw();
				if (listener != null) listener.post_draw();
			}

			test_interupt();
			
			// cancel all drawing transforms
			drawer.transf.reset();
						
		}
	}
	private int nodraw_frame = 0;
	public void add_nodraw_frame(int n) { nodraw_frame += n; }
	private void try_nodraw_frame() {
		if (nodraw_frame == 0) return;
		for (int i = 0 ; i < nodraw_frame ; i++) { 

			frame_counter++;
			forced_frame++;
			
			test_interupt();
			
			if (!block_custom_metodes) {
				pre_draw();
				if (listener != null) listener.pre_draw();
			}

			test_interupt();
			
			if (!block_custom_metodes) {
				post_draw();
				if (listener != null) listener.post_draw();
			}

			test_interupt();
			
			// cancel all drawing transforms
			drawer.transf.reset();
			
		}
		nodraw_frame = 0;
	}
	
	
	nMap<nRun> all_exec = new nMap<nRun>();
	nMap<nRun> crashing_exec = new nMap<nRun>();
	
	public boolean something_crashed = false;

	nMap<TimeTrack> timeTracker = new nMap<TimeTrack>();
	
	public boolean exec_nothrow(String ref, nRun run) {
		if (!CATCH_THROW) { run.run(); return true; }
		test_interupt();
		if (!all_exec.hasKey(ref)) all_exec.put(ref,run); 
		if (!timeTracker.hasKey(ref)) {
			TimeTrack tr = new TimeTrack(ref);
			timeTracker.put(ref,tr); 
		}
//		if (all_exec.hasKey(ref) && all_exec.get(ref) != run) {
//			log("INFO : exec_nothrow : the key "+ref+" point to another runnable in all_exec");
//		}
		
		if (block_custom_metodes || crashing_exec.hasKey(ref)) return false;
		TimeTrack tr = timeTracker.get(ref);
		tr.start();
		try {
			run.run();
		} catch (Exception ex) {
			interupt();
			if (!crashing_exec.hasKey(ref)) crashing_exec.put(ref,run);
			ex.printStackTrace(System.out);
			Utl.logn("ERROR : exec_nothrow  < "+ref+" >  catched an Exception. "
					+ "Render is paused, press space to continue");
			Utl.logn("          This exec is stored as crashing and will be ignored");
			test_interupt();
			return false;
		}
		tr.stop();
		if (PRINT_TIMETRACK) Utl.logn("exec "+ref+" med duration: "+tr.tps_med);
		test_interupt();
		return true;
	}
	
	private boolean exeption_interupt = false;
	private boolean block_custom_metodes = false;
	void test_interupt() {
		if (exeption_interupt) {
			exeption_interupt = false;
			block_custom_metodes = true;
		}
		if (!CATCH_THROW) block_custom_metodes = false;
	}
	public void interupt() {
		exeption_interupt = true;
		something_crashed = true;
	}
	
	
	private boolean ask_screenshot = false;
	private String screenshot_base_path = "screenshot";
	private String screenshot_path = "";
	private int scrnshtcnt = 0;
	public void screenshot() {
		scrnshtcnt = 0;
		screenshot_path = screenshot_base_path + scrnshtcnt + ".png";
		while (Utl.file_exist(screenshot_path)) { 
			scrnshtcnt++;
			screenshot_path = screenshot_base_path + scrnshtcnt + ".png"; }
		ask_screenshot = true;
	}
	

	private boolean ask_fs = false, ask_wn = false, ask_sw = false;
	public void fullscreen() {
		ask_fs = true;
	}
	public void window() {
		ask_wn = true;
	}
	public void switchscreen() {
		ask_sw = true;
	}
	public void stopscreen() {
		ask_fs = false; ask_wn = false; ask_sw = false;
	}
	private void do_fullscreen() {
		ask_fs = false;
        if (!Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            screenwidth = Gdx.graphics.getDisplayMode().width; 
            screenheight = Gdx.graphics.getDisplayMode().height;
	        resize(screenwidth, screenheight); }
	}
	private void do_window() {
		ask_wn = false;
        if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode(WIDTH, HEIGHT);
            screenwidth = WIDTH; screenheight = HEIGHT;
	        resize(screenwidth, screenheight); }
	}
	private void do_switchscreen() {
		ask_sw = false;
        if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode(WIDTH, HEIGHT);
            screenwidth = WIDTH; screenheight = HEIGHT;
	        resize(screenwidth, screenheight); }
        else { 
        		Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
	        screenwidth = Gdx.graphics.getDisplayMode().width; 
	        screenheight = Gdx.graphics.getDisplayMode().height;
	        resize(screenwidth, screenheight); }
	}
	public boolean isfullscreen() { return Gdx.graphics.isFullscreen(); }

	public float getscreenwidth() { return screenwidth; }
	public float getscreenheight() { return screenheight; }

	public int screenwidth = WIDTH, screenheight = HEIGHT;
	ArrayList<nRun> eventsScreen = new ArrayList<nRun>();

	public GdxApp addEventScreen(nRun r) { eventsScreen.add(r); return this; }
	public GdxApp removeEventScreen(nRun r) { eventsScreen.remove(r); return this; }
	
	

	private boolean show_cursor = false;

	public void cursor(boolean c) {
		if (c && c != show_cursor) {
			Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
		} else if (!c && c != show_cursor) {
			Gdx.graphics.setSystemCursor(SystemCursor.None);
		}
		show_cursor = c;
	}

	

	enum DebugShape { LINE, POINT, RECT }
	class Debug {
		DebugShape shape;
		Vector2 v1,v2;
		public Debug() {}
		public void init_rect(Vector2 _v1, Vector2 _v2) { shape = DebugShape.RECT; v1 = _v1; v2 = _v2; }
		public void init_line(Vector2 _v1, Vector2 _v2) { shape = DebugShape.LINE; v1 = _v1; v2 = _v2; }
		public void init(Vector2 _v1) { shape = DebugShape.POINT; v1 = _v1; }
		public void draw() {
			if (shape == DebugShape.RECT) {
				drawer.noFill(); drawer.stroke(255,255,255,255,4); drawer.rect(v1.x,v1.y,v2.x,v2.y); }
			else if (shape == DebugShape.LINE) {
				drawer.noFill(); drawer.stroke(255,255,255,255,4); drawer.line(v1,v2); }
			else if (shape == DebugShape.POINT) {
				drawer.noStroke(); drawer.fill(255,255); drawer.circle(v1.x,v1.y,4); }
		}
	}
	public Vector2 debug_ref = new Vector2();
	public float debug_scale = 1.0f, debug_rot = 0.0f;
	nPool<Debug> debugs = new nPool<Debug>() {
		protected Debug newObject() { return new Debug(); } };
	public void drawDebug() { 
		drawer.push(); 
		drawer.translate(debug_ref); 
		drawer.scale(debug_scale);
		drawer.rotate(debug_rot);
		for (Debug d : debugs.all()) d.draw();
		drawer.pop();
		debugs.freeAll(); 
		debug_ref.set(0,0); 
		debug_scale = 1.0f; 
		debug_rot = 0.0f; 
	}

	public void debugRect(Rectangle r) {
		debugs.obtain().init_rect(new Vector2(r.x,r.y),new Vector2(r.width,r.height)); }
	public void debugRect(float x1, float y1, float x2, float y2) {
		debugs.obtain().init_rect(new Vector2(x1,y1),new Vector2(x2,y2)); }
	public void debugRect(Vector2 v1, Vector2 v2) {
		debugs.obtain().init_rect(v1,v2); }
	public void debugLine(Vector2 v1, Vector2 v2) {
		debugs.obtain().init_line(v1,v2); }
	public void debugPoint(Vector2 v1) {
		debugs.obtain().init(v1); }
	public void debugPoint(float x, float y) {
		debugs.obtain().init(new Vector2(x,y)); }
	
	public void debugTransf(nTransform t) { 
		debugTransf(t.getTranslation(), t.getScale(), t.getRotation()); }
	public void debugTransf(Vector2 v) { debug_ref.set(v); }
	public void debugTransf(Vector2 v, float s, float r) { 
		debug_ref.set(v); debug_scale = s; debug_rot = r; }

	

	public static void crash() { Utl.logn(" -- FORCED CRASH -- "); String s = to_crash(); s+=s; }
	private static String to_crash() { return null; }
	
	
}
