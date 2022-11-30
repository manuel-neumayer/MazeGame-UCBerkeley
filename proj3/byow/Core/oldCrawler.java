package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class oldCrawler {

    private TETile[][] grid;
    private int WIDTH;
    private int HEIGHT;
    HashMap<Position, InteriorElement> positionToElement;

    private TERenderer ter;

    public static void main(String[] args) {
        /*HashSet<Position> set = new HashSet<>();
        LinkedList<Position> list = new LinkedList<>();
        Position pos = new Position(1,1);
        set.add(pos);
        list.add(pos);
        System.out.println(Objects.equals(pos, new Position(1,1)));
        System.out.println(pos.equals(new Position(1,1)));
        System.out.println(set.contains(new Position(1,1)));
        System.out.println(list.contains(new Position(1,1)));*/
        World world = new World();
        world.setup();
        TETile[][] grid = world.getGrid();
        Crawler crawler = new Crawler(world.getGrid());
    }

    public oldCrawler(TETile[][] grid) {
        this.grid = grid;
        WIDTH = grid.length;
        HEIGHT = grid[0].length;
        positionToElement = new HashMap<>();

        ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(grid);
        StdDraw.pause(1000);

        crawl();
    }

    public void crawl() {
        Position startPosition = randomPositionInInterior();
        spawnCrawler(startPosition);
    }

    private void spawnCrawler(Position startPosition) {
        // if a hallway ends in a deadend, it will call spanwCralwer with starttPosition=null. In this case, we do nothing.
        if (startPosition == null) {
            return;
        }
        // if a hallway ends in a room that has already be taken care of, we do nothing.
        if (positionToElement.containsKey(startPosition)) {
            return;
        }
        if (isHallway(startPosition)) {
            HallwayCrawler crawler = new HallwayCrawler(startPosition);
        } else if (isRoom(startPosition)) {
            RoomCrawler crawler = new RoomCrawler(startPosition);
        }
        // it should never be the case that a tile is neither a room nor a corridor!
        System.out.println("error at spawnCrawler! this position is neither a hallway nor a room.");
        grid[startPosition.x()][startPosition.y()] = Tileset.SAND;
        ter.renderFrame(grid);
    }

    private interface InteriorElement {
        public boolean containsPosition(Position position);
    }

    private class InteriorRoom implements InteriorElement {
        private LinkedList<Position> positions;
        private LinkedList<LinkedList<Position>> positionGrid;
        private LinkedList<Position> corridors;
        private int width;
        private int height;

        public InteriorRoom() {
            positions = new LinkedList<>();
            positionGrid = new LinkedList<>();
            corridors = new LinkedList<>();
        }

        public boolean containsPosition(Position position) {
            return positions.contains(position);
        }

        /* Make sure that what is added here will correspond to addInterior */
        public void addPosition(Position position) {
            positions.add(position);
            positionToElement.put(position, this);
        }

        /* Make sure that the interior corresponds to what was added through addPosition */
        public void addInteriorAndFindCorridors(LinkedList<LinkedList<Position>> positionGrid) {
            this.positionGrid = positionGrid;
            width = positionGrid.size();
            height = positionGrid.get(0).size();
            for (int j = 0; j < height; j++) {
                Position leftMostPosition = positionGrid.getFirst().get(j);
                Position rightMostPosition = positionGrid.getLast().get(j);
                Position leftWall = Position.add(leftMostPosition, RoomCrawler.horizontalDirection.inverse());
                Position rightWall = Position.add(rightMostPosition, RoomCrawler.horizontalDirection);
                if (isRoom(leftWall) && !isRoomInterior(leftWall)) {
                    addCorridor(leftWall);
                    visualize(leftWall);
                }
                if (isRoom(rightWall) && !isRoomInterior(rightWall)) {
                    addCorridor(rightWall);
                    visualize(rightWall);
                }
            }
            for (int i = 0; i < width; i++) {
                Position upMostPosition = positionGrid.get(i).get(0);
                Position downMostPosition = positionGrid.get(i).get(height - 1);
                Position upWall = Position.add(upMostPosition, RoomCrawler.verticalDirection.inverse());
                Position downWall = Position.add(downMostPosition, RoomCrawler.verticalDirection);
                if (isRoom(upWall) && !isRoomInterior(upWall)) {
                    addCorridor(upWall);
                    visualize(upWall);
                }
                if (isRoom(downWall) && !isRoomInterior(downWall)) {
                    addCorridor(downWall);
                    visualize(downWall);
                }
            }
        }

        public void addCorridor(Position position) {
            if (!positions.contains(position)) {
                positions.add(position);
                positionToElement.put(position, this);
                corridors.add(position);
            }
        }

        public Iterator<Position> getCorridorIterator() {
            return corridors.iterator();
        }
    }

    private class InteriorHallway implements InteriorElement {
        private LinkedList<Position> positions;
        private Position corridorBeginning;
        private Position corridorEnd;

        public InteriorHallway() {
            positions = new LinkedList<>();
        }

        public boolean containsPosition(Position position) {
            return positions.contains(position);
        }

        public void addPosition(Position position) {
            visualize(position);
            positions.add(position);
            positionToElement.put(position, this);
        }

        public void reversePositionsList() {
            LinkedList<Position> newPositions = new LinkedList<>();
            int size = positions.size();
            for (int i = 0; i < size; i++) {
                newPositions.addFirst(positions.remove());
            }
            positions = newPositions;
        }
    }

    private class RoomCrawler {
        private InteriorRoom currentInteriorElement;
        private static final Position.Step verticalDirection = Position.Steps[0];
        private static final Position.Step horizontalDirection = verticalDirection.orthogonalSteps()[0];

        public RoomCrawler(Position startPosition) {
            currentInteriorElement = new InteriorRoom();
            crawl(enterRoomInterior(startPosition));
            Iterator<Position> corridorIterator = currentInteriorElement.getCorridorIterator();
            while (corridorIterator.hasNext()) {
                spawnCrawler(enterHallway(corridorIterator.next()));
            }
        }

        private Position enterHallway(Position startPosition) {
            if (isHallway(startPosition) && !positionToElement.containsKey(startPosition)) {
                return startPosition;
            }
            Position.Step[] directions = Position.Steps;
            for (int i = 0; i < 4; i++) {
                Position newPosition = Position.add(startPosition, directions[i]);
                if (isHallway(newPosition) && !positionToElement.containsKey(newPosition)) {
                    return newPosition;
                }
            }
            return null;
        }

        private Position enterRoomInterior(Position startPosition) {
            if (isRoomInterior(startPosition)) {
                return startPosition;
            }
            Position.Step[] directions = Position.Steps;
            for (int i = 0; i < 4; i++) {
                Position newPosition = Position.add(startPosition, directions[i]);
                if (isRoomInterior(newPosition)) {
                    return newPosition;
                }
            }
            return null;
        }

        /* Crawl through room! the only assumption is that startPosition is in a roominterior. */
        private void crawl(Position startPosition) {
            visualize(startPosition);
            LinkedList<LinkedList<Position>> positionGrid = new LinkedList<>();
            Position currentPosition = startPosition.copy();
            while (isRoomInterior(currentPosition)) {
                positionGrid.addLast(crawlVerticalRow(currentPosition));
                currentPosition.add(horizontalDirection);
                visualize(currentPosition);
            }
            currentPosition = Position.add(startPosition, horizontalDirection.inverse());
            while (isRoomInterior(currentPosition)) {
                positionGrid.addFirst(crawlVerticalRow(currentPosition));
                currentPosition.add(horizontalDirection.inverse());
                visualize(currentPosition);
            }
            visualize(positionGrid);
            currentInteriorElement.addInteriorAndFindCorridors(positionGrid);
        }

        private LinkedList<Position> crawlVerticalRow(Position startPosition) {
            LinkedList<Position> positionRow = new LinkedList<>();
            Position currentPosition = startPosition.copy();
            while (isRoomInterior(currentPosition)) {
                positionRow.addLast(currentPosition.copy());
                currentInteriorElement.addPosition(currentPosition.copy());
                currentPosition.add(verticalDirection);
                visualize(currentPosition);
            }
            currentPosition = Position.add(startPosition, verticalDirection.inverse());
            while (isRoomInterior(currentPosition)) {
                positionRow.addFirst(currentPosition.copy());
                currentInteriorElement.addPosition(currentPosition.copy());
                currentPosition.add(verticalDirection.inverse());
                visualize(currentPosition);
            }
            return positionRow;
        }
    }

    private class HallwayCrawler {
        private InteriorHallway currentInteriorElement;

        public HallwayCrawler(Position startPosition) {
            currentInteriorElement = new InteriorHallway();
            currentInteriorElement.corridorBeginning = crawl(startPosition.copy());
            currentInteriorElement.reversePositionsList();
            currentInteriorElement.corridorEnd = crawl(startPosition);
            spawnCrawler(enterRoom(currentInteriorElement.corridorBeginning));
            spawnCrawler(enterRoom(currentInteriorElement.corridorEnd));
        }

        private Position crawl(Position position) {
            Position.Step[] directions = Position.Steps;
            boolean canTakeStep = true;
            while (canTakeStep) {
                currentInteriorElement.addPosition(position);
                canTakeStep = false;
                for (int i = 0; i < 4; i++) {
                    Position newPosition = position.add(position, directions[i]);
                    if (isHallway(newPosition) && !currentInteriorElement.containsPosition(newPosition)) {
                        position = newPosition;
                        canTakeStep = true;
                        break;
                    }
                }
            }
            return position;
        }

        private Position enterRoom(Position startPosition) {
            if (isRoom(startPosition) && !positionToElement.containsKey(startPosition)) {
                return startPosition;
            }
            Position.Step[] directions = Position.Steps;
            for (int i = 0; i < 4; i++) {
                Position newPosition = Position.add(startPosition, directions[i]);
                if (isRoom(newPosition) && !positionToElement.containsKey(newPosition)) {
                    return newPosition;
                }
            }
            return null;
        }
    }

    private Position randomPositionInInterior() {
        Position position = new Position((int) (RandomWrapper.nextDouble() * WIDTH), (int) (RandomWrapper.nextDouble() * HEIGHT));
        while (!isInterior(position)) {
            position = new Position((int) (RandomWrapper.nextDouble() * WIDTH), (int) (RandomWrapper.nextDouble() * HEIGHT));
        }
        return position;
    }

    private boolean isInterior(Position position) {
        if (!validPosition(position)) {
            return false;
        }
        return isInterior(grid[position.x()][position.y()]);
    }

    private static boolean isInterior(TETile tile) {
        return (!World.isBackgroundTile(tile) && !World.isWallTile(tile));
    }

    /* returns true if position is in a hallway (which we define to be the case if and only if there are at most two directions to move in) */
    private boolean isHallway(Position position) {
        if (!validPosition(position)) {
            return false;
        }
        return (isInterior(position) && countNeighboringInteriors(position) <= 2);
    }

    /* returns true if position is at the deadend of a hallway */
    private boolean isHallwayDeadend(Position position) {
        if (!validPosition(position)) {
            return false;
        }
        return (isInterior(position) && countNeighboringInteriors(position) == 1);
    }

    /* returns true if position is in a room, that is, not a wall tile and contained in the rectangle that encompasses a room the walls around it */
    private boolean isRoom(Position position) {
        if (!validPosition(position)) {
            return false;
        }
        return (isInterior(position) && countNeighboringInteriors(position) > 2);
    }

    /* returns true if position is inside a room, and, moreover, inside the maximal rectangle contained in a room. */
    private boolean isRoomInterior(Position position) {
        if (!validPosition(position)) {
            return false;
        }
        if (isWall(Position.add(position, RoomCrawler.horizontalDirection.inverse())) && isWall(Position.add(position, RoomCrawler.horizontalDirection))) {
            return false;
        }
        if (isWall(Position.add(position, RoomCrawler.verticalDirection.inverse())) && isWall(Position.add(position, RoomCrawler.verticalDirection))) {
            return false;
        }
        return (isInterior(position) && countNeighboringInteriors(position) > 2);
    }

    private int countNeighboringInteriors(Position position) {
        int interiorCount = 0;
        Position.Step[] directions = Position.Steps;
        for (int i = 0; i < 4; i++) {
            Position neighbor = Position.add(position, directions[i]);
            Position diagonalNeighbor = Position.add(position, directions[i]).add(directions[i].orthogonalSteps()[0]);
            if (isInterior(neighbor)) {
                interiorCount++;
            }
            if (isInterior(diagonalNeighbor)) {
                interiorCount++;
            }
        }
        return interiorCount;
    }

    private boolean isWall(Position position) {
        if (!validPosition(position)) {
            return false;
        }
        return World.isWallTile(grid[position.x()][position.y()]);
    }

    private boolean validPosition(Position position) {
        return (position.x() >= 0 && position.x() < WIDTH && position.y() >= 0 && position.y() < HEIGHT);
    }

    private void visualize(Position position) {
        TETile oldTile = grid[position.x()][position.y()];
        grid[position.x()][position.y()] = Tileset.SAND;
        ter.renderFrame(grid);
        grid[position.x()][position.y()] = oldTile;
        StdDraw.pause(100);
    }

    private void visualize(LinkedList<LinkedList<Position>> positionGrid) {
        changeTiles(positionGrid, Tileset.SAND);
        ter.renderFrame(grid);
        changeTiles(positionGrid, Tileset.FLOWER);
        StdDraw.pause(500);
    }

    private void changeTiles(LinkedList<LinkedList<Position>> positionGrid, TETile tile) {
        for (int i = 0; i < positionGrid.size(); i++) {
            for (int j = 0; j < positionGrid.get(i).size(); j++) {
                Position pos = positionGrid.get(i).get(j);
                grid[pos.x()][pos.y()] = tile;
            }
        }
    }

}
