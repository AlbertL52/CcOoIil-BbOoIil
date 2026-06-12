import java.awt.*;
import java.awt.image.BufferedImage;

public class Player {
    private BufferedImage image;
    private int width, height;
    private int flashTime;
    private int maxFlash;
    private int projectiles;
    private double size;
    private double x, y;
    private double xCenter, yCenter;
    private double speed;
    private double maxHealth, health;
    private double reload;
    private double spread;
    private Rectangle rect;

    public Player(BufferedImage image, double size, double x, double y, int speed, double maxHealth, double reload, double spread, int projectiles) {
        this.image = image;
        this.size = size;
        width = (int) (image.getWidth() * size);
        height = (int) (image.getHeight() * size);
        flashTime = 0;
        maxFlash = 8;
        this.x = x;
        this.y = y;
        xCenter = x + ((double) width / 2) - (double) width / 7;
        yCenter = y + ((double) height / 2);
        this.speed = speed;
        this.maxHealth = maxHealth;
        health = maxHealth;
        this.reload = reload;
        this.spread = spread;
        this.projectiles = projectiles;
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
        width = (int) (image.getWidth() * size);
        return width;
    }

    public int getHeight() {
        height = (int) (image.getHeight() * size);
        return height;
    }

    public double updatexCenter() {
        xCenter = x + (double) width / 2 - (double) width / 7;
        return xCenter;
    }

    public double updateyCenter() {
        yCenter = y + (double) height / 2;
        return yCenter;
    }

    public double getDistance(double x, double y) {
        return Math.sqrt(Math.pow(updatexCenter() - x, 2) + Math.pow(updateyCenter() - y, 2));
    }

    public double getSpeed() {
        return speed;
    }

    public double getRoundedSpeed() {
        return (double) (int) (speed * 100) / 100;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getHealth() {
        return health;
    }

    public double getReload() {
        return reload;
    }

    public double getRoundedReload() {
        return (double) (int) (reload * 100) / 100;
    }

    public double getSpread() {
        return spread;
    }

    public double getRoundedSpread() {
        return (double) (int) (spread * 100) / 100;
    }

    public int getProjectiles() {
        return projectiles;
    }

    public Rectangle getRect() {
        rect.setBounds((int) x, (int) y, width, height);
        return rect;
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

    public void setxCenter(double xCenter) {
        this.xCenter = xCenter;
    }

    public void setyCenter(double yCenter) {
        this.yCenter = yCenter;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setMaxFlash(int maxFlash) {
        this.maxFlash = maxFlash;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public void setReload(double reload) {
        this.reload = reload;
    }

    public void setSpread(double spread) {
        this.spread = spread;
    }

    public void setProjectiles(int projectiles) {
        this.projectiles = projectiles;
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
}