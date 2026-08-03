package app;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.BloomEffect;
import com.crashinvaders.vfx.effects.GaussianBlurEffect;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer.Renderer;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer.RendererAdapter;
import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.generator.cellular.CellularAutomataGenerator;
import com.github.czyzby.noise4j.map.generator.noise.NoiseGenerator;
import com.github.czyzby.noise4j.map.generator.room.dungeon.DungeonGenerator;
import com.github.czyzby.noise4j.map.generator.util.Generators;

import box2dLight.RayHandler;
import data.*;
import gui.nAlign;

import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class GDXApplet implements ApplicationListener {


//	public static boolean CATCH_THROW = true;
	public static boolean CATCH_THROW = false;

//	public static boolean PRINT_TIMETRACK = true;
	public static boolean PRINT_TIMETRACK = false;
	
	//default
	public GDXApplet() { }
	
	public GDXApplet(String t) { window_title = t; }
	
	public static boolean START_FULLSCREEN = false;

	public static int WIDTH = 500;
	public static int HEIGHT = 500;
	
	// SOLO WINDOW
//	public static int WIDTH = 1300;
//	public static int HEIGHT = 960;
		
	// DOUBLE WINDOW
//	public static int WIDTH = 900;
//	public static int HEIGHT = 800;
	
	
	





	public String window_title = "";

	public BitmapFont bitmapfont;

    public OrthographicCamera camera; 
	public ScreenViewport viewport; 
	public Timer timer;
	
	public long frame_counter = 0;
	public Rectangle screenrect = new Rectangle(0,0,Applet.WIDTH,Applet.HEIGHT);

//	static Cursor emptyCursor;
//	Cursor cursorshow, cursorhide;
//	int xHotspot, yHotspot;
//	boolean hwVisible = false;
//	Texture cursor;

	public boolean USE_FX = false;

	public nDrawer drawer;
	public Color buffer_clear_color = Color.WHITE;
	
	public void fx() { if (USE_FX) drawer.fx(); }
	public void noFx() { if (USE_FX) drawer.noFx(); }

	public void pause_batch() { drawer.pause_batch(); }
	public void restart_batch() { drawer.restart_batch(); }

	public Matrix4 getTransformMatrix() { return drawer.getTransformMatrix(); }
	public void flush() { drawer.flush(); }

	
	
	
	
	
	
	
	
	/*
	 * 				NOISE4J  -  map generator
	 * 		TODO
	 * */

    private static void noiseStage(final Grid grid, final NoiseGenerator noiseGenerator, final int radius,
            final float modifier) {
        noiseGenerator.setRadius(radius);
        noiseGenerator.setModifier(modifier);
        // Seed ensures randomness, can be saved if you feel the need to
        // generate the same map in the future.
        noiseGenerator.setSeed(Generators.rollSeed());
        noiseGenerator.generate(grid);
    }
    public static Texture noiseGenerator() {

        final Pixmap map = new Pixmap(512, 512, Format.RGBA8888);
        final Grid grid = new Grid(512);
		NoiseGenerator noiseGenerator = new NoiseGenerator();
		noiseStage(grid, noiseGenerator, 32, 0.6f);
        noiseStage(grid, noiseGenerator, 16, 0.2f);
        noiseStage(grid, noiseGenerator, 8, 0.1f);
        noiseStage(grid, noiseGenerator, 4, 0.1f);
        noiseStage(grid, noiseGenerator, 1, 0.05f);

        final Color color = new Color();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                final float cell = grid.get(x, y);
                color.set(cell, cell, cell, 1f);
                map.drawPixel(x, y, Color.rgba8888(color));
            }
        }

        Texture texture = new Texture(map);
//        batch.begin();
//        batch.draw(texture, 0f, 0f);
//        batch.end();
//        texture.dispose();
        return texture;
    }
    
    public static Texture cellularGenerator() {

        final Pixmap map = new Pixmap(512, 512, Format.RGBA8888);
        final Grid grid = new Grid(512);

        final CellularAutomataGenerator cellularGenerator = new CellularAutomataGenerator();
        cellularGenerator.setAliveChance(0.5f);
        cellularGenerator.setIterationsAmount(4);
        cellularGenerator.generate(grid);
        
        //bigger isle
//        final CellularAutomataGenerator cellularGenerator = new CellularAutomataGenerator();
//        cellularGenerator.setAliveChance(0.5f);
//        cellularGenerator.setRadius(2);
//        cellularGenerator.setBirthLimit(13);
//        cellularGenerator.setDeathLimit(9);
//        cellularGenerator.setIterationsAmount(6);
//        cellularGenerator.generate(grid);

        final Color color = new Color();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                final float cell = grid.get(x, y);
                color.set(cell, cell, cell, 1f);
                map.drawPixel(x, y, Color.rgba8888(color));
            }
        }

        Texture texture = new Texture(map);
//        batch.begin();
//        batch.draw(texture, 0f, 0f);
//        batch.end();
//        texture.dispose();
        return texture;
    }

    public static Texture dungeonGenerator() {
    	final Pixmap map = new Pixmap(512, 512, Format.RGBA8888);
        final Grid grid = new Grid(512); // This algorithm likes odd-sized maps, although it works either way.

        final DungeonGenerator dungeonGenerator = new DungeonGenerator();
        dungeonGenerator.setRoomGenerationAttempts(500);
        dungeonGenerator.setMaxRoomSize(75);
        dungeonGenerator.setTolerance(10); // Max difference between width and height.
        dungeonGenerator.setMinRoomSize(9);
        dungeonGenerator.generate(grid);

//        final DungeonGenerator dungeonGenerator = new DungeonGenerator();
//        dungeonGenerator.setRoomGenerationAttempts(200);
//        dungeonGenerator.setMaxRoomSize(25);
//        dungeonGenerator.setTolerance(6);
//        dungeonGenerator.setMinRoomSize(9);
//        dungeonGenerator.setWindingChance(0.5f); // More chaotic!
//        dungeonGenerator.setDeadEndRemovalIterations(5); // Introducing dead ends.
//        dungeonGenerator.setRandomConnectorChance(0f); // One way to solve the maze.
//        dungeonGenerator.generate(grid);
        
        final Color color = new Color();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                final float cell = 1f - grid.get(x, y);
                color.set(cell, cell, cell, 1f);
                map.drawPixel(x, y, Color.rgba8888(color));
            }
        }

        Texture texture = new Texture(map);
//        batch.begin();
//        batch.draw(texture, 0f, 0f);
//        batch.end();
//        texture.dispose();
        return texture;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		
		
		// use libGDX's default font
//		bitmapfont = new BitmapFont();
		
		FreeTypeFontGenerator fontgenerator = new FreeTypeFontGenerator(
				Gdx.files.internal("Mx437_IBM_BIOS-2y.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
//		parameter.borderWidth = basetxtSize/20f;
//		parameter.borderColor = Color.BLACK; 
//		parameter.borderStraight = true;
		parameter.size = (int) basetxtSize;
		bitmapfont = fontgenerator.generateFont(parameter);
		fontgenerator.dispose();
		
		
		camera = new OrthographicCamera(WIDTH, HEIGHT);
		viewport = new ScreenViewport(camera);

		camera.position.set(WIDTH / 2, HEIGHT / 2, 0);
		camera.update();

		//font has 15pt, but we need to scale it to our viewport by ratio of viewport height to screen height
		bitmapfont.setUseIntegerPositions(false);
		//		bitmapfont.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

		bitmapfont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		drawer = new nDrawer(this, false);
		
////		Pixmap pixmaphide = new Pixmap(1, 1, Format.RGBA8888);
//		Pixmap pixmapshow = new Pixmap(Gdx.files.internal("cursor.png"));
//		// Set hotspot to the middle of it (0,0 would be the top-left corner)
//		xHotspot = 15; yHotspot = 15;
//		Cursor cursorshow = Gdx.graphics.newCursor(pixmapshow, xHotspot, yHotspot);
////		Cursor cursorhide = Gdx.graphics.newCursor(pixmaphide, 0, 0);
//		pixmap.dispose(); // We don't need the pixmap anymore
//		Gdx.graphics.setCursor(cursorshow);


//		cursor = new Texture(Gdx.files.internal("data/cursor.png"));
//		xHotspot = 0;
//		yHotspot = cursor.getHeight(); // lower left origin!

		
		timer = new Timer();

		Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
		Gdx.graphics.setFullscreenMode(currentMode);
		Gdx.graphics.setWindowedMode(WIDTH, HEIGHT);
		
		build_types();
		
		setup();
		
	}
	
	public void setup() {}
	
	@Override
	public void render() {
		
		try_nodraw_frame();
		
		if (close_app_flag) Gdx.app.exit();
		
		frame_counter++;
		
		javaHeap = Gdx.app.getJavaHeap();
		nativeHeap = Gdx.app.getNativeHeap();

//		try {
//			setHWCursorVisible(hwVisible);
//		} catch (LWJGLException e) {
//			throw new GdxRuntimeException(e);
//		}

		test_interupt();
		
		if (!block_custom_metodes) pre_draw();

		test_interupt();

		//Draw
		
		drawer.draw_begin();
		
		test_interupt();
		
		if (!block_custom_metodes) screen_draw();

		test_interupt();
		
		//draw debug shapes
		drawDebug();
		
		//color screen red if something crash
		if (something_crashed) {
			fill(255,0,0,30); noStroke();
			rect(screenrect); }

		// draw SW cursor
		if (show_cursor) {
			fill(255); stroke(0,3f);
			push();
			translate(Gdx.input.getX(), screenrect.height-Gdx.input.getY());
			polygon(new Vector2(0,2),new Vector2(0,-24),new Vector2(12,-18)); 
			pop();
		} 
		
		drawer.end();
		
        
		test_interupt();
		
		if (!block_custom_metodes) post_draw();

		test_interupt();
		
		// cancel all drawing transforms
		transf.reset();
		
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			block_custom_metodes = false; }

		if (frame_counter < 2) stopscreen();
		if (START_FULLSCREEN && frame_counter - forced_frame == 3) ask_fs = true;
		if (ask_fs) do_fullscreen();
		if (ask_wn) do_window();
		if (ask_sw) do_switchscreen();
		
	}
	
	
	public long javaHeap = 0;
	public long nativeHeap = 0;
	
	
	
	

	private int forced_frame = 0;
	public void force_nodraw_frame(int n) {
		for (int i = 0 ; i < n ; i++) { 

			frame_counter++; 
			forced_frame++;
			
			test_interupt();
			
			if (!block_custom_metodes) pre_draw();

			test_interupt();
			
			if (!block_custom_metodes) post_draw();

			test_interupt();
			
			// cancel all drawing transforms
			transf.reset();
			
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
			
			if (!block_custom_metodes) pre_draw();

			test_interupt();
			
			if (!block_custom_metodes) post_draw();

			test_interupt();
			
			// cancel all drawing transforms
			transf.reset();
			
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
			logn("ERROR : exec_nothrow  < "+ref+" >  catched an Exception. "
					+ "Render is paused, press space to continue");
			logn("          This exec is stored as crashing and will be ignored");
			test_interupt();
			return false;
		}
		tr.stop();
		if (PRINT_TIMETRACK) logn("exec "+ref+" med duration: "+tr.tps_med);
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
	
	
	
	

	private boolean show_cursor = false;

	public void cursor(boolean c) {
		if (c && c != show_cursor) {
//			hwVisible = true;
		} else if (!c && c != show_cursor) {
//			hwVisible = false;
		}
		show_cursor = c;
	}

//	private static void setHWCursorVisible(boolean visible) 
////			throws LWJGLException 
//			{
////		if (Gdx.app.getType() != ApplicationType.Desktop && Gdx.app instanceof LwjglApplication)
////			return;
//		if (emptyCursor == null) {
//			if (Mouse.isCreated()) {
//				int min = org.lwjgl.input.Cursor.getMinCursorSize();
//				IntBuffer tmp = BufferUtils.createIntBuffer(min * min);
//				emptyCursor = new org.lwjgl.input.Cursor(min, min, min / 2, min / 2, 1, tmp, null);
//			} else {
//				throw new LWJGLException(
//						"Could not create empty cursor before Mouse object is created");
//			}
//		}
//		if (Mouse.isInsideWindow())
//			Mouse.setNativeCursor(visible ? null : emptyCursor);
//	}

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

	public GDXApplet addEventScreen(nRun r) { eventsScreen.add(r); return this; }
	public GDXApplet removeEventScreen(nRun r) { eventsScreen.remove(r); return this; }
	
	public void pre_draw() {}
	public void post_draw() {}
	public void screen_draw() {}

	private boolean close_app_flag = false;
	public void close_app() { close_app_flag = true; }

	public void closing() {}

	@Override
	public void dispose() {
		closing();

//		cursor.dispose();
		
		bitmapfont.dispose();

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
        
//        buffer.reset();
//        buffer.initialize(width, height);
		
        nRun.runEvents(eventsScreen); 
	}
	@Override
	public void pause() { }
	@Override
	public void resume() { }


	
	
	

	//        --- drawing helper ---
	
	
	
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
				noFill(); stroke(255,255,255,255,4); rect(v1.x,v1.y,v2.x,v2.y); }
			else if (shape == DebugShape.LINE) {
				noFill(); stroke(255,255,255,255,4); line(v1,v2); }
			else if (shape == DebugShape.POINT) {
				noStroke(); fill(255,255); circle(v1.x,v1.y,4); }
		}
	}
	public Vector2 debug_ref = new Vector2();
	public float debug_scale = 1.0f, debug_rot = 0.0f;
	nPool<Debug> debugs = new nPool<Debug>() {
		protected Debug newObject() { return new Debug(); } };
	public void drawDebug() { 
		push(); 
		translate(debug_ref); 
		scale(debug_scale);
		rotate(debug_rot);
		for (Debug d : debugs.all()) d.draw();
		pop();
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

	
	
	
	
	public nTransform transf = new nTransform();
	public void push() { transf.push(); }
	public void pop() { transf.pop(); }
	public void transf(nTransform t) { transf.transf(t); }
	public void translate(float x, float y) { transf.translate(x, y); }
	public void translate(Vector2 v) { transf.translate(v.x, v.y); }
	public void scale(float s) { transf.scale(s); }
	public void rotate(float s) { transf.rotate(s); }
	
	
	
	
	public static final char[] Alphabet = {'0','1','2','3','4','5','6','7','8','9',
			'A','B','C','D','E','F','G','H','I','J','K','L','M',
			'N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
			'a','b','c','d','e','f','g','h','i','j','k','l','m',
			'n','o','p','q','r','s','t','u','v','w','x','y','z'};

	private nAlign textAlignmentX = nAlign.CENTER;
	private nAlign textAlignmentY = nAlign.CENTER;
	private float txtSize = basetxtSize;
	private float txtSizeTransf = basetxtSize;
	private static final float basetxtSize = 64;
	// to redo correctly
	public float txtCharSize = txtSize/2f;
	// 	game.font.getBounds(t.subSequence(0,t.length()-1));
	public float textWidth(String t) { return txtCharSize * t.length(); }
	public float textWidth(char t) { return txtCharSize; }
	public float textHeight() { return bitmapfont.getLineHeight(); }
	public GDXApplet textAlign(nAlign ax, nAlign ay) {
		textAlignmentX = ax;
		textAlignmentY = ay;
		return this;
	}
	public GDXApplet text(String t, Vector2 v, float s) {
		return text(t,v.x,v.y,s,Color.WHITE); }
	public GDXApplet text(String t, Vector2 v, float s, Color c) {
		return text(t,v.x,v.y,s,c); }
	public GDXApplet text(String t, float x, float y, float s) {
		return text(t,x,y,s,Color.WHITE); }
	public GDXApplet text(String t, float x, float y, float s, Color c) {
		txtSizeTransf = s * transf.getScale();
		txtSize = txtSizeTransf;
		txtCharSize = txtSizeTransf/2f;
		float f = txtSize/basetxtSize;
		bitmapfont.getData().setScale(f);
		float alignmentOffsetX = 0;
		float alignmentOffsetY = 0;
		if (textAlignmentX == nAlign.CENTER) 
			alignmentOffsetX = -textWidth(t) / (2.0f * transf.getScale());
		else if (textAlignmentX == nAlign.RIGHT) 
			alignmentOffsetX = -textWidth(t) / transf.getScale();
		if (textAlignmentY == nAlign.CENTER) 
			alignmentOffsetY = bitmapfont.getLineHeight() / (2.0f * transf.getScale());
		else if (textAlignmentY == nAlign.BOTTOM) 
			alignmentOffsetY = bitmapfont.getLineHeight() / transf.getScale();
		x += alignmentOffsetX;
		y += alignmentOffsetY;
		Vector2 p = transf.transform(x, y);
		bitmapfont.setColor(c);
		bitmapfont.draw(drawer.spritebatch, t, p.x, p.y);
		return this;
	}
	
	static public float distanceSegmentPoint(Vector2 s1, Vector2 s2, Vector2 p) {
		return Intersector.distanceSegmentPoint(s1, s2, p); }
	
	
//	private final static FloatArray floatArray = new FloatArray();
//	private final static FloatArray floatArray2 = new FloatArray();
//	private final static Vector2 ip = new Vector2();
//	private final static Vector2 ep1 = new Vector2();
//	private final static Vector2 ep2 = new Vector2();
//	private final static Vector2 s = new Vector2();
//	private final static Vector2 e = new Vector2();
//	public static boolean intersectPolygons (Polygon p1, Polygon p2, Polygon overlap) {
////		Applet.app.log("intersectPolygons");
//		if (p1.getVertices().length == 0 || p2.getVertices().length == 0) {
////			Applet.app.log("getVertices().length == 0");
//			return false;
//		}
//		floatArray.clear();
//		floatArray2.clear();
//		floatArray2.addAll(p1.getTransformedVertices());
//		float[] vertices2 = p2.getTransformedVertices();
//		for (int i = 0, last = vertices2.length - 2; i <= last; i += 2) {
//			ep1.set(vertices2[i], vertices2[i + 1]);
//			// wrap around to beginning of array if index points to end;
//			if (i < last)
//				ep2.set(vertices2[i + 2], vertices2[i + 3]);
//			else
//				ep2.set(vertices2[0], vertices2[1]);
//			if (floatArray2.size == 0) {
////				Applet.app.log("floatArray2.size == 0");
//				return false;
//			}
//			s.set(floatArray2.get(floatArray2.size - 2), floatArray2.get(floatArray2.size - 1));
//			for (int j = 0; j < floatArray2.size; j += 2) {
//				e.set(floatArray2.get(j), floatArray2.get(j + 1));
//				// determine if point is inside clip edge
//				boolean side = Intersector.pointLineSide(ep2, ep1, s) > 0;
//				if (Intersector.pointLineSide(ep2, ep1, e) > 0) {
//					if (!side) {
//						Intersector.intersectLines(s, e, ep1, ep2, ip);
//						if (floatArray.size < 2 || floatArray.get(floatArray.size - 2) != ip.x
//							|| floatArray.get(floatArray.size - 1) != ip.y) {
//							floatArray.add(ip.x);
//							floatArray.add(ip.y);
//						}
//					}
//					floatArray.add(e.x);
//					floatArray.add(e.y);
//				} else if (side) {
//					Intersector.intersectLines(s, e, ep1, ep2, ip);
//					floatArray.add(ip.x);
//					floatArray.add(ip.y);
//				}
//				s.set(e.x, e.y);
//			}
//			floatArray2.clear();
//			floatArray2.addAll(floatArray);
//			floatArray.clear();
//		}
//		// Ensure first and last point are different
//		if (floatArray2.size >= 6 && floatArray2.get(0) == floatArray2.get(floatArray2.size - 2)
//			&& floatArray2.get(1) == floatArray2.get(floatArray2.size - 1)) floatArray2.setSize(floatArray2.size - 2);
//		// Check for 3 or more vertices needed due to floating point precision errors
//		if (floatArray2.size >= 6) {
//			if (overlap != null) {
//				overlap.resetTransformations();
//				if (overlap.getVertices().length == floatArray2.size)
//					System.arraycopy(floatArray2.items, 0, overlap.getVertices(), 0, floatArray2.size);
//				else
//					overlap.setVertices(floatArray2.toArray());
//			}
////			Applet.app.log("floatArray2.size >= 6 return true");
//			return true;
//		}
////		Applet.app.log("floatArray2.size < 6");
//		return false;
//	}
	
	
	/** Determines whether the supplied rectangles intersect and, if they do,
	 *  sets the supplied {@code intersection} rectangle to the area of overlap.
	 * 
	 * @return whether the rectangles intersect
	 */
	static public boolean intersect(Rectangle rectangle1, Rectangle rectangle2) {
	    return rectangle1.overlaps(rectangle2); }
	static public boolean intersect(Rectangle rectangle1, Rectangle rectangle2, Rectangle intersection) {
	    if (rectangle1.overlaps(rectangle2)) {
	        intersection.x = Math.max(rectangle1.x, rectangle2.x);
	        intersection.width = Math.min(rectangle1.x + rectangle1.width, rectangle2.x + rectangle2.width) - intersection.x;
	        intersection.y = Math.max(rectangle1.y, rectangle2.y);
	        intersection.height = Math.min(rectangle1.y + rectangle1.height, rectangle2.y + rectangle2.height) - intersection.y;
	        return true;
	    }
	    return false;
	}
	
	public static Vector2 getRectCenter(Rectangle r) {
		return new Vector2(r.x+r.width/2f,r.y+r.height/2f); }

	public GDXApplet filledrect(Rectangle r, Color c) {
		drawer.drawer.filledRectangle(transf.transform(r), c); return this; }
	public GDXApplet filledrect(Rectangle r) {
		drawer.drawer.filledRectangle(transf.transform(r), color_fill); return this; }
	public GDXApplet strokerect(Rectangle r) {
		drawer.drawer.rectangle(transf.transform(r), color_stroke); return this; }
	public GDXApplet strokerect(float x, float y, float w, float h) {
		Rectangle r = transf.transform(new Rectangle(x,y,w,h));
		drawer.drawer.rectangle(r.x, r.y, r.width, r.height, color_stroke, strokeW * transf.getScale()); return this; }

	public GDXApplet rect(Rectangle n) {
		Rectangle r = transf.transform(n);
		if (do_fill) drawer.drawer.filledRectangle(r.x, r.y, r.width, r.height, color_fill);
		if (do_stroke) drawer.drawer.rectangle(r.x, r.y, r.width, r.height, color_stroke, strokeW * transf.getScale()); 
		return this; }
	public GDXApplet rect(float x, float y, float w, float h) {
		rect(new Rectangle(x,y,w,h)); return this; }
	
//	public GDXApplet rect(Rectangle r) {
//		rect(r.x, r.y, r.width, r.height); return this; }
//	public GDXApplet rect(float x, float y, float w, float h) {
//		Rectangle r = transf.transform(new Rectangle(x,y,w,h));
//		if (do_fill) drawer.filledRectangle(r.x, r.y, r.width, r.height, color_fill);
//		if (do_stroke) drawer.rectangle(r.x, r.y, r.width, r.height, color_stroke, strokeW * transf.getScale()); 
//		return this; }
	
	public GDXApplet filledcircle(float x, float y, float r, Color cl) {
		Circle c = transf.transform(new Circle(x,y,r));
		drawer.drawer.filledEllipse(c.x, c.y, c.radius, c.radius, 0, cl, cl);
		return this; }
	public GDXApplet circle(Rectangle r) {
		r = transf.transform(r);
		circle(r.x+r.width/2f, r.y+r.height/2f, Math.min(r.width, r.height)/2f); return this; }
	public GDXApplet circle(float x, float y, float r) {
		Circle c = transf.transform(new Circle(x,y,r));
		if (do_fill) drawer.drawer.filledEllipse(c.x, c.y, c.radius, c.radius, 0, color_fill, color_fill);
		if (do_stroke) { drawer.drawer.setColor(color_stroke); drawer.drawer.circle(c.x, c.y, c.radius, strokeW * transf.getScale()); } 
		return this; }
	
	
	public GDXApplet filledtrig(float x, float y, float s, float r) {
		Circle c = transf.transform(new Circle(x,y,s));
		drawer.drawer.filledPolygon(c.x, c.y, 3, c.radius, c.radius, r, color_fill, color_fill); return this; }
//	public GDXApplet stroketrig(float x, float y, float s, float r) {
//		Circle c = transf.transform(new Circle(x,y,s));
//		drawer.polygon(c.x, c.y, 3, c.radius, c.radius, r, color_stroke, color_stroke); return this; }
	

	public GDXApplet line(Vector2 p1, Vector2 p2) {
		line(p1.x, p1.y, p2.x, p2.y); return this; }
	public GDXApplet line(float x1, float y1, float x2, float y2) {
		Vector2 v1 = transf.transform(x1,y1);
		Vector2 v2 = transf.transform(x2,y2);
		float s = strokeW * transf.getScale();
		if (s > 1) drawer.drawer.line(v1.x, v1.y, v2.x, v2.y, color_stroke, s);
		else {
			Color c = new Color(color_stroke);
			c.a = c.a * s;
			drawer.drawer.line(v1.x, v1.y, v2.x, v2.y, c, 1); }
		return this; }
	
	public GDXApplet polygon(Polygon p) {
		float[] v = p.getTransformedVertices(); polygon(v); return this; }
	public GDXApplet polygon(Vector2 v0, Vector2 v1, Vector2 v2) {
		Vector2[] v = new Vector2[3]; v[0] = v0; v[1] = v1; v[2] = v2; polygon(v); return this; }
	public GDXApplet polygon(Vector2 v0, Vector2 v1, Vector2 v2, Vector2 v3) {
		Vector2[] v = new Vector2[4]; v[0] = v0; v[1] = v1; v[2] = v2; v[3] = v3; polygon(v); return this; }
	public GDXApplet polygon(Vector2[] v) {
		float[] f = new float[v.length * 2];
		for (int i = 0 ; i < v.length ; i++) { f[i*2] = v[i].x; f[(i*2)+1] = v[i].y; }
		polygon(f); return this; }
	public GDXApplet polygon(float[] v) {
		for (int i = 0 ; i < v.length ; i += 2) {
			Vector2 p = transf.transform(v[i], v[i+1]); v[i] = p.x; v[i+1] = p.y; }
		if (do_fill) { drawer.drawer.setColor(color_fill); drawer.drawer.filledPolygon(v); }
		if (do_stroke) { 
			drawer.drawer.setColor(color_stroke); 
			drawer.drawer.polygon(v, strokeW * transf.getScale(), JoinType.SMOOTH); } 
		return this; }
	
	public GDXApplet diamond(Rectangle r) {
		float[] v = new float[8];
		v[0] = r.x; v[1] = r.y + r.height / 2f;
		v[2] = r.x + r.width / 2f; v[3] = r.y + r.height; 
		v[4] = r.x + r.width; v[5] = r.y + r.height / 2f;
		v[6] = r.x + r.width / 2f; v[7] = r.y;
		polygon(v);
		return this; }
	
	public Color color_back = new Color(40);
	Color color_fill = new Color();
	Color color_stroke = new Color();
	private float strokeW = 2;
	private boolean do_fill = true, do_stroke = false;
	
	public GDXApplet fill(Color c) {
		color_fill.set(c); do_fill = true; return this; }
	public GDXApplet fill(int c) {
		color_fill.set(color(c)); do_fill = true; return this; }
	public GDXApplet fill(int l, int a) {
		color_fill.set(color(l,a)); do_fill = true; return this; }
	public GDXApplet fill(int r, int g, int b) {
		color_fill.set(color(r,g,b)); do_fill = true; return this; }
	public GDXApplet fill(int r, int g, int b, int a) {
		color_fill.set(color(r,g,b,a)); do_fill = true; return this; }

	public GDXApplet stroke(Color c) {
		color_stroke.set(c); do_stroke = true; return this; }
	public GDXApplet stroke(Color c, float w) {
		color_stroke.set(c); strokeW = w; do_stroke = true; return this; }
	public GDXApplet stroke(int c) {
		color_stroke.set(color(c)); do_stroke = true; return this; }
	public GDXApplet stroke(int l, float w) {
		color_stroke.set(color(l,l,l)); strokeW = w; do_stroke = true; return this; }
	public GDXApplet stroke(int r, int g, int b) {
		color_stroke.set(color(r,g,b)); do_stroke = true; return this; }
	public GDXApplet stroke(int r, int g, int b, int a) {
		color_stroke.set(color(r,g,b, a)); do_stroke = true; return this; }
	public GDXApplet stroke(int r, int g, int b, int a, float w) {
		color_stroke.set(color(r,g,b)); strokeW = w; do_stroke = true; return this; }
	
	public GDXApplet noFill() { do_fill = false; return this; }
	public GDXApplet noStroke() { do_stroke = false; return this; }
	public void strokeWeight(float strokeW) { this.strokeW = strokeW; }

	public static final float point_size = 1f;
	public GDXApplet point(Vector2 v) { return point(v.x,v.y); }
	public GDXApplet point(float x, float y) {
		fill(color_fill); noStroke(); circle(x,y,point_size); return this; }
	public GDXApplet point(float x, float y, Color c) {
		fill(c); noStroke(); circle(x,y,point_size); return this; }

	public Color color(float r, float g, float b, float a) {
		return new Color(r/255.0f, g/255.0f, b/255.0f, a/255.0f); }
	public Color color(int r, int g, int b, int a) {
		return new Color(r/255.0f, g/255.0f, b/255.0f, a/255.0f); }
	public Color color(int r, int g, int b) {
		return new Color(r/255.0f, g/255.0f, b/255.0f, 1.0f); }
	public Color color(int l, int a) {
		return new Color(l/255.0f, l/255.0f, l/255.0f, a/255.0f); }
	public Color color(int l) {
		return new Color(l/255.0f, l/255.0f, l/255.0f, 1.0f); }

	//  --- tools ---
	
	public static float linear_to_log(float f, int c) {
		for (int i = 0 ; i < c ; i++) f = linear_to_log(f);
		return f; }
	public static float linear_to_log(float f) { // curve 0 > 1 line to log
		return 1f - (float)Math.log10(1f + (1f-f)*9f); }

	public static float log_to_linear(float f, int c) {
		for (int i = 0 ; i < c ; i++) f = log_to_linear(f);
		return f; }
	public static float log_to_linear(float f) { // curve 0 > 1 
		return 1f - ((float)Math.pow(10f,1f-f) - 1f) / 9f; }
	
	public static int roundDown(int value, int multiplier) {
	    if (multiplier <= 0) throw new IllegalArgumentException();
	    return value / multiplier * multiplier;
	}
	public static int roundHalfUp(int value, int multiplier) {
	    if (multiplier <= 0) throw new IllegalArgumentException();
	    return (value + (value < 0 ? multiplier / -2 : multiplier / 2)) / multiplier * multiplier;
	}
	public static int roundUp(int value, int multiplier) {
	    if (multiplier <= 0) throw new IllegalArgumentException();
	    return (value + (value < 0 ? 1 - multiplier : multiplier - 1)) / multiplier * multiplier;
	}
	
	public static int toint(String s) { 
		try {
			if (s.trim().length() > 0) return (int)Float.parseFloat(s); else return 0; 
		} catch (NumberFormatException ex) {
			ex.printStackTrace(System.out);
		}
		return 0; }
	public static float tofloat(String s) { 
		try {
			if (s.length() > 0) return Float.parseFloat(s); else return 0; 
		} catch (NumberFormatException ex) {
			ex.printStackTrace(System.out);
		}
		return 0; 
	}
	public static boolean tobool(String s) { 
		if (s.length() > 0) return (s.equals("T")); else return false; }
	public static Vector2 tovec(String s) { return new Vector2().fromString(s); }
	
	public static int toint(float s) { return (int)(s); }
	
	public static String tostr(int s) { return ""+s; }
	public static String tostr(float s) { return ""+s; }
	public static String tostr(boolean s) { if (s) return "T"; else return "F"; }
	public static String tostr(Vector2 s) { return s.toString(); }
	
	public static String trimFlt(float s) { return trimFlt(s,2); }
	public static String trimFlt(float s, int r) { 
		String f = "";
		f += "0.";
		for (int i = 0; i < r; i++) f += "0";
		if ((s > 0 && s < Math.pow(10, -r)) || (s > 0 && s > Math.pow(10, r)) || 
				(s < 0 && s > Math.pow(10, -r)) || (s < 0 && s < Math.pow(10, r)) ) f += "E0";
		DecimalFormatSymbols symb = new DecimalFormatSymbols();
		symb.setExponentSeparator("e");
		DecimalFormat frmt = new DecimalFormat(f, symb);
		return frmt.format(s);
	}
	
	
	
	public static float mapToCircularValuesDist(float current, float cible, float increment, float start, float stop) {
		if (start > stop) {float i = start; start = stop; stop = i;}
		increment = Math.abs(increment);

		while (cible > stop) {cible -= (stop - start);}
		while (current > stop) {current -= (stop - start);}
		while (cible < start) {cible += (stop - start);}
		while (current < start) {current += (stop - start);}

		if (cible < current) {
			if ( (current - cible) <= (stop - current + cible - start) ) {
				if (increment >= current - cible) {return current - cible;}
				else                              {return increment;}
			} else {
				if (increment >= stop - current + cible - start) {return stop - current + cible - start;}
				else if (current + increment < stop)             {return increment;}
				else                                             {return increment;}
			}
		} else if (cible > current) {
			if ( (cible - current) <= (stop - cible + current - start) ) {
				if (increment >= cible - current) {return cible - current;}
				else                              {return increment;}
			} else { 
				if (increment >= stop - cible + current - start) {return stop - cible + current - start;}
				else if (current - increment > start)            {return increment;}
				else                                             {return increment;}
			}
		}
		return 0;
	}
	public static float mapToCircularValuesDir(float current, float cible, float increment, float start, float stop) {
		if (start > stop) {float i = start; start = stop; stop = i;}
		increment = Math.abs(increment);

		while (cible > stop) {cible -= (stop - start);}
		while (current > stop) {current -= (stop - start);}
		while (cible < start) {cible += (stop - start);}
		while (current < start) {current += (stop - start);}

		if (cible < current) {
			if ( (current - cible) <= (stop - current + cible - start) ) {
				return -1;
			} else {
				return 1;
			}
		} else if (cible > current) {
			if ( (cible - current) <= (stop - cible + current - start) ) {
				return 1;
			} else { 
				return -1;
			}
		}
		return 0;
	}
	public static float mapToCircularValues(float current, float cible, float increment, float start, float stop) {
		if (start > stop) {float i = start; start = stop; stop = i;}
		increment = Math.abs(increment);

		while (cible > stop) {cible -= (stop - start);}
		while (current > stop) {current -= (stop - start);}
		while (cible < start) {cible += (stop - start);}
		while (current < start) {current += (stop - start);}

		if (cible < current) {
			if ( (current - cible) <= (stop - current + cible - start) ) {
				if (increment >= current - cible) {return cible;}
				else                              {return current - increment;}
			} else {
				if (increment >= stop - current + cible - start) {return cible;}
				else if (current + increment < stop)             {return current + increment;}
				else                                             {return start + (increment - (stop - current));}
			}
		} else if (cible > current) {
			if ( (cible - current) <= (stop - cible + current - start) ) {
				if (increment >= cible - current) {return cible;}
				else                              {return current + increment;}
			} else { 
				if (increment >= stop - cible + current - start) {return cible;}
				else if (current - increment > start)            {return current - increment;}
				else                                             {return stop - (increment - (current - start));}
			}
		}
		return cible;
	}
	
	
	
	
	

	public static Rectangle get_bounding_rect(ArrayList<Rectangle> arr) { 
		Rectangle r = new Rectangle();
		if (arr == null || arr.size() == 0) return null;
		ArrayList<Rectangle> arr2 = new ArrayList<Rectangle>();
		for (Rectangle a : arr) if (a != null) arr2.add(a);
		if (arr2.size() == 0) return null;
		r.set(arr2.get(0));
		for (Rectangle a : arr2) {
			if (a.x < r.x) r.x = a.x; if (a.y < r.y) r.y = a.y; }
		r.width = 0; r.height = 0;
		for (Rectangle a : arr2) {
			if (a.x+a.width > r.x+r.width) r.width = a.x+a.width-r.x; 
			if (a.y+a.height > r.y+r.height) r.height = a.y+a.height-r.y; }
		return r;
	}
	
	
	
	
	
	public static String[] split(String s, char c) { 
		int cnt = 0; for (int i = 0; i < s.length(); i++) if (s.charAt(i) == c) cnt++;
		String[] r = new String[cnt+1]; cnt = 0; int prev = 0;
		for (int i = 0; i < s.length(); i++) if (s.charAt(i) == c) {
			r[cnt] = s.substring(prev, i); cnt++; prev = i+1; }
		if (prev < s.length()) r[cnt] = s.substring(prev, s.length());
		return r; }
	public String[] concat(String[] s1, String[] s2) { 
		String[] s = new String[s1.length + s2.length];
		for (int i = 0; i < s1.length; i++) s[i] = s1[i];
		for (int i = 0; i < s2.length; i++) s[i + s1.length] = s2[i];
		return s; }
	public static String copy(String n) { 
		if (n != null) return new String(n);
		else return ""; }
	
//	public static <T> T castTo(Object o, Class<T> cl) {
//        try { return (T) o; } catch (ClassCastException e) {
//            // log the exception or other error handling
//        } return null; }
	
	@SuppressWarnings("unchecked")
	public static <T> T copy(T n) { 
		if (n == null) return n;
		if (n instanceof String) return (T)new String((String)n);
		else if (n instanceof Vector2) return (T)new Vector2((Vector2)n);
		else if (n instanceof Integer) return (T)((Integer)n);
		else if (n instanceof Float) return (T)((Float)n);
		else if (n instanceof Boolean) return (T)((Boolean)n);
		else return n; }
	

	public static Object[] duplic(Object[] arr) {
		if (arr == null) return null;
		Object[] dup = new Object[arr.length];
		for (int i = 0 ; i < arr.length ; i++) dup[i] = copy(arr[i]);
		return dup;
	}

	public static <T> nMap<T> duplic(nMap<T> arr) {
		nMap<T> dup = new nMap<T>();
		if (arr != null) 
			for (Map.Entry<String,T> me : arr.entrySet()) 
				dup.put(me.getKey(), copy(me.getValue()));
		return dup;
	}

	public static <T> ArrayList<T> duplic(ArrayList<T> arr) {
		ArrayList<T> dup = new ArrayList<T>();
		if (arr != null) 
			for (int i = 0 ; i < arr.size() ; i++) 
				dup.add(copy(arr.get(i)));
		return dup;
	}

	public static boolean contains(String[] arr, String r) { 
		for (String s : arr) if (s.equals(r)) return true;
		return false; }

	public static boolean contains(ArrayList<String> arr, String r) { 
		for (String s : arr) if (s.equals(r)) return true;
		return false; }

	public static <T> boolean has(ArrayList<T> arr, T r) { 
		for (T s : arr) if (s == r) return true;
		return false; }
	
	
	
	public float rng() { return (float)Math.random(); }
	public float rng(float min, float max) { 
		return min + (float)Math.random() * (max - min); }
	
	public static void logg(String t) { Applet.app.log(t); }
	public static void loggn() { Applet.app.logn(); }
	public static void loggn(String t) { Applet.app.logn(t); }
	
	public void logn() { Gdx.app.log(window_title+":"+frame_counter, log_stack); log_stack = ""; }
	public void logn(String t) { Gdx.app.log(window_title+":"+frame_counter+log_pref, log_stack+t); log_stack = ""; }
	public String log_pref = "";
	public void log(String t) { log_stack += t; }
	private String log_stack = "";
	
	public long time_track_start = 0;
	public void track_time_start() {
		time_track_start = System.currentTimeMillis();
	}
	public void log_track_time(String t) {
		long diff = System.currentTimeMillis() - time_track_start;
		logn("track "+t+" : "+diff);
	}
	public void log_track_time_restart(String t) {
		long diff = System.currentTimeMillis() - time_track_start;
		logn("track "+t+" : "+diff);
		time_track_start = System.currentTimeMillis();
	}
	
	
//	public static <T> ArrayList<T> toList(T ... elements) {
//		if (elements == null) return new ArrayList<T>(); 
//		ArrayList<T> list = new ArrayList<T>();
//		for (T element : elements) list.add(element); 
//		return list; }

//	public static Object[] toArray(Object[] e) {
//		if (e == null) return new Object[0]; 
//		Object[] list = new Object[e.length];
//		int cnt = 0; for (Object o : e) { list[cnt] = o; cnt++; } 
//		return list; }
	
	public boolean file_exist(String path) {
		FileHandle handle = Gdx.files.local(path);
		if (handle.exists()) return true;
		return false;
	}
	
	
	public static final int data_type_nb = 5;
	public static final Class<?>[] data_type = new Class<?>[data_type_nb];
	public static final String[] type_names = new String[data_type_nb];
	public static final String[] type_short_names = new String[data_type_nb];
	public static final byte[] type_id = new byte[data_type_nb];
	
	private static void build_types() {

//		data_type = new Class<?>[data_type_nb];
//		type_names = new String[data_type_nb];
//		type_short_names = new String[data_type_nb];
//		type_id = new byte[data_type_nb];
		
		new_type(Float.class, "flt", "FLT", sFlt.class, 1);
		new_type(Integer.class, "int", "INT", sInt.class, 1);
		new_type(Boolean.class, "boo", "BOO", sBoo.class, 1);
		new_type(String.class, "str", "STR", sStr.class, 1);
		new_type(Vector2.class, "vec", "VEC", sVec.class, 2);
		
	}
	static class vType {
		public String name, ref, type, type_maj;
		public Class<?> _class; public Class<? extends sValue> sval;
		public int index = -1;
		public vType(Class<?> c, String t, String tm, Class<? extends sValue> cv) {
			_class = c; name = c.getSimpleName(); ref = c.getName();
			type = t; type_maj = tm; sval = cv; index = Applet.type_cnt; } }
	
	static int type_cnt = 0;
	private static void new_type(Class<?> ct, String t, String tm, Class<? extends sValue> cv, int s) {
		vType type = new vType(ct, t, tm, cv);
		type_name_class.put(type.name,type._class);
		type_ref_class.put(type.ref,type._class);
		type_type_class.put(type.type,type._class);
		type_type_maj.put(type.type,type.type_maj);
		type_class_name.put(type._class, type.name);
		type_class_type.put(type._class, type.type);
		type_class_index.put(type._class, type.index);
		type_data_size.put(type._class, s);
		data_type[type_cnt] = type._class;
		type_names[type_cnt] = type.name;
		type_short_names[type_cnt] = type.type;
		type_id[type_cnt] = (byte)type_cnt;
		type_id_class.put(type_id[type_cnt],type._class);
		type_class_id.put(type._class,type_id[type_cnt]);
		type_cnt++;
	}
	
	public static HashMap<Byte, Class<?>> type_id_class = new HashMap<Byte, Class<?>>();
	public static HashMap<Class<?>, Byte> type_class_id = new HashMap<Class<?>, Byte>();
	public static HashMap<String, Class<?>> type_name_class = new HashMap<String, Class<?>>();
	public static HashMap<String, Class<?>> type_ref_class = new HashMap<String, Class<?>>();
	public static HashMap<String, Class<?>> type_type_class = new HashMap<String, Class<?>>();
	public static HashMap<String, String> type_type_maj = new HashMap<String, String>();
	public static HashMap<Class<?>, String> type_class_name = new HashMap<Class<?>, String>();
	public static HashMap<Class<?>, String> type_class_type = new HashMap<Class<?>, String>();
	public static HashMap<Class<?>, Integer> type_class_index = new HashMap<Class<?>, Integer>();
	public static HashMap<Class<?>, Integer> type_data_size = new HashMap<Class<?>, Integer>();
	

	public static boolean type_is_used(Class<?> ct) {
		return type_class_name.get(ct) != null; }

	public static String to_string(Object o) {
		if (o == null) return "null";
		if (!type_is_used(o.getClass())) return "??";
		if (o instanceof Vector2) {
			Vector2 v = (Vector2)o;
			return v.toString();
		} else if (o instanceof Float) {
			return Float.toString((float)o);
		} else if (o instanceof Integer) {
			return Integer.toString((int)o);
		} else if (o instanceof Boolean) {
			return Boolean.toString((boolean)o);
		} else if (o instanceof String) {
			return (String)o;
		}
		return "??";
	}

	public static <T> T from_string(String o, Class<?> ct) {
		if (!type_is_used(ct)) return null;
		if (o == null || o.length() == 0) return null;
		if (ct == Vector2.class) {
			try {
				Vector2 v = new Vector2();
				v.fromString(o);
				return (T)v; 
			} catch (Exception ex) {
				Applet.app.logn(ex.toString());
			} }
		else if (ct == Float.class) {
			try {
				Object v = Float.parseFloat(o);
				return (T)v; 
			} catch (NumberFormatException ex) {
				Applet.app.logn(ex.toString());
			} }
		else if (ct == Integer.class) {
			try {
				Object v = Integer.parseInt(o);
				return (T)v; 
			} catch (NumberFormatException ex) {
				Applet.app.logn(ex.toString());
			} }
		else if (ct == Boolean.class) {
			try {
				Object v = Boolean.parseBoolean(o);
				return (T)v; 
			} catch (NumberFormatException ex) {
				Applet.app.logn(ex.toString());
			} }
		else if (ct == String.class) {
			return (T)o; }
		return null;
	}

	public static <T> T new_object(Class<T> ct) {
		if (!type_is_used(ct)) return null;
		if (ct == Vector2.class) {
			Vector2 v = new Vector2();
			
			//TODO  delete this, use this ^
//			Vector2 v = new Vector2((float)Math.random()*100-50,(float)Math.random()*100-50);
			
			
			return (T)v; }
		else if (ct == Float.class) {
			Object v = 0.0f;
			return (T)v; }
		else if (ct == Integer.class) {
			Object v = (int)0;
			return (T)v; }
		else if (ct == Boolean.class) {
			Object v = false;
			return (T)v; }
		else if (ct == String.class) {
			return (T)""; }
		return null;
	}
	
	
	
	
	public static void crash() { loggn(" -- FORCED CRASH -- "); String s = to_crash(); s+=s; }
	private static String to_crash() { return null; }
	
}
