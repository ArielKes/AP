package model;

import game_src.ClientHandler;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class GameClientHandler implements ClientHandler{

    PrintWriter outToClient;
    Scanner in;
    Socket clientSocket, gameServer;

    public GameClientHandler(Socket clientSocket) {
    }


    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {

    }


    @Override
    public void handleClient(Socket clientSocket, Socket gameServer) throws IOException {
        this.clientSocket = clientSocket;
        this.gameServer = gameServer;
        //send input from client to server and vice versa
        GameClient.Request<Integer> r = new GameClient.Request<>( "your_turn","String",-1);
        r.sendRequest(new ObjectOutputStream(clientSocket.getOutputStream()));
        // wait to client to send 'client is done'
        interactWithClient(clientSocket,gameServer);
    }

    public void interactWithClient(Socket clientSocket,Socket gameServer) {
        this.clientSocket = clientSocket;
        this.gameServer = gameServer;
        while (true) {
            // sent client request to game server
            try {
                GameClient.Request res = utils.getRequestFromInput(clientSocket.getInputStream());
                //check if turn ended
                if ((res.requestCommand.equals("turn_ended")) || (res.requestCommand.equals("update_done"))) {
                    System.out.println("Game Host: client is done, moving to next client");
                    break;
                }

                else {
                    System.out.println("Game Host: client request is: " + res.requestCommand);
                    System.out.println("Game Host: sending client request to game server");
                    res.sendRequest(new ObjectOutputStream(gameServer.getOutputStream()));
                    // wait for game server response
                    System.out.println("Game Host: waiting for game server response");
                    GameClient.Request serverResponse = utils.getRequestFromInput(gameServer.getInputStream());
                    System.out.println("Game Host: game server response is: " + serverResponse.requestCommand);
                    // send game server response to client
                    System.out.println("Game Host: sending game server response to client");
                    serverResponse.sendRequest(new ObjectOutputStream(clientSocket.getOutputStream()));
                }

            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public void updateClient(String board) throws IOException {
        GameClient.Request<Integer> r = new GameClient.Request<>("update","String",-1);
        r.sendRequest(new ObjectOutputStream(clientSocket.getOutputStream()));

    }


    void sendToClient(String msg) throws IOException {
        GameClient.Request<Integer> r = new GameClient.Request<>(msg,"String",-1);
        r.sendRequest(new ObjectOutputStream(clientSocket.getOutputStream()));
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
