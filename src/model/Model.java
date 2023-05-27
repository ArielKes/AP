package model;

import game_src.Board;
import game_src.Tile;
import game_src.Word;

import java.util.Arrays;
import java.util.Observable;

public class Model extends Observable {
    public Board board;
    public Tile.Bag bag;
    public Model(){
        board = new Board();
        bag = new Tile.Bag();

    }

    public void tryPlaceWord(Word w) {
        int score = this.board.tryPlaceWord(w);
        setChanged();
        notifyObservers();
        System.out.println("score:" + score);
    }

}
