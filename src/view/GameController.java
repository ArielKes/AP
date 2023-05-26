package view;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.*;

import javafx.scene.control.Label;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import view_model.ViewModel;

public class GameController extends BaseController implements Observer,Initializable {
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
    @FXML
    Button testButton;
    @FXML
    Label score;
    private ArrayList<Button> buttons;

    @FXML
    private GridPane gridPane;

    private final int gridSize = 15;

    private boolean letterChosen = false;
    private Text letter;
    ViewModel vm;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttons = new ArrayList<>(Arrays.asList(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,delete));

        buttons.forEach(button ->{
            setupButton(button);
            button.setFocusTraversable(false);
        });
        testButton.setOnMouseClicked(this::test);
        //initialize gridPane
        gridPane.setGridLinesVisible(true);
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                Text t = new Text(40, 30, "");
                Rectangle r = new Rectangle(38, 30);
                r.setFill(Color.WHITE);
                StackPane pane = new StackPane(r, t);
                pane.setOnMouseClicked(this::handleMouseClick);
                gridPane.add(pane, col, row);
            }
        }
    }

    private void test(Event event) {
        wordVerifiedDisplay(true, 0, 0, 0, 4);
        Map<String, Integer> map = new HashMap<>();
        map.put("A", 1);
        map.put("B", 3);
        map.put("C", 3);
        setScores(map);
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
            Text text = (Text) pane.getChildren().get(1);
            if (text.getText().equals("") || letter.getText().equals(""))  {
                text.setText(letter.getText());

                // reset letter
                letterChosen = false;
            }
        }
    }

    private void setPlayerChoice(Button button){
        // get the letter from the clicked button
        letter = new Text(button.getText());
        letterChosen = true;
    }

    private void changeColor(int row, int col, Color color){
        StackPane pane = (StackPane) gridPane.getChildren().get((row * gridSize) + col + 1);
        Rectangle r = (Rectangle) pane.getChildren().get(0);
        r.setFill(color);

    }

    private void glowRectangle(int row, int col, Color color){
        /*
        make the rectanle glow in a given color and change it back to the original color
         */
        StackPane pane = (StackPane) gridPane.getChildren().get((row * gridSize) + col + 1);
        Rectangle r = (Rectangle) pane.getChildren().get(0);
        Timeline timeline = new Timeline();
        timeline.setCycleCount(2);
        timeline.setAutoReverse(true);
        KeyValue kv = new KeyValue(r.fillProperty(), color);
        KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

//    ************************************************** API **************************************************

    public void resetButtonsView(Button button){
        for(Button b:buttons){
            b.setDisable(false);
        }
    }

    public void wordVerifiedDisplay(boolean verified, int startRow, int startCol, int endRow, int endCol){
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                if (verified)
                    glowRectangle(row, col, Color.GREEN);
                else
                    glowRectangle(row, col, Color.RED);
            }
        }
    }

    public void setScores(Map<String, Integer> dict){
        // set the scores in the view
        StringBuilder text = new StringBuilder("Scores: ");
        for (Map.Entry<String, Integer> entry : dict.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            text.append(key).append(": ").append(value).append(", ");
        }
        score.setText(text.toString());

    }

    public void setViewModel(ViewModel vm) {
        this.vm = vm;
        vm.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == vm) {
            // update the view
        }
    }



}