package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Enemy {
    private Player player;
    private Crawler crawler;
    public Position position;
    private long waitBetweenMoves = 500;
    private long lastTimeMoved = -waitBetweenMoves;
    private TETile myBeautifulFace = Tileset.SAND;

    public Enemy(Position startPosition, Player player, Crawler crawler) {
        position = startPosition;
        this.player = player;
        this.crawler = crawler;
    }

    public void move() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTimeMoved >= waitBetweenMoves) {
            lastTimeMoved = currentTime;
            position = crawler.nextPosition(position, player.position);
        }
    }

    public void draw(TETile[][] canvas) {
        canvas[position.x()][position.y()] = myBeautifulFace;
    }

    public void drawPath(TETile[][] canvas) {
        Position currentPosition = position.copy();
        while (!currentPosition.equals(player.position)) {
            canvas[currentPosition.x()][currentPosition.y()] = Tileset.MOUNTAIN;
            currentPosition = crawler.nextPosition(currentPosition, player.position);
        }
    }

}
