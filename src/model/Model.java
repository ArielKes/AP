package model;

import game_src.Tile;
import game_src.Word;

import java.util.HashMap;
import java.util.List;

public interface Model {

    // administration functions
    boolean isGameStarted();
    boolean isMyTurn();
    void turnEnded();
    String getBoard() throws InterruptedException;
    HashMap<String, Integer> getScoreTable();


    // game functions
    List<Tile> getNTiles(int n);
    int placeWord (Word w);
    boolean checkWord (Word w);


}
