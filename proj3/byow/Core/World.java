package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.RandomInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class World {

    private int WIDTH;
    private int HEIGHT;

    private long SEED;
    private Random RANDOM;

    private TETile[][] grid;

    private TERenderer ter;

    private final int minL = 3;//5; //3;
    private final int randomComponentL = 10;//5; //3;
    private final int minW = 5;//10; //5;
    private final int randomComponentW = 20;

    public World(long seed, int width, int height) {
        WIDTH = width;
        HEIGHT = height;
        SEED = seed;
        RANDOM = new Random(seed);
    }

    public static void main(String[] args) {
        long seed = (long) (100000 * Math.random());
        //long seed = 58822;//70175;// 56315; 64198
        System.out.println("Seed: " + seed);
        World world = new World(seed, 80, 40);
        world.setup();
    }

    public boolean roomFits(LinkedList<LinkedList<Position>> room){
        for (int i = 0; i < room.size(); i++) {
            LinkedList<Position> y = room.get(i);
            for (int j = 0; j < y.size(); j++) {
                Position pos = y.get(j);
                if (pos.x() < 0 || pos.x() >= WIDTH || pos.y() < 0 || pos.y() >= HEIGHT) {
                    return false;
                }
                if (grid[pos.x()][pos.y()] != Tileset.NOTHING) {
                    return false;
                }
            }
        }
        return true;
    }

    private Room placeRoom(Position position, Position.Step direction) {
        for (int i = 0; i < 100; i++) {
            int l1 = minL + (int) (randomComponentL * RANDOM.nextDouble());
            int l2 = minL + (int) (randomComponentL * RANDOM.nextDouble());
            int w = minW + (int) (randomComponentW * RANDOM.nextDouble());
            LinkedList<LinkedList<Position>> positions = getPotentialPositions(position, direction, l1, l2, w);
            if (roomFits(positions)) {
                return new Room(positions, RANDOM, this);
            }
        }
        LinkedList<LinkedList<Position>> positions = getPotentialPositions(position, direction, 1, 1, 3);
        if (roomFits(positions)) {
            return new Room(positions, RANDOM, this);
        }
        return null;
    }

    private LinkedList<LinkedList<Position>> getPotentialPositions(Position position, Position.Step direction, int l1, int l2, int w) {
        LinkedList<LinkedList<Position>> positions = new LinkedList<>();
        positions.add(rowOfPositions(position, direction, w));
        Position.Step[] orthogonalDirections = direction.orthogonalSteps();
        Position currentPosition = position.copy();
        for (int j = 0; j < l1; j++) {
            currentPosition.add(orthogonalDirections[0]);
            positions.addFirst(rowOfPositions(currentPosition, direction, w));
        }
        currentPosition = position.copy();
        for (int j = 0; j < l2; j++) {
            currentPosition.add(orthogonalDirections[1]);
            positions.add(rowOfPositions(currentPosition, direction, w));
        }
        //System.out.println("l1: " + l1 + ", l2:" + l2 + ", w: " + w + ", dim:" + positions.size() + ", " + positions.get(0).size());
        return positions;
    }

    private LinkedList<Position> rowOfPositions(Position position, Position.Step direction, int w) {
        LinkedList<Position> newRow = new LinkedList<>();
        Position currentPosition = position.copy();
        for (int i = 0; i < w; i++) {
            newRow.add(currentPosition.copy());
            currentPosition.add(direction);
        }
        return newRow;
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
        ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        initializeGrid();
        Room firstRoom = placeRoom(new Position((int) (WIDTH / 2), (int) (HEIGHT / 2)), Position.Up) ; // or use randomPosition() ?
        runFromRoom(firstRoom);
        ter.renderFrame(getGrid());
        /*for (int i = 0; i < 10; i++) {
            Runner runner = new Runner(randomPosition());
            runner.createHallway(1000);
        }*/
    }

    private void createHallwayAndRoom(Position startPosition, int iteration) {
        Runner runner = new Runner(startPosition);
        runner.createHallway(randomCorridorLength());
        if (runner.deadend() == true || runner.merged() == true) {
            return;
        }
        Room newRoom = placeRoom(runner.nextPosition(), runner.direction()); // make sure that runner.dirction() is not null if !(runner.deadend() == true || runner.merged() == true)
        ter.renderFrame(getGrid());
        if (newRoom == null) {
            if (iteration < 0) {
                createHallwayAndRoom(runner.position(), iteration + 1); // will just keep running!!
            } else {
                runner.closeCorridor(runner.position(), runner.direction());
            }
        } else {
            setTileToFloor(runner.nextPosition());
            runFromRoom(newRoom);
        }
    }

    private void runFromRoom(Room room) {
        System.out.println("Sending " + room.newCorridors.size() + " runners to run!");
        for (int newRunnerI = 0; newRunnerI < room.newCorridors.size(); newRunnerI++) {
            Position newCorridorStartPosition = room.newCorridors.get(newRunnerI);
            createHallwayAndRoom(newCorridorStartPosition, 0);
        }
    }

    private int randomCorridorLength() {
        return 4 + (int) (10 * RANDOM.nextDouble());
    }
    public int WIDTH() {
        return WIDTH;
    }

    public int HEIGHT() {
        return HEIGHT;
    }

    /* Returns a random position on the screen! */
    private Position randomPosition() {
        /* Not yet implemented! */
        Position pos = new Position(WIDTH / 4 + (int) (RANDOM.nextDouble() * WIDTH / 2), HEIGHT / 4 + (int) (RANDOM.nextDouble() * HEIGHT / 2));
        while (!isBackground(pos)) {
            pos = new Position(WIDTH / 4 + (int) (RANDOM.nextDouble() * WIDTH / 2), HEIGHT / 4 + (int) (RANDOM.nextDouble() * HEIGHT / 2));
        }
        return pos;
    }

    public TETile[][] getGrid() {
        return grid;
    }

    /* The runner should never start off near the edge of the screen! */
    private class Runner {
        private Position position;
        private Position.Step nextStep;
        private boolean deadend;
        private boolean merged;
        private int stepsTaken;
        private double likelyhoodOfRandomTurn = 0.25; // could make this increase with stepsTaken!!

        public Runner(Position startPosition) {
            position = startPosition;
            nextStep = null;
        }

        public boolean deadend() {
            return deadend;
        }

        public boolean merged() {
            return merged;
        }

        public void createHallway(int maxLength) {
            stepsTaken = 0;
            deadend = false;
            merged = false;
            Position.Step currentDirection = findOutwardDirection(); // Should never be null!!!
            if (!validStep(position, currentDirection)) {
                deadend = true;
                setTileToWall(position);
                return;
                //closeCorridor(position, currentDirection);
            }
            while (stepsTaken < maxLength) {
                ter.renderFrame(getGrid());
                StdDraw.pause(10);
                // nextStep shall never be null if keepRunning is true!
                if (isMerge(position, currentDirection)) {
                    merge(currentDirection);
                    merged = true;
                    return;
                }
                boolean canGoStraight = validStep(position, currentDirection);
                Position.Step potentialTurn = canTakeTurn(position, currentDirection);
                if (!canGoStraight && potentialTurn == null) {
                    deadend = true;
                    closeCorridor(position, currentDirection);
                    return;
                }
                if (potentialTurn == null) {
                    // we know that this is not a merge!
                    takeStep(currentDirection);
                } else if (!canGoStraight) {
                    if (isMerge(position, potentialTurn)) {
                        takeTurn(currentDirection, potentialTurn);
                        merge(potentialTurn);
                        merged = true;
                        return;
                    } else {
                        takeTurn(currentDirection, potentialTurn);
                        currentDirection = potentialTurn;
                    }
                } else {
                    if (RANDOM.nextDouble() < likelyhoodOfRandomTurn) {
                        if (isMerge(position, potentialTurn)) {
                            takeTurn(currentDirection, potentialTurn);
                            merge(potentialTurn);
                            merged = true;
                            return;
                        } else {
                            takeTurn(currentDirection, potentialTurn);
                            currentDirection = potentialTurn;
                        }
                    } else {
                        takeStep(currentDirection);
                    }
                }
                stepsTaken++;
            }
            drawOrthogonalWalls(position, currentDirection);
            nextStep = currentDirection;
        }

        public Position position() {
            return position;
        }

        private void takeTurn(Position.Step oldDirection, Position.Step newDirection) {
            setTileToWall(Position.sub(position, newDirection));
            setTileToWall(Position.sub(position, newDirection).add(oldDirection));
            setTileToWall(Position.add(position, oldDirection));
            position.add(newDirection);
            setTileToFloor(position);
        }

        private void takeStep(Position.Step currentDirection) {
            setTileToFloor(position);
            Position.Step[] orthogonalSteps = currentDirection.orthogonalSteps();
            drawOrthogonalWalls(position, currentDirection);
            position.add(currentDirection);
            setTileToFloor(position);
        }

        private void merge(Position.Step step) {
            drawOrthogonalWalls(position, step);
            position.add(step);
            setTileToFloor(position);
        }

        private void drawOrthogonalWalls(Position pos, Position.Step step) {
            Position.Step[] orthogonalSteps = step.orthogonalSteps();
            for (int i = 0; i < orthogonalSteps.length; i++) {
                setTileToWall(Position.add(pos, orthogonalSteps[i]));
            }
        }

        Position.Step canTakeTurn(Position pos, Position.Step oldDirection) {
            Position.Step[] directions = randomSteps();
            for (int i = 0; i < directions.length; i++) {
                if (!directions[i].equals(oldDirection)) {
                    if (validStep(pos, directions[i])) {
                        return directions[i];
                    }
                }
            }
            return null;
        }

        private Position.Step findMerge() {
            Position.Step[] directions = Position.Steps;
            for (int i = 0; i < directions.length; i++) {
                Position[] nextThreePositions = nextThreePositions(position, directions[i]);
                if (validPosition(nextThreePositions[0]) && validPosition(nextThreePositions[1]) && validPosition(nextThreePositions[2])
                        && isBackground(nextThreePositions[0]) && isWall(nextThreePositions[1]) && isFloor(nextThreePositions[2])) {
                    return directions[i];
                }
            }
            return null;
        }

        private Position.Step findOutwardDirection() {
            Position.Step[] directions = Position.Steps;
            for (int i = 0; i < directions.length; i++) {
                if (isInwardDirection(directions[i])) {
                    return directions[i].inverse();
                }
            }
            return null;
        }

        private boolean isInwardDirection(Position.Step step) {
            Position[] nextThreePositions = nextThreePositions(position, step);
            if (isFloor(nextThreePositions[0])) {
                return true;
            }
            return false;
        }

        // Draw walls at current position, take step, and draw floor tile!
        private void takeStepAndDraw() {
            setTileToFloor(position);
            Position.Step[] orthogonalSteps = nextStep.orthogonalSteps();
            for (int neighborI = 0; neighborI < orthogonalSteps.length; neighborI++) {
                Position neighbor = Position.add(position, orthogonalSteps[neighborI]);
                // Currently we may possibly create hallways that are at the edge of the screen - we should avoid this!
                // As a side consequence, the following if-statement would then be unnecessary.
                if (validPosition(neighbor)) {
                    setTileToWall(neighbor);
                }
            }
            position.add(nextStep);
            setTileToFloor(position);
        }

        private void takeTurnAndDraw(Position.Step newDirection) {
            Position.Step oldStep = nextStep;
            nextStep = newDirection;
            setTileToFloor(position);
            setTileToWall(Position.sub(position, nextStep));
            setTileToWall(Position.sub(position, nextStep).add(oldStep));
            setTileToWall(Position.add(position, oldStep));
            position.add(nextStep);
            setTileToFloor(position);
            setTileToWall(Position.add(position, oldStep));
        }

        private boolean validStep(Position pos, Position.Step step) {
            Position[] nextThreePositions = nextThreePositions(pos, step);
            //Position[] leftThreePositions = nextThreePositions(Position.add(pos, step.orthogonalSteps()[0]), step);
            //Position[] rightThreePositions = nextThreePositions(Position.add(pos, step.orthogonalSteps()[1]), step);
            if (!(validPosition(nextThreePositions[0]) && validPosition(nextThreePositions[1]))) {
                return false;
            }
            // Don't go in a direction that is already a corridor / room !
            if (isFloor(nextThreePositions[0])) {
                return false;
            }
            // If there is nothing ahead, just go !
            if (isBackground(nextThreePositions[0])) {
                return true;
            }
            return isMerge(pos, step);
        }

        boolean isMerge(Position pos, Position.Step step) {
            Position[] nextThreePositions = nextThreePositions(pos, step);
            // Can we merge?
            if (isWall(nextThreePositions[0]) && isFloor(nextThreePositions[1])) {
                //&& isWall(leftThreePositions[0]) && isFloor(leftThreePositions[1])
                //        && isWall(rightThreePositions[0]) && isFloor(rightThreePositions[1])
                return true;
            }
            return false;
        }

        private Position[] nextThreePositions(Position pos, Position.Step step) {
            Position nextPos = Position.add(pos, step);
            Position nextnextPos = Position.add(nextPos, step);
            Position nextnextnextPos = Position.add(nextnextPos, step);
            return new Position[]{nextPos, nextnextPos, nextnextnextPos};
        }

        private void closeCorridor(Position pos, Position.Step step) {
            drawOrthogonalWalls(pos, step);
            Position endPosition = Position.add(position, step);
            drawOrthogonalWalls(endPosition, step);
            setTileToWall(endPosition);
        }

        private Position.Step[] randomSteps() {
            Position.Step[] steps = Position.Steps.clone();
            RandomUtils.shuffle(RANDOM, steps);
            return steps;
        }

        public Position nextPosition() {
            return Position.add(position, nextStep);
        }

        public Position.Step direction() {
            return nextStep;
        }

        /*// Find valid step that is not equal to step !
        private Position.Step findValidStep(Position.Step step) {
            Position.Step[] possibleSteps = randomSteps();
            for (int i = 0; i < possibleSteps.length; i++) {
                if (validStep(possibleSteps[i]) && !possibleSteps[i].equals(step)) {
                    return possibleSteps[i];
                }
            }
            return null;
        }*/

        /*

        // Assumes that nextStep != null
        private void determineNextStep() {
            boolean nextStepValid = validStep(nextStep);
            Position.Step potentialTurn = findValidStep(nextStep);
            if (potentialTurn == null && !nextStepValid) {
                nextStep = null;
                return;
            }
            Position.Step oldStep = nextStep;
            if (!nextStepValid) {
                nextStep = potentialTurn;
                takeTurn(oldStep);
                return;
            }
            if (RANDOM.nextDouble() < likelyhoodOfRandomTurn) {
                nextStep = potentialTurn;
                takeTurn(oldStep);
                return;
            } else {
                return;
            }
        }

        // Interacts intimately with nextStepForHallway !!!
        private boolean nextStepSafeForHallway() {
            if (nextStep == null) {
                return false;
            }
            // Check if you can merge the corridor with another corridor / room.
            Position posInFront = Position.add(position, nextStep);
            Position posTwoInFront = Position.add(posInFront, nextStep);
            if (validPosition(posInFront) && validPosition(posTwoInFront) && isWallTile(grid[posInFront.x()][posInFront.y()]) &&orTile(grid[posTwoInFront.x()][posTwoInFront.y()])) {
                setTileToFloor(position);
                setTileToFloor(posInFront);
                return false;
            }

            // Otherwise, if the current nextStep is valid, ...
            if (StepSafeForHallway(nextStep)) {
                // ... and the runner does not decide to randomly take a turn...
                if (!justTookTurn && RANDOM.nextDouble() > likelyhoodOfRandomTurn) {
                    // this function simply returns true, meaning the runner can go in the directio of nextStep.
                    return true;
                }
            }
            // Otherwise, the runner is (definitely (*)) going to take a turn.
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
                // Currently we may possibly create hallways that are at the edge of the screen - we should avoid this!
                // As a side consequence, the following if-statement would then be unnecessary.
                if (validPosition(neighbor)) {
                    setTileToWall(neighbor);
                }
            }
        }

        // Determine whether the given step can be implemented!
        private boolean StepSafeForHallway(Position.Step step) {
            if (step == null) {
                return false;
            }
            Position positionPlusTwoSteps = Position.add(position, step).add(step);
            if (!validPosition(positionPlusTwoSteps)) {
                return false;
            }
            if (!isBackgroundTile(grid[positionPlusTwoSteps.x()][positionPlusTwoSteps.y()])) {
                return false;
            }
            return true;
        }

        // Interacts intimately with nextStepSafeForHallway !!!
        private void nextStepForHallway() {
            position.add(nextStep);
            setTileToFloor(position);
            stepsTaken++;
            Position.Step[] orthogonalSteps = nextStep.orthogonalSteps();
            for (int neighborI = 0; neighborI < orthogonalSteps.length; neighborI++) {
                Position neighbor = Position.add(position, orthogonalSteps[neighborI]);

                // Currently we may possibly create hallways that are at the edge of the screen - we should avoid this!
                As a side consequence, the following if-statement would then be unnecessary.
                if (validPosition(neighbor)) {
                    setTileToWall(neighbor);
                }
            }
            justTookTurn = false;
        }
         */
    }

    public void setTileToWall(Position position) {
        if (!validPosition(position)) {
            return;
        }
        grid[position.x()][position.y()] = Tileset.WALL;
    }

    public void setTileToFloor(Position position) {
        if (!validPosition(position)) {
            return;
        }
        grid[position.x()][position.y()] = Tileset.FLOWER;
    }

    public void setTileToBackground(Position position) {
        if (!validPosition(position)) {
            return;
        }
        grid[position.x()][position.y()] = Tileset.NOTHING;
    }

    private boolean isBackgroundTile(TETile tile) {
        return tile == Tileset.NOTHING;
    }

    private boolean isFloorTile(TETile tile) {
        return tile == Tileset.FLOWER;
    }

    private boolean isWallTile(TETile tile) {
        return tile == Tileset.WALL;
    }

    private boolean isBackground(Position position) {
        if (!validPosition(position)) {
            return false;
        }
        return grid[position.x()][position.y()] == Tileset.NOTHING;
    }

    private boolean isFloor(Position position) {
        if (!validPosition(position)) {
            return false;
        }
        return grid[position.x()][position.y()] ==Tileset.FLOWER;
    }

    private boolean isWall(Position position) {
        if (!validPosition(position)) {
            return false;
        }
        return grid[position.x()][position.y()] == Tileset.WALL;
    }

    private boolean validPosition(Position position) {
        if (position.x() < 0 || position.x() >= WIDTH || position.y() < 0 || position.y() >= HEIGHT) {
            return false;
        }
        return true;
    }
}