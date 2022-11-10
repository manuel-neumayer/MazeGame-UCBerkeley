package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.RandomInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

public class World {

    private int WIDTH;
    private int HEIGHT;

    private long SEED = 2873123;
    private Random RANDOM;

    private TETile[][] grid;

    public World(long seed, int width, int height) {
        WIDTH = width;
        HEIGHT = height;
        SEED = seed;
        RANDOM = new Random(seed);
    }

    public static void main(String[] args) {
        World world = new World(10404, 200, 200);
        TERenderer ter = new TERenderer();
        ter.initialize(world.WIDTH(), world.HEIGHT());
        world.setup();
        ter.renderFrame(world.getGrid());
    }

    private void initializeGrid() {
        grid = new TETile[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                grid[i][j] = Tileset.NOTHING;
            }
        }
    }

    public void setup() {
        initializeGrid();
        Runner runner = new Runner(randomPosition());
        createHallway(runner);

        /*write code*/
    }

    public int WIDTH() {
        return WIDTH;
    }

    public int HEIGHT() {
        return HEIGHT;
    }

    private void createHallway(Runner runner) {
        while (runner.nextStepSafeForHallway() && RANDOM.nextDouble() > 0.1) {
            runner.nextStepForHallway();
        }
    }

    /* Returns a random position on the screen! */
    private Position randomPosition() {
        /* Not yet implemented! */
        return new Position(0, 0);
    }

    public TETile[][] getGrid() {
        return grid;
    }

    private void setTileToWall(Position position) {
        grid[position.x][position.y] = Tileset.WALL;
    }

    private class Runner {
        private Position position;
        private Position.Step nextStep;

        public Runner(Position startPosition) {
            position = startPosition;
            nextStep = Position.randomSteps()[0];
        }

        public boolean nextStepSafeForHallway() {
            if (RANDOM.nextDouble() > 0.1 && StepSafeForHallway(nextStep)) {
                return true;
            }
            Position.Step[] possbileSteps = Position.randomSteps();
            for (int stepI = 0; stepI < possbileSteps.length; stepI++) {
                nextStep = possbileSteps[stepI];
                if (StepSafeForHallway(nextStep)) {
                    return true;
                }
            }
            nextStep = null;
            return false;
        }

        /* Determine whether the given step can be implemented! */
        private boolean StepSafeForHallway(Position.Step step) {
            /* Not yet implemented! */
            return true;
        }

        public void nextStepForHallway() {
            position.add(nextStep);
            for (int neighborI = 0; neighborI < nextStep.orthogonalSteps.length; neighborI++) {
                Position neighbor = Position.add(position, nextStep.orthogonalSteps[neighborI]);
                setTileToWall(neighbor);
            }
        }
    }
}
