import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class DisplayPanel extends JPanel implements MouseListener, KeyListener, ActionListener {
    private final double DIAG = 1 / Math.sqrt(2);
    private Player player;
    private BufferedImage playerImage;
    private BufferedImage bulletImage;
    private BufferedImage enemyImage;
    private final int WINDOWWIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private final int WINDOWHEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private int borderSize;
    private int reload;
    private double playerSize;
    private double bulletSize;
    private double angleToMouse;
    private Point mousePoint = new Point(0, 0);
    private boolean eWasPressed;
    private boolean autofire;
    private final boolean[] PRESSEDKEYS = new boolean[1024];
    private ArrayList<Enemy> enemies;
    private ArrayList<Bullet> bullets;

    public DisplayPanel() throws IOException {
        playerSize = 0.5;
        bulletSize = playerSize * 1.25;
        playerImage = ImageIO.read(new File("src/Tonk.png"));
        player = new Player(playerImage, playerSize, (int) (WINDOWWIDTH / 2.0 - playerImage.getWidth() / (2 / playerSize) + 20), (int) (WINDOWHEIGHT / 2.0 - playerImage.getHeight() / (2 / playerSize)), 4, 50, 0);
        bulletImage = ImageIO.read(new File("src/Bullet.png"));
        enemyImage = ImageIO.read(new File("src/square.png"));
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        borderSize = 20;
        reload = (int) player.getReload() - 1;
        eWasPressed = false;
        autofire = false;
        Timer timer = new Timer(8, this);
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePoint = e.getPoint();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                mousePoint = e.getPoint();
            }
        });
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WINDOWWIDTH, WINDOWHEIGHT);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(WINDOWWIDTH / 8 - borderSize, WINDOWHEIGHT / 16 - borderSize, WINDOWWIDTH * 3 / 4 + borderSize * 2, WINDOWHEIGHT * 7 / 8 + borderSize * 2);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(WINDOWWIDTH / 8, WINDOWHEIGHT / 16, WINDOWWIDTH * 3 / 4, WINDOWHEIGHT * 7 / 8);
        for (Bullet b : bullets) {
            g.drawImage(b.getImage(), (int) b.getX(), (int) b.getY(), b.getWidth(), b.getHeight(), null);
        }
        for (Enemy e : enemies) {
            g.drawImage(e.getImage(), (int) e.getX(), (int) e.getY(), e.getWidth(), e.getHeight(), null);
        }
        Graphics2D g2d = (Graphics2D) g.create();
        double pxc = player.updatexCenter();
        double pyc = player.updateyCenter();
        g2d.rotate(angleToMouse, pxc, pyc);
        g2d.drawImage(player.getImage(), (int) player.getX(), (int) player.getY(), player.getWidth(), player.getHeight(), null);
        g2d.dispose();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        PRESSEDKEYS[keyCode] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        PRESSEDKEYS[key] = false;
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

    private void movePlayer() {
        double speed = player.getSpeed();
        double diagSpeed = speed * DIAG;

        boolean up = PRESSEDKEYS[KeyEvent.VK_W];
        boolean down = PRESSEDKEYS[KeyEvent.VK_S];
        boolean left = PRESSEDKEYS[KeyEvent.VK_A];
        boolean right = PRESSEDKEYS[KeyEvent.VK_D];

        if (left && player.getX() > WINDOWWIDTH / 8.0) {
            player.setX(player.getX() - (up || down ? diagSpeed : speed));
        }
        if (right && player.getX() < (WINDOWWIDTH * 7) / 8.0 - player.getWidth() * 0.75) {
            player.setX(player.getX() + (up || down ? diagSpeed : speed));
        }
        if (up && player.getY() > WINDOWHEIGHT / 16.0) {
            player.setY(player.getY() - (left || right ? diagSpeed : speed));
        }
        if (down && player.getY() < (WINDOWHEIGHT * 15) / 16.0 - player.getHeight()) {
            player.setY(player.getY() + (left || right ? diagSpeed : speed));
        }
    }

    private void checkAutofire() {
        if (PRESSEDKEYS[KeyEvent.VK_E]) {
            if (!eWasPressed) {
                autofire = !autofire;
            }
            eWasPressed = true;
        } else {
            eWasPressed = false;
        }
    }

    private void shoot() {
        double pxc = player.updatexCenter();
        double pyc = player.updateyCenter();
        angleToMouse = Math.atan2(mousePoint.y - pyc, mousePoint.x - pxc);
        reload++;
        if ((PRESSEDKEYS[KeyEvent.VK_SPACE] || autofire) && reload >= (int) player.getReload()) {
            double random = Math.random() * player.getSpread() - player.getSpread() / 2;
            bullets.add(new Bullet(bulletImage, ((pxc - bulletImage.getWidth() * bulletSize / 2) + (player.getWidth() / 2.0 + bulletImage.getWidth() / 3.0 * bulletSize) * Math.cos(angleToMouse)), ((pyc - bulletImage.getHeight() * bulletSize / 2) + (player.getWidth() / 2.0 + bulletImage.getWidth() / 3.0 * bulletSize) * Math.sin(angleToMouse)), bulletSize, 4, angleToMouse + random));
            enemies.add(new Enemy(enemyImage, 0, 1, 200, 200, 5));
            reload = 0;
        }
    }

    private void moveBullets() {
        Iterator<Bullet> i = bullets.iterator();
        while (i.hasNext()) {
            Bullet b = i.next();
            b.setX(b.getX() + b.getxSpeed());
            b.setY(b.getY() + b.getySpeed());

            if (outOfBounds(b)) {
                i.remove();
            }
        }
    }

    private boolean outOfBounds(Bullet b) {
        double bxc = b.updatexCenter();
        double byc = b.updateyCenter();
        return bxc < 0 || bxc > WINDOWWIDTH || byc < 0 || byc > WINDOWHEIGHT;
    }

    private Rectangle playerRect() {
        return new Rectangle((int) player.getX(), (int) player.getY(), player.getWidth(), player.getHeight());
    }

    private Rectangle bulletRect(Bullet b) {
        return new Rectangle((int) b.getX(), (int) b.getY(), b.getWidth(), b.getHeight());
    }

    private Rectangle enemyRect(Enemy e) {
        return new Rectangle((int) e.getX(), (int) e.getY(), e.getWidth(), e.getHeight());
    }

    private void checkCollision() {
        for (int r = WINDOWWIDTH / 8; r < (WINDOWWIDTH * 7) / 8.0; r += WINDOWWIDTH / 100) {
            for (int c = WINDOWHEIGHT / 16; c < (WINDOWHEIGHT * 15) / 16.0; c += WINDOWHEIGHT / 100) {
//                for (Bullet b : bullets) {
//                    if (bulletRect(b).intersects(r, c, WINDOWWIDTH / 100.0, WINDOWHEIGHT / 100.0)) {
//                        for (Enemy e : enemies) {
//                            if (bulletRect(b).intersects(enemyRect(e))) {
//                                bullets.remove(b);
//                                enemies.remove(e);
//                            }
//                        }
//                    }
//                }
                Iterator<Bullet> i = bullets.iterator();
                while (i.hasNext()) {
                    Bullet b = i.next();
                    if (bulletRect(b).intersects(r, c, WINDOWWIDTH / 100.0, WINDOWHEIGHT / 100.0)) {
                        Iterator<Enemy> i2 = enemies.iterator();
                        while (i2.hasNext()) {
                            Enemy e = i2.next();
                            if ((bulletRect(b).intersects(enemyRect(e)))) {
                                i.remove();
                                i2.remove();
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    //public void spawnEnemies(){
    //  int xPos = (int)(Math.random(WINDOWWIDTH + 1));
    //  int yPos = (int)(Math.random(WINDOWHEIGHT + 1));
    //  double determined spawn based on timer that egg will get from other project = ;
    //  if()
    // }

    @Override
    public void actionPerformed(ActionEvent e) {
        movePlayer();
        checkAutofire();
        shoot();
        moveBullets();
        checkCollision();
        repaint();
    }
}