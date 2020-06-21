package com.redsponge.tdwd;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.redsponge.redengine.physics.PBodyType;
import com.redsponge.redengine.screen.components.PhysicsComponent;
import com.redsponge.redengine.screen.components.TextureComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class WorldGeometry extends ScreenEntity {

    private final int x;
    private final int y;
    private final int w;
    private final int h;

    public WorldGeometry(SpriteBatch batch, ShapeRenderer shapeRenderer, int x, int y, int w, int h) {
        super(batch, shapeRenderer);
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    @Override
    public void added() {
        super.added();
        pos.set(x, y);
        size.set(w, h);
        add(new PhysicsComponent(PBodyType.SOLID));
        render.setColor(Color.BROWN);
    }

    @Override
    public void loadAssets() {
        add(new TextureComponent(assets.get("pixel", Texture.class)));
    }
}
