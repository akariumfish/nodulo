package com.noodle.nodulo;

import com.badlogic.gdx.Gdx;
import com.noodle.nodulo.Main;

import app.App;
import app.AppConfig;


public class EditorApp extends App {

	
	
	public static GdxApp make(Main m, AppConfig c) {
		return new GdxApp(m, c, new EditorApp()); }
	
	
	public EditorApp() { 
		app = this; 
	}
	
	
	
	public static EditorApp app;
	
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
