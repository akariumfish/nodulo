package util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.generator.cellular.CellularAutomataGenerator;
import com.github.czyzby.noise4j.map.generator.noise.NoiseGenerator;
import com.github.czyzby.noise4j.map.generator.room.dungeon.DungeonGenerator;
import com.github.czyzby.noise4j.map.generator.util.Generators;

public class nNoise {

	
	/*
	 * 				NOISE4J  -  map generator
	 * 		TODO
	 * */

    public static void noiseStage(final Grid grid, final NoiseGenerator noiseGenerator, 
    			final int radius,
            final float modifier) {
        noiseGenerator.setRadius(radius);
        noiseGenerator.setModifier(modifier);
        // Seed ensures randomness, can be saved if you feel the need to
        // generate the same map in the future.
        noiseGenerator.setSeed(Generators.rollSeed());
        noiseGenerator.generate(grid);
    }
    public static Texture noiseGenerator() {

        final Pixmap map = new Pixmap(512, 512, Format.RGBA8888);
        final Grid grid = new Grid(512);
		NoiseGenerator noiseGenerator = new NoiseGenerator();
		noiseStage(grid, noiseGenerator, 32, 0.6f);
        noiseStage(grid, noiseGenerator, 16, 0.2f);
        noiseStage(grid, noiseGenerator, 8, 0.1f);
        noiseStage(grid, noiseGenerator, 4, 0.1f);
        noiseStage(grid, noiseGenerator, 1, 0.05f);

        final Color color = new Color();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                final float cell = grid.get(x, y);
                color.set(cell, cell, cell, 1f);
                map.drawPixel(x, y, Color.rgba8888(color));
            }
        }

        Texture texture = new Texture(map);
//        batch.begin();
//        batch.draw(texture, 0f, 0f);
//        batch.end();
//        texture.dispose();
        return texture;
    }
    
    public static Texture cellularGenerator() {

        final Pixmap map = new Pixmap(512, 512, Format.RGBA8888);
        final Grid grid = new Grid(512);

        final CellularAutomataGenerator cellularGenerator = new CellularAutomataGenerator();
        cellularGenerator.setAliveChance(0.5f);
        cellularGenerator.setIterationsAmount(4);
        cellularGenerator.generate(grid);
        
        //bigger isle
//        final CellularAutomataGenerator cellularGenerator = new CellularAutomataGenerator();
//        cellularGenerator.setAliveChance(0.5f);
//        cellularGenerator.setRadius(2);
//        cellularGenerator.setBirthLimit(13);
//        cellularGenerator.setDeathLimit(9);
//        cellularGenerator.setIterationsAmount(6);
//        cellularGenerator.generate(grid);

        final Color color = new Color();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                final float cell = grid.get(x, y);
                color.set(cell, cell, cell, 1f);
                map.drawPixel(x, y, Color.rgba8888(color));
            }
        }

        Texture texture = new Texture(map);
//        batch.begin();
//        batch.draw(texture, 0f, 0f);
//        batch.end();
//        texture.dispose();
        return texture;
    }

    public static Texture dungeonGenerator() {
    	final Pixmap map = new Pixmap(512, 512, Format.RGBA8888);
        final Grid grid = new Grid(512); // This algorithm likes odd-sized maps, although it works either way.

        final DungeonGenerator dungeonGenerator = new DungeonGenerator();
        dungeonGenerator.setRoomGenerationAttempts(500);
        dungeonGenerator.setMaxRoomSize(75);
        dungeonGenerator.setTolerance(10); // Max difference between width and height.
        dungeonGenerator.setMinRoomSize(9);
        dungeonGenerator.generate(grid);

//        final DungeonGenerator dungeonGenerator = new DungeonGenerator();
//        dungeonGenerator.setRoomGenerationAttempts(200);
//        dungeonGenerator.setMaxRoomSize(25);
//        dungeonGenerator.setTolerance(6);
//        dungeonGenerator.setMinRoomSize(9);
//        dungeonGenerator.setWindingChance(0.5f); // More chaotic!
//        dungeonGenerator.setDeadEndRemovalIterations(5); // Introducing dead ends.
//        dungeonGenerator.setRandomConnectorChance(0f); // One way to solve the maze.
//        dungeonGenerator.generate(grid);
        
        final Color color = new Color();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                final float cell = 1f - grid.get(x, y);
                color.set(cell, cell, cell, 1f);
                map.drawPixel(x, y, Color.rgba8888(color));
            }
        }

        Texture texture = new Texture(map);
//        batch.begin();
//        batch.draw(texture, 0f, 0f);
//        batch.end();
//        texture.dispose();
        return texture;
    }
    
    
}
