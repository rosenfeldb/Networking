import CatlinGraphics2D.AnimationCanvas2D;
import CatlinGraphics2D.GraphicsWindow;
import CatlinGraphics2D.ImageUtilities;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class GUI extends AnimationCanvas2D {
    public static BufferedImage buffer;
    boolean test;


    public static void main(String[] args) {
        Component gameCanvas = new GUI(720, 800, true);
        GraphicsWindow.makeWindow(gameCanvas, "Game");
    }
    public GUI(int width, int height, boolean test) {
        super(width, height, 100);
        this.test=test;
    }

    public void setBuffer(BufferedImage image){
        buffer = image;
    }
    @Override
    public void start() {

    }

    @Override
    public void update(double elapsedMilliseconds) {

    }

    @Override
    public void draw(Graphics2D pen) {
        ImageUtilities.drawImage(pen, buffer, 0, 0);
    }

//    public class Key implements KeyListener{
//
//        @Override
//        public void keyTyped(KeyEvent e) {
//
//        }
//
//        @Override
//        public void keyPressed(KeyEvent e) {
//            if (!test){
//                robot.keyPress(e.getKeyCode());
//            }
//        }
//
//        @Override
//        public void keyReleased(KeyEvent e) {
//            if (!test){
//                robot.keyRelease(e.getKeyCode());
//            }
//        }
//    }

//    public class Clicker implements MouseListener, MouseMotionListener {
//        int x = 0;
//        int y = 0;
//
//
//        @Override
//        public void mouseClicked(MouseEvent e) {
//            if (test) {
//                int x1 = x;
//                int y1 = y;
//                System.out.println(x);
//                robot.mouseMove(x, y);
//                robot.mousePress(InputEvent.BUTTON1_MASK);
//                robot.mouseRelease(InputEvent.BUTTON1_MASK);
//
//                robot.mouseMove(720 + x1, y1 + 38);
//            }
//
//        }
//
//        @Override
//        public void mousePressed(MouseEvent e) {
//            if (!test){
//                robot.mousePress(InputEvent.BUTTON1_MASK);
//            }
//        }
//
//        @Override
//        public void mouseReleased(MouseEvent e) {
//            if (!test){
//                robot.mouseRelease(InputEvent.BUTTON1_MASK);
//            }
//        }
//
//        @Override
//        public void mouseEntered(MouseEvent e) {
//
//        }
//
//        @Override
//        public void mouseExited(MouseEvent e) {
//
//        }
//
//        @Override
//        public void mouseDragged(MouseEvent e) {
//
//        }
//
//        @Override
//        public void mouseMoved(MouseEvent e) {
//            x = e.getX();
//            y = e.getY();
//            if (!test){
//                robot.mouseMove(x,y);
//            }
//
//        }
//
//    }
}