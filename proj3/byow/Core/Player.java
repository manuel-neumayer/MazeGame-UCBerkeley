package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Player {
    public Position position;
    private TETile myBeautifulFace = Tileset.AVATAR;

    public Player(Position pos) {
        position = pos;
    }

    public void move(TETile[][] grid, String input) {
        if (validPress(input)) {
            Position.Step st = keyMove(input);
            if (validMove(grid, st)) {
                position = Position.add(position, st);
            }
        }

    }

    private Position.Step keyMove(String st) {
        Position.Step step;
        if (st.equalsIgnoreCase("W")) {
            step = new Position.Step(0,1);
        } else if (st.equalsIgnoreCase("A")) {
            step = new Position.Step(-1,0);
        } else if (st.equalsIgnoreCase("S")) {
            step = new Position.Step(0, -1);
        } else {
            step = new Position.Step(1, 0);
        }
        return step;
    }
    private boolean validPress(String s) {
        if (s.equalsIgnoreCase("W") || s.equalsIgnoreCase("A") || s.equalsIgnoreCase("S") || s.equalsIgnoreCase("D")) {
            return true;
        }
        return false;
    }

    private boolean validMove(TETile[][] grid, Position.Step step) {
        Position newPos = Position.add(position, step);
        TETile tile = grid[newPos.x()][newPos.y()];
        if (tile == Tileset.FLOWER ) {
            return true;
        } else {
            return false;
        }
    }

    public void draw(TETile[][] grid) {
        grid[position.x()][position.y()] = myBeautifulFace;
    }
}
