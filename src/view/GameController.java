package view;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.*;

import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
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
    private Button A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,addTile;
    @FXML
    Button testButton;
    @FXML
    Button checkButton;
    @FXML
    Button del;
    @FXML
    Text score;
    @FXML
    Text turn;
    private ArrayList<Button> buttons;

    @FXML
    private GridPane gridPane;
    private final int gridSize = 15;

    private boolean letterChosen = false;
    BooleanProperty check = new SimpleBooleanProperty();
    StringProperty cols = new SimpleStringProperty();
    StringProperty rows = new SimpleStringProperty();
    StringProperty word = new SimpleStringProperty();
    ObservableList<Integer> tilesAmountlist = new SimpleListProperty<>();
    ObservableList<Integer> observableList = FXCollections.observableArrayList(tilesAmountlist);
    private ListProperty<Integer> tilesAmount = new SimpleListProperty<>(observableList);
    private Text letter;
    boolean deleteFlag = false;
    ArrayList<Integer[]> pressedLocations = new ArrayList<>();
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
        for (Button b : buttons) {
            setupButton(b);
            b.setFocusTraversable(false);
            tilesAmount.add(1);
        }
        updateTilesDisplay();
        testButton.setOnMouseClicked(this::test);
        checkButton.setOnMouseClicked(this::checkButtonPushed);
        del.setOnMouseClicked(this::del);
        addTile.setOnMouseClicked(this::addTile);
        //initialize gridPane
        gridPane.setGridLinesVisible(true);
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                Text t = new Text(40, 30, "");
                Rectangle r = new Rectangle(38, 30);
                // coloring according to bonus
                switch (bonus[row][col]) {
                    case 'r' -> r.setFill(Color.INDIANRED);
                    case 'g' -> r.setFill(Color.LIGHTGREEN);
                    case 'y' -> r.setFill(Color.YELLOW);
                    case 'l' -> r.setFill(Color.LIGHTBLUE);
                    case 'd' -> r.setFill(Color.DARKBLUE);
                }
                StackPane pane = new StackPane(r, t);
                pane.setOnMouseClicked(this::TileClicked);
                gridPane.add(pane, col, row);
            }
        }

    }

//    ************************************************** Buttons **************************************************

//    TODO: fill according to model
    private void addTile(MouseEvent mouseEvent) {
        // increment tilesAmount
        tilesAmount.replaceAll(integer -> integer + 1);
        updateTilesDisplay();
    }

    private void updateTilesDisplay() {
        for (int i = 0; i < tilesAmount.size(); i++) {
            Button b = buttons.get(i);
            b.setTooltip(new Tooltip(String.valueOf(tilesAmount.get(i))));
        }
    }

    private void test(Event event) {
        updateBoardDisplay();
        compareBoardToModel();
        addTile(null);
    }

    private void setupButton(Button button) {
        button.setOnMouseClicked(mouseEvent -> {
            resetButtonsView();
            if (checkTilesLeft(button.getText().charAt(0) - 'A')){
                setPlayerChoice(button);
            }
            button.setDisable(true);
        });
    }

    private void TileClicked(Event event) {
        if (letterChosen || deleteFlag) {
            // write letter to text
            StackPane pane = (StackPane) event.getSource();
            Text text = (Text) pane.getChildren().get(1);
            if (deleteFlag && verifyDelete(pane)) {
                text.setText("");
                deleteFlag = false;

            }
            else if (text.getText().equals("") && checkTilesLeft(letter.getText().charAt(0) - 'A')){;
                text.setText(letter.getText());
                pressedLocations.add(new Integer[]{GridPane.getRowIndex(pane), GridPane.getColumnIndex(pane)});
                // lower tilesAmount of clicked tile by 1
                tilesAmount.set(letter.getText().charAt(0) - 'A', tilesAmount.get(letter.getText().charAt(0) - 'A') - 1);
                updateTilesDisplay();
                letterChosen = false;
            }
        }
    }

    private void resetButtonsView(){
        for(Button b:buttons){
            b.setDisable(false);
        }
    }

    private void checkButtonPushed(MouseEvent mouseEvent) {
        check.set(true);
        compareBoardToModel();
        ArrayList<Integer> arr = getWordCoordinates();
        wordVerifiedDisplay(vm.trySetWord(), arr.get(0), arr.get(1), arr.get(2), arr.get(3));
        System.out.println(vm.word.get());
        System.out.println("col: " + vm.col.get() + "row: " + vm.row.get());
        resetChoice();
        updateBoardDisplay();
    }

    private void del(MouseEvent mouseEvent) {
        // delete letter from text of clicked tile
        deleteFlag = true;

    }

    //    ************************************************** Data **************************************************

    private boolean verifyDelete(StackPane pane){
        // get row and col of pressed tile
        int row = GridPane.getRowIndex(pane);
        int col = GridPane.getColumnIndex(pane);
        String text = ((Text) pane.getChildren().get(1)).getText();
        // check if row, col in pressedLocations
        for (Integer[] arr : pressedLocations) {
            if (arr[0] == row && arr[1] == col) {
                pressedLocations.remove(arr);
                // increment tilesAmount of deleted tile by 1
                tilesAmount.set(text.charAt(0) - 'A', tilesAmount.get(text.charAt(0) - 'A') + 1);
                updateTilesDisplay();
                return true;
            }
        }
        return false;


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
        letterChosen = false;
        deleteFlag = false;
        pressedLocations = new ArrayList<>();
    }

    private boolean checkTilesLeft(int index){
        return tilesAmount.get(index) > 0;
    }

    private ArrayList<Integer> getWordCoordinates(){
        ArrayList<Integer> coordinates = new ArrayList<>();
        coordinates.add(Integer.parseInt(String.valueOf(rows.get().charAt(0))));
        coordinates.add(Integer.parseInt(String.valueOf(cols.get().charAt(0))));
        coordinates.add(Integer.parseInt(String.valueOf(rows.get().charAt(rows.get().length()-1))));
        coordinates.add(Integer.parseInt(String.valueOf(cols.get().charAt(cols.get().length()-1))));
        return coordinates;
    }

    private String getBoardString(){
        StringBuilder boardString = new StringBuilder();
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                StackPane pane = (StackPane) gridPane.getChildren().get((row * gridSize) + col + 1);
                Text t = (Text) pane.getChildren().get(1);
                if (t.getText().equals("")) {
                    boardString.append("_");
                } else {
                    boardString.append(t.getText());
                }
            }
        }
        return boardString.toString();
    }

    private void compareBoardToModel(){
        /* updates model with the current board from the GUI */

        StringBuilder diffRows = new StringBuilder();
        StringBuilder diffCols = new StringBuilder();
        StringBuilder diffWord = new StringBuilder();
        String modelBoard = vm.model.board.get_as_string();
        String guiBoard = getBoardString();
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                int index = (row * 15) + col;
                if (modelBoard.charAt(index) != guiBoard.charAt(index)){
                    diffRows.append(row);
                    diffCols.append(col);
                    diffWord.append(guiBoard.charAt(index));
                }
            }
        }
        rows.set(diffRows.toString());
        cols.set(diffCols.toString());
        word.set(diffWord.toString());
    }

//    ************************************************** Display **************************************************

    private void changeColor(int row, int col, Color color){
        StackPane pane = (StackPane) gridPane.getChildren().get((row * gridSize) + col + 1);
        Rectangle r = (Rectangle) pane.getChildren().get(0);
        r.setFill(color);

    }

    private void glowRectangle(int row, int col, Color color){
        /*
        make the rectangle glow in a given color and change it back to the original color
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

    public void updateBoardDisplay(){
        /* updates GUI board display from the model's board*/
        String board = vm.model.board.get_as_string();
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
        }
    }

    public void displayTurn(String name){
        turn.setText("Turn: " + name);
    }


    public void setViewModel(ViewModel vm){
        this.vm = vm;
        vm.check.bind(this.check);
        vm.word.bind(this.word);
        vm.col.bind(this.cols);
        vm.row.bind(this.rows);
//        vm.tilesAmount.bind(this.tilesAmount);
    }



}