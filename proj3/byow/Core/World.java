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
        World world = new World((long) (568383956), 80, 40);
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
        Room firstRoom = placeRoom(WIDTH / 2, HEIGHT / 2); // or use randomPosition() ?
        runFromRoom(firstRoom);
        /*write code*/
    }

    private void createHallwayAndRoom(Position startPosition) {
        Runner runner = new Runner(startPosition);
        runner.createHallway(randomCorridorLength());
        Room newRoom = placeRoom(runner.nextPosition());
        if (newRoom == null) {
            runner.closeCorridor();
        } else {
            runFromRoom(newRoom);
        }
    }

    private void runFromRoom(Room room) {
        for (int newRunnerI = 0; newRunnerI < room.newCorridors.length; newRunnerI++) {
            Position newCorridorStartPosition = room.newCorridors[newRunnerI];
            createHallwayAndRoom(newCorridorStartPosition);
        }
    }

    private int randomCorridorLength() {
        return (int) (20 * Math.random());
    }
    public int WIDTH() {
        return WIDTH;
    }

    public int HEIGHT() {
        return HEIGHT;
    }

    /*private void createHallway(Runner runner) {
        while (runner.nextStepSafeForHallway() && runner.stepsTaken() < 1000) {
            runner.nextStepForHallway();
        }
    }*/

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

    private boolean isBackgroundTile(TETile tile) {
        return tile == Tileset.NOTHING;
    }

    private class Runner {
        private Position position;
        private Position.Step nextStep;
        private boolean justTookTurn;
        private int stepsTaken;
        private double likelyhoodOfRandomTurn = 0.05;

        public Runner(Position startPosition) {
            position = startPosition;
            nextStep = randomSteps()[0];
            justTookTurn = false;
            stepsTaken = 0;
        }

        public void createHallway(int maxLength) {
            while (nextStepSafeForHallway() && stepsTaken < maxLength) {
                nextStepForHallway();
            }
        }

        public Position nextPosition() {
            return Position.add(position, nextStep);
        }

        public void closeCorridor() {
            setTileToWall(position);
        }

        public int stepsTaken() {
            return stepsTaken;
        }

        /* Interacts intimately with nextStepForHallway !!! */
        private boolean nextStepSafeForHallway() {
            // if the current nextStep is valid, ...
            if (StepSafeForHallway(nextStep)) {
                // ... and the runner does not decide to randomly take a turn...
                if (!justTookTurn && RANDOM.nextDouble() > likelyhoodOfRandomTurn) {
                    // this function simply returns true, meaning the runner can go in the directio of nextStep.
                    return true;
                }
            }
            /* Otherwise, the runner is (definitely (*)) going to take a turn. */
            justTookTurn = true;
            Position.Step oldNextStep = nextStep;
            Position.Step[] possbileSteps = randomSteps();
            for (int stepI = 0; stepI < possbileSteps.length; stepI++) {
                nextStep = possbileSteps[stepI];
                //here we (also) make sure that the runner really changes direction now
                if (!nextStep.equals(oldNextStep) && StepSafeForHallway(nextStep)) {
                    fillThreeTilesBehind(oldNextStep);
                    return true;
                }
            }
            nextStep = null;
            return false;
        }

        private void fillThreeTilesBehind(Position.Step direction) {
            Position behind = Position.add(position, direction);
            setTileToWall(behind);
            Position.Step[] orthogonalSteps = direction.orthogonalSteps();
            for (int neighborI = 0; neighborI < orthogonalSteps.length; neighborI++) {
                Position neighbor = Position.add(behind, orthogonalSteps[neighborI]);
                /* Currently we may possibly create hallways that are at the edge of the screen - we should avoid this!
                As a side consequence, the following if-statement would then be unnecessary. */
                if (validPosition(neighbor)) {
                    setTileToWall(neighbor);
                }
            }
        }

        private Position.Step[] randomSteps() {
            Position.Step[] steps = Position.Steps.clone();
            RandomUtils.shuffle(RANDOM, steps);
            return steps;
        }

        /* Determine whether the given step can be implemented! */
        private boolean StepSafeForHallway(Position.Step step) {
            Position positionPlusTwoSteps = Position.add(position, step).add(step);
            if (!validPosition(positionPlusTwoSteps)) {
                return false;
            }
            if (!isBackgroundTile(grid[positionPlusTwoSteps.x()][positionPlusTwoSteps.y()])) {
                return false;
            }
            return true;
        }

        /* Interacts intimately with nextStepSafeForHallway !!! */
        private void nextStepForHallway() {
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
            justTookTurn = false;
        }
    }

    private boolean validPosition(Position position) {
        if (position.x() < 0 || position.x() >= WIDTH || position.y() < 0 || position.y() >= HEIGHT) {
            return false;
        }
        return true;
    }
}
