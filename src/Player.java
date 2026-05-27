import java.awt.image.BufferedImage;

public class Player {
    private BufferedImage image;
    private double x;
    private double y;
    private int width;
    private int height;
    private double xCenter;
    private double yCenter;
    private double speed;

    public Player(BufferedImage image, double size, int x, int y, int speed) {
        this.image = image;
        width = (int) (image.getWidth() * size);
        height = (int) (image.getHeight() * size);
        this.x = x;
        this.y = y;
        xCenter = x + ((double) width / 2) - 20;
        yCenter = y + ((double) height / 2) - 20;
        this.speed = speed;
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
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

    public double getSpeed() {
        return speed;
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
}

