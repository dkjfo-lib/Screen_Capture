package HEAD;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static HEAD.ScreenCapture.VERBOSE;

public class MessageSender implements Runnable {

    private static final int PORT = 8080;

    private static ScreenCapture _screenCapture;

    private final Socket socket;
    private final String ip;
    private final OutputStream out;

    private MessageSender(String ip) throws IOException {
        // configure socket
        socket = new Socket(ip, PORT);
        out = socket.getOutputStream();
        this.ip = ip;
    }

    static void sendImage(String[] clientList, ScreenCapture screenCapture) {
        _screenCapture = screenCapture;

        System.out.println(clientList.length + " threads were created for sending nudes...");

        for (String ip : clientList) {
            try {
                new Thread(new MessageSender(ip)).start();
            } catch (IOException ioe) {
                System.err.println("Can not create socket for the ip " + ip + ":" + PORT + " : " + ioe);
            }
        }
    }

    @Override
    public void run() {
        boolean success = true;
        try {
            // send message
            System.out.println(Thread.currentThread().getName() + " sends a message to " + ip);

            while (true) {
                // wait for command

                _screenCapture.waitOnMe();

                // prepares image to descend -> may have troubles with multiple threads writing and reading same file
                BufferedImage image = _screenCapture.screenshot;
                File picFile = new File("pic.bmp");
                ImageIO.write(image, "bmp", picFile);
                int fileLength = (int) picFile.length();
                byte[] fileData = new byte[fileLength];

                new FileInputStream(picFile).read(fileData);

                // sends image
                if (image != null){
                    out.write(fileData, 0, fileLength);
                    if (VERBOSE)
                        System.out.println(Thread.currentThread().getName() + " have sent a message...");
                }
                out.flush();
            } // while end

        } catch (IOException ioe) {
            System.err.println(Thread.currentThread().getName() + " had IOException during sending answer message : " + ioe);
            success = false;
            try {
                socket.close();
            } catch (IOException _ioe) {
                System.err.println(Thread.currentThread().getName() + " can not close socket : " + _ioe);
            }
        }
        finally {
            try {
                socket.close();
            } catch (IOException _ioe) {
                System.err.println(Thread.currentThread().getName() + " can not close socket : " + _ioe);
            }
        }

        if (VERBOSE && success)
            System.out.println(Thread.currentThread().getName() + " had successfully sent a message to " + ip);
    }
}
