package com.noodle.nodulo.lwjgl3;

import app.GDXApplet;

public class Lwjgl3LauncherDouble1 extends Lwjgl3LauncherDouble {

	public static String title = "server";
	
	public static String setting_file = "setting_server";
	
	public static boolean autorize_autoload = true;
	public static boolean autorize_autobuild = true;
	
	public static boolean is_server = true;
	
	public static int window_pos_x = 20;
	public static int window_pos_y = 50;
	
	public static void main(String[] args) {
		GDXApplet.WIDTH = Lwjgl3Launcher_app.WIN_DOUBLE_WIDTH;
		GDXApplet.HEIGHT = Lwjgl3Launcher_app.WIN_DOUBLE_HEIGHT;
		
		title = 				Lwjgl3Launcher_app.Lwjgl3LD1_title;
		setting_file = 		Lwjgl3Launcher_app.Lwjgl3LD1_setting_file;
		autorize_autoload = 	Lwjgl3Launcher_app.Lwjgl3LD1_autorize_autoload;
		autorize_autobuild = Lwjgl3Launcher_app.Lwjgl3LD1_autorize_autobuild;
		is_server = 			Lwjgl3Launcher_app.Lwjgl3LD1_is_server;
		window_pos_x = 		Lwjgl3Launcher_app.Lwjgl3LD1_window_pos_x;
		window_pos_y = 		Lwjgl3Launcher_app.Lwjgl3LD1_window_pos_y;
		
		// This handles macOS support and helps on Windows.
		if (StartupHelper.startNewJvmIfRequired()) return; 
		
		createApplication(title, setting_file, autorize_autoload, autorize_autobuild, is_server, 
				window_pos_x, window_pos_y);
		
	}
	
}
