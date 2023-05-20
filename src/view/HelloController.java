package view;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Arrays;

public class HelloController implements Initializable {
    @FXML
    private Button A;
    @FXML
    private Button B;
    @FXML
    private Button C;
    @FXML
    private Button D;
    @FXML
    private Button E;
    @FXML
    private Button F;
    @FXML
    private Button G;
    @FXML
    private Button H;
    @FXML
    private Button I;
    @FXML
    private Button J;
    @FXML
    private Button K;
    @FXML
    private Button L;
    @FXML
    private Button M;
    @FXML
    private Button N;
    @FXML
    private Button O;
    @FXML
    private Button P;
    @FXML
    private Button Q;
    @FXML
    private Button R;
    @FXML
    private Button S;
    @FXML
    private Button T;
    @FXML
    private Button U;
    @FXML
    private Button V;
    @FXML
    private Button W;
    @FXML
    private Button X;
    @FXML
    private Button Y;
    @FXML
    private Button Z;
    @FXML
    private Button delete;
    private ArrayList<Button> buttons;

    @FXML
    private GridPane gridPane;

    private int gridSize = 15;

    private boolean letterChosen = false;
    private Text letter;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttons = new ArrayList<>(Arrays.asList(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,delete));

        buttons.forEach(button ->{
            setupButton(button);
            button.setFocusTraversable(false);
        });
        //initialize gridPane
        gridPane.setGridLinesVisible(true);
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                Text t = new Text(40, 30, "");
                StackPane pane = new StackPane(t);
                pane.setOnMouseClicked(this::handleMouseClick);
                gridPane.add(pane, col, row);
            }
        }
    }

    private void setupButton(Button button) {
        button.setOnMouseClicked(mouseEvent -> {
            resetButtonsView(button);
            setPlayerChoice(button);
            button.setDisable(true);
        });
    }

    private void handleMouseClick(Event event) {
        if (letterChosen){
            // write letter to text
            StackPane pane = (StackPane) event.getSource();
            Text text = (Text) pane.getChildren().get(0);
            if (text.getText().equals("") || letter.getText().equals(""))  {
                text.setText(letter.getText());

                // reset letter
                letterChosen = false;
            }
        }
    }

    public void resetButtonsView(Button button){
        for(Button b:buttons){
            b.setDisable(false);
        }
    }
    public void setPlayerChoice(Button button){
        // get the letter from the clicked button
        letter = new Text(button.getText());
        letterChosen = true;
    }
}