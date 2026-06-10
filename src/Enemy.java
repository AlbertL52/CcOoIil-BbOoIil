import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Enemy {

    private BufferedImage image;
    private int type;
    private int width, height;
    private int flashTime;
    private int maxFlash;
    private int xp;
    private double size;
    private double x, y;
    private double xCenter, yCenter;
    private double angle;
    private double speed;
    private double health;
    private double damage;
    private double kX, kY;
    private double reload;
    private double spread;

    public Enemy(int type, double size, double x, double y, double speed, double health, double damage) throws IOException {
        image = ImageIO.read(new File("src/Enemy" + type + ".png"));
        this.type = type;
        width = (int) (image.getWidth() * size);
        height = (int) (image.getHeight() * size);
        this.size = size;
        this.x = x;
        this.y = y;
        xCenter = x + ((double) width / 2);
        yCenter = y + ((double) height / 2);
        this.speed = speed;
        this.health = health;
        this.damage = damage;
        flashTime = 0;
        maxFlash = 8;
        kX = 0;
        kY = 0;
        angle = 0;
        reload = 0;
        spread = 0;
        if (type == 1) {
            xp = 1;
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getType() {
        return type;
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

    public int getXp() {
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

    public double getDamage() {
        return damage;
    }

    public double getkX() {
        return kX;
    }

    public double getkY() {
        return kY;
    }

    public double getReload() {
        return reload;
    }

    public double getSpread() {
        return spread;
    }

    public double getDistance(double x, double y) {
        return Math.sqrt(Math.pow(updatexCenter() - x, 2) + Math.pow(updateyCenter() - y, 2));
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void setType(int type) {
        this.type = type;
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

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setReload(double reload) {
        this.reload = reload;
    }

    public void setSpread(double spread) {
        this.spread = spread;
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

    public void dampenKnockback(double d) {
        kX *= d;
        kY *= d;
        if (kX < 0.1 && kX > -0.1) {
            kX = 0;
        }
        if (kY < 0.1 && kY > -0.1) {
            kY = 0;
        }
    }
}