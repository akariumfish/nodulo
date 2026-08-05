package com.noodle.nodulo;

import com.badlogic.gdx.Game;

import aa_nodulo.PlaneApplet;
import util.Utl;
import app.AppConfig;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
	
	public AppConfig conf;
	
	public Main(AppConfig c) {
		conf = c;
	}
	
	@Override
	public void create() {
		
//		setScreen(new FirstScreen2()); 
		
		Utl.build();
		
		PlaneApplet.build_setup();
		
		if (PlaneApplet.TITLE_SCREEN) 
			setScreen(new TitleScreen(this)); 
		else launch_nodulo();
		
	}
	
	public void launch_nodulo() {
		setScreen(PlaneApplet.make(this, new AppConfig("nodulo", 1300, 960))); }
	
	public void launch_nodulo(String model) {
		setScreen(PlaneApplet.make(this, new AppConfig("nodulo", 1300, 960), 
				new PlaneApplet.AppletConfig(model))); }
	
	
}