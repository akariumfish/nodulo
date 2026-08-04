package com.noodle.nodulo;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import app.nDrawer;

/** First screen of the application. Displayed after the application is created. */
public class TitleScreen implements Screen ,nDrawer.DrawContext {
	
	public Main main;
	
	Skin skinl,skinm,skinh;
	Stage stage;

    public OrthographicCamera camera; 
	public ScreenViewport viewport; 
	public nDrawer drawer;
	public Rectangle screenrect;

	
	public TitleScreen(Main m) {
		main = m;

		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		screenrect = new Rectangle(0,0,m.conf.WIDTH,m.conf.HEIGHT);
		
		camera = new OrthographicCamera(m.conf.WIDTH,m.conf.HEIGHT);
		viewport = new ScreenViewport(camera);

		camera.position.set(m.conf.WIDTH / 2, m.conf.HEIGHT / 2, 0);
		camera.update();
		
		drawer = new nDrawer(this, false);
		
		skinl = new Skin(Gdx.files.internal("ui/Holo-dark-ldpi.json"));
		skinm = new Skin(Gdx.files.internal("ui/Holo-dark-mdpi.json"));
		skinh = new Skin(Gdx.files.internal("ui/Holo-dark-hdpi.json"));
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);

		Table table1 = new Table();
		table1.setSize(m.conf.WIDTH,m.conf.HEIGHT * 4f / 5f);
		table1.setPosition(0,0);
		stage.addActor(table1);
		
		Table table2 = new Table();
		table2.setSize(m.conf.WIDTH,m.conf.HEIGHT * 4f / 5f);
		table2.setPosition(0,0);

		TextButtonStyle style = new TextButtonStyle(skinh.get(TextButtonStyle.class));

		TextButton button = new TextButton("Back", style);
		button.addListener(new InputListener() { public boolean touchDown (
				InputEvent event, float x, float y, int pointer, int button) {
			stage.addActor(table1); table2.remove(); return false; }});
		
		table2.row();
		table2.add(button);
		

		Label label = new Label("Some text", skinh);
//		label.setFontScale(6f);
//		label.setWrap(true);
		table1.row();
		table1.add(label);
		button = new TextButton("New", style);
		button.addListener(new InputListener() { public boolean touchDown (
				InputEvent event, float x, float y, int pointer, int button) {
			stage.addActor(table2); table1.remove(); 
			return false; }});
		table1.row();
		table1.add(button);

		button = new TextButton("Load", style);
		button.addListener(new InputListener() { public boolean touchDown (
				InputEvent event, float x, float y, int pointer, int button) {
			return false; }});
		table1.row();
		table1.add(button);

		button = new TextButton("Join", style);
		button.addListener(new InputListener() { public boolean touchDown (
				InputEvent event, float x, float y, int pointer, int button) {
			return false; }});
		table1.row();
		table1.add(button);

		button = new TextButton("Setting", style);
		button.addListener(new InputListener() { public boolean touchDown (
				InputEvent event, float x, float y, int pointer, int button) {
			return false; }});
		table1.row();
		table1.add(button);

		button = new TextButton("Exit", style);
		button.addListener(new InputListener() { public boolean touchDown (
				InputEvent event, float x, float y, int pointer, int button) {
			Gdx.app.exit(); return false; }});
		table1.row();
		table1.add(button);
		
		table1.layout();
//		table.debug();

	}
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
		skinl.dispose(); skinm.dispose(); skinh.dispose(); 
		drawer.dispose();
	}
}