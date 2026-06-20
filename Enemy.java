import java.awt.*;
import java.awt.image.BufferedImage;

public class Enemy {

    private BufferedImage image, bulletImage;
    private int width, height;
    private int flashTime, maxFlash;
    private int count, counter, move;
    private double size;
    private double x, y;
    private double xp;
    private double xCenter, yCenter;
    private double angle;
    private double speed;
    private double health, maxHealth;
    private double damage;
    private double kX, kY, kbd;
    private int reloadTime, projectiles, bounces;
    private double reload, spread, bulletSize, bulletSpeed;
    private boolean circle, one, two, three;
    private Rectangle rect;

    public Enemy(BufferedImage image, boolean circle, double size, double x, double y, double angle, double xp, double speed, double health, double damage, double kbd, BufferedImage bulletImage, int projectiles, int bounces, double reload, double spread, double bulletSize, double bulletSpeed, int count) {
        this.image = image;
        this.xp = xp;
        this.size = size;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.speed = speed;
        this.health = health;
        this.maxHealth = health;
        this.damage = damage;
        this.kbd = kbd;
        this.bulletImage = bulletImage;
        this.projectiles = projectiles;
        this.bounces = bounces;
        this.reload = reload;
        this.spread = spread;
        this.bulletSize = bulletSize;
        this.bulletSpeed = bulletSpeed;
        this.count = count;
        this.circle = circle;
        width = (int) (image.getWidth() * size);
        height = (int) (image.getHeight() * size);
        counter = 0;
        move = 0;
        xCenter = x + ((double) width / 2);
        yCenter = y + ((double) height / 2);
        flashTime = 0;
        maxFlash = 8;
        reloadTime = 0;
        kX = 0;
        kY = 0;
        one = false;
        two = false;
        three = false;
        rect = new Rectangle((int) x, (int) y, width, height);
    }

    public BufferedImage getImage() {
        return image;
    }

    public double getSize() {
        return size;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCount() {
        return count;
    }

    public int getCounter() {
        return counter;
    }

    public int getMove() {
        return move;
    }

    public double getXp() {
        return xp;
    }

    public double updatexCenter() {
        xCenter = x + (double) width / 2;
        return xCenter;
    }

    public double updateyCenter() {
        yCenter = y + (double) height / 2;
        return yCenter;
    }

    public double getAngle() {
        return angle;
    }

    public double getSpeed() {
        return speed;
    }

    public double getHealth() {
        return health;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getDamage() {
        return damage;
    }

    public double getkX() {
        return kX;
    }

    public double getkY() {
        return kY;
    }

    public BufferedImage getBulletImage() {
        return bulletImage;
    }

    public int getReloadTime() {
        return reloadTime;
    }

    public int getProjectiles() {
        return projectiles;
    }

    public int getBounces() {
        return bounces;
    }

    public double getReload() {
        return reload;
    }

    public double getSpread() {
        return spread;
    }

    public double getBulletSize() {
        return bulletSize;
    }

    public double getBulletSpeed() {
        return bulletSpeed;
    }

    public double getDistance(double x, double y) {
        return Math.sqrt(Math.pow(updatexCenter() - x, 2) + Math.pow(updateyCenter() - y, 2));
    }

    public Rectangle getRect() {
        rect.setBounds((int) x, (int) y, width, height);
        return rect;
    }

    public boolean isCircle() {
        return circle;
    }

    public boolean isOne() {
        return one;
    }

    public boolean isTwo() {
        return two;
    }

    public boolean isThree() {
        return three;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public void setMove(int move) {
        this.move = move;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void setxCenter(double xCenter) {
        this.xCenter = xCenter;
    }

    public void setyCenter(double yCenter) {
        this.yCenter = yCenter;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setReloadTime(int reloadTime) {
        this.reloadTime = reloadTime;
    }

    public void setReload(double reload) {
        this.reload = reload;
    }

    public void setSpread(double spread) {
        this.spread = spread;
    }

    public void setOne(boolean one) {
        this.one = one;
    }

    public void setTwo(boolean two) {
        this.two = two;
    }

    public void setThree(boolean three) {
        this.three = three;
    }

    public void hitFlash() {
        flashTime = maxFlash;
    }

    public void updateFlash() {
        if (flashTime > 0) {
            flashTime--;
        }
    }

    public boolean isFlashing() {
        return flashTime > 0;
    }

    public void addKnockback(double x, double y) {
        kX += x;
        kY += y;
    }

    public void dampenKnockback() {
        kX *= kbd;
        kY *= kbd;
        if (kX < 0.1 && kX > -0.1) {
            kX = 0;
        }
        if (kY < 0.1 && kY > -0.1) {
            kY = 0;
        }
    }
}