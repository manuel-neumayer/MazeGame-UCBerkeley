package byow.TileEngine;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class MyImage {
    public static void main(String args[])throws IOException{
        int width = 16;    //width of the image
        int height = 16;   //height of the image
        BufferedImage image = null;
        File f = null;
        try{
            f = new File("byow/TileEngine/Tiles/Grass2.jpg"); //image file path
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            image = ImageIO.read(f);
            System.out.println("Reading complete.");
        }catch(IOException e){
            System.out.println("Error: "+e);
        }
    }

}
