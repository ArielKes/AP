package view;

import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.*;

import javafx.scene.Node;
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
    private Button A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z;
    @FXML
    Button testButton;
    @FXML
    Label score;
    private ArrayList<Button> buttons;

    @FXML
    private GridPane gridPane;
    private final int gridSize = 15;

    private boolean letterChosen = false;
    BooleanProperty check = new SimpleBooleanProperty();
    StringProperty cols = new SimpleStringProperty();
    StringProperty rows = new SimpleStringProperty();
    StringProperty word = new SimpleStringProperty();
    private char tilesArray[][] = new char[15][15];
    private Text letter;
    ViewModel vm;
    private final char[][] bonus = {
            {'r','g','g','l','g','g','g','r','g','g','g','l','g','g','r'},
            {'g','y','g','g','g','d','g','g','g','d','g','g','g','y','g'},
            {'g','g','y','g','g','g','l','g','l','g','g','g','y','g','g'},
            {'l','g','g','y','g','g','g','l','g','g','g','y','g','g','l'},
            {'g','g','g','g','y','g','g','g','g','g','y','g','g','g','g'},
            {'g','d','g','g','g','d','g','g','g','d','g','g','g','d','g'},
            {'g','g','l','g','g','g','l','g','l','g','g','g','l','g','g'},
            {'r','g','g','l','g','g','g','y','g','g','g','l','g','g','r'},
            {'g','g','l','g','g','g','l','g','l','g','g','g','l','g','g'},
            {'g','d','g','g','g','d','g','g','g','d','g','g','g','d','g'},
            {'g','g','g','g','y','g','g','g','g','g','y','g','g','g','g'},
            {'l','g','g','y','g','g','g','l','g','g','g','y','g','g','l'},
            {'g','g','y','g','g','g','l','g','l','g','g','g','y','g','g'},
            {'g','y','g','g','g','d','g','g','g','d','g','g','g','y','g'},
            {'r','g','g','l','g','g','g','r','g','g','g','l','g','g','r'}
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttons = new ArrayList<>(Arrays.asList(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z));
        resetChoice();

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
                // coloring according to bonus
                switch (bonus[row][col]){
                    case 'r':
                        r.setFill(Color.RED);
                        break;
                    case 'g':
                        r.setFill(Color.GREEN);
                        break;
                    case 'y':
                        r.setFill(Color.YELLOW);
                        break;
                    case 'l':
                        r.setFill(Color.LIGHTBLUE);
                        break;
                    case 'd':
                        r.setFill(Color.DARKBLUE);
                        break;
                }
                StackPane pane = new StackPane(r, t);
                pane.setOnMouseClicked(this::handleMouseClick);
                gridPane.add(pane, col, row);
            }
        }

    }

    private void test(Event event) {
        updateBoardDisplay();
    }

    private void setupButton(Button button) {
        button.setOnMouseClicked(mouseEvent -> {
            resetButtonsView();
            setPlayerChoice(button);
            button.setDisable(true);
        });
    }

    private void colsAppend(int col){
        cols.set(cols.get() + col);
    }

    private void rowsAppend(int row){
        rows.set(rows.get() + row);
    }

    private void handleMouseClick(Event event) {
        if (letterChosen){
            // write letter to text
            StackPane pane = (StackPane) event.getSource();
            Text text = (Text) pane.getChildren().get(1);
            if (text.getText().equals("") || letter.getText().equals(""))  {
                text.setText(letter.getText());
                Node node = (Node) event.getSource();
                Integer col = GridPane.getColumnIndex(node);
                Integer row = GridPane.getRowIndex(node);
                colsAppend(col);
                rowsAppend(row);
                word.set(word.get()+letter.getText());
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

    private void resetChoice(){
        check.set(false);
        word.set("");
        cols.set("");
        rows.set("");
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

    public void resetButtonsView(){
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

    public void checkButtonPushed(){
        check.set(true);
        vm.trySetWord();
        System.out.println(vm.word.get());
        System.out.println("col: " + vm.col.get() + "row: " + vm.row.get());
        //System.out.println(vm.model.board.get_as_string());
        resetChoice();
    }

    public void updateBoardDisplay(){
       /* String board = vm.model.board.get_as_string();
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                StackPane pane = (StackPane) gridPane.getChildren().get((row * gridSize) + col + 1);
                Text text = (Text) pane.getChildren().get(1);
                char c = board.charAt((row * gridSize) + col);
                if (c == '_')
                    text.setText("");
                else {
                    text.setText(String.valueOf(c));
                }
            }
        }*/
    }



    public void setViewModel(ViewModel vm){
        this.vm = vm;
        vm.check.bind(this.check);
        vm.word.bind(this.word);
        vm.col.bind(this.cols);
        vm.row.bind(this.rows);
    }



}