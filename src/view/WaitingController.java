package view;

import java.io.IOException;
import java.net.URL;
import java.util.Observer;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import view_model.GameServerConnection;
import view_model.ViewModel;


public class WaitingController extends BaseController implements Observer,Initializable{

    @FXML
    private Label waitingLabel;

    @FXML
    private Button startButton;

    @FXML
    private Label playerCountLabel;

    private int playerCount = 1;
    GameServerConnection gsc;
    ViewModel vm;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        waitingLabel.setText("");
        updatePlayerCount(playerCount);
        startButton.setOnAction(event -> {
            new Thread (()-> {
                    this.gsc.StartGame();
                    if (this.gsc.isGameStart()) {
                        this.setViewModel(this.gsc.getVM());
                        Platform.runLater(() -> {
                            try {
                                FXMLLoader fxmlLoader = changeScene("game.fxml", event);
                                GameController gc = fxmlLoader.getController();
                                if (vm != null) {
                                    gc.setViewModel(vm);
                                    vm.addObserver(gc);
                                }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        });
                    }

            }).start();
        });

    }

    private void updatePlayerCount(int playerCount) {
        playerCountLabel.setText("Current number of players: " + playerCount);
    }



    public void addPlayer(String name) {
        waitingLabel.setText(name + " has joined the game!");
        playerCount++;
        updatePlayerCount(playerCount);
    }

    public void setGameServerConnection(GameServerConnection gsc) {this.gsc = gsc;}

    public void setViewModel(ViewModel vm){this.vm = vm;}
}
