package view;

import game_src.BookScrabbleHandler;
import javafx.application.Platform;
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
        //new Thread (()-> {
            GameServerConnection gsc = new GameServerConnection();
           // Platform.runLater(() -> {
                FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("welcome.fxml"));
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                WelcomeController wc = fxmlLoader.getController();
                wc.setGameServerConnection(gsc);

                stage.setTitle("Scrabble!");
                stage.setScene(scene);
                stage.show();
          //  });
        //}).start();
    }

    public static void main(String[] args) {
        launch();
    }

}