package test;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class BookScrabbleHandler implements ClientHandler{
    PrintWriter out;
    Scanner in;
    DictionaryManager dm;

    public BookScrabbleHandler(){
        dm = DictionaryManager.get();
    }

    private boolean DictionaryManagerQuestion(String input){
        String[] parseInput = input.split(",");
        String command = parseInput[0];
        String[] args = new String[parseInput.length - 1];
        for(int i = 1;i < parseInput.length;i++) {
            args[i-1] = parseInput[i];
        }
        if (command.equals("Q"))
            return dm.query(args);
        else if(command.equals("C"))
            return dm.query(args);
        return false;
    }

    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient) {
        out = new PrintWriter(outToClient);
        in = new Scanner(inFromclient);
        boolean res = DictionaryManagerQuestion(in.next());
        out.println(res);
        out.flush();
    }

    @Override
    public void close() {
        in.close();
        out.close();
    }
}
