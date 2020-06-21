package com.redsponge.tdwd;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.redsponge.redengine.screen.components.RenderRunnableComponent;
import com.redsponge.redengine.screen.components.TextureComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class Player extends ScreenEntity {

    public Player(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
    }

    @Override
    public void added() {
        super.added();
        pos.set(100, 100);
        size.set(16, 30);
    }

    @Override
    public void loadAssets() {
        add(new TextureComponent(assets.get("playerTex", Texture.class)));
    }
}
