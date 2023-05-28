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
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;
    Request request;
    private volatile boolean inRequestReady = false;

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
        //this.listenToHost();
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
//        this.out.println("placeWord#" + w);
//        this.out.flush();
        objectOutputStream.writeObject(w);
        //wait for server to send score
        String serverRespond = utils.getRespondFromServer(hs);
        if (!serverRespond.split("#")[0].equals("score")) {
            System.out.println("error in server respond, expected score got: " + serverRespond.split("#")[0]);
            throw new IOException();
        }
        return Integer.parseInt(serverRespond.split("#")[1]);
    }

//    void listenToHost() {
//        // start new thread to listen to server
//        // if server send 'your turn' then set myTurn to true
//        // else set myTurn to false
//        Thread clientThread = new Thread(() -> {
//            while (true) {
//                Request serverRespond = null;
//
//                try {
//                    serverRespond = utils.getResponseFromServer1(hs.getInputStream());
//                    if (serverRespond.requestCommand.equals("your_turn")) {
//                        System.out.println("client on " + Thread.currentThread().getId() + ": got the turn");
//                        this.myTurn = true;
//
//                    }
//                    else if (serverRespond.requestCommand.equals("game_started")) {
//                        System.out.println("client on " + Thread.currentThread().getId() + ": game started");
//                        this.gameStarted = true;
//                    }
//                    else{
//                        this.request = serverRespond;
//                        this.inRequestReady = true;
//                        System.out.println("client on " + Thread.currentThread().getId() + ": got request");
//                    }
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                } catch (ClassNotFoundException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//        clientThread.start();
//    }




    void waitToTurn()  {
        Request r = null;// wait here as long as it is not my turn
        try {
            r = utils.getResponseFromServer1(hs.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (r.requestCommand.equals("your_turn")) {
            System.out.println("client on " + Thread.currentThread().getId() + ": got the turn");
            this.myTurn = true;

        }
        else {
            System.out.println("client on " + Thread.currentThread().getId());
        }

        //System.out.println("client: start my turn");

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
        Request r = new Request("command", "turnEnded", -1);
        r.sendRequest(this.objectOutputStream);
    }

    public String getBoard() {
        waitToTurn();
        Request r = new Request("command", "get_board", -1);
        r.sendRequest(this.objectOutputStream);

        //wait for server to send score
        String serverRespond = utils.getRespondFromServer(hs);
        System.out.println("server respond: " + serverRespond);
        return serverRespond;
    }

    @Override
    public HashMap<String, Integer> getScoreTable() {
        return null;
    }


    private Tile getTile() {
        waitToTurn();
        Request r = new Request("command", "get_tile", -1);
        r.sendRequest(this.objectOutputStream);

        //wait for server to send score
        Tile tile = null;
        while (!inRequestReady) {
        }
        tile = (Tile) this.request.object;
        this.inRequestReady = false;
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
        Request r = new Request("placeWord", "placeWord", w);
        r.sendRequest(this.objectOutputStream);
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



    public static class Request<T extends Serializable> implements Serializable {

        public String requestType;
        public String requestCommand;
        public T object;

        // Constructor
        public Request(String requestType, String requestCommand, T object) {
            this.requestType = requestType;
            this.requestCommand = requestCommand;
            this.object = object;
        }

        public void sendRequest(ObjectOutputStream objectOutputStream)  {
            try {
                objectOutputStream.writeUTF(this.requestType);
                objectOutputStream.writeUTF(this.requestCommand);
                objectOutputStream.writeObject(this.object);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}



