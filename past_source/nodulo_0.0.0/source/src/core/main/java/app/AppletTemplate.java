package app;

import com.badlogic.gdx.Gdx;
import com.noodle.nodulo.GdxApp;
import com.noodle.nodulo.Main;


public class AppletTemplate extends App {

	
	
	public static GdxApp make(Main m, AppConfig c) {
		return new GdxApp(m, c, new AppletTemplate()); }
	
	
	public AppletTemplate() { 
		app = this; 
	}
	
	
	
	public static AppletTemplate app;
	
	@Override
	public void setInputProcessor() {
		Gdx.input.setInputProcessor(input);
	}

	
	@Override
	public void setup(GdxApp a) {
		super.setup(a);
		
		
		
		startup();
	}

	@Override
	public void closing() {
		super.closing();
		
	}
	
	@Override 
	protected void gui_frame() { 
		
	}
	
	@Override 
	protected void gui_draw() { 
		
	}

	@Override 
	public void draw_start() {
		
	}
	@Override 
	public void draw_end() {
		
	}
	
	
	
	
	
	
	
	

}
