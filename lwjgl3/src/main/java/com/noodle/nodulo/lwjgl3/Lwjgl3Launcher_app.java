package com.noodle.nodulo.lwjgl3;

import java.io.IOException;
import java.util.LinkedList;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import app.App;
import app.GdxApp;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher_app {
	
	
	
	
//	public static final boolean solo_double = true;		// DOUBLE
	public static final boolean solo_double = false;		// SOLO
	
	
//	public static final int launch_delay = 2000;
	public static final int launch_delay = 4000;

	
	// SOLO WINDOW
	public static final int WIN_SOLO_WIDTH = 1300;
	public static final int WIN_SOLO_HEIGHT = 960;
	
	//DOUBLE WINDOW
	public static final int WIN_DOUBLE_WIDTH = 900;
	public static final int WIN_DOUBLE_HEIGHT = 860;
	

	public static final String Lwjgl3LD1_title = "server";
	public static final String Lwjgl3LD1_setting_file = "setting_server";
	public static final boolean Lwjgl3LD1_autorize_autoload = true;
	public static final boolean Lwjgl3LD1_autorize_autobuild = true;
	public static final boolean Lwjgl3LD1_is_server = true;
	public static final int Lwjgl3LD1_window_pos_x = 20;
	public static final int Lwjgl3LD1_window_pos_y = 50;

	public static final String Lwjgl3LD2_title = "client";
	public static final String Lwjgl3LD2_setting_file = "setting_client";
	public static final boolean Lwjgl3LD2_autorize_autoload = true;
	public static final boolean Lwjgl3LD2_autorize_autobuild = true;
	public static final boolean Lwjgl3LD2_is_server = false;
	public static final int Lwjgl3LD2_window_pos_x = 1020;
	public static final int Lwjgl3LD2_window_pos_y = 50;
	
	
	
	
	public static Thread t1,t2;

	public static void main(String[] args) {

		
		// 		>>>  RUN SINGLE <<<
		if (!solo_double) {
			GdxApp.WIDTH = Lwjgl3Launcher_app.WIN_SOLO_WIDTH;
			GdxApp.HEIGHT = Lwjgl3Launcher_app.WIN_SOLO_HEIGHT;
		
			// This handles macOS support and helps on Windows :
			if (StartupHelper.startNewJvmIfRequired()) return; 
			
//			new Lwjgl3Application(new Applet("solo", "setting_solo"), 
//					getDefaultConfiguration());
		}
		
		
		// 		>>>  RUN DOUBLE <<<
		if (solo_double) {
			launchDouble();
		}
		
	}
	
	private static void launchDouble() {
		
		t1 = new Thread() {
			public void run() {
				try {
					int res = JavaProcess.exec(Lwjgl3LauncherDouble1.class, 
							new LinkedList<String>()); 
					System.out.println("exec res: "+res);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		t2 = new Thread() {
			public void run() {
				try {
					int res = JavaProcess.exec(Lwjgl3LauncherDouble2.class, 
							new LinkedList<String>()); 
					System.out.println("exec res: "+res);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		t1.start();
		
		try { Thread.sleep(launch_delay); } 
		catch (InterruptedException e) { e.printStackTrace(); }
		
		t2.start();
	}

//	private static Lwjgl3Application createApplication() {
//		return new Lwjgl3Application(new Applet(), getDefaultConfiguration());
//	}

	private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
		Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
		configuration.setTitle("Applet");
		//// Vsync limits the frames per second to what your hardware can display, and helps eliminate
		//// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
		configuration.useVsync(true);
		//// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
		//// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
		configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
		//// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
		//// useful for testing performance, but can also be very stressful to some hardware.
		//// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.

//		configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());

		configuration.setWindowedMode(GdxApp.WIDTH, GdxApp.HEIGHT);
		
		//// You can change these files; they are in lwjgl3/src/main/resources/ .
		//// They can also be loaded from the root of assets/ .
		configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

		configuration.setWindowPosition(610, 50);

		//// This should improve compatibility with Windows machines with buggy OpenGL drivers, Macs
		//// with Apple Silicon that have to emulate compatibility with OpenGL anyway, and more.
		//// This uses the dependency `com.badlogicgames.gdx:gdx-lwjgl3-angle` to function.
		//// You can choose to remove the following line and the mentioned dependency if you want; they
		//// are not intended for games that use GL30 (which is compatibility with OpenGL ES 3.0).
		//        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.ANGLE_GLES20, 0, 0);

		return configuration;
	}
}