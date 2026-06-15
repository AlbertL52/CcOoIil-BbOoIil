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
    private final BufferedImage explosionImage = ImageIO.read(new File("src/Explosion.png"));
    private final BufferedImage enemyImage1 = ImageIO.read(new File("src/Enemy1.png"));
    private final BufferedImage enemyImage2 = ImageIO.read(new File("src/Enemy2.png"));
    private final BufferedImage enemyImage3 = ImageIO.read(new File("src/Enemy3.png"));
    private final BufferedImage enemyImage3Bullet = ImageIO.read(new File("src/Enemy3Bullet.png"));
    private final BufferedImage enemyImage4 = ImageIO.read(new File("src/Enemy4.png"));
    private final BufferedImage enemyImage5 = ImageIO.read(new File("src/Enemy5.png"));
    private final BufferedImage enemyImage5Bullet = ImageIO.read(new File("src/Enemy5Bullet.png"));
    private final BufferedImage enemyImage6 = ImageIO.read(new File("src/Enemy6.png"));
    private final BufferedImage enemyImage7 = ImageIO.read(new File("src/Enemy7.png"));
    private final ArrayList<Integer> count = new ArrayList<>(), counts = new ArrayList<>();
    private final ArrayList<Indicator> sIndicators = new ArrayList<>(), gIndicators = new ArrayList<>();
    private final ArrayList<Bullet> bullets = new ArrayList<>();
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<EnemyBullet> enemyBullets = new ArrayList<>();
    private final ArrayList<Upgrade> upgrades = new ArrayList<>(), commonUpgrades = new ArrayList<>(), rareUpgrades = new ArrayList<>(), epicUpgrades = new ArrayList<>(), legendaryUpgrades = new ArrayList<>();
    private final ArrayList<JButton> upgradeButtons = new ArrayList<>();
    private final RescaleOp whiteFlashOp = new RescaleOp(new float[]{0f, 0f, 0f, 1f}, new float[]{255f, 255f, 255f, 0f}, null);
    private final JButton resetButton = new JButton("Reset");
    private final boolean[] PRESSED_KEYS = new boolean[1024];
    private final Player player = new Player(playerImage, 0.5, (int) (WINDOW_WIDTH / 2.0 - playerImage.getWidth() / 4.0 + 20), (int) (WINDOW_HEIGHT / 2.0 - playerImage.getHeight() / 4.0), 3, 100, 1, 100, 0, 1);
    private double xp = 0, score = 0;
    private int xpReq = 5;
    private double regeneration = (int) player.getRegeneration() - 1;
    private int reload = (int) player.getReload() - 1;
    private ArrayList<Double> spawnTimes = new ArrayList<>(), spawnDelays = new ArrayList<>();
    private double bounces = 0, splinters = 0, penetrations = 0, ricochets = 0, warps = 0;
    private double bulletSize = player.getSize() * 1.25, bulletSpeed = 4, damage = 40, knockback = 2, explosionSize = 0;
    private double baseSpawnDelay = 450, baseEnemySize = 1, baseEnemySpeed = 1, baseEnemyHealth = 60, baseEnemyDamage = 10, baseEnemyRandom = 1;
    private double spawnDelay, enemySize, enemySpeed, enemyHealth, enemyDamage, enemyRandom;
    private double angleToMouse;
    private boolean first = true, gameOver = false, eWasPressed = false, autofire = false, paused = false;
    private Point mousePoint = new Point(0, 0);
    private Timer timer = new Timer(8, this);

    public DisplayPanel() throws IOException {
        for (int i = 0; i < 7; i++) {
            spawnTimes.add(0.0);
            spawnDelays.add(baseSpawnDelay);
            count.add(0);
            counts.add(0);
        }
        commonUpgrades.add(new Upgrade(new ImageIcon("src/velocity.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.000002, 0.0000005, 0, 0, 0, 0, 0, 0, 0, "Velocity:\n+20% Bullet Speed\n+5% Damage"));
        commonUpgrades.add(new Upgrade(new ImageIcon("src/biggerbullets.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0.000003, 0, 0, 0, 0, 0, 0, 0, 0, 0, "Bigger Bullets:\n+30% Bullet Size"));
        commonUpgrades.add(new Upgrade(new ImageIcon("src/knockback.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, "Knockback:\n+2 Knockback"));
        commonUpgrades.add(new Upgrade(new ImageIcon("src/health.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 30, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "Health:\n+30 Max Health"));
        commonUpgrades.add(new Upgrade(new ImageIcon("src/heavybullets.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, -0.000001, 0, 0, 0, 0.000001, 0.000001, 0, 0, 0, 0, 0, 0, "Heavy Bullets:\n+10% Damage\n+10% Knockback\n-10% Speed"));
        commonUpgrades.add(new Upgrade(new ImageIcon("src/lightbullets.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, -0.00000075, 0.00000075, 0, 0, 0, 0.00000075, 0, 0, 0, 0, 0, 0, 0, 0, "Light Bullets:\n+7.5% Speed\n-7.5% Reload\n+7.5% Bullet Speed"));
        commonUpgrades.add(new Upgrade(new ImageIcon("src/sharpen.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0.000003, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, "Sharpen:\n+7 Damage"));
        commonUpgrades.add(new Upgrade(new ImageIcon("src/speed.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "Speed:\n+30% Speed"));
        rareUpgrades.add(new Upgrade(new ImageIcon("src/firerate.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, -0.0000015, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "Firerate:\n-15% Reload"));
        rareUpgrades.add(new Upgrade(new ImageIcon("src/damage.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0000015, 0, 0, 0, 0, 0, 0, 0, "Damage:\n+15% Damage"));
        rareUpgrades.add(new Upgrade(new ImageIcon("src/amplify.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0.2, 0, 0.2, 0.5, 3, 1, 0, 0, 0, 0, 0, 0, "Amplify:\n+0.2 Speed\n+0.2 Bullet Size\n+0.5 Bullet Speed\n+3 Damage\n+1 Knockback"));
        rareUpgrades.add(new Upgrade(new ImageIcon("src/grapple.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, -0.000001, 0, 0, 0, 0, 0, 0.000001, -1, 0, 0, 0, 0, 0, 0, "Grapple:\n-10% Reload\n+10% Damage\n-1 Knockback"));
        rareUpgrades.add(new Upgrade(new ImageIcon("src/magnitude.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0000015, 0, 0.00000075, 0, 0, 0, 0, 0, 0.00000075, 0, "Magnitude:\n+15% Bullet Size\n+7.5% Damage\n+7.5% Explosion Size"));
        rareUpgrades.add(new Upgrade(new ImageIcon("src/precision.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, -0.000001, 0.0000015, 0.00000125, 0, 0, 0, 0, 0, 0, 0, "Precision:\n+15% Bullet Speed\n+12.5% Damage\n-10% Bullet Size"));
        rareUpgrades.add(new Upgrade(new ImageIcon("src/regeneration.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, player.getMaxHealth() / 4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "Regeneration:\n+25% Health\n+1 Regeneration"));
        rareUpgrades.add(new Upgrade(new ImageIcon("src/reroll.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "Reroll:\nReroll current\nupgrade selection"));
        rareUpgrades.add(new Upgrade(new ImageIcon("src/sluggishbullets.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0.000004, -0.000002, 0, 0, 0, 0, 0, 0, 0, 0, "Sluggish Bullets:\n+40% Bullet Size\n-20% Bullet Speed"));
        epicUpgrades.add(new Upgrade(new ImageIcon("src/splinter.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0.000001, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, "Splinter:\n+1 Splinter\n+10% Reload"));
        epicUpgrades.add(new Upgrade(new ImageIcon("src/spray.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, -0.0000025, Math.PI / 8, 0, 0, -0.000001, 0, 0, 0, 0, 0, 0, 0, 0, 0, "Spray:\n-25% Reload\n-10% Bullet Size\n+" + (double) (int) (Math.PI * 25 / 2) / 100 + " Spread"));
        epicUpgrades.add(new Upgrade(new ImageIcon("src/bounce.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, "Bounce:\n+1 Bounce"));
        epicUpgrades.add(new Upgrade(new ImageIcon("src/penetration.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -0.000003, 0, 0, 0, 1, 0, 0, 0, "Penetration:\n+1 Penetration\n-30% Damage"));
        epicUpgrades.add(new Upgrade(new ImageIcon("src/bloodbullets.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, -0.000003, -0.000003, 0, 0, 0, 0, 0, 0, 0, 0.0000025, 0, 0, 0, 0, 0, 0, 0, "Blood Bullets:\n+25% Damage\n-30% Max Health"));
        epicUpgrades.add(new Upgrade(new ImageIcon("src/charge.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, player.getReload() * 0.0000001 / 4, 0, 0, 0, 0, 0, 0, 0, "Charge:\nGain 1% Damage for\nevery 4 Reload\n(+" + (double) (int) (player.getReload() * 25) / 100 + "% Damage)"));
        epicUpgrades.add(new Upgrade(new ImageIcon("src/force.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, knockback * 0.0000006, 0, 0, 0, 0, 0, 0, "Force:\nGain 6% Damage for\nevery 1 Knockback\n(+" + (double) (int) (knockback * 100) / 100 + "% Damage)"));
        epicUpgrades.add(new Upgrade(new ImageIcon("src/gamble.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, (Math.random() * 20 - 5) * 0.0000001, (Math.random() * 20 - 5) * 0.0000001, (Math.random() * 20 - 5) * 0.0000001, (Math.random() * 20 - 5) * 0.0000001, (Math.random() * 20 - 5) * 0.0000001, (Math.random() * 20 - 5) * 0.0000001, (Math.random() * 20 - 5) * 0.0000001, (Math.random() * 20 - 5) * 0.0000001, (Math.random() * 20 - 5) * 0.0000001, (Math.random() * 20 - 5) * 0.0000001, (Math.random() * 20 - 5) * 0.0000001, (Math.random() * 20 - 5) * 0.0000001, (Math.random() * 20 - 5) * 0.0000001, (Math.random() * 20 - 5) * 0.0000001, (Math.random() * 20 - 5) * 0.0000001, (Math.random() * 20 - 5) * 0.0000001, (Math.random() * 20 - 5) * 0.0000001, (Math.random() * 20 - 5) * 0.0000001, "Gamble:\n-5% to +15%\non every stat"));
        epicUpgrades.add(new Upgrade(new ImageIcon("src/inertia.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "Inertia:\n+1 Knockback for\nevery 0.3 Bullet Size\n(+" + (double) (int) (bulletSize / 0.3 * 100) / 100 + " Knockback)"));
        epicUpgrades.add(new Upgrade(new ImageIcon("src/reuse.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0.000005, 0.000005, 0, 0, 0, 0.000003, 0, 0, 0, 0, 0, -0.0000001, -0.0000001, 0, 0, 0, 0, "Reuse:\n+50% Max Health\n+30% Speed\n-10% Splinters\n-10% Bounces"));
        legendaryUpgrades.add(new Upgrade(new ImageIcon("src/ricochet.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0.000002, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, "Ricochet:\n+1 Ricochet\n+20% Reload"));
        legendaryUpgrades.add(new Upgrade(new ImageIcon("src/doubletap.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0.0000015, Math.PI / 12, 0, 1, -0.000002, 0, -0.000001, 0, 0, 0, 0, 0, 0, 0, "Double Tap:\n+1 Projectile\n+15% Reload\n+" + (double) (int) (Math.PI * 25 / 3) / 100 + " Spread\n-20% Bullet Size\n-10% Damage"));
        legendaryUpgrades.add(new Upgrade(new ImageIcon("src/mastery.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0.0000007, 0.0000007, 0, -0.0000007, 0, 0.0000007, 0, 0.0000007, 0.0000007, 0.0000007, 0, 0, 0, 0, 0, 0, 0, "Mastery:\n+7% Max Health\n-7% Reload\n+7% Speed\n+7% Bullet Size\n+7% Bullet Speed\n+7% Damage"));
        legendaryUpgrades.add(new Upgrade(new ImageIcon("src/explosionu.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, "Explosion:\n+1 Explosion Size"));
        legendaryUpgrades.add(new Upgrade(new ImageIcon("src/processor.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0000025, 0, 0, 0.0000025, 0, 0.0000025, 0.0000025, 0, 0, "Processor:\n+25% Bullet Speed\n+25% Bounces\n+25% Penetrations\n+25% Ricochets"));
        legendaryUpgrades.add(new Upgrade(new ImageIcon("src/warp.png"), 0.5, WINDOW_WIDTH / 4.0, WINDOW_HEIGHT / 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, "Warp:\n+1 Warp"));
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
            Iterator<EnemyBullet> ebi = enemyBullets.iterator();
            while (ei.hasNext()) {
                Enemy e = ei.next();
                if (first) {
                    gIndicators.add(new Indicator(e.getImage(), enemySize, e.getX(), e.getY(), e.getAngle(), 1, 30, 1, 0, 2));
                }
                ei.remove();
            }
            while (bi.hasNext()) {
                Bullet b = bi.next();
                if (first) {
                    gIndicators.add(new Indicator(b.getImage(), b.getSize(), b.getX(), b.getY(), 0, 1, 30, 1, 0, 2));
                }
                bi.remove();
            }
            while (ebi.hasNext()) {
                EnemyBullet eb = ebi.next();
                if (first) {
                    gIndicators.add(new Indicator(eb.getImage(), eb.getSize(), eb.getX(), eb.getY(), 0, 1, 30, 1, 0, 2));
                }
                ebi.remove();
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
            //draw enemy bullets
            for (EnemyBullet eb : enemyBullets) {
                g.drawImage(eb.getImage(), (int) eb.getX(), (int) eb.getY(), eb.getWidth(), eb.getHeight(), null);
            }
            //draw spawn indicators
            for (Indicator i : sIndicators) {
                g2d.rotate(i.getAngle(), i.updatexCenter(), i.updateyCenter());
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) i.getAlpha()));
                g2d.drawImage(i.getImage(), (int) i.getX(), (int) i.getY(), i.getsWidth(), i.getsHeight(), null);
                g2d.rotate(-i.getAngle(), i.updatexCenter(), i.updateyCenter());
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
        g.setFont(new Font("Arial", Font.PLAIN, WINDOW_HEIGHT / 50));
        g.drawString("score: " + (double) (int) (score * 100) / 100, WINDOW_WIDTH / 128, WINDOW_HEIGHT * 3 / 40);
        g.drawString("player width: " + player.getWidth(), WINDOW_WIDTH / 128, WINDOW_HEIGHT * 4 / 40);
        g.drawString("player height: " + player.getHeight(), WINDOW_WIDTH / 128, WINDOW_HEIGHT * 5 / 40);
        g.drawString("player speed: " + player.getRoundedSpeed(), WINDOW_WIDTH / 128, WINDOW_HEIGHT * 6 / 40);
        g.drawString("regeneration: " + player.getRoundedRegeneration(), WINDOW_WIDTH / 128, WINDOW_HEIGHT * 7 / 40);
        g.drawString("reload: " + player.getRoundedReload(), WINDOW_WIDTH / 128, WINDOW_HEIGHT * 8 / 40);
        g.drawString("spread (r): " + player.getRoundedSpread(), WINDOW_WIDTH / 128, WINDOW_HEIGHT * 9 / 40);
        g.drawString("projectiles: " + player.getRoundedProjectiles(), WINDOW_WIDTH / 128, WINDOW_HEIGHT * 10 / 40);
        g.drawString("bullet size: " + (double) (int) (bulletSize * 100) / 100, WINDOW_WIDTH / 128, WINDOW_HEIGHT * 11 / 40);
        g.drawString("bullet speed: " + (double) (int) (bulletSpeed * 100) / 100, WINDOW_WIDTH / 128, WINDOW_HEIGHT * 12 / 40);
        g.drawString("damage: " + (double) (int) (damage * 100) / 100, WINDOW_WIDTH / 128, WINDOW_HEIGHT * 13 / 40);
        g.drawString("knockback: " + (double) (int) (knockback * 100) / 100, WINDOW_WIDTH / 128, WINDOW_HEIGHT * 14 / 40);
        g.drawString("bounces: " + (double) (int) (bounces * 100) / 100, WINDOW_WIDTH / 128, WINDOW_HEIGHT * 15 / 40);
        g.drawString("splinters: " + (double) (int) (splinters * 100) / 100, WINDOW_WIDTH / 128, WINDOW_HEIGHT * 16 / 40);
        g.drawString("penetrations: " + (double) (int) (penetrations * 100) / 100, WINDOW_WIDTH / 128, WINDOW_HEIGHT * 17 / 40);
        g.drawString("ricochets: " + (double) (int) (ricochets * 100) / 100, WINDOW_WIDTH / 128, WINDOW_HEIGHT * 18 / 40);
        g.drawString("explosion: " + (double) (int) (explosionSize * 100) / 100, WINDOW_WIDTH / 128, WINDOW_HEIGHT * 19 / 40);
        g.drawString("warps: " + (double) (int) (warps * 100) / 100, WINDOW_WIDTH / 128, WINDOW_HEIGHT * 20 / 40);

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
            for (Upgrade c : commonUpgrades) {
                if (c.getImage() == u.getImage()) {
                    g.setColor(new Color(255, 255, 255));
                    break;
                }
            }
            for (Upgrade r : rareUpgrades) {
                if (r.getImage() == u.getImage()) {
                    g.setColor(new Color(0, 160, 255));
                    break;
                }
            }
            for (Upgrade e : epicUpgrades) {
                if (e.getImage() == u.getImage()) {
                    g.setColor(new Color(225, 0, 255));
                    break;
                }
            }
            for (Upgrade l : legendaryUpgrades) {
                if (l.getImage() == u.getImage()) {
                    g.setColor(new Color(255, 255, 0));
                    break;
                }
            }
            g.setFont(new Font("Arial", Font.BOLD, WINDOW_HEIGHT / 40));
            fm = g.getFontMetrics();
            for (int i = 0; i < u.getDesc().split("\n").length; i++) {
                if (i == 0) {
                    g.drawString(u.getDesc().split("\n")[i], (int) u.getX() - fm.stringWidth(u.getDesc().split("\n")[i]) / 2, (int) u.getY() + u.getHeight() / 2 + WINDOW_HEIGHT * (i + 1) / 40);
                } else {
                    g.drawString(u.getDesc().split("\n")[i], (int) u.getX() - u.getWidth() / 2, (int) u.getY() + u.getHeight() / 2 + WINDOW_HEIGHT * (i + 1) / 40);
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
        if (e.getKeyCode() == KeyEvent.VK_0) {
            for (int i = 0; i < 10; i++) {
                double x, y;
                do {
                    x = Math.random() * (WINDOW_WIDTH * 3 / 4.0 - enemyImage1.getWidth() * enemySize) + WINDOW_WIDTH / 8.0 - enemyImage1.getWidth() * enemySize / 4.0;
                    y = Math.random() * (WINDOW_HEIGHT * 7 / 8.0 - enemyImage1.getHeight() * enemySize) + WINDOW_HEIGHT / 16.0 - enemyImage1.getHeight() * enemySize / 4.0;
                } while (player.getDistance(x + enemyImage1.getWidth() / 2.0, y + enemyImage1.getHeight() / 2.0) < 300);
                sIndicators.add(new Indicator(enemyImage3, enemySize * 2, x, y, 0, 1, 30, 0.1, 5, 0));
            }
        }
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

    //hitboxes
    private boolean pointToPointCollision(Point p1, double d1, Point p2, double d2) {
        double dx = p1.x - p2.x;
        double dy = p1.y - p2.y;
        double d = d1 + d2;
        return (dx * dx) + (dy * dy) < (d * d);
    }

    private boolean pointToRectCollision(Point p, double d, Rectangle r) {
        double dx = p.x - Math.max(r.x, Math.min(p.x, r.x + r.width));
        double dy = p.y - Math.max(r.y, Math.min(p.y, r.y + r.height));
        return (dx * dx) + (dy * dy) < (d * d);
    }

    private boolean bulletEnemyCollision(Bullet b, Enemy e) {
        if (e.isCircle()) {
            return pointToPointCollision(new Point((int) b.updatexCenter(), (int) b.updateyCenter()), b.getWidth() / 2.0, new Point((int) e.updatexCenter(), (int) e.updateyCenter()), e.getWidth() / 2.5);
        } else {
            return pointToRectCollision(new Point((int) b.updatexCenter(), (int) b.updateyCenter()), b.getWidth() / 2.0, e.getRect());
        }
    }

    private boolean enemyEnemyCollision(Enemy e1, Enemy e2) {
        if (e1.isCircle() && e2.isCircle()) {
            return pointToPointCollision(new Point((int) e1.updatexCenter(), (int) e1.updateyCenter()), e1.getWidth() / 2.5, new Point((int) e2.updatexCenter(), (int) e2.updateyCenter()), e2.getWidth() / 2.5);
        } else if (e1.isCircle()) {
            return pointToRectCollision(new Point((int) e1.updatexCenter(), (int) e1.updateyCenter()), e1.getWidth() / 2.5, e2.getRect());
        } else if (e2.isCircle()) {
            return pointToRectCollision(new Point((int) e2.updatexCenter(), (int) e2.updateyCenter()), e2.getWidth() / 2.5, e1.getRect());
        } else {
            return e1.getRect().intersects(e2.getRect());
        }
    }

    private boolean enemyPlayerCollision(Enemy e) {
        if (e.isCircle()) {
            return pointToRectCollision(new Point((int) e.updatexCenter(), (int) e.updateyCenter()), e.getWidth() / 2.5, player.getRect());
        } else {
            return player.getRect().intersects(e.getRect());
        }
    }

    //player
    private void regenerate() {
        regeneration++;
        if (regeneration >= 500 / player.getRegeneration() && player.getHealth() < player.getMaxHealth()) {
            player.setHealth(player.getHealth() + 1);
            regeneration = 0;
        }
    }

    private void movePlayer() {
        player.updateFlash();
        double speed = player.getSpeed();
        double diagonalSpeed = speed * DIAGONAL;

        boolean up = PRESSED_KEYS[KeyEvent.VK_W];
        boolean down = PRESSED_KEYS[KeyEvent.VK_S];
        boolean left = PRESSED_KEYS[KeyEvent.VK_A];
        boolean right = PRESSED_KEYS[KeyEvent.VK_D];

        if (left && player.getX() > WINDOW_WIDTH / 8.0) {
            player.setX(player.getX() - (up || down ? diagonalSpeed : speed));
        }
        if (right && player.getX() < (WINDOW_WIDTH * 7) / 8.0 - player.getWidth() * 0.75) {
            player.setX(player.getX() + (up || down ? diagonalSpeed : speed));
        }
        if (up && player.getY() > WINDOW_HEIGHT / 16.0) {
            player.setY(player.getY() - (left || right ? diagonalSpeed : speed));
        }
        if (down && player.getY() < (WINDOW_HEIGHT * 15) / 16.0 - player.getHeight()) {
            player.setY(player.getY() + (left || right ? diagonalSpeed : speed));
        }
    }

    private void checkAutofire() {
        if (PRESSED_KEYS[KeyEvent.VK_E] && !eWasPressed) {
            autofire = !autofire;
        }
        eWasPressed = PRESSED_KEYS[KeyEvent.VK_E];
    }

    private void shoot() throws IOException {
        double pxc = player.updatexCenter();
        double pyc = player.updateyCenter();
        angleToMouse = Math.atan2(mousePoint.y - pyc, mousePoint.x - pxc);
        reload++;
        if ((PRESSED_KEYS[KeyEvent.VK_SPACE] || autofire) && reload >= (int) player.getReload()) {
            for (int i = 0; i < (int) player.getProjectiles(); i++) {
                double random = Math.random() * player.getSpread() - player.getSpread() / 2;
                bullets.add(new Bullet(bulletImage, ((pxc - bulletImage.getWidth() * bulletSize / 2) + (player.getWidth() / 2.0 + bulletImage.getWidth() / 3.0 * bulletSize) * Math.cos(angleToMouse)), ((pyc - bulletImage.getHeight() * bulletSize / 2) + (player.getWidth() / 2.0 + bulletImage.getWidth() / 3.0 * bulletSize) * Math.sin(angleToMouse)), bulletSize, bulletSpeed, angleToMouse + random, damage, knockback, bounces, splinters, penetrations, ricochets, explosionSize, warps));
            }
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

    //bullets
    private void moveBullets() {
        Iterator<Bullet> i = bullets.iterator();
        while (i.hasNext()) {
            Bullet b = i.next();
            b.setX(b.getX() + b.getxSpeed());
            b.setY(b.getY() + b.getySpeed());
            double bxc = b.updatexCenter();
            double byc = b.updateyCenter();
            //update bounces
            if ((int) b.getWarps() > 0) {
                if (bxc <= WINDOW_WIDTH / 8.0) {
                    b.setX(b.getX() + WINDOW_WIDTH * 3 / 4.0);
                    b.setWarps(b.getWarps() - 1);
                } else if (bxc >= WINDOW_WIDTH * 7 / 8.0) {
                    b.setX(b.getX() - WINDOW_WIDTH * 3 / 4.0);
                    b.setWarps(b.getWarps() - 1);
                } else if (byc <= WINDOW_HEIGHT / 16.0) {
                    b.setY(b.getY() + WINDOW_HEIGHT * 7 / 8.0);
                    b.setWarps(b.getWarps() - 1);
                } else if (byc >= WINDOW_HEIGHT * 15 / 16.0) {
                    b.setY(b.getY() - WINDOW_HEIGHT * 7 / 8.0);
                    b.setWarps(b.getWarps() - 1);
                }
            }
            if ((int) b.getBounces() > 0 && b.getWarps() == 0) {
                if (bxc <= WINDOW_WIDTH / 8.0 || bxc >= WINDOW_WIDTH * 7 / 8.0) {
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

    //enemies
    public void spawnEnemy() {
        spawnTimes.set(0, spawnTimes.getFirst() + 1);
        if (spawnTimes.getFirst() >= spawnDelays.getFirst()) {
            spawnTimes.set(0, 0.0);
            spawnDelays.set(0, spawnDelay * Math.pow(enemyRandom, -Math.random()));
            double x, y;
            do {
                x = Math.random() * (WINDOW_WIDTH * 3 / 4.0 - enemyImage1.getWidth() * enemySize) + WINDOW_WIDTH / 8.0 - enemyImage1.getWidth() * enemySize / 4.0;
                y = Math.random() * (WINDOW_HEIGHT * 7 / 8.0 - enemyImage1.getHeight() * enemySize) + WINDOW_HEIGHT / 16.0 - enemyImage1.getHeight() * enemySize / 4.0;
            } while (player.getDistance(x + enemyImage1.getWidth() / 2.0, y + enemyImage1.getHeight() / 2.0) < 300);
            sIndicators.add(new Indicator(enemyImage1, enemySize * 2, x, y, Math.random() * Math.PI * 2, 1, 30, 0.1, 5, 0));
        }
        if (count.get(1) == 0) {
            count.set(1, (int) (Math.random() * 4 + 1));
        }
        spawnTimes.set(1, spawnTimes.get(1) + 1);
        if (spawnTimes.get(1) >= spawnDelays.get(1)) {
            counts.set(1, counts.get(1) + 1);
            spawnTimes.set(1, 0.0);
            spawnDelays.set(1, 3 * spawnDelay * Math.pow(enemyRandom, -Math.random()));
            double x, y;
            do {
                x = Math.random() * (WINDOW_WIDTH * 3 / 4.0 - enemyImage2.getWidth() * enemySize / 3) + WINDOW_WIDTH / 8.0 - enemyImage2.getWidth() * enemySize / 12.0;
                y = Math.random() * (WINDOW_HEIGHT * 7 / 8.0 - enemyImage2.getHeight() * enemySize / 3) + WINDOW_HEIGHT / 16.0 - enemyImage2.getHeight() * enemySize / 12.0;
            } while (player.getDistance(x + enemyImage2.getWidth() / 2.0, y + enemyImage2.getHeight() / 2.0) < 300);
            sIndicators.add(new Indicator(enemyImage2, enemySize * 2 / 3, x, y, Math.random() * Math.PI * 2, 1, 30, 0.1, 5, 0));
            if (counts.get(1) >= count.get(1) && score > 81) {
                for (int i = 0; i < Math.random() * 2 + enemyRandom; i++) {
                    do {
                        x = Math.random() * (WINDOW_WIDTH * 3 / 4.0 - enemyImage2.getWidth() * enemySize / 3) + WINDOW_WIDTH / 8.0 - enemyImage2.getWidth() * enemySize / 12.0;
                        y = Math.random() * (WINDOW_HEIGHT * 7 / 8.0 - enemyImage2.getHeight() * enemySize / 3) + WINDOW_HEIGHT / 16.0 - enemyImage2.getHeight() * enemySize / 12.0;
                    } while (player.getDistance(x + enemyImage2.getWidth() / 2.0, y + enemyImage2.getHeight() / 2.0) < 300);
                    sIndicators.add(new Indicator(enemyImage2, enemySize * 2 / 3, x, y, Math.random() * Math.PI * 2, 1, 30, 0.1, 5, 0));
                }
                counts.set(1, 0);
                count.set(1, (int) (Math.random() * 4 + 1));
                spawnDelays.set(2, 6 * spawnDelay * Math.pow(enemyRandom, -Math.random()));
            }
        }
        if (score > 26) {
            if (count.get(2) == 0) {
                count.set(2, (int) (Math.random() * 3 + 3));
            }
            spawnTimes.set(2, spawnTimes.get(2) + 1);
            if (spawnTimes.get(2) >= spawnDelays.get(2)) {
                counts.set(2, counts.get(2) + 1);
                spawnTimes.set(2, 0.0);
                spawnDelays.set(2, 10 * spawnDelay * Math.pow(enemyRandom, -Math.random()));
                double x, y;
                do {
                    x = Math.random() * (WINDOW_WIDTH * 3 / 4.0 - enemyImage3.getWidth() * enemySize / 3) + WINDOW_WIDTH / 8.0 - enemyImage3.getWidth() * enemySize / 12.0;
                    y = Math.random() * (WINDOW_HEIGHT * 7 / 8.0 - enemyImage3.getHeight() * enemySize / 3) + WINDOW_HEIGHT / 16.0 - enemyImage3.getHeight() * enemySize / 12.0;
                } while (player.getDistance(x + enemyImage3.getWidth() / 2.0, y + enemyImage3.getHeight() / 2.0) < 300);
                sIndicators.add(new Indicator(enemyImage3, enemySize * 2 / 3, x, y, Math.random() * Math.PI * 2, 1, 30, 0.1, 5, 0));
                if (counts.get(2) >= count.get(2) && score > 143) {
                    for (int i = 0; i < Math.random() * 3 + enemyRandom; i++) {
                        Indicator temp = sIndicators.getLast();
                        do {
                            x = Math.random() * (WINDOW_WIDTH * 3 / 4.0 - enemyImage3.getWidth() * enemySize / 3) + WINDOW_WIDTH / 8.0 - enemyImage3.getWidth() * enemySize / 12.0;
                            y = Math.random() * (WINDOW_HEIGHT * 7 / 8.0 - enemyImage3.getHeight() * enemySize / 3) + WINDOW_HEIGHT / 16.0 - enemyImage3.getHeight() * enemySize / 12.0;
                        } while (player.getDistance(x + enemyImage3.getWidth() / 2.0, y + enemyImage3.getHeight() / 2.0) < 300 || temp.getDistance(x + enemyImage3.getWidth() / 2.0, y + enemyImage3.getHeight() / 2.0) > 100);
                        sIndicators.add(new Indicator(enemyImage3, enemySize * 2 / 3, x, y, Math.random() * Math.PI * 2, 1, 30, 0.1, 5, 0));
                    }
                    counts.set(2, 0);
                    count.set(2, (int) (Math.random() * 3 + 3));
                    spawnDelays.set(2, 20 * spawnDelay * Math.pow(enemyRandom, -Math.random()));
                }
            }
        }
        if (score > 11) {
            spawnTimes.set(3, spawnTimes.get(3) + 1);
            if (spawnTimes.get(3) >= spawnDelays.get(3)) {
                spawnTimes.set(3, 0.0);
                spawnDelays.set(3, 25 * spawnDelay * Math.pow(enemyRandom, -Math.random()));
                double x, y;
                do {
                    x = Math.random() * (WINDOW_WIDTH * 3 / 4.0 - enemyImage4.getWidth() * enemySize / 2) + WINDOW_WIDTH / 8.0 - enemyImage4.getWidth() * enemySize / 8.0;
                    y = Math.random() * (WINDOW_HEIGHT * 7 / 8.0 - enemyImage4.getHeight() * enemySize / 2) + WINDOW_HEIGHT / 16.0 - enemyImage4.getHeight() * enemySize / 8.0;
                } while (player.getDistance(x + enemyImage4.getWidth() / 2.0, y + enemyImage4.getHeight() / 2.0) < 300);
                sIndicators.add(new Indicator(enemyImage4, enemySize, x, y, Math.random() * Math.PI * 2, 1, 30, 0.1, 5, 0));
            }
        }
        if (score > 95) {
            spawnTimes.set(4, spawnTimes.get(4) + 1);
            if (spawnTimes.get(4) >= spawnDelays.get(4)) {
                spawnTimes.set(4, 0.0);
                spawnDelays.set(4, 11 * spawnDelay * Math.pow(enemyRandom * 2, -Math.random()));
                double x, y;
                do {
                    x = Math.random() * (WINDOW_WIDTH * 3 / 4.0 - enemyImage5.getWidth() * enemySize / 3) + WINDOW_WIDTH / 8.0 - enemyImage5.getWidth() * enemySize / 12.0;
                    y = Math.random() * (WINDOW_HEIGHT * 7 / 8.0 - enemyImage5.getHeight() * enemySize / 3) + WINDOW_HEIGHT / 16.0 - enemyImage5.getHeight() * enemySize / 12.0;
                } while (player.getDistance(x + enemyImage5.getWidth() / 2.0, y + enemyImage5.getHeight() / 2.0) < 300);
                sIndicators.add(new Indicator(enemyImage5, enemySize * 2 / 3, x, y, Math.random() * Math.PI * 2, 1, 30, 0.1, 5, 0));
            }
        }
        if (score > 2) {
            spawnTimes.set(5, spawnTimes.get(5) + 1);
            if (spawnTimes.get(5) >= spawnDelays.get(5)) {
                spawnTimes.set(5, 0.0);
                spawnDelays.set(5, 7 * baseSpawnDelay * Math.pow(enemyRandom * 2, -Math.random()));
                double x, y;
                for (int i = 1; i < enemyRandom; i++) {
                    do {
                        x = Math.random() * (WINDOW_WIDTH * 3 / 4.0 - enemyImage6.getWidth() * enemySize / 2) + WINDOW_WIDTH / 8.0 - enemyImage6.getWidth() * enemySize / 8.0;
                        y = Math.random() * (WINDOW_HEIGHT * 7 / 8.0 - enemyImage6.getHeight() * enemySize / 2) + WINDOW_HEIGHT / 16.0 - enemyImage6.getHeight() * enemySize / 8.0;
                    } while (player.getDistance(x + enemyImage6.getWidth() / 2.0, y + enemyImage6.getHeight() / 2.0) < 300);
                    sIndicators.add(new Indicator(enemyImage6, enemySize, x, y, Math.random() * Math.PI * 2, 1, 30, 0.1, 5, 0));
                }
            }
        }
        if (score > 200) {
            spawnTimes.set(6, spawnTimes.get(6) + 1);
            if (spawnTimes.get(6) >= spawnDelays.get(6)) {
                spawnTimes.set(6, 0.0);
                spawnDelays.set(6, 20 * spawnDelay * Math.pow(enemyRandom * 5, -Math.random()));
                double x, y;
                do {
                    x = Math.random() * (WINDOW_WIDTH * 3 / 4.0 - enemyImage7.getWidth() * enemySize / 2) + WINDOW_WIDTH / 8.0 - enemyImage7.getWidth() * enemySize / 8.0;
                    y = Math.random() * (WINDOW_HEIGHT * 7 / 8.0 - enemyImage7.getHeight() * enemySize / 2) + WINDOW_HEIGHT / 16.0 - enemyImage7.getHeight() * enemySize / 8.0;
                } while (player.getDistance(x + enemyImage7.getWidth() / 2.0, y + enemyImage7.getHeight() / 2.0) < 300);
                sIndicators.add(new Indicator(enemyImage7, enemySize, x, y, Math.random() * Math.PI * 2, 1, 30, 0.1, 5, 0));
            }
        }
    }

    private void moveEnemy() {
        for (Enemy e : enemies) {
            e.updateFlash();
            e.dampenKnockback();
            double angle = Math.atan2(player.updateyCenter() - e.updateyCenter(), player.updatexCenter() - e.updatexCenter());
            if (e.getImage() == enemyImage5 || e.getImage() == enemyImage6) {
                e.setX(e.getX() + e.getSpeed() * Math.cos(e.getAngle()) + e.getkX());
                e.setY(e.getY() + e.getSpeed() * Math.sin(e.getAngle()) + e.getkY());
            } else if (e.getImage() == enemyImage7) {
                e.setCounter(e.getCounter() + 1);
                if (e.getCounter() >= e.getCount()) {
                    if (player.getDistance(e.updatexCenter(), e.updateyCenter()) < 300) {
                        e.setOne(true);
                    }
                    if (player.getDistance(e.updatexCenter(), e.updateyCenter()) >= 300 && e.isOne()) {
                        e.setOne(false);
                        e.setCounter(0);
                    }
                    e.setX(e.getX() + e.getSpeed() * Math.cos(e.getAngle()) + e.getkX());
                    e.setY(e.getY() + e.getSpeed() * Math.sin(e.getAngle()) + e.getkY());
                } else {
                    e.setAngle(angle);
                }
            } else {
                e.setAngle(angle);
                if (e.getImage() == enemyImage3 && player.getDistance(e.updatexCenter(), e.updateyCenter()) < 300) {
                    e.setX(e.getX() + e.getkX());
                    e.setY(e.getY() + e.getkY());
                    if (player.getDistance(e.updatexCenter(), e.updateyCenter()) < 200) {
                        e.setX(e.getX() - e.getSpeed() * Math.cos(angle));
                        e.setY(e.getY() - e.getSpeed() * Math.sin(angle));
                    }
                } else {
                    e.setX(e.getX() + e.getSpeed() * Math.cos(angle) + e.getkX());
                    e.setY(e.getY() + e.getSpeed() * Math.sin(angle) + e.getkY());
                }
            }
        }
    }

    private void checkBulletEnemyCollision() throws IOException {
        ArrayList<Enemy> es = new ArrayList<>(), re = new ArrayList<>();
        ArrayList<Bullet> bs = new ArrayList<>();
        Iterator<Bullet> i = bullets.iterator();
        while (i.hasNext()) {
            Bullet b = i.next();
            Iterator<Enemy> i2 = enemies.iterator();
            while (i2.hasNext()) {
                Enemy e = i2.next();
                if (bulletEnemyCollision(b, e) && ((e.getkX() == 0 && e.getkY() == 0) || !b.hit())) {
                    if ((int) b.getSplinters() > 0) {
                        es.add(e);
                        for (int s = 0; s < (int) b.getSplinters(); s++) {
                            bs.add(new Bullet(b.getImage(), e.updatexCenter(), e.updateyCenter(), b.getSize() * 0.75, b.getSpeed(), Math.random() * Math.PI * 2, b.getDamage() / 2, b.getKnockback() / 2, b.getBounces(), b.getSplinters(), b.getPenetrations(), b.getRicochets(), b.getExplosionSize(), b.getWarps()));
                        }
                    }
                    if (b.getExplosionSize() > 0) {
                        gIndicators.add(new Indicator(explosionImage, b.getExplosionSize(), b.updatexCenter() - explosionImage.getWidth() * b.getExplosionSize() / 4, b.updateyCenter() - explosionImage.getHeight() * b.getExplosionSize() / 4, b.getAngle(), 0.5, 15, 1, 0, 2));
                        for (Enemy enemy : enemies) {
                            int temp = b.getWidth();
                            b.setWidth((int) (explosionImage.getWidth() * b.getExplosionSize() / 1.75));
                            b.setX(b.getX() - b.getWidth() / 2.0);
                            if (bulletEnemyCollision(b, enemy)) {
                                enemy.setHealth(enemy.getHealth() - b.getDamage());
                                enemy.hitFlash();
                                double a = Math.atan2(enemy.updateyCenter() - b.updateyCenter(), enemy.updatexCenter() - b.updatexCenter());
                                enemy.addKnockback(b.getKnockback() * Math.cos(a), b.getKnockback() * Math.sin(a));
                                if (enemy.getHealth() <= 0) {
                                    re.add(enemy);
                                }
                            }
                            b.setX(b.getX() + b.getWidth() / 2.0);
                            b.setWidth(temp);
                        }
                    } else {
                        e.setHealth(e.getHealth() - b.getDamage());
                        e.hitFlash();
                        double a = Math.atan2(e.updateyCenter() - b.updateyCenter(), e.updatexCenter() - b.updatexCenter());
                        e.addKnockback(b.getKnockback() * Math.cos(a), b.getKnockback() * Math.sin(a));
                        if (e.getHealth() <= 0) {
                            re.add(e);
                        }
                    }
                    if ((int) b.getPenetrations() <= 0 && (int) b.getRicochets() <= 0) {
                        i.remove();
                    }
                    if ((int) b.getRicochets() > 0) {
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
                            while (bulletEnemyCollision(b, e)) {
                                b.setX(b.getX() + b.getxSpeed() * 2);
                                b.setY(b.getY() + b.getySpeed() * 2);
                            }
                            b.setX(b.getX() + b.getxSpeed() * 2);
                            b.setY(b.getY() + b.getySpeed() * 2);
                            b.setRicochets(b.getRicochets() - 1);
                        }
                        while (bulletEnemyCollision(b, e)) {
                            b.setX(b.getX() + b.getxSpeed() * 2);
                            b.setY(b.getY() + b.getySpeed() * 2);
                        }
                        b.setX(b.getX() + b.getxSpeed() * 2);
                        b.setY(b.getY() + b.getySpeed() * 2);
                    } else {
                        b.setPenetrations(b.getPenetrations() - 1);
                        b.setHit(true);
                    }
                    break;
                }
            }
        }
        //apply splinters
        for (int n = 0; n < bs.size(); n++) {
            while (bulletEnemyCollision(bs.get(n), es.get(n / (int) splinters))) {
                bs.get(n).setX(bs.get(n).getX() + bs.get(n).getSpeed() * Math.cos(bs.get(n).getAngle()));
                bs.get(n).setY(bs.get(n).getY() + bs.get(n).getSpeed() * Math.sin(bs.get(n).getAngle()));
            }
            bs.get(n).setX(bs.get(n).getX() + bs.get(n).getSpeed() * Math.cos(bs.get(n).getAngle()));
            bs.get(n).setY(bs.get(n).getY() + bs.get(n).getSpeed() * Math.sin(bs.get(n).getAngle()));
            bullets.add(new Bullet(bs.get(n).getImage(), bs.get(n).getX(), bs.get(n).getY(), bs.get(n).getSize(), bs.get(n).getSpeed(), bs.get(n).getAngle(), bs.get(n).getDamage(), bs.get(n).getKnockback(), bs.get(n).getBounces(), 0, bs.get(n).getPenetrations(), bs.get(n).getRicochets(), bs.get(n).getExplosionSize() / 2, bs.get(n).getWarps()));
        }
        //remove enemies
        Iterator<Enemy> ei = enemies.iterator();
        while (ei.hasNext()) {
            Enemy e = ei.next();
            if (re.contains(e)) {
                gIndicators.add(new Indicator(e.getImage(), e.getSize(), e.getX(), e.getY(), e.getAngle(), 1, 30, 1, 0, 2));
                gIndicators.getLast().hitFlash();
                xp += e.getXp();
                score += e.getXp();
                ei.remove();
            }
        }
    }

    private void checkEnemyEnemyCollision() {
        for (int i = 0; i < enemies.size(); i++) {
            for (int j = i + 1; j < enemies.size(); j++) {
                Enemy a = enemies.get(i);
                Enemy b = enemies.get(j);
                while (enemyEnemyCollision(a, b)) {
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
            if (e.getImage() == enemyImage5 || e.getImage() == enemyImage6 || e.getImage() == enemyImage7) {
                if (e.getX() < WINDOW_WIDTH / 8.0 || e.getX() > (WINDOW_WIDTH * 7) / 8.0 - e.getWidth() && e.getX() < 9000) {
                    e.setAngle(Math.PI - e.getAngle());
                    while (e.getX() < WINDOW_WIDTH / 8.0) {
                        e.setX(e.getX() + e.getWidth() / 4.0);
                    }
                    while (e.getX() > (WINDOW_WIDTH * 7) / 8.0 - e.getWidth() && e.getX() < 9000) {
                        e.setX(e.getX() - e.getWidth() / 4.0);
                    }
                }
                if (e.getY() < WINDOW_HEIGHT / 16.0 || e.getY() > (WINDOW_HEIGHT * 15) / 16.0 - e.getHeight() && e.getY() < 9000) {
                    e.setAngle(-e.getAngle());
                    while (e.getY() < WINDOW_HEIGHT / 16.0) {
                        e.setY(e.getY() + e.getHeight() / 4.0);
                    }
                    while (e.getY() > (WINDOW_HEIGHT * 15) / 16.0 - e.getHeight() && e.getY() < 9000) {
                        e.setY(e.getY() - e.getHeight() / 4.0);
                    }
                }
            } else {
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
    }

    private void checkEnemyPlayerCollision() {
        Iterator<Enemy> i = enemies.iterator();
        while (i.hasNext()) {
            Enemy e = i.next();
            if (enemyPlayerCollision(e)) {
                player.hitFlash();
                player.setHealth(player.getHealth() - e.getDamage());
                gIndicators.add(new Indicator(e.getImage(), e.getSize(), e.getX(), e.getY(), e.getAngle(), 1, 30, 1, 0, 2));
                gIndicators.getLast().hitFlash();
                i.remove();
            }
        }
    }

    //enemy bullets
    private void enemyShoot(Enemy e) {
        if (e.getBulletImage() != null) {
            double exc = e.updatexCenter();
            double eyc = e.updateyCenter();
            double a = Math.atan2(player.getY() - eyc, player.getX() - exc);
            double random = Math.random() * e.getSpread() - e.getSpread() / 2;
            e.setReloadTime(e.getReloadTime() + 1);
            if (e.getReloadTime() >= e.getReload()) {
                for (int i = 0; i < e.getProjectiles(); i++) {
                    if (e.getImage() == enemyImage5) {
                        enemyBullets.add(new EnemyBullet(e.getBulletImage(), ((exc - e.getBulletImage().getWidth() * e.getBulletSize() / 3) - (e.getWidth() / 2.0 + e.getBulletImage().getWidth() / 3.0 * e.getBulletSize()) * Math.cos(e.getAngle())), ((eyc - e.getBulletImage().getHeight() * e.getBulletSize() / 3) - (e.getWidth() / 2.0 + e.getBulletImage().getWidth() / 3.0 * e.getBulletSize()) * Math.sin(e.getAngle())), e.getBulletSize(), e.getBulletSpeed(), e.getAngle() + Math.PI + random, e.getDamage(), e.getBounces()));
                    } else {
                        enemyBullets.add(new EnemyBullet(e.getBulletImage(), ((exc - e.getBulletImage().getWidth() * e.getBulletSize() / 3) + (e.getWidth() / 2.0 + e.getBulletImage().getWidth() / 3.0 * e.getBulletSize()) * Math.cos(a)), ((eyc - e.getBulletImage().getHeight() * e.getBulletSize() / 3) + (e.getWidth() / 2.0 + e.getBulletImage().getWidth() / 3.0 * e.getBulletSize()) * Math.sin(a)), e.getBulletSize(), e.getBulletSpeed(), a + random, e.getDamage(), e.getBounces()));
                    }
                }
                e.setReloadTime(0);
            }
        }
    }

    private void moveEnemyBullets() {
        Iterator<EnemyBullet> i = enemyBullets.iterator();
        while (i.hasNext()) {
            EnemyBullet eb = i.next();
            eb.setX(eb.getX() + eb.getxSpeed());
            eb.setY(eb.getY() + eb.getySpeed());
            double exc = eb.updatexCenter();
            double eyc = eb.updateyCenter();
            //update bounces
            if (eb.getBounces() > 0) {
                if (exc < WINDOW_WIDTH / 8.0 || exc > WINDOW_WIDTH * 7 / 8.0) {
                    eb.setxSpeed(-eb.getxSpeed());
                    eb.setBounces(eb.getBounces() - 1);
                } else if (eyc <= WINDOW_HEIGHT / 16.0 || eyc >= WINDOW_HEIGHT * 15 / 16.0) {
                    eb.setySpeed(-eb.getySpeed());
                    eb.setBounces(eb.getBounces() - 1);
                }
            }
            //out of bounds
            if (exc < 0 || exc > WINDOW_WIDTH || eyc < 0 || eyc > WINDOW_HEIGHT) {
                i.remove();
            }
        }
    }

    private void checkEnemyBulletPlayerCollision() {
        Iterator<EnemyBullet> i = enemyBullets.iterator();
        while (i.hasNext()) {
            EnemyBullet eb = i.next();
            if (pointToRectCollision(new Point((int) eb.updatexCenter(), (int) eb.updateyCenter()), eb.getWidth() / 2.0, player.getRect())) {
                player.hitFlash();
                player.setHealth(player.getHealth() - eb.getDamage());
                gIndicators.add(new Indicator(eb.getImage(), eb.getSize(), eb.getX(), eb.getY(), eb.getAngle(), 1, 30, 1, 0, 2));
                gIndicators.getLast().hitFlash();
                i.remove();
            }
        }
    }

    //misc
    private void updateIndicators() throws IOException {
        //spawn indicators
        Iterator<Indicator> it = sIndicators.iterator();
        while (it.hasNext()) {
            Indicator i = it.next();
            if (i.shrink()) {
                if (i.getImage() == enemyImage1) {
                    enemies.add(new Enemy(i.getImage(), false, i.getSize() / 2 * (Math.random() * enemyRandom / 10 + 1), i.getOx() + i.getoWidth() / 4.0, i.getOy() + i.getoHeight() / 4.0, i.getAngle(), 1, enemySpeed * (Math.random() * enemyRandom / 10 + 1), enemyHealth * (Math.random() * enemyRandom / 10 + 1), enemyDamage * (Math.random() * enemyRandom / 10 + 1), 0.9, null, 0, 0, 0, 0, 0, 0, 0));
                } else if (i.getImage() == enemyImage2) {
                    enemies.add(new Enemy(i.getImage(), true, i.getSize() / 2 * (Math.random() * enemyRandom / 10 + 1), i.getOx() + i.getoWidth() / 4.0, i.getOy() + i.getoHeight() / 4.0, i.getAngle(), 1.2, enemySpeed * 1.4 * (Math.random() * enemyRandom / 10 + 1), enemyHealth * (Math.random() * enemyRandom / 10 + 1), enemyDamage * 1.5 * (Math.random() * enemyRandom / 10 + 1), 0.9, null, 0, 0, 0, 0, 0, 0, 0));
                } else if (i.getImage() == enemyImage3) {
                    enemies.add(new Enemy(i.getImage(), true, i.getSize() / 2 * (Math.random() * enemyRandom / 10 + 1), i.getOx() + i.getoWidth() / 4.0, i.getOy() + i.getoHeight() / 4.0, i.getAngle(), 1.5, enemySpeed * 0.8 * (Math.random() * enemyRandom / 10 + 1), enemyHealth * (Math.random() * enemyRandom / 10 + 1), enemyDamage * (Math.random() * enemyRandom / 10 + 1), 0.9, enemyImage3Bullet, 1, 0, spawnDelay, 0, i.getSize() / 6 * 1.25, enemySpeed * 2 * (Math.random() * enemyRandom / 10 + 1), 0));
                } else if (i.getImage() == enemyImage4) {
                    enemies.add(new Enemy(i.getImage(), true, i.getSize() / 2 * (Math.random() * enemyRandom / 10 + 1), i.getOx() + i.getoWidth() / 4.0, i.getOy() + i.getoHeight() / 4.0, i.getAngle(), 4, enemySpeed * 0.3 * (Math.random() * enemyRandom / 10 + 1), enemyHealth * 10 * (Math.random() * enemyRandom / 10 + 1), enemyDamage * 3 * (Math.random() * enemyRandom / 10 + 1), 0.8, null, 0, 0, 0, 0, 0, 0, 0));
                } else if (i.getImage() == enemyImage5) {
                    enemies.add(new Enemy(i.getImage(), true, i.getSize() / 2 * (Math.random() * enemyRandom / 10 + 1), i.getOx() + i.getoWidth() / 4.0, i.getOy() + i.getoHeight() / 4.0, i.getAngle(), 1.2, enemySpeed * 1.5 * (Math.random() * enemyRandom / 10 + 1), enemyHealth * 0.8 * (Math.random() * enemyRandom / 10 + 1), enemyDamage * 1.2 * (Math.random() * enemyRandom / 10 + 1), 0.9, enemyImage5Bullet, 1, 0, spawnDelay / 2, Math.PI / 8, i.getSize() / 6 * 1.25, enemySpeed * 3 * (Math.random() * enemyRandom / 10 + 1), 0));
                } else if (i.getImage() == enemyImage6) {
                    enemies.add(new Enemy(i.getImage(), true, i.getSize() / 2 * (Math.random() * enemyRandom / 10 + 1), i.getOx() + i.getoWidth() / 4.0, i.getOy() + i.getoHeight() / 4.0, i.getAngle(), 2, Math.random() * 0.5, enemyHealth * 3 * (Math.random() * enemyRandom / 10 + 1), enemyDamage * 1.4 * (Math.random() * enemyRandom / 10 + 1), 0.8, null, 0, 0, 0, 0, 0, 0, 0));
                } else if (i.getImage() == enemyImage7) {
                    enemies.add(new Enemy(i.getImage(), false, i.getSize() / 2 * (Math.random() * enemyRandom / 10 + 1), i.getOx() + i.getoWidth() / 4.0, i.getOy() + i.getoHeight() / 4.0, i.getAngle(), 2.5, enemySpeed * 2 * (Math.random() * enemyRandom / 10 + 1), enemyHealth * 3.5 * (Math.random() * enemyRandom / 10 + 1), enemyDamage * 1.6 * (Math.random() * enemyRandom / 10 + 1), 0.85, null, 0, 0, 0, 0, 0, 0, (int) spawnDelay));
                }
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
        spawnDelay = baseSpawnDelay * Math.pow(score * 2 + 100, -0.1);
        enemySize = baseEnemySize * (1 + Math.sqrt(score) / 500.0);
        enemyHealth = baseEnemyHealth * (1 + score / 500.0);
        enemySpeed = baseEnemySpeed * (1 + score / 1000.0);
        enemyDamage = baseEnemyDamage * (1 + score / 500.0);
        enemyRandom = baseEnemyRandom * (1 + score / 500.0);
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
                upgrades.add(new Upgrade(u.getImage(), u.getSize(), u.getX(), u.getY(), u.getPlayerSize(), u.getMaxHealth(), u.getHealth(), u.getRegeneration(), u.getReload(), u.getSpread(), u.getSpeed(), u.getProjectiles(), u.getBulletSize(), u.getBulletSpeed(), u.getDamage(), u.getKnockback(), u.getBounces(), u.getSplinters(), u.getPenetrations(), u.getRicochets(), u.getExplosionSize(), u.getWarps(), u.getDesc()));
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
            do {
                random = (int) (Math.random() * epicUpgrades.size());
            } while (epicUpgrades.get((int) random).getDesc().equals("Reuse:\n+50% Max Health\n+30% Speed\n-10% Splinters\n-10% Bounces") && splinters <= 0 && bounces <= 0);
            return epicUpgrades.get((int) random);
        }
        do {
            random = (int) (Math.random() * legendaryUpgrades.size());
        } while (legendaryUpgrades.get((int) random).getDesc().equals("Processor:\n+25% Bullet Speed\n+25% Bounces\n+25% Penetrations\n+25% Ricochets") && bounces <= 0 && penetrations <= 0 && ricochets <= 0);
        return epicUpgrades.get((int) random);
    }

    private void upgrade(Upgrade u) {
        if (u.getPlayerSize() != 0) {
            if (Math.abs(u.getPlayerSize()) < 0.01) {
                player.setSize(player.getSize() + player.getSize() * u.getPlayerSize() * 100000);
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
        if (u.getRegeneration() != 0) {
            if (Math.abs(u.getRegeneration()) < 0.01) {
                player.setRegeneration(player.getRegeneration() + player.getRegeneration() * u.getRegeneration() * 100000);
            } else {
                player.setRegeneration(player.getRegeneration() + u.getRegeneration());
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
        if (u.getExplosionSize() != 0) {
            if (Math.abs(u.getExplosionSize()) < 0.01) {
                explosionSize += explosionSize * u.getExplosionSize() * 100000;
            } else {
                explosionSize += u.getExplosionSize();
            }
        }
        if (u.getWarps() != 0) {
            if (Math.abs(u.getWarps()) < 0.01) {
                warps += warps * u.getWarps() * 100000;
            } else {
                warps += u.getWarps();
            }
        }
        if (u.getDesc().equals("Reroll:\nReroll current\nupgrade selection")) {
            xpReq--;
            xp = xpReq;
        }
        if (player.getHealth() >= player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
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
        player.setSpeed(3);
        player.setSpread(0);
        player.setReload(100);
        player.setProjectiles(1);
        reload = (int) player.getReload() - 1;
        warps = 0;
        bulletSize = player.getSize() * 1.25;
        bulletSpeed = 4;
        damage = 40;
        knockback = 2;
        bounces = 0;
        splinters = 0;
        penetrations = 0;
        ricochets = 0;
        explosionSize = 0;
        spawnDelay = baseSpawnDelay;
        enemySize = baseEnemySize;
        enemySpeed = baseEnemySpeed;
        enemyHealth = baseEnemyHealth;
        enemyDamage = baseEnemyDamage;
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
                gIndicators.add(new Indicator(player.getImage(), player.getSize(), player.getX(), player.getY(), angleToMouse, 1, 30, 1, 0, 2));
            }
            gameOver = true;
        }
        try {
            scale();
            regenerate();
            movePlayer();
            checkAutofire();
            shoot();
            moveBullets();
            updateIndicators();
            spawnEnemy();
            moveEnemy();
            for (Enemy enemy : enemies) {
                enemyShoot(enemy);
            }
            moveEnemyBullets();
            checkBulletEnemyCollision();
            checkEnemyEnemyCollision();
            checkEnemyBorderCollision();
            checkEnemyPlayerCollision();
            checkEnemyBulletPlayerCollision();
            level();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        repaint();
    }
}