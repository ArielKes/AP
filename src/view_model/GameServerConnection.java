package view_model;

import model.GameClient;
import model.GameHost;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class GameServerConnection {
    GameHost gameHost = null;
    GameClient gameClient = null;

    String name = new String("Player");
    int nameIndex = 0;

    ViewModel vm=null;
    public void connectToServer(boolean isNewGame){
        if(isNewGame) {
            try {
                setGameHost();
            }catch (Exception e){}
        }
        else {
            try {
                setGameClient(name);
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

    public void setName(String name){
        this.name = name;
    }

    public boolean isGameStart(){
        if(this.gameClient!=null) {
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
                this.vm = new ViewModel(gameClient);
                gameClient.addObserver(vm);
                gameClient.notifyViewModel();
                while(!gameClient.isGameStarted()){}
            } catch (Exception e) {e.printStackTrace();}
        }).start();

    }

    public ViewModel getVM() {
        return this.vm;
    }
}
