import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class DisplayPanel extends JPanel implements MouseListener, KeyListener, ActionListener {
    private Timer timer;
    private final int WINDOWWIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private final int WINDOWHEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private final double DIAG = 1 / Math.sqrt(2);
    private RescaleOp whiteFlashOp;
    private Player player;
    private BufferedImage playerImage, bulletImage;
    private int borderSize;
    private int reload;
    private int spawnRate, spawnTime;
    private double playerSize;
    private double bulletSize, bulletSpeed, bulletKnockback, bulletDamage;
    private double enemySize, enemySpeed, enemyHealth;
    private double angleToMouse;
    private Point mousePoint = new Point(0, 0);
    private boolean first;
    private boolean gameOver;
    private boolean eWasPressed, autofire;
    private final boolean[] PRESSEDKEYS = new boolean[1024];
    private ArrayList<Indicator> sindicators;
    private ArrayList<Indicator> gindicators;
    private ArrayList<Bullet> bullets;
    private ArrayList<Enemy> enemies;

    public DisplayPanel() throws IOException {
        whiteFlashOp = new RescaleOp(new float[]{0f, 0f, 0f, 1f}, new float[]{255f, 255f, 255f, 0f}, null);
        playerImage = ImageIO.read(new File("src/Tonk1.png"));
        playerSize = 0.5;
        player = new Player(playerImage, playerSize, (int) (WINDOWWIDTH / 2.0 - playerImage.getWidth() / (2 / playerSize) + 20), (int) (WINDOWHEIGHT / 2.0 - playerImage.getHeight() / (2 / playerSize)), 4, 100, 50, 0);
        bulletImage = ImageIO.read(new File("src/Bullet.png"));
        borderSize = 20;
        reload = (int) player.getReload() - 1;
        spawnRate = 200;
        spawnTime = 0;
        bulletSize = playerSize * 1.25;
        bulletSpeed = 4;
        bulletDamage = 20;
        bulletKnockback = 2;
        enemySize = 1;
        enemySpeed = 1;
        enemyHealth = 60;
        first = true;
        gameOver = false;
        eWasPressed = false;
        autofire = false;
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        sindicators = new ArrayList<>();
        gindicators = new ArrayList<>();
        timer = new Timer(8, this);
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

        //draw window
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WINDOWWIDTH, WINDOWHEIGHT);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(WINDOWWIDTH / 8 - borderSize, WINDOWHEIGHT / 16 - borderSize, WINDOWWIDTH * 3 / 4 + borderSize * 2, WINDOWHEIGHT * 7 / 8 + borderSize * 2);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(WINDOWWIDTH / 8, WINDOWHEIGHT / 16, WINDOWWIDTH * 3 / 4, WINDOWHEIGHT * 7 / 8);
        Graphics2D g2d = (Graphics2D) g.create();

        //when lose, display lose and kill all bullets/enemies
        if (gameOver) {
            if (!first) {
                g.drawString("Kabloomy", 300, 300);
            }
            Iterator<Enemy> ei = enemies.iterator();
            Iterator<Bullet> bi = bullets.iterator();
            while (ei.hasNext()) {
                Enemy e = ei.next();
                if (first) {
                    gindicators.add(new Indicator(e.getImage(), 1, e.getX(), e.getY(), e.getAngle(), 30, 1, 0, 2));
                }
                ei.remove();
            }
            while (bi.hasNext()) {
                Bullet b = bi.next();
                if (first) {
                    gindicators.add(new Indicator(b.getImage(), bulletSize, b.getX(), b.getY(), 0, 30, 1, 0, 2));
                }
                bi.remove();
            }
            first = false;
            if (gindicators.isEmpty()) {
                timer.stop();
            }
        }
        //when game running
        else {
            //drawing bullets
            for (Bullet b : bullets) {
                g.drawImage(b.getImage(), (int) b.getX(), (int) b.getY(), b.getWidth(), b.getHeight(), null);
            }
            //drawing enemies, rotated towards player
            for (Enemy e : enemies) {
                g2d.rotate(e.getAngle(), e.updatexCenter(), e.updateyCenter());
                g2d.drawImage(e.getImage(), (int) e.getX(), (int) e.getY(), e.getWidth(), e.getHeight(), null);
                if (e.isFlashing()) {
                    Composite old = g2d.getComposite();
                    g2d.setComposite(AlphaComposite.SrcOver.derive(0.4f));
                    g2d.drawImage(whiteFlashOp.filter(e.getImage(), null), (int) e.getX(), (int) e.getY(), e.getWidth(), e.getHeight(), null);
                    g2d.setComposite(old);
                }
                g2d.rotate(-e.getAngle(), e.updatexCenter(), e.updateyCenter());
            }
            //drawing spawn indicators
            for (Indicator i : sindicators) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) i.getAlpha()));
                g2d.drawImage(i.getImage(), (int) i.getX(), (int) i.getY(), i.getsWidth(), i.getsHeight(), null);
            }
            //resets image composition, draws image
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            double pxc = player.updatexCenter();
            double pyc = player.updateyCenter();
            g2d.rotate(angleToMouse, pxc, pyc);
            g2d.drawImage(player.getImage(), (int) player.getX(), (int) player.getY(), player.getWidth(), player.getHeight(), null);
            if (player.isFlashing()) {
                Composite old = g2d.getComposite();
                g2d.setComposite(AlphaComposite.SrcOver.derive(0.4f));
                g2d.drawImage(whiteFlashOp.filter(player.getImage(), null), (int) player.getX(), (int) player.getY(), player.getWidth(), player.getHeight(), null);
                g2d.setComposite(old);
            }
            g2d.rotate(-angleToMouse, pxc, pyc);
        }
        //draws arena
        g.setColor(Color.DARK_GRAY);
        g.fillRect(WINDOWWIDTH / 128 - borderSize / 2, WINDOWHEIGHT / 64 - borderSize / 2, WINDOWWIDTH * 7 / 64, WINDOWHEIGHT / 32 + borderSize);
        g.setColor(new Color(255, 0, 0, 200));
        g.fillRect(WINDOWWIDTH / 128, WINDOWHEIGHT / 64, (int) ((WINDOWWIDTH * 7 / 64.0 - borderSize) * player.getHealth() / player.getMaxHealth()), WINDOWHEIGHT / 32);
        g.setColor(new Color(153, 0, 0, 100));
        g.fillRect(WINDOWWIDTH / 128, WINDOWHEIGHT / 64, WINDOWWIDTH * 7 / 64 - borderSize, WINDOWHEIGHT / 32);
        g.setFont(new Font("Arial", Font.PLAIN, WINDOWHEIGHT / 32));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(new Color(255, 255, 255, 200));
        g.drawString((int) player.getHealth() + "/100", WINDOWWIDTH / 128 + (WINDOWWIDTH * 7 / 64 - borderSize - fm.stringWidth((int) player.getHealth() + "/100")) / 2, WINDOWHEIGHT / 64 + ((WINDOWHEIGHT / 32 - fm.getHeight()) / 2) + fm.getAscent());
        //
        for (Indicator i : gindicators) {
            if (i.getImage().equals(playerImage)) {
                g2d.rotate(i.getAngle(), i.updatepxCenter(), i.updateyCenter());
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) i.getAlpha()));
                g2d.drawImage(i.getImage(), (int) i.getX(), (int) i.getY(), i.getWidth(), i.getHeight(), null);
                if (i.isFlashing()) {
                    Composite old = g2d.getComposite();
                    g2d.setComposite(AlphaComposite.SrcOver.derive(0.4f));
                    g2d.drawImage(whiteFlashOp.filter(i.getImage(), null), (int) i.getX(), (int) i.getY(), i.getWidth(), i.getHeight(), null);
                    g2d.setComposite(old);
                }
                g2d.rotate(-i.getAngle(), i.updatepxCenter(), i.updateyCenter());
            } else {
                g2d.rotate(i.getAngle(), i.updatexCenter(), i.updateyCenter());
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) i.getAlpha()));
                g2d.drawImage(i.getImage(), (int) i.getX(), (int) i.getY(), i.getWidth(), i.getHeight(), null);
                if (i.isFlashing()) {
                    Composite old = g2d.getComposite();
                    g2d.setComposite(AlphaComposite.SrcOver.derive(0.4f));
                    g2d.drawImage(whiteFlashOp.filter(i.getImage(), null), (int) i.getX(), (int) i.getY(), i.getWidth(), i.getHeight(), null);
                    g2d.setComposite(old);
                }
                g2d.rotate(-i.getAngle(), i.updatexCenter(), i.updateyCenter());
            }
        }
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

    private Rectangle playerRect() {
        return new Rectangle((int) player.getX(), (int) player.getY(), player.getWidth(), player.getHeight());
    }

    private Rectangle bulletRect(Bullet b) {
        return new Rectangle((int) b.getX(), (int) b.getY(), b.getWidth(), b.getHeight());
    }

    private Rectangle enemyRect(Enemy e) {
        return new Rectangle((int) e.getX(), (int) e.getY(), e.getWidth(), e.getHeight());
    }

    private void movePlayer() {
        player.updateFlash();
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
        if (PRESSEDKEYS[KeyEvent.VK_E] && !eWasPressed) {
            autofire = !autofire;
        }
        eWasPressed = PRESSEDKEYS[KeyEvent.VK_E];
    }

    private void shoot() throws IOException {
        double pxc = player.updatexCenter();
        double pyc = player.updateyCenter();
        angleToMouse = Math.atan2(mousePoint.y - pyc, mousePoint.x - pxc);
        reload++;
        if ((PRESSEDKEYS[KeyEvent.VK_SPACE] || autofire) && reload >= (int) player.getReload()) {
            double random = Math.random() * player.getSpread() - player.getSpread() / 2;
            bullets.add(new Bullet(bulletImage, ((pxc - bulletImage.getWidth() * bulletSize / 2) + (player.getWidth() / 2.0 + bulletImage.getWidth() / 3.0 * bulletSize) * Math.cos(angleToMouse)), ((pyc - bulletImage.getHeight() * bulletSize / 2) + (player.getWidth() / 2.0 + bulletImage.getWidth() / 3.0 * bulletSize) * Math.sin(angleToMouse)), bulletSize, bulletSpeed, angleToMouse + random, bulletDamage, bulletKnockback));
            player.setImage(ImageIO.read(new File("src/Tonk2.png")));
            reload = 0;
        }
        if (reload >= player.getReload() * 0.05) {
            player.setImage(ImageIO.read(new File("src/Tonk3.png")));
        }
        if (reload >= player.getReload() * 0.1) {
            player.setImage(ImageIO.read(new File("src/Tonk4.png")));
        }
        if (reload >= player.getReload() * 0.25) {
            player.setImage(ImageIO.read(new File("src/Tonk3.png")));
        }
        if (reload >= player.getReload() * 0.4) {
            player.setImage(ImageIO.read(new File("src/Tonk2.png")));
        }
        if (reload >= player.getReload() * 0.55) {
            player.setImage(ImageIO.read(new File("src/Tonk1.png")));
        }
    }

    private void moveBullets() {
        Iterator<Bullet> i = bullets.iterator();
        while (i.hasNext()) {
            Bullet b = i.next();
            b.setX(b.getX() + b.getxSpeed());
            b.setY(b.getY() + b.getySpeed());
            double bxc = b.updatexCenter();
            double byc = b.updateyCenter();
            if (bxc < 0 || bxc > WINDOWWIDTH || byc < 0 || byc > WINDOWHEIGHT) {
                i.remove();
            }
        }
    }

    public void spawnEnemy() throws IOException {
        spawnTime++;
        if (spawnTime == spawnRate) {
            spawnTime = 0;
            BufferedImage image = ImageIO.read(new File("src/Enemy" + 1 + ".png"));
            sindicators.add(new Indicator(image, 2, Math.random() * (WINDOWWIDTH * 3 / 4.0 - image.getWidth()) + WINDOWWIDTH / 8.0 - image.getWidth() / 4.0, Math.random() * (WINDOWHEIGHT * 7 / 8.0 - image.getHeight()) + WINDOWHEIGHT / 16.0 - image.getHeight() / 4.0, 0, 30, 0.1, 5, 0));
        }
    }

    private void moveEnemy() {
        for (Enemy e : enemies) {
            e.updateFlash();
            double angle = Math.atan2(player.updateyCenter() - e.updateyCenter(), player.updatexCenter() - e.updatexCenter());
            e.setAngle(angle);
            e.setX(e.getX() + e.getSpeed() * Math.cos(angle) + e.getkX());
            e.setY(e.getY() + e.getSpeed() * Math.sin(angle) + e.getkY());
            e.dampenKnockback(0.9);
        }
    }

    private void checkBulletEnemyCollision() {
        Iterator<Bullet> i = bullets.iterator();
        while (i.hasNext()) {
            Bullet b = i.next();
            Iterator<Enemy> i2 = enemies.iterator();
            while (i2.hasNext()) {
                Enemy e = i2.next();
                if ((bulletRect(b).intersects(enemyRect(e)))) {
                    i.remove();
                    e.setHealth(e.getHealth() - b.getDamage());
                    if (e.getHealth() <= 0) {
                        gindicators.add(new Indicator(e.getImage(), 1, e.getX(), e.getY(), e.getAngle(), 30, 1, 0, 2));
                        gindicators.getLast().hitFlash();
                        i2.remove();
                    } else {
                        e.hitFlash();
                        double a = Math.atan2(e.updateyCenter() - b.updateyCenter(), e.updatexCenter() - b.updatexCenter());
                        e.addKnockback(b.getKnockback() * Math.cos(a), b.getKnockback() * Math.sin(a));
                    }
                    break;
                }
            }
        }
    }

    private void checkEnemyEnemyCollision() {
        for (int i = 0; i < enemies.size(); i++) {
            for (int j = i + 1; j < enemies.size(); j++) {
                Enemy a = enemies.get(i);
                Enemy b = enemies.get(j);
                while (enemyRect(a).intersects(enemyRect(b))) {
                    a.setX(a.getX() + Math.cos(Math.atan2(a.updateyCenter() - b.updateyCenter(), a.updatexCenter() - b.updatexCenter())));
                    a.setY(a.getY() + Math.sin(Math.atan2(a.updateyCenter() - b.updateyCenter(), a.updatexCenter() - b.updatexCenter())));
                    b.setX(b.getX() + Math.cos(Math.atan2(b.updateyCenter() - a.updateyCenter(), b.updatexCenter() - a.updatexCenter())));
                    b.setY(b.getY() + Math.sin(Math.atan2(b.updateyCenter() - a.updateyCenter(), b.updatexCenter() - a.updatexCenter())));
                }
            }
        }
    }

    private void checkEnemyBorderCollision() {
        for (Enemy e : enemies) {
            while (e.getX() < WINDOWWIDTH / 8.0) {
                e.setX(e.getX() + e.getWidth() / 4.0);
            }
            while (e.getX() > (WINDOWWIDTH * 7) / 8.0 - e.getWidth() && e.getX() < 9000) {
                e.setX(e.getX() - e.getWidth() / 4.0);
            }
            while (e.getY() < WINDOWHEIGHT / 16.0) {
                e.setY(e.getY() + e.getHeight() / 4.0);
            }
            while (e.getY() > (WINDOWHEIGHT * 15) / 16.0 - e.getHeight() && e.getY() < 9000) {
                e.setY(e.getY() - e.getHeight() / 4.0);
            }
        }
    }

    private void checkEnemyPlayerCollision() {
        Iterator<Enemy> i = enemies.iterator();
        while (i.hasNext()) {
            Enemy e = i.next();
            if (enemyRect(e).intersects(playerRect())) {
                player.hitFlash();
                player.setHealth(player.getHealth() - e.getDamage());
                gindicators.add(new Indicator(e.getImage(), 1, e.getX(), e.getY(), e.getAngle(), 30, 1, 0, 2));
                gindicators.getLast().hitFlash();
                i.remove();
            }
        }
    }

    private void updateIndicators() throws IOException {
        Iterator<Indicator> it = sindicators.iterator();
        while (it.hasNext()) {
            Indicator i = it.next();
            if (i.shrink()) {
                enemies.add(new Enemy(1, enemySize, i.getOx() + i.getoWidth() / 4.0, i.getOy() + i.getoHeight() / 4.0, enemySpeed, enemyHealth, 10));
                it.remove();
            }
        }
        Iterator<Indicator> it2 = gindicators.iterator();
        while (it2.hasNext()) {
            Indicator i = it2.next();
            i.updateFlash();
            if (i.grow()) {
                it2.remove();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (player.getHealth() <= 0) {
            if (!gameOver) {
                gindicators.add(new Indicator(player.getImage(), playerSize, player.getX(), player.getY(), angleToMouse, 30, 1, 0, 2));
            }
            gameOver = true;
        }
        try {
            spawnEnemy();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        try {
            updateIndicators();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        moveEnemy();
        movePlayer();
        checkAutofire();
        try {
            shoot();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        moveBullets();
        checkBulletEnemyCollision();
        checkEnemyEnemyCollision();
        checkEnemyBorderCollision();
        checkEnemyPlayerCollision();
        repaint();
    }
}