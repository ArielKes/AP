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

    public void tryPlaceWord(String text,int col,int row, boolean vertical) {
        Tile[] t = new Tile[text.length()];
        for(int i=0 ; i<text.length() ; i++){
            Arrays.fill(t,this.bag.getTile(text.toCharArray()[i]));
        }
        Word w = new Word(t ,col ,row ,vertical);
        int score = this.board.tryPlaceWord(w);
        setChanged();
        notifyObservers();
        System.out.println("score:" + score);
    }

}
