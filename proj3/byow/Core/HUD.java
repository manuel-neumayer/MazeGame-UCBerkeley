package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

public class HUD {
    int h;
    int w;
    TETile[][] world;

    public Position mouseLocation() {
        int x = (int) (StdDraw.mouseX());
        int y = (int) ((StdDraw.mouseY() - 2));
        return new Position(x, y);
    }
    public HUD(int width, int height, TETile[][] wor){
        this.world = wor;
        this.w = width;
        this.h = height;
        //StdDraw.setCanvasSize(width, height / 16);
        /*Font fontSmallest = new Font("Monaco", Font.BOLD, 8);
        StdDraw.setFont(fontSmallest);
        StdDraw.textLeft(w-5, 1, "   This is the HUD");*/
    }
    //check if y and x out of X and Y range for world
    // We created a Render function in our World Generator class, which had a while(true) loop that continuously calls on our HUD function, and breaks if the user does a keypress.
    //When you render your HUD, make sure you do it after you call ter.renderFrame() from the Engine class as this method clears the canvas before drawing the 2D TETile array and make sure that when you draw your HUD, you're NOT clearing the canvas again.
    //do renderFrame in HUD method
    public void checkAndDisplay(int width, int height) {
        //drawFrame("");
        String text;
        Position p = mouseLocation();
        if (p.y() >= height || p.x() >= width || p.x() < 0 || p.y() < 0) {
            text = displayNothing();
        } else {
            text = displayTile(p);
        }
        drawFrame(text);
    }

    public void drawFrame(String s) {
        StdDraw.setPenColor(Color.WHITE);
        Font oldFont = StdDraw.getFont();
        Font fontSmallest = new Font("Monaco", Font.BOLD, 25);
        StdDraw.setFont(fontSmallest);
        StdDraw.textLeft(0, 1, s);
        StdDraw.setFont(oldFont);
        StdDraw.show();
    }
    public String displayTile(Position p) {
        String output = "";
        TETile placement = world[p.x()][p.y()];
        //at this position, figure out which tile shares these coordinates and tell em the type
        if (placement == Tileset.NOTHING) {
            output = "   Nothing here";
        } else if (placement == Tileset.WALL) {
            output = "   Wall";
        } else if (placement == Tileset.FLOWER) {
            output = "   Floor";
        }
        return output;
    }
    public String displayNothing() {
        return "Out of Bounds";
    }

}


