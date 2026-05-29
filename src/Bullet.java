import java.awt.image.BufferedImage;

public class Bullet {
    private BufferedImage image;
    private String type;
    private double x;
    private double y;
    private int width;
    private int height;
    private double xCenter;
    private double yCenter;
    private double speed;
    private double xSpeed;
    private double ySpeed;

    public Bullet(BufferedImage image, double x, double y, double size, double speed, double angle) {
        this.image = image;
        this.type = "";
        this.width = (int) (image.getWidth() * size);
        this.height = (int) (image.getHeight() * size);
        this.x = x;
        this.y = y;
        xCenter = x + ((double) width / 2);
        yCenter = y + ((double) height / 2);
        this.speed = speed;
        xSpeed = speed * Math.cos(angle);
        ySpeed = speed * Math.sin(angle);
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getType() {
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

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void setType(String type) {
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
}