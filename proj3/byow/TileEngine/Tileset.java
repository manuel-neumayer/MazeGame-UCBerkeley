package byow.TileEngine;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile AVATAR = new TETile('@', Color.white, Color.black, "me", "./byow/TileEngine/Tiles/Me.jpg");
    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall", "./byow/TileEngine/Tiles/Brick2.jpg");
    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.black,
            "floor");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower", "./byow/TileEngine/Tiles/Grass2 (1).jpg");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand", "./byow/TileEngine/Tiles/Manuel2.jpg");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");

    public static HashMap<Integer, TETile> integerToTile = new HashMap<>();
    public static HashMap<TETile, Integer> tileToInteger = new HashMap<>();
    private static int alphabetKey = 0;

    public static void setupTileAlphabet() {
        addTileToAlphabet(AVATAR);
        addTileToAlphabet(WALL);
        addTileToAlphabet(FLOOR);
        addTileToAlphabet(NOTHING);
        addTileToAlphabet(GRASS);
        addTileToAlphabet(WATER);
        addTileToAlphabet(FLOWER);
        addTileToAlphabet(LOCKED_DOOR);
        addTileToAlphabet(UNLOCKED_DOOR);
        addTileToAlphabet(SAND);
        addTileToAlphabet(MOUNTAIN);
        addTileToAlphabet(TREE);
    }

    public static void addTileToAlphabet(TETile tile) {
        if (tileToInteger.containsKey(tile)) {
            return;
        }
        tileToInteger.put(tile, alphabetKey);
        integerToTile.put(alphabetKey, tile);
        alphabetKey++;
    }

}
