import javax.sound.sampled.*;
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
    private static final int WINDOW_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), WINDOW_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private static final int BORDER_SIZE = 20;
    private static final double DIAGONAL = 1 / Math.sqrt(2);
    private final BufferedImage playerImage = ImageIO.read(new File("src/Player1.png")), playerImage2 = ImageIO.read(new File("src/Player2.png")), playerImage3 = ImageIO.read(new File("src/Player3.png")), playerImage4 = ImageIO.read(new File("src/Player4.png"));
    private final BufferedImage bulletImage = ImageIO.read(new File("src/Bullet.png"));
    private final ArrayList<Indicator> sIndicators = new ArrayList<>(), gIndicators = new ArrayList<>();
    private final ArrayList<Bullet> bullets = new ArrayList<>();
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Upgrade> upgrades = new ArrayList<>(), commonUpgrades = new ArrayList<>(), rareUpgrades = new ArrayList<>(), epicUpgrades = new ArrayList<>(), legendaryUpgrades = new ArrayList<>();
    private final ArrayList<JButton> upgradeButtons = new ArrayList<>();
    private final RescaleOp whiteFlashOp = new RescaleOp(new float[]{0f, 0f, 0f, 1f}, new float[]{255f, 255f, 255f, 0f}, null);
    private final JButton resetButton = new JButton("Reset");
    private final boolean[] PRESSED_KEYS = new boolean[1024];
    private final Player player = new Player(playerImage, 0.5, (int) (WINDOW_WIDTH / 2.0 - playerImage.getWidth() / 4.0 + 20), (int) (WINDOW_HEIGHT / 2.0 - playerImage.getHeight() / 4.0), 4, 100, 50, 0, 1);
    private int xp = 0, xpReq = 5, score = 0;
    private int reload = (int) player.getReload() - 1;
    private int spawnTime = 0;
    private int bounces = 0, splinters = 0, penetrations = 0, ricochets = 0;
    private double bulletSize = player.getSize() * 1.25, bulletSpeed = 4, damage = 20, knockback = 2;
    private double baseSpawnDelay = 200, baseEnemySize = 1, baseEnemySpeed = 1, baseEnemyHealth = 50, baseEnemyDamage = 10, baseEnemyRandom = 1;
    private double spawnDelay, enemySize, enemySpeed, enemyHealth, enemyDamage, enemyRandom;
    private double angleToMouse;
    private boolean first = true, gameOver = false, eWasPressed = false, autofire = false, paused = false;
    private Point mousePoint = new Point(0, 0);
    private Timer timer = new Timer(8, this);

    public DisplayPanel() throws IOException {
        commonUpgrades.add(new Upgrade(new ImageIcon("src/velocity.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0.000002, 0.0000005, 0, 0, 0, 0, 0, "Velocity:\n+20% Bullet Speed\n+5% Damage"));
        commonUpgrades.add(new Upgrade(new ImageIcon("src/biggerbullets.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0.0000025, 0, 0, 0, 0, 0, 0, 0, "Bigger Bullets:\n+25% Bullet Size"));
        commonUpgrades.add(new Upgrade(new ImageIcon("src/knockback.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, "Knockback:\n+1 Knockback"));
        rareUpgrades.add(new Upgrade(new ImageIcon("src/firerate.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, -0.0000015, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "Firerate:\n-15% Reload"));
        rareUpgrades.add(new Upgrade(new ImageIcon("src/damage.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0000015, 0, 0, 0, 0, 0, "Damage:\n+15% Damage"));
        rareUpgrades.add(new Upgrade(new ImageIcon("src/splinter.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, "Splinter:\n+1 Splinter"));
        epicUpgrades.add(new Upgrade(new ImageIcon("src/spray.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, -0.000003, Math.PI / 8, 0, 0, -0.000001, 0, 0, 0, 0, 0, 0, 0, "Spray:\n-30% Reload\n-10% Bullet Size\n+" + (double) (int) (Math.PI * 25) / 100 + " Spread"));
        epicUpgrades.add(new Upgrade(new ImageIcon("src/bounce.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, "Bounce:\n+1 Bounce"));
        epicUpgrades.add(new Upgrade(new ImageIcon("src/penetration.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.000003, 0, 0, 0, 1, 0, "Penetration:\n+1 Penetration\n-30% Damage"));
        legendaryUpgrades.add(new Upgrade(new ImageIcon("src/ricochet.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, +0.000002, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, "Ricochet:\n+1 Ricochet\n+20% Reload"));
        legendaryUpgrades.add(new Upgrade(new ImageIcon("src/doubletap.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, +0.0000015, Math.PI / 12, 0, 1, -0.000002, 0, -0.000001, 0, 0, 0, 0, 0, "Double Tap:\n+1 Projectile\n+15% Reload\n+" + (double) (int) (Math.PI * 25 / 3) / 100 + " Spread\n-20% Bullet Size\n-10% Damage"));
        legendaryUpgrades.add(new Upgrade(new ImageIcon("src/mastery.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, +0.0000007, +0.0000007, -0.0000007, 0, +0.0000007, 0, +0.0000007, +0.0000007, +0.0000007, 0, 0, 0, 0, 0, "Mastery:\n+7% Max Health\n-7% Reload\n+7% Speed\n+7% Bullet Size\n+7% Bullet Speed\n+7% Damage"));
        add(resetButton);
        resetButton.addActionListener(this);
        resetButton.setVisible(true);
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

        //draw buttons
        resetButton.setLocation(WINDOW_WIDTH / 2 - resetButton.getWidth() / 2, WINDOW_HEIGHT / 128);

        //draw window
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(WINDOW_WIDTH / 8 - BORDER_SIZE, WINDOW_HEIGHT / 16 - BORDER_SIZE, WINDOW_WIDTH * 3 / 4 + BORDER_SIZE * 2, WINDOW_HEIGHT * 7 / 8 + BORDER_SIZE * 2);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(WINDOW_WIDTH / 8, WINDOW_HEIGHT / 16, WINDOW_WIDTH * 3 / 4, WINDOW_HEIGHT * 7 / 8);
        Graphics2D g2d = (Graphics2D) g.create();

        //game ends remove all entities
        if (gameOver) {
            Iterator<Enemy> ei = enemies.iterator();
            Iterator<Bullet> bi = bullets.iterator();
            while (ei.hasNext()) {
                Enemy e = ei.next();
                if (first) {
                    gIndicators.add(new Indicator(e.getImage(), enemySize, e.getX(), e.getY(), e.getAngle(), 30, 1, 0, 2));
                }
                ei.remove();
            }
            while (bi.hasNext()) {
                Bullet b = bi.next();
                if (first) {
                    gIndicators.add(new Indicator(b.getImage(), b.getSize(), b.getX(), b.getY(), 0, 30, 1, 0, 2));
                }
                bi.remove();
            }
            first = false;
            if (gIndicators.isEmpty()) {
                timer.stop();
            }
        }
        else {
            //draw bullets
            for (Bullet b : bullets) {
                g.drawImage(b.getImage(), (int) b.getX(), (int) b.getY(), b.getWidth(), b.getHeight(), null);
            }
            //draw enemies
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
            //draw spawn indicators
            for (Indicator i : sIndicators) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) i.getAlpha()));
                g2d.drawImage(i.getImage(), (int) i.getX(), (int) i.getY(), i.getsWidth(), i.getsHeight(), null);
            }
            //resets image composition, draw player
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
        //draw death animations
        for (Indicator i : gIndicators) {
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
        //draw health bar
        g.setColor(Color.DARK_GRAY);
        g.fillRect(WINDOW_WIDTH / 128 - BORDER_SIZE / 4, WINDOW_HEIGHT / 64 - BORDER_SIZE / 4, WINDOW_WIDTH * 7 / 64 - BORDER_SIZE / 2, WINDOW_HEIGHT / 32 + BORDER_SIZE / 2);
        g.setColor(new Color(255, 0, 0, 200));
        g.fillRect(WINDOW_WIDTH / 128, WINDOW_HEIGHT / 64, (int) ((WINDOW_WIDTH * 7 / 64.0 - BORDER_SIZE) * player.getHealth() / player.getMaxHealth()), WINDOW_HEIGHT / 32);
        g.setColor(new Color(153, 0, 0, 100));
        g.fillRect(WINDOW_WIDTH / 128, WINDOW_HEIGHT / 64, WINDOW_WIDTH * 7 / 64 - BORDER_SIZE, WINDOW_HEIGHT / 32);
        g.setFont(new Font("Arial", Font.PLAIN, WINDOW_HEIGHT / 32));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(new Color(255, 255, 255, 200));
        g.drawString((int) player.getHealth() + "/" + (int) player.getMaxHealth(), WINDOW_WIDTH / 128 + (WINDOW_WIDTH * 7 / 64 - BORDER_SIZE - fm.stringWidth((int) player.getHealth() + "/100")) / 2, WINDOW_HEIGHT / 64 + ((WINDOW_HEIGHT / 32 - fm.getHeight()) / 2) + fm.getAscent());
        //draw xp bar
        g.setColor(Color.DARK_GRAY);
        g.fillRect(WINDOW_WIDTH * 17 / 128 - BORDER_SIZE / 4, WINDOW_HEIGHT * 57 / 64 - BORDER_SIZE / 4, WINDOW_WIDTH * 7 / 64 - BORDER_SIZE / 2, WINDOW_HEIGHT / 32 + BORDER_SIZE / 2);
        g.setColor(new Color(150, 150, 150, 200));
        g.fillRect(WINDOW_WIDTH * 17 / 128, WINDOW_HEIGHT * 57 / 64, (int) (WINDOW_WIDTH * 7 / 64.0 - BORDER_SIZE), WINDOW_HEIGHT / 32);
        g.setColor(new Color(255, 255, 255, 200));
        g.fillRect(WINDOW_WIDTH * 17 / 128, WINDOW_HEIGHT * 57 / 64, (int) ((WINDOW_WIDTH * 7 / 64.0 - BORDER_SIZE) * xp / xpReq), WINDOW_HEIGHT / 32);
        //draw stats
        g.setFont(new Font("Arial", Font.PLAIN, WINDOW_HEIGHT / 48));
        g.drawString("score: " + score, WINDOW_WIDTH / 128, WINDOW_HEIGHT * 3 / 40);
        g.drawString("player width: " + player.getWidth(), WINDOW_WIDTH / 128, WINDOW_HEIGHT * 4 / 40);
        g.drawString("player height: " + player.getHeight(), WINDOW_WIDTH / 128, WINDOW_HEIGHT * 5 / 40);
        g.drawString("player speed: " + player.getRoundedSpeed(), WINDOW_WIDTH / 128, WINDOW_HEIGHT * 6 / 40);
        g.drawString("reload: " + player.getRoundedReload(), WINDOW_WIDTH / 128, WINDOW_HEIGHT * 7 / 40);
        g.drawString("spread (r): " + player.getRoundedSpread(), WINDOW_WIDTH / 128, WINDOW_HEIGHT * 8 / 40);
        g.drawString("projectiles: " + player.getProjectiles(), WINDOW_WIDTH / 128, WINDOW_HEIGHT * 9 / 40);
        g.drawString("bullet size: " + (double) (int) (bulletSize * 100) / 100, WINDOW_WIDTH / 128, WINDOW_HEIGHT * 10 / 40);
        g.drawString("bullet speed: " + (double) (int) (bulletSpeed * 100) / 100, WINDOW_WIDTH / 128, WINDOW_HEIGHT * 11 / 40);
        g.drawString("damage: " + (double) (int) (damage * 100) / 100, WINDOW_WIDTH / 128, WINDOW_HEIGHT * 12 / 40);
        g.drawString("knockback: " + (double) (int) (knockback * 100) / 100, WINDOW_WIDTH / 128, WINDOW_HEIGHT * 13 / 40);
        g.drawString("bounces: " + bounces, WINDOW_WIDTH / 128, WINDOW_HEIGHT * 14 / 40);
        g.drawString("splinters: " + splinters, WINDOW_WIDTH / 128, WINDOW_HEIGHT * 15 / 40);
        g.drawString("penetrations: " + penetrations, WINDOW_WIDTH / 128, WINDOW_HEIGHT * 16 / 40);
        g.drawString("ricochets: " + ricochets, WINDOW_WIDTH / 128, WINDOW_HEIGHT * 17 / 40);

        g.drawString("Enemy (Base)", WINDOW_WIDTH * 447 / 500, WINDOW_HEIGHT / 20);
        g.drawString("spawn delay: " + (int) (spawnDelay * Math.pow(enemyRandom, -0.5)), WINDOW_WIDTH * 447 / 500, WINDOW_HEIGHT * 3 / 40);
        g.drawString("size: " + (double) (int) (enemySize * (enemyRandom / 20 + 1) * 100) / 100, WINDOW_WIDTH * 447 / 500, WINDOW_HEIGHT / 10);
        g.drawString("speed: " + (double) (int) (enemySpeed * (enemyRandom / 20 + 1) * 100) / 100, WINDOW_WIDTH * 447 / 500, WINDOW_HEIGHT / 8);
        g.drawString("health: " + (double) (int) (enemyHealth * (enemyRandom / 20 + 1) * 100) / 100, WINDOW_WIDTH * 447 / 500, WINDOW_HEIGHT * 3 / 20);
        g.drawString("damage: " + (double) (int) (enemyDamage * (enemyRandom / 20 + 1) * 100) / 100, WINDOW_WIDTH * 447 / 500, WINDOW_HEIGHT * 7 / 40);

        //draw upgrade desc
        for (Upgrade u : upgrades) {
            g.setColor(new Color(50, 50, 50, 200));
            g.fillRect((int) u.getX() - u.getWidth() / 2 - BORDER_SIZE, (int) u.getY() - u.getHeight() / 2 - BORDER_SIZE, u.getWidth() + BORDER_SIZE * 2, WINDOW_HEIGHT);
            g.setColor(new Color(100, 100, 100, 200));
            g.fillRect((int) u.getX() - u.getWidth() / 2 - BORDER_SIZE / 2, (int) u.getY() - u.getHeight() / 2 - BORDER_SIZE / 2, u.getWidth() + BORDER_SIZE, WINDOW_HEIGHT);
            g.setColor(new Color(255, 255, 255, 200));
            g.setFont(new Font("Arial", Font.BOLD, WINDOW_HEIGHT / 32));
            fm = g.getFontMetrics();
            for (int i = 0; i < u.getDesc().split("\n").length; i++) {
                if (i == 0) {
                    g.drawString(u.getDesc().split("\n")[i], (int) u.getX() - fm.stringWidth(u.getDesc().split("\n")[i]) / 2, (int) u.getY() + u.getHeight() / 2 + WINDOW_HEIGHT * (i + 1) / 32);
                } else {
                    g.drawString(u.getDesc().split("\n")[i], (int) u.getX() - u.getWidth() / 2, (int) u.getY() + u.getHeight() / 2 + WINDOW_HEIGHT * (i + 1) / 32);
                }
            }
        }
        g2d.dispose();
    }

    //check key triggers
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        PRESSED_KEYS[keyCode] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        PRESSED_KEYS[key] = false;
    }

    //unused methods
    @Override
    public void keyTyped(KeyEvent e) {

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

    //rectangular hitboxes
    private Rectangle playerRect() {
        return new Rectangle((int) player.getX(), (int) player.getY(), player.getWidth(), player.getHeight());
    }

    private Rectangle bulletRect(Bullet b) {
        return new Rectangle((int) b.getX(), (int) b.getY(), b.getWidth(), b.getHeight());
    }

    private Rectangle enemyRect(Enemy e) {
        return new Rectangle((int) e.getX(), (int) e.getY(), e.getWidth(), e.getHeight());
    }

//    public static void playSoundWithPitch(String path, float minPitch, float maxPitch) {
//        try {
//            File audioFile = new File(path);
//            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
//            float pitch = (float) Math.random() * (maxPitch - minPitch) + minPitch;
//            AudioFormat base = audioInputStream.getFormat();
//            AudioFormat pitchedFormat = new AudioFormat(base.getEncoding(), base.getSampleRate() * pitch, base.getSampleSizeInBits(), base.getChannels(), base.getFrameSize(), base.getFrameRate() * pitch, base.isBigEndian());
//            AudioInputStream pitchedStream = AudioSystem.getAudioInputStream(pitchedFormat, audioInputStream);
//            Clip clip = AudioSystem.getClip();
//            clip.open(pitchedStream);
//            clip.start();
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }

    private void movePlayer() {
        player.updateFlash();
        double speed = player.getSpeed();
        double diagSpeed = speed * DIAGONAL;

        boolean up = PRESSED_KEYS[KeyEvent.VK_W];
        boolean down = PRESSED_KEYS[KeyEvent.VK_S];
        boolean left = PRESSED_KEYS[KeyEvent.VK_A];
        boolean right = PRESSED_KEYS[KeyEvent.VK_D];

        if (left && player.getX() > WINDOW_WIDTH / 8.0) {
            player.setX(player.getX() - (up || down ? diagSpeed : speed));
        }
        if (right && player.getX() < (WINDOW_WIDTH * 7) / 8.0 - player.getWidth() * 0.75) {
            player.setX(player.getX() + (up || down ? diagSpeed : speed));
        }
        if (up && player.getY() > WINDOW_HEIGHT / 16.0) {
            player.setY(player.getY() - (left || right ? diagSpeed : speed));
        }
        if (down && player.getY() < (WINDOW_HEIGHT * 15) / 16.0 - player.getHeight()) {
            player.setY(player.getY() + (left || right ? diagSpeed : speed));
        }
    }

    private void checkAutofire() {
        if (PRESSED_KEYS[KeyEvent.VK_E] && !eWasPressed) {
            autofire = !autofire;
        }
        eWasPressed = PRESSED_KEYS[KeyEvent.VK_E];
    }

    private void shoot() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        double pxc = player.updatexCenter();
        double pyc = player.updateyCenter();
        angleToMouse = Math.atan2(mousePoint.y - pyc, mousePoint.x - pxc);
        reload++;
        if ((PRESSED_KEYS[KeyEvent.VK_SPACE] || autofire) && reload >= (int) player.getReload()) {
            for (int i = 0; i < player.getProjectiles(); i++) {
                double random = Math.random() * player.getSpread() - player.getSpread() / 2;
                bullets.add(new Bullet(bulletImage, ((pxc - bulletImage.getWidth() * bulletSize / 2) + (player.getWidth() / 2.0 + bulletImage.getWidth() / 3.0 * bulletSize) * Math.cos(angleToMouse)), ((pyc - bulletImage.getHeight() * bulletSize / 2) + (player.getWidth() / 2.0 + bulletImage.getWidth() / 3.0 * bulletSize) * Math.sin(angleToMouse)), bulletSize, bulletSpeed, angleToMouse + random, damage, knockback, bounces, splinters, penetrations, ricochets));
            }
//            playSoundWithPitch("src/shoot.wav", 0.02f, 0.05f);
            player.setImage(playerImage2);
            reload = 0;
        }
        //firing animation
        if (reload >= player.getReload() * 0.05) {
            player.setImage(playerImage3);
        }
        if (reload >= player.getReload() * 0.1) {
            player.setImage(playerImage4);
        }
        if (reload >= player.getReload() * 0.25) {
            player.setImage(playerImage3);
        }
        if (reload >= player.getReload() * 0.4) {
            player.setImage(playerImage2);
        }
        if (reload >= player.getReload() * 0.55) {
            player.setImage(playerImage);
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
            //update bounces
            if (b.getBounces() > 0) {
                if (bxc < WINDOW_WIDTH / 8.0 || bxc > WINDOW_WIDTH * 7 / 8.0) {
                    b.setxSpeed(-b.getxSpeed());
                    b.setBounces(b.getBounces() - 1);
                } else if (byc <= WINDOW_HEIGHT / 16.0 || byc >= WINDOW_HEIGHT * 15 / 16.0) {
                    b.setySpeed(-b.getySpeed());
                    b.setBounces(b.getBounces() - 1);
                }
            }
            //out of bounds
            if (bxc < 0 || bxc > WINDOW_WIDTH || byc < 0 || byc > WINDOW_HEIGHT) {
                i.remove();
            }
        }
    }

    public void spawnEnemy() throws IOException {
        double sd = spawnDelay * Math.pow(enemyRandom, -Math.random());
        spawnTime++;
        if (spawnTime >= sd) {
            spawnTime = 0;
            BufferedImage image = ImageIO.read(new File("src/Enemy" + 1 + ".png"));
            double x, y;
            do {
                x = Math.random() * (WINDOW_WIDTH * 3 / 4.0 - image.getWidth() * enemySize) + WINDOW_WIDTH / 8.0 - image.getWidth() * enemySize / 4.0;
                y = Math.random() * (WINDOW_HEIGHT * 7 / 8.0 - image.getHeight() * enemySize) + WINDOW_HEIGHT / 16.0 - image.getHeight() * enemySize / 4.0;
            } while (player.getDistance(x + image.getWidth() / 2.0, y + image.getHeight() / 2.0) < 300);
            sIndicators.add(new Indicator(image, enemySize * 2, x, y, 0, 30, 0.1, 5, 0));
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
        ArrayList<Enemy> es = new ArrayList<>();
        ArrayList<Bullet> bs = new ArrayList<>();
        Iterator<Bullet> i = bullets.iterator();
        while (i.hasNext()) {
            Bullet b = i.next();
            Iterator<Enemy> i2 = enemies.iterator();
            while (i2.hasNext()) {
                Enemy e = i2.next();
                if ((bulletRect(b).intersects(enemyRect(e))) && ((e.getkX() == 0 && e.getkY() == 0) || !b.hit())) {
                    if (b.getSplinters() > 0) {
                        es.add(e);
                        for (int s = 0; s < b.getSplinters(); s++) {
                            bs.add(new Bullet(b.getImage(), e.updatexCenter(), e.updateyCenter(), b.getSize() * 0.75, b.getSpeed(), Math.random() * Math.PI * 2, b.getDamage() / 2, b.getKnockback() / 2, b.getBounces(), b.getSplinters(), b.getPenetrations(), b.getRicochets()));
                        }
                    }
                    e.setHealth(e.getHealth() - b.getDamage());
                    if (e.getHealth() <= 0) {
                        gIndicators.add(new Indicator(e.getImage(), enemySize, e.getX(), e.getY(), e.getAngle(), 30, 1, 0, 2));
                        gIndicators.getLast().hitFlash();
                        xp += e.getXp();
                        score += e.getXp();
                        i2.remove();
                    } else {
                        e.hitFlash();
                        double a = Math.atan2(e.updateyCenter() - b.updateyCenter(), e.updatexCenter() - b.updatexCenter());
                        e.addKnockback(b.getKnockback() * Math.cos(a), b.getKnockback() * Math.sin(a));
                    }
                    if (b.getPenetrations() <= 0 && b.getRicochets() <= 0) {
                        i.remove();
                    }
                    if (b.getRicochets() > 0) {
                        double d = Double.MAX_VALUE;
                        double min = d;
                        Enemy ce = null;
                        for (Enemy enemy : enemies) {
                            if (enemy == e) {
                                continue;
                            }
                            d = enemy.getDistance(b.updatexCenter(), b.updateyCenter());
                            if (d < min) {
                                min = d;
                                ce = enemy;
                            }
                        }
                        if (ce != null) {
                            double a = Math.atan2(ce.updateyCenter() - b.updateyCenter(), ce.updatexCenter() - b.updatexCenter());
                            b.setxSpeed(b.getSpeed() * Math.cos(a));
                            b.setySpeed(b.getSpeed() * Math.sin(a));
                            b.setRicochets(b.getRicochets() - 1);
                        }
                    } else {
                        b.setPenetrations(b.getPenetrations() - 1);
                    }
                    b.setHit(true);
                    break;
                }
            }
        }
        //apply splinters
        for (int n = 0; n < bs.size(); n++) {
            while (enemyRect(es.get(n / splinters)).intersects(bs.get(n).getX(), bs.get(n).getY(), bs.get(n).getWidth(), bs.get(n).getHeight())) {
                bs.get(n).setX(bs.get(n).getX() + bs.get(n).getSpeed() * Math.cos(bs.get(n).getAngle()));
                bs.get(n).setY(bs.get(n).getY() + bs.get(n).getSpeed() * Math.sin(bs.get(n).getAngle()));
            }
            bs.get(n).setX(bs.get(n).getX() + bs.get(n).getSpeed() * Math.cos(bs.get(n).getAngle()));
            bs.get(n).setY(bs.get(n).getY() + bs.get(n).getSpeed() * Math.sin(bs.get(n).getAngle()));
            bullets.add(new Bullet(bs.get(n).getImage(), bs.get(n).getX(), bs.get(n).getY(), bs.get(n).getSize(), bs.get(n).getSpeed(), bs.get(n).getAngle(), bs.get(n).getDamage(), bs.get(n).getKnockback(), bs.get(n).getBounces(), 0, bs.get(n).getPenetrations(), bs.get(n).getRicochets()));
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
            while (e.getX() < WINDOW_WIDTH / 8.0) {
                e.setX(e.getX() + e.getWidth() / 4.0);
            }
            while (e.getX() > (WINDOW_WIDTH * 7) / 8.0 - e.getWidth() && e.getX() < 9000) {
                e.setX(e.getX() - e.getWidth() / 4.0);
            }
            while (e.getY() < WINDOW_HEIGHT / 16.0) {
                e.setY(e.getY() + e.getHeight() / 4.0);
            }
            while (e.getY() > (WINDOW_HEIGHT * 15) / 16.0 - e.getHeight() && e.getY() < 9000) {
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
                gIndicators.add(new Indicator(e.getImage(), enemySize, e.getX(), e.getY(), e.getAngle(), 30, 1, 0, 2));
                gIndicators.getLast().hitFlash();
                i.remove();
            }
        }
    }

    private void updateIndicators() throws IOException {
        //spawn indicators
        Iterator<Indicator> it = sIndicators.iterator();
        while (it.hasNext()) {
            Indicator i = it.next();
            if (i.shrink()) {
                enemies.add(new Enemy(1, enemySize * (Math.random() * enemyRandom / 10 + 1), i.getOx() + i.getoWidth() / 4.0, i.getOy() + i.getoHeight() / 4.0, enemySpeed * (Math.random() * enemyRandom / 10 + 1), enemyHealth * (Math.random() * enemyRandom / 10 + 1), enemyDamage * (Math.random() * enemyRandom / 10 + 1)));
                it.remove();
            }
        }
        //death animations
        Iterator<Indicator> it2 = gIndicators.iterator();
        while (it2.hasNext()) {
            Indicator i = it2.next();
            i.updateFlash();
            if (i.grow()) {
                it2.remove();
            }
        }
    }

    private void scale() {
        spawnDelay = baseSpawnDelay * Math.pow(Math.sqrt(score + 1), -0.1);
        enemySize = baseEnemySize + Math.sqrt(score) / 50.0;
        enemyHealth = baseEnemyHealth * (1 + Math.sqrt(score) / 50.0);
        enemySpeed = baseEnemySpeed + Math.sqrt(score) / 100.0;
        enemyDamage = baseEnemyDamage * (1 + Math.sqrt(score) / 50.0);
        enemyRandom = baseEnemyRandom + Math.sqrt(score) / 100.0;
    }

    private void level() {
        if (xp >= xpReq) {
            ArrayList<Upgrade> n = new ArrayList<>();
            Upgrade u;
            paused = true;
            xp = 0;
            xpReq++;
            for (int i = 1; i < 4; i++) {
                do {
                    u = getUpgrade(55, 30, 10);
                } while (n.contains(u));
                n.add(u);
                upgrades.add(new Upgrade(u.getImage(), u.getSize(), u.getX(), u.getY(), u.getPlayerSize(), u.getMaxHealth(), u.getHealth(), u.getReload(), u.getSpread(), u.getSpeed(), u.getProjectiles(), u.getBulletSize(), u.getBulletSpeed(), u.getDamage(), u.getKnockback(), u.getBounces(), u.getSplinters(), u.getPenetrations(), u.getRicochets(), u.getDesc()));
                upgrades.getLast().setX(upgrades.getLast().getX() * i);
                JButton ub = new JButton(new ImageIcon(upgrades.getLast().getImage().getImage().getScaledInstance(upgrades.getLast().getWidth(), upgrades.getLast().getHeight(), Image.SCALE_SMOOTH)));
                ub.setBounds((int) (WINDOW_WIDTH * i / 4.0 - upgrades.getLast().getWidth() / 2.0), (int) (WINDOW_HEIGHT / 4.0 - upgrades.getLast().getHeight() / 2.0), upgrades.getLast().getWidth(), upgrades.getLast().getHeight());
                ub.setFocusable(false);
                upgradeButtons.add(ub);
                add(ub);
                ub.addActionListener(this);
                ub.setVisible(true);
            }
        }
    }

    private Upgrade getUpgrade(double c, double r, double e) {
        double random = Math.random() * 100;
        if (random >= 0 && random < c) {
            return commonUpgrades.get((int) (Math.random() * commonUpgrades.size()));
        } else if (random >= c && random < c + r) {
            return rareUpgrades.get((int) (Math.random() * rareUpgrades.size()));
        } else if (random >= c + r && random < c + r + e) {
            return epicUpgrades.get((int) (Math.random() * epicUpgrades.size()));
        }
        return legendaryUpgrades.get((int) (Math.random() * legendaryUpgrades.size()));
    }

    private void upgrade(Upgrade u) {
        if (u.getPlayerSize() != 0) {
            if (Math.abs(u.getPlayerSize()) < 0.01) {
                player.setSize(player.getSize() + player.getSize() * u.getSize() * 100000);
            } else {
                player.setSize(player.getSize() + u.getSize());
            }
        }
        if (u.getMaxHealth() != 0) {
            if (Math.abs(u.getMaxHealth()) < 0.01) {
                player.setMaxHealth(player.getMaxHealth() + player.getMaxHealth() * u.getMaxHealth() * 100000);
            } else {
                player.setMaxHealth(player.getMaxHealth() + u.getMaxHealth());
            }
        }
        if (u.getHealth() != 0) {
            if (Math.abs(u.getHealth()) < 0.01) {
                player.setHealth(player.getHealth() + player.getHealth() * u.getHealth() * 100000);
            } else {
                player.setHealth(player.getHealth() + u.getHealth());
            }
        }
        if (u.getReload() != 0) {
            if (Math.abs(u.getReload()) < 0.01) {
                player.setReload(player.getReload() + player.getReload() * u.getReload() * 100000);
            } else {
                player.setReload(player.getReload() + u.getReload());
            }
        }
        if (u.getSpread() != 0) {
            if (Math.abs(u.getSpread()) < 0.01) {
                player.setSpread(player.getSpread() + player.getSpread() * u.getSpread() * 100000);
            } else {
                player.setSpread(player.getSpread() + u.getSpread());
            }
        }
        if (u.getSpeed() != 0) {
            if (Math.abs(u.getSpeed()) < 0.01) {
                player.setSpeed(player.getSpeed() + player.getSpeed() * u.getSpeed() * 100000);
            } else {
                player.setSpeed(player.getSpeed() + u.getSpeed());
            }
        }
        if (u.getProjectiles() != 0) {
            if (Math.abs(u.getProjectiles()) < 0.01) {
                player.setProjectiles(player.getProjectiles() + player.getProjectiles() * u.getProjectiles() * 100000);
            } else {
                player.setProjectiles(player.getProjectiles() + u.getProjectiles());
            }
        }
        if (u.getBulletSize() != 0) {
            if (Math.abs(u.getBulletSize()) < 0.01) {
                bulletSize += bulletSize * u.getBulletSize() * 100000;
            } else {
                bulletSize += u.getBulletSize();
            }
        }
        if (u.getBulletSpeed() != 0) {
            if (Math.abs(u.getBulletSpeed()) < 0.01) {
                bulletSpeed += bulletSpeed * u.getBulletSpeed() * 100000;
            } else {
                bulletSpeed += u.getBulletSpeed();
            }
        }
        if (u.getDamage() != 0) {
            if (Math.abs(u.getDamage()) < 0.01) {
                damage += damage * u.getDamage() * 100000;
            } else {
                damage += u.getDamage();
            }
        }
        if (u.getKnockback() != 0) {
            if (Math.abs(u.getKnockback()) < 0.01) {
                knockback += knockback * u.getKnockback() * 100000;
            } else {
                knockback += u.getKnockback();
            }
        }
        if (u.getBounces() != 0) {
            if (Math.abs(u.getBounces()) < 0.01) {
                bounces += bounces * u.getBounces() * 100000;
            } else {
                bounces += u.getBounces();
            }
        }
        if (u.getSplinters() != 0) {
            if (Math.abs(u.getSplinters()) < 0.01) {
                splinters += splinters * u.getSplinters() * 100000;
            } else {
                splinters += u.getSplinters();
            }
        }
        if (u.getPenetrations() != 0) {
            if (Math.abs(u.getPenetrations()) < 0.01) {
                penetrations += penetrations * u.getPenetrations() * 100000;
            } else {
                penetrations += u.getPenetrations();
            }
        }
        if (u.getRicochets() != 0) {
            if (Math.abs(u.getRicochets()) < 0.01) {
                ricochets += ricochets * u.getRicochets() * 100000;
            } else {
                ricochets += u.getRicochets();
            }
        }
        upgrades.remove(u);
        requestFocusInWindow();
        paused = false;
    }

    private void reset() {
        gameOver = false;
        xp = 0;
        xpReq = 5;
        score = 0;
        player.setMaxHealth(100);
        player.setHealth(100);
        player.setSize(0.5);
        player.setReload(50);
        reload = (int) player.getReload() - 1;
        spawnDelay = 200;
        spawnTime = 0;
        bounces = 0;
        splinters = 0;
        penetrations = 0;
        ricochets = 0;
        bulletSize = player.getSize() * 1.25;
        bulletSpeed = 4;
        damage = 20;
        knockback = 2;
        enemySize = 1;
        enemySpeed = 1;
        enemyHealth = 60;
        enemyDamage = 10;
        first = true;
        requestFocusInWindow();
        timer.start();
    }

    //timer ticks
    @Override
    public void actionPerformed(ActionEvent e) {
        if (paused) {
            for (JButton b : upgradeButtons) {
                Iterator<Indicator> it = gIndicators.iterator();
                while (it.hasNext()) {
                    Indicator i = it.next();
                    i.updateFlash();
                    if (i.grow()) {
                        it.remove();
                    }
                }
                if (e.getSource() == b) {
                    upgrade(upgrades.get(upgradeButtons.indexOf(b)));
                    for (JButton ub : upgradeButtons) {
                        remove(ub);
                    }
                    upgradeButtons.clear();
                    upgrades.clear();
                    break;
                }
            }
            repaint();
            return;
        }
        if (e.getSource() == resetButton) {
            reset();
        }
        if ((int) player.getHealth() <= 0) {
            if (!gameOver) {
                gIndicators.add(new Indicator(player.getImage(), player.getSize(), player.getX(), player.getY(), angleToMouse, 30, 1, 0, 2));
            }
            gameOver = true;
        }
        try {
            spawnEnemy();
            updateIndicators();
            shoot();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ex) {
            throw new RuntimeException(ex);
        }
        scale();
        moveEnemy();
        movePlayer();
        checkAutofire();
        moveBullets();
        checkBulletEnemyCollision();
        checkEnemyEnemyCollision();
        checkEnemyBorderCollision();
        checkEnemyPlayerCollision();
        level();
        repaint();
    }
}