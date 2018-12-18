package HEAD;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScreenCapture implements Runnable {


    static boolean VERBOSE = false;

    static int MILLISECONDS_DELAY =
            //500;
            100;

    volatile BufferedImage screenshot;

    private final Robot robot;

    ScreenCapture() throws AWTException {
        robot = new Robot();
    }

    public static void main(String[] args) {
        if (args.length > 0 && !args[0].isEmpty())
            VERBOSE = Boolean.valueOf(args[0]);
        if (args.length > 1 && !args[1].isEmpty()) {
            try {
                MILLISECONDS_DELAY = Integer.valueOf(args[1]);
            } catch (NumberFormatException nfe) {
                System.err.println("Can not solve interval value : " + nfe);
            }
        }
        System.out.println("Verbose mode set to : " + VERBOSE);
        System.out.println("Delay was set to    : " + MILLISECONDS_DELAY +" ms.");
        System.out.println();

        try {
            // sets picture capturing and monitor window
            ScreenCapture screenCapture = new ScreenCapture();
            new Thread(screenCapture).start();
            new Thread(new monitorWindow(screenCapture)).start();

            // collect clients
            String[] clientAddresses = ConnectionCollector.collectClients();
            if (clientAddresses == null)
                return;

            // send a screenshot
            MessageSender.sendImage(clientAddresses, screenCapture);
        } catch (AWTException awte) {
            System.err.println("Can not get a screen capture : " + awte + "...");
        }
    }

    BufferedImage getScreenShot() {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage img = robot.createScreenCapture(screenRect);
        return img;
    }

    void waitOnMe() {
        synchronized (this) {
            try {
                if (VERBOSE)
                    System.out.println(Thread.currentThread().getName() + " is going to sleep...");
                this.wait();
            } catch (InterruptedException ie) {
                System.out.println(Thread.currentThread().getName() + " is going out...");
            }
        }
    }

    private void waitOnMe(int time) {
        synchronized (this) {
            try {
                if (VERBOSE)
                    System.out.println(Thread.currentThread().getName() + " is going to sleep...");
                this.wait(time);
            } catch (InterruptedException ie) {
                System.out.println(Thread.currentThread().getName() + " is going out...");
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            waitOnMe(MILLISECONDS_DELAY);
            screenshot = getScreenShot();

            synchronized (this) {
                this.notifyAll();
            }
        }
    }
}
