package model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import game_src.ClientHandler;


public class GameHost {

    /*
    this is the server!
    */
    private static final int MAX_PLAYERS = 5;
    HashMap<String, String> properties;
    Socket bookServerSocket;
    //ServerSocket hostServerSocket;
    int currentPlayerCount ;
    volatile boolean stop = false;
    ThreadPoolExecutor threadPool;

    public GameHost(String propertiesFileName) throws IOException {
        this.properties = getProperties(propertiesFileName);
        this.bookServerSocket = getBookServerSocket();
        currentPlayerCount = 0;
        this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_PLAYERS);
    }

    public void start() {
        stop = false;
        new Thread(this::startServer).start();
    }

    private void startServer(){
        try {
            int gameHostPort = Integer.parseInt(this.properties.get("game_host.port"));
            ServerSocket hostServerSocket = new ServerSocket(gameHostPort);
            System.out.println("Server started. Listening on port " + gameHostPort);
            hostServerSocket.setSoTimeout(1000);
            while(!stop){
                if(currentPlayerCount < MAX_PLAYERS) {
                    try {
                        currentPlayerCount++;
                        Socket aClient = hostServerSocket.accept();
                        System.out.println("New client connected: " + aClient.getInetAddress());
                        threadPool.execute(() -> {
                            try {
                                // create a new instance of the client handler
                                ClientHandler ch = new GameClientHandler();
                                // handle the client
                                ch.handleClient(aClient.getInputStream(), aClient.getOutputStream(), bookServerSocket);
                                // close the client handler
                                aClient.close();
                                ch.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (SocketTimeoutException ignored) {
                    }
                }
            }
            hostServerSocket.close();
            threadPool.shutdown();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, String> getProperties(String propertiesFileName) throws IOException {
        HashMap<String, String> p = new HashMap<>();
        BufferedReader in = new BufferedReader(new FileReader(propertiesFileName));
        String line;
        while ((line = in.readLine()) != null) {
            String[] sp = line.split("=");
            p.put(sp[0], sp[1]);
        }
        in.close();
        return p;
    }

    Socket getBookServerSocket() throws IOException {
        int port = Integer.parseInt(this.properties.get("game_server.port"));
        return new Socket(this.properties.get("game_server.ip"), port);
    }

    public void close(){
        stop = true;
    }

}
