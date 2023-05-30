package view_model;

import game_src.Tile;
import game_src.Word;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Model;

import java.util.*;

public class ViewModel extends Observable implements Observer {
    public Model model;
    public BooleanProperty check;
    public StringProperty word;
    public StringProperty board;
    public StringProperty col;
    public StringProperty row;
    private boolean vertical;
    ObservableList<Integer> tilesAmountList = new SimpleListProperty<>();
    ObservableList<Integer> observableList = FXCollections.observableArrayList(tilesAmountList);
    public ListProperty<Integer> tilesAmount = new SimpleListProperty<Integer>(observableList);
    List<Tile> playersTiles;

    public ViewModel(Model model){
        this.model = model;
        this.check = new SimpleBooleanProperty();
        this.word = new SimpleStringProperty();
        this.board = new SimpleStringProperty();
        this.col = new SimpleStringProperty();
        this.row = new SimpleStringProperty();
        this.vertical = true;
    }

    private void getTilesForPlayer(){
        for(int i=0 ; i<26 ; i++){
            this.tilesAmount.add(0);
        }
        for(Tile t:this.model.getClientTiles()){
            this.playersTiles.add(t);
            int j = t.letter - 'A';
            this.tilesAmount.set(j,this.tilesAmount.get(j)+1);
        }
    }

    private Tile popPlayersTiles(char c){
        List<Tile>newList = new ArrayList<Tile>();
        Tile tile = null;
        for(Tile t:this.playersTiles){
            newList.add(t);
        }
        for(int i=0 ; i<newList.size() ; i++){
            if(newList.get(i).letter == c) {
                this.tilesAmount.set((c - 'A'),this.tilesAmount.get(c - 'A')-1) ;
                tile = newList.get(i);
                newList.set(i, null);
                break;
            }
        }
        this.playersTiles.clear();
        for(Tile t:newList){
            if(t!=null)
                this.playersTiles.add(t);
        }
        return tile;
    }

    private int[] parseIntString(String s){
        /* input: string of integers, for example "1234".
         * output: array of integers.
         * */
        int[] arr = new int[s.length()];
        for (int i = 0; i < s.length(); i++) {
            int num = Character.getNumericValue(s.charAt(i));
            arr[i] = num;
        }
        Arrays.sort(arr);
        return arr;
    }

    private boolean checkValidity(int[] col_arr ,int[] row_arr){
        boolean horizontal = true;
        if(col_arr.length == 1 || row_arr.length == 1)
            return false;
        for(int i=1 ; i<row_arr.length ; i++){
            if(row_arr[i-1] != row_arr[i])
                horizontal = false;
        }
        for(int i=1 ; i<col_arr.length ; i++){
            if(col_arr[i-1] != col_arr[i])
                vertical = false;
        }
        if(vertical == horizontal)
            return false;
        return true;
    }
    private Tile[] buildTilesArr(int[] col_arr ,int[] row_arr, char[] word){
        ArrayList<Character> newWord = new ArrayList<Character>();
        if(vertical == true) {
            newWord.add(word[0]);
            for (int i = 1; i < row_arr.length; i++) {
                int gap = row_arr[i] - row_arr[i-1];
                while (gap > 1) {
                    newWord.add('_');
                    gap--;
                }
                newWord.add(word[i]);
            }
        }
        else{
            newWord.add(word[0]);
            for(int i=1 ; i<col_arr.length ; i++){
                int gap = col_arr[i] - col_arr[i-1];
                while(gap > 1){
                    newWord.add('_');
                    gap--;
                }
                newWord.add(word[i]);
            }

        }
        Tile[] t = new Tile[newWord.size()];
        for(int i=0 ; i<newWord.size() ; i++)
            t[i] = this.popPlayersTiles(newWord.get(i));
        return t;
    }

    public boolean trySetWord(){
        boolean valid = false;
        int[] col_arr = parseIntString(col.get());
        int[] row_arr = parseIntString(row.get());
        if(checkValidity(col_arr ,row_arr)){
            int smallestRow = Arrays.stream(row_arr).min().getAsInt();
            int smallestCol = Arrays.stream(col_arr).min().getAsInt();
            Tile[] t = buildTilesArr(col_arr, row_arr, word.get().toCharArray());
            Word w = new Word(t  ,smallestRow ,smallestCol ,vertical);
            for(Tile y : w.getTiles()){
                if(y!=null)
                    System.out.print(y.letter);
                else
                    System.out.print("_");
            }
            System.out.println("");
            this.model.placeWord(w);
            valid = true;
        }
        vertical = true;
        return valid;
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o==model){
            try{
                this.board.set(model.getBoard());
            }catch (Exception e){}
        }
    }
}
