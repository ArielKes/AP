package view;

import game_src.BookScrabbleHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.GameClient;
import model.GameHost;
import model.Model;
import model.dummyA;
import view_model.GameServerConnection;
import view_model.ViewModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException{
        GameServerConnection gsc = new GameServerConnection();

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("welcome.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        WelcomeController wc = fxmlLoader.getController();
        wc.setGameServerConnection(gsc);

        stage.setTitle("Scrabble!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}