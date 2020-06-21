package com.redsponge.tdwd;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.redengine.screen.components.PositionComponent;
import com.redsponge.redengine.screen.components.TextureComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class Sword extends ScreenEntity {

    private float time;
    private final float maxTime = 0.5f;
    private final int dir;

    private PositionComponent attachedTo;
    private Vector2 offset;

    public Sword(SpriteBatch batch, ShapeRenderer shapeRenderer, int dir, PositionComponent attachedTo, Vector2 offset) {
        super(batch, shapeRenderer);
        this.dir = dir;
        this.attachedTo = attachedTo;
        this.offset = offset;
    }

    @Override
    public void added() {
        super.added();
        pos.set(100, 100);
        size.set(13, 13);
    }

    @Override
    public void loadAssets() {
        add(new TextureComponent(assets.get("swordTex", Texture.class)));
    }

    @Override
    public void additionalTick(float delta) {
        super.additionalTick(delta);
        time += delta;
        pos.set(attachedTo.getX() + offset.x, attachedTo.getY() + offset.y);
        render.setRenderOriginX(-7);
        render.setRenderOriginY(-7);
        render.setRotation(Interpolation.swingOut.apply(time / maxTime) * dir * -180 + 45);

        if(time >= maxTime) remove();
    }

    public boolean canRelease() {
        return time >= maxTime - 0.1f;
    }

    public boolean isDone() {
        return time >= maxTime;
    }
}
