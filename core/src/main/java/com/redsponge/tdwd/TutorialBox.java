package com.redsponge.tdwd;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.redsponge.redengine.lighting.LightTextures;
import com.redsponge.redengine.lighting.LightTextures.Soft;
import com.redsponge.redengine.lighting.LightType;
import com.redsponge.redengine.lighting.PointLight;
import com.redsponge.redengine.screen.components.TextureComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class TutorialBox extends ScreenEntity {

    public TutorialBox(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
    }

    @Override
    public void added() {
        super.added();
        pos.set(20, 45, -7);
        size.set(87, 25);

        ((GameScreen)screen).getLightSystem().addLight(new PointLight(55, 60, 200, Soft.cone), LightType.MULTIPLICATIVE);
    }

    @Override
    public void loadAssets() {
        add(new TextureComponent(assets.getTextureRegion("tutorial")));
    }
}
