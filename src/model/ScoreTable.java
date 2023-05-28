package model;

import java.io.Serializable;
import java.util.HashMap;

public class ScoreTable implements Serializable {
    HashMap<String, Integer> scores;

    public ScoreTable(){
        scores = new HashMap<>();
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public void addScore(String clientName, int score) {
        if (scores.containsKey(clientName)){
            scores.put(clientName, scores.get(clientName) + score);
        }
        else{
            scores.put(clientName, score);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String clientName : scores.keySet()){
            sb.append(clientName).append(": ").append(scores.get(clientName)).append("\n");
        }
        return sb.toString();
    }
}
