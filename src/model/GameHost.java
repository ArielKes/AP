package model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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
    HashMap<Integer, Socket> clients;
    ServerSocket hostServerSocket;
    HashMap<Socket, ClientHandler> handlers;

    public GameHost(String propertiesFileName) throws IOException {
        this.properties = getProperties(propertiesFileName);
        this.bookServerSocket = getBookServerSocket();
        currentPlayerCount = 0;
        this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_PLAYERS);
        this.clients = new HashMap<>();
        this.hostServerSocket = null;
        this.handlers = new HashMap<>();
    }

    public void start() {
        stop = false;
        hostServerSocket = firstCall();
        new Thread(this::acceptClients).start();
        new Thread(this::handleClients).start();
    }

    private void acceptClients(){
        try {
            hostServerSocket.setSoTimeout(1000);
            while (!stop) {
                if (currentPlayerCount < MAX_PLAYERS) {
                    try {
                        connectNewClient();
                    } catch (IOException e) {e.printStackTrace();
                    }
                    threadPool.execute(() -> {
                        try {
                            ClientHandler ch = new GameClientHandler();
                            ch.handleClient(clients.get(currentPlayerCount-1).getInputStream(), clients.get(currentPlayerCount-1).getOutputStream(), bookServerSocket);
                            handlers.put(clients.get(currentPlayerCount-1),ch);
                            //clients.get(currentPlayerCount-1).close();
                            //ch.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        } catch (SocketException ignored) {
        }

        lastCall();

    }

    private void handleClients() {

    }


    private ServerSocket firstCall(){
        try {
            int gameHostPort = Integer.parseInt(this.properties.get("game_host.port"));

            System.out.println("Server started. Listening on port " + gameHostPort);
            return new ServerSocket(gameHostPort);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void lastCall(){
        try {
            hostServerSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        threadPool.shutdown();
    }

    private void connectNewClient() throws IOException {
        Socket aClient = hostServerSocket.accept();
        clients.put(currentPlayerCount,aClient);
        currentPlayerCount++;
        System.out.println("New client connected: " + aClient.getInetAddress());
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
