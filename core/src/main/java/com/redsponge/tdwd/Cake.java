package com.redsponge.tdwd;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.redsponge.redengine.lighting.LightTextures.Point;
import com.redsponge.redengine.lighting.LightType;
import com.redsponge.redengine.lighting.PointLight;
import com.redsponge.redengine.screen.components.TextureComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class Cake extends ScreenEntity {

    private PointLight light;

    public Cake(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
    }

    @Override
    public void added() {
        super.added();
        size.set(124 , 63);
        pos.set(640 / 2f - size.getX() / 2f, 42, -3);

        light = new PointLight(pos.getX() + size.getX() / 2f, pos.getY() + size.getY() / 2f, 200, Point.feathered);
        light.getColor().set(Color.WHITE);
        ((GameScreen)screen).getLightSystem().addLight(light, LightType.MULTIPLICATIVE);
    }

    @Override
    public void loadAssets() {
        add(new TextureComponent(assets.getTextureRegion("cake")));
    }
}
