package model;

import game_src.Tile;
import game_src.Word;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class GameClient extends Observable implements Model{

    // Connection variables
    protected Socket hs;
    private HashMap<String, String> properties;
//    ObjectOutputStream objectOutputStream;


    // administration variables
    private volatile boolean myTurn = false;
    private volatile boolean update = false;
    private volatile boolean isGameStarted = false;

    // Game variables
    String clientName;
    List<Tile> tiles = new ArrayList<>();
    String boardString;
    ScoreTable scoreTable;



    private void basicConstructor(String clientName) throws IOException {
        this.clientName = clientName;
        new Thread( ()->{listenToTurn();}).start();
        initTiles();
        boardString = getBoard();
        int a = placeWord(new Word(new Tile[]{this.tiles.get(0)}, 7, 7, true));
        this.endTurn();

    }

    @Override
    public HashMap<String, Integer> getScoreTableHashMap() {
        return scoreTable.getScores();
    }

    @Override
    public String getBoardString() {
        return boardString;
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

    void waitToTurnOrUpdate(){
        while (!this.myTurn&&!this.update) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("client on " + Thread.currentThread().getId() + ": waitToTurn");
            }
        }
    }
    void waitToTurn(){
        while (!this.myTurn) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("client on " + Thread.currentThread().getId() + ": waitToTurn");
            }
        }
    }

    void listenToTurn()  {
        this.isGameStarted = true;
        while(this.myTurn || this.update) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("client on " + Thread.currentThread().getId() + ": listenToTurn");
            }
        }
        if(!this.myTurn && !this.update) {
            Request r ;
            try {
                r = utils.getRequestFromInput(hs.getInputStream());
                if (r.requestCommand.equals("your_turn")) {
                    System.out.println("client on " + Thread.currentThread().getId() + ": got the turn");
                    this.myTurn = true;
                    this.notifyViewModel();
                }
                else if (r.requestCommand.equals("update")) {
                    System.out.println("client on " + Thread.currentThread().getId() + ": got update command");
                    this.update = true;
                    this.getBoard();
                    this.getScoreTable();
                    Request<Integer> res = new Request<>("update_done", "command", -1);
                    res.sendRequest(new ObjectOutputStream(hs.getOutputStream()));
                    this.update = false;
                    this.notifyViewModel();
                    listenToTurn();

                }
                else {
                    System.out.println("client on " + Thread.currentThread().getId());
                }

            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            listenToTurn();
        }
    }


    @Override
    public boolean isMyTurn() {
        return this.myTurn;
    }

    @Override
    public void endTurn() {
        waitToTurn();
        Request<Integer> r = new Request<Integer>("turn_ended","command", -1);
        try {
            r.sendRequest(new ObjectOutputStream(hs.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.myTurn = false;
        this.notifyViewModel();
    }

    public String getBoard() {
        waitToTurnOrUpdate();
        Request<Integer> r = new Request<Integer>("get_board", "command", -1);
        //wait for server to send score
        try {
            r.sendRequest(new ObjectOutputStream(hs.getOutputStream()));
            Request respond = utils.getRequestFromInput(hs.getInputStream());
            System.out.println("client on " + Thread.currentThread().getId()+ " - server respond: " + respond.object);
            boardString = (String) respond.object;
            return (String) respond.object;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public void notifyViewModel() {
        setChanged();
        notifyObservers();
        System.out.println("client on " + Thread.currentThread().getId() + ": notified view model");
    }

    @Override
    public HashMap<String, Integer> getScoreTable() {
        waitToTurnOrUpdate();
        Request<Integer> r = new Request<Integer>("get_score_table", "command", -1);
        try {
            r.sendRequest(new ObjectOutputStream(hs.getOutputStream()));
            Request respond = utils.getRequestFromInput(hs.getInputStream());
            if (!respond.requestCommand.equals("score_table")) {
                System.out.println("error in server respond, expected score table got: " + respond.requestCommand);
                throw new RuntimeException();
            }
            ScoreTable s = (ScoreTable) respond.object;
            this.scoreTable = s;
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
    public void updateClient() {
        waitToTurn();
        Request<Integer> r = new Request<>("update", "command", -1);
        try {
            r.sendRequest(new ObjectOutputStream(hs.getOutputStream()));
            Request respond = utils.getRequestFromInput(hs.getInputStream());
            if (!respond.requestCommand.equals("update_done")) {
                System.out.println("error in server respond, expected update done got: " + respond.requestCommand);
                throw new RuntimeException();
            }
            else {
                System.out.println("client on " + Thread.currentThread().getId() + ": got update done");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean isGameStarted() {
        return this.isGameStarted;
    }

    @Override
    public void addTile() {
        waitToTurn();
        getNTiles(1);
        this.endTurn();
    }

    private Tile getTile() {
        waitToTurn();
        Request<String> r = new Request<>( "get_tile","String",clientName );
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
    private void initTiles() {
        waitToTurn();
        Request<String> r = new Request<>( "init_tiles","clientName", clientName);
        Request respond;
        try {
            r.sendRequest(new ObjectOutputStream(hs.getOutputStream()));
            respond = utils.getRequestFromInput(hs.getInputStream());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (!respond.requestCommand.equals("sent_initial_tiles")) {
            System.out.println("error in server respond, expected init_tiles got: " + respond.requestCommand);
            throw new RuntimeException();
        }
        // loop through the tiles sent from the server and add them to the client's tiles
        tiles.addAll((List<Tile>) respond.object);

    }
    private void getNTiles(int n) {
        waitToTurn();
        for (int i = 0; i < n; i++) {
            tiles.add(getTile());
        }
        this.notifyViewModel();
    }


    @Override
    public int placeWord(Word w) {
        // if the score is positive, the word was placed successfully and the turn ended automatically
        waitToTurn();
        Request<Word> r = new Request<>("place_word", clientName, w);
        try {
            r.sendRequest(new ObjectOutputStream(hs.getOutputStream()));
            //wait for server to send score
            Request serverRespond = utils.getRequestFromInput(hs.getInputStream());
            if (!serverRespond.requestCommand.equals("score")) {
                System.out.println("error in server respond, expected score got: " + serverRespond.requestCommand);
                //throw new RuntimeException();
            }
            if ((int) serverRespond.object > 0) {
                System.out.println("client on " + Thread.currentThread().getId() + ": place word successfully");
                for(Tile t : w.getTiles()) {
                    this.tiles.remove(t);
                }
                this.endTurn();
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
        Request<String> r = new Request<>("check_word", clientName, w);
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

    public static class Request<T> implements Serializable {

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



