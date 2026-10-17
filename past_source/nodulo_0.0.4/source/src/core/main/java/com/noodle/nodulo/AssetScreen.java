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
public class AssetScreen implements Screen ,nDrawer.DrawContext {
	
	public Main main;
	
	Skin skin;
	Stage stage;

    public OrthographicCamera camera; 
	public ScreenViewport viewport; 
	public Rectangle screenrect;
	
	public nDrawer drawer;

	TextButtonStyle textbuttstyle;
	
	public AssetScreen(Main m) {
		main = m;

		screenrect = new Rectangle(0,0,m.conf.WIDTH,m.conf.HEIGHT);
		
		camera = main.camera;
		viewport = main.viewport;
		camera.position.set(m.conf.WIDTH / 2, m.conf.HEIGHT / 2, 0);
		camera.update();
		
		drawer = new nDrawer(this, false);

		skin = new Skin(Gdx.files.internal("ui/skin.json"));
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);

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
	
}