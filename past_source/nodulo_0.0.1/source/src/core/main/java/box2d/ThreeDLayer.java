package box2d;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

import aa_nodulo.PlaneApplet;
import aa_nodulo.pView;
import gui.nGUI;
import util.Utl;
import util.nRun;

public class ThreeDLayer extends nRenderer.Layer {

	PlaneApplet app;

	pView view;
	
	PerspectiveCamera cam;

	public ModelBatch modelBatch;
	public Model model;
	public ArrayList<ModelInstance> instance;
	public Environment environment;

	VfxFrameBuffer render_buffer;

	public ThreeDLayer(nRenderer tm, int p) {
		super(tm,p);
		app = tm.app;
		view = tm.view;
		
		modelBatch = new ModelBatch();
		
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0f, 0f, 100f);
		cam.lookAt(0,0,0);
		cam.near = 0f;
		cam.far = 1000f;
		cam.update();

		render_buffer = new VfxFrameBuffer(Pixmap.Format.RGBA8888);
		render_buffer.initialize((int)app.gdx.getscreenwidth(),
				(int)app.gdx.getscreenheight());

		app.gdx.addEventScreen(new nRun() { public void run() {
			render_buffer.reset();
			render_buffer.initialize((int)app.gdx.getscreenwidth(),
					(int)app.gdx.getscreenheight());
			cam.viewportWidth = app.gdx.getscreenwidth();
			cam.viewportHeight = app.gdx.getscreenheight();
			cam.update();
		}});

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, 1f));
//		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0f, 0f, 0f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0.5f, 0.5f, -1f));
//		environment.add(new PointLight().set(1f, 1f, 1f, 0f, 0f, -1f,10000f));

		float v = 400f;
		
		ModelBuilder modelBuilder = new ModelBuilder();
		model = modelBuilder.createBox(v*0.9f, v*0.9f, 1000000f, 
			new Material(ColorAttribute.createDiffuse(new Color(0.3f, 0.3f, 0.3f, 1))),
			Usage.Position | Usage.Normal);
		
		instance = new ArrayList<ModelInstance>();
		
		v *= 1.5f;

		instance.add(new ModelInstance(model, v, v, 0f));
		instance.add(new ModelInstance(model, v, 0f, 0f));
		instance.add(new ModelInstance(model, v, -v, 0f));
		instance.add(new ModelInstance(model, 0f, v, 0f));
		instance.add(new ModelInstance(model, 0f, -v, 0f));
		instance.add(new ModelInstance(model, -v, v, 0f));
		instance.add(new ModelInstance(model, -v, 0f, 0f));
		instance.add(new ModelInstance(model, -v, -v, 0f));
		
	}

	private Vector2 render_pos = new Vector2();
	private Vector2 render_size = new Vector2();
	public void prepareCam(
			Vector2 view_pos, Vector2 view_size, 
			Vector2 cam_pos, float cam_scale, float cam_rot) {

		Vector2 position2 = new Vector2();
		float zoom = 1;
		
		zoom = 1f / cam_scale;
		position2.set(0,0);
//		position2.add(view_pos);
//		position2.x += view_size.x / 2.0f;
//		position2.y -= view_size.y / 2.0f + nGUI.book.RS;
//		position2.sub(app.gdx.getscreenwidth() / 2.0f, app.gdx.getscreenheight() / 2.0f);
////		position2.scl(zoom).rotateRad(-cam_rot);
		position2.add(cam_pos);
		position2.scl(1f/zoom);
//		position2.rotateRad(-cam_rot);
//		position2.rotateRad(cam_rot);
//		position2.scl(-1f);

//		cam.position.set(position2.x, position2.y, 100f);
		cam.position.set(0f, 0f, 1f*zoom);
//		Vector2 u = new Vector2(0f,1f);
//		u.rotateRad(cam_rot);
//		cam.up.set(u.x, u.y, 0f);
//		cam.lookAt(position2.x, position2.y,0);
		cam.lookAt(0,0,0);
		cam.position.add(position2.x, position2.y, 0f);
		cam.update();
		
		render_size.set(app.gdx.getscreenwidth(), 
				app.gdx.getscreenheight());
		
		render_pos.set(0,0);
		render_pos.add(view_pos);
		render_pos.x += view_size.x / 2.0f;
		render_pos.y -= view_size.y / 2.0f + nGUI.book.RS;
		render_pos.sub(app.gdx.getscreenwidth() / 2.0f, app.gdx.getscreenheight() / 2.0f);

	}
	
	@Override
	public void render() {
		
		if (!rend.box.drawground()) return;

		removeScissors();
		
		prepareCam(view.val_pos.get(), view.val_view_size.get(), 
				view.val_cam_pos.get(), view.val_cam_scale.get(), view.val_cam_rot.get());

		render_buffer.begin(); 

		Color c = Utl.color(0,0);
		Gdx.gl.glClearColor(c.r,c.g,c.b,c.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		modelBatch.begin(cam);
		for (ModelInstance m : instance)
			modelBatch.render(m, environment);
		modelBatch.end();

		render_buffer.end();
		
		restoreScissors();
		
		app.gdx.drawer.spritebatch.begin();

		app.gdx.drawer.spritebatch.draw(render_buffer.getTexture(), 
				render_pos.x, render_pos.y, 
				render_size.x, render_size.y, 
//				app.gdx.getscreenwidth(), 
//				app.gdx.getscreenheight(), 
				0, 0, 1, 1);
		
		app.gdx.drawer.spritebatch.end();

	}

	private final ArrayList<Rectangle> scissors = new ArrayList<Rectangle>();

	public void removeScissors() {
		for (Rectangle r : Utl.duplic(app.gui.scissors)) {
			scissors.add(r); ScissorStack.popScissors(); }
		app.gui.scissors.clear();
	}
	public void restoreScissors() {
		for (Rectangle r : Utl.duplic(scissors)) {
			app.gui.scissors.add(r); ScissorStack.pushScissors(r); }
		scissors.clear();
	}

	
	
}
