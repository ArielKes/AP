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
//    ObjectOutputStream objectOutputStream;


    // administration variables
    private volatile boolean myTurn = false;


    // Game variables
    String clientName;
    //Scanner in;
    ScoreTable scoreTable;
    List<Tile> tiles = new ArrayList<>();


    private void basicConstructor(String clientName) throws IOException {
        this.clientName = clientName;
        this.scoreTable = new ScoreTable();
        properties = utils.getProperties("src/resources/properties.txt");
    }


    public GameClient(String clientName) throws IOException {
        properties = utils.getProperties("src/resources/properties.txt");
        int port = Integer.parseInt(properties.get("game_host.port"));
        hs = new Socket(properties.get("game_host.ip"), port);
        this.basicConstructor(clientName);
    }

    public GameClient(String clientName, String hostIpAddress) throws IOException {
        properties = utils.getProperties("src/resources/properties.txt");
        hs = new Socket(hostIpAddress, Integer.parseInt(properties.get("game_host.port")));
        this.basicConstructor(clientName);
    }

    int placeWordOnBoard(Word w) throws IOException {
        // -1 if word is not valid
        // -2 if word is valid but not in dictionary
        // n if word is valid and in dictionary and n is the score
        waitToTurn();
        int score = -1;
        Request<Word> r = new Request<>("command","place_word", w);
        r.sendRequest(new ObjectOutputStream(hs.getOutputStream()));
        //wait for server to send score
        String serverRespond = utils.getRespondFromServer(hs);
        if (!serverRespond.split("#")[0].equals("score")) {
            System.out.println("error in server respond, expected score got: " + serverRespond.split("#")[0]);
            throw new IOException();
        }
        return Integer.parseInt(serverRespond.split("#")[1]);
    }


    void waitToTurn()  {
        if(!this.myTurn) {
            Request r ;
            try {
                r = utils.getResponseFromServer1(hs.getInputStream());
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (r.requestCommand.equals("your_turn")) {
                System.out.println("client on " + Thread.currentThread().getId() + ": got the turn");
                this.myTurn = true;

            }
            else {
                System.out.println("client on " + Thread.currentThread().getId());
            }

        }
    }


    @Override
    public boolean isMyTurn() {
        return this.myTurn;
    }

    @Override
    public void endTurn() {
        Request<Integer> r = new Request<Integer>("command", "turn_ended", -1);
        try {
            r.sendRequest(new ObjectOutputStream(hs.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.myTurn = false;
    }

    public String getBoard() {
        waitToTurn();
        Request<Integer> r = new Request<Integer>("command", "get_board", -1);
        try {
            r.sendRequest(new ObjectOutputStream(hs.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //wait for server to send score
        try {
            Request respond = utils.getResponseFromServer1(hs.getInputStream());
            System.out.println("server respond: " + respond.object);
            return (String) respond.object;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public HashMap<String, Integer> getScoreTable() {
        return null;
    }

    @Override
    public List<Tile> getTiles(int n) {
        return this.tiles;
    }


    private Tile getTile() {
        waitToTurn();
        Request r = new Request("command", "get_tile", -1);
        try {
            r.sendRequest(new ObjectOutputStream(hs.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //wait for server to send score
        Request respond;
        try {
            respond = utils.getResponseFromServer1(hs.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (!respond.requestCommand.equals("sent_tile")) {
            System.out.println("error in server respond, expected tile got: " + respond.requestCommand);
            throw new RuntimeException();
        }
        else return (Tile) respond.object;

    }
    private List<Tile> getNTiles(int n) {
        waitToTurn();
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
        Request<Word> r = new Request<>("placeWord", "placeWord", w);
        try {
            r.sendRequest(new ObjectOutputStream(hs.getOutputStream()));
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
                //objectOutputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}



