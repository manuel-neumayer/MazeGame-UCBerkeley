package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.util.LinkedList;
import java.util.Random;

public class World {

    private int WIDTH;
    private int HEIGHT;

    private double randomTurnProbability = 0.35;
    private TETile[][] grid;

    private TERenderer ter;

    private final int minL = 3;//1;//5; //3;
    private final int randomComponentL = 5;//10; //3;
    private final int minW = 5;//3;//10;//5;
    private final int randomComponentW = 10; //20

    private final int corridorLengthMin = 4;

    private final int corridorLengthRandomComponent = 6;

    private int pauseTime;

    /* Potential improvement: Have a list of possible starting positions for runners which we constantly add to (as runners run and rooms are placed),
    and let runners start from this list once the first build is done. */

    /* Also, count number of rooms, and require that at least a given number of rooms is created (15?) */

    /* Another potential metric, besides counting created rooms, is using a heuristic to guess how many rooms could still be placed after the algorithm finished.
    * I.e., place 100 rooms of reasonable size randomly and see how many could actually be placed. The larger the number, the more work is to be done. */
    public World(int width, int height) {
        WIDTH = width;
        HEIGHT = height;
    }

    public World() {
        WIDTH = 90;
        HEIGHT = 45;
    }

    public static void main(String[] args) {
        RandomWrapper.setup();
        World world = new World(90, 45);
        world.setup();
    }

    public void setup() {
        pauseTime = 0;
        ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        boolean notGoodEnough = true;
        while (notGoodEnough) {
            initializeGrid();
            Room firstRoom = placeRoom(new Position((int) (WIDTH / 2), (int) (HEIGHT / 2)), Position.Up);
            runFromRoom(firstRoom);
            notGoodEnough = false;
            for (int i = 0; i < 5; i++) {
                int heuristic = heuristicForEmptinessOfGrid();
                System.out.print(heuristic + ", ");
                if (heuristic >= 20) {
                    notGoodEnough = true;
                }
            }
            System.out.println("");
            //StdDraw.pause(1000);
        }
        ter.renderFrame(getGrid());
    }

    private boolean roomFits(LinkedList<LinkedList<Position>> room){
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
        LinkedList<LinkedList<Position>> positions;
        for (int i = 0; i < 100; i++) {
            positions = roomOfRandomDimensions(position, direction);
            if (roomFits(positions)) {
                return new Room(positions, this);
            }
        }
        positions = getPotentialPositions(position, direction, 1, 1, 3);
        if (roomFits(positions)) {
            return new Room(positions, this);
        }
        return null;
    }

    private LinkedList<LinkedList<Position>> roomOfRandomDimensions(Position position, Position.Step direction) {
        int l1 = minL + (int) (randomComponentL * RandomWrapper.nextDouble());
        int l2 = minL + (int) (randomComponentL * RandomWrapper.nextDouble());
        int w = minW + (int) (randomComponentW * RandomWrapper.nextDouble());
        return getPotentialPositions(position, direction, l1, l2, w);
    }

    private int heuristicForEmptinessOfGrid() {
        int potentialRooms = 0;
        for (int i = 0; i < 1000; i++) {
            Position position = randomPosition();
            Position.Step direction = randomStep();
            LinkedList<LinkedList<Position>> positions = roomOfRandomDimensions(position, direction);
            if (roomFits(positions)) {
                potentialRooms++;
            }
        }
        return potentialRooms;
    }

    private Position.Step randomStep() {
        return Position.Steps[(int) (Position.Steps.length * RandomWrapper.nextDouble())];
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

    private void createHallwayAndRoom(Position startPosition, int iteration) {
        Runner runner = new Runner(startPosition);
        runner.createHallway(randomCorridorLength());
        if (runner.deadend() == true || runner.merged() == true) {
            return;
        }
        Room newRoom = placeRoom(runner.nextPosition(), runner.direction()); // make sure that runner.direction() is not null if !(runner.deadend() == true || runner.merged() == true)
        if (newRoom == null) {
            if (iteration < 5) {
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
        for (int newRunnerI = 0; newRunnerI < room.newCorridors.size(); newRunnerI++) {
            Position newCorridorStartPosition = room.newCorridors.get(newRunnerI);
            createHallwayAndRoom(newCorridorStartPosition, 0);
        }
    }

    private int randomCorridorLength() {
        return corridorLengthMin + (int) (corridorLengthRandomComponent * RandomWrapper.nextDouble());
    }
    public int WIDTH() {
        return WIDTH;
    }

    public int HEIGHT() {
        return HEIGHT;
    }

    /* Returns a random position on the screen! */
    private Position randomPosition() {
        Position pos = new Position((int) (RandomWrapper.nextDouble() * WIDTH), (int) (RandomWrapper.nextDouble() * HEIGHT));
        while (!isBackground(pos)) {
            pos = new Position((int) (RandomWrapper.nextDouble() * WIDTH), (int) (RandomWrapper.nextDouble() * HEIGHT));
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
        private double likelyhoodOfRandomTurn = randomTurnProbability; // could make this increase with stepsTaken!!

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
                if (pauseTime != 0) {
                    ter.renderFrame(getGrid());
                    StdDraw.pause(pauseTime);
                }
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
                    if (RandomWrapper.nextDouble() < likelyhoodOfRandomTurn) {
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
            drawOrthogonalWalls(position, step); // this method call ...
            setTileToFloor(position);
            drawOrthogonalWalls(Position.add(position, step), step); // and this method call are to ensure that even in very weird merges (for example,
            // see what happens if you remove these method calls and run with seed 83896) no floor is exposed to nothingness. Since
            // drawOrthogonalWalls(position, step) sets tiles to wall only if they are not already floor, nothing can really go wrong (I hope).
        }

        private void drawOrthogonalWalls(Position pos, Position.Step step) {
            Position.Step[] orthogonalSteps = step.orthogonalSteps();
            for (int i = 0; i < orthogonalSteps.length; i++) {
                Position newWallPosition = Position.add(pos, orthogonalSteps[i]);
                if (!isFloor(newWallPosition)) {
                    setTileToWall(newWallPosition);
                }
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
                //if (validStep(position, directions[i])) {
                //    return directions[i];
                //}
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

        public Position nextPosition() {
            return Position.add(position, nextStep);
        }

        public Position.Step direction() {
            return nextStep;
        }
    }

    private Position.Step[] randomSteps() {
        Position.Step[] steps = Position.Steps.clone();
        RandomWrapper.shuffle(steps);
        return steps;
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

    public static boolean isBackgroundTile(TETile tile) {
        return tile == Tileset.NOTHING;
    }

    public static boolean isFloorTile(TETile tile) {
        return tile == Tileset.FLOWER;
    }

    public static boolean isWallTile(TETile tile) {
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