package game_src;


import model.GameClient;
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

    public BookScrabbleHandler() {
        dm = DictionaryManager.get();
        board = new Board();
        bag = new Tile.Bag();
    }

    private boolean DictionaryManagerHandler(String input) {
        //get input as string of args with "," as separators
        //for example: input:"command, text file 1, ..., text file i, question for the dictionary"
        String[] parseInput = input.split(",");
//        System.out.println("gameServer printing "+in);
        //the first arg in the input is command to the Dictionary Manager
        String command = parseInput[0];

        //the other args from the input souled be passed down to the
        //Dictionary Manager for it to use
        String[] args = new String[parseInput.length - 1];
        for (int i = 1; i < parseInput.length; i++) {
            args[i - 1] = parseInput[i];
        }

        //here we use the command to decide what the Dictionary Manager will do
        if (command.equals("Q"))
            return dm.query(args);
        else if (command.equals("C"))
            return dm.challenge(args);
        //if for some reason the command is not Q, or C as we acspected the
        //function will return 'false'
        return false;
    }

    private void parseRequest(GameClient.Request request) {
        try {
            String command = request.requestCommand;
            if (command.equals("get_board")) send_board();
            else if (command.equals("place_word")) place((Word) request.object);
            else if (command.equals("get_tile")) send_tiles();
            else if (command.equals("check_word")) check_word((String) request.object);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void check_word(String word) throws IOException {
        boolean res =  dm.challenge(word);
        GameClient.Request<Boolean> r = new GameClient.Request<>("boolean", "checked_word", res);
        r.sendRequest(new ObjectOutputStream(out));
    }

    private void send_tiles() throws IOException {
        GameClient.Request<Tile> r = new GameClient.Request<>("tile", "sent_tile", bag.getRand());
        r.sendRequest(new ObjectOutputStream(out));
    }

    private void place(Word w) throws IOException {
        int score = board.tryPlaceWord(w);
        GameClient.Request<Integer> r = new GameClient.Request<>("int", "score", score);
        r.sendRequest(new ObjectOutputStream(out));
    }

    private void send_board() throws IOException {
        System.out.println("sending board");
        GameClient.Request<String> r = new GameClient.Request<>("board", "sent_board", board.get_as_string());
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
                System.out.println("Game Server: client request is - " + clientRequest);
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
