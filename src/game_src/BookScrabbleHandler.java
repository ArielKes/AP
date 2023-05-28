package game_src;


import model.GameClient;
import model.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.io.*;

public class BookScrabbleHandler implements ClientHandler{
    PrintWriter out;
    Scanner in;
    ObjectInputStream ois = null;
    ObjectOutputStream oos = null;
    DictionaryManager dm;
    Board board;


    Tile.Bag bag;
    public BookScrabbleHandler(){
        dm = DictionaryManager.get();
        board = new Board();
        bag = new Tile.Bag();
    }

    private boolean DictionaryManagerHandler(String input){
        //get input as string of args with "," as separators
        //for example: input:"command, text file 1, ..., text file i, question for the dictionary"
        String[] parseInput = input.split(",");
        System.out.println("gameServer printing "+in);
        //the first arg in the input is command to the Dictionary Manager
        String command = parseInput[0];

        //the other args from the input souled be passed down to the
        //Dictionary Manager for it to use
        String[] args = new String[parseInput.length - 1];
        for(int i = 1;i < parseInput.length;i++) {
            args[i-1] = parseInput[i];
        }

        //here we use the command to decide what the Dictionary Manager will do
        if (command.equals("Q"))
            return dm.query(args);
        else if(command.equals("C"))
            return dm.challenge(args);
        //if for some reason the command is not Q, or C as we acspected the
        //function will return 'false'
        return false;
    }

    private void parseRequest(GameClient.Request request){
        String command = request.requestCommand;
        if (command.contains("get_board")) {
            send_board();
        }
        if (command.contains("get_dictionary")) {
            //send_dictionary();
        }
        if (command.contains("placeWord")) {
            place();
        }
        if (command.contains("get_tile")) {
            send_tiles();
        }
    }

    private void send_tiles() {
        GameClient.Request r = new GameClient.Request("tile","sent_tile",bag.getRand());
        r.sendRequest(oos);
    }

    private  void place(){
        Word w = null;
        try {
            w = (Word) ois.readObject();
            int score = board.tryPlaceWord(w);
            if(score != -1){
                out.println("score#"+score);
                out.flush();
            }
            else{
                out.println("not placed#");
                out.flush();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }
    private void send_board(){
        System.out.println("sending board");
        out.println(board.get_as_string());
        out.flush();
    }

    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient) throws IOException {
        // get input from client (Game Host) and send it to the Dictionary Manager or the Board
        out = new PrintWriter(outToClient);
        in = new Scanner(inFromclient);
//        this.ois = new ObjectInputStream(inFromclient);
        this.oos = new ObjectOutputStream(outToClient);
        while (true) {
            GameClient.Request clientRequest = null;
            try {
                clientRequest = utils.getResponseFromServer1(inFromclient);
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
        in.close();
        out.close();
    }
}
