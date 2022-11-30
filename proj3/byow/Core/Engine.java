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
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

    private TETile[][] grid;
    private Crawler crawler;
    private Player player;
    private Enemy enemy;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        Menu menu = new Menu(40, 40);
        String input = menu.startGame();
        setupGame(input);
        runGame();
    }

    private TETile[][] drawCanvas(TETile[][] canvas) {
        player.draw(canvas);
        enemy.draw(canvas);
        return canvas;
    }

    public void setupGame(String input) {
        if (input == "l") {
            grid = DataHandling.restoreGrid();
        } else {
            RandomWrapper.setup((long) Integer.parseInt(input));
            World world = new World(90, 45);
            world.setup();
            grid = world.getGrid();
        }
        crawler = new Crawler(grid);
        player = new Player(crawler.randomPositionInInterior());
        enemy = new Enemy(crawler.randomPositionInInterior(), player);
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
            enemy.move(crawler);
        }
        runGame();
        return drawCanvas(grid.clone());
    }

    /* May only be called after everything has been set up! (grid + player + enemy) */
    private void runGame() {
        while (true) {
            String input = gatherKeyInput();
            if (input == ":") {
                TETile[][] canvas = drawCanvas(grid.clone());
                DataHandling.storeGrid(canvas);
                StdDraw.pause(500);
                System.exit(0);
            } else {
                player.move(grid, input);
                enemy.move(crawler);
                TETile[][] canvas = drawCanvas(grid.clone());
                ter.renderFrame(canvas);
                StdDraw.pause(10);
            }
        }
    }

    public static void main(String[] args) {
        World world = new World();
        DataHandling.setup();
        TETile[][] grid = new TETile[][]{new TETile[]{Tileset.NOTHING}};
        DataHandling.storeGrid(grid);
        TETile[][] grid1 = DataHandling.restoreGrid();
        System.out.println(DataHandling.turnGridToString(grid1));
    }

}
