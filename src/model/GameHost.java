package model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import game_src.ClientHandler;


public class GameHost {

    private static final int MAX_PLAYERS = 5;
    private final HashMap<String, String> properties;
    private final Socket bookServerSocket;
    private final ThreadPoolExecutor threadPool;
    private final HashMap<Integer, Socket> clients;
    private final HashMap<Socket, ClientHandler> handlers;
    private volatile boolean stop = false;
    private ServerSocket hostServerSocket;
    int currentPlayerCount;

    public GameHost(String propertiesFileName) throws IOException {
        this.properties = getProperties(propertiesFileName);
        this.bookServerSocket = getBookServerSocket();
        this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_PLAYERS);
        this.clients = new HashMap<>();
        this.hostServerSocket = null;
        this.handlers = new HashMap<>();
        currentPlayerCount = 0;
    }

    public void start() {
        stop = false;
        hostServerSocket = firstCall();
        new Thread(this::acceptClients).start();
        new Thread(this::handleClients).start();
    }

    private void acceptClients() {
        try {
            hostServerSocket.setSoTimeout(1000);
            while (!stop) {
                if (currentPlayerCount < MAX_PLAYERS) {
                    try {
                        connectNewClient();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    threadPool.execute(() -> {
                        try {
                            Socket clientSocket = clients.get(currentPlayerCount - 1);
                            ClientHandler ch = new GameClientHandler();
                            ch.handleClient(clientSocket.getInputStream(), clientSocket.getOutputStream(), bookServerSocket);
                            handlers.put(clientSocket, ch);
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
        while (!stop) {
            for (Map.Entry<Socket, ClientHandler> entry : handlers.entrySet()) {
                Socket clientSocket = entry.getKey();
                ClientHandler clientHandler = entry.getValue();
                try {
                    if (clientSocket.getInputStream().available() > 0) {
                        clientHandler.handleClient(clientSocket.getInputStream(), clientSocket.getOutputStream(), bookServerSocket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ServerSocket firstCall() {
        try {
            int gameHostPort = Integer.parseInt(this.properties.get("game_host.port"));
            ServerSocket serverSocket = new ServerSocket(gameHostPort);
            System.out.println("Server started. Listening on port " + gameHostPort);
            return serverSocket;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void lastCall() {
        try {
            hostServerSocket.close();
            for (Socket clientSocket : clients.values()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        threadPool.shutdown();
    }

    private void connectNewClient() throws IOException {
        Socket clientSocket = hostServerSocket.accept();
        clients.put(currentPlayerCount, clientSocket);
        currentPlayerCount++;
        System.out.println("New client connected: " + clientSocket.getInetAddress());
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

    public void close() {
        stop = true;
    }
}
