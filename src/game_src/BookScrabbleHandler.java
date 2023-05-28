package game_src;


import model.GameClient;
import model.ScoreTable;
import model.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.io.*;

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


    private void parseRequest(GameClient.Request request) {
        try {
            String command = request.requestCommand;
            if (command.equals("get_board")) send_board();
            else if (command.equals("place_word")) place((Word) request.object, request.requestArgs);
            else if (command.equals("get_tile")) send_tile();
            else if (command.equals("check_word")) check_word((String) request.object);
            else if (command.equals("get_score_table")) send_score_table();



        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void send_score_table() {
        GameClient.Request<ScoreTable> r = new GameClient.Request<>( "score_table","score_table", scoreTable);
    }

    private void check_word(String word) throws IOException {
        boolean res =  dm.challenge(word);
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
    public void handleClient(InputStream inFromclient, OutputStream outToClient, Socket serverSocket) {

    }

    @Override
    public void handleClient(Socket clientSocket, Socket serverSocket) throws IOException {
    }

    @Override
    public void close() {
        dm.closeLibrary();
    }
}
