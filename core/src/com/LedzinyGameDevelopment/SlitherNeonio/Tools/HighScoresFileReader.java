package com.LedzinyGameDevelopment.SlitherNeonio.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.Collections;
import java.util.LinkedList;

public class HighScoresFileReader {

    private LinkedList<Integer> scoresList;
    private String highScoresString;
    private Preferences prefs;

    public HighScoresFileReader() { // sorts and transfer to list high scores
        prefs = Gdx.app.getPreferences("highScores");
        String text = prefs.getString("highScores");
        String[] wordsArray = text.split(",");
        scoresList = new LinkedList<>();
        for(int i = 0; i < wordsArray.length; i++){
            try{
                scoresList.add(Integer.valueOf(wordsArray[i]));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Gdx.app.log("exception: ", "Not enough high scores");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Collections.sort(scoresList);
        Collections.reverse(scoresList);
        if(scoresList.size() > 5) {
            for(int i = 0; i < scoresList.size(); i++) {
                if(i >= 5) {
                    scoresList.remove(i);
                }
            }
        }
    }

    public LinkedList<Integer> getScoresList() {
        return scoresList;
    }

    public void addScore(int score) { // adds scores to list and save
        scoresList.add(score);
        highScoresString = String.valueOf(scoresList.get(0));
        for(int i = 1; i < scoresList.size(); i++) {
            highScoresString += "," + scoresList.get(i);
        }
        prefs.putString("highScores", highScoresString);
        prefs.flush();
    }

}
