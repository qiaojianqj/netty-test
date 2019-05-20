package bio.timeserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

/**
 *
 */
public class TimeServerHandler implements Runnable {
    private Socket socket;

    public TimeServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
           in = new BufferedReader ( new InputStreamReader ( this.socket.getInputStream () ) );
           out = new PrintWriter ( this.socket.getOutputStream (), true );
           String currentTime = null;
           String line = null;
           while (true) {
               line = in.readLine ();
               if (line == null) {
                   break;
               }
               System.out.println ( "Time Server receive order: " + line + " on thread: " + Thread.currentThread ().getName ());
               currentTime = "QUERY TIME ORDER".equalsIgnoreCase ( line ) ? new Date (System.currentTimeMillis ()).toString () : "BAD ORDER";
               out.println (currentTime);
           }
        } catch (Exception e) {
            if (in != null) {
                try {
                    in.close ();
                } catch (IOException e1) {
                    e1.printStackTrace ();
                }
                in = null;
            }
            if (out != null) {
                out.close ();
                out = null;
            }
            if (this.socket != null) {
                try {
                    this.socket.close ();
                } catch (IOException e1) {
                    e1.printStackTrace ();
                }
                this.socket = null;
            }
        }
    }
}
