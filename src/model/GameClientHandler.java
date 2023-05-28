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
    private ObjectOutputStream oos;

    public GameClientHandler(Socket clientSocket) throws IOException {
        hasSocket = false;
        outToClient = new PrintWriter(clientSocket.getOutputStream());
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
        this.outToClient = new PrintWriter(clientSocket.getOutputStream());
        this.in = new Scanner(clientSocket.getInputStream());
        //this.ois = new ObjectInputStream(clientSocket.getInputStream());
        this.oos = new ObjectOutputStream(clientSocket.getOutputStream());
        this.outToClient.println("your turn#");
        this.outToClient.flush();
        // wait to client to send 'client is done'
        while (true) {
            // sent client request to game server
            try {
                GameClient.Request r = (GameClient.Request) utils.getResponseFromServer1(clientSocket);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            String clientRequest = utils.getRespondFromServer(clientSocket.getInputStream());
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

            //check if client is done
            if (in.nextLine().equals("turnEnded#")) {
                System.out.println("Game Host: client is done, moving to next client");
                break;
            }
        }
    }


    void sendToClient(String msg){
        outToClient.println(msg);
        outToClient.flush();
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
