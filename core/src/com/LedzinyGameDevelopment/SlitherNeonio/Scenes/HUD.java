package com.LedzinyGameDevelopment.SlitherNeonio.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;
import com.LedzinyGameDevelopment.SlitherNeonio.Screens.PlayScreen;

public class HUD implements Disposable {

    private BitmapFont borderFont;
    private BitmapFont font;
    private Viewport viewport;
    private Stage stage;
    private Stage borderStage;

    private Integer score;
    private static boolean worldBoundTouched;
    private Integer deadTimer;
    private float borderTimeCount;
    private boolean newHighScore;

    private Label scoreLabel;
    private Label scoreStrLabel;
    private Label deadTimerLabel;
    private Label deadTimerStrLabel;
    private Label newHighScoreLabel;
    private PlayScreen screen;
    private Table borderTable;
    private Table highScoreTable;

    public HUD(SpriteBatch sb, PlayScreen screen) {
        this.screen = screen;

        viewport = new ExtendViewport(BetterSnake.V_WIDTH, BetterSnake.V_HEIGHT, new OrthographicCamera());

        stage = new Stage(viewport, sb);
        borderStage = new Stage(viewport, sb);

        score = Integer.valueOf(0);

        Table table = new Table();
        table.top();
        table.setFillParent(true);
        font = new BitmapFont(Gdx.files.internal("font/NorthCultureTypeface_font.fnt"), false);
        borderFont = new BitmapFont(Gdx.files.internal("font/NorthCultureTypeface_font.fnt"), false); // font that shows up when player out of screen border
        borderFont.getData().setScale(0.8f);
        font.getData().setScale(0.4f);
        scoreStrLabel = new Label("SCORE", new Label.LabelStyle(font, Color.WHITE));
        scoreLabel = new Label(String.valueOf(score), new Label.LabelStyle(font, Color.WHITE));
        table.add(scoreStrLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();
        stage.addActor(table);

        worldBoundTouched = false;
        borderTimeCount = 0;
        deadTimer = 2;
        newHighScore = false;

        newHighScoreLabel = new Label("NEW HIGH SCORE!", new Label.LabelStyle(borderFont, new Color(251/255f, 210/255f, 210/255f, 1)));
        highScoreTable = new Table();
        highScoreTable.center();
        highScoreTable.setFillParent(true);
        highScoreTable.add(newHighScoreLabel).expandX();

    }

    public void update(float dt) {

        scoreLabel.setText(Math.round(screen.getCreator().getPlayer().getScale() * 1000) - 500);

        //checks if player is out of map
        if(worldBoundTouched) {
            borderTimeCount += dt;
            if(borderTimeCount >= 1) {
                deadTimer--;
                borderTimeCount = 0;
            }
            if(deadTimer < 0) {
                screen.getCreator().getPlayer().setPlayerDead(true);
            }



            if(borderStage.getActors().size <= 0) {
                borderTable = new Table();

                borderTable.center();

                borderTable.setFillParent(true);
                deadTimerStrLabel = new Label("TURN BACK!", new Label.LabelStyle(borderFont, Color.WHITE));
                deadTimerLabel = new Label(String.valueOf(deadTimer), new Label.LabelStyle(borderFont, Color.WHITE));
                borderTable.add(deadTimerStrLabel);
                borderTable.row();
                borderTable.add(deadTimerLabel);
                borderStage.addActor(borderTable);
            } else {
                deadTimerLabel.setText(deadTimer);
            }

        } else {
            deadTimer = 2;
            borderTimeCount = 0;
            for(int i = 0; i < borderStage.getActors().size; i++) {
                borderStage.getActors().get(i).remove();
            }
        }
        if(newHighScore) {
            stage.addActor(highScoreTable);
        } else {
            if(stage.getActors().contains(highScoreTable, true))
                stage.getActors().removeValue(highScoreTable, true);
        }
    }

    public void dispose() {
        stage.dispose();
        borderStage.dispose();
        font.dispose();
        borderFont.dispose();
    }

    public static void setWorldBoundTouched(boolean worldBoundTouched) {
        HUD.worldBoundTouched = worldBoundTouched;
    }

    public Stage getStage() {
        return stage;
    }

    public Stage getBorderStage() {
        return borderStage;
    }

    public void setNewHighScore(boolean newHighScore) {
        this.newHighScore = newHighScore;
    }
}
