package view_model;

import game_src.BookScrabbleHandler;
import model.GameClient;
import model.GameHost;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class GameServerConnection {
    GameHost gameHost = null;
    GameClient gameClient = null;

    ViewModel vm=null;
    public void connectToServer(boolean isNewGame){
        if(isNewGame) {
            try {
                setGameHost();
            }catch (Exception e){}
        }
        else {
            try {
                setGameClient("1");
            }catch (Exception e){}
        }
    }

    public boolean StartGame(){
        if(this.gameHost != null) {
            this.gameHost.startGame();
            return true;
        }
        return false;
    }

    public boolean isGameStart(){
        if(this.gameClient!=null) {
            this.vm = new ViewModel(gameClient);
            return this.gameClient.isGameStarted();
        }
        return false;
    }

    public void setGameHost() throws IOException, InterruptedException {
        this.gameHost = new GameHost("src/resources/properties.txt");
        this.gameHost.start();
        sleep(1000);
    }

    public void setGameClient(String name) throws IOException {
        new Thread(() -> {
            try {
                gameClient = new GameClient(name);
                gameClient.getBoard();
                while(!gameClient.isGameStarted()){}
            } catch (Exception e) {e.printStackTrace();}
        }).start();

    }

    public ViewModel getVM() {
        return this.vm;
    }
}
