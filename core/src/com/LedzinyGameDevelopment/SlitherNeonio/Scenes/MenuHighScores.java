package com.LedzinyGameDevelopment.SlitherNeonio.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;
import com.LedzinyGameDevelopment.SlitherNeonio.Tools.HighScoresFileReader;

import java.util.LinkedList;

public class MenuHighScores implements Disposable {
    private BitmapFont font;
    private HighScoresFileReader highScoresFileReader;
    private Stage stage;
    private ExtendViewport viewport;
    private BetterSnake game;

    private LinkedList<Integer> highScoresList;
    private Array<Label> labels;

    public MenuHighScores(BetterSnake game) {
        this.game = game;
        viewport = new ExtendViewport(BetterSnake.V_WIDTH, BetterSnake.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);
        highScoresFileReader = new HighScoresFileReader();
        highScoresList = highScoresFileReader.getScoresList();

        Table table = new Table();
        table.top();
        table.pad(200, 0, 0, 0);
        table.setFillParent(true);
        font = new BitmapFont(Gdx.files.internal("font/NorthCultureTypeface_font.fnt"), false);
        font.getData().setScale(0.4f);
        labels = new Array<>();
        for(Integer highScore : highScoresList) {
            labels.add(new Label(String.valueOf(highScore), new Label.LabelStyle(font, Color.WHITE)));
        }
        for(Label label : labels) {
            table.row();
            table.add(label).expandX();
        }
        stage.addActor(table);
    }


    public void dispose() {
        stage.dispose();
        font.dispose();
    }

    public Stage getStage() {
        return stage;
    }
}
