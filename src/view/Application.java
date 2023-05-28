package view;

import game_src.BookScrabbleHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Model;
import model.dummyA;
import view_model.ViewModel;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        Model m = new Model();
        ViewModel vm = new ViewModel(m);
        m.addObserver(vm);

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("welcome.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        WelcomeController wc = fxmlLoader.getController();
        wc.setViewModel(vm);
        vm.addObserver(wc);

        stage.setTitle("Scrabble!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}