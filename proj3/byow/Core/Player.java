package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Player {
    private Position position;
    private TETile myBeautifulFace = Tileset.AVATAR;

    public Player(Position pos) {
        position = pos;
    }

    public void move(TETile[][] grid, String input) {

    }

    private boolean validMove(TETile[][] grid, Position.Step step) {

    }

    public void draw(TETile[][] grid) {
        grid[position.x()][position.y()] = myBeautifulFace;
    }
}
