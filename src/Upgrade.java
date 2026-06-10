import javax.swing.*;

public class Upgrade {
    private ImageIcon image;
    private int width, height;
    private int projectiles;
    private int bounces, splinters, penetrations, ricochets;
    private double size;
    private double x, y;
    private double playerSize, maxHealth, health, reload, spread, speed;
    private double bulletSize, bulletSpeed, damage, knockback;
    private String desc;

    public Upgrade(ImageIcon image, double size, double x, double y, double playerSize, double maxHealth, double health, double reload, double spread, double speed, int projectiles, double bulletSize, double bulletSpeed, double damage, double knockback, int bounces, int splinters, int penetrations, int ricochets, String desc) {
        this.image = image;
        this.size = size;
        width = (int) (image.getIconWidth() * size);
        height = (int) (image.getIconHeight() * size);
        this.x = x;
        this.y = y;
        this.playerSize = playerSize;
        this.maxHealth = maxHealth;
        this.health = health;
        this.reload = reload;
        this.spread = spread;
        this.speed = speed;
        this.projectiles = projectiles;
        this.bulletSize = bulletSize;
        this.bulletSpeed = bulletSpeed;
        this.damage = damage;
        this.knockback = knockback;
        this.bounces = bounces;
        this.splinters = splinters;
        this.penetrations = penetrations;
        this.ricochets = ricochets;
        this.desc = desc;
    }

    public ImageIcon getImage() {
        return image;
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

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getPlayerSize() {
        return playerSize;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getRoundedMaxHealth() {
        return (double) (int) (maxHealth * 100) / 100;
    }

    public double getHealth() {
        return health;
    }

    public double getRoundedHealth() {
        return (double) (int) (health * 100) / 100;
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

    public double getSpeed() {
        return speed;
    }

    public double getRoundedSpeed() {
        return (double) (int) (speed * 100) / 100;
    }

    public int getProjectiles() {
        return projectiles;
    }

    public double getBulletSize() {
        return bulletSize;
    }

    public double getBulletSpeed() {
        return bulletSpeed;
    }

    public double getRoundedBulletSpeed() {
        return (double) (int) (bulletSpeed * 100) / 100;
    }

    public double getDamage() {
        return damage;
    }

    public double getRoundedDamage() {
        return (double) (int) (damage * 100) / 100;
    }

    public double getKnockback() {
        return knockback;
    }

    public double getRoundedKnockback() {
        return (double) (int) (knockback * 100) / 100;
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

    public String getDesc() {
        return desc;
    }

    public void setX(double x) {
        this.x = x;
    }
}