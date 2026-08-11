package aa_nodulo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.generator.noise.NoiseGenerator;

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

		public pGround init(sValueBloc b) { return (pGround) super.init(b); }

		public pSpace space;

		public sBoo val_do_draw, val_debug_ground, val_white_ground;
		sFlt val_limit_dist;

		pView view;

		int size = 96;
		Color[] ground_colors, fog_colors;
		float cell_size = 500f;
		Color ground_col, fog_col, border_col;
		
		public void system_init() {
			bloc.addObject("ground", this);

			app.storeSystemType(bloc.ref, this.getClass());

			val_do_draw = bloc.obtainBoo("val_do_draw", app.config.DRAW_GROUND);
			val_debug_ground = bloc.obtainBoo("val_debug_ground", false);
			val_white_ground = bloc.obtainBoo("val_white_ground", false);
			val_limit_dist = bloc.obtainFlt("val_limit_dist", 5000f);

			
			final Grid grid = new Grid(size);
			NoiseGenerator noiseGenerator = new NoiseGenerator();
//			nNoise.noiseStage(grid, noiseGenerator, 64, 1f);
			nNoise.noiseStage(grid, noiseGenerator, 13, 0.5f);
//			nNoise.noiseStage(grid, noiseGenerator, 7, 0.2f);
			nNoise.noiseStage(grid, noiseGenerator, 5, 0.3f);
//			nNoise.noiseStage(grid, noiseGenerator, 4, 0.4f);
			nNoise.noiseStage(grid, noiseGenerator, 1, 0.05f);

			
			
			ground_col = Utl.color(225, 224, 220, 255);
			fog_col = Utl.color(60, 65, 80, 255);
			border_col = Utl.color(130, 100, 50, 120);

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
						else if (cel >= 0.25f) cel = 0.25f + (cel-0.25f) / 4f;
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

		public void draw_ground() { 
			if (val_do_draw.get()) {
				if (val_debug_ground.get()) {
					app.push();
					app.translate(-size*cell_size/2f,-size*cell_size/2f);
					app.fill(0,0); app.noStroke();
					app.rect(0,0,size*cell_size,size*cell_size);
					app.pop();
				} else if (val_white_ground.get()) {
					app.push();
					app.translate(-size*cell_size/2f,-size*cell_size/2f);
					app.fill(200); app.noStroke();
					app.rect(0,0,size*cell_size,size*cell_size);
					app.pop();
				} else {
					app.push();
					app.translate(-size*cell_size/2f,-size*cell_size/2f);
					app.gdx.drawer.grid(size, cell_size, ground_colors);
					app.pop();
				}
			}
		}
		public void draw_fog() { 
			if (val_do_draw.get() && !val_debug_ground.get() 
					&& !val_white_ground.get()) {
				app.push();
				app.translate(-size*cell_size,-size*cell_size);
				app.gdx.drawer.grid(size, cell_size*2f, fog_colors);
				app.pop();
			}
			

			
		}

}
