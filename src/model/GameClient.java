package model;

import game_src.Tile;
import game_src.Word;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameClient implements Model{

    // Connection variables
    protected Socket hs;
    private HashMap<String, String> properties;
    PrintWriter out;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;


    // administration variables
    private volatile boolean gameStarted = false;
    private volatile boolean myTurn = false;


    // Game variables
    String clientName;
    //Scanner in;
    ScoreTable scoreTable;
    Tile[] tiles;


    private void basicConstructor(String clientName) throws IOException {
        this.clientName = clientName;
        this.scoreTable = new ScoreTable();
        properties = utils.getProperties("src/resources/properties.txt");
        objectOutputStream = new ObjectOutputStream(hs.getOutputStream());
        objectInputStream = new ObjectInputStream(hs.getInputStream());
        this.out = new PrintWriter(hs.getOutputStream());
        this.listenToGameStart();
        this.listenToMyTurn();
    }


    public GameClient(String clientName) throws IOException {
        properties = utils.getProperties("src/resources/properties.txt");
        int port = Integer.parseInt(properties.get("game_host.port"));
        hs = new Socket(properties.get("game_host.ip"), port);
        this.basicConstructor(clientName);
    }

    public GameClient(String clientName, String hostIpAddress) throws IOException {
        hs = new Socket(hostIpAddress, Integer.parseInt(properties.get("game_host.port")));
        this.basicConstructor(clientName);
    }

    int placeWordOnBoard(Word w) throws IOException, InterruptedException {
        // -1 if word is not valid
        // -2 if word is valid but not in dictionary
        // n if word is valid and in dictionary and n is the score
        waitToTurn();
        int score = -1;
        this.out.println("placeWord#" + w);
        this.out.flush();
        objectOutputStream.writeObject(w);
        //wait for server to send score
        String serverRespond = utils.getRespondFromServer(hs);
        if (!serverRespond.split("#")[0].equals("score")) {
            System.out.println("error in server respond, expected score got: " + serverRespond.split("#")[0]);
            throw new IOException();
        }
        return Integer.parseInt(serverRespond.split("#")[1]);
    }

    void listenToMyTurn() {
        // start new thread to listen to server
        // if server send 'your turn' then set myTurn to true
        // else set myTurn to false
        Thread clientThread = new Thread(() -> {
            while (true) {
                String serverRespond = utils.getRespondFromServer(hs);
                if (serverRespond.equals("your turn#")) {
                    System.out.println("client on " + Thread.currentThread().getId() + ": got the turn");
                    this.myTurn = true;
                    try {
                        // pause for 2 seconds
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // handle the exception
                        e.printStackTrace();
                    }
                }
            }
        });
        clientThread.start();
    }

    private void listenToGameStart() {
        Thread clientThread = new Thread(() -> {
            while (true) {
                String serverRespond = utils.getRespondFromServer(hs);
                if (serverRespond.equals("gameStarted#")) {
                    System.out.println("client on " + Thread.currentThread().getId() + ": game start");
                    this.gameStarted = true;
                    waitToTurn();
                    getNTiles(7);
                    turnEnded();
                    break;
                }
            }
        });
        clientThread.start();
    }


    void waitToTurn()  {
        while (!myTurn) {
        }// wait here as long as it is not my turn
        System.out.println("client: start my turn");

    }


    @Override
    public boolean isGameStarted() {
        return this.gameStarted;
    }


    @Override
    public boolean isMyTurn() {
        return false;
    }

    @Override
    public void turnEnded() {
        out.println("turnEnded#");
        out.flush();
    }

    public String getBoard() {
        waitToTurn();
        this.out.println("get_board#");
        this.out.flush();
        //wait for server to send score
        String serverRespond = utils.getRespondFromServer(hs);
        System.out.println("server respond: " + serverRespond);
        return serverRespond;
    }

    @Override
    public HashMap<String, Integer> getScoreTable() {
        return null;
    }


    private Tile getTile(){
        waitToTurn();
        this.out.println("get_tile#");
        this.out.flush();
        //wait for server to send score
        Tile tile = null;
        try {
            tile = (Tile) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return tile;
    }
    public List<Tile> getNTiles(int n) {
        waitToTurn();
        List<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            tiles.add(getTile());
        }
        return tiles;
    }

    @Override
    public int placeWord(Word w) {
        // -1 if word is not valid
        // -2 if word is valid but not in dictionary
        // n if word is valid and in dictionary and n is the score
        waitToTurn();
        int score = -1;
        this.out.println("placeWord#" + w);
        this.out.flush();
        try {
            objectOutputStream.writeObject(w);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //wait for server to send score
        String serverRespond = utils.getRespondFromServer(hs);
        if (!serverRespond.split("#")[0].equals("score")) {
            System.out.println("error in server respond, expected score got: " + serverRespond.split("#")[0]);
            throw new RuntimeException();

        }
        return Integer.parseInt(serverRespond.split("#")[1]);
    }


    @Override
    public boolean checkWord(Word w) {
        return false;
    }
}



