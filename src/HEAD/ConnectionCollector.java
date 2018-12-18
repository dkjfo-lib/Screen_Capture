package HEAD;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static Files.FileWork.writeToFile;

public class ConnectionCollector {


    static final int PORT = 8081;

    static String[] collectClients(){
        try {
            String headLocalAddress = InetAddress.getLocalHost().getHostAddress();
            try {
                writeToFile(headLocalAddress + ":" + PORT);
            } catch (IOException ioe) {
                System.err.println("Could not write local ip to file : " + ioe);
            }

            System.out.println("Local ip : " + headLocalAddress + ":" + PORT + "...");

            // configure listening socket
            ServerSocket serverSocket = new ServerSocket();
            InetSocketAddress currAddress = new InetSocketAddress(headLocalAddress, PORT);
            serverSocket.bind(currAddress);

            System.out.println("Server was successfully opened on local ip : " + headLocalAddress + ":" + PORT + "...");
            System.out.println();

            // listen for incoming connections
            ExecutorService pool = Executors.newFixedThreadPool(1);
            System.out.println("Waiting for new connections...");
            Future<String[]> result = pool.submit(() -> {
                StringBuilder ConnectionsList = new StringBuilder();
                try {
                    while (true) {
                        Socket connection = serverSocket.accept();
                        String incomingAddress = connection.getInetAddress().getHostAddress();
                        System.out.println("\tNew connection on : " + incomingAddress);
                        ConnectionsList.append(incomingAddress);
                        ConnectionsList.append("\n");
                    }
                } catch (IOException ioe) {
                    System.out.println("Connection collection closed : " + ioe.getMessage());
                }
                if (ConnectionsList.toString().isEmpty())
                    return null;
                return ConnectionsList.toString().split("\n");
            });

            // wait for command to end listening
            {
                System.out.println("Press `ENTER` to stop listen for incoming connections");
                Scanner scanner = new Scanner(System.in);
                scanner.nextLine();
                scanner.close();
            }

            // free resources
            serverSocket.close();
            pool.shutdown();

            return result.get();

        }catch (UnknownHostException uhe){
            System.err.println("Can not get local host : " + uhe);
        }catch (IOException ioe){
            System.err.println("Can not work with files : " + ioe);
        }catch (InterruptedException ie){
            System.err.println("Interrupted while waiting for the connections : " + ie);
        }catch (ExecutionException ee){
            System.err.println("Exception during client collection : " + ee);
        }
        return null;
    }


}
