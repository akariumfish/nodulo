package com.noodle.nodulo.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.noodle.nodulo.Main;

import app.AppConfig;

//import zz_applet.Applet;

public class Lwjgl3LauncherDouble {
	
	protected static Lwjgl3Application createApplication(
			String title, String setting_file, boolean autol, boolean autob, boolean netmode, int x, int y) {
		return createApplication("nodulo", x, y, false);

//		return new Lwjgl3Application(new Applet(title, setting_file, autol, autob, netmode), 
//				getDefaultConfiguration("app - "+title, x, y)); 
//		return null;
	}

	private static Lwjgl3Application createApplication(String title, 
			int posx, int posy, boolean fullscreen) {
		return new Lwjgl3Application(new Main(new AppConfig(title, 
					Lwjgl3Launcher.WIN_DOUBLE_WIDTH, 
					Lwjgl3Launcher.WIN_DOUBLE_HEIGHT
					, fullscreen)), 
				getDefaultConfiguration("app - "+title, posx, posy)
//				getConfiguration(posx, posy, sizex, sizey)
				);
	}

	private static Lwjgl3ApplicationConfiguration getDefaultConfiguration(String t, int x, int y) {
		Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
		configuration.setTitle(t);
		configuration.useVsync(true);
		configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
//		configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		configuration.setWindowedMode(Lwjgl3Launcher.WIN_DOUBLE_WIDTH, Lwjgl3Launcher.WIN_DOUBLE_HEIGHT);
		configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
		configuration.setWindowPosition(x, y);
		return configuration;
	}
}
