package model;
import game_src.ClientHandler;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class GameClientHandler implements ClientHandler {

    GameClientHandler(Socket clientSocket){



    }
    @Override
    void handle(InputStream inFromclient, OutputStream outToClient){

    }



    @Override
    public void close() {

    }

}
