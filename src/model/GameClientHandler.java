package model;

import game_src.ClientHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GameClientHandler implements ClientHandler{

    PrintWriter outToClient,outToGameServer;
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
    public void handleClient(InputStream inFromclient, OutputStream outToClient, Socket gameServer) throws IOException {
    }

    @Override
    public void handleClient(Socket clientSocket, Socket gameServer) throws IOException {
        //send input from client to server and vice versa
        this.outToClient = new PrintWriter(clientSocket.getOutputStream());
        this.in = new Scanner(clientSocket.getInputStream());
        this.outToClient.println("your turn#");
        this.outToClient.flush();
        // wait to client to send 'client is done'
        while (true) {
            // sent client request to game server
            String clientRequest = utils.getRespondFromServer(clientSocket);
            System.out.println("Game Host: client request is: " + clientRequest);
            System.out.println("Game Host: sending client request to game server");
            outToGameServer = new PrintWriter(gameServer.getOutputStream());
            outToGameServer.println(clientRequest);
            outToGameServer.flush();
            // wait for game server response
            System.out.println("Game Host: waiting for game server response");
            String serverResponse = utils.getRespondFromServer(gameServer);
            System.out.println("Game Host: game server response is: " + serverResponse);
            // send game server response to client
            System.out.println("Game Host: sending game server response to client");
            this.outToClient.println(serverResponse);
            this.outToClient.flush();
            // check if client is done
            if (in.nextLine().equals("done#")) {
                System.out.println("Game Host: client is done, moving to next client");
                break;
            }
        }
    }

    @Override
    public void close() {
        if(in!=null) {
            in.close();
        }
        if(outToClient !=null) {
            outToClient.close();
        }
    }
}
