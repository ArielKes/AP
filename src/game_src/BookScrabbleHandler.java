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
import java.util.HashMap;
import java.util.List;

public class BookScrabbleHandler implements ClientHandler {
    DictionaryManager dm;
    Board board;
    OutputStream out;
    InputStream in;
    Tile.Bag bag;
    ScoreTable scoreTable;

    public BookScrabbleHandler() {
        dm = DictionaryManager.get();
        board = new Board();
        bag = new Tile.Bag();
        scoreTable = new ScoreTable();
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
    //public HashMap<String, List<Tile>> getPlayersTilesFromDB(String gameID){
    //}



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
        collection.insertOne(document);
        mongoClient.close();
    }

    public void saveToDB(String gameID) {
        saveGameStateToDB(gameID);
        saveBoardToDB(gameID);
    }

/*
    //TODO: save board(string), save score table(HashMap<String,Integer>), save users Tiles(List<Tile>),

    public String getFromDB(String gameID) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Scrabble");
        Bson query = Filters.eq("Game Index", this.gameIndex);
        MongoCollection<Document> collection = database.getCollection("GameState");
        // Execute the query and retrieve the result
        Document result = collection.find(query).first();
        String ResumeCurrentPlayer = null;
        if (result != null) {
            // Access the retrieved fields from the document
            ResumeCurrentPlayer = result.getString("Test");
        }
        mongoClient.close();
        return ResumeCurrentPlayer;
    }
*/
    private void parseRequest(GameClient.Request request) {
        try {
            String command = request.requestCommand;
            if (command.equals("get_board")){
                getScoreTableFromDB("0");
                send_board();
            }
            else if (command.equals("place_word")) place((Word) request.object, request.requestArgs);
            else if (command.equals("get_tile")) send_tile();
            else if (command.equals("check_word")) check_word((String) request.object, request.requestArgs);
            else if (command.equals("get_score_table")) send_score_table();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void send_score_table() throws IOException {
        GameClient.Request<ScoreTable> r = new GameClient.Request<>( "score_table","score_table", scoreTable);
        r.sendRequest(new ObjectOutputStream(out));
    }

    private void check_word(String word,String clientName) throws IOException {
        boolean res =  dm.challenge("/Users/shlomo/IdeaProjects/AP/src/resources/words_alpha.txt",word);
        if (!res){
            scoreTable.addScore(clientName,-5);
        }
        else {
            scoreTable.addScore(clientName,5);
        }
        GameClient.Request<Boolean> r = new GameClient.Request<>( "checked_word","boolean", res);
        r.sendRequest(new ObjectOutputStream(out));
    }

    private void send_tile() throws IOException {
        GameClient.Request<Tile> r = new GameClient.Request<>( "sent_tile","tile", bag.getRand());
        r.sendRequest(new ObjectOutputStream(out));
    }

    private void place(Word w,String clientName) throws IOException {
        int score = board.tryPlaceWord(w,dm);
        scoreTable.addScore(clientName,score);
        System.out.println("Score Table :" + scoreTable.toString());
        System.out.println("Board: " + board.get_as_string());
        GameClient.Request<Integer> r = new GameClient.Request<>( "score","int", score);
        r.sendRequest(new ObjectOutputStream(out));
    }

    private void send_board() throws IOException {
        System.out.println("sending board");
        GameClient.Request<String> r = new GameClient.Request<>( "sent_board","board", board.get_as_string());
        r.sendRequest(new ObjectOutputStream(out));
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
