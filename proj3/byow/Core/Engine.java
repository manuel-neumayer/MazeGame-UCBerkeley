package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.FileWriter;   // Import the FileWriter class

import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
public class Engine {
    private TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 88;
    public static final int HEIGHT = 44;

    private TETile[][] grid;
    private Crawler crawler;
    private Player player;
    private Enemy enemy;
    private HUD hud;
    private boolean showPath = true;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        System.out.println("heloooo");
        Menu menu = new Menu(40, 40);
        String input = menu.startGame();
        System.out.println("input" + input);
        setupGame(input);
        runGame();
    }

    private TETile[][] drawCanvas(TETile[][] canvas) {
        player.draw(canvas);
        if (showPath) {
            enemy.drawPath(canvas);
        }
        enemy.draw(canvas);
        return canvas;
    }

    private Position findTile(TETile[][] grid, TETile tile) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == tile) {
                    return new Position(i, j);
                }
            }
        }
        return null;
    }

    private void clearGrid(TETile[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == Tileset.SAND) {
                    grid[i][j] = Tileset.FLOWER;
                }
                if (grid[i][j] == Tileset.AVATAR) {
                    grid[i][j] = Tileset.FLOWER;
                }
                if (grid[i][j] == Tileset.MOUNTAIN) {
                    grid[i][j] = Tileset.FLOWER;
                }
            }
        }
    }

    public void setupGame(String input) {
        Tileset.setupTileAlphabet();
        if (input.equalsIgnoreCase("L")) {
            grid = DataHandling.restoreGrid();
            Position playerPos = findTile(grid, Tileset.AVATAR);
            Position enemyPos = findTile(grid, Tileset.SAND);
            clearGrid(grid);
            crawler = new Crawler(grid);
            player = new Player(playerPos);
            enemy = new Enemy(enemyPos, player, crawler);
        } else {
            RandomWrapper.setup((long) Integer.parseInt(input));
            World world = new World(WIDTH, HEIGHT);
            world.setup();
            grid = world.getGrid();
            crawler = new Crawler(grid);
            player = new Player(crawler.randomPositionInInterior());
            enemy = new Enemy(crawler.randomPositionInInterior(), player, crawler);
        }
        ter.initialize(WIDTH, HEIGHT + 2, 0, 2);
        hud = new HUD(WIDTH, HEIGHT, grid);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        char[] charArray = input.toCharArray();
        String firstLetter = "" + charArray[0];
        String setupInput = "";
        int currentIndex = 1;
        if (firstLetter.equalsIgnoreCase("N")) {
            String nextLetter = "" + charArray[currentIndex];
            while (Menu.isNumeric(nextLetter)) {
                setupInput += nextLetter;
                currentIndex++;
            }
            currentIndex++;
        } else if (firstLetter.equalsIgnoreCase("L")) {
            setupInput = "L";
        } else {
            System.exit(0);
        }
        setupGame(setupInput);
        while (currentIndex < charArray.length) {
            String letter = "" + charArray[currentIndex];
            player.move(grid, letter);
            enemy.move();
        }
        runGame();
        return drawCanvas(cloneGrid(grid));
    }

    /* May only be called after everything has been set up! (grid + player + enemy) */
    private void runGame() {
        while (true) {
            String input = gatherKeyInput();
            if (input.equals(".")) {
                TETile[][] canvas = drawCanvas(cloneGrid(grid));
                DataHandling.storeGrid(canvas);
                StdDraw.pause(500);
                System.exit(0);
            } else {
                player.move(grid, input);
                enemy.move();
                TETile[][] canvas = drawCanvas(cloneGrid(grid));
                ter.renderFrame(canvas);
                hud.mouseLocation();
                hud.checkAndDisplay(WIDTH, HEIGHT);
                StdDraw.pause(10);
            }
            if (player.position.equals(enemy.position)) {
                /* create endgame and call the endgame function */
                //StdDraw.pause(2000);
                //System.exit(0);
            }
        }
    }
    private String gatherKeyInput() {
        return Menu.solicitLetter();
    }

    public static void main(String[] args) {
        World world = new World();
        DataHandling.setup();
        TETile[][] grid = new TETile[][]{new TETile[]{Tileset.NOTHING}};
        DataHandling.storeGrid(grid);
        TETile[][] grid1 = DataHandling.restoreGrid();
        System.out.println(DataHandling.turnGridToString(grid1));
    }

    private TETile[][] cloneGrid(TETile[][] grid) {
        TETile[][] clone = new TETile[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                clone[i][j] = grid[i][j];
            }
        }
        return clone;
    }

}
