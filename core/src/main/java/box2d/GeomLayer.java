package box2d;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.noodle.nodulo.GdxApp;

import aa_nodulo.pView;
import app.nDrawer;
import gui.nGUI;
import util.Utl;

public class GeomLayer extends nRenderer.Layer {

	public final Array<Geom> geomList = new Array<Geom>(false, 4);

	int lightRenderedLastFrame = 0;

	
	final ShaderProgram geomShader;

	/**
	 * Blend function for lights rendering without shadows and diffusion 
	 * <p>Default: (GL20.GL_SRC_ALPHA, GL20.GL_ONE)
	 */
	public final BlendFunc simpleBlendFunc =
	//def simple
//			new BlendFunc(GL20.GL_ONE, GL20.GL_ONE);
	
	//def shadow
			new BlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
	
	//COLOR
//			new BlendFunc(GL20.GL_DST_COLOR, GL20.GL_ONE);
	

	final Matrix4 combined = new Matrix4();

	private final pView view;
	private final OrthographicCamera cam;
	
	public GeomLayer(nRenderer tm) { this(tm,0); }
	public GeomLayer(nRenderer tm, int p) {
		super(tm,p);
		view = tm.view;
		cam = tm.cam;
		geomShader = LightShader.createLightShader();
//		geomShader = WithoutShadowShader.createLightShader();
	}

	
	@Override
	public void render() {
		for (Geom g : geomList) {
			if (g.active) g.update();
		}
		

		cam.setToOrtho(false, (int)(GdxApp.app.getscreenwidth()), 
				(int)(GdxApp.app.getscreenheight()));
		Vector2 view_center = new Vector2(view.val_pos.get());
		view_center.x += view.val_view_size.x() / 2.0f;
		view_center.y -= view.val_view_size.y() / 2.0f + nGUI.book.RS;
		float scale = view.val_cam_scale.get();
		float sclinv = 1f / scale;
		float rot = view.val_cam_rot.get();
		float rotDeg = Utl.radToDeg(rot);
		Vector2 m = new Vector2(view_center)
				.sub(GdxApp.app.getscreenwidth() / 2.0f, 
						GdxApp.app.getscreenheight() / 2.0f);
		m.scl(sclinv).rotateRad(-view.val_cam_rot.get());
		m.add(view.val_cam_pos.get()).scl(-1f);
		cam.zoom = sclinv;
		cam.position.set(m.x, m.y, 0f);
		cam.direction.set(0f, 0f, -1f);
		Vector2 u = new Vector2(0f,1f).rotateRad(-view.val_cam_rot.get());
		cam.up.set(u.x, u.y, 0f);
		cam.update();
		System.arraycopy(cam.combined.val, 0, this.combined.val, 0, 16);

		
		Gdx.gl.glDepthMask(false);
		Gdx.gl.glEnable(GL20.GL_BLEND); 
		
		simpleBlendFunc.apply();

		ShaderProgram shader = geomShader;
		shader.bind();
		{
			geomShader.setUniformMatrix("u_projTrans", combined);
			shader.setUniformMatrix("u_projTrans", combined);

			for (Geom g : geomList) if (g.active) {
				g.render();
			}
		}
	}
	

	public void debugRender(nDrawer.Drawer draw) {
		GdxApp.app.drawer.restart_batch();
		for (Geom g : geomList) {
			g.debugRender(draw);
		}
		GdxApp.app.drawer.pause_batch();
	}

}
