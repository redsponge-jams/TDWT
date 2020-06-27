package com.redsponge.tdwd;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.redsponge.redengine.screen.components.TextureComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class Background extends ScreenEntity {

    public Background(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
    }

    @Override
    public void added() {
        pos.set(0, 0, -10);
        size.set(640, 360);
        render.setUseRegH(true).setUseRegW(true);
    }

    @Override
    public void loadAssets() {
        add(new TextureComponent(assets.getTextureRegion("background")));
    }
}
