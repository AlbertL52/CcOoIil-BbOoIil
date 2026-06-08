import javax.swing.JFrame;
import java.io.IOException;

public class ArenaMain {
    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("idk");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(960, 580);
        frame.setLocationRelativeTo(null);
        DisplayPanel panel = new DisplayPanel();
        frame.add(panel);
        frame.setVisible(true);
        panel.requestFocusInWindow();
    }
}