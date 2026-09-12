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

	
////	public static final boolean solo_double = true;		// DOUBLE
//	public static final boolean solo_double = false;		// SOLO
	
	
////	public static final int launch_delay = 2000;
//	public static final int launch_delay = 4000;

	
//	// SOLO WINDOW
//	public static final int WIN_SOLO_WIDTH = 1300;
//	public static final int WIN_SOLO_HEIGHT = 960;
	
	//DOUBLE WINDOW
//	public static final int WIN_DOUBLE_WIDTH = 900;
//	public static final int WIN_DOUBLE_HEIGHT = 860;
//	
//
//	public static final String Lwjgl3LD1_title = "server";
//	public static final String Lwjgl3LD1_setting_file = "setting_server";
//	public static final boolean Lwjgl3LD1_autorize_autoload = true;
//	public static final boolean Lwjgl3LD1_autorize_autobuild = true;
//	public static final boolean Lwjgl3LD1_is_server = true;
//	public static final int Lwjgl3LD1_window_pos_x = 20;
//	public static final int Lwjgl3LD1_window_pos_y = 50;
//
//	public static final String Lwjgl3LD2_title = "client";
//	public static final String Lwjgl3LD2_setting_file = "setting_client";
//	public static final boolean Lwjgl3LD2_autorize_autoload = true;
//	public static final boolean Lwjgl3LD2_autorize_autobuild = true;
//	public static final boolean Lwjgl3LD2_is_server = false;
//	public static final int Lwjgl3LD2_window_pos_x = 1020;
//	public static final int Lwjgl3LD2_window_pos_y = 50;
	
	
	public static void main(String[] args) {
		

		// using Conf
//		launch();
	

		// 		>>>  RUN SINGLE <<<
//		if (!solo_double) {
		if (!PlaneApplet.NETWORK) {
			if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
			createApplication("nodulo", 610, 50, 
//					Lwjgl3Launcher.WIN_SOLO_WIDTH, 
//					Lwjgl3Launcher.WIN_SOLO_HEIGHT, 
					1300,960,
					false, 
					new nRun() { public void run() {
//						Launcher.launch();
						Lwjgl3Launcher.launch(arg(0,String.class),
								arg(1,Integer.class), 
								arg(2,Integer.class), 
								arg(3,Integer.class), 
								arg(4,Integer.class), 
								arg(5,Boolean.class), 
								arg(6,Boolean.class));
					}});
		} else {
			if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
			createApplication("nodulo", 1020, 50, 
//					Lwjgl3Launcher.WIN_SOLO_WIDTH, 
//					Lwjgl3Launcher.WIN_SOLO_HEIGHT, 
					900,860,
					false, true,
					new nRun() { public void run() {
//						Launcher.launch();
						Lwjgl3Launcher.launch(arg(0,String.class),
								arg(1,Integer.class), 
								arg(2,Integer.class), 
								arg(3,Integer.class), 
								arg(4,Integer.class), 
								arg(5,Boolean.class), 
								arg(6,Boolean.class));
					}});
		}
			
			
//		
//		// 		>>>  RUN DOUBLE <<<
//		if (solo_double) {
//			launch_net_apps();
//		}
//		
		
	}
	
//	public static Thread t1,t2;
//
//	public static void launch_net_apps() {
//
//		PlaneApplet.TITLE_SCREEN = false;
//		
//		t1 = new Thread() {
//			public void run() {
//				try {
//					int res = JavaProcess.exec(Lwjgl3LauncherDouble1.class, 
//							new LinkedList<String>()); 
//					System.out.println("exec res: "+res);
//				} catch (IOException e) {
//					e.printStackTrace();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//
//		t2 = new Thread() {
//			public void run() {
//				try {
//					int res = JavaProcess.exec(Lwjgl3LauncherDouble2.class, 
//							new LinkedList<String>()); 
//					System.out.println("exec res: "+res);
//				} catch (IOException e) {
//					e.printStackTrace();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//
//		t1.start();
//
//		try { Thread.sleep(launch_delay); } 
//		catch (InterruptedException e) { e.printStackTrace(); }
//		
//		t2.start();
//	}
	
//	public static Conf tmp_conf = null;

	public static String tmp_title = "app";
	public static int tmp_window_pos_x = 610;
	public static int tmp_window_pos_y = 50;
	public static int tmp_window_width = 1300;
	public static int tmp_window_height = 960;
	public static boolean tmp_fs = false;
	public static boolean tmp_net = false;

//	public static void launch(String title, int x, int y, int w, int h, Conf c) {
//		tmp_conf = c;
//		tmp_title = title; 
//		tmp_window_pos_x = x;
//		tmp_window_pos_y = y;
//		tmp_window_width = w;
//		tmp_window_height = h;
//		tmp_fs = false;
//		tmp_net = false;
//		Launcher.launch();
//	}

	public static void launch(String title, int x, int y, int w, int h, boolean fs, boolean net) {
//		tmp_conf = c;
		tmp_title = title; 
		tmp_window_pos_x = x;
		tmp_window_pos_y = y;
		tmp_window_width = w;
		tmp_window_height = h;
		tmp_fs = fs;
		tmp_net = net;
		Launcher.launch();
	}

//	public static void launch() {
//		tmp_conf = new Conf().set("launch", new Main.Launch() { 
//			public void launch(String title, int x, int y, int w, int h, Conf c) {
//				Lwjgl3Launcher.launch(title,x,y,w,h,c); }});
//		
//		tmp_title = "app"; 
//		tmp_window_pos_x = 610;
//		tmp_window_pos_y = 50;
//		tmp_window_width = 1300;
//		tmp_window_height = 960;
//		tmp_fs = false;
//		tmp_net = false;
//		Launcher.launch();
//	}
	
	public static class Launcher {

		public static void launch() {

			PlaneApplet.TITLE_SCREEN = false;

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

//		public static Conf conf = null;
//
//		public static String title = "app";
//		
//		public static boolean fullscreen = true;
//		
//		public static int window_pos_x = 20;
//		public static int window_pos_y = 50;
//		public static int window_width = 20;
//		public static int window_height = 50;
		
		public static void main(String[] args) {
			
//			conf = 				new Conf(Lwjgl3Launcher.tmp_conf);
//			title = 				new String(Lwjgl3Launcher.tmp_title);
//			fullscreen = 		Lwjgl3Launcher.tmp_fullscreen;
//			window_pos_x = 		Lwjgl3Launcher.tmp_window_pos_x;
//			window_pos_y = 		Lwjgl3Launcher.tmp_window_pos_y;
//			window_width = 		Lwjgl3Launcher.tmp_window_width;
//			window_height = 		Lwjgl3Launcher.tmp_window_height;
			
			GdxApp.WIDTH = Lwjgl3Launcher.tmp_window_width;
			GdxApp.HEIGHT = Lwjgl3Launcher.tmp_window_height;
			
			// This handles macOS support and helps on Windows.
			if (StartupHelper.startNewJvmIfRequired()) return; 
			
//			createApplication(title, setting_file, autorize_autoload, autorize_autobuild, is_server, 
//			window_pos_x, window_pos_y);
//			createApplication(new String(Lwjgl3Launcher.tmp_title), 
//					Lwjgl3Launcher.tmp_window_pos_x, 
//					Lwjgl3Launcher.tmp_window_pos_y, 
//					Lwjgl3Launcher.tmp_window_width, 
//					Lwjgl3Launcher.tmp_window_height, 
//					new Conf(Lwjgl3Launcher.tmp_conf));
			
			createApplication(Lwjgl3Launcher.tmp_title, 
					Lwjgl3Launcher.tmp_window_pos_x, 
					Lwjgl3Launcher.tmp_window_pos_y,
					Lwjgl3Launcher.tmp_window_width,
					Lwjgl3Launcher.tmp_window_height,
					Lwjgl3Launcher.tmp_fs,
					Lwjgl3Launcher.tmp_net,
					new nRun() { public void run() {
//						Launcher.launch();
						Lwjgl3Launcher.launch(arg(0,String.class),
								arg(1,Integer.class), 
								arg(2,Integer.class), 
								arg(3,Integer.class), 
								arg(4,Integer.class), 
								arg(5,Boolean.class), 
								arg(6,Boolean.class));
					}});
			
		}
		
//		protected static Lwjgl3Application createApplication(
//				String title, String setting_file, boolean autol, boolean autob, boolean netmode, int x, int y) {
//			return createApplication("nodulo", x, y, false);
//		}

		private static Lwjgl3Application createApplication(String title, 
				int posx, int posy, int w, int h, Conf c) {
			return new Lwjgl3Application(
					new Main(new AppConfig(title,w,h,false)),
//					new Main(c), 
					getDefaultConfiguration(title,posx,posy,w,h) );
		}

		private static Lwjgl3ApplicationConfiguration getDefaultConfiguration(String t, int x, int y, int w, int h) {
			Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
			configuration.setTitle(t);
			configuration.useVsync(true);
			configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
//			configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
			configuration.setWindowedMode(w, h);
			configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
			configuration.setWindowPosition(x, y);
			return configuration;
		}
		private static Lwjgl3Application createApplication(String title, 
				int posx, int posy, int sizex, int sizey, boolean fullscreen, boolean net,
				nRun app_run) {
			return new Lwjgl3Application(new Main(new AppConfig(title, sizex, sizey, fullscreen, app_run)), 
					getConfiguration(title, posx, posy, sizex, sizey));
		}

//		private static Lwjgl3Application createApplication(String title, 
//				int posx, int posy, int sizex, int sizey, boolean fullscreen) {
//			return new Lwjgl3Application(new Main(new AppConfig(title, sizex, sizey, fullscreen)), 
//					getConfiguration(posx, posy, sizex, sizey));
//		}

		private static Lwjgl3ApplicationConfiguration getConfiguration(String t, int px, int py, int sx, int sy) {
			Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
			configuration.setTitle(t);
			configuration.useVsync(true);
			configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
//			configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
			configuration.setWindowedMode(sx,sy);
			configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
			configuration.setWindowPosition(px,py);
			return configuration;
		}
	}
	

	private static Lwjgl3Application createApplication(String title, 
			int posx, int posy, int sizex, int sizey, boolean fullscreen,
			nRun app_run) {
		return new Lwjgl3Application(new Main(new AppConfig(title, sizex, sizey, fullscreen, 
				app_run)), 
				getConfiguration(title, posx, posy, sizex, sizey));
	}

	private static Lwjgl3Application createApplication(String title, 
			int posx, int posy, int sizex, int sizey, boolean fullscreen, boolean net,
			nRun app_run) {
		return new Lwjgl3Application(new Main(new AppConfig(title, sizex, sizey, 
				fullscreen, app_run)), 
				getConfiguration(title, posx, posy, sizex, sizey));
	}

//	private static Lwjgl3Application createApplication(String title, 
//			int posx, int posy, int sizex, int sizey, boolean fullscreen) {
//		return new Lwjgl3Application(new Main(new AppConfig(title, sizex, sizey, fullscreen)), 
//				getConfiguration(posx, posy, sizex, sizey));
//	}

	private static Lwjgl3ApplicationConfiguration getConfiguration(String t, int px, int py, int sx, int sy) {
		Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
		configuration.setTitle(t);
		configuration.useVsync(true);
		configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
//		configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		configuration.setWindowedMode(sx,sy);
		configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
		configuration.setWindowPosition(px,py);
		return configuration;
	}

//	private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
//		Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
//		configuration.setTitle("Nodulo");
//		//// Vsync limits the frames per second to what your hardware can display, and helps eliminate
//		//// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
//		configuration.useVsync(true);
//		//// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
//		//// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
//		configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
//		//// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
//		//// useful for testing performance, but can also be very stressful to some hardware.
//		//// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.
//
////		configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
//
//		configuration.setWindowedMode(GdxApp.WIDTH, GdxApp.HEIGHT);
//		
//		//// You can change these files; they are in lwjgl3/src/main/resources/ .
//		//// They can also be loaded from the root of assets/ .
//		configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
//
//		configuration.setWindowPosition(610, 50);
//
//		//// This should improve compatibility with Windows machines with buggy OpenGL drivers, Macs
//		//// with Apple Silicon that have to emulate compatibility with OpenGL anyway, and more.
//		//// This uses the dependency `com.badlogicgames.gdx:gdx-lwjgl3-angle` to function.
//		//// You can choose to remove the following line and the mentioned dependency if you want; they
//		//// are not intended for games that use GL30 (which is compatibility with OpenGL ES 3.0).
//		//        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.ANGLE_GLES20, 0, 0);
//
//		return configuration;
//	}
}