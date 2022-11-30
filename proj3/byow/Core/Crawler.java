package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.StdDraw;

import java.util.*;

public class Crawler {
    private TETile[][] grid;
    private int WIDTH;
    private int HEIGHT;
    private Graph graph;
    TERenderer ter = new TERenderer();

    public Graph getGraph() {
        return graph;
    }

    public static void main(String[] args) {
        //World world = new World(4695, 90, 45);
        //World world = new World(2562, 90, 45);
        RandomWrapper.setup();
        World world = new World(90, 45);
        world.setup();
        Crawler crawler = new Crawler(world.getGrid());
        Dijkstra d = new Dijkstra(crawler.getGraph(), world.getGrid());
        Position targetPos = crawler.randomPositionInInterior();
        int target = crawler.getVertex(targetPos);
        int[] edgeTo = d.shortestPath(target);
        Position sourcePos = crawler.randomPositionInInterior();
        int source = crawler.getVertex(sourcePos);
        TETile[][] grid = crawler.grid;
        grid[targetPos.x()][targetPos.y()] = Tileset.TREE;
        grid[sourcePos.x()][sourcePos.y()] = Tileset.TREE;
        crawler.ter.renderFrame(grid);
        while (source != target) {
            Position position = crawler.getPosition(source);
            grid[position.x()][position.y()] = Tileset.FLOOR;
            crawler.ter.renderFrame(crawler.grid);
            StdDraw.pause(100);
            source = edgeTo[source];
        }
        grid[targetPos.x()][targetPos.y()] = Tileset.TREE;
        grid[sourcePos.x()][sourcePos.y()] = Tileset.TREE;
        crawler.ter.renderFrame(grid);
    }

    public Crawler(TETile[][] grid) {
        this.grid = grid;
        WIDTH = grid.length;
        HEIGHT = grid[0].length;
        graph = new Graph(WIDTH * HEIGHT);

        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(grid);

        Position startPosition = randomPositionInInterior();
        HashSet<Integer> closedPositions = new HashSet<>();
        LinkedList<Position> openPositions = new LinkedList<>();
        openPositions.add(startPosition);
        closedPositions.add(getVertex(startPosition));
        while(openPositions.size() > 0) {
            Position currentPosition = openPositions.remove(0);

            //grid[currentPosition.x()][currentPosition.y()] = Tileset.SAND;
            //ter.renderFrame(grid);
            //StdDraw.pause(10);

            for (int i = 0; i < Position.Steps.length; i++) {
                Position newPosition = Position.add(currentPosition, Position.Steps[i]);
                if (isInterior(newPosition)) {
                    graph.addEdge(getVertex(currentPosition), getVertex(newPosition));
                    if (closedPositions.add(getVertex(newPosition))) {
                        openPositions.add(newPosition);
                    }
                }
            }
        }

        //ter.renderFrame(grid);
    }

    public int getVertex(Position position) {
        return position.y() * WIDTH + position.x();
    }

    public Position getPosition(int vertex) {
        return new Position(vertex % WIDTH, vertex / WIDTH);
    }

    private Position randomPositionInInterior() {
        Position position = new Position((int) (RandomWrapper.nextDouble() * WIDTH), (int) (RandomWrapper.nextDouble() * HEIGHT));
        while (!isInterior(position)) {
            position = new Position((int) (RandomWrapper.nextDouble() * WIDTH), (int) (RandomWrapper.nextDouble() * HEIGHT));
        }
        return position;
    }

    private boolean isInterior(Position position) {
        return isInterior(grid[position.x()][position.y()]);
    }

    private static boolean isInterior(TETile tile) {
        return (!World.isBackgroundTile(tile) && !World.isWallTile(tile));
    }



}
