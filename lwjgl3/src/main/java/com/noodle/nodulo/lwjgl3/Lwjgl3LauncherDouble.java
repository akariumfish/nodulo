package com.noodle.nodulo.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import app.Applet;

public class Lwjgl3LauncherDouble {
	
	protected static Lwjgl3Application createApplication(
			String title, String setting_file, boolean autol, boolean autob, boolean netmode, int x, int y) {
		return new Lwjgl3Application(new Applet(title, setting_file, autol, autob, netmode), 
				getDefaultConfiguration("app - "+title, x, y)); 
	}

	private static Lwjgl3ApplicationConfiguration getDefaultConfiguration(String t, int x, int y) {
		Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
		configuration.setTitle(t);
		configuration.useVsync(true);
		configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
		configuration.setWindowedMode(Lwjgl3Launcher_app.WIN_DOUBLE_WIDTH, Lwjgl3Launcher_app.WIN_DOUBLE_HEIGHT);
		configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
		configuration.setWindowPosition(x, y);
		return configuration;
	}
}
