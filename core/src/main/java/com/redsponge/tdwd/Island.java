package com.redsponge.tdwd;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.redsponge.redengine.physics.PBodyType;
import com.redsponge.redengine.screen.components.PhysicsComponent;
import com.redsponge.redengine.screen.components.TextureComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class Island extends ScreenEntity {

    private int sx, sy;

    public Island(SpriteBatch batch, ShapeRenderer shapeRenderer, int sx, int sy) {
        super(batch, shapeRenderer);
        this.sx = sx;
        this.sy = sy;
    }

    @Override
    public void added() {
        pos.set(sx, sy);
        size.set(70, 5);
        render.setUseRegH(true).setOffsetY(-5);
        add(new PhysicsComponent(PBodyType.SOLID));
    }

    @Override
    public void loadAssets() {
        add(new TextureComponent(assets.getTextureRegion("island")));
    }
}
