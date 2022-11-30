package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.In;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Array;
import java.util.Collections;

public class DataHandling {
    public static final String file_for_stored_grids_name = "saved_grid_strings.txt";
    public static final Path folder_for_stored_grids_location = Paths.get(".", "data");
    public static final Path file_for_stored_grids_location = Paths.get(".", "data", file_for_stored_grids_name);

    public static void setup() {
        Tileset.setupTileAlphabet();
    }

    /*private static String getSavedGridString() {
        In in = new In(file_for_stored_grids_location.toString());
        String gridString;
        try {
            gridString = Files.readString(file_for_stored_grids_location);
        } catch (IOException exception) {
            System.out.println(file_for_stored_grids_name + " does not exist! (Or another error occurred.)");
            exception.printStackTrace();
            gridString = "";
        }
        return gridString;
        /*char[] gridStringsArray = gridStrings.toCharArray();
        int currentIndex = 0;
        while (currentIndex < gridStringsArray.length) {
            String gridString = "";
            while (gridStringsArray[currentIndex] != '&') {
                gridString += gridStringsArray[currentIndex];
                currentIndex++;
            }
            storedGridStrings.add(gridString);
            currentIndex++;
        }
        return storedGridStrings;
    }*/

    public static String turnGridToString(TETile[][] grid) {
        String gridString = RandomWrapper.seed() + "!" + RandomWrapper.callCount() + "!" + grid.length + "!" + grid[0].length + "!";
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                gridString += Tileset.tileToInteger.get(grid[i][j]) + ":";
            }
            gridString += "&";
        }
        gridString += "*";
        return gridString;
    }

    /* Sets up the RandomWrapper with the information provided in the string and creates the grid corresponding to the sgtring! */
    public static TETile[][] turnStringToGrid(String gridString) {
        int[] dimensions = new int[4];
        char[] gridStringArray = gridString.toCharArray();
        int currentIndex = 0;
        for (int index = 0; index < dimensions.length; index++) {
            String dimensionAsString = "";
            while (gridStringArray[currentIndex] != '!') {
                dimensionAsString += gridStringArray[currentIndex];
                currentIndex++;
            }
            Integer dimension = Integer.parseInt(dimensionAsString);
            dimensions[index] = dimension;
            currentIndex++;
        }
        long seed = (long) dimensions[0];
        int callCount = dimensions[1];
        RandomWrapper.setup(seed, callCount);
        TETile[][] grid = new TETile[dimensions[2]][dimensions[3]];
        int i = 0;
        int j = 0;
        while (gridStringArray[currentIndex] != '*') {
            String tileIntegerAsString = "";
            while (gridStringArray[currentIndex] != ':') {
                tileIntegerAsString += gridStringArray[currentIndex];
                currentIndex++;
            }
            Integer tileInteger = Integer.parseInt(tileIntegerAsString);
            TETile tile = Tileset.integerToTile.get(tileInteger);
            grid[i][j] = tile;
            currentIndex++;
            i++;
            if (gridStringArray[currentIndex] == '&') {
                i = 0;
                j++;
                currentIndex++;
            }
        }
        return grid;
    }

    /* Creates an empty file with name 'file_for_stored_grids_name' in location 'file_for_stored_grids_location',
     * or empties the file if it already exists. */
    private static void prepareFileToStoreGrids() {
        try {
            File gridFile = new File(folder_for_stored_grids_location.toString());
            if (gridFile.createNewFile()) {
                System.out.println("File created: " + gridFile.getName());
            } else {
                FileWriter fileWriter = new FileWriter(file_for_stored_grids_name);
                fileWriter.close();
                System.out.println("File already exists, and was emptied.");
            }
        } catch (IOException exception) {
            System.out.println("An error occurred when 'prepareFileToStoreGrids' was called.");
            exception.printStackTrace();
        }
    }

    /* Saves the given grid, overriding a previous saved grid (if it exists). */
    public static void storeGrid (TETile[][] grid) {
        prepareFileToStoreGrids();
        try {
            //FileWriter fileWriter = new FileWriter(file_for_stored_grids_name);
            String gridString = turnGridToString(grid);
            //fileWriter.write(gridString);
            Files.write(file_for_stored_grids_location, Collections.singleton(gridString), StandardCharsets.UTF_8);
            System.out.println("gridString: " + gridString);
            System.out.println("helloooo " + Files.readString(file_for_stored_grids_location));
        } catch (IOException exception) {
            System.out.println("An error occurred when 'storeGrid' was called.");
            exception.printStackTrace();
        }
        /*

        String gridStrings = "";
            LinkedList<String> storedGridStrings = getSavedGridStrings();
            System.out.println("storedGridString: " + storedGridStrings.toString());
            for (int i = 0; i < storedGridStrings.size(); i++) {
                gridStrings += storedGridStrings.get(i) + "&";
            }
            gridStrings += representAsString(grid) + "&";
            System.out.println("gridStrings: " + gridStrings);

         */
    }

    public static TETile[][] restoreGrid() {
        TETile[][] grid;
        try {
            String gridAsString = Files.readString(file_for_stored_grids_location);
            grid = turnStringToGrid(gridAsString);
        } catch (IOException exception) {
            System.out.println("An error occurred when 'restoreGrid' was called.");
            exception.printStackTrace();
            grid = new TETile[0][0];
        }
        return grid;
    }
}
