package view;

import java.io.IOException;
import java.net.URL;
import java.util.Observer;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


public class WaitingController extends BaseController implements Observer,Initializable{

    @FXML
    private Label waitingLabel;

    @FXML
    private Button startButton;

    @FXML
    private Label playerCountLabel;

    private int playerCount = 1;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        waitingLabel.setText("");
        updatePlayerCount(playerCount);
        startButton.setOnAction(event -> {
            try {
                changeScene("game.fxml", event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
}
