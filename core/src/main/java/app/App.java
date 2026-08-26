package app;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.codedisaster.steamworks.SteamAPI;
import com.noodle.nodulo.GdxApp;
import com.noodle.nodulo.GdxApp.nAppListener;

import data.sData;
import gui.nAlign;
import gui.nGUI;
import util.Utl;
import util.nRun;
import util.nTransform;

public class App implements nAppListener, Runner, nDrawer.Drawer {
	
	public int LOADING_SCREEN_FRAME = 1;
	
	public GdxApp gdx;
	public nInput input;
	public sData data;
	public nGUI gui;
	
	public static App ap;

	ArrayList<nRun> eventInit = new ArrayList<nRun>();
	ArrayList<nRun> eventInitEnd = new ArrayList<nRun>();

	public void addEventInit(nRun n) { eventInit.add(n); }
	public void addEventInitEnd(nRun n) { eventInitEnd.add(n); }
	
	@Override
	public void setup(GdxApp a) {
		gdx = a; ap = this;
		
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

	    data = new sData(this);
	    
		input = new nInput(this);

		gui = new nGUI(this);

	}

	@Override
	public void closing() {
		
		data.dispose();

		gui.dispose();
		
//		SteamAPI.shutdown();
	}

	public void startup() { do_startup = true; }
	private boolean do_startup = false;
	
	protected void do_startup() {
		
		LOADING_SCREEN_FRAME = 3;

		nRun.runEvents(eventInit, data.root_bloc);
		
		addDelayEvent(1, new nRun() { public void run() {
			gdx.add_nodraw_frame(40); 
		}});
		addDelayEvent(42, new nRun() { public void run() {

			LOADING_SCREEN_FRAME = 3;

			addDelayEvent(1, new nRun() { public void run() {
				gdx.add_nodraw_frame(40); 
			}});
			addDelayEvent(2, new nRun() { public void run() {

				nRun.runEvents(eventInitEnd, data.root_bloc);
				
			}});
		}});
	}
	
	protected void gui_frame() { }
	protected void gui_draw() { }
	public void draw_end() {}
	public void setInputProcessor() {}
	
	@Override
	public void pre_draw() {

//		if (SteamAPI.isSteamRunning()) {
//		    SteamAPI.runCallbacks();
//		}
		
		gdx.exec_nothrow("input.frame_str()", new nRun() { public void run() {	
			//sInput
			if (input != null) input.frame_str();
		}});
		
		gdx.exec_nothrow("data.frame_start()", new nRun() { public void run() {	
			//data update
			if (data != null) data.frame_start();
		}});

		gdx.exec_nothrow("runEvents(eventsFrame)", new nRun() { public void run() {	
			// frame event
			nRun.runEvents(runFrameStart);
		}});

		gdx.exec_nothrow("runEvents(eventsNextFrame)", new nRun() { public void run() {	
			if (!active_nxtfrm_pile) { nRun.runEvents(eventsNextFrame1); eventsNextFrame1.clear(); } 
		    else { nRun.runEvents(eventsNextFrame2); eventsNextFrame2.clear(); } 
		    active_nxtfrm_pile = !active_nxtfrm_pile;
		}});

		gdx.exec_nothrow("runEvents(delay_events)", new nRun() { public void run() {	
		    for (int i = delay_events.size() - 1 ; i >= 0 ; i--) {
		    		DelayEvent d = delay_events.get(i);
		    		d.delay -= 1;
		    		if (d.delay <= 0) {
		    			d.event.run();
		    			delay_events.remove(d);
		    		}
		    }
		}});
		
		gdx.exec_nothrow("gui_frame()", new nRun() { public void run() {	
			gui.frame(); gui_frame(); 
		}});

		gdx.exec_nothrow("runEvents(runFrame, delta)", new nRun() { public void run() {	
			//update
			nRun.runEvents(runFrame, Gdx.graphics.getDeltaTime());
		}});
		
	}
	
	public void draw_start() {

		gdx.exec_nothrow("gui.draw_start()", new nRun() { public void run() {	
			
			gui.draw_start(); 
			
		}});
		
	}

	@Override
	public void drawer_draw() {

		gdx.exec_nothrow("gui_draw()", new nRun() { public void run() {	
			
			gui.draw(); 
			
			gui_draw();
			
		}});
		
		if (LOADING_SCREEN_FRAME > 0) {
			LOADING_SCREEN_FRAME--;
			noStroke(); fill(20,255);
			rect(gdx.screenrect);
			textAlign(nAlign.CENTER, nAlign.CENTER);
			text("LOADING Please wait ...", gdx.screenrect.width / 2f, gdx.screenrect.height / 2f, 20);
		}
		
//		//mouse pointer
//		drawer.filledCircle(input.mouse.x, input.mouse.y, 2);
//		
	}

	@Override
	public void post_draw() {

		// gui debug
//		if (input.getClick('P')) gui.print_state();

		gdx.exec_nothrow("runEvents(eventsFrameEnd)", new nRun() { public void run() {	
			// frame event
			nRun.runEvents(runFrameEnd);
		}});

		gdx.exec_nothrow("data.frame_end()", new nRun() { public void run() {	
			//data update
			if (data != null) data.frame_end();
		}});

		gdx.exec_nothrow("input.frame_end()", new nRun() { public void run() {	
			//sInput
			if (input != null) input.frame_end();
		}});
		
		if (do_startup) { do_startup = false; do_startup(); }
	}


	
	
	//	------------   EVENT   ------------
	
	public App clearAllEvent() { 
		eventsNextFrame1.clear(); 
		eventsNextFrame2.clear();
		delay_events.clear();
		return this; 
	}
	
	ArrayList<nRun> runFrame = new ArrayList<nRun>();
	public ArrayList<nRun> runFrameStart = new ArrayList<nRun>();
	ArrayList<nRun> runFrameEnd = new ArrayList<nRun>();
	ArrayList<nRun> eventsNextFrame1 = new ArrayList<nRun>();
	ArrayList<nRun> eventsNextFrame2 = new ArrayList<nRun>();
	boolean active_nxtfrm_pile = false;

	public void addRunFrame(nRun r) { runFrame.add(r); }
	public void removeRunFrame(nRun r) { runFrame.remove(r); }
	public void addRunFrameStart(nRun r) { runFrameStart.add(r); }
	public void removeRunFrameStart(nRun r) { runFrameStart.remove(r); }
	public void addRunFrameEnd(nRun r) { runFrameEnd.add(r); }
	public void removeRunFrameEnd(nRun r) { runFrameEnd.remove(r); }
	public void addEventNextFrame(nRun r) { 
		if (active_nxtfrm_pile) eventsNextFrame1.add(r); else eventsNextFrame2.add(r); }

	private class DelayEvent {
		public nRun event;
		public int delay = 0; 
		public DelayEvent(int d, nRun r) { delay = d; event = r; } }

	ArrayList<DelayEvent> delay_events = new ArrayList<DelayEvent>();
	public void addDelayEvent(int delay, nRun r) { 
		if (delay <= 0) { r.run(); return; }
		delay_events.add(new DelayEvent(delay, r)); }
	
	
	
	
	
	
	
	
	
	

	public void flush() { gdx.drawer.flush(); }
	public void use_fx(boolean v) { gdx.drawer.USE_FX = v; }
	public void fx() { gdx.drawer.fx(); }
	public void noFx() { gdx.drawer.noFx(); }
	public Matrix4 getTransformMatrix() { return gdx.drawer.getTransformMatrix(); }

	public void halo(Vector2 p, float r, Color c1, Color c2) {
		gdx.drawer.halo(p,r,c1,c2); }
	
	public void line(float x1, float y1, float x2, float y2, 
			Color c1, Color c2) {
		gdx.drawer.line(x1,y1,x2,y2,c1,c2);
	}
	public void face(float x1, float y1, float x2, float y2, 
			float x3, float y3, Color c1, Color c2, Color c3) {
		gdx.drawer.face(x1,y1,x2,y2,x3,y3,c1,c2,c3);
	}

	public void transf(boolean b) { 
		gdx.drawer.transf(b); } 
	public void push() { 
		gdx.drawer.push(); }
	public void pop() { 
		gdx.drawer.pop(); }
	public void transf(nTransform t) { 
		gdx.drawer.transf(t); }
	public void translate(float x, float y) { 
		gdx.drawer.translate(x,y); }
	public void translate(Vector2 v) { 
		gdx.drawer.translate(v); }
	public void scale(float s) { 
		gdx.drawer.scale(s); }
	public void rotate(float s) { 
		gdx.drawer.rotate(s); }
	
	
	public float textWidth(String t,float s) { 
		return gdx.drawer.textWidth(t,s); }
	public float textWidth(char t,float s) { 
		return gdx.drawer.textWidth(t,s); }
	public float textHeight() { 
		return gdx.drawer.textHeight(); }
	public void textAlign(nAlign ax, nAlign ay) {
		gdx.drawer.textAlign(ax,ay); }
	public void text(String t, Vector2 v, float s) {
		gdx.drawer.text(t,v,s); }
	public void text(String t, Vector2 v, float s, Color c) {
		gdx.drawer.text(t,v,s,c); }
	public void text(String t, float x, float y, float s) {
		gdx.drawer.text(t,x,y,s); }
	public void text(String t, float x, float y, float s, Color c) {
		gdx.drawer.text(t,x,y,s,c); }
	
	
	

	public void rect(Rectangle n) {
		gdx.drawer.rect(n); }
	public void rect(float x, float y, float w, float h) {
		gdx.drawer.rect(x,y,w,h); }
	
	public void circle(Rectangle r) {
		gdx.drawer.circle(r); }
	public void circle(float x, float y, float r) {
		gdx.drawer.circle(x,y,r); }
	
	public void line(Vector2 p1, Vector2 p2) {
		gdx.drawer.line(p1,p2); }
	public void line(float x1, float y1, float x2, float y2) {
		gdx.drawer.line(x1,y1,x2,y2); }
	
	public void polygon(Polygon p) {
		gdx.drawer.polygon(p); }
	public void polygon(Vector2 v0, Vector2 v1, Vector2 v2) {
		gdx.drawer.polygon(v0,v1,v2); }
	public void polygon(Vector2 v0, Vector2 v1, Vector2 v2, Vector2 v3) {
		gdx.drawer.polygon(v0,v1,v2,v3); }
	public void polygon(Vector2[] v) {
		gdx.drawer.polygon(v); }
	public void polygon(float[] v) {
		gdx.drawer.polygon(v); }
	
	public void diamond(Rectangle r) {
		gdx.drawer.diamond(r); }
	
	public void fill(Color c) {
		gdx.drawer.fill(c); }
	public void fill(int c) {
		gdx.drawer.fill(c); }
	public void fill(int l, int a) {
		gdx.drawer.fill(l,a); }
	public void fill(int r, int g, int b) {
		gdx.drawer.fill(r,g,b); }
	public void fill(int r, int g, int b, int a) {
		gdx.drawer.fill(r,g,b,a); }

	public void stroke(Color c) {
		gdx.drawer.stroke(c); }
	public void stroke(Color c, float w) {
		gdx.drawer.stroke(c,w); }
	public void stroke(int c) {
		gdx.drawer.stroke(c); }
	public void stroke(int l, float w) {
		gdx.drawer.stroke(l,w); }
	public void stroke(int r, int g, int b) {
		gdx.drawer.stroke(r,g,b); }
	public void stroke(int r, int g, int b, int a) {
		gdx.drawer.stroke(r,g,b,a); }
	public void stroke(int r, int g, int b, int a, float w) {
		gdx.drawer.stroke(r,g,b,a,w); }
	
	public void noFill() { 
		gdx.drawer.noFill(); }
	public void noStroke() { 
		gdx.drawer.noStroke(); }
	public void strokeWeight(float strokeW) { 
		gdx.drawer.strokeWeight(strokeW); }

	public void point(Vector2 v) { 
		gdx.drawer.point(v); }
	public void point(float x, float y) {
		gdx.drawer.point(x,y); }
	public void point(float x, float y, Color c) {
		gdx.drawer.point(x,y,c); }

	
	
	
	
	
	
	
}
