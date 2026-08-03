package com.noodle.nodulo;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
	@Override
	public void create() {
		FirstScreen2 fscreen = new FirstScreen2();
		fscreen.setup();
		setScreen(fscreen);
	}
}