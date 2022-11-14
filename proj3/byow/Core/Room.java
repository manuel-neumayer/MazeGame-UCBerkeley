package byow.Core;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;


import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Room {
    private int width;
    private int length;
    private Random rand;
    private World world;
    public LinkedList<Position> newCorridors;

    public Room(LinkedList<LinkedList<Position>> positions, Random rand, World world) {
        this.rand = rand;
        this.world = world;
        newCorridors = new LinkedList<>();
        int size = positions.get(0).size();
        if (rand.nextDouble() < 0.5) {
            // UP
            Position newCorridor = positions.get(0).get(size / 4 + (int) ((size / 2) * rand.nextDouble()));
            newCorridors.push(newCorridor);
            world.setTileToWall(newCorridor);
        }
        if (rand.nextDouble() < 0.5) {
            // DOWN
            Position newCorridor = positions.get(positions.size() - 1).get(size / 4 + (int) ((size / 2) * rand.nextDouble()));
            newCorridors.push(newCorridor);
            world.setTileToWall(newCorridor);
        }
        if (rand.nextDouble() < 0.5) {
            // UP
            size = positions.size();
            Position newCorridor = positions.get(size / 4 + (int) ((size / 2) * rand.nextDouble())).get(positions.get(0).size() - 1);
            newCorridors.push(newCorridor);
            world.setTileToFloor(newCorridor);
        }
        furnishRoom(positions);
    }

    private void furnishRoom(LinkedList<LinkedList<Position>> p) {
        TETile[][] g = world.getGrid();
        for (int i = 0; i < p.size(); i++) {
            LinkedList<Position> y = p.get(i);
            for (int j = 0; j < y.size(); j++) {
                Position pos = y.get(j);
                if (g[pos.x()][pos.y()] == Tileset.NOTHING) {
                    g[pos.x()][pos.y()] = Tileset.FLOWER;
                }
            }
        }
    }

        public Room makeRoom(Position s){
        TETile[][] g = world.getGrid();
        int leftCorner = s.x() - (width / 2);
        int rightCorner = s.x() + (width / 2);
        //width
        for (int i = leftCorner ; i <= rightCorner ; i += 1) {
            g[i][s.y()] = Tileset.FLOWER;
        }
        for (int i = s.x() -(width / 2) ; i <= s.x() + (width / 2); i += 1) {
            g[i][s.y() + length] = Tileset.FLOWER;
        }
        for (int i = s.y(); i <= s.y() + length; i += 1) {
            g[s.x() - (width / 2)][i] = Tileset.FLOWER;
        }
        for (int i = s.y(); i <= s.y() + length; i += 1) {
            g[s.x() + (width / 2)][i] = Tileset.FLOWER;
        }
    return null;

    }
    //make a corridor, remove wall and mark it
    private int giveGoodWidth(Long seed, Position p){
        int w = 0;
        for (int i = 0; i < 100; i += 1) {
            if (!checkWidth(w, p)) {
                w = randomizeSize(seed);
            } else {
                break;
            }
        }
        return w;


    }
    private int giveGoodLen(Long seed, Position p){
        int l = 0;
        for (int i = 0; i < 100; i += 1) {
            if (!checkLength(l, p)) {
                l = randomizeSize(seed);
            } else {
                break;
            }
        }
        return l;


    }


    private int randomizeSize(Long seed){
        int result = rand.nextInt();
        while (result == 0) {
            result = rand.nextInt();
        }
        return result;
    }
    private boolean checkWidth(int wi, Position pos) {
        TETile[][] g = world.getGrid();
        if (pos.x() -(wi / 2) < 0 || pos.x() + (wi / 2) > world.WIDTH()) {
            return false;
        }
        for (int i = pos.x() -(wi / 2) ; i <= pos.x() + (wi / 2); i += 1) {
            if (g[pos.x()][pos.y()] != Tileset.NOTHING) {
                return false;
            }
        }
        return true;
    }
    private boolean checkLength(int len, Position p){
        TETile[][] g = world.getGrid();
        if (p.y() + len > world.HEIGHT()) {
            return false;
        }
        for (int i = p.y(); i <= p.y() + len; i += 1) {
            if (g[p.x()][p.y()] != Tileset.NOTHING) {
                return false;
            }
        }
        return true;

    }

}