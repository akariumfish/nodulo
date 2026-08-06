package com.noodle.nodulo;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import aa_nodulo.PlaneApplet;
import app.nDrawer;
import gui.nDrawable;
import util.Utl;

/** First screen of the application. Displayed after the application is created. */
public class TitleScreen implements Screen ,nDrawer.DrawContext {
	
	public Main main;
	
	Skin skin;
	Stage stage;

    public OrthographicCamera camera; 
	public ScreenViewport viewport; 
	public Rectangle screenrect;
	
	public nDrawer drawer;

	TextButtonStyle textbuttstyle;
	
	public TitleScreen(Main m) {
		main = m;

		screenrect = new Rectangle(0,0,m.conf.WIDTH,m.conf.HEIGHT);
		
		camera = main.camera;
		viewport = main.viewport;
		camera.position.set(m.conf.WIDTH / 2, m.conf.HEIGHT / 2, 0);
		camera.update();
		
		drawer = new nDrawer(this, false);

		title_effect = new TitleEffect(this);

		skin = new Skin(Gdx.files.internal("ui/skin.json"));
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);

		Table table1 = new Table();
		table1.setSize(m.conf.WIDTH / 1f,m.conf.HEIGHT * 4f / 5f);
		table1.setPosition(m.conf.WIDTH * 0f / 4f,0);
		table1.layout();
//		table1.debug();
		stage.addActor(table1);

		Table table2 = new Table();
		table2.setSize(m.conf.WIDTH / 1f,m.conf.HEIGHT * 4f / 5f);
		table2.setPosition(m.conf.WIDTH * 0f / 4f,0);
		table2.layout();

		textbuttstyle = new TextButtonStyle(skin.get(TextButtonStyle.class));

		makeButton("New", table1).addListener(new InputListener() { public boolean touchDown (
				InputEvent event, float x, float y, int pointer, int button) {
			stage.addActor(table2); table1.remove(); return false; }});
//		makeButton("Load", table1).addListener(new InputListener() { public boolean touchDown (
//				InputEvent event, float x, float y, int pointer, int button) {
//			return false; }});
//		makeButton("Join", table1).addListener(new InputListener() { public boolean touchDown (
//				InputEvent event, float x, float y, int pointer, int button) {
//			return false; }});
//		makeButton("Test - Net", table1).addListener(new InputListener() { public boolean touchDown (
//				InputEvent event, float x, float y, int pointer, int button) {
//			Lwjgl3Launcher.launch_net_apps(); return false; }});
		makeButton("About", table1).addListener(new InputListener() { public boolean touchDown (
				InputEvent event, float x, float y, int pointer, int button) {
			return false; }});
		makeButton("Exit", table1).addListener(new InputListener() { public boolean touchDown (
				InputEvent event, float x, float y, int pointer, int button) {
			main.exit(); return false; }});

		CheckBox themeCheckBox = new CheckBox("Dark Theme", skin);
		themeCheckBox.setChecked(!PlaneApplet.RELEASE);
		table1.row().fill().pad(100,10,10,10).minWidth(main.conf.WIDTH / 5f);
		table1.add(themeCheckBox);
		CheckBox fullScreenCheckBox = new CheckBox("Fullscreen", skin);
		fullScreenCheckBox.setChecked(PlaneApplet.START_FULLSCREEN);
		table1.row().fill().pad(10).minWidth(main.conf.WIDTH / 5f);
		table1.add(fullScreenCheckBox);
		table1.row().fill().pad(10).minWidth(main.conf.WIDTH / 5f);
		table1.add(new Label("Some text, contact, ext ... ", skin));

		
		table2.row().fill().pad(10).minWidth(main.conf.WIDTH / 5f);
		table2.add(new Label("Build Models :", skin));
		
		for (String nm : PlaneApplet.getModels()) {
			makeButton(nm, table2).addListener(new InputListener() { public boolean touchDown (
					InputEvent event, float x, float y, int pointer, int button) {
				main.launch_nodulo(nm, themeCheckBox.isChecked(), 
						fullScreenCheckBox.isChecked()); return false; }}); }
		
		makeButton("Back", table2).addListener(new InputListener() { public boolean touchDown (
				InputEvent event, float x, float y, int pointer, int button) {
			stage.addActor(table1); table2.remove(); return false; }});
		
		
	}
	
	private TextButton makeButton(String t, Table table) {
		TextButton button = new TextButton(t, textbuttstyle);
		table.row()
		.fill()
		.pad(10)
		.minWidth(main.conf.WIDTH / 4f)
		.minHeight(main.conf.HEIGHT / 18f)
		;
		table.add(button);
		return button; }
	
	@Override
	public nDrawer getDrawer() { return drawer; }
	@Override
	public Viewport getViewport() { return viewport; }
	@Override
	public Rectangle getScreenRect() { return screenrect; }
	@Override
	public OrthographicCamera getCamera() { return camera; }
	
	@Override
	public void show() {
		// Prepare your screen here.
		
	}

	@Override
	public void render(float delta) {
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
		ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);

		drawer.draw_begin();
		
		title_effect.draw();

		drawer.fill(60); drawer.stroke(0,6f);
		float sx = screenrect.width / 1.5f;
		float sy = screenrect.height / 6f;
		drawer.rect(screenrect.width / 2f - sx / 2f, 
				screenrect.height * 4f / 5f - sy / 2f + 10f, 
				sx, sy);
		sx = screenrect.width / 3.5f;
		sy = screenrect.height * 3f / 5f;
		drawer.rect(screenrect.width / 2f - sx / 2f, 
				0f + sy / 6f, 
				sx, sy);
		drawer.fill(255); drawer.stroke(0,3f); drawer.setLargeFont();
		drawer.text("NODULO",screenrect.width / 2f, screenrect.height * 4f / 5f, 120);
		drawer.setDefaultFont();
		
		drawer.draw_end();
		
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		if(width <= 0 || height <= 0) return;
		stage.getViewport().update(width, height, true);
		screenrect.set(0, 0, width, height);
		viewport.update(width, height, false); 

		camera.setToOrtho(false, width, height);
		camera.position.set(width / 2, height / 2, 0);
		camera.update();

        drawer.resize(width, height);
        
	}

	@Override
	public void pause() {
		// Invoked when your application is paused.
		
	}

	@Override
	public void resume() {
		// Invoked when your application is resumed after pause.
		
	}

	@Override
	public void hide() {
		// This method is called when another screen replaces this one.
		
	}

	@Override
	public void dispose() {
		// Destroy screen's assets here.
		stage.dispose();
		skin.dispose(); 
		drawer.dispose();
	}
	
	
	
	

	public static class TitleEffect {
		public TitleScreen app;
		public nDrawable drawable;
		int sx, sy, w = 0, h = 0;
		float density = 1.0f/10000.0f;
		float scale = 6.0f;
		int hue = 190;
		int[][][] world;
		Color[][] pic;
		int frame_counter = 0;
		public TitleEffect(TitleScreen a) {
			app = a;
			drawable = new nDrawable() {public void drawing() {
				draw(); }};
			w = (int)(app.main.conf.WIDTH / scale);
			h = (int)(app.main.conf.HEIGHT / scale);
			sx = w; sy = h;
			world = new int[sx][sy][2];
			pic = new Color[sx][sy];
			for (int x = 0; x < sx; x++) 
				for (int y = 0; y < sy; y++) {
					pic[x][y] = Utl.color(0); }
			reset();
		}
		void reset() {
			for (int x = 0; x < sx; x++) 
				for (int y = 0; y < sy; y++) {
					world[x][y][0] = 0;
					world[x][y][1] = 0;
//					pic[x][y].set(Color.BLACK); 
				}
			// Set random cells to 'on'
			for (int i = 0; i < sx * sy * density; i++) {
				int a = (int)(Math.random()*(float)(sx-1));
				int b = (int)(Math.random()*(float)(sy-1));
				world[a][b][1] = 1; }
			frame_counter = 0;
		}

		void draw() {
			frame_counter++;
			if (frame_counter%100 == 0) reset();
			app.drawer.fill(0); app.drawer.noStroke();
			app.drawer.rect(0,0,app.main.conf.WIDTH,app.main.conf.HEIGHT);
			conway_up();
			hue = (hue+10)%210;
			for (int x = 0; x < sx; x=x+1) for (int y = 0; y < sy; y=y+1) {
				app.drawer.fill(pic[x][y]); app.drawer.noStroke();
				app.drawer.rect(x*scale,y*scale,scale,scale); }
		}

		void conway_up() {
			for (int x = 0; x < sx; x++) for (int y = 0; y < sy; y++) {
				if ((world[x][y][1] == 1) || 
						(world[x][y][1] == 0 && world[x][y][0] == 1)) { 
					world[x][y][0] = 1; }
				if (world[x][y][1] == -1) { world[x][y][0] = 0; }
				world[x][y][1] = 0;
			}
			for (int x = 0; x < sx; x++) for (int y = 0; y < sy; y++) {
				int count = neighbors(x, y);
				if ((count == 1 || count == 2) && world[x][y][0] == 0) {
					world[x][y][1] = 1;
					pic[x][y].set(Utl.color(40+hue));
				}
			}
		}

		// Count the number of adjacent cells 'on'
		int neighbors(int x, int y) {
			return world[(x + 1) % sx][y][0] + 
					world[x][(y + 1) % sy][0] + 
					world[(x + sx - 1) % sx][y][0] + 
					world[x][(y + sy - 1) % sy][0] + 
					world[(x + 1) % sx][(y + 1) % sy][0] + 
					world[(x + sx - 1) % sx][(y + 1) % sy][0] + 
					world[(x + sx - 1) % sx][(y + sy - 1) % sy][0] + 
					world[(x + 1) % sx][(y + sy - 1) % sy][0];
		}
	}
	
	public TitleEffect title_effect;
}