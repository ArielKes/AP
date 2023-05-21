package model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import game_src.ClientHandler;
import game_src.MyServer;


public class GameHost implements ClientHandler {

    /*
    this part is in charge of communicating with the book server and acting as a proxy to the other clients.
    */
    private static final int MAX_PLAYERS = 5;
    HashMap<String, String> properties;
    Socket bookServerSocket;
    ServerSocket serverSocket;
    int currentPlayerCount ;
    PrintWriter out2fg;
    private List<Socket> clientSockets = new ArrayList<>();

    public GameHost(String propertiesFileName) throws IOException {
        this.properties = getProperties(propertiesFileName);
        this.bookServerSocket = getBookServerSocket();
        this.out2fg = new PrintWriter(this.bookServerSocket.getOutputStream());
        MyServer game_host_server = new MyServer(Integer.parseInt(this.properties.get("game_host.port")), new GameClientHandler(), 6);
        currentPlayerCount = 0;
    }

    public void start() {
        try {
            int PORT = Integer.parseInt(this.properties.get("game_host.port"));
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started. Listening on port " + PORT);
            while (currentPlayerCount < MAX_PLAYERS) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                GameClientHandler gameClientHandler = new GameClientHandler(clientSocket);
                gameClientHandler.start();
                currentPlayerCount++;
            }

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                GameClientHandler gameClientHandler = new GameClientHandler(clientSocket);
                gameClientHandler.start();
            }

        } catch (IOException e) {
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

    @Override
    public void handle(InputStream inFromclient, OutputStream outToClient) {

    }

    @Override
    public void close() {

    }


    Socket getBookServerSocket() throws IOException {
        int port = Integer.parseInt(this.properties.get("game_server.port"));
        return new Socket(this.properties.get("game_server.ip"), port);
    }
}
