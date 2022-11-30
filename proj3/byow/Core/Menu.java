package byow.Core;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.awt.event.KeyEvent;

import static java.awt.event.KeyEvent.*;

public class Menu {
    private int width;
    private int height;
    private boolean startOfGame;


    public Menu(int width, int height){
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
    public String solicitSeed(){
        String display = "";
        while (StdDraw.hasNextKeyTyped() && !display.contains("S")) {
            String e = String.valueOf(StdDraw.nextKeyTyped());
            display += e;
            drawFrame(display);
            StdDraw.pause(800);
        }
        return display;
    }
    public String solicitLetter(){
        String display = "";
        while (StdDraw.hasNextKeyTyped()) {
            display += StdDraw.nextKeyTyped();
        }
        return display;
    }

    public void drawMain() {
        String firstInput;
        if (startOfGame == true) {
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.clear(Color.BLACK);
            Font fontSmall = new Font("Monaco", Font.BOLD, 20);
            Font fontSmallest = new Font("Monaco", Font.BOLD, 8);
            StdDraw.setFont(fontSmallest);
            drawTop(1);
            StdDraw.pause(2000);
            StdDraw.clear(Color.BLACK);
            drawNLQ();
            drawTop(2);
            while (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            firstInput = solicitLetter();
            if (firstInput.equalsIgnoreCase("N")) {
                StdDraw.clear(Color.BLACK);
                drawTop(3);
                while (!StdDraw.hasNextKeyTyped()) {
                    continue;
                }
                String secondInput = solicitSeed();
                Engine engine = new Engine();
                engine.interactWithInputString(secondInput);

            } else if (firstInput.equalsIgnoreCase("L")) {
                //load previous screen
                drawFrame("");
                System.out.println("load previous");
            } else if (firstInput.equalsIgnoreCase("Q")) {
                drawFrame("");
                System.exit(0);
            }

        }

    }
    public void drawTop(int num) {
        Font fontSmall = new Font("Monaco", Font.BOLD, 20);
        Font fontSmallest = new Font("Monaco", Font.BOLD, 8);
        StdDraw.setFont(fontSmall);
        StdDraw.line(0, height - 2, width, height - 2);

        if (num == 3) {
            StdDraw.text(width / 2, height - 1, "Type in the seed followed by 'S'");
        } else if (num == 1 ){
            StdDraw.text(width / 2, height - 1, "BYOW Project 3");
            StdDraw.setFont(fontSmallest);
            StdDraw.textRight(width, height -1, "Manuel Neumayer and Isabela Moise  ");
        } else {
            StdDraw.text(width / 2, height - 1, "Please type either 'Q', 'N' or 'L'");
        }
        StdDraw.show();

    }
    public void drawNLQ() {
        drawButtons("New World (N)", width / 2, height - 10 );
        drawButtons("Load (L)", width / 2, height - 20 );
        drawButtons("Quit (Q)", width / 2, height - 30 );
        StdDraw.show();

    }
    public void drawButtons(String l, int x, int y) {
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(x, y, l);

    }
    public void drawFrame(String s) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(this.width / 2, this.height / 2, s);
        StdDraw.show();
    }


    public void startGame() {
        this.startOfGame = true;
        drawMain();
    }

    public static void main(String[] args) {
        Menu menu = new Menu(40, 40);
        menu.startGame();
    }



}
