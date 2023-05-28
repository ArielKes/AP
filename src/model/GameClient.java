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
    List<Tile> tiles = new ArrayList<>();


    private void basicConstructor(String clientName) throws IOException {
        this.clientName = clientName;
        getNTiles(7);
        int a = placeWord(new Word(new Tile[]{this.tiles.get(0)}, 7, 7, true));
        this.endTurn();

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



    void waitToTurn()  {
        if(!this.myTurn) {
            Request r ;
            try {
                r = utils.getRequestFromInput(hs.getInputStream());
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
        Request<Integer> r = new Request<Integer>("turn_ended","command", -1);
        try {
            r.sendRequest(new ObjectOutputStream(hs.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.myTurn = false;
    }

    public String getBoard() {
        waitToTurn();
        Request<Integer> r = new Request<Integer>("get_board", "command", -1);
        try {
            r.sendRequest(new ObjectOutputStream(hs.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //wait for server to send score
        try {
            Request respond = utils.getRequestFromInput(hs.getInputStream());
            System.out.println("client on" + Thread.currentThread().getId()+ " -server respond: " + respond.object);
            return (String) respond.object;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public HashMap<String, Integer> getScoreTable() {
        waitToTurn();
        Request<Integer> r = new Request<Integer>("get_score_table", "command", -1);
        try {
            r.sendRequest(new ObjectOutputStream(hs.getOutputStream()));
            Request respond = utils.getRequestFromInput(hs.getInputStream());
            if (!respond.requestCommand.equals("score_table")) {
                System.out.println("error in server respond, expected score table got: " + respond.requestCommand);
                throw new RuntimeException();
            }
            ScoreTable s = (ScoreTable) respond.object;
            return s.scores;


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Tile> getClientTiles() {
        return this.tiles;
    }

    @Override
    public void addTile() {
        waitToTurn();
        getNTiles(1);

    }


    private Tile getTile() {
        waitToTurn();
        Request r = new Request( "get_tile","command", -1);
        Request respond;
        try {
            r.sendRequest(new ObjectOutputStream(hs.getOutputStream()));
            respond = utils.getRequestFromInput(hs.getInputStream());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (!respond.requestCommand.equals("sent_tile")) {
            System.out.println("error in server respond, expected tile got: " + respond.requestCommand);
            throw new RuntimeException();
        }
        else return (Tile) respond.object;

    }
    private void getNTiles(int n) {
        waitToTurn();
        for (int i = 0; i < n; i++) {
            tiles.add(getTile());
        }
    }


    @Override
    public int placeWord(Word w) {
        // todo: implement return -1 if word is invalid -2 if physically impossible
        waitToTurn();
        Request<Word> r = new Request<>("place_word", clientName, w);
        try {
            r.sendRequest(new ObjectOutputStream(hs.getOutputStream()));
            //wait for server to send score
            Request serverRespond = utils.getRequestFromInput(hs.getInputStream());
            if (!serverRespond.requestCommand.equals("score")) {
                System.out.println("error in server respond, expected score got: " + serverRespond.requestCommand);
                throw new RuntimeException();
            }
            if ((int) serverRespond.object > 0) {
                System.out.println("client on " + Thread.currentThread().getId() + ": place word successfully");
                //todo check whats happening here if tiles contains a tile from w twice
                this.tiles.remove(w.getTiles());
            }
            return (int) serverRespond.object;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean checkWord(String w) {
        waitToTurn();
        Request<String> r = new Request<>("String", "check_word", w);
        try {
            r.sendRequest(new ObjectOutputStream(hs.getOutputStream()));
            //wait for server to send score
            Request serverRespond = utils.getRequestFromInput(hs.getInputStream());
            if (!serverRespond.requestCommand.equals("checked_word")) {
                System.out.println("error in server respond, expected boolean got: " + serverRespond.requestCommand);
                throw new RuntimeException();
            }
            return (boolean) serverRespond.object;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }



    public static class Request<T extends Serializable> implements Serializable {

        public String requestCommand;
        public T object;
        public String requestArgs;


        // Constructor
        public Request(String requestCommand,String requestArgs, T object) {
            this.requestCommand = requestCommand;
            this.object = object;
            this.requestArgs = requestArgs;
        }

        public void sendRequest(ObjectOutputStream objectOutputStream)  {
            try {
                objectOutputStream.writeUTF(this.requestArgs);
                objectOutputStream.writeUTF(this.requestCommand);
                objectOutputStream.writeObject(this.object);
                //objectOutputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}



