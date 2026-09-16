package com.noodle.nodulo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {

	Stage stage;
	
	public void setup() {
		
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		Skin skin = new Skin(Gdx.files.internal("ui/skin.json"));

		Label nameLabel = new Label("Name:", skin);
		TextField nameText = new TextField("", skin);
		Label addressLabel = new Label("Address:", skin);
		TextField addressText = new TextField("", skin);

		Table table = new Table();
		stage.addActor(table);
		table.setSize(260, 195);
		table.setPosition(190, 142);
		// table.align(Align.right | Align.bottom);

		table.debug();

		TextureRegion upRegion = skin.getRegion("button-normal");
		TextureRegion downRegion = skin.getRegion("button-normal-pressed");
		BitmapFont buttonFont = skin.getFont("default");

		TextButton button = new TextButton("Button 1", skin);
		button.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("touchDown 1");
				return false;
			}
		});
		table.add(button);
		// table.setTouchable(Touchable.disabled);

		Table table2 = new Table();
		stage.addActor(table2);
		table2.setFillParent(true);
		table2.bottom();

		TextButton button2 = new TextButton("Button 2", skin);
		button2.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				System.out.println("2!");
			}
		});
		button2.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("touchDown 2");
				return false;
			}
		});
		table2.add(button2);

	}
	
	@Override
	public void show() {
		// Prepare your screen here.
		

	}

	@Override
	public void render(float delta) {

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		// If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
		// In that case, we don't resize anything, and wait for the window to be a normal size before updating.
		if(width <= 0 || height <= 0) return;
		stage.getViewport().update(width, height, true);

		// Resize your screen here. The parameters represent the new window size.
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
	}
}