import java.awt.image.BufferedImage;

public class Explosion {
    private BufferedImage image;
    private int width, height, oWidth, oHeight;
    private int scale;
    private double life;
    private double fade;
    private double threshold;
    private double alpha;
    private double size;
    private double x, y, ox, oy;
    private double xCenter, yCenter;
    private double damage;
    private double knockback;

    public Explosion(BufferedImage image, double x, double y, double size, double damage, double knockback, double alpha, double life, double fade, double threshold, int scale) {
        this.image = image;
        this.size = size;
        this.width = (int) (image.getWidth() * size);
        this.height = (int) (image.getHeight() * size);
        oWidth = width;
        oHeight = height;
        this.x = x;
        this.y = y;
        ox = x;
        oy = y;
        xCenter = x + ((double) width / 2);
        yCenter = y + ((double) height / 2);
        this.damage = damage;
        this.knockback = knockback;
        this.alpha = alpha;
        this.life = life;
        this.fade = fade;
        this.threshold = threshold;
        this.scale = scale;
    }

    public BufferedImage getImage() {
        return image;
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

    public double getDamage() {
        return damage;
    }

    public double getKnockback() {
        return knockback;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
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

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setKnockback(double knockback) {
        this.knockback = knockback;
    }

    public boolean fade() {
        life -= fade;
        alpha = Math.max(0, life / 30);
        width += scale;
        height += scale;
        x = ox + (oWidth - width) / 2.0;
        y = oy + (oHeight - height) / 2.0;
        return life <= threshold;
    }
}