package game_src;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
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

    @Override
    public void handle(InputStream inFromclient, OutputStream outToClient) {
        out = new PrintWriter(outToClient);
        in = new Scanner(inFromclient);
        boolean res = DictionaryManagerHandler(in.next());
        out.println(res);
        out.flush();
    }

    @Override
    public void close() {
        dm.closeLibrary();
        in.close();
        out.close();
    }
}
