package com.noodle.nodulo;

import com.badlogic.gdx.Gdx;

import app.App;
import app.AppConfig;
import gui.nAlign;
import gui.nGUI;
import gui.nWidget;
import util.Utl;
import util.nRun;


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
		
		float RS = nGUI.book.RS;

		nWidget menu_right = gui.addWidget("ref")
				.setParent(gui.menu_back)
				.setBoundChild(true)
				.setStackAxis(nAlign.HORIZONTAL) // HORIZONTAL   VERTICAL
				.setStackDirection(nAlign.LEFT) // RIGHT   LEFT   UP   DOWN
				.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
				.setBoundOutspace(0)
				.setStackSpacing(RS/10f)
				.setPassif()
				.set_color_background(Utl.color(0,0))
				.asWidget()
				;
		
		gui.addWidget("ref")
				.setSize(4f*RS, RS)
				.setBoundParent(true)
				.setStacked(true)
				.setFont(20)
				.setTrigger()
				.setText("Exit")
				.asWidget()
				.setParent(menu_right)
				.addEventTrigger(new nRun() { public void run() {
					gui.pop_exit(); }})
				;

		gdx.addEventScreen(new nRun() { public void run() {
			menu_right.force_calc_child();
			menu_right.setPos(app.gdx.getscreenwidth() - menu_right.getSX(),RS/6f);
		}});
		
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
