package bio.timeserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 */
public class TimeServer {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf ( args[0] );
            } catch (Exception e) {

            }
        }
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket ( port );
            System.out.println ( "Time Server start on port " + port );
            Socket socket = null;
            while (true) {
                socket = serverSocket.accept ();
                new Thread ( new TimeServerHandler(socket) ).start ();
            }
        } finally {
           if (serverSocket != null) {
               System.out.println ( "Time Server close" );
               serverSocket.close ();
               serverSocket = null;
           }
        }
    }
}
