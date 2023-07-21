package view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import model.utils;
import view_model.GameServerConnection;
import view_model.ViewModel;


import java.io.IOException;
import java.util.HashMap;
import java.util.Observer;
import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeController extends BaseController implements Observer,Initializable {

    @FXML
    private Button startButton;

    @FXML
    private ChoiceBox<String> gameTypeChoiceBox;

    @FXML
    private TextField playerNameField;

    @FXML
    private TextField gameIDField;

    @FXML
    private TextField gamePortField;

    private final String[] gameTypes = {"Join an existing game", "Create a new game"};
    private boolean newGame;
    GameServerConnection gsc;

   HashMap<String, String> properties;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.properties = utils.getProperties("src/resources/properties.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gameTypeChoiceBox.getItems().addAll(gameTypes);
        gameTypeChoiceBox.setValue(gameTypes[0]);
        gameIDField.setText("0");
        gamePortField.setText(this.properties.get("game_host.port"));
        gameTypeChoiceBox.setOnAction(event -> {
            newGame = !gameTypeChoiceBox.getValue().equals(gameTypes[0]);
        });
        startButton.setOnAction(event -> {
            this.properties.replace("game_host.port", gamePortField.getText());
            this.gsc.setName(playerNameField.getText());
            this.gsc.setGameID(gameIDField.getText());
                this.gsc.connectToServer(newGame);
                try {
                    FXMLLoader fxmlLoader = changeScene("waiting.fxml", event);
                    WaitingController wc = fxmlLoader.getController();
                    wc.setPlayerName(newGame ? "Host" : playerNameField.getText());
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
