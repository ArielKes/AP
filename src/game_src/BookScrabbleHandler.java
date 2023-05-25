package game_src;


import model.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class BookScrabbleHandler implements ClientHandler{
    PrintWriter out;
    Scanner in;
    DictionaryManager dm;
    Board board;
    public BookScrabbleHandler(){
        dm = DictionaryManager.get();
        board = new Board();
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

    private void parseRequest(String request){
        String[] parseInput = request.split("#");
        String command = parseInput[0];
        if (command.contains("get_board")) {
            send_board();
        }
        if (command.contains("get_dictionary")) {
            send_dictionary();
        }
    }
    private  void place(String w){
         int score = board.tryPlaceWord(w);
            if(score != -1){
                out.println("score#"+score);
                out.flush();
            }
            else{
                out.println("not placed#");
                out.flush();
            }

    }
    private void send_board(){
        System.out.println("sending board");
        out.println(board.get_as_string());
        out.flush();
    }

    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient) {
        // get input from client (Game Host) and send it to the Dictionary Manager or the Board
        out = new PrintWriter(outToClient);
        in = new Scanner(inFromclient);
        while (true) {
            String clientRequest = utils.getRespondFromServer(inFromclient);
            System.out.println("Game Server: client request is - " + clientRequest);
            parseRequest(clientRequest);
//            out.println("Hi I got your message");
//            out.flush();
//            boolean res = DictionaryManagerHandler(in.next());
//            out.println(res);
//            out.flush();
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
