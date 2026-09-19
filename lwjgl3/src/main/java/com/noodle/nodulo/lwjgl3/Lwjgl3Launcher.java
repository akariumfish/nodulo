package com.noodle.nodulo.lwjgl3;

import java.io.IOException;
import java.util.LinkedList;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.noodle.nodulo.GdxApp;
import com.noodle.nodulo.Main;

import aa_nodulo.PlaneApplet;
import app.AppConfig;
import app.Conf;
import util.nRun;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
	
	
	public static void main(String[] args) {
		
		if (!PlaneApplet.NETWORK) {
			if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
			createApplication("nodulo", 610, 50, 
					1300,960, false);
		} else {
			
			Launcher.launch();

			try { Thread.sleep(3000); } 
			catch (InterruptedException e) { e.printStackTrace(); }
			
			if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
			createApplication("nodulo - client", 1020, 50, 
					900,860,
					false, true, true);
		}
			
			
	}
	
	


	private static Lwjgl3Application createApplication(String title, 
			int posx, int posy, int sizex, int sizey, boolean fullscreen) {
		return new Lwjgl3Application(new Main(new AppConfig(title, sizex, sizey, 
				fullscreen)), 
				getConfiguration(title, posx, posy, sizex, sizey));
	}

	private static Lwjgl3Application createApplication(String title, 
			int posx, int posy, int sizex, int sizey, boolean fullscreen, boolean net, 
			boolean cli) {
		return new Lwjgl3Application(new Main(new AppConfig(title, sizex, sizey, 
				fullscreen, net, cli)), 
				getConfiguration(title, posx, posy, sizex, sizey));
	}

	private static Lwjgl3ApplicationConfiguration getConfiguration(String t, int px, int py, int sx, int sy) {
		Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
		configuration.setTitle(t);
		configuration.useVsync(true);
		configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
//		configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		configuration.setWindowedMode(sx,sy);
//		configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
		configuration.setWindowPosition(px,py);
		if (PlaneApplet.OPENGLES3) configuration.setOpenGLEmulation(
				Lwjgl3ApplicationConfiguration.GLEmulation.GL32,3,3);
		return configuration;
	}
	
	
	
	
	
	public static class Launcher {

		public static void launch() {

			Thread thread = new Thread() {
				public void run() {
					try {
						int res = JavaProcess.exec(Lwjgl3Launcher.Launcher.class, 
								new LinkedList<String>()); 
						System.out.println("exec res: "+res);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};

			thread.start();
		}

		public static String tmp_title = "nodulo - server";
		public static int tmp_window_pos_x = 20;
		public static int tmp_window_pos_y = 50;
		public static int tmp_window_width = 900;
		public static int tmp_window_height = 860;
		public static boolean tmp_fs = false;
		public static boolean tmp_net = true;
		public static boolean tmp_client = false;

		public static void main(String[] args) {
			
			GdxApp.WIDTH = tmp_window_width;
			GdxApp.HEIGHT = tmp_window_height;
			
			// This handles macOS support and helps on Windows.
			if (StartupHelper.startNewJvmIfRequired()) return; 
			
			Launcher.createApplication(tmp_title, 
					tmp_window_pos_x, 
					tmp_window_pos_y,
					tmp_window_width,
					tmp_window_height,
					tmp_fs,
					tmp_net,
					tmp_client);
			
		}
		
		private static Lwjgl3Application createApplication(String title, 
				int posx, int posy, int sizex, int sizey, boolean fullscreen, boolean net,
				boolean cli) {
			return new Lwjgl3Application(new Main(new AppConfig(title, sizex, sizey, 
					fullscreen, net, cli)), 
					Launcher.getConfiguration(title, posx, posy, sizex, sizey));
		}

		private static Lwjgl3ApplicationConfiguration getConfiguration(String t, int px, int py, int sx, int sy) {
			Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
			configuration.setTitle(t);
			configuration.useVsync(true);
			configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
//			configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
			configuration.setWindowedMode(sx,sy);
//			configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
			configuration.setWindowPosition(px,py);
			if (PlaneApplet.OPENGLES3) configuration.setOpenGLEmulation(
					Lwjgl3ApplicationConfiguration.GLEmulation.GL32,3,3);
			return configuration;
		}
	}
	
	
	
	
	

}