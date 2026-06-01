import java.awt.image.BufferedImage;

public class Bullet {
    private BufferedImage image;
    private int type;
    private int width, height;
    private double x, y;
    private double xCenter, yCenter;
    private double speed, xSpeed, ySpeed;
    private double damage;
    private double knockback;

    public Bullet(BufferedImage image, double x, double y, double size, double speed, double angle, double damage, double knockback) {
        this.image = image;
        this.type = 1;
        this.width = (int) (image.getWidth() * size);
        this.height = (int) (image.getHeight() * size);
        this.x = x;
        this.y = y;
        xCenter = x + ((double) width / 2);
        yCenter = y + ((double) height / 2);
        this.speed = speed;
        xSpeed = speed * Math.cos(angle);
        ySpeed = speed * Math.sin(angle);
        this.damage = damage;
        this.knockback = knockback;
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getType() {
        return type;
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

    public double updatexCenter() {
        xCenter = x + ((double) width / 2);
        return xCenter;
    }

    public double updateyCenter() {
        yCenter = y + ((double) height / 2);
        return yCenter;
    }

    public double getSpeed() {
        return speed;
    }

    public double getxSpeed() {
        return xSpeed;
    }

    public double getySpeed() {
        return ySpeed;
    }

    public double getDamage() {
        return damage;
    }

    public double getKnockback() {
        return knockback;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void setType(int type) {
        this.type = type;
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

    public void setxSpeed(double xSpeed) {
        this.xSpeed = xSpeed;
    }

    public void setySpeed(double ySpeed) {
        this.ySpeed = ySpeed;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setKnockback(double knockback) {
        this.knockback = knockback;
    }
}