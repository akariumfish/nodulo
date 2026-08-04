package app;


public class AppConfig {
	public boolean START_FULLSCREEN = false;
	public int WIDTH = 500;
	public int HEIGHT = 500;
	public String window_title = "";
	public AppConfig() {}
	public AppConfig(String t) { window_title = t; }
	public AppConfig(String t, int w, int h) {
		window_title = t; WIDTH = w; HEIGHT = h; }
	public AppConfig(String t, int w, int h, boolean f) {
		window_title = t; WIDTH = w; HEIGHT = h; START_FULLSCREEN = f; }
}

