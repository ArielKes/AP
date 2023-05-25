package model.utilObjects;
import java.io.Serializable;


public class PlaceWord implements Serializable {
    int x;
    int y;
    String word;
    boolean isHorizontal;
    public PlaceWord(int x, int y, String word, boolean isHorizontal){
        this.x = x;
        this.y = y;
        this.word = word;
        this.isHorizontal = isHorizontal;
    }
}
