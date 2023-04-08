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

    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient) {
        out = new PrintWriter(outToClient);
        in = new Scanner(inFromclient);
        String text = in.next();
        String[] parseText = text.split(",");
        String command = parseText[0];
        String[] args = new String[parseText.length - 1];
        boolean res;
        for(int i = 1;i<parseText.length;i++) {
            args[i-1] = parseText[i];
        }
        if (command.equals("Q"))
            res = dm.query(args);
        else if(command.equals("C"))
            res = dm.query(args);
        else
            res = false;
        out.println(res);
        out.flush();
    }

    @Override
    public void close() {
        in.close();
        out.close();
    }
}
