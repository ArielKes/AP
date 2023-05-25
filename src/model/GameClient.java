package model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.lang.management.ManagementFactory;

import static model.utils.*;

public class GameClient {
    protected final Socket hs;
    private final HashMap<String, String> properties;
    PrintWriter out;
    //Scanner in;

    public GameClient() throws IOException {
        this.properties = utils.getProperties("src/resources/properties.txt");
        this.hs = getGameHostSocket();
    }

    public void runClient() throws IOException {
        System.out.println("client on :"+ Thread.currentThread().getId()+ " is running");
        out = new PrintWriter(hs.getOutputStream());
        out.println("hi, I'm client with PID" + utils.getProcessId() );
        out.flush();
        //in = new Scanner(hs.getInputStream());
        //wait for server to send 'it your turn'
        while (true) {

            BufferedReader in = new BufferedReader(new InputStreamReader(hs.getInputStream()));
            if (in.readLine().equals("it's your turn")) {
                System.out.println("client on :"+ Thread.currentThread().getId()+ " got the turn");
                //todo : here get action from user



                out.println("client" + " is done");
                out.flush();
            }
        }

    }

    Socket getGameHostSocket() throws IOException {
        assert properties != null;
        int port = Integer.parseInt(properties.get("game_host.port"));
        return new Socket(properties.get("game_host.ip"), port);
    }




}



