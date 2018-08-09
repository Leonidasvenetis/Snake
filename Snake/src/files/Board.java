package files;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.*;

public class Board extends JPanel implements ActionListener {

    private final int B_WIDTH = 300;
    private final int B_HEIGHT = 300;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 900;
    private final int RAND_POS = 29;
    private final int DELAY = 140;

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private int dots, apple_x, apple_y, score = 0;

    private boolean leftD = false;
    private boolean rightD = false;
    private boolean upD = false;
    private boolean downD = false;
    private boolean inGame = true;
    private boolean specialAp = false;

    private Timer timer;
    private Image ball, apple, spapple, head, wall;

    public Board() {
        initBoard();
    }

    private void initBoard() {

        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);
        setDoubleBuffered(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();
    }

    private void loadImages() {
        ImageIcon iib = new ImageIcon("src/resources/ball.png");
        ball = iib.getImage();

        ImageIcon iia = new ImageIcon("src/resources/apple.png");
        apple = iia.getImage();

        ImageIcon iisa = new ImageIcon("src/resources/specialapple.png");
        spapple = iisa.getImage();

        ImageIcon iih = new ImageIcon("src/resources/head.png");
        head = iih.getImage();

        ImageIcon iiw = new ImageIcon("src/resources/wall.png");
        wall = iiw.getImage();
        }

    private void initGame() {
        dots = 3;
        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 50;
        }

        locateApple();
        timer = new Timer(DELAY,this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        if (inGame) {
            if (specialAp) {
                g.drawImage(spapple, apple_x, apple_y, this); //placeholder for double apple drawing
            } else {
                g.drawImage(apple, apple_x, apple_y, this);

            }
            for (int x = 0; x < 20; x++) {
                g.drawImage(wall, 0+x, 100, this);
            }
            for (int x = 0; x < 30; x++) {
                g.drawImage(wall, 20, 100+x, this);
            }
            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.drawImage(head, x[z], y[z], this);
                } else {
                    g.drawImage(ball, x[z], y[z], this);
                }
            }

            String Score = "Score: " + score;
            Font text = new Font("Helvetica", Font.BOLD, 14);
            g.setColor(Color.white);
            g.setFont(text);
            g.drawString(Score, 225,300);
            Toolkit.getDefaultToolkit().sync();
        } else {
            gameOver(g);
        }
    }

    private void gameOver(Graphics g) {

        String msg = "Game Over";
        String msg2 = "Retry? (Y/N)";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg,(B_WIDTH-metr.stringWidth(msg))/2,B_HEIGHT/2);
        g.drawString(msg2, ((B_WIDTH-metr.stringWidth(msg))/2), (B_HEIGHT/2)+15);

        addKeyListener(new Retry());

    }


    private void checkApple() {
        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            if (specialAp) {
                dots *= 2;
                if (score == 0) {
                    score++;
                }
                score *= 2;
            } else {
                dots++;
                score++;
            }
            specialAp = false;
            locateApple();
        }
    }

    private void move() {
        for (int z = dots; z > 0; z--) {
            x[z] = x[(z-1)];
            y[z] = y[(z-1)];
        }

        if (leftD) {
            x[0] -= DOT_SIZE;
        }
        if(rightD) {
            x[0] += DOT_SIZE;
        }
        if (upD) {
            y[0] -= DOT_SIZE;
        }
        if (downD) {
            y[0] += DOT_SIZE;
        }
        repaint();
    }

    private void checkCollision() {
        for (int z = dots; z > 0; z--) {
            if ((z > 3)&& (x[0]==x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }

        for (int z = 0; z < 20; z++) {
            if ((x[0] == x[100+z] && y[0] == y[100]) || (x[0] == x[20] && y[0] == y[100+z])) {
                inGame = false;
            }
        }



        if (y[0] >= B_HEIGHT || y[0] < 0) {
            inGame = false;
        }
        if (x[0] >= B_WIDTH || x[0] < 0) {
            inGame = false;
        }
        if (!inGame) {
            timer.stop();
        }
    }

    private void locateApple() {
        Random a = new Random();
        int b = a.nextInt(100);
        if (b > 89) {
            specialAp = true;
        }
        int r = (int) (Math.random() * RAND_POS);
        apple_x = ((r * DOT_SIZE));
        r = (int) (Math.random() * RAND_POS);
        apple_y = ((r * DOT_SIZE));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkApple();
            checkCollision();
            move();
        }
        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if((key == KeyEvent.VK_LEFT) && (!rightD)) {
                leftD = true;
                upD = false;
                downD = false;
            }
            if ((key == KeyEvent.VK_RIGHT) && (!leftD)) {
                rightD = true;
                upD = false;
                downD = false;
            }
            if ((key == KeyEvent.VK_UP) && (!downD)) {
                upD = true;
                rightD = false;
                leftD = false;
            }
            if ((key == KeyEvent.VK_DOWN) && (!upD)) {
                downD = true;
                rightD = false;
                leftD = false;
            }
        }
    }

    private class Retry extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_N) {
                System.exit(0);
            } else if (key == KeyEvent.VK_Y) {
                EventQueue.invokeLater(() -> {
                    JFrame ex = new Snake();
                    ex.setVisible(true);
                });
            }
        }
    }
}
