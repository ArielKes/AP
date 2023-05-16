package game_src;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class RunClient {

    public static void client1(int port) {
        new Thread(
                () -> {
                    try {
                        Socket server = new Socket("localhost", port);
                        Random r = new Random();
                        String text = "" + (1000 + r.nextInt(100000));
                        String rev = new StringBuilder(text).reverse().toString();
                        PrintWriter outToServer = new PrintWriter(server.getOutputStream());
                        Scanner in = new Scanner(server.getInputStream());
                        outToServer.println(text);
                        outToServer.flush();
                        String response = in.next();
                        if (response == null || !response.equals(rev))
                            System.out.println("problem getting the right response from your server, cannot continue the test (-25)");
                        in.close();
                        outToServer.close();
                        server.close();
                    } catch (Exception e) {
                        System.out.println("Exception was thrown when running a client (-25)");
                        e.printStackTrace();
                    }
                }).start();
    }

    public static void main(String[] args){
        boolean ok = true;
        int port = 8080;
        try {
            client1(port);
            client1(port);
            client1(port);
            client1(port);
        } catch (Exception e) {
            System.out.println("some exception was thrown while testing your server, cannot continue the test (-100)");
            ok = false;
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        System.out.println("ok = " + ok);
    }

}

