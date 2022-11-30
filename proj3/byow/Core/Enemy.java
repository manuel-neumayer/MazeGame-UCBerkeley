package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Enemy {
    private Player player;
    private Position position;
    private long waitBetweenMoves = 500;
    private long lastTimeMoved = -waitBetweenMoves;
    private TETile myBeautifulFace = Tileset.MOUNTAIN;

    public Enemy(Position startPosition, Player player) {
        position = startPosition;
        this.player = player;
    }

    public void move(Crawler crawler) {
        if (System.nanoTime() - lastTimeMoved >= waitBetweenMoves) {
            position = crawler.nextPosition(position, player.position);
        }
    }

    public void draw(TETile[][] canvas) {
        canvas[position.x()][position.y()] = myBeautifulFace;
    }

}
