import java.awt.image.BufferedImage;

public class Bullet {
    private BufferedImage image;
    private int type;
    private int width, height;
    private int bounces, splinters, penetrations, ricochets;
    private double size;
    private double x, y;
    private double xCenter, yCenter;
    private double angle;
    private double speed, xSpeed, ySpeed;
    private double damage;
    private double knockback;
    private boolean hit;

    public Bullet(BufferedImage image, double x, double y, double size, double speed, double angle, double damage, double knockback, int bounces, int splinters, int penetrations, int ricochets) {
        this.image = image;
        this.type = 1;
        this.size = size;
        this.width = (int) (image.getWidth() * size);
        this.height = (int) (image.getHeight() * size);
        this.x = x;
        this.y = y;
        xCenter = x + ((double) width / 2);
        yCenter = y + ((double) height / 2);
        this.angle = angle;
        this.speed = speed;
        xSpeed = speed * Math.cos(angle);
        ySpeed = speed * Math.sin(angle);
        this.damage = damage;
        this.knockback = knockback;
        this.bounces = bounces;
        this.splinters = splinters;
        this.penetrations = penetrations;
        this.ricochets = ricochets;
        hit = false;
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

    public double getSize() {
        return size;
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

    public double getAngle() {
        return angle;
    }

    public double getSpeed() {
        return speed;
    }

    public double getxSpeed() {
        xSpeed = speed * Math.cos(angle);
        return xSpeed;
    }

    public double getySpeed() {
        ySpeed = speed * Math.sin(angle);
        return ySpeed;
    }

    public double getDamage() {
        return damage;
    }

    public double getKnockback() {
        return knockback;
    }

    public int getBounces() {
        return bounces;
    }

    public int getSplinters() {
        return splinters;
    }

    public int getPenetrations() {
        return penetrations;
    }

    public int getRicochets() {
        return ricochets;
    }

    public boolean hit() {
        return hit;
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

    public void setSize(double size) {
        this.size = size;
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

    public void setAngle(double angle) {
        this.angle = angle;
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

    public void setBounces(int bounces) {
        this.bounces = bounces;
    }

    public void setSplinters(int splinters) {
        this.splinters = splinters;
    }

    public void setPenetrations(int penetrations) {
        this.penetrations = penetrations;
    }

    public void setRicochets(int ricochets) {
        this.ricochets = ricochets;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }
}