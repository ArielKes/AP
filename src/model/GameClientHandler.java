package model;

import game_src.ClientHandler;
import game_src.MyServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GameClientHandler implements ClientHandler{

    PrintWriter out;
    Scanner in;


    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient) {

    }

    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient, Socket gameServer) {
        out = new PrintWriter(outToClient);//writing to client
        in = new Scanner(inFromclient);//receiving from client
        PrintWriter a = null;
        try {
            a = new PrintWriter(gameServer.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String line = null;
        if (in.hasNext()) {
            line = in.nextLine();
            System.out.println(line);
        }
        a.println(line);
        System.out.println("host printing "+line);
        out.println();
        out.flush();
    }

    @Override
    public void close() {
        in.close();
        out.close();
    }
}
