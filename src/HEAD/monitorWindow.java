package HEAD;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;

public class monitorWindow extends JComponent implements Runnable {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private final JFrame window;
    private final ScreenCapture screenCapture;

    monitorWindow(ScreenCapture screenCapture) {
        this.screenCapture = screenCapture;
        window = new JFrame();
        window.setSize(WIDTH, HEIGHT);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().add(this);
        window.setVisible(true);
    }

    public void paint(Graphics g) {
        g.drawImage(screenCapture.screenshot, 0, 0, window.getWidth(), window.getHeight(), null);
    }

    @Override
    public void run() {
        while (true) {
            screenCapture.waitOnMe();
            paint(window.getGraphics());
        }
    }
}
