package view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
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

    @FXML
    private TextField playerNameField;

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
            new Thread (()-> {
                this.gsc.setName(playerNameField.getText());
                Platform.runLater(() -> {
                    this.gsc.connectToServer(newGame);
                    try {
                        FXMLLoader fxmlLoader = changeScene("waiting.fxml", event);
                        WaitingController wc = fxmlLoader.getController();
                        wc.setGameServerConnection(this.gsc);
                        wc.setViewModel(this.gsc.getVM());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }).start();
        });

    }

    public void setGameServerConnection(GameServerConnection gsc) {
       this.gsc = gsc;
    }

}
