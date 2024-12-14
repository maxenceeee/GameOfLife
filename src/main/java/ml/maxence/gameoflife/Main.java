package ml.maxence.gameoflife;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;


public class Main extends JPanel
{

    static JFrame frame;

    int width;
    int height;

    boolean[][] cells;

    int fps;
    int fpsToShow;

    int generation = 0;

    int offsetX = 0, offsetY = 0;
    int lastMouseX = -1, lastMouseY = -1;
    public Main(int width, int height) {
        this.width = width;
        this.height = height;

        this.cells = new boolean[width][height];

        frame = new JFrame("GameOfLife | By Maxence");
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(this);
        frame.setBackground(Color.YELLOW);

        frame.setVisible(true);

        frame.addKeyListener(new Keyboard(this));
        frame.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (lastMouseX == -1 || lastMouseY == -1) {
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                }
                int dx = e.getX() - lastMouseX;
                int dy = e.getY() - lastMouseY;
                offsetX += dx;
                offsetY += dy;
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                repaint();
            }
        });

        this.randomCells();
        this.runGame();
    }

    public void runGame() {
        long startTime = System.currentTimeMillis();
        long lastUpdate = System.nanoTime();
        long tick = 1000000000 / 25; // max tps (update)
        int tps = 0;

        while(true) {
            long currentTime = System.nanoTime();
            if(currentTime - lastUpdate > tick) {
                lastUpdate += tick;
                tps++;
                this.update();
            } else {
                this.frame.repaint();
            }

            if(System.currentTimeMillis() - startTime > 1000) {
                fpsToShow = fps;
                fps = 0;
                tps = 0;
                startTime += 1000;

                System.out.println(testNumberCells());
            }
        }
    }

    public void randomCells() {
        int numberCells = 6000;
        Random rand = new Random();

        for(int i = 0; i < numberCells; i++) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            if(cells[x][y]) continue;
            cells[x][y] = true;
        }
    }

    public void update() {
        boolean[][] newCells = new boolean[this.width][this.height];

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {

                int count = 0;

                for (int ix = -1; ix <= 1; ix++) {
                    for (int iy = -1; iy <= 1; iy++) {
                        if (ix == 0 && iy == 0) {
                            continue;
                        }

                        int indexX = x + ix;
                        int indexY = y + iy;
                        if (indexX >= 0 && indexX < this.width && indexY >= 0 && indexY < this.height && cells[indexX][indexY]) {
                            count++;
                        }
                    }
                }
                newCells[x][y] = cells[x][y] ? (count == 2 || count == 3) : (count == 3);
            }
        }

        this.cells = newCells;
        this.generation++;
    }

    Color cellColor = Color.RED;
    public void paintComponent(Graphics g) {

        int offsetX = (int) (1100 / this.width * Keyboard.zoom);
        int offsetY = (int) (800 / this.height * Keyboard.zoom);

        fps++;
        g.setColor(cellColor);
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                if(!cells[x][y]) continue;
                g.fillRect(x*offsetX,y*offsetY,offsetX,offsetY);
            }
        }

        g.setColor(Color.BLACK);

        g.drawString("Génération n°"+ generation +" | FPS: " + fpsToShow + " | Zoom: "+ Keyboard.zoom, 10, 20);
    }

    public int testNumberCells() {
        int count = 0;
        for(int x = 0; x < this.width; x++) {
            for(int y = 0; y < this.height; y++) {
                count += cells[x][y] ? 1 : 0;
            }
        }
        return count;
    }
    public static void main(String[] args) {
        new Main(300, 200);
    }

    private static class Keyboard extends KeyAdapter {

        public static double zoom = 1;
        Main main;

        public Keyboard(Main main) {
            this.main = main;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            final int keyCode = e.getKeyCode();
            if(keyCode == KeyEvent.VK_UP) {
                zoom = Math.min(3.0, zoom + 0.1);
                Main.frame.repaint();
            } else if (keyCode == KeyEvent.VK_DOWN) {
                zoom = Math.max(0.1, zoom - 0.1);
                Main.frame.repaint();
            } else if (keyCode == KeyEvent.VK_E) {
                main.randomCells();
            }
            super.keyPressed(e);
        }

    }
}