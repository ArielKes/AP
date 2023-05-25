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
    //static int i;
    boolean hasSocket;

    public GameClientHandler(){
        hasSocket = false;
    }


    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient) {

    }

    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient, Socket gameServer) {

        //send input from client to server and vice versa
        out = new PrintWriter(outToClient);
        in = new Scanner(inFromclient);
        out.println("it's your turn");
        out.flush();
        // wait to clinet to send 'client is done'
        while (true) {
            if (in.nextLine().equals("client is done")) {
                System.out.println("Game Host: " + " got the turn");
                out.println("server" + " is done");
                out.flush();
            }
        }


    }

    @Override
    public void close() {
        if(in!=null) {
            in.close();
        }
        if(out!=null) {
            out.close();
        }
    }
}
