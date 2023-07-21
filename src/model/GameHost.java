package model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import game_src.ClientHandler;
import static java.lang.Thread.sleep;


public class GameHost{

    private static final int MAX_PLAYERS = 4;
    private final HashMap<String, String> properties;
    private final Socket bookServerSocket;
    private final ThreadPoolExecutor threadPool;
    private final HashMap<Integer, Socket> clients;
    private final Map<Socket, GameClientHandler> handlers;
    private volatile boolean gameOver = false;
    private  boolean gameStarted;
    private volatile boolean allow_to_connect = true;
    private ServerSocket hostServerSocket;
    private volatile int currentPlayerCount;
    int gameHostPort ;

    String gameID;

    public GameHost(String clientName, String gameID) throws IOException {
        this.properties = utils.getProperties("src/resources/properties.txt");
        this.bookServerSocket = getBookServerSocket();
        this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_PLAYERS);
        this.clients = new HashMap<>();
        this.hostServerSocket = null;
        this.handlers = new ConcurrentHashMap<>();
        this.currentPlayerCount = 0;
        this.gameID = gameID;
        GameClient.Request<String> request = new GameClient.Request<>("load_game_ID", gameID, clientName);
        request.sendRequest(new ObjectOutputStream(bookServerSocket.getOutputStream()));
    }

    public void start() {
        gameOver = false;
        hostServerSocket = startHostServer();
        new Thread(this::acceptClients).start();
    }

    private void acceptClients() {
        try {
            hostServerSocket.setSoTimeout(1000);
            while (allow_to_connect) {
                if (currentPlayerCount < MAX_PLAYERS) {
                    try {
                        connectNewClient();
                    } catch (SocketTimeoutException e) {
                        // Handle the timeout exception
                        System.out.println("Server accept timeout. Waiting for clients...");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (currentPlayerCount == MAX_PLAYERS) {
                    System.out.println("Max players reached. Stop taking new requests...");
                    allow_to_connect = false;

                }

            }
        } catch (SocketException ignored) {
        }

        //lastCall();
    }

    private void handleClients() {
        while (!gameOver) {
            // iterate over clients and handle each one
            for (Map.Entry<Socket, GameClientHandler> entry : handlers.entrySet()) {
                Socket clientSocket = entry.getKey();
                ClientHandler clientHandler = entry.getValue();
                try {
                    if (clientSocket.getInputStream().available() >= 0) {
                        System.out.println("Game Host: Handling client: " + entry.getKey());
                        clientHandler.handleClient(clientSocket, bookServerSocket);
                        if (this.gameStarted) updateClients();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            GameClient.Request<Integer> request = new GameClient.Request<>("save_state", gameID, -1);
            try {
                request.sendRequest(new ObjectOutputStream(bookServerSocket.getOutputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void updateClients() {
        try {
            sleep(3000); // make sure all clients are ready to receive update
        } catch (InterruptedException e) {

        }
        for (Map.Entry<Socket, GameClientHandler> entry : handlers.entrySet()) {
            Socket clientSocket = entry.getKey();
            GameClientHandler clientHandler = (GameClientHandler) entry.getValue();
            try {
                if (clientSocket.getInputStream().available() >= 0) {
                    System.out.println("Game Host: updating client: " + entry.getKey());
                    GameClient.Request<Integer> request = new GameClient.Request<>("update", "update",-1);
                    request.sendRequest(new ObjectOutputStream(clientSocket.getOutputStream()));
                    clientHandler.interactWithClient(clientSocket, bookServerSocket);
                    System.out.println("Game Host: updated client: " + entry.getKey());

                }
            } catch (IOException e) {
                e.printStackTrace();}
        }
    }

    private ServerSocket startHostServer() {
        try {
            gameHostPort = Integer.parseInt(this.properties.get("game_host.port"));
            ServerSocket serverSocket = new ServerSocket(gameHostPort);
            System.out.println("Game Host: server started, waiting for clients. Listening on port " + gameHostPort);
            return serverSocket;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void connectNewClient() throws IOException {
        Socket clientSocket = hostServerSocket.accept();
        clients.put(currentPlayerCount, clientSocket);
        handlers.put(clientSocket, new GameClientHandler(clientSocket));
        currentPlayerCount++;
        System.out.println("Game Host: New client connected: " + clientSocket);
    }

    Socket getBookServerSocket() throws IOException {
        int port = Integer.parseInt(this.properties.get("game_server.port"));
        return new Socket(this.properties.get("game_server.ip"), port);
    }

    public void startGame() {
        System.out.println("Game Host: Starting game...");
        allow_to_connect = false;
        gameStarted = true;
        new Thread(this::handleClients).start();
    }

    public void close() {
        gameOver = true;
    }
}
