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
        for(int i = 1;i<parseText.length;i++) {
            args[i-1] = parseText[i];
        }
        if (command.equals("Q"))
            dm.query(args);
        else if(command.equals("C"))
            dm.query(args);
        else
            System.out.println("wrong argument");

        out.println(new StringBuilder(text).reverse().toString());
        out.flush();
    }

    @Override
    public void close() {
        in.close();
        out.close();
    }
}
