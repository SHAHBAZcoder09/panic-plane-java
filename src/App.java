import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public final class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::launch);
    }

    private static void launch() {
        JFrame frame = new JFrame("Panic Plane");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new PanicPlane());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}