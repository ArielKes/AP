package model;

import model.utilObjects.PlaceWord;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.lang.management.ManagementFactory;

import static model.utils.*;

public class GameClient{
    protected final Socket hs;
    private final HashMap<String, String> properties;

    private volatile boolean myTurn = false;
    PrintWriter out;

    ObjectOutputStream objectOutputStream;
    //Scanner in;

    public GameClient() throws IOException {
        properties = utils.getProperties("src/resources/properties.txt");
        hs = getGameHostSocket();
        objectOutputStream = new ObjectOutputStream(hs.getOutputStream());
        this.out = new PrintWriter(hs.getOutputStream());

        this.listenToMyTurn();
    }






    Socket getGameHostSocket() throws IOException {
        assert properties != null;
        int port = Integer.parseInt(properties.get("game_host.port"));
        return new Socket(properties.get("game_host.ip"), port);
    }




    int placeWordOnBoard(String w) throws IOException, InterruptedException {
        // -1 if word is not valid
        // -2 if word is valid but not in dictionary
        // n if word is valid and in dictionary and n is the score
        waitToTurn();
        this.out.println("place_word#"+w);
        this.out.flush();
        //wait for server to send score
        String serverRespond = utils.getRespondFromServer(hs);
        if (!serverRespond.split("#")[0].equals("score")) {
            System.out.println("error in server respond, expected score got: "+serverRespond.split("#")[0]);
            throw new IOException();
        }
        return Integer.parseInt(serverRespond.split("#")[1]);
    }
//    int placeWordOnBoard(PlaceWord w) throws IOException {
//        // -1 if word is not valid
//        // -2 if word is valid but not in dictionary
//        // n if word is valid and in dictionary and n is the score
//        waitToTurn();
//        int score = -1;
//        //sent word
//        objectOutputStream.writeObject(w);
//        //wait for server to send score
//        String serverRespond = utils.getRespondFromServer(hs);
//        if (!serverRespond.split("#")[0].equals("score")) {
//            System.out.println("error in server respond");
//            throw new IOException();
//        }
//        return Integer.parseInt(serverRespond.split("#")[1]);
//
//    }

    void listenToMyTurn(){
        // start new thread to listen to server
        // if server send 'your turn' then set myTurn to true
        // else set myTurn to false
        Thread clientThread = new Thread(() -> {
            while (true) {
                String serverRespond = utils.getRespondFromServer(hs);
                if (serverRespond.equals("your turn#")) {
                    System.out.println("client on " + Thread.currentThread().getId() + ": got the turn");
                    this.myTurn = true;
                }
//                while (this.myTurn){
//
//
//                }
            }
        });
        clientThread.start();
    }

    void waitToTurn() throws InterruptedException {
        while (!myTurn){
        }// wait here as long as it is not my turn
        System.out.println("client: start my turn");

    }


    public void getBoard() throws InterruptedException {
        waitToTurn();
        this.out.println("get_board#");
        this.out.flush();
        //wait for server to send score
        String serverRespond = utils.getRespondFromServer(hs);
        System.out.println("server respond: "+serverRespond);
    }
}



