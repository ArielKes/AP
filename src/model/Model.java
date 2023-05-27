package model;

import game_src.Board;
import game_src.Tile;
import game_src.Word;

import java.util.Arrays;
import java.util.Observable;

public class Model extends Observable {
    public Board board;
    Tile.Bag bag;
    public Model(){
        board = new Board();
        bag = new Tile.Bag();

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
        return arr;
    }

    public void tryPlaceWord(String text,String col,String row, boolean vertical) {
        Tile[] t = new Tile[text.length()];
        for(int i=0 ; i<text.length() ; i++){
            Arrays.fill(t,this.bag.getTile(text.toCharArray()[i]));
        }
        int[] col_arr = parseIntString(col);
        int[] row_arr = parseIntString(row);
        int smallestRow = Arrays.stream(row_arr).min().getAsInt();
        int smallestCol = Arrays.stream(col_arr).min().getAsInt();
        Word w = new Word(t ,smallestCol ,smallestRow ,vertical);
        int score = this.board.tryPlaceWord(w);
        setChanged();
        notifyObservers();
        System.out.println("score:" + score);
    }

}
