package aa_nodulo;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.generator.noise.NoiseGenerator;
import com.noodle.nodulo.GdxApp;

import data.*;
import gui.*;
import util.*;

public class pGround extends pSystem {

	public static sBloc_Builder builder = null;

	public static void build(sData data) {

		builder = builder(data, "ground", pGround.class, true, new nRun() { public void run(Object o) {
			sValueBloc b = (sValueBloc)o; newObject(b); }});

	}

	public static void dispose(PlaneApplet app) { pool.dispose(); }
	public static final nPool<pGround> pool = new nPool<pGround>() {
		protected pGround newObject() { return new pGround(); } };
		public static pGround newObject(sValueBloc b) {
			return pool.obtain().init(b); }

		public pGround() { 
			super(); 
			draw_ground_run = new nDrawable() { public void drawing() { draw_ground(); }}; 
			draw_fog_run = new nDrawable() { public void drawing() { draw_fog(); }}; 
		}

		nDrawable draw_ground_run, draw_fog_run;

		OrthographicCamera cam;
		Map tilemap;

		public pGround init(sValueBloc b) { return (pGround) super.init(b); }

		public pSpace space;
		public pBox2d box;

		public sBoo val_do_draw, val_grid_ground, val_debug_ground, val_white_ground;
		sFlt val_limit_dist;

		pView view;

		int size = 96;
		Color[] ground_colors, fog_colors;
		float cell_size = 300f;
		Color ground_col, fog_col, border_col;
		
		public void system_init() {
			bloc.addObject("ground", this);

			app.storeSystemType(bloc.ref, this.getClass());

			val_do_draw = bloc.obtainBoo("val_do_draw", app.config.DRAW_GROUND);
			val_grid_ground = bloc.obtainBoo("val_grid_ground", false);
			val_debug_ground = bloc.obtainBoo("val_debug_ground", false);
			val_white_ground = bloc.obtainBoo("val_white_ground", false);
			val_limit_dist = bloc.obtainFlt("val_limit_dist", 8000f);

			cam = new OrthographicCamera(GdxApp.WIDTH, GdxApp.HEIGHT);

	        tilemap = new Map("Map.tmx", GdxApp.app.drawer.spritebatch);
	        
	        tilemap.toggleLightingLayerVisibility();
	        tilemap.restartLightingGeneration();
			
			
			
			
			
			
			final Grid grid = new Grid(size);
			NoiseGenerator noiseGenerator = new NoiseGenerator();
			nNoise.noiseStage(grid, noiseGenerator, 37, 0.4f);
			nNoise.noiseStage(grid, noiseGenerator, 13, 0.5f);
			nNoise.noiseStage(grid, noiseGenerator, 7, 0.2f);
//			nNoise.noiseStage(grid, noiseGenerator, 5, 0.3f);
//			nNoise.noiseStage(grid, noiseGenerator, 4, 0.2f);
			nNoise.noiseStage(grid, noiseGenerator, 1, 0.1f);

			ground_col = Utl.color(225, 224, 220, 255);
			fog_col = Utl.color(160, 165, 180, 255);
			border_col = Utl.color(180, 150, 100, 160);

			ground_colors = new Color[size*size];
			fog_colors = new Color[size*size];
			for (int x = 0; x < grid.getWidth(); x++) {
				for (int y = 0; y < grid.getHeight(); y++) {
					final float cell = grid.get(x, y);
					float cel = cell - 0.29f;
					if (cel < 0f) cel = 0f;
					if (cel > 0.08f && cel < 0.085f) {
						ground_colors[x+size*y] = Utl.color(
								(int)(border_col.r*255), 
								(int)(border_col.g*255),
								(int)(border_col.b*255),
								(int)(255*border_col.a));
					} else {
						if (cel <= 0.08f) cel = 0.08f - cel;
						else if (cel >= 0.25f) cel = 0.25f + (cel-0.25f) / 1f;
						ground_colors[x+size*y] = Utl.color(
								(int)(cel*ground_col.r*255), 
								(int)(cel*ground_col.g*255),
								(int)(cel*ground_col.b*255),
								(int)(255*ground_col.a));
					}
					float dist = new Vector2((x-grid.getWidth()/2f)*cell_size,
							(y-grid.getHeight()/2f)*cell_size).len();
//					Utl.logn(""+x+" "+y+" "+dist);

					float limit = val_limit_dist.get();
					float grid_end = (grid.getWidth()/2f)*cell_size*0.8f;
					float fog_start = limit / 1.8f;
					cel = 0;
					if (dist > fog_start && dist <= limit) {
						limit -= fog_start; dist -= fog_start;
						cel = cell * dist/limit;
					} else if (dist > limit && dist <= grid_end) { 
						grid_end -= limit; dist -= limit;
						cel = (dist/grid_end + cell*(grid_end-dist)/grid_end); 
					} else if (dist > grid_end) { cel = 1f; }
					
					fog_colors[x+size*y] = new Color(
							fog_col.r, fog_col.g, fog_col.b, cel*fog_col.a);
				}
			}

		}
		public void system_load() {
			
			box = app.getSystem(pBox2d.class);

			app.view.addDrawable(0,draw_ground_run);
			app.view.addDrawable(15,draw_fog_run);
			space = app.space;
			view = app.view;
			//		if (!app.RELEASE) 
			tool_setup(false);

		}
		public void system_clear() {
			app.view.removeDrawable(draw_ground_run);
			app.view.removeDrawable(draw_fog_run);
		}

		public void tool_init(nInterface interf) {

			interf.setContext(bloc);
			interf.add_row();
			interf.add_row_switch_boo(4, "draw", "val_do_draw");
			interf.add_row_label(6, "");
			interf.add_row();
			interf.add_row_switch_boo(4, "debug", "val_debug_ground");
			interf.add_row_label(2, "");
			interf.add_row_switch_boo(4, "white", "val_white_ground");

		}

		public void frame(float delta) { }
		public void tick(float delta) { }
		public void net_frame(float delta) { }
		public void net_tick(float delta) { }

		public final ArrayList<Rectangle> scissors = new ArrayList<Rectangle>();
		
		public void draw_ground() { 
			if (val_do_draw.get()) {
				

				app.push();
				app.translate(-size*cell_size/2f,-size*cell_size/2f);
				app.fill(100,100); app.noStroke();
				app.rect(0,0,size*cell_size,size*cell_size);
				app.pop();
				
//				if (box.val_do_ray.get() && app.gdx.drawer.USE_FX) {
//
//					app.gdx.drawer.pause_batch();
//					
//					box.buffer.begin(); 
//					
//					ScreenUtils.clear(app.gdx.drawer.buffer_clear_color);
//					
////					Color c = app.gdx.drawer.buffer_clear_color;
////			        Gdx.gl.glClearColor(c.r,c.g,c.b,c.a);
////			        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//	//
////			        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
////			        Gdx.gl20.glEnable(GL20.GL_BLEND);
//			        
//					app.gdx.drawer.restart_batch();
//					
//				}

				app.gdx.drawer.end();

		        tilemap.update();

		        
		        
				cam.setToOrtho(false, (int)(app.gdx.getscreenwidth()), 
						(int)(app.gdx.getscreenheight()));
				cam.zoom = 1f;
				cam.position.set(0f, 0f, 0f);
				cam.direction.set(0f, 0f, -1f);
				Vector2 u = new Vector2(0f,1f);
				cam.up.set(u.x, u.y, 0f);//.rotateRad(-view.val_cam_rot.get())
				cam.update();
				
				

				float scale = view.val_cam_scale.get();
				float sclinv = 1f / scale;

				Vector2 screen_center = new Vector2(app.gdx.getscreenwidth() / 2f, 
						app.gdx.getscreenheight() / 2f);
				Vector2 view_center = new Vector2(view.val_pos.get());
				view_center.x += view.val_view_size.x() / 2.0f;
				view_center.y -= view.val_view_size.y() / 2.0f + nGUI.book.RS;

				
		        tilemap.renderer.transform = new Matrix4()
		        		.setToTranslation(0f,0f,0f);
		        
		        Vector2 sv = new Vector2(view_center).sub(screen_center);
		        tilemap.renderer.transform.translate(sv.x,sv.y,0f);
		        
				Vector2 m = new Vector2();
				m.add(view.val_cam_pos.get());
				m.scl(view.val_cam_scale.get());
//				m.scl(1f/view.val_cam_scale.get());
//				m.scl(1f/10f);
//				m.rotateRad(-view.val_cam_rot.get());
		        tilemap.renderer.transform.translate(m.x,m.y,0f);

		        tilemap.renderer.transform.scl(view.val_cam_scale.get());
		        
		        tilemap.renderer.transform.scl(100f);

//		        tilemap.renderer.transform.rotateRad(0f,0f,-1f, view.val_cam_rot.get());

//		        

//				Vector2 n = new Vector2();
//				n.sub(view.val_cam_pos.get());
//				n.scl(view.val_cam_scale.get());
////				m.scl(1f/view.val_cam_scale.get());
////				m.scl(1f/10f);
//				m.rotateRad(view.val_cam_rot.get());
//		        tilemap.renderer.transform.translate(n.x,n.y,0f);
		        
		        
		        
		        tilemap.render(cam.projection, 
		        		-app.gdx.getscreenwidth() / 2f, 
		        		-app.gdx.getscreenheight() / 2f,
		        		app.gdx.getscreenwidth(), 
					app.gdx.getscreenheight());

				app.gdx.drawer.begin();
				
				
//				if (val_debug_ground.get()) {
//					app.push();
//					app.translate(-size*cell_size/2f,-size*cell_size/2f);
//					app.fill(0,0); app.noStroke();
//					app.rect(0,0,size*cell_size,size*cell_size);
//					app.pop();
//				} else if (val_white_ground.get()) {
//					app.push();
//					app.translate(-size*cell_size/2f,-size*cell_size/2f);
//					app.fill(200); app.noStroke();
//					app.rect(0,0,size*cell_size,size*cell_size);
//					app.pop();
//				} else if (val_grid_ground.get()) {
//					app.push();
//					app.translate(-size*cell_size/2f,-size*cell_size/2f);
//					app.gdx.drawer.grid(size, cell_size, ground_colors);
//					app.pop();
//				} 
			}
		}
		public void draw_fog() { 
//			if (val_do_draw.get() && !val_debug_ground.get() 
//					&& !val_white_ground.get()) {
//				app.push();
//				app.translate(-size*cell_size,-size*cell_size);
//				app.gdx.drawer.grid(size, cell_size*2f, fog_colors);
//				app.pop();
//			}
			

			
		}

}
