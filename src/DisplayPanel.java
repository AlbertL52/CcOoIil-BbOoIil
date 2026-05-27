import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DisplayPanel extends JPanel implements MouseListener, KeyListener, ActionListener, MouseMotionListener {
    private BufferedImage player;
    private BufferedImage bullet;
    private int borderSize;
    private double playerX;
    private double playerCenterX;
    private double playerY;
    private double playerCenterY;
    private double playerSpeed;
    private double angleToMouse;
    private Point mousePoint = new Point(0, 0);
    private Timer timer;
    private boolean[] pressedKeys;
    private ArrayList<Point> bullets;


    public DisplayPanel(BufferedImage playerImage) throws IOException {
        player = playerImage;
        bullet = ImageIO.read(new File("src/Bullet.png"));
        bullets = new ArrayList<>();
        borderSize = 20;
        playerSpeed = 5;
        playerX = 200;
        playerY = 300;
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
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        g.fillRect(getWidth() / 8 - borderSize, getHeight() / 16 - borderSize, getWidth() * 3 / 4 + borderSize * 2, getHeight() * 7 / 8 + borderSize * 2);
        g.setColor(Color.GRAY);
        g.fillRect(getWidth() / 8, getHeight() / 16, getWidth() * 3 / 4, getHeight() * 7 / 8);
        for (Point b : bullets) {
            g.drawImage(bullet, b.x, b.y, null);
        }
        Graphics2D g2d = (Graphics2D) g.create();
        playerCenterX = playerX + (double) (player.getWidth() / 2) - 20;
        playerCenterY = playerY + (double) player.getHeight() / 2;
        angleToMouse = Math.atan2(mousePoint.y - playerCenterY, mousePoint.x - playerCenterX);
        g2d.rotate(angleToMouse, playerCenterX, playerCenterY);
        g2d.drawImage(player, (int) playerX, (int) playerY, null);
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
            if (playerX > (double) getWidth() / 8) {
                if (pressedKeys[KeyEvent.VK_W] || pressedKeys[KeyEvent.VK_S]) {
                    playerX -= Math.sqrt(Math.pow(playerSpeed, 2) / 2);
                } else {
                    playerX -= playerSpeed;
                }
            }
        }
        if (pressedKeys[KeyEvent.VK_D]) {
            if (playerX < (double) getWidth() * 7 / 8 + 35 - player.getWidth()) {
                if (pressedKeys[KeyEvent.VK_W] || pressedKeys[KeyEvent.VK_S]) {
                    playerX += Math.sqrt(Math.pow(playerSpeed, 2) / 2);
                } else {
                    playerX += playerSpeed;
                }
            }
        }
        if (pressedKeys[KeyEvent.VK_W]) {
            if (playerY > (double) getHeight() / 16) {
                if (pressedKeys[KeyEvent.VK_A] || pressedKeys[KeyEvent.VK_D]) {
                    playerY -= Math.sqrt(Math.pow(playerSpeed, 2) / 2);
                } else {
                    playerY -= playerSpeed;
                }
            }
        }
        if (pressedKeys[KeyEvent.VK_S]) {
            if (playerY < (double) getHeight() * 15 / 16 - player.getHeight()) {
                if (pressedKeys[KeyEvent.VK_A] || pressedKeys[KeyEvent.VK_D]) {
                    playerY += Math.sqrt(Math.pow(playerSpeed, 2) / 2);
                } else {
                    playerY += playerSpeed;
                }
            }
        }
    }

    public void shoot() {
        if (pressedKeys[KeyEvent.VK_SPACE]) {
            bullets.add(new Point((int) ((playerCenterX - 12) + 80 * Math.cos(angleToMouse)), (int) ((playerCenterY - 12) + 80 * Math.sin(angleToMouse))));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        movePlayer();
        shoot();
        repaint();
    }
}