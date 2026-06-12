import java.awt.image.BufferedImage;

public class Indicator {
    private BufferedImage image;
    private double size;
    private int width, height, oWidth, oHeight;
    private int flashTime;
    private int maxFlash;
    private double x, y, ox, oy;
    private double xCenter, yCenter;
    private double angle;
    private int scale;
    private double life;
    private double fade;
    private double threshold;
    private double alpha = 1.0f;

    public Indicator(BufferedImage image, double size, double x, double y, double angle, double life, double fade, double threshold, int scale) {
        this.image = image;
        this.size = size;
        width = (int) (image.getWidth() * size * alpha);
        oWidth = width;
        height = (int) (image.getHeight() * size * alpha);
        oHeight = height;
        flashTime = 0;
        maxFlash = 8;
        this.x = x;
        ox = x;
        this.y = y;
        oy = y;
        xCenter = x + ((double) width / 2);
        yCenter = y + ((double) height / 2);
        this.angle = angle;
        this.life = life;
        this.fade = fade;
        this.threshold = threshold;
        this.scale = scale;
    }

    public BufferedImage getImage() {
        return image;
    }

    public double getSize() {
        return size;
    }

    public int getsWidth() {
        width = (int) (image.getWidth() * size * alpha);
        return width;
    }

    public int getsHeight() {
        height = (int) (image.getHeight() * size * alpha);
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getoWidth() {
        return oWidth;
    }

    public int getoHeight() {
        return oHeight;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getOx() {
        return ox;
    }

    public double getOy() {
        return oy;
    }

    public double updatexCenter() {
        xCenter = x + ((double) width / 2);
        return xCenter;
    }

    public double updatepxCenter() {
        xCenter = x + ((double) width / 2) - (double) width / 7;
        return xCenter;
    }

    public double updateyCenter() {
        yCenter = y + ((double) height / 2);
        return yCenter;
    }

    public double getAngle() {
        return angle;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
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

    public boolean shrink() {
        life -= fade;
        alpha = Math.max(0, life / 30f);
        x = ox + (oWidth - getsWidth()) / 2.0;
        y = oy + (oHeight - getsHeight()) / 2.0;
        return life <= threshold;
    }

    public boolean grow() {
        life -= fade;
        alpha = Math.max(0, life / 30f);
        width += scale;
        height += scale;
        x = ox + (oWidth - width) / 2.0;
        y = oy + (oHeight - height) / 2.0;
        return life <= threshold;
    }
}