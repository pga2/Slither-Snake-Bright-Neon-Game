package com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.EnemyAndAllys;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.LedzinyGameDevelopment.SlitherNeonio.BetterSnake;
import com.LedzinyGameDevelopment.SlitherNeonio.Screens.PlayScreen;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.Snake;
import com.LedzinyGameDevelopment.SlitherNeonio.Sprites.EnemysAndPlayer.SnakeTail;

public class EnemyTail extends SnakeTail {


    public EnemyTail(PlayScreen screen, double angle, float posX, float posY, TextureRegion texture, Color color, Snake snake, float scale, float transparency) {
        super(screen, angle, posX, posY, texture, color, snake, scale, transparency);
        defineEnemyTail();
    }

    public void update(float dt) {
        super.update(dt);

    }

    //completes defining enemy tail
    public void defineEnemyTail() {
        fdef.filter.categoryBits = BetterSnake.ENEMY_TAIL_BIT;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);
    }

}
