package app;

//import org.lwjgl.BufferUtils;
//import org.lwjgl.LWJGLException;
//import org.lwjgl.input.Mouse;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
//import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
//import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
//import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.GdxRuntimeException;

/** NOTE: Presumably LibGDX will implement this functionality in time, making
 * this snippet obsolete. */
public class HideCursor{// implements ApplicationListener {

//	public static void setHWCursorVisible(boolean visible) throws LWJGLException {
//		if (Gdx.app.getType() != ApplicationType.Desktop && Gdx.app instanceof LwjglApplication)
//			return;
//		if (emptyCursor == null) {
//			if (Mouse.isCreated()) {
//				int min = org.lwjgl.input.Cursor.getMinCursorSize();
//				IntBuffer tmp = BufferUtils.createIntBuffer(min * min);
//				emptyCursor = new org.lwjgl.input.Cursor(min, min, min / 2, min / 2, 1, tmp, null);
//			} else {
//				throw new LWJGLException(
//						"Could not create empty cursor before Mouse object is created");
//			}
//		}
//		if (Mouse.isInsideWindow())
//			Mouse.setNativeCursor(visible ? null : emptyCursor);
//	}
//
//	public static void main(String[] args) {
//		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
//		cfg.title = "atmos";
//		cfg.useGL20 = true;
//		cfg.width = 480;
//		cfg.height = 320;
//		LwjglApplication app = new LwjglApplication(new HideCursor(), cfg);
//
//	}
//
//	SpriteBatch batch;
//	OrthographicCamera camera;
//	static lwjgl.input.Cursor emptyCursor;
//	boolean hwVisible = false;
//	Texture cursor;
//	int xHotspot, yHotspot;
//
//	@Override
//	public void create() {
//		batch = new SpriteBatch();
//		camera = new OrthographicCamera();
//
//		cursor = new Texture(Gdx.files.internal("data/cursor.png"));
//		xHotspot = 0;
//		yHotspot = cursor.getHeight(); // lower left origin!
//
//		// press SPACE to toggle software cursor
//		Gdx.input.setInputProcessor(new InputAdapter() {
//			public boolean keyDown(int key) {
//				if (key == Keys.SPACE) {
//					hwVisible = !hwVisible;
//					return true;
//				}
//				return false;
//			}
//		});
//	}
//
//	@Override
//	public void resize(int width, int height) {
//		camera.setToOrtho(false, width, height);
//		batch.setProjectionMatrix(camera.combined);
//	}
//
//	@Override
//	public void render() {
//		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
//		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
//
//		try {
//			setHWCursorVisible(hwVisible);
//		} catch (LWJGLException e) {
//			throw new GdxRuntimeException(e);
//		}
//
//		batch.begin();
//
//		// ... draw sprites ...
//
//		// draw SW cursor
//		if (!hwVisible) {
//			int x = Gdx.input.getX();
//			int y = Gdx.input.getY();
//			batch.draw(cursor, x - xHotspot, Gdx.graphics.getHeight() - y - 1 - yHotspot);
//		}
//		batch.end();
//	}
//
//	@Override
//	public void pause() {
//
//	}
//
//	@Override
//	public void resume() {
//
//	}
//
//	@Override
//	public void dispose() {
//		cursor.dispose();
//		batch.dispose();
//	}
}
