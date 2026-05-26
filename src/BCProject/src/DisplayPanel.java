import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DisplayPanel extends JPanel implements MouseListener, KeyListener, ActionListener, MouseMotionListener {
    private BufferedImage player;
    private BufferedImage background;
    private int playerX;
    private int playerY;
    private Point mousePoint = new Point(0, 0);
    private Timer timer;
    private boolean[] pressedKeys;


    public DisplayPanel(BufferedImage image) {
        this.player = image;
        playerX = 200;
        playerY = 300;
        pressedKeys = new boolean[256];
        timer = new Timer(1, this);
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
        Graphics2D g2d = (Graphics2D) g.create();
        int playerCenterX = playerX + player.getWidth() / 2;
        int playerCenterY = playerY + player.getHeight() / 2;
        double angle = Math.atan2(mousePoint.y - playerCenterY, mousePoint.x - playerCenterX);
        g2d.rotate(angle, playerCenterX, playerCenterY);
        g2d.drawImage(player, playerX, playerY, null);
        g2d.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        movePlayer();
        repaint();
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

    public void movePlayer(){
        if (pressedKeys[KeyEvent.VK_A]) {
            if (playerX > 0) {
                playerX -= 1;
            }
        }

        // player moves right (D)
        if (pressedKeys[KeyEvent.VK_D]) {
            if (playerX < 960 - player.getWidth()) {
                playerX += 1;
            }
        }

        // player moves up (W)
        if (pressedKeys[KeyEvent.VK_W]) {
            if (playerY > 0) {
                playerY -= 1;
            }
        }

        // player moves down (S)
        if (pressedKeys[KeyEvent.VK_S]) {
            if (playerY < 580 - player.getHeight()) {
                playerY += 1;
            }
        }
    }

}
