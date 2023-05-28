package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import view_model.GameServerConnection;
import view_model.ViewModel;


import java.io.IOException;
import java.util.Observer;
import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeController extends BaseController implements Observer,Initializable {

    @FXML
    private Button startButton;

    @FXML
    private ChoiceBox<String> gameTypeChoiceBox;

    private final String[] gameTypes = {"Join an existing game", "Create a new game"};
    private boolean newGame;
    GameServerConnection gsc;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gameTypeChoiceBox.getItems().addAll(gameTypes);
        gameTypeChoiceBox.setValue(gameTypes[0]);
        gameTypeChoiceBox.setOnAction(event -> {
            newGame = !gameTypeChoiceBox.getValue().equals(gameTypes[0]);
        });
        startButton.setOnAction(event -> {
            try {
                this.gsc.connectToServer(newGame);
                FXMLLoader fxmlLoader = changeScene("waiting.fxml", event);
                WaitingController wc = fxmlLoader.getController();
                wc.setGameServerConnection(this.gsc);
                wc.setViewModel(this.gsc.getVM());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void setGameServerConnection(GameServerConnection gsc) {
       this.gsc = gsc;
    }

}