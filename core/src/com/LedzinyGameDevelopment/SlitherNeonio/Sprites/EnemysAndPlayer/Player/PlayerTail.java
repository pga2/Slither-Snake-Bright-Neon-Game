package com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.Player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;
import com.LedzinyGameDevelopment.SlitherNeonio.Screens.PlayScreen;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.Snake;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.SnakeTail;

public class PlayerTail extends SnakeTail {

    private Player player;

    public PlayerTail(PlayScreen screen, double angle, float posX, float posY, TextureRegion texture, Color color, Snake snake, float scale, float transparency) {
        super(screen, angle, posX, posY, texture, color, snake, scale, transparency);
        this.player = ((Player) snake);
        definePlayerTail();
    }

    public void update(float dt) {
        super.update(dt);

    }

    public void definePlayerTail() {
        fdef.filter.categoryBits = BetterSnake.PLAYER_TAIL_BIT;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);
    }

    public Player getPlayer() {
        return player;
    }

}
