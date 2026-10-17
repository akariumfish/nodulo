package aa_proto;

import static com.badlogic.gdx.graphics.profiling.GLInterceptor.resolveErrorNumber;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.profiling.GLErrorListener;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Rectangle;
import com.noodle.nodulo.Main;

import util.*;

public class Proto implements Screen {
	

	
	
	
	
	
	
	public static class Conf {
		
		public boolean USE_GLPROFILER = true;
//		public boolean USE_GLPROFILER = false;

		public int WIDTH = 500;
		public int HEIGHT = 500;
		
	}
	
	
	

	public Proto(Main m) { this(m,new Conf()); }
	public Proto(Main m, Conf c) { 
		Utl.proto = this; conf = c; main = m; 
		create(); }

	public Main main;
	
	public Conf conf;
	
	public long frame_counter = 0;
	public Rectangle screenrect = new Rectangle();//0,0,GdxApp.WIDTH,GdxApp.HEIGHT

	GLProfiler glprofiler;

	/** the total gl calls made since the last reset */
	public int glCalls = 0, 
	/** the total amount of texture bindings made since the last reset */
			textureBindings = 0, 
	/** the total amount of draw calls made since the last reset */		
			drawCalls = 0, 
	/** the total amount of shader switches made since the last reset */
			shaderSwitch = 0;
	
	public void setInputProcessor() {
		
	}
	
	public void create() {

		
		if (conf.USE_GLPROFILER) {
			glprofiler = new GLProfiler(Gdx.graphics);
			glprofiler.setListener(new GLErrorListener() { @Override public void onError (int error) {
				String place = null; try {
					final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
					for (int i = 0; i < stack.length; i++) {
						if ("check".equals(stack[i].getMethodName())) {
							if (i + 1 < stack.length) {
								final StackTraceElement glMethod = stack[i + 1];
								place = glMethod.getMethodName(); }
							break; } }
				} catch (Exception ignored) {}
				if (place != null) { Utl.logn("GLProfiler : Error " + resolveErrorNumber(error) + " from " + place); } 
				else { Utl.logn("GLProfiler : Error " + resolveErrorNumber(error) + " at: " + new Exception()); }
			}});
			glprofiler.enable();
		}
		
//		Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
//		Gdx.graphics.setFullscreenMode(currentMode);
		Gdx.graphics.setWindowedMode(conf.WIDTH, conf.HEIGHT);
		
//		Gdx.graphics.setTitle(window_title);
//		
//		cursor(false);
	}
	
	@Override
	public void dispose() {
		 
//		if (Gdx.graphics.isFullscreen()) {
//	            Gdx.graphics.setWindowedMode(WIDTH, HEIGHT);
//	            screenwidth = WIDTH; screenheight = HEIGHT;
//		        resize(screenwidth, screenheight); }
		
		if (conf.USE_GLPROFILER) glprofiler.disable();
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

}
