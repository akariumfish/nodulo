package box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Mesh.VertexDataType;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.generator.noise.NoiseGenerator;

import app.nDrawer;
import util.Utl;
import util.nNoise;


public class GroundLight extends GridLight {

	int size;
	float cell_size;
	
	public GroundLight(LightLayer layer, boolean fog) { this(layer, 128, 500, fog); }
	public GroundLight(LightLayer layer, int w, float scl, boolean fog) {
		super(layer, w, scl);
		size = w; cell_size = scl;
		build_grid(w,scl);
		if (!fog) grid(ground_colors);
		else grid(fog_colors);
		updateMesh();
	}
	
	private static float limit_dist = 10000f;
	private static Color[] ground_colors, fog_colors;
	private static Color ground_col, fog_col, border_col;
	private static boolean grid_is_build = false;
	private static void build_grid(int size, float cell_size) {
		if (grid_is_build) return;
		grid_is_build = true;
		final Grid grid = new Grid(size);
		NoiseGenerator noiseGenerator = new NoiseGenerator();
		nNoise.noiseStage(grid, noiseGenerator, 37, 0.4f);
		nNoise.noiseStage(grid, noiseGenerator, 13, 0.5f);
		nNoise.noiseStage(grid, noiseGenerator, 7, 0.2f);
//		nNoise.noiseStage(grid, noiseGenerator, 5, 0.3f);
//		nNoise.noiseStage(grid, noiseGenerator, 4, 0.2f);
		nNoise.noiseStage(grid, noiseGenerator, 1, 0.1f);

		ground_col = Utl.color(225, 224, 220, 255);
		fog_col = Utl.color(50, 50, 60, 255);
		border_col = Utl.color(180, 150, 100, 160);

		ground_colors = new Color[size*size];
		fog_colors = new Color[size*size];
		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
				float dist = new Vector2((x-grid.getWidth()/2f)*cell_size,
						(y-grid.getHeight()/2f)*cell_size).len();
//				Utl.logn(""+x+" "+y+" "+dist);

				float limit = limit_dist*0.85f;
				float glimit = limit*1.3f*1.25f/0.85f;
				float grid_end = (grid.getWidth()/2f)*cell_size*0.5f;
				float fog_start = limit / 3f;
				float grid_alpha = 1f;
				if (dist > glimit && dist < glimit*1.5f) 
					grid_alpha = (glimit*0.5f - (dist-glimit)) / (glimit*0.5f); 
				else if (dist >= glimit*1.5f) grid_alpha = 0f;
				
				final float cell = grid.get(x, y);
				float cel = cell - 0.29f;
				if (cel < 0f) cel = 0f;
				if (cel > 0.08f && cel < 0.095f) {
					ground_colors[x+size*y] = Utl.color(
							(int)(border_col.r*255), 
							(int)(border_col.g*255),
							(int)(border_col.b*255),
							(int)(border_col.a*255));
				} else {
					if (cel <= 0.08f) cel = 0.08f - cel;
					else if (cel >= 0.25f) cel = 0.25f + (cel-0.25f) / 1f;
					ground_colors[x+size*y] = Utl.color(
							(int)(cel*ground_col.r*255), 
							(int)(cel*ground_col.g*255),
							(int)(cel*ground_col.b*255),
							(int)(ground_col.a*255*grid_alpha));
				}
				cel = 0;
				float c2 = 1f;
				if (dist > fog_start && dist <= limit) {
					limit -= fog_start; dist -= fog_start;
					cel = cell * dist/limit;
				} else if (dist > limit && dist <= grid_end) { 
					grid_end -= limit; dist -= limit;
					cel = (dist/grid_end + cell*(grid_end-dist)/grid_end); 
				} else if (dist > grid_end && dist < grid_end * 2f) { 
					cel = 1f; c2 = 1f - (dist - grid_end) / grid_end;
				} else if (dist >= grid_end * 2f) { cel = 1f; c2 = 0f; }
				
				fog_colors[x+size*y] = new Color(
						fog_col.r*c2, fog_col.g*c2, fog_col.b*c2, cel*fog_col.a);
			}
		}
	}

	
}
