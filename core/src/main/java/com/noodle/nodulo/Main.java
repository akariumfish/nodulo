package com.noodle.nodulo;

import com.badlogic.gdx.Game;

import app.Applet;
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

		setScreen(new TitleScreen(this));
		
//		Utl.build_types();
//
//		setScreen(Applet.make(this, new AppConfig("nodulo", 1300, 960, false)));
	}
}