import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DisplayPanel extends JPanel implements MouseListener, KeyListener, ActionListener, MouseMotionListener {
    private Player player;
    private BufferedImage playerImage;
    private BufferedImage bulletImage;
    private int windowWidth;
    private int windowHeight;
    private int borderSize;
    private double playerSize;
    private double bulletSize;
    private double angleToMouse;
    private Point mousePoint = new Point(0, 0);
    private Timer timer;
    private boolean[] pressedKeys;
    private ArrayList<Bullet> bullets;


    public DisplayPanel() throws IOException {
        playerSize = 0.5;
        bulletSize = playerSize * 1.25;
        windowWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        windowHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        playerImage = ImageIO.read(new File("src/Tonk.png"));
        player = new Player(playerImage, playerSize, (int) ((double) windowWidth / 2 - playerImage.getWidth() / (2 / playerSize) + 20), (int) ((double) windowHeight / 2 - playerImage.getHeight() / (2 / playerSize)), 5);
        bulletImage = ImageIO.read(new File("src/Bullet.png"));
        bullets = new ArrayList<>();
        borderSize = 20;
        pressedKeys = new boolean[256];
        timer = new Timer(4, this);
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePoint = e.getPoint();
                repaint();
            }
        });
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, windowWidth, windowHeight);
        g.setColor(Color.WHITE);
        g.fillRect(windowWidth / 8 - borderSize, windowHeight / 16 - borderSize, windowWidth * 3 / 4 + borderSize * 2, windowHeight * 7 / 8 + borderSize * 2);
        g.setColor(Color.GRAY);
        g.fillRect(windowWidth / 8, windowHeight / 16, windowWidth * 3 / 4, windowHeight * 7 / 8);
        for (Bullet b : bullets) {
            g.drawImage(b.getImage(), (int) b.getX(), (int) b.getY(), b.getWidth(), b.getHeight(), null);
        }
        Graphics2D g2d = (Graphics2D) g.create();
        angleToMouse = Math.atan2(mousePoint.y - player.updateyCenter(), mousePoint.x - player.updatexCenter());
        g2d.rotate(angleToMouse, player.updatexCenter(), player.updateyCenter());
        g2d.drawImage(player.getImage(), (int) player.getX(), (int) player.getY(), player.getWidth(), player.getHeight(), null);
        g2d.dispose();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        pressedKeys[keyCode] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        pressedKeys[key] = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public void movePlayer() {
        if (pressedKeys[KeyEvent.VK_A]) {
            if (player.getX() > (double) windowWidth / 8) {
                if (pressedKeys[KeyEvent.VK_W] || pressedKeys[KeyEvent.VK_S]) {
                    player.setX(player.getX() - Math.sqrt(Math.pow(player.getSpeed(), 2) / 2));
                } else {
                    player.setX(player.getX() - player.getSpeed());
                }
            }
        }
        if (pressedKeys[KeyEvent.VK_D]) {
            if (player.getX() < (double) windowWidth * 7 / 8 - (double) player.getWidth() * 3 / 4) {
                if (pressedKeys[KeyEvent.VK_W] || pressedKeys[KeyEvent.VK_S]) {
                    player.setX(player.getX() + Math.sqrt(Math.pow(player.getSpeed(), 2) / 2));
                } else {
                    player.setX(player.getX() + player.getSpeed());
                }
            }
        }
        if (pressedKeys[KeyEvent.VK_W]) {
            if (player.getY() > (double) windowHeight / 16) {
                if (pressedKeys[KeyEvent.VK_A] || pressedKeys[KeyEvent.VK_D]) {
                    player.setY(player.getY() - Math.sqrt(Math.pow(player.getSpeed(), 2) / 2));
                } else {
                    player.setY(player.getY() - player.getSpeed());
                }
            }
        }
        if (pressedKeys[KeyEvent.VK_S]) {
            if (player.getY() < (double) windowHeight * 15 / 16 - player.getHeight()) {
                if (pressedKeys[KeyEvent.VK_A] || pressedKeys[KeyEvent.VK_D]) {
                    player.setY(player.getY() + Math.sqrt(Math.pow(player.getSpeed(), 2) / 2));
                } else {
                    player.setY(player.getY() + player.getSpeed());
                }
            }
        }
    }

    public void shoot() {
        if (pressedKeys[KeyEvent.VK_SPACE]) {
            bullets.add(new Bullet(bulletImage, ((player.updatexCenter() - bulletImage.getWidth() * bulletSize / 2) + (double) player.getWidth() / 1.67 * Math.cos(angleToMouse)), ((player.updateyCenter() - bulletImage.getHeight() * bulletSize / 2) + (double) player.getWidth() / 1.67 * Math.sin(angleToMouse)), bulletSize, 5, angleToMouse));
        }
    }

    public void moveBullets() {
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).setX(bullets.get(i).getX() + bullets.get(i).getxSpeed());
            bullets.get(i).setY(bullets.get(i).getY() + bullets.get(i).getySpeed());
            if (bullets.get(i).updatexCenter() < (double) windowWidth / 8 || bullets.get(i).updatexCenter() > (double) windowWidth * 7 / 8 || bullets.get(i).updateyCenter() < (double) windowHeight / 16 || bullets.get(i).updateyCenter() > (double) windowHeight * 15 / 16) {
                bullets.remove(i);
                i--;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        movePlayer();
        shoot();
        moveBullets();
        repaint();
    }
}