import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ArenaMain {
    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Geometry Dash");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(960, 580);
        frame.setLocationRelativeTo(null);
        BufferedImage player = ImageIO.read(new File("src/Tonk.png"));
        DisplayPanel panel = new DisplayPanel(player);
        frame.add(panel);
        frame.setVisible(true);

    }
}