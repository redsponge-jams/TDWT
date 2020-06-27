package com.redsponge.tdwd;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.redsponge.redengine.lighting.LightTextures.Point;
import com.redsponge.redengine.lighting.LightType;
import com.redsponge.redengine.lighting.PointLight;
import com.redsponge.redengine.screen.components.AnimationComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class Coin extends ScreenEntity {

    private int sx, sy;
    private Rectangle r;
    private PooledEffect particle;
    private Sound collectSound;

    private PointLight light;

    public Coin(SpriteBatch batch, ShapeRenderer shapeRenderer, int sx, int sy) {
        super(batch, shapeRenderer);
        this.sx = sx;
        this.sy = sy;
    }

    @Override
    public void added() {
        pos.set(sx, sy);
        size.set(8, 8);
        r = new Rectangle(sx, sy, 8, 8);
        particle = ((GameScreen)screen).getStarRain().spawnParticle(sx, sy);
        light = new PointLight(pos.getX() + 4, pos.getY() + 4, 16, Point.feathered);
        light.getColor().set(Color.YELLOW);
        ((GameScreen) screen).getLightSystem().addLight(light, LightType.MULTIPLICATIVE);
    }

    @Override
    public void loadAssets() {
        add(new AnimationComponent(assets.getAnimation("coinAnimation")));
        collectSound = assets.get("coinPickup", Sound.class);
    }

    public Rectangle getRect() {
        return r;
    }

    public void collect() {
        collectSound.play();
        remove();
        ((GameScreen)screen).getCoins().removeValue(this, true);
    }

    @Override
    public void removed() {
        particle.allowCompletion();
        ((GameScreen)screen).getLightSystem().removeLight(light, LightType.MULTIPLICATIVE);
    }
}
