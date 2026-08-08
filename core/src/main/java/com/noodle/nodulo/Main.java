package com.noodle.nodulo;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;

import aa_nodulo.PlaneApplet;
import util.Utl;
import app.AppConfig;
import app.GdxApp;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
	

    public OrthographicCamera camera; 
	public ScreenViewport viewport; 
	
	
	public AppConfig conf;
	
	public Main(AppConfig c) {
		conf = c;
	}
	
	@Override
	public void create() {

		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		camera = new OrthographicCamera(conf.WIDTH, conf.HEIGHT);
		viewport = new ScreenViewport(camera);
		
//		setScreen(new FirstScreen2()); 
		
		Utl.build();
		
		PlaneApplet.build_setup();
		
		if (PlaneApplet.TITLE_SCREEN) launch_title(); 
		else launch_nodulo();
		
//		VisUI.load();k
		
	}
	
	public void exit() { Gdx.app.exit(); }
	
	TitleScreen titleScreen = null;

	GdxApp nodulo_app = null;
	
	public void launch_title() {
		if (titleScreen == null)
			titleScreen = new TitleScreen(this);
		Gdx.input.setInputProcessor(titleScreen.stage);
		setScreen(titleScreen); 
	}
	
	public void launch_nodulo() {
		if (nodulo_app == null)
			nodulo_app = PlaneApplet.make(this, new AppConfig("nodulo", 1300, 960));
		nodulo_app.setInputProcessor();
		setScreen(nodulo_app); }
	
	public void launch_nodulo(String model, boolean dark_theme, boolean fullscreen) {
		if (nodulo_app == null) {
			PlaneApplet.AppletConfig conf = 
					new PlaneApplet.AppletConfig(model, dark_theme);
			nodulo_app = PlaneApplet.make(this, 
					new AppConfig("nodulo", 1300, 960, fullscreen), conf);
		}
		nodulo_app.setInputProcessor();
		setScreen(nodulo_app); }

	public void close_nodulo() {
		launch_title(); 
		nodulo_app.dispose(); 
		nodulo_app = null; 
	}
	
	
}