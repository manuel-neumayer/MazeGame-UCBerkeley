package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.RandomInputSource;
import byowTools.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byowTools.TileEngine.Tileset;

import java.util.Random;

public class World {

    private int WIDTH;
    private int HEIGHT;

    private long SEED = 2873123;
    private RandomInputSource RANDOM;

    private TETile[][] grid;

    public World(long seed, int width, int height) {
        WIDTH = width;
        HEIGHT = height;
        SEED = seed;
        RANDOM = new RandomInputSource(seed);
    }

    public void setup() {
        /*write code*/
    }

    public TETile[][] getGrid() {
        return grid;
    }


}
