package model;

import game_src.ClientHandler;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class GameClientHandler implements ClientHandler{

    PrintWriter outToClient,outToGameServer;
    Scanner in;
    Socket clientSocket;
    boolean hasSocket;
    private ObjectInputStream ois;
    private ObjectOutputStream objectOutToGameServer;
    private ObjectOutputStream objectOutToClient;

    public GameClientHandler(Socket clientSocket) throws IOException {
        hasSocket = false;
        outToClient = new PrintWriter(clientSocket.getOutputStream());
        objectOutToClient = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new Scanner(clientSocket.getInputStream());
        this.clientSocket = clientSocket;
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
        //this.outToClient = new PrintWriter(clientSocket.getOutputStream());
        //this.in = new Scanner(clientSocket.getInputStream());
        //this.ois = new ObjectInputStream(clientSocket.getInputStream());
        this.objectOutToClient = new ObjectOutputStream(clientSocket.getOutputStream());
        this.objectOutToGameServer = new ObjectOutputStream(gameServer.getOutputStream());
        GameClient.Request r = new GameClient.Request("text","your_turn",-1);
        r.sendRequest(objectOutToClient);
        // wait to client to send 'client is done'
        while (true) {
            // sent client request to game server
            try {
                GameClient.Request res = utils.getResponseFromServer1(clientSocket.getInputStream());
                //check if turn ended
                if (res.requestCommand.equals("turnEnded")) {
                    System.out.println("Game Host: client is done, moving to next client");
                    break;
                }

                else {
                    System.out.println("Game Host: client request is: " + r);
                    System.out.println("Game Host: sending client request to game server");
                    res.sendRequest(objectOutToGameServer);
                    // wait for game server response
                    System.out.println("Game Host: waiting for game server response");
                    GameClient.Request serverResponse = utils.getResponseFromServer1(gameServer.getInputStream());
                    System.out.println("Game Host: game server response is: " + serverResponse);
                    // send game server response to client
                    System.out.println("Game Host: sending game server response to client");
                    serverResponse.sendRequest(objectOutToClient);
                }

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
//
//            String clientRequest = utils.getRespondFromServer(clientSocket.getInputStream());
//            System.out.println("Game Host: client request is: " + clientRequest);
//            System.out.println("Game Host: sending client request to game server");
//            outToGameServer = new PrintWriter(gameServer.getOutputStream());
//            outToGameServer.println(clientRequest);
//            outToGameServer.flush();
//
//            // wait for game server response
//            System.out.println("Game Host: waiting for game server response");
//            String serverResponse = utils.getRespondFromServer(gameServer);
//            System.out.println("Game Host: game server response is: " + serverResponse);
//
//            // send game server response to client
//            System.out.println("Game Host: sending game server response to client");
//            this.outToClient.println(serverResponse);
//            this.outToClient.flush();
//
//            //check if client is done
//            if (in.nextLine().equals("turnEnded#")) {
//                System.out.println("Game Host: client is done, moving to next client");
//                break;
//            }
        }
    }


    void sendToClient(String msg){
        GameClient.Request r = new GameClient.Request("text",msg,-1);
        r.sendRequest(objectOutToClient);
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
