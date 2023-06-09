package model;

import game_src.Tile;
import game_src.Word;

import java.util.HashMap;
import java.util.List;

public interface Model {

    // administration functions

    boolean isMyTurn();
    void endTurn();
    String getBoard() throws InterruptedException;
    HashMap<String, Integer> getScoreTable();
    List<Tile> getClientTiles();
    void updateClient();
    boolean isGameStarted();


    // game functions
    void addTile();
    int placeWord (Word w);
    boolean checkWord (String w);

    String getBoardString();
    HashMap<String, Integer> getScoreTableHashMap();

}
