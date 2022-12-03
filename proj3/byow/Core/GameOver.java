package byow.Core;
import edu.princeton.cs.algs4.StdDraw;
import java.awt.*;

public class GameOver {
        private int width;
        private int height;

        public GameOver(int width, int height) {
            this.width = width;
            this.height = height;
            StdDraw.setCanvasSize(this.width * 16, this.height * 16);
            Font font = new Font("Monaco", Font.BOLD, 30);
            StdDraw.setFont(font);
            StdDraw.setXscale(0, this.width);
            StdDraw.setYscale(0, this.height);
            StdDraw.clear(Color.BLACK);
            StdDraw.enableDoubleBuffering();

        }
        public void drawEnd() {
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.clear(Color.BLACK);
            Font fontBig = new Font("Monaco", Font.BOLD, 30);
            StdDraw.setFont(fontBig);
            StdDraw.text(this.width / 2, this.height / 2, "GAME OVER: You were caught...");
            StdDraw.show();
        }
    public void endGame() {
        drawEnd();
    }

    public static void main(String[] args) {
        GameOver menu = new GameOver(40, 40);
        menu.endGame();
    }
}
