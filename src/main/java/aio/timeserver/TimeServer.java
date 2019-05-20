package aio.timeserver;

import java.io.IOException;

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
        AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
        new Thread ( timeServer, "NIO-AsyncTimeServerHandler" ).start ();
    }
}
