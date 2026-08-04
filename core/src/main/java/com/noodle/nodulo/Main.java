package com.noodle.nodulo;

import com.badlogic.gdx.Game;

import aa_nodulo.PlaneApplet;
import util.Utl;
import zz_applet.Applet;
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
		
		Utl.build_types();
		
		if (PlaneApplet.TITLE_SCREEN) 
			setScreen(new TitleScreen(this)); 
		else if (PlaneApplet.STARTUP_APPLET) 
			launch_applet();
		else launch_nodulo();
		
	}
	
	public void launch_applet() {
		setScreen(Applet.make(this, new AppConfig("nodulo", 1300, 960, false))); }
	
	public void launch_nodulo() {
		setScreen(PlaneApplet.make(this, new AppConfig("nodulo", 1300, 960, false))); }
	
	public void launch_nodulo(String model) {
		setScreen(PlaneApplet.make(this, new AppConfig("nodulo", 1300, 960, false), 
				new PlaneApplet.AppletConfig(model))); }
}