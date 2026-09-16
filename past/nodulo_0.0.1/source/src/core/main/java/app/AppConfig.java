package app;

import aa_nodulo.PlaneApplet;
import util.nRun;

public class AppConfig {
	public boolean START_FULLSCREEN = PlaneApplet.START_FULLSCREEN;
	public int WIDTH = 500;
	public int HEIGHT = 500;
	public String window_title = "";
	public boolean net = false;
	public boolean client = false;
	public AppConfig() {}
	public AppConfig(String t) { window_title = t; }
	public AppConfig(String t, int w, int h) {
		window_title = t; WIDTH = w; HEIGHT = h; }
	public AppConfig(String t, int w, int h, boolean f) {
		window_title = t; WIDTH = w; HEIGHT = h; START_FULLSCREEN = f; }
	public AppConfig(String t, int w, int h, boolean f, boolean ne, boolean c) {
		window_title = t; WIDTH = w; HEIGHT = h; START_FULLSCREEN = f; net = ne; client = c; }
}

