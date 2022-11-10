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
        World world = new World((long) (100000 * Math.random()), 80, 40);
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
        while (runner.nextStepSafeForHallway() && runner.stepsTaken() < 1000) {
            runner.nextStepForHallway();
        }
    }

    /* Returns a random position on the screen! */
    private Position randomPosition() {
        /* Not yet implemented! */
        return new Position(WIDTH / 2, HEIGHT / 2);
    }

    public TETile[][] getGrid() {
        return grid;
    }

    private void setTileToWall(Position position) {
        grid[position.x()][position.y()] = Tileset.WALL;
        // System.out.println("Tile at " + position.x() + " and " + position.y() + " changed!");
    }

    private void setTileToFloor(Position position) {
        grid[position.x()][position.y()] = Tileset.FLOWER;
        // System.out.println("Tile at " + position.x() + " and " + position.y() + " changed!");
    }

    private class Runner {
        private Position position;
        private Position.Step nextStep;
        private int stepsTaken;

        public Runner(Position startPosition) {
            position = startPosition;
            nextStep = randomSteps()[0];
            stepsTaken = 0;
        }

        public int stepsTaken() {
            return stepsTaken;
        }

        public boolean nextStepSafeForHallway() {
            if (RANDOM.nextDouble() > 0.05 && StepSafeForHallway(nextStep)) {
                return true;
            }
            Position.Step[] possbileSteps = randomSteps();
            for (int stepI = 0; stepI < possbileSteps.length; stepI++) {
                nextStep = possbileSteps[stepI];
                if (StepSafeForHallway(nextStep)) {
                    return true;
                }
            }
            nextStep = null;
            return false;
        }

        private Position.Step[] randomSteps() {
            Position.Step[] steps = Position.Steps.clone();
            RandomUtils.shuffle(RANDOM, steps);
            return steps;
        }

        /* Determine whether the given step can be implemented! */
        private boolean StepSafeForHallway(Position.Step step) {
            /* Not yet implemented! */
            if (!validPosition(Position.add(position, step))) {
                return false;
            }
            return true;
        }

        public void nextStepForHallway() {
            position.add(nextStep);
            setTileToFloor(position);
            stepsTaken++;
            Position.Step[] orthogonalSteps = nextStep.orthogonalSteps();
            for (int neighborI = 0; neighborI < orthogonalSteps.length; neighborI++) {
                Position neighbor = Position.add(position, orthogonalSteps[neighborI]);

                /* Currently we may possibly create hallways that are at the edge of the screen - we should avoid this!
                As a side consequence, the following if-statement would then be unnecessary. */
                if (validPosition(neighbor)) {
                    setTileToWall(neighbor);
                }
            }
        }
    }

    private boolean validPosition(Position position) {
        if (position.x() < 0 || position.x() >= WIDTH || position.y() < 0 || position.y() >= HEIGHT) {
            return false;
        }
        return true;
    }
}
