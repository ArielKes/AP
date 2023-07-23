package game_src;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import model.GameClient;
import model.ScoreTable;
import model.utils;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BookScrabbleHandler implements ClientHandler {
    DictionaryManager dm;
    Board board;
    OutputStream out;
    InputStream in;
    Tile.Bag bag;
    ScoreTable scoreTable;
    HashMap<String, List<Tile>> playersTiles;

    public BookScrabbleHandler() {
        dm = DictionaryManager.get();
        board = new Board();
        bag = new Tile.Bag();
        scoreTable = new ScoreTable();
        playersTiles = new HashMap<>();
    }

    public BookScrabbleHandler(String GameID) {
        dm = DictionaryManager.get();
        board = new Board();
        bag = new Tile.Bag();
    }
    void loadGame(String GameID){
        if (GameID.equals("0")) return;
        loadBoard(GameID);
        loadScoreBoard(GameID);
        loadPlayersTiles(GameID);
    }

    public String getBoardStringFromDB(String gameID){
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Scrabble");
        MongoCollection<Document> collection = database.getCollection("GameBoard");
        Bson query = Filters.eq("Game Index", gameID);
        Document result = collection.find(query).sort(Sorts.descending("_id")).first();
        String boardResult = null;
        if (result != null) {
            // Access the retrieved fields from the document
            boardResult = result.getString("Board");
        }
        mongoClient.close();
        return boardResult;
    }

    public HashMap<String,Integer> getScoreTableFromDB(String gameID){
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Scrabble");
        MongoCollection<Document> collection = database.getCollection("GameState");
        Bson query = Filters.eq("Game Index", gameID);
        Document result = collection.find(query).sort(Sorts.descending("_id")).first();
        HashMap<String,Integer> gameStateResult = new HashMap<String,Integer>();
        if (result != null) {
            // Access the retrieved fields from the document
            String resultString = result.getString("Score Table");
            gameStateResult = utils.convertStringToHashMap(resultString);
        }
        mongoClient.close();
        return gameStateResult;
    }

    public HashMap<String, List<Tile>> getPlayersTilesFromDB(String gameID){
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Scrabble");
        MongoCollection<Document> collection = database.getCollection("GameState");
        Bson query = Filters.eq("Game Index", gameID);
        Document result = collection.find(query).sort(Sorts.descending("_id")).first();
        HashMap<String, List<Tile>> playersTilesResult = new HashMap<String, List<Tile>>();
        if (result != null) {
            // Access the retrieved fields from the document
            String resultString = result.getString("Players Tile");
            playersTilesResult = convertStringToPlayersTiles(resultString);
        }
        mongoClient.close();
        return playersTilesResult;
    }

    public void saveBoardToDB(String gameID) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Scrabble");
        MongoCollection<Document> collection = database.getCollection("GameBoard");
        Document document = new Document("Game Index" , gameID);
        document.append("Board", this.board.get_as_string());
        collection.insertOne(document);
        mongoClient.close();
    }

    public void saveGameStateToDB(String gameID) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Scrabble");
        MongoCollection<Document> collection = database.getCollection("GameState");
        Document document = new Document("Game Index" , gameID);
        document.append("Score Table", this.scoreTable.toString());
        document.append("Players Tile", convertPlayersTilesToString());
        collection.insertOne(document);
        mongoClient.close();
    }

    private String convertPlayersTilesToString(){
        String result = "";
        for (String key : playersTiles.keySet()) {
            result += key+ ":";
            for (Tile tile : playersTiles.get(key)) {
                result += tile.letter + ",";
            }
            result += ";";
        }
        return result;
    }

    private HashMap<String, List<Tile>> convertStringToPlayersTiles(String playersTilesString){
        HashMap<String, List<Tile>> p = new HashMap<String, List<Tile>>();
        String[] playersTilesArray = playersTilesString.split(";");
        for (String playerTiles : playersTilesArray) {
            if(playerTiles.equals("")) continue;
            String[] playerTilesArray = playerTiles.split(":");
            String playerName = playerTilesArray[0];
            String[] tilesArray = playerTilesArray[1].split(",");
            List<Tile> tiles = new ArrayList<>();
            for (String tile : tilesArray) {
                if(tile.equals("")) continue;
                tiles.add(bag.getTile(tile.charAt(0)));
            }
            p.put(playerName, tiles);
        }
        return p;
    }

    public void saveToDB(String gameID) {
        if (gameID.equals("0")) return;
        saveGameStateToDB(gameID);
        saveBoardToDB(gameID);
    }


    private void parseRequest(GameClient.Request request) {
        try {
            String command = request.requestCommand;
            if (command.equals("load_game_ID")) loadGame(request.requestArgs);
            else if (command.equals("get_board")) send_board();
            else if (command.equals("place_word")) place((Word) request.object, request.requestArgs);
            else if (command.equals("get_tile")) send_tile((String) request.object);
            else if (command.equals("check_word")) check_word((String) request.object, request.requestArgs);
            else if (command.equals("get_score_table")) send_score_table();
            else if (command.equals("init_tiles")) send_initial_tiles((String) request.object);
            else if (command.equals("save_state")) saveToDB(request.requestArgs);



        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void send_score_table() throws IOException {
        GameClient.Request<ScoreTable> r = new GameClient.Request<>(
                "score_table", "score_table", scoreTable);
        r.sendRequest(new ObjectOutputStream(out));
    }

    private void check_word(String word, String clientName) throws IOException {
        boolean res = dm.challenge("/Users/shlomo/IdeaProjects/AP/src/resources/words_alpha.txt", word);
        if (!res) {
            scoreTable.addScore(clientName, -5);
        } else {
            scoreTable.addScore(clientName, 5);
        }
        GameClient.Request<Boolean> r = new GameClient.Request<>(
                "checked_word", "boolean", res);
        r.sendRequest(new ObjectOutputStream(out));
    }

    private void send_tile(String clientName) throws IOException {
        Tile t = bag.getRand();
        playersTiles.get(clientName).add(t);
        GameClient.Request<Tile> r = new GameClient.Request<>(
                "sent_tile", "tile", t);
        r.sendRequest(new ObjectOutputStream(out));
    }

    private void send_initial_tiles(String clientName) throws IOException {
        if (!playersTiles.containsKey(clientName)) {
            List<Tile> t = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                t.add(bag.getRand());
            }
            playersTiles.put(clientName, t);
        }
        GameClient.Request<List<Tile>> r = new GameClient.Request<>(
                "sent_initial_tiles", "tiles", playersTiles.get(clientName));
        r.sendRequest(new ObjectOutputStream(out));

    }

    private void place(Word w, String clientName) throws IOException {
        int score = board.tryPlaceWord(w, dm);
        if (score > 0){
            for (Tile t : w.getTiles()){
                playersTiles.get(clientName).remove(t);
            }
        }
        scoreTable.addScore(clientName, score);
        System.out.println("Score Table :" + scoreTable.toString());
        System.out.println("Board: " + board.get_as_string());
        GameClient.Request<Integer> r = new GameClient.Request<>("score", "int", score);
        r.sendRequest(new ObjectOutputStream(out));
    }

    private void send_board() throws IOException {
        System.out.println("sending board");
        GameClient.Request<String> r = new GameClient.Request<>(
                "sent_board", "board", board.get_as_string());
        r.sendRequest(new ObjectOutputStream(out));
    }

    public void loadBoard(String gameID) {
        String board_str = getBoardStringFromDB(gameID);
        if (board_str == null) return;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                char c = board_str.charAt(i * 15 + j);
                if (c != '_') {
                    board.tiles[i][j] = bag.getTile(c);
                    board.isEmpty = false;
                }
            }
        }
    }

    public void loadScoreBoard(String gameID) {
        HashMap<String, Integer> s = getScoreTableFromDB(gameID);
        if (s == null) return;
        scoreTable.scores = s;
    }

    private void loadPlayersTiles(String gameID) {
        HashMap<String, List<Tile>> p = getPlayersTilesFromDB(gameID);
        if (p == null) return;
        playersTiles = p;// getPlayersTilesFromDB(gameID);
    }

    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) throws IOException {
        // get input from client (Game Host) and send it to the Dictionary Manager or the Board
        this.out = outToClient;
        this.in = inFromClient;

        while (true) {
            GameClient.Request clientRequest = null;
            try {
                clientRequest = utils.getRequestFromInput(inFromClient);
                System.out.println("Game Server: client request is - " + clientRequest.requestCommand);
                parseRequest(clientRequest);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
    }


    @Override
    public void handleClient(Socket clientSocket, Socket serverSocket) throws IOException {
    }

    @Override
    public void close() {
        dm.closeLibrary();
    }
}
