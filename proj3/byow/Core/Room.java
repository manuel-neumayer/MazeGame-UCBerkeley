package byow.Core;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;


import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Room {
    private int width;
    private int length;
    private Random rand;
    private World world;
    public boolean hasBeenPlaced;
    public ArrayList<Position> newCorridors;

    private LinkedList<LinkedList<Position>> getPotentialPositions(Position position, Position.Step direction, int l1, int l2, int w) {
        LinkedList<LinkedList<Position>> positions = new LinkedList<>();
        positions.add(rowOfPositions(position, direction, w));
        Position.Step[] orthogonalDirections = direction.orthogonalSteps();
        Position currentPosition = position.copy();
        for (int j = 0; j < l1; j++) {
            positions.addFirst(rowOfPositions(currentPosition, direction, w));
            currentPosition.add(orthogonalDirections[0]);
        }
        currentPosition = position.copy();
        for (int j = 0; j < l1; j++) {
            positions.add(rowOfPositions(currentPosition, direction, w));
            currentPosition.add(orthogonalDirections[1]);
        }
        return positions;
    }

    private LinkedList<Position> rowOfPositions(Position position, Position.Step direction, int w) {
        LinkedList<Position> newRow = new LinkedList<>();
        Position currentPosition = position.copy();
        for (int i = 0; i < w; i++) {
            newRow.add(currentPosition);
            currentPosition.add(direction);
        }
        return newRow;
    }

    public Room(Position position, Long seed, World world) {
        
    }
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

    public void makeRoom(Tileset tile, Position position, Long seed){

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
