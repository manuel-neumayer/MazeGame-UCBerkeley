package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class HUD {
    int mouseY;
    int mouseX;
    Position p;
    int h;
    int w;
    TETile[][] world;

    public Position mouseLocation() {
        int x = Math.round(mouseX);
        int y = Math.round(mouseY);
        p = new Position(x, y);
        return p;
    }
    public HUD(int width, int height, TETile[][] wor){
        this.world = wor;
        this.w = width;
        this.h = height;
        StdDraw.setCanvasSize(width, height / 16);
        Font fontSmallest = new Font("Monaco", Font.BOLD, 8);
        StdDraw.setFont(fontSmallest);
        StdDraw.textLeft(w, h -1, "   This is the HUD");
    }
    //check if y and x out of X and Y range for world
    // We created a Render function in our World Generator class, which had a while(true) loop that continuously calls on our HUD function, and breaks if the user does a keypress.
    //When you render your HUD, make sure you do it after you call ter.renderFrame() from the Engine class as this method clears the canvas before drawing the 2D TETile array and make sure that when you draw your HUD, you're NOT clearing the canvas again.
    //do renderFrame in HUD method
    public void checkAndDisplay(int width, int height) {
        drawFrame("");
        String text;
        if (p.y() > height || p.x() > width || p.x() < 0 || p.y() < 0) {
            text = displayNothing();
        } else {
            text = displayTile(p);
        }
        drawFrame(text);
    }

    public void drawFrame(String s) {
        StdDraw.setPenColor(Color.WHITE);
        Font fontSmallest = new Font("Monaco", Font.BOLD, 8);
        StdDraw.setFont(fontSmallest);
        StdDraw.textLeft(w, h -1, s);
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


