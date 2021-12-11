package com.LedzinyGameDevelopment.SlitherNeonio.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;
import com.LedzinyGameDevelopment.SlitherNeonio.Tools.HighScoresFileReader;

public class GameOverScreen implements Screen {

    private final BitmapFont bitmapFont;
    private final BitmapFont scoreBitmapFont;
    private int score;
    private Viewport viewport;
    private Stage stage;

    private BetterSnake game;
    private float time;
    private HighScoresFileReader highScoresFileReader;

    public GameOverScreen(BetterSnake game, int score) {
        this.game = game;
        this.score = score;
        viewport = new ExtendViewport(game.V_WIDTH, game.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);

        scoreBitmapFont = new BitmapFont(Gdx.files.internal("font/NorthCultureTypeface_font.fnt"), false);
        bitmapFont = new BitmapFont(Gdx.files.internal("font/NorthCultureTypeface_font.fnt"), false);
        Label.LabelStyle font = new Label.LabelStyle(bitmapFont, Color.WHITE);
        highScoresFileReader = new HighScoresFileReader();

        Label scoreLabel;

        int highScoresListZeroPosition;
        if(highScoresFileReader.getScoresList().size() == 0)
            highScoresListZeroPosition = 0;
        else
            highScoresListZeroPosition = highScoresFileReader.getScoresList().get(0);

        if(score > highScoresListZeroPosition) {
            Label.LabelStyle scoreFont = new Label.LabelStyle(scoreBitmapFont, Color.RED);
            scoreBitmapFont.getData().setScale(0.45f);
            scoreLabel = new Label("GREAT JOB NEW HIGH SCORE: " + score + "!", scoreFont);
        } else {
            Label.LabelStyle scoreFont = new Label.LabelStyle(scoreBitmapFont, Color.WHITE);
            scoreBitmapFont.getData().setScale(0.4f);
            scoreLabel = new Label("SCORE: " + score, scoreFont);
        }

        bitmapFont.getData().setScale(0.8f);
        Table table = new Table();
        table.center();
        table.setFillParent(true);
        Label gameOverLabel = new Label("GAME OVER", font);
        table.add(gameOverLabel).expandX();
        stage.addActor(table);

        Table scoreTable = new Table();
        scoreTable.top();
        scoreTable.setFillParent(true);
        scoreTable.add(scoreLabel).expandX();
        stage.addActor(scoreTable);

        time = 0;
        BetterSnake.manager.get("audio/sounds/game_over.ogg", Sound.class).play();
    }

    public void show() {

    }

    public void handleInput(float dt) {

    }

    public void update(float dt) {
        handleInput(dt);
        time += dt;
        if(time > 1.5) {
            highScoresFileReader.addScore(score);
            game.setScreen(new MenuScreen(game));
            dispose();
        }
    }

    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    public void resize(int width, int height) {

    }

    public void pause() {

    }

    public void resume() {

    }

    public void hide() {

    }

    public void dispose() {
        stage.dispose();
        bitmapFont.dispose();
        scoreBitmapFont.dispose();
    }
}
