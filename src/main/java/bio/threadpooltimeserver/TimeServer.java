package bio.threadpooltimeserver;

import bio.timeserver.TimeServerHandler;

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
            TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(10, 10000);
            while (true) {
                socket = serverSocket.accept ();
                singleExecutor.execute( new TimeServerHandler (socket) );
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
