package model;

import java.io.Serializable;
import java.util.HashMap;

public class ScoreTable implements Serializable {
    public HashMap<String, Integer> scores;

    public ScoreTable(){
        scores = new HashMap<>();
    }
    public ScoreTable(HashMap<String, Integer> prev_scores){
        scores = prev_scores;
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public void addScore(String clientName, int score) {
        if (score == -1 || score == -2) score = 0; // in case of -1 / -2 from illegal word
        if (scores.containsKey(clientName)){
            scores.put(clientName, scores.get(clientName) + score);
        }
        else{
            scores.put(clientName, score);
        }
    }

    public HashMap<String, Integer> getScores() {
        return scores;
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
