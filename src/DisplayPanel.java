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
    private Timer timer;
    private final int WINDOWWIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private final int WINDOWHEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private final double DIAG = 1 / Math.sqrt(2);
    private RescaleOp whiteFlashOp;
    private Player player;
    private BufferedImage playerImage, playerImage2, playerImage3, playerImage4, bulletImage;
    private int borderSize;
    private int xp, xpReq;
    private int reload;
    private int spawnDelay, spawnTime;
    private double playerSize;
    private int bounces, splinters, penetrations, ricochets;
    private double bulletSize, bulletSpeed, damage, knockback;
    private double enemySize, enemySpeed, enemyHealth, enemyDamage;
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
    private JButton resetButton;
    private JButton upgrade1;
    private JButton upgrade2;
    private JButton upgrade3;

    public DisplayPanel() throws IOException {
        whiteFlashOp = new RescaleOp(new float[]{0f, 0f, 0f, 1f}, new float[]{255f, 255f, 255f, 0f}, null);
        playerImage = ImageIO.read(new File("src/Player1.png"));
        playerImage2 = ImageIO.read(new File("src/Player2.png"));
        playerImage3 = ImageIO.read(new File("src/Player3.png"));
        playerImage4 = ImageIO.read(new File("src/Player4.png"));
        playerSize = 0.5;
        player = new Player(playerImage, playerSize, (int) (WINDOWWIDTH / 2.0 - playerImage.getWidth() / (2 / playerSize) + 20), (int) (WINDOWHEIGHT / 2.0 - playerImage.getHeight() / (2 / playerSize)), 4, 100, 50, 0, 1);
        bulletImage = ImageIO.read(new File("src/Bullet.png"));
        borderSize = 20;
        xp = 0;
        xpReq = 10;
        reload = (int) player.getReload() - 1;
        spawnDelay = 2;
        spawnTime = 0;
        bounces = 100;
        splinters = 100;
        penetrations = 1;
        ricochets = 1;
        bulletSize = playerSize * 10.25;
        bulletSpeed = 4;
        damage = 20;
        knockback = 2;
        enemySize = 1;
        enemySpeed = 1;
        enemyHealth = 60;
        enemyDamage = 10;
        first = true;
        gameOver = false;
        eWasPressed = false;
        autofire = false;
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        sindicators = new ArrayList<>();
        gindicators = new ArrayList<>();
        timer = new Timer(8, this);
        resetButton = new JButton("Reset");
        add(resetButton);
        resetButton.addActionListener(this);
        resetButton.setVisible(true);
        upgrade1.addActionListener(this);
        upgrade1.setVisible(true);
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

        resetButton.setLocation(40, 60);


        //draw window
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WINDOWWIDTH, WINDOWHEIGHT);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(WINDOWWIDTH / 8 - borderSize, WINDOWHEIGHT / 16 - borderSize, WINDOWWIDTH * 3 / 4 + borderSize * 2, WINDOWHEIGHT * 7 / 8 + borderSize * 2);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(WINDOWWIDTH / 8, WINDOWHEIGHT / 16, WINDOWWIDTH * 3 / 4, WINDOWHEIGHT * 7 / 8);
        Graphics2D g2d = (Graphics2D) g.create();

        //game ends remove all entities
        if (gameOver) {
            Iterator<Enemy> ei = enemies.iterator();
            Iterator<Bullet> bi = bullets.iterator();
            while (ei.hasNext()) {
                Enemy e = ei.next();
                if (first) {
                    gindicators.add(new Indicator(e.getImage(), enemySize, e.getX(), e.getY(), e.getAngle(), 30, 1, 0, 2));
                }
                ei.remove();
            }
            while (bi.hasNext()) {
                Bullet b = bi.next();
                if (first) {
                    gindicators.add(new Indicator(b.getImage(), b.getSize(), b.getX(), b.getY(), 0, 30, 1, 0, 2));
                }
                bi.remove();
            }
            first = false;
            if (gindicators.isEmpty()) {
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
            for (Indicator i : sindicators) {
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
        //draw health bar
        g.setColor(Color.DARK_GRAY);
        g.fillRect(WINDOWWIDTH / 128 - borderSize / 4, WINDOWHEIGHT / 64 - borderSize / 4, WINDOWWIDTH * 7 / 64 - borderSize / 2, WINDOWHEIGHT / 32 + borderSize / 2);
        g.setColor(new Color(255, 0, 0, 200));
        g.fillRect(WINDOWWIDTH / 128, WINDOWHEIGHT / 64, (int) ((WINDOWWIDTH * 7 / 64.0 - borderSize) * player.getHealth() / player.getMaxHealth()), WINDOWHEIGHT / 32);
        g.setColor(new Color(153, 0, 0, 100));
        g.fillRect(WINDOWWIDTH / 128, WINDOWHEIGHT / 64, WINDOWWIDTH * 7 / 64 - borderSize, WINDOWHEIGHT / 32);
        g.setFont(new Font("Arial", Font.PLAIN, WINDOWHEIGHT / 32));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(new Color(255, 255, 255, 200));
        g.drawString((int) player.getHealth() + "/" + (int) player.getMaxHealth(), WINDOWWIDTH / 128 + (WINDOWWIDTH * 7 / 64 - borderSize - fm.stringWidth((int) player.getHealth() + "/100")) / 2, WINDOWHEIGHT / 64 + ((WINDOWHEIGHT / 32 - fm.getHeight()) / 2) + fm.getAscent());
        //draw xp bar
        g.setColor(Color.DARK_GRAY);
        g.fillRect(WINDOWWIDTH * 17 / 128 - borderSize / 4, WINDOWHEIGHT * 57 / 64 - borderSize / 4, WINDOWWIDTH * 7 / 64 - borderSize / 2, WINDOWHEIGHT / 32 + borderSize / 2);
        g.setColor(new Color(150, 150, 150, 200));
        g.fillRect(WINDOWWIDTH * 17 / 128, WINDOWHEIGHT * 57 / 64, (int) (WINDOWWIDTH * 7 / 64.0 - borderSize), WINDOWHEIGHT / 32);
        g.setColor(new Color(255, 255, 255, 200));
        g.fillRect(WINDOWWIDTH * 17 / 128, WINDOWHEIGHT * 57 / 64, (int) ((WINDOWWIDTH * 7 / 64.0 - borderSize) * xp / xpReq), WINDOWHEIGHT / 32);
        //draw stats
        g.setFont(new Font("Arial", Font.PLAIN, WINDOWHEIGHT / 48));
        g.drawString("player width: " + player.getWidth(), WINDOWWIDTH / 128, WINDOWHEIGHT * 3 / 40);
        g.drawString("player height: " + player.getHeight(), WINDOWWIDTH / 128, WINDOWHEIGHT / 10);
        g.drawString("player speed: " + player.getSpeed(), WINDOWWIDTH / 128, WINDOWHEIGHT / 8);
        g.drawString("reload: " + player.getReload(), WINDOWWIDTH / 128, WINDOWHEIGHT * 3 / 20);
        g.drawString("spread (r): " + player.getSpread(), WINDOWWIDTH / 128, WINDOWHEIGHT * 7 / 40);
        g.drawString("projectiles: " + player.getProjectiles(), WINDOWWIDTH / 128, WINDOWHEIGHT / 5);
        g.drawString("bullet size: " + bulletSize, WINDOWWIDTH / 128, WINDOWHEIGHT * 9 / 40);
        g.drawString("bullet speed: " + bulletSpeed, WINDOWWIDTH / 128, WINDOWHEIGHT / 4);
        g.drawString("damage: " + damage, WINDOWWIDTH / 128, WINDOWHEIGHT * 11 / 40);
        g.drawString("knockback: " + knockback, WINDOWWIDTH / 128, WINDOWHEIGHT * 3 / 10);
        g.drawString("bounces: " + bounces, WINDOWWIDTH / 128, WINDOWHEIGHT * 13 / 40);
        g.drawString("splinters: " + splinters, WINDOWWIDTH / 128, WINDOWHEIGHT * 7 / 20);
        g.drawString("penetrations: " + penetrations, WINDOWWIDTH / 128, WINDOWHEIGHT * 3 / 8);
        g.drawString("ricochets: " + ricochets, WINDOWWIDTH / 128, WINDOWHEIGHT * 2 / 5);

        g.drawString("Enemy", WINDOWWIDTH * 447 / 500, WINDOWHEIGHT / 20);
        g.drawString("spawn delay: " + spawnDelay, WINDOWWIDTH * 447 / 500, WINDOWHEIGHT * 3 / 40);
        g.drawString("size: " + enemySize, WINDOWWIDTH * 447 / 500, WINDOWHEIGHT / 10);
        g.drawString("speed: " + enemySpeed, WINDOWWIDTH * 447 / 500, WINDOWHEIGHT / 8);
        g.drawString("health: " + enemyHealth, WINDOWWIDTH * 447 / 500, WINDOWHEIGHT * 3 / 20);
        g.drawString("damage: " + enemyDamage, WINDOWWIDTH * 447 / 500, WINDOWHEIGHT * 7 / 40);
        g2d.dispose();
    }

    //check key triggers
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

    public static void playSoundWithPitch(String path, float minPitch, float maxPitch) {
        try {
            File audioFile = new File(path);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            float pitch = (float) Math.random() * (maxPitch - minPitch) + minPitch;
            AudioFormat base = audioInputStream.getFormat();
            AudioFormat pitchedFormat = new AudioFormat(base.getEncoding(), base.getSampleRate() * pitch, base.getSampleSizeInBits(), base.getChannels(), base.getFrameSize(), base.getFrameRate() * pitch, base.isBigEndian());
            AudioInputStream pitchedStream = AudioSystem.getAudioInputStream(pitchedFormat, audioInputStream);
            Clip clip = AudioSystem.getClip();
            clip.open(pitchedStream);
            clip.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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

    private void shoot() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        double pxc = player.updatexCenter();
        double pyc = player.updateyCenter();
        angleToMouse = Math.atan2(mousePoint.y - pyc, mousePoint.x - pxc);
        reload++;
        if ((PRESSEDKEYS[KeyEvent.VK_SPACE] || autofire) && reload >= (int) player.getReload()) {
            for (int i = 0; i < player.getProjectiles(); i++) {
                double random = Math.random() * player.getSpread() - player.getSpread() / 2;
                bullets.add(new Bullet(bulletImage, ((pxc - bulletImage.getWidth() * bulletSize / 2) + (player.getWidth() / 2.0 + bulletImage.getWidth() / 3.0 * bulletSize) * Math.cos(angleToMouse)), ((pyc - bulletImage.getHeight() * bulletSize / 2) + (player.getWidth() / 2.0 + bulletImage.getWidth() / 3.0 * bulletSize) * Math.sin(angleToMouse)), bulletSize, bulletSpeed, angleToMouse + random, damage, knockback, bounces, splinters, penetrations, ricochets));
            }
            playSoundWithPitch("src/shoot.wav", 0.02f, 0.05f);
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
                if (bxc < WINDOWWIDTH / 8.0 || bxc > WINDOWWIDTH * 7 / 8.0) {
                    b.setxSpeed(-b.getxSpeed());
                    b.setBounces(b.getBounces() - 1);
                } else if (byc <= WINDOWHEIGHT / 16.0 || byc >= WINDOWHEIGHT * 15 / 16.0) {
                    b.setySpeed(-b.getySpeed());
                    b.setBounces(b.getBounces() - 1);
                }
            }
            //out of bounds
            if (bxc < 0 || bxc > WINDOWWIDTH || byc < 0 || byc > WINDOWHEIGHT) {
                i.remove();
            }
        }
    }

    public void spawnEnemy() throws IOException {
        spawnTime++;
        if (spawnTime == spawnDelay) {
            spawnTime = 0;
            BufferedImage image = ImageIO.read(new File("src/Enemy" + 1 + ".png"));
            sindicators.add(new Indicator(image, enemySize * 2, Math.random() * (WINDOWWIDTH * 3 / 4.0 - image.getWidth() * enemySize) + WINDOWWIDTH / 8.0 - image.getWidth() * enemySize / 4.0, Math.random() * (WINDOWHEIGHT * 7 / 8.0 - image.getHeight() * enemySize) + WINDOWHEIGHT / 16.0 - image.getHeight() * enemySize / 4.0, 0, 30, 0.1, 5, 0));
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
                        gindicators.add(new Indicator(e.getImage(), enemySize, e.getX(), e.getY(), e.getAngle(), 30, 1, 0, 2));
                        gindicators.getLast().hitFlash();
                        xp += e.getXp();
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
                            d = enemy.getDistance(b.getX(), b.getY());
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
                gindicators.add(new Indicator(e.getImage(), enemySize, e.getX(), e.getY(), e.getAngle(), 30, 1, 0, 2));
                gindicators.getLast().hitFlash();
                i.remove();
            }
        }
    }

    private void updateIndicators() throws IOException {
        //spawn indicators
        Iterator<Indicator> it = sindicators.iterator();
        while (it.hasNext()) {
            Indicator i = it.next();
            if (i.shrink()) {
                enemies.add(new Enemy(1, enemySize, i.getOx() + i.getoWidth() / 4.0, i.getOy() + i.getoHeight() / 4.0, enemySpeed, enemyHealth, enemyDamage));
                it.remove();
            }
        }
        //death animations
        Iterator<Indicator> it2 = gindicators.iterator();
        while (it2.hasNext()) {
            Indicator i = it2.next();
            i.updateFlash();
            if (i.grow()) {
                it2.remove();
            }
        }
    }

    private void upgrade() {
        if (xp >= xpReq) {
            xp = 0;
            xpReq++;
            upgrade1 = new JButton("1");
            add(upgrade1);
            upgrade1.setLocation(WINDOWWIDTH / 2, WINDOWHEIGHT / 2);
        }
    }

    private void upgradeButton() {

    }

    private void reset() {
        gameOver = false;
        xp = 0;
        xpReq = 10;
        player.setMaxHealth(100);
        player.setHealth(100);
        player.setSize(1);
        player.setReload(50);
        reload = (int) player.getReload() - 1;
        spawnDelay = 2;
        spawnTime = 0;
        bounces = 100;
        splinters = 100;
        penetrations = 1;
        ricochets = 1;
        bulletSize = playerSize * 10.25;
        bulletSpeed = 4;
        damage = 20;
        knockback = 2;
        enemySize = 1;
        enemySpeed = 1;
        enemyHealth = 60;
        enemyDamage = 10;
        first = false;
        requestFocusInWindow();
        timer.start();
    }

    //timer ticks
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == resetButton) {
            reset();
        }
        if (e.getSource() == upgrade1) {
            upgradeButton();
        }
        if ((int) player.getHealth() <= 0) {
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
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ex) {
            throw new RuntimeException(ex);
        }
        moveBullets();
        checkBulletEnemyCollision();
        checkEnemyEnemyCollision();
        checkEnemyBorderCollision();
        checkEnemyPlayerCollision();
        upgrade();
        repaint();
    }
}